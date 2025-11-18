package org.duckdns.hjow.colonization.elements.ship;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.constants.Constants;
import org.duckdns.hjow.colonization.elements.Celestials;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.enemies.Enemy;
import org.duckdns.hjow.colonization.elements.facilities.Port;
import org.duckdns.hjow.colonization.elements.products.Product;
import org.duckdns.hjow.colonization.elements.states.State;
import org.duckdns.hjow.colonization.ColonyManagerInterface;
import org.duckdns.hjow.colonization.ui.ColonyPanel;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.DataUtil;

/** 함선 - 공통 구현 파트 */
public class AbstractShip implements Ship {
	private static final long serialVersionUID = 1415044038948566331L;
	protected volatile long key = ColonyManager.generateKey();
	protected String name = getDefaultName() + "_" + ColonyManager.getNaturalNumberFrom(key);
	protected int hp = getMaxHp();
	protected int level = 0;
	
	protected long leftProgress = Long.MAX_VALUE; // 이 함선 생산까지 남은 시간, 이 값이 0 초과 시 사용 불가, 매 사이클마다 감소
	
	protected List<State> states = new Vector<State>();
	protected List<Product> stored = new Vector<Product>();
	
	protected long x = 0L;
    protected long y = 0L;
    protected long z = 0L;
    
    protected long destinationX = 0L;
    protected long destinationY = 0L;
    protected long destinationZ = 0L;
    
    @Override
    public void init(Port port, Colony colony) {
    	level        = 0;
    	leftProgress = getMaxProgress(port, colony);
    	
    	City city = port.findCityBelongsTo(colony);
    	if(city != null) {
    		x = city.getX();
    		y = city.getY();
    		z = city.getZ();
    	} else {
    		x = colony.getX();
    		y = colony.getY();
    		z = colony.getZ();
    	}
    	
    	states.clear();
    	stored.clear();
    }
    
	@Override
	public int getAttackCycle() {
		return 120;
	}

	@Override
	public int getAttackCount() {
		return 1;
	}

	@Override
	public int getDamage() {
		return 5;
	}
	
	@Override
	public int getSpeed() {
		return 10;
	}

	@Override
	public short getAttackType() {
		return ColonyManager.ATTACKTYPE_NORMAL;
	}

	@Override
	public long getKey() {
		return key;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getClassName() {
		return getClass().getSimpleName();
	}

	@Override
	public String getTooltip() {
		return null;
	}

	@Override
	public int getHp() {
		return hp;
	}

	@Override
	public int getMaxHp() {
		return 100;
	}

	@Override
	public void setHp(int hp) {
		this.hp = hp;
	}

	@Override
	public void addHp(int amount) {
		hp += amount;
        int mx = getMaxHp();
        if(hp >  mx) hp = mx;
        if(hp <   0) hp = 0;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	@Override
	public void increaseLevel() {
		if(getLeftProgress() >= 1) return;
		setLeftProgress(0L);
		setLevel(getLevel() + 1);
	}

	@Override
	public short getDefenceType() {
		return ColonyManager.DEFENCETYPE_SMALL;
	}

	@Override
	public int getDefencePoint() {
		return getDefaultDefencePoint() + (int) getDefaultDefencePoint();
	}
	
	/** 함선 모델 자체의 기본 방어력 */
	public int getDefaultDefencePoint() {
		return 1;
	}
	
	/** 함선의 레벨 당 방어력 증가량 */
	protected double getDefencePointIncreases() {
		return (getLevel() * 0.1);
	}
	
	/** 이동 수행 */
	protected void processMove(Colony colony) {
		if(isArrived()) return; // 목적지에 이미 있으면 그냥 리턴
		
		long distance = DataUtil.getDistance(getX(), getY(), getZ(), getDestinationX(), getDestinationY(), getDestinationZ());
		
		// 거리가 1 이하이면 그냥 도착한 것으로 간주하고 넘기기
		if(distance <= 1) {
			setX(getDestinationX());
			setY(getDestinationY());
			setZ(getDestinationZ());
			return;
		}
		
		long speed = getRealSpeed(colony);
		long leftX = getDestinationX() - getX();
		long leftY = getDestinationY() - getY();
		long leftZ = getDestinationZ() - getZ();
		
		// 속도보다 거리가 더 가까운 경우, 1 사이클 지나면 목적지에 도착하므로, 마찬가지로 도착한 것으로 간주
		if(speed <= distance) {
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
	
	@Override
	public long getEstimatedArrivalTime(Colony colony) {
		if(isArrived()) return 0L; // 목적지에 이미 있으면 0 반환
		
        long distance = DataUtil.getDistance(getX(), getY(), getZ(), getDestinationX(), getDestinationY(), getDestinationZ());
		
		// 거리가 1 이하이면 1 사이클 내에 도착하므로 1 반환
		if(distance <= 1) {
			return 1L;
		}
		
		long speed = getRealSpeed(colony);
		
		// 속도보다 거리가 더 가까운 경우, 1 사이클 지나면 목적지에 도착하므로 1 반환
        if(speed <= distance) {
        	return 1L;
        }
        
        if(speed <= 0) return Long.MAX_VALUE; // 속도가 없으면 (뭔가의 이유로 저하) 무한대의 의미로 Long 최대값 반환
		return Math.abs(distance / speed) + 1;
	}

	@Override
	public void oneCycle(int cycle, ColonyElements stage, Colony colony, int efficiency100, ColonyPanel colPanel) {
		if(getLeftProgress() >= 1) return; // 제조/수리 진행 처리는 City 의 oneCycle 에서 진행
		
		// 이동 수행
		if(! isArrived()) {
			processMove(colony);
		}
		
		// 공격 수행
        List<Enemy> enemies = null;
        if(cycle % getAttackCycle() == 0) {
        	int castLeft    = getAttackCount();
            int damages     = getDamage();
            int naturalized = damages;
            
        	if(stage instanceof City) {
        		City city = (City) stage;
        		enemies = city.getEnemies();
        		
                for(Enemy e : enemies) {
                    if(e.getHp() >= 1) {
                    	if(castLeft <= 0) break;
                    	damages = getRealDamage(e, colony);
                        naturalized = ColonyManager.naturalizeDamage(this, e, damages);
                        e.addHp(naturalized * (-1));
                        processAfterAttack(cycle, e, naturalized);
                        castLeft--;
                    }
                }
                
                if(castLeft >= 1) {
                    enemies = colony.getEnemies();
                    for(Enemy e : enemies) {
                        if(e.getHp() >= 1) {
                        	if(castLeft <= 0) break;
                        	damages = getRealDamage(e, colony);
                            naturalized = ColonyManager.naturalizeDamage(this, e, damages);
                            e.addHp(naturalized * (-1));
                            processAfterAttack(cycle, e, naturalized);
                            castLeft--;
                        }
                    }
                }
        	} else if(stage instanceof Celestials) {
                Celestials cele = (Celestials) stage;
                enemies = cele.getEnemies();
                
                for(Enemy e : enemies) {
                    if(e.getHp() >= 1) {
                    	if(castLeft <= 0) break;
                    	damages = getRealDamage(e, colony);
                        naturalized = ColonyManager.naturalizeDamage(this, e, damages);
                        e.addHp(naturalized * (-1));
                        processAfterAttack(cycle, e, naturalized);
                        castLeft--;
                    }
                }
        	}
        }
	}
	
	/** 대미지 처리 후 추가 작업 (상태를 부여한다거나 등등) 이 메소드에서 구현 */
    protected void processAfterAttack(int cycle, ColonyElements element, int finalDamage) { }

	@Override
	public int cycleGap(Colony colony) {
		return 60;
	}

	@Override
	public void fromJson(JsonObject json) {
		setName(json.get("name").toString());
		key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
        setLevel(Integer.parseInt(json.get("level").toString()));
        setLeftProgress(Long.parseLong(json.get("leftProgress").toString()));
        
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
        
        list = (JsonArray) json.get("stored");
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
        json.put("type", getClassName());
        json.put("name", getName());
        json.put("key", String.valueOf(getKey()));
        json.put("hp", new Integer(getHp()));
        json.put("level", new Integer(getLevel()));
        json.put("leftProgress", String.valueOf(getLeftProgress()));
        
        json.put("x", String.valueOf(getX()));
        json.put("y", String.valueOf(getY()));
        json.put("z", String.valueOf(getZ()));
        
        json.put("dx", String.valueOf(getDestinationX()));
        json.put("dy", String.valueOf(getDestinationY()));
        json.put("dz", String.valueOf(getDestinationZ()));
        
        JsonArray list = new JsonArray();
        for(State s : getStates()) { list.add(s.toJson(details, col, city, excludeSecrets)); }
        json.put("states", list);
        
        list = new JsonArray();
        for(Product p : getStored()) { list.add(p.toJson()); }
        json.put("stored", list);
        
        return json;
	}

	@Override
	public BigInteger getCheckerValue() {
		BigInteger res = new BigInteger(String.valueOf(getKey()));
        for(int idx=0; idx<getClassName().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getClassName().charAt(idx)))); }
        for(int idx=0; idx<getName().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getName().charAt(idx)))); }
        res = res.add(new BigInteger(String.valueOf(getHp())));
        for(State st : getStates()) { res = res.add(st.getCheckerValue()); }
        for(Product p : getStored()) { res = res.add(p.getCheckerValue().multiply(Constants.BIGINTEGER_17)); }
        return res;
	}

	@Override
	public boolean isMarkedAsRefresh() {
		return false;
	}

	@Override
	public void markAsRefresh(boolean f) { }

	@Override
	public void markAsRefreshChildren(boolean f) { }

	@Override
	public void dispose() {
		
	}

	@Override
	public long getX() {
		return x;
	}

	@Override
	public long getY() {
		return y;
	}

	@Override
	public long getZ() {
		return z;
	}

	@Override
	public void setX(long x) {
		this.x = x;
	}

	@Override
	public void setY(long y) {
		this.y = y;
	}

	@Override
	public void setZ(long z) {
		this.z = z;
	}

	public void setKey(long key) {
		this.key = key;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public List<State> getStates() {
		return states;
	}

	public void setStates(List<State> states) {
		this.states = states;
	}

	public List<Product> getStored() {
		return stored;
	}

	public void setStored(List<Product> stored) {
		this.stored = stored;
	}

	@Override
	public void store(Product p) {
		stored.add(p);
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
    public int getStoredCount() {
        return stored.size();
    }

	@Override
	public int getMaxStoredCapacity() {
		return 10;
	}

	public long getDestinationX() {
		return destinationX;
	}

	@Override
	public void setDestinationX(long destinationX) {
		this.destinationX = destinationX;
	}

	public long getDestinationY() {
		return destinationY;
	}

	@Override
	public void setDestinationY(long destinationY) {
		this.destinationY = destinationY;
	}

	public long getDestinationZ() {
		return destinationZ;
	}

	@Override
	public void setDestinationZ(long destinationZ) {
		this.destinationZ = destinationZ;
	}

	@Override
	public long getLeftProgress() {
		return leftProgress;
	}
	
	@Override
	public void decreaseProgress(City city, Colony colony) {
		leftProgress = leftProgress - 1L;
		if(leftProgress < 0) leftProgress = 0L;
	}

	public void setLeftProgress(long leftProgress) {
		this.leftProgress = leftProgress;
	}

	@Override
	public void stop() {
		destinationX = x;
		destinationY = y;
		destinationZ = z;
	}

	@Override
	public void moveStartTo(int x, int y, int z) {
		destinationX = x;
		destinationY = y;
		destinationZ = z;
	}
	
	@Override
	public boolean isArrived() {
		return ( getX() == getDestinationX() && getY() == getDestinationY() && getZ() == getDestinationZ() );
	}

	@Override
	public long getRealSpeed(Colony col) {
		return getSpeed() + (int) Math.floor(getSpeedIncreases(col));
	}
	
	/** 레벨 당 속도 증가치 등 계산 */
	protected double getSpeedIncreases(Colony colony) {
		return level * (getSpeed() * 0.1);
	}
	
	@Override
    public int getRealDamage(ColonyElements target, Colony colony) {
    	return getDamage() + (int) Math.floor(getDamageIncreases(target, colony));
    }
	
	/** 레벨 당 대미지 증가치 등 계산 */
    protected double getDamageIncreases(ColonyElements target, Colony colony) {
    	return level * (getDamage() * 0.1);
    }
    
    @Override
    public String getDefaultName() {
    	try {
    	    Class<? extends Ship> classes = getClass();
    	    Method mthd = classes.getMethod("getMetaName");
    	    return (String) mthd.invoke(null);
    	} catch(Exception ex) {
    		throw new RuntimeException(ex.getMessage(), ex);
    	}
    }
    
    @Override
    public String getDescription() {
    	try {
    	    Class<? extends Ship> classes = getClass();
    	    Method mthd = classes.getMethod("getMetaDescription");
    	    return (String) mthd.invoke(null);
    	} catch(Exception ex) {
    		throw new RuntimeException(ex.getMessage(), ex);
    	}
    }
    
    @Override
    public long getMaxProgress(Port port, Colony colony) {
    	try {
    	    Class<? extends Ship> classes = getClass();
    	    Method mthd = classes.getMethod("getMetaBuildCycle", Port.class, Colony.class);
    	    return ((Number) mthd.invoke(null, port, colony)).longValue();
    	} catch(Exception ex) {
    		throw new RuntimeException(ex.getMessage(), ex);
    	}
    }
    
    @Override
    public long getPrice(Port port, Colony colony) {
    	try {
    	    Class<? extends Ship> classes = getClass();
    	    Method mthd = classes.getMethod("getMetaPrice", Port.class, Colony.class);
    	    return ((Number) mthd.invoke(null, port, colony)).longValue();
    	} catch(Exception ex) {
    		throw new RuntimeException(ex.getMessage(), ex);
    	}
    }
    
    @Override
	public int getSize() {
    	try {
    	    Class<? extends Ship> classes = getClass();
    	    Method mthd = classes.getMethod("getMetaSize");
    	    return ((Number) mthd.invoke(null)).intValue();
    	} catch(Exception ex) {
    		throw new RuntimeException(ex.getMessage(), ex);
    	}
	}
    
    @Override
	public String getStatusString(Colony col, ColonyManagerInterface superInstance) {
		return ""; // TODO
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
    
    /** 함선 명칭 */
    public static String getMetaName() {
		return ColonyManager.t("함선");
	}
    
    /** 함선 설명 */
    public static String getMetaDescription() {
    	return "";
    }
    
    /** 함선 건조 시간 (사이클) */
    public static long getMetaBuildCycle(Port port, Colony colony) {
    	return 200;
    }
    
    /** 함선 건조 가능여부, null 리턴 시 가능한 것. 그외의 경우 건조 불가능 사유 리턴 */
    public static String getMetaBuildAvail(Port port, Colony colony) {
    	return null;
    }
    
    /** 함선 건조 비용 */
    public static long getMetaPrice(Port port, Colony colony) {
    	return 10000L;
    }

    /** 함선의 크기 */
	public static int getMetaSize() {
		return 1;
	}
}
