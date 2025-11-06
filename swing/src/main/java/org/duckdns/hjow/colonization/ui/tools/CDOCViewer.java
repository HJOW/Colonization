package org.duckdns.hjow.colonization.ui.tools;

import java.awt.Window;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.ui.GUIColonyManager;

/** cdoc 파일 관리 툴 */
public class CDOCViewer extends org.duckdns.hjow.commons.ui.CDOCViewer implements Tool {
    public CDOCViewer(Window superInst) {
        super(superInst);
        dialog.setIconImage(GUIColonyManager.getIcon());
    }
    
    /** 다국어 지원 호출 */
    @Override
    protected String t(String originals) {
        return ColonyManager.t(originals);
    }

    @Override
    public String getName() {
        return "CDOC";
    }

    @Override
    public String getTitle() {
        return "압축된 문서 (CDOC) 뷰어";
    }
    
    @Override
    public boolean isAvail() { return true; }
}
