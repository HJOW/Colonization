package org.duckdns.hjow.colonization;

import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;

/** ColonyManager 인터페이스, Mod 새로고침 호출 등에 사용 */
public interface ColonyManagerInterface extends Disposeable {
    /** 프로그램 종료 */
    public void exit();
    /** 현재 프로그램 설정 내용을 반환. 객체가 복제되어 반환되므로, 이 객체로 설정을 변경할 수는 없음. */
    public ColonyManagerConfig getConfig();
    /** 화면 UI 새로고침 예약 - 화면 새로고침 필요 사유가 발생한 경우 이 메소드를 호출할 것 */
    public void reserveRefresh();
    /** 로그 출력 */
    public void log(String msg);
    /** 알림 메시지 출력 */
    public void alert(String msg);
    /** 시뮬레이션 정지 */
    public void pauseSimulation();
    /** 시뮬레이션 재개, cycleCount 에 음수를 입력하면, 별도 정지 명령 시까지 진행, 양수를 넣으면 해당 횟수만큼만 진행 */
    public void resumeSimulation(int cycleCount);
    /** 현재 선택된 정착지의 정보를 JsonObject 으로 반환 */
    public JsonObject getSelectColonyInfo();
    /** 현재 불러온 모든 정착지 정보를 JsonArray 로 반환 */
    public JsonArray getAllColonies();
    /** 현재 선택된 정착지 반환 */
    public Colony getColony();
    /** 해당 키를 갖는 정착지 찾아 반환 (목록에 없으면 null 반환) */
    public Colony getColony(long colonyKey);
    /** 도시가 속한 정착지 찾기 */
    public Colony getColonyFrom(City city);
    /** Colonization 실행 */
    public void open(ColonizationMainClass superInstance);
    /** GUI 지원 인스턴스인 경우, 메인 창에 해당하는 Window 타입의 객체 반환, 그 외의 경우 null 반환 */
    public Object getDialogObject();
    
    public long    getCycleGapEachCity();         
    public long    getCycleGapEachFacility();     
    public boolean isUsingCheckDisablingContent(); 
}
