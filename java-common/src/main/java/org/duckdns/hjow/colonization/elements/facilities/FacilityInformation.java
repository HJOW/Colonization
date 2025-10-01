package org.duckdns.hjow.colonization.elements.facilities;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.elements.City;
import org.duckdns.hjow.colonization.elements.Colony;

/** 시설 정보 */
public class FacilityInformation implements Serializable {
    private static final long serialVersionUID = -5378970571423008845L;
    protected String name, description, title;
    protected String imageHex;
    protected Long price = new Long(0L);
    protected Long tech  = new Long(0L);
    protected int buildingCycle = 1200;
    protected Class<?> facilityClass;
    public FacilityInformation() {}
    public String getName() {
        return name;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public Class<?> getFacilityClass() {
        return facilityClass;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setFacilityClass(Class<?> facilityClass) {
        this.facilityClass = facilityClass;
    }
    public Long getPrice() {
        return price;
    }
    public int getBuildingCycle() {
        return buildingCycle;
    }
    public void setPrice(Long price) {
        this.price = price;
    }
    public void setBuildingCycle(int buildingCycle) {
        this.buildingCycle = buildingCycle;
    }
    public Long getTech() {
        return tech;
    }
    public void setTech(Long tech) {
        this.tech = tech;
    }
    public String getImageHex() {
        return imageHex;
    }
    public void setImageHex(String imageHex) {
        this.imageHex = imageHex;
    }
    @Override
    public String toString() {
        return getTitle();
    }
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public String isBuildAvail(Colony col, City city) {
        try {
            Method mthd = facilityClass.getMethod("isBuildAvail", Colony.class, City.class);
            return (String) mthd.invoke(null, col, city);
        } catch(NoSuchMethodException ex) {
            GlobalLogs.processExceptionOccured(ex, false);
            return null;
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
