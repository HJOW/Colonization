package org.duckdns.hjow.colonization.elements.research.physics;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.research.AbstractResearch;

public class Mathematics extends AbstractResearch {
    private static final long serialVersionUID = -6112245443498463187L;

    public Mathematics() { super(); }
    
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
    
    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 60L; }
    public double getMaxProgressIncreaseRate() { return 1.1;  }

    @Override
    public String getTitle() {
        return ColonyManager.t("수학");
    }
}
