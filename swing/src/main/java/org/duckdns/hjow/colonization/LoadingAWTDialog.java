package org.duckdns.hjow.colonization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.duckdns.hjow.commons.util.GUIUtil;

/** 로딩용 임시 대화상자 팝업, AWT로 구현 (Swing과 동시사용 가능) */
public class LoadingAWTDialog implements Runnable {
    protected Dialog dialog;
    protected Label lbTitle, lbSub, lbVer;
    protected AWTProgressBar pnProg;
    protected volatile boolean threadSwitch = false;
    
    public LoadingAWTDialog() {
        this("", "");
    }
    
    public LoadingAWTDialog(String title, String sub) {
    	this(title, sub, "");
    }
    
    public LoadingAWTDialog(String title, String sub, String ver) {
        dialog = new Dialog(null, false);
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
        
        lbTitle = new Label(title);
        lbSub   = new Label(sub);
        lbVer   = new Label(ver);
        
        lbTitle.setFont(new Font("Consolas", Font.BOLD, 20));
        
        pnProg = new AWTProgressBar();
        
        init();
    }
    
    protected void init() {
        Panel pnMain = new Panel();
        dialog.add(pnMain, BorderLayout.CENTER);
        
        pnMain.setBackground(new Color(50, 50, 50));
        pnMain.setLayout(new BorderLayout());
        
        Panel pnCenter, pnDown;
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
        pnSub2.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnSub.add(pnSub1, BorderLayout.CENTER);
        pnSub.add(pnSub2, BorderLayout.SOUTH);
        
        lbTitle.setForeground(new Color(200, 200, 200));
        lbSub.setForeground(new Color(200, 200, 200));
        lbVer.setForeground(new Color(170, 170, 170));
        pnTitle.add(lbTitle);
        pnSub1.add(lbSub);
        pnSub2.add(lbVer);
        
        pnDown.add(pnProg, BorderLayout.CENTER);
    }
    
    public void open() {
        threadSwitch = true;
        new Thread(this).start();
        dialog.setVisible(true);
    }
    
    public void close() {
        threadSwitch = false;
        dialog.setVisible(false);
        dialog.removeAll();
        dialog  = null;
        lbTitle = null;
        lbSub   = null;
    }
    
    public boolean isVisible() {
        if(dialog == null) return false;
        return dialog.isVisible();
    }

    @Override
    public void run() {
        while(threadSwitch) {
            pnProg.increase();
            try {Thread.sleep(100L);} catch(InterruptedException ex) { ex.printStackTrace(); break; }
        }
    }
}

class AWTProgressBar extends TextField {
    private static final long serialVersionUID = 3925874180808886964L;
    public int value = 0;
    public AWTProgressBar() { super(); setEditable(false);; }
    
    public int getMax() { return 50; }
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
        if(this.value > getMax()) this.value = 0;
        refresh();
    }
    
    public void increase() {
        this.value++;
        if(this.value > getMax()) this.value = 0;
        refresh();
    }
    
    public void refresh() {
        int w = getWidth();
        int chars = w / 5;
        int loc   = (int) ((getValue() * 1.0 / getMax()) * chars);
        
        StringBuilder res = new StringBuilder("");
        for(int idx=0; idx<chars; idx++) {
            if(idx < loc-1 || idx > loc+1) res = res.append("□");
            else res = res.append("■");
        }
        setText(res.toString());
    }
}