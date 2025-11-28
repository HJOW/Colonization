package org.duckdns.hjow.colonization.elements.states;
import java.math.BigInteger;
import java.util.List;

import org.duckdns.hjow.commons.exception.KnownRuntimeException;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.colonization.ColonyClassManager;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.ui.ColonyPanel;

/** 시민, 혹은 시설의 상태 하나를 지칭하는 객체를 위한 클래스 */
public abstract class AbstractState implements State {
    private static final long serialVersionUID = -8452951686397752158L;
    protected volatile long key = ColonyManager.generateKey();
    protected volatile int  hp = getMaxHp();
    protected volatile long lefts = getDefaultLefts();
    
    protected transient boolean fNeedRefresh = true;

    @Override
    public long getKey() {
        return key;
    }
    
    public void setKey(long key) {
        this.key = key;
    }
    
    @Override
    public String getTooltip() {
        return getName();
    }
    
    @Override
    public final String getClassName() {
        return getClass().getSimpleName();
    }

    @Override
    public int getHp() {
        return hp;
    }
    
    @Override
    public void setHp(int h) {
        this.hp = h;
    }
    
    @Override
    public short getDefenceType() {
        return 0;
    }

    @Override
    public int getDefencePoint() {
        return 0;
    }

    @Override
    public void addHp(int amount) {
        setHp(getHp() + amount);
        if(getHp() < 0) setHp(0);
        if(getHp() > getMaxHp()) setHp(getMaxHp());
    }

    @Override
    public long getLefts() {
        return lefts;
    }

    public void setLefts(long lefts) {
        this.lefts = lefts;
    }
    
    @Override
    public int cycleGap(Colony colony) { return 1; }

    @Override
    public final void oneCycle(int cycle, ColonyElements stage, Colony colony, int efficiency100, ColonyPanel colPanel) {
        // 이 메소드는 수정하거나 오버라이드하지 말 것 !
        
        if(lefts < Long.MAX_VALUE) lefts--;
        if(lefts < 0) lefts = 0L;
        if(lefts == 0) setHp(0);
    }

    @Override
    public void fromJson(JsonObject json) {
        String clsName = getClass().getSimpleName();
        if(! clsName.equals(json.get("type"))) throw new KnownRuntimeException("This object is not " + clsName + " type.");
        key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
        setLefts(Long.parseLong(json.get("lefts").toString()));
    }

    @Override
    public JsonObject toJson() {
        return toJson(false, null, null, false);
    }
    
    @Override
    public JsonObject toJson(boolean excludeSecrets) {
    	return toJson(false, null, null, excludeSecrets);
    }
    
    @Override
    public JsonObject toJson(boolean details, Colony col, City city, boolean excludeSecrets) {
        JsonObject json = new JsonObject();
        json.put("type", getClass().getSimpleName());
        json.put("name", getName());
        json.put("key", String.valueOf(getKey()));
        json.put("hp", new Integer(getHp()));
        json.put("lefts", String.valueOf(getLefts()));
        return json;
    }

    @Override
    public BigInteger getCheckerValue() {
        BigInteger res = new BigInteger(String.valueOf(getKey()));
        res = res.add(new BigInteger(String.valueOf(getHp())));
        res = res.add(new BigInteger(String.valueOf(getLefts())));
        for(int idx=0; idx<getName().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getName().charAt(idx)))); }
        return res;
    }
    
    @Override
    public void dispose() {
        
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
    
    @Override
    public Object cloneThis() {
    	try {
    	    Class<?> classThis = getClass();
    	    ColonyElements col = (ColonyElements) classThis.newInstance();
    	    col.fromJson(toJson());
    	    return col;
    	} catch(Exception ex) {
    		throw new RuntimeException(ex.getMessage(), ex);
    	}
    }
    
    @Override
    public String describeForAI(Colony colony, City city) {
    	return ""; // TODO
    }

    protected static List<Class<?>> stateClasses = ColonyClassManager.stateClasses();
    
    public static void reset() {
        stateClasses.clear();
        stateClasses.addAll(ColonyClassManager.stateClasses());
    }
    
    public static State createStateInstance(String type) {
        Class<?> stateClass = null;
        
        if(stateClasses.isEmpty()) reset();
        
        for(Class<?> classOne : stateClasses) {
            if(classOne.getName().equals(type)) { stateClass = classOne; break; }
        }
        
        if(stateClass == null) {
            for(Class<?> classOne : stateClasses) {
                if(classOne.getSimpleName().equals(type)) { stateClass = classOne; break; }
            }
        }
        
        if(stateClass != null) {
            try { return (State) stateClass.newInstance(); } catch(Exception ex) { throw new RuntimeException(ex.getMessage(), ex); }
        }
        return null;
    }
}
