package org.duckdns.hjow.colonization.elements.facilities;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;
import org.duckdns.hjow.colonization.ui.ColonyPanel;
import org.duckdns.hjow.commons.json.JsonObject;

/** 일종의 지식산업 센터, 오피스 빌딩 개념으로, Product 생산 없이 예산 수익 발생 */
public abstract class BusinessCenter extends DefaultFacility {
    private static final long serialVersionUID = 8250769518410521415L;
    @Override
    protected String getDefaultNamePrefix() {
        return ColonyManager.t("비지니스센터");
    }
    
    @Override
    public int getPowerConsume() {
        return 20;
    }

    /** 여기서의 Capacity 는 수익 발생량 */
    @Override
    protected int getDefaultCapacity() {
        return 20;
    }

    @Override
    public int increasingCityMaxHP() {
        return 5;
    }

    @Override
    public int getComportGrade() {
        return 0;
    }

    @Override
    public int getMaxHp() {
        return 600;
    }
    @Override
    public int getWorkerNeeded() {
        return 10;
    }
    @Override
    public int getWorkerCapacity() {
        return 20;
    }

    @Override
    public int getWorkerSuitability(Citizen citizen) {
        int point = 2;
        if(citizen.getCarisma()     >= 6) point += 2;
        if(citizen.getAgility()     >= 5) point += 1;
        if(citizen.getStrength()    >= 3) point += 1;
        if(citizen.getIntelligent() >= 8) point += 4;
        
        return point;
    }

    @Override
    public void fromJson(JsonObject json) {
        super.fromJson(json);
        setName(json.get("name").toString());
        key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
        setLevel(Integer.parseInt(json.get("level").toString()));
    }

    @Override
    public JsonObject toJson(boolean details, Colony col, City city) {
        JsonObject json = new JsonObject();
        json.putAll(super.toJson(details, col, city));
        json.put("type", getType());
        json.put("name", getName());
        json.put("key", String.valueOf(getKey()));
        json.put("hp", String.valueOf(getHp()));
        json.put("level", new Integer(getLevel()));
        
        return json;
    }
    
    /** 수익 발생 주기 */
    protected int getProfitCycle() {
        return 600;
    }

    @Override
    public void oneCycle(int cycle, ColonyElements stage, Colony colony, int efficiency100, ColonyPanel colPanel) {
        super.oneCycle(cycle, stage, colony, efficiency100, colPanel);
        City city = (City) stage;
        
        if(cycle % getProfitCycle() == 0) {
            int increases = getCapacity();
            increases = (int) (increases * ( efficiency100 / 100.0 ));
            colony.modifyingMoney(increases, city, this, "Work", getName());
        }
    }
    
    public static String getFacilityName() {
        return ColonyManager.t("비지니스센터");
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return ColonyManager.t("오피스 센터로, 네트워크를 통해 서비스를 제공하여 수익을 발생시킵니다. 네트워크 시설이 부족하면 효율이 상당히 저하됩니다.");
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
        return new Long(0);
    }
    
    public static Object getImage() {
        return null;
    }
    
    public static List<ResearchCondition> getResearchCoditions(Colony col) {
        List<ResearchCondition> list = new ArrayList<ResearchCondition>();
        return list;
    }
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public static String isBuildAvail(Colony col, City city) { return null; }
    
    public static boolean isScriptBasedFacility() { return false; }
}
