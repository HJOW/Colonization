package org.duckdns.hjow.colonization.elements;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.facilities.FacilityInformation;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.elements.research.ResearchManager;
import org.duckdns.hjow.colonization.pack.BundledPack;
import org.duckdns.hjow.colonization.pack.Pack;

/** 기본 제공되는 정착지 시나리오 클래스 */
public final class NormalColony extends AbstractColony {
    private static final long serialVersionUID = -5381698598742715021L;
    protected transient Pack parentPack = new BundledPack();
    
    public NormalColony() {
        super();
    }
    
    public String getType() {
        return NormalColony.getColonyClassName();
    }
    
    @Override
    public boolean supportedFacility(FacilityInformation info) {
    	if(info == null) return false;
    	if(parentPack == null) parentPack = new BundledPack(); // 리플렉션을 통해 객체를 생성하면 필드 기본값이 안타는 현상 대응
        return (parentPack.getFacilityClasses().contains(info.getFacilityClass()));
    }
    
    @Override
    public boolean supportedResearch(String researchTypeName) {
    	if(researchTypeName == null) return false;
    	Research res = ResearchManager.createResearchInstance(researchTypeName);
    	if(res == null) return false;
    	if(parentPack == null) parentPack = new BundledPack(); // 리플렉션을 통해 객체를 생성하면 필드 기본값이 안타는 현상 대응
    	return (parentPack.getResearchClasses().contains(res.getClass()));
    }
    
    public static String getColonyClassName() {
        return "NormalColony";
    }
    
    public static String getColonyClassTitle() {
        return ColonyManager.t("메소 정착지 시나리오");
    }
    
    public static String getColonyClassDescription() {
        return ColonyManager.t("메소 연방 자치령 소속으로 새 정착지를 개척합니다.");
    }
}
