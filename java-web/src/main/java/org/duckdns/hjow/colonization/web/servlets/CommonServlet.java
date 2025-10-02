package org.duckdns.hjow.colonization.web.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.duckdns.hjow.colonization.web.accounts.Account;
import org.duckdns.hjow.colonization.web.accounts.AccountUtil;
import org.duckdns.hjow.colonization.web.key.Key;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.HexUtil;
import org.duckdns.hjow.commons.util.SecurityUtil;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

public abstract class CommonServlet extends HttpServlet {
    private static final long serialVersionUID = -9127592158446596240L;
    protected Logger logger = LogManager.getLogger(this.getClass());
    protected Algorithm algJwt;
    protected String    verifyingClaim = "";

    @Override
    public void init() throws ServletException {
        super.init(); 
        String jwtKey = "Hello";
        try {
            Key key = (Key) Class.forName("org.duckdns.hjow.colonization.web.key.CurrentKey").newInstance();
            jwtKey = key.getJWTKey();
            verifyingClaim = key.getJWTVerifyingClaim();
        } catch(Exception ex) {
            logger.error("Error on init " + this.getClass().getSimpleName(), ex);
        }
        algJwt = Algorithm.HMAC384(SecurityUtil.hash( jwtKey , "SHA-384"));
    }
    
    @Override
    public void destroy() { super.destroy(); }
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            doCommon(req, resp);
        } catch(Throwable tx) {
            logger.error("Exception on doGet", tx);
        }
    }
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            doCommon(req, resp);
        } catch(Throwable tx) {
            logger.error("Exception on doPost", tx);
        }
    }
    
    protected abstract void doCommon(HttpServletRequest req, HttpServletResponse resp) throws Throwable;
    public    abstract String getName();
    
    /** 로그인 필요 여부 반한 */
    protected boolean isLoginNeeded() { return false; }
    
    /** 해당 등급 이상만 접근 가능한지를 반환 */
    protected int getMinimumGradeRequired() { return 0; }
    
    /** JSON 을 응답으로 내보냄 */
    public void response(HttpServletResponse resp, JsonObject json) {
        
    }
    
    /** doCommon 메소드 내 맨 앞에서 반드시 호출 ! */
    protected void doBefore(HttpServletRequest req, HttpServletResponse resp) throws Throwable {
        boolean logined = false;
        
        // JWT 토큰이 있는 경우 로그인 여부 판단
        String jwt = req.getHeader("jwt"); // 먼저 헤더에 있는지 체크
        if(jwt == null) {
            jwt = req.getParameter("jwt"); // 헤더에 없으면 매개변수에 있는지 체크
            if(jwt != null) jwt = HexUtil.decodeString(jwt); // 매개변수의 경우 HEX로 인코딩된 값이 넘어올 테니 디코딩해 사용
        }
        
        if(jwt != null) {
            try {
                JWTVerifier veri = JWT.require(algJwt).withClaim("auth", verifyingClaim).build();
                
                if(jwt != null) {
                    DecodedJWT decoded = veri.verify(jwt);
                    
                    Claim claim = decoded.getClaim("id");
                    String id = claim.asString();
                    
                    Account acc = AccountUtil.load(id);
                    if(acc != null) {
                        req.setAttribute("id"   ,  id);
                        req.setAttribute("key"  , new Long(acc.getKey()));
                        req.setAttribute("name" , acc.getName());
                        req.setAttribute("grade", new Integer(acc.getGrade()));
                        logined = true;
                    }
                }
            } catch(Exception ex) {
                logger.error("Error on doBefore when checking jwt token " + ex.getMessage(), ex);
                logined = false;
            }
        }
        
        if(isLoginNeeded()) {
            if(! logined) throw new RuntimeException("Please login first !");
        }
        
        int grade = 0;
        if(req.getAttribute("grade") != null) grade = ((Integer) req.getAttribute("grade")).intValue();
        
        if(grade < getMinimumGradeRequired()) {
            throw new RuntimeException("No privileges");
        }
    }
    
    /** doCommon 메소드 내 맨 뒤에서 반드시 호출 ! */
    protected void doAfter(HttpServletRequest req, HttpServletResponse resp) throws Throwable {
        
    }
}
