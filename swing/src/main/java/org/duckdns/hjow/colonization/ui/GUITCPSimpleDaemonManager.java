package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;

import org.duckdns.hjow.colonization.daemon.TCPSimpleDaemon;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.ui.JLogArea;
import org.duckdns.hjow.commons.util.GUIUtil;

/** TCP 데몬 구동 대화상자 */
public class GUITCPSimpleDaemonManager implements Disposeable {
    protected JDialog    dialog;
    protected JSplitPane split;
    protected JSpinner   spPort;
    protected JLogArea   taLog;
    protected JButton    btnToggle;
    protected JComboBox<String> cbxCharset;
    
    protected TCPSimpleDaemon daemon;
    protected boolean         flagToggle = false;

    public GUITCPSimpleDaemonManager(Window win) {
        if(dialog != null || daemon != null) dispose();
        
        dialog = new JDialog(win);
        dialog.setSize(600, 500);
        GUIUtil.centerWindow(dialog);
        dialog.setTitle("Colonization TCP Daemon Manager");
        dialog.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) { dispose(true); }
		});
        
        dialog.setLayout(new BorderLayout());
        
        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BorderLayout());
        dialog.add(pnMain, BorderLayout.CENTER);
        
        split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        pnMain.add(split, BorderLayout.CENTER);
        
        JPanel pnTop = new JPanel();
        pnTop.setLayout(new BorderLayout());
        split.setTopComponent(pnTop);
        
        JPanel pnUp, pnCenter, pnDown;
        pnUp     = new JPanel();
        pnCenter = new JPanel();
        pnDown   = new JPanel();
        pnUp.setLayout(new BorderLayout());
        pnCenter.setLayout(new BorderLayout());
        pnDown.setLayout(new BorderLayout());
        
        pnTop.add(pnUp    , BorderLayout.NORTH);
        pnTop.add(pnCenter, BorderLayout.CENTER);
        pnTop.add(pnDown  , BorderLayout.SOUTH);
        
        JToolBar toolbar = new JToolBar();
        pnUp.add(toolbar, BorderLayout.CENTER);
        
        Vector<String> charsets = new Vector<String>();
        charsets.add("UTF-16");
        charsets.add("UTF-8");
        charsets.add("EUC-KR");
        
        cbxCharset = new JComboBox<String>(charsets);
        toolbar.add(cbxCharset);
        
        SpinnerNumberModel intModel = new SpinnerNumberModel(TCPSimpleDaemon.PORT_DEFAULT, 1000, 65300, 1);
        spPort = new JSpinner(intModel);
        toolbar.add(spPort);
        
        btnToggle = new JButton("시작");
        toolbar.add(btnToggle);
        btnToggle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onToggleRequested();
			}
		});
        
        taLog = new JLogArea();
        split.setBottomComponent(new JScrollPane(taLog));
	}
    
    protected void onToggleRequested() {
    	btnToggle.setEnabled(false);
    	String charset = (String) cbxCharset.getSelectedItem();
    	int    port    = ((Number) spPort.getValue()).intValue();
    	
    	cbxCharset.setEnabled(false);
    	spPort.setEnabled(false);
    	
    	flagToggle = (! flagToggle);
    	
    	if(daemon != null) { daemon.dispose(); daemon = null; try { Thread.sleep(200L); } catch(InterruptedException ex) {} }
    	daemon = null;
    	
    	if(flagToggle) {
    		daemon = new TCPSimpleDaemon(port, charset, taLog);
    	}
    	
    	new Thread(new Runnable() {
			@Override
			public void run() {
				if(daemon != null) daemon.start();
				afterToggled();
			}
		}).start();
    }
    
    protected void afterToggled() {
    	try { Thread.sleep(200L); } catch(InterruptedException ex) {}
    	if(flagToggle) {
    		btnToggle.setText("종료");
    	} else {
    	    cbxCharset.setEnabled(true);
    	    spPort.setEnabled(true);
    	    btnToggle.setText("시작");
    	}
    	btnToggle.setEnabled(true);
    }
    
    public void open() {
    	dialog.setVisible(true);
    }
    
	@Override
	public void dispose() {
		dispose(false);
	}
	
	protected void dispose(boolean noClose) {
		if(! noClose) { if(dialog != null) dialog.setVisible(false); }
		if(daemon != null) { daemon.dispose(); }
		if(taLog  != null) { taLog.clear();    }
		daemon = null;
		dialog = null;
	}
}
