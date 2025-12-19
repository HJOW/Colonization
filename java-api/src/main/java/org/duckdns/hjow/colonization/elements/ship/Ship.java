package org.duckdns.hjow.colonization.elements.ship;

import java.util.List;

import org.duckdns.hjow.colonization.ColonyManagerInterface;
import org.duckdns.hjow.colonization.elements.AttackableObject;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.Movable;
import org.duckdns.hjow.colonization.elements.Space;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.facilities.Port;
import org.duckdns.hjow.colonization.elements.products.Product;
import org.duckdns.hjow.colonization.elements.states.State;

/** 함선 */
public interface Ship extends AttackableObject, Movable {
	/** 초기 함선 상태로 리셋 (단, 기존 함선 불러오는 경우는 이 메소드 호출할 필요 없음) */
	public void init(Port port, Colony colony);
	/** 이름 지정 */
	public void setName(String name);
	/** 기본 명칭 반환 */
	public String getDefaultName();
	/** 함선 종류의 설명 반환 */
	public String getDescription();
	/** 소유자 정착지 고유 KEY 반환 */
	public long getOwner();
	/** 소유자 정착지 객체 반환, 찾지 못한 경우 null 이 반한 */
	public Colony getOwner(Space space);
	/** 소유자 정착지 변경, 정착지의 고유 KEY 입력 */
	public void setOwner(long owner);
	/** 소유자 정착지 변경  */
	public void setOwner(Colony owner);
	/** 소유자와 동맹 관계의 정착지 KEY 목록 반환 (문자열의 List로 반환) */
	public List<String> getAllied();
	/** 소유자와 동맹 관계의 정착지 목록 교체 (정착지 KEY들을 문자열로 변환해 넣어야 함) */
	public void setAllied(List<String> allied);
	/** 함선 건조에 필요한 최대 시간 (사이클) 반환 */
	public long getMaxProgress(Port port, Colony colony);
	/** 함선의 레벨 반환 */
	public int getLevel();
    /** 실제 속도 반환 (연구 등 적용) */
    public long getRealSpeed(Colony col);
    /** 상태 객체들 반환 */
    public List<State> getStates();
    /** 화물칸 내에 있는 Product 들 반환 */
    public List<Product> getStored();
    /** 해당 화물 적재 */
    public void store(Product p);
    /** 현재 화물 적재량 (특정 화물만 카운트) */
    public int getStoredCount(String productType);
    /** 현재 화물 적재량 */
    public int getStoredCount();
    /** 최대 화물 수용량 */
    public int getMaxStoredCapacity();
    /** 정지 명령 */
    public void stop();
    /** 해당 좌표로 이동 명령 */
    public void moveStartTo(int x, int y, int z);
    /** 도착 예정시간 (사이클 수) 반환 */
    public long getEstimatedArrivalTime(Colony colony);
    /** 함선 제조/수리까지 남은 시간(사이클) 반환 - 이 값이 1 이상이면 조작 불가, 매 사이클마다 감소 */
    public long getLeftProgress();
    /** 함선 제조/수리 진행 */
    public void decreaseProgress(City city, Colony colony);
    /** 레벨 증가, 단, 잔여 제조/수리 시간이 있는 경우 아무 일도 하지 않음. */
    public void increaseLevel();
    /** 함선의 크기 (격납공간 차지하는 양) */
    public int getSize();
    /** 함선 건조 비용 */
    public long getPrice(Port port, Colony colony);
    /** 상태 메시지 생성 (UI 내 JTextArea 에 출력됨) */
    public String getStatusString(Colony col, ColonyManagerInterface superInstance);
    
    /* 다음 static 메소드 의무 탑재 !
     * 
     *  // 함선 명칭
        public static String getMetaName()
        // 함선 설명
        public static String getMetaDescription()
        // 함선 건조 시간 (사이클)
        public static long getMetaBuildCycle(Port port, Colony colony)
        // 함선 건조 가능여부, null 리턴 시 가능한 것. 그외의 경우 건조 불가능 사유 리턴
        public static String getMetaBuildAvail(Port port, Colony colony);
        // 함선 건조 가격
        public static long getMetaPrice(Port port, Colony colony);
        // 함선의 크기
        public static int getMetaSize()
     */
}
