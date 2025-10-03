package org.duckdns.hjow.colonization.elements.research;

import java.util.List;

import org.duckdns.hjow.colonization.elements.Colony;

public class BasicBuildingTech extends Research {
    private static final long serialVersionUID = 1818201774541715641L;

    public BasicBuildingTech() { super(); }

    @Override
    public String getName() {
        return "BasicBuildingTech";
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
        boolean cond2 = false;
        boolean cond3 = false;
        boolean cond4 = false;
        
        List<Research> researches = col.getResearches();
        for(Research one : researches) {
            
            // 기초과학 레벨이 이 건축학 레벨의 3배가 되어야 연구가능
            if(one instanceof BasicScience) {
                if(one.getLevel() >= (int)(chooseMaxInt(getLevel(), 1) * 3)) cond1 = true;
            }
            
            // 공학기초 레벨이 이 건축학 레벨의 2배가 되어야 연구가능
            if(one instanceof BasicEngineering) {
                if(one.getLevel() >= (int)(chooseMaxInt(getLevel(), 1) * 2)) cond2 = true;
            }
            
            if(getLevel() >= 10) { // 레벨 10부터
                // 신금속학 레벨이 있어야 연구 가능
                if(one instanceof NewMetals) {
                    if(one.getLevel() >= 1) cond3 = true;
                }
            } else if(getLevel() >= 25) { // 레벨 25부터
                // 신금속학 레벨이 이 건축학 레벨만큼 되어야 연구 가능
                if(one instanceof NewMetals) {
                    if(one.getLevel() >= chooseMaxInt(getLevel(), 1)) cond3 = true;
                }
            }
            
            if(getLevel() >= 15) { // 레벨 15부터
                // 기초인문학 레벨이 이 건축학 레벨보다 높아야 연구가능
                if(one instanceof BasicHumanities) {
                    if(one.getLevel() > chooseMaxInt(getLevel(), 1)) cond4 = true;
                }
            }
        }
        
        return cond1 && cond2 && cond3 && cond4;
    }

    @Override
    public String getTitle() {
        return "기초건축학";
    }
}
