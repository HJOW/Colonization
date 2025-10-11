package org.duckdns.hjow.colonization.elements.facilities;

import org.duckdns.hjow.commons.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.City;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.products.Product;
import org.duckdns.hjow.colonization.ui.ColonyPanel;

public class Arcade extends DefaultFacility implements ServiceFacility {
    private static final long serialVersionUID = 6472512678804457223L;
    protected int comportGrade = 0;

    @Override
    public String getStatusDescription(City city, Colony colony) {
        // TODO Auto-generated method stub
        return "";
    }
    
    @Override
    protected String getDefaultNamePrefix() {
        return ColonyManager.t("아케이드");
    }

    @Override
    public int getPowerConsume() {
        return 10;
    }

    @Override
    public int getWorkerSuitability(Citizen citizen) {
        int point = 1;
        if(citizen.getCarisma()     >= 3) point += 1;
        if(citizen.getAgility()     >= 6) point += 3;
        if(citizen.getStrength()    >= 6) point += 1;
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
        setComportGrade(Integer.parseInt(json.get("comportGrade").toString()));
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
        json.put("comportGrade", new Integer(getComportGrade()));
        
        return json;
    }

    @Override
    protected int getDefaultCapacity() {
        return 100;
    }

    @Override
    public int getComportGrade() {
        return comportGrade;
    }
    
    public void setComportGrade(int g) {
        comportGrade = g;
    }
    
    @Override
    public double additionalComportGradeRate(City city, Colony colony) {
        return 0.0;
    }
    
    @Override
    public List<String> getProductTypeNeeded() {
    	return new ArrayList<String>();
    }
    
    /** 수익 발생 주기 */
    protected int getProfitCycle() {
        return 600;
    }
    
    @Override
    public long usingFee() {
        return 2L;
    }
    
    @Override
    public void oneCycle(int cycle, City city, Colony colony, int efficiency100, ColonyPanel colPanel) {
        super.oneCycle(cycle, city, colony, efficiency100, colPanel);
        
        if(cycle % getProfitCycle() == 0) {
            double efficiencyRate = efficiency100 / 100.0;
            double additionalRate = additionalComportGradeRate(city, colony);
            if(additionalRate < 0.0) additionalRate = 0.0;
            if(additionalRate != 0.0) {
                efficiencyRate = efficiencyRate + ((1.0 - efficiencyRate) * additionalRate);
            }
            if(efficiencyRate > 1.0) efficiencyRate = 1.0;
            
            int compGrade = getComportGrade();
            compGrade = (int) Math.round(compGrade * efficiencyRate);
            
            int servicingCount = 0;
            for(Citizen c : city.getCitizens()) {
                if(c.getHappy() >= 100) continue;
                if(c.getMoney() < usingFee()) continue;
                
                long fee = usingFee();
                long tax = getTax(city, colony); // 사용료에 붙는 세금, 이미 계산 시에 사용료를 반영함.
                
                servicingCount++;
                c.setHappy(c.getHappy() + 5 + (compGrade / 2));
                c.setMoney(c.getMoney() - fee - tax);
                colony.modifyingMoney(tax, city, c, "Tax", getName());
                
                if(servicingCount >= getCapacity()) break;
            }
        }
    }
    
    @Override
	public List<Product> getStored() {
		return new ArrayList<Product>();
	}

	@Override
	public Product takeOut(String type) {
		return null;
	}

	@Override
	public void store(Product p) { throw new RuntimeException("Cannot store here !"); }

	@Override
	public int getStoredCount(String productType) {
		return 0;
	}

	@Override
	public int getStoredCount() {
		return 0;
	}

	@Override
	public int getMaxStoredCapacity() {
		return 0;
	}

	@Override
	public boolean isStoreAvail(Product p) {
		return false;
	}

    public static String getFacilityName() {
        return ColonyManager.t("아케이드");
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return ColonyManager.t("시민들의 행복을 위한 기본적인 오락 시설입니다.");
    }
    
    public static Long getFacilityPrice() {
        return new Long(10000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(240);
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
