package org.duckdns.hjow.colonization.elements;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.duckdns.hjow.colonization.ColonyManagerInterface;
import org.duckdns.hjow.colonization.elements.celestials.Celestials;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.enemies.Enemy;
import org.duckdns.hjow.colonization.elements.facilities.Port;
import org.duckdns.hjow.colonization.elements.ship.Ship;
import org.duckdns.hjow.colonization.ui.ColonyPanel;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.core.JsonCompatible;
import org.duckdns.hjow.commons.json.JsonObject;

/** 우주 */ // TODO : 정착지에 구현된 개념들 일부가 우주로 이관될 예정
public interface Space extends Serializable, Disposeable, JsonCompatible {
	/** 객체 타입 반환, JSON 변환 시 type 으로 들어갈 내용 */
    public String getType();
    public long getKey();
    
	/** 이 우주에 등록된 정착지들 반환 (로컬 플레이 시 단 하나의 정착지만 등록됨) - 해당 사용자의 정착지도 포함되어야 함 */
    public List<Colony> getColonies();
    
    /** 해당 사용자의 정착지 반환 */
    public Colony getYourColony();
    
    /** 정착지 등록 (정착지를 먼저 불러오고 그 안에서 우주를 불러오는 순서이므로, 우주 불러올 때 이 메소드가 호출됨) */
    public void addColony(Colony col);
    
    public List<Enemy> getEnemies();
    /** 정착지에 적 등록 (도시에 바로 등록하면 안 됨 ! 필히 이 메소드로 등록할 것. 해당 도시에 적 도착 시 자동으로 도시에 등록됨) */
    public void addEnemy(Enemy en);
    public boolean contains(Enemy en);
    public boolean contains(Colony col);
    
    /** 천체 목록 반환 */
    public List<Celestials> getCelestials();
    /** 주변 천체 목록 랜덤화 (단, 천체 목록이 이미 생성된 경우 아무 동작하지 않음) */
    public void randomizeCelestials();
    
    /** 인증 여부 체크용 시리얼 반환 */
    public String getCheckerSerial();
    /** 인증 비활성화 */
    public void disableChecked();
    /** 인증 유효 여부 반환 */
    public boolean isCheckEnabled();
    /** 이 정착지를 마지막으로 저장한 ColonyManager 의 버전 반환 */
    public String getClientVersion();
    /** 이 정착지를 마지막으로 저장한 ColonyManager 의 빌드 번호 반환 */
    public long getClientBuildNo();
    /** 버전 정보 리셋 */
    public void resetClientVersion(ColonyManagerInterface man);
    /** 변조방지값 계산 */
    public BigInteger getCheckerValue();
    
    /** 쓰레드 N 사이클 당 1회 호출됨. */
    public void oneCycle(int cycle, ColonyPanel colPanel, Set<ColonyElements> excludes);
    
    /** JSON 으로 변환 (무한반복을 막기 위해, 정착지 정보는 미포함시킬 수 있음) */
    public JsonObject toJson(boolean excludeColonies);
    
    /** JSON 으로 변환 (무한반복을 막기 위해, 정착지 정보는 미포함시킬 수 있음) */
    public JsonObject toJson(boolean excludeColonies,boolean details, boolean excludeSecrets);
    
    /** 해당 함선의 소속 항구 찾기 */
    public Port findPort(Ship ship);
    /** 해당 함선의 소속 도시 찾기 */
    public City findCity(Ship ship);
    /** 해당 함선의 소속 정착지 찾기 */
    public Colony findColony(Ship ship);
    /** 도시 내 소속 함선들 반환 (말그대로 소속 함선으로, 실제 위치는 도시 내가 아닐수도 있음) - 건조 중인 함선 포함 */
    public Vector<Ship> getShips();
    /** 도시 내 소속 함선들 반환 (말그대로 소속 함선으로, 실제 위치는 도시 내가 아닐수도 있음) - 건조 중인 함선 제외 */
    public Vector<Ship> getShipsLive();
    /** 해당 위치의 모든 함선들 반환 - 건조 중인 함선 제외 */
    public Vector<Ship> getShips(long x, long y, long z);
    /** 해당 위치의 해당 범위 내 모든 함선들 반환 - 건조 중인 함선 제외 */
    public Vector<Ship> getShips(long x, long y, long z, long dist);
    /** 소속 함선 수 반환 - 건조 중인 함선 포함 */
    public int getShipCount();
    /** 소속 함선 수 반환 - 건조 중인 함선 제외 */
    public int getLiveShipCount();
    /** 해당 key 의 함선 찾아 반환 */
    public Ship getShip(long key);
    /** 이 정착지 내 모든 도시, 모든 우주공항에서 해당 함선 제거 (타 우주공항으로, 혹은 타 도시로 이동 시 이 메소드 호출 후 해당 도시에 다시 추가) */
    public void removeShip(Ship ship);
    /** 우주공항 목록 반환 */
    public List<Port> getPorts();
}
