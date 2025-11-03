package org.duckdns.hjow.colonization.elements.facilities;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;

public class SmallAntenna extends NetworkFacility {
    private static final long serialVersionUID = -7296133463450746092L;

    @Override
    public int getPowerConsume() {
        return 5;
    }

    @Override
    protected String getDefaultNamePrefix() {
        return ColonyManager.t("소형_안테나_시설");
    }

    @Override
    protected int getDefaultCapacity() {
        return 1000;
    }
    
    @Override
    public String getStatusDescription(City city, Colony colony) {
        return ""; // TODO
    }
    
    @Override
    public int getMaxHp() {
        return 300;
    }
    
    @Override
    public int getWorkerNeeded() {
        return 1;
    }
    @Override
    public int getWorkerCapacity() {
        return 1;
    }
    
    @Override
    public int getSpaceSize() {
        return 1;
    }
    
    /** 업그레이드 비용 시작 금액 */
    @Override
    protected long startUpgradePrice() {
        return 5000L;
    }
    
    /** 업그레이드 비용의 레벨 당 증가율 */
    @Override
    protected double increateUpgradePriceRate() {
        return 0.2;
    }
    
    /** 업그레이드 비용 시작 금액 */
    @Override
    protected int startUpgradeCycle() {
        return 200;
    }
    
    /** 업그레이드 비용의 레벨 당 증가율 */
    @Override
    protected double increaseUpgradeCycleRate() {
        return 0.2;
    }
    
    /** 레벨 당 Capacity 증가율 */
    @Override
    protected double increateCapacityRate() {
        return 0.1;
    }
    
    public static String getFacilityName() {
        return ColonyManager.t("소형 안테나 시설");
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return ColonyManager.t("도시 내에 무선 네트워크망을 서비스하는 소형 시설입니다.");
    }
    
    public static Long getFacilityPrice() {
        return new Long(20000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(300);
    }
    
    public static Long getTechNeeded() {
        return new Long(0);
    }
    
    public static int getUniqueFacilityGrade() {
    	return FACILITY_UNIQUE_GRADE_NONE;
    }
    
    public static Object getImage() {
        return null;
    }
    
    public static List<ResearchCondition> getResearchCoditions(Colony col) {
    	List<ResearchCondition> list = new ArrayList<ResearchCondition>();
    	list.add(new ResearchCondition("EnergyTech", 1));
    	list.add(new ResearchCondition("LightTech", 1));
    	list.add(new ResearchCondition("BasicBuildingTech", 1));
    	return list;
    }
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public static String isBuildAvail(Colony col, City city) {
        return null;
    }
    
    public static boolean isScriptBasedFacility() { return false; }
}
