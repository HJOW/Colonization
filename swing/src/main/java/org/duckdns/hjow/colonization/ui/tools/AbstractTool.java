package org.duckdns.hjow.colonization.ui.tools;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.commons.util.GUIUtil;

/** 도구 (게임 자체와 관련은 적고, 보조를 위한 간이 프로그램) 일부 구현체 */
public abstract class AbstractTool implements Tool {
	protected JDialog dialog;
	protected JPanel pnUp, pnCenter, pnDown;
	
	public AbstractTool(Window win) {
		if(win == null) dialog = new JDialog();
		else dialog = new JDialog(dialog);
		dialog.setSize(getDialogProperWidth(), getDialogProperHeight());
		dialog.setTitle(ColonyManager.t(getTitle()));
		GUIUtil.centerWindow(dialog);
		dialog.setLayout(new BorderLayout());
		
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());
		dialog.add(pnMain, BorderLayout.CENTER);
		
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
	
	public JDialog getDialog() {
		return dialog;
	}

	@Override
	public void dispose() {
		if(dialog != null) { dialog.setVisible(false); dialog.removeAll(); }
		dialog = null;
	}

	@Override
	public boolean isAvail() {
		return true;
	}

    @Override
	public void open() {
		dialog.setVisible(true);
	}
    
}
