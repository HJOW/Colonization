package org.duckdns.hjow.colonization.elements.facilities;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

public class SmallApartment extends Residence {
	private static final long serialVersionUID = 8130774192386570274L;

	@Override
    protected String getDefaultNamePrefix() {
        return ColonyManager.t("소형APT");
    }

    @Override
    public int increasingCityMaxHP() {
        return 5;
    }

    @Override
    public String getStatusDescription(City city, Colony colony) {
        return "";
    }
    
    @Override
    public int getMaxHp() {
        return 3000;
    }
    
    @Override
    public int getPowerConsume() {
        return 7;
    }
    
    @Override
    public int getSpaceSize() {
    	return 10;
    }

    @Override
    protected int getDefaultCapacity() {
        return 120;
    }

    @Override
    public double additionalComportGradeRate(City city, Colony colony) {
        return 0.0;
    }

    public static String getFacilityName() {
        return ColonyManager.t("소형 APT");
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return ColonyManager.t("고층 주거 모듈로 수많은 시민이 거주할 수 있는 시설입니다.");
    }
    
    public static Long getFacilityPrice() {
        return new Long(400000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(680);
    }
    
    public static int getUniqueFacilityGrade() {
    	return FACILITY_UNIQUE_GRADE_NONE;
    }
    
    public static Long getTechNeeded() {
        return new Long(30);
    }
    
    public static Object getImage() {
        return null;
    }
    
    public static List<ResearchCondition> getResearchCoditions(Colony col) {
    	List<ResearchCondition> list = new ArrayList<ResearchCondition>();
    	list.add(new ResearchCondition("BasicBuildingTech", 15));
    	list.add(new ResearchCondition("BasicHumanities", 30));
    	list.add(new ResearchCondition("NewMetals", 15));
    	list.add(new ResearchCondition("ConstructionDrones", 1));
    	list.add(new ResearchCondition("Printing3DStructure", 1));
    	return list;
    }
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public static String isBuildAvail(Colony col, City city) {
        return null;
    }
}
