package org.duckdns.hjow.colonization.web.servlets
import org.duckdns.hjow.colonization.web.servlets.CommonServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.duckdns.hjow.commons.json.JsonObject

/** 코틀린 서블릿 구현 가능성 테스트용 서블릿 */
class TestServlet : CommonServlet() { // 부모 클래스 이름 옆 () 를 넣어 기본 생성자 부여
    public override fun doCommon(req: HttpServletRequest, resp: HttpServletResponse) {
        val responses = JsonObject();
        responses.put("success", true);
        responses.put("message", "Hello");
        
        response(resp, responses);
        doAfter(req, resp);
    }
    
    public override fun getName(): String {
        return "TEST";
    }
}