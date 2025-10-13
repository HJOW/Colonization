package org.duckdns.hjow.colonization.ui;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.duckdns.hjow.colonization.elements.City;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.facilities.FacilityInformation;
import org.duckdns.hjow.colonization.elements.loan.Loan;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.DataUtil;
import org.duckdns.hjow.commons.util.NetUtil;

/** 서블릿 클라이언트용 정착지 정보 출력 및 컨트롤을 담당하는 UI 컴포넌트 */
public class ServletClientColonyPanel extends DefaultColonyPanel {
    private static final long serialVersionUID = 3133188756376393398L;
    protected String url, token;
    public ServletClientColonyPanel() {
        super();
    }
    
    public ServletClientColonyPanel(Colony colony, GUIColonyManager superInstance, String url, String token) {
        super(colony, superInstance);
        this.url = url;
        this.token = token;
    }
    
    public void setUrl(String url) { this.url = url; }
    public void setToken(String token) { this.token = token; }
    
    /** 새 시설 건설 선택이 완료된 경우 호출 */
    protected void onNewFacilityAdded(FacilityInformation f, City city, Colony colony) {
        try {
            URL urls = new URL(this.url);
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("svName", "colony");
            parameters.put("svSub" , "new");
            parameters.put("jwt"   , this.token);
            parameters.put("type"  , "Facility");
            parameters.put("name"  , f.getName());
            parameters.put("city"  , String.valueOf(city.getKey()));
            parameters.put("colony", String.valueOf(colony.getKey()));
            
            String responseString = NetUtil.sendPost(urls, parameters, "application/json", "UTF-8");
            JsonObject responseJson = (JsonObject) JsonObject.parseJson(responseString.trim());
            
            boolean success = DataUtil.parseBoolean(responseJson.get("success").toString().trim());
            if(! success) throw new RuntimeException(responseJson.get("message").toString().trim());
            
            this.superInstance.refreshColonyContent();
        } catch(Throwable t) {
            throw new RuntimeException(t.getMessage(), t);
        }
    }
    
    /** 새 대출 선택이 완료된 경우 호출 */
    protected void onNewLoanAdded(Loan l, Colony colony) {
        try {
            URL urls = new URL(this.url);
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("svName", "colony");
            parameters.put("svSub" , "new");
            parameters.put("jwt"   , this.token);
            parameters.put("type"  , "Loan");
            parameters.put("loan"  , String.valueOf(l.getKey()));
            parameters.put("colony", String.valueOf(colony.getKey()));
            
            String responseString = NetUtil.sendPost(urls, parameters, "application/json", "UTF-8");
            JsonObject responseJson = (JsonObject) JsonObject.parseJson(responseString.trim());
            
            boolean success = DataUtil.parseBoolean(responseJson.get("success").toString().trim());
            if(! success) throw new RuntimeException(responseJson.get("message").toString().trim());
            
            this.superInstance.refreshColonyContent();
        } catch(Throwable t) {
            throw new RuntimeException(t.getMessage(), t);
        }
    }
}
