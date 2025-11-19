package org.duckdns.hjow.colonization.ui;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;
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
            
        } catch(Throwable t) {
            throw new RuntimeException(t.getMessage(), t);
        }
        
        this.superInstance.requestLoadServletColony();
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
            
        } catch(Throwable t) {
            throw new RuntimeException(t.getMessage(), t);
        }
        
        this.superInstance.requestLoadServletColony();
    }
    
    @Override
    public void newCity() {
        Colony col = getColony();
        
        // 최대 도시 수 제한 체크
        int cityCnt = col.getCityCount();
        if(cityCnt >= col.getMaxCityCount()) throw new RuntimeException(ColonyManager.t("더 이상 새 도시를 건설할 수 없습니다."));
        
        // 예산 체크
        long howMuch = col.getBuildingNewCityFee();
        long nowHave = col.getMoney();
        if(nowHave < howMuch) throw new RuntimeException(ColonyManager.t("새 도시 건설에는 [MONEY] 의 예산이 더 필요합니다.").replace("[MONEY]", String.valueOf(howMuch - nowHave)));
        
        // 인구 체크 (소모는 되지 않지만, 최소 조건으로 적용)
        long population = col.getCitizenCount();
        if(cityCnt >= 1) {
            if((population / cityCnt) < 1000) throw new RuntimeException(ColonyManager.t("새 도시를 건설하려면, 현재의 도시들의 인구 평균이 [AVERAGE] 을 넘어야 합니다.").replace("[AVERAGE]", "1000"));
        }
        
        try {
            URL urls = new URL(this.url);
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("svName", "colony");
            parameters.put("svSub" , "new");
            parameters.put("jwt"   , this.token);
            parameters.put("type"  , "City");
            parameters.put("colony", String.valueOf(colony.getKey()));
            
            String responseString = NetUtil.sendPost(urls, parameters, "application/json", "UTF-8");
            JsonObject responseJson = (JsonObject) JsonObject.parseJson(responseString.trim());
            
            boolean success = DataUtil.parseBoolean(responseJson.get("success").toString().trim());
            if(! success) throw new RuntimeException(responseJson.get("message").toString().trim());
            
        } catch(Throwable t) {
            throw new RuntimeException(t.getMessage(), t);
        }
        
        this.superInstance.requestLoadServletColony();
    }
}
