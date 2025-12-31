package org.duckdns.hjow.colonization.elements.research.energy;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.AbstractResearch;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

public class Nuclear extends AbstractResearch {
    private static final long serialVersionUID = -1087627366882865085L;

    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 80L; }
    public double getMaxProgressIncreaseRate() { return 1.4;  }
    
    @Override
    public List<ResearchCondition> getResearchCoditions(Colony col, int level) {
        List<ResearchCondition> list = new ArrayList<ResearchCondition>();
        list.add(new ResearchCondition("EnergyTech", 40, 5.0));
        list.add(new ResearchCondition("Chemical", 30, 3.0));
        list.add(new ResearchCondition("LightTech", 30, 2.0));
        return list;
    }

    @Override
    public String getTitle() {
        return ColonyManager.t("원자력");
    }
}
