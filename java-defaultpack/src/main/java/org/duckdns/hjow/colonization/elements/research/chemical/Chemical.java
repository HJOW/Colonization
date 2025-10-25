package org.duckdns.hjow.colonization.elements.research.chemical;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

public class Chemical extends Research {
    private static final long serialVersionUID = -5288682651598045570L;

    @Override
    public String getName() {
        return "Chemical";
    }
    
    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 600L; }
    public double getMaxProgressIncreaseRate() { return 1.1;  }
    
    @Override
    public List<ResearchCondition> getResearchCoditions(Colony col, int level) {
    	List<ResearchCondition> list = new ArrayList<ResearchCondition>();
    	list.add(new ResearchCondition("BasicScience", 1, 5.0));
    	list.add(new ResearchCondition("BasicScience", 6, 1.0));
    	return list;
    }
    
    @Override
    public String getTitle() {
        return ColonyManager.t("화학");
    }
}
