package org.duckdns.hjow.colonization.ui;

import java.awt.Graphics;

import javax.swing.JPanel;

import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.commons.core.Disposeable;

/** 함선 위치 현황 출력을 위한 패널 */
public abstract class SpacePanel extends JPanel implements Disposeable {
	private static final long serialVersionUID = 3120354750879319010L;
	protected Colony colony;
	public SpacePanel() { setLayout(null); }
	
	/** 정착지 객체 받기 */
	public void setColony(Colony colony) {
		this.colony = colony;
		refresh();
	}
	
	@Override
	public void dispose() {
		colony = null;
	}
	
	/** 새로고침 */
	public void refresh() {
		repaint();
	}
	
	/** 그리기 작업 수행 */
	protected abstract void draw(Graphics g);
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
}