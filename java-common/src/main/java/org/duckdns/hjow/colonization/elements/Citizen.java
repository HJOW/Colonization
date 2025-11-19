package org.duckdns.hjow.colonization.elements;

import java.math.BigInteger;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManagerInterface;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.facilities.Home;
import org.duckdns.hjow.colonization.elements.states.State;

/** 시민 */
public interface Citizen extends ColonyElements {
	public void setName(String name);
	public int getHappy();
	public void addHappy(int amount);
	public int getHunger();
	public int getStamina();
	public void setStamina(int stamina);
    public int getMaxStemina();
    public void setMoney(long money);
	public void setHunger(int hunger);
	public int getStrength();
	public int getAgility();
	public int getIntelligent();
	public int getCarisma();
	public int getEducatedIntelligence();
	public int getEducatedPhysical();
	public void setEducatedIntelligence(int educatedIntelligence);
	public void setEducatedPhysical(int educatedPhysical);
	public long getMoney();
	public long getExperience();
	public int getMaxHp();
	public long getWorkingFacility();
	public Facility getWorkingFacility(City c);
	public long getWorkingCity();
	public long getBuildingFacility();
	public long getLivingHome();
	public Home getLivingHome(City c);
	public void setLivingHome(long livingHome);
	/** 소속 도시 반환 */
    public City getWorkingCity(Colony col);
	public HoldingJob getBuildingFacility(City c);
	public void setWorkingFacility(long workingFacility);
	public void setBuildingFacility(long buildingFacility);
	public BigInteger getAge();
	public BigInteger getAgeYear();
	public List<State> getStates();
	/** 상태 메시지 생성 (UI 내 JTextArea 에 출력됨) */
    public String getStatusString(City city, Colony colony, ColonyManagerInterface superInstance);
    /** 일자리 찾는 중인지를 반환 */
    public boolean isJobSeeker();
    /** 노숙자인지를 반환 */
    public boolean isHomeless();
    public void setAgeYear(BigInteger year);
    public void setAgeYear(String year);
    public void setHappy(int happy);
}
