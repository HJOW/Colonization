package org.duckdns.hjow.colonization.elements.states;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.ui.ColonyPanel;

/** 시민, 혹은 시설의 상태 */
public interface State extends ColonyElements {
	public long getLefts();	
    public String getTitle();
    public long getDefaultLefts();
    /** 1 사이클마다 쓰레드에서 호출됨. */
    public void oneCycle(int cycle, ColonyElements hosts, City city, Colony colony, ColonyPanel colPanel);
}
