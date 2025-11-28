package org.duckdns.hjow.colonization.elements.facilities;

import java.math.BigInteger;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.duckdns.hjow.colonization.ColonyClassManager;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.constants.Constants;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.ship.Ship;
import org.duckdns.hjow.colonization.ui.ColonyPanel;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.DataUtil;

/** 우주공항 상위 클래스 */
public abstract class DefaultPort extends AbstractFacility implements Port {
    private static final long serialVersionUID = 7289781729220167L;
    protected Vector<Ship> ships = new Vector<Ship>();

    @Override
    public int getWorkerSuitability(Citizen citizen) {
        int point = 3;
        if(citizen.getCarisma()     >= 3) point += 1;
        if(citizen.getAgility()     >= 6) point += 2;
        if(citizen.getStrength()    >= 6) point += 2;
        if(citizen.getIntelligent() >= 6) point += 2;
        return point;
    }

    @Override
    protected String getDefaultNamePrefix() {
        return ColonyManager.t("우주공항");
    }

    @Override
    public void fromJson(JsonObject json) {
        super.fromJson(json);
        setName(json.get("name").toString());
        key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
        setLevel(Integer.parseInt(json.get("level").toString()));
        
        JsonArray list = (JsonArray) json.get("ships");
        ships.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        JsonObject jsonObj = (JsonObject) o;
                        Ship s = null;
                        
                        String type = jsonObj.get("type").toString();
                        Class<?> shipClass = ColonyClassManager.getShipClass(type);
                        
                        s = (Ship) shipClass.newInstance();
                        s.fromJson(jsonObj);
                        ships.add(s);
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
        json.putAll(super.toJson(details, col, city, excludeSecrets));
        json.put("type", getType());
        json.put("name", getName());
        json.put("key", String.valueOf(getKey()));
        json.put("hp", String.valueOf(getHp()));
        json.put("level", new Integer(getLevel()));
        
        JsonArray list = new JsonArray();
        for(Ship s : getShips()) { list.add(s.toJson()); }
        json.put("ships", list);
        
        return json;
    }
    
    @Override
    public BigInteger getCheckerValue() {
        BigInteger res = super.getCheckerValue();
        for(Ship p : getShips()) { res = res.add(p.getCheckerValue().multiply(Constants.BIGINTEGER_19)); }
        return res;
    }
    
    /** 소속 함선 수 반환 (건조 수 포함) */
    @Override
    public int getShipCount() {
        return ships.size();
    }
    
    /** 소속 함선 수 반환 (건조 수 제외) */
    @Override
    public int getLiveShipCount() {
        int res = 0;
        for(Ship s : getShips()) {
            if(s.getLevel() <= 0) continue;
            if(s.getHp()    <= 0) continue;
            res++;
        }
        return res;
    }
    
    /** 소속 함선들 반환 (격납 중인 함선이 아님 ! 파견되어 있더라도 소속이 이 곳이면 여전히 조회됨) - 건조 중인 함선 제외 */
    @Override
    public Vector<Ship> getShipsLive() {
        Vector<Ship> ships = new Vector<Ship>();
        for(Ship s : getShips()) {
            if(s.getLevel() <= 0) continue;
            if(s.getHp()    <= 0) continue;
            ships.add(s);
        }
        return ships;
    }
    
    /** 건조 중인 함선들 반환 */
    @Override
    public Vector<Ship> getShipsBuilding() {
        Vector<Ship> ships = new Vector<Ship>();
        for(Ship s : getShips()) {
            if(s.getHp()    <= 0) continue;
            if(s.getLevel() >= 1) continue;
            ships.add(s);
        }
        return ships;
    }

    /** 소속 함선들 반환 (격납 중인 함선이 아님 ! 파견되어 있더라도 소속이 이 곳이면 여전히 조회됨) - 건조 중인 함선 포함 */
    @Override
    public Vector<Ship> getShips() {
        return ships;
    }

    @Override
    public void setShips(Vector<Ship> ships) {
        this.ships = ships;
    }
    
    @Override
    public Port addShip(Ship s) {
        if(s.getSize() > leftShipSpaces()) {
            throw new RuntimeException(ColonyManager.t("이 우주공항에 정박할 공간이 부족합니다."));
        }
        ships.add(s);
        return this;
    }
    
    /** 건조 / 업그레이드 중인 함선 수 반환 */
    @Override
    public int getBuildingShipCount() {
        int now = 0;
        for(Ship s : getShips()) {
            if(s.getLeftProgress() >= 1) now++;
        }
        return now;
    }
    
    /** 함선 건조 / 업그레이드 라인 수 반환 */
    @Override
    public int getBuildingLineCount() {
        return 1;
    }
    
    /** 함선 격납공간 사용량 */
    @Override
    public int usingShipSpaces() {
        int now = 0;
        for(Ship s : getShips()) {
            now += s.getSize();
        }
        return now;
    }
    
    /** 함선 격납공간 남은 공간 */
    @Override
    public int leftShipSpaces() {
        int r = getCapacity() - usingShipSpaces();
        if(r < 0) r = 0;
        return r;
    }
    
    @Override
    public void oneCycle(int cycle, ColonyElements stage, Colony colony, int efficiency100, ColonyPanel colPanel) {
        super.oneCycle(cycle, stage, colony, efficiency100, colPanel);
        
        // 수명 다된 함선 제거
        int std = 0;
        while(std < getShips().size()) {
            Ship st = getShips().get(std);
            if(st.getHp() <= 0) {
                st.dispose();
                getShips().remove(std);
                continue;
            }
            std++;
        }
        
        // 적 공격 등은 도시 oneCycle 에서 따로 처리
    }
    
    @Override
    public void removeShip(Ship ship) {
        int idx = 0;
        while(idx < ships.size()) {
            if(ship.getKey() == ships.get(idx).getKey()) {
                ships.remove(idx);
                continue;
            }
            idx++;
        }
    }
    
    @Override
    protected String additionalDescribes(Colony col, City city) {
        StringBuilder res = new StringBuilder("우주공항");
        res = res.append("\n").append("    ").append("함선 수용량 : ").append(getCapacity()).append(" (").append("잔여 수용량 : ").append(leftShipSpaces()).append(")");
        
        List<Ship> ships = getShipsLive();
        if(! ships.isEmpty()) {
            res = res.append("\n").append("    ").append("이 우주공항 소속으로는 다음 함선들이 등록되어 있습니다.");
            for(Ship o : ships) {
                String desc = o.describeForAI(col, city);
                if(DataUtil.isEmpty(desc)) {
                    res = res.append("\n").append("        ").append("함선 \"" + o.getName() + "\" (상세정보를 조회할 수 없습니다.)");
                } else {
                    StringTokenizer lineTokenizer = new StringTokenizer(desc, "\n");
                    while(lineTokenizer.hasMoreTokens()) {
                        res = res.append("\n").append("        ").append(lineTokenizer.nextToken());
                    }
                }
            }
        }
        
        ships = getShipsBuilding();
        if(! ships.isEmpty()) {
            res = res.append("\n").append("    ").append("이 우주공항에서는 다음 함선들이 건조되고 있습니다.");
            for(Ship o : ships) {
            	res = res.append("\n").append("        ").append("함선 \"" + o.getName() + "\"").append(" (").append(o.getLeftProgress()).append(" / ").append(o.getMaxProgress(this, col)).append(")");
            }
        }
        
        return res.toString().trim();
    }
}
