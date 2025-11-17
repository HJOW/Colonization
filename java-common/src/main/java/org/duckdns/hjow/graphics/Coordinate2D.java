package org.duckdns.hjow.graphics;

import java.io.Serializable;

import org.duckdns.hjow.commons.json.JsonObject;

/** 2D 좌표 */
public class Coordinate2D implements Serializable {
	private static final long serialVersionUID = 1080468103919411939L;
	protected long x, y;
	public Coordinate2D() {}
	public Coordinate2D(long x, long y) {this.x = x; this.y = y;}

	public long getX() {
		return x;
	}

	public void setX(long x) {
		this.x = x;
	}

	public long getY() {
		return y;
	}

	public void setY(long y) {
		this.y = y;
	}
	
	@Override
	public String toString() {
		return "(" + getX() + ", " + getY() + ")";
	}
	
	/** JSON 객체로 변환 */
	public JsonObject toJSON() {
		JsonObject json = new JsonObject();
		json.put("x", String.valueOf(getX()));
		json.put("y", String.valueOf(getY()));
		return json;
	}
}
