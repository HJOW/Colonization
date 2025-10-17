package org.duckdns.hjow.colonization.mutables;

/** Mutable 타입의 정수 Wrapper 클래스, 메소드의 호출로 객체 자체의 값이 변할 수 있음. */
public class LongWrapper extends Number { // TODO 공통 lib로 이관
	private static final long serialVersionUID = -442192262006962528L;
	protected long value = 0;
    public LongWrapper() {}
    public LongWrapper(long v) { this.value = v; }
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}
	public void increase() {
		if(this.value <= Long.MAX_VALUE - 1) this.value++;
	}
	@Override
	public int intValue() {
		return (int) getValue();
	}
	@Override
	public long longValue() {
		return getValue();
	}
	@Override
	public float floatValue() {
		return (float) getValue();
	}
	@Override
	public double doubleValue() {
		return (double) getValue();
	}
	@Override
	public String toString() {
		return String.valueOf(getValue());
	}
}
