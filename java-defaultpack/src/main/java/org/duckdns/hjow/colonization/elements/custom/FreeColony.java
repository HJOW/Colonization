package org.duckdns.hjow.colonization.elements.custom;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.AbstractColony;
import org.duckdns.hjow.colonization.elements.Facility;
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
import org.duckdns.hjow.colonization.events.EasyGorrdInvasion;
import org.duckdns.hjow.colonization.events.InfluenzaEvent;
import org.duckdns.hjow.colonization.events.Riot;
import org.duckdns.hjow.colonization.events.TimeEvent;
import org.duckdns.hjow.commons.exception.KnownRuntimeException;
import org.duckdns.hjow.commons.json.JsonObject;

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

    /** 새 도시를 생성 */
    @Override
    public City newCity() {
        if(getCityCount() >= getMaxCityCount()) throw new KnownRuntimeException(ColonyManager.t("이 정착지에는 더 이상 도시를 건설할 수 없습니다."));
        
        City city = new NormalCity();
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
        
        getCities().add(city);
        return city;
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

    @Override
    protected City createCityInstance(JsonObject json) throws IOException {
        return new NormalCity(json);
    }
}
