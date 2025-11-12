package org.duckdns.hjow.colonization.elements.ship;

import java.math.BigInteger;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.ui.ColonyPanel;
import org.duckdns.hjow.commons.json.JsonObject;

/** 함선 - 공통 구현 파트 */
public class AbstractShip implements Ship {
	private static final long serialVersionUID = 1415044038948566331L;
	protected volatile long key = ColonyManager.generateKey();
	protected String name = ColonyManager.t("함선") + "_" + ColonyManager.getNaturalNumberFrom(key);
	protected int hp = getMaxHp();
	
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public int cycleGap(Colony colony) {
		return 60;
	}

	@Override
	public void fromJson(JsonObject json) {
		setName(json.get("name").toString());
		key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
	}

	@Override
	public JsonObject toJson() {
		return toJson(false, null, null);
	}

	@Override
	public JsonObject toJson(boolean details, Colony col, City city) {
		JsonObject json = new JsonObject();
        json.put("type", "Citizen");
        json.put("name", getName());
        json.put("key", String.valueOf(getKey()));
        
        json.put("hp"                , new Integer(getHp()));
        
        return json;
	}

	@Override
	public BigInteger getCheckerValue() {
		// TODO Auto-generated method stub
		return null;
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

}
