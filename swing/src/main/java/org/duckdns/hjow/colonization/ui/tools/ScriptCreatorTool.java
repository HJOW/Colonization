package org.duckdns.hjow.colonization.ui.tools;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.ui.GUIPreWorks;
import org.duckdns.hjow.commons.util.ClassUtil;
import org.duckdns.hjow.commons.util.FileUtil;
import org.duckdns.hjow.commons.util.GUIUtil;

/** 스크립트 모듈 (MOD / Facility) 개발 툴 상위 클래스 */
public abstract class ScriptCreatorTool extends AbstractTool {
	protected JEditorPane taEditor;
	protected JButton btnNew, btnSave, btnLoad;
	protected JMenuItem menuFileNew, menuFileSave, menuFileLoad, menuFileSaveAs;
	protected JFileChooser fileChooser;
	protected FileFilter fileFilter;
	
	protected transient File file;
	
	public ScriptCreatorTool(Window win) {
		super(win);
	}
	
	/** 이 Tool 이 만드려는 대상 명칭 */
	protected abstract String target();
	
	@Override
	protected void init() {
		dialog.setSize(750, 650);
		GUIUtil.centerWindow(dialog);
		
		String systemCharset = ClassUtil.getDefaultCharset();
		
		taEditor = new JEditorPane();
		if(GUIPreWorks.isSyntaxPaneEnabled()) taEditor.setContentType("text/javascript; charset=" + systemCharset);
        else taEditor.setContentType("text/plain; charset=" + systemCharset);
		
		pnCenter.add(new JScrollPane(taEditor), BorderLayout.CENTER);
		
		JToolBar toolbar = new JToolBar();
		pnUp.add(toolbar, BorderLayout.NORTH);
		
		btnNew = new JButton(UIManager.getIcon("FileView.fileIcon"));
		btnSave = new JButton(UIManager.getIcon("FileView.floppyDriveIcon"));
		btnLoad = new JButton(UIManager.getIcon("FileView.directoryIcon"));
		
		toolbar.add(btnNew);
		toolbar.add(btnSave);
		toolbar.add(btnLoad);
		
		JMenuBar menuBar = new JMenuBar();
		dialog.setJMenuBar(menuBar);
		
		JMenu menuFile = new JMenu(ColonyManager.t("파일"));
		menuBar.add(menuFile);
		
		menuFileNew = new JMenuItem(ColonyManager.t("새 " + target()));
		menuFileSave = new JMenuItem(ColonyManager.t("저장"));
		menuFileSaveAs = new JMenuItem(ColonyManager.t("다른 이름으로 저장"));
		menuFileLoad = new JMenuItem(ColonyManager.t("불러오기"));
		
		menuFile.add(menuFileNew);
		menuFile.add(menuFileSave);
		menuFile.add(menuFileSaveAs);
		menuFile.add(menuFileLoad);
		
		menuFile.addSeparator();
		
		JMenuItem menuClose = new JMenuItem(ColonyManager.t("닫기"));
		menuFile.add(menuClose);
		
		ActionListener actionNew = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onNewRequested();
			}
		};
		btnNew.addActionListener(actionNew);
		menuFileNew.addActionListener(actionNew);
		
		ActionListener actionSave = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onSaveRequested();
			}
		};
		btnSave.addActionListener(actionSave);
		menuFileSave.addActionListener(actionSave);
		
		ActionListener actionSaveAs = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onSaveAsRequested();
			}
		};
		menuFileSaveAs.addActionListener(actionSaveAs);
		
		ActionListener actionLoad = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onLoadRequested();
			}
		};
		btnLoad.addActionListener(actionLoad);
		menuFileLoad.addActionListener(actionLoad);
		
		menuClose.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				onNewRequested();
				dialog.setVisible(false);
			}
		});
		
		fileFilter = createFileFilter();
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.addChoosableFileFilter(fileFilter);
		fileChooser.setFileFilter(fileFilter);
		
		onNewRequested();
	}
	
	/** 파일 필터 지정 (확장자와 파일 설명) */
	protected FileFilter createFileFilter() {
		return new FileFilter() {	
			@Override
			public String getDescription() {
				return ColonyManager.t(target() + "스크립트 파일") + " (.js)";
			}
			
			@Override
			public boolean accept(File f) {
				if(f.isDirectory()) return false;
				return f.getName().trim().toLowerCase().endsWith(".js");
			}
		};
	}
	
	protected abstract File getDefaultDirectory() throws Throwable;
	
	/** 새로 만들기 동작 시 호출됨 */
    protected void onNewRequested() {
    	file = null;
    	taEditor.setText(getDefaultContent());
    }
    /** 저장 요청 시 호출됨 */
	protected void onSaveRequested() {
		if(file == null) { onSaveAsRequested(); return; }
		try {
		    FileUtil.writeString(file, "UTF-8", taEditor.getText());
		} catch(Exception ex) {
			JOptionPane.showMessageDialog(dialog, ColonyManager.t("오류") + " : " + ex.getMessage());
		}
	}
    /** 다른 이름으로 저장 시 호출됨 */
    protected void onSaveAsRequested() {
        try { fileChooser.setCurrentDirectory(getDefaultDirectory()); } catch(Throwable ignores) {}
        
		int sel = fileChooser.showSaveDialog(dialog);
		if(sel != JFileChooser.APPROVE_OPTION) return;
		
		try {
			File f = fileChooser.getSelectedFile();
			FileUtil.writeString(f, "UTF-8", taEditor.getText());
			file = f;
		} catch(Exception ex) {
			JOptionPane.showMessageDialog(dialog, ColonyManager.t("오류") + " : " + ex.getMessage());
		}
	}
    /** 불러오기 시 호출됨 */
    protected void onLoadRequested() {
    	try { fileChooser.setCurrentDirectory(getDefaultDirectory()); } catch(Throwable ignores) {}
    	
		int sel = fileChooser.showOpenDialog(dialog);
		if(sel != JFileChooser.APPROVE_OPTION) return;
		
		try {
			File f = fileChooser.getSelectedFile();
			taEditor.setText(FileUtil.readString(f, "UTF-8"));
			file = f;
		} catch(Exception ex) {
			JOptionPane.showMessageDialog(dialog, ColonyManager.t("오류") + " : " + ex.getMessage());
		}
	}
    
    protected abstract String getDefaultContent();
}
