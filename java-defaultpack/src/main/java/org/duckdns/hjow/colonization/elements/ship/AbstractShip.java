package org.duckdns.hjow.colonization.elements.ship;

import java.math.BigInteger;
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
import org.duckdns.hjow.colonization.elements.products.Product;
import org.duckdns.hjow.colonization.elements.states.State;
import org.duckdns.hjow.colonization.ui.ColonyPanel;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;

/** 함선 - 공통 구현 파트 */
public class AbstractShip implements Ship {
	private static final long serialVersionUID = 1415044038948566331L;
	protected volatile long key = ColonyManager.generateKey();
	protected String name = ColonyManager.t("함선") + "_" + ColonyManager.getNaturalNumberFrom(key);
	protected int hp = getMaxHp();
	
	protected List<State> states = new Vector<State>();
	protected List<Product> stored = new Vector<Product>();
	
	protected long x = 0L;
    protected long y = 0L;
    protected long z = 0L;

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
		return 1;
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

	@Override
	public short getDefenceType() {
		return ColonyManager.DEFENCETYPE_SMALL;
	}

	@Override
	public int getDefencePoint() {
		return 1;
	}

	@Override
	public void oneCycle(int cycle, ColonyElements stage, Colony colony, int efficiency100, ColonyPanel colPanel) {
		int castLeft    = getAttackCount();
        int damages     = getDamage();
        int naturalized = damages;
        
        List<Enemy> enemies = null;
        if(cycle % getAttackCycle() == 0) {
        	if(stage instanceof City) {
        		City city = (City) stage;
        		enemies = city.getEnemies();
        		
                for(Enemy e : enemies) {
                    if(e.getHp() >= 1) {
                        naturalized = ColonyManager.naturalizeDamage(this, e, damages);
                        e.addHp(naturalized * (-1));
                        processAfterAttack(cycle, e, naturalized);
                        castLeft--;
                        if(castLeft <= 0) break;
                    }
                }
                
                if(castLeft >= 1) {
                    enemies = colony.getEnemies();
                    for(Enemy e : enemies) {
                        if(e.getHp() >= 1) {
                            naturalized = ColonyManager.naturalizeDamage(this, e, damages);
                            e.addHp(naturalized * (-1));
                            processAfterAttack(cycle, e, naturalized);
                            castLeft--;
                            if(castLeft <= 0) break;
                        }
                    }
                }
        	} else if(stage instanceof Celestials) {
                Celestials cele = (Celestials) stage;
                enemies = cele.getEnemies();
                
                for(Enemy e : enemies) {
                    if(e.getHp() >= 1) {
                        naturalized = ColonyManager.naturalizeDamage(this, e, damages);
                        e.addHp(naturalized * (-1));
                        processAfterAttack(cycle, e, naturalized);
                        castLeft--;
                        if(castLeft <= 0) break;
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
		return toJson(false, null, null);
	}

	@Override
	public JsonObject toJson(boolean details, Colony col, City city) {
		JsonObject json = new JsonObject();
        json.put("type", getClassName());
        json.put("name", getName());
        json.put("key", String.valueOf(getKey()));
        
        json.put("hp", new Integer(getHp()));
        
        JsonArray list = new JsonArray();
        for(State s : getStates()) { list.add(s.toJson(details, col, city)); }
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

}
