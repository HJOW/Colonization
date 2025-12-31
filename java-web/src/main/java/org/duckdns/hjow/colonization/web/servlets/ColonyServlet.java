package org.duckdns.hjow.colonization.web.servlets;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.DefaultHoldingJob;
import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.elements.HoldingJob;
import org.duckdns.hjow.colonization.elements.Space;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.facilities.FacilityInformation;
import org.duckdns.hjow.colonization.elements.facilities.FacilityManager;
import org.duckdns.hjow.colonization.elements.facilities.Factory;
import org.duckdns.hjow.colonization.elements.facilities.ResearchCenter;
import org.duckdns.hjow.colonization.elements.loan.Loan;
import org.duckdns.hjow.colonization.elements.products.AbstractProduct;
import org.duckdns.hjow.colonization.elements.products.Product;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.web.accounts.Account;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.DataUtil;

public class ColonyServlet extends CommonServlet {
    private static final long serialVersionUID = 4083159248474182017L;
    
    @Override
    protected boolean isLoginNeeded() { return true; }
    
    @Override
    public String getName() {
        return "colony"; // URL : /web/json?svName=colony
    }

    @Override
    protected void doCommon(HttpServletRequest req, HttpServletResponse resp) throws Throwable {
        doBefore(req, resp);
        
        JsonObject responses = new JsonObject();
        responses.put("success", new Boolean(false));
        responses.put("message", "");
        
        try {
            String svSub1 = getParameter(req, "svSub");
            if(svSub1 == null) throw new RuntimeException("No sub keyword !");
            
            Account acc = checkLogined(req);
            
            if(svSub1.equalsIgnoreCase("list")) {
                serviceList(req, acc, responses);
            } else if(svSub1.equalsIgnoreCase("detail")) {
                serviceDetail(req, acc, responses);
            } else if(svSub1.equalsIgnoreCase("cycle")) {
                serviceCycle(req, acc, responses);
            } else if(svSub1.equalsIgnoreCase("rename")) {
                serviceRename(req, acc, responses);
            } else if(svSub1.equalsIgnoreCase("new")) {
                serviceNew(req, acc, responses);
            } else if(svSub1.equalsIgnoreCase("work")) {
                serviceWork(req, acc, responses);
            }
        } catch(RuntimeException ex) {
            responses.put("success", new Boolean(false));
            responses.put("message", ex.getMessage());
        } catch(Throwable ex) {
            logger.error("Error on " + this.getName() + " - " + ex.getMessage(), ex);
            responses.put("success", new Boolean(false));
            responses.put("message", ex.getMessage());
        }
        
        response(resp, responses);
        doAfter(req, resp);
    }
    
    protected void serviceList(HttpServletRequest req, Account acc, JsonObject responses) throws Throwable {
        JsonArray arr = new JsonArray();
        String type = getParameter(req, "type");
        if(DataUtil.isEmpty(type)) type = "Colony";
        
        if("Colony".equalsIgnoreCase(type)) {
            for(Colony c : acc.getColonies()) {
                JsonObject row = new JsonObject();
                row.put("name", c.getName());
                row.put("key" , String.valueOf(c.getKey()));
                arr.add(row);
            }
        } else {
            String strKey = getParameter(req, "colonyKey");
            long key = Long.parseLong(strKey);
            
            Colony col = null;
            City city = null;
            
            for(Colony c : acc.getColonies()) {
                if(key == c.getKey()) { col = c; break; }
            }
            
            if(col == null) {
                responses.put("success", new Boolean(false));
                responses.put("message", "Not existing colony.");
                return;
            }
            
            strKey = getParameter(req, "cityKey");
            key = Long.parseLong(strKey);
            
            for(City ct : col.getCities()) {
                if(key == ct.getKey()) { city = ct; break; }
            }
            
            if(city == null) {
                responses.put("success", new Boolean(false));
                responses.put("message", "Not existing city.");
                return;
            }
            
            if("FacilitiesAvailable".equalsIgnoreCase(type)) {
                List<FacilityInformation> lists = new ArrayList<FacilityInformation>();
                lists.addAll(FacilityManager.getFacilityInformations());
                for(FacilityInformation info : lists) {
                    if(! col.supportedFacility(info)) continue;
                    if(info.isBuildAvail(col, city) != null) continue;
                    arr.add(info.toJson());
                }
            }
        }
        
        
        responses.put("success", new Boolean(true));
        responses.put("message", "");
        responses.put("list", arr);
    }

    protected void serviceDetail(HttpServletRequest req, Account acc, JsonObject responses) throws Throwable {
        String strKey = getParameter(req, "key");
        long key = Long.parseLong(strKey);
        
        Colony col = null;
        for(Colony c : acc.getColonies()) {
            if(key == c.getKey()) { col = c; break; }
        }
        
        if(col == null) {
            responses.put("success", new Boolean(false));
            responses.put("message", "Not existing colony.");
        } else {
            String strSubType = getParameter(req, "subType"); // 정착지가 아닌, 정착지 내 일부 요소만 조회할 경우 지정 (선택사항)
            String strSubKey  = getParameter(req, "subKey");  // subType 지정 시 같이 지정해야 함. 조회할 요소의 key
            long subKey = 0L;
            if(DataUtil.isNotEmpty(strSubKey)) subKey = Long.parseLong(strSubKey);
            if(subKey != 0L && DataUtil.isNotEmpty(strSubType)) {
                // 정착지가 아닌, 정착지 내 일부 요소만 조회하려는 경우 처리
                if(strSubType.equalsIgnoreCase("city")) {
                    City city = null;
                    for(City ct : col.getCities()) {
                        if(ct.getKey() == subKey) { city = ct; break; }
                    }
                    
                    if(city == null) {
                        responses.put("success", new Boolean(false));
                        responses.put("message", "Not existing city.");
                    } else {
                        responses.put("success", new Boolean(true));
                        responses.put("message", "");
                        responses.put("type", "City");
                        responses.put("detail", city.toJson(true, col, city, false));
                    }
                    
                } else {
                    responses.put("success", new Boolean(true));
                    responses.put("message", "");
                    responses.put("type", "Colony");
                    responses.put("detail", col.toJson(true, col, null, false));
                }
            } else {
                // 정착지 자체 조회
                responses.put("success", new Boolean(true));
                responses.put("message", "");
                responses.put("type", "Colony");
                responses.put("detail", col.toJson(true, col, null, false));
            }
        }
    }
    
    protected void serviceCycle(HttpServletRequest req, Account acc, JsonObject responses) throws Throwable {
        String strKey = getParameter(req, "key");
        long key = Long.parseLong(strKey);
        
        String strCyclePass = getParameter(req, "cycle");
        if(strCyclePass == null || "".equals(strCyclePass)) strCyclePass = "1";
        int cyclePass = Integer.parseInt(strCyclePass);
        if(cyclePass > 10) cyclePass = 10;
        if(cyclePass <  1) cyclePass =  1;
        
        Colony col = null;
        for(Colony c : acc.getColonies()) {
            if(key == c.getKey()) { col = c; break; }
        }
        
        BigInteger time = col.getTime();
        while(time.compareTo(ColonyManager.CYCLE_NO_MAXIMUM_BIG) >= 0) {
            time = time.subtract(ColonyManager.CYCLE_NO_MAXIMUM_BIG);
        }
        int cycle = time.intValue();
        
        for(int idx=0; idx<cyclePass; idx++) {
            cycle++;
            col.oneCycle(cycle, null, null, col, 100, null);
        }
        
        responses.put("success", new Boolean(true));
        responses.put("message", "");
        
        String strRefresh = getParameter(req, "refresh");
        if(DataUtil.isNotEmpty(strRefresh) && DataUtil.parseBoolean(strRefresh)) responses.put("detail", col.toJson(true, col, null, false));
    }
    
    protected void serviceRename(HttpServletRequest req, Account acc, JsonObject responses) throws Throwable {
        String type = getParameter(req, "type");
        String name = getParameter(req, "name");
        String strKey = getParameter(req, "key");
        long key = Long.parseLong(strKey);
        
        if("Colony".equalsIgnoreCase(type)) {
            for(Colony c : acc.getColonies()) {
                if(c.getKey() == key) {
                    c.setName(name);
                }
            }
            responses.put("success", new Boolean(true));
            responses.put("message", "");
        } else if("City".equalsIgnoreCase(type)) {
            for(Colony c : acc.getColonies()) {
                for(City ct : c.getCities()) {
                    if(ct.getKey() == key) {
                        ct.setName(name);
                    }
                }
            }
            responses.put("success", new Boolean(true));
            responses.put("message", "");
        } else if("Facility".equalsIgnoreCase(type)) {
            for(Colony c : acc.getColonies()) {
                for(City ct : c.getCities()) {
                    for(Facility f : ct.getFacility()) {
                        if(f.getKey() == key) {
                            f.setName(name);
                        }
                    }
                }
            }
            responses.put("success", new Boolean(true));
            responses.put("message", "");
        } else if("Citizen".equalsIgnoreCase(type)) {
            for(Colony c : acc.getColonies()) {
                for(City ct : c.getCities()) {
                    for(Citizen ctz : ct.getCitizens()) {
                        if(ctz.getKey() == key) {
                            ctz.setName(name);
                        }
                    }
                }
            }
            responses.put("success", new Boolean(true));
            responses.put("message", "");
        } else {
            responses.put("success", new Boolean(false));
            responses.put("message", "Cannot find the type " + type);
        }
    }
    
    protected void serviceNew(HttpServletRequest req, Account acc, JsonObject responses) throws Throwable {
        String strKey = getParameter(req, "colony");
        long colKey = Long.parseLong(strKey);
        
        String type  = getParameter(req, "type");
        String name  = getParameter(req, "name");
        
        if("Facility".equalsIgnoreCase(type)) {
            strKey = getParameter(req, "city");
            long cityKey = Long.parseLong(strKey);
            
            FacilityInformation info = FacilityManager.getFacilityInformation(name);
            
            Colony col = null;
            Space spc = null;
            for(Colony c : acc.getColonies()) {
                if(colKey == c.getKey()) { col = c; break; }
            }
            if(col == null) throw new RuntimeException("No colony found.");
            spc = col.getSpace();
            
            City city = null;
            for(City ct : col.getCities()) {
                if(cityKey == ct.getKey()) { city = ct; break; }
            }
            if(city == null) throw new RuntimeException("No city found.");
            
            long prices = (int) (info.getPrice().longValue() * spc.getMoneyCostRate());
            
            if(col.getMoney() < prices) {
                throw new RuntimeException(ColonyManager.t("예산이 부족합니다.\n[MONEY] 의 예산이 더 필요합니다.").replace("[MONEY]", String.valueOf(info.getPrice() - col.getMoney())));
            };
            
            if(col.getTech() < info.getTech().longValue()) {
                throw new RuntimeException(ColonyManager.t("기술이 부족합니다.\n[TECH] 의 기술이 더 필요합니다.").replace("[TECH]", String.valueOf(info.getTech() - col.getTech())));
            };
            
            int leftSpaces = city.getLeftSpaces();
            int needSpaces = info.getSpaceSize();
            if(leftSpaces < needSpaces) {
                throw new RuntimeException(ColonyManager.t("잔여 공간이 부족합니다.\n[SPACE] 의 공간이 더 필요합니다.").replace("[SPACE]", String.valueOf(needSpaces - leftSpaces)));
            }
            
            Method mthdChecker = info.getFacilityClass().getMethod("isBuildAvail", Colony.class, City.class);
            String chkRes = (String) mthdChecker.invoke(null, col, city);
            if(chkRes != null) {
                throw new RuntimeException(chkRes);
            }
            
            if(! col.supportedFacility(info)) {
                throw new RuntimeException(ColonyManager.t("이 정착지에는 설치할 수 없는 시설입니다."));
            }
            
            int cycles = (int) (info.getBuildingCycle() * spc.getCycleCostRate());
            
            HoldingJob job = new DefaultHoldingJob(cycles, cycles, "NewFacility", info.getName());
            job.setUsingSpace(info.getSpaceSize());
            city.getHoldings().add(job);
            
            col.modifyingMoney(prices * (-1) , city, city, "Building", info.getTitle());
            
            responses.put("success", new Boolean(true));
            responses.put("message", "");
            responses.put("detail", col.toJson(true, col, null, false));
        } else if("City".equalsIgnoreCase(type)) {
            Colony col = null;
            for(Colony c : acc.getColonies()) {
                if(colKey == c.getKey()) { col = c; break; }
            }
            if(col == null) throw new RuntimeException("No colony found.");
            
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
            
            City c = col.newCity();
            col.modifyingMoney( col.getBuildingNewCityFee() * (-1) , c, col, "NewCity", "");
            
            responses.put("success", new Boolean(true));
            responses.put("message", "");
            responses.put("detail", col.toJson(true, col, null, false));
        } else if("Loan".equalsIgnoreCase(type)) {
            Colony col = null;
            for(Colony c : acc.getColonies()) {
                if(colKey == c.getKey()) { col = c; break; }
            }
            if(col == null) throw new RuntimeException("No colony found.");
            
            strKey = getParameter(req, "loan");
            long loanKey = Long.parseLong(strKey);
            
            Loan loan = null;
            for(Loan l : col.getLoanAvail()) {
                if(l.getKey() == loanKey) {
                    loan = l;
                    break;
                }
            }
            
            if(loan == null) throw new RuntimeException("No loan found.");
            
            col.addLoan(loan);
            
            responses.put("success", new Boolean(true));
            responses.put("message", "");
            responses.put("detail", col.toJson(true, col, null, false));
        } else {
            throw new RuntimeException("Don't know what type do you want to create.");
        }
    }
    
    protected void serviceWork(HttpServletRequest req, Account acc, JsonObject responses) throws Throwable {
        String strKey = getParameter(req, "colony");
        long colKey = Long.parseLong(strKey);
        
        strKey = getParameter(req, "city");
        long cityKey = Long.parseLong(strKey);
        
        strKey = getParameter(req, "facility");
        long facKey = Long.parseLong(strKey);
        
        String name  = getParameter(req, "name");
        
        Colony col = null;
        for(Colony c : acc.getColonies()) {
            if(colKey == c.getKey()) { col = c; break; }
        }
        if(col == null) throw new RuntimeException("No colony found.");
        
        City city = null;
        for(City ct : col.getCities()) {
            if(cityKey == ct.getKey()) { city = ct; break; }
        }
        if(city == null) throw new RuntimeException("No city found.");
        
        Facility fac = null;
        for(Facility f : city.getFacility()) {
            if(facKey == f.getKey()) { fac = f; break; }
        }
        if(fac == null) throw new RuntimeException("No facility found.");
        
        if(fac instanceof ResearchCenter) {
            ResearchCenter resCenter = (ResearchCenter) fac;
            Research research = null;
            for(Research r : col.getResearches()) {
                if(name.equals(String.valueOf(r.getKey()))) {
                    research = r;
                    break;
                }
            }
            if(research == null) {
                for(Research r : col.getResearches()) {
                    if(name.equals(r.getName())) {
                        research = r;
                        break;
                    }
                }
            }
            if(research == null) throw new RuntimeException("No research found.");
            
            resCenter.setResearchKey(research.getKey());
            
            responses.put("success", new Boolean(true));
            responses.put("message", "");
            responses.put("detail", col.toJson(true, col, null, false));
        } else if(fac instanceof Factory) {
            Factory factory = (Factory) fac;
            
            if(DataUtil.isEmpty(name)) {
                factory.setProductType(null);
            } else {
                Product prod = null;
                for(Product p : AbstractProduct.getProductTypeList()) {
                    if(name.equals(p.getKey())) { prod = p; break; }
                }
                if(prod == null) {
                    for(Product p : AbstractProduct.getProductTypeList()) {
                        if(name.equals(p.getType())) { prod = p; break; }
                    }
                }
                if(prod == null) {
                    for(Product p : AbstractProduct.getProductTypeList()) {
                        if(name.equals(p.getName())) { prod = p; break; }
                    }
                }
                if(prod == null) throw new RuntimeException("No product found.");
                factory.setProductType(prod.getType());
            }
            
            responses.put("success", new Boolean(true));
            responses.put("message", "");
            responses.put("detail", col.toJson(true, col, null, false));
        }
    }
}
