package org.duckdns.hjow.colonization.elements.products.metal;

import org.duckdns.hjow.colonization.ColonyManager;

/** 카테고리 2 금속 - 구리, 은, 금, 백금 등 비교적 구하기 어려운 금속들 */
public class Iron2 extends Metal {
	private static final long serialVersionUID = -5491988745491442635L;

	@Override
	protected String getDefaultNamePrefix() {
		return ColonyManager.t("금속-카테고리2");
	}

	@Override
	public String getTitle() {
		return ColonyManager.t("금속 - 카테고리2");
	}

	@Override
	public int getCategory() {
		return 2;
	}

}
