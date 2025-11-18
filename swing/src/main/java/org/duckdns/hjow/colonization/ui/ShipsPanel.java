package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.ColonyManagerInterface;
import org.duckdns.hjow.colonization.elements.Celestials;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.ship.Ship;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.ui.graphics.Coordinate2D;
import org.duckdns.hjow.commons.ui.graphics.Coordinate3D;
import org.duckdns.hjow.commons.ui.graphics.LineObject2D;
import org.duckdns.hjow.commons.ui.graphics.OvalObject2D;

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

class SpacePanel extends JPanel implements Disposeable {
	private static final long serialVersionUID = 3120354750879319010L;
	protected Colony colony;
	public SpacePanel() { setLayout(null); }
	public void setColony(Colony colony) {
		this.colony = colony;
		repaint();
	}
	
	@Override
	public void dispose() {
		colony = null;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(colony == null) return;
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// 배경 그리기
		int rootWidth  = getWidth();
		int rootHeight = getHeight();
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, rootWidth, rootHeight);
		
		// 3차원 그리기 (Graphics 로)
		List<LineObject2D> lines = new ArrayList<LineObject2D>();
		List<OvalObject2D> ovals = new ArrayList<OvalObject2D>();
		
		int centerX = rootWidth  / 2;
		int centerY = rootHeight / 2;
		long divides = 10L;
		
		// 점들 그리기 (도시)
		for(City city : colony.getCities()) {
			Coordinate3D coordinate = new Coordinate3D(city.getX(), city.getY(), city.getZ());
			
			// 2D에 투영 - 이렇게 만들어진 "좌표" 에는 Z축이 없음에 유의 !
			Coordinate2D proj = coordinate.project(new Coordinate3D(colony.getX(),  colony.getY(),  colony.getZ()), (double) centerX, (double) centerY);
			
			OvalObject2D ov = new OvalObject2D();
			ov.setCenter(proj); // 2D 정보만 입력됨
			ov.setR(10);
			ov.setColor(Color.BLUE);
			ovals.add(ov);
		}
		
		// 점들 그리기 (천체)
		for(Celestials cele : colony.getCelestials()) {
			Coordinate3D coordinate = new Coordinate3D(cele.getX(), cele.getY(), cele.getZ());
			if(! cele.isOpened()) continue;
			
			// 2D에 투영 - 이렇게 만들어진 "좌표" 에는 Z축이 없음에 유의 !
			Coordinate2D proj = coordinate.project(new Coordinate3D(colony.getX(),  colony.getY(),  colony.getZ()), (double) centerX, (double) centerY);
			
			OvalObject2D ov = new OvalObject2D();
			ov.setCenter(proj); // 2D 정보만 입력됨
			ov.setR(7);
			ov.setColor(Color.MAGENTA);
			ovals.add(ov);
		}
		
		// 점들 그리기 (함선)
		for(Ship ship : colony.getShips()) {
			Coordinate3D coordinate = new Coordinate3D(ship.getX(), ship.getY(), ship.getZ());
			
			// 2D에 투영 - 이렇게 만들어진 "좌표" 에는 Z축이 없음에 유의 !
			Coordinate2D proj = coordinate.project(new Coordinate3D(colony.getX(),  colony.getY(),  colony.getZ()), (double) centerX, (double) centerY);
			
			OvalObject2D ov = new OvalObject2D();
			ov.setCenter(proj); // 2D 정보만 입력됨
			ov.setR(5);
			ov.setColor(Color.GREEN);
			ovals.add(ov);
		}
		
		// 적절한 스케일 구하기
		long max = 0L;
		long abs = 0L;
		
		for(OvalObject2D ov : ovals) {
			abs = Math.abs(ov.getX());
			if(max < abs) max = abs;
			
			abs = Math.abs(ov.getY());
			if(max < abs) max = abs;
		}
		
		for(LineObject2D ln : lines) {
			abs = Math.abs(ln.getFrom().getX());
			if(max < abs) max = abs;
			
			abs = Math.abs(ln.getFrom().getY());
			if(max < abs) max = abs;
			
			abs = Math.abs(ln.getTo().getX());
			if(max < abs) max = abs;
			
			abs = Math.abs(ln.getTo().getY());
			if(max < abs) max = abs;
		}
		
		while(((max / divides) / 10L) > (long) Integer.MAX_VALUE) {
			if(divides < 1L) divides = 1L;
			divides = divides * 10L;
		}
		
		// 출력
		for(OvalObject2D ov : ovals) {
			g2d.setColor(ov.getColor());
			g2d.fillOval((int) (ov.getCenter().getX() / divides), (int) (ov.getCenter().getY() / divides), ov.getR(), ov.getR());
		}
		for(LineObject2D ln : lines) {
			g2d.setColor(ln.getColor());
			g2d.drawLine((int) (ln.getFrom().getX() / divides), (int) (ln.getFrom().getY() / divides), (int) (ln.getTo().getX() / divides), (int) (ln.getTo().getY() / divides));
		}
	}
}