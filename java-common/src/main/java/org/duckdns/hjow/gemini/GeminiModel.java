package org.duckdns.hjow.gemini;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

/** 
 * 
 * Gemini 모델 정보 // TODO : 공통 lib로 이관 
 *
 *     Gemini API 서비스 약관 : https://ai.google.dev/gemini-api/terms?hl=ko
 *     
 */
public class GeminiModel implements Serializable {
	private static final long serialVersionUID = -7306392778523676115L;

	public static final String MODEL_2_5_FLASH_LITE = "gemini-2.5-flash-lite";
	public static final String MODEL_2_5_FLASH      = "gemini-2.5-flash";
	public static final String MODEL_2_5_PRO        = "gemini-2.5-pro";
	public static final String MODEL_3_PRO_PREV     = "gemini-3-pro-preview";
	
	public static final String LOCATION_SINGAPOLE   = "asia-southeast1";
	public static final String LOCATION_FINLAND     = "europe-north1";
	public static final String LOCATION_BELGIUM     = "europe-west1";
	public static final String LOCATION_CENTRAL     = "us-central1";
	
    protected String modelCode = MODEL_2_5_FLASH_LITE;
    protected String projectId = "";
    protected String apiKey    = "";
    protected String location  = LOCATION_SINGAPOLE;
    
    public GeminiModel() {}
    public GeminiModel(String projectId, String apiKey) {
		this();
		this.projectId = projectId;
		this.apiKey = apiKey;
	}
	public GeminiModel(String modelCode, String projectId, String apiKey, String location) {
		this();
		this.modelCode = modelCode;
		this.projectId = projectId;
		this.apiKey = apiKey;
		this.location = location;
	}
	public String getModelCode() {
		return modelCode;
	}
	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	/** 요청을 보낼 URL 반환 */
    public URL getURL() {
        try {
            return new URL("https://generativelanguage.googleapis.com/v1beta/models/[MODEL]:generateContent?key=[KEY]".replace("[MODEL]", getModelCode()).replace("[KEY]", getApiKey()));
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
