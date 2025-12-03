package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GUIColonyManagerInterface;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.ui.graphics.Coordinate3D;
import org.duckdns.hjow.commons.util.GUIUtil;

/** 우주 현황판 대화상자 */
public class SpaceViewDialog implements Disposeable {
	protected transient GUIColonyManagerInterface superInstance;
	
    protected transient JDialog    dialog;
    protected transient SpacePanel spaces;
    
    protected transient int speedMove   = 10;
    protected transient int speedRotate =  2;
    
    public SpaceViewDialog(GUIColonyManagerInterface superInstance) {
    	this.superInstance = superInstance;
    	
    	dialog = new JDialog(superInstance.getDialog());
    	dialog.setSize(300, 300);
        dialog.setTitle(ColonyManager.t("View"));
        GUIUtil.centerWindow(dialog);
        dialog.setIconImage(GUIColonyManager.getIcon());
        dialog.setLayout(new BorderLayout());
    	
        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BorderLayout());
        dialog.add(pnMain, BorderLayout.CENTER);
        
        JPanel pnCenter, pnDown;
        pnCenter = new JPanel();
        pnDown   = new JPanel();
        pnCenter.setLayout(new BorderLayout());
        pnDown.setLayout(new BorderLayout());
        pnMain.add(pnCenter, BorderLayout.CENTER);
        pnMain.add(pnDown  , BorderLayout.SOUTH);
        
        spaces = new SwingSpacePanel();
        
        Colony col = superInstance.getColony();
        spaces.setColony(col);
        if(col != null) spaces.setCameraLocation(col.getCoordinate());
        
        pnCenter.add(spaces.getComponent(), BorderLayout.CENTER);
        dialog.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		if(superInstance != null) superInstance.onChildDialogClosed();
        	}
		});
        
        dialog.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {				
				onKeyPressed(e.getKeyCode());
			}
		});
        
        remakeMessage();
    }
    
    /** 키보드 키 입력 시 호출 */
    protected void onKeyPressed(int code) {
    	if(code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
    		spaces.setCameraLocation(new Coordinate3D(spaces.getCameraLocation().getX() + speedMove, spaces.getCameraLocation().getY(), spaces.getCameraLocation().getZ()));
    		remakeMessage();
		} else if(code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
			spaces.setCameraLocation(new Coordinate3D(spaces.getCameraLocation().getX() - speedMove, spaces.getCameraLocation().getY(), spaces.getCameraLocation().getZ()));
			remakeMessage();
		} else if(code == KeyEvent.VK_LEFT  || code == KeyEvent.VK_A) {
			spaces.setCameraLocation(new Coordinate3D(spaces.getCameraLocation().getX(), spaces.getCameraLocation().getY() - speedMove, spaces.getCameraLocation().getZ()));
			remakeMessage();
		} else if(code == KeyEvent.VK_RIGHT  || code == KeyEvent.VK_D) {
			spaces.setCameraLocation(new Coordinate3D(spaces.getCameraLocation().getX(), spaces.getCameraLocation().getY() + speedMove, spaces.getCameraLocation().getZ()));
			remakeMessage();
		} else if(code == KeyEvent.VK_R || code == KeyEvent.VK_PAGE_UP) {
			spaces.setCameraLocation(new Coordinate3D(spaces.getCameraLocation().getX(), spaces.getCameraLocation().getY(), spaces.getCameraLocation().getZ() + speedMove));
			remakeMessage();
		} else if(code == KeyEvent.VK_F || code == KeyEvent.VK_PAGE_DOWN) {
			spaces.setCameraLocation(new Coordinate3D(spaces.getCameraLocation().getX(), spaces.getCameraLocation().getY(), spaces.getCameraLocation().getZ() - speedMove));
			remakeMessage();
		} else if(code == KeyEvent.VK_Q) {
			spaces.rotateCamera(spaces.getCameraYaw() - Math.toRadians(speedRotate), spaces.getCameraPitch());
			remakeMessage();
		} else if(code == KeyEvent.VK_E) {
			spaces.rotateCamera(spaces.getCameraYaw() + Math.toRadians(speedRotate), spaces.getCameraPitch());
			remakeMessage();
		} else if(code == KeyEvent.VK_T) {
			spaces.rotateCamera(spaces.getCameraYaw(), spaces.getCameraPitch() - Math.toRadians(speedRotate));
			remakeMessage();
		} else if(code == KeyEvent.VK_G) {
			spaces.rotateCamera(spaces.getCameraYaw(), spaces.getCameraPitch() + Math.toRadians(speedRotate));
			remakeMessage();
		} else if(code == KeyEvent.VK_Y) {
			speedMove += 2;
			if(speedMove   >= 65536) speedMove   = 65536;
			if(speedRotate >=    90) speedRotate =    90;
			remakeMessage();
		} else if(code == KeyEvent.VK_H) {
			speedMove -= 2;
			if(speedMove   <= 2) speedMove   = 2;
			if(speedRotate <= 1) speedRotate = 1;
			remakeMessage();
		}  else if(code == KeyEvent.VK_O) {
			Colony col = superInstance.getColony(); // 정착지
            spaces.setColony(col);
            
            // 카메라 위치 초기화
            spaces.resetCameraPosition();
            
            // 새로 고침
            remakeMessage();
		}
    }
    
    protected void remakeMessage() {
    	String msg =  ColonyManager.t("카메라 위치 : [X], [Y], [Z]");
    	msg += "\n" + ColonyManager.t("카메라 방향 : [YAW], [PITCH]");
    	msg += "\n" + ColonyManager.t("속도 : [SPEEDNORMAL], [SPEEDROTATE]");
    	spaces.setMessage(msg.replace("[SPEEDNORMAL]", String.valueOf(speedMove)).replace("[SPEEDROTATE]", String.valueOf(speedRotate)));
    	spaces.refresh();
    }
    
    public JDialog getDialog() { return dialog; }
    public void open(Colony col) {
    	refresh(col);
    	dialog.setVisible(true);
    }
    
    public void refresh(Colony col) {
    	spaces.setColony(col);
    	spaces.refresh();
    }
    
    public Dimension getSize() {
    	return dialog.getSize();
    }
    
    public void setSize(int w, int h) {
    	dialog.setSize(w, h);
    }
    
    public void setLocation(int x, int y) {
    	dialog.setLocation(x, y);
    }
    
    public void setLocationBottom(Window superDialog) {
    	setLocationBottom(superDialog, 0, 0);
    }
    
    public void setLocationBottom(Window superDialog, int xAdds, int yAdds) {
        Point p = superDialog.getLocation();
        dialog.setSize(superDialog.getWidth(), dialog.getHeight());
        dialog.setLocation((int) p.getX() + xAdds, (int) (p.getY() + superDialog.getHeight() + yAdds));
    }
    
    public boolean isVisible() {
    	if(dialog == null) return false;
    	return dialog.isVisible();
    }
    
	@Override
	public void dispose() {
		if(dialog != null) {
			if(dialog.isVisible()) dialog.setVisible(false);
			dialog = null;
		}
		if(spaces != null) spaces.dispose();
		spaces = null;
		superInstance = null;
	}
    
}
