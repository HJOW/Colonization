package org.duckdns.hjow.colonization.elements.loan;

import org.duckdns.hjow.colonization.elements.ColonyElements;

/** 대출 인터페이스 */
public interface Loan extends ColonyElements {
	/** 1회 이자 금액 계산 */
    public long getInterestOnce(int multiply);
    /** 총 이자액 중 남은 금액 반환 */
    public int getInterestLeft();
    /** 이자율 % 단위로 반환 */
    public int getInterestRate100();
    /** 이자 총 횟수 반환 */
    public int getInterestCount();
    /** 원금 반환 */
    public long getOriginals();
    /** 잔여 금액 반환 */
    public long getAmount();
    /** 이름 변경 */
    public void setName(String str);
}
