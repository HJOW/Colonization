package org.duckdns.hjow.colonization.elements.enemies;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.colonization.ColonyClassManager;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.elements.Space;
import org.duckdns.hjow.colonization.elements.celestials.Celestials;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.ship.Ship;
import org.duckdns.hjow.colonization.elements.states.AbstractState;
import org.duckdns.hjow.colonization.elements.states.State;
import org.duckdns.hjow.colonization.ui.ColonyPanel;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.ui.graphics.Coordinate3D;
import org.duckdns.hjow.commons.util.DataUtil;

/** 적 개체 - 공통 파트 */
public abstract class AbstractEnemy implements Enemy {
    private static final long serialVersionUID = 8827673273232204593L;
    protected volatile long key   = ColonyManager.generateKey();
    protected volatile int  hp    = getMaxHp();
    protected volatile int  level = 1;
    protected List<State> states = new Vector<State>();
    
    protected long x = 0L;
    protected long y = 0L;
    protected long z = 0L;
    
    protected long destinationX = x;
    protected long destinationY = y;
    protected long destinationZ = z;
    
    protected int speed = 3;
    
    protected transient boolean fNeedRefresh = true;
    
    public AbstractEnemy() {}

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
    public void oneCycle(int cycle, ColonyElements stage, Space space, Colony colony, int efficiency100, ColonyPanel colPanel) {
        
        // 공격 처리 (이동 처리는 oneCycle 에서 안함)
        int castLeft    = getAttackCount();
        int damages     = 0;
        int naturalized = 0;
        
        if(cycle % getAttackCycle() == 0) {
            if(stage instanceof City) {
                City city = (City) stage;
                
                // 위치 체크
                if(getX() == city.getX() && getY() == city.getY() && getZ() == city.getZ()) {
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
                }
            } else if(stage instanceof Celestials) {
            	Celestials cele = (Celestials) stage;
            	
            	// 위치 체크
                if(getX() == cele.getX() && getY() == cele.getY() && getZ() == cele.getZ()) {
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
    }
    
    /** 이동 사이클 수행 (정착지 oneCycle 에서 호출) */
    @Override
    public void processMove(int cycle) {
    	if(isArrived()) return;
    	
        long distance = DataUtil.getDistance(getX(), getY(), getZ(), getDestinationX(), getDestinationY(), getDestinationZ());
		
		// 거리가 1 이하이면 그냥 도착한 것으로 간주하고 넘기기
		if(distance <= 1) {
			setX(getDestinationX());
			setY(getDestinationY());
			setZ(getDestinationZ());
			return;
		}
		
		long leftX = getDestinationX() - getX();
		long leftY = getDestinationY() - getY();
		long leftZ = getDestinationZ() - getZ();
		
		// 속도보다 거리가 더 가까운 경우, 1 사이클 지나면 목적지에 도착하므로, 마찬가지로 도착한 것으로 간주
		if(speed >= distance) {
			setX(getDestinationX());
			setY(getDestinationY());
			setZ(getDestinationZ());
			return;
		}
		
		BigDecimal ratio  = new BigDecimal(String.valueOf(speed)).divide(new BigDecimal(String.valueOf(distance)), 50, RoundingMode.HALF_UP);
		BigDecimal deltaX = new BigDecimal(String.valueOf(leftX)).multiply(ratio);
		BigDecimal deltaY = new BigDecimal(String.valueOf(leftY)).multiply(ratio);
		BigDecimal deltaZ = new BigDecimal(String.valueOf(leftZ)).multiply(ratio);
		
		// 이동 수행
		setX(getX() + deltaX.longValue());
		setY(getY() + deltaY.longValue());
		setZ(getZ() + deltaZ.longValue());
    }
    
    /** 도착 여부 반환 */
    @Override
    public boolean isArrived() {
		return ( getX() == getDestinationX() && getY() == getDestinationY() && getZ() == getDestinationZ() );
	}
    
    @Override
    public void fromJson(JsonObject json) {
        key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
        setLevel(Integer.parseInt(json.get("level").toString()));
        setSpeed(Integer.parseInt(json.get("speed").toString()));
        
        try { x = Long.parseLong(json.get("x").toString());               } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setX(0L); }
        try { y = Long.parseLong(json.get("y").toString());               } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setY(0L); }
        try { z = Long.parseLong(json.get("z").toString());               } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setZ(0L); }
        
        try { destinationX = Long.parseLong(json.get("dx").toString());   } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setDestinationX(0L); }
        try { destinationY = Long.parseLong(json.get("dy").toString());   } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setDestinationY(0L); }
        try { destinationZ = Long.parseLong(json.get("dz").toString());   } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setDestinationZ(0L); }
        
        JsonArray list = (JsonArray) json.get("states");
        states.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        JsonObject jsonObj = (JsonObject) o;
                        State stateOne = AbstractState.createStateInstance(jsonObj.get("type").toString());
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
        json.put("speed", new Integer(getSpeed()));
        
        json.put("x", String.valueOf(getX()));
        json.put("y", String.valueOf(getY()));
        json.put("z", String.valueOf(getZ()));
        
        json.put("dx", String.valueOf(getDestinationX()));
        json.put("dy", String.valueOf(getDestinationY()));
        json.put("dz", String.valueOf(getDestinationZ()));
        
        JsonArray list = new JsonArray();
        for(State s : getStates()) { list.add(s.toJson(details, col, city, excludeSecrets)); }
        json.put("states", list);
        
        return json;
    }
    
    
    protected static List<Class<?>> enemyClasses = ColonyClassManager.enemyClasses();
    
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
    public Coordinate3D getCoordinate() {
        return new Coordinate3D(getX(), getY(), getZ());
    }

    @Override
    public void setCoordinate(Coordinate3D coordinate) {
        setX(coordinate.getX());
        setY(coordinate.getY());
        setZ(coordinate.getZ());
    }
    
    @Override
	public boolean isSameLocation(Coordinate3D coordinate) {
		return (getX() == coordinate.getX() && getY() == coordinate.getY() && getZ() == coordinate.getZ());
	}
    
    public long getDestinationX() {
		return destinationX;
	}

	public void setDestinationX(long destinationX) {
		this.destinationX = destinationX;
	}

	public long getDestinationY() {
		return destinationY;
	}

	public void setDestinationY(long destinationY) {
		this.destinationY = destinationY;
	}

	public long getDestinationZ() {
		return destinationZ;
	}

	public void setDestinationZ(long destinationZ) {
		this.destinationZ = destinationZ;
	}
	
	@Override
	public Coordinate3D getDestination() {
		return new Coordinate3D(getDestinationX(), getDestinationY(), getDestinationZ());
	}

	@Override
	public void setDestination(Coordinate3D dest) {
		setDestinationX(dest.getX());
        setDestinationY(dest.getY());
        setDestinationZ(dest.getZ());
	}
    
	@Override
    public int getSpeed() {
		return speed;
	}

    @Override
	public void setSpeed(int speed) {
		this.speed = speed;
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
	public long getEstimatedArrivalTime(Colony colony) {
        if(isArrived()) return 0L; // 목적지에 이미 있으면 0 반환
		
        long distance = DataUtil.getDistance(getX(), getY(), getZ(), getDestinationX(), getDestinationY(), getDestinationZ());
		
		// 거리가 1 이하이면 1 사이클 내에 도착하므로 1 반환
		if(distance <= 1) {
			return 1L;
		}
		
		// 속도보다 거리가 더 가까운 경우, 1 사이클 지나면 목적지에 도착하므로 1 반환
        if(speed <= distance) {
        	return 1L;
        }
        
        if(speed <= 0) return Long.MAX_VALUE; // 속도가 없으면 (뭔가의 이유로 저하) 무한대의 의미로 Long 최대값 반환
		return Math.abs(distance / speed) + 1;
	}
    
    @Override
    public String describeForAI(Colony colony, City city) {
        return ""; // TODO
    }
}
