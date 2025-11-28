package org.duckdns.hjow.colonization.ui.ask;

import java.util.List;

import org.duckdns.hjow.gemini.GeminiSpeak;

/** 채팅 대화내역 하나 (버그 수정버전) */ // TODO : 테스트 완료 후 제거
public class AGeminiSpeak extends GeminiSpeak {
	public AGeminiSpeak() { super(); }
	public AGeminiSpeak(String role, String ... parts) { super(role, parts); }
	public AGeminiSpeak(String role, List<String> parts) { super(role, parts); }
	
}
