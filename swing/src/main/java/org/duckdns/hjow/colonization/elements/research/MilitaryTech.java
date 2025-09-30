package org.duckdns.hjow.colonization.elements.research;

import java.util.List;

import org.duckdns.hjow.colonization.elements.Colony;

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
    public double getMaxProgressIncreaseRate() { return 1.5;  }

    @Override
    public boolean isResearchAvail(Colony col) {
        boolean cond1 = false;
        boolean cond2 = false;
        boolean cond3 = false;
        
        List<Research> researches = col.getResearches();
        for(Research one : researches) {
            
            // 기초과학 레벨이 이 군사학 레벨의 4배가 되어야 연구가능
            if(one instanceof BasicScience) {
                if(one.getLevel() >= chooseMaxInt(getLevel(), 1) * 4) cond1 = true;
            }
            
             // 공학기초 레벨이 이 군사학 레벨의 2배가 되어야 연구가능
            if(one instanceof BasicEngineering) {
                if(one.getLevel() >= chooseMaxInt(getLevel(), 1) * 2) cond2 = true;
            }
            
            if(getLevel() >= 20) { // 레벨 20부터
                // 기초인문학 레벨이 이 군사학 레벨보다 높아야 연구가능
                if(one instanceof BasicHumanities) {
                    if(one.getLevel() > chooseMaxInt(getLevel(), 1)) cond3 = true;
                }
            }
        }
        
        return cond1 && cond2 && cond3;
    }

    @Override
    public String getTitle() {
        return "군사학";
    }
}
