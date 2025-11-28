package org.duckdns.hjow.colonization.ui.ask;

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
	
}
