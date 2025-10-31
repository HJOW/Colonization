package org.duckdns.hjow.colonization.elements.research;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;

public class BasicScience extends Research {
    private static final long serialVersionUID = -6067100861366848018L;
    public BasicScience() { super(); }

    @Override
    public String getName() {
        return "BasicScience";
    }
    
    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 600L; }
    public double getMaxProgressIncreaseRate() { return 1.15;  }
    
    @Override
    public List<ResearchCondition> getResearchCoditions(Colony col, int level) {
    	List<ResearchCondition> list = new ArrayList<ResearchCondition>();
    	list.add(new ResearchCondition("Mathematics", 1, 1.0));
    	return list;
    }

    @Override
    public String getTitle() {
        return ColonyManager.t("기초 과학");
    }

}
