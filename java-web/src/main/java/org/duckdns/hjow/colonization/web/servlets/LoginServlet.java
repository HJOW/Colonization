package org.duckdns.hjow.colonization.web.servlets;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duckdns.hjow.colonization.constants.StaticMethods;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.NormalColony;
import org.duckdns.hjow.colonization.web.accounts.Account;
import org.duckdns.hjow.colonization.web.accounts.AccountUtil;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.DataUtil;

public class LoginServlet extends CommonServlet {
    private static final long serialVersionUID = 2644868350197546589L;
    
    @Override
    protected boolean isLoginNeeded() { return false; }
    
    @Override
    protected void doCommon(HttpServletRequest req, HttpServletResponse resp) throws Throwable {
        doBefore(req, resp);
        
        JsonObject responses = new JsonObject();
        responses.put("success", new Boolean(false));
        responses.put("message", "");
        responses.put("result", new Boolean(false));
        
        try {
            String svSub1 = getParameter(req, "svSub");
            if(svSub1 == null) throw new RuntimeException("No sub keyword !");
            if(svSub1.equalsIgnoreCase("login")) {
                String strLoginPacket1 = getParameter(req, "login");
                String strLoginPacket2 = StaticMethods.decodeString(strLoginPacket1);
                
                JsonObject json = (JsonObject) JsonObject.parseJson(strLoginPacket2);
                
                strLoginPacket1 = null;
                strLoginPacket2 = null;
                
                if(DataUtil.isEmpty(json.get("id"))) throw new RuntimeException("Please input ID for login !");
                String id = json.get("id").toString();
                
                if(DataUtil.isEmpty(json.get("pw"))) throw new RuntimeException("Please input Password for login !");
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
                String jwt = AccountUtil.buildJWT(acc, headerContent);
                jwt = StaticMethods.encodeString(jwt);
                
                resp.setHeader("jwt", jwt);
                responses.put("token", jwt);
                
                JsonArray arr = new JsonArray();
                for(Colony c : acc.getColonies()) {
                    JsonObject row = new JsonObject();
                    row.put("name", c.getName());
                    row.put("key" , String.valueOf(c.getKey()));
                    arr.add(row);
                }
                
                responses.put("list", arr);
                responses.put("success", new Boolean(true));
                responses.put("message", "");
            } else if(svSub1.equals("check")) {
                String jwtMaybe = req.getHeader("jwt");
                if(jwtMaybe == null || "".equals(jwtMaybe)) jwtMaybe = getParameter(req, "jwt");
                if(jwtMaybe == null || "".equals(jwtMaybe)) {
                    responses.put("success", new Boolean(false));
                    responses.put("message", "No JWT token !");
                    responses.put("result", new Boolean(false));
                } else {
                    jwtMaybe = StaticMethods.decodeString(jwtMaybe);
                    Account acc = AccountUtil.verifyJWT(jwtMaybe);
                    if(acc == null) {
                        responses.put("success", new Boolean(true));
                        responses.put("message", "");
                        responses.put("result", new Boolean(false));
                    } else {
                        responses.put("success", new Boolean(true));
                        responses.put("message", "");
                        responses.put("result", new Boolean(true));
                        
                        responses.put("id", acc.getId());
                        responses.put("name", acc.getName());
                    }
                }
            } else if(svSub1.equals("join")) {
                String strLoginPacket1 = getParameter(req, "login");
                String strLoginPacket2 = StaticMethods.decodeString(strLoginPacket1);
                
                JsonObject json = (JsonObject) JsonObject.parseJson(strLoginPacket2);
                
                strLoginPacket1 = null;
                strLoginPacket2 = null;
                
                if(DataUtil.isEmpty(json.get("id"))) throw new RuntimeException("Please input ID to join !");
                String id = json.get("id").toString();
                
                String idChecker = Account.removeProhibitedChars(id);
                if(! idChecker.equals(id)) throw new RuntimeException("Please input ID correct !");
                
                if(AccountUtil.existingId(id)) throw new RuntimeException("Someone using that ID already !");
                
                if(DataUtil.isEmpty(json.get("name"))) throw new RuntimeException("Please input your name to join !");
                String name = json.get("name").toString();
                
                if(DataUtil.isEmpty(json.get("pw"))) throw new RuntimeException("Please input Password to join !");
                String pw = json.get("pw").toString();
                
                Account newAcc = new Account();
                newAcc.setId(id);
                newAcc.setName(name);
                newAcc.setPassword(pw);
                newAcc.setGrade(1);
                newAcc.setStatus(1);
                
                Colony newCol = new NormalColony();
                newCol.newCity();
                newAcc.getColonies().add(newCol);
                
                AccountUtil.save(newAcc);
                
                responses.put("success", new Boolean(true));
                responses.put("message", "");
            }
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
