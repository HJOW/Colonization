package org.duckdns.hjow.colonization.elements.facilities;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

public class CapsuleBusStation extends TransportStation {
    private static final long serialVersionUID = 7222508474329385493L;
    
    @Override
    protected String getDefaultNamePrefix() {
        return ColonyManager.t("캡슐버스플랫폼");
    }
    
    @Override
    public String getStatusDescription(City city, Colony colony) {
        return "";
    }

    @Override
    public int getPowerConsume() {
        return 1;
    }

    @Override
    protected int getDefaultCapacity() {
        return 100;
    }

    /** 수익 발생 주기 */
    protected int getProfitCycle() {
        return 0;
    }
    
    @Override
    public int getSpaceSize() {
    	return 1;
    }
    
    @Override
    public long usingFee() {
        return 0L;
    }
    
    public static String getFacilityName() {
        return ColonyManager.t("캡슐 버스 플랫폼");
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return ColonyManager.t("기본적인 교통 수단입니다.\n2곳 이상을 건설해야 동작합니다.\n교통 한도를 증가시킵니다.\n교통 한도가 부족하면, 일부 시설에 직원이 통근할 수 없게 됩니다.");
    }
    
    public static Long getFacilityPrice() {
        return new Long(1000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(120);
    }
    
    public static int getUniqueFacilityGrade() {
    	return FACILITY_UNIQUE_GRADE_NONE;
    }
    
    public static Long getTechNeeded() {
        return new Long(0);
    }
    
    public static Object getImage() {
        return null;
    }
    
    public static List<ResearchCondition> getResearchCoditions(Colony col) {
    	List<ResearchCondition> list = new ArrayList<ResearchCondition>();
    	list.add(new ResearchCondition("BasicScience", 1));
    	list.add(new ResearchCondition("BasicBuildingTech", 1));
    	return list;
    }
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public static String isBuildAvail(Colony col, City city) {
        return null;
    }
    
    protected static boolean isScriptBasedFacility() { return false; }
}
