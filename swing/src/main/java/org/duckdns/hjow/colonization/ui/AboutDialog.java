package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.util.ClassUtil;
import org.duckdns.hjow.commons.util.GUIUtil;

public class AboutDialog implements Disposeable {
	protected Dialog dialog;
	protected Panel pnMain, pnCenter, pnDown;
    protected Label lbTitle, lbSub, lbVer;
    public AboutDialog() {
    	this(null, null, null, null);
    }
    
    public AboutDialog(Window win, String title, String sub, String ver) {
    	init(win, title, sub, ver);
    }
    
    protected void init(Window win, String title, String sub, String ver) {
    	if(win == null) dialog = new Dialog(null, false);
    	else dialog = new Dialog(win);
    	
        dialog.setSize(300, 170);
        dialog.setTitle(title);
        dialog.setLayout(new BorderLayout());
        GUIUtil.centerWindow(dialog);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        setIconImage(getDefaultIcon());
        
        lbTitle = new Label(title);
        lbSub   = new Label(sub);
        lbVer   = new Label(ver);
        
        lbTitle.setFont(new Font("Consolas", Font.BOLD, 20));
        
        pnMain = new Panel();
        dialog.add(pnMain, BorderLayout.CENTER);
        
        pnMain.setBackground(new Color(50, 50, 50));
        pnMain.setLayout(new BorderLayout());
        
        pnCenter = new Panel();
        pnDown   = new Panel();
        pnCenter.setBackground(new Color(50, 50, 50));
        pnDown.setBackground(new Color(50, 50, 50));
        pnCenter.setLayout(new BorderLayout());
        pnDown.setLayout(new BorderLayout());
        pnMain.add(pnCenter, BorderLayout.CENTER);
        pnMain.add(pnDown  , BorderLayout.SOUTH);
        
        Panel pnTitle, pnSub, pnSub1, pnSub2;
        pnTitle = new Panel();
        pnSub   = new Panel();
        pnTitle.setLayout(new FlowLayout(FlowLayout.CENTER));
        pnSub.setLayout(new BorderLayout());
        pnCenter.add(pnTitle, BorderLayout.CENTER);
        pnCenter.add(pnSub, BorderLayout.SOUTH);
        
        pnSub1 = new Panel();
        pnSub2 = new Panel();
        pnSub1.setLayout(new FlowLayout(FlowLayout.CENTER));
        pnSub2.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnSub.add(pnSub1, BorderLayout.CENTER);
        pnSub.add(pnSub2, BorderLayout.SOUTH);
        
        lbTitle.setForeground(new Color(200, 200, 200));
        lbSub.setForeground(new Color(200, 200, 200));
        lbVer.setForeground(new Color(170, 170, 170));
        pnTitle.add(lbTitle);
        pnSub1.add(lbSub);
        pnSub2.add(lbVer);
    }
    
    public void setIconImage(Image img) {
    	dialog.setIconImage(img);
    }
    
    public void open() {
        dialog.setVisible(true);
    }
    
    public void close() {
        dialog.setVisible(false);
    }
    
    public boolean isVisible() {
        if(dialog == null) return false;
        return dialog.isVisible();
    }
    
    public Image getDefaultIcon() {
    	InputStream inp = null;
    	Image img = null;
    	try {
    		inp = this.getClass().getResourceAsStream("icon256.png");
    		img = ImageIO.read(inp);
    		img = img.getScaledInstance(12, 12, Image.SCALE_SMOOTH);
    		return img;
    	} catch(Exception ex) {
    		GlobalLogs.processExceptionOccured(ex, false);
    	} finally {
    		ClassUtil.closeAll(inp);
    	}
        return img;
    }

	@Override
	public void dispose() {
		close();
        dialog.removeAll();
        dialog  = null;
        lbTitle = null;
        lbSub   = null;
	}
}
