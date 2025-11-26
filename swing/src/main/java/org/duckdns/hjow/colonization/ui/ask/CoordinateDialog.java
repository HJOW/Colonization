package org.duckdns.hjow.colonization.ui.ask;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

import org.duckdns.hjow.colonization.ui.GUIColonyManager;
import org.duckdns.hjow.commons.ui.graphics.Coordinate3D;
import org.duckdns.hjow.commons.util.GUIUtil;

/** 3차원 좌표 입력 받는 대화상자, static 메소드로 호출하며, Modal 타입 */ // TODO : 공통 lib 로 이관
public class CoordinateDialog extends JDialog {
	private static final long serialVersionUID = -2233011964684488187L;
	protected transient JSpinner spX, spY, spZ;
	protected transient JButton btnOk, btnCancel;
	protected transient JTextArea ta;
	protected transient JPanel pnCtrl;
	protected transient ActionListener actionOk, actionCancel;
	protected transient WindowAdapter actionClosing;
	
	protected transient Coordinate3D defaults = null;
	protected transient Coordinate3D results  = null;

	protected CoordinateDialog(JFrame win, String title, String msg, Coordinate3D defaultValue) {
		super(win, true);
		init(win, title, msg, defaultValue);
    }
	
	protected CoordinateDialog(JDialog win, String title, String msg, Coordinate3D defaultValue) {
		super(win, true);
		init(win, title, msg, defaultValue);
    }
	
	protected void init(Window win, String title, String msg, Coordinate3D defaultValue) {
		setSize(600, 250);
		GUIUtil.centerWindow(this);
		setTitle(title);
        if(getDialogIconImage() != null) setIconImage(getDialogIconImage());
    	setLayout(new BorderLayout());
    	
    	actionClosing = new WindowAdapter() {
    		@Override
    		public void windowClosing(WindowEvent e) {
    			onClose();
    		}
		};
		
		if(defaultValue == null) defaultValue = new Coordinate3D(0L, 0L, 0L);
    	
    	JPanel pnMain = new JPanel();
    	pnMain.setLayout(new BorderLayout());
    	add(pnMain, BorderLayout.CENTER);
    	
    	JPanel pnCenter, pnDown;
    	pnCenter = new JPanel();
    	pnDown   = new JPanel();
    	pnCenter.setLayout(new BorderLayout());
    	pnDown.setLayout(new BorderLayout());
    	pnMain.add(pnCenter, BorderLayout.CENTER);
    	pnMain.add(pnDown  , BorderLayout.SOUTH);
    	
    	JPanel pnCoordinate;
    	pnCtrl       = new JPanel();
    	pnCoordinate = new JPanel();
    	pnCtrl.setLayout(new FlowLayout(FlowLayout.RIGHT));
    	pnCoordinate.setLayout(new FlowLayout());
    	pnDown.add(pnCoordinate, BorderLayout.CENTER);
    	pnDown.add(pnCtrl, BorderLayout.SOUTH);
    	
    	ta = new JTextArea();
    	ta.setEditable(false);
    	ta.setText(msg);
    	pnCenter.add(new JScrollPane(ta), BorderLayout.CENTER);
    	
    	SpinnerNumberModel spNum = new SpinnerNumberModel(new Long(defaultValue.getX()), new Long(Long.MIN_VALUE), new Long(Long.MAX_VALUE), new Long(1L));
    	spX = new JSpinner(spNum);
    	spX.setPreferredSize(new Dimension(130, 25));
    	pnCoordinate.add(spX);
    	
    	spNum = new SpinnerNumberModel(new Long(defaultValue.getY()), new Long(Long.MIN_VALUE), new Long(Long.MAX_VALUE), new Long(1L));
    	spY = new JSpinner(spNum);
    	spY.setPreferredSize(new Dimension(130, 25));
    	pnCoordinate.add(spY);
    	
    	spNum = new SpinnerNumberModel(new Long(defaultValue.getZ()), new Long(Long.MIN_VALUE), new Long(Long.MAX_VALUE), new Long(1L));
    	spZ = new JSpinner(spNum);
    	spZ.setPreferredSize(new Dimension(130, 25));
    	pnCoordinate.add(spZ);
    	
    	actionOk = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		};
    	
		actionCancel = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClose();
			}
		};
		
		btnOk = new JButton(getOkMsg());
		btnCancel = new JButton(getCancelMsg());
		
		btnOk.addActionListener(actionOk);
		btnCancel.addActionListener(actionCancel);
		
		controlPanelLayout();
		
		results = null;
	}
	
	protected void controlPanelLayout() {
		pnCtrl.add(btnOk);
		pnCtrl.add(btnCancel);
	}
	
	protected String getOkMsg() {
		return "확인";
	}
	
	protected String getCancelMsg() {
		return "확인";
	}
	
	protected Image getDialogIconImage() {
		return GUIColonyManager.getIcon();
	}
	
	protected void onClose() {
		if(actionOk != null) {
			btnOk.removeActionListener(actionOk);
			actionOk = null;
		}
		
		if(actionCancel != null) {
			btnCancel.removeActionListener(actionCancel);
			actionCancel = null;
		}
		
		if(actionClosing != null) {
			removeWindowListener(actionClosing);
			actionClosing = null;
		}
		
		setVisible(false);
	}
	
	protected void onOk() {
		results = new Coordinate3D(((Number) spX.getValue()).longValue(), ((Number) spY.getValue()).longValue(), ((Number) spZ.getValue()).longValue());
		onClose();
	}
	
	/** 결과 리턴 */
	protected Coordinate3D getResult() {
		return results;
	}
	
	/** 좌표 입력받기 */
	public static Coordinate3D ask(Window win, String title, String msg) {
		return ask(win, title, msg, new Coordinate3D(0L, 0L, 0L));
	}
	
	/** 좌표 입력받기 */
	public static Coordinate3D ask(Window win, String title, String msg, Coordinate3D defaultValue) {
		CoordinateDialog diag = null;
		if(     win instanceof JFrame ) diag = new CoordinateDialog((JFrame)  win, title, msg, defaultValue);
		else if(win instanceof JDialog) diag = new CoordinateDialog((JDialog) win, title, msg, defaultValue);
		else return null;
		
		diag.setVisible(true);
		return diag.getResult();
	}
}
