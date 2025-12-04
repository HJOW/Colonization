package org.duckdns.hjow.colonization.elements;

import org.duckdns.hjow.commons.ui.graphics.Coordinate3D;

/** 위치 개념이 존재하는 객체 여부 판단 */
public interface HasLocation extends ColonyElements {
	public long getX();
    public long getY();
    public long getZ();
    public Coordinate3D getCoordinate();
    
    public void setX(long x);
    public void setY(long y);
    public void setZ(long z);
    public void setCoordinate(Coordinate3D coordinate);
    
    public boolean isSameLocation(Coordinate3D coordinate);
}
