package org.duckdns.hjow.colonization.elements;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.commons.exception.KnownRuntimeException;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.HexUtil;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.constants.Constants;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.facilities.Home;
import org.duckdns.hjow.colonization.elements.states.State;
import org.duckdns.hjow.colonization.ui.ColonyManagerUI;
import org.duckdns.hjow.colonization.ui.ColonyPanel;

/** 시민 구현 클래스 */
public class Citizen implements ColonyElements {
    private static final long serialVersionUID = -6856576686789163067L;
    protected volatile long key = ColonyManager.generateKey();
    protected transient boolean fNeedRefresh = true;
    protected String name = ColonyManager.t("시민") + "_" + ColonyManager.getNaturalNumberFrom(key);
    
    protected List<State> states = new Vector<State>();
    
    protected int hp = 100;
    protected int hunger  = 50;
    protected int stamina = 50;
    protected int happy   = 50;
    
    protected int strength    = (int) (Math.random() * 5) + 4;
    protected int agility     = (int) (Math.random() * 5) + 4;
    protected int carisma     = (int) (Math.random() * 5) + 4;
    protected int intelligent = (int) (Math.random() * 5) + 4;
    
    protected int educatedIntelligence = 0;
    protected int educatedPhysical     = 0;
    
    protected long money           = 100L;
    protected long experience      =   0L;
    
    protected BigInteger age = BigInteger.ZERO;
    
    protected long workingFacility  = 0L;
    protected long workingCity      = 0L;
    protected long livingHome       = 0L;
    protected long buildingFacility = 0L;
    
    public Citizen() { super(); }
    public Citizen(long age) { this(); this.age = new BigInteger(String.valueOf(age)); }
    public Citizen(JsonObject json) {
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
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getTooltip() {
    	return getName();
    }
    
    @Override
    public final String getClassName() {
    	return getClass().getSimpleName();
    }
    
    @Override
    public int cycleGap(Colony colony) { return 1; }

    @Override
    public void oneCycle(int cycle, City city, Colony colony, int efficiency100, ColonyPanel colPanel) {
        // HP 처리
        if(hp < 0) hp = 0;
        if(hp <= 0) return;
        
        boolean harm = false;
        int std;
        
        std = 120;
        if(cycle % std == 0) {
            if(livingHome < 0L) { // 집이 없으면
                happy--;
                hp--;
                harm = true;
            }
        }
        
        std = 600;
        if(cycle % std == 0) {
            hunger--;
        }
        if(hunger < 0) {
            hunger = 0;
        }
        
        if(hunger <= 0) {
            std = 60;
            if(cycle % std == 0) {
                hp--;
                if(efficiency100 < 50) hp--;
                harm = true;
            }
        } else if(hunger >= 50) {
            std = 600;
            if(cycle % std == 0 && hp < getMaxHp()) hp++;
        }
        
        std = 600000 / ( (101 - efficiency100) < 1 ? 1 : (101 - efficiency100) );
        if(cycle % std == 0) {
            happy--;
            if(happy <    0) happy =   0;
            if(happy >  100) happy = 100;
        }
        
        if(! harm) {
            hp++;
            int mx = getMaxHp();
            if(hp >= mx) hp = mx;
        }
        
        // State 영향력 동작
        for(State st : getStates()) {
        	if(cycle % st.cycleGap(colony) == 0) st.oneCycle(cycle, this, city, colony, colPanel);
        }
        
        // State 수명 동작
        for(State st : getStates()) {
        	if(cycle % st.cycleGap(colony) == 0) st.oneCycle(cycle, city, colony, efficiency100, colPanel);
        }
        
        // 수명 다된 state 제거
        std = 0;
        while(std < getStates().size()) {
            State st = getStates().get(std);
            if(st.getHp() <= 0 || st.getLefts() <= 0) {
                st.dispose();
                getStates().remove(std);
                continue;
            }
            std++;
        }
        
        // 연령 처리
        age = age.add(BigInteger.ONE);
    }
    
    @Override
    public void addHp(int amount) {
        hp += amount;
        int mx = getMaxHp();
        if(hp >  mx) hp = mx;
        if(hp <   0) hp = 0;
    }
    
    public void addHappy(int amount) {
        happy += amount;
        if(happy <    0) happy =   0;
        if(happy >  100) happy = 100;
    }
    
    @Override
    public short getDefenceType() {
        return ColonyManager.DEFENCETYPE_SMALL;
    }

    @Override
    public int getDefencePoint() {
        return 0;
    }

    @Override
    public int getHp() {
        return hp;
    }

    public int getHunger() {
        return hunger;
    }

    public int getStamina() {
        return stamina;
    }

    public int getStrength() {
        return strength;
    }

    public int getAgility() {
        return agility;
    }

    public int getIntelligent() {
        return intelligent;
    }

    public long getMoney() {
        return money;
    }

    public long getWorkingFacility() {
        return workingFacility;
    }
    
    public Facility getWorkingFacility(City c) {
        return c.getFacility(getWorkingFacility());
    }

    public long getWorkingCity() {
        return workingCity;
    }

    public long getBuildingFacility() {
        return buildingFacility;
    }
    
    public HoldingJob getBuildingFacility(City c) {
        return c.getHoldingJobOne(getBuildingFacility());
    }

    public void setBuildingFacility(long buildingFacility) {
        this.buildingFacility = buildingFacility;
    }
    
    public BigInteger getAge() {
		return age;
	}
	public void setAge(BigInteger age) {
		this.age = age;
	}
	public void setAge(String age) {
		this.age = new BigInteger(age);
	}
	public BigInteger getAgeYear() {
		BigInteger ageYear = getAge();
		ageYear = ageYear.divide(Constants.BIGINTEGER_10); // 초 to 분
        ageYear = ageYear.divide(Constants.BIGINTEGER_60); // 분 to 시
        ageYear = ageYear.divide(Constants.BIGINTEGER_24); // 시 to 일
        ageYear = ageYear.divide(Constants.BIGINTEGER_30); // 일 to 월
        ageYear = ageYear.divide(Constants.BIGINTEGER_12); // 월 to 년
        return ageYear;
	}
	public void setAgeYear(BigInteger year) {
		BigInteger ageYear = year;
		ageYear = ageYear.multiply(Constants.BIGINTEGER_12); // 년 to 월
		ageYear = ageYear.multiply(Constants.BIGINTEGER_30); // 월 to 일
		ageYear = ageYear.multiply(Constants.BIGINTEGER_24); // 일 to 시
		ageYear = ageYear.multiply(Constants.BIGINTEGER_60); // 시 to 분
		ageYear = ageYear.multiply(Constants.BIGINTEGER_10); // 분 to 초
        setAge(ageYear);
	}
	public void setAgeYear(String year) {
		setAgeYear(new BigInteger(year));
	}
	/** 일자리 찾는 중인지를 반환 */
    public boolean isJobSeeker() {
        if(getWorkingFacility()  != 0L) return false;
        if(getBuildingFacility() != 0L) return false;
        return true;
    }
    
    /** 노숙자인지를 반환 */
    public boolean isHomeless() {
        if(getLivingHome() != 0L) return false;
        return true;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
        if(this.hunger <   0) this.hunger =   0;
        if(this.hunger > 100) this.hunger = 100;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
        if(this.stamina < 0) this.stamina = 0;
        if(this.stamina > getMaxStemina()) this.stamina = getMaxStemina();
    }
    
    public int getMaxStemina() {
        return 50 + (getAgility() / 2);
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public void setIntelligent(int intelligent) {
        this.intelligent = intelligent;
    }

    public int getCarisma() {
        return carisma;
    }

    public void setCarisma(int carisma) {
        this.carisma = carisma;
    }

    public int getEducatedIntelligence() {
        return educatedIntelligence;
    }

    public int getEducatedPhysical() {
        return educatedPhysical;
    }

    public void setEducatedIntelligence(int educatedIntelligence) {
        this.educatedIntelligence = educatedIntelligence;
    }

    public void setEducatedPhysical(int educatedPhysical) {
        this.educatedPhysical = educatedPhysical;
    }

    public void setMoney(long money) {
        this.money = money;
        if(this.money < 0) this.money = 0L;
    }

    public void setWorkingFacility(long workingFacility) {
        this.workingFacility = workingFacility;
    }

    public void setWorkingCity(long workingCity) {
        this.workingCity = workingCity;
    }

    public int getHappy() {
        return happy;
    }

    public void setHappy(int happy) {
        this.happy = happy;
        if(this.happy <   0) this.happy =   0;
        if(this.happy > 100) this.happy = 100;
    }

    public long getLivingHome() {
        return livingHome;
    }
    
    public Home getLivingHome(City c) {
        return (Home) c.getFacility(getLivingHome());
    }

    public void setLivingHome(long livingHome) {
        this.livingHome = livingHome;
    }

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }
    
    public int getMaxHp() {
        return 100 + (getStrength() / 5) + (getAgility() / 10);
    }
    
    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }

    @Override
    public JsonObject toJson() {
        return toJson(false, null, null);
    }
    
    @Override
    public JsonObject toJson(boolean details, Colony col, City city) {
        JsonObject json = new JsonObject();
        json.put("type", "Citizen");
        json.put("name", getName());
        json.put("key", String.valueOf(getKey()));
        
        json.put("hp"                , new Integer(getHp()));
        json.put("hunger"            , new Integer(getHunger()));
        json.put("stamina"           , new Integer(getStamina()));
        json.put("happy"             , new Integer(getHappy()));
        json.put("strength"          , new Integer(getStrength()));
        json.put("agility"           , new Integer(getAgility()));
        json.put("carisma"           , new Integer(getCarisma()));
        json.put("intelligent"       , new Integer(getIntelligent()));
        json.put("educatedIntel"     , new Integer(getEducatedIntelligence()));
        json.put("educatedPhysical"  , new Integer(getEducatedPhysical()));
        json.put("money"             , String.valueOf(getMoney()));
        json.put("age"               , String.valueOf(getAge()));
        json.put("experience"        , String.valueOf(getExperience()));
        json.put("workingFacility"   , String.valueOf(getWorkingFacility()));
        json.put("buildingFacility"  , String.valueOf(getBuildingFacility()));
        json.put("workingCity"       , String.valueOf(getWorkingCity()));
        json.put("livingHome"        , String.valueOf(getLivingHome()));
        
        JsonArray list = new JsonArray();
        for(State s : getStates()) { list.add(s.toJson(details, col, city)); }
        json.put("states", list);
        
        // 추가 정보 (불러올 때는 필요가 없는) 첨가
        json.put("maxHp", String.valueOf(getMaxHp()));
        
        if(details) {
            String str = getStatusString(city, col, null);
            if(str == null) str = "";
            json.put("statusString", HexUtil.encodeString(str));
        }
        
        return json;
    }
    
    public void fromJson(JsonObject json) {
        if(! "Citizen".equals(json.get("type"))) throw new KnownRuntimeException("This object is not Citizen type.");
        setName(json.get("name").toString());
        key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
        setMoney(Long.parseLong(json.get("money").toString()));
        setAge(new BigInteger(json.get("age").toString()));
        
        setHunger(     Integer.parseInt(json.get("hunger"     ).toString()));
        setStamina(    Integer.parseInt(json.get("stamina"    ).toString()));
        setHappy(      Integer.parseInt(json.get("happy"      ).toString()));
        setStrength(   Integer.parseInt(json.get("strength"   ).toString()));
        setAgility(    Integer.parseInt(json.get("agility"    ).toString()));
        setCarisma(    Integer.parseInt(json.get("carisma"    ).toString()));
        setIntelligent(Integer.parseInt(json.get("intelligent").toString()));
        
        setEducatedIntelligence(Integer.parseInt(json.get("educatedIntel"   ).toString()));
        setEducatedPhysical(    Integer.parseInt(json.get("educatedPhysical").toString()));
        
        setExperience(      Long.parseLong(json.get("experience"      ).toString()));
        setWorkingFacility( Long.parseLong(json.get("workingFacility" ).toString()));
        setBuildingFacility(Long.parseLong(json.get("buildingFacility").toString()));
        setWorkingCity(     Long.parseLong(json.get("workingCity"     ).toString()));
        setLivingHome(      Long.parseLong(json.get("livingHome"      ).toString()));
        
        JsonArray list = (JsonArray) json.get("states");
        states.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        JsonObject jsonObj = (JsonObject) o;
                        State stateOne = State.createStateInstance(jsonObj.get("type").toString());
                        if(stateOne == null) throw new NullPointerException("Cannot found these state type " + jsonObj);
                        
                        stateOne.fromJson(jsonObj);
                        states.add(stateOne);
                    } catch(Exception ex) {
                        GlobalLogs.processExceptionOccured(ex, false);
                    }
                }
            }
        }
    }
    
    /** 상태 메시지 생성 (UI 내 JTextArea 에 출력됨) */
    public String getStatusString(City city, Colony colony, ColonyManagerUI superInstance) {
        DecimalFormat formatterInt  = new DecimalFormat("#,###,###,###,###,##0");
        
        StringBuilder desc = new StringBuilder("");
        desc = desc.append("\n").append(ColonyManager.t("자금") + " : ").append(formatterInt.format(getMoney()));
        desc = desc.append("\n").append(ColonyManager.t("행복도") + " : ").append(formatterInt.format(getHappy())).append(" / ").append("100");
        desc = desc.append("\n").append(ColonyManager.t("나이") + " : ").append(formatterInt.format(getAgeYear()));
        
        Facility f = null;
        Facility h = null;
        Facility b = null;
        
        if(getWorkingFacility()  != 0L) f = city.getFacility(getWorkingFacility());
        if(getLivingHome()       != 0L) h = city.getFacility(getLivingHome());
        if(getBuildingFacility() != 0L) b = city.getFacility(getBuildingFacility());
        
        if(h != null) desc = desc.append("\n").append(ColonyManager.t("거주") + " : ").append(h.getName());
        if(f != null) desc = desc.append("\n").append(ColonyManager.t("직장") + " : ").append(f.getName());
        if(b != null) desc = desc.append("\n").append(ColonyManager.t("건설") + " : ").append(b.getName());
        
        List<State> states = getStates();
        if(! states.isEmpty()) {
            desc = desc.append("\n");
            for(State st : states) { desc = desc.append(st.getTitle()).append("\t"); }
        }
        
        return desc.toString().trim();
    }

    /** 소속 도시 반환 */
    public City getWorkingCity(Colony col) {
        if(col == null) return null;
        if(getWorkingCity() == 0L) return null;
        return col.getCity(getWorkingCity());
    }
    
    @Override
    public BigInteger getCheckerValue() {
        BigInteger res = new BigInteger(String.valueOf(getKey()));
        res = res.add(new BigInteger(String.valueOf(getHp())).multiply(Constants.BIGINTEGER_3));
        res = res.add(getAge().multiply(Constants.BIGINTEGER_5));
        res = res.add(new BigInteger(String.valueOf(getHunger())).multiply(Constants.BIGINTEGER_7));
        res = res.add(new BigInteger(String.valueOf(getStamina())).multiply(Constants.BIGINTEGER_11));
        res = res.add(new BigInteger(String.valueOf(getHappy())).multiply(Constants.BIGINTEGER_13));
        res = res.add(new BigInteger(String.valueOf(getStrength())).multiply(Constants.BIGINTEGER_17));
        res = res.add(new BigInteger(String.valueOf(getAgility())).multiply(Constants.BIGINTEGER_19));
        res = res.add(new BigInteger(String.valueOf(getCarisma())).multiply(Constants.BIGINTEGER_23));
        res = res.add(new BigInteger(String.valueOf(getIntelligent())).multiply(Constants.BIGINTEGER_29));
        res = res.add(new BigInteger(String.valueOf(getEducatedIntelligence())).multiply(Constants.BIGINTEGER_31));
        res = res.add(new BigInteger(String.valueOf(getEducatedPhysical())).multiply(Constants.BIGINTEGER_37));
        res = res.add(new BigInteger(String.valueOf(getExperience())).multiply(Constants.BIGINTEGER_41));
        res = res.add(new BigInteger(String.valueOf(getWorkingFacility())).multiply(Constants.BIGINTEGER_43));
        res = res.add(new BigInteger(String.valueOf(getBuildingFacility())).multiply(Constants.BIGINTEGER_47));
        res = res.add(new BigInteger(String.valueOf(getWorkingCity())).multiply(Constants.BIGINTEGER_53));
        res = res.add(new BigInteger(String.valueOf(getLivingHome())).multiply(Constants.BIGINTEGER_59));
        for(int idx=0; idx<getName().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getName().charAt(idx)))); }
        for(State st : getStates()) { res = res.add(st.getCheckerValue().multiply(Constants.BIGINTEGER_61)); }
        return res;
    }
    
    @Override
    public void dispose() {
        for(State st : getStates()) {
            st.dispose();
        }
        states.clear();
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
        for(State s : getStates()) { s.markAsRefreshChildren(f); }
    }
}
