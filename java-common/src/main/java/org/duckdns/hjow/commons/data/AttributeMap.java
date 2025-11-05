package org.duckdns.hjow.commons.data;

/** Map<String, String> 대안 클래스, Kotlin 과 사용 시 ClassFormatError 방지 용도 */
public class AttributeMap extends java.util.HashMap<String, Object> {
	private static final long serialVersionUID = 4314017342641734729L;
	public AttributeMap() { super(); }
    
}
