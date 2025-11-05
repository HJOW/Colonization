package org.duckdns.hjow.colonization.web.service

import org.duckdns.hjow.commons.json.JsonObject
import org.duckdns.hjow.commons.data.StringMap
import org.duckdns.hjow.commons.data.AttributeMap

/** 코틀린 서블릿 구현 가능성 테스트용 서비스 */
class TestService : AbstractService() { // 부모 클래스 이름 옆 () 를 넣어 기본 생성자 부여
    public override fun getName(): String {
        return "TEST";
    }
    
    public override fun doCommon(parameters: StringMap, headers: StringMap, attribute: AttributeMap): JsonObject {
        val responses = JsonObject();
        responses.put("success", true);
        responses.put("message", "Hello");
        return responses;
    }
}