package org.duckdns.hjow.colonization.elements.research;

import java.util.List;

import org.duckdns.hjow.colonization.elements.Colony;

public class NewMetals extends Research {
	private static final long serialVersionUID = -8126939140258858406L;
	
	@Override
    public String getName() {
        return "NewMetals";
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
            
            // 화학 레벨이 이 연구 레벨의 2배가 되어야 연구가능
            if(one instanceof Chemical) {
                if(one.getLevel() >= (int)(chooseMaxInt(getLevel(), 1) * 2)) cond2 = true;
            }
            
            // 공학기초 레벨이 이 연구 레벨의 7배가 되어야 연구가능 (최소 10)
            if(one instanceof BasicEngineering) {
            	if(one.getLevel() >= (int)(chooseMaxInt(getLevel(), 1) * 7)) cond3 = true;
            	if(one.getLevel() < 10) cond3 = false;
            }
        }
        
        return cond1 && cond2 && cond3;
    }

    @Override
    public String getTitle() {
        return "신금속공학";
    }
}
