package org.duckdns.hjow.colonization.elements.facilities;

import org.duckdns.hjow.commons.exception.KnownRuntimeException;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.products.AbstractProduct;
import org.duckdns.hjow.colonization.elements.products.Product;
import org.duckdns.hjow.colonization.elements.products.food.NutritionBlock;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;
import org.duckdns.hjow.colonization.ui.ColonyPanel;

public class Restaurant extends AbstractFacility implements ServiceFacility, Storage {
    private static final long serialVersionUID = -7371044845340026748L;
    protected List<Product> stored = new Vector<Product>();
    protected int comportGrade = 0;

    @Override
    protected String getDefaultNamePrefix() {
        return "식당";
    }

    @Override
    public int getMaxHp() {
        return 1000;
    }
    
    @Override
    protected int getDefaultCapacity() {
        return 30;
    }
    
    @Override
    public double additionalComportGradeRate(City city, Colony colony) {
        return 0.0;
    }
    
    /** 행사 주기 */
    protected int getProfitCycle() {
        return 60;
    }
    
    @Override
    public long usingFee() {
        return 5L;
    }

    @Override
    public void oneCycle(int cycle, ColonyElements stage, Colony colony, int efficiency100, ColonyPanel colPanel) {
        super.oneCycle(cycle, stage, colony, efficiency100, colPanel);
        City city = (City) stage;
        
        if(cycle % getProfitCycle() == 0) {
            // 효율 계산
            double efficiencyRate = efficiency100 / 100.0;
            double additionalRate = additionalComportGradeRate(city, colony);
            if(additionalRate < 0.0) additionalRate = 0.0;
            if(additionalRate != 0.0) {
                efficiencyRate = efficiencyRate + ((1.0 - efficiencyRate) * additionalRate);
            }
            if(efficiencyRate > 1.0) efficiencyRate = 1.0;
            
            int solvingHunger = 50;
            solvingHunger = (int) Math.round(solvingHunger * efficiencyRate);
            
            int compGrade = getComportGrade();
            compGrade = (int) Math.round(compGrade * efficiencyRate);
            
            List<Product> using  = new ArrayList<Product>();
            List<String> needed = new ArrayList<String>();
            
            // 서비스
            int servicingCount = 0;
            for(Citizen c : city.getCitizens()) {
                if(c.getHunger() >= 80) continue;
                if(c.getMoney() < usingFee()) continue;
                
                // 재료 계산
                using.clear();
                needed.clear();
                
                for(String pt : getProductTypeNeeded()) { needed.add(new String(pt)); }
                for(Product p : getStored()) {
                    int idx = 0;
                    while(idx<needed.size()) {
                        String needOne = needed.get(idx);
                        if(p.getType().equals(needOne)) {
                            using.add(p);
                            needed.remove(idx);
                            continue;
                        }
                        
                        idx++;
                    }
                }
                
                // 재료가 부족하면 중단
                if(! needed.isEmpty()) break;
                
                // 사용료 및 세금 계산
                long fee = usingFee();
                long tax = getTax(city, colony); // 사용료에 붙는 세금, 이미 계산 시에 사용료를 반영함.
                
                // 서비스
                servicingCount++;
                c.setMoney(c.getMoney() - fee - tax);
                c.setHunger(c.getHunger() + solvingHunger);
                colony.modifyingMoney(tax, city, c, "Tax", getName());
                
                if(compGrade >= 2) {
                    c.setHappy(c.getHappy() + (compGrade / 2));
                }
                
                // 재료 소모
                if(servicingCount % 10 == 1) { // 재료 1개 당 10명에게 서비스 (단 무조건 최소 1개 소모)
                    for(Product p : using) {
                        stored.remove(p);
                    }
                }
                
                if(servicingCount >= getCapacity()) break;
            }
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
        return 1;
    }
    
    @Override
    public int getSpaceSize() {
        return 5;
    }

    @Override
    public int getComportGrade() {
        return comportGrade;
    }
    
    public void setComportGrade(int g) {
        comportGrade = g;
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
    public int getWorkerSuitability(Citizen citizen) {
        int point = 5;
        if(citizen.getCarisma()     >= 7) point += 2;
        if(citizen.getAgility()     >= 6) point += 1;
        if(citizen.getStrength()    >= 6) point += 1;
        if(citizen.getIntelligent() >= 4) point += 1;
        
        return point;
    }
    
    @Override
    public List<Product> getStored() {
        return stored;
    }
    
    public void setStored(List<Product> p) {
        this.stored = p;
    }

    @Override
    public Product takeOut(String type) {
        for(int idx=0; idx<stored.size(); idx++) {
            Product p = stored.get(idx);
            if(p.getType().equals(type)) {
                stored.remove(p);
                return p;
            }
        }
        return null;
    }
    
    @Override
    public void store(Product p) {
        if(! isStoreAvail(p)) throw new KnownRuntimeException("Cannot store here !");
        stored.add(p);
    }
    
    @Override
    public int getStoredCount() {
        return stored.size();
    }
    
    @Override
    public int getStoredCount(String productType) {
        int c = 0;
        for(Product p : stored) {
            if(p.getType().equals(productType)) c++;
        }
        return c;
    }
    
    @Override
    public int getMaxStoredCapacity() {
        return 200;
    }
    
    @Override
    public boolean isStoreAvail(Product p) {
        return (p instanceof NutritionBlock);
    }
    
    @Override
    public List<String> getProductTypeNeeded() {
        List<String> list = new ArrayList<String>();
        list.add("NutritionBlock");
        return list;
    }

    @Override
    public void fromJson(JsonObject json) {
        super.fromJson(json);
        setName(json.get("name").toString());
        key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
        setComportGrade(Integer.parseInt(json.get("comportGrade").toString()));
        setLevel(Integer.parseInt(json.get("level").toString()));
        
        JsonArray list = (JsonArray) json.get("stored");
        stored.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        JsonObject jsonObj = (JsonObject) o;
                        Product productOne = AbstractProduct.createProductInstance(jsonObj.get("type").toString());
                        if(productOne == null) throw new NullPointerException("Cannot found these product type " + jsonObj);
                        
                        productOne.fromJson(jsonObj);
                        stored.add(productOne);
                    } catch(Exception ex) {
                        GlobalLogs.processExceptionOccured(ex, false);
                    }
                }
            }
        }
    }

    @Override
    public JsonObject toJson(boolean details, Colony col, City city, boolean excludeSecrets) {
        JsonObject json = new JsonObject();
        json.putAll(super.toJson(details, col, city, excludeSecrets));
        json.put("type", getType());
        json.put("name", getName());
        json.put("key", String.valueOf(getKey()));
        json.put("hp", String.valueOf(getHp()));
        json.put("comportGrade", new Integer(getComportGrade()));
        json.put("level", new Integer(getLevel()));
        
        JsonArray list = new JsonArray();
        for(Product p : getStored()) { list.add(p.toJson()); }
        json.put("stored", list);
        
        return json;
    }
    
    public static String getFacilityName() {
        return ColonyManager.t("식당");
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return ColonyManager.t("식당으로, 시민들에게 유상으로 음식을 제공합니다.");
    }
    
    public static Long getFacilityPrice() {
        return new Long(10000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(180);
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
