package org.duckdns.hjow.colonization.elements.facilities;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

public class TownHouse extends Residence {
    private static final long serialVersionUID = 6619376624827956402L;
    
    public TownHouse() {}
    
    @Override
    protected String getDefaultNamePrefix() {
        return ColonyManager.t("타운하우스");
    }

    @Override
    public int increasingCityMaxHP() {
        return 2;
    }

    @Override
    public String getStatusDescription(City city, Colony colony) {
        return "";
    }
    
    @Override
    public int getMaxHp() {
        return 2000;
    }
    
    @Override
    public int getPowerConsume() {
        return 2;
    }

    @Override
    protected int getDefaultCapacity() {
        return 12;
    }
    
    @Override
    public int getSpaceSize() {
        return 5;
    }

    @Override
    public double additionalComportGradeRate(City city, Colony colony) {
        return 0.0;
    }

    public static String getFacilityName() {
        return ColonyManager.t("타운하우스");
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return ColonyManager.t("규모가 큰 주거 모듈로, 더 많은 시민이 거주할 수 있습니다.");
    }
    
    public static Long getFacilityPrice() {
        return new Long(20000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(360);
    }
    
    public static int getUniqueFacilityGrade() {
        return FACILITY_UNIQUE_GRADE_NONE;
    }
    
    public static Long getTechNeeded() {
        return new Long(20);
    }
    
    public static Object getImage() {
        return null;
    }
    
    public static List<ResearchCondition> getResearchCoditions(Colony col) {
        List<ResearchCondition> list = new ArrayList<ResearchCondition>();
        list.add(new ResearchCondition("BasicScience", 5));
        list.add(new ResearchCondition("BasicBuildingTech", 2));
        return list;
    }
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public static String isBuildAvail(Colony col, City city) {
        return null;
    }
    
    public static boolean isScriptBasedFacility() { return false; }
}
