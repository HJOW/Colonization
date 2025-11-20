package org.duckdns.hjow.colonization.ui;

import javax.swing.JLabel;

import org.duckdns.hjow.colonization.ColonyManagerInterface;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ship.Ship;

/** 함선 작업현황 표시기 */
public class ShipMovementIndicator extends JLabel {
	private static final long serialVersionUID = 2154542097135255891L;
	protected transient String currentText = "";
	
	public ShipMovementIndicator() { getMainText(null); }
	
	@Override
	public void setText(String text) {
		if(text == null) text = "";
		if(currentText != null && currentText.equals(text)) return;
		
		currentText = text;
		super.setText(text);
	}
	
	/** 함선의 메인 상태 아이콘 텍스트 반환 */
	protected String getMainText(Ship ship) {
		if(ship == null) return "■";
		if(ship.isArrived()) {
			return "■";
		} else {
			return "▶";
		}
	}
	
	protected String getLeftText(int cycle, Ship ship) {
		int mods = cycle % 4;
		if(ship.isArrived()) {
			return "";
		}
		switch(mods) {
		case 0:
			return "";
		case 1:
			return "▷";
		case 2:
			return "▷▷";
		}
		return "▷▷▷";
	}
	
	protected String getRightText(int cycle, Ship ship) {
		int mods = cycle % 4;
		if(ship.isArrived()) {
			return "";
		}
		switch(mods) {
		case 0:
			return "▷▷▷";
		case 1:
			return "▷▷";
		case 2:
			return "▷";
		}
		return "";
	}
	
	/** 새로 고침 시 호출되는 메소드 */
	public void refresh(int cycle, Ship ship, Colony colony, ColonyManagerInterface superInstance) {
		setText(getLeftText(cycle, ship) + getMainText(ship) + getRightText(cycle, ship));
	}
}
