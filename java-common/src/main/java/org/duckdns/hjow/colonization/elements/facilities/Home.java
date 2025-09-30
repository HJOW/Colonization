package org.duckdns.hjow.colonization.elements.facilities;

import java.util.List;

import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.City;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.Facility;

public interface Home extends Facility, ServiceFacility {
    public List<Citizen> getCitizens(City city, Colony colony);
    public boolean isFull(City city, Colony colony);
}
