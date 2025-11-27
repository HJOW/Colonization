package org.duckdns.hjow.colonization.ui.ask;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.ClassUtil;
import org.duckdns.hjow.gemini.GeminiModel;
import org.duckdns.hjow.gemini.GeminiSession;

/** GeminiSession 버그 임시수정  */ // TODO : 테스트 완료 후 제거
public class AGeminiSession extends GeminiSession {
	/** 모델과 KEY 등을 설정 */
	@Override
    public void setModel(GeminiModel model) {
    	this.model.setLocation(model.getLocation());
    	this.model.setModelCode(model.getModelCode());
    	this.model.setApiKey(model.getApiKey());
    	this.model.setProjectId(model.getProjectId());
    }
	
	/** 접속 */
	@Override
    protected void connect() {
    	OutputStream       out1 = null;
    	OutputStreamWriter out2 = null;
    	try {
    		// http = (HttpURLConnection) model.getURL().openConnection();
    		http = (HttpURLConnection) new URL("https://generativelanguage.googleapis.com/v1beta/models/[MODEL]:generateContent".replace("[MODEL]", model.getModelCode())).openConnection();
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("x-goog-api-key", model.getApiKey());
            
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
	@Override
    public JsonObject request(JsonObject jsonObject) {
    	System.out.println(jsonObject.toJSON());
    	return super.request(jsonObject);
    }
}
