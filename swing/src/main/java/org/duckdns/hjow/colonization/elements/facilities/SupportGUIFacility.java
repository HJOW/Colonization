package org.duckdns.hjow.colonization.elements.facilities;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.City;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.ui.FacilityPanel;

public interface SupportGUIFacility extends Facility {
    public FacilityPanel createPanel(City city, Colony colony, ColonyManager superInstance);
    public boolean checkPanelAccept(FacilityPanel pn);
}
