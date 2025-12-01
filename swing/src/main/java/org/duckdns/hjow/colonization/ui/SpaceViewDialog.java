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
	protected GUIColonyManagerInterface superInstance;
	
    protected JDialog    dialog;
    protected SpacePanel spaces;
    
    public SpaceViewDialog(GUIColonyManagerInterface superInstance) {
    	dialog = new JDialog(superInstance.getDialog());
    	dialog.setSize(300, 300);
        dialog.setTitle(ColonyManager.t("View"));
        GUIUtil.centerWindow(dialog);
        dialog.setIconImage(GUIColonyManager.getIcon());
        dialog.setLayout(new BorderLayout());
    	
        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BorderLayout());
        dialog.add(pnMain, BorderLayout.CENTER);
        
        spaces = new SwingSpacePanel();
        dialog.add(spaces.getComponent(), BorderLayout.CENTER);
        
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
    }
    
    /** 키보드 키 입력 시 호출 */
    protected void onKeyPressed(int code) {
    	if(code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
    		spaces.setCameraLocation(new Coordinate3D(spaces.getCameraLocation().getX() + 1, spaces.getCameraLocation().getY(), spaces.getCameraLocation().getZ()));
    		spaces.refresh();
		} else if(code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
			spaces.setCameraLocation(new Coordinate3D(spaces.getCameraLocation().getX() - 1, spaces.getCameraLocation().getY(), spaces.getCameraLocation().getZ()));
			spaces.refresh();
		} else if(code == KeyEvent.VK_LEFT  || code == KeyEvent.VK_A) {
			spaces.setCameraLocation(new Coordinate3D(spaces.getCameraLocation().getX(), spaces.getCameraLocation().getY() - 1, spaces.getCameraLocation().getZ()));
			spaces.refresh();
		} else if(code == KeyEvent.VK_RIGHT  || code == KeyEvent.VK_D) {
			spaces.setCameraLocation(new Coordinate3D(spaces.getCameraLocation().getX(), spaces.getCameraLocation().getY() + 1, spaces.getCameraLocation().getZ()));
			spaces.refresh();
		} else if(code == KeyEvent.VK_R) {
			spaces.setCameraLocation(new Coordinate3D(spaces.getCameraLocation().getX(), spaces.getCameraLocation().getY(), spaces.getCameraLocation().getZ() + 1));
			spaces.refresh();
		} else if(code == KeyEvent.VK_F) {
			spaces.setCameraLocation(new Coordinate3D(spaces.getCameraLocation().getX(), spaces.getCameraLocation().getY(), spaces.getCameraLocation().getZ() - 1));
			spaces.refresh();
		} else if(code == KeyEvent.VK_Q) {
			spaces.rotateCamera(spaces.getCameraYaw() + Math.toRadians(1), spaces.getCameraPitch());
			spaces.refresh();
		} else if(code == KeyEvent.VK_E) {
			spaces.rotateCamera(spaces.getCameraYaw() - Math.toRadians(1), spaces.getCameraPitch());
			spaces.refresh();
		}
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
