package org.duckdns.hjow.colonization.mod;

import java.io.Serializable;

import org.duckdns.hjow.colonization.ColonyManagerInterface;
import org.duckdns.hjow.commons.core.Disposeable;

/** MOD, 사용자 정의 컴포넌트가 구현해야 할 인터페이스 */
public interface Mod extends Disposeable, Serializable {
    /** UI 초기화 시 호출 */
    public void init(ColonyManagerInterface manager);
    
    /** 이 MOD 의 이름 반환 */
    public String getName();
    
    /** 이 MOD 의 설명문 반환 */
    public String getDescription();
    
    /** 0을 리턴해야 함 (차후 대화상자형 외 다른 형태의 MOD 지원 시 사용 예정) */
    public int getLocation();
    
    /** 대화상자 내 중앙에 배치될 AWT / Swing 컴포넌트 반환 */
    public Object getComponent();
    
    /** 
     * 시뮬레이션 중 새로 고침 시 호출, 혹은 전체 새로고침 시에도 호출. 
     *     읽기 전용 모드인 경우 colony 에는 정착지 정보가 JsonObject 타입으로 들어감.
     *     읽기 전용 모드가 아닌 경우, colony 에는 정착지 객체 자체가 들어와 데이터 변경이 가능
     */
    public void refresh(int cycle, Object colony, ColonyManagerInterface manager);
    
    /** 읽기 전용 여부 반환. (true 시 읽기 전용). 읽기 전용 모드가 아닌 경우, 정착지의 인증이 해제됨. */
    public boolean isReadOnly();
}
