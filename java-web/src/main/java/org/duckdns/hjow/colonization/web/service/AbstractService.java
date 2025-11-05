package org.duckdns.hjow.colonization.web.service;

import org.duckdns.hjow.colonization.web.accounts.Account;
import org.duckdns.hjow.colonization.web.accounts.AccountUtil;
import org.duckdns.hjow.commons.util.HexUtil;

/** 서비스 (Servlet 대용) - kotlin 사용 시 Servlet API 때문에 문제가 발생하는 듯. */
public abstract class AbstractService implements Service {
//	protected Logger logger = LogManager.getLogger(this.getClass());
	public AbstractService() { init(); }
	
    /** 서비스 초기화 */
    public void init() { }
    
    @Override
    public void dispose() {};
    
    /** 로그인 필요 여부 반환 */
    @Override
    public boolean isLoginNeeded() { return false; }
    
    /** 해당 등급 이상만 접근 가능한지를 반환 */
    @Override
    public int getMinimumGradeRequired() { return 0; }
    
    /** 서비스 동작 사전작업, MainServlet 제외 다른데서 호출하지 말 것 ! */
    @Override
    public final void doBefore(StringMap parameters, StringMap headers, AttributeMap attributeReferenceMap) {
        boolean logined = false;
        
        // JWT 토큰이 있는 경우 로그인 여부 판단
        String jwt = headers.get("jwt"); // 먼저 헤더에 있는지 체크
        if(jwt == null) {
            jwt = parameters.get("jwt"); // 헤더에 없으면 매개변수에 있는지 체크
            if(jwt != null) jwt = HexUtil.decodeString(jwt); // 매개변수의 경우 HEX로 인코딩된 값이 넘어올 테니 디코딩해 사용
        }
        
        if(jwt != null) {
            try {
                Account acc = AccountUtil.verifyJWT(jwt);
                if(acc != null) {
                    attributeReferenceMap.put("id"   , acc.getId());
                    attributeReferenceMap.put("key"  , new Long(acc.getKey()));
                    attributeReferenceMap.put("name" , acc.getName());
                    attributeReferenceMap.put("grade", new Integer(acc.getGrade()));
                    logined = true;
                }
            } catch(Exception ex) {
                System.out.println("Error on doBefore when checking jwt token " + ex.getMessage());
                logined = false;
            }
        }
        
        if(isLoginNeeded()) {
            if(! logined) throw new RuntimeException("Please login first !");
        }
        
        int grade = 0;
        if(attributeReferenceMap.get("grade") != null) grade = ((Integer) attributeReferenceMap.get("grade")).intValue();
        
        if(grade < getMinimumGradeRequired()) {
            throw new RuntimeException("No privileges");
        }
    }
}
