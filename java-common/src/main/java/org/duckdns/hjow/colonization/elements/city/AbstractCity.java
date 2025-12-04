package org.duckdns.hjow.colonization.elements.city;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.duckdns.hjow.colonization.ColonyClassManager;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.ColonyManagerInterface;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.constants.Constants;
import org.duckdns.hjow.colonization.constants.StaticMethods;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.DefaultCitizen;
import org.duckdns.hjow.colonization.elements.DefaultHoldingJob;
import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.elements.HoldingJob;
import org.duckdns.hjow.colonization.elements.Space;
import org.duckdns.hjow.colonization.elements.custom.CustomElement;
import org.duckdns.hjow.colonization.elements.enemies.AbstractEnemy;
import org.duckdns.hjow.colonization.elements.enemies.Enemy;
import org.duckdns.hjow.colonization.elements.facilities.BusinessCenter;
import org.duckdns.hjow.colonization.elements.facilities.FacilityInformation;
import org.duckdns.hjow.colonization.elements.facilities.FacilityManager;
import org.duckdns.hjow.colonization.elements.facilities.Factory;
import org.duckdns.hjow.colonization.elements.facilities.Home;
import org.duckdns.hjow.colonization.elements.facilities.NetworkFacility;
import org.duckdns.hjow.colonization.elements.facilities.Port;
import org.duckdns.hjow.colonization.elements.facilities.PowerPlant;
import org.duckdns.hjow.colonization.elements.facilities.ResearchCenter;
import org.duckdns.hjow.colonization.elements.facilities.Residence;
import org.duckdns.hjow.colonization.elements.facilities.TransportStation;
import org.duckdns.hjow.colonization.elements.policy.Policy;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.elements.ship.Satellite;
import org.duckdns.hjow.colonization.elements.ship.Ship;
import org.duckdns.hjow.colonization.events.TimeEvent;
import org.duckdns.hjow.colonization.ui.ColonyPanel;
import org.duckdns.hjow.commons.exception.KnownRuntimeException;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.ui.graphics.Coordinate3D;
import org.duckdns.hjow.commons.util.DataUtil;

/** 도시 공통 구현 클래스 */
public abstract class AbstractCity implements City {
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
    protected int spaces = 500 + ((int) ( 700 * ColonyManager.random() ));
    protected int tax = 10;
    
    protected long x = 0L;
    protected long y = 0L;
    protected long z = 0L;
    
    protected transient long calculatedTransPoint     = 0L;
    protected transient long calculatedTransPointLeft = 0L;
    
    public AbstractCity() {
        resetPolicies();
    }
    
    public AbstractCity(JsonObject json) throws IOException {
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
    
    @Override
    public String getTooltip() {
        return getName();
    }
    
    @Override
    public List<Facility> getFacility() {
        return facility;
    }
    
    @Override
    public Facility getFacility(long facKey) {
        for(Facility f : getFacility()) {
            if(f.getKey() == facKey) return f;
        }
        return null;
    }
    /** 특정 타입의 시설 목록 반환 */
    @Override
    public List<Facility> getFacilities(Class<?> facilityClass) {
        List<Facility> list = new ArrayList<Facility>();
        for(Facility f : getFacility()) {
            if(f.getClass() == facilityClass && (! list.contains(f))) list.add(f);
        }
        return list;
    }
    
    /** 정책 목록 반환 */
    @Override
    public List<Policy> getPolicies() {
        return policies;
    }

    public void setPolicies(List<Policy> policies) {
        this.policies = policies;
    }

    /** 도시 이름 변경 */
    @Override
    public void setName(String name) {
        this.name = name;
        markAsRefresh(true);
    }
    public void setFacility(List<Facility> facility) {
        this.facility = facility;
        markAsRefresh(true);
    }
    @Override
    public List<Citizen> getCitizens() {
        return citizens;
    }

    public void setCitizens(List<Citizen> citizens) {
        this.citizens = citizens;
        markAsRefresh(true);
    }
    
    @Override
    public Citizen getCitizen(long citizenKey) {
        for(Citizen c : getCitizens()) {
            if(c.getKey() == citizenKey) return c;
        }
        return null;
    }
    
    @Override
    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
    }
    
    /** 적 개체 등록 (이미 존재 시 무시, 도시와 위치 좌표가 동일해야 등록됨) - 중복 등록 방지 기능 포함 */
    @Override
    public void addEnemy(Enemy enemy) {
    	if(this.enemies.contains(enemy)) return;
    	if(! (enemy.getX() == getX() && enemy.getY() == getY() && enemy.getZ() == getZ())) return;
    	if(enemy.getHp() <= 0) return;
    	this.enemies.add(enemy);
    }

    @Override
    public List<HoldingJob> getHoldings() {
        return holdings;
    }
    
    @Override
    public HoldingJob getHoldingJobOne(long key) {
        for(HoldingJob j : getHoldings()) {
            if(j.getKey() == key) return j;
        }
        return null;
    }

    public void setHoldings(List<HoldingJob> holdings) {
        this.holdings = holdings;
    }
    
    @Override
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
        return Constants.DEFENCETYPE_BUILDING;
    }

    @Override
    public int getDefencePoint() {
        return 9;
    }
    
    /** 이 도시의 전체 공간량 반환 */
    @Override
    public int getSpaces() {
        return spaces;
    }

    public void setSpaces(int spaces) {
        this.spaces = spaces;
        markAsRefresh(true);
    }
    
    /** 사용 중인 공간량 반환 */
    @Override
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
    @Override
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
    @Override
    public int getTax() {
        return tax;
    }

    @Override
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
    
    /** 어떤 값이 std 이상의 값임을 보장함. std 보다 작은 경우 std 반환. 양수인 경우만 값을 반환. 절대값과는 다르니 주의 ! */
    protected double guaranteeBiggerValue(double val, double std) {
        if(val < std) return std;
        return val;
    }

    @Override
    public void oneCycle(int cycle, ColonyElements stage, Space space, Colony colony, int efficiency100, ColonyPanel colPanel) { // city should be a self
        int idx;
        boolean warnNetworkNeeded = false;
        
        // 각종 보너스 정책들
        double powBoostRate   = 1.0;
        double traBoostRate   = 1.0;
        double netBoostRate   = 1.0;
        double birthBoostRate = 1.0;
        
        for(Policy p : policies) {
            if(! p.isEnabled()) continue;
            if(! p.isAvail(colony, this)) {
                p.setEnabled(false);
                continue;
            }
            
            powBoostRate   = powBoostRate   * guaranteeBiggerValue(p.getPowerSupplyRate(  colony, this), 0.5);
            traBoostRate   = traBoostRate   * guaranteeBiggerValue(p.getTransSupplyRate(  colony, this), 0.5);
            netBoostRate   = netBoostRate   * guaranteeBiggerValue(p.getNetworkSupplyRate(colony, this), 0.5);
            birthBoostRate = birthBoostRate * guaranteeBiggerValue(p.getBirthBonusRate(   colony, this), 0.5);
        }
        
        // 출산율 및 이주 계산
        processBornChance(cycle, colony, efficiency100, birthBoostRate);
        processMoveInChance(cycle, colony, efficiency100);
        processMoveOutChance(cycle, colony, efficiency100);
        
        // 전력 생산량 및 교통점수 계산
        long power    = getPowerGenerate(colony);
        long trans    = getDefaultTransportPoint(); 
        long networks = getNetworkCapacity(colony);
        
        power    = new BigDecimal(String.valueOf(power   )).multiply(new BigDecimal(String.valueOf(powBoostRate))).longValue();
        trans    = new BigDecimal(String.valueOf(trans   )).multiply(new BigDecimal(String.valueOf(traBoostRate))).longValue();
        networks = new BigDecimal(String.valueOf(networks)).multiply(new BigDecimal(String.valueOf(netBoostRate))).longValue();
        
        // 시설 파워 및 효율성 계산, 효과 처리
        for(Facility f : getFacility()) {
            int efficiency = efficiency100;
            double boostRate = 1.0;
            
            // 스크립트 기반 시설이면 정착지 인증 해제
            if(f.isScriptBased()) colony.disableChecked();
            
            // 정책 스캔해서 부스트 보너스 계산
            for(Policy p : policies) {
                if(! p.isEnabled()) continue;
                if(! p.isAvail(colony, this)) {
                    p.setEnabled(false);
                    continue;
                }
                
                boostRate = boostRate * p.getFacilityBonusRate(colony, this, f);
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
                ColonyManager.logGlobals(ColonyManager.t("전력 부족으로 [FACILITY] 효율 저하").replace("[FACILITY]", f.getName()), 1);
            } else {
                efficiencyPow = 0;
                power = 0L;
                ColonyManager.logGlobals(ColonyManager.t("전력 부족으로 [FACILITY] 효율 저하").replace("[FACILITY]", f.getName()), 1);
            }
            
            // 직원 부족 시 효율 저하 (절반으로)
            int efficiencyWorker = efficiency100;
            if(f.getWorkerNeeded() >= 1) {
                int working = f.getWorkingCitizensCount(this, colony);
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
                    ColonyManager.logGlobals(ColonyManager.t("네트워크 인프라 부족으로 [FACILITY] 효율 저하").replace("[FACILITY]", f.getName()), 1);
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
            if(cycle % f.cycleGap(colony) == 0) f.oneCycle(cycle, this, space, colony, efficiency, colPanel);
            
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
            if(cycle % ct.cycleGap(colony) == 0) ct.oneCycle(cycle, this, space, colony, efficiency100, colPanel);
            
            if(networks <= 0L) {
                if(ct.getHappy() > 70) ct.setHappy(70); // 네트워크 사용 불가 시 행복도 상한 적용
                
                if(! warnNetworkNeeded) {
                    ColonyManager.logGlobals(ColonyManager.t("네트워크 인프라 부족으로 시민 행복도 저하 중"), 1);
                    warnNetworkNeeded = true;
                }
            } else {
                networks = networks - 1L;
            }
        }
        
        // 정책 처리
        for(Policy p : getPolicies()) {
            if(! p.isEnabled()) continue;
            if(! p.isAvail(colony, this)) {
                p.setEnabled(false);
                continue;
            }
            
            // 비용 처리
            if(cycle % (60 * 24 * 30) == 0) {
                colony.modifyingMoney((-1) * p.getMonthlyFee(colony, this), this, p, "Policy", ColonyManager.t("월간 정책 집행 예산"));
            }
            
            // 효과 처리
            if(cycle % p.cycleGap(colony) == 0) {
                p.oneCycle(cycle, this, space, colony, efficiency100, colPanel);
            }
        }
        
        // 함선 사이클 처리 (파괴된 함선 제거는 시설 oneCycle 에서 처리)
        for(Ship s : getShips()) {
        	if(s.getHp() <= 0) continue;
        	if(s.getLeftProgress() >= 1) {
        		s.decreaseProgress(this, colony);
        		if(s.getLeftProgress() <= 0) s.increaseLevel();
        		continue; 
        	}
        	if(s.getLevel() <= 0) continue;
        	
        	boolean isHere = (getX() == s.getX() && getY() == s.getY() && getZ() == s.getZ());
        	
        	ColonyElements target = null;
        	if(isHere) target = this;
        	
        	if(cycle % s.cycleGap(colony) == 0) s.oneCycle(cycle, target, space, colony, efficiency100, colPanel);
        }
        
        // 적 사이클 처리
        for(Enemy e : getEnemies()) {
        	if(e.getHp() <= 0) continue;
            if(cycle % e.cycleGap(colony) == 0) e.oneCycle(cycle, this, space, colony, efficiency100, colPanel);
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
                    if(ColonyManager.random() <= ev.getOccurRate(this, colony, this)) ev.onEventOccured(this, colony, this, colPanel);
                }
            } else if(ev.getEventSize() == TimeEvent.EVENTSIZE_FACILITY) {
                for(Facility fac : getFacility()) {
                    if(cycle % ev.getOccurCycle(colony, this) == 0) {
                        if(ColonyManager.random() <= ev.getOccurRate(fac, colony, this)) ev.onEventOccured(fac, colony, this, colPanel);
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
            ColonyManager.logGlobals(ColonyManager.t("도시 부도 사태로 시민 행복도 저하 중"), 1);
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
                
                FacilityInformation info = FacilityManager.getFacilityInformation(params);
                Facility newOne = info.createFacility();
                if(newOne != null) {
                    getFacility().add(newOne);
                    ColonyManager.logGlobals(ColonyManager.t("시설 [FACILITY] 건설 완료").replace("[FACILITY]", newOne.getName()), 1);
                }
                
                j.setCompleted(true);
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
    @Override
    public int getHoldingBuildFacility() {
        int res = 0;
        for(HoldingJob j : getHoldings()) {
            if("NewFacility".equalsIgnoreCase(j.getCommand())) res++; 
        }
        return res;
    }
    
    /** 총 전력 생산량 반환 (사용량 미반영) */
    @Override
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
        
        for(Ship ship : getShips()) {
        	if(ship instanceof Satellite) {
        		if(ship.getHp() <= 0) continue;
        		if(! (ship.getX() == getX() && ship.getY() == getY() && ship.getZ() == getZ())) continue;
        		
        		int gen = ((Satellite) ship).getPowerGenerate(col, this);
        		if(gen >= 1) power += gen;
        	}
        }
        
        return power;
    }
    
    /** 총 네트워크 지원량 반환 (사용량 미반영) */
    @Override
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
    protected void removeDeads(Colony col) {
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
            
            // HP 0 체트
            if(en.getHp() <= 0) {
                getEnemies().remove(idx); // dispose 하지 않고 제거 (dispose 는 정착지의 oneCycle 에서 처리)
                continue;
            }
            
            // 위치 체크
            if(! (en.getX() == getX() && en.getY() == getY() && en.getZ() == getZ())) {
            	getEnemies().remove(idx); // dispose 하지 않고 제거 (dispose 는 정착지의 oneCycle 에서 처리)
                continue;
            }
            
            // 정착지에 등록되어 있는지 체크
            if(! col.contains(en)) {
            	en.dispose(); // 이 경우는 dispose 해야 함. 정착지에도 등록 안되어 있으면 dispose 호출할 주체가 없음.
            	getEnemies().remove(idx);
                continue;
            }
            
            idx++;
        }
    }
    
    /** 노숙자 수 계산 */
    @Override
    public int getHomelesses() {
        int counts = 0;
        for(Citizen c : getCitizens()) {
            if(c.isHomeless()) counts++;
        }
        return counts;
    }
    
    /** 백수의 수 계산 */
    @Override
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
    
    /** 새 시민 생성 (20세로 생성됨) */
    @Override
    public Citizen createNewCitizen() {
        Citizen c = new DefaultCitizen();
        c.setAgeYear(Constants.BIGINTEGER_20);
        getCitizens().add(c);
        return c;
    }
    
    /** 새 시민 생성 (나이 지정) */
    @Override
    public Citizen createNewCitizen(int ageYear) {
        Citizen c = new DefaultCitizen();
        c.setAgeYear(String.valueOf(ageYear));
        getCitizens().add(c);
        return c;
    }
    
    /**  시민 수 반환 */
    @Override
    public int getCitizenCount() {
        return getCitizens().size();
    }
    
    /** 이 도시 내 거주 시설 수용 인원 반환 (이미 거주 중인 자리도 포함) */
    @Override
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
    @Override
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
    @Override
    public long getJobsCount() {
        long res = 0L;
        for(Facility f : getFacility()) {
            res += f.getWorkerCapacity();
        }
        return res;
    }
    
    /** 이 도시 내 잔여 직장 자리 수 반환 */
    @Override
    public long getLeftJobsCount() {
        long res = 0L;
        for(Facility f : getFacility()) {
            res += f.getWorkerCapacity();
            for(Citizen c : getCitizens()) { if(c.getWorkingFacility() == f.getKey()) res--; }
        }
        return res;
    }
    
    @Override
    public long getX() {
		return x;
	}

	public void setX(long x) {
		this.x = x;
	}

	@Override
	public long getY() {
		return y;
	}

	public void setY(long y) {
		this.y = y;
	}

	@Override
	public long getZ() {
		return z;
	}

	public void setZ(long z) {
		this.z = z;
	}
	
	@Override
	public Coordinate3D getCoordinate() {
		return new Coordinate3D(getX(), getY(), getZ());
	}

	@Override
	public void setCoordinate(Coordinate3D coordinate) {
		setX(coordinate.getX());
		setY(coordinate.getY());
		setZ(coordinate.getZ());
	};
	
	@Override
	public boolean isSameLocation(Coordinate3D coordinate) {
		return (getX() == coordinate.getX() && getY() == coordinate.getY() && getZ() == coordinate.getZ());
	}

	/** 출산률 계산 */
	@Override
    public double getBornChanceRate(Colony col, int efficiency100, double birthBoostRate) {
        double res = efficiency100 / 100.0;
        if(res > 50.0) res = 50.0;
        return res * birthBoostRate;
    }
    
    /** 출산률 적용 */
    protected void processBornChance(int cycle, Colony col, int efficiency100, double birthBoostRate) {
        if(getCitizenCount() >= Integer.MAX_VALUE) return;
        
        if(cycle % 600 == 0) {
            if(getHomeCapacity() > getCitizenCount()) {
                if(ColonyManager.random() < ( getBornChanceRate(col, efficiency100, birthBoostRate))) {
                    createNewCitizen(0);
                }
            }
        }
    }
    
    /** 이주율 계산 (이주해 들어올 확률만 계산) */
    @Override
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
    @Override
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
    protected void processMoveInChance(int cycle, Colony col, int efficiency100) {
        if(getCitizenCount() >= Integer.MAX_VALUE) return;
        
        double moveRate = getMoveChangeRate(col, efficiency100);
        if(cycle % 600 == 0) {
            if(ColonyManager.random() < moveRate) {
                int ageYear = 25 + ((int) ( ColonyManager.random() * 10 ) - 5);
                createNewCitizen(ageYear);
            }
        }
    }
    
    /** 이주율 (탈출) 적용 */
    protected void processMoveOutChance(int cycle, Colony col, int efficiency100) {
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
                    if(ColonyManager.random() <= rates) {
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
                        
                        if(ColonyManager.random() <= rates) {
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
        return toJson(false, null, this, false);
    }
    
    @Override
    public JsonObject toJson(boolean excludeSecrets) {
    	return toJson(false, null, this, excludeSecrets);
    }
    
    @Override
    public JsonObject toJson(boolean details, Colony col, City city, boolean excludeSecrets) {
        city = this;
        
        JsonObject json = new JsonObject();
        json.put("type", "City");
        json.put("name", getName());
        json.put("key", String.valueOf(getKey()));
        json.put("hp", String.valueOf(getHp()));
        json.put("tax", new Integer(getTax()));
        json.put("spaces", new Integer(getSpaces()));
        json.put("className", getClassName());
        
        json.put("x", String.valueOf(getX()));
        json.put("y", String.valueOf(getY()));
        json.put("z", String.valueOf(getZ()));
        
        JsonArray list = new JsonArray();
        for(Facility f : getFacility()) { list.add(f.toJson(details, col, city, excludeSecrets)); }
        json.put("facilities", list);
        
        list = new JsonArray();
        for(Citizen c : getCitizens()) { list.add(c.toJson(details, col, city, excludeSecrets)); }
        json.put("citizens", list);
        
        list = new JsonArray();
        for(HoldingJob h : holdings) { list.add(h.toJson()); }
        json.put("holdings", list);
        
        list = new JsonArray();
        for(Enemy h : enemies) { list.add(h.toJson(details, col, city, excludeSecrets)); }
        json.put("enemies", list);
        
        list = new JsonArray();
        for(Policy p : policies) { list.add(p.toJson(details, col, city, excludeSecrets)); }
        json.put("policies", list);
        
        // 추가 정보 (불러올 때는 필요가 없는) 첨가
        json.put("maxHp", String.valueOf(getMaxHp()));
        json.put("spaceUsing", new Integer(getUsingSpaces()));
        json.put("spaceLeft", new Integer(getLeftSpaces()));
        
        if(details) {
            json.put("statusString", StaticMethods.encodeString(getStatusString(col, null)));
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
        
        try { x = Long.parseLong(json.get("x").toString());               } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setX(0L); }
        try { y = Long.parseLong(json.get("y").toString());               } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setY(0L); }
        try { z = Long.parseLong(json.get("z").toString());               } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); setZ(0L); }
        
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
                        Citizen cit = new DefaultCitizen((JsonObject) o);
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
                        HoldingJob h = new DefaultHoldingJob();
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
                        Enemy en = AbstractEnemy.createEnemyFromJson((JsonObject) o);
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
                        Policy p = ColonyClassManager.createPolicyInstance(type);
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
    @Override
    public String getStatusString(Colony col, ColonyManagerInterface superInstance) {
        StringBuilder desc = new StringBuilder("");
        
        long powerConsume = 0L;
        for(Facility f : getFacility()) {
            powerConsume += f.getPowerConsume();
        }
        
        if(col == null && superInstance != null) col = getColony(superInstance);
        desc = desc.append("\n").append("위치 : ").append(ColonyManager.formatCoordinate(this));
        desc = desc.append("\n").append("HP : ").append(ColonyManager.formatInt(getHp())).append(" / ").append(ColonyManager.formatInt(getMaxHp()));
        desc = desc.append("\n").append(ColonyManager.t("전력") + " : ").append(ColonyManager.formatInt(powerConsume)).append(" / ").append(ColonyManager.formatInt(getPowerGenerate(col)));
        desc = desc.append("\n").append(ColonyManager.t("공간") + " : ").append(ColonyManager.formatInt(getUsingSpaces())).append(" / ").append(ColonyManager.formatInt(getSpaces()));
        if(getCalculatedTransPoint() > 0L) desc = desc.append("\n").append(ColonyManager.t("교통 용량") + " : ").append(ColonyManager.formatInt(getCalculatedTransPoint() - getCalculatedTransLeftPoint())).append(" / ").append(ColonyManager.formatInt(getCalculatedTransPoint()));
        desc = desc.append("\n").append(ColonyManager.t("인구") + " : ").append(ColonyManager.formatInt(getCitizenCount()));
        desc = desc.append("\n").append(ColonyManager.t("시설 수") + " : ").append(ColonyManager.formatInt(getFacility().size()));
        desc = desc.append("\n").append(ColonyManager.t("평균 행복도") + " : ").append(ColonyManager.formatRate(getAverageHappiness()));
        desc = desc.append("\n").append(ColonyManager.t("거주 수용량") + " : ").append(ColonyManager.formatInt(getHomeCapacity()));
        desc = desc.append("\n").append(ColonyManager.t("직장 수") + " : ").append(ColonyManager.formatInt(getJobsCount()));
        desc = desc.append("\n").append(ColonyManager.t("노숙자") + " : ").append(ColonyManager.formatInt(getHomelesses()));
        desc = desc.append("\n").append(ColonyManager.t("백수") + " : ").append(ColonyManager.formatInt(getJobSeekers()));
        desc = desc.append("\n");
        for(Facility f : getFacility()) {
            if(f instanceof Factory) {
                Factory fc = (Factory) f;
                if(fc.getProductType() != null) desc = desc.append("\n").append(ColonyManager.t("[FACILITYNAME] 에서 [PRODUCTTYPE] 생산 중.").replace("[FACILITYNAME]", f.getName()).replace("[PRODUCTTYPE]", fc.getProducingName() ));
            }
        }
        desc = desc.append("\n");
        for(Facility f : getFacility()) {
        	String percents = "";
            if(f instanceof ResearchCenter) {
                ResearchCenter rc = (ResearchCenter) f;
                
                Research rNow = rc.getResearch(col);
                String rsName = " - ";
                if(rNow != null) rsName = rNow.getTitle();
                
                percents = "";
                if(rNow != null) percents = ColonyManager.formatRate(rNow.getProgressPercents()) + "%";
                
                desc = desc.append("\n").append(ColonyManager.t("[FACILITYNAME] 에서 연구 진행 중 : [RESEARCH] ([PROGRESS])").replace("[FACILITYNAME]", f.getName()).replace("[RESEARCH]", rsName).replace("[PROGRESS]", percents));
            } else if(f instanceof Port) {
            	Port p = (Port) f;
            	
            	int cnt = p.getLiveShipCount();
            	if(cnt > 0) desc = desc.append("\n").append(ColonyManager.t("[FACILITYNAME] 에서 함선 대기 중 : [SHIPCOUNT]").replace("[FACILITYNAME]", p.getName()).replace("[SHIPCOUNT]", ColonyManager.formatInt(cnt)));
            	for(Ship s : p.getShips()) {
            	    if(s.getLevel() <= 0) {
            	    	long max = s.getMaxProgress(p, col);
            	    	if(max <= 0) max = 1L;
            	    	percents = ColonyManager.formatRate(((max - s.getLeftProgress()) * 100.0) / max) + "%";
            	    	desc = desc.append("\n").append(ColonyManager.t("[FACILITYNAME] 에서 함선 건조 진행 중 : [SHIP] ([PROGRESS])").replace("[FACILITYNAME]", p.getName()).replace("[SHIP]", s.getDefaultName()).replace("[PROGRESS]", percents));
            	    }
            	}
            }
        }
        
        return desc.toString().trim();
    }
    
    /** 소속 정착지 찾기 */
    @Override
    public Colony getColony(ColonyManagerInterface man) {
        return man.getColonyFrom(this);
    }
    
    /** 계산된 교통 점수 반환 */
    protected long getCalculatedTransPoint() {
        return calculatedTransPoint;
    }
    
    /** 계산된 잔여 교통 점수 반환 */
    protected long getCalculatedTransLeftPoint() {
        return calculatedTransPointLeft;
    }
    
    /** 정책 목록 초기화 - 하위 클래스에서 오버라이드해 사용해야 함 */
    @Override
    public void resetPolicies() {
        policies.clear();
    }
    
    /** 함선 격납 공간 크기 반환 */
    @Override
    public int getShipSpaces() {
    	int res = 0;
    	for(Facility f : getFacility()) {
    		if(f instanceof Port) {
    			res += ((Port) f).getCapacity();
    		}
    	}
    	return res;
    }
    
    /** 잔여 함선 격납 공간 크기 반환 */
    @Override
    public int getLeftShipSpaces() {
    	int res = getShipSpaces();
    	for(Ship s : getShips()) {
    		res -= s.getSize();
    	}
    	if(res < 0) res = 0;
    	return res;
    }
    
    /** 도시 내 소속 함선들 반환 (말그대로 소속 함선으로, 실제 위치는 도시 내가 아닐수도 있음) - 건조 중인 함선 포함 */
    @Override
    public Vector<Ship> getShips() {
    	Vector<Ship> list = new Vector<Ship>();
    	for(Facility f : getFacility()) {
    		if(f instanceof Port) {
    			list.addAll(((Port) f).getShips());
    		}
    	}
    	return list;
    }
    
    /** 도시 내 소속 함선들 반환 (말그대로 소속 함선으로, 실제 위치는 도시 내가 아닐수도 있음) - 건조 중인 함선 제외 */
    @Override
    public Vector<Ship> getShipsLive() {
    	Vector<Ship> list = new Vector<Ship>();
    	for(Facility f : getFacility()) {
    		if(f instanceof Port) {
    			list.addAll(((Port) f).getShipsLive());
    		}
    	}
    	return list;
    }
    
    /** 해당 위치의 모든 함선들 반환 - 건조 중인 함선 제외 */
    @Override
    public Vector<Ship> getShips(long x, long y, long z) {
    	Vector<Ship> list = new Vector<Ship>();
    	for(Ship s : getShipsLive()) {
    		if(s.getX() == x && s.getY() == y && s.getZ() == z) { list.add(s); }
    	}
    	return list;
    }
    
    /** 해당 위치의 해당 범위 내 모든 함선들 반환 - 건조 중인 함선 제외 */
    @Override
    public Vector<Ship> getShips(long x, long y, long z, long dist) {
    	Vector<Ship> list = new Vector<Ship>();
    	for(Ship s : getShipsLive()) {
    		if(DataUtil.getDistance(x, y, z, s.getX(), s.getY(), s.getZ()) <= dist) { list.add(s); }
    	}
    	return list;
    }
    
    /** 소속 함선 수 반환 - 건조 수 포함 */
    @Override
    public int getShipCount() {
    	int res = 0;
    	for(Facility f : getFacility()) {
    		if(f instanceof Port) {
    			res += ((Port) f).getShipCount();
    		}
    	}
    	return res;
    }
    
    /** 소속 함선 수 반환 - 건조 수 제외 */
    @Override
    public int getLiveShipCount() {
    	int res = 0;
    	for(Facility f : getFacility()) {
    		if(f instanceof Port) {
    			res += ((Port) f).getLiveShipCount();
    		}
    	}
    	return res;
    }
    
    /** 해당 key 의 함선 찾아 반환 */
    @Override
    public Ship getShip(long key) {
    	for(Ship s : getShips()) {
    		if(s.getKey() == key) return s;
    	}
    	return null;
    }
    
    /** 함선 하나를 도시에서 제거 (파괴 혹은 다른 도시로 이동했다거나 등의 이유 발생 시 호출, 단순 파견으로는 이 메소드를 호출하면 안 됨) */
    @Override
    public void removeShip(Ship s) {
    	for(Facility f : getFacility()) {
    		if(f instanceof Port) {
    			Port p = (Port) f;
    			p.removeShip(s);
    		}
    	}
    }
    
    @Override
    public Port addShip(Ship s) {
    	for(Facility f : getFacility()) {
    		if(f instanceof Port) {
    			Port p = (Port) f;
    			if(s.getSize() <= p.leftShipSpaces()) {
    				// 여유공간 존재하는 우주공항 찾음
    				removeShip(s); // 혹시 모르니, 이 도시에서는 제거
    				return p.addShip(s);
    			}
    		}
    	}
    	return null;
    }
    
    /** 우주 공항 리스트 반환 */
    @Override
    public List<Port> getPorts() {
    	List<Port> ports = new ArrayList<Port>();
    	for(Facility f : getFacility()) {
    		if(f instanceof Port) {
    			ports.add((Port) f);
    		}
    	}
    	return ports;
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
    
    @Override
    public Object cloneThis() {
    	try {
    	    Class<?> classThis = getClass();
    	    ColonyElements col = (ColonyElements) classThis.newInstance();
    	    col.fromJson(toJson());
    	    return col;
    	} catch(Exception ex) {
    		throw new RuntimeException(ex.getMessage(), ex);
    	}
    }
    
    @Override
    public String describeForAI(Colony colony, City city) {
    	StringBuilder res = new StringBuilder("도시 \"" + getName() + "\" 의 브리핑을 시작합니다.");
    	List<Facility> fs = getFacility();
    	if(! fs.isEmpty()) {
    		res = res.append("\n").append("    ").append("이 도시에는 다음과 같은 건물과 시설들이 있습니다.");
    		for(Facility o : fs) {
        		String desc = o.describeForAI(colony, this);
        		if(DataUtil.isEmpty(desc)) {
        			res = res.append("\n").append("        ").append("시설 \"" + o.getName() + "\" (상세정보를 조회할 수 없습니다.)");
        		} else {
        			StringTokenizer lineTokenizer = new StringTokenizer(desc, "\n");
            		while(lineTokenizer.hasMoreTokens()) {
            			res = res.append("\n").append("        ").append(lineTokenizer.nextToken());
            		}
        		}
        	}
    	}
    	fs = null;
    	
    	List<Citizen> cs = getCitizens();
    	if(! cs.isEmpty()) {
    		res = res.append("\n").append("    ").append("이 도시에는 다음과 같은 시민들이 있습니다.");
    		for(Citizen o : cs) {
        		String desc = o.describeForAI(colony, this);
        		if(DataUtil.isEmpty(desc)) {
        			res = res.append("\n").append("        ").append("시민 \"" + o.getName() + "\" (상세정보를 조회할 수 없습니다.)");
        		} else {
        			StringTokenizer lineTokenizer = new StringTokenizer(desc, "\n");
            		while(lineTokenizer.hasMoreTokens()) {
            			res = res.append("\n").append("        ").append(lineTokenizer.nextToken());
            		}
        		}
        	}
    	}
    	cs = null;
        
    	List<Policy> ps = getPolicies();
    	int policyEnabled = 0;
    	for(Policy o : ps) { if(o.isEnabled()) policyEnabled++; }
    	if(policyEnabled >= 1) {
    		res = res.append("\n").append("    ").append("이 도시에는 다음과 같은 정책들이 활성화되어 있습니다.");
    		for(Policy o : ps) {
        		if(! o.isEnabled()) continue;
        		
        		String desc = o.describeForAI(colony, this);
        		if(DataUtil.isEmpty(desc)) {
        			res = res.append("\n").append("        ").append("정책 \"" + o.getName() + "\" (상세정보를 조회할 수 없습니다.)");
        		} else {
        			StringTokenizer lineTokenizer = new StringTokenizer(desc, "\n");
            		while(lineTokenizer.hasMoreTokens()) {
            			res = res.append("\n").append("        ").append(lineTokenizer.nextToken());
            		}
        		}
        	}
    	}
    	res = res.append("\n").append("여기까지, 도시 \"" + getName() + "\" 의 브리핑을 마칩니다.");
    	return res.toString();
    }
    
    /** 도시 건설 비용 */
    public static long getBuildingNewCityFee(Colony col) { return 1000000L; };
}
