package org.duckdns.hjow.colonization.elements.facilities;

import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.elements.research.humanities.BasicHumanities;

public class Academy extends School {
	private static final long serialVersionUID = -5410352140322320308L;

	@Override
    protected String getDefaultNamePrefix() {
        return "아카데미";
    }
	
	@Override
    protected int getDefaultCapacity() {
        return 100;
    }
	
	@Override
    public long usingFee() {
        return 10L;
    }
	
	/** 이 시설에서 도달할 수 있는 최대 Intelligence 값 */
	@Override
    protected int defaultMaxIntelligence() {
    	return 20;
    }
    
    /** 이 시설에서 도달할 수 있는 최대 Physical 값 */
	@Override
    protected int defaultMaxPhysical() {
    	return 10;
    }
    
    @Override
    public int getPowerConsume() {
        return 5;
    }
    
    @Override
    public int getSpaceSize() {
    	return 20;
    }
    
    @Override
    public int getWorkerNeeded() {
        return 5;
    }

    @Override
    public int getWorkerCapacity() {
        return 8;
    }
	
    public static String getFacilityName() {
        return ColonyManager.t("아카데미");
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return ColonyManager.t("기본적인 교육 시설입니다.");
    }
    
    public static Long getFacilityPrice() {
        return new Long(50000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(180);
    }
    
    public static int getUniqueFacilityGrade() {
    	return FACILITY_UNIQUE_GRADE_NONE;
    }
    
    public static Long getTechNeeded() {
        return new Long(0);
    }
    
    public static String getImageHex() {
        return null;
    }
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public static String isBuildAvail(Colony col, City city) { 
        boolean cond1 = false;
        List<Research> researches = col.getResearches();
        for(Research r : researches) {
            if(r instanceof BasicHumanities) {
                if(r.getLevel() >= 5) cond1 = true;
            }
        }
        
        if(! cond1) return ColonyManager.t("기초인문학 연구가 부족합니다.");
        return null;
    }
}
