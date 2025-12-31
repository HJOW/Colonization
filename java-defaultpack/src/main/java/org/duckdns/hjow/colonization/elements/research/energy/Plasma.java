package org.duckdns.hjow.colonization.elements.research.energy;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.AbstractResearch;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

/** 플라즈마 */
public class Plasma extends AbstractResearch {
    private static final long serialVersionUID = -8026610470560139921L;

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
    
    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 150L; }
    public double getMaxProgressIncreaseRate() { return 1.5;  }
    
    @Override
    public List<ResearchCondition> getResearchCoditions(Colony col, int level) {
        List<ResearchCondition> list = new ArrayList<ResearchCondition>();
        list.add(new ResearchCondition("Chemical", 50, 1.5));
        list.add(new ResearchCondition("EnergyTech", 30, 10.0));
        list.add(new ResearchCondition("LightTech", 20, 1.5));
        list.add(new ResearchCondition("ElectroMagneticTech", 30, 1.5));
        return list;
    }

    @Override
    public String getTitle() {
        return ColonyManager.t("플라즈마");
    }
}
