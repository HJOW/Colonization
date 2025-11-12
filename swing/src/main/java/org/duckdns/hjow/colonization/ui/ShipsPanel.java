package org.duckdns.hjow.colonization.ui;

import javax.swing.JPanel;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.commons.core.Disposeable;

/** 함선들 현황 출력 및 컨트롤 화면 */
public class ShipsPanel extends JPanel implements Disposeable {
	private static final long serialVersionUID = 1251621290381343350L;
    public ShipsPanel() { init(); }
    
    /** UI 초기화 */
    protected void init() {
    	// TODO
    }
    
	@Override
	public void dispose() {
		
	}
	
	/** 화면 새로고침 시 호출 */
	public void refresh(int cycle, Colony colony, ColonyManager superInstance) {
		// TODO
	}
}
