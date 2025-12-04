package org.duckdns.hjow.colonization.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Space;
import org.duckdns.hjow.colonization.elements.celestials.Celestials;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.enemies.Enemy;
import org.duckdns.hjow.colonization.elements.ship.Ship;
import org.duckdns.hjow.commons.ui.graphics.Coordinate2D;
import org.duckdns.hjow.commons.ui.graphics.Coordinate3D;
import org.duckdns.hjow.commons.ui.graphics.LineObject2D;
import org.duckdns.hjow.commons.ui.graphics.OvalObject2D;
import org.duckdns.hjow.commons.ui.graphics.TextObject2D;

/** 함선 위치 현황 출력을 위한 패널 - 기본 Swing 의 Graphics 2D 사용 */
public class SwingSpacePanel extends AbstractSpacePanel {
    private static final long serialVersionUID = 7794697275657421978L;
    public SwingSpacePanel() { super(); }
    
    /** 새로고침 */
    @Override
    public void refresh() {
        repaint();
    }
    
    /** 그리기 작업 수행 */
    @Override
    protected void draw(Graphics g) {
        if(colony == null) return;
        Space space = colony.getSpace();
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(new Font(null, Font.PLAIN, 12));
        
        FontMetrics metric = g2d.getFontMetrics();
        
        // 배경 그리기
        int rootWidth  = getViewWidth();
        int rootHeight = getViewHeight();
        
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, rootWidth, rootHeight);
        
        // 3차원 그리기 (Graphics 로)
        List<LineObject2D> lines = new ArrayList<LineObject2D>();
        List<OvalObject2D> ovals = new ArrayList<OvalObject2D>();
        List<TextObject2D> texts = new ArrayList<TextObject2D>();
        
        int centerX   = (int) Math.round((rootWidth  / 2.0) * 10.0);
        int centerY   = (int) Math.round((rootHeight / 2.0) *  2.0);
        long divides  = 10L;
        double sizes  = 1000.0;
        double focals = 2000.0;
        
        double radius;
        
        // 도시 그리기
        for(City city : colony.getCities()) {
            Coordinate3D coordinate = new Coordinate3D(city.getX(), city.getY(), city.getZ());
            
            // 2D에 투영
            // Coordinate2D proj = coordinate.project(getCameraLocation(), getCameraYaw(), getCameraPitch(), focals, (double) centerX, (double) centerY);
            Coordinate2D proj = project(coordinate, getCameraLocation(), getCameraYaw(), getCameraPitch(), focals, (double) centerX, (double) centerY);
            if(proj == null) continue; // 카메라 뒤로 가려지는 케이스 존재
            
            radius = Math.ceil(sizes * 3 / ( coordinate.getDistance(getCameraLocation()) + 0.01 ));
            if(radius >= Integer.MAX_VALUE) radius = (double) Integer.MAX_VALUE;
            if(radius < 1.0) radius = 1.0;
            
            OvalObject2D ov = new OvalObject2D();
            ov.setCenter(proj); // 2D 정보만 입력됨
            ov.setR((int) radius);
            ov.setColor(Color.BLUE);
            ovals.add(ov);
            
            TextObject2D tx = new TextObject2D();
            tx.setLeft(ov.getCenter());
            tx.getLeft().setY(tx.getLeft().getY() + 10);
            tx.setColor(Color.WHITE);
            tx.setContent(city.getName());
            texts.add(tx);
        }
        
        // 천체 그리기
        for(Celestials cele : space.getCelestials()) {
            Coordinate3D coordinate = new Coordinate3D(cele.getX(), cele.getY(), cele.getZ());
            // if(! cele.isOpened()) continue; // 그리기는 해야 함
            
            // 2D에 투영 - 이렇게 만들어진 "좌표" 에는 Z축이 없음에 유의 !
            // Coordinate2D proj = coordinate.project(getCameraLocation(), getCameraYaw(), getCameraPitch(), focals, (double) centerX, (double) centerY);
            Coordinate2D proj = project(coordinate, getCameraLocation(), getCameraYaw(), getCameraPitch(), focals, (double) centerX, (double) centerY);
            if(proj == null) continue; // 카메라 뒤로 가려지는 케이스 존재
            
            radius = Math.ceil(sizes * 2 / ( coordinate.getDistance(getCameraLocation()) + 0.01 ));
            if(radius >= Integer.MAX_VALUE) radius = (double) Integer.MAX_VALUE;
            if(radius < 1.0) radius = 1.0;
            
            OvalObject2D ov = new OvalObject2D();
            ov.setCenter(proj); // 2D 정보만 입력됨
            ov.setR((int) radius);
            ov.setColor(Color.MAGENTA);
            ovals.add(ov);
        }
        
        // 함선 그리기
        for(Ship ship : space.getShips()) {
            if(ship.getHp() <= 0) continue;
            Coordinate3D coordinate = new Coordinate3D(ship.getX(), ship.getY(), ship.getZ());
            
            City city = colony.findCity(ship);
            if(ship.getX() == city.getX() && ship.getY() == city.getY() && ship.getZ() == city.getZ()) continue; // 도시 안에 정박중인 경우 스킵
            
            // 2D에 투영
            // Coordinate2D proj = coordinate.project(getCameraLocation(), getCameraYaw(), getCameraPitch(), focals, (double) centerX, (double) centerY);
            Coordinate2D proj = project(coordinate, getCameraLocation(), getCameraYaw(), getCameraPitch(), focals, (double) centerX, (double) centerY);
            if(proj == null) continue; // 카메라 뒤로 가려지는 케이스 존재 
            
            radius = Math.ceil(sizes * 1.5 / ( coordinate.getDistance(getCameraLocation()) + 0.01 ));
            if(radius >= Integer.MAX_VALUE) radius = (double) Integer.MAX_VALUE;
            if(radius < 1.0) radius = 1.0;
            
            OvalObject2D ov = new OvalObject2D();
            ov.setCenter(proj); // 2D 정보만 입력됨
            ov.setR((int) radius);
            ov.setColor(Color.GREEN);
            ovals.add(ov);
            
            if(! ship.isArrived()) {
                // Coordinate2D dest = ship.getDestination().project(getCameraLocation(), getCameraYaw(), getCameraPitch(), focals, (double) centerX, (double) centerY);
                Coordinate2D dest = project(ship.getDestination(), getCameraLocation(), getCameraYaw(), getCameraPitch(), focals, (double) centerX, (double) centerY);
                
                LineObject2D line = new LineObject2D();
                line.setFrom(proj);
                line.setTo(dest);
                line.setColor(Color.GRAY);
                lines.add(line);
            }
        }
        
        // 적 그리기
        for(Enemy en : space.getEnemies()) {
            if(en.getHp() <= 0) continue;
            
            Coordinate3D coordinate = new Coordinate3D(en.getX(), en.getY(), en.getZ());
            
            // 정박 중인지 체크
            boolean docked =  false;
            for(City ct : colony.getCities()) {
                if(en.isSameLocation(ct.getCoordinate())) { docked = true; break; }
            }
            for(Celestials cele : colony.getSpace().getCelestials()) {
                if(en.isSameLocation(cele.getCoordinate())) { docked = true; break; }
            }
            if(docked) continue;
            
            // 2D에 투영
            // Coordinate2D proj = coordinate.project(getCameraLocation(), getCameraYaw(), getCameraPitch(), focals, (double) centerX, (double) centerY);
            Coordinate2D proj = project(coordinate, getCameraLocation(), getCameraYaw(), getCameraPitch(), focals, (double) centerX, (double) centerY);
            if(proj == null) continue; // 카메라 뒤로 가려지는 케이스 존재 
            
            radius = Math.ceil(sizes * 1.3 / ( coordinate.getDistance(getCameraLocation()) + 0.01 ));
            if(radius >= Integer.MAX_VALUE) radius = (double) Integer.MAX_VALUE;
            if(radius < 1.0) radius = 1.0;
            
            OvalObject2D ov = new OvalObject2D();
            ov.setCenter(proj); // 2D 정보만 입력됨
            ov.setR((int) radius);
            ov.setColor(Color.RED);
            ovals.add(ov);
            
            if(! en.isArrived()) {
                // Coordinate2D dest = ship.getDestination().project(getCameraLocation(), getCameraYaw(), getCameraPitch(), focals, (double) centerX, (double) centerY);
                Coordinate2D dest = project(en.getDestination(), getCameraLocation(), getCameraYaw(), getCameraPitch(), focals, (double) centerX, (double) centerY);
                
                LineObject2D line = new LineObject2D();
                line.setFrom(proj);
                line.setTo(dest);
                line.setColor(Color.MAGENTA);
                lines.add(line);
            }
            
        }
        
        // 적절한 스케일 구하기
        long max = 0L;
        long abs = 0L;
        int x, y, tox, toy;
        
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
            x = (int) (ov.getCenter().getX() / divides);
            y = (int) (ov.getCenter().getY() / divides);
            g2d.fillOval(x, y, ov.getR(), ov.getR());
        }
        
        for(LineObject2D ln : lines) {
            g2d.setColor(ln.getColor());
            x = (int) (ln.getFrom().getX() / divides);
            y = (int) (ln.getFrom().getY() / divides);
            tox = (int) (ln.getTo().getX() / divides);
            toy = (int) (ln.getTo().getY() / divides);
            g2d.drawLine(x, y, tox, toy);
        }
        
        for(TextObject2D tx : texts) {
            g2d.setColor(tx.getColor());
            x = (int) ((tx.getLeft().getX() - (metric.stringWidth(tx.getContent()) / 2)) / divides);
            y = (int) (tx.getLeft().getY() / divides);
            g2d.drawString(tx.getContent(), x, y);
        }
        
        // 정보 출력
        int sy = 20;
        g2d.setColor(Color.DARK_GRAY);
        StringTokenizer lineTokenzier = new StringTokenizer(getMessage(), "\n");
        while(lineTokenzier.hasMoreTokens()) {
            g2d.drawString(ColonyManager.t(lineTokenzier.nextToken().replace("[X]", String.valueOf(cameraLocation.getX())).replace("[Y]", String.valueOf(cameraLocation.getY())).replace("[Z]", String.valueOf(cameraLocation.getZ())).replace("[YAW]", ColonyManager.formatRate(getCameraYaw())).replace("[PITCH]", ColonyManager.formatRate(getCameraPitch()))  ), 10, sy); sy+=15;
        }
    }
}
