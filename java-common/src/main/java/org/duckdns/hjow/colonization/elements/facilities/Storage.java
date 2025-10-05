package org.duckdns.hjow.colonization.elements.facilities;

import java.util.List;

import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.elements.products.Product;

public interface Storage extends Facility {
    public List<Product> getStored();
    public Product takeOut(String type);
    public void store(Product p);
    public int getStoredCount();
    public int getMaxStoredCapacity();
    public boolean isStoreAvail(Product p);
}
