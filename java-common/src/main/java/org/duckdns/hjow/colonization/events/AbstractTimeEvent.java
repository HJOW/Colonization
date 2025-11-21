package org.duckdns.hjow.colonization.events;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.ui.ColonyPanel;

/** 랜덤 발생 이벤트 공통 부분을 정의하는 상위 클래스 */
public abstract class AbstractTimeEvent extends TimeEvent {
	private static final long serialVersionUID = -755790781368884839L;
	/** 이벤트 처리 */
    public void onEventOccured(ColonyElements target, Colony col, City city, ColonyPanel colPanel) {
        ColonyManager.logGlobals(getTitle() + " " + ColonyManager.t("발생") + " !");
    }
    
    public static final short EVENTSIZE_COLONY   = 9;
    public static final short EVENTSIZE_CITY     = 8;
    public static final short EVENTSIZE_FACILITY = 7;
}
