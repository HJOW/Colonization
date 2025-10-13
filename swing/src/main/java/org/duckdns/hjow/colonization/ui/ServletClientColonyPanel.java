package org.duckdns.hjow.colonization.ui;

import org.duckdns.hjow.colonization.elements.Colony;

/** 서블릿 클라이언트용 정착지 정보 출력 및 컨트롤을 담당하는 UI 컴포넌트 */
public class ServletClientColonyPanel extends DefaultColonyPanel {
    private static final long serialVersionUID = 3133188756376393398L;
    public ServletClientColonyPanel() {
        super();
    }
    
    public ServletClientColonyPanel(Colony colony, GUIColonyManager superInstance) {
        super(colony, superInstance);
    }
    
    
    
}
