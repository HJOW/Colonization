package org.duckdns.hjow.colonization.mutables;

/** Mutable 타입의 실수 Wrapper 클래스, 메소드의 호출로 객체 자체의 값이 변할 수 있음. */
public class DoubleWrapper extends Number { // TODO 공통 lib로 이관
	private static final long serialVersionUID = -4534329865813690816L;
	protected double value = 0.0;
    public DoubleWrapper() {}
    public DoubleWrapper(double v) { this.value = v; }
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public void increase() {
		if(this.value <= Double.MAX_VALUE - 1.0) this.value += 1.0;
	}
	@Override
	public int intValue() {
		return (int) getValue();
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
		return getValue();
	}
	@Override
	public String toString() {
		return String.valueOf(getValue());
	}
}
