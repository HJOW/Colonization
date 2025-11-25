package org.duckdns.hjow.gemini;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.ClassUtil;

/** Gemini 채팅 세션 */ // TODO : 공통 lib로 이관
public class GeminiSession implements Disposeable {
    protected transient final GeminiModel  model  = new GeminiModel();
    protected transient HttpURLConnection  http   = null;
    protected transient BufferedWriter     writer = null;
    
    /** 기본 생성자로, 접속이 안된 상태로 생성된다. */
    public GeminiSession() {  }
    
    /** 접속 */
    protected void connect() {
    	OutputStream       out1 = null;
    	OutputStreamWriter out2 = null;
    	try {
    		http = (HttpURLConnection) model.getURL().openConnection();
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestMethod("POST");
            
            out1   = http.getOutputStream();
            out2   = new OutputStreamWriter(out1, "UTF-8");
            writer = new BufferedWriter(out2);
    	} catch(Exception ex) {
    		dispose();
    		ClassUtil.closeAll(out2, out1);
    		throw new RuntimeException(ex.getMessage(), ex);
    	}
    }
    
    /** 채팅 요청 전송하고 응답 반환 */
    public JsonObject request(JsonObject jsonObject) {
        InputStream       inp1 = null;
        InputStreamReader inp2 = null;
        BufferedReader    inp3 = null;
        StringBuilder responseCollector = new StringBuilder("");
        boolean firsts = true;
        try {
        	String json = jsonObject.toJSON();
        	jsonObject = null;
            
            StringTokenizer lineTokenizer = new StringTokenizer(json, "\n");
            
            connect();
            while(lineTokenizer.hasMoreTokens()) {
                String line = lineTokenizer.nextToken();
                
                if(! firsts) writer.newLine();
                writer.write(line);
                
                firsts = false;
            }
            
            writer.close(); writer = null;
            http.getResponseCode();
            
            inp1 = http.getInputStream();
            inp2 = new InputStreamReader(inp1, "UTF-8");
            inp3 = new BufferedReader(inp2);
            
            firsts = true;
            String line;
            while(true) {
                line = inp3.readLine();
                if(line == null) break;
                
                if(firsts) responseCollector = responseCollector.append("\n");
                responseCollector = responseCollector.append(line);
                
                firsts = false;
            }
            
            inp3.close(); inp3 = null;
            dispose();
            
            return (JsonObject) JsonObject.parseJson(responseCollector.toString().trim());
        } catch (Exception causes) {
            if(causes instanceof RuntimeException) throw (RuntimeException) causes;
            throw new RuntimeException(causes.getMessage(), causes);
        } finally {
        	dispose();
        }
    }
    
    /** 채팅 요청 전송하고 응답 반환 */
    public JsonObject request(String question) {
    	return request(buildRequest(question));
    }
    
    /** 현재 설정된 모델 및 KEY 정보 반환 */
    public GeminiModel getModel() {
    	return model;
    }
    
    /** 모델과 KEY 등을 설정 */
    public void setModel(GeminiModel model) {
    	model.setLocation(model.getLocation());
    	model.setModelCode(model.getModelCode());
    	model.setApiKey(model.getApiKey());
    	model.setProjectId(model.getProjectId());
    }
    
    /** 접속 상태 확인 */
    public boolean isAlive() {
    	return (writer != null);
    }
    
	@Override
	public void dispose() {
		ClassUtil.closeAll(writer, http);
		writer = null;
		http   = null;
	}
	
	/** Map 을 JSON Object 으로 변환 */
	public static JsonObject convertObject(Map<?, ?> map) {
		JsonObject json = new JsonObject();
		Set<?> keys = map.keySet();
		for(Object k : keys) {
			String key = k.toString();
			Object val = map.get(key);
			if(val instanceof Map<?, ?>)    val = convertObject((Map<?, ?>) val);
			else if(val instanceof List<?>) val = convertList((List<?>) val);
			
			json.put(key, val);
		}
		return json;
	}
	
	/** List 를 Json Array 로 변환 */
	public static JsonArray convertList(List<?> list) {
		JsonArray arr = new JsonArray();
		for(Object obj : list) {
			Object val = obj;
			if(val instanceof Map<?, ?>)    val = convertObject((Map<?, ?>) val);
			else if(val instanceof List<?>) val = convertList((List<?>) val);
			
			arr.add(val);
		}
		
		return arr;
	}
	
	/** 질문 패키지 생성 (질문 문장을 Gemini API에 맞도록 구조화) (참고 : https://ai.google.dev/api?hl=ko) - 대화 맥락 보관 기능 없음 - 매번 새 채팅이 됨 */
    public static JsonObject buildRequest(String question) {
    	JsonArray contentArray = new JsonArray();
    	contentArray.add(new GeminiSpeak("user", question).toJson());
        return buildRequest(contentArray);
    }
    
    /** 질문 패키지 생성 (질문 문장을 Gemini API에 맞도록 구조화) (참고 : https://ai.google.dev/api?hl=ko) - 매번 채팅 시 채팅내용 전체를 매번 보내야 하며, buildContent 메소드를 사용하여 각 채팅 대화상대의 컨텐츠를 다 만들어 넣어야 함 */
    public static JsonObject buildRequest(JsonArray contentArray) {
        JsonObject roots = new JsonObject();
        roots.put("contents", contentArray);
        return roots;
    }
    
    /** 응답 JSON 에서, 답변 텍스트 추출 (참고 : https://ai.google.dev/api?hl=ko) */
    public static String getResponseMessage(JsonObject responseJson) {
    	JsonArray  candidates   = ((JsonArray) responseJson.get("candidates"));
        JsonObject candidateOne = (JsonObject) candidates.get(0);
        JsonObject contents     = (JsonObject) candidateOne.get("content");
        JsonArray  parts        = (JsonArray)  contents.get("parts");
        JsonObject partOne      = (JsonObject) parts.get(0);
        
        return partOne.get("text").toString();
    }
    
    /** 질문 요청하고 답변 반환 */
    public static String requestSimple(GeminiModel model, String question) {
    	GeminiSession session = new GeminiSession();
    	session.setModel(model);
    	return getResponseMessage(session.request(question));
    }
}
