package org.duckdns.hjow.colonization.elements.research.energy;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.AbstractResearch;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

public class NuclearFusion extends AbstractResearch {
    private static final long serialVersionUID = 7873858927668983687L;

    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 2000L; }
    public double getMaxProgressIncreaseRate() { return 2.0;  }
    
    @Override
    public List<ResearchCondition> getResearchCoditions(Colony col, int level) {
        List<ResearchCondition> list = new ArrayList<ResearchCondition>();
        list.add(new ResearchCondition("EnergyTech", 50, 6.0));
        list.add(new ResearchCondition("Nuclear", 5, 1.5));
        list.add(new ResearchCondition("Plasma", 5, 1.5));
        list.add(new ResearchCondition("Plasteel", 2, 1.2));
        return list;
    }

    @Override
    public String getTitle() {
        return ColonyManager.t("핵융합 기술");
    }
}
