package org.duckdns.hjow.colonization.ui;

import java.awt.Color;
import java.awt.geom.Area;

import org.duckdns.hjow.commons.ui.graphics.Coordinate2D;
import org.duckdns.hjow.commons.ui.graphics.Object2D;

/** 텍스트 출력을 위한 객체 */
public class TextObject2D implements Object2D {
	private static final long serialVersionUID = -1577361436962945761L;
	protected Coordinate2D left;
	protected String content;
	protected Color color = Color.BLUE;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	@Override
	public Area area() {
		return null; // 미지원 (텍스트의 정확한 길이를 알려면 FontMetrics 객체를 구해야 함
	}
	public Coordinate2D getLeft() {
		return left;
	}
	public void setLeft(Coordinate2D left) {
		this.left = left;
	}
}
