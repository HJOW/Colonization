package org.duckdns.hjow.colonization.elements.city;
import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.colonization.ColonyManagerInterface;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.elements.HasLocation;
import org.duckdns.hjow.colonization.elements.HoldingJob;
import org.duckdns.hjow.colonization.elements.enemies.Enemy;
import org.duckdns.hjow.colonization.elements.facilities.Port;
import org.duckdns.hjow.colonization.elements.policy.Policy;
import org.duckdns.hjow.colonization.elements.ship.Ship;

/** 도시 인터페이스 */
public interface City extends HasLocation {
	/** 도시 이름 변경 */
    public void setName(String name);
    /** 시설 목록 반환 */
	public List<Facility> getFacility();
	/** 해당 key 에 대응하는 시설 찾아 반환 */
	public Facility getFacility(long facKey);
	/** 특정 타입의 시설 목록 반환 */
    public List<Facility> getFacilities(Class<?> facilityClass);
    /** 정책 목록 반환 */
    public List<Policy> getPolicies();
    /** 시민 목록 반환 */
    public List<Citizen> getCitizens();
    /** 해당 key 에 대응하는 시민 찾아 반환 */
    public Citizen getCitizen(long citizenKey);
    /** 적 목록 반환 */
    public List<Enemy> getEnemies();
    /** 적 개체 등록 (이미 존재 시 무시, 도시와 위치 좌표가 동일해야 등록됨) */
    public void addEnemy(Enemy enemy);
    /** 도시 단위 작업 목록 반환 */
    public List<HoldingJob> getHoldings();
    /** 도시 단위 작업 중 해당 key 에 대응하는 작업 반환 */
    public HoldingJob getHoldingJobOne(long key);
    /** 새 작업 등록 */
    public void addHoldingJob(HoldingJob job);
    /** 이 도시의 전체 공간량 반환 */
    public int getSpaces();
    /** 사용 중인 공간량 반환 */
    public int getUsingSpaces();
    /** 잔여 공간량 반환 */
    public int getLeftSpaces();
    /** 세금 수치 반환 (%) */
    public int getTax();
    /** 세금 변경 */
    public void setTax(int tax);
    /** 건설 중인 시설 수 반환 */
    public int getHoldingBuildFacility();
    /** 총 전력 생산량 반환 (사용량 미반영) */
    public long getPowerGenerate(Colony col);
    /** 총 네트워크 지원량 반환 (사용량 미반영) */
    public long getNetworkCapacity(Colony col);
    /**  시민 수 반환 */
    public int getCitizenCount();
    /** 노숙자 수 계산 */
    public int getHomelesses();
    /** 백수의 수 계산 */
    public int getJobSeekers();
    /** 출산률 계산 */
    public double getBornChanceRate(Colony col, int efficiency100, double birthBoostRate);
    /** 이주율 계산 (이주해 들어올 확률만 계산) */
    public double getMoveChangeRate(Colony col, int efficiency100);
    /** 평균행복도 계산 (참고 - 시민의 행복도는 최소 0, 최초값은 50) */
    public double getAverageHappiness();
    /** 새 시민 생성 (적정 나이로 생성) */
    public Citizen createNewCitizen();
    /** 새 시민 생성 (나이 지정) */
    public Citizen createNewCitizen(int ageYear);
    /** 이 도시 내 거주 시설 수용 인원 반환 (이미 거주 중인 자리도 포함) */
    public long getHomeCapacity();
    /** 이 도시 내 잔여 거주 시설 수용 인원 반환 */
    public long getHomeCapacityLeft();
    /** 이 도시 내 직장 자리 수 반환 (이미 일하고 있는 자리 수도 포함) */
    public long getJobsCount();
    /** 이 도시 내 잔여 직장 자리 수 반환 */
    public long getLeftJobsCount();
    /** 상태 메시지 생성 */
    public String getStatusString(Colony col, ColonyManagerInterface superInstance);
    /** 소속 정착지 찾기 */
    public Colony getColony(ColonyManagerInterface man);
    /** 정책 목록 초기화 */
    public void resetPolicies();
    /** 함선 격납 공간 크기 반환 */
    public int getShipSpaces();
    /** 잔여 함선 격납 공간 크기 반환 */
    public int getLeftShipSpaces();
    /** 도시 내 소속 함선들 반환 (말그대로 소속 함선으로, 실제 위치는 도시 내가 아닐수도 있음) - 건조 중인 함선 포함 */
    public Vector<Ship> getShips();
    /** 도시 내 소속 함선들 반환 (말그대로 소속 함선으로, 실제 위치는 도시 내가 아닐수도 있음) - 건조 중인 함선 제외 */
    public Vector<Ship> getShipsLive();
    /** 해당 위치의 모든 함선들 반환 - 건조 중인 함선 제외 */
    public Vector<Ship> getShips(long x, long y, long z);
    /** 해당 위치의 해당 범위 내 모든 함선들 반환 - 건조 중인 함선 제외 */
    public Vector<Ship> getShips(long x, long y, long z, long dist);
    /** 소속 함선 수 반환 - 건조 수 포함 */
    public int getShipCount();
    /** 소속 함선 수 반환 - 건조 수 제외 */
    public int getLiveShipCount();
    /** 해당 key 의 함선 찾아 반환 */
    public Ship getShip(long key);
    /** 함선 하나를 도시에서 제거 (파괴 혹은 다른 도시로 이동했다거나 등의 이유 발생 시 호출, 단순 파견으로는 이 메소드를 호출하면 안 됨) */
    public void removeShip(Ship s);
    /** 함선 추가, 이 도시 내의 우주공항들 중 여유공간이 있는 곳에 정박하게 됨. 정박하게 될 우주공항 객체 반환. */
    public Port addShip(Ship s);
    /** 우주 공항 리스트 반환 */
    public List<Port> getPorts();
}
