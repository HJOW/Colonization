package org.duckdns.hjow.colonization.elements.facilities;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

public class BigFactory extends Factory {
	private static final long serialVersionUID = -3039009660687368839L;

	@Override
    protected String getDefaultNamePrefix() {
        return ColonyManager.t("대규모_생산_시설");
    }

    @Override
    public int getPowerConsume() {
        return 50;
    }
    
    @Override
    protected int getDefaultCapacity() {
        return 100;
    }
    
    @Override
    public int getWorkerNeeded() {
        return 50;
    }
    @Override
    public int getWorkerCapacity() {
        return 100;
    }
    
    @Override
    public int getSpaceSize() {
    	return 30;
    }
    
    @Override
    public int getMaxStoredCapacity() {
        return 10000;
    }
    
    @Override
    protected int getProfitCycle() {
        return 600;
    }
    
    public static String getFacilityName() {
        return ColonyManager.t("대규모 생산 시설");
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return ColonyManager.t("대규모 생산 시설은 대량 생산을 위한 시설로, 더 많은 자원과 공간을 필요로 합니다. 공간 효율이 높습니다.");
    }
    
    public static Long getFacilityPrice() {
        return new Long(500000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(1500);
    }
    
    public static int getUniqueFacilityGrade() {
    	return FACILITY_UNIQUE_GRADE_NONE;
    }
    
    public static Long getTechNeeded() {
        return new Long(40);
    }
    
    public static Object getImage() {
        return null;
    }
    
    public static List<ResearchCondition> getResearchCoditions(Colony col) {
    	List<ResearchCondition> list = new ArrayList<ResearchCondition>();
    	list.add(new ResearchCondition("BasicBuildingTech", 20));
    	list.add(new ResearchCondition("ComputerTech", 20));
    	list.add(new ResearchCondition("NewMetals", 15));
    	list.add(new ResearchCondition("ConstructionDrones", 2));
    	list.add(new ResearchCondition("Printing3DStructure", 2));
    	return list;
    }
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public static String isBuildAvail(Colony col, City city) {
        return null;
    }
    
    protected static boolean isScriptBasedFacility() { return false; }
}
