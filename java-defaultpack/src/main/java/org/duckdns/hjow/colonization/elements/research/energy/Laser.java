package org.duckdns.hjow.colonization.elements.research.energy;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

/* 레이저 기술 **/
public class Laser extends Research {
    private static final long serialVersionUID = 1636336604448677624L;

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
    
    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 1200L; }
    public double getMaxProgressIncreaseRate() { return 1.5;  }
    
    @Override
    public List<ResearchCondition> getResearchCoditions(Colony col, int level) {
        List<ResearchCondition> list = new ArrayList<ResearchCondition>();
        list.add(new ResearchCondition("Mathematics", 20, 20.0));
        list.add(new ResearchCondition("EnergyTech", 5, 1.5));
        list.add(new ResearchCondition("LightTech", 10, 1.5));
        list.add(new ResearchCondition("ElectroMagneticTech", 15, 1.5));
        return list;
    }

    @Override
    public String getTitle() {
        return ColonyManager.t("레이저");
    }
}
