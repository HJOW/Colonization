package org.duckdns.hjow.colonization.elements.products;

import org.duckdns.hjow.colonization.ColonyManager;

/** 엄밀히 말해 Product 는 아니고, 화면 출력용 (공장에서 Product 가 아닌 예산을 생산하는 경우) */
public class Money extends Product {
    private static final long serialVersionUID = -2161207384444617048L;

    @Override
    protected String getDefaultNamePrefix() {
        return ColonyManager.t("기타재화");
    }

    @Override
    public String getTitle() {
        return ColonyManager.t("자율 생산 (예산 수급)");
    }

}
