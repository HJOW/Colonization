package org.duckdns.hjow.colonization.elements.facilities;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.commons.util.classwrapper.ClassWrapper;
import org.duckdns.hjow.commons.util.classwrapper.SimpleClassWrapper;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.DataUtil;

/** 시설 정보 */
public class DefaultFacilityInformation implements FacilityInformation {
    private static final long serialVersionUID = -5378970571423008845L;
    protected String name, description, title;
    protected Object image;
    protected Long price = new Long(0L);
    protected Long tech  = new Long(0L);
    protected int buildingCycle = 1200;
    protected int uniqueGrade = AbstractFacility.FACILITY_UNIQUE_GRADE_NONE;
    protected boolean scriptBasedFacility = false;
    protected ClassWrapper facilityClassWrapper;
    public DefaultFacilityInformation() {}
    public DefaultFacilityInformation(Class<?> facilityClass) {
        this();
        readClass(facilityClass);
    }
    
    /** 클래스에서 시설 정보 읽기 */
    protected void readClass(Class<?> facilityClass) {
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
            
            method = facilityClass.getMethod("isScriptBasedFacility");
            setScriptBasedFacility(DataUtil.parseBoolean( String.valueOf( method.invoke(null) )));
            
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
        json.put("uniqueGrade", new Integer(getUniqueGrade()));
        json.put("scriptBasedFacility", new Boolean(isScriptBasedFacility()));
        json.put("tech", String.valueOf(getTech()));
        json.put("imagehex", "");
        if(getImage() != null) {
            if(getImage() instanceof CharSequence) json.put("imagehex", getImage().toString());
        }
        
        return json;
    }
    
    /** 시설 객체 생성 */
    public Facility createFacility() {
        try { return (Facility) getFacilityClass().newInstance(); } catch(Exception ex) { throw new RuntimeException(ex.getMessage(), ex); }
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
        return facilityClassWrapper.getWrappedClass();
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setFacilityClass(Class<?> facilityClass) {
        this.facilityClassWrapper = new SimpleClassWrapper(facilityClass);
    }
    public void setFacilityClassWrapper(ClassWrapper wrapper) {
    	this.facilityClassWrapper = wrapper;
    }
    public ClassWrapper getFacilityClassWrapper() {
		return facilityClassWrapper;
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
    public boolean isScriptBasedFacility() {
        return scriptBasedFacility;
    }
    public void setScriptBasedFacility(boolean scriptBasedFacility) {
        this.scriptBasedFacility = scriptBasedFacility;
    }
    @Override
    public String toString() {
        return getTitle();
    }
    
    /** 건설에 필요한 연구 및 레벨 제한 반환 */
    @SuppressWarnings("unchecked")
    public List<ResearchCondition> getResearchCoditions(Colony col) {
        try {
            Method mthd = getFacilityClass().getMethod("getResearchCoditions", Colony.class);
            if(mthd == null) return new ArrayList<ResearchCondition>();
            
            return (List<ResearchCondition>) mthd.invoke(null, col);
        } catch(NoSuchMethodException ex) {
            GlobalLogs.processExceptionOccured(ex, false);
            return new ArrayList<ResearchCondition>();
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public String isBuildAvail(Colony col, City city) {
        try {
            String reason = null;
            
            // 필요 연구 검사
            List<ResearchCondition> listRes = getResearchCoditions(col);
            for(ResearchCondition c : listRes) {
                String resClass = c.getResearchClassName(); // 필요 연구 클래스명
                int    lev      = c.getLevel();             // 필요 연구의 레벨 (시설 건설 가능여부 체크 시에는 시설이 1레벨이 될 때만을 검사하므로, 레벨 증가폭과 조건 시작레벨은 필요가 없음)
                
                boolean exists = false;
                boolean levFit = false;
                for(Research r : col.getResearches()) {
                    if(r.getClassName().equals(resClass)) {
                        exists = true;
                        if(r.getLevel() >= lev) {
                            levFit = true;
                        }
                    }
                }
                if(! exists) { return ColonyManager.t("건설에 연구가 더 필요합니다."); }
                if(! levFit) { return ColonyManager.t("건설에 연구가 더 필요합니다."); }
            }
            
            // 각 시설 별 따로 지정된 건설 가능여부 검사
            Method mthd = getFacilityClass().getMethod("isBuildAvail", Colony.class, City.class);
            reason = (String) mthd.invoke(null, col, city);
            if(reason != null) return reason;
            
            // 고유성 설정 검사
            int uniqGrade = getUniqueGrade();
            if(uniqGrade == AbstractFacility.FACILITY_UNIQUE_GRADE_NONE) return null;
            
            if(uniqGrade == AbstractFacility.FACILITY_UNIQUE_GRADE_CITY) {
                if(! city.getFacilities(getFacilityClass()).isEmpty()) return ColonyManager.t("도시 당 하나만 건설할 수 있는 시설입니다.");
            } else if(uniqGrade == AbstractFacility.FACILITY_UNIQUE_GRADE_COLONY) {
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
        try { return ((Facility) getFacilityClass().newInstance()).getSpaceSize(); } catch(Exception ex) { throw new RuntimeException(ex.getMessage(), ex); }
    }
    public int getUniqueGrade() {
        return uniqueGrade;
    }
    public void setUniqueGrade(int uniqueGrade) {
        this.uniqueGrade = uniqueGrade;
    }
}
