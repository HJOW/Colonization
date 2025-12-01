package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GUIColonyManagerInterface;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.util.GUIUtil;

/** 우주 현황판 대화상자 */
public class SpaceViewDialog implements Disposeable {
	protected GUIColonyManagerInterface superInterface;
	
    protected JDialog    dialog;
    protected SpacePanel spaces;
    
    public SpaceViewDialog(GUIColonyManagerInterface superInterface) {
    	dialog = new JDialog(superInterface.getDialog());
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
		superInterface = null;
	}
    
}
