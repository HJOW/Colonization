package org.duckdns.hjow.colonization.web.servlets;

import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.web.accounts.Account;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;

public class ColonyServlet extends CommonServlet {
	private static final long serialVersionUID = 4083159248474182017L;
	
	@Override
	protected boolean isLoginNeeded() { return true; }

	@Override
	protected void doCommon(HttpServletRequest req, HttpServletResponse resp) throws Throwable {
        doBefore(req, resp);
        
        JsonObject responses = new JsonObject();
        responses.put("success", new Boolean(false));
        responses.put("message", "");
        
        try {
        	String svSub1 = req.getParameter("svSub");
        	if(svSub1 == null) throw new RuntimeException("No sub keyword !");
        	
        	Account acc = checkLogined(req);
        	
        	if(svSub1.equalsIgnoreCase("list")) {
        		JsonArray arr = new JsonArray();
        		
        		for(Colony c : acc.getColonies()) {
        			JsonObject row = new JsonObject();
        			row.put("name", c.getName());
        			row.put("key" , String.valueOf(c.getKey()));
        			arr.add(row);
        		}
        		
        		responses.put("success", new Boolean(true));
                responses.put("message", "");
                responses.put("list", arr);
        	} else if(svSub1.equalsIgnoreCase("detail")) {
        		String strKey = req.getParameter("key");
        		long key = Long.parseLong(strKey);
        		
        		Colony col = null;
        		for(Colony c : acc.getColonies()) {
        			if(key == c.getKey()) { col = c; break; }
        		}
        		
        		if(col == null) {
        			responses.put("success", new Boolean(false));
                    responses.put("message", "Not existing colony.");
        		} else {
        			responses.put("success", new Boolean(true));
                    responses.put("message", "");
                    responses.put("detail", col.toJson(true, col, null));
        		}
        	} else if(svSub1.equalsIgnoreCase("cycle")) {
        		String strKey = req.getParameter("key");
        		long key = Long.parseLong(strKey);
        		
        		String strCyclePass = req.getParameter("cycle");
        		if(strCyclePass == null || "".equals(strCyclePass)) strCyclePass = "1";
        		int cyclePass = Integer.parseInt(strCyclePass);
        		if(cyclePass > 10) cyclePass = 10;
        		if(cyclePass <  1) cyclePass =  1;
        		
        		Colony col = null;
        		for(Colony c : acc.getColonies()) {
        			if(key == c.getKey()) { col = c; break; }
        		}
        		
        		BigInteger time = col.getTime();
                time = time.add(BigInteger.ONE);
                
                BigInteger timeMax = new BigInteger(String.valueOf(Integer.MAX_VALUE - 10));
                while(time.compareTo(timeMax) >= 0) {
                    time = time.subtract(timeMax);
                }
                int cycle = time.intValue();
                
                for(int idx=0; idx<cyclePass; idx++) {
                    cycle++;
                    col.oneCycle(cycle, null, col, 100, null);
                }
                
                responses.put("success", new Boolean(true));
                responses.put("message", "");
                responses.put("detail", col.toJson());
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
		return "colony"; // URL : /web/json?svName=colony
	}

}
