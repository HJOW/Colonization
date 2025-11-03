package org.duckdns.hjow.colonization;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.LinkedList;
import java.util.Queue;

import org.duckdns.hjow.commons.util.FileUtil;

/** 전역 로그 관리용 클래스, 별도의 UI 클래스와 함께 동작해야 함. Queue 안에 로그를 쌓아두고, UI 클래스에서 이를 꺼내 출력 */
public class GlobalLogs implements Serializable {
    private static final long serialVersionUID = 3967001988050207241L;
    protected Queue<String> logs = new LinkedList<String>();
    
    /** 로그 추가 */
    public void add(String msg) {
        logs.add(msg);
    } 
    /** 로그 꺼내기 */
    public String poll() {
        return logs.poll();
    }
    /** 출력할 로그가 없는지 확인 */
    public boolean isEmpty() {
        return logs.isEmpty();
    }
    /** 로그들이 포함된 Queue 객체 자체를 반환 */
    public Queue<String> getLogs() {
        return logs;
    }
    /** Queue 객체 자체를 교체 */
    public void setLogs(Queue<String> logs) {
        this.logs = logs;
    }
    /** 로그 모두 삭제 */
    public void clear() {
        this.logs.clear();
    }
    
    protected static GlobalLogs instances = new GlobalLogs();
    protected static Object logger;
    protected static Method methodInfoLogger, methodDebugLogger;
    
    public static GlobalLogs getInstance() { return instances; }
    
    /** 로그 출력 */
    public static void log(String msg) {
    	System.out.println(msg);
    	getInstance().add(msg);
    	
    	if(logger != null) {
    		try { methodInfoLogger.invoke(logger, msg); } catch(Throwable ignores) { logger = null; methodInfoLogger = null; methodDebugLogger = null; }
    	}
    }
    
    /** 오류 처리 (Colony 이내 쪽에서 발생한 예외는 각 패널에서 처리할 것 !) */
    public static void processExceptionOccured(Throwable tx, boolean isSerious) {
        if(tx instanceof RuntimeException) {
            Throwable caused = tx.getCause();
            if(caused != null) tx = caused;
        }
        
        tx.printStackTrace();
        
        String msg = ColonyManager.t("오류") + " - (" + tx.getClass().getSimpleName() + ") " + ColonyManager.t(tx.getMessage());
        ByteArrayOutputStream byteCollector = new ByteArrayOutputStream();
        if(isSerious) {
            PrintStream ps = new PrintStream(byteCollector);
            tx.printStackTrace(ps);
            ps.close();

            msg = msg + "\n" + new String(byteCollector.toByteArray());
        }
        log(msg);
    }
    
    /** Log4J2 사용 가능한 경우 설정 */
    public static void tryingToInitLog4j() {
    	try {
    		Class<?> configClass = Class.forName("org.apache.logging.log4j.core.config.Configurator");
    		
    		// Detect XML path
    		File cfgRoot = ColonyManager.getHomeDir("colonization", "configs");
    		if(! cfgRoot.exists()) {
    			cfgRoot.mkdirs();
    		}
    		
    		File xmlFile = new File(cfgRoot.getAbsolutePath() + File.separator + "log4j.xml");
    		if(! xmlFile.exists()) {
    			FileUtil.writeString(xmlFile, "UTF-8", getSampleLog4jXml());
    			xmlFile = new File(cfgRoot.getAbsolutePath() + File.separator + "log4j.xml");
    		}
    		
    		URI uri = xmlFile.toURI();
    		
    		// Get initialize method
    		Method mthd = configClass.getMethod("initialize", String.class, ClassLoader.class, URI.class);
    		mthd.invoke(null, null, null, uri); // set XML Path
    		
    		// Get LogManager
    	    Class<?> managerClass = Class.forName("org.apache.logging.log4j.LogManager");
    	    
    	    // getLogger
    	    mthd = managerClass.getMethod("getLogger");
    	    logger = mthd.invoke(null);
    	    
    	    // info and debug methods
    	    if(logger != null) {
    	    	Class<?> loggerClass = logger.getClass();
        	    methodInfoLogger  = loggerClass.getMethod("info" , String.class);
        	    methodDebugLogger = loggerClass.getMethod("debug", String.class);
        	    log("log4j prepared.");
    	    }
    	} catch(ClassNotFoundException e) {
    		logger = null;
    		methodDebugLogger = null;
    		methodInfoLogger  = null;
    	} catch(Throwable tx) {
    		log("Using default logging because Initializing log4j failed. (" + tx.getClass().getSimpleName() + ") " + tx.getMessage());
    		logger = null;
    		methodDebugLogger = null;
    		methodInfoLogger  = null;
    	}
    }
    
    /** log4j2 설정 샘플 */
    protected static String getSampleLog4jXml() {
    	StringBuilder sample = new StringBuilder("");
    	
    	sample = sample.append("\n").append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    	sample = sample.append("\n").append("<Configuration>");
    	sample = sample.append("\n").append("    <Appenders>");
        sample = sample.append("\n").append("        <Console name=\"console\" target=\"SYSTEM_OUT\" immediateFlush=\"true\">");
        sample = sample.append("\n").append("            <PatternLayout pattern=\"%d %5p [%c] %m%n\" />");
        sample = sample.append("\n").append("        </Console>");
    	sample = sample.append("\n").append("    </Appenders>");
    	sample = sample.append("\n").append("    <Loggers>");
    	sample = sample.append("\n").append("        <Root level=\"INFO\">");
    	sample = sample.append("\n").append("            <AppenderRef ref=\"console\" />");
    	sample = sample.append("\n").append("        </Root>");
    	sample = sample.append("\n").append("    </Loggers>");
    	sample = sample.append("\n").append("</Configuration>");
    	
    	return sample.toString().trim();
    } 
}
