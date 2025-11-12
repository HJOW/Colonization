package org.duckdns.hjow.colonization.elements.ship;

import java.util.List;

import org.duckdns.hjow.colonization.elements.AttackableObject;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.HasLocation;
import org.duckdns.hjow.colonization.elements.products.Product;
import org.duckdns.hjow.colonization.elements.states.State;

/** 함선 */
public interface Ship extends AttackableObject, HasLocation {
	/** 속도 - 1 사이클 당 이동 거리 반환 */
    public int getSpeed();
    /** 실제 속도 반환 (연구 등 적용) */
    public long getRealSpeed(Colony col);
    /** 상태 객체들 반환 */
    public List<State> getStates();
    /** 화물칸 내에 있는 Product 들 반환 */
    public List<Product> getStored();
    /** 해당 화물 적재 */
    public void store(Product p);
    /** 현재 화물 적재량 (특정 화물만 카운트) */
    public int getStoredCount(String productType);
    /** 현재 화물 적재량 */
    public int getStoredCount();
    /** 최대 화물 수용량 */
    public int getMaxStoredCapacity();
    /** 목적지 X 좌표 */
    public long getDestinationX();
    /** 목적지 Y 좌표 */
    public long getDestinationY();
    /** 목적지 Z 좌표 */
    public long getDestinationZ();
    /** 정지 명령 */
    public void stop();
    /** 해당 좌표로 이동 명령 */
    public void moveStartTo(int x, int y, int z);
    /** 도착 예정시간 (사이클 수) 반환 */
    public long getEstimatedArrivalTime(Colony colony);
    /** 도착 여부 반환 */
    public boolean isArrived();
}
