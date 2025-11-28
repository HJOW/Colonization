package org.duckdns.hjow.colonization.ui.ask;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.ui.GUIColonyManager;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.ClassUtil;
import org.duckdns.hjow.commons.util.DataUtil;
import org.duckdns.hjow.commons.util.FileUtil;
import org.duckdns.hjow.commons.util.GUIUtil;
import org.duckdns.hjow.gemini.GeminiModel;
import org.duckdns.hjow.gemini.GeminiSession;
import org.duckdns.hjow.gemini.GeminiSpeak;

/** Gemini Assist */
public class GeminiAssist implements Disposeable {
	protected GUIColonyManager superInstance; // 직접 연관될 일이 많으므로 본 객체 그대로 가져오기
    protected JDialog     dialog;
    
    protected JPanel      pnCardRoot, pnConnect, pnChat;
    protected CardLayout  cardRoot;
    
    protected JEditorPane view;
    protected JTextField  field;
    protected JButton     btnReq;
    
    protected JTextField  tfProjId, tfApiKey;
    
    protected transient BufferedWriter writer;
    
    protected transient GeminiModel   model;
    protected transient GeminiSession session;
    
    public static final String EACH_DELIMITER = "------------------------------------------";
    
    public GeminiAssist(GUIColonyManager superInstance) {
    	this.superInstance = superInstance;
    	
    	dialog = new JDialog(superInstance.getDialog());
    	dialog.setSize(400, 600);
    	GUIUtil.centerWindow(dialog);
    	dialog.setTitle(ColonyManager.t("조언자와의 채팅"));
    	dialog.setIconImage(GUIColonyManager.getIcon());
    	
    	init();
    }
    
    /** UI 초기화 */
    protected void init() {
    	dialog.setLayout(new BorderLayout());
    	
    	JPanel pnMain = new JPanel();
    	pnMain.setLayout(new BorderLayout());
    	dialog.add(pnMain, BorderLayout.CENTER);
    	
    	pnCardRoot = new JPanel();
    	cardRoot = new CardLayout();
    	pnCardRoot.setLayout(cardRoot);
    	pnMain.add(pnCardRoot, BorderLayout.CENTER);
    	
    	pnConnect = new JPanel();
    	pnChat    = new JPanel();
    	pnConnect.setLayout(new BorderLayout());
    	pnChat.setLayout(new BorderLayout());
    	pnCardRoot.add(pnConnect, "C1");
    	pnCardRoot.add(pnChat   , "C2");
    	pnCardRoot.add(new JPanel(), "C3");
    	cardRoot.show(pnCardRoot, "C3");
    	
    	JPanel pnCenter, pnDown;
    	pnCenter = new JPanel();
    	pnDown   = new JPanel();
    	pnCenter.setLayout(new BorderLayout());
    	pnDown.setLayout(new BorderLayout());
    	pnChat.add(pnCenter, BorderLayout.CENTER);
    	pnChat.add(pnDown, BorderLayout.SOUTH);
    	
    	view = new JEditorPane();
    	view.setEditable(false);
    	view.setContentType("text/html");
    	pnCenter.add(new JScrollPane(view), BorderLayout.CENTER);
    	
    	ActionListener actionReq = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onRequestCalled();
			}
		};
    	
    	field = new JTextField();
    	field.addActionListener(actionReq);
    	pnDown.add(field, BorderLayout.CENTER);
    	
    	btnReq = new JButton(ColonyManager.t("말하기"));
    	btnReq.addActionListener(actionReq);
    	pnDown.add(btnReq, BorderLayout.EAST);
    	
    	// 접속 정보 입력 패널
    	JPanel pnGrid = new JPanel();
		pnDown = new JPanel();
		pnGrid.setLayout(new GridLayout(8, 1));
		pnDown.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnConnect.add(pnGrid, BorderLayout.NORTH);
		pnConnect.add(pnDown, BorderLayout.SOUTH);
		
		JPanel pn;
		JLabel lb;
		
		pn = new JPanel();
		pn.setLayout(new FlowLayout(FlowLayout.CENTER));
		lb = new JLabel(ColonyManager.t("Gemini Assist 인증"));
		pn.add(lb);
		pnGrid.add(pn);
		
		pn = new JPanel();
		pn.setLayout(new FlowLayout(FlowLayout.LEFT));
		lb = new JLabel(ColonyManager.t("프로젝트 ID"));
		pn.add(lb);
		pnGrid.add(pn);
		
		pn = new JPanel();
		pn.setLayout(new BorderLayout());
		tfProjId = new JTextField();
		pn.add(tfProjId);
		pnGrid.add(pn);
		
		pn = new JPanel();
		pn.setLayout(new FlowLayout(FlowLayout.LEFT));
		lb = new JLabel(ColonyManager.t("API KEY"));
		pn.add(lb);
		pnGrid.add(pn);
		
		pn = new JPanel();
		pn.setLayout(new BorderLayout());
		tfApiKey = new JTextField();
		pn.add(tfApiKey);
		pnGrid.add(pn);
		
		pn = new JPanel();
		pnGrid.add(pn);
		
		pn = new JPanel();
		pnGrid.add(pn);
		
		pn = new JPanel();
		pnGrid.add(pn);
		
		JButton btnCancel, btnOk;
		btnOk     = new JButton(ColonyManager.t("접속"));
		btnCancel = new JButton(ColonyManager.t("취소"));
		pnDown.add(btnOk);
		pnDown.add(btnCancel);
		
		btnOk.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				cardRoot.show(pnCardRoot, "C3");
				if(model == null) model = new GeminiModel();
				
				JsonObject json = loadConfig();
				
				String projId, apiKey, loc, modelCode;
				projId    = tfProjId.getText();
				apiKey    = tfApiKey.getText();
				loc       = null;
				modelCode = null;
				
				if(json.containsKey("location")) loc = json.get("location").toString();
				if(DataUtil.isEmpty(loc)) loc = GeminiModel.LOCATION_SINGAPOLE;
				
				if(json.containsKey("model")) modelCode = json.get("model").toString();
				if(DataUtil.isEmpty(modelCode)) modelCode = GeminiModel.MODEL_2_5_FLASH_LITE;
				
				model.setProjectId(projId);
				model.setApiKey(apiKey);
				model.setLocation(loc);
				model.setModelCode(modelCode);
				
				json.put("projectId"  , model.getProjectId());
				json.put("apkKey"     , model.getApiKey());
				json.put("location"   , model.getLocation());
				json.put("model"      , model.getModelCode());
				
				saveConfig(json);
				connect();
			}
		});
		
		btnCancel.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				cardRoot.show(pnCardRoot, "C3");
				dialog.setVisible(false);
			}
		});
    }
    
    /** Gemini 설정 읽기 */
    protected JsonObject loadConfig() {
    	File root   = null;
    	File config = null;
    	try {
    		root = superInstance.getColonyAssistRootDirectory();
        	if(! root.exists()) root.mkdirs();
        	
        	config = new File(root.getAbsolutePath() + File.separator + "config.json");
        	if(! config.exists()) {
        		FileUtil.writeString(config, "UTF-8", "{}");
        	}
        	
        	String str = FileUtil.readString(config, "UTF-8");
        	JsonObject json = (JsonObject) JsonObject.parseJson(str);
        	
        	return json;
    	} catch(Throwable ex) {
    		if(config != null) {
    			try { FileUtil.writeString(config, "UTF-8", "{}"); } catch(Exception ignores) {}
    		}
    		
    		put("system", ColonyManager.t("오류") + " : " + ex.getMessage());
    		GlobalLogs.processExceptionOccured(ex, true);
    		disconnect();
    		return null;
    	}
    }
    /** Gemini 설정 저장 */
    protected void saveConfig(JsonObject json) {
    	File root   = null;
    	File config = null;
    	try {
    		root = superInstance.getColonyAssistRootDirectory();
        	if(! root.exists()) root.mkdirs();
        	
        	config = new File(root.getAbsolutePath() + File.separator + "config.json");
        	FileUtil.writeString(config, "UTF-8", json.toJSON());
    	} catch(Throwable ex) {
    		GlobalLogs.processExceptionOccured(ex, true);
    	}
    }
    
    /** 접속 */
    protected void connect() {
    	disconnect();
    	
    	try {
    		File root = superInstance.getColonyAssistRootDirectory();
        	if(! root.exists()) root.mkdirs();
        	
        	File conversations = new File(root.getAbsolutePath() + File.separator + "conversation.txt");
        	if(! conversations.exists()) FileUtil.writeString(conversations, "UTF-8", "");
        	
        	if(writer != null) { ClassUtil.closeAll(writer); }
        	writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(conversations, true), "UTF-8"));
        	
        	JsonObject json = loadConfig();
        	
        	String projectId, apiKey;
        	projectId = null;
        	apiKey = null;
        	
        	if(json.containsKey("projectId")) projectId = json.get("projectId").toString();
        	if(json.containsKey("apkKey"   )) apiKey    = json.get("apkKey").toString();
        	
        	if(DataUtil.isEmpty(projectId) || DataUtil.isEmpty(apiKey)) {
        		tfProjId.setText("");
        		tfApiKey.setText("");
        		cardRoot.show(pnCardRoot, "C1");
        		return;
        	}
        	
        	session = new AGeminiSession();
        	model.setApiKey(apiKey);
        	model.setProjectId(projectId);
        	
        	field.setEnabled(true);
        	cardRoot.show(pnCardRoot, "C2");
    	} catch(Throwable ex) {
    		put("system", ColonyManager.t("오류") + " : " + ex.getMessage());
    		GlobalLogs.processExceptionOccured(ex, true);
    		disconnect();
    	}
    }
    
    /** Gemini 세션 접속 해제 */
    protected void disconnect() {
    	if(session != null) {
    		session.dispose();
    		session = null;
    	}
    	
    	if(writer != null) {
    		ClassUtil.closeAll(writer);
    		writer = null;
    	}
    	
    	field.setEnabled(false);
    	
    	tfProjId.setText("");
		tfApiKey.setText("");
    	cardRoot.show(pnCardRoot, "C1");
    }
    
    /** 대화상자 열기 */
    public void open() {
    	if(model == null || session == null) {
    		connect();
    	} else {
    		cardRoot.show(pnCardRoot, "C2");
    	}
    	
    	dialog.setVisible(true);
    	field.requestFocus();
    }

	@Override
	public void dispose() {
		if(dialog != null) {
			if(dialog.isVisible()) dialog.setVisible(false);
		}
		disconnect();
		
		if(writer != null) { ClassUtil.closeAll(writer); writer = null; }
		
		dialog.removeAll();
		dialog = null;
		
		if(session != null) session.dispose();
		session = null;
		
		superInstance = null;
	}
	
	/** 대화 내용 추가 */
	protected void put(String whoSpeaks, String msg) {
		try {
			File root = superInstance.getColonyAssistRootDirectory();
        	if(! root.exists()) root.mkdirs();
        	
        	File conversations = new File(root.getAbsolutePath() + File.separator + "conversation.txt");
        	if(! conversations.exists()) FileUtil.writeString(conversations, "UTF-8", "");
        	
			if(writer == null) {
	        	writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(conversations, true), "UTF-8"));
			}
			
		    writer.write(EACH_DELIMITER);
			writer.newLine();
			
			if(whoSpeaks.equals("model")) whoSpeaks = "Assist";
			
			StringTokenizer lineTokenizer = new StringTokenizer(msg, "\n");
			String firstLine = lineTokenizer.nextToken();
			String prefix    = "[" + whoSpeaks.trim() + "] ";
			String indents   = "";
			for(int idx=0; idx<prefix.length(); idx++) { indents += " "; }
			
			writer.write(prefix + firstLine);
			writer.newLine();
			
			while(lineTokenizer.hasMoreTokens()) {
				String line = lineTokenizer.nextToken();
				writer.write(indents + line);
				writer.newLine();
			}
			
			view.setPage(conversations.toURI().toURL());
			field.requestFocus();
		} catch(Throwable tx) {
			GlobalLogs.processExceptionOccured(tx, true);
			field.setEnabled(false);
		}
	}
	
	/** 과거 대화내용 불러오기 */
	protected List<GeminiSpeak> getHistory() {
		List<GeminiSpeak> speaks = new ArrayList<GeminiSpeak>();
		
		BufferedReader inp1 = null;
		try {
			File root = superInstance.getColonyAssistRootDirectory();
        	if(! root.exists()) root.mkdirs();
        	
        	File conversations = new File(root.getAbsolutePath() + File.separator + "conversation.txt");
        	if(! conversations.exists()) FileUtil.writeString(conversations, "UTF-8", "");
        	
        	inp1 = new BufferedReader(new InputStreamReader(new FileInputStream(conversations)));
        	String line;
        	
        	GeminiSpeak inst = null;
        	StringBuilder content = new StringBuilder("");
        	boolean firstDelimiterPassed = false;
        	boolean firstContent = false;
        	
        	while(true) {
        		line = inp1.readLine();
        		if(line == null) break;
        		
        		if(line.trim().equals(EACH_DELIMITER)) {
        			firstDelimiterPassed = true;
        			
        			if(inst != null) {
        				ArrayList<String> singles = new ArrayList<String>();
        				singles.add(content.toString().trim());
        				content.setLength(0);
        				inst.setParts(singles);
        				if(DataUtil.isNotEmpty(inst.getRole())) speaks.add(inst);
        				inst = null;
        			}
        			
        			inst = new AGeminiSpeak();
        			content.setLength(0);
        			firstContent = true;
        			continue;
        		} else if(firstDelimiterPassed) {
        			continue;
        		} else {
        			if(inst == null) inst = new AGeminiSpeak();
        			String appendee = line;
        			
        			if(firstContent) {
        				String[] splits = line.split("]");
        				String whoSpeaks = splits[0].substring(1);
        				whoSpeaks = whoSpeaks.trim();
        				if(whoSpeaks.equals("Assist")) whoSpeaks = "model";
        				inst.setRole(whoSpeaks.trim());
        				appendee = line.substring(("[" + whoSpeaks + "]").length());
        			}
        			firstContent = false;
        			content = content.append("\n").append(appendee.trim());
        		}
        	}
        	if(inst != null) {
        		ArrayList<String> singles = new ArrayList<String>();
				singles.add(content.toString().trim());
				content.setLength(0);
				inst.setParts(singles);
				if(DataUtil.isNotEmpty(inst.getRole())) speaks.add(inst);
        	}
        	
		} catch(Throwable tx) {
			GlobalLogs.processExceptionOccured(tx, true);
			field.setEnabled(false);
		}
		
		return speaks;
	}
	
	/** 채팅 요청 시 호출 */
	protected void onRequestCalled() {
		if(model == null || session == null) {
			connect();
		}
		
		// 대화 내용 불러오기
		List<GeminiSpeak> speaks = getHistory();
		
		// 현재의 질문 준비
		String question = field.getText();
		GeminiSpeak speak = new AGeminiSpeak("user", question);
		speaks.add(speak);
		
		// Json 준비
		JsonArray arr = new JsonArray();
		for(GeminiSpeak sp : speaks) {
			arr.add(sp.toJson());
		}
		
		// 요청 발송 및 응답 수신
		JsonObject responseJson = session.request(GeminiSession.buildRequest(arr));
		JsonArray  candidates   = ((JsonArray) responseJson.get("candidates"));
		JsonObject candidateOne = (JsonObject) candidates.get(0);
		JsonObject contents     = (JsonObject) candidateOne.get("content");
		
		String     role         = contents.get("role").toString();
		JsonArray  parts        = (JsonArray)  contents.get("parts");
		for(Object obj : parts) {
			JsonObject part = (JsonObject) obj;
			String text = part.get("text").toString();
			put(role, text);
		}
		
	}
}