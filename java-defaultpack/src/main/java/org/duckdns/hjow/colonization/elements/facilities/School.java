package org.duckdns.hjow.colonization.elements.facilities;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.constants.Constants;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.Space;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;
import org.duckdns.hjow.colonization.ui.ColonyPanel;
import org.duckdns.hjow.commons.json.JsonObject;

/** 교육 시설 */
public abstract class School extends AbstractFacility {
    private static final long serialVersionUID = 7034948757213309005L;

    @Override
    public int getMaxHp() {
        return 1000;
    }
    
    @Override
    protected int getDefaultCapacity() {
        return 100;
    }
    
    /** 행사 주기 */
    protected int getProfitCycle() {
        return 60;
    }
    
    @Override
    public long usingFee() {
        return 10L;
    }
    
    /** 이 시설에서 도달할 수 있는 최대 Intelligence 값 */
    protected int defaultMaxIntelligence() {
        return 20;
    }
    
    /** 이 시설에서 도달할 수 있는 최대 Physical 값 */
    protected int defaultMaxPhysical() {
        return 10;
    }
    
    /** 적정 최대 Intelligence 값. 이 시설에서, 증가율 적용 없이 (100% 적용된다는 의미) 도달할 수 있는 최대 Intelligence 값 */
    protected int defaultMaxIntelligenceEasily() {
        return defaultMaxIntelligence() / 2;
    }
    
    /** 적정 최대 Physical 값. 이 시설에서, 증가율 적용 없이 (100% 적용된다는 의미) 도달할 수 있는 최대 Physical 값 */
    protected int defaultMaxPhysicalEasily() {
        return defaultMaxPhysical() / 2;
    }
    
    /** 적정 최대 Intelligence 초과로 올릴 때 적용되는 증가율, 0~1 */
    protected double defaultIncreaseIntelligenceRate() {
        return 0.3;
    }
    
    /** 적정 최대 Physical 초과로 올릴 때 적용되는 증가율, 0~1 */
    protected double defaultIncreasePhysicalRate() {
        return 0.2;
    }
    
    @Override
    public void oneCycle(int cycle, ColonyElements stage, Space space, Colony colony, int efficiency100, ColonyPanel colPanel) {
    	City city = (City) stage;
        if(cycle % getProfitCycle() == 0) {
            int counts = getCapacity();
            double efficiency = (efficiency100 / 100.0);
            
            counts = (int) (counts * efficiency);
            
            // 청소년 먼저 처리
            for(Citizen c : city.getCitizens()) {
                if(counts <= 0) break;
                if(! checkProfitCondition(c, efficiency)) continue;
                
                if(c.getAgeYear().compareTo(Constants.BIGINTEGER_20) < 0) {
                    profitCitizen(c, efficiency);
                    counts--;
                }
            }
            
            // 성인 처리
            for(Citizen c : city.getCitizens()) {
                if(counts <= 0) break;
                if(! checkProfitCondition(c, efficiency)) continue;
                
                profitCitizen(c, efficiency);
                counts--;
            }
        }
    }
    
    /** 혜택을 받을 수 있는지 체크 (제한 교육수치만 체크, 인원수는 oneCycle 에서 체크해야 함) */
    protected boolean checkProfitCondition(Citizen c, double efficiency) {
        int intell = c.getEducatedIntelligence();
        int physic = c.getEducatedPhysical();
        
        int maxIntell = (int) (defaultMaxIntelligence() * efficiency);
        int maxPhysic = (int) (defaultMaxPhysical()     * efficiency);
        
        if(intell >= maxIntell) return false;
        if(physic >= maxPhysic) return false;
        return true;
    }
    
    /** 시민에게 교육 혜택 제공 */
    protected void profitCitizen(Citizen c, double efficiency) {
        int intell = c.getEducatedIntelligence();
        int physic = c.getEducatedPhysical();
        
        int propIntell = defaultMaxIntelligenceEasily();
        int propPhysic = defaultMaxPhysicalEasily();
        
        boolean accept = false;
        if(intell < propIntell) accept = true;
        else if(ColonyManager.random() <= defaultIncreaseIntelligenceRate()) accept = true;
        if(accept) {
            int increases = 1;
            if(ColonyManager.random() <= getBoostRate()) increases++;
            c.setEducatedIntelligence(c.getEducatedIntelligence() + increases);
        }
        
        accept = false;
        if(physic < propPhysic) accept = true;
        else if(ColonyManager.random() <= defaultIncreasePhysicalRate()) accept = true;
        if(accept) {
            int increases = 1;
            if(ColonyManager.random() <= getBoostRate()) increases++;
            c.setEducatedPhysical(c.getEducatedPhysical() + increases);
        }
    }
    
    @Override
    public int increasingCityMaxHP() {
        return 1;
    }

    @Override
    public String getStatusDescription(City city, Colony colony) {
        // TODO
        return "";
    }

    @Override
    public int getPowerConsume() {
        return 5;
    }
    
    @Override
    public int getSpaceSize() {
        return 20;
    }
    
    @Override
    public int getComportGrade() {
        return 1;
    }
    
    @Override
    public int getWorkerNeeded() {
        return 5;
    }

    @Override
    public int getWorkerCapacity() {
        return 8;
    }

    @Override
    public int getWorkerSuitability(Citizen citizen) {
        int point = 5;
        if(citizen.getCarisma()     >= 6) point += 1;
        if(citizen.getAgility()     >= 3) point += 1;
        if(citizen.getStrength()    >= 4) point += 1;
        if(citizen.getIntelligent() >= 7) point += 2;
        
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
    public JsonObject toJson(boolean details, Colony col, City city, boolean excludeSecrets) {
        JsonObject json = new JsonObject();
        json.putAll(super.toJson(details, col, city, excludeSecrets));
        json.put("type", getType());
        json.put("name", getName());
        json.put("key", String.valueOf(getKey()));
        json.put("hp", String.valueOf(getHp()));
        json.put("level", new Integer(getLevel()));
        
        return json;
    }
    
    public static String getFacilityName() {
        return ColonyManager.t("학교");
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return ColonyManager.t("기본적인 교육 시설입니다.");
    }
    
    public static Long getFacilityPrice() {
        return new Long(50000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(60);
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
