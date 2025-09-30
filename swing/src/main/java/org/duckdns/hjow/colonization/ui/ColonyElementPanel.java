package org.duckdns.hjow.colonization.ui;

import java.awt.Component;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.City;
import org.duckdns.hjow.colonization.elements.Colony;

public interface ColonyElementPanel {
    public void setEditable(boolean editable);
    public void refresh(int cycle, City city, Colony colony, ColonyManager superInstance);
    public void dispose();
    public String getTargetName();
    public Component getComponent();
}
