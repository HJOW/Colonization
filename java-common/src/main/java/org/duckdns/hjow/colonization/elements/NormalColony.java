package org.duckdns.hjow.colonization.elements;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.facilities.FacilityInformation;

/** 기본 제공되는 정착지 시나리오 클래스 */
public class NormalColony extends AbstractColony {
    private static final long serialVersionUID = -5381698598742715021L;
    
    public NormalColony() {
        super();
    }
    
    public String getType() {
        return NormalColony.getColonyClassName();
    }
    
    @Override
    public boolean supportedFacility(FacilityInformation info) {
    	if(info == null) return false;
        return true;
    }
    
    @Override
    public boolean supportedResearch(String researchTypeName) {
    	if(researchTypeName == null) return false;
        return true;
    }
    
    public static String getColonyClassName() {
        return "NormalColony";
    }
    
    public static String getColonyClassTitle() {
        return ColonyManager.t("일반 정착지 시나리오");
    }
    
    public static String getColonyClassDescription() {
        return ColonyManager.t("일반 정착지 시나리오");
    }
}
