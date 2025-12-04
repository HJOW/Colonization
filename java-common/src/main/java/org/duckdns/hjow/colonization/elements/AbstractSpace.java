package org.duckdns.hjow.colonization.elements;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.duckdns.hjow.colonization.ColonyClassManager;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.ColonyManagerInterface;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.elements.celestials.Celestials;
import org.duckdns.hjow.colonization.elements.celestials.DefaultCelestials;
import org.duckdns.hjow.colonization.elements.celestials.SoFarCelestials;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.custom.CustomElement;
import org.duckdns.hjow.colonization.elements.enemies.AbstractEnemy;
import org.duckdns.hjow.colonization.elements.enemies.Enemy;
import org.duckdns.hjow.colonization.ui.ColonyPanel;
import org.duckdns.hjow.commons.exception.KnownRuntimeException;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.ui.graphics.Coordinate3D;
import org.duckdns.hjow.commons.util.SecurityUtil;

/** 우주 */ // TODO : 정착지에 구현된 개념들 일부가 우주로 이관될 예정
public abstract class AbstractSpace implements Space {
	private static final long serialVersionUID = 2399047051505848540L;
	protected volatile long key = ColonyManager.generateKey();
    protected transient boolean fNeedRefresh = true;
    
    protected List<Colony>     colonies   = new ArrayList<Colony>();
    protected List<Enemy>      enemies    = new ArrayList<Enemy>();
    protected List<Celestials> celestials = new ArrayList<Celestials>();
    protected long userColonyKey = 0L;
    
    protected volatile BigInteger time   = BigInteger.ZERO;
    protected transient String originalFileName;
    protected transient boolean checked = false;
    protected transient String clientVersion = ColonyManager.getVersionString();
    protected transient String clientBuildNo = String.valueOf(ColonyManager.BUILD_NO);
    
    public AbstractSpace() {}

	public List<Colony> getColonies() {
		return colonies;
	}

	public void setColonies(List<Colony> colonies) {
		this.colonies = colonies;
	}

	public List<Enemy> getEnemies() {
		return enemies;
	}

	public void setEnemies(List<Enemy> enemies) {
		this.enemies = enemies;
	}

	public List<Celestials> getCelestials() {
		return celestials;
	}

	public void setCelestials(List<Celestials> celestials) {
		this.celestials = celestials;
	}

	public long getUserColonyKey() {
		return userColonyKey;
	}

	public void setUserColonyKey(long userColonyKey) {
		this.userColonyKey = userColonyKey;
	}

	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public BigInteger getTime() {
		return time;
	}

	public void setTime(BigInteger time) {
		this.time = time;
	}

	@Override
	public void fromJson(JsonObject json) {
		if(! ("Space".equals(json.get("type")) || getType().equals(json.get("type")))) throw new KnownRuntimeException("This object is not Space type.");
		key = Long.parseLong(json.get("key").toString());
		try { setTime(new BigInteger(json.get("time").toString()));                   } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); time       = BigInteger.ZERO; }
		try { clientVersion = json.get("version").toString();                         } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); clientVersion = "0.0.1"; } 
        try { clientBuildNo = json.get("buildNo").toString();                         } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); clientBuildNo = "1"; }
        try { setUserColonyKey(new Long(json.get("userColonyKey").toString()));       } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); userColonyKey = 0L; }
		
        JsonArray list = null;
        try { list = (JsonArray) json.get("colonies"); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        colonies.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        Colony col = ColonyClassManager.loadColony((JsonObject) o);
                        colonies.add(col);
                    } catch(Exception ex) {
                        GlobalLogs.processExceptionOccured(ex, false);
                    }
                }
            }
        }
        
        list = null;
        try { list = (JsonArray) json.get("celestials"); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        celestials.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        Celestials cele = new DefaultCelestials();
                        cele.fromJson((JsonObject) o);
                        celestials.add(cele);
                    } catch(Exception ex) {
                        GlobalLogs.processExceptionOccured(ex, false);
                    }
                }
            }
        }
        
        list = null;
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
	}
	
	@Override
	public JsonObject toJson() {
		return toJson(true, false);
	}

	public JsonObject toJson(boolean details, boolean excludeSecrets) {
		JsonObject json = new JsonObject();
        json.put("type", getType());
        json.put("key", String.valueOf(getKey()));
        json.put("time", getTime().toString());
        json.put("version", getClientVersion());
        json.put("buildNo", clientBuildNo);
        json.put("userColonyKey", String.valueOf(getUserColonyKey()));
        
        JsonArray list = null;
        
        list = new JsonArray();
        for(Colony col : getColonies()) { list.add(col.toJson(details, col, null, excludeSecrets)); }
        json.put("colonies", list);
        
        list = new JsonArray();
        for(Celestials c : getCelestials()) { if((! excludeSecrets) || c.isOpened())  list.add(c.toJson(details, null, null, excludeSecrets)); }
        json.put("celestials", list);
        
        list = new JsonArray();
        for(Enemy h : enemies) { list.add(h.toJson(details, null, null, excludeSecrets)); }
        json.put("enemies", list);
        
        return json;
	}

	@Override
	public Object cloneThis() {
		try {
			Class<? extends AbstractSpace> thisClass = getClass();
			AbstractSpace newInst = thisClass.newInstance();
			newInst.fromJson(toJson(true, false));
			return newInst;
		} catch(Exception ex) {
		    throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public String getType() {
		return "Space";
	}

	@Override
	public Colony getYourColony() {
		for(Colony col : getColonies()) {
			if(col.getKey() == userColonyKey) return col;
		}
		return null;
	}

	@Override
	public void addEnemy(Enemy en) {
		if(contains(en)) return;
    	if(en.getHp() <= 0) return;
    	enemies.add(en);
	}

	@Override
	public boolean contains(Enemy en) {
		return (enemies.contains(en));
	}

	/** 주변 천체 목록 랜덤화 (단, 천체 목록이 이미 생성된 경우 아무 동작하지 않음) */
    @Override
    public void randomizeCelestials() {
    	if(! celestials.isEmpty()) return;
    	Celestials newOne;
    	
    	Coordinate3D centers;
    	
    	Colony yours = getYourColony();
    	if(yours == null) {
    		centers = new Coordinate3D((long) ((Integer.MAX_VALUE / 10) * Math.random()), (long) ((Integer.MAX_VALUE / 10) * Math.random()), (long) ((Integer.MAX_VALUE / 10) * Math.random()));
    	} else {
    		centers = yours.getCoordinate();
    	}
    	
    	Random rand = new Random();
		int intRand = ((int) (Math.abs(rand.nextInt())) / (Integer.MAX_VALUE / 1000)) + 1000;
		int grade = 1;
		int idx=0;
		
	    for(idx=0; idx<intRand; idx++) {
	    	newOne = DefaultCelestials.createRandom(centers.getX(), centers.getY(), centers.getZ(), 10000, (int) (100000 + (Math.random() * idx)), grade + (Math.random() >= 0.5 ? 1 : 0) + (Math.random() >= 0.8 ? 1 : 0) );
	    	if(idx % 100 == 0) grade++;
	    	celestials.add(newOne);
	    }
	    
	    intRand = ((int) (Math.abs(rand.nextInt())) / (Integer.MAX_VALUE / 1000)) + 1000;
	    for(idx=0; idx<intRand; idx++) {
	    	newOne = SoFarCelestials.createRandom(centers.getX(), centers.getY(), centers.getZ());
	    	celestials.add(newOne);
	    }
    }

	@Override
	public String getCheckerSerial() {
        if(! checked) return "";
        
        StringBuilder res = new StringBuilder(String.valueOf(getCheckerValue()));
        // TODO
        
        String serial = res.toString().trim();
        res = null;
        
        return SecurityUtil.hash(serial, "SHA-256");
	}
	
	@Override
    public BigInteger getCheckerValue() {
		BigInteger res = new BigInteger(String.valueOf(getKey()));
		for(Colony     col : getColonies()  ) { res = res.add(col.getCheckerValue().multiply(ColonyManager.getCheckerConst(getClientBuildNo()))); if(col instanceof CustomElement) res = BigInteger.ZERO; }
		for(Celestials c   : getCelestials()) { res = res.add(c.getCheckerValue().multiply(  ColonyManager.getCheckerConst(getClientBuildNo()))); if(c   instanceof CustomElement) res = BigInteger.ZERO; }
		for(Enemy      e   : getEnemies()   ) { res = res.add(e.getCheckerValue()); if(e instanceof CustomElement) res = BigInteger.ZERO; }
		return res;
	}

	/** 인증 제거 (인증 제거 사유 발생 시 호출) */
    @Override
    public void disableChecked() {
        checked = false;
    }

	@Override
	public boolean isCheckEnabled() {
		return checked;
	}

	@Override
	public String getClientVersion() {
		return clientVersion;
	}

	@Override
	public long getClientBuildNo() {
		return new Long(clientBuildNo);
	}

	@Override
	public void resetClientVersion(ColonyManagerInterface man) {
		if(man == null) throw new NullPointerException();
    	clientVersion = ColonyManager.getVersionString();
        clientBuildNo = String.valueOf(ColonyManager.BUILD_NO);
	}
	
	@Override
	public void oneCycle(int cycle, ColonyElements stage, Space space, int efficiency100, ColonyPanel colPanel) {
		
		// 적 - 정착지, 도시에 위치한 경우 도시에 등록, 사이클은 도시 oneCycle 에서 처리
        for(Enemy en : getEnemies()) {
        	// 이동 처리
        	en.processMove(cycle);
        	
        	for(Colony col : getColonies()) {
        		// 도시와 좌표가 동일한 경우, 정착지에 등록 (도시에 등록하는 부분은 정착지 쪽 oneCycle 에서 처리)
            	for(final City c : col.getCities()) {
            		if(en.getX() == c.getX() && en.getY() == c.getY() && en.getZ() == c.getZ()) { col.addEnemy(en); }
            	}
        	}
        }
        
        // 정착지
        for(Colony col : getColonies()) {
        	col.oneCycle(cycle, col, col, efficiency100, colPanel);
        }
		
		// 시간 지남
        time = time.add(BigInteger.ONE);
	}
	
	@Override
	public void dispose() {
		for(Colony col : getColonies()) { col.dispose(); }
		colonies.clear();
		
		for(Celestials cele : getCelestials()) { cele.dispose(); }
		celestials.clear();
		
		for(Enemy en : getEnemies()) { en.dispose(); }
		enemies.clear();
		
		userColonyKey = 0L;
	}
}
