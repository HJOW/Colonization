package org.duckdns.hjow.colonization.ui.tools;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.data.CompressedDocument;
import org.duckdns.hjow.commons.util.GUIUtil;

/** cdoc 파일 관리 툴 */
public class CDOCViewer implements Disposeable {
    protected JDialog dialog;
    protected JTextArea ta;
    protected JTextPane taPreview;
    protected JComboBox<String> cbx;
    protected JFileChooser chooserCDoc;
    protected JTabbedPane tab;
    
    protected transient File file;
    
    public CDOCViewer(Window superInst) {
    	dialog = new JDialog(superInst.getOwner());
    	dialog.setTitle(t(CompressedDocument.FILE_DESC) + " Tool");
    	dialog.setSize(600, 400);
    	GUIUtil.centerWindow(dialog);
    	dialog.setLayout(new BorderLayout());
    	
    	JPanel pnMain, pnUp, pnCenter, pnDown;
    	JToolBar toolbar;
    	
    	pnMain = new JPanel();
    	pnMain.setLayout(new BorderLayout());
    	dialog.add(pnMain, BorderLayout.CENTER);
    	
    	pnUp     = new JPanel();
    	pnCenter = new JPanel();
    	pnDown   = new JPanel();
    	pnUp.setLayout(new BorderLayout());
    	pnCenter.setLayout(new BorderLayout());
    	pnDown.setLayout(new BorderLayout());
    	
    	pnMain.add(pnUp    , BorderLayout.NORTH);
    	pnMain.add(pnCenter, BorderLayout.CENTER);
    	pnMain.add(pnDown  , BorderLayout.SOUTH);
    	
    	toolbar = new JToolBar();
    	pnUp.add(toolbar, BorderLayout.NORTH);
    	
    	JButton btnSave, btnLoad, btnSaveAs;
    	
    	btnSave   = new JButton(UIManager.getIcon("FileView.floppyDriveIcon"));
    	btnLoad   = new JButton(UIManager.getIcon("FileView.directoryIcon"));
    	btnSaveAs = new JButton(t("다른 이름으로 저장"));
    	toolbar.add(btnSave);
    	toolbar.add(btnLoad);
    	toolbar.add(btnSaveAs);
    	
    	chooserCDoc = new JFileChooser();
    	chooserCDoc.setMultiSelectionEnabled(false);
    	chooserCDoc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	chooserCDoc.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return t(CompressedDocument.FILE_DESC) + " (*" + "." + CompressedDocument.FILE_EXT + ")";
			}
			@Override
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith("." + CompressedDocument.FILE_EXT);
			}
		});
    	
    	btnSave.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				onSaveRequested();
			}
		});
    	
    	btnLoad.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				onLoadRequested();
			}
		});
    	
    	btnSaveAs.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				onSaveAsRequested();
			}
		});
    	
    	Vector<String> contentTypes = new Vector<String>();
    	contentTypes.add("text/plain");
    	contentTypes.add("text/html");
    	cbx = new JComboBox<String>(contentTypes);
    	cbx.setSelectedIndex(0);
    	
    	cbx.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				taPreview.setContentType(cbx.getSelectedItem().toString());
			}
		});
    	toolbar.add(cbx);
    	
    	tab = new JTabbedPane();
    	pnCenter.add(tab, BorderLayout.CENTER);
    	
    	JPanel pnTabEdit = new JPanel();
    	pnTabEdit.setLayout(new BorderLayout());
    	tab.add(t("편집"), pnTabEdit);
    	
    	JPanel pnTabPrev = new JPanel();
    	pnTabPrev.setLayout(new BorderLayout());
    	tab.add(t("미리보기"), pnTabPrev);
    	
    	ta = new JTextArea();
    	pnTabEdit.add(new JScrollPane(ta), BorderLayout.CENTER);
    	
    	taPreview = new JTextPane();
    	taPreview.setEditable(false);
    	taPreview.setContentType(contentTypes.get(0));
    	pnTabPrev.add(new JScrollPane(taPreview), BorderLayout.CENTER);
    	
    	tab.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				taPreview.setText(ta.getText());
				taPreview.setContentType(cbx.getSelectedItem().toString());
			}
		});
    }
    
    public JDialog getDialog() {
    	return dialog;
    }
    
    /** 다국어 지원 호출 */
    protected String t(String originals) {
    	return ColonyManager.t(originals);
    }
    
    /** 창 띄우기 */
    public void open() {
    	file = null;
    	ta.setText("");
    	taPreview.setText("");
    	cbx.setSelectedIndex(0);
    	tab.setSelectedIndex(0);
    	dialog.setVisible(true);
    }
    
    /** 창 닫기 */
    public void close() {
    	file = null;
    	dialog.setVisible(false);
    	ta.setText("");
    	taPreview.setText("");
    	cbx.setSelectedIndex(0);
    }
    
    protected void onSaveRequested() {
    	if(file == null) {
    		int sel = chooserCDoc.showSaveDialog(getDialog());
    		if(sel != JFileChooser.APPROVE_OPTION) return;
    		
    		file = chooserCDoc.getSelectedFile();
    	}
    	
    	try {
    		String nameLower = file.getName().toLowerCase();
	        if(! nameLower.endsWith("." + CompressedDocument.FILE_EXT)) {
	        	file = new File(file.getAbsolutePath() + "." + CompressedDocument.FILE_EXT);
	        }
    		
    	    CompressedDocument doc = new CompressedDocument();
    	    doc.setContentType(cbx.getSelectedItem().toString());
    	    doc.setContent(ta.getText());
    	    doc.write(file);
    	} catch(Exception ex) {
			JOptionPane.showMessageDialog(getDialog(), ColonyManager.t("오류") + " : " + ex.getMessage());
		}
    }
    
    protected void onSaveAsRequested() {
    	int sel = chooserCDoc.showSaveDialog(getDialog());
		if(sel != JFileChooser.APPROVE_OPTION) return;
		
		file = chooserCDoc.getSelectedFile();
    	
    	try {
    		String nameLower = file.getName().toLowerCase();
	        if(! nameLower.endsWith("." + CompressedDocument.FILE_EXT)) {
	        	file = new File(file.getAbsolutePath() + "." + CompressedDocument.FILE_EXT);
	        }
    		
    	    CompressedDocument doc = new CompressedDocument();
    	    doc.setContentType(cbx.getSelectedItem().toString());
    	    doc.setContent(ta.getText());
    	    doc.write(file);
    	} catch(Exception ex) {
			JOptionPane.showMessageDialog(getDialog(), ColonyManager.t("오류") + " : " + ex.getMessage());
		}
    }
    
    protected void onLoadRequested() {
    	int sel = chooserCDoc.showOpenDialog(getDialog());
		if(sel != JFileChooser.APPROVE_OPTION) return;
		
		file = chooserCDoc.getSelectedFile();
		
		try {
		    CompressedDocument doc = new CompressedDocument(file);
		    if(doc.getContentType() != null) cbx.setSelectedItem(doc.getContentType());
		    else cbx.setSelectedIndex(0);
		    
		    ta.setText(doc.getContent());
		    taPreview.setText(doc.getContent());
		    taPreview.setContentType(cbx.getSelectedItem().toString());
		} catch(Exception ex) {
			JOptionPane.showMessageDialog(getDialog(), ColonyManager.t("오류") + " : " + ex.getMessage());
			taPreview.setText("");
		}
    }

	@Override
	public void dispose() {
		close();
	}
}
