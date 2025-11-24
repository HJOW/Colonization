package org.duckdns.hjow.colonization.ui;

import java.awt.Component;

import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.ui.graphics.Coordinate3D;

/** 함선 위치 현황 출력을 위한 패널 */
public interface SpacePanel extends Disposeable {
	public Component getComponent();
	public void setCameraLocation(Coordinate3D cameraLocation);
	public void refresh();
	public void setCameraYaw(double cameraYaw);
	public void setCameraPitch(double cameraPitch);
	public Colony getColony();
	/** 정착지 객체 받기 */
	public void setColony(Colony colony);
}