package org.duckdns.hjow.colonization.elements.products.metal;

import org.duckdns.hjow.colonization.ColonyManager;

/** 카테고리 1 금속 - 철, 알루미늄 등 비교적 흔한 금속들 */
public class Iron1 extends Metal {
	private static final long serialVersionUID = -5491988745491442635L;

	@Override
	protected String getDefaultNamePrefix() {
		return ColonyManager.t("금속-카테고리1");
	}

	@Override
	public String getTitle() {
		return ColonyManager.t("금속 - 카테고리1");
	}

	@Override
	public int getCategory() {
		return 1;
	}

}
