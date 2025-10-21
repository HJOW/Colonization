package org.duckdns.hjow.colonization.elements.products;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
    protected transient boolean fNeedRefresh = true;
    
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
        return getTitle();
    }
    
    public String getType() {
        return this.getClass().getSimpleName();
    }
    
    @Override
    public final String getClassName() {
    	return getClass().getSimpleName();
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
    public int cycleGap(Colony colony) { return 1; }

    @Override
    public void oneCycle(int cycle, City city, Colony colony, int efficiency100, ColonyPanel colPanel) { }

    @Override
    public void fromJson(JsonObject json) {
        key  = Long.parseLong(json.get("key").toString());
    }

    @Override
    public JsonObject toJson() {
        return toJson(false, null, null);
    }
    
    @Override
    public JsonObject toJson(boolean details, Colony col, City city) {
        JsonObject json = new JsonObject();
        json.put("type", getType());
        json.put("key", String.valueOf(getKey()));
        
        return json;
    }

    @Override
    public BigInteger getCheckerValue() {
        BigInteger res = new BigInteger(String.valueOf(getKey()));
        for(int idx=0; idx<getName().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getName().charAt(idx)))); }
        return res;
    }
    
    /** 이 Product 의 표시 이름 반환 */
    public abstract String getTitle();
    
    /** 이 Product 생산에 필요한 재료로 어떤 Product가 필요한지를 반환, 중복 가능 (같은 종류 여러 개가 필요한 경우). 재료가 필요없는 경우 빈 List 객체 반환. getSourceProductsStatic 와 동일. */
    public List<Product> getSourceProducts() {
        return new ArrayList<Product>();
    }
    
    @Override
    public String toString() {
    	return getTitle();
    }
    
    @Override
    public boolean isMarkedAsRefresh() {
        return fNeedRefresh;
    }

    @Override
    public void markAsRefresh(boolean f) {
        fNeedRefresh = f;
    }
    
    @Override
    public void markAsRefreshChildren(boolean f) {
        markAsRefresh(f);
    }
    
    private static final List<Product> productTypeList = new Vector<Product>();
    private static synchronized void prepareProductTypeList() {
        productTypeList.clear();
        List<Class<?>> prodClasses = ColonyClassLoader.productClasses();
        if(! prodClasses.isEmpty()) {
            for(Class<?> prodClass : prodClasses) {
                try {
                    Product p = (Product) prodClass.newInstance();
                    productTypeList.add(p);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    /** Product 목록 반환. Info 클래스 따로 없이 빈 Product 객체 자체를 사용. */
    public static List<Product> getProductTypeList() {
        if(productTypeList.isEmpty()) {
            prepareProductTypeList();
        }
        
        List<Product> list = new Vector<Product>();
        list.addAll(productTypeList);
        return list;
    }
    
    /** 새 Product 객체 생성 */
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
