package org.duckdns.hjow.colonization.ui.ask;

import java.util.List;

import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.gemini.GeminiSpeak;

/** 채팅 대화내역 하나 (버그 수정버전) */ // TODO : 테스트 완료 후 제거
public class AGeminiSpeak extends GeminiSpeak {
	public AGeminiSpeak() { super(); }
	public AGeminiSpeak(String role, String ... parts) { super(role, parts); }
	public AGeminiSpeak(String role, List<String> parts) { super(role, parts); }
	
	@Override
	public void fromJson(JsonObject json) {
		setRole(json.get("role").toString());
		
		JsonArray list = null;
        try { list = (JsonArray) json.get("parts"); } catch(Exception ex) { throw new RuntimeException(ex.getMessage(), ex); }
        parts.clear();
        if(list != null) {
            for(Object o : list) {
            	JsonObject part = (JsonObject) o;
                String str = part.get("text") == null ? "" : part.get("text").toString();
                
                parts.add(str);
            }
        }
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.put("role", getRole());
		
		JsonArray arr = new JsonArray();
		for(String str : getParts()) {
			JsonObject part = new JsonObject();
			part.put("text", str);
			
			arr.add(part);
		}
		json.put("parts", arr);
		return json;
	}
}
