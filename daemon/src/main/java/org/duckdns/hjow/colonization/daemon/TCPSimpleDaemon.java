package org.duckdns.hjow.colonization.daemon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.duckdns.hjow.colonization.ColonyClassLoader;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.ClassUtil;

/** TCP로 접속을 받는 Colonization Daemon */
public class TCPSimpleDaemon {
    public static void main(String[] args) {
    	int port = 65246;
    	
    	TCPSimpleDaemon daemon = null;
    	if(args == null) { daemon = new TCPSimpleDaemon(port); daemon.start(); }
    	
    }
    
    protected volatile int     port           = 65246;
    protected volatile boolean threadAcceptor = true;
    
    protected volatile ServerSocket           acceptor;
    protected volatile List<TCPSimpleSession> sessions = new Vector<TCPSimpleSession>();
    
    protected boolean started = false;
    
    /** 데몬 객체 생성, 포트는 기본값 (65246) 사용 */
    public TCPSimpleDaemon() {}
    /** 포트 지정해 데몬 객체 생성 */
    public TCPSimpleDaemon(int port) {
    	this();
    	this.port = port;
    }
    
    /** 프론트 데몬 구동 시작, 이 때부터 해당 포트로 TCP 접속을 받기 시작 */
    public void start() {
    	threadAcceptor = true;
    	new Thread(new Runnable() {
			public void run() {
				threadAcceptor = true;
				while(threadAcceptor) {
					if(acceptor == null) {
						try { acceptor = new ServerSocket(port); } catch(Throwable tx) { tx.printStackTrace(); threadAcceptor = false; break; }
					}
					
					try {
						Socket soc = acceptor.accept();
						TCPSimpleSession session = new TCPSimpleSession(soc);
						session.activate();
						sessions.add(session);
					} catch(Throwable tx) {
						tx.printStackTrace();
					}
				}
				threadAcceptor = false;
				
				for(TCPSimpleSession sessions : sessions) {
					sessions.dispose();
				}
				sessions.clear();
				
				System.exit(0);
			}
    	}).start();
    	
    	/** 청소부 데몬 구동 시작 */
    	new Thread(new Runnable() {
			@Override
			public void run() {
				while(threadAcceptor) {
					int idx = 0;
					while(idx < sessions.size()) {
						TCPSimpleSession sess = sessions.get(idx);
						if(sess.isDisposed()) {
							sessions.remove(idx);
						}
					}
					
					try { Thread.sleep(200L); } catch(InterruptedException ex) { break; }
				}
			}
		}).start();
    }
    
    /** 클라이언트의 요청을 처리하는 메소드 */
    public static JsonObject process(JsonObject json) throws Exception {
    	String strCycle = json.get("cycle") == null ? "1" : json.get("cycle").toString().trim();
    	int cyclePass = Integer.parseInt(strCycle);
    	
    	JsonObject jsonCol = (JsonObject) json.get("colony");
    	Colony col = ColonyClassLoader.loadColony(jsonCol);
    	jsonCol = null;
    	json = null;
    	
    	BigInteger time = col.getTime();
    	time = time.add(BigInteger.ONE);
    	
    	BigInteger timeMax = new BigInteger(String.valueOf(Integer.MAX_VALUE - 10));
    	while(time.compareTo(timeMax) >= 0) {
    		time = time.subtract(timeMax);
    	}
    	int cycle = time.intValue();
    	
    	for(int idx=0; idx<cyclePass; idx++) {
    		cycle++;
    		col.oneCycle(cycle, null, col, 100, null);
    	}
    	
    	JsonObject res = new JsonObject();
    	res.put("cycle", new Integer(cyclePass));
    	res.put("colony", col.toJson());
    	
        return res;
    }
    
    static volatile boolean flagExit = false;

    public static final String STRINGLINE_REQUEST_START = "/*START_COLONIZATION_TCP_REQUEST*/";
    public static final String STRINGLINE_REQUEST_END   = "/*END_COLONIZATION_TCP_REQUEST*/";
    public static final String STRINGLINE_REQUEST_EXIT  = "/*EXIT_COLONIZATION_TCP*/";
}

/** TCP 접속 각 세션 */
class TCPSimpleSession implements Serializable, Disposeable, Runnable {
	private static final long serialVersionUID = -5085242057143667451L;
	protected long key = ColonyManager.generateKey();
	protected Socket socket = null;
	
	protected volatile transient boolean threadSwitch = false;
	protected volatile transient boolean stateReading = false;
	protected volatile transient BufferedReader reader;
	protected volatile transient BufferedWriter writer;
	
	public TCPSimpleSession() { }
	public TCPSimpleSession(Socket socket) { this(); this.socket = socket; init(); }
	
	/** 소켓 초기화 */
	protected void init() {
		if(reader != null || writer != null) { ClassUtil.closeAll(reader, writer); }
		try {
		    reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
		} catch(Throwable t) {
			dispose();
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	/** 소켓 및 Stream 모두 닫아 정리 */
	public void dispose() {
		threadSwitch = false;
		if(reader != null) { ClassUtil.closeAll(reader); reader = null; }
		if(writer != null) { ClassUtil.closeAll(writer); writer = null; }
		if(socket != null) { ClassUtil.closeAll(socket); socket = null; } 
	}
	
	/** 세션이 사용 종료됐는지 판별 */
	public boolean isDisposed() {
		return (socket == null && reader == null && writer == null);
	}
	
	public long getKey() {
		return key;
	}
	public void setKey(long key) {
		this.key = key;
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
		init();
	}
	
	/** 소켓 수신 및 응답 처리 쓰레드 구동 */
	public void activate() {
		threadSwitch = true;
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		StringBuilder collector = new StringBuilder("");
		String line;
		while(threadSwitch) {
			try {
				if(reader == null || writer == null || socket == null) { dispose(); break; }
				
				line = reader.readLine();
				if(line == null) { dispose(); break; }
				line = line.trim();
				
				if(! stateReading) {	
					if(line.equals(TCPSimpleDaemon.STRINGLINE_REQUEST_START)) { // 요청 내용 시작
						stateReading = true;
						collector.setLength(0);
						continue;
					} else if(line.equals(TCPSimpleDaemon.STRINGLINE_REQUEST_EXIT)) {
						threadSwitch = false;
						TCPSimpleDaemon.flagExit = true;
						break;
					}
				} else {
					if(line.equals(TCPSimpleDaemon.STRINGLINE_REQUEST_END)) { // 요청 내용 종료
						stateReading = false;
						
						// JSON 수신
						String strJson = collector.toString().trim();
						collector.setLength(0);
						
						//  JSON 파싱
						JsonObject json = (JsonObject) JsonObject.parseJson(strJson);
						strJson = null;
						
						// 요청 처리
						json = TCPSimpleDaemon.process(json);
						strJson = json.toJSON();
						json = null;
						
						// 응답 앞부분 보내기
						writer.write(TCPSimpleDaemon.STRINGLINE_REQUEST_START); writer.newLine();
						
						// 응답 본문 줄 별로 잘라서 보내기
						StringTokenizer lineTokenizer = new StringTokenizer(strJson.trim(), "\n");
						strJson = null;
						
						while(lineTokenizer.hasMoreTokens()) {
							writer.write(lineTokenizer.nextToken().trim()); writer.newLine();
						}
						
						// 응답 뒷부분 보내 마무리
						writer.write(TCPSimpleDaemon.STRINGLINE_REQUEST_END);
						lineTokenizer = null;
						
						continue;
					} else { // 요청 내용 수신 중
						collector = collector.append("\n").append(line);
					}
				}
				
			} catch(Throwable t) {
				if(! threadSwitch) return;
				t.printStackTrace();
			}
		}
		threadSwitch = false;
		dispose();
	}
}