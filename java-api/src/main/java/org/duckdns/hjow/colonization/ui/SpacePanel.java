package org.duckdns.hjow.colonization.ui;

import java.awt.Component;

import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.ui.graphics.Coordinate3D;

/** 함선 위치 현황 출력을 위한 패널 */
public interface SpacePanel extends Disposeable {
	public Component getComponent();
	public void refresh();
	/** 카메라 위치 초기화 */
	public void resetCameraPosition();
	/** 카메라 위치 지정 */
	public void setCameraLocation(Coordinate3D cameraLocation);
	/** 카메라 XY 회전 방향 지정 */
	public void setCameraYaw(double cameraYaw);
	/** 카메라 XZ 회전 방향 지정 */
	public void setCameraPitch(double cameraPitch);
	/** 카메라 회전 (yaw, pitch 모두 지정) */
	public void rotateCamera(double yaw, double pitch);
	/** 카메라가 정착지 좌표를 바라보도록 방향 조정시키기 (카메라 위치는 변하지 않음) */
    public void setCameraToSee();
	/** 카메라가 특정 좌표를 바라보도록 방향 조정시키기 (카메라 위치는 변하지 않음) */
    public void setCameraToSee(Coordinate3D target);
    public void setMessage(String message);
    public String getMessage();
    /** 카메라 위치 좌표 반환 */
	public Coordinate3D getCameraLocation();
	public double getCameraYaw();
	public double getCameraPitch();
	public int getViewWidth();
	public void setViewWidth(int viewWidth);
	public int getViewHeight();
	public void setViewHeight(int viewHeight);
	/** 정착지 객체 반환 */
	public Colony getColony();
	/** 정착지 객체 받기 */
	public void setColony(Colony colony);
}