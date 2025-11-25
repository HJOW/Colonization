package org.duckdns.hjow.colonization.console;

import java.util.HashMap;
import java.util.Map;

import org.duckdns.hjow.classwrapper.ClassLoaderManager;
import org.duckdns.hjow.colonization.ColonizationMainClass;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.commons.util.ClassUtil;

/** 콘솔 모드로 Colonization 실행 */
public class ConsoleColonization implements ColonizationMainClass {
    private static final ConsoleColonization INSTANCES = new ConsoleColonization();
    public static void main(String[] args) {
        GlobalLogs.tryingToInitLog4j();
        
        Map<String, String> argMap = ClassUtil.convertAppParams(args);
        INSTANCES.prepare(argMap);
        INSTANCES.run();
    }
    
    protected transient Map<String, String> arguments = new HashMap<String, String>();
    protected transient ColonyManager manager   = null;

    /** 사전 준비, 프로그램 실행 시 받은 매개변수 입력 */
    protected void prepare(Map<String, String> args) {
        if(args != null) this.arguments.putAll(args);
        preWorks(args);
    }

    @Override
    public void run() {
        restart();
    }

    @Override
    public void restart() {
        if(manager != null) {
            manager.dispose();
            manager = null;
        }
        manager = new ConsoleColonyManager();
        manager.open(this);
    }

    @Override
    public void exit() {
    	ClassLoaderManager.closeAll();
        System.exit(0);
    }
    
    /** 메인 UI 호출 전 선 작업 수행 */
    protected void preWorks(Map<String, String> args) {
        new PreWorks(args).work();
    }
}
