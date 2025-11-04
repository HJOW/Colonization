package org.duckdns.hjow.colonization.ui.tools;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.ui.GUIPreWorks;
import org.duckdns.hjow.commons.util.FileUtil;

/** log4j 설정 관리자 */
public class Log4JManager extends AbstractTool {
    protected JTabbedPane tabMain;
    protected JPanel pnTabNow, pnTabSample;
    protected JEditorPane taNow, taSample;
    protected JSplitPane splitSample;
    
	public Log4JManager(Window win) {
		super(win);
	}
	
	@Override
	protected void init() {
		tabMain = new JTabbedPane();
		pnCenter.add(tabMain, BorderLayout.CENTER);
		
		pnTabNow    = new JPanel();
		pnTabSample = new JPanel();
		pnTabNow.setLayout(new BorderLayout());
		pnTabSample.setLayout(new BorderLayout());
		
		tabMain.add(ColonyManager.t("현재 설정 XML 수정"), pnTabNow);
		tabMain.add(ColonyManager.t("XML 생성"), pnTabSample);
		
		JToolBar toolbar = new JToolBar();
		pnTabNow.add(toolbar, BorderLayout.NORTH);
		
		taNow = new JEditorPane();
		pnTabNow.add(new JScrollPane(taNow), BorderLayout.CENTER);
		
		if(GUIPreWorks.isSyntaxPaneEnabled()) taNow.setContentType("text/xml");
		else taNow.setContentType("text/plain");
		
		JButton btnSave = new JButton(UIManager.getIcon("FileView.floppyDriveIcon"));
		toolbar.add(btnSave);
		
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onSaveCalled();
			}
		});
		
		splitSample = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		pnTabSample.add(splitSample, BorderLayout.CENTER);
		
		taSample = new JEditorPane();
		taSample.setEditable(false);
		taSample.setText(GlobalLogs.getSampleLog4jXml());
		splitSample.setRightComponent(new JScrollPane(taSample));
		
		JPanel pnSampleCreateRoot = new JPanel();
		splitSample.setLeftComponent(pnSampleCreateRoot);
		
	}
	
	protected void onSaveCalled() {
		try {
			String content = taNow.getText();
			File file = getLog4jXmlFile();
			
			FileUtil.writeString(file, "UTF-8", content);
			
			GlobalLogs.tryingToInitLog4j();
		} catch(Exception ex) {
		    GlobalLogs.processExceptionOccured(ex, true);
		    JOptionPane.showMessageDialog(getDialog(), ColonyManager.t("오류") + " : " + ex.getMessage());
		}
	}

	@Override
	public String getName() {
		return "LOGGING";
	}

	@Override
	public String getTitle() {
		return "로그 관리자";
	}
	
	@Override
	public void open() {
		try {
			String content = null;
			File file = getLog4jXmlFile();
			if(! file.exists()) content = GlobalLogs.getSampleLog4jXml();
			else content = FileUtil.readString(file, "UTF-8");
			taNow.setText(content);
		} catch(Exception ex) {
		    GlobalLogs.processExceptionOccured(ex, true);
		}
		
		super.open();
		if(splitSample != null) splitSample.setDividerLocation(0.5);
	}

	@Override
	public boolean isAvail() {
		return (GlobalLogs.isLog4jAvail());
	}

	/** XML 파일 경로 반환 */
	protected File getLog4jXmlFile() {
		File cfgRoot = ColonyManager.getHomeDir("colonization", "configs");
		if(! cfgRoot.exists()) {
			cfgRoot.mkdirs();
		}
		
		return new File(cfgRoot.getAbsolutePath() + File.separator + "log4j.xml");
	}
}
