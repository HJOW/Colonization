package org.duckdns.hjow.classwrapper;

import java.io.Serializable;

/** 클래스 풀네임을 가지고 있는 Class Wrapper */ // TODO : 공통 lib로 이관
public class ClassNameOriginlClassWrapper implements ClassWrapper, Serializable {
	private static final long serialVersionUID = -5735182358814379440L;
	protected String className;
    public ClassNameOriginlClassWrapper() {}
    public ClassNameOriginlClassWrapper(String className) { this.className = className; }
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	@Override
	public Class<?> getWrappedClass() {
		try { return Class.forName(className); } catch(Exception ex) { throw new RuntimeException(ex.getMessage(), ex); }
	}
}
