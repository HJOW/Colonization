package org.duckdns.hjow.colonization;
import java.util.Map;

import org.duckdns.hjow.colonization.console.ConsoleColonization;
import org.duckdns.hjow.colonization.constants.Constants;
import org.duckdns.hjow.colonization.daemon.TCPSimpleDaemon;
import org.duckdns.hjow.colonization.ui.GUIColonyManager;
import org.duckdns.hjow.colonization.ui.GUIPreWorks;
import org.duckdns.hjow.colonization.ui.LoadingAWTDialog;
import org.duckdns.hjow.colonization.ui.help.HelpDialog;
import org.duckdns.hjow.colonization.ui.licenses.LicenseDialog;
import org.duckdns.hjow.colonization.ui.tools.files.FileManager;
import org.duckdns.hjow.commons.ui.LogComponent;
import org.duckdns.hjow.commons.util.ClassUtil;
import org.duckdns.hjow.commons.util.DataUtil;
import org.duckdns.hjow.commons.util.GUIUtil;

public class Colonization extends ConsoleColonization implements GUIColonizationMainClass {
    private static final Colonization INSTANCES = new Colonization();
    public static void main(String[] args) {
        GlobalLogs.tryingToInitLog4j();
        Constants.prepare();
        
        // Core.loadCore("col", "colonization", ColonyManager.getVersionString(), args);
        
        GlobalLogs.log("Start to run Colonization ...");
        
        Map<String, String> argMap = ClassUtil.convertAppParams(args);
        INSTANCES.prepare(argMap);
        INSTANCES.run();
    }
    
    protected transient LoadingAWTDialog loadingScreen;
    protected transient String mode = "gui";
    
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
        mode = arguments.get("mode");
        
        if(DataUtil.isEmpty(mode) && DataUtil.isNotEmpty(arguments.get("--mode"))) mode = arguments.get("--mode");
        if(DataUtil.isEmpty(mode)) mode = arguments.get("m");
        if(DataUtil.isEmpty(mode)) mode = "gui";
        
        mode = mode.trim().toLowerCase();
        if(mode.equals("console") || mode.equals("c")) {
        	GlobalLogs.log("Console-mode program is called.");
            super.run(); return;
        } else if(mode.equals("daemon") || mode.equals("d")) {
        	GlobalLogs.log("The TCP daemon is called.");
            String port = arguments.get("port");
            if(DataUtil.isEmpty(port)) port = arguments.get("p");
            
            String charset = "UTF-16";
            if(! DataUtil.isEmpty(arguments.get("charset"))) charset = arguments.get("charset").trim();
            
            TCPSimpleDaemon daemon = new TCPSimpleDaemon(Integer.parseInt(port.trim()), charset, new LogComponent() {
                @Override
                public void log(String msg) {
                    System.out.println(msg);
                }
            });
            daemon.start();
        } else if(mode.equals("gui") || mode.equals("g")) {
            openLoadingDialog();
            GlobalLogs.log("The main program based Swing is called.");
            
            if(manager != null) {
                manager.dispose();
                manager = null;
            }
            manager = new GUIColonyManager(this);
            manager.open(this);
        } else if(mode.equals("help") || mode.equals("h")) {
        	defaultJobNoManager();
        	new HelpDialog().open();
        } else if(mode.equals("license") || mode.equals("l")) {
        	defaultJobNoManager();
        	new LicenseDialog().open();
        } else if(mode.equals("fileman") || mode.equals("f")) {
        	defaultJobNoManager();
        	new FileManager().open();
        }
    }
    
    @Override
    public void restart() {
    	GlobalLogs.log("Restarting...");
        if(mode.equals("gui") || mode.equals("g")) openLoadingDialog();
        
        try { manager.dispose(((GUIColonyManager) manager).isVisible()); } catch(Exception notImportant) { notImportant.printStackTrace(); }
        if(manager instanceof GUIColonyManager) {
            try { Thread.sleep(3000L); } catch(InterruptedException ex) { exit(); return; }
            GUIColonyManager guiMan = (GUIColonyManager) manager;
            guiMan.loadLocalConfigs();
            guiMan.loadLocalStorage();
            guiMan.initializeUI();
            guiMan.open(this);
            closeLoadingDialog();
        } else {
            manager = null;
            try { Thread.sleep(3000L); } catch(InterruptedException ex) { exit(); return; }
            run();
        }
    }
    
    /** 로딩 대화상자 열기 */
    public void openLoadingDialog() {
        if(loadingScreen == null) loadingScreen = new LoadingAWTDialog("Colonization", "", "v" + ColonyManager.getVersionString() + " (No. " + ColonyManager.BUILD_NO + ")");
        if(! loadingScreen.isVisible()) loadingScreen.open();
    }
    
    /** 로딩 대화상자 닫기 */
    public void closeLoadingDialog() {
        if(loadingScreen != null) loadingScreen.close();
        loadingScreen = null;
    }
    
    /** 메인 UI 호출 전 선 작업 수행 */
    @Override
    protected void preWorks(Map<String, String> args) {
        new GUIPreWorks(args).work();
    }
    
    /** 매니저 없는 실행 모드인 경우 기본 작업 수행 */
    protected void defaultJobNoManager() {
    	GUIUtil.setLookAndFeel(null); // 시스템 기본 LookAndFeel
    }
}
