package org.duckdns.hjow.colonization.ui;

import javax.swing.JPanel;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ship.Ship;
import org.duckdns.hjow.commons.core.Disposeable;

/** 함선 하나의 현황 출력 및 컨트롤 화면 */
public class ShipPanel extends JPanel implements Disposeable {
	private static final long serialVersionUID = -3246934564467455324L;
	protected long shipKey = 0L;
	
	public ShipPanel() {}
	public ShipPanel(Ship ship) { this(); this.shipKey = ship.getKey(); }
	
	@Override
	public void dispose() {
		
	}
	
	/** 화면 새로고침 시 호출 */
	public void refresh(int cycle, Colony colony, ColonyManager superInstance) {
		// TODO
	}
	
	/** Ship 객체 찾기 */
	public Ship getShip(Colony col) {
		return col.getShip(getShipKey());
	}

	public long getShipKey() {
		return shipKey;
	}

	public void setShipKey(long shipKey) {
		this.shipKey = shipKey;
	}
}
