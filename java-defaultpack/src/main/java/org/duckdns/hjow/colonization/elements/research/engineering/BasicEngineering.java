package org.duckdns.hjow.colonization.elements.research.engineering;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

public class BasicEngineering extends Research {
    private static final long serialVersionUID = -2727850120481565932L;

    public BasicEngineering() { super(); }

    @Override
    public String getName() {
        return "BasicEngineering";
    }
    
    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 900L; }
    public double getMaxProgressIncreaseRate() { return 1.1;  }
    
    @Override
    public List<ResearchCondition> getResearchCoditions(Colony col, int level) {
    	List<ResearchCondition> list = new ArrayList<ResearchCondition>();
    	list.add(new ResearchCondition("BasicScience", 1, 2.0));
    	list.add(new ResearchCondition("BasicHumanities", 1, 1.0, 30));
    	return list;
    }

    @Override
    public String getTitle() {
        return ColonyManager.t("공학기초");
    }
}
