package org.duckdns.hjow.colonization.elements.research.engineering;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

public class ComputerTech extends Research {
	private static final long serialVersionUID = -7399830477909963591L;

	public ComputerTech() { super(); }
    
    @Override
    public String getName() {
        return "ComputerTech";
    }
    
    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 1200L; }
    public double getMaxProgressIncreaseRate() { return 2.0;  }
    
    @Override
    public List<ResearchCondition> getResearchCoditions(Colony col, int level) {
    	List<ResearchCondition> list = new ArrayList<ResearchCondition>();
    	list.add(new ResearchCondition("BasicScience", 1, 5.0));
    	list.add(new ResearchCondition("BasicHumanities", 1, 5.0));
    	list.add(new ResearchCondition("BasicEngineering", 1, 1.0));
    	return list;
    }

    @Override
    public String getTitle() {
        return ColonyManager.t("컴퓨터 기술");
    }
}
