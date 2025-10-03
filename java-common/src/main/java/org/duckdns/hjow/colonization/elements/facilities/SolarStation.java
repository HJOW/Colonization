package org.duckdns.hjow.colonization.elements.facilities;

import java.util.List;

import org.duckdns.hjow.colonization.elements.City;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.BasicBuildingTech;
import org.duckdns.hjow.colonization.elements.research.LightTech;
import org.duckdns.hjow.colonization.elements.research.Research;

public class SolarStation extends PowerPlant {
    private static final long serialVersionUID = 4079646708867981024L;

    protected String getDefaultNamePrefix() {
        return "광학 발전소";
    }

    protected int getDefaultCapacity() {
        return 250;
    }
    
    @Override
    public String getStatusDescription(City city, Colony colony) {
        return ""; // TODO
    }
    @Override
    public int getPowerConsume() {
        return 0;
    }
    public int getPowerGenerate(Colony col, City city) {
        return getCapacity();
    }

    @Override
    public int getMaxHp() {
        return 500;
    }
    @Override
    public int getWorkerNeeded() {
        return 1;
    }
    @Override
    public int getWorkerCapacity() {
        return 2;
    }
    
    public static String getFacilityName() {
        return "광학 발전소";
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return "항성 빛과 열복사 에너지를 이용한 대형 전력 생산 시설입니다.\n내장 배터리를 통해 빛이 닿지 않는 시간대에서도 전력 공급이 가능합니다.";
    }
    
    public static Long getFacilityPrice() {
        return new Long(50000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(600);
    }
    
    public static Long getTechNeeded() {
        return new Long(20);
    }
    
    public static String getImageHex() {
        return null;
    }
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public static String isBuildAvail(Colony col, City city) { 
        boolean cond1 = false;
        boolean cond2 = false;
        List<Research> researches = col.getResearches();
        for(Research r : researches) {
            if(r instanceof LightTech) {
                if(r.getLevel() >= 1) cond1 = true;
            }
            if(r instanceof BasicBuildingTech) {
                if(r.getLevel() >= 1) cond2 = true;
            }
        }
        
        if(! cond1) return "광학 연구가 부족합니다.";
        if(! cond2) return "기초건축학 연구가 부족합니다.";
        return null;
    }
}
