package org.duckdns.hjow.colonization.web.servlets;

import java.util.Hashtable;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends CommonServlet {
    private static final long serialVersionUID = -1479626510774035644L;
    protected Hashtable<String, CommonServlet> children = new Hashtable<String, CommonServlet>();
    
    @Override
    public void init() throws ServletException {
        super.init();
        logger.info("Colonization Web Servlet initializing...");
        registerServlets();
        
        Set<String> servletNames = children.keySet();
        for(String svName : servletNames) {
        	children.get(svName).init();
        }
    }
    
    /** 소속 서블릿 등록 */
    @SuppressWarnings("unchecked")
	protected void registerServlets() {
    	CommonServlet sv;
        sv = new LoginServlet();
        children.put(sv.getName(), sv);
        
        sv = new ColonyServlet();
        children.put(sv.getName(), sv);
        
        sv = new AdminServlet();
        children.put(sv.getName(), sv);
        
        try {
        	Class<? extends CommonServlet> classSv = null;
        	try { 
        		classSv = (Class<? extends CommonServlet>) Class.forName("org.duckdns.hjow.colonization.web.TestServlet");
        		sv = classSv.newInstance(); 
        		children.put(sv.getName(), sv);
        	} catch(ClassNotFoundException exc) { exc.printStackTrace(); }
        	
        	// 추가로 필요한 경우 이 곳에 등록 (Kotlin 기반인 경우 reflection 으로 객체를 생성할 것)
        	
        } catch(Exception ex) {
            logger.error("Exception on registering children servlets - " + ex.getMessage(), ex);
        }
    }
    
    @Override
    public void destroy() {
        super.destroy(); 
    }
    
    @Override
    protected boolean isLoginNeeded() { return false; }
    
    @Override
    protected void doCommon(HttpServletRequest req, HttpServletResponse resp) throws Throwable {
        try {
            String remote = req.getRemoteAddr();
            
            String svName = getParameter(req, "svName");
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
