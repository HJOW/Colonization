package org.duckdns.hjow.colonization.elements.enemies;

import org.duckdns.hjow.colonization.elements.AttackableObject;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.Movable;

/** 적 개체 */
public interface Enemy extends Movable, AttackableObject {
	/** 속도 변경 */
    public void setSpeed(int speed);
    /** 이동 사이클 수행 (정착지 oneCycle 에서 호출) */
    public void processMove(int cycle, Colony col);
}
