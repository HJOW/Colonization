package org.duckdns.hjow.colonization.elements.products;

import java.math.BigInteger;

import org.duckdns.hjow.colonization.ColonyClassLoader;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.City;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.ui.ColonyPanel;
import org.duckdns.hjow.commons.json.JsonObject;

public abstract class Product implements ColonyElements {
	private static final long serialVersionUID = -6737394925448600561L;
	protected volatile long key = ColonyManager.generateKey();
    protected String name = getDefaultNamePrefix();
    
    protected abstract String getDefaultNamePrefix();
	
	@Override
	public void dispose() {
		
	}

	@Override
	public long getKey() {
		return key;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public String getType() {
		return this.getClass().getSimpleName();
	}

	@Override
	public int getHp() {
		return 1;
	}

	@Override
	public int getMaxHp() {
		return 1;
	}
	
	public long getPrice() {
		return 1;
	}

	@Override
	public void setHp(int hp) {}

	@Override
	public void addHp(int amount) {}

	@Override
	public short getDefenceType() {
		return ColonyManager.DEFENCETYPE_NORMAL;
	}

	@Override
	public int getDefencePoint() {
		return 0;
	}

	@Override
	public void oneCycle(int cycle, City city, Colony colony, int efficiency100, ColonyPanel colPanel) { }

	@Override
	public void fromJson(JsonObject json) {
		name = json.get("name").toString();
        key  = Long.parseLong(json.get("key").toString());
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.put("type", getType());
        json.put("name", getName());
        json.put("key", new Long(getKey()));
        return json;
	}

	@Override
	public BigInteger getCheckerValue() {
		BigInteger res = new BigInteger(String.valueOf(getKey()));
		for(int idx=0; idx<getName().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getName().charAt(idx)))); }
		return res;
	}
	
	public static Product createProductInstance(String type) {
        Class<?> productClass = null;
        
        for(Class<?> classOne : ColonyClassLoader.productClasses()) {
            if(classOne.getName().equals(type)) { productClass = classOne; break; }
        }
        
        if(productClass == null) {
            for(Class<?> classOne : ColonyClassLoader.productClasses()) {
                if(classOne.getSimpleName().equals(type)) { productClass = classOne; break; }
            }
        }
        
        if(productClass != null) {
            try { return (Product) productClass.newInstance(); } catch(Exception ex) { throw new RuntimeException(ex.getMessage(), ex); }
        }
        return null;
	}
}
