package org.duckdns.hjow.graphics;

import java.awt.Color;
import java.io.Serializable;

/** 2D 원 도형 */
public class OvalObjects2D implements Serializable {
	private static final long serialVersionUID = -1441714764004875901L;
	protected Coordinate2D center;
	protected int r;
	protected Color color = Color.BLUE;
	public OvalObjects2D() {}
	public Coordinate2D getCenter() {
		return center;
	}
	public void setCenter(Coordinate2D center) {
		this.center = center;
	}
	public int getR() {
		return r;
	}
	public void setR(int r) {
		this.r = r;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public long getX() {
		return center.getX();
	}
	public long getY() {
		return center.getY();
	}
}
