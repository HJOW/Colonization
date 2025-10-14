package org.duckdns.hjow.colonization.elements.loan;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.City;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.ui.ColonyPanel;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.DataUtil;

/** 대출 */
public class Loan implements ColonyElements {
    private static final long serialVersionUID = -2168986905022004701L;
    protected volatile long key = ColonyManager.generateKey();
    protected String name = ColonyManager.t("대출");
    protected long originals       = 0L;
    protected long amount          = 0L;
    protected int  interestCount   = 0;
    protected int  interestLeft    = 0;
    protected int  interestRate100 = 5;
    
    protected transient boolean fNeedRefresh = true;
    
    public Loan() {}
    public Loan(long amount, int interestCount, int interestRate100) {
        this();
        
        this.originals = amount;
        this.amount    = amount;
        
        this.interestCount = interestCount;
        this.interestLeft  = interestCount;
        
        this.interestRate100 = interestRate100;
    }

    @Override
    public void dispose() { }

    @Override
    public long getKey() {
        return key;
    }
    
    public void setKey(long key) { this.key = key; }

    @Override
    public String getName() {
        return name;
    }
    
    public void setName(String str) { this.name = str; }

    @Override
    public int getHp() {
        if(amount <= 0) return 0;
        return 1;
    }

    @Override
    public int getMaxHp() {
        return 1;
    }

    @Override
    public void setHp(int hp) { }

    @Override
    public void addHp(int amount) { }

    @Override
    public short getDefenceType() { return 0; }

    @Override
    public int getDefencePoint() { return 0;  }
    
    /** 이자 발생 주기 */
    protected int getDealCycle(City city, Colony colony) {
        return 60 * 24 * 30; // 월 당 이자 발생
    }
    
    /** 1회 이자 금액 계산 */
    public long getInterestOnce(int multiply) {
        if(getInterestCount() <= 0) return 0;
        
        BigDecimal bigAmount = new BigDecimal(String.valueOf(getOriginals())); // 원금
        bigAmount = bigAmount.multiply( new BigDecimal(String.valueOf( getInterestRate100() * multiply )).divide(new BigDecimal("100")) ); // 이자율 곱셈
        // bigAmount = bigAmount.divide(new BigDecimal(String.valueOf(getInterestCount())), 50, RoundingMode.FLOOR); // 이자 발생일만큼 분할 - 월 이자이므로 횟수로 분할할 필요가 없음
        bigAmount = bigAmount.divide(new BigDecimal(String.valueOf(12)), 50, RoundingMode.FLOOR); // 월 이자이므로 연 이자를 12로 나눠야 함
        bigAmount = bigAmount.setScale(0, RoundingMode.FLOOR); // 소수 버림
        
        return bigAmount.longValue();
    }

    @Override
    public void oneCycle(int cycle, City city, Colony colony, int efficiency100, ColonyPanel colPanel) {
        if(cycle % getDealCycle(city, colony) == 0) {
            long money = 0L;
            
            if(interestCount == interestLeft) { // 대출 받은 당일은 이자 납부 제외
                interestLeft--;
                return;
            }
            
            if(interestLeft <= 0) {
                // 이지 납부 기간 (즉 대출 기간) 이 다 된 경우 - 원금 상환해야 함
                money = amount;
                if(colony.getMoney() < money) { 
                    // 상환할 예산이 없는 경우 - 지연이자 발생 (원래의 이자의 2배)
                    money = getInterestOnce(2);
                    colony.modifyingMoney(money * (-1L), city, colony, "Loan", ColonyManager.t("대출이자"));
                    
                    // 신용도도 하락
                    colony.setCredit( (int) (colony.getCredit() * 0.75) );
                    
                    // 제안 중인 대출상품 목록 모두 리셋
                    colony.resetAvailLoans();
                } else {
                    // 원금 상환
                    colony.modifyingMoney(money * (-1L), city, colony, "Loan", ColonyManager.t("대출원금"));
                    amount = 0L;
                    colony.setCredit(colony.getCredit() + 1); // 신용도 상승
                }
            } else {
                // 이자 납부 기간
                money = getInterestOnce(1);
                colony.modifyingMoney(money * (-1L), city, colony, "Loan", ColonyManager.t("대출이자"));
                interestLeft--;
            }
        }
    }

    @Override
    public void fromJson(JsonObject json) {
        key = Long.parseLong(json.get("key").toString());
        setOriginals(Long.parseLong(json.get("originals").toString()));
        setAmount(Long.parseLong(json.get("amount").toString()));
        setInterestRate100(Integer.parseInt(json.get("interestRate").toString()));
        setInterestCount(Integer.parseInt(json.get("interestCount").toString()));
        setInterestLeft(Integer.parseInt(json.get("interestLeft").toString()));
        setName(json.get("name").toString());
    }

    @Override
    public JsonObject toJson() {
        return toJson(true, null, null);
    }

    @Override
    public JsonObject toJson(boolean details, Colony col, City city) {
        JsonObject json = new JsonObject();
        
        json.put("type", "Loan");
        json.put("key", String.valueOf(getKey()));
        json.put("originals", String.valueOf(getOriginals()));
        json.put("amount", String.valueOf(getAmount()));
        json.put("interestRate", new Integer(getInterestRate100()));
        json.put("interestCount", new Integer(getInterestCount()));
        json.put("interestLeft", new Integer(getInterestLeft()));
        json.put("name", getName());
        
        return json;
    }

    @Override
    public BigInteger getCheckerValue() {
        BigInteger res = new BigInteger(String.valueOf(getKey()));
        for(int idx=0; idx<getName().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getName().charAt(idx)))); }
        res = res.add(new BigInteger(String.valueOf(getOriginals())).multiply(new BigInteger("17")));
        res = res.add(new BigInteger(String.valueOf(getAmount())));
        res = res.add(new BigInteger(String.valueOf(getInterestRate100())).multiply(new BigInteger("31")));
        res = res.add(new BigInteger(String.valueOf(getInterestCount())));
        res = res.add(new BigInteger(String.valueOf(getInterestLeft())).multiply(new BigInteger("3")));
        return res;
    }
    public int getInterestLeft() {
        return interestLeft;
    }
    public int getInterestRate100() {
        return interestRate100;
    }
    public void setInterestLeft(int interestLeft) {
        this.interestLeft = interestLeft;
    }
    public void setInterestRate100(int interestRate100) {
        this.interestRate100 = interestRate100;
    }
    public int getInterestCount() {
        return interestCount;
    }
    public void setInterestCount(int interestCount) {
        this.interestCount = interestCount;
    }
    public long getOriginals() {
        return originals;
    }
    public void setOriginals(long originals) {
        this.originals = originals;
    }
    public long getAmount() {
        return amount;
    }
    public void setAmount(long amount) {
        this.amount = amount;
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    @Override
    public boolean isMarkedAsRefresh() {
        return fNeedRefresh;
    }

    @Override
    public void markAsRefresh(boolean f) {
        fNeedRefresh = f;
    }
    
    @Override
    public void markAsRefreshChildren(boolean f) {
        markAsRefresh(f);
    }
    
    /** 사용 가능한 대출 목록 만들기 */
    public static List<Loan> makeAvailableLoanListRandom(Colony col) {
        List<Loan> loans = new ArrayList<Loan>();
        int credit = col.getCredit(); // 신용도
        int interestStd = 10; // 이자 기준치
        int interestCnt = 12; // 대출 기간
        int offerCount  =  5; // 대출 상품 수
        long amount = 10000L; // 대출 원금액
        
        if(credit >= 900) {
            interestStd = 3;
            offerCount  = 10;
        } else if(credit >= 800) {
            interestStd = 5;
            offerCount  = 7;
        } else if(credit >= 700) {
            interestStd = 7;
            offerCount  = 5;
        } else if(credit >= 600) {
            interestStd = 9;
            offerCount  = 5;
        } else if(credit >= 500) {
            interestStd = 12;
            offerCount  = 4;
        } else if(credit >= 400) {
            interestStd = 16;
            offerCount  = 3;
        } else if(credit >= 300) {
            interestStd = 21;
            offerCount  = 2;
        } else if(credit >= 200) {
            interestStd = 32;
            offerCount  = 1;
        } else {
            interestStd = 100;
            offerCount  = 0;
        }
        
        offerCount += ((Math.random() * offerCount) / 2.0);
        
        // 대출 상품 만들기
        for(int idx=0; idx<offerCount; idx++) {
            interestCnt = 6      + (int)  (Math.random() *       36); // 대출 기한 다양하게 제공
            amount      = 10000L + (long) (Math.random() * 1000000L); // 대출 금액 다양하게 제공
            
            // 기간이 길 수록 이자 더 높게 설정
            if(interestStd < 8) {
                if(     interestCnt >= 36) interestStd += (int) Math.round(Math.random() * 5);
                else if(interestCnt >= 24) interestStd += (int) Math.round(Math.random() * 3);
                else if(interestCnt >= 18) interestStd += (int) Math.round(Math.random() * 1);
            } else if(interestStd < 15) {
                if(     interestCnt >= 36) interestStd += (int) Math.round(Math.random() * 7);
                else if(interestCnt >= 30) interestStd += (int) Math.round(Math.random() * 5);
                else if(interestCnt >= 24) interestStd += (int) Math.round(Math.random() * 3);
                else if(interestCnt >= 18) interestStd += (int) Math.round(Math.random() * 2);
                else if(interestCnt >= 12) interestStd += (int) Math.round(Math.random() * 1);
            } else {
                if(     interestCnt >= 36) interestStd += (int) Math.round(Math.random() * 9);
                else if(interestCnt >= 30) interestStd += (int) Math.round(Math.random() * 7);
                else if(interestCnt >= 24) interestStd += (int) Math.round(Math.random() * 5);
                else if(interestCnt >= 18) interestStd += (int) Math.round(Math.random() * 3);
                else if(interestCnt >= 12) interestStd += (int) Math.round(Math.random() * 1);
            }
            
            
            // 상품 생성
            Loan loan = new Loan(amount, interestCnt, interestStd + (int) ((Math.random() * interestStd) / 2.0) );
            loan.setName(getRandomLoanName(loan.getInterestRate100(), interestCnt, amount));
            loans.add(loan);
        }
        
        return loans;
    }
    
    /** 대출상품명 랜덤 생성 */
    protected static String getRandomLoanName(int interestRate100, int interestCnt, long amount) {
        String adjectiveCnt    = ""; // 기간에 따른 수식어
        String adjectiveAmount = ""; // 금액에 따른 수식어
        String adjectiveRate   = ""; // 이자율에 따른 수식어
        double rand;
        
        if(interestCnt < 12) { adjectiveCnt = "단기"; }
        else if(interestCnt < 24) { adjectiveCnt = ""; }
        else { adjectiveCnt = "장기"; }
        if(DataUtil.isNotEmpty(adjectiveCnt)) adjectiveCnt = ColonyManager.t(adjectiveCnt);
        
        if(amount < 100000L) { 
            rand = Math.random();
            if(rand <= 0.2) adjectiveAmount = "미니";
            else if(rand <= 0.4) adjectiveAmount = "혜성";
            else if(rand <= 0.6) adjectiveAmount = "융통";
            else if(rand <= 0.8) adjectiveAmount = "목돈";
            else adjectiveAmount = "희망";
        } else if(amount < 300000L) {
            rand = Math.random();
            if(rand <= 0.2) adjectiveAmount = "행성";
            else if(rand <= 0.4) adjectiveAmount = "계획형";
            else if(rand <= 0.6) adjectiveAmount = "하이";
            else if(rand <= 0.8) adjectiveAmount = "포톤";
            else adjectiveAmount = "비지니스";
        } else {
            rand = Math.random();
            if(rand <= 0.2) adjectiveAmount = "은하";
            else if(rand <= 0.4) adjectiveAmount = "블랙홀";
            else if(rand <= 0.6) adjectiveAmount = "초신성";
            else if(rand <= 0.8) adjectiveAmount = "울트라";
            else adjectiveAmount = "익스트림";
        }
        if(DataUtil.isNotEmpty(adjectiveAmount)) adjectiveAmount = ColonyManager.t(adjectiveAmount);
        
        if(interestRate100 < 10) {
            rand = Math.random();
            if(rand <= 0.3) adjectiveRate = "알뜰";
            else if(rand <= 0.6) adjectiveRate = "효율";
            else adjectiveRate = "스마트";
        } else if(interestRate100 <= 15) {
            adjectiveRate = "";
        } else {
            rand = Math.random();
            if(rand <= 0.333) adjectiveRate = "기회";
            else if(rand <= 0.666) adjectiveRate = "욜로";
            else adjectiveRate = "라스트";
        }
        
        if(DataUtil.isNotEmpty(adjectiveRate)) adjectiveRate = ColonyManager.t(adjectiveRate);
        
        String res = "";
        if(DataUtil.isNotEmpty(adjectiveCnt   )) res += " " + adjectiveCnt;
        if(DataUtil.isNotEmpty(adjectiveAmount)) res += " " + adjectiveAmount;
        if(DataUtil.isNotEmpty(adjectiveRate  )) res += " " + adjectiveRate;
        
        rand = Math.random();
        if(rand <= 0.5) res += "론";
        else res += "대출";
        
        return res.trim();
    }
}
