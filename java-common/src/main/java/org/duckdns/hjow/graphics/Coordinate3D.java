package org.duckdns.hjow.graphics;

import java.io.Serializable;

import org.duckdns.hjow.commons.json.JsonObject;

/** 3D 좌표 */
public class Coordinate3D implements Serializable {
	private static final long serialVersionUID = -3845428403760941094L;
	protected long x, y, z;
	public Coordinate3D() {}
	public Coordinate3D(long x, long y, long z) {this.x = x; this.y = y; this.z = z;}

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
	
	public long getZ() {
		return z;
	}
	
	public void setZ(long z) {
		this.z = z;
	}
	
	/** 2D 영역에 투사, 새 2D 좌표 반환 (Z값이 초기화되지 않음) */
	public Coordinate2D project(Coordinate3D camera, double screenCenterX, double screenCenterY) {
		return project(camera, 500, screenCenterX, screenCenterY);
	}
	
	/** 2D 영역에 투사, 새 2D 좌표 반환 (Z값이 초기화되지 않음) focalLength 는 카메라의 초점 거리를 의미 */
	public Coordinate2D project(Coordinate3D camera, double focalLength, double screenCenterX, double screenCenterY) {
		// Translate point relative to camera
        double x_prime = getX() - camera.getX();
        double y_prime = getY() - camera.getY();
        double z_prime = getZ() - camera.getZ();

        // Apply perspective projection formula
        double scale = focalLength / z_prime;
        double x2d = (x_prime * scale) + screenCenterX;
        double y2d = (y_prime * scale) + screenCenterY;

        return new Coordinate2D((long) x2d, (long) y2d);
	}
	
	@Override
	public String toString() {
		return "(" + getX() + ", " + getY() + ", " + getZ() + ")";
	}
	
	/** JSON 객체로 변환 */
	public JsonObject toJSON() {
		JsonObject json = new JsonObject();
		json.put("x", String.valueOf(getX()));
		json.put("y", String.valueOf(getY()));
		json.put("z", String.valueOf(getZ()));
		return json;
	}
}
