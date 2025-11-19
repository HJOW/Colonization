package org.duckdns.hjow.colonization.elements.celestials;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.constants.Constants;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.enemies.AbstractEnemy;
import org.duckdns.hjow.colonization.elements.enemies.Enemy;
import org.duckdns.hjow.colonization.elements.products.AbstractProduct;
import org.duckdns.hjow.colonization.elements.products.Product;
import org.duckdns.hjow.colonization.elements.ship.Ship;
import org.duckdns.hjow.colonization.ui.ColonyPanel;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.DataUtil;

/** 천체, 여기서는 탐험지 */
public class DefaultCelestials implements Celestials {
	private static final long serialVersionUID = 912340193572680618L;
	protected volatile long key = ColonyManager.generateKey();
	protected long x = 0L;
    protected long y = 0L;
    protected long z = 0L;
    protected String name = "소행성_" + key;
    protected boolean opened = false;

	protected List<Enemy>   enemies = new ArrayList<Enemy>();
	protected List<Product> debries = new ArrayList<Product>();
	
	public DefaultCelestials() {}

	@Override
	public String getClassName() {
		return "celestials";
	}
	
	@Override
	public void fromJson(JsonObject json) {
		try { setName(json.get("name").toString());                                  } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setName("");  }
		try { key = Long.parseLong(json.get("key").toString());                      } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setKey(ColonyManager.generateKey()); }
		try { setOpened(DataUtil.parseBoolean(String.valueOf(json.get("opened"))));  } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setOpened(false); }
		
		try { x = Long.parseLong(json.get("x").toString());               } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setX(0L); }
        try { y = Long.parseLong(json.get("y").toString());               } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setY(0L); }
        try { z = Long.parseLong(json.get("z").toString());               } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setZ(0L); }
        
        JsonArray list = null;
        
        try { list = (JsonArray) json.get("enemies"); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        enemies.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        Enemy en = AbstractEnemy.createEnemyFromJson((JsonObject) o);
                        enemies.add(en);
                    } catch(Exception ex) {
                        GlobalLogs.processExceptionOccured(ex, false);
                    }
                }
            }
        }
		
        list = null;
        try { list = (JsonArray) json.get("debries"); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        debries.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        JsonObject jsonObj = (JsonObject) o;
                        Product productOne = AbstractProduct.createProductInstance(jsonObj.get("type").toString());
                        if(productOne == null) throw new NullPointerException("Cannot found these product type " + jsonObj);
                        
                        productOne.fromJson(jsonObj);
                        debries.add(productOne);
                    } catch(Exception ex) {
                        GlobalLogs.processExceptionOccured(ex, false);
                    }
                }
            }
        }
	}
	
	@Override
	public JsonObject toJson(boolean details, Colony col, City city, boolean excludeSecrets) {
		JsonObject json = new JsonObject();
		json.put("type", getClassName());
        json.put("name", getName());
        json.put("key", String.valueOf(getKey()));
        json.put("opened", isOpened() ? "Y" : "N");
        
        json.put("x", String.valueOf(getX()));
        json.put("y", String.valueOf(getY()));
        json.put("z", String.valueOf(getZ()));
        
        JsonArray list = null;
        
        list = new JsonArray();
        for(Enemy h : enemies) { list.add(h.toJson(details, col, city, excludeSecrets)); }
        json.put("enemies", list);
        
        list = new JsonArray();
        for(Product p : getDebries()) { list.add(p.toJson()); }
        json.put("debries", list);
		
        return json;
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
	public BigInteger getCheckerValue() {
		BigInteger res = new BigInteger(String.valueOf(getKey()));
		for(int idx=0; idx<getName().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getName().charAt(idx)))); }
		for(Enemy   e : getEnemies()) { res = res.add(e.getCheckerValue().multiply(Constants.BIGINTEGER_13)); }
		for(Product e : getDebries()) { res = res.add(e.getCheckerValue().multiply(Constants.BIGINTEGER_19)); }
		return res;
	}

	@Override
	public boolean isMarkedAsRefresh() { return false; }

	@Override
	public void markAsRefresh(boolean f) { }

	@Override
	public void markAsRefreshChildren(boolean f) { }

	@Override
	public void dispose() {
		enemies.clear();
		debries.clear();
	}
	
	/** 적과 보상이 모두 남아있지 않으면 true 반환 */
	public boolean isEmpty() {
		return ( getDebries().isEmpty() && getEnemies().isEmpty() );
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
	
	@Override
	public long getKey() {
		return key;
	}
	
	public void setKey(long k) {
		this.key = k;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public List<Enemy> getEnemies() {
		return enemies;
	}

	public void setEnemies(List<Enemy> enemies) {
		this.enemies = enemies;
	}

	@Override
	public List<Product> getDebries() {
		return debries;
	}

	public void setDebries(List<Product> debries) {
		this.debries = debries;
	}

	@Override
	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	@Override
	public String getTooltip() { return ""; }

	@Override
	public int getHp() { return 1; }

	@Override
	public int getMaxHp() { return 1; }

	@Override
	public void setHp(int hp) { }

	@Override
	public void addHp(int amount) { }

	@Override
	public short getDefenceType() { return 0; }

	@Override
	public int getDefencePoint() { return 0; }

	@Override
	public void oneCycle(int cycle, ColonyElements stage, Colony colony, int efficiency100, ColonyPanel colPanel) {
		// 함선의 공격
		for(Ship s : colony.getShips()) {
        	if(s.getHp() <= 0) continue;
        	if(! (getX() == s.getX() && getY() == s.getY() && getZ() == s.getZ())) continue;
        	if(cycle % s.cycleGap(colony) == 0) s.oneCycle(cycle, this, colony, efficiency100, colPanel);
        }
		
		// 적의 공격
		List<Enemy> enemies = getEnemies();
		for(Enemy e : enemies) {
			if(e.getHp() <= 0) continue;
			if(cycle % e.cycleGap(colony) == 0) e.oneCycle(cycle, this, colony, efficiency100, colPanel);
		}
		
		int enemiesLeft = 0;
		for(Enemy e : enemies) { if(e.getHp() >= 1) enemiesLeft++; }
		
		// 적이 없으면...
		if(enemiesLeft <= 0) {
			enemies.clear();
			
			// 보상 수거
			for(Ship s : colony.getShips()) {
				if(s.getHp() <= 0) continue;
	        	if(! (getX() == s.getX() && getY() == s.getY() && getZ() == s.getZ())) continue;
	        	
	        	int lefts = s.getMaxStoredCapacity() - s.getStoredCount();
	        	while(lefts >= 1 && debries.size() >= 1) {
	        		Product debrieOne = debries.get(0);
	        		debries.remove(0);
	        		s.store(debrieOne);
	        	}
			}
		}
	}

	@Override
	public int cycleGap(Colony colony) { return 99999; }
	
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
	
	/** 랜덤 천체 생성 */
	public static Celestials createRandom(long stdx, long stdy, long stdz, int minDist, int maxDist, int grade) {
		Celestials c = new DefaultCelestials();
		Map<String, Number> coordinate = DataUtil.createCoordinateIntScale(stdx, stdy, stdz, minDist, maxDist);
		c.setX(coordinate.get("x").longValue());
		c.setY(coordinate.get("y").longValue());
		c.setZ(coordinate.get("z").longValue());
		
		return c;
	}
}
