package org.duckdns.hjow.colonization.events;

import java.io.Serializable;

import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.Space;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.ui.ColonyPanel;

/** 랜덤 발생 이벤트 */
public abstract class TimeEvent implements Serializable {
	private static final long serialVersionUID = -8101310052495079728L;
	/** 이벤트 규모 */
    public abstract short  getEventSize();
    
    /** 이벤트 발생 타이밍 사이클 (이 때 이벤트 발생률에 따라 발생) */
    public abstract int    getOccurCycle(Colony col, City city);
    
    /** 이벤트가 발생할 최소 타이밍 (이 이상 시간이 지나야 발생) */
    public abstract long   getOccurMinimumTime(Colony col);
    
    /** 이벤트 발생률 */
    public abstract double getOccurRate(ColonyElements target, Space space, Colony col, City city);
    
    /** 이벤트 처리 */
    public abstract void onEventOccured(ColonyElements target, Space space, Colony col, City city, ColonyPanel colPanel);
    
    /** 이벤트 명칭 반환 */
    public abstract String getTitle();
    
    public static final short EVENTSIZE_COLONY   = 9;
    public static final short EVENTSIZE_CITY     = 8;
    public static final short EVENTSIZE_FACILITY = 7;
}
