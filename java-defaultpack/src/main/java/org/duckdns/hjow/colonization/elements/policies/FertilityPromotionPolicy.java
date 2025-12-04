package org.duckdns.hjow.colonization.elements.policies;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.elements.Space;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.policy.AbstractPolicy;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.elements.research.humanities.BasicHumanities;
import org.duckdns.hjow.colonization.ui.ColonyPanel;

/** 출산 장려 정책 */
public class FertilityPromotionPolicy extends AbstractPolicy {
    private static final long serialVersionUID = -7883592808138695130L;

    @Override
    public String getTitle() {
        return ColonyManager.t("출산 장려 정책");
    }
    
    @Override
    public void oneCycle(int cycle, ColonyElements stage, Space space, Colony colony, int efficiency100, ColonyPanel colPanel) { }

    @Override
    public long getMonthlyFee(Colony col, City ct) {
        long fee = 100000L;
        int citizens = ct.getCitizenCount();
        fee += (5 * citizens);
        return fee;
    }
    
    @Override
    public boolean isAvail(Colony col, City ct) {
        for(Research r : col.getResearches()) {
            if((r instanceof BasicHumanities) && r.getLevel() >= 5) return true;
        }
        return false; 
    }

    @Override
    public double getPowerSupplyRate(Colony col, City ct) { return 1.0; }
    
    @Override
    public double getTransSupplyRate(Colony col, City ct) { return 1.0; }
    
    @Override
    public double getNetworkSupplyRate(Colony col, City ct) { return 1.0; }
    
    @Override
    public double getFacilityBonusRate(Colony col, City ct, Facility f) { return 1.0; }
    
    @Override
    public double getBirthBonusRate(Colony col, City ct) {
        double rate = 1.3;
        
        for(Research r : col.getResearches()) {
            if(r instanceof BasicHumanities) rate += (0.0025 * r.getLevel());
        }
        
        return rate;
    }
}
