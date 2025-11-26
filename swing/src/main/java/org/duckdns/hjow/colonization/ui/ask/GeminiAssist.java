package org.duckdns.hjow.colonization.ui.ask;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.duckdns.hjow.colonization.ui.GUIColonyManager;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.util.GUIUtil;
import org.duckdns.hjow.gemini.GeminiModel;
import org.duckdns.hjow.gemini.GeminiSession;

/** Gemini Assist */
public class GeminiAssist implements Disposeable {
	protected GUIColonyManager superInstance; // 직접 연관될 일이 많으므로 본 객체 그대로 가져오기
    protected JDialog     dialog;
    protected JEditorPane view;
    protected JTextField  field;
    
    protected transient GeminiModel   model;
    protected transient GeminiSession session;
    
    public GeminiAssist(GUIColonyManager superInstance) {
    	dialog = new JDialog(superInstance.getDialog());
    	dialog.setSize(400, 600);
    	GUIUtil.centerWindow(dialog);
    	dialog.setTitle("Assist");
    	dialog.setIconImage(GUIColonyManager.getIcon());
    	dialog.setLayout(new BorderLayout());
    	
    	init();
    }
    
    /** UI 초기화 */
    protected void init() {
    	dialog.removeAll();
    	
    	JPanel pnMain = new JPanel();
    	pnMain.setLayout(new BorderLayout());
    	dialog.add(pnMain, BorderLayout.CENTER);
    	
    	JPanel pnCenter, pnDown;
    	pnCenter = new JPanel();
    	pnDown   = new JPanel();
    	pnCenter.setLayout(new BorderLayout());
    	pnDown.setLayout(new BorderLayout());
    	pnMain.add(pnCenter, BorderLayout.CENTER);
    	pnMain.add(pnDown, BorderLayout.SOUTH);
    	
    	view = new JEditorPane();
    	view.setEditable(false);
    	view.setContentType("text/html");
    	pnCenter.add(new JScrollPane(view), BorderLayout.CENTER);
    	
    	field = new JTextField();
    	pnDown.add(field, BorderLayout.CENTER);
    }
    
    /** 대화상자 열기 */
    public void open() {
    	dialog.setVisible(true);
    }

	@Override
	public void dispose() {
		if(dialog != null) {
			if(dialog.isVisible()) dialog.setVisible(false);
		}
		dialog.removeAll();
		dialog = null;
		
		if(session != null) session.dispose();
		session = null;
		
		superInstance = null;
	}
}
