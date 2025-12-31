package org.duckdns.hjow.colonization.elements.research.energy;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.AbstractResearch;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

public class FissionReactor extends AbstractResearch {
    private static final long serialVersionUID = 6658907007666189048L;

    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 120L; }
    public double getMaxProgressIncreaseRate() { return 1.5;  }
    
    @Override
    public List<ResearchCondition> getResearchCoditions(Colony col, int level) {
        List<ResearchCondition> list = new ArrayList<ResearchCondition>();
        list.add(new ResearchCondition("EnergyTech", 40, 6.0));
        list.add(new ResearchCondition("Nuclear", 1, 1.0));
        list.add(new ResearchCondition("BasicBuildingTech", 30, 3.0));
        return list;
    }

    @Override
    public String getTitle() {
        return ColonyManager.t("핵분열 반응로 기술");
    }
}
