package org.duckdns.hjow.colonization.elements.facilities;

import java.io.Serializable;
import java.util.List;

import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;
import org.duckdns.hjow.commons.json.JsonObject;

/** 시설 정보 */
public interface FacilityInformation extends Serializable {
    /** JSON 정보 생성 */
    public JsonObject toJson();
    
    /** 시설 객체 생성 */
    public Facility createFacility();
    
    public String getName();
    public String getTitle();
    public String getDescription();
    public Class<?> getFacilityClass();
    public Long getPrice();
    public int getBuildingCycle();
    public Long getTech();
    public Object getImage();
    public boolean isScriptBasedFacility();
    /** 건설에 필요한 연구 및 레벨 제한 반환 */
    public List<ResearchCondition> getResearchCoditions(Colony col);
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public String isBuildAvail(Colony col, City city);
    /** 이 시설이 차지하는 공간 크기 반환 */
    public int getSpaceSize();
    public int getUniqueGrade();
}
