package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileFilter;

import org.duckdns.hjow.colonization.ColonyClassLoader;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.util.DataUtil;
import org.duckdns.hjow.commons.util.FileUtil;
import org.duckdns.hjow.commons.util.GUIUtil;

/** 설정 변경 대화상자 */
public class ConfigManager implements Disposeable {
	protected GUIColonyManager superInstance;
    protected JDialog dialog;
    protected JButton btnSave, btnCancel, btnSelStringTable;
    protected JComboBox<String> cbxLookAndFeel;
    protected JTextField tfStringTable, tfModClasses;
    protected JTextArea taPacks;
    protected JCheckBox chkOldVer;
    
    public ConfigManager(GUIColonyManager superInstance) {
    	this.superInstance = superInstance;
    	dialog = new JDialog(superInstance.getDialog(), true);
    	dialog.setSize(400, 350);
    	dialog.setTitle(ColonyManager.t("설정"));
    	GUIUtil.centerWindow(dialog);
    	dialog.setIconImage(GUIColonyManager.getIcon());
    	dialog.setLayout(new BorderLayout());
    	
    	JPanel pnMain = new JPanel();
    	pnMain.setLayout(new BorderLayout());
    	dialog.add(pnMain, BorderLayout.CENTER);
    	
    	JPanel pnUp, pnCenter, pnDown;
    	pnUp     = new JPanel();
    	pnCenter = new JPanel();
    	pnDown   = new JPanel();
    	pnUp.setLayout(new BorderLayout());
    	pnCenter.setLayout(new BorderLayout());
    	pnDown.setLayout(new BorderLayout());
    	
    	pnMain.add(pnUp    , BorderLayout.NORTH);
    	pnMain.add(pnCenter, BorderLayout.CENTER);
    	pnMain.add(pnDown  , BorderLayout.SOUTH);
    	
    	JPanel pnConfigs = new JPanel();
    	pnConfigs.setLayout(new GridBagLayout());
    	pnCenter.add(new JScrollPane(pnConfigs, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
    	
    	GridBagConstraints gridBagConst;
        int rowNo = 0;
        JPanel pn;
        JLabel lb;
        
        // 1. 룩앤필
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 1;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 0.1;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
        gridBagConst.fill = GridBagConstraints.HORIZONTAL;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        pn = new JPanel();
        pn.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        lb = new JLabel(ColonyManager.t("룩앤필(테마)"));
        pn.add(lb);
        
        pnConfigs.add(pn, gridBagConst);
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 1;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 9;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 0.9;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
        gridBagConst.fill = GridBagConstraints.HORIZONTAL;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        pn = new JPanel();
        pn.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        Vector<String> lookAndFeels = new Vector<String>();
        LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
        for(LookAndFeelInfo i : infos) {
        	lookAndFeels.add(i.getName());
        }
        
        cbxLookAndFeel = new JComboBox<String>(lookAndFeels);
        pn.add(cbxLookAndFeel);
        
        pnConfigs.add(pn, gridBagConst);
        
        rowNo++;
        
        // 2. 언어 파일
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 1;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 0.1;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
        gridBagConst.fill = GridBagConstraints.HORIZONTAL;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        pn = new JPanel();
        pn.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        lb = new JLabel(ColonyManager.t("언어 파일"));
        pn.add(lb);
        
        pnConfigs.add(pn, gridBagConst);
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 1;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 9;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 0.9;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
        gridBagConst.fill = GridBagConstraints.HORIZONTAL;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        pn = new JPanel();
        pn.setLayout(new BorderLayout());
        
        tfStringTable = new JTextField();
        pn.add(tfStringTable, BorderLayout.CENTER);
        
        btnSelStringTable = new JButton("...");
        pn.add(btnSelStringTable, BorderLayout.EAST);
        
        final JFileChooser chooserSelStringTable = new JFileChooser();
        chooserSelStringTable.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooserSelStringTable.setMultiSelectionEnabled(false);
        chooserSelStringTable.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return ColonyManager.t("언어 설정 파일") + " (.xml)";
			}
			@Override
			public boolean accept(File f) {
				String n = f.getName().toLowerCase();
				return n.endsWith(".xml");
			}
		});
        
        btnSelStringTable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int sel = chooserSelStringTable.showOpenDialog(dialog);
				if(sel != JFileChooser.APPROVE_OPTION) return;
				tfStringTable.setText(chooserSelStringTable.getSelectedFile().getAbsolutePath());
			}
		});
        
        pnConfigs.add(pn, gridBagConst);
        
        rowNo++;
        
        // 3. Mods
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 1;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 0.1;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
        gridBagConst.fill = GridBagConstraints.HORIZONTAL;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        pn = new JPanel();
        pn.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        lb = new JLabel(ColonyManager.t("MOD 설정"));
        pn.add(lb);
        
        pnConfigs.add(pn, gridBagConst);
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 1;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 9;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 0.9;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
        gridBagConst.fill = GridBagConstraints.HORIZONTAL;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        pn = new JPanel();
        pn.setLayout(new BorderLayout());
        
        tfModClasses = new JTextField();
        tfModClasses.setEditable(false);
        pn.add(tfModClasses, BorderLayout.CENTER);
        
        pnConfigs.add(pn, gridBagConst);
        
        rowNo++;
        
        // 4. Y/N 설정들
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 1.0;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
        gridBagConst.fill = GridBagConstraints.HORIZONTAL;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        pn = new JPanel();
        pn.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        chkOldVer = new JCheckBox(ColonyManager.t("구버전 세이브 사용"));
        pn.add(chkOldVer);
        
        pnConfigs.add(pn, gridBagConst);
        
        rowNo++;
        
        // 5. Packs
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 10;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 1.0;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
        gridBagConst.fill = GridBagConstraints.HORIZONTAL;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        pn = new JPanel();
        pn.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        lb = new JLabel(ColonyManager.t("Packs"));
        pn.add(lb);
        
        pnConfigs.add(pn, gridBagConst);
        
        rowNo++;
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 10;
        gridBagConst.gridheight = 2;
        gridBagConst.weightx = 1.0;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
        gridBagConst.weighty = 1.0;
        gridBagConst.fill = GridBagConstraints.BOTH;
        
        pn = new JPanel();
        pn.setLayout(new BorderLayout());
        
        taPacks = new JTextArea();
        pn.add(new JScrollPane(taPacks), BorderLayout.CENTER);
        
        pnConfigs.add(pn, gridBagConst);
        
        rowNo++;
        
        // 버튼 설정
        
        JPanel pnCtrl = new JPanel();
        pnCtrl.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnDown.add(pnCtrl, BorderLayout.CENTER);
        
        btnSave = new JButton(ColonyManager.t("저장 (일부 항목은 재시작 시 적용)"));
        btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onSaveRequested();
				close();
			}
		});
        
        btnCancel = new JButton(ColonyManager.t("닫기"));
        btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
        
        pnCtrl.add(btnSave);
        pnCtrl.add(btnCancel);
    }
    
    /** 저장 버튼 클릭 시 호출 */
    protected void onSaveRequested() {
    	String val = null;
    	File f = null;
    	
    	// 룩앤필
    	String lookAndFeel = cbxLookAndFeel.getSelectedItem() == null ? "Nimbus" : cbxLookAndFeel.getSelectedItem().toString();
    	superInstance.getConfig().set("LookAndFeel", lookAndFeel);
    	
    	// 언어 설정 파일
    	val = tfStringTable.getText();
    	val = val.replace("[CONFIGPATH]", superInstance.getColonyConfigRootDirectory().getAbsolutePath());
    	
    	f = new File(val);
    	if(! f.exists()) val = "[CONFIGPATH]" + File.separator + "stringTable.xml";
    	
    	val = val.replace(superInstance.getColonyConfigRootDirectory().getAbsolutePath(), "[CONFIGPATH]");
    	superInstance.getConfig().set("StringTableFile", val);
    	
    	// Mods
    	val = tfModClasses.getText();
    	superInstance.getConfig().set("Mods", val);
    	
    	// Y/Ns
    	superInstance.getConfig().set("LoadOldVersion", chkOldVer.isSelected() ? "Y" : "N");
    	
    	// Packs
    	val = taPacks.getText();
    	try {
    		File libDir = ColonyClassLoader.getHomeLibDir();
    		if(! libDir.exists()) libDir.mkdirs();
    		
    		File packClassFile = ColonyClassLoader.getLibPackClassFile();
    		FileUtil.writeString(packClassFile, "UTF-8", val);
    	} catch(Exception ex) {
    		GlobalLogs.processExceptionOccured(ex, true);
    	}
    	
    	// 룩앤필 적용 시도
        if(DataUtil.isEmpty(lookAndFeel)) { lookAndFeel = "Nimbus"; }
        GUIUtil.setLookAndFeel(lookAndFeel.trim());
        superInstance.refreshLookAndFeel();
    	
    	// 설정 파일 저장
    	superInstance.saveLocalConfigs();
    }
    
    /** 설정 창 열기 */
    public void open() {
    	dialog.setVisible(false);
    	Exception caused = null;
    	
    	cbxLookAndFeel.setSelectedItem(superInstance.getConfig().getString("LookAndFeel"));
    	tfStringTable.setText(superInstance.getConfig().getString("StringTableFile"));
    	tfModClasses.setText(superInstance.getConfig().getString("Mods"));
    	
    	String packClasses = "";
    	File packClassFile = ColonyClassLoader.getLibPackClassFile();
    	if(packClassFile.exists()) {
    		try {
    		    packClasses = FileUtil.readString(packClassFile, "UTF-8");
    		} catch(Exception ex) {
    			caused = ex;
    		}
    	}
    	
    	chkOldVer.setSelected(superInstance.getConfig().getBool("LoadOldVersion"));
    	
    	taPacks.setText(packClasses);
    	dialog.setVisible(true);
    	
    	if(caused != null) JOptionPane.showMessageDialog(dialog, ColonyManager.t("Error") + " : " + caused.getMessage());
    }
    
    /** 설정 창 닫기 */
    public void close() {
    	dialog.setVisible(false);
    }
    
    /** 디버그 모드 관련 */
    protected void setDebugMode(boolean enabled) {
    	tfModClasses.setEditable(enabled);
    }

	@Override
	public void dispose() {
		close();
		superInstance = null;
		dialog = null;
	}
}
