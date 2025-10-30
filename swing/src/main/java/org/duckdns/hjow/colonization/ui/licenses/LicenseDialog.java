package org.duckdns.hjow.colonization.ui.licenses;

import java.util.Vector;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.ui.GUIColonyManager;
import org.duckdns.hjow.colonization.ui.help.HelpContent;
import org.duckdns.hjow.colonization.ui.help.HelpDialog;

/** 이 소프트웨어 및 써드파티 라이브러리 소프트웨어 라이센스 고지 창 */
public class LicenseDialog extends HelpDialog {
	public LicenseDialog(GUIColonyManager superInstance) {
		super(superInstance);
	}
	
	/** 도움말 컨텐츠 대신 라이센스 컨텐츠 반환 */
	@Override
    public Vector<HelpContent> getHelpContents() {
    	Vector<HelpContent> list = new Vector<HelpContent>();
    	list.addAll(HelpContent.getHelpContentsFrom(this.getClass(), "content.json"));
    	
    	return list;
    }
    
    /** 대화 상자 열기 */
    @Override
    public void open() {
    	dialog.setTitle(ColonyManager.t("Licenses & Third'party library usage"));
    	
    	if(listHelp.getModel().getSize() >= 1) listHelp.setSelectedIndex(0);
    	onListSelected();
    	
    	dialog.setVisible(true);
    	splits.setDividerLocation(0.3);
    }
}
