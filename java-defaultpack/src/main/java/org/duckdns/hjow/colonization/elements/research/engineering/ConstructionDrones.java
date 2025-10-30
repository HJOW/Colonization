package org.duckdns.hjow.colonization.elements.research.engineering;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

public class ConstructionDrones extends Research {
	private static final long serialVersionUID = -5175538458630010983L;

	@Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 1800L; }
    public double getMaxProgressIncreaseRate() { return 1.5;  }
    
    @Override
    public List<ResearchCondition> getResearchCoditions(Colony col, int level) {
    	List<ResearchCondition> list = new ArrayList<ResearchCondition>();
    	list.add(new ResearchCondition("NewMetals", 1, 10.0));
    	list.add(new ResearchCondition("LightTech", 1, 10.0));
    	list.add(new ResearchCondition("BasicEngineering", 1, 10.0));
    	list.add(new ResearchCondition("MilitaryTech", 1, 10.0));
    	list.add(new ResearchCondition("ComputerTech", 1, 1.0));
    	list.add(new ResearchCondition("Physics", 1, 4.0));
    	list.add(new ResearchCondition("BasicHumanities", 1, 20));
    	return list;
    }

    @Override
    public String getTitle() {
        return ColonyManager.t("건설용 드론 기술");
    }
}
