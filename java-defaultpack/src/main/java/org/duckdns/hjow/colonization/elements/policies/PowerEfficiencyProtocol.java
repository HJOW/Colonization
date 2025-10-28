package org.duckdns.hjow.colonization.elements.policies;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.facilities.PowerPlant;
import org.duckdns.hjow.colonization.elements.policy.Policy;
import org.duckdns.hjow.colonization.ui.ColonyPanel;

/** 발전 고효율 촉매 사용 */
public class PowerEfficiencyProtocol extends Policy {
	private static final long serialVersionUID = 1357547542463465636L;

	@Override
	public String getTitle() {
		return ColonyManager.t("발전 고효율 촉매");
	}

	@Override
	public void oneCycle(int cycle, City city, Colony colony, int efficiency100, ColonyPanel colPanel) { }

	@Override
	public long getMonthlyFee(Colony col, City ct) {
        long fee = 100000L;
        for(Facility f : ct.getFacility()) {
        	if(f instanceof PowerPlant) {
        		PowerPlant p = (PowerPlant) f;
        		fee += p.getMaintainFee(ct, col) / 100;
        	}
        }
		return fee;
	}

	@Override
	public double getPowerSupplyRate() { return 1.3; }
}
