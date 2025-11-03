package org.duckdns.hjow.colonization.elements.facilities;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.AttackableObject;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.enemies.Enemy;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;
import org.duckdns.hjow.colonization.ui.ColonyPanel;
import org.duckdns.hjow.commons.json.JsonObject;

public abstract class DefenceFacility extends DefaultFacility implements AttackableObject {
    private static final long serialVersionUID = -8553101924279880106L;
    
    @Override
    public int getAttackCount() {
        return 1;
    }
    
    @Override
    public int getDamage() {
        return 1;
    }
    
    @Override
    public int getAttackCycle() {
        return 120;
    }
    
    /** 대미지 처리 후 추가 작업 (상태를 부여한다거나 등등) 이 메소드에서 구현 */
    protected void processAfterAttack(int cycle, ColonyElements element, int finalDamage) { }
    
    @Override
    public void oneCycle(int cycle, City city, Colony colony, int efficiency100, ColonyPanel colPanel) {
        super.oneCycle(cycle, city, colony, efficiency100, colPanel);
        
        int castLeft    = getAttackCount();
        int damages     = getDamage();
        int naturalized = damages;
        
        if(cycle % getAttackCycle() == 0) {
            List<Enemy> enemies = city.getEnemies();
            for(Enemy e : enemies) {
                if(e.getHp() >= 1) {
                    naturalized = ColonyManager.naturalizeDamage(this, e, damages);
                    e.addHp(naturalized * (-1));
                    processAfterAttack(cycle, e, naturalized);
                    castLeft--;
                    if(castLeft <= 0) break;
                }
            }
            
            if(castLeft >= 1) {
                enemies = colony.getEnemies();
                for(Enemy e : enemies) {
                    if(e.getHp() >= 1) {
                        naturalized = ColonyManager.naturalizeDamage(this, e, damages);
                        e.addHp(naturalized * (-1));
                        processAfterAttack(cycle, e, naturalized);
                        castLeft--;
                        if(castLeft <= 0) break;
                    }
                }
            }
        }
    }
    
    @Override
    public int increasingCityMaxHP() {
        return 3;
    }
    
    @Override
    public String getStatusDescription(City city, Colony colony) {
        return ""; // TODO
    }
    
    @Override
    public int getPowerConsume() {
        return 20;
    }
    
    @Override
    public int getComportGrade() {
        return 0;
    }
    @Override
    public int getMaxHp() {
        return 500;
    }
    @Override
    public int getWorkerNeeded() {
        return 1;
    }
    @Override
    public int getWorkerCapacity() {
        return 2;
    }
    
    @Override
    public short getAttackType() {
        return 0;
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
    protected int getDefaultCapacity() {
        return 0;
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
    
    public static String getFacilityName() {
        return ColonyManager.t("방어시설");
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return ColonyManager.t("방어 시설");
    }
    
    public static Long getFacilityPrice() {
        return new Long(15000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(400);
    }
    
    public static int getUniqueFacilityGrade() {
    	return FACILITY_UNIQUE_GRADE_NONE;
    }
    
    public static Long getTechNeeded() {
        return new Long(10);
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
