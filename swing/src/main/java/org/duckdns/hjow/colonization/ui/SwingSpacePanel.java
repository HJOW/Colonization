package org.duckdns.hjow.colonization.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.celestials.Celestials;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.ship.Ship;
import org.duckdns.hjow.commons.ui.graphics.AdvancedCoordinate3D;
import org.duckdns.hjow.commons.ui.graphics.Coordinate2D;
import org.duckdns.hjow.commons.ui.graphics.LineObject2D;
import org.duckdns.hjow.commons.ui.graphics.OvalObject2D;

/** 함선 위치 현황 출력을 위한 패널 - 기본 Swing 의 Graphics 2D 사용 */ // TODO SampleJavaCodes 에 있는 Space3D 예제 참고하여 yaw, pitch (카메라의 방향) 개념 적용
public class SwingSpacePanel extends SpacePanel {
	private static final long serialVersionUID = 7794697275657421978L;
    public SwingSpacePanel() { super(); }
    
    /** 그리기 작업 수행 */
	protected void draw(Graphics g) {
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
		double focals = 500.0;
		
		// 점들 그리기 (도시)
		for(City city : colony.getCities()) {
			AdvancedCoordinate3D coordinate = new AdvancedCoordinate3D(city.getX(), city.getY(), city.getZ());
			
			// 2D에 투영
			Coordinate2D proj = coordinate.project(getCameraLocation(), getCameraYaw(), getCameraPitch(), focals, (double) centerX, (double) centerY);
			
			OvalObject2D ov = new OvalObject2D();
			ov.setCenter(proj); // 2D 정보만 입력됨
			ov.setR(30);
			ov.setColor(Color.BLUE);
			ovals.add(ov);
		}
		
		// 점들 그리기 (천체)
		for(Celestials cele : colony.getCelestials()) {
			AdvancedCoordinate3D coordinate = new AdvancedCoordinate3D(cele.getX(), cele.getY(), cele.getZ());
			if(! cele.isOpened()) continue;
			
			// 2D에 투영 - 이렇게 만들어진 "좌표" 에는 Z축이 없음에 유의 !
			Coordinate2D proj = coordinate.project(getCameraLocation(), getCameraYaw(), getCameraPitch(), focals, (double) centerX, (double) centerY);
			
			OvalObject2D ov = new OvalObject2D();
			ov.setCenter(proj); // 2D 정보만 입력됨
			ov.setR(21);
			ov.setColor(Color.MAGENTA);
			ovals.add(ov);
		}
		
		// 점들 그리기 (함선)
		for(Ship ship : colony.getShips()) {
			AdvancedCoordinate3D coordinate = new AdvancedCoordinate3D(ship.getX(), ship.getY(), ship.getZ());
			
			// 2D에 투영 - 이렇게 만들어진 "좌표" 에는 Z축이 없음에 유의 !
			Coordinate2D proj = coordinate.project(getCameraLocation(), getCameraYaw(), getCameraPitch(), focals, (double) centerX, (double) centerY);
			
			OvalObject2D ov = new OvalObject2D();
			ov.setCenter(proj); // 2D 정보만 입력됨
			ov.setR(15);
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
		
		// 정보 출력
		int sy = 20;
		g2d.drawString(ColonyManager.t("카메라 위치 : [X], [Y], [Z]").replace("[X]", ColonyManager.formatInt(cameraLocation.getX())).replace("[Y]", ColonyManager.formatInt(cameraLocation.getY())).replace("[Z]", ColonyManager.formatInt(cameraLocation.getZ())), 10, sy); sy += 10;
		g2d.drawString(ColonyManager.t("카메라 방향 : [YAW], [PITCH]").replace("[YAW]", ColonyManager.formatRate(getCameraYaw())).replace("[PITCH]", ColonyManager.formatRate(getCameraPitch())), 10, sy); sy += 10;
		
	}
}
