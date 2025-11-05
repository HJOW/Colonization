package org.duckdns.hjow.colonization.elements;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.city.NormalCity;
import org.duckdns.hjow.colonization.elements.facilities.CapsuleBusStation;
import org.duckdns.hjow.colonization.elements.facilities.CargoRailSystem;
import org.duckdns.hjow.colonization.elements.facilities.FacilityInformation;
import org.duckdns.hjow.colonization.elements.facilities.PowerStation;
import org.duckdns.hjow.colonization.elements.facilities.Residence;
import org.duckdns.hjow.colonization.elements.facilities.ResidenceModule;
import org.duckdns.hjow.colonization.elements.facilities.Restaurant;
import org.duckdns.hjow.colonization.elements.facilities.SmallFactory;
import org.duckdns.hjow.colonization.elements.facilities.SmallResearchCenter;
import org.duckdns.hjow.colonization.elements.products.food.NutritionBlock;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.elements.research.ResearchManager;
import org.duckdns.hjow.colonization.events.EasyGorrdInvasion;
import org.duckdns.hjow.colonization.events.InfluenzaEvent;
import org.duckdns.hjow.colonization.events.Riot;
import org.duckdns.hjow.colonization.events.TimeEvent;
import org.duckdns.hjow.colonization.pack.BundledPack;
import org.duckdns.hjow.colonization.pack.Pack;
import org.duckdns.hjow.commons.exception.KnownRuntimeException;
import org.duckdns.hjow.commons.json.JsonObject;

/** 기본 제공되는 정착지 시나리오 클래스 */
public final class NormalColony extends AbstractColony {
    private static final long serialVersionUID = -5381698598742715021L;
    protected transient Pack parentPack = new BundledPack();
    
    public NormalColony() {
        super();
    }
    
    @Override
    public String getType() {
        return NormalColony.getColonyClassName();
    }
    
    @Override
    protected City createCityInstance(JsonObject json) throws IOException {
        return new NormalCity(json);
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
    
    /** 새 도시를 생성 */
    @Override
    public City newCity() {
        if(getCityCount() >= getMaxCityCount()) throw new KnownRuntimeException(ColonyManager.t("이 정착지에는 더 이상 도시를 건설할 수 없습니다."));
        
        City city = new NormalCity();
        addDefaultStarts(city);
        getCities().add(city);
        return city;
    }
    
    @Override
    protected void addDefaultStarts(City city) {
        int idx;
        for(idx=0; idx<50; idx++) {
            city.createNewCitizen();
        }
        
        Facility fac;
        
        for(idx=0; idx<12; idx++) {
            fac = new ResidenceModule();
            ((Residence) fac).setComportGrade(0);
            city.getFacility().add(fac);
        }
        
        for(idx=0; idx<3; idx++) {
            fac = new PowerStation();
            city.getFacility().add(fac);
        }
        
        for(idx=0; idx<2; idx++) {
            fac = new Restaurant();
            for(idx=0; idx<10; idx++) {
                ((Restaurant) fac).store(new NutritionBlock());
            }
            city.getFacility().add(fac);
        }
        
        fac = new SmallResearchCenter();
        city.getFacility().add(fac);
        
        for(idx=0; idx<2; idx++) {
            fac = new SmallFactory();
            city.getFacility().add(fac);
        }
        
        for(idx=0; idx<2; idx++) {
            fac = new CapsuleBusStation();
            city.getFacility().add(fac);
        }
        
        fac = new CargoRailSystem();
        city.getFacility().add(fac);
    }
    
    /** 발생할 수 있는 이벤트 유형들 반환 */
    @Override
    public List<TimeEvent> getEvents() {
        List<TimeEvent> events = new Vector<TimeEvent>();
        
        events.add(new InfluenzaEvent());
        events.add(new Riot());
        events.add(new EasyGorrdInvasion());
        
        return events;
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
