package org.duckdns.hjow.colonization.elements.enemies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.colonization.ColonyClassLoader;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.elements.AttackableObject;
import org.duckdns.hjow.colonization.elements.Celestials;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.ship.Ship;
import org.duckdns.hjow.colonization.elements.states.State;
import org.duckdns.hjow.colonization.ui.ColonyPanel;

/** 적 개체 */
public abstract class Enemy implements ColonyElements, AttackableObject {
    private static final long serialVersionUID = 8827673273232204593L;
    protected volatile long key   = ColonyManager.generateKey();
    protected volatile int  hp    = getMaxHp();
    protected volatile int  level = 1;
    protected List<State> states = new Vector<State>();
    
    protected long x = 0L;
    protected long y = 0L;
    protected long z = 0L;
    
    protected transient boolean fNeedRefresh = true;
    
    public Enemy() {}

    public long getKey() {
        return key;
    }
    
    @Override
    public String getTooltip() {
        return getName();
    }

    public int getHp() {
        return hp;
    }
    
    public int getMaxHp() {
        return 100;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setHp(int hp) {
        this.hp = hp;
        int mx = getMaxHp();
        if(hp >  mx) hp = mx;
        if(hp <   0) hp = 0;
    }
    
    @Override
    public int getAttackCount() {
        return 1;
    }
    
    @Override
    public int getDamage() {
        return 1;
    }
    
    @Override
    public int getAttackCycle() {
        return 120;
    }
    
    @Override
    public short getAttackType() {
        return 0;
    }
    
    @Override
    public void addHp(int amount) {
        hp += amount;
        int mx = getMaxHp();
        if(hp >  mx) hp = mx;
        if(hp <   0) hp = 0;
    }
    
    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }
    
    /** 대미지 처리 후 추가 작업 (상태를 부여한다거나 등등) 이 메소드에서 구현 */
    protected void processAfterAttack(int cycle, ColonyElements element, int finalDamage) { }
    
    @Override
    public int cycleGap(Colony colony) { return 1; }

    @Override
    public void oneCycle(int cycle, ColonyElements stage, Colony colony, int efficiency100, ColonyPanel colPanel) {
        
        // 공격 처리
        int castLeft    = getAttackCount();
        int damages     = 0;
        int naturalized = 0;
        
        if(cycle % getAttackCycle() == 0) {
        	if(stage instanceof City) {
        		City city = (City) stage;
        		// 함선 목록 불러오기
        		List<Ship> ships = city.getShips();
                
                // 시설 목록 불러오기
                List<Facility> facs = new ArrayList<Facility>();
                facs.addAll(city.getFacility());
                
                //     순서 랜덤화 시키기
                Collections.sort(facs, new Comparator<Facility>() {
                    @Override
                    public int compare(Facility o1, Facility o2) {
                        if(ColonyManager.random() >= 0.5) return -1;
                        return 1;
                    }
                });
                
                //     순서대로 공격 처리
                // 함선
                for(Ship fac : ships) {
                	if(castLeft <= 0) break;
                    if(fac.getHp() >= 1) {
                    	damages     = getRealDamage(fac, colony);
                        naturalized = ColonyManager.naturalizeDamage(this, fac, damages);
                        fac.addHp(naturalized * (-1));
                        processAfterAttack(cycle, fac, naturalized);
                        castLeft--;
                    }
                    
                }
                
                // 시설
                for(Facility fac : facs) {
                	if(castLeft <= 0) break;
                    if(fac.getHp() >= 1) {
                    	damages     = getRealDamage(fac, colony);
                        naturalized = ColonyManager.naturalizeDamage(this, fac, damages);
                        fac.addHp(naturalized * (-1));
                        processAfterAttack(cycle, fac, naturalized);
                        castLeft--;
                    }
                    
                }
                
                // 시설이 없으면 시민 공격
                if(castLeft >= 1) {
                    List<Citizen> citizens = city.getCitizens();
                    for(Citizen ct : citizens) {
                    	if(castLeft <= 0) break;
                        if(ct.getHp() >= 1) {
                        	damages     = getRealDamage(ct, colony);
                            naturalized = ColonyManager.naturalizeDamage(this, ct, damages);
                            ct.addHp(naturalized * (-1));
                            processAfterAttack(cycle, ct, naturalized);
                            castLeft--;
                        }
                    }
                }
                
                // 시설, 시민 모두 없으면 도시 자체
                if(castLeft >= 1) {
                    if(city.getHp() >= 1) {
                    	if(castLeft <= 0) return;
                    	damages     = getRealDamage(city, colony);
                        naturalized = ColonyManager.naturalizeDamage(this, city, damages);
                        processAfterAttack(cycle, city, naturalized);
                        city.addHp(naturalized * (-1));
                        castLeft--;
                    }
                }
        	} else if(stage instanceof Celestials) {
        		List<Ship> ships = colony.getShips(getX(), getY(), getZ());
        		for(Ship ship : ships) {
        			if(castLeft <= 0) break;
        			if(ship.getHp() >= 1) {
        				damages     = getRealDamage(ship, colony);
                        naturalized = ColonyManager.naturalizeDamage(this, ship, damages);
                        processAfterAttack(cycle, ship, naturalized);
                        ship.addHp(naturalized * (-1));
                        castLeft--;
        			}
        		}
        	}
        }
    }
    
    @Override
    public void fromJson(JsonObject json) {
        key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
        setLevel(Integer.parseInt(json.get("level").toString()));
        
        try { x = Long.parseLong(json.get("x").toString());               } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setX(0L); }
        try { y = Long.parseLong(json.get("y").toString());               } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setY(0L); }
        try { z = Long.parseLong(json.get("z").toString());               } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setZ(0L); }
        
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
        json.put("key", String.valueOf(getKey()));
        json.put("hp", String.valueOf(getHp()));
        json.put("level", new Integer(getLevel()));
        
        json.put("x", String.valueOf(getX()));
        json.put("y", String.valueOf(getY()));
        json.put("z", String.valueOf(getZ()));
        
        JsonArray list = new JsonArray();
        for(State s : getStates()) { list.add(s.toJson(details, col, city, excludeSecrets)); }
        json.put("states", list);
        
        return json;
    }
    
    
    protected static List<Class<?>> enemyClasses = ColonyClassLoader.enemyClasses();
    
    /** type 를 읽어, 기본 Enemy 객체 생성 */
    public static Enemy createEnemyObject(String type) {
        Class<?> enemyClass = null;
        for(Class<?> classOne : enemyClasses) {
            if(classOne.getSimpleName().equals(type) || classOne.getName().equals(type)) {
                enemyClass = classOne;
                break;
            }
        }
        if(enemyClass == null) return null;
        try { return (Enemy) enemyClass.newInstance(); } catch(Exception ex) { throw new RuntimeException(ex.getMessage(), ex); }
    }
    
    /** JSON을 읽어 Enemy 객체 생성 */
    public static Enemy createEnemyFromJson(JsonObject json) {
        String type = json.get("type").toString();
        
        Enemy en = createEnemyObject(type);
        if(en == null) return null;
        
        en.fromJson(json);
        return en;
    }
    
    @Override
    public void dispose() { }
    
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

	public long getX() {
		return x;
	}

	public void setX(long x) {
		this.x = x;
	}

	public long getY() {
		return y;
	}

	public void setY(long y) {
		this.y = y;
	}

	public long getZ() {
		return z;
	}

	public void setZ(long z) {
		this.z = z;
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
}
