package org.duckdns.hjow.colonization.console;

import java.util.HashMap;
import java.util.Map;

import org.duckdns.hjow.colonization.ColonizationMainClass;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.commons.util.ClassUtil;
import org.duckdns.hjow.consolemenu.ConsoleMenu;

public class ConsoleColonization implements ColonizationMainClass {
    private static final ConsoleColonization INSTANCES = new ConsoleColonization();
    public static void main(String[] args) {
        Map<String, String> argMap = ClassUtil.convertAppParams(args);
        INSTANCES.prepare(argMap);
        INSTANCES.run();
    }
    
    protected transient Map<String, String> arguments = new HashMap<String, String>();
    protected transient ConsoleMenu   rootMenu  = null;
    protected transient ColonyManager manager   = null;

    /** 사전 준비, 프로그램 실행 시 받은 매개변수 입력 */
    protected void prepare(Map<String, String> args) {
        if(args != null) this.arguments.putAll(args);
    }

    @Override
    public void run() {
        if(manager != null) {
            manager.dispose();
            manager = null;
        }
        manager = new ConsoleColonyManager();
        
        restart();
    }

    @Override
    public void restart() {
        if(rootMenu != null) rootMenu.closeMenu();
        rootMenu = new ConsoleMenu();
        rootMenu.setEndOperationExit(true);
        rootMenu.launch();
    }

    @Override
    public void exit() {
        System.exit(0);
    }
}
