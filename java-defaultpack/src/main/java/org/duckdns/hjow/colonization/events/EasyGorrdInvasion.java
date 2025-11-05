package org.duckdns.hjow.colonization.events;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.enemies.Goord;
import org.duckdns.hjow.colonization.ui.ColonyPanel;

public class EasyGorrdInvasion extends TimeEvent {
    private static final long serialVersionUID = -3467466002865179687L;

    @Override
    public short getEventSize() {
        return TimeEvent.EVENTSIZE_CITY;
    }

    @Override
    public int getOccurCycle(Colony col, City city) {
        return 3600;
    }
    
    @Override
    public long getOccurMinimumTime(Colony col) {
        return 1000L * 60 * 60 * 24 * 6;
    }
    
    @Override
    public double getOccurRate(ColonyElements target, Colony col, City city) {
        return 0.05;
    }
    
    @Override
    public void onEventOccured(ColonyElements target, Colony col, City city, ColonyPanel colPanel) {
        int floatCounts = 5;
        floatCounts = floatCounts + (int) Math.round(ColonyManager.random() * 5 * floatCounts);
        
        if(floatCounts >= 1) {
            for(int idx=0; idx<floatCounts; idx++) {
                city.getEnemies().add(new Goord());
            }
            ColonyManager.logGlobals(ColonyManager.t("고드 무리가 침략해 오고 있습니다."));
        }
    }

    @Override
    public String getTitle() {
        return ColonyManager.t("고드의 침략");
    }
}
