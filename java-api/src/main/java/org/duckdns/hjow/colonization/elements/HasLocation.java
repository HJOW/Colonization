package org.duckdns.hjow.colonization.elements;

/** 위치 개념이 존재하는 객체 여부 판단 */
public interface HasLocation extends ColonyElements {
	public long getX();
    public long getY();
    public long getZ();
    public void setX(long x);
    public void setY(long y);
    public void setZ(long z);
}
