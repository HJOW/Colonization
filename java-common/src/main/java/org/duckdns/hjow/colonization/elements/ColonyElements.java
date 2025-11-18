package org.duckdns.hjow.colonization.elements;

import java.io.Serializable;
import java.math.BigInteger;

import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.interfaces.JsonCompatible;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.ui.ColonyPanel;

/** Colonization 내 소속 클래스임을 나타내는 인터페이스 */
public interface ColonyElements extends Serializable, Disposeable, JsonCompatible {
    /** 이 객체의 고유 ID 값 반환. 0이 될 수 없음. */
    public long getKey();
    
    /** 이 객체의 이름 반환. */
    public String getName();
    
    /** 이 객체의 클래스 이름 반환 */
    public String getClassName();
    
    /** 툴팁 반환 (없으면 null 반환) */
    public String getTooltip();
    
    /** 현재의 HP 반환 */
    public int getHp();
    
    /** HP 최대값 반환 */
    public int getMaxHp();
    
    /** HP 값 설정, 전체 회복이거나 객체를 불러오는 경우를 제외하면 호출 비권장 */
    public void setHp(int hp);
    
    /** HP 추가 (음수 사용 가능) HP 변경 이슈 발생 시 되도록이면 이 함수 사용 권장 */
    public void addHp(int amount);
    
    /** 방어 속성 */
    public short getDefenceType();
    
    /** 방어력, 이 값 만큼 대미지에서 깎인다. 단, 대미지가 1 이하로는 떨어지지 않는다. */
    public int getDefencePoint();
    
    /** 쓰레드 N 사이클 당 1회 호출됨. 여기서 N값은 cycleGap 메소드에서 반환. (매개변수 cycle 은 N 관계없이 그대로 들어옴.) */
    public void oneCycle(int cycle, ColonyElements stage, Colony colony, int efficiency100, ColonyPanel colPanel);
    
    /** 쓰레드 N 사이클 당 위 oneCycle 메소드를 1회 호출, 이 N값을 반환하는 메소드 */
    public int cycleGap(Colony colony);
    
    /** 이 객체를 JSON 형태로 출력, 추가 정보 포함 여부 지정, details 를 true 로 지정하는 경우 Colony 객체와 City 객체가 필요함. details 가 false 인 경우 다른 매개변수는 null 을 넣으면 됨. */
    public JsonObject toJson(boolean details, Colony col, City city);
    
    /** 변조방지값 계산 */
    public BigInteger getCheckerValue();
    
    /** UI 상에 새로고침이 필요한 지 표시되었는지를 반환 */
    public boolean isMarkedAsRefresh();
    /** UI 새로고침 필요 표시 혹은 표시 제거 */
    public void markAsRefresh(boolean f);
    /** UI 새로고침 필요 표시 혹은 표시 제거, 자기자신 뿐만 아니라 내부 소속 객체 전체에 적용 */
    public void markAsRefreshChildren(boolean f);
}
