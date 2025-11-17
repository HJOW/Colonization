package org.duckdns.hjow.colonization.ui;

import org.duckdns.hjow.colonization.ColonizationMainClass;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;

public interface ColonyManagerUI {
    /** 해당 키를 갖는 정착지 찾아 반환 (목록에 없으면 null 반환) */
    public Colony getColony(long colonyKey);
    
    /** 도시가 속한 정착지 찾기 */
    public Colony getColonyFrom(City city);
    
    /** Colonization 실행 */
    public void open(ColonizationMainClass superInstance);
    
    /** 화면 새로고침 예약 */
    public void reserveRefresh();
}
