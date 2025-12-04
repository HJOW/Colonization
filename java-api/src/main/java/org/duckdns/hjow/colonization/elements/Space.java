package org.duckdns.hjow.colonization.elements;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManagerInterface;
import org.duckdns.hjow.colonization.elements.celestials.Celestials;
import org.duckdns.hjow.colonization.elements.enemies.Enemy;
import org.duckdns.hjow.colonization.ui.ColonyPanel;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.core.JsonCompatible;

/** 우주 */ // TODO : 정착지에 구현된 개념들 일부가 우주로 이관될 예정
public interface Space extends Serializable, Disposeable, JsonCompatible {
	/** 객체 타입 반환, JSON 변환 시 type 으로 들어갈 내용 */
    public String getType();
    
	/** 이 우주에 등록된 정착지들 반환 (로컬 플레이 시 단 하나의 정착지만 등록됨) - 해당 사용자의 정착지도 포함되어야 함 */
    public List<Colony> getColonies();
    
    /** 해당 사용자의 정착지 반환 */
    public Colony getYourColony();
    
    public List<Enemy> getEnemies();
    /** 정착지에 적 등록 (도시에 바로 등록하면 안 됨 ! 필히 이 메소드로 등록할 것. 해당 도시에 적 도착 시 자동으로 도시에 등록됨) */
    public void addEnemy(Enemy en);
    public boolean contains(Enemy en);
    
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
    public void oneCycle(int cycle, ColonyElements stage, Space space, Colony colony, int efficiency100, ColonyPanel colPanel);
}
