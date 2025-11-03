package org.duckdns.hjow.colonization.elements.facilities;

import org.duckdns.hjow.commons.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;
import org.duckdns.hjow.colonization.ui.ColonyPanel;

public abstract class PowerPlant extends DefaultFacility {
    private static final long serialVersionUID = -7738915080952447743L;

    @Override
    protected String getDefaultNamePrefix() {
        return ColonyManager.t("발전소");
    }

    @Override
    protected int getDefaultCapacity() {
        return 100;
    }

    @Override
    public int increasingCityMaxHP() {
        return 1;
    }

    @Override
    public int getComportGrade() {
        return 0;
    }

    @Override
    public int getMaxHp() {
        return 1000;
    }
    @Override
    public int getWorkerNeeded() {
        return 1;
    }
    @Override
    public int getWorkerCapacity() {
        return 2;
    }
    
    public int getPowerGenerate(Colony col, City city) {
        return getCapacity();
    }

    @Override
    public int getWorkerSuitability(Citizen citizen) {
        int point = 3;
        if(citizen.getCarisma()     >= 3) point += 1;
        if(citizen.getAgility()     >= 6) point += 2;
        if(citizen.getStrength()    >= 6) point += 2;
        if(citizen.getIntelligent() >= 6) point += 2;
        
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

    @Override
    public void oneCycle(int cycle, City city, Colony colony, int efficiency100, ColonyPanel colPanel) {
        super.oneCycle(cycle, city, colony, efficiency100, colPanel);
        
        // Do nothing on PowerStation (implemented on City class)
    }
    
    public static String getFacilityName() {
        return "";
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return "";
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
    	return list;
    }
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public static String isBuildAvail(Colony col, City city) {
        return null;
    }
    
    protected static boolean isScriptBasedFacility() { return false; }
}
