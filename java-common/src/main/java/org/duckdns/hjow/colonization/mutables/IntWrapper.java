package org.duckdns.hjow.colonization.mutables;

/** Mutable 타입의 정수 Wrapper 클래스, 메소드의 호출로 객체 자체의 값이 변할 수 있음. */
public class IntWrapper extends Number { // TODO 공통 lib로 이관
	private static final long serialVersionUID = -442192262006962528L;
	protected int value = 0;
    public IntWrapper() {}
    public IntWrapper(int v) { this.value = v; }
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public void increase() {
		if(this.value <= Integer.MAX_VALUE - 1) this.value++;
	}
	@Override
	public int intValue() {
		return getValue();
	}
	@Override
	public long longValue() {
		return (long) getValue();
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
