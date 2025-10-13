package org.duckdns.hjow.colonization.elements.facilities;

import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.City;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.elements.research.energy.EnergyTech;
import org.duckdns.hjow.colonization.elements.research.energy.LightTech;
import org.duckdns.hjow.colonization.elements.research.engineering.BasicBuildingTech;
import org.duckdns.hjow.colonization.elements.research.engineering.BasicEngineering;

public class MagneticLevitationMetroStation extends TransportStation {
	private static final long serialVersionUID = -2098189817987614924L;

	@Override
    protected String getDefaultNamePrefix() {
        return ColonyManager.t("자기부상_열차_플랫폼");
    }
    
    @Override
    public String getStatusDescription(City city, Colony colony) {
        return "";
    }

    @Override
    public int getPowerConsume() {
        return 10;
    }

    @Override
    protected int getDefaultCapacity() {
        return 2000;
    }
    
    @Override
    public int getSpaceSize() {
    	return 4;
    }

    /** 수익 발생 주기 */
    protected int getProfitCycle() {
        return 0;
    }
    
    @Override
    public long usingFee() {
        return 0L;
    }
    
    public static String getFacilityName() {
        return ColonyManager.t("자기부상 열차 플랫폼");
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return ColonyManager.t("강한 자기력을 이용해 여러 차량이 연결된 열차를 띄워 정해진 레일을 따라 운행하는 교통 시설입니다.\n2곳 이상을 건설해야 동작합니다.\n교통 한도를 대폭 증가시킵니다.\n교통 한도가 부족하면, 일부 시설에 직원이 통근할 수 없게 됩니다.");
    }
    
    public static Long getFacilityPrice() {
        return new Long(200000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(1200);
    }
    
    public static Long getTechNeeded() {
        return new Long(0);
    }
    
    public static String getImageHex() {
        return null;
    }
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public static String isBuildAvail(Colony col, City city) { 
        boolean cond1 = false;
        boolean cond2 = false;
        boolean cond3 = false;
        boolean cond4 = false;
        List<Research> researches = col.getResearches();
        for(Research r : researches) {
            if(r instanceof BasicEngineering) {
                if(r.getLevel() >= 12) cond1 = true;
            }
            if(r instanceof BasicBuildingTech) {
                if(r.getLevel() >= 20) cond2 = true;
            }
            if(r instanceof EnergyTech) {
                if(r.getLevel() >= 7) cond3 = true;
            }
            if(r instanceof LightTech) {
                if(r.getLevel() >= 3) cond4 = true;
            }
        }
        
        if(! cond1) return ColonyManager.t("공학기초 연구가 부족합니다.");
        if(! cond2) return ColonyManager.t("기초건축학 연구가 부족합니다.");
        if(! cond3) return ColonyManager.t("에너지 연구가 부족합니다.");
        if(! cond4) return ColonyManager.t("광학 연구가 부족합니다.");
        return null;
    }
}
