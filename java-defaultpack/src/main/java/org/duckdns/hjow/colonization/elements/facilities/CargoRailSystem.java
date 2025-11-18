package org.duckdns.hjow.colonization.elements.facilities;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.products.Product;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;
import org.duckdns.hjow.colonization.ui.ColonyPanel;
import org.duckdns.hjow.commons.json.JsonObject;

public class CargoRailSystem extends DefaultFacility {
    private static final long serialVersionUID = 5918363930987437500L;

    @Override
    protected String getDefaultNamePrefix() {
        return ColonyManager.t("화물레일시스템");
    }

    @Override
    public int getPowerConsume() {
        return 10;
    }
    
    @Override
    protected int getDefaultCapacity() {
        return 1000;
    }
    
    @Override
    public long usingFee() {
        return 5L;
    }
    
    /** 액션 발생 주기 */
    protected int getProfitCycle() {
        return 60;
    }
    
    @Override
    public int getWorkerSuitability(Citizen citizen) {
        int point = 1;
        if(citizen.getCarisma()     >= 1) point += 1;
        if(citizen.getAgility()     >= 6) point += 3;
        if(citizen.getStrength()    >= 2) point += 1;
        if(citizen.getIntelligent() >= 6) point += 3;
        
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
    
    @Override
    public void oneCycle(int cycle, ColonyElements stage, Colony colony, int efficiency100, ColonyPanel colPanel) {
        super.oneCycle(cycle, stage, colony, efficiency100, colPanel);
        City city = (City) stage;
        
        if(cycle % getProfitCycle() == 0) {
            int lefts = getCapacity();
            List<Facility> facilities = city.getFacility();
            
            // 저장 기능이 존재하는 모든 시설 분류
            List<Storage>  storages = new ArrayList<Storage>();
            for(Facility f : facilities) { if(f instanceof Storage) { storages.add((Storage) f); } }
            facilities = null;
            
            // 생산 능력이 있는 시설 분류
            List<Factory> factories = new ArrayList<Factory>();
            for(Storage s : storages) {
                if(s instanceof Factory) { factories.add((Factory) s); }
            }
            
            List<Product> moving = new ArrayList<Product>();
            
            // 생산 시설 루프 - 생산 중인 Product 들은 우선적으로 다른 곳으로 이송
            for(Factory fac : factories) {
                moving.clear();
                
                String producingType = fac.getProductType();
                if(producingType != null) {
                    for(Product stored : fac.getStored()) {
                        if(stored.getType().equals(producingType)) {
                            moving.add(stored);
                        }
                    }
                }
                
                if(moving.isEmpty()) continue;
                
                int idx = 0;
                while(idx < moving.size()) {
                    if(lefts <= 0) break; // 이 운송 시설 서비스 제한 초과 시 중단
                    Product m = moving.get(idx);
                    
                    // 우선순위 1 : 재료로 이 Product 가 필요로 하는 생산 시설
                    for(Storage s : storages) {
                        if(s.getKey() == fac.getKey()) continue; // 동일시설 건너뛰기
                        if(lefts <= 0) break; // 이 운송 시설 서비스 제한 초과 시 중단
                        if(m == null) break;
                        
                        if(s instanceof Factory) {
                            Factory facOther = (Factory) s;
                            if(facOther.isStoreAvail(m) && facOther.isProducingSource(m.getType())) {
                                // 이송
                                m = fac.takeOut(m.getType());
                                if(m == null) break;
                                facOther.store(m);
                                moving.remove(m);
                                m = null;
                                break; // for 문 중단
                            }
                        }
                    }
                    if(m == null) continue;
                    
                    // 우선순위 2 : 재료로 이 Product 가 필요로 하는 서비스
                    for(Storage s : storages) {
                        if(s.getKey() == fac.getKey()) continue; // 동일시설 건너뛰기
                        if(lefts <= 0) break; // 이 운송 시설 서비스 제한 초과 시 중단
                        if(m == null) break;
                        
                        if(s instanceof ServiceFacility) {
                            ServiceFacility serv = (ServiceFacility) s;
                            
                            if(serv.isStoreAvail(m) && serv.getProductTypeNeeded().contains(m.getType())) {
                                // 이송
                                m = fac.takeOut(m.getType());
                                if(m == null) break;
                                serv.store(m);
                                moving.remove(m);
                                m = null;
                                break; // for 문 중단
                            }
                        }
                    }
                    if(m == null) continue;
                    
                    // TODO ? 기타 우선순위가 있을까?
                    
                    idx++;
                }
            }
        }
    }
    
    @Override
    public String getStatusDescription(City city, Colony colony) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public static String getFacilityName() {
        return ColonyManager.t("화물 레일 시스템");
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return ColonyManager.t("생산 시설에서 화물을 자동으로 각 필요 시설로 운송합니다.");
    }
    
    public static Long getFacilityPrice() {
        return new Long(10000L);
    }
    
    public static int getUniqueFacilityGrade() {
        return FACILITY_UNIQUE_GRADE_CITY;
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(240);
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
