package org.duckdns.hjow.colonization.elements.facilities;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.City;
import org.duckdns.hjow.colonization.elements.Colony;

public class SmallResearchCenter extends ResearchCenter {
	private static final long serialVersionUID = -6131743828237724751L;
    
    @Override
    protected String getDefaultNamePrefix() {
        return ColonyManager.t("연구소");
    }

    @Override
    public String getStatusDescription(City city, Colony colony) {
        return "";
    }

    @Override
    public int getPowerConsume() {
        return 15;
    }
    
    @Override
    public int getSpaceSize() {
    	return 7;
    }
    
    /** 기본 연구 진행 증가폭 */
    @Override
    protected int defaultIncreaseResearchProgress() {
    	return 10;
    }

    @Override
    public int getWorkerSuitability(Citizen citizen) {
        int point = 0;
        if(citizen.getCarisma()     >= 3) point += 2;
        if(citizen.getAgility()     >= 4) point += 2;
        if(citizen.getStrength()    >= 4) point += 2;
        if(citizen.getIntelligent() >= 7) point += 4;
        
        return point;
    }
    
    @Override
    public int getWorkerNeeded() {
        return 3;
    }
    @Override
    public int getWorkerCapacity() {
        return 5;
    }

    @Override
    protected int getDefaultCapacity() {
        return 0;
    }
    
    /** 테크 포인트 증가 사이클 */
    protected int getTechPointIncreaseCycle() {
        return 120;
    }
    
    public static String getFacilityName() {
        return ColonyManager.t("소형 연구 모듈");
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return ColonyManager.t("기술 개발 시설입니다.");
    }
    
    public static Long getFacilityPrice() {
        return new Long(30000L);
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