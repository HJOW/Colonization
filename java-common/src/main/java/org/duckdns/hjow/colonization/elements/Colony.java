package org.duckdns.hjow.colonization.elements;

import java.math.BigInteger;
import java.util.List;

import org.duckdns.hjow.colonization.AccountingData;
import org.duckdns.hjow.colonization.elements.enemies.Enemy;
import org.duckdns.hjow.colonization.elements.facilities.FacilityInformation;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.events.TimeEvent;
import org.duckdns.hjow.colonization.ui.ColonyManagerUI;

/** 정착지 인터페이스 */
public interface Colony extends ColonyElements {
    /** 객체 타입 반환, JSON 변환 시 type 으로 들어갈 내용 */
    public String getType();
    public void setName(String name);
    public List<City> getCities();
    public City getCity(long key);
    /** 새 도시를 생성 */
    public City newCity();
    public List<Enemy> getEnemies();
    public List<HoldingJob> getHoldings();
    public List<Research> getResearches();
    /** 연구 목록 초기화 (비우고, 초기 상태로 다시 채움) */
    public void resetResearches();
    /** 총 인구 수 구하기 */
    public long getCitizenCount();
    public int getDifficulty();
    public long getMoney();
    public void modifyingMoney(long money, City city, ColonyElements objType, String reason);
    public long getTech();
    public void setTech(long tech);
    public BigInteger getTime();
    public void setTime(BigInteger time);
    public String getDateString();
    public List<AccountingData> getAccountingData();
    public void setAccountingData(List<AccountingData> accountingData);
    public void addAccountingData(AccountingData data);
    /** 이 정착지가 감당 가능한 도시 수를 반환 */
    public int getMaxCityCount();
    /** 현재 도시 수 반환 */
    public int getCityCount();
    /** 회계 산출 주기 반환 */
    public int getAccountingPeriod();
    /** 해당 시설 이 정착지에서 사용 가능 여부 결정 */
    public boolean supportedFacility(FacilityInformation info);
    /** 해당 연구 이 정착지에서 사용 가능 여부 결정 */
    public boolean supportedResearch(String researchTypeName);
    /** 상세 내역 */
    public String getStatusString(ColonyManagerUI superInstance);
    /** 발생할 수 있는 이벤트 유형들 반환 */
    public List<TimeEvent> getEvents();
}
