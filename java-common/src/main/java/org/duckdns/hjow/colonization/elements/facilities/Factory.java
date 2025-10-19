package org.duckdns.hjow.colonization.elements.facilities;

import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.City;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.products.Money;
import org.duckdns.hjow.colonization.elements.products.Product;
import org.duckdns.hjow.colonization.ui.ColonyPanel;

public abstract class Factory extends DefaultFacility implements Storage {
    private static final long serialVersionUID = 8465140770981665970L;
    protected String name = getDefaultNamePrefix() + "_" + ColonyManager.generateNaturalNumber();
    protected List<Product> stored = new Vector<Product>();
    protected transient String productType = null;
    
    public Factory() {
        
    }
    
    @Override
    protected String getDefaultNamePrefix() {
        return ColonyManager.t("공장");
    }

    @Override
    public String getStatusDescription(City city, Colony colony) {
        
        return "";
    }

    @Override
    public int getPowerConsume() {
        return 10;
    }
    
    @Override
    public int getWorkerNeeded() {
        return 5;
    }
    @Override
    public int getWorkerCapacity() {
        return 10;
    }

    @Override
    public int getWorkerSuitability(Citizen citizen) {
        int point = 0;
        if(citizen.getCarisma()     >= 1) point += 1;
        if(citizen.getAgility()     >= 5) point += 4;
        if(citizen.getStrength()    >= 6) point += 4;
        if(citizen.getIntelligent() >= 4) point += 2;
        
        return point;
    }

    @Override
    protected int getDefaultCapacity() {
        return 10;
    }
    
    /** 수익 발생 주기 */
    protected int getProfitCycle() {
        return 600;
    }
    
    /** 1번 생산에 생산되는 결과물 Product 의 양 (재료와 무관, 즉 재료 1번 사용량 소모 당 생산량을 의미) */
    protected int getProduceResultCount(Product p) {
    	return 1;
    }
    
    @Override
    public void oneCycle(int cycle, City city, Colony colony, int efficiency100, ColonyPanel colPanel) {
        super.oneCycle(cycle, city, colony, efficiency100, colPanel);
        
        // 공장 업무 처리
        if(cycle % getProfitCycle() == 0) {
            int increases = getCapacity();
            increases = (int) (increases * ( efficiency100 / 100.0 ));
            
            boolean createSuccess = false;
            if(getProductType() == null) createSuccess = false;
            else if(getProductType().equals("Money")) createSuccess = false;
            else {
                Product p = Product.createProductInstance(getProductType());
                if(p != null) {
                    // 재료 확인
                    List<Product> sources = p.getSourceProducts();
                    
                    // 저장소 및 생산 가능성 확인
                    if(! (isStoreAvail(p) && isProduced(p))) createSuccess = false;
                    else {
                        if(sources.isEmpty()) { // 필요 재료가 없으면 성공으로 처리
                            createSuccess = true;
                        } else {
                            // 사용 대상 재료 선정
                            List<Product> using = new ArrayList<Product>();
                            
                            int idx = 0;
                            while(idx < sources.size()) {
                                Product sourceOne = sources.get(idx);
                                
                                for(Product storedOne : getStored()) { // 저장된 재료들 루프
                                    if(storedOne.getType().equals(sourceOne.getType())) {
                                        using.add(storedOne); // 사용할 목록에 추가
                                        sourceOne = null;     // 충족 표시
                                        break;
                                    }
                                }
                                
                                // 아직 필요 재료 충족이 안된 경우 실패로 처리 (실패했으므로 사용 재료 비우기)
                                if(sourceOne != null) { createSuccess = false; using.clear(); break; }
                                idx++;
                            }
                            
                            if(! using.isEmpty()) { // 필요 재료가 있는데, 사용 재료도 있으면 --> 성공 처리
                                // 사용 재료 지금 차감
                                for(Product px : using) {
                                    stored.remove(px);
                                }
                                
                                createSuccess = true;
                            }
                        }
                        
                        if(createSuccess) {
                            // 생산품 추가
                        	for(int idx=0; idx<getProduceResultCount(p); idx++) {
                        		if(getStoredCount() < getMaxStoredCapacity()) store(Product.createProductInstance(p.getType()));
                        	}
                        }
                    }
                } else {
                    setProductType(null);
                    createSuccess = false;
                }
            }
            
            if(! createSuccess) {
                // 생산 실패, 혹은 생산품 지정이 안된 경우, 다른 돈벌이를 했다고 가정하고 자금 추가
                colony.modifyingMoney(increases, city, this, "Work", getName());
            }
        }
    }
    
    @Override
    public List<Product> getStored() {
        return stored;
    }

    public void setStored(List<Product> stored) {
        this.stored = stored;
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
        if(! isStoreAvail(p)) throw new RuntimeException("Cannot store here !");
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
        return 1000;
    }
    
    @Override
    public boolean isStoreAvail(Product p) {
    	if(isProduced(p)) return true;
        return false;
    }
    
    /** 이 Product 가 이 공장에서 생산 가능한지를 반환, 재료 보유 여부와 저장 가능 (isStoreAvail) 여부는 이 메소드로 검사하지 않으므로 따로 검사해야 함. */
    public boolean isProduced(Product p) {
    	return true;
    }
    
    /** 생산 중인 Product 의 Type 반환 */
    public String getProductType() {
        return productType;
    }
    
    /** 생산 중인 Product 의 이름(Title) 반환 */
    public String getProducingName() {
    	Product p = getProducingSample();
    	if(p == null) p = new Money();
    	return p.getTitle();
    }
    
    /** 생산 중인 Product 의 샘플 객체 만들어 반환 */
    public Product getProducingSample() {
    	if(getProductType() == null) return null;
    	return Product.createProductInstance(getProductType());
    }
    
    /** 생산 중인 Product 의 원재료 리스트 반환 */
    public List<Product> getProducingProductSources() {
    	if(getProductType() == null) return null;
    	Product p = Product.createProductInstance(getProductType());
    	return p.getSourceProducts();
    }
    
    /** 해당 타입의 Product 가 지금 이 시설에서 생산 중인 Product 의 원재료인지를 반환 */
    public boolean isProducingSource(String type) {
    	if(getProductType() == null) return false;
    	List<Product> sources = getProducingProductSources();
    	if(sources == null) return false;
    	for(Product s : sources) {
    		if(getProductType().equals(s.getType())) return true;
    	}
    	return false;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    @Override
    public void fromJson(JsonObject json) {
        super.fromJson(json);
        setName(json.get("name").toString());
        key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
        setLevel(Integer.parseInt(json.get("level").toString()));
        if(json.get("productType") != null) setProductType(json.get("productType").toString());
        
        JsonArray list = (JsonArray) json.get("stored");
        stored.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        JsonObject jsonObj = (JsonObject) o;
                        Product productOne = Product.createProductInstance(jsonObj.get("type").toString());
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
    public JsonObject toJson(boolean details, Colony col, City city) {
        JsonObject json = new JsonObject();
        json.putAll(super.toJson(details, col, city));
        json.put("type", getType());
        json.put("name", getName());
        json.put("key", String.valueOf(getKey()));
        json.put("hp", String.valueOf(getHp()));
        json.put("level", new Integer(getLevel()));
        if(getProductType() != null) json.put("productType", getProductType());
        
        JsonArray list = new JsonArray();
        for(Product p : getStored()) { list.add(p.toJson()); }
        json.put("stored", list);
        
        return json;
    }
    
    public static String getFacilityName() {
        return ColonyManager.t("생산 시설");
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return ColonyManager.t("기본적인 생산 시설입니다. 이 곳에서 생산된 물품으로 정착지의 재정에 수익이 발생합니다.");
    }
    
    public static Long getFacilityPrice() {
        return new Long(20000L);
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
