package org.duckdns.hjow.colonization.elements.research.chemical;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.AbstractResearch;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

public class Plasteel extends AbstractResearch {
    private static final long serialVersionUID = -2169367042192180974L;

    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 1200L; }
    public double getMaxProgressIncreaseRate() { return 1.5;  }
    
    @Override
    public List<ResearchCondition> getResearchCoditions(Colony col, int level) {
        List<ResearchCondition> list = new ArrayList<ResearchCondition>();
        list.add(new ResearchCondition("NewMetals", 30, 3.0));
        list.add(new ResearchCondition("Chemical", 30, 4.0));
        list.add(new ResearchCondition("BasicBiology", 10, 2.0));
        return list;
    }

    @Override
    public String getTitle() {
        return ColonyManager.t("플라스릴 소재");
    }
}
