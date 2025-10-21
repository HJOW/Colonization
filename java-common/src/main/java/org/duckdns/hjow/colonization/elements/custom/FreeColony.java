package org.duckdns.hjow.colonization.elements.custom;

import java.math.BigInteger;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.AbstractColony;
import org.duckdns.hjow.colonization.elements.facilities.FacilityInformation;

/** 모든 종류의 시설과 연구를 지원하는 정착지 시나리오 */
public final class FreeColony extends AbstractColony {
	private static final long serialVersionUID = 7091084518100557257L;
	
	public FreeColony() {
        super();
    }

	public String getType() {
        return getColonyClassName();
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
        return "FreeColony";
    }
    
    /** 사용 가능한 난이도 목록 반환 */
    public static int[] getAvailableDifficulties() {
    	return createAvailableDifficulties(1, 9);
    }
    
    public static String getColonyClassTitle() {
        return ColonyManager.t("프리 정착지 시나리오");
    }
    
    public static String getColonyClassDescription() {
        return ColonyManager.t("모든 시설과 연구를 지원하는 커스텀 시나리오로,\n정착지가 공식 인증되지 않습니다.");
    }
}
