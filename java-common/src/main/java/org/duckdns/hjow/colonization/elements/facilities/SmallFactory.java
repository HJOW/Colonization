package org.duckdns.hjow.colonization.elements.facilities;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.City;
import org.duckdns.hjow.colonization.elements.Colony;

public class SmallFactory extends Factory {
    private static final long serialVersionUID = -4382361172571288943L;
    
    @Override
    protected String getDefaultNamePrefix() {
        return ColonyManager.t("공장");
    }

    @Override
    public int getPowerConsume() {
        return 10;
    }
    
    @Override
    protected int getDefaultCapacity() {
        return 10;
    }
    
    @Override
    public int getWorkerNeeded() {
        return 5;
    }
    @Override
    public int getWorkerCapacity() {
        return 10;
    }
    
    @Override
    protected int getProfitCycle() {
        return 600;
    }
    
    public static String getFacilityName() {
        return ColonyManager.t("생산 시설");
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return ColonyManager.t("기본적인 생산 시설입니다. 이 곳에서 생산된 물품으로 정착지의 재정에 수익이 발생합니다.");
    }
    
    public static Long getFacilityPrice() {
        return new Long(20000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(1200);
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
    public static String isBuildAvail(Colony col, City city) { return null; }
}
