package org.duckdns.hjow.colonization.elements.facilities;

import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.elements.research.energy.EnergyTech;
import org.duckdns.hjow.colonization.elements.research.energy.FissionReactor;
import org.duckdns.hjow.colonization.elements.research.energy.LightTech;
import org.duckdns.hjow.colonization.elements.research.engineering.BasicBuildingTech;
import org.duckdns.hjow.colonization.elements.research.engineering.ComputerTech;

public class FissionReactorStation extends PowerPlant {
    private static final long serialVersionUID = 4079646708867981024L;

    @Override
    protected String getDefaultNamePrefix() {
        return ColonyManager.t("핵분열_발전소");
    }

    @Override
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
    
    @Override
    public int getSpaceSize() {
    	return 20;
    }
    
    public static String getFacilityName() {
        return ColonyManager.t("핵분열 발전소");
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return ColonyManager.t("핵분열 반응로를 이용하여 전력을 생산하는 발전소입니다. 기술의 발전으로, 사고 가능성이 매우 낮습니다.");
    }
    
    public static Long getFacilityPrice() {
        return new Long(500000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(1200);
    }
    
    public static int getUniqueFacilityGrade() {
    	return FACILITY_UNIQUE_GRADE_NONE;
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
        boolean cond3 = false;
        boolean cond4 = false;
        boolean cond5 = false;
        List<Research> researches = col.getResearches();
        for(Research r : researches) {
            if(r instanceof EnergyTech) {
                if(r.getLevel() >= 20) cond1 = true;
            }
            if(r instanceof LightTech) {
                if(r.getLevel() >= 30) cond2 = true;
            }
            if(r instanceof BasicBuildingTech) {
                if(r.getLevel() >= 30) cond3 = true;
            }
            if(r instanceof ComputerTech) {
                if(r.getLevel() >= 5) cond4 = true;
            }
            if(r instanceof FissionReactor) {
                if(r.getLevel() >= 1) cond5 = true;
            }
        }
        
        if(! cond1) return ColonyManager.t("에너지 연구가 부족합니다.");
        if(! cond2) return ColonyManager.t("광학 연구가 부족합니다.");
        if(! cond3) return ColonyManager.t("기초건축학 연구가 부족합니다.");
        if(! cond4) return ColonyManager.t("컴퓨터 기술 연구가 부족합니다.");
        if(! cond5) return ColonyManager.t("핵분열 반응로 기술 연구가 부족합니다.");
        return null;
    }
}
