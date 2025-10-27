package org.duckdns.hjow.colonization.elements.policy;

import java.math.BigInteger;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.DataUtil;

/** 정책 개념 */
public abstract class Policy implements ColonyElements {
	private static final long serialVersionUID = 371358482693283220L;
	protected volatile long key = ColonyManager.generateKey();
	protected volatile boolean enabled = false;

	@Override
	public void dispose() { }

	@Override
	public long getKey() {
		return key;
	}
	
	public void setKey(long key) {
		this.key = key;
	}

	@Override
	public String getClassName() {
		return getClass().getSimpleName();
	}

	@Override
	public int getHp() {
		return 1;
	}

	@Override
	public int getMaxHp() {
		return 1;
	}

	@Override
	public void setHp(int hp) {}

	@Override
	public void addHp(int amount) {}

	@Override
	public short getDefenceType() { return 0; }

	@Override
	public int getDefencePoint() { return 0; }

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/** 활성화 가능여부 반환 */
	public boolean isAvail(Colony col, City ct) { return true; }

	/** 월간 비용 반환 */
	public abstract long getMonthlyFee();

	@Override
	public int cycleGap(Colony colony) {
		return 60;
	}

	@Override
	public void fromJson(JsonObject json) {
		setKey(Long.parseLong(json.get("key").toString()));
		setEnabled(DataUtil.parseBoolean(json.get("enabled").toString()));
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.put("type", getClassName());
		json.put("key", String.valueOf(getKey()));
		json.put("enabled", String.valueOf(isEnabled()));
		return json;
	}

	@Override
	public JsonObject toJson(boolean details, Colony col, City city) {
		return toJson();
	}

	@Override
	public BigInteger getCheckerValue() {
		return new BigInteger(String.valueOf(getKey()));
	}

	@Override
	public boolean isMarkedAsRefresh() {
		return false;
	}

	@Override
	public void markAsRefresh(boolean f) {}

	@Override
	public void markAsRefreshChildren(boolean f) { }
}
