package org.duckdns.hjow.graphics;

import java.awt.Color;
import java.io.Serializable;

/** 직선 도형 */
public class LineObject2D implements Serializable {
	private static final long serialVersionUID = 763902670992651439L;
	protected Coordinate2D from, to;
	protected Color color = Color.BLUE;
	public LineObject2D() {}
	public Coordinate2D getFrom() {
		return from;
	}
	public void setFrom(Coordinate2D from) {
		this.from = from;
	}
	public Coordinate2D getTo() {
		return to;
	}
	public void setTo(Coordinate2D to) {
		this.to = to;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
}
