package org.duckdns.hjow.colonization.ui.tools;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.ui.GUIPreWorks;
import org.duckdns.hjow.commons.util.DataUtil;
import org.duckdns.hjow.commons.util.FileUtil;

/** log4j 설정 관리자 */
public class Log4JManager extends AbstractTool {
    protected JTabbedPane tabMain;
    protected JPanel pnTabNow, pnTabSample;
    protected JEditorPane taNow, taSample;
    protected JSplitPane splitSample;
    protected JTextField tfFileName, tfFilePattern;
    protected JCheckBox chkFile;
    
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
        if(GUIPreWorks.isSyntaxPaneEnabled()) taSample.setContentType("text/xml");
        else taSample.setContentType("text/plain");
        splitSample.setRightComponent(new JScrollPane(taSample));
        
        JPanel pnSampleCreateRoot = new JPanel();
        pnSampleCreateRoot.setLayout(new BorderLayout());
        splitSample.setLeftComponent(pnSampleCreateRoot);
        
        JPanel pnSampleCreateCenter = new JPanel();
        JPanel pnSampleCreateDown   = new JPanel();
        pnSampleCreateCenter.setLayout(new GridBagLayout());
        pnSampleCreateDown.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnSampleCreateRoot.add(pnSampleCreateCenter, BorderLayout.CENTER);
        pnSampleCreateRoot.add(pnSampleCreateDown  , BorderLayout.SOUTH);
        
        int rowNo = 0;
        GridBagConstraints gridBagConst;
        JPanel pn;
        JLabel lb;
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 10;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 1.0;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        pn = new JPanel();
        pn.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnSampleCreateCenter.add(pn, gridBagConst);
        
        chkFile = new JCheckBox(ColonyManager.t("파일로 저장"));
        chkFile.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    tfFileName.setEnabled(true);
                    tfFilePattern.setEnabled(true);
                } else {
                    tfFileName.setEnabled(false);
                    tfFilePattern.setEnabled(false);
                }
            }
        });
        pn.add(chkFile);
        
        rowNo++;
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 1;
        gridBagConst.gridheight = 1;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        pn = new JPanel();
        pn.setLayout(new FlowLayout(FlowLayout.LEFT));
        lb = new JLabel(ColonyManager.t("파일명"));
        pn.add(lb);
        pnSampleCreateCenter.add(pn, gridBagConst);
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 1;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 9;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 1.0;
        gridBagConst.fill = GridBagConstraints.HORIZONTAL;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        File loggingPath = ColonyManager.getHomeDir("colonization", "logs");
        if(! loggingPath.exists()) loggingPath.mkdirs();
        
        tfFileName = new JTextField();
        tfFileName.setText(new File(loggingPath.getAbsolutePath() + File.separator + "colonization.log").getAbsolutePath());
        tfFileName.setEnabled(false);
        pnSampleCreateCenter.add(tfFileName, gridBagConst);
        
        rowNo++;
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 1;
        gridBagConst.gridheight = 1;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        pn = new JPanel();
        pn.setLayout(new FlowLayout(FlowLayout.LEFT));
        lb = new JLabel(ColonyManager.t("파일명 패턴"));
        pn.add(lb);
        pnSampleCreateCenter.add(pn, gridBagConst);
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 1;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 9;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 1.0;
        gridBagConst.fill = GridBagConstraints.HORIZONTAL;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        tfFilePattern = new JTextField();
        tfFilePattern.setText(new File(loggingPath.getAbsolutePath() + File.separator + "colonization.%d{yyyyMMdd-HHmm}%i.log").getAbsolutePath());
        tfFilePattern.setEnabled(false);
        pnSampleCreateCenter.add(tfFilePattern, gridBagConst);
        
        rowNo++;
        
        // 맨 아래에 빈 공간 넣기
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 10;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 1.0;
        gridBagConst.weighty = 1.0;
        gridBagConst.fill = GridBagConstraints.BOTH;
        pnSampleCreateCenter.add(new JPanel(), gridBagConst);
        
        JButton btnCreate = new JButton(ColonyManager.t("샘플 생성") + " >");
        pnSampleCreateDown.add(btnCreate);
        btnCreate.addActionListener(new ActionListener() {    
            @Override
            public void actionPerformed(ActionEvent e) {
                onCreateCalled();
            }
        });
        
        taSample.setText(buildLog4jSample());
    }
    
    protected int getDialogProperWidth( ) { return 800; }
    protected int getDialogProperHeight() { return 600; }
    
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
    
    protected void onCreateCalled() {
        try {
            taSample.setText("");
            taSample.setText(buildLog4jSample());
        } catch(Exception ex) {
            GlobalLogs.processExceptionOccured(ex, true);
            JOptionPane.showMessageDialog(getDialog(), ColonyManager.t("오류") + " : " + ex.getMessage());
        }
    }
    
    protected String buildLog4jSample() {
        StringBuilder sample = new StringBuilder("");
        
        sample = sample.append("\n").append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sample = sample.append("\n").append("<Configuration>");
        sample = sample.append("\n").append("    <Appenders>");
        sample = sample.append("\n").append("        <Console name=\"console\" target=\"SYSTEM_OUT\" immediateFlush=\"true\">");
        sample = sample.append("\n").append("            <PatternLayout pattern=\"%d %5p [%c] %m%n\" />");
        sample = sample.append("\n").append("        </Console>");
        
        if(chkFile.isSelected()) {
            String name    = tfFileName.getText().trim();
            String pattern = tfFilePattern.getText().trim();
            if(DataUtil.isEmpty(name   )) throw new RuntimeException(ColonyManager.t("파일명을 입력해 주세요."));
            if(DataUtil.isEmpty(pattern)) throw new RuntimeException(ColonyManager.t("파일명 패턴을 입력해 주세요."));
            if(name.contains("\"") || pattern.contains("\"")) throw new RuntimeException(ColonyManager.t("파일명과 패턴에서는 \" 기호를 사용할 수 없습니다."));
            
            sample = sample.append("\n").append("        <RollingFile name=\"fileLog\" fileName=\"").append(name).append("\" filePattern=\"").append(pattern).append("\" immediateFlush=\"true\">");
            sample = sample.append("\n").append("            <PatternLayout pattern=\"%d %5p [%c] %m%n\" />");
            sample = sample.append("\n").append("            <Policies>");
            sample = sample.append("\n").append("                <SizeBasedTriggeringPolicy size=\"1000\" />");
            sample = sample.append("\n").append("            </Policies>");
            sample = sample.append("\n").append("            <DefaultRolloverStrategy max=\"3\" fileIndex=\"min\" />");
            sample = sample.append("\n").append("        </RollingFile>");
        }
        
        sample = sample.append("\n").append("    </Appenders>");
        sample = sample.append("\n").append("    <Loggers>");
        sample = sample.append("\n").append("        <Root level=\"INFO\">");
        if(chkFile.isSelected()) {
            sample = sample.append("\n").append("            <AppenderRef ref=\"fileLog\" />");
        } else {
            sample = sample.append("\n").append("            <AppenderRef ref=\"console\" />");
        }
        sample = sample.append("\n").append("        </Root>");
        sample = sample.append("\n").append("    </Loggers>");
        sample = sample.append("\n").append("</Configuration>");
        
        return sample.toString().trim();
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
