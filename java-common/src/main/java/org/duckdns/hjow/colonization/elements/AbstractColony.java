package org.duckdns.hjow.colonization.elements;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;

import org.duckdns.hjow.colonization.AccountingData;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.constants.Constants;
import org.duckdns.hjow.colonization.constants.StaticMethods;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.custom.CustomElement;
import org.duckdns.hjow.colonization.elements.enemies.Enemy;
import org.duckdns.hjow.colonization.elements.facilities.FacilityInformation;
import org.duckdns.hjow.colonization.elements.facilities.Residence;
import org.duckdns.hjow.colonization.elements.facilities.Storage;
import org.duckdns.hjow.colonization.elements.loan.Loan;
import org.duckdns.hjow.colonization.elements.products.Product;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.elements.research.ResearchManager;
import org.duckdns.hjow.colonization.elements.ship.Ship;
import org.duckdns.hjow.colonization.events.TimeEvent;
import org.duckdns.hjow.colonization.ui.ColonyManagerUI;
import org.duckdns.hjow.colonization.ui.ColonyPanel;
import org.duckdns.hjow.commons.exception.KnownRuntimeException;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.stream.SimultaneousWork;
import org.duckdns.hjow.commons.stream.SingleAction;
import org.duckdns.hjow.commons.util.FileUtil;
import org.duckdns.hjow.commons.util.SecurityUtil;

/** 정착지 구현 공통 클래스 */
public abstract class AbstractColony implements Colony {
    private static final long serialVersionUID = -3144963237818493111L;
    protected volatile long key = ColonyManager.generateKey();
    protected transient boolean fNeedRefresh = true;
    
    protected List<City>       cities     = new Vector<City>();
    protected List<Enemy>      enemies    = new Vector<Enemy>();
    protected List<HoldingJob> holdings   = new Vector<HoldingJob>();
    protected List<Research>   researches = new Vector<Research>();
    protected List<Loan>       loanAvail  = new Vector<Loan>();
    protected List<Loan>       loanHave   = new Vector<Loan>();
    protected List<Celestials> celestials = new Vector<Celestials>();
    
    protected String name = getDefaultNamePrefix() + "_" + ColonyManager.getNaturalNumberFrom(key);
    protected int  difficulty = 0;
    protected int  hp         = getMaxHp();
    protected int  credit     = getStartCredit();
    protected long money      = getStartMoney();
    protected long tech       = 0L;
    
    protected BigInteger moneyOvers = BigInteger.ZERO; // 예산, long 타입 범위 초과분 저장 용도
    
    protected volatile BigInteger time   = BigInteger.ZERO;
    protected volatile BigInteger income = BigInteger.ZERO; // 1시간 (600 사이클) 당 인컴
    protected transient List<AccountingData> accountingData = new Vector<AccountingData>();
    protected transient String originalFileName;
    protected transient boolean checked = false;
    
    protected transient String clientVersion = ColonyManager.getVersionString();
    protected transient String clientBuildNo = String.valueOf(ColonyManager.BUILD_NO);
    
    protected transient Vector<SingleAction> actionsOnCycle = new Vector<SingleAction>();
    protected transient SimultaneousWork workOnCycle;
    
    /** 기본 밸런스로 초기세팅하여 정착지 객체 생성 */
    public AbstractColony() {
        resetAll(0);
    }
    
    /** 배수를 적용하여 정착지 객체 생성, 초기 예산과 신용도에 배수 적용 */
    public AbstractColony(int difficulty) {
        super();
        resetAll(difficulty);
    }
    
    /** 완전히 초기화, 난이도 지정 */
    public void resetAll(int difficulty) {
        checked = true;
        hp = getMaxHp();
        accountingData.clear();
        cities.clear();    
        enemies.clear();   
        holdings.clear(); 
        loanAvail.clear(); 
        loanHave.clear();  
        resetResearches();
        
        setDifficulty(difficulty);    // 값 세팅하면서, 최대/최소도 이 메소드에서 적용
        difficulty = getDifficulty(); // 다시 꺼내기 (값이 변경됨)
        
        double multiplyRate = 1.0;
        Loan startLoan = null;
        
        switch(difficulty) {
        case 1:
            multiplyRate = 1.0;
            break;
        case 2:
            multiplyRate = 0.5;
            break;
        case 3:
            multiplyRate = 0.375;
            break;
        case 4:
            multiplyRate = 0.25;
            break;
        case 5:
            multiplyRate = 0.2;
            break;
        case 6:
            multiplyRate = 0.15;
            break;
        default:
            multiplyRate = 0.1;
            break;
        }
        
        if(difficulty > 1) {
            credit = (int)  Math.round(getStartCredit() * multiplyRate);
            money  = (long) Math.round(getStartMoney()  * multiplyRate);
        } else {
            credit = getStartCredit();
            money  = getStartMoney();
        }
        
        if(difficulty >= 4) {
            switch(difficulty) {
            case 4:
                startLoan = new Loan(money / 2L, 36, 6);
            case 5:
                startLoan = new Loan(money, 36, 7);
            case 6:
                startLoan = new Loan(money + Math.round(money * 1.5), 36, 7);
            case 7:
                startLoan = new Loan(money + Math.round(money * 1.75), 36, 9);
            case 8:
                startLoan = new Loan(money * 2L, 36, 9);
            default:
                startLoan = new Loan(money * 4L, 36, 9);
            }
        }
        
        if(credit > getMaxCredit()) credit = getMaxCredit();
        if(startLoan != null) {
            loanHave.add(startLoan);
        }
        markAsRefresh(true);
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
    public final String getClassName() {
        return getClass().getSimpleName();
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getTooltip() {
        return getName();
    }
    
    /** 이 정착지를 마지막으로 저장한 ColonyManager 의 버전 반환 */
    @Override
    public String getClientVersion() {
        return clientVersion;
    }
    
    /** 이 정착지를 마지막으로 저장한 ColonyManager 의 빌드 번호 반환 */
    @Override
    public long getClientBuildNo() {
        return new Long(clientBuildNo);
    }
    
    /** 버전 정보 리셋 */
    @Override
    public void resetClientVersion(ColonyManagerUI man) {
    	if(man == null) throw new NullPointerException();
    	clientVersion = ColonyManager.getVersionString();
        clientBuildNo = String.valueOf(ColonyManager.BUILD_NO);
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
    
    /** 모든 도시의 시설 목록 반환 (모든 소속 도시들 다 스캔) */
    @Override
    public List<Facility> getFacilities() {
        List<Facility> list = new ArrayList<Facility>();
        for(City c : getCities()) {
            list.addAll(c.getFacility());
        }
        return list;
    }
    
    /** 특정 타입의 시설 목록 반환 (모든 소속 도시들 다 스캔) */
    @Override
    public List<Facility> getFacilities(Class<?> facilityClass) {
        List<Facility> list = new ArrayList<Facility>();
        for(City c : getCities()) {
            for(Facility f : c.getFacility()) {
                if(f.getClass() == facilityClass && (! list.contains(f))) list.add(f);
            }
        }
        return list;
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

    public List<Celestials> getCelestials() {
		return celestials;
	}

	public void setCelestials(List<Celestials> celestials) {
		this.celestials = celestials;
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
    
    /** 누락된 연구 추가 */
    protected void addOmittedResearches() {
        List<Research> allZeros = ResearchManager.initList(this);
        List<Research> nows = researches;
        
        for(Research r : allZeros) {
            boolean exists = false;
            for(Research rc : nows) {
                if(rc.getClassName().equals(r.getClassName())) { exists = true; break; }
            }
            if(! exists) {
                researches.add(r);
            }
        }
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
    
    @Override
    public void setDifficulty(int difficulty) {
        if(difficulty < 1) difficulty = 1;
        if(difficulty > 9) difficulty = 9;
        
        this.difficulty = difficulty;
    }

    @Override
    public long getMoney() {
        return money;
    }

    /** 낮은 단계의 setter 로 내부 사용 용도, 외부에서는 직접 호출 자제 ! */
    public void setMoney(long money) {
        this.money = money;
    }
    
    public BigInteger getMoneyOvers() {
        return moneyOvers;
    }
    
    /** 실질 금액을 반환 */
    @Override
    public BigInteger getMoneyTotals() {
        return getMoneyOvers().add(new BigInteger(String.valueOf(getMoney())));
    }

    public void setMoneyOvers(BigInteger moneyOvers) {
        this.moneyOvers = moneyOvers;
    }

    @Override
    public int getCredit() {
        return credit;
    }

    @Override
    public void setCredit(int credit) {
        this.credit = credit;
        if(this.credit < 0) this.credit = 0;
        if(this.credit > getMaxCredit()) this.credit = getMaxCredit();
    }

    @Override
    public void modifyingMoney(long money, City city, ColonyElements objType, String reason, String moreString) {
        BigInteger calculates = new BigInteger(String.valueOf(getMoney()));
        BigInteger max        = new BigInteger(String.valueOf(getMaxMoney1()));
        
        // 더하기
        calculates = calculates.add(new BigInteger(String.valueOf(money)));
        
        // 예산이 늘어나서 long 최대값 초과 시 예산2 필드로 절반 넘기기
        if(calculates.compareTo(max) >= 0) {
            // 합산 결과가 예산 1 (long 필드) 초과 위험으로 예산 2로 일부 금액 넘기기
            BigInteger halfs = calculates.divide(Constants.BIGINTEGER_2); // 계산 결과의 절반
            if(moneyOvers == null) moneyOvers = BigInteger.ZERO;
            
            // 예산2로 옮기기
            moneyOvers = moneyOvers.add(halfs);
            calculates = calculates.subtract(halfs); // 예산1 에서는 빼기
        }
        
        // 예산이 줄어들어서 long 필드의 절반 이하로 떨어졌으나, 예산2필드에 금액이 남아있는 경우
        //     long 필드 지원 가능한 값까지 옮기기
        if(calculates.compareTo(max.divide(Constants.BIGINTEGER_2)) >= 0 && getMoneyOvers().compareTo(BigInteger.ONE) > 0) {
            // 비교대상 1 - 예산1 -- 예산1 필드 최대값에서 예산1 현재 값을 뺄셈 (즉 예산1에 얼마나 더 들어갈 수 있는지의 값)
            BigInteger comp1 = new BigInteger(String.valueOf( getMaxMoney1() )).subtract(new BigInteger(String.valueOf(getMoney())));
            
            BigInteger lefts = null;
            if(getMoneyOvers().compareTo(comp1) >= 0) {
                // 예산2 값이 예산1 남은 공간보다 크면 - 예산1의 남은 공간만큼 최대한 옮기기
                lefts = comp1;
            } else {
                // 예산2 값이 예산1 남은 공간보다 작으면 - 예산2 전부 예산1로 옮기기
                lefts = getMoneyOvers();
            }
            if(lefts != null) {
                // 예산1로 옮기기
                calculates = calculates.add(lefts);
                setMoneyOvers(getMoneyOvers().subtract(lefts));
            }
            
        }
        
        setMoney(calculates.longValue());
        income = income.add(calculates);
        
        AccountingData data = new AccountingData(getTime(), money, reason, city, objType, moreString);
        addAccountingData(data);
    }

    @Override
    public long getTech() {
        return tech;
    }

    @Override
    public void setTech(long tech) {
        if(tech >= Long.MAX_VALUE - Integer.MAX_VALUE - 1) tech = Long.MAX_VALUE - Integer.MAX_VALUE - 1;
        if(tech < 0L) tech = 0L;
        this.tech = tech;
    }

    @Override
    public BigInteger getTime() {
        return time;
    }
    
    /** 시작 년도 반환 */
    @Override
    public BigInteger getStartYear() {
        return Constants.BIGINTEGER_3000;
    }
    
    /** 시작 예산 변환 */
    @Override
    public long getStartMoney() {
        return 5000000L;
    }
    
    /** 시작 신용도 반환 */
    @Override
    public int getStartCredit() {
        return 500;
    }
    
    @Override
    public String getDateString() {
        BigInteger originals = new BigInteger(getTime().toByteArray());
        BigInteger seconds, minutes, hour, date, month, year;
        seconds = new BigInteger(originals.toByteArray());
        // minutes = new BigInteger(BigInteger.ZERO.toByteArray());
        minutes = new BigInteger(BigInteger.ZERO.toByteArray());
        hour    = new BigInteger(BigInteger.ZERO.toByteArray());
        date    = new BigInteger(BigInteger.ONE.toByteArray());
        month   = new BigInteger(BigInteger.ONE.toByteArray());
        year    = getStartYear();
        
        // DIVIDE - MOD Calculation
        // Seconds
        if(seconds.compareTo(Constants.BIGINTEGER_10) >= 0) { // 여기서는 10초에 1분으로 계산
            minutes = minutes.add(new BigInteger(seconds.toByteArray()).divide(Constants.BIGINTEGER_10));
            seconds = seconds.mod(Constants.BIGINTEGER_10);
        }
        
        // Minutes (Once again)
        if(minutes.compareTo(Constants.BIGINTEGER_60) >= 0) {
            hour = hour.add(new BigInteger(minutes.toByteArray()).divide(Constants.BIGINTEGER_60));
            minutes = minutes.mod(Constants.BIGINTEGER_60);
        }
        
        // Hour
        if(hour.compareTo(Constants.BIGINTEGER_24) >= 0) {
            date = date.add(new BigInteger(hour.toByteArray()).divide(Constants.BIGINTEGER_24));
            hour = hour.mod(Constants.BIGINTEGER_24);
        }
        
        // DIVIDE - Loop Calculation
        // Seconds
        while(seconds.compareTo(Constants.BIGINTEGER_10) >= 0) {
            seconds = seconds.subtract(Constants.BIGINTEGER_10);
            minutes = minutes.add(BigInteger.ONE);
        }
        
        // Minutes (Once again)
        while(minutes.compareTo(Constants.BIGINTEGER_60) >= 0) {
            minutes = minutes.subtract(Constants.BIGINTEGER_60);
            hour = hour.add(BigInteger.ONE);
        }
        
        // Hour
        while(hour.compareTo(Constants.BIGINTEGER_24) >= 0) {
            hour = hour.subtract(Constants.BIGINTEGER_24);
            date = date.add(BigInteger.ONE);
        }
        
        // Date
        while(date.compareTo(Constants.BIGINTEGER_30) > 0) {
            date = date.subtract(Constants.BIGINTEGER_30);
            month = month.add(BigInteger.ONE);
        }
        
        // Month
        while(month.compareTo(Constants.BIGINTEGER_12) > 0) {
            month = month.subtract(Constants.BIGINTEGER_12);
            year = year.add(BigInteger.ONE);
        }
        
        // Create String
        StringBuilder res = new StringBuilder("");
        res = res.append(year.toString()).append("-").append(String.format("%02d", month.intValue())).append("-").append(String.format("%02d", date.intValue()));
        res = res.append(" ");
        res = res.append(String.format("%02d", hour.intValue())).append(":").append(String.format("%02d", minutes.intValue())).append(":").append(String.format("%02d", seconds.intValue()));
        
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
        if(! loanAvail.contains(l)) throw new KnownRuntimeException(ColonyManager.t("이 대출은 현재 사용할 수 없습니다."));
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
    public int cycleGap(Colony colony) { return 1; }
    
    

    @Override
    public void oneCycle(final int cycle, ColonyElements stage, Colony colony, int efficiency100, final ColonyPanel colPanel) {
        int idx;
        colony = this;
        stage = this;
        
        /** 체력이 없는 객체 삭제 */
        removeDeadObjects();
        
        // 대출 사이클 처리
        processLoans(cycle, colPanel);
        
        // 누락 연구 추가
        if(cycle == 0 || cycle % 600 == 0) addOmittedResearches();
        
        // 도시별 사이클 처리 (멀티쓰레드 처리)
        actionsOnCycle.clear();
        for(final City c : getCities()) {
            actionsOnCycle.add(new SingleAction() {    
                @Override
                public void run(int index) throws Throwable {
                    if(cycle % c.cycleGap(getSelf()) == 0) c.oneCycle(cycle, c, getSelf(), 100, colPanel);
                }
            });
        }
        workOnCycle = new SimultaneousWork(actionsOnCycle);
        workOnCycle.start();
        
        // 적 사이클 처리
        for(Enemy e : getEnemies()) {
            if(cycle % e.cycleGap(colony) == 0) e.oneCycle(cycle, stage, colony, efficiency100, colPanel);
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
                if(cycle % ev.getOccurCycle(this, null) == 0) {
                    if(ColonyManager.random() <= ev.getOccurRate(this, this, null)) ev.onEventOccured(this, this, null, colPanel);
                }
            }
        }
        
        // 예산이 음수인 경우 이자 발생
        if(getMoney() < 0L) {
            if(cycle % 60 == 0) {
                long interests = ((long) Math.floor(Math.abs(getMoney()) * 0.009)) * (-1);
                modifyingMoney(interests, null, colony, "Interest", colony.getName());
            }
        }
        
        // 1시간 마다 수익량 초기화
        if(income.mod(Constants.BIGINTEGER_600).equals(BigInteger.ZERO)) {
            income = BigInteger.ZERO;
        }
        
        // 탐험 진행
        for(Celestials c : getCelestials()) {
        	c.oneCycle(cycle, null, colony, efficiency100, colPanel);
        }
        
        // 시간 지남
        time = time.add(BigInteger.ONE);
    }
    
    /** 체력이 없는 객체 삭제 */
    protected void removeDeadObjects() {
        int idx;
        
        // 체력이 없는 도시 삭제
        idx = 0;
        while(idx < getCities().size()) {
            City cityOne = getCities().get(idx);
            if(cityOne.getHp() <= 0) {
                cityOne.dispose();
                getCities().remove(idx);
                ColonyManager.logGlobals(ColonyManager.t("도시 [CITY] 파괴됨").replace("[CITY]", cityOne.getName()), 1);
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
                ColonyManager.logGlobals(ColonyManager.t("적 [ENEMY] 파괴됨").replace("[ENEMY]", en.getName()), 1);
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
                ColonyManager.logGlobals(ColonyManager.t("대출 [LOAN] 상환됨").replace("[LOAN]", en.getName()), 1);
                continue;
            }
            idx++;
        }
    }
    
    /** 대출 사이클 처리 */
    protected void processLoans(int cycle, ColonyPanel colPanel) {
        for(Loan l : getLoanHave()) {
            if(cycle % l.cycleGap(this) == 0) l.oneCycle(cycle, null, this, 100, colPanel);
        }
        
        // 1년 지날 때마다, 사용 가능한 대출 목록 갱신
        if(cycle % 60 * 24 * 30 * 12 == 0) {
            resetAvailLoans();
            ColonyManager.logGlobals(ColonyManager.t("사용 가능한 대출 목록이 변경됨"), 1);
        }
    }
    
    /** 예약 작업 처리 */
    @SuppressWarnings("unused")
    protected void executeHoldJob(HoldingJob j) {
        String command, params;
        command = j.getCommand();
        params  = j.getParameter();
        
        try {
            if(command.equalsIgnoreCase("NewCity")) {
                City city = newCity();
                ColonyManager.logGlobals(ColonyManager.t("새 도시 [CITY] 건설됨").replace("[CITY]", city.getName()), 1);
                return;
            }
        } catch(RuntimeException ex) {
            throw ex;
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    /** 소속 도시들의 평균 X 좌표 반환 */
    public long getX() {
    	BigDecimal sums = BigDecimal.ZERO;
    	
    	List<City> cities = getCities();
    	if(cities.isEmpty()) return sums.longValue();
    	
    	for(City c : cities) { sums = sums.add(new BigDecimal(String.valueOf(c.getX()))); }
    	BigDecimal av = sums.divide(new BigDecimal(String.valueOf(cities.size())), 0, RoundingMode.HALF_UP);
    	return av.longValue();
    }
    
    /** 소속 도시들의 평균 Y 좌표 반환 */
    public long getY() {
    	BigDecimal sums = BigDecimal.ZERO;
    	
    	List<City> cities = getCities();
    	if(cities.isEmpty()) return sums.longValue();
    	
    	for(City c : cities) { sums = sums.add(new BigDecimal(String.valueOf(c.getY()))); }
    	BigDecimal av = sums.divide(new BigDecimal(String.valueOf(cities.size())), 0, RoundingMode.HALF_UP);
    	return av.longValue();
    }
    
    /** 소속 도시들의 평균 Z 좌표 반환 */
    public long getZ() {
    	BigDecimal sums = BigDecimal.ZERO;
    	
    	List<City> cities = getCities();
    	if(cities.isEmpty()) return sums.longValue();
    	
    	for(City c : cities) { sums = sums.add(new BigDecimal(String.valueOf(c.getZ()))); }
    	BigDecimal av = sums.divide(new BigDecimal(String.valueOf(cities.size())), 0, RoundingMode.HALF_UP);
    	return av.longValue();
    }
    
    @Override
	public void setX(long x) { 
    	// 도시들의 중심위치가 정착지의 위치이므로, 위치를 변경한다는 건 도시의 위치들을 모두 변경한다는 뜻.
    	// 변화량을 계산한 후, 소속 도시들 모두 해당 변화량만큼 이동
    	
    	long changes = getX() - x;
    	for(City c : getCities()) { c.setX(changes); }
    }

	@Override
	public void setY(long y) { 
		long changes = getY() - y;
    	for(City c : getCities()) { c.setY(changes); }
	}

	@Override
	public void setZ(long z) { 
		long changes = getZ() - z;
    	for(City c : getCities()) { c.setZ(changes); }
	}
    
    /** 새 도시의 좌표 지정 */
    protected void setNewCityCoordinate(City newCity) {
    	List<City> cities = getCities();
    	
    	Random rd = new Random();
    	boolean positive = rd.nextBoolean();
    	
    	long x = 0L;
    	long y = 0L;
    	long z = 0L;
    	
    	long stdx = (long) rd.nextInt();
    	long stdy = (long) rd.nextInt();
    	long stdz = (long) rd.nextInt();
    	
    	if(! cities.isEmpty()) {
    		stdx = getX();
    		stdy = getY();
    		stdz = getZ();
    	}
    	
    	while(true) {
    		// 적당한 범위 내로 랜덤 위치 설정
    	    x = stdx + (((rd.nextInt() + Integer.MAX_VALUE) / (Integer.MAX_VALUE / 4000)) * (positive ? 1 : (-1)));
            y = stdy + (((rd.nextInt() + Integer.MAX_VALUE) / (Integer.MAX_VALUE / 4000)) * (positive ? 1 : (-1)));
            z = stdz + (((rd.nextInt() + Integer.MAX_VALUE) / (Integer.MAX_VALUE / 4000)) * (positive ? 1 : (-1)));
            
            // 기 존재하는 도시들 중 이 좌표와 너무 가까운 도시가 있는지 체크
            boolean failed = false;
            for(City c : cities) {
            	if(c.getKey() == newCity.getKey()) continue;
            	
            	if(    Math.abs(x - c.getX()) <= 100L 
            	    && Math.abs(y - c.getY()) <= 100L 
            	    && Math.abs(z - c.getZ()) <= 100L
                  ) { failed = true; break; }
            }
            if(! failed) break;
    	}
    	
    	newCity.setX(x);
    	newCity.setY(y);
    	newCity.setZ(z);
    }
    
    /** 새 도시를 생성 */
    @Override
    public City newCity() {
        if(getCityCount() >= getMaxCityCount()) throw new KnownRuntimeException(ColonyManager.t("이 정착지에는 더 이상 도시를 건설할 수 없습니다."));
        
        City city = null;
        try { city = (City) Class.forName("org.duckdns.hjow.colonization.elements.city.NormalCity").newInstance(); } catch(Exception ex) { throw new RuntimeException("java-default-pack not detected."); }
        
        setNewCityCoordinate(city);
        addDefaultStarts(city);
        getCities().add(city);
        return city;
    }
    
    /** 새 도시에 기본 요소들 추가 */
    protected void addDefaultStarts(City city) {
        int idx;
        for(idx=0; idx<50; idx++) {
            city.createNewCitizen();
        }
        
        Facility fac;
        
        for(idx=0; idx<12; idx++) {
            try { fac = (Facility) Class.forName("org.duckdns.hjow.colonization.elements.facilities.ResidenceModule").newInstance(); } catch(Exception ex) { throw new RuntimeException("java-default-pack not detected."); }
            ((Residence) fac).setComportGrade(0);
            city.getFacility().add(fac);
        }
        
        for(idx=0; idx<3; idx++) {
            try { fac = (Facility) Class.forName("org.duckdns.hjow.colonization.elements.facilities.PowerStation").newInstance(); } catch(Exception ex) { throw new RuntimeException("java-default-pack not detected."); }
            city.getFacility().add(fac);
        }
        
        for(idx=0; idx<2; idx++) {
            try { 
                fac = (Facility) Class.forName("org.duckdns.hjow.colonization.elements.facilities.Restaurant").newInstance();
                for(idx=0; idx<10; idx++) {
                    ((Storage) fac).store((Product) Class.forName("org.duckdns.hjow.colonization.elements.products.food.NutritionBlock").newInstance());
                }
            } catch(Exception ex) { throw new RuntimeException("java-default-pack not detected."); }
            city.getFacility().add(fac);
        }
        
        try { fac = (Facility) Class.forName("org.duckdns.hjow.colonization.elements.facilities.SmallResearchCenter").newInstance(); } catch(Exception ex) { throw new RuntimeException("java-default-pack not detected."); }
        city.getFacility().add(fac);
        
        for(idx=0; idx<2; idx++) {
            try { fac = (Facility) Class.forName("org.duckdns.hjow.colonization.elements.facilities.SmallFactory").newInstance(); } catch(Exception ex) { throw new RuntimeException("java-default-pack not detected."); }
            city.getFacility().add(fac);
        }
        
        for(idx=0; idx<2; idx++) {
            try { fac = (Facility) Class.forName("org.duckdns.hjow.colonization.elements.facilities.CapsuleBusStation").newInstance(); } catch(Exception ex) { throw new RuntimeException("java-default-pack not detected."); }
            city.getFacility().add(fac);
        }
        
        try { fac = (Facility) Class.forName("org.duckdns.hjow.colonization.elements.facilities.CargoRailSystem").newInstance(); } catch(Exception ex) { throw new RuntimeException("java-default-pack not detected."); }
        city.getFacility().add(fac);
        
        Collections.sort(city.getFacility(), new Comparator<Facility>() {
            @Override
            public int compare(Facility o1, Facility o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        
        Collections.sort(city.getCitizens(), new Comparator<Citizen>() {
            @Override
            public int compare(Citizen o1, Citizen o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
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
    
    @Override
    public int getMaxHp() {
        return 1000000;
    }
    
    /** 신용도 최대값 반환 */
    public final int getMaxCredit() {
        return 1000;
    }
    
    /** 예산 1 의 최대값 (이를 넘어가면 예산 2로 넘김) */
    public final long getMaxMoney1() {
        return 9000000000000000000L;
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
        StringBuilder desc = new StringBuilder("");
        desc = desc.append("\t").append("HP : ").append(ColonyManager.formatInt(getHp())).append(" / ").append(ColonyManager.formatInt(getMaxHp()));
        desc = desc.append("\t").append(ColonyManager.t("예산") + " : ").append(ColonyManager.formatInt(getMoneyTotals(), false, 2));
        desc = desc.append("\t").append(ColonyManager.t("기술") + " : ").append(ColonyManager.formatInt(getTech()));
        desc = desc.append("\t").append(ColonyManager.t("도시 수") + " : ").append(ColonyManager.formatInt(getCityCount())).append(" / ").append(ColonyManager.formatInt(getMaxCityCount()));
        desc = desc.append("\t").append(ColonyManager.t("총 인구") + " : ").append(ColonyManager.formatInt(getCitizenCount()));
        desc = desc.append("\t").append(ColonyManager.t("신용도") + " : ").append(ColonyManager.formatInt(getCredit())).append(" / ").append(ColonyManager.formatInt(getMaxCredit()));
        if(checked) desc = desc.append("\t").append(ColonyManager.t("인증됨"));
        
        return desc.toString().trim();
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    /** 도시 내 소속 함선들 반환 (말그대로 소속 함선으로, 실제 위치는 도시 내가 아닐수도 있음) */
    public Vector<Ship> getShips() {
    	Vector<Ship> list = new Vector<Ship>();
    	for(City c : getCities()) {
    		list.addAll(c.getShips());
    	}
    	return list;
    }
    
    /** 해당 위치의 모든 함선들 반환 */
    public Vector<Ship> getShips(int x, int y, int z) {
    	Vector<Ship> list = new Vector<Ship>();
    	for(City c : getCities()) {
    		list.addAll(c.getShips(x, y, z));
    	}
    	return list;
    }
    
    /** 해당 위치의 해당 범위 내 모든 함선들 반환 */
    public Vector<Ship> getShips(int x, int y, int z, int dist) {
    	Vector<Ship> list = new Vector<Ship>();
    	for(City c : getCities()) {
    		list.addAll(c.getShips(x, y, z, dist));
    	}
    	return list;
    }
    
    /** Json 데이터를 읽어 City 불러오기 */
    protected abstract City createCityInstance(JsonObject json) throws IOException;

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
        json.put("buildNo", clientBuildNo);
        if(getMoneyOvers() != null) json.put("money2", getMoneyOvers().toString());
        
        json.put("x", String.valueOf(getX()));
        json.put("y", String.valueOf(getY()));
        json.put("z", String.valueOf(getZ()));
        
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
        for(Celestials c : getCelestials()) { list.add(c.toJson(details, col, city)); }
        json.put("celestials", list);
        
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
        
        if(checked) json.put("checkkey", getCheckerSerial());
        else        json.put("checkkey", "");
        
        // 추가 정보 (불러올 때는 필요가 없는) 첨가
        json.put("maxHp", String.valueOf(getMaxHp()));
        
        if(details) {
            json.put("statusString", StaticMethods.encodeString(getStatusString(null)));
        }
        
        if(details) {
            String str = getStatusString(null);
            if(str == null) str = "";
            json.put("statusString", StaticMethods.encodeString(str));
            
            str = getDateString();
            json.put("dates", StaticMethods.encodeString(str));
        }
        
        return json;
    }
    
    @Override
    public void fromJson(JsonObject json) {
        if(! ("Colony".equals(json.get("type")) || getType().equals(json.get("type")))) throw new KnownRuntimeException("This object is not Colony type.");
        setName(json.get("name").toString());
        key = Long.parseLong(json.get("key").toString());
        try { setHp(Integer.parseInt(json.get("hp").toString()));                     } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); hp         = 10;              }
        try { setDifficulty(Integer.parseInt(json.get("difficulty").toString()));     } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); difficulty = 0;               }
        try { setMoney(Long.parseLong(json.get("money").toString()));                 } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); money      = 0;               }
        try { setTech(Long.parseLong(json.get("tech").toString()));                   } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); tech       = 0;               }
        try { setTime(new BigInteger(json.get("time").toString()));                   } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); time       = BigInteger.ZERO; }
        try { setCredit(Integer.parseInt(json.get("credit").toString()));             } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); credit     = 500;             }
        try {
            if(json.containsKey("money2")) {
                moneyOvers = new BigInteger(json.get("money2").toString());
            }
        } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); moneyOvers = BigInteger.ZERO;             }
        try { clientVersion = json.get("version").toString();                         } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); clientVersion = "0.0.1"; } 
        try { clientBuildNo = json.get("buildNo").toString();                         } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); clientBuildNo = "1"; } 
        
        JsonArray list = null;
        try { list = (JsonArray) json.get("cities"); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        cities.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        City city = createCityInstance((JsonObject) o);
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
        try { list = (JsonArray) json.get("celestials"); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        celestials.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        Celestials cele = new Celestials();
                        cele.fromJson((JsonObject) o);
                        celestials.add(cele);
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
        for(int idx=0; idx<getName().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getName().charAt(idx))).multiply(ColonyManager.getCheckerConst(getClientBuildNo()))); }
        for(int idx=0; idx<clientVersion.length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) clientVersion.charAt(idx))).multiply(ColonyManager.getCheckerConst(getClientBuildNo()))); }
        for(int idx=0; idx<clientBuildNo.length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) clientBuildNo.charAt(idx))).multiply(Constants.BIGINTEGER_31.multiply(ColonyManager.getCheckerConst(getClientBuildNo())))); }
        res = res.add(getMoneyTotals()).multiply(Constants.BIGINTEGER_23.multiply(ColonyManager.getCheckerConst(getClientBuildNo())));
        res = res.add(new BigInteger(String.valueOf(getHp())).multiply(Constants.BIGINTEGER_3.multiply(ColonyManager.getCheckerConst(getClientBuildNo()))));
        res = res.add(new BigInteger(String.valueOf(getDifficulty())).multiply(Constants.BIGINTEGER_17.multiply(ColonyManager.getCheckerConst(getClientBuildNo()))));
        
        for(City       c : getCities())     { res = res.add(c.getCheckerValue().multiply(ColonyManager.getCheckerConst(getClientBuildNo()))); if(c instanceof CustomElement) res = BigInteger.ZERO; }
        for(Loan       l : getLoanAvail())  { res = res.add(l.getCheckerValue().multiply(ColonyManager.getCheckerConst(getClientBuildNo()))); if(l instanceof CustomElement) res = BigInteger.ZERO; }
        for(Loan       l : getLoanHave())   { res = res.add(l.getCheckerValue().multiply(ColonyManager.getCheckerConst(getClientBuildNo()))); if(l instanceof CustomElement) res = BigInteger.ZERO; }
        for(Celestials c : getCelestials()) { res = res.add(c.getCheckerValue().multiply(ColonyManager.getCheckerConst(getClientBuildNo()))); if(c instanceof CustomElement) res = BigInteger.ZERO; }
        
        return res;
    }
    
    /** 인증용 시리얼 문자열 */
    public String getCheckerSerial() {
        if(! checked) return "";
        
        StringBuilder res = new StringBuilder(String.valueOf(getCheckerValue()));
        // TODO
        
        String serial = res.toString().trim();
        res = null;
        
        return SecurityUtil.hash(serial, "SHA-256");
    }
    
    /** 인증 제거 (인증 제거 사유 발생 시 호출) */
    @Override
    public void disableChecked() {
        checked = false;
    }
    
    /** 인증 유효 여부 반환 */
    @Override
    public boolean isCheckEnabled() {
        return checked;
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
    
    @Override
    public boolean isMarkedAsRefresh() {
        return fNeedRefresh;
    }

    @Override
    public void markAsRefresh(boolean f) {
        fNeedRefresh = f;
    }
    
    @Override
    public void markAsRefreshChildren(boolean f) {
        markAsRefresh(f);
        for(City     ct : getCities() )    { ct.markAsRefreshChildren(f); }
        for(Enemy    en : getEnemies())    { en.markAsRefreshChildren(f); }
        for(Research r  : getResearches()) { r.markAsRefreshChildren(f);  }
        for(Loan     l  : getLoanAvail())  { l.markAsRefreshChildren(f);  }
        for(Loan     l  : getLoanHave())   { l.markAsRefreshChildren(f);  }
    }
    
    /** 자기자신 반환 */
    public Colony getSelf() {
        return this;
    }
    
    /** 주변 천체 목록 랜덤화 (단, 천체 목록이 이미 생성된 경우 아무 동작하지 않음) */
    @Override
    public void randomizeCelestials() {
    	if(! celestials.isEmpty()) return;
    	Celestials newOne;
    	
    	Random rand = new Random();
		int intRand = ((int) (Math.abs(rand.nextInt())) / (Integer.MAX_VALUE / 1000)) + 1000;
		int grade = 1;
		
	    for(int idx=0; idx<intRand; idx++) {
	    	newOne = Celestials.createRandom(getX(), getY(), getZ(), 10000, (int) (100000 + (Math.random() * idx)), grade + (Math.random() >= 0.5 ? 1 : 0) + (Math.random() >= 0.8 ? 1 : 0) );
	    	if(idx % 100 == 0) grade++;
	    	celestials.add(newOne);
	    }
    }
    
    public static String getColonyClassName() {
        return "Colony";
    }
    
    /** 난이도 배열 생성 */
    protected static int[] createAvailableDifficulties(int min, int max) {
        int[] arr = new int[max - min+1];
        int now = min;
        for(int idx=0; idx<arr.length; idx++) {
            arr[idx] = now;
            now++;
        }
        return arr;
    }
    
    /** 사용 가능한 난이도 목록 반환 */
    public static int[] getAvailableDifficulties() {
        return createAvailableDifficulties(1, 9);
    }
    
    public static String getColonyClassTitle() {
        return ColonyManager.t("정착지");
    }
    
    public static String getColonyClassDescription() {
        return "";
    }
}
