package org.duckdns.hjow.colonization.ui.tools;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.ui.GUIColonyManager;
import org.duckdns.hjow.commons.util.GUIUtil;

/** 도구 (게임 자체와 관련은 적고, 보조를 위한 간이 프로그램) 일부 구현체 */
public abstract class AbstractTool implements Tool {
    protected JDialog dialog;
    protected JFrame  frame;
    protected JPanel  pnMain, pnUp, pnCenter, pnDown;
    
    public AbstractTool() {
    	this(null);
    }
    public AbstractTool(Window win) {
        if(win == null) frame = new JFrame();
        else dialog = new JDialog(dialog);
        
        if(frame != null) {
        	frame.setSize(getDialogProperWidth(), getDialogProperHeight());
        	frame.setTitle(ColonyManager.t(getTitle()));
            GUIUtil.centerWindow(frame);
            frame.setIconImage(GUIColonyManager.getIcon());
            frame.setLayout(new BorderLayout());
        } else {
        	dialog.setSize(getDialogProperWidth(), getDialogProperHeight());
            dialog.setTitle(ColonyManager.t(getTitle()));
            GUIUtil.centerWindow(dialog);
            dialog.setIconImage(GUIColonyManager.getIcon());
            dialog.setLayout(new BorderLayout());
        }
        
        pnMain = new JPanel();
        pnMain.setLayout(new BorderLayout());
        
        if(frame != null) {
        	frame.add(pnMain, BorderLayout.CENTER);
        } else {
        	dialog.add(pnMain, BorderLayout.CENTER);
        }
        
        
        pnUp     = new JPanel();
        pnCenter = new JPanel();
        pnDown   = new JPanel();
        pnUp.setLayout(new BorderLayout());
        pnCenter.setLayout(new BorderLayout());
        pnDown.setLayout(new BorderLayout());
        pnMain.add(pnUp, BorderLayout.NORTH);
        pnMain.add(pnCenter, BorderLayout.CENTER);
        pnMain.add(pnDown, BorderLayout.SOUTH);
        
        init();
    }
    
    protected int getDialogProperWidth( ) { return 600; }
    protected int getDialogProperHeight() { return 400; }
    
    /** UI 초기화 */
    protected abstract void init();
    
    /** 대화 상자 반환 */
    public Window getDialog() {
    	if(frame != null) return frame;
        return dialog;
    }

    @Override
    public void dispose() {
        if(dialog != null) { dialog.setVisible(false); dialog.removeAll(); }
        if(frame  != null) { frame.setVisible(false);  frame.removeAll();  }
        dialog = null;
        frame  = null;
    }

    @Override
    public boolean isAvail() {
        return true;
    }

    @Override
    public void open() {
    	if(frame != null) frame.setVisible(true);
    	else dialog.setVisible(true);
    }
    
}
