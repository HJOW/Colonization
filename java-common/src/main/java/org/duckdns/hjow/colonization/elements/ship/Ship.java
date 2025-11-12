package org.duckdns.hjow.colonization.elements.ship;

import java.util.List;

import org.duckdns.hjow.colonization.elements.AttackableObject;
import org.duckdns.hjow.colonization.elements.HasLocation;
import org.duckdns.hjow.colonization.elements.products.Product;
import org.duckdns.hjow.colonization.elements.states.State;

/** 함선 */
public interface Ship extends AttackableObject, HasLocation {
	/** 1 사이클 당 이동 거리 반환 */
    public int getSpeed();
    /** 상태 객체들 반환 */
    public List<State> getStates();
    /** 화물칸 내에 있는 Product 들 반환 */
    public List<Product> getStored();
    public void store(Product p);
    public int getStoredCount(String productType);
    public int getStoredCount();
    public int getMaxStoredCapacity();
}
