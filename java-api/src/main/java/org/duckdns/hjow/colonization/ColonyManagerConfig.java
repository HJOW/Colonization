package org.duckdns.hjow.colonization;

import java.io.Serializable;
import java.util.List;

import org.duckdns.hjow.commons.core.JsonCompatible;

/** Colonization 설정 인터페이스 */
public interface ColonyManagerConfig extends Serializable, JsonCompatible {
	/** 설정 값 반환, 해당 키가 없으면 null 이 리턴 */
    public Object get(String key);
    /** 설정 값 반환, 값이 Map 이어야 예외가 발생하지 않음. 해당 키가 없으면 null 이 리턴 */
    public ColonyManagerConfig getMap(String key);
    /** 설정 값 반환, 값이 List 이어야 예외가 발생하지 않음. 해당 키가 없으면 null 이 리턴 */
    public List<Object> getList(String key);
    /** 설정 값을 문자열로 취급하여 문자열 반환, 강제 형변환될 수 있음. null 일 경우 공란 반환 */
    public String getString(String key);
    /** 설정 값을 boolean 으로 취급하여 반환, 변환이 불가능한 값의 경우 예외가 발생함. 숫자 타입의 경우 0인 경우만 false, 그외에는 true 리턴 */
    public boolean getBool(String key);
    /** 설정 값을 int 로 취급하여 반환, 변환 불가능한 경우 예외가 발생함 */
    public int getInt(String key);
    /** 설정 값을 double 로 취급하여 반환, 변환 불가능한 경우 예외가 발생함 */
    public double getDouble(String key);
    /** 해당 설정 값 존재여부 반환 */
    public boolean containsKey(String key);
    /** 설정 변경 */
    public void set(String key, Object obj);
    /** 설정 모두 삭제 */
    public void clear();
    /** 이 객체를 복제 */
    public ColonyManagerConfig cloneSelf();
}
