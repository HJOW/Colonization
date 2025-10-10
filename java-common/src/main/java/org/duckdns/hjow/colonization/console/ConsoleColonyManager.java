package org.duckdns.hjow.colonization.console;

import org.duckdns.hjow.colonization.ColonizationMainClass;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.commons.console.ConsoleMenu;

/** 콘솔 모드에서 사용되는 매니저 */
public class ConsoleColonyManager extends ColonyManager {
    private static final long serialVersionUID = 7584671897900957493L;
    public ConsoleColonyManager() { super(); }
    
    @Override
    public void open(ColonizationMainClass superInstance) {
        ConsoleMenu rootMenu = new ConsoleMenu();
        // TODO
        rootMenu.launch();
    }
    
    
}
