package org.duckdns.hjow.colonization.elements.ship;

import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;

/** 인공위성 (함선이지만 이동 능력이 매우 떨어지며, 대신 도시에 정박하고 있으면 시설과 같은 효과를 내는 함선. 공간 차지를 하지 않으며 무한히 띄울 수 있음. 대신 공격에 매우 취약하다는 특성.) */
public interface Satellite extends Ship {
	/** 발전 기능이 있는 경우 매 사이클 발전량 반환 (없으면 0 반환) */
	public int getPowerGenerate(Colony col, City city);
}
