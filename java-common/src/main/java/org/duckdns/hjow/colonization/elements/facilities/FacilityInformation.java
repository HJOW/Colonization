package org.duckdns.hjow.colonization.elements.facilities;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.commons.json.JsonObject;

/** 시설 정보 */
public class FacilityInformation implements Serializable {
    private static final long serialVersionUID = -5378970571423008845L;
    protected String name, description, title;
    protected Object image;
    protected Long price = new Long(0L);
    protected Long tech  = new Long(0L);
    protected int buildingCycle = 1200;
    protected int uniqueGrade = DefaultFacility.FACILITY_UNIQUE_GRADE_NONE;
    protected Class<?> facilityClass;
    public FacilityInformation() {}
    public FacilityInformation(Class<?> facilityClass) {
    	this();
        setFacilityClass(facilityClass);
        try {
            Method method = facilityClass.getMethod("getFacilityName");
            setName((String) method.invoke(null));
            
            method = facilityClass.getMethod("getFacilityTitle");
            setTitle((String) method.invoke(null));
            
            method = facilityClass.getMethod("getFacilityDescription");
            setDescription((String) method.invoke(null));
            
            method = facilityClass.getMethod("getFacilityPrice");
            setPrice((Long) method.invoke(null));
            
            method = facilityClass.getMethod("getTechNeeded");
            setTech((Long) method.invoke(null));
            
            method = facilityClass.getMethod("getFacilityBuildingCycle");
            setBuildingCycle((Integer) method.invoke(null));
            
            method = facilityClass.getMethod("getUniqueFacilityGrade");
            setUniqueGrade((Integer) method.invoke(null));
            
            method = facilityClass.getMethod("getImage");
            Object obj = method.invoke(null);
            if(obj != null) setImage(obj);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /** JSON 정보 생성 */
    public JsonObject toJson() {
    	JsonObject json = new JsonObject();
    	
    	json.put("name", getName());
    	json.put("title", getTitle());
    	json.put("desc", getDescription());
    	json.put("class", getFacilityClass().getName());
    	json.put("price", String.valueOf(getPrice()));
    	json.put("buildingCycle", new Integer(getBuildingCycle()));
    	json.put("tech", String.valueOf(getTech()));
    	json.put("imagehex", "");
    	if(getImage() != null) {
    		if(getImage() instanceof CharSequence) json.put("imagehex", getImage().toString());
    	}
    	
    	return json;
    }
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
    public Object getImage() {
        return image;
    }
    public void setImage(Object image) {
        this.image = image;
    }
    @Override
    public String toString() {
        return getTitle();
    }
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public String isBuildAvail(Colony col, City city) {
        try {
            Method mthd = facilityClass.getMethod("isBuildAvail", Colony.class, City.class);
            String reason = (String) mthd.invoke(null, col, city);
            if(reason != null) return reason;
            
            int uniqGrade = getUniqueGrade();
            if(uniqGrade == DefaultFacility.FACILITY_UNIQUE_GRADE_NONE) return null;
            
            if(uniqGrade == DefaultFacility.FACILITY_UNIQUE_GRADE_CITY) {
            	if(! city.getFacilities(getFacilityClass()).isEmpty()) return ColonyManager.t("도시 당 하나만 건설할 수 있는 시설입니다.");
            } else if(uniqGrade == DefaultFacility.FACILITY_UNIQUE_GRADE_COLONY) {
            	if(! col.getFacilities(getFacilityClass()).isEmpty()) return ColonyManager.t("정착지 당 하나만 건설할 수 있는 시설입니다.");
            }
            
            return null;
        } catch(NoSuchMethodException ex) {
            GlobalLogs.processExceptionOccured(ex, false);
            return null;
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    /** 이 시설이 차지하는 공간 크기 반환 */
    public int getSpaceSize() {
    	try { return ((Facility) facilityClass.newInstance()).getSpaceSize(); } catch(Exception ex) { throw new RuntimeException(ex.getMessage(), ex); }
    }
	public int getUniqueGrade() {
		return uniqueGrade;
	}
	public void setUniqueGrade(int uniqueGrade) {
		this.uniqueGrade = uniqueGrade;
	}
}
