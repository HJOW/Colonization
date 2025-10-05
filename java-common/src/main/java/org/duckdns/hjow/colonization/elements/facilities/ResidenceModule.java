package org.duckdns.hjow.colonization.elements.facilities;

import org.duckdns.hjow.colonization.elements.City;
import org.duckdns.hjow.colonization.elements.Colony;

public class ResidenceModule extends Residence {
    private static final long serialVersionUID = -4063295537669464654L;
    public ResidenceModule() { }
    
    @Override
    protected String getDefaultNamePrefix() {
        return "보급형_주거모듈";
    }
    
    @Override
    public int getMaxHp() {
        return 1000;
    }
    
    @Override
    public int getPowerConsume() {
        return 1;
    }

    @Override
    protected int getDefaultCapacity() {
        return 5;
    }
    
    public static String getFacilityName() {
        return "보급형 주거 모듈";
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return "기본적인 주거 모듈로 시민이 거주하는 데 필요한 기본적인 시설이 포함됩니다.";
    }
    
    public static Long getFacilityPrice() {
        return new Long(10000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(180);
    }
    
    public static Long getTechNeeded() {
        return new Long(0);
    }
    
    public static String getImageHex() {
        return null;
    }
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public static String isBuildAvail(Colony col, City city) { return null; }
}
