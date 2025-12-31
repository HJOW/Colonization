package org.duckdns.hjow.colonization.elements.research.physics;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.AbstractResearch;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

public class Physics extends AbstractResearch {
    private static final long serialVersionUID = 8277640950196928464L;

    public Physics() { super(); }
    
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
    
    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 60L; }
    public double getMaxProgressIncreaseRate() { return 1.1;  }
    
    @Override
    public List<ResearchCondition> getResearchCoditions(Colony col, int level) {
        List<ResearchCondition> list = new ArrayList<ResearchCondition>();
        list.add(new ResearchCondition("BasicScience", 1, 1.2));
        list.add(new ResearchCondition("BasicHumanities", 10, 1.0));
        list.add(new ResearchCondition("ElectroMagneticTech", 1, 1.0, 10));
        return list;
    }

    @Override
    public String getTitle() {
        return ColonyManager.t("물리학");
    }
}
