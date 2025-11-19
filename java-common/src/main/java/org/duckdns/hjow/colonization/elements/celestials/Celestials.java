package org.duckdns.hjow.colonization.elements.celestials;

import java.util.List;

import org.duckdns.hjow.colonization.elements.HasLocation;
import org.duckdns.hjow.colonization.elements.enemies.Enemy;
import org.duckdns.hjow.colonization.elements.products.Product;

/** 천체, 여기서는 탐험지 */
public interface Celestials extends HasLocation {
	/** 적과 보상이 모두 남아있지 않으면 true 반환 */
	public boolean isEmpty();
	/** 적 목록 반환 */
	public List<Enemy> getEnemies();
	/** 전리품 목록 반환 */
	public List<Product> getDebries();
	/** 공개 여부 반환 */
	public boolean isOpened();
	/** 공개 여부 설정 */
	public void setOpened(boolean opened);
	
}
