package org.duckdns.hjow.colonization.web.servlets;

import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/web/json")
public class MainServlet extends CommonServlet {
    private static final long serialVersionUID = -1479626510774035644L;
    protected Hashtable<String, CommonServlet> children = new Hashtable<String, CommonServlet>();
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        CommonServlet sv;
        sv = new LoginServlet();
        children.put(sv.getName(), sv);
    }
    
    @Override
    public void destroy() {
        super.destroy(); 
    }
    
    @Override
    protected void doCommon(HttpServletRequest req, HttpServletResponse resp) throws Throwable {
        try {
            String remote = req.getRemoteAddr();
            
            String svName = req.getParameter("svName");
            if(svName == null) { resp.sendError(404); return; }
            
            CommonServlet sv = children.get(svName);
            if(sv == null) { resp.sendError(404); return; }
            
            logger.info("Access /web/json from " + remote + ", requesting " + svName + " ...");
            
            sv.doCommon(req, resp);
        } catch(Exception ex) {
            logger.error("Exception on doCommon main", ex);
        }
    }

    @Override
    public String getName() {
        return "main";
    }
}
