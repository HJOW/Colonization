package org.duckdns.hjow.colonization.elements.facilities;

import java.math.BigInteger;
import java.util.Vector;

import org.duckdns.hjow.colonization.ColonyClassLoader;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.constants.Constants;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.ship.Ship;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;

/** 우주공항 상위 클래스 */
public abstract class Port extends DefaultFacility {
	private static final long serialVersionUID = -1456423038829036816L;
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
                        Class<?> shipClass = ColonyClassLoader.getShipClass(type);
                        
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
    public JsonObject toJson(boolean details, Colony col, City city) {
        JsonObject json = new JsonObject();
        json.putAll(super.toJson(details, col, city));
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

    /** 격납 중인 함선들 반환 */
	public Vector<Ship> getShips() {
		return ships;
	}

	public void setShips(Vector<Ship> ships) {
		this.ships = ships;
	}
}
