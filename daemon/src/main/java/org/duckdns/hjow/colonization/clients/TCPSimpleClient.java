package org.duckdns.hjow.colonization.clients;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import org.duckdns.hjow.colonization.ColonyClassLoader;
import org.duckdns.hjow.colonization.daemon.TCPSimpleDaemon;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.core.Releasable;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.ClassUtil;

/** TCPSimpleDaemon 에 대응하는 Client Util */
public class TCPSimpleClient {
    public static Colony request(Colony col, String host, int port, String charset) {
    	Socket socket = null;
    	BufferedReader reader = null;
    	BufferedWriter writer = null;
    	ResponseReceiver receiver = null;
    	Throwable caused = null;
    	Colony responsed = null;
    	
    	try {
    		// 전송할 문자열 준비
    		String strJson = col.toJson().toJSON();
    		StringTokenizer lineTokenizer = new StringTokenizer(strJson.trim(), "\n");
    		strJson = null;
    		
    		// TCP 접속
    		socket = new Socket(host, port);
    		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()  , charset));
		    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), charset));
		    receiver = new ResponseReceiver(reader);
		    
		    // 빈 줄 전송 (무시될 것. TCP 연결 테스트 목적)
		    writer.newLine();
		    
		    // 시작 파트 전송
		    writer.write(TCPSimpleDaemon.STRINGLINE_REQUEST_START); writer.newLine();
		    
		    // 요청 본문 전송
		    while(lineTokenizer.hasMoreTokens()) {
		    	writer.write(lineTokenizer.nextToken().trim()); writer.newLine();
		    }
		    lineTokenizer = null;
		    
		    // 종료 파트 전송
		    writer.write(TCPSimpleDaemon.STRINGLINE_REQUEST_END);
			
		    // 응답 수신까지 대기
		    while(! receiver.isDisposed()) {
		    	Thread.sleep(100L);
		    }
		    
		    // 응답 내용
		    strJson = receiver.collector.toString().trim();
		    
		    // JSON 파싱
		    JsonObject json = (JsonObject) JsonObject.parseJson(strJson);
		    strJson = null;
		    
		    json = (JsonObject) json.get("colony");
		    
		    // JSON 으로부터 Colony 불러오기
		    responsed = ColonyClassLoader.loadColony(json);
		    json = null;
    	} catch(Throwable t) {
    		responsed = null;
    		caused = t;
    	} finally {
    		ClassUtil.closeAll(reader, writer, receiver, socket);
    	}
    	
    	if(caused != null) throw new RuntimeException(caused.getMessage(), caused);
    	return responsed;
    }
}

/** 응답 수신 유틸 */
class ResponseReceiver implements Runnable, Releasable, Disposeable {
	private static final long serialVersionUID = -5434742586149201612L;
	volatile transient StringBuilder  collector = new StringBuilder("");
	volatile transient BufferedReader reader;
	
	volatile transient boolean threadSwitch = true;
	volatile transient Thread  thread       = null;
	
    public ResponseReceiver(BufferedReader reader) {
    	this.reader = reader;
    	threadSwitch = true;
    	thread = new Thread(this);
    	thread.start();
    }
    
	@Override
	public void run() {
		String line;
		boolean stateReading = false;
		while(threadSwitch) {
			try {
				line = reader.readLine();
				if(line == null) { dispose(); break; }
				
				line = line.trim();
				if(! stateReading) {
					if(line.equals(TCPSimpleDaemon.STRINGLINE_REQUEST_START)) { // 요청 내용 시작
						stateReading = true;
						collector.setLength(0);
						continue;
					} else {
						continue; // Do nothing
					}
				} else {
					if(line.equals(TCPSimpleDaemon.STRINGLINE_REQUEST_END)) { // 요청 내용 종료
						dispose();
						break;
					} else {
						collector = collector.append("\n").append(line);
					}
				}
			} catch(Throwable t) {
				t.printStackTrace();
			}
		}
		threadSwitch = false;
	}
	
	public boolean isDisposed() {
		return (! threadSwitch);
	}

	@Override
	public void dispose() {
		threadSwitch = false;
		if(thread != null) { try { thread.interrupt(); } catch(SecurityException ex) {} }
	}
	
	@Override
	public void releaseResource() {
		dispose();
	}
	
}