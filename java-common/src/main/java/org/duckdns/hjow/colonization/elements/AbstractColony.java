package org.duckdns.hjow.colonization.elements;

import java.io.File;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;

import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.FileUtil;
import org.duckdns.hjow.commons.util.HexUtil;
import org.duckdns.hjow.colonization.AccountingData;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.elements.enemies.Enemy;
import org.duckdns.hjow.colonization.elements.facilities.CargoRailSystem;
import org.duckdns.hjow.colonization.elements.facilities.FacilityInformation;
import org.duckdns.hjow.colonization.elements.facilities.PowerStation;
import org.duckdns.hjow.colonization.elements.facilities.ResearchCenter;
import org.duckdns.hjow.colonization.elements.facilities.Residence;
import org.duckdns.hjow.colonization.elements.facilities.ResidenceModule;
import org.duckdns.hjow.colonization.elements.facilities.Restaurant;
import org.duckdns.hjow.colonization.elements.facilities.SmallFactory;
import org.duckdns.hjow.colonization.elements.loan.Loan;
import org.duckdns.hjow.colonization.elements.products.food.NutritionBlock;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.elements.research.ResearchManager;
import org.duckdns.hjow.colonization.events.InfluenzaEvent;
import org.duckdns.hjow.colonization.events.Riot;
import org.duckdns.hjow.colonization.events.TimeEvent;
import org.duckdns.hjow.colonization.ui.ColonyManagerUI;
import org.duckdns.hjow.colonization.ui.ColonyPanel;

/** 정착지 구현 공통 클래스 */
public abstract class AbstractColony implements Colony {
    private static final long serialVersionUID = -3144963237818493111L;
    protected volatile long key = ColonyManager.generateKey();
    
    protected List<City>       cities     = new Vector<City>();
    protected List<Enemy>      enemies    = new Vector<Enemy>();
    protected List<HoldingJob> holdings   = new Vector<HoldingJob>();
    protected List<Research>   researches = new Vector<Research>();
    protected List<Loan>       loanAvail  = new Vector<Loan>();
    protected List<Loan>       loanHave   = new Vector<Loan>();
    
    protected String name = getDefaultNamePrefix() + "_" + ColonyManager.generateNaturalNumber();
    protected int  difficulty = 0;
    protected int  hp         = getMaxHp();
    protected int  credit     = 500;
    protected long money      = 1000000L;
    protected long tech       = 0L;
    
    protected volatile BigInteger time = new BigInteger("0");
    protected transient List<AccountingData> accountingData = new Vector<AccountingData>();
    protected transient String originalFileName;
    protected transient boolean checked = false;
    
    protected transient String clientVersion = ColonyManager.getVersionString();
    
    public AbstractColony() {
        checked = true;
        resetResearches();
    }
    
    /** 기본 이름 앞부분 */
    protected String getDefaultNamePrefix() {
        return ColonyManager.t("정착지");
    }
    
    /** 객체 타입 반환, JSON 변환 시 type 으로 들어갈 내용 */
    public String getType() {
        return getColonyClassName();
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    /** 이 정착지를 마지막으로 저장한 ColonyManager 의 버전 반환 */
    public String getClientVersion() {
        return clientVersion;
    }

    /** 이 정착지 내 도시들 반환 */
    public List<City> getCities() {
        return cities;
    }
    
    @Override
    public City getCity(long key) {
        for(City c : getCities()) {
            if(c.getKey() == key) return c;
        }
        return null;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    @Override
    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
    }

    @Override
    public List<HoldingJob> getHoldings() {
        return holdings;
    }

    public void setHoldings(List<HoldingJob> holdings) {
        this.holdings = holdings;
    }

    @Override
    public List<Research> getResearches() {
        return researches;
    }

    public void setResearches(List<Research> researches) {
        this.researches = researches;
    }
    
    /** 연구 목록 초기화 (비우고, 초기 상태로 다시 채움) */
    @Override
    public void resetResearches() {
        researches.clear();
        researches.addAll(ResearchManager.initList(this));
    }
    
    /** 총 인구 수 구하기 */
    @Override
    public long getCitizenCount() {
        long now = 0L;
        for(City c : getCities()) {
            now += c.getCitizenCount();
        }
        return now;
    }

    @Override
    public long getKey() {
        return key;
    }
    
    public void setKey(long key) {
        this.key = key;
    }

    @Override
    public void addHp(int amount) {
        hp += amount;
        int mx = getMaxHp();
        if(hp >= mx) hp = mx;
        if(hp <   0) hp = 0;
    }

    @Override
    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    @Override
    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }
    
    @Override
    public int getCredit() {
        return credit;
    }

    @Override
    public void setCredit(int credit) {
        this.credit = credit;
        if(this.credit <    0) this.credit =    0;
        if(this.credit > 1000) this.credit = 1000;
    }

    @Override
    public void modifyingMoney(long money, City city, ColonyElements objType, String reason, String moreString) {
        setMoney(getMoney() + money);
        
        AccountingData data = new AccountingData(getTime(), money, reason, city, objType, moreString);
        addAccountingData(data);
    }

    @Override
    public long getTech() {
        return tech;
    }

    @Override
    public void setTech(long tech) {
        this.tech = tech;
    }

    @Override
    public BigInteger getTime() {
        return time;
    }
    
    @Override
    public String getDateString() {
        BigInteger originals = new BigInteger(getTime().toByteArray()).divide(BigInteger.TEN); // 10 으로 나눠야 1초 단위가 됨
        BigInteger minutes, hour, date, month, year;
        // seconds = new BigInteger(originals.toByteArray());
        // minutes = new BigInteger(BigInteger.ZERO.toByteArray());
        minutes = new BigInteger(originals.toByteArray());
        hour    = new BigInteger(BigInteger.ZERO.toByteArray());
        date    = new BigInteger(BigInteger.ONE.toByteArray());
        month   = new BigInteger(BigInteger.ONE.toByteArray());
        year    = new BigInteger("3000");
        
        BigInteger std60, std30, std24, std12;
        std60 = new BigInteger("60");
        std30 = new BigInteger("30");
        std24 = new BigInteger("24");
        std12 = new BigInteger("12");
        
        // DIVIDE - MOD Calculation
        // // Seconds
        // if(seconds.compareTo(std60) >= 0) {
        //     minutes = minutes.add(new BigInteger(seconds.toByteArray()).divide(std60));
        //     seconds = seconds.mod(std60);
        // }
        
        // Minutes (Once again)
        if(minutes.compareTo(std60) >= 0) {
            hour = hour.add(new BigInteger(minutes.toByteArray()).divide(std60));
            minutes = minutes.mod(std60);
        }
        
        // Hour
        if(hour.compareTo(std24) >= 0) {
            date = date.add(new BigInteger(hour.toByteArray()).divide(std24));
            hour = hour.mod(std24);
        }
        
        // DIVIDE - Loop Calculation
        // Seconds
        // while(seconds.compareTo(std60) >= 0) {
        //     seconds = seconds.subtract(std60);
        //     minutes = minutes.add(BigInteger.ONE);
        // }
        
        // Minutes (Once again)
        while(minutes.compareTo(std60) >= 0) {
            minutes = minutes.subtract(std60);
            hour = hour.add(BigInteger.ONE);
        }
        
        // Hour
        while(hour.compareTo(std24) >= 0) {
            hour = hour.subtract(std24);
            date = date.add(BigInteger.ONE);
        }
        
        // Date
        while(date.compareTo(std30) > 0) {
            date = date.subtract(std30);
            month = month.add(BigInteger.ONE);
        }
        
        // Month
        while(month.compareTo(std12) > 0) {
            month = month.subtract(std12);
            year = year.add(BigInteger.ONE);
        }
        
        // Create String
        StringBuilder res = new StringBuilder("");
        res = res.append(year.toString()).append("-").append(String.format("%02d", month.intValue())).append("-").append(String.format("%02d", date.intValue()));
        res = res.append(" ");
        res = res.append(String.format("%02d", hour.intValue())).append(":").append(String.format("%02d", minutes.intValue())); // .append(":").append(String.format("%02d", seconds.intValue()));
        
        return res.toString().trim();
    }

    @Override
    public void setTime(BigInteger time) {
        this.time = time;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    @Override
    public List<AccountingData> getAccountingData() {
        return accountingData;
    }

    @Override
    public void setAccountingData(List<AccountingData> accountingData) {
        this.accountingData = accountingData;
    }
    
    @Override
    public void addAccountingData(AccountingData data) {
        if(getAccountingData().size() >= 100000) getAccountingData().remove(0);
        getAccountingData().add(data);
    }

    @Override
    public List<Loan> getLoanAvail() {
        return loanAvail;
    }

    @Override
    public List<Loan> getLoanHave() {
        return loanHave;
    }

    public void setLoanAvail(List<Loan> loanAvail) {
        this.loanAvail = loanAvail;
    }

    public void setLoanHave(List<Loan> loanHave) {
        this.loanHave = loanHave;
    }
    
    @Override
    public void addLoan(Loan l) {
        if(! loanAvail.contains(l)) throw new RuntimeException(ColonyManager.t("이 대출은 현재 사용할 수 없습니다."));
        loanAvail.remove(l);
        modifyingMoney(l.getAmount(), null, l, "Loan", ColonyManager.t("대출금"));
        loanHave.add(l);
        
        // 신용도 하락
        double lowerRate = 0.9;
        if(l.getInterestRate100() >= 10) lowerRate = 0.75;
        if(l.getInterestRate100() >= 15) lowerRate = 0.5;
        setCredit( (int) (getCredit() * lowerRate) );
        
        // 대출 상품 목록 초기화
        resetAvailLoans();
    }
    
    @Override
    public void resetAvailLoans() {
        loanAvail.clear();
        loanAvail.addAll(Loan.makeAvailableLoanListRandom(this));
    }

    @Override
    public void oneCycle(int cycle, City city, Colony colony, int efficiency100, ColonyPanel colPanel) { // parameters are null
        int idx;
        colony = this;
        
        // 체력이 없는 도시 삭제
        idx = 0;
        while(idx < getCities().size()) {
            City cityOne = getCities().get(idx);
            if(cityOne.getHp() <= 0) {
                cityOne.dispose();
                getCities().remove(idx);
                continue;
            }
            idx++;
        }
        
        // 체력이 없는 적 삭제
        idx = 0;
        while(idx < getEnemies().size()) {
            Enemy en = getEnemies().get(idx);
            if(en.getHp() <= 0) {
                en.dispose();
                getEnemies().remove(idx);
                continue;
            }
            idx++;
        }
        
        // 잔액이 없는 대출 삭제
        idx = 0;
        while(idx < getLoanHave().size()) {
            Loan en = getLoanHave().get(idx);
            if(en.getAmount() <= 0) {
                en.dispose();
                getLoanHave().remove(idx);
                continue;
            }
            idx++;
        }
        
        // 대출 사이클 처리
        for(Loan l : getLoanHave()) {
            l.oneCycle(cycle, city, colony, efficiency100, colPanel);
        }
        
        // 1년 지날 때마다, 사용 가능한 대출 목록 갱신
        if(cycle % 60 * 24 * 30 * 12 == 0) {
            resetAvailLoans();
        }
        
        // 도시별 사이클 처리
        for(City c : getCities()) {
            c.oneCycle(cycle, c, this, 100, colPanel);
        }
        
        // 적 사이클 처리
        for(Enemy e : getEnemies()) {
            e.oneCycle(cycle, city, colony, efficiency100, colPanel);
        }
        
        // 예약 작업 처리
        for(HoldingJob h : getHoldings()) {
            int lefts = h.getCycleLeft();
            h.decreaseCycle();
            if(lefts >= 1) continue;
            
            executeHoldJob(h);
        }
        
        // 소모된 예약 작업 삭제
        idx = 0;
        while(idx < getHoldings().size()) {
            if(getHoldings().get(idx).getCycleLeft() <= 0) {
                getHoldings().remove(idx);
                continue;
            }
            idx++;
        }
        
        // 이벤트 처리
        for(TimeEvent ev : getEvents()) {
            if(time.compareTo(new BigInteger("" + ev.getOccurMinimumTime(this))) < 0) continue;
            
            if(ev.getEventSize() == TimeEvent.EVENTSIZE_COLONY) {
                if(cycle % ev.getOccurCycle(this, city) == 0) {
                    if(Math.random() <= ev.getOccurRate(this, this, city)) ev.onEventOccured(this, this, city, colPanel);
                }
            }
        }
        
        // 예산이 음수인 경우 이자 발생
        if(getMoney() < 0L) {
        	if(cycle % 60 == 0) {
        	    long interests = ((long) Math.floor(Math.abs(getMoney()) * 0.009)) * (-1);
        	    modifyingMoney(interests, city, colony, "Interest", colony.getName());
        	}
        }
        
        // 시간 지남
        time = time.add(BigInteger.ONE);
    }
    
    /** 예약 작업 처리 */
    @SuppressWarnings("unused")
    protected void executeHoldJob(HoldingJob j) {
        String command, params;
        command = j.getCommand();
        params  = j.getParameter();
        
        try {
            if(command.equalsIgnoreCase("NewCity")) {
                newCity();
                return;
            }
        } catch(RuntimeException ex) {
            throw ex;
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    /** 이 정착지가 감당 가능한 도시 수를 반환 */
    @Override
    public int getMaxCityCount() {
        return 10000;
    }
    
    /** 현재 도시 수 반환 */
    @Override
    public int getCityCount() {
        return getCities().size();
    }
    
    /** 새 도시를 생성 */
    @Override
    public City newCity() {
        if(getCityCount() >= getMaxCityCount()) throw new RuntimeException(ColonyManager.t("이 정착지에는 더 이상 도시를 건설할 수 없습니다."));
        
        City city = new City();
        int idx;
        
        for(idx=0; idx<30; idx++) {
            city.createNewCitizen();
        }
        
        Facility fac;
        
        for(idx=0; idx<8; idx++) {
            fac = new ResidenceModule();
            ((Residence) fac).setComportGrade(0);
            city.getFacility().add(fac);
        }
        
        fac = new PowerStation();
        city.getFacility().add(fac);
        
        fac = new Restaurant();
        for(idx=0; idx<10; idx++) {
            ((Restaurant) fac).store(new NutritionBlock());
        }
        city.getFacility().add(fac);
        
        fac = new ResearchCenter();
        city.getFacility().add(fac);
        
        for(idx=0; idx<2; idx++) {
            fac = new SmallFactory();
            city.getFacility().add(fac);
        }
        
        fac = new CargoRailSystem();
        city.getFacility().add(fac);
        
        getCities().add(city);
        return city;
    }
    
    @Override
    public int getMaxHp() {
        return 1000000;
    }
    
    @Override
    public short getDefenceType() {
        return ColonyManager.DEFENCETYPE_BUILDING;
    }

    @Override
    public int getDefencePoint() {
        return 9;
    }
    
    @Override
    public int getAccountingPeriod() {
        return 600;
    }
    
    @Override
    public boolean supportedFacility(FacilityInformation info) {
        return true;
    }
    
    @Override
    public boolean supportedResearch(String researchTypeName) {
        return true;
    }
    
    /** 상세 내역 */
    @Override
    public String getStatusString(ColonyManagerUI superInstance) {
        DecimalFormat formatterInt  = new DecimalFormat("#,###,###,###,###,##0");
        // DecimalFormat formatterRate = new DecimalFormat("##0.00");
        
        StringBuilder desc = new StringBuilder("");
        desc = desc.append("\t").append("HP : ").append(formatterInt.format(getHp())).append(" / ").append(formatterInt.format(getMaxHp()));
        desc = desc.append("\t").append(ColonyManager.t("예산") + " : ").append(formatterInt.format(getMoney()));
        desc = desc.append("\t").append(ColonyManager.t("기술") + " : ").append(formatterInt.format(getTech()));
        desc = desc.append("\t").append(ColonyManager.t("도시 수") + " : ").append(formatterInt.format(getCityCount())).append(" / ").append(formatterInt.format(getMaxCityCount()));
        desc = desc.append("\t").append(ColonyManager.t("총 인구") + " : ").append(formatterInt.format(getCitizenCount()));
        desc = desc.append("\t").append(ColonyManager.t("신용도") + " : ").append(formatterInt.format(getCredit())).append(" / ").append(formatterInt.format(1000));
        
        return desc.toString().trim();
    }
    
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public JsonObject toJson() {
        return toJson(false, this, null);
    }
    
    @Override
    public JsonObject toJson(boolean details, Colony col, City city) {
        col  = this;
        city = null;
        
        JsonObject json = new JsonObject();
        json.put("type", getType());
        json.put("name", getName());
        json.put("key", String.valueOf(getKey()));
        json.put("hp", String.valueOf(getHp()));
        json.put("difficulty", new Integer(getDifficulty()));
        json.put("money", String.valueOf(getMoney()));
        json.put("tech", String.valueOf(getTech()));
        json.put("time", getTime().toString());
        json.put("credit", new Integer(getCredit()));
        json.put("version", getClientVersion());
        
        JsonArray list = new JsonArray();
        for(City c : getCities()) { list.add(c.toJson(details, col, c)); }
        json.put("cities", list);
        
        list = new JsonArray();
        for(HoldingJob h : getHoldings()) { list.add(h.toJson()); }
        json.put("holdings", list);
        
        list = new JsonArray();
        for(AccountingData d : getAccountingData()) { list.add(d.toJson()); }
        json.put("accountinghis", list);
        
        list = new JsonArray();
        for(Research d : getResearches()) { list.add(d.toJson(details, col, city)); }
        json.put("researches", list);
        
        list = new JsonArray();
        for(Loan l : getLoanAvail()) { list.add(l.toJson(details, col, city)); }
        json.put("loanAvail", list);
        
        list = new JsonArray();
        for(Loan l : getLoanHave()) { list.add(l.toJson(details, col, city)); }
        json.put("loanHave", list);
        
        if(checked) json.put("checker", getCheckerValue().toString());
        else        json.put("checker", "0");
        
        // 추가 정보 (불러올 때는 필요가 없는) 첨가
        json.put("maxHp", String.valueOf(getMaxHp()));
        
        if(details) {
            json.put("statusString", HexUtil.encodeString(getStatusString(null)));
        }
        
        if(details) {
            String str = getStatusString(null);
            if(str == null) str = "";
            json.put("statusString", HexUtil.encodeString(str));
            
            str = getDateString();
            json.put("dates", HexUtil.encodeString(str));
        }
        
        return json;
    }
    
    @Override
    public void fromJson(JsonObject json) {
        if(! ("Colony".equals(json.get("type")) || getType().equals(json.get("type")))) throw new RuntimeException("This object is not Colony type.");
        setName(json.get("name").toString());
        key = Long.parseLong(json.get("key").toString());
        try { setHp(Integer.parseInt(json.get("hp").toString()));                     } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); hp         = 10;              }
        try { setDifficulty(Integer.parseInt(json.get("difficulty").toString()));     } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); difficulty = 0;               }
        try { setMoney(Long.parseLong(json.get("money").toString()));                 } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); money      = 0;               }
        try { setTech(Long.parseLong(json.get("tech").toString()));                   } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); tech       = 0;               }
        try { setTime(new BigInteger(json.get("time").toString()));                   } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); time       = BigInteger.ZERO; }
        try { setCredit(Integer.parseInt(json.get("credit").toString()));             } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); credit     = 500;             }
        try { clientVersion = json.get("version").toString();                         } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); clientVersion = "0.0.0.0"; }
        
        JsonArray list = null;
        try { list = (JsonArray) json.get("cities"); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        cities.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        City city = new City((JsonObject) o);
                        cities.add(city);
                    } catch(Exception ex) {
                        GlobalLogs.processExceptionOccured(ex, false);
                    }
                }
            }
        }
        
        list = null;
        try { list = (JsonArray) json.get("holdings"); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        holdings.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        HoldingJob h = new HoldingJob();
                        h.fromJson((JsonObject) o);
                        holdings.add(h);
                    } catch(Exception ex) {
                        GlobalLogs.processExceptionOccured(ex, false);
                    }
                }
            }
        }
        
        list = null;
        try { list = (JsonArray) json.get("accountinghis"); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        accountingData.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        AccountingData d = new AccountingData();
                        d.fromJson((JsonObject) o);
                        accountingData.add(d);
                    } catch(Exception ex) {
                        GlobalLogs.processExceptionOccured(ex, false);
                    }
                }
            }
        }
        
        list = null;
        try { list = (JsonArray) json.get("researches"); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        researches.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        Research res = ResearchManager.fromJson((JsonObject) o);
                        researches.add(res);
                    } catch(Exception ex) {
                        GlobalLogs.processExceptionOccured(ex, false);
                    }
                }
            }
        }
        
        list = null;
        try { list = (JsonArray) json.get("loanAvail"); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        loanAvail.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        Loan loan = new Loan();
                        loan.fromJson((JsonObject) o);
                        loanAvail.add(loan);
                    } catch(Exception ex) {
                        GlobalLogs.processExceptionOccured(ex, false);
                    }
                }
            }
        }
        
        list = null;
        try { list = (JsonArray) json.get("loanHave"); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        loanHave.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        Loan loan = new Loan();
                        loan.fromJson((JsonObject) o);
                        loanHave.add(loan);
                    } catch(Exception ex) {
                        GlobalLogs.processExceptionOccured(ex, false);
                    }
                }
            }
        }
        
        // 포함 안된 연구 넣기
        for(Research oth : ResearchManager.initList(this)) {
            boolean exists = false;
            for(Research alr : researches) {
                if(oth.getName().equals(alr.getName())) { exists = true; break; }
            }
            if(! exists) researches.add(oth);
        }
        
        if(json.get("checker") != null) {
            try {
                BigInteger checker = new BigInteger(json.get("checker").toString());
                checked = (checker.equals(getCheckerValue()));
            } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); checked = false; }
        } else {
            checked = false;
        }
    }
    
    @Override
    public BigInteger getCheckerValue() {
        BigInteger res = new BigInteger(String.valueOf(getKey()));
        for(int idx=0; idx<getName().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getName().charAt(idx)))); }
        res = res.add(new BigInteger(String.valueOf(getHp())));
        for(City c : getCities())    { res = res.add(c.getCheckerValue()); }
        for(Loan l : getLoanAvail()) { res = res.add(l.getCheckerValue()); }
        for(Loan l : getLoanHave())  { res = res.add(l.getCheckerValue()); }
        
        return res;
    }

    /** 파일로 저장 */
    public void save(File f) throws Exception {
        String fileName = f.getName().toLowerCase();
        if(fileName.endsWith(".colgz")) {
            FileUtil.writeString(f, "UTF-8", toJson().toJSON(), GZIPOutputStream.class); 
        } else {
            FileUtil.writeString(f, "UTF-8", toJson().toJSON()); 
        }
    }
    
    /** 발생할 수 있는 이벤트 유형들 반환 */
    @Override
    public List<TimeEvent> getEvents() {
        List<TimeEvent> events = new Vector<TimeEvent>();
        
        events.add(new InfluenzaEvent());
        events.add(new Riot());
        
        return events;
    }
    
    @Override
    public void dispose() {
        for(City c : cities) {
            c.dispose();
        }
        cities.clear();
        for(Enemy en : enemies) {
            en.dispose();
        }
        enemies.clear();
    }
    
    public static String getColonyClassName() {
        return "Colony";
    }
    
    public static String getColonyClassTitle() {
        return ColonyManager.t("정착지");
    }
    
    public static String getColonyClassDescription() {
        return "";
    }
}
