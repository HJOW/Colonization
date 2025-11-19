package org.duckdns.hjow.colonization.elements.research.biology;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.AbstractResearch;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

public class GeneTech extends AbstractResearch {
    private static final long serialVersionUID = -4457886782318161313L;

    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 900L; }
    public double getMaxProgressIncreaseRate() { return 1.3;  }
    
    @Override
    public List<ResearchCondition> getResearchCoditions(Colony col, int level) {
        List<ResearchCondition> list = new ArrayList<ResearchCondition>();
        list.add(new ResearchCondition("BasicBiology", 1, 1.2));
        list.add(new ResearchCondition("Chemical", 1, 1.0));
        return list;
    }

    @Override
    public String getTitle() {
        return ColonyManager.t("유전자 공학");
    }
}
