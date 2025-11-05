package org.duckdns.hjow.colonization.web.service;

import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.json.JsonObject;

/** 서비스 (Servlet 대용) - kotlin 사용 시 Servlet API 때문에 문제가 발생하는 듯. */
public interface Service extends Disposeable {
	/** 서비스 초기화 */
    public void init();    
    /** 로그인 필요 여부 반환 */
    public boolean isLoginNeeded();
    /** 해당 등급 이상만 접근 가능한지를 반환 */
    public int getMinimumGradeRequired();
    /** 이 서비스 이름 반환 */
    public String getName();
    /** 서비스 동작 후 전송할 응답 반환 */
    public JsonObject doCommon(StringMap parameters, StringMap headers, AttributeMap attribute) throws Throwable;
    /** 서비스 동작 사전작업  */
    public void doBefore(StringMap parameters, StringMap headers, AttributeMap attributeReferenceMap);
}
