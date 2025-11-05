package org.duckdns.hjow.colonization.web.servlets;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.duckdns.hjow.colonization.web.service.AttributeMap;
import org.duckdns.hjow.colonization.web.service.Service;
import org.duckdns.hjow.colonization.web.service.StringMap;
import org.duckdns.hjow.commons.json.JsonObject;

public class MainServlet extends CommonServlet {
    private static final long serialVersionUID = -1479626510774035644L;
    protected Hashtable<String, CommonServlet> children = new Hashtable<String, CommonServlet>();
    protected Hashtable<String, Service>       services = new Hashtable<String, Service>();
    
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
            Class<? extends Service> classSv = null;
            Service serv = null;
            String svcClassName;
            
            svcClassName = "org.duckdns.hjow.colonization.web.service.TestService";
            try { 
                classSv = (Class<? extends Service>) Class.forName(svcClassName);
                serv = classSv.newInstance(); 
                services.put(serv.getName(), serv);
            } catch(ClassNotFoundException exc) { logger.error("Cannot find the class " + svcClassName);
            } catch(ClassFormatError exc) { logger.error("Cannot use the class " + svcClassName + " - " + exc.getMessage()); }
            
            // 추가로 필요한 경우 이 곳에 등록 (Kotlin 기반인 경우 reflection 으로 객체를 생성할 것)
            
        } catch(Exception ex) {
            logger.error("Exception on registering children servlets - " + ex.getMessage(), ex);
        }
    }
    
    @Override
    public void destroy() {
        Set<String> keys;
        
        keys = services.keySet();
        for(String k : keys) {
            try { services.get(k).dispose(); } catch(Exception ex) { logger.error(ex.getMessage(), ex); }
        }
        
        keys = children.keySet();
        for(String k : keys) {
            try { children.get(k).destroy(); } catch(Exception ex) { logger.error(ex.getMessage(), ex); }
        }
        
        super.destroy(); 
    }
    
    @Override
    protected boolean isLoginNeeded() { return false; }
    
    @Override
    protected void doCommon(HttpServletRequest req, HttpServletResponse resp) throws Throwable {
        try {
            String remote = req.getRemoteAddr();
            boolean serviceComplete = false;
            
            String svName = getParameter(req, "svName");
            if(svName == null) { resp.sendError(404); return; }
            
            CommonServlet sv = children.get(svName);
            
            // 자식 서블릿이 있는 경우 호출
            if(sv != null) {
                logger.info("Access /web/json from " + remote + ", requesting " + svName + " ...");
                sv.doCommon(req, resp);
                
                serviceComplete = true; // 완료 처리
            }
            
            if(! serviceComplete) {
                // 서비스가 있는 경우 호출
                Service serv = services.get(svName);
                if(serv != null) {
                    logger.info("Access /web/json from " + remote + ", requesting " + svName + " ...");
                    
                    // 매개 변수 파싱
                    StringMap           paramMap  = new StringMap();
                    Enumeration<String> paramKeys = req.getParameterNames();
                    
                    while(paramKeys.hasMoreElements()) {
                        String k = paramKeys.nextElement();
                        paramMap.put(k, getParameter(req, k));
                    }
                    
                    // 헤더 값도 파싱
                    StringMap           headerMap  = new StringMap();
                    Enumeration<String> headerKeys = req.getHeaderNames();
                    
                    while(headerKeys.hasMoreElements()) {
                        String k = headerKeys.nextElement();
                        headerMap.put(k, req.getHeader(k));
                    }
                    
                    // 선 작업 수행
                    AttributeMap attributes = extractAttributes(req);
                    serv.doBefore(paramMap, headerMap, attributes);
                    
                    // 서비스 호출
                    JsonObject responseObj = serv.doCommon(paramMap, headerMap, attributes);
                    
                    // 응답
                    response(resp, responseObj);
                    
                    // 후 작업 수행
                    doAfter(req, resp);
                    serviceComplete = true; // 완료 처리
                }
            }
            
            if(! serviceComplete) { resp.sendError(404); return; }
        } catch(Exception ex) {
            logger.error("Exception on doCommon main", ex);
        }
    }
    
    /** Attributes 꺼내기 */
    protected AttributeMap extractAttributes(HttpServletRequest req) {
        AttributeMap attributes = new AttributeMap();
        
        HttpSession session = req.getSession();
        ServletContext ctx = session.getServletContext();
        
        Enumeration<String> keys;
        
        // Servlet Context
        keys = ctx.getAttributeNames();
        while(keys.hasMoreElements()) {
            String k = keys.nextElement();
            attributes.put(k, ctx.getAttribute(k));
        }
        
        // Session
        keys = session.getAttributeNames();
        while(keys.hasMoreElements()) {
            String k = keys.nextElement();
            attributes.put(k, session.getAttribute(k));
        }
        
        // Request
        keys = req.getAttributeNames();
        while(keys.hasMoreElements()) {
            String k = keys.nextElement();
            attributes.put(k, req.getAttribute(k));
        }
        
        return attributes;
    }

    @Override
    public String getName() {
        return "main";
    }
}
