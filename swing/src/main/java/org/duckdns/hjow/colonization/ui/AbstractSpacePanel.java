package org.duckdns.hjow.colonization.ui;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JPanel;

import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.commons.ui.graphics.Coordinate3D;

/** 함선 위치 현황 출력을 위한 패널 */
public abstract class AbstractSpacePanel extends JPanel implements SpacePanel {
	private static final long serialVersionUID = 3120354750879319010L;
	protected Colony       colony;
	protected Coordinate3D cameraLocation = new Coordinate3D(0L, 0L, 0L);
	protected double       cameraYaw      = 0.0;
	protected double       cameraPitch    = 0.0;
	public AbstractSpacePanel() { setLayout(null); }
	
	/** 정착지 객체 받기 */
	@Override
	public void setColony(Colony colony) {
		this.colony = colony;
		if(colony != null) { 
			cameraLocation.setX(colony.getX() - 100L);
			cameraLocation.setY(colony.getY() - 100L);
			cameraLocation.setZ(colony.getZ() +  10L);
		}
		refresh();
	}
	
	@Override
	public void dispose() {
		colony = null;
	}
	
	/** 새로고침 */
	@Override
	public void refresh() { }
	
	/** 그리기 작업 수행 */
	protected void draw(Graphics g) { }
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}

	@Override
	public Coordinate3D getCameraLocation() {
		return cameraLocation;
	}

	@Override
	public void setCameraLocation(Coordinate3D cameraLocation) {
		this.cameraLocation = cameraLocation;
	}

	@Override
	public double getCameraYaw() {
		return cameraYaw;
	}

	@Override
	public void setCameraYaw(double cameraYaw) {
		this.cameraYaw = cameraYaw;
	}

	@Override
	public double getCameraPitch() {
		return cameraPitch;
	}

	@Override
	public void setCameraPitch(double cameraPitch) {
		this.cameraPitch = cameraPitch;
	}
	
	public void rotateCamera(double yaw, double pitch) {
		setCameraYaw(yaw);
		setCameraPitch(pitch);
		
		while(this.getCameraYaw()   > Math.PI * 2) this.setCameraYaw(  this.getCameraYaw()   - (2 * Math.PI)); 
		while(this.getCameraPitch() > Math.PI * 2) this.setCameraPitch(this.getCameraPitch() - (2 * Math.PI)); 
		while(this.getCameraYaw()   < 0) this.setCameraYaw(  2 * Math.PI - this.getCameraYaw()  );
		while(this.getCameraPitch() < 0) this.setCameraPitch(2 * Math.PI - this.getCameraPitch());
	}

	@Override
	public Colony getColony() {
		return colony;
	}
	
	@Override
	public Component getComponent() {
		return this;
	}
}