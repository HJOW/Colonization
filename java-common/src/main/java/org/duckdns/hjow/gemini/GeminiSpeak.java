package org.duckdns.hjow.gemini;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.commons.core.JsonCompatible;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;

/** 채팅 대화 하나 (참고 : https://ai.google.dev/api?hl=ko) */ // TODO 공통 lib로 이관
public class GeminiSpeak implements JsonCompatible {
	protected String role;
	protected List<String> parts;
	
	public GeminiSpeak() { if(this.parts == null) this.parts = new ArrayList<String>(); }
	public GeminiSpeak(String role, String ... parts) {
		this();
		this.role = role;
		
		if(this.parts == null) this.parts = new ArrayList<String>();
		else this.parts.clear();
		for(String p : parts) { this.parts.add(p); }
	}
	public GeminiSpeak(String role, List<String> parts) {
		this();
		this.role  = role;
		this.parts = parts;
		if(this.parts == null) this.parts = new ArrayList<String>();
	}
	
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public List<String> getParts() {
		return parts;
	}
	public void setParts(List<String> parts) {
		this.parts = parts;
	}

	@Override
	public Object cloneThis() {
		GeminiSpeak newOne = new GeminiSpeak();
		newOne.setRole(getRole());
		newOne.setParts(getParts());
		return newOne;
	}

	@Override
	public void fromJson(JsonObject json) {
		setRole(json.get("role").toString());
		
		JsonArray list = null;
        try { list = (JsonArray) json.get("parts"); } catch(Exception ex) { throw new RuntimeException(ex.getMessage(), ex); }
        parts.clear();
        if(list != null) {
            for(Object o : list) {
                String part = o.toString();
                parts.add(part);
            }
        }
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.put("role", getRole());
		
		JsonArray arr = new JsonArray();
		for(String part : getParts()) {
			arr.add(part);
		}
		json.put("parts", arr);
		return json;
	}
}
