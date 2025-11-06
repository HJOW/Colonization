package org.duckdns.hjow.colonization;

import java.io.Serializable;
import java.math.BigInteger;

import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.exception.KnownRuntimeException;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.city.City;

/** 돈의 수입, 지출 이력 하나를 담는 VO */
public class AccountingData implements Serializable, Disposeable {
    private static final long serialVersionUID = 6059734786112483575L;
    protected BigInteger time = BigInteger.ZERO;
    protected long amount = 0L;
    protected String reason = "";
    protected String moreString = "";
    protected long cityKey;
    protected long sourceKey;
    
    protected transient boolean disposed = false;
    
    /** 기본 생성자 (Serializable 가능한 객체를 위해 존재할 뿐, 직접 사용 비권장) */
    public AccountingData() {}

    /**  
     * 생성자, 수입/지출 발생한 시간, 금액, 사유, 발생한 도시와 원인 제공 요소 (시민 또는 시설) 을 받는다. 
     * 
     * @param time 수입/지출 발생한 시간
     * @param amount 수입/지출 금액
     * @param reason 수입/지출 사유
     * @param city 수입/지출이 발생한 도시
     * @param sources 수입/지출의 원인 제공 요소 (시민 또는 시설)
    */
    public AccountingData(BigInteger time, long amount, String reason, City city, ColonyElements sources) {
        this();
        this.time = time;
        this.amount = amount;
        this.reason = reason;
        if(city != null) this.cityKey = city.getKey();
        else this.cityKey = 0L;
        this.sourceKey = sources.getKey();
    }
    /**  
     * 생성자, 수입/지출 발생한 시간, 금액, 사유, 발생한 도시와 원인 제공 요소 (시민 또는 시설), 추가 사항(문자열) 을 받는다. 
     * 
     * @param time 수입/지출 발생한 시간
     * @param amount 수입/지출 금액
     * @param reason 수입/지출 사유
     * @param city 수입/지출이 발생한 도시
     * @param sources 수입/지출의 원인 제공 요소 (시민 또는 시설)
     * @param moreString 추가 사항(문자열)
    */
    public AccountingData(BigInteger time, long amount, String reason, City city, ColonyElements sources, String moreString) {
        this(time, amount, reason, city, sources);
        this.moreString = moreString;
    }

    /**
     * JSON 객체로부터 수입/지출 데이터를 읽어 객체 생성
     * @param json : JSON 객체
     */
    public AccountingData(JsonObject json) {
        this();
        fromJson(json);
    }
    
    public BigInteger getTime() {
        return time;
    }
    public void setTime(BigInteger time) {
        this.time = time;
    }

    public long getCityKey() {
        return cityKey;
    }
    public void setCityKey(long cityKey) {
        this.cityKey = cityKey;
    }
    public long getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public long getSourceKey() {
        return sourceKey;
    }
    public void setSourceKey(long sourceKey) {
        this.sourceKey = sourceKey;
    }
    public String getMoreString() {
        return moreString;
    }
    public void setMoreString(String moreString) {
        this.moreString = moreString;
    }
    public boolean isDisposed() {
        return disposed;
    }
    /** 객체 사용 중단 시 호출 권장 */
    public void dispose() {
        this.disposed = true;
    }
    
    /** 이 수입/지출 내역을 JSON 형태로 만들어 반환 */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type", "AccountingHistory");
        json.put("time", getTime().toString());
        json.put("reason", getReason());
        json.put("city", String.valueOf(getCityKey()));
        json.put("source", String.valueOf(getSourceKey()));
        json.put("more", String.valueOf(getMoreString()));
        
        return json;
    }
    
    /** 
     * JSON 객체로부터 수입/지출 데이터 받아 이 객체에 적용
     * @param json : JSON 객체
     */
    public void fromJson(JsonObject json) {
        if(! "AccountingHistory".equals(json.get("type"))) throw new KnownRuntimeException("This object is not AccountingHistory type.");
        
        setTime(new BigInteger(json.get("time").toString()));
        setReason(json.get("reason").toString());
        setCityKey(Long.parseLong(json.get("city").toString()));
        setSourceKey(Long.parseLong(json.get("source").toString()));
        if(json.containsKey("more")) setMoreString(json.get("more").toString());
    }
}
