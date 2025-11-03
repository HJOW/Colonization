package org.duckdns.hjow.colonization.elements.facilities;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

public class SolarStation extends PowerPlant {
    private static final long serialVersionUID = 4079646708867981024L;

    @Override
    protected String getDefaultNamePrefix() {
        return ColonyManager.t("광학_발전_모듈");
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
        return ColonyManager.t("광학 발전 모듈");
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return ColonyManager.t("항성 빛과 열복사 에너지를 이용한 대형 전력 생산 시설입니다.\n내장 배터리를 통해 빛이 닿지 않는 시간대에서도 전력 공급이 가능합니다.");
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
        return new Long(35);
    }
    
    public static Object getImage() {
        return null;
    }
    
    public static List<ResearchCondition> getResearchCoditions(Colony col) {
    	List<ResearchCondition> list = new ArrayList<ResearchCondition>();
    	list.add(new ResearchCondition("EnergyTech", 2));
    	list.add(new ResearchCondition("LightTech", 1));
    	list.add(new ResearchCondition("BasicBuildingTech", 3));
    	return list;
    }
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public static String isBuildAvail(Colony col, City city) {
        return null;
    }
    
    protected static boolean isScriptBasedFacility() { return false; }
}
