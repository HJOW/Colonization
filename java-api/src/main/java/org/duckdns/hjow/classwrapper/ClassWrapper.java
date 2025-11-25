package org.duckdns.hjow.classwrapper;

/** Class 타입 객체를 감싸거나, 혹은 Class 타입을 만들어 리턴할 수 있는 객체임을 표시 */ // TODO : 공통 lib로 이관
public interface ClassWrapper {
    public Class<?> getWrappedClass();
}
