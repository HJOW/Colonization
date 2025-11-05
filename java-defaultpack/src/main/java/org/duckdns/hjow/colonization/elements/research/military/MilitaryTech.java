package org.duckdns.hjow.colonization.elements.research.military;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

public class MilitaryTech extends Research {
    private static final long serialVersionUID = -6913431604370242959L;

    public MilitaryTech() { super(); }
    
    @Override
    public String getName() {
        return "MilitaryTech";
    }
    
    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 800L; }
    public double getMaxProgressIncreaseRate() { return 1.1;  }
    
    @Override
    public List<ResearchCondition> getResearchCoditions(Colony col, int level) {
        List<ResearchCondition> list = new ArrayList<ResearchCondition>();
        list.add(new ResearchCondition("BasicScience", 1, 4.0));
        list.add(new ResearchCondition("BasicEngineering", 1, 2.0));
        list.add(new ResearchCondition("BasicHumanities", 1, 1.0, 20));
        return list;
    }

    @Override
    public String getTitle() {
        return ColonyManager.t("군사학");
    }
}
