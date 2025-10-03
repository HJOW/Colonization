package org.duckdns.hjow.colonization.elements.facilities;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.City;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.elements.states.State;
import org.duckdns.hjow.colonization.ui.ColonyPanel;

public abstract class DefaultFacility implements Facility {
    private static final long serialVersionUID = 8012568139388326869L;
    protected volatile long key = ColonyManager.generateKey();
    protected int hp = getMaxHp();
    protected int level = 1;
    
    protected List<State> states = new Vector<State>();
    
    @Override
    public int getComportGrade() {
        return 0;
    }
    
    @Override
    public int getHp() {
        return hp;
    }
    
    @Override
    public void setHp(int hp) {
        this.hp = hp;
    }
    
    @Override
    public void addHp(int amount) {
        hp += amount;
        int mx = getMaxHp();
        if(hp >= mx) hp = mx;
        if(hp <   0) hp = 0;
    }
    
    @Override
    public int getMaxHp() {
        return 1000;
    }
    
    @Override
    public short getDefenceType() {
        return ColonyManager.DEFENCETYPE_BUILDING;
    }

    @Override
    public int getDefencePoint() {
        return 1;
    }
    
    @Override
    public long getKey() {
        return key;
    }
    
    public void setKey(long key) {
        this.key = key;
    }

    @Override
    public int getWorkingCitizensCount(City city, Colony colony) {
        int count = 0;
        
        for(Citizen c : city.getCitizens()) {
            if(getKey() == c.getWorkingFacility()) {
                count++;
            }
        }
        
        return count;
    }
    
    @Override
    public List<Citizen> getWorkingCitizens(City city, Colony colony) {
        List<Citizen> list = new ArrayList<Citizen>();
        
        for(Citizen c : city.getCitizens()) {
            if(getKey() == c.getWorkingFacility()) {
                list.add(c);
            }
        }
        
        return list;
    }
    
    @Override
    public int getWorkerNeeded() {
        return 0;
    }
    @Override
    public int getWorkerCapacity() {
        return 0;
    }
    @Override
    public int increasingCityMaxHP() {
        return 0;
    }
    
    /** 사용료 */
    public long usingFee() { return 0; }
    
    /** 사용료에 붙는 세금을 반환 */
    protected long getTax(City city, Colony colony) {
        double rate = 0.0;
        if(city.getTax() >= 1) {
            rate = city.getTax() / 100.0;
        }
        
        BigDecimal res = new BigDecimal(String.valueOf(usingFee()));
        res = res.setScale(50, RoundingMode.FLOOR);
        res = res.multiply(new BigDecimal(String.valueOf(rate)));
        return res.longValue();
    }
    
    @Override
    public void oneCycle(int cycle, City city, Colony colony, int efficiency100, ColonyPanel colPanel) {
        // State 영향력 동작
        for(State st : getStates()) {
            st.oneCycle(cycle, this, city, colony, colPanel);
        }
        
        // State 수명 동작
        for(State st : getStates()) {
            st.oneCycle(cycle, city, colony, efficiency100, colPanel);
        }
        
        // 수명 다된 state 제거
        int std = 0;
        while(std < getStates().size()) {
            State st = getStates().get(std);
            if(st.getHp() <= 0 || st.getLefts() <= 0) {
            	st.dispose();
                getStates().remove(std);
                continue;
            }
            std++;
        }
    }
    
    @Override
    public long getSalary(City city, Colony colony) {
        return 1000L;
    }
    
    @Override
    public long getMaintainFee(City city, Colony colony) {
        return 1000L;
    }
    
    @Override
    public void fromJson(JsonObject json) {
        setName(json.get("name").toString());
        key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
        json.put("level", new Integer(getLevel()));
        
        JsonArray list = (JsonArray) json.get("states");
        states.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        JsonObject jsonObj = (JsonObject) o;
                        State stateOne = State.createStateInstance(jsonObj.get("type").toString());
                        if(stateOne == null) throw new NullPointerException("Cannot found these state type " + jsonObj);
                        
                        stateOne.fromJson(jsonObj);
                        states.add(stateOne);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type", getType());
        json.put("name", getName());
        json.put("key", new Long(getKey()));
        json.put("hp", new Long(getHp()));
        json.put("level", new Integer(getLevel()));
        
        JsonArray list = new JsonArray();
        for(State s : getStates()) { list.add(s.toJson()); }
        json.put("states", list);
        
        return json;
    }
    
    @Override
    public List<State> getStates() {
        return states;
    }
    
    public void setStates(List<State> states) {
        this.states = states;
    }

    @Override
    public BigInteger getCheckerValue() {
        BigInteger res = new BigInteger(String.valueOf(getKey()));
        for(int idx=0; idx<getType().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getType().charAt(idx)))); }
        for(int idx=0; idx<getName().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getName().charAt(idx)))); }
        res = res.add(new BigInteger(String.valueOf(getHp())));
        for(State st : getStates()) { res = res.add(st.getCheckerValue()); }
        return res;
    }
    
    @Override
    public void dispose() {
    	for(State st : getStates()) {
    		st.dispose();
    	}
    	states.clear();
    }
    
    @Override
    public int getLevel() { return level; }
    
    @Override
    public void setLevel(int lv) { this.level = lv; if(this.level > getMaxLevel()) this.level = getMaxLevel(); }
    
    @Override
    public int getMaxLevel() { return Integer.MAX_VALUE - 1; }
    
    @Override
    public boolean isUpgradeAvail(Colony col, City city) {
    	if(getLevel() >= getMaxLevel()) return false;
    	if(col.getMoney() < getUpgradePrice(col, city)) return false;
    	
    	return true;
    }
    
    @Override
    public long getUpgradePrice(Colony col, City city) {
    	long res = startUpgradePrice();
    	for(int idx=1; idx<getLevel(); idx++) {
    		if(res >= Long.MAX_VALUE / 10) return Long.MAX_VALUE / 10;
    		
    		long increases = (long) Math.floor( res * increateUpgradePriceRate() );
    		if(increases < 1L) increases = 1L;
    		res = res + increases;
    	}
    	return res;
    }
    
    @Override
    public int getUpgradeCycle(Colony col, City city) {
    	int res = startUpgradeCycle();
    	for(int idx=1; idx<getLevel(); idx++) {
    		if(res >= Integer.MAX_VALUE / 10) return Integer.MAX_VALUE / 10;
    		
    		int increases = (int) Math.floor( res * increaseUpgradeCycleRate() );
    		if(increases < 1) increases = 1;
    		res = res + increases;
    	}
    	return res;
    }
    
    @Override
    public int getCapacity() {
    	int res = getDefaultCapacity();
    	if(res == 0) return res;
    	for(int idx=1; idx<getLevel(); idx++) {
    		if(res >= Integer.MAX_VALUE / 10) return Integer.MAX_VALUE / 10;
    		
    		int increases = (int) Math.floor( res * increateCapacityRate() );
    		if(increases < 1) increases = 1;
    		res = res + increases;
    	}
    	return res;
    }
    
    /** 업그레이드 비용 시작 금액 */
    protected long startUpgradePrice() {
    	return 5000L;
    }
    
    /** 업그레이드 비용의 레벨 당 증가율 */
    protected double increateUpgradePriceRate() {
    	return 0.2;
    }
    
    /** 업그레이드 비용 시작 금액 */
    protected int startUpgradeCycle() {
    	return 200;
    }
    
    /** 업그레이드 비용의 레벨 당 증가율 */
    protected double increaseUpgradeCycleRate() {
    	return 0.2;
    }
    
    /** 기본 레벨의 Capacity 값 */
    protected int getDefaultCapacity() {
    	return 0;
    }
    
    /** 레벨 당 Capacity 증가율 */
    protected double increateCapacityRate() {
    	return 0.1;
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
        return new Long(10000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(1200);
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
