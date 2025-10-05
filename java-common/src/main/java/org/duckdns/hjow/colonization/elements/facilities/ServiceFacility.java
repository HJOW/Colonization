package org.duckdns.hjow.colonization.elements.facilities;

import java.util.List;

import org.duckdns.hjow.colonization.elements.City;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.Facility;

public interface ServiceFacility extends Facility, Storage {
    public double additionalComportGradeRate(City city, Colony colony);
    public int getComportGrade();
    public List<String> getProductTypeNeeded();
}
