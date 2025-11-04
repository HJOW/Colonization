package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.TextField;
import java.awt.Window;

/** 로딩용 임시 대화상자 팝업, AWT로 구현 (Swing과 동시사용 가능) */
public class LoadingAWTDialog extends AboutDialog implements Runnable {
    protected AWTProgressBar pnProg;
    protected volatile boolean threadSwitch = false;
    
    public LoadingAWTDialog() {
        this("", "");
    }
    
    public LoadingAWTDialog(String title, String sub) {
    	this(title, sub, "");
    }
    
    public LoadingAWTDialog(String title, String sub, String ver) {
        init(null, title, sub, ver);
    }
    
    @Override
    protected void init(Window win, String title, String sub, String ver) {
    	super.init(win, title, sub, ver);
    	pnProg = new AWTProgressBar();
        pnDown.add(pnProg, BorderLayout.CENTER);
    }
    
    @Override
    public void open() {
        threadSwitch = true;
        new Thread(this).start();
        dialog.setVisible(true);
    }
    
    @Override
	public void dispose() {
    	threadSwitch = false;
		close();
        dialog.removeAll();
        dialog  = null;
        lbTitle = null;
        lbSub   = null;
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