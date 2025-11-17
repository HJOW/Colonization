package org.duckdns.hjow.graphics;

import java.awt.Color;
import java.io.Serializable;

/** 직선 도형 */
public class LineObject3D implements Serializable {
	private static final long serialVersionUID = 763902670992651439L;
	protected Coordinate3D from, to;
	protected Color color = Color.BLUE;
	public LineObject3D() {}
	public Coordinate3D getFrom() {
		return from;
	}
	public void setFrom(Coordinate3D from) {
		this.from = from;
	}
	public Coordinate3D getTo() {
		return to;
	}
	public void setTo(Coordinate3D to) {
		this.to = to;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
}
