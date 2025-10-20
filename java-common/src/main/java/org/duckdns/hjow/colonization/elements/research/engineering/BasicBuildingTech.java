package org.duckdns.hjow.colonization.elements.research.engineering;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

public class BasicBuildingTech extends Research {
    private static final long serialVersionUID = 1818201774541715641L;

    public BasicBuildingTech() { super(); }

    @Override
    public String getName() {
        return "BasicBuildingTech";
    }
    
    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 600L; }
    public double getMaxProgressIncreaseRate() { return 1.5;  }
    
    @Override
    public List<ResearchCondition> getResearchCoditions(Colony col, int level) {
    	List<ResearchCondition> list = new ArrayList<ResearchCondition>();
    	list.add(new ResearchCondition("BasicScience", 1, 3.0));
    	list.add(new ResearchCondition("BasicEngineering", 1, 2.0));
    	list.add(new ResearchCondition("NewMetals", 10, 0.0));
    	list.add(new ResearchCondition("NewMetals", 1, 1.0, 25));
    	list.add(new ResearchCondition("BasicHumanities", 1, 1.0, 15));
    	return list;
    }

    @Override
    public String getTitle() {
        return ColonyManager.t("기초건축학");
    }
}
