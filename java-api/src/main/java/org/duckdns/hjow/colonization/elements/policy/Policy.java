package org.duckdns.hjow.colonization.elements.policy;

import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.elements.city.City;

/** 정책 */
public interface Policy extends ColonyElements {
    public String getTitle();
    public String getTooltip();
    public boolean isEnabled();
    public void setEnabled(boolean enabled);
    /** 활성화 가능여부 반환 */
    public boolean isAvail(Colony col, City ct);
    /** 월간 비용 반환 */
    public long getMonthlyFee(Colony col, City ct);
    /** 발전량 증감 배율 반환 */
    public double getPowerSupplyRate(Colony col, City ct);
    /** 교통수용량 증감 배율 반환 */
    public double getTransSupplyRate(Colony col, City ct);
    /** 네트워크 증감 배율 반환 */
    public double getNetworkSupplyRate(Colony col, City ct);
    /** 시설 보너스 배율 반환 */
    public double getFacilityBonusRate(Colony col, City ct, Facility f);
    /** 출산률 배율 반환 */
    public double getBirthBonusRate(Colony col, City ct);
}
