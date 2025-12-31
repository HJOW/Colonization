package org.duckdns.hjow.colonization.elements.research.chemical;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.AbstractResearch;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

public class NewMetals extends AbstractResearch {
    private static final long serialVersionUID = -6806613925102489640L;

    @Override
    public String getName() {
        return "NewMetals";
    }
    
    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 60L; }
    public double getMaxProgressIncreaseRate() { return 1.2;  }
    
    @Override
    public List<ResearchCondition> getResearchCoditions(Colony col, int level) {
        List<ResearchCondition> list = new ArrayList<ResearchCondition>();
        list.add(new ResearchCondition("BasicScience", 1, 5.0));
        list.add(new ResearchCondition("Chemical", 1, 2.0));
        list.add(new ResearchCondition("BasicEngineering", 1, 7.0));
        list.add(new ResearchCondition("BasicEngineering", 10, 0.0));
        return list;
    }

    @Override
    public String getTitle() {
        return ColonyManager.t("신금속공학");
    }
}
