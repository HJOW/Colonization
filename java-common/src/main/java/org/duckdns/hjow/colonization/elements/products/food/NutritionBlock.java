package org.duckdns.hjow.colonization.elements.products.food;

import org.duckdns.hjow.colonization.ColonyManager;

public class NutritionBlock extends Food {
    private static final long serialVersionUID = 74752871747134632L;

    @Override
    protected String getDefaultNamePrefix() {
        return ColonyManager.t("영양블록");
    }

    @Override
    public String getTitle() {
        return ColonyManager.t("영양블록");
    }

    @Override
    public int getComportGrade() {
        return 0;
    }

}
