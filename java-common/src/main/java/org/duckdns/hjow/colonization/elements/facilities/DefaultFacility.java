package org.duckdns.hjow.colonization.elements.facilities;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;
import org.duckdns.hjow.colonization.elements.states.State;
import org.duckdns.hjow.colonization.ui.ColonyPanel;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.DataUtil;
import org.duckdns.hjow.commons.util.HexUtil;

public abstract class DefaultFacility implements Facility {
    private static final long serialVersionUID = 8012568139388326869L;
    protected volatile long key = ColonyManager.generateKey();
    protected transient boolean fNeedRefresh = true;
    protected transient double boostRate = 0.0;
    
    protected String name = getDefaultNamePrefix() + "_" + ColonyManager.getNaturalNumberFrom(key);
    protected int hp = getMaxHp();
    protected int level = 1;
    
    protected List<State> states = new Vector<State>();
    
    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
    
    @Override
    public final String getClassName() {
        return getClass().getSimpleName();
    }
    
    @Override
    public String getTooltip() {
        return getName();
    }
    
    /** 이름 앞부분 */
    protected abstract String getDefaultNamePrefix();
    
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
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
    public int cycleGap(Colony colony) { return 1; }
    
    @Override
    public void oneCycle(int cycle, City city, Colony colony, int efficiency100, ColonyPanel colPanel) {
        // State 영향력 동작
        for(State st : getStates()) {
            if(cycle % st.cycleGap(colony) == 0) st.oneCycle(cycle, this, city, colony, colPanel);
        }
        
        // State 수명 동작
        for(State st : getStates()) {
            if(cycle % st.cycleGap(colony) == 0) st.oneCycle(cycle, city, colony, efficiency100, colPanel);
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
    public long getDestructionFee(City city, Colony colony) {
        return 1000L;
    }
    
    @Override
    public String getStatusDescription(City city, Colony colony) {
        return "";
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
        return toJson(false, null, null);
    }
    
    @Override
    public JsonObject toJson(boolean details, Colony col, City city) {
        JsonObject json = new JsonObject();
        json.put("type", getType());
        json.put("name", getName());
        json.put("key", String.valueOf(getKey()));
        json.put("hp", String.valueOf(getHp()));
        json.put("level", new Integer(getLevel()));
        
        JsonArray list = new JsonArray();
        for(State s : getStates()) { list.add(s.toJson(details, col, city)); }
        json.put("states", list);
        
        // 추가 정보 (불러올 때는 필요가 없는) 첨가
        json.put("maxHp", String.valueOf(getMaxHp()));
        json.put("spaceSize", new Integer(getSpaceSize()));
        
        if(details) {
            String str = getStatusDescription(city, col);
            if(str == null) str = "";
            json.put("statusString", HexUtil.encodeString(str));
        }
        
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
    public String checkUpgradeAvail(Colony col, City city) {
    	if(getLevel() >= getMaxLevel()) return "더 이상 증축할 수 없는 시설입니다.";
        if(col.getMoney() < getUpgradePrice(col, city)) return "예산이 부족하여 증축할 수 없습니다.";
        return null;
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
    
    @Override
    public int getSpaceSize() {
        return 1;
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
    
    @Override
    public void setBoostRate(double boostRate) {
        this.boostRate = boostRate;
    }
    
    public double getBoostRate() {
        return this.boostRate;
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
        for(State s : getStates()) { s.markAsRefreshChildren(f); }
    }
    
    @Override
    public final boolean isScriptBased() {
        try {
            Class<? extends Facility> classThis = getClass();
            Method mthd = classThis.getMethod("isScriptBasedFacility");
            return DataUtil.parseBoolean(String.valueOf(mthd.invoke(null)));
        } catch(NoSuchMethodException ex) {
            return true; // 알 수 없는 경우 true 로 취급 - 인증 해제됨
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    @Override
    public Object getImageContent() {
        try {
            Class<? extends Facility> classThis = getClass();
            Method mthd = classThis.getMethod("getImage");
            return mthd.invoke(null);
        } catch(NoSuchMethodException ex) {
            return null; // 알 수 없는 경우 null
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
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
    
    public static int getUniqueFacilityGrade() {
        return FACILITY_UNIQUE_GRADE_NONE;
    }
    
    public static Object getImage() {
        return null;
    }
    
    public static List<ResearchCondition> getResearchCoditions(Colony col) {
        return new ArrayList<ResearchCondition>();
    }
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public static String isBuildAvail(Colony col, City city) { 
        return null;
    }
    
    public static boolean isScriptBasedFacility() { return false; }
    
    public static final int FACILITY_UNIQUE_GRADE_NONE   =   0; // 고유하지 않은, 예산과 공간만 있으면 얼마든지 지을 수 있는 시설
    public static final int FACILITY_UNIQUE_GRADE_CITY   =  10; // 도시 당 하나만 건설이 가능한 시설
    public static final int FACILITY_UNIQUE_GRADE_COLONY = 100; // 정착지 당 하나만 건설이 가능한 시설
}
