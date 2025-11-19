package org.duckdns.hjow.colonization.web.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.duckdns.hjow.colonization.ColonyClassLoader;
import org.duckdns.hjow.colonization.ColonyManagerConfig;
import org.duckdns.hjow.colonization.DefaultColonyManagerConfig;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.constants.StaticMethods;
import org.duckdns.hjow.colonization.pack.Pack;
import org.duckdns.hjow.colonization.web.accounts.Account;
import org.duckdns.hjow.colonization.web.accounts.AccountUtil;
import org.duckdns.hjow.colonization.web.db.DBUtil;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.ClassUtil;
import org.duckdns.hjow.commons.util.FileUtil;

public abstract class CommonServlet extends HttpServlet {
    private static final long serialVersionUID = -9127592158446596240L;
    protected static final ColonyManagerConfig configs = new DefaultColonyManagerConfig();
    
    protected Logger logger = LogManager.getLogger(this.getClass());
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        File root = AccountUtil.getAccountRootDirectory();
        if(! root.exists()) root.mkdirs();
        
        try {
            File conf = new File(root.getAbsolutePath() + File.separator + "config.json");
            if(conf.exists()) {
                String strJson = FileUtil.readString(conf, "UTF-8"); // 파일 읽고
                JsonObject json = (JsonObject) JsonObject.parseJson(strJson); // JSON 파싱
                configs.fromJson(json); // 설정 넣기
            }
            
            DBUtil.init();
            
            // 설정들 중 클래스 관련 설정 적용
            ColonyClassLoader.clearAll();
            // Pack 목록 불러오기
            List<Object> packList = null;
            try {
                packList = configs.getList("packs");
            } catch(Exception ex) {
                GlobalLogs.processExceptionOccured(ex, false);
            }
            
            if(packList == null) {
                packList = new ArrayList<Object>();
                configs.set("packs", packList);
            }
            
            List<Pack> packs = new ArrayList<Pack>();
            
            for(Object o : packList) {
                try {
                    ColonyManagerConfig child = (ColonyManagerConfig) o;
                    Class<?> classObj = ColonyClassLoader.loadClassFrom(child);
                    Pack packOne = (Pack) classObj.newInstance();
                    
                    if(! packs.contains(packOne)) packs.add(packOne);
                } catch(Exception ex) {
                    GlobalLogs.processExceptionOccured(ex, false);
                }
            }
            
            for(Pack p : packs) {
                try {
                    if(! packs.contains(p)) ColonyClassLoader.loadPack(p);
                } catch(Exception ex) {
                    GlobalLogs.processExceptionOccured(ex, false);
                }
            }
        } catch(Exception ex) {
            GlobalLogs.processExceptionOccured(ex, false);
        }
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
    
    /** 로그인 필요 여부 반환 */
    protected boolean isLoginNeeded() { return false; }
    
    /** 해당 등급 이상만 접근 가능한지를 반환 */
    protected int getMinimumGradeRequired() { return 0; }
    
    /** JSON 을 응답으로 내보냄 */
    public void response(HttpServletResponse resp, JsonObject json) {
        resp.setCharacterEncoding("UTF-8");
        try { resp.getWriter().write(json.toJSON()); } catch(Exception ex) { throw new RuntimeException(ex.getMessage(), ex); }
    }
    
    /** 로그인 여부 체크 */
    protected Account checkLogined(HttpServletRequest req) {
        String jwt = req.getHeader("jwt");
        if(jwt == null || "".equals(jwt)) {
            try { jwt = getParameter(req, "jwt"); } catch(Exception ex) { throw new RuntimeException(ex.getMessage(), ex); }
            if(jwt != null) jwt = StaticMethods.decodeString(jwt); // 매개변수의 경우 HEX로 인코딩된 값이 넘어올 테니 디코딩해 사용
        }
        if(jwt == null || "".equals(jwt)) {
            return null;
        } else {
            Account acc = AccountUtil.verifyJWT(jwt);
            if(acc == null) return null;
            return acc;
        }
    }
    
    /** doCommon 메소드 내 맨 앞에서 반드시 호출 ! */
    protected void doBefore(HttpServletRequest req, HttpServletResponse resp) throws Throwable {
        boolean logined = false;
        
        // JWT 토큰이 있는 경우 로그인 여부 판단
        String jwt = req.getHeader("jwt"); // 먼저 헤더에 있는지 체크
        if(jwt == null) {
            jwt = getParameter(req, "jwt"); // 헤더에 없으면 매개변수에 있는지 체크
            if(jwt != null) jwt = StaticMethods.decodeString(jwt); // 매개변수의 경우 HEX로 인코딩된 값이 넘어올 테니 디코딩해 사용
        }
        
        if(jwt != null) {
            try {
                Account acc = AccountUtil.verifyJWT(jwt);
                if(acc != null) {
                    req.setAttribute("id"   , acc.getId());
                    req.setAttribute("key"  , new Long(acc.getKey()));
                    req.setAttribute("name" , acc.getName());
                    req.setAttribute("grade", new Integer(acc.getGrade()));
                    logined = true;
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
    
    /** 매개변수 받기 */
    @SuppressWarnings("unchecked")
    public static String getParameter(HttpServletRequest req, String key) throws Exception {
        Map<String, String> paramMap = (Map<String, String>) req.getAttribute("__params"); // 어트리뷰트에 캐시된 데이터 먼저 찾기
        if(paramMap == null) paramMap = getParameterMap(req);
        return paramMap.get(key);
    }
    
    /** 매개변수 받아 파싱해서 Attribute 에 넣기 */
    public static Map<String, String> getParameterMap(HttpServletRequest req) throws Exception {
        Map<String, String> paramMap = new HashMap<String, String>();
        
        // URL 매개변수 받기
        Enumeration<String> paramKeys = req.getParameterNames();
        while(paramKeys.hasMoreElements()) {
            String paramKey = paramKeys.nextElement();
            String[] paramArr = req.getParameterValues(paramKey);
            if(paramArr == null) continue;
            if(paramArr.length <= 0) continue;
            if(paramArr.length == 1) {
                paramMap.put(paramKey, req.getParameter(paramKey));
            } else {
                StringBuilder values = new StringBuilder("");
                for(int idx=0; idx<paramArr.length; idx++) {
                    if(idx >= 1) values = values.append(",");
                    values = values.append(paramArr[idx]);
                }
            }
        }
        
        // Body 받기
        boolean firsts = true;
        StringBuilder bodys = new StringBuilder("");
        InputStream       inp = null;
        InputStreamReader rd1 = null;
        BufferedReader    rd2 = null;
        try {
            inp = req.getInputStream();
            rd1 = new InputStreamReader(inp, "UTF-8");
            rd2 = new BufferedReader(rd1);
            String line;
            
            while(true) {
                line = rd2.readLine();
                if(line == null) break;
                
                if(firsts) bodys = bodys.append("\n");
                bodys = bodys.append(line);
                firsts = false;
            }
            
            ClassUtil.closeAll(rd2, rd1, inp);
            rd2 = null; rd1 = null; inp = null;
            
            StringTokenizer andTokenizer = new StringTokenizer(bodys.toString().trim(), "&");
            bodys = null;
            
            while(andTokenizer.hasMoreTokens()) {
                String paramBlockOne = andTokenizer.nextToken().trim();
                String[] parts = paramBlockOne.split("=");
                String key = parts[0];
                String values = "";
                if(parts.length >= 2) values = parts[1];
                
                values = URLDecoder.decode(values, "UTF-8");
                paramMap.put(key, values);
            }
            
            req.setAttribute("__params", paramMap);
        } catch(Exception ex) {
            req.setAttribute("__params", new HashMap<String, String>());
            throw ex;
        } finally {
            ClassUtil.closeAll(rd2, rd1, inp);
        }
        
        return paramMap;
    }
}
