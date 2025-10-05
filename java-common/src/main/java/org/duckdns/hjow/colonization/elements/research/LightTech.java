package org.duckdns.hjow.colonization.elements.research;

import java.util.List;

import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.energy.EnergyTech;

public class LightTech extends Research {
	private static final long serialVersionUID = -6737998466808533544L;

	@Override
    public String getName() {
        return "LightTech";
    }
    
    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 600L; }
    public double getMaxProgressIncreaseRate() { return 1.7;  }

    @Override
    public boolean isResearchAvail(Colony col) {
        boolean cond1 = false;
        boolean cond2 = false;
        boolean cond3 = false;
        
        List<Research> researches = col.getResearches();
        for(Research one : researches) {
            
            // 기초과학 레벨이 이 연구 레벨의 5배가 되어야 연구가능 (최소 10)
            if(one instanceof BasicScience) {
                if(one.getLevel() >= (int)(chooseMaxInt(getLevel(), 1) * 5)) cond1 = true;
                if(one.getLevel() < 10) cond1 = false;
            }
            
            // 공학기초 레벨이 이 연구 레벨의 3배가 되어야 연구가능 (최소 6)
            if(one instanceof BasicEngineering) {
                if(one.getLevel() >= (int)(chooseMaxInt(getLevel(), 1) * 3)) cond2 = true;
                if(one.getLevel() < 6) cond2 = false;
            }
            
            // 에너지 레벨이 이 연구 레벨만큼 되어야 연구 가능
            if(one instanceof EnergyTech) {
                if(one.getLevel() >= (int) chooseMaxInt(getLevel(), 1)) cond3 = true;
            }
        }
        
        return cond1 && cond2 && cond3;
    }

    @Override
    public String getTitle() {
        return "광학";
    }
}
