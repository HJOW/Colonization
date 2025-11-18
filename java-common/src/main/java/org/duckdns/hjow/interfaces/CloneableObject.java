package org.duckdns.hjow.interfaces;

/** 복제 가능 여부를 표시 - 기존 java.lang 의 Cloneable 과 분리 */
public interface CloneableObject {
	/** 객체 복제 */
    public Object cloneThis();
}
