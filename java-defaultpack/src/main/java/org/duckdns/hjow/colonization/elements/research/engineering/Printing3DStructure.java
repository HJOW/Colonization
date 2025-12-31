package org.duckdns.hjow.colonization.elements.research.engineering;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.AbstractResearch;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

public class Printing3DStructure extends AbstractResearch {
    private static final long serialVersionUID = -238086059316650293L;

    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 180L; }
    public double getMaxProgressIncreaseRate() { return 1.5;  }
    
    @Override
    public List<ResearchCondition> getResearchCoditions(Colony col, int level) {
        List<ResearchCondition> list = new ArrayList<ResearchCondition>();
        list.add(new ResearchCondition("BasicBuildingTech", 20, 2.0));
        list.add(new ResearchCondition("LightTech", 1, 5.0));
        list.add(new ResearchCondition("ComputerTech", 1, 5.0));
        list.add(new ResearchCondition("Plasteel", 1, 1.0));
        list.add(new ResearchCondition("Physics", 1, 10.0));
        return list;
    }

    @Override
    public String getTitle() {
        return ColonyManager.t("3D 프린팅 건축");
    }
}
