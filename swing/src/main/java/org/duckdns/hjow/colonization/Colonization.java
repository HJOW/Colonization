package org.duckdns.hjow.colonization;

import org.duckdns.hjow.colonization.ui.GUIColonyManager;

public class Colonization implements Runnable {
	private static final Colonization INSTANCES = new Colonization();
    public static void main(String[] args) {
    	INSTANCES.run();
    }
    
    protected GUIColonyManager guiMain;
    public void run() {
    	if(guiMain == null) {
    		guiMain = new GUIColonyManager(this);
    		guiMain.open(this);
    	}
    }
    
    public void exit() {
    	System.exit(0);
    }
}
