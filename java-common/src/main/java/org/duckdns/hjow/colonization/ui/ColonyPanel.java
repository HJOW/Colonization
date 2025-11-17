package org.duckdns.hjow.colonization.ui;

import org.duckdns.hjow.colonization.elements.Colony;

public interface ColonyPanel extends ColonyElementPanel {
    /** 화면 새로고침 예약 */
    public void reserveRefresh();
    /** 정착지 기본정보만 새로고침 */
    public void refreshColonyBasicMeta(Colony colony, ColonyManagerUI superInstance);
}
