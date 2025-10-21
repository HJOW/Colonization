package org.duckdns.hjow.colonization;

import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;

/** ColonyManager 인터페이스, Mod 새로고침 호출 등에 사용 */
public interface ColonyManagerInterface extends Disposeable {
	public void exit();
	public ColonyManagerConfig getConfig();
	public void reserveRefresh();
	public void log(String msg);
	public void alert(String msg);
	public void pauseSimulation();
	public void resumeSimulation(int cycleCount);
	public JsonObject getSelectColonyInfo();
	public JsonArray getAllColonies();
}
