package org.duckdns.hjow.colonization.elements;

import java.math.BigInteger;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.facilities.FacilityInformation;

/** 커스텀 정착지 시나라오로, 모든 종류의 시설과 연구 지원 */
public class CustomColony extends AbstractColony {
	private static final long serialVersionUID = -4732649232228406754L;

	public CustomColony() {
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
    
    @Override
    public BigInteger getCheckerValue() {
        return BigInteger.ZERO;
    }
    
    public static String getColonyClassName() {
        return "CustomColony";
    }
    
    public static String getColonyClassTitle() {
        return ColonyManager.t("커스텀 정착지 시나리오");
    }
    
    public static String getColonyClassDescription() {
        return ColonyManager.t("모든 시설과 연구를 지원하는 커스텀 시나리오로,\n정착지가 공식 인증되지 않습니다.");
    }
}
