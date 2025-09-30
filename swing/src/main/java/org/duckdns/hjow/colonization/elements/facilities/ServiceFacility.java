package org.duckdns.hjow.colonization.elements.facilities;

import org.duckdns.hjow.colonization.elements.City;
import org.duckdns.hjow.colonization.elements.Colony;

public interface ServiceFacility {
    public double additionalComportGradeRate(City city, Colony colony);
    public int getComportGrade();
}
