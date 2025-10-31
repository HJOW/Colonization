package org.duckdns.hjow.colonization.elements.research.energy;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

/** 전자기학 */
public class ElectroMagneticTech extends Research {
	private static final long serialVersionUID = 8942715919409557057L;

	public ElectroMagneticTech() { super(); }
    
    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 600L; }
    public double getMaxProgressIncreaseRate() { return 1.1;  }
    
    @Override
    public List<ResearchCondition> getResearchCoditions(Colony col, int level) {
    	List<ResearchCondition> list = new ArrayList<ResearchCondition>();
    	list.add(new ResearchCondition("BasicScience", 1, 1.3));
    	list.add(new ResearchCondition("Mathematics", 1, 1.4));
    	return list;
    }

    @Override
    public String getTitle() {
        return ColonyManager.t("전자기학");
    }
}
