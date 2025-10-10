package org.duckdns.hjow.colonization;
import java.util.Map;

import org.duckdns.hjow.colonization.console.ConsoleColonization;
import org.duckdns.hjow.colonization.ui.GUIColonyManager;
import org.duckdns.hjow.commons.util.ClassUtil;
import org.duckdns.hjow.commons.util.DataUtil;

public class Colonization extends ConsoleColonization implements GUIColonizationMainClass {
    private static final Colonization INSTANCES = new Colonization();
    public static void main(String[] args) {
        Map<String, String> argMap = ClassUtil.convertAppParams(args);
        INSTANCES.prepare(argMap);
        INSTANCES.run();
    }
    
    protected transient LoadingAWTDialog loadingScreen;
    
    /** 매개변수로 GUI 실행 여부 판단 */
    protected boolean isGUI() {
        String argGUI = arguments.get("gui");
        if(DataUtil.isEmpty(argGUI)) argGUI = arguments.get("g");
        if(DataUtil.isEmpty(argGUI)) return true;
        return DataUtil.parseBoolean(argGUI.trim());
    }
    
    /** 프로그램 실행 */
    @Override
    public void run() {
        if(! isGUI()) { super.run(); return; }
        
        openLoadingDialog();
        
        if(manager != null) {
            manager.dispose();
            manager = null;
        }
        manager = new GUIColonyManager(this);
        ((GUIColonyManager) manager).open(this);
    }
    
    @Override
    public void restart() {
        if(! isGUI()) { super.restart(); return; }
        
        openLoadingDialog();
        try { manager.dispose(((GUIColonyManager) manager).isVisible()); } catch(Exception notImportant) { notImportant.printStackTrace(); }
        manager = null;
        try { Thread.sleep(3000L); } catch(InterruptedException ex) { exit(); return; }
        run();
    }
    
    /** 로딩 대화상자 열기 */
    public void openLoadingDialog() {
        if(loadingScreen == null) loadingScreen = new LoadingAWTDialog("Colonization", "");
        if(! loadingScreen.isVisible()) loadingScreen.open();
    }
    
    /** 로딩 대화상자 닫기 */
    public void closeLoadingDialog() {
        if(loadingScreen != null) loadingScreen.close();
        loadingScreen = null;
    }
}
