package org.duckdns.hjow.colonization.elements.research;

import java.util.List;

import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.Space;

/** 연구 */
public interface Research extends ColonyElements {
	/** 이 객체의 타입 반환 */
	public String getType();
	/** 연구 진행률 */
	public long getProgress();
	/** 연구 진행률 (%) */
	public double getProgressPercents(Space space);
	/** 연구 진행률 (%) */
	public double getProgressPercents(Space space, boolean left2FloatPoint);
	/** 진행 상태 증가 (레벨업 로직 포함 - adds는 반드시 양수로 입력해야 함) 레벨 변동 시 true 리턴 */
    public boolean increaseProgress(Space space, int adds);
    /** 현재 연구 레벨 */
    public int getLevel();
    /** 도달할 수 있는 최대 레벨 */
    public int getMaxLevel();
    /** 다음 레벨까지 도달하기에 필요한 진행상태(cycle) 필요 요구량 계산 - 밸런스 배율 제외 */
    public long getMaxProgress();
    /** 다음 레벨까지 도달하기에 필요한 진행상태(cycle) 필요 요구량 계산 - 밸런스 배율 적용 */
    public long getMaxProgress(Space space);
    public long   getMaxProgressStarts();
    public double getMaxProgressIncreaseRate();
    /** 연구에 필요한 사전조건 중 필요 연구와 레벨 목록을 반환 */
    public List<ResearchCondition> getResearchCoditions(Colony col);
    /** 연구/업그레이드에 필요한 사전조건 중 필요 연구와 레벨 목록을 반환, 매개변수 level 에는 현재의 레벨 입력 */
    public List<ResearchCondition> getResearchCoditions(Colony col, int level);
    /** 연구 시작 가능여부 반환 (선행 연구 완료여부만 체크) */
    public boolean isResearchAvail(Colony col);
    /** 연구 이름 반환 */
    public String getTitle();
    /** 이 연구의 설명 반환 */
    public String getDescription();
}
