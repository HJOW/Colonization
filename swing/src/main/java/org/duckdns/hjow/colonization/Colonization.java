package org.duckdns.hjow.colonization;

import org.duckdns.hjow.colonization.ui.GUIColonyManager;

public class Colonization implements GUIColonizationMainClass {
	private static final Colonization INSTANCES = new Colonization();
    public static void main(String[] args) {
    	INSTANCES.run();
    }
    
    protected LoadingAWTDialog loadingScreen;
    protected GUIColonyManager guiMain;
    public void run() {
    	openLoadingDialog();
    	
    	if(guiMain == null) {
    		guiMain = new GUIColonyManager(this);
    		guiMain.open(this);
    	}
    }
    
    public void restart() {
    	openLoadingDialog();
    	try { guiMain.dispose(guiMain.isVisible()); } catch(Exception notImportant) { notImportant.printStackTrace(); }
    	guiMain = null;
    	try { Thread.sleep(3000L); } catch(InterruptedException ex) { exit(); return; }
    	run();
    }
    
    public void openLoadingDialog() {
    	if(loadingScreen == null) loadingScreen = new LoadingAWTDialog("Colonization", "");
    	if(! loadingScreen.isVisible()) loadingScreen.open();
    }
    
    public void closeLoadingDialog() {
    	if(loadingScreen != null) loadingScreen.close();
    	loadingScreen = null;
    }
    
    public void exit() {
    	System.exit(0);
    }
}
