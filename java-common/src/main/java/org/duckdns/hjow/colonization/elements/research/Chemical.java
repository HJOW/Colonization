package org.duckdns.hjow.colonization.elements.research;

import java.util.List;

import org.duckdns.hjow.colonization.elements.Colony;

public class Chemical extends Research {
	private static final long serialVersionUID = -8126939140258858406L;
	
	@Override
    public String getName() {
        return "Chemical";
    }
    
    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 600L; }
    public double getMaxProgressIncreaseRate() { return 1.5;  }

    @Override
    public boolean isResearchAvail(Colony col) {
        boolean cond1 = false;
        
        List<Research> researches = col.getResearches();
        for(Research one : researches) {
            
            // 기초과학 레벨이 이 연구 레벨의 3배가 되어야 연구가능 (최소 6)
            if(one instanceof BasicScience) {
                if(one.getLevel() >= (int)(chooseMaxInt(getLevel(), 1) * 5)) cond1 = true;
                if(one.getLevel() < 6) cond1 = false;
            }
        }
        
        return cond1;
    }

    @Override
    public String getTitle() {
        return "화학";
    }
}
