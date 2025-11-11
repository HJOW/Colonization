package org.duckdns.hjow.colonization.elements.ship;

import org.duckdns.hjow.colonization.elements.AttackableObject;
import org.duckdns.hjow.colonization.elements.HasLocation;

/** 함선 */
public interface Ship extends AttackableObject, HasLocation {
	/** 1 사이클 당 이동 거리 반환 */
    public int getSpeed();
}
