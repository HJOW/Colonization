package org.duckdns.hjow.colonization.elements.city;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.colonization.ColonyClassLoader;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.elements.HoldingJob;
import org.duckdns.hjow.colonization.elements.custom.CustomElement;
import org.duckdns.hjow.colonization.elements.enemies.Enemy;
import org.duckdns.hjow.colonization.elements.facilities.BusinessCenter;
import org.duckdns.hjow.colonization.elements.facilities.FacilityManager;
import org.duckdns.hjow.colonization.elements.facilities.Factory;
import org.duckdns.hjow.colonization.elements.facilities.Home;
import org.duckdns.hjow.colonization.elements.facilities.NetworkFacility;
import org.duckdns.hjow.colonization.elements.facilities.PowerPlant;
import org.duckdns.hjow.colonization.elements.facilities.ResearchCenter;
import org.duckdns.hjow.colonization.elements.facilities.Residence;
import org.duckdns.hjow.colonization.elements.facilities.TransportStation;
import org.duckdns.hjow.colonization.elements.policy.Policy;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.events.TimeEvent;
import org.duckdns.hjow.colonization.ui.ColonyManagerUI;
import org.duckdns.hjow.colonization.ui.ColonyPanel;
import org.duckdns.hjow.commons.exception.KnownRuntimeException;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.HexUtil;

/** 도시 구현 클래스 */
public abstract class City implements ColonyElements {
    private static final long serialVersionUID = -8442328554683565064L;
    protected volatile long key = ColonyManager.generateKey();
    protected transient boolean fNeedRefresh = true;
    
    protected String name = ColonyManager.t("도시") + "_" + ColonyManager.getNaturalNumberFrom(key);
    protected List<Facility>   facility = new Vector<Facility>();
    protected List<Citizen>    citizens = new Vector<Citizen>();
    protected List<Policy>     policies = new Vector<Policy>();
    protected List<Enemy>      enemies  = new Vector<Enemy>();
    protected List<HoldingJob> holdings = new Vector<HoldingJob>();
    protected int hp = getMaxHp();
    protected int spaces = 500 + ((int) ( 700 * Math.random() ));
    protected int tax = 10;
    
    protected transient long calculatedTransPoint     = 0L;
    protected transient long calculatedTransPointLeft = 0L;
    
    public City() {
        resetPolicies();
    }
    
    public City(JsonObject json) throws IOException {
        this();
        fromJson(json);
    }
    
    @Override
    public long getKey() {
        return key;
    }
    public void setKey(long key) {
        this.key = key;
    }

    @Override
    public String getName() {
        return name;
    }
    @Override
    public final String getClassName() {
    	return getClass().getSimpleName();
    }
    
    public List<Facility> getFacility() {
        return facility;
    }
    public Facility getFacility(long facKey) {
        for(Facility f : getFacility()) {
            if(f.getKey() == facKey) return f;
        }
        return null;
    }
    /** 특정 타입의 시설 목록 반환 */
    public List<Facility> getFacilities(Class<?> facilityClass) {
    	List<Facility> list = new ArrayList<Facility>();
    	for(Facility f : getFacility()) {
    		if(f.getClass() == facilityClass && (! list.contains(f))) list.add(f);
    	}
    	return list;
    }
    
    /** 정책 목록 반환 */
    public List<Policy> getPolicies() {
		return policies;
	}

	public void setPolicies(List<Policy> policies) {
		this.policies = policies;
	}

	/** 도시 이름 변경 */
    public void setName(String name) {
        this.name = name;
        markAsRefresh(true);
    }
    public void setFacility(List<Facility> facility) {
        this.facility = facility;
        markAsRefresh(true);
    }
    public List<Citizen> getCitizens() {
        return citizens;
    }

    public void setCitizens(List<Citizen> citizens) {
        this.citizens = citizens;
        markAsRefresh(true);
    }
    
    public Citizen getCitizen(long citizenKey) {
        for(Citizen c : getCitizens()) {
            if(c.getKey() == citizenKey) return c;
        }
        return null;
    }
    
    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
    }

    public List<HoldingJob> getHoldings() {
        return holdings;
    }
    
    public HoldingJob getHoldingJobOne(long key) {
        for(HoldingJob j : getHoldings()) {
            if(j.getKey() == key) return j;
        }
        return null;
    }

    public void setHoldings(List<HoldingJob> holdings) {
        this.holdings = holdings;
    }
    
    public void addHoldingJob(HoldingJob job) {
    	holdings.add(job);
    	markAsRefresh(true);
    }

    @Override
    public void addHp(int amount) {
        hp += amount;
        int mx = getMaxHp();
        if(hp >= mx) hp = mx;
        if(hp <   0) hp = 0;
        markAsRefresh(true);
    }

    @Override
    public int getHp() {
        return hp;
    }
    
    @Override
    public void setHp(int hp) {
        this.hp = hp;
        int mx = getMaxHp();
        if(hp >= mx) hp = mx;
        if(hp <   0) hp = 0;
        markAsRefresh(true);
    }
    
    @Override
    public short getDefenceType() {
        return ColonyManager.DEFENCETYPE_BUILDING;
    }

    @Override
    public int getDefencePoint() {
        return 9;
    }
    
    /** 이 도시의 전체 공간량 반환 */
    public int getSpaces() {
        return spaces;
    }

    public void setSpaces(int spaces) {
        this.spaces = spaces;
        markAsRefresh(true);
    }
    
    /** 사용 중인 공간량 반환 */
    public int getUsingSpaces() {
    	int res = 0;
    	
    	for(Facility f : getFacility()) {
    		res += f.getSpaceSize();
    	}
    	
    	for(HoldingJob j : getHoldings()) {
    		res += j.getUsingSpace();
    	}
    	
    	return res;
    }
    
    /** 잔여 공간량 반환 */
    public int getLeftSpaces() {
    	return getSpaces() - getUsingSpaces();
    }

    @Override
    public int getMaxHp() {
        int max = 100000;
        for(Facility f : facility) {
            max += f.increasingCityMaxHP();
        }
        return max;
    }
    /** 세금 수치 반환 (%) */
    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
        markAsRefresh(true);
    }
    
    /** 기본 제공 교통 점수 */
    protected long getDefaultTransportPoint() {
        return 300L;
    }
    
    @Override
    public int cycleGap(Colony colony) { return 1; }

    @Override
    public void oneCycle(int cycle, City city, Colony colony, int efficiency100, ColonyPanel colPanel) { // city should be a self
        int idx;
        
        // 출산율 및 이주 계산
        processBornChance(cycle, colony, efficiency100);
        processMoveInChance(cycle, colony, efficiency100);
        processMoveOutChance(cycle, colony, efficiency100);
        
        // 전력 생산량 및 교통점수 계산
        long power    = getPowerGenerate(colony);
        long trans    = getDefaultTransportPoint(); 
        long networks = getNetworkCapacity(colony);
        
        double powBoostRate = 1.0;
        double traBoostRate = 1.0;
        double netBoostRate = 1.0;
        
        for(Policy p : policies) {
        	powBoostRate = powBoostRate * p.getPowerSupplyRate();
        	traBoostRate = traBoostRate * p.getTransSupplyRate();
        	netBoostRate = netBoostRate * p.getNetworkSupplyRate();
        }
        
        power    = new BigDecimal(String.valueOf(power   )).multiply(new BigDecimal(String.valueOf(powBoostRate))).longValue();
        trans    = new BigDecimal(String.valueOf(trans   )).multiply(new BigDecimal(String.valueOf(traBoostRate))).longValue();
        networks = new BigDecimal(String.valueOf(networks)).multiply(new BigDecimal(String.valueOf(netBoostRate))).longValue();
        
        // 시설 파워 및 효율성 계산, 효과 처리
        for(Facility f : getFacility()) {
            int efficiency = efficiency100;
            double boostRate = 1.0;
            
            for(Policy p : policies) {
            	boostRate = boostRate * p.getFacilityBonusRate(f);
            }
            f.setBoostRate(boostRate);
            
            // 전력 사용량 계산
            int efficiencyPow = efficiency100;
            int consume = f.getPowerConsume();
            
            if(consume == 0 || power >= consume) {
                efficiencyPow = efficiency100;
                power -= consume;
            } else if(consume >= 1 && power >= 1 && power < consume) {
                efficiencyPow = (int) (power * efficiency100) / consume;
                power = 0L;
            } else {
                efficiencyPow = 0;
                power = 0L;
            }
            
            // 직원 부족 시 효율 저하 (절반으로)
            int efficiencyWorker = efficiency100;
            if(f.getWorkerNeeded() >= 1) {
                int working = f.getWorkingCitizensCount(city, colony);
                if(f.getWorkerNeeded() > working) {
                    efficiencyWorker = (int) Math.round(efficiencyWorker * 0.5);
                }
            }
            
            // 네트워크 사용 시설 계산
            int efficiencyNetwork = efficiency100;
            if(! ((f instanceof NetworkFacility) || (f instanceof Residence))) {
                if(networks <= 0L) {
                    // 네트워크 용량 초과 시 효율 저하
                	double lowerRate = 0.8;
                	if(f instanceof BusinessCenter) lowerRate = 0.25; 
                    efficiencyNetwork = (int) Math.round(efficiencyNetwork * lowerRate);
                } else {
                    networks = networks - 1L;
                }
            }
            
            efficiency = (int) Math.round(   ((efficiencyPow / 100.0) * (efficiencyWorker / 100.0) * (efficiencyNetwork / 100.0)) * 100  );
            
            if(cycle % (colony.getAccountingPeriod()) == 0) {
                // 시설의 비용 처리
                processFacilityFees(f, colony);
            }
            
            // 시설 효과 처리
            if(cycle % f.cycleGap(colony) == 0) f.oneCycle(cycle, this, colony, efficiency, colPanel);
            
            // 교통시설인 경우 교통점수 계산
            if(f instanceof TransportStation) {    
                TransportStation t = (TransportStation) f;
                
                // 두 쌍 이상이 되어야 효력 발생
                boolean exists = false;
                for(Facility fc : getFacility()) {
                    if(fc.getKey() == t.getKey()) continue;
                    if(fc.getType().equalsIgnoreCase(t.getType())) { exists = true; break; }
                }
                
                if(exists) {
                    // 점수 합산
                    int adds = t.getCapacity();
                    adds = (int) Math.floor( adds * (efficiency / 100.0));
                    trans += adds;
                }
            }
        }
        calculatedTransPoint = trans;
        
        // 시민 처리
        for(Citizen ct : getCitizens()) {
        	if(cycle % ct.cycleGap(colony) == 0) ct.oneCycle(cycle, city, colony, efficiency100, colPanel);
            
            if(networks <= 0L) {
                if(ct.getHappy() > 70) ct.setHappy(70); // 네트워크 사용 불가 시 행복도 상한 적용
            } else {
                networks = networks - 1L;
            }
        }
        
        // 정책 처리
        for(Policy p : getPolicies()) {
        	if(! p.isEnabled()) continue;
        	if(! p.isAvail(colony, city)) {
        		p.setEnabled(false);
        		continue;
        	}
        	
        	// 비용 처리
        	if(cycle % (60 * 24 * 30) == 0) {
        		colony.modifyingMoney((-1) * p.getMonthlyFee(colony, city), city, p, "Policy", ColonyManager.t("월간 정책 집행 예산"));
        	}
        	
        	// 효과 처리
        	if(cycle % p.cycleGap(colony) == 0) {
        		p.oneCycle(cycle, city, colony, efficiency100, colPanel);
        	}
        }
        
        // 적 사이클 처리
        for(Enemy e : getEnemies()) {
        	if(cycle % e.cycleGap(colony) == 0) e.oneCycle(cycle, city, colony, efficiency100, colPanel);
        }
        
        // 사망 개체 제거
        removeDeads(colony);
        
        // 대중교통 포인트 부족 시설 구직자 만들기
        trans = applyTransport(trans, colony);
        
        // 거주자 및 일자리 할당 (다음 사이클에 적용)
        allocateHome(colony);
        allocateWorkers(trans, colony);
        
        // 예약 작업 처리
        for(HoldingJob h : getHoldings()) {
            int lefts = h.getCycleLeft();
            
            if("NewFacility".equalsIgnoreCase(h.getCommand())) {
                if(h.getWorkingCitizens(this).isEmpty()) continue; // 시설 건설의 경우, 건설에 시민이 필요함
            }
            
            h.decreaseCycle();
            lefts = h.getCycleLeft();
            if(lefts >= 1) continue; // 아직 사이클이 남아있으면 execute 하지 않고 건너뜀
            
            h.setCompleted(true);
            executeHoldJob(h);
        }
        
        // 완료된 예약 작업 삭제
        idx = 0;
        while(idx < getHoldings().size()) {
            HoldingJob j = getHoldings().get(idx);
            if(j.getCycleLeft() <= 0 || j.isCompleted()) {
                // 건설하고 있는 시민들 노숙자로 변경
                for(Citizen c : j.getWorkingCitizens(this)) {
                    if(c.getBuildingFacility() == j.getKey()) c.setBuildingFacility(0L);
                }
                
                // 삭제
                getHoldings().remove(idx);
                continue;
            }
            idx++;
        }
        
        // 이벤트 처리
        for(TimeEvent ev : colony.getEvents()) {
            if(colony.getTime().compareTo(new BigInteger("" + ev.getOccurMinimumTime(colony))) < 0) continue;
            
            if(ev.getEventSize() == TimeEvent.EVENTSIZE_CITY) {
                if(cycle % ev.getOccurCycle(colony, this) == 0) {
                    if(Math.random() <= ev.getOccurRate(this, colony, this)) ev.onEventOccured(this, colony, this, colPanel);
                }
            } else if(ev.getEventSize() == TimeEvent.EVENTSIZE_FACILITY) {
                for(Facility fac : getFacility()) {
                    if(cycle % ev.getOccurCycle(colony, this) == 0) {
                        if(Math.random() <= ev.getOccurRate(fac, colony, this)) ev.onEventOccured(fac, colony, this, colPanel);
                    }
                }
            }
        }
        
        // 예산이 마이너스인 경우 행복도 등 감소
        if(colony.getMoney() < 0L) {
        	for(Citizen c : getCitizens()) {
        		if(c.getHappy() > 30) c.setHappy(30);
        		if(cycle % 60 == 0) c.addHappy(-1);
        	}
        }
    }
    
    /** 시설의 비용 처리 */
    protected void processFacilityFees(Facility f, Colony colony) {
        // 임금 처리
        for(Citizen c : f.getWorkingCitizens(this, colony)) {
            long sal = f.getSalary(this, colony);
            
            // 세금 떼기
            double tax = getTax() / 100.0;
            long taxAmount = (long) Math.floor(sal * tax);
            sal -= taxAmount;
            
            // 예산에서 빼서 시민에게 주기
            colony.modifyingMoney( sal * (-1), this, f, "Salary", c.getName());
            c.setMoney(c.getMoney() + sal);
        }
        // 유지비 처리
        colony.modifyingMoney( f.getMaintainFee(this, colony) * (-1), this, f, "Maintain", f.getName());
    }
    
    /** 예약 작업 처리 */
    protected void executeHoldJob(HoldingJob j) {
        String command, params;
        command = j.getCommand();
        params  = j.getParameter();
        
        try {
            if(command.equalsIgnoreCase("NewCitizen")) {
                if(getCitizenCount() < Integer.MAX_VALUE) {
                	Citizen c = createNewCitizen();
                	ColonyManager.logGlobals(ColonyManager.t("시민 [CITIZEN] 입주 신고").replace("[CITIZEN]", c.getName()), 1);
                }
                j.setCompleted(true);
                return;
            }
            
            if(command.equalsIgnoreCase("NewFacility")) {
                if(params == null) return;
                if(params.equals("")) return;
                if(getFacility().size() >= getSpaces()) return;
                
                Class<?> facilityClass = FacilityManager.getFacilityClass(params);
                if(facilityClass == null) return;
                Facility newOne = (Facility) facilityClass.newInstance();
                getFacility().add(newOne);
                j.setCompleted(true);
                ColonyManager.logGlobals(ColonyManager.t("시설 [FACILITY] 건설 완료").replace("[FACILITY]", newOne.getName()), 1);
            }
            
            if(command.equalsIgnoreCase("UpgradeFacility")) {
                if(params == null) return;
                if(params.equals("")) return;
                
                long l = Long.parseLong(params.trim());
                for(Facility f : getFacility()) {
                    if(f.getKey() == l) {
                        f.setLevel(f.getLevel() + 1);
                        j.setCompleted(true);
                        ColonyManager.logGlobals(ColonyManager.t("시설 [FACILITY] 증축 완료").replace("[FACILITY]", f.getName()), 1);
                        break;
                    }
                }
            }
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
        
    }
    /** 건설 중인 시설 수 반환 */
    public int getHoldingBuildFacility() {
        int res = 0;
        for(HoldingJob j : getHoldings()) {
            if("NewFacility".equalsIgnoreCase(j.getCommand())) res++; 
        }
        return res;
    }
    
    /** 총 전력 생산량 반환 (사용량 미반영) */
    public long getPowerGenerate(Colony col) {
        long power = 0L;
        
        for(Facility f : facility) {
            int working = f.getWorkingCitizensCount(this, col);
            if(working >= 1) {
                if(f instanceof PowerPlant) {
                    if(working >= f.getWorkerNeeded()) power += ((PowerPlant) f).getPowerGenerate(col, this);
                    else                               power += (((PowerPlant) f).getPowerGenerate(col, this)) / 2;
                }
            }
        }
        
        return power;
    }
    
    /** 총 네트워크 지원량 반환 (사용량 미반영) */
    public long getNetworkCapacity(Colony col) {
        long capa = 10L;
        
        for(Facility f : facility) {
            if(! (f instanceof NetworkFacility)) continue;
            int working = f.getWorkingCitizensCount(this, col);
            if(working >= 1) {
                if(working >= f.getWorkerNeeded()) capa += ((NetworkFacility) f).getCapacity();
                else                               capa += (((NetworkFacility) f).getCapacity()) / 2;
            }
        }
        
        return capa;
    }
    
    /** HP가 0인 시민과 시설, 적 제거 */
    public void removeDeads(Colony col) {
        int idx = 0;
        
        // 시민
        while(idx < getCitizens().size()) {
            Citizen c = getCitizens().get(idx);
            if(c.getHp() <= 0) {
                c.dispose();
                getCitizens().remove(idx);
                ColonyManager.logGlobals(ColonyManager.t("시민 [CITIZEN] 이 사망").replace("[CITIZEN]", c.getName()), 1);
                continue;
            }
            idx++;
        }
        
        // 시설
        idx = 0;
        while(idx < getFacility().size()) {
            Facility f = getFacility().get(idx);
            if(f.getHp() <= 0) {
                // 일하는 중인 시민 구직자 만들기
                long facKey = f.getKey();
                for(Citizen c : getCitizens()) {
                    if(c.getWorkingFacility() == facKey) {
                    	c.setWorkingFacility(0L);
                    	ColonyManager.logGlobals(ColonyManager.t("시민 [CITIZEN] 이 일자리를 잃음 (시설이 파괴됨)").replace("[CITIZEN]", c.getName()), 1);
                    }
                }
                
                if(f instanceof Home) {
                    // 살던 시민 노숙자 만들기
                    for(Citizen c : getCitizens()) {
                        if(c.getLivingHome() == facKey) {
                        	c.setLivingHome(0L);
                        	ColonyManager.logGlobals(ColonyManager.t("시민 [CITIZEN] 이 집을 잃음 (시설이 파괴됨)").replace("[CITIZEN]", c.getName()), 1);
                        }
                    }
                }
                
                // 시설 제거
                f.dispose();
                getFacility().remove(idx);
                ColonyManager.logGlobals(ColonyManager.t("시설 [FACILITY] 파괴됨").replace("[FACILITY]", f.getName()), 1);
                continue;
            }
            idx++;
        }
        
        // 적
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
    }
    
    /** 노숙자 수 계산 */
    public int getHomelesses() {
        int counts = 0;
        for(Citizen c : getCitizens()) {
            if(c.isHomeless()) counts++;
        }
        return counts;
    }
    
    /** 백수의 수 계산 */
    public int getJobSeekers() {
        int counts = 0;
        for(Citizen c : getCitizens()) {
            if(c.isJobSeeker()) counts++;
        }
        return counts;
    }
    
    /** 노숙자를 주거 모듈에 할당 */
    protected void allocateHome(Colony col) {
        // 시민들 확인해서, 존재하지 않는 주거 모듈에 산다고 되어 있으면 리셋
        for(Citizen c : getCitizens()) {
            // 거주민 여부 확인
            if(c.isHomeless()) continue;
            
            if(c.getLivingHome() != 0L) {
                Home h = c.getLivingHome(this);
                if(h == null) {
                	c.setLivingHome(0L);
                	ColonyManager.logGlobals(ColonyManager.t("시민 [CITIZEN] 이 집을 잃음").replace("[CITIZEN]", c.getName()), 1);
                }
            }
        }
        
        // 시민들 별로 주거 할당
        for(Citizen c : getCitizens()) {
            // 노숙자 여부 판단
            if(! c.isHomeless()) continue;
            
            // 비어있는 주거 모듈 찾기
            for(Facility f : facility) {
                if(f instanceof Home) {
                    Home home = (Home) f;
                    if(! home.isFull(this, col)) {
                        c.setLivingHome(home.getKey());
                        ColonyManager.logGlobals(ColonyManager.t("시민 [CITIZEN] 이 주거 시설 [FACILITY] 에 거주 시작").replace("[CITIZEN]", c.getName()).replace("[FACILITY]", home.getName()), 1);
                        break;
                    }
                }
            }
        }
    }
    
    /** 백수에게 새 직장 할당 */
    protected void allocateWorkers(long transportPoint, Colony col) {
        long trans = transportPoint;
        
        List<Facility> list = new ArrayList<Facility>();
        // 시민들 확인해서, 존재하지 않는 직장에 있는지 확인
        for(final Citizen c : getCitizens()) {
            // 직장인 여부 판단
            if(c.isJobSeeker()) continue;
            
            // 존재하지 않는 직장인 경우 (직장 시설이 없어졌거나 등등) 리셋
            if(c.getWorkingFacility() != 0L) {
                Facility f = c.getWorkingFacility(this);
                if(f == null) {
                	c.setWorkingFacility(0L);
                	ColonyManager.logGlobals(ColonyManager.t("시민 [CITIZEN] 이 무직자로 판명").replace("[CITIZEN]", c.getName()), 1);
                }
            }
            
            // 존재하지 않는 건설 현장인 경우 (완공되었거나 등등) 리셋
            if(c.getBuildingFacility() != 0L) {
                HoldingJob j = c.getBuildingFacility(this);
                if(j == null) {
                	c.setBuildingFacility(0L);
                	ColonyManager.logGlobals(ColonyManager.t("시민 [CITIZEN] 이 무직자로 판명").replace("[CITIZEN]", c.getName()), 1);
                }
            }
        }
        
        // 시민별로 일자리 할당
        for(final Citizen c : getCitizens()) {
            // 백수 여부 판단
            if(! c.isJobSeeker()) continue;
            
            // 교통 점수가 부족하면 일자리 할당 중단
            if(trans <= 0) {
            	ColonyManager.logGlobals(ColonyManager.t("교통 인프라 부족으로 일자리 할당 중단 !"), 1);
            	break;
            }
            
            // 건설 일자리들 확인
            list.clear();
            HoldingJob building = null;
            for(HoldingJob j : getHoldings()) {
                if(! "NewFacility".equalsIgnoreCase(j.getCommand())) continue;
                int working = j.getWorkingCitizens(this).size();
                if(working <= 0) {
                    building = j;
                    break;
                }
            }
            if(building != null) {
                c.setBuildingFacility(building.getKey()); // 건설 시에는 교통 점수 계산 안 함
                continue;
            }
            
            // 일자리가 당장 필요한 직장들 찾기
            list.clear();
            for(Facility f : facility) {
                if(f.getWorkerNeeded() > f.getWorkingCitizensCount(this, col)) {
                    list.add(f);
                }
            }
            
            if(! list.isEmpty()) {
                // 이 시민이 일하기에 적합한 정도 순으로 정렬
                Collections.sort(list, new Comparator<Facility>() {
                    @Override
                    public int compare(Facility o1, Facility o2) {
                        int res = o1.getWorkerSuitability(c) - o2.getWorkerSuitability(c);
                        if(res <  0) return -1;
                        if(res == 0) return  0;
                        return 1;
                    }
                });
                
                if(trans <= 0) break;
                
                Facility f = list.get(0);
                c.setWorkingFacility(f.getKey());
                trans = trans - 1;
                
                ColonyManager.logGlobals(ColonyManager.t("시민 [CITIZEN] 이 적합도 [SUITABLITY] 으로 시설 [FACILITY] 에 취직").replace("[CITIZEN]", c.getName()).replace("[SUITABLITY]", f.getWorkerSuitability(c) + "").replace("[FACILITY]", f.getName()), 1);
                continue;
            }
            
            // 일자리를 더 제공할 수 있는 직장들 찾기 - 정렬 고민해야...
            list.clear();
            for(Facility f : facility) {
                if(f.getWorkerCapacity() > f.getWorkingCitizensCount(this, col)) {
                    list.add(f);
                }
            }
            
            if(! list.isEmpty()) {
                // 이 시민이 일하기에 적합한 정도 순으로 정렬
                Collections.sort(list, new Comparator<Facility>() {
                    @Override
                    public int compare(Facility o1, Facility o2) {
                        int res = o1.getWorkerSuitability(c) - o2.getWorkerSuitability(c);
                        if(res <  0) return -1;
                        if(res == 0) return  0;
                        return 1;
                    }
                });
                
                if(trans <= 0) break;
                
                Facility f = list.get(0);
                c.setWorkingFacility(f.getKey());
                trans = trans - 1;
                
                ColonyManager.logGlobals(ColonyManager.t("시민 [CITIZEN] 이 적합도 [SUITABLITY] 으로 시설 [FACILITY] 에 취직").replace("[CITIZEN]", c.getName()).replace("[SUITABLITY]", f.getWorkerSuitability(c) + "").replace("[FACILITY]", f.getName()), 1);
            }
        }
        
        calculatedTransPointLeft = trans;
    }
    
    /** 대중교통 포인트 계산, 벗어나는 시민들 구직자 만들기, 남은 교통점수 반환 */
    protected long applyTransport(long transPoint, Colony colony) {
        long now = transPoint;
        
        for(Facility f : getFacility()) {
        	if((f instanceof Residence) || (f instanceof TransportStation)) continue;
            if(now >= 1L) now = now - f.getWorkingCitizensCount(this, colony);
            if(now <= 0L) {
                for(Citizen c : f.getWorkingCitizens(this, colony)) {
                    c.setWorkingFacility(0L);
                    ColonyManager.logGlobals(ColonyManager.t("시민 [CITIZEN] 이 교통 인프라 부족으로 출퇴근이 불가능해져 일자리를 잃음").replace("[CITIZEN]", c.getName()), 1);
                    break; // 1명씩만 구직자 만들기
                }
            }
        }
        
        return now;
    }
    
    /** 새 시민 생성 */
    public Citizen createNewCitizen() {
        Citizen c = new Citizen();
        getCitizens().add(c);
        return c;
    }
    
    /**  시민 수 반환 */
    public int getCitizenCount() {
        return getCitizens().size();
    }
    
    /** 이 도시 내 거주 시설 수용 인원 반환 (이미 거주 중인 자리도 포함) */
    public long getHomeCapacity() {
        long res = 0L;
        for(Facility f : getFacility()) {
            if(f instanceof Home) {
                res += f.getCapacity();
            }
        }
        return res;
    }
    
    /** 이 도시 내 잔여 거주 시설 수용 인원 반환 */
    public long getHomeCapacityLeft() {
        long res = 0L;
        for(Facility f : getFacility()) {
            if(f instanceof Home) {
                res += f.getCapacity();
                for(Citizen c : getCitizens()) { if(c.getLivingHome() == f.getKey()) res--; }
            }
        }
        return res;
    }
    
    /** 이 도시 내 직장 자리 수 반환 (이미 일하고 있는 자리 수도 포함) */
    public long getJobsCount() {
        long res = 0L;
        for(Facility f : getFacility()) {
            res += f.getWorkerCapacity();
        }
        return res;
    }
    
    /** 이 도시 내 잔여 직장 자리 수 반환 */
    public long getLeftJobsCount() {
        long res = 0L;
        for(Facility f : getFacility()) {
            res += f.getWorkerCapacity();
            for(Citizen c : getCitizens()) { if(c.getWorkingFacility() == f.getKey()) res--; }
        }
        return res;
    }
    
    
    /** 출산률 계산 */
    public double getBornChanceRate(Colony col, int efficiency100) {
        double res = efficiency100 / 100.0;
        if(res > 50.0) res = 50.0;
        return res;
    }
    
    /** 출산률 적용 */
    public void processBornChance(int cycle, Colony col, int efficiency100) {
        if(getCitizenCount() >= Integer.MAX_VALUE) return;
        
        if(cycle % 600 == 0) {
            if(getHomeCapacity() > getCitizenCount()) {
                if(Math.random() < ( getBornChanceRate(col, efficiency100))) {
                    createNewCitizen();
                }
            }
        }
    }
    
    /** 이주율 계산 (이주해 들어올 확률만 계산) */
    public double getMoveChangeRate(Colony col, int efficiency100) {
        // 거주지가 부족하면 이주 0
        if(getHomeCapacity() <= getCitizenCount()) return 0.0;
        
        // 행복도 체크
        boolean happinessAccepts = false;
        double rate = getAverageHappiness();
        if(getCitizenCount() <= 0) {
            happinessAccepts = true;
            rate = 10.0; // 시민이 아예 없으면 10 적용
        } else {
            if(rate >= 50.0) happinessAccepts = true;
            rate = rate - 50.0;
        }
        
        if(! happinessAccepts) return 0.0;
        if(rate <= 0.0) return 0.0;
        
        // 세금 적용
        int tax = getTax();
        //    세금이 10% 보다 높으면 효율 감소
        if(tax >= 16) efficiency100 = (int) Math.round(efficiency100 / 4.0);
        if(tax == 15) efficiency100 = (int) Math.round(efficiency100 / 2.0);
        if(tax == 14) efficiency100 = (int) Math.round(efficiency100 / 1.7);
        if(tax == 13) efficiency100 = (int) Math.round(efficiency100 / 1.5);
        if(tax == 12) efficiency100 = (int) Math.round(efficiency100 / 1.3);
        if(tax == 11) efficiency100 = (int) Math.round(efficiency100 / 1.1);
        //    세금이 9% 보다 높으면 효율 증가 (100까지 남은 수치의 일정 비율만큼 가산)
        if(tax ==  9) efficiency100 = efficiency100 + (int) Math.round((100.0 - efficiency100) / 10.0);
        if(tax ==  8) efficiency100 = efficiency100 + (int) Math.round((100.0 - efficiency100) /  9.0);
        if(tax ==  7) efficiency100 = efficiency100 + (int) Math.round((100.0 - efficiency100) /  8.0);
        if(tax ==  6) efficiency100 = efficiency100 + (int) Math.round((100.0 - efficiency100) /  7.0);
        if(tax <=  5) efficiency100 = efficiency100 + (int) Math.round((100.0 - efficiency100) /  6.0);
        
        // 효율 적용
        rate = rate * (efficiency100 / 100.0);
        
        // 백분율 to 0~1
        return rate / 100.0;
    }
    
    /** 평균행복도 계산 (참고 - 시민의 행복도는 최소 0, 최초값은 50) */
    public double getAverageHappiness() {
        BigDecimal happiness = new BigDecimal("0");
        if(getCitizenCount() >= 1) {
            for(Citizen c : getCitizens()) {
                happiness = happiness.add(new BigDecimal(String.valueOf(c.getHappy())));
            }
            happiness = happiness.divide(new BigDecimal(String.valueOf(getCitizenCount())), 5, BigDecimal.ROUND_DOWN);
            return happiness.doubleValue();
        }
        return 0.0;
    }
    
    /** 이주율 (입주) 적용 */
    public void processMoveInChance(int cycle, Colony col, int efficiency100) {
        if(getCitizenCount() >= Integer.MAX_VALUE) return;
        
        double moveRate = getMoveChangeRate(col, efficiency100);
        if(cycle % 600 == 0) {
            if(Math.random() < moveRate) {
                createNewCitizen();
            }
        }
    }
    
    /** 이주율 (탈출) 적용 */
    public void processMoveOutChance(int cycle, Colony col, int efficiency100) {
        if(cycle % 600 == 0) {
            if(getCitizenCount() >= 1) {
                int idx = 0;
                List<Citizen> citizens = getCitizens();
                while(idx < citizens.size()) {
                    // 세금 적용
                    int tax = getTax();
                    double rates    = 0.0;
                    double multiple = 1.0;
                    //    세금이 10% 보다 높으면 효율 감소
                    if(tax >= 16) { multiple = 3.0; rates = 0.30; }
                    if(tax == 15) { multiple = 1.9; rates = 0.16; }
                    if(tax == 14) { multiple = 1.6; rates = 0.12; }
                    if(tax == 13) { multiple = 1.4; rates = 0.07; }
                    if(tax == 12) { multiple = 1.2; rates = 0.04; }
                    if(tax == 11) { multiple = 1.1; rates = 0.02; }
                    //    세금이 9% 보다 높으면 효율 증가 (100까지 남은 수치의 일정 비율만큼 가산)
                    if(tax ==  9) { multiple = 0.9; }
                    if(tax ==  8) { multiple = 0.8; }
                    if(tax ==  7) { multiple = 0.7; }
                    if(tax ==  6) { multiple = 0.6; }
                    if(tax <=  5) { multiple = 0.5; }
                    
                    //    세금으로 인한 탈출 적용
                    if(Math.random() <= rates) {
                        Citizen c = citizens.get(idx);
                        c.dispose();
                        citizens.remove(idx); // 탈출
                        continue;
                    }
                    
                    // 행복도로 인한 탈출 가능성 계산 (20보다 낮은 경우만 적용) - 여기에도 세금이 반영됨
                    int happy = citizens.get(idx).getHappy();
                    if(happy > 100) happy = 100;
                    
                    rates = 0.0;
                    if(happy < 20) { // 20보다 낮으면 확률 계산
                        rates = (happy / 100.0);
                        rates = 0.2 - rates; // 최대값이 0.2 이므로...
                        if(rates < 0.0) rates = 0.0;
                        rates = rates * multiple; // 세금 추가 적용
                        
                        if(Math.random() <= rates) {
                            Citizen c = citizens.get(idx);
                            c.dispose();
                            citizens.remove(idx); // 탈출
                            continue;
                        }
                    }
                    
                    idx++;
                }
            }
        }
    }
    
    @Override
    public JsonObject toJson() {
        return toJson(false, null, this);
    }
    
    @Override
    public JsonObject toJson(boolean details, Colony col, City city) {
        city = this;
        
        JsonObject json = new JsonObject();
        json.put("type", "City");
        json.put("name", getName());
        json.put("key", String.valueOf(getKey()));
        json.put("hp", String.valueOf(getHp()));
        json.put("tax", new Integer(getTax()));
        json.put("spaces", new Integer(getSpaces()));
        json.put("className", getClassName());
        
        JsonArray list = new JsonArray();
        for(Facility f : getFacility()) { list.add(f.toJson(details, col, city)); }
        json.put("facilities", list);
        
        list = new JsonArray();
        for(Citizen c : getCitizens()) { list.add(c.toJson(details, col, city)); }
        json.put("citizens", list);
        
        list = new JsonArray();
        for(HoldingJob h : holdings) { list.add(h.toJson()); }
        json.put("holdings", list);
        
        list = new JsonArray();
        for(Enemy h : enemies) { list.add(h.toJson(details, col, city)); }
        json.put("enemies", list);
        
        list = new JsonArray();
        for(Policy p : policies) { list.add(p.toJson(details, col, city)); }
        json.put("policies", list);
        
        // 추가 정보 (불러올 때는 필요가 없는) 첨가
        json.put("maxHp", String.valueOf(getMaxHp()));
        json.put("spaceUsing", new Integer(getUsingSpaces()));
        json.put("spaceLeft", new Integer(getLeftSpaces()));
        
        if(details) {
            json.put("statusString", HexUtil.encodeString(getStatusString(col, null)));
        }
        
        return json;
    }
    
    @Override
    public void fromJson(JsonObject json) {
        if(! "City".equals(json.get("type"))) throw new KnownRuntimeException("This object is not City type.");
        try { setName(json.get("name").toString());                       } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setName("");  }
        try { key = Long.parseLong(json.get("key").toString());           } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setKey(ColonyManager.generateKey()); }
        try { setHp(Integer.parseInt(json.get("hp").toString()));         } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setHp(0);     }
        try { setTax(Integer.parseInt(json.get("tax").toString()));       } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setTax(0);    }
        try { setSpaces(Integer.parseInt(json.get("spaces").toString())); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setSpaces(0); }
        
        JsonArray list = null;
        try { list = (JsonArray) json.get("facilities"); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        facility.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        Facility fac = FacilityManager.fromJson((JsonObject) o);
                        if(fac == null) throw new NullPointerException("Cannot found these facility type " + o);
                        facility.add(fac);
                    } catch(Exception ex) {
                        GlobalLogs.processExceptionOccured(ex, false);
                    }
                }
            }
        }
        
        list = null;
        try { list = (JsonArray) json.get("citizens"); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        citizens.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        Citizen cit = new Citizen((JsonObject) o);
                        citizens.add(cit);
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
                        addHoldingJob(h);
                    } catch(Exception ex) {
                        GlobalLogs.processExceptionOccured(ex, false);
                    }
                }
            }
        }
        
        list = null;
        try { list = (JsonArray) json.get("enemies"); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        enemies.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        Enemy en = Enemy.createEnemyFromJson((JsonObject) o);
                        enemies.add(en);
                    } catch(Exception ex) {
                        GlobalLogs.processExceptionOccured(ex, false);
                    }
                }
            }
        }
        
        list = null;
        try { list = (JsonArray) json.get("policies"); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        policies.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                    	JsonObject jsonIn = (JsonObject) o;
                    	String type = jsonIn.get("type").toString();
                    	Policy p = ColonyClassLoader.createPolicyInstance(type);
                    	p.fromJson(jsonIn);
                        policies.add(p);
                    } catch(Exception ex) {
                        GlobalLogs.processExceptionOccured(ex, false);
                    }
                }
            }
        }
    }
    
    /** 상태 메시지 생성 (UI 내 JTextArea 에 출력됨) */
    public String getStatusString(Colony col, ColonyManagerUI superInstance) {
        StringBuilder desc = new StringBuilder("");
        
        DecimalFormat formatterInt  = new DecimalFormat("#,###,###,###,###,##0");
        DecimalFormat formatterRate = new DecimalFormat("##0.00");
        
        long powerConsume = 0L;
        for(Facility f : getFacility()) {
            powerConsume += f.getPowerConsume();
        }
        
        if(col == null && superInstance != null) col = getColony(superInstance);
        
        desc = desc.append("\n").append("HP : ").append(formatterInt.format(getHp())).append(" / ").append(formatterInt.format(getMaxHp()));
        desc = desc.append("\n").append(ColonyManager.t("전력") + " : ").append(formatterInt.format(powerConsume)).append(" / ").append(formatterInt.format(getPowerGenerate(col)));
        desc = desc.append("\n").append(ColonyManager.t("공간") + " : ").append(formatterInt.format(getUsingSpaces())).append(" / ").append(formatterInt.format(getSpaces()));
        if(getCalculatedTransPoint() > 0L) desc = desc.append("\n").append(ColonyManager.t("교통 용량") + " : ").append(formatterInt.format(getCalculatedTransPoint() - getCalculatedTransLeftPoint())).append(" / ").append(formatterInt.format(getCalculatedTransPoint()));
        desc = desc.append("\n").append(ColonyManager.t("인구") + " : ").append(formatterInt.format(getCitizenCount()));
        desc = desc.append("\n").append(ColonyManager.t("시설 수") + " : ").append(formatterInt.format(getFacility().size()));
        desc = desc.append("\n").append(ColonyManager.t("평균 행복도") + " : ").append(formatterRate.format(getAverageHappiness()));
        desc = desc.append("\n").append(ColonyManager.t("거주 수용량") + " : ").append(formatterInt.format(getHomeCapacity()));
        desc = desc.append("\n").append(ColonyManager.t("직장 수") + " : ").append(formatterInt.format(getJobsCount()));
        desc = desc.append("\n").append(ColonyManager.t("노숙자") + " : ").append(formatterInt.format(getHomelesses()));
        desc = desc.append("\n").append(ColonyManager.t("백수") + " : ").append(formatterInt.format(getJobSeekers()));
        desc = desc.append("\n");
        for(Facility f : getFacility()) {
        	if(f instanceof Factory) {
        		Factory fc = (Factory) f;
        		if(fc.getProductType() != null) desc = desc.append("\n").append(ColonyManager.t("[FACILITYNAME] 에서 [PRODUCTTYPE] 생산 중.").replace("[FACILITYNAME]", f.getName()).replace("[PRODUCTTYPE]", fc.getProducingName() ));
        	}
        }
        desc = desc.append("\n");
        for(Facility f : getFacility()) {
        	if(f instanceof ResearchCenter) {
        		ResearchCenter rc = (ResearchCenter) f;
        		
        		Research rNow = rc.getResearch(col);
        		String rsName = " - ";
        		if(rNow != null) rsName = rNow.getTitle();
        		
        		String percents = "";
        		if(rNow != null) percents = ColonyManager.formatRate(rNow.getProgressPercents()) + "%";
        		
        		desc = desc.append("\n").append(ColonyManager.t("[FACILITYNAME] 에서 연구 진행 중 : [RESEARCH] ([PROGRESS])").replace("[FACILITYNAME]", f.getName()).replace("[RESEARCH]", rsName).replace("[PROGRESS]", percents));
        	}
        }
        
        return desc.toString().trim();
    }
    
    /** 소속 정착지 찾기 */
    public Colony getColony(ColonyManagerUI man) {
        return man.getColonyFrom(this);
    }
    
    /** 계산된 교통 점수 반환 */
    public long getCalculatedTransPoint() {
        return calculatedTransPoint;
    }
    
    /** 계산된 잔여 교통 점수 반환 */
    public long getCalculatedTransLeftPoint() {
        return calculatedTransPointLeft;
    }
    
    /** 정책 목록 갱신 */
    public void resetPolicies() {
    	policies.clear();
    	// TODO
    }
    
    @Override
    public BigInteger getCheckerValue() {
        BigInteger res = new BigInteger(String.valueOf(getKey()));
        for(int idx=0; idx<getName().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getName().charAt(idx)))); }
        res = res.add(new BigInteger(String.valueOf(getHp())));
        for(Facility   f : getFacility()) { res = res.add(f.getCheckerValue()); if(f instanceof CustomElement) res = BigInteger.ZERO; }
        for(Citizen    c : getCitizens()) { res = res.add(c.getCheckerValue()); if(c instanceof CustomElement) res = BigInteger.ZERO; }
        for(Enemy      e : getEnemies())  { res = res.add(e.getCheckerValue()); if(e instanceof CustomElement) res = BigInteger.ZERO; }
        for(HoldingJob h : getHoldings()) { res = res.add(h.getCheckerValue()); if(h instanceof CustomElement) res = BigInteger.ZERO; }
        
        return res;
    }

    @Override
    public void dispose() {
        for(Citizen ct : citizens) {
            ct.dispose();
        }
        citizens.clear();
        for(Facility f : facility) {
            f.dispose();
        }
        facility.clear();
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
        for(Citizen  c  : getCitizens()) { c.markAsRefreshChildren(f); }
        for(Facility t  : getFacility()) { t.markAsRefreshChildren(f); }
        for(Enemy    en : getEnemies() ) { en.markAsRefreshChildren(f); }
    }
    
    /** 도시 건설 비용 */
    public static long getBuildingNewCityFee(Colony col) { return 1000000L; };
}
