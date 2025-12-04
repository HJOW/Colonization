package org.duckdns.hjow.colonization.elements;

import org.duckdns.hjow.commons.ui.graphics.Coordinate3D;

/** 이동 가능한 객체임을 표시 */
public interface Movable extends HasLocation {
	/** 목적지 X 좌표 반환 */
    public long getDestinationX();
    /** 목적지 Y 좌표 반환 */
    public long getDestinationY();
    /** 목적지 Z 좌표 반환 */
    public long getDestinationZ();
    /** 목적지 반환 */
    public Coordinate3D getDestination();
    /** 목적지 X 좌표 변경 */
    public void setDestinationX(long destinationX);
    /** 목적지 Y 좌표 변경 */
    public void setDestinationY(long destinationY);
    /** 목적지 Z 좌표 변경 */
    public void setDestinationZ(long destinationZ);
    /** 목적지 좌표 변경 */
    public void setDestination(Coordinate3D dest);
    /** 도착 여부 반환 */
    public boolean isArrived();
    /** 속도 - 1 사이클 당 이동 거리 반환 */
    public int getSpeed();
    /** 도착 예정시간 (사이클 수) 반환 */
    public long getEstimatedArrivalTime(Colony colony);
}
