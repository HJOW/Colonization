package org.duckdns.hjow.commons.ui.graphics;

public class AdvancedCoordinate3D extends Coordinate3D { // TODO : 공통 lib 의 Coordinate3D 에 기능 병합
	private static final long serialVersionUID = 3488063769489228736L;
	public AdvancedCoordinate3D() { super(); }
	public AdvancedCoordinate3D(long x, long y, long z) { super(x, y, z); }
	
	/** 2D 영역에 투사, 새 2D 좌표 반환, focalLength 는 카메라의 초점 거리를 의미하며 500 사용을 권장, yaw 는 카메라의 X축 회전 (방향), pitch 는 카메라의 Y축 회전(방향) 을 의미 */
	public Coordinate2D project(Coordinate3D camera, double yaw, double pitch, double focalLength, double screenCenterX, double screenCenterY) {
		double dx = getX() - camera.getX();
        double dy = getY() - camera.getY();
        double dz = getZ() - camera.getZ();
        
        // yaw (Y축 회전)
        double cosY = Math.cos(yaw);
        double sinY = Math.sin(yaw);
        double tx = dx * cosY - dx * sinY;
        double tz = dx * sinY + dz * cosY;
        dx = tx;
        dz = tz;
        
        // pitch (X축 회전)
        double cosP = Math.cos(pitch);
        double sinP = Math.sin(pitch);
        double ty = dy * cosP - dz * sinP;
        tz = dy * sinP + dz * cosP;
        dy = ty;
        dz = tz;
        
        double cx = dx;
        double cy = dy;
        double cz = dz;
        
        if(cz <= 0) return null; // 카메라 뒤에 있는 경우 제외
        
        double px = focalLength * cx / cz;
        double py = focalLength * cy / cz;
        
        double u = px + screenCenterX;
        double v = py + screenCenterY;
        
        return new Coordinate2D((long) u, (long) v);
	}
}
