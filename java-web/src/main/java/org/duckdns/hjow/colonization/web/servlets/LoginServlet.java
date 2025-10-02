package org.duckdns.hjow.colonization.web.servlets;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duckdns.hjow.colonization.web.accounts.Account;
import org.duckdns.hjow.colonization.web.accounts.AccountUtil;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.HexUtil;

import com.auth0.jwt.JWT;

public class LoginServlet extends CommonServlet {
    private static final long serialVersionUID = 2644868350197546589L;
    
    protected void doCommon(HttpServletRequest req, HttpServletResponse resp) throws Throwable {
        doBefore(req, resp);
        
        JsonObject responses = new JsonObject();
        responses.put("success", new Boolean(false));
        responses.put("message", "");
        
        try {
            String strLoginPacket1 = req.getParameter("login");
            String strLoginPacket2 = HexUtil.decodeString(strLoginPacket1);
            
            JsonObject json = (JsonObject) JsonObject.parseJson(strLoginPacket2);
            
            strLoginPacket1 = null;
            strLoginPacket2 = null;
            
            if(json.get("id") == null) throw new RuntimeException("Please input ID for login !");
            String id = json.get("id").toString();
            
            if(json.get("pw") == null) throw new RuntimeException("Please input Password for login !");
            String pw    = json.get("pw").toString();
            String pwEnc = Account.hashPassword(pw);
            
            Account acc = AccountUtil.load(id);
            if(acc == null) throw new RuntimeException("Cannot find that account.");
            
            if(! pwEnc.equals(acc.getPasswordHash())) {
                pwEnc = Account.hashPassword(pw); // 1회 더 해싱
                if(! pwEnc.equals(acc.getPasswordHash())) {
                    // TODO
                    throw new RuntimeException("Cannot find that account."); // ID 찾지 못하는 경우와 동일한 메시지 리턴
                }
            }
            
            Map<String, Object> headerContent = new HashMap<String, Object>();
            String jwt = JWT.create().withHeader(headerContent).withClaim("auth", verifyingClaim).withClaim("id", acc.getId()).withClaim("key", String.valueOf(acc.getKey())).withClaim("when", String.valueOf(System.currentTimeMillis())).sign(algJwt);
            
            resp.setHeader("jwt", jwt);
            
            responses.put("success", new Boolean(true));
            responses.put("message", "");
            responses.put("token", jwt);
        } catch(Exception ex) {
            logger.error("Error on " + this.getName() + " - " + ex.getMessage(), ex);
            responses.put("success", new Boolean(false));
            responses.put("message", ex.getMessage());
        }
        
        response(resp, responses);
        doAfter(req, resp);
    }

    @Override
    public String getName() {
        return "login"; // URL : /web/json?svName=login
    }
}
