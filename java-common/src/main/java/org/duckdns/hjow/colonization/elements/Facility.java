package org.duckdns.hjow.colonization.elements;

import java.util.List;

import org.duckdns.hjow.colonization.elements.states.State;

/** 시설 클래스임을 나타내는 인터페이스 */
public interface Facility extends ColonyElements {
    public void setName(String name);
    public String getType();
    
    /** 도시 최대 HP 증가 영향도를 반환 */
    public int increasingCityMaxHP();
    
    /** 설명 문구 반환 (화면에 출력됨) */
    public String getStatusDescription(City city, Colony colony);
    
    /** 전력 사용량 반환 */
    public int getPowerConsume();
    
    /** 편안함 등급 반환 (거주 모듈, 식당 등) */
    public int getComportGrade();
    
    /** 근무 중인 시민들 수 반환 */
    public int getWorkingCitizensCount(City city, Colony colony);
    
    /** 근무 중인 시민들 목록 반환 */
    public List<Citizen> getWorkingCitizens(City city, Colony colony);
    
    /** 상태 객체들 반환 */
    public List<State> getStates();
    
    /** 최소 직장 자리 수 (이만큼이 채워지지 않으면 효율이 떨어짐) */
    public int getWorkerNeeded();
    
    /** 최대 직장 자리 수 */
    public int getWorkerCapacity();
    
    /** 해당 시민이 이 직장에 적합한 정도를 반환 */
    public int getWorkerSuitability(Citizen citizen);
    
    /** 수용량 반환 (거주 모듈의 경우 거주 시민 수용량, 발전 모듈의 경우 전력 생산량) */
    public int getCapacity();
    
    /** 임금 반환 */
    public long getSalary(City city, Colony colony);
    
    /** 유지비 반환 (양수로 반환) */
    public long getMaintainFee(City city, Colony colony);
    
    /** 이 시설의 레벨 반환 */
    public int getLevel();
    
    /** 이 시설의 레벨 변경 */
    public void setLevel(int lv);
    
    /** 이 시설의 최대 레벨 반환 */
    public int getMaxLevel();
    
    /** 업그레이드 가능 여부 반환 */
    public boolean isUpgradeAvail(Colony col, City city);
    
    /** 이 시설의 업그레이드 비용 반환 */
    public long getUpgradePrice(Colony col, City city);
    
    /** 이 시설의 업그레이드 소요 시간 반환 */
    public int getUpgradeCycle(Colony col, City city);
    
    /*
     * 이 Facility 구현 클래스는 다음 static 메소드들을 반드시 구현해야 함.
     * 
     *    public static String getFacilityName()
     *    public static String getFacilityTitle()
     *    public static String getFacilityDescription()
     *    public static Long getFacilityPrice()
     *    public static Integer getFacilityBuildingCycle()
     *    public static Long getTechNeeded()
     *    public static String getImageHex()
     *    public static String isBuildAvail(Colony col, City city)
     */
}
