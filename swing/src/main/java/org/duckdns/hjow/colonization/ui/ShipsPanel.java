package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.ColonyManagerInterface;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ship.Ship;
import org.duckdns.hjow.commons.core.Disposeable;

/** 함선들 현황 출력 및 컨트롤 화면 */
public class ShipsPanel extends JPanel implements Disposeable {
	private static final long serialVersionUID = 1251621290381343350L;
	protected transient Vector<ShipPanel> pnShips = new Vector<ShipPanel>();
	protected transient JPanel pnShipRoot;
	protected transient SpacePanel pnSpace;
	
    public ShipsPanel() { init(); }
    
    /** UI 초기화 */
    protected void init() {
    	setLayout(new BorderLayout());
    	
    	JTabbedPane tabs = new JTabbedPane();
    	add(tabs, BorderLayout.CENTER);
    	
    	pnSpace    = new SpacePanel();
    	pnShipRoot = new JPanel();
    	pnSpace.setLayout(null);
    	pnShipRoot.setLayout(new GridBagLayout());
    	
    	tabs.add(ColonyManager.t("스크린"), pnSpace);
    	tabs.add(ColonyManager.t("현황"), pnShipRoot);
    }
    
	@Override
	public void dispose() {
		for(ShipPanel p : pnShips) {
			p.dispose();
		}
		pnShips.clear();
		removeAll();
	}
	
	/** 화면 새로고침 시 호출 */
	public void refresh(int cycle, Colony colony, ColonyManagerInterface superInstance) {
		pnSpace.setColony(colony);
		if(pnShips.size() != colony.getShipCount()) {
			for(ShipPanel p : pnShips) {
				p.dispose();
			}
			pnShips.clear();
			pnShipRoot.removeAll();
			
	        int rowNo = 0;
	        GridBagConstraints gridBagConst;
			
			for(Ship s : colony.getShips()) {
				ShipPanel pnOne = new ShipPanel(s, superInstance);
				
				gridBagConst = new GridBagConstraints();
	            gridBagConst.gridx = 0;
	            gridBagConst.gridy = rowNo; rowNo++;
	            gridBagConst.gridwidth  = 1;
	            gridBagConst.gridheight = 1;
	            gridBagConst.weightx = 1.0;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
	            gridBagConst.fill = GridBagConstraints.HORIZONTAL;
	            gridBagConst.anchor = GridBagConstraints.NORTH;
	            pnShipRoot.add(pnOne, gridBagConst);
				
				pnShips.add(pnOne);
			}
			
			gridBagConst = new GridBagConstraints();
	        gridBagConst.gridx = 0;
	        gridBagConst.gridy = rowNo;
	        gridBagConst.gridwidth = 1;
	        gridBagConst.gridheight = 1;
	        gridBagConst.weightx = 1.0;
	        gridBagConst.weighty = 1.0;
	        gridBagConst.fill = GridBagConstraints.BOTH;
	        pnShipRoot.add(new JPanel(), gridBagConst);
		}
		
		for(ShipPanel p : pnShips) {
			p.refresh(cycle, colony, superInstance);
		}
	}
}