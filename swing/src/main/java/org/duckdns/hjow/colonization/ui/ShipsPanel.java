package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.ColonyManagerInterface;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ship.Ship;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.ui.graphics.Coordinate3D;

/** 함선들 현황 출력 및 컨트롤 화면 */
public class ShipsPanel extends JPanel implements Disposeable {
	private static final long serialVersionUID = 1251621290381343350L;
	protected transient Vector<ShipPanel> pnShips = new Vector<ShipPanel>();
	protected transient JPanel     pnShipRoot;
	protected transient JToolBar   toolbar;
	protected transient JSpinner   spCameraX, spCameraY, spCameraZ, spYaw, spPitch;
	protected transient SpacePanel pnSpace;
	
    public ShipsPanel() { init(); }
    
    /** UI 초기화 */
    protected void init() {
    	setLayout(new BorderLayout());
    	
    	JTabbedPane tabs = new JTabbedPane();
    	add(tabs, BorderLayout.CENTER);
    	
    	JPanel pnScreen = new JPanel();
    	pnScreen.setLayout(new BorderLayout());
    	
    	pnSpace    = new SwingSpacePanel();
        // pnSpace    = new Java3DSpacePanel();
    	pnScreen.add(pnSpace, BorderLayout.CENTER);
    	
    	toolbar = new JToolBar();
    	pnScreen.add(toolbar, BorderLayout.NORTH);
    	
    	pnShipRoot = new JPanel();
    	pnShipRoot.setLayout(new GridBagLayout());
    	
    	tabs.add(ColonyManager.t("스크린"), pnScreen);
    	tabs.add(ColonyManager.t("현황"), pnShipRoot);
    	
    	JLabel lb;
    	SpinnerNumberModel spNum;
    	
    	lb = new JLabel("X");
    	spNum = new SpinnerNumberModel(new Long(0L), new Long(Long.MIN_VALUE), new Long(Long.MAX_VALUE), new Long(1L));
    	spCameraX = new JSpinner(spNum);
    	toolbar.add(lb);
    	toolbar.add(spCameraX);
    	
    	lb = new JLabel("Y");
    	spNum = new SpinnerNumberModel(new Long(0L), new Long(Long.MIN_VALUE), new Long(Long.MAX_VALUE), new Long(1L));
    	spCameraY = new JSpinner(spNum);
    	toolbar.add(lb);
    	toolbar.add(spCameraY);
    	
    	lb = new JLabel("Z");
    	spNum = new SpinnerNumberModel(new Long(0L), new Long(Long.MIN_VALUE), new Long(Long.MAX_VALUE), new Long(1L));
    	spCameraZ = new JSpinner(spNum);
    	toolbar.add(lb);
    	toolbar.add(spCameraZ);
    	
    	lb = new JLabel(ColonyManager.t("YAW"));
    	spNum = new SpinnerNumberModel(0, 0, 360, 1);
    	spYaw = new JSpinner(spNum);
    	toolbar.add(lb);
    	toolbar.add(spYaw);
    	
    	lb = new JLabel(ColonyManager.t("Pitch"));
    	spNum = new SpinnerNumberModel(0, 0, 360, 1);
    	spPitch = new JSpinner(spNum);
    	toolbar.add(lb);
    	toolbar.add(spPitch);
    	
    	ChangeListener chgXYZ = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				pnSpace.setCameraLocation(new Coordinate3D(((Number) spCameraX.getValue()).longValue(), ((Number) spCameraY.getValue()).longValue(), ((Number) spCameraZ.getValue()).longValue()));
				pnSpace.refresh();
			}
		};
    	
    	spCameraX.addChangeListener(chgXYZ);
    	spCameraY.addChangeListener(chgXYZ);
    	spCameraZ.addChangeListener(chgXYZ);
    	
    	spYaw.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				pnSpace.setCameraYaw(Math.toRadians(((Number) spYaw.getValue()).doubleValue()));
				pnSpace.refresh();
			}
		});
    	
    	spPitch.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				pnSpace.setCameraPitch(Math.toRadians(((Number) spPitch.getValue()).doubleValue()));
				pnSpace.refresh();
			}
		});
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
		if(pnSpace.getColony() == null || colony != pnSpace.getColony()) {
		    pnSpace.setColony(colony);
		    spCameraX.setValue(colony.getX());
		    spCameraY.setValue(colony.getY());
		    spCameraZ.setValue(colony.getZ());
		    spYaw.setValue(0.0);
		    spPitch.setValue(0.0);
		}
		
		if(pnShips.size() != colony.getLiveShipCount()) {
			for(ShipPanel p : pnShips) {
				p.dispose();
			}
			pnShips.clear();
			pnShipRoot.removeAll();
			
	        int rowNo = 0;
	        GridBagConstraints gridBagConst;
			
			for(Ship s : colony.getShipsLive()) {
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
		
		pnSpace.refresh();
	}
}