package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.duckdns.hjow.colonization.ColonyManager;
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
	public void refresh(int cycle, Colony colony, ColonyManager superInstance) {
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
				ShipPanel pnOne = new ShipPanel(s);
				
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
		
		// 3차원 그리기 (Graphics 로)
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		List<OvalObjects> ovals = new ArrayList<OvalObjects>();
		
		int rootWidth  = getWidth();
		int rootHeight = getHeight();
		int centerX = rootWidth  / 2;
		int centerY = rootHeight / 2;
		long divides = 10L;
		
		// 점들 그리기 (함선)
		for(Ship ship : colony.getShips()) {
			Coordinate coordinate = new Coordinate(ship.getX(), ship.getY(), ship.getZ());
			coordinate = coordinate.project(new Coordinate(colony.getX(),  colony.getY(),  colony.getZ()), (double) centerX, (double) centerY);
			
			OvalObjects ov = new OvalObjects();
			ov.setCenter(coordinate);
			ov.setR(5);
			ovals.add(ov);
		}
		
		// 출력
		for(OvalObjects ov : ovals) {
			g2d.setColor(ov.getColor());
			g2d.fillOval((int) (ov.getCenter().getX() / divides), (int) (ov.getCenter().getY() / divides), ov.getR(), ov.getR());
		}
	}
}

/** 원형 도형 */
class OvalObjects implements Serializable {
	private static final long serialVersionUID = -3566815526299588912L;
	protected Coordinate center;
	protected int r;
	protected Color color = Color.BLUE;
	public OvalObjects() {}
	public Coordinate getCenter() {
		return center;
	}
	public void setCenter(Coordinate center) {
		this.center = center;
	}
	public int getR() {
		return r;
	}
	public void setR(int r) {
		this.r = r;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
}

/** 2D / 3D 좌표 */
class Coordinate implements Serializable {
	private static final long serialVersionUID = -3845428403760941094L;
	protected long x, y, z;
	public Coordinate() {}
	public Coordinate(long x, long y) {this.x = x; this.y = y;}
	public Coordinate(long x, long y, long z) {this.x = x; this.y = y; this.z = z;}

	public long getX() {
		return x;
	}

	public void setX(long x) {
		this.x = x;
	}

	public long getY() {
		return y;
	}

	public void setY(long y) {
		this.y = y;
	}

	public long getZ() {
		return z;
	}

	public void setZ(long z) {
		this.z = z;
	}
	
	/** 2D 영역에 투사, 새 2D 좌표 반환 (Z값이 초기화되지 않음) */
	public Coordinate project(Coordinate camera, double screenCenterX, double screenCenterY) {
		return project(camera, 500, screenCenterX, screenCenterY);
	}
	
	/** 2D 영역에 투사, 새 2D 좌표 반환 (Z값이 초기화되지 않음) */
	public Coordinate project(Coordinate camera, double focalLength, double screenCenterX, double screenCenterY) {
		// Translate point relative to camera
        double x_prime = getX() - camera.getX();
        double y_prime = getY() - camera.getY();
        double z_prime = getZ() - camera.getZ();

        // Apply perspective projection formula
        double scale = focalLength / z_prime;
        double x2d = (x_prime * scale) + screenCenterX;
        double y2d = (y_prime * scale) + screenCenterY;

        return new Coordinate((long) x2d, (long) y2d);
	}
}