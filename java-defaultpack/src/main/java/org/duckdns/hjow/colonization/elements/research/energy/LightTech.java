package org.duckdns.hjow.colonization.elements.research.energy;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

public class LightTech extends Research {
    private static final long serialVersionUID = -6737998466808533544L;

    @Override
    public String getName() {
        return "LightTech";
    }
    
    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 600L; }
    public double getMaxProgressIncreaseRate() { return 1.2;  }
    
    @Override
    public List<ResearchCondition> getResearchCoditions(Colony col, int level) {
    	List<ResearchCondition> list = new ArrayList<ResearchCondition>();
    	list.add(new ResearchCondition("BasicScience", 1, 5.0));
    	list.add(new ResearchCondition("BasicScience", 10, 0.0));
    	list.add(new ResearchCondition("BasicEngineering", 1, 3.0));
    	list.add(new ResearchCondition("BasicEngineering", 6, 0.0));
    	list.add(new ResearchCondition("EnergyTech", 1, 1.0));
    	return list;
    }

    @Override
    public String getTitle() {
        return ColonyManager.t("광학");
    }
}
