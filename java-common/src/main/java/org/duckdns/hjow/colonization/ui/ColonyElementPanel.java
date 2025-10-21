package org.duckdns.hjow.colonization.ui;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;

public interface ColonyElementPanel {
    public void setEditable(boolean editable);
    public void refresh(int cycle, City city, Colony colony, ColonyManager superInstance);
    public void dispose();
    public String getTargetName();
    public Object getComponent();
}
