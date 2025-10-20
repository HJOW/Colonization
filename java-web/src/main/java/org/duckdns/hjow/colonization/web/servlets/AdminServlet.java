package org.duckdns.hjow.colonization.web.servlets;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duckdns.hjow.colonization.web.accounts.Account;
import org.duckdns.hjow.colonization.web.db.ColonizationMapper;
import org.duckdns.hjow.colonization.web.db.DBUtil;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;

public class AdminServlet extends CommonServlet {
	private static final long serialVersionUID = -2017938310629250602L;

	@Override
	protected boolean isLoginNeeded() { return true; }
	
	@Override
	protected int getMinimumGradeRequired() { return 9; }
	
	@Override
	public String getName() {
		return "admin"; // URL : /web/json?svName=admin
	}

	@Override
	protected void doCommon(HttpServletRequest req, HttpServletResponse resp) throws Throwable {
        doBefore(req, resp);
        
        JsonObject responses = new JsonObject();
        responses.put("success", new Boolean(false));
        responses.put("message", "");
        
        try {
        	String svSub1 = getParameter(req, "svSub");
        	if(svSub1 == null) throw new RuntimeException("No sub keyword !");
        	
        	Account acc = checkLogined(req);
        	
        	if(svSub1.equalsIgnoreCase("sql")) {
        		sql(req, acc, responses);
        	}
        	
        } catch(RuntimeException ex) {
            responses.put("success", new Boolean(false));
            responses.put("message", ex.getMessage());
        } catch(Throwable ex) {
            logger.error("Error on " + this.getName() + " - " + ex.getMessage(), ex);
            responses.put("success", new Boolean(false));
            responses.put("message", ex.getMessage());
        }
        
        response(resp, responses);
        doAfter(req, resp);
	}

	protected void sql(HttpServletRequest req, Account acc, JsonObject responses) throws Throwable {
		String sql = getParameter(req, "sql");
		if(sql == null) sql = "SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS";
		
		ColonizationMapper mapper = DBUtil.openMapper();
		List<Map<String, Object>> list = mapper.selectCustomSql(sql);
		JsonArray arr = new JsonArray();
		for(Map<String, Object> row : list) {
			JsonObject json = new JsonObject();
			json.putAll(row);
			arr.add(json);
		}
		responses.put("list", arr);
		
		responses.put("success", new Boolean(true));
        responses.put("message", "");
	}
}
