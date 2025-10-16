package org.duckdns.hjow.colonization.elements.facilities;

import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.City;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.BasicScience;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.elements.research.engineering.BasicEngineering;
import org.duckdns.hjow.colonization.elements.research.humanities.BasicHumanities;

/** 소형 오피스 센터 */
public class MiniCenter extends BusinessCenter {
    private static final long serialVersionUID = 1850152318642086894L;
    @Override
    protected String getDefaultNamePrefix() {
        return ColonyManager.t("소형_비지니스센터");
    }
    
    @Override
    public int getPowerConsume() {
        return 20;
    }

    /** 여기서의 Capacity 는 수익 발생량 */
    @Override
    protected int getDefaultCapacity() {
        return 20;
    }

    @Override
    public int increasingCityMaxHP() {
        return 5;
    }

    @Override
    public int getComportGrade() {
        return 0;
    }

    @Override
    public int getMaxHp() {
        return 600;
    }
    @Override
    public int getWorkerNeeded() {
        return 10;
    }
    @Override
    public int getWorkerCapacity() {
        return 20;
    }
    
    public static String getFacilityName() {
        return ColonyManager.t("소형 비지니스센터");
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return ColonyManager.t("소형 오피스 센터로, 네트워크를 통해 서비스를 제공하여 수익을 발생시킵니다. 네트워크 시설이 부족하면 효율이 상당히 저하됩니다.");
    }
    
    public static Long getFacilityPrice() {
        return new Long(50000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(600);
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
        boolean cond2 = false;
        boolean cond3 = false;
        List<Research> researches = col.getResearches();
        for(Research r : researches) {
            if(r instanceof BasicScience) {
                if(r.getLevel() >= 10) cond1 = true;
            }
            if(r instanceof BasicHumanities) {
                if(r.getLevel() >= 10) cond2 = true;
            }
            if(r instanceof BasicEngineering) {
                if(r.getLevel() >= 1) cond3 = true;
            }
        }
        
        if(! cond1) return ColonyManager.t("기초과학 연구가 부족합니다.");
        if(! cond2) return ColonyManager.t("기초인문학 연구가 부족합니다.");
        if(! cond3) return ColonyManager.t("공학기초 연구가 부족합니다.");
        return null;
    }
}
