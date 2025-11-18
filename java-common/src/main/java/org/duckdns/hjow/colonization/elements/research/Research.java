package org.duckdns.hjow.colonization.elements.research;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.ui.ColonyPanel;

public abstract class Research implements ColonyElements {
    private static final long serialVersionUID = -3391024381630960804L;
    protected volatile long    key = ColonyManager.generateKey();
    protected volatile long    progress = 0;
    protected volatile int     level    = 0;
    
    protected transient boolean fNeedRefresh = true;

    @Override
    public long getKey() {
        return key;
    }
    
    public void setKey(long key) {
        this.key = key;
    }
    
    @Override
    public final String getClassName() {
        return getClass().getSimpleName();
    }
    
    /** 이 객체의 타입 반환, 클래스명과 동일 */
    public final String getType() {
        return getClassName();
    }
    
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
    
    @Override
    public String getTooltip() {
        return getName();
    }

    @Override
    public int getHp() {
        return 100;
    }

    @Override
    public int getMaxHp() {
        return 100;
    }

    @Override
    public void setHp(int hp) { }

    @Override
    public void addHp(int amount) { }
    
    @Override
    public short getDefenceType() {
        return 0;
    }

    @Override
    public int getDefencePoint() {
        return 0;
    }
    
    public double getProgressPercents() {
        return getProgressPercents(true);
    }
    
    public double getProgressPercents(boolean left2FloatPoint) {
        BigDecimal r = new BigDecimal(String.valueOf(getMaxProgress()));
        if(r.compareTo(BigDecimal.ZERO) <= 0) return 0.0;
        
        BigDecimal p = new BigDecimal(String.valueOf(getProgress()));
        p = p.multiply(new BigDecimal("100"));
        r = p.divide(r, 50, BigDecimal.ROUND_HALF_UP);
        if(left2FloatPoint) r = r.setScale(2, RoundingMode.DOWN);
        return r.doubleValue();
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
        if(this.progress < 0) this.progress = 0;
        if(this.progress > getMaxProgress()) this.progress = getMaxProgress();
    }
    
    /** 진행 상태 증가 (레벨업 로직 포함 - adds는 반드시 양수로 입력해야 함) 레벨 변동 시 true 리턴 */
    public boolean increaseProgress(int adds) {
        if(adds < 0) adds = 0;
        boolean increased = false;
        
        this.progress += adds;
        if(this.progress < 0) this.progress = 0;
        while(this.progress >= getMaxProgress()) {
            increased = true;
            if(getLevel() < getMaxLevel()) {
                this.progress -= getMaxProgress();
                if(this.progress < 0) this.progress = 0;
                setLevel(getLevel() + 1);
            } else {
                this.progress = 0L;
                setLevel(getMaxLevel());
                break;
            }
        }
        if(this.progress > getMaxProgress()) this.progress = getMaxProgress();
        return increased;
    }
    
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    
    /** 도달할 수 있는 최대 레벨 */
    public int getMaxLevel() {
        return Integer.MAX_VALUE / 10;
    }
    
    /** 다음 레벨까지 도달하기에 필요한 진행상태(cycle) 필요 요구량 계산 */
    public long getMaxProgress() {
        long res = getMaxProgressStarts();
        int nowLevelLefts = getLevel() - 1;
        if(nowLevelLefts < 0) nowLevelLefts = 0;
        
        while(nowLevelLefts >= 1) {
            if(res >= Long.MAX_VALUE / 2) return res;
            res = (long) Math.round(res * getMaxProgressIncreaseRate());
            nowLevelLefts--;
        }
        return res;
    }
    
    public long   getMaxProgressStarts()       { return 600L; }
    public double getMaxProgressIncreaseRate() { return 1.1;  }
    
    @Override
    public int cycleGap(Colony colony) { return 1; }
    
    @Override
    public void oneCycle(int cycle, ColonyElements stage, Colony colony, int efficiency100, ColonyPanel colPanel) { }
    
    /** 연구에 필요한 사전조건 중 필요 연구와 레벨 목록을 반환 */
    public final List<ResearchCondition> getResearchCoditions(Colony col) {
        return getResearchCoditions(col, getLevel());
    }
    
    /** 연구/업그레이드에 필요한 사전조건 중 필요 연구와 레벨 목록을 반환, 매개변수 level 에는 현재의 레벨 입력 */
    public List<ResearchCondition> getResearchCoditions(Colony col, int level) {
        return new ArrayList<ResearchCondition>();
    }
    
    /** 연구 시작 가능여부 반환 (선행 연구 완료여부만 체크) */
    public boolean isResearchAvail(Colony col) {
        boolean cond1, cond2, cond3;
        
        if(col != null) {
            cond1 = col.supportedResearch(getClassName());
            if(! cond1) {
                ColonyManager.logGlobals(ColonyManager.t("[COLONYTYPE] 유형의 정착지에 [RESEARCH] 연구는 지원되지 않습니다.").replace("[COLONYTYPE]", col.getName()).replace("[RESEARCH]", getClassName()), 1);
                return false;
            }
        }
        
        boolean avail  = true;
        boolean exists = false;
        List<ResearchCondition> listRes = getResearchCoditions(col, getLevel());
        for(ResearchCondition c : listRes) {
            // 이 조건이 활성화되는 시작 레벨 체크 (시작 레벨에 맞지 않으면 이 조건은 맞춘 걸로 치고 건너뜀)
            cond1 = (c.getStartLevel() >= 1 && getLevel() < c.getStartLevel());
            if(cond1) continue;
            
            // 조건 검사
            exists = false;
            for(Research r : col.getResearches()) {
                cond1 = (c.getResearchClassName().equals(r.getClassName()));
                cond2 = (chooseMaxInt(c.getLevel(), 1) <= r.getLevel());
                cond3 = (c.getLevelRate() * chooseMaxInt(getLevel(), 1) <= r.getLevel());
                
                if(cond1 && cond2 && cond3) {
                    exists = true;
                    break;
                }
            }
            if(! exists) { avail = false; break; }
        }
        
        return avail;
    }
    
    /** 연구 이름 반환 */
    public abstract String  getTitle();
    
    /** 이 연구의 설명 반환 */
    public String getDescription() {
        return getTitle();
    }
    
    @Override
    public void fromJson(JsonObject json) {
        key = Long.parseLong(json.get("key").toString());
        setLevel(Integer.parseInt(json.get("level").toString()));
        setProgress(Long.parseLong(json.get("progress").toString()));
    }
    
    @Override
    public JsonObject toJson() {
        return toJson(false, null, null);
    }
    
    @Override
    public JsonObject toJson(boolean details, Colony col, City city) {
        JsonObject json = new JsonObject();
        json.put("type"    , getClass().getSimpleName());
        json.put("key"     , String.valueOf(getKey()));
        json.put("level"   , new Integer(getLevel()));
        json.put("progress", String.valueOf(getProgress()));
        
        return json;
    }
    
    @Override
    public BigInteger getCheckerValue() {
        BigInteger res = new BigInteger(String.valueOf(getKey()));
        String type = getClass().getSimpleName();
        for(int idx=0; idx<type.length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) type.charAt(idx)))); }
        for(int idx=0; idx<getName().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getName().charAt(idx)))); }
        res = res.add(new BigInteger(String.valueOf(getLevel())));
        res = res.add(new BigInteger(String.valueOf(getProgress())));
        
        return res;
    }
    
    @Override
    public String toString() {
        return getTitle() + " (Lv " + (getLevel() + 1) + ")"; // 콤보박스 출력용이므로...
    }
    
    /** 정수 둘 중 큰 값을 반환 (레벨 비교에 사용) */
    protected int chooseMaxInt(int a, int b) {
        if(a >= b) return a;
        return b;
    }
    
    @Override
    public void dispose() { }
    
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
}
