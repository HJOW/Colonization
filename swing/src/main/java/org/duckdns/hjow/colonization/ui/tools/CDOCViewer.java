package org.duckdns.hjow.colonization.ui.tools;

import java.awt.Window;

import org.duckdns.hjow.colonization.ColonyManager;

/** cdoc 파일 관리 툴 */
public class CDOCViewer extends org.duckdns.hjow.commons.ui.CDOCViewer {
    public CDOCViewer(Window superInst) {
    	super(superInst);
    }
    
    /** 다국어 지원 호출 */
    @Override
    protected String t(String originals) {
    	return ColonyManager.t(originals);
    }
}
