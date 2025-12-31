package org.duckdns.hjow.colonization.elements.research.humanities;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.research.AbstractResearch;

public class BasicHumanities extends AbstractResearch {
    private static final long serialVersionUID = 7591943260162203350L;

    public BasicHumanities() { super(); }

    @Override
    public String getName() {
        return "BasicHumanities";
    }
    
    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 60L; }
    public double getMaxProgressIncreaseRate() { return 1.1;  }


    @Override
    public String getTitle() {
        return ColonyManager.t("기초인문학");
    }
}
