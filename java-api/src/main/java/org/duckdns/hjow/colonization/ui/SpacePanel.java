package org.duckdns.hjow.colonization.ui;

import java.awt.Component;

import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.ui.graphics.Coordinate3D;

/** 함선 위치 현황 출력을 위한 패널 */
public interface SpacePanel extends Disposeable {
	public Component getComponent();
	public void refresh();
	public void setCameraLocation(Coordinate3D cameraLocation);
	public void setCameraYaw(double cameraYaw);
	public void setCameraPitch(double cameraPitch);
	/** 카메라 회전 */
	public void rotateCamera(double yaw, double pitch);
	/** 카메라가 특정 좌표를 바라보도록 방향 조정시키기 (카메라 위치는 변하지 않음) */
    public void setCameraToSee(Coordinate3D target);
	public Coordinate3D getCameraLocation();
	public double getCameraYaw();
	public double getCameraPitch();
	public Colony getColony();
	/** 정착지 객체 받기 */
	public void setColony(Colony colony);
}