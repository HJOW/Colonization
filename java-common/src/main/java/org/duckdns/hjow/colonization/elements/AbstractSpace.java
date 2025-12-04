package org.duckdns.hjow.colonization.elements;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

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
import org.duckdns.hjow.colonization.elements.facilities.Port;
import org.duckdns.hjow.colonization.elements.ship.Ship;
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
        try { list = (JsonArray) json.get("celestials"); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); ex.printStackTrace(); }
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
        return toJson(false);
    }
    
    @Override
    public JsonObject toJson(boolean excludeColonies) {
        return toJson(excludeColonies, true, false);
    }

    @Override
    public JsonObject toJson(boolean excludeColonies, boolean details, boolean excludeSecrets) {
        JsonObject json = new JsonObject();
        json.put("type", getType());
        json.put("key", String.valueOf(getKey()));
        json.put("time", getTime().toString());
        json.put("version", getClientVersion());
        json.put("buildNo", clientBuildNo);
        json.put("userColonyKey", String.valueOf(getUserColonyKey()));
        
        JsonArray list = null;
        
        list = new JsonArray();
        if(! excludeColonies) {
            for(Colony col : getColonies()) { list.add(col.toJson(details, col, null, excludeSecrets)); }
        }
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
            newInst.fromJson(toJson(false, true, false));
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
    
    @Override
    public void addColony(Colony col) {
        if(contains(col)) return;
        colonies.add(col);
    }
    
    @Override
    public boolean contains(Colony col) {
        return (colonies.contains(col));
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
    
    /** 해당 함선의 소속 항구 찾기 */
    @Override
    public Port findPort(Ship ship) {
        for(Colony col : getColonies()) {
            for(City ct : col.getCities()) {
                for(Facility f : ct.getFacility()) {
                    if(f instanceof Port) {
                        Port p = (Port) f;
                        for(Ship s : p.getShips()) {
                            if(s.getKey() == ship.getKey()) return p;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /** 해당 함선의 소속 도시 찾기 */
    @Override
    public City findCity(Ship ship) {
        for(Colony col : getColonies()) {
            for(City ct : col.getCities()) {
                for(Facility f : ct.getFacility()) {
                    if(f instanceof Port) {
                        Port p = (Port) f;
                        for(Ship s : p.getShips()) {
                            if(s.getKey() == ship.getKey()) return ct;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public Colony findColony(Ship ship) {
    	for(Colony col : getColonies()) {
            for(City ct : col.getCities()) {
                for(Facility f : ct.getFacility()) {
                    if(f instanceof Port) {
                        Port p = (Port) f;
                        for(Ship s : p.getShips()) {
                            if(s.getKey() == ship.getKey()) return col;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /** 도시 내 소속 함선들 반환 (말그대로 소속 함선으로, 실제 위치는 도시 내가 아닐수도 있음) - 건조 중인 함선 포함 */
    @Override
    public Vector<Ship> getShips() {
        Vector<Ship> list = new Vector<Ship>();
        for(Colony col : getColonies()) {
            for(City c : col.getCities()) {
                list.addAll(c.getShips());
            }
        }
        return list;
    }
    
    /** 도시 내 소속 함선들 반환 (말그대로 소속 함선으로, 실제 위치는 도시 내가 아닐수도 있음) - 건조 중인 함선 제외 */
    @Override
    public Vector<Ship> getShipsLive() {
        Vector<Ship> list = new Vector<Ship>();
        for(Colony col : getColonies()) {
            for(City c : col.getCities()) {
                list.addAll(c.getShipsLive());
            }
        }
        return list;
    }
    
    /** 해당 위치의 모든 함선들 반환 */
    @Override
    public Vector<Ship> getShips(long x, long y, long z) {
        Vector<Ship> list = new Vector<Ship>();
        for(Colony col : getColonies()) {
            for(City c : col.getCities()) {
                list.addAll(c.getShips(x, y, z));
            }
        }
        return list;
    }
    
    /** 해당 위치의 해당 범위 내 모든 함선들 반환 */
    @Override
    public Vector<Ship> getShips(long x, long y, long z, long dist) {
        Vector<Ship> list = new Vector<Ship>();
        for(Colony col : getColonies()) {
            for(City c : col.getCities()) {
                list.addAll(c.getShips(x, y, z, dist));
            }
        }
        return list;
    }
    
    /** 소속 함선 수 반환 - 건조 중인 함선 포함 */
    @Override
    public int getShipCount() {
        int res = 0;
        for(Colony col : getColonies()) {
            for(City c : col.getCities()) {
                res += c.getShipCount();
            }
        }
        return res;
    }
    
    /** 소속 함선 수 반환 - 건조 중인 함선 제외 */
    @Override
    public int getLiveShipCount() {
        int res = 0;
        for(Colony col : getColonies()) {
            for(City c : col.getCities()) {
                res += c.getLiveShipCount();
            }
        }
        return res;
    }
    
    @Override
    public Ship getShip(long key) {
        Ship sh;
        for(Colony col : getColonies()) {
            for(City city : col.getCities()) {
                sh = city.getShip(key);
                if(sh != null) return sh;
            }
        }
        return null;
    } 
    
    @Override
    public void removeShip(Ship ship) {
        Port p = findPort(ship);
        if(p == null) return;
        p.removeShip(ship);
        
        Colony col = findColony(ship);
        if(col == null) return;
        
        City city = p.getCity(col);
        if(city == null) return;
        city.removeShip(ship);
    }
    
    /** 우주공항 목록 반환 */
    public List<Port> getPorts() {
        List<Port> ports = new ArrayList<Port>();
        for(Colony col : getColonies()) {
            for(City c : col.getCities()) {
                for(Facility f : c.getFacility()) {
                    if(f.getHp() <= 0) continue;
                    if(f instanceof Port) ports.add((Port) f);
                }
            }
        }
        return ports;
    }
    
    /** 체력이 없는 객체 삭제 */
    protected void removeDeadObjects() {
    	int idx;
    	
    	// 체력이 없는 적 삭제
        idx = 0;
        while(idx < getEnemies().size()) {
            Enemy en = getEnemies().get(idx);
            if(en.getHp() <= 0) {
                en.dispose();
                getEnemies().remove(idx);
                ColonyManager.logGlobals(ColonyManager.t("적 [ENEMY] 파괴됨").replace("[ENEMY]", en.getName()), 1);
                continue;
            }
            idx++;
        }
        
        // 적과 보상이 남아있지 않은 천체 삭제
        idx = 0;
        while(idx < getCelestials().size()) {
            Celestials cele = getCelestials().get(idx);
            if(cele.isEmpty()) {
                cele.dispose();
                getCelestials().remove(idx);
                ColonyManager.logGlobals(ColonyManager.t("천체 [CELE] 에는 더 이상 갈 필요가 없음.").replace("[CELE]", cele.getName()), 1);
                continue;
            }
            idx++;
        }
    }
    
    @Override
    public void oneCycle(int cycle, ColonyPanel colPanel, Set<ColonyElements> excludes) {
    	int efficiency100 = 100;
    	
        // 체력이 없는 객체 삭제
        removeDeadObjects();
        
        // 적 - 정착지, 도시에 위치한 경우 도시에 등록, 사이클은 도시 oneCycle 에서 처리
        for(Enemy en : getEnemies()) {
        	if(excludes.contains(en)) continue;
        	
            // 이동 처리
            en.processMove(cycle);
            
            for(Colony col : getColonies()) {
                // 도시와 좌표가 동일한 경우, 도시에 등록
                for(final City c : col.getCities()) {
                    if(en.getX() == c.getX() && en.getY() == c.getY() && en.getZ() == c.getZ()) {
                        c.addEnemy(en);
                    }
                }
            }
        }
        
        // 정착지
        for(Colony col : getColonies()) {
        	if(excludes.contains(col)) continue;
        	
            if(cycle % col.cycleGap(col) == 0) {
                col.oneCycle(cycle, col, this, col, efficiency100, colPanel);
            }
        }
        
        // 탐험 진행
        for(Celestials c : getCelestials()) {
        	if(excludes.contains(c)) continue;
        	
            c.oneCycle(cycle, null, this, null, efficiency100, colPanel);
            
            // 함선들이 이 천체 근처에 하나라도 있으면 오픈
            if(! c.isOpened()) {
            	for(Colony colony : getColonies()) {
            		for(Ship s : colony.getShips()) {
                        if(Math.abs(s.getX() - c.getX()) <= 10 && Math.abs(s.getY() - c.getY()) <= 10 && Math.abs(s.getZ() - c.getZ()) <= 10) {
                            c.setOpened(true);
                            break;
                        }
                    }
            	}
            }
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
