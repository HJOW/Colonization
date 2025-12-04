package org.duckdns.hjow.colonization.ui;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JPanel;

import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.commons.ui.graphics.Coordinate2D;
import org.duckdns.hjow.commons.ui.graphics.Coordinate3D;

/** 함선 위치 현황 출력을 위한 패널 */
public abstract class AbstractSpacePanel extends JPanel implements SpacePanel {
	private static final long serialVersionUID = 3120354750879319010L;
	protected Colony       colony;
	protected Coordinate3D cameraLocation = new Coordinate3D(0L, 0L, 0L);
	protected double       cameraYaw      = 0.0;
	protected double       cameraPitch    = 0.0;
	protected int          viewWidth      = 600;
	protected int          viewHeight     = 400;
	protected String       message        = "";
	public AbstractSpacePanel() { setLayout(null); }
	
	/** 정착지 객체 받기 */
	@Override
	public void setColony(Colony colony) {
		this.colony = colony;
		resetCameraPosition();
		refresh();
	}
	
	/** 카메라 위치 초기화 */
	public void resetCameraPosition() {
		if(colony != null) { 
			cameraLocation.setX(colony.getX());
			cameraLocation.setY(colony.getY());
			cameraLocation.setZ(colony.getZ() + 100L);
		} else {
			cameraLocation.setX(0L);
			cameraLocation.setY(0L);
			cameraLocation.setZ(0L);
		}
		setCameraToSee();
	}
	
	@Override
	public void dispose() {
		colony = null;
	}
	
	/** 새로고침 */
	@Override
	public void refresh() { }
	
	/** 그리기 작업 수행 */
	protected void draw(Graphics g) { }
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	
	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public Coordinate3D getCameraLocation() {
		return cameraLocation;
	}

	@Override
	public void setCameraLocation(Coordinate3D cameraLocation) {
		this.cameraLocation = cameraLocation;
	}

	@Override
	public double getCameraYaw() {
		return cameraYaw;
	}

	@Override
	public void setCameraYaw(double cameraYaw) {
		this.cameraYaw = cameraYaw;
	}

	@Override
	public double getCameraPitch() {
		return cameraPitch;
	}

	@Override
	public void setCameraPitch(double cameraPitch) {
		this.cameraPitch = cameraPitch;
	}

	@Override
	public void rotateCamera(double yaw, double pitch) {
		setCameraYaw(yaw);
		setCameraPitch(pitch);
		
		while(this.getCameraYaw()   > Math.PI * 2) this.setCameraYaw(  this.getCameraYaw()   - (2 * Math.PI)); 
		while(this.getCameraPitch() > Math.PI * 2) this.setCameraPitch(this.getCameraPitch() - (2 * Math.PI)); 
		while(this.getCameraYaw()   < 0) this.setCameraYaw(  2 * Math.PI - this.getCameraYaw()  );
		while(this.getCameraPitch() < 0) this.setCameraPitch(2 * Math.PI - this.getCameraPitch());
	}
	
	/** 카메라가 정착지 좌표를 바라보도록 방향 조정시키기 (카메라 위치는 변하지 않음) */
    public void setCameraToSee() {
    	Colony col = getColony();
    	if(col == null) {
    	    rotateCamera(0.0, 0.0);
    	} else {
    	    setCameraToSee(getColony().getCoordinate());
    	}
    }
	
	/** 카메라가 특정 좌표를 바라보도록 방향 조정시키기 (카메라 위치는 변하지 않음) */
    public void setCameraToSee(Coordinate3D target) {
    	if(target == null) target = getColony().getCoordinate();
    	// 이 카메라가 타겟을 바라보도록 yaw 와 pitch 값 지정
    	Coordinate3D coord = getCameraLocation();
    	
        // 1. 방향 계산
        long dx = target.getX() - coord.getX();
        long dy = target.getY() - coord.getY();
        long dz = target.getZ() - coord.getZ();

        // 2. yaw (XZ 평면에서의 각도) 계산
        double yaw = Math.atan2((double) dx, (double) dz);

        // 3. pitch (수평면과 방향 사이의 각도) 계산
        double horizontalDist = Math.sqrt( (dx * dx) + (dz * dz) );
        double pitch = Math.atan2((double) dy, (double) horizontalDist);

		rotateCamera(yaw, pitch);
    }

    @Override
	public int getViewWidth() {
		return viewWidth;
	}

    @Override
	public void setViewWidth(int viewWidth) {
    	if(viewWidth < 400) viewWidth = 400;
		this.viewWidth = viewWidth;
	}

    @Override
	public int getViewHeight() {
		return viewHeight;
	}

    @Override
	public void setViewHeight(int viewHeight) {
    	if(viewHeight < 400) viewHeight = 400;
		this.viewHeight = viewHeight;
	}

	@Override
	public Colony getColony() {
		return colony;
	}
	
	@Override
	public Component getComponent() {
		return this;
	}
	
	/** 2D로 좌표 투영 */ // TODO : 공통 lib 에 반영
    public static Coordinate2D project(Coordinate3D target, Coordinate3D camera, double yaw, double pitch, double focalLength, double screenCenterX, double screenCenterY) {
        // Translate point relative to camera
        double dx = target.getX() - camera.getX();
        double dy = target.getY() - camera.getY();
        double dz = target.getZ() - camera.getZ();

        // Rotate point around camera
        // Yaw (Y-axis rotation)
        double cosYaw = Math.cos(yaw);
        double sinYaw = Math.sin(yaw);
        double rotatedX = dx * cosYaw + dz * sinYaw;
        double rotatedZ = -dx * sinYaw + dz * cosYaw;

        // Pitch (X-axis rotation)
        double cosPitch = Math.cos(pitch);
        double sinPitch = Math.sin(pitch);
        double rotatedY = dy * cosPitch - rotatedZ * sinPitch;
        rotatedZ = dy * sinPitch + rotatedZ * cosPitch;

        // Roll is not used in this case

        double cx = rotatedX;
        double cy = rotatedY;
        double cz = rotatedZ;

        if(cz <= 0) return null; // Behind the camera

        // Project to 2D
        double px = focalLength * cx / cz;
        double py = focalLength * cy / cz;

        // Convert to screen coordinates
        double u = px + screenCenterX;
        double v = py + screenCenterY;

        return new Coordinate2D((long) u, (long) v);
    }
}