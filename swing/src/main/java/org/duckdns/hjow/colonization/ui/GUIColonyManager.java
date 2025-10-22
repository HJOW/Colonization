package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.duckdns.hjow.colonization.ColonizationMainClass;
import org.duckdns.hjow.colonization.ColonyClassLoader;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GUIColonizationMainClass;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.SimulationSpeed;
import org.duckdns.hjow.colonization.benchmark.BenchmarkManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.mod.Mod;
import org.duckdns.hjow.colonization.ui.help.HelpDialog;
import org.duckdns.hjow.colonization.ui.tools.CDOCViewer;
import org.duckdns.hjow.colonization.ui.tools.GUITCPSimpleDaemonManager;
import org.duckdns.hjow.commons.data.CompressedDocument;
import org.duckdns.hjow.commons.util.DataUtil;
import org.duckdns.hjow.commons.util.GUIUtil;

/** Colonization 프로그램 핵심 클래스 Swing 버전 */
public class GUIColonyManager extends ColonyManager {
    private static final long serialVersionUID = -2483528821790634383L;
    
    protected transient JFrame frame;
    protected transient JPanel pnMain, pnLocalRoot, pnLocalSecond, pnFront;
    protected transient JProgressBar progFront;
    protected transient JTabbedPane tabMain;
    protected transient CardLayout cardLocalLoading1, cardLocalLoading2;
    protected transient JButton btnSaveAs, btnLoadAs, btnThrPlay, btnGotoGame;
    
    protected transient JEditorPane webNotice;
    
    protected transient JPanel pnCols, pnNoColonies;
    protected transient DefaultColonyPanel cpNow;
    protected transient JComboBox<Colony> cbxColony;
    protected transient JComboBox<String> cbxSimuCount;
    protected transient List<DefaultColonyPanel> pnColonies = new Vector<DefaultColonyPanel>();
    
    protected transient JProgressBar progThreadStatus;
    protected transient JLabel lbRunningTime;
    
    protected transient JFileChooser fileChooser;
    protected transient javax.swing.filechooser.FileFilter filterCol, filterColGz;
    
    protected transient BackupManager backupManager;
    protected transient BenchmarkManager benchManager;
    protected transient GUITCPSimpleDaemonManager daemonManager;
    protected transient ConfigManager configManager;
    protected transient ModManager modManager;
    protected transient HelpDialog helpDialog;
    protected transient ServletClientPanel servletClient;
    protected transient CDOCViewer cdocViewer;
    
    protected transient JMenuBar menuBar;
    protected transient JMenu menuFile, menuAction, menuView, menuHelp, menuMods;
    protected transient JMenuItem menuActionThrPlay, menuFileSave, menuFileLoad, menuFileBackup, menuFileRestore, menuFileReset, menuFileNew, menuFileDel, menuFileConfig, menuFileMods;
    
    protected transient Queue<RefreshRequest> queueRefreshes = new LinkedList<RefreshRequest>();
    protected transient List<ModDialog> modDialogs = new ArrayList<ModDialog>();
    
    /** 생성자, 상위 프로그램에서 호출됨 */
    public GUIColonyManager(GUIColonizationMainClass superInstance) {
        super();
        this.superInstance = superInstance;
    }

    /** UI 초기화 */
    public void init(GUIColonizationMainClass superInstance) {
        // 설정 파일 읽기
        loadLocalConfigs();
        
        // LookAndFeel 설정
        String lookAndFeel = configs.getString("LookAndFeel");
        if(DataUtil.isEmpty(lookAndFeel)) { lookAndFeel = "Nimbus"; configs.set("LookAndFeel", lookAndFeel); }
        GUIUtil.setLookAndFeel(lookAndFeel.trim()); 
        
        // JFrame 생성
        frame = new JFrame();
    	
    	// JFrame 설정
        Dimension winSize = GUIUtil.getScreenSize();
        int w, h;
        w = (int) (winSize.getWidth()  * 0.8);
        h = (int) (winSize.getHeight() * 0.8);
        
        if(w >= winSize.getWidth()  - 50) w = (int) (winSize.getWidth()  - 50);
        if(h >= winSize.getHeight() - 50) h = (int) (winSize.getHeight() - 80);
        
        if(w < 800) w = 800;
        if(h < 600) h = 600;
        int logHeight = 250;
        
        frame.setSize(w, h);
        GUIUtil.centerWindow(frame);
        frame.setSize(w, h - logHeight); // 로그 대화상자 들어갈 자리 마련
        frame.setTitle("Colonization");
        frame.setIconImage(GUIUtil.iconToImage(getIcon()));
        frame.setLayout(new BorderLayout());
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onWindowClosing();
            }
        });
        /*frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                refreshArenaPanel(0);
            }
        });*/
        
        initializeUI();
    }
    
    /** UI 파트 초기화 */
    public void initializeUI() {
    	Dimension winSize = GUIUtil.getScreenSize();
        int w, h;
        w = (int) (winSize.getWidth()  * 0.8);
        h = (int) (winSize.getHeight() * 0.8);
        
        if(w >= winSize.getWidth()  - 50) w = (int) (winSize.getWidth()  - 50);
        if(h >= winSize.getHeight() - 50) h = (int) (winSize.getHeight() - 80);
        
        if(w < 800) w = 800;
        if(h < 600) h = 600;
        int logHeight = 250;
        
        if(dialogGlobalLog != null) dialogGlobalLog.dispose();
        GlobalLogDialog logDiag = new GlobalLogDialog(this);
        dialogGlobalLog = logDiag;
        logDiag.setSize(w, logHeight);
        logDiag.setLocationBottom(frame);
        logDiag.setDetailLevel(2);
        
        if(fileChooser == null) {
            fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);
            filterCol = new javax.swing.filechooser.FileFilter() {
                @Override
                public String getDescription() {
                    return t("정착지 파일") + " (*.colony)";
                }
                
                @Override
                public boolean accept(File f) {
                    if(f == null) return false;
                    if(f.isDirectory()) return false;
                    
                    return f.getName().toLowerCase().endsWith(".colony");
                }
            };
            filterColGz = new javax.swing.filechooser.FileFilter() {
                @Override
                public String getDescription() {
                    return t("정착지 GZ 압축형 파일") + " (*.colgz)";
                }
                
                @Override
                public boolean accept(File f) {
                    if(f == null) return false;
                    if(f.isDirectory()) return false;
                    
                    return f.getName().toLowerCase().endsWith(".colgz");
                }
            };
            fileChooser.addChoosableFileFilter(filterCol);
            fileChooser.addChoosableFileFilter(filterColGz);
        }
        
        if(pnMain == null) {
        	pnMain = new JPanel();
            frame.add(pnMain, BorderLayout.CENTER);
        } else {
        	pnMain.removeAll();
        }
        
        
        tabMain = new JTabbedPane();
        pnMain.setLayout(new BorderLayout());
        pnMain.add(tabMain, BorderLayout.CENTER);
        
        JPanel pnMainCard1, pnMainCard2;
        pnLocalRoot = new JPanel();
        pnFront     = new JPanel();
        pnMainCard1 = new JPanel();
        pnMainCard2 = new JPanel();
        
        servletClient = new ServletClientPanel(this);
        
        tabMain.add(t("홈"), pnFront);
        tabMain.add(t("로컬"), pnLocalRoot);
        tabMain.add(t("웹"), servletClient);
        
        pnFront.setLayout(new BorderLayout());
        
        JPanel pnFrontCenter, pnFrontDown;
        pnFrontCenter = new JPanel();
        pnFrontDown   = new JPanel();
        pnFront.add(pnFrontCenter, BorderLayout.CENTER);
        pnFront.add(pnFrontDown  , BorderLayout.SOUTH);
        
        webNotice = new JEditorPane();
        webNotice.setEditable(false);
        webNotice.setContentType("text/html; charset=UTF-8");
        webNotice.setText(ColonyClassLoader.htmlNoticeEmpty());
        
        pnFrontCenter.setLayout(new BorderLayout());
        pnFrontCenter.add(new JScrollPane(webNotice), BorderLayout.CENTER);
        
        JPanel pnFrontDownCenter, pnFrontDownRight, pnFrontDownLeft;
        pnFrontDownLeft   = new JPanel();
        pnFrontDownCenter = new JPanel();
        pnFrontDownRight  = new JPanel();
        pnFrontDown.setLayout(new BorderLayout());
        pnFrontDown.add(pnFrontDownLeft  , BorderLayout.WEST);
        pnFrontDown.add(pnFrontDownCenter, BorderLayout.CENTER);
        pnFrontDown.add(pnFrontDownRight , BorderLayout.EAST);
        
        pnFrontDownLeft.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnFrontDownCenter.setLayout(new FlowLayout(FlowLayout.CENTER));
        pnFrontDownRight.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        progFront = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        pnFrontDownLeft.add(progFront);
        
        btnGotoGame = new JButton("Colonization");
        pnFrontDownRight.add(btnGotoGame);
        
        btnGotoGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabMain.setSelectedIndex(1);
                progFront.setVisible(false);
            }
        });
        
        JButton btnExit = new JButton(t("종료"));
        pnFrontDownRight.add(btnExit);
        
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flagSaveBeforeClose = false;
                dispose(true);
            }
        });
        
        cardLocalLoading1 = new CardLayout(); // 카드 레이아웃 1층 (로딩이 중복으로 걸리는 경우 혼란을 방지)
        cardLocalLoading2 = new CardLayout(); // 카드 레이아웃 2층
        
        pnLocalRoot.setLayout(cardLocalLoading1);
        
        pnLocalRoot.add(pnMainCard1, "C1");
        pnLocalRoot.add(pnMainCard2, "C2");
        
        cardLocalLoading1.show(pnLocalRoot, "C2");
        
        pnMainCard1.setLayout(new BorderLayout());
        pnMainCard2.setLayout(new BorderLayout());
        
        JPanel pnHide = new JPanel();
        pnMainCard2.add(pnHide, BorderLayout.CENTER);
        pnMainCard2.add(new JPanel(), BorderLayout.NORTH);
        pnMainCard2.add(new JPanel(), BorderLayout.SOUTH);
        
        pnHide.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        JProgressBar progHide = new JProgressBar();
        progHide.setIndeterminate(true);
        pnHide.add(progHide);
        
        JPanel pnSouth, pnCenter, pnNorth;
        pnSouth  = new JPanel();
        pnCenter = new JPanel();
        pnNorth  = new JPanel();
        
        pnSouth.setLayout( new BorderLayout());
        pnCenter.setLayout(new BorderLayout());
        pnNorth.setLayout( new BorderLayout());
        
        pnMainCard1.add(pnSouth , BorderLayout.SOUTH);
        pnMainCard1.add(pnCenter, BorderLayout.CENTER);
        pnMainCard1.add(pnNorth , BorderLayout.NORTH);
        
        JToolBar toolbarNorth = new JToolBar();
        pnNorth.add(toolbarNorth, BorderLayout.NORTH);
        
        btnSaveAs = new JButton(UIManager.getIcon("FileView.floppyDriveIcon"));
        toolbarNorth.add(btnSaveAs);
        btnSaveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSaveRequested();
            }
        });
        
        btnLoadAs = new JButton(UIManager.getIcon("FileView.directoryIcon"));
        toolbarNorth.add(btnLoadAs);
        btnLoadAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLoadRequested();
            }
        });
        
        cbxColony  = new JComboBox<Colony>();
        toolbarNorth.add(cbxColony);
        
        cbxColony.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                time = null;
                SwingUtilities.invokeLater(new Runnable() {   
                    @Override
                    public void run() {
                        refreshColonyContent();
                    }
                });
            }
        });
        
        Vector<String> vCounts = new Vector<String>();
        vCounts.add(t("수동"));
        vCounts.add(t("1분 후 자동 정지"));
        vCounts.add(t("10분 후 자동 정지"));
        cbxSimuCount = new JComboBox<String>(vCounts);
        toolbarNorth.add(cbxSimuCount);

        Vector<SimulationSpeed> strSpeeds = getSpeedList();
        JComboBox<SimulationSpeed> cbxSpeed = new JComboBox<SimulationSpeed>(strSpeeds);
        cbxSpeed.setSelectedIndex(0);
        toolbarNorth.add(cbxSpeed);

        cbxSpeed.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                SimulationSpeed spd = (SimulationSpeed) cbxSpeed.getSelectedItem();
                if(spd == null) cycleGap = CYCLEGAP_DEFAULT;
                else cycleGap = spd.getThreadGap();
            }
        });
        
        btnThrPlay = new JButton(t("시뮬레이션 시작"));
        toolbarNorth.add(btnThrPlay);
        
        btnThrPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleSimulationRunning();
            }
        });
        
        progThreadStatus = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        toolbarNorth.add(progThreadStatus);
        
        lbRunningTime = new JLabel();
        toolbarNorth.add(lbRunningTime);
        
        toolbarNorth.add(new JPanel()); // 특정 LookAndFeel 에서 우측에 공간 남기기 위함
        
        lbRunningTime.setVisible(false);
        
        pnCols       = new JPanel();
        pnNoColonies = new JPanel();
        
        JPanel pnArenaRoot = new JPanel();
        pnArenaRoot.setLayout(cardLocalLoading2);
        pnLocalSecond = pnArenaRoot;
        pnCenter.add(pnArenaRoot, BorderLayout.CENTER);
        
        JPanel pnArenaFirst, pnArenaSecond;
        pnArenaFirst  = new JPanel();
        pnArenaSecond = new JPanel();
        pnArenaFirst.setLayout(new BorderLayout());
        pnArenaSecond.setLayout(new BorderLayout());
        
        pnArenaRoot.add(pnArenaFirst , "C1F");
        pnArenaRoot.add(pnArenaSecond, "C1S");
        
        pnHide = new JPanel();
        pnArenaSecond.add(pnHide, BorderLayout.CENTER);
        pnArenaSecond.add(new JPanel(), BorderLayout.NORTH);
        pnArenaSecond.add(new JPanel(), BorderLayout.SOUTH);
        
        progHide = new JProgressBar();
        progHide.setIndeterminate(true);
        pnHide.add(progHide);
        
        cardLocalLoading2.show(pnArenaRoot, "C1S");
        
        
        JPanel pnArena = new JPanel();
        JPanel pnCtrl  = new JPanel();
        
        pnArena.setLayout( new BorderLayout());
        pnCtrl.setLayout( new BorderLayout());
        pnCols.setLayout( new BorderLayout());
        pnNoColonies.setLayout( new BorderLayout());
        pnArenaFirst.add(pnArena, BorderLayout.CENTER);
        
        pnArena.add(pnCols, BorderLayout.CENTER);
        pnArena.add(pnCtrl , BorderLayout.NORTH);
        
        pnNoColonies.add(new JPanel(), BorderLayout.NORTH);
        pnNoColonies.add(new JPanel(), BorderLayout.SOUTH);
        pnNoColonies.add(new JPanel(), BorderLayout.EAST);
        pnNoColonies.add(new JPanel(), BorderLayout.WEST);
        
        JPanel pnNoColMain = new JPanel();
        pnNoColMain.setLayout(new BorderLayout());
        pnNoColonies.add(pnNoColMain, BorderLayout.CENTER);
        
        JPanel pnNoColCenter, pnNoColSouth;
        pnNoColCenter = new JPanel();
        pnNoColSouth  = new JPanel();
        pnNoColCenter.setLayout(new FlowLayout(FlowLayout.CENTER));
        pnNoColSouth.setLayout( new FlowLayout(FlowLayout.CENTER));
        pnNoColMain.add(pnNoColCenter, BorderLayout.CENTER);
        pnNoColMain.add(pnNoColSouth , BorderLayout.SOUTH);
        
        JButton btnNewCol = new JButton(t("새 정착지 개척"));
        btnNewCol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLocalLoading1.show(pnLocalRoot, "C2");
                onNewColonyRequested();
                cardLocalLoading1.show(pnLocalRoot, "C1");
            }
        });
        pnNoColCenter.add(btnNewCol);
        
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        JMenuItem menuItem;
        
        menuFile = new JMenu(t("파일"));
        menuBar.add(menuFile);
        
        menuFileNew = new JMenuItem(t("새 정착지 개척"));
        menuFile.add(menuFileNew);
        menuFileNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNewColonyRequested();
            }
        });
        
        menuFileDel = new JMenuItem(t("이 정착지 삭제"));
        menuFile.add(menuFileDel);
        menuFileDel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteThisRequested();
            }
        });
        
        menuFile.addSeparator();
        
        menuFileSave = new JMenuItem(t("다른 이름으로 이 정착지 저장"));
        menuFile.add(menuFileSave);
        menuFileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
        menuFileSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSaveRequested();
            }
        });
        
        menuFileLoad = new JMenuItem(t("외부 정착지 파일 불러오기"));
        menuFile.add(menuFileLoad);
        menuFileLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_MASK));
        menuFileLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLoadRequested();
            }
        });
        
        menuFile.addSeparator();

        menuFileBackup = new JMenuItem(t("백업"));
        menuFile.add(menuFileBackup);
        menuFileBackup.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK));
        menuFileBackup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onBackupRequested();
            }
        });
        
        menuFileRestore = new JMenuItem(t("복원"));
        menuFile.add(menuFileRestore);
        menuFileRestore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRestoreRequested();
            }
        });
        
        menuFileReset = new JMenuItem(t("정착지 모두 포기 (초기화)"));
        menuFile.add(menuFileReset);
        menuFileReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onResetAllRequested();
            }
        });
        
        menuFile.addSeparator();
        
        menuFileConfig = new JMenuItem(t("설정"));
        menuFile.add(menuFileConfig);
        menuFileConfig.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, KeyEvent.CTRL_MASK));
        menuFileConfig.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				configManager.open();
			}
		});
        
        menuFileMods = new JMenuItem(t("MOD 관리"));
        menuFile.add(menuFileMods);
        menuFileMods.addActionListener(new ActionListener() {
        	@Override
			public void actionPerformed(ActionEvent e) {
        		modManager.open();
			}
        });
        
        menuFile.addSeparator();
        
        menuItem = new JMenuItem(t("재시작"));
        menuFile.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flagSaveBeforeClose = false;
                dispose(true);
                superInstance.restart();
            }
        });
        
        menuItem = new JMenuItem(t("종료"));
        menuFile.add(menuItem);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flagSaveBeforeClose = false;
                dispose(true);
                superInstance.exit();
            }
        });
        
        menuAction = new JMenu(t("동작"));
        menuBar.add(menuAction);
        
        menuActionThrPlay = new JMenuItem(t("시뮬레이션 시작") + " (" + t("로컬") + ")");
        menuAction.add(menuActionThrPlay);
        menuActionThrPlay.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));
        menuActionThrPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleSimulationRunning();
            }
        });

        if(ColonyClassLoader.getInstalledPackNewFeatures().contains("DebugEnable")) menuAction.addSeparator();

        menuItem = new JCheckBoxMenuItem(t("디버그 모드"));
        menuAction.add(menuItem);
        menuItem.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                flagDebugMode = ((JCheckBoxMenuItem)e.getSource()).isSelected();
                lbRunningTime.setVisible(isDebugModeEnabled());
                
                if(dialogGlobalLog != null) {
                	if(flagDebugMode) dialogGlobalLog.setDetailLevel(1);
                    else              dialogGlobalLog.setDetailLevel(2);
                }
            }
        });
        ((JCheckBoxMenuItem) menuItem).setSelected(isDebugModeEnabled());
        if(! ColonyClassLoader.getInstalledPackNewFeatures().contains("DebugEnable")) {
        	menuItem.setVisible(false);
        }

        
        menuView = new JMenu(t("보기"));
        menuBar.add(menuView);
        
        menuItem = new JMenuItem(t("전역 로그 보기"));
        menuView.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialogGlobalLog.open(getSelf());
                
                GlobalLogDialog logDiag = (GlobalLogDialog) dialogGlobalLog;
                logDiag.setLocationBottom(getDialog());
            }
        });
        
        menuView.addSeparator();
        
        menuItem = new JMenuItem(t("성능 벤치마크"));
        menuView.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                benchManager.open();
            }
        });
        
        menuItem = new JMenuItem(t("TCP 데몬"));
        menuView.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                daemonManager.open();
            }
        });
        
        menuView.addSeparator();
        
        cdocViewer = new CDOCViewer(frame);
        menuItem = new JMenuItem(t(CompressedDocument.FILE_DESC) + " Tool");
        menuView.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	cdocViewer.open();
            }
        });
        
        menuView.addSeparator();
        
        menuMods = new JMenu(t("MOD"));
        menuView.add(menuMods);
        
        menuHelp = new JMenu(t("도움말"));
        menuBar.add(menuHelp);
        
        menuItem = new JMenuItem(t("도움말"));
        menuHelp.add(menuItem);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, KeyEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	helpDialog.open();
            }
        });
        
        benchManager  = new BenchmarkManager(frame);
        backupManager = new BackupManager(this);
        daemonManager = new GUITCPSimpleDaemonManager(frame);
        helpDialog    = new HelpDialog(this);
        configManager = new ConfigManager(this);
        modManager    = new ModManager(this);
        
        refreshColonyContent();
    }

    /** 지원되는 시뮬 속도 목록 반환 */
    protected Vector<SimulationSpeed> getSpeedList() {
        Vector<SimulationSpeed> strSpeeds = new Vector<SimulationSpeed>();
        strSpeeds.add(new SimulationSpeed(1));
        strSpeeds.add(new SimulationSpeed(2));
        strSpeeds.add(new SimulationSpeed(3));
        return strSpeeds;
    }

    /** 창이 열리기 전 수행해야 할 작업 */
    public void onBeforeOpened(GUIColonizationMainClass superInstance) {
        if(thread != null) { try { threadSwitch = false; thread.interrupt(); Thread.sleep(1000L); } catch(Exception exc) {} }
        if(frame == null) init(superInstance);
        
        cardLocalLoading1.show(pnLocalRoot, "C2");
        cardLocalLoading2.show(pnLocalSecond, "C1S");
        
        // 정착지 목록 불러오기
        loadColonies();
        
        // 공지사항 등 웹 설정 불러오기
        loadWebConfigs();
        
        btnThrPlay.setEnabled(false);
        menuActionThrPlay.setEnabled(false);
        
        threadPaused = true;
        reserveSaving  = false;
        reserveRefresh = false;
        flagAlreadyDisposed = false;
        
        // 쓰레드 가동 시작
        assureMainThreadRunning();
        
        btnThrPlay.setText("시뮬레이션 시작");
        btnThrPlay.setEnabled(true);
        menuActionThrPlay.setEnabled(true);
        
        // 공지사항 미리 화면에 로딩
        try { webNotice.setPage(ColonyClassLoader.htmlNoticeUrl()); } catch(java.net.UnknownHostException ex) {
            webNotice.setText(ColonyClassLoader.htmlNoticeEmpty());
        } catch(Exception ex) { 
            GlobalLogs.processExceptionOccured(ex, false); 
        }
        
        // UI 에 MODS 반영
        applyModOnUI();
        
        // 모두 활성화
        setEditable(true);
        
        // 전역 로그 대화상자 세팅
        if(dialogGlobalLog == null) {
        	dialogGlobalLog = new GlobalLogDialog(this);
        	dialogGlobalLog.setDetailLevel(2);
        }
        
        // 전체 새로고침
        refreshArenaPanelIn(0);
    }

    /** 창이 열린 후 수행해야 할 작업 */
    public void onAfterOpened(GUIColonizationMainClass superInstance) {
        SwingUtilities.invokeLater(new Runnable() {   
            @Override
            public void run() {
                ((GUIColonizationMainClass) superInstance).closeLoadingDialog();
                
                cardLocalLoading1.show(pnLocalRoot, "C1");
                cardLocalLoading2.show(pnLocalSecond, "C1F");
            }
        });
    }
    
    /** 별도 쓰레드에서 웹 서버에서 설정 불러오기 */
    protected void loadWebConfigs() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                progFront.setValue(0);
                progFront.setIndeterminate(true);
                ColonyClassLoader.loadWebConfigs(getSelf());
                
                progFront.setIndeterminate(false);
                if(progFront.isVisible()) {
                    int r = 1;
                    
                    while(r < 100) {
                        try { Thread.sleep(12L); } catch(InterruptedException ex) { GlobalLogs.processExceptionOccured(ex, false); break; }
                        progFront.setValue(r);
                        
                        if(frame.isVisible()) r++;
                    }
                    
                    if(tabMain.getSelectedIndex() == 0) tabMain.setSelectedIndex(1);
                    progFront.setVisible(false);
                }
            }
        }).start();
    }
    
    /** 메인 쓰레드 실행 */
    protected void turnOnMainThread() {
        if(thread != null) {
            thread.interrupt();
            try { Thread.sleep(1000L); } catch(InterruptedException ex) { GlobalLogs.processExceptionOccured(ex, false); }
        }
        thread = new Thread(new Runnable() {    
            @Override
            public void run() {
                while(threadSwitch) {
                    if(! onMainThread()) break;
                }
                threadShutdown = true;
                progThreadStatus.setIndeterminate(false);
                btnThrPlay.setEnabled(false);
                menuActionThrPlay.setEnabled(false);
            }
        });
        threadSwitch   = true;
        threadShutdown = false;
        flagSaveBeforeClose = true;
        thread.start();
    }
    
    /** 메인 쓰레드 동작 */
    protected boolean onMainThread() {
        threadShutdown = false;
        long elapsed = System.currentTimeMillis();
        long gap = cycleGap;
        
        // 쓰레드에서 수행할 실질 작업 수행
        try { if(! threadPaused) { bCheckerPauseCompleted = false; oneCycle(); } } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        
        // 실행 횟수 제한이 있는 경우 차감 (단, 음수인 경우는 무제한이라고 판단)
        if(cycleRunCount > 0 && (! threadPaused)) {
        	cycleRunCount--;
        	if(cycleRunCount <= 0) pauseSimulation();
        }
        
        // 저장 요청 수행
        if(reserveSaving) { try { saveColonies(); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); } reserveSaving = false; }
        
        // 리프레시 요청 수행
        if(reserveRefresh) {
            try {
            	refreshColonyContent();
            } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
            reserveRefresh = false;
        } else {
        	handleRefreshRequests();
        }
        
        // 일시정지 후 쓰레드가 실제 정지 중인지 판단하는 플래그
        if(threadPaused) bCheckerPauseCompleted = true;
        else bCheckerPauseCompleted = false;
        
        // 쓰레드 Sleep
        try { Thread.sleep(gap); } catch(InterruptedException e) { threadSwitch = false; return false; }

        cycleRunningTime = System.currentTimeMillis() - elapsed - gap;
        if(isDebugModeEnabled()) lbRunningTime.setText("  " + String.valueOf(cycleRunningTime) + " ms");
        
        threadShutdown = false;
        progThreadStatus.setIndeterminate(! threadPaused);
        
        return true;
    }
    
    /** 정착지 생성 요청 시 호출됨 */
    protected void onNewColonyRequested() {
        NewColonyManager dialogNewCol = new NewColonyManager(this);
        dialogNewCol.open();
    }
    
    /** 새 정착지 대화상자 응답 시 호출 */
    public void onNewColonyTypeDecided(String type, String name, int difficulty, NewColonyManager decider) {
        if(decider == null) return;
        Colony newCol = newColony(type, name);
        cbxColony.setSelectedItem(newCol);
        
        cardLocalLoading1.show(pnLocalRoot, "C2");
        SwingUtilities.invokeLater(new Runnable() {   
            @Override
            public void run() {
                refreshColonyContent();
                cardLocalLoading1.show(pnLocalRoot, "C1");
            }
        });
    }
    
    /** 현재의 정착지 삭제 요청 시 호출됨 */
    protected void onDeleteThisRequested() {
        Colony col = getColony();
        int sel = JOptionPane.showConfirmDialog(getDialog(), t("정착지 [COLONY] 을/를 포기하시겠습니까?").replace("[COLONY]", col.getName()), t("확인"), JOptionPane.YES_NO_OPTION);
        if(sel != JOptionPane.YES_OPTION) return;
        
        cardLocalLoading1.show(pnLocalRoot, "C2");
        
        // 리스트에서 삭제
        int idx = 0;
        while(idx < colonies.size()) {
            if(colonies.get(idx).getKey() == col.getKey()) {
                colonies.remove(idx);
            }
            idx++;
        }
        
        // 파일도 삭제
        File root = getColonySaveRootDirectory();
        File[] lists = root.listFiles(getColonyFileFilter());
        for(File f : lists) {
            try {
                Colony temp = ColonyClassLoader.loadColony(f);
                if(temp.getKey() == col.getKey()) f.delete();
            } catch(Exception ex) {} // 오류 건너뛰기
        }
        
        // 정착지 목록이 비어 있으면 생성
        if(colonies.isEmpty()) newColony();
        
        // 새로 고침
        refreshColonyList();
        cardLocalLoading1.show(pnLocalRoot, "C1");
    }
    
    /** 정착지 하나를 별도 파일로 저장 요청 시 호출됨 */
    protected void onSaveRequested() {
        Colony c = getSelectedColony();
        if(c == null) { alert(t("저장할 정착지를 선택해 주세요.")); return; }
        
        int s = fileChooser.showSaveDialog(getDialog());
        if(s == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            saveColony(c, f, true);
        }
    }
    
    /** 정착지 파일 불러오기 요청 시 호출됨 */
    protected void onLoadRequested() {
        int s = fileChooser.showOpenDialog(getDialog());
        if(s == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            loadColony(f, true);
            refreshColonyList();
        }
    }

    /** 정착지 전체 백업 요청 시 호출됨 */
    protected void onBackupRequested() {
        backupManager.openSave(colonies);
    }

    /** 정착지 복원 요청 시 호출됨 */
    protected void onRestoreRequested() {
        backupManager.openLoad();
    }
    
    /** 정착지 세이브 모두 초기화 요청 시 호출됨 */
    protected void onResetAllRequested() {
        int sel = JOptionPane.showConfirmDialog(getDialog(), "정착지들을 모두 포기하시겠습니까?\n별도로 저장하지 않은 모든 정착지가 사라집니다 !", "확인", JOptionPane.YES_NO_OPTION);
        if(sel == JOptionPane.YES_OPTION) {
            cardLocalLoading1.show(pnLocalRoot, "C2");
            pauseSimulation();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    resetAllColony();
                    refreshArenaPanel(0);
                    cardLocalLoading1.show(pnLocalRoot, "C1");
                }
            }).start();
        }
    }
    
    /** 백업 복원 받기 */
    public void applyRestore(List<Colony> colonies, BackupManager backupMan, boolean concat) {
        if(backupManager != backupMan) return;
        
        cardLocalLoading1.show(pnLocalRoot, "C2");
        
        // 파일 다 지워야 함
        File root = getColonySaveRootDirectory();
        File[] lists = root.listFiles(getColonyFileFilter());
        for(File f : lists) {
            f.delete();
        }
        
        // 복원 처리
        if(concat) { // 병합
            List<Colony> temp = new ArrayList<Colony>();
            temp.addAll(this.colonies);
            
            this.colonies.clear();
            
            for(Colony c : temp) {
                boolean dupl = false;
                for(Colony alreadyIn : this.colonies) {
                    if(c.getKey() == alreadyIn.getKey()) { dupl = true; break; }
                }
                if(dupl) continue;
                this.colonies.add(c);
            }
            
            for(Colony c : colonies) {
                boolean dupl = false;
                for(Colony alreadyIn : this.colonies) {
                    if(c.getKey() == alreadyIn.getKey()) { dupl = true; break; }
                }
                if(dupl) continue;
                this.colonies.add(c);
            }
        } else { // 대체
            this.colonies.clear();
            this.colonies.addAll(colonies);
        }
        
        reserveSaving = true; // 저장 예약
        refreshColonyList();  // 목록 갱신
        
        cardLocalLoading1.show(pnLocalRoot, "C1");
    }
    
    @Override
    public void log(String msg) {
        System.out.println(msg);
        GlobalLogs.log(msg);
    }

    /** 메인 대화 상자를 연다. */
    @Override
    public void open(ColonizationMainClass superInstance) {
        onBeforeOpened((GUIColonizationMainClass) superInstance);
        frame.setVisible(true);
        if(dialogGlobalLog != null) dialogGlobalLog.open(this);
        onAfterOpened((GUIColonizationMainClass) superInstance);
    }

    /** 메인 창이 떠 있는지 확인 */
    public boolean isVisible() {
        if(frame == null) return false;
        return frame.isVisible();
    }

    /** 쓰레드 종료 까지 대기 */
    protected void waitThreadShutdown() {
        threadSwitch = false;
        int prevInfinites = 0;
        while(true) {
            if(threadShutdown) break;
            try { Thread.sleep(100L); } catch(Exception ex) {  }
            
            prevInfinites++;
            if(prevInfinites >= 100000) break;
        }
    }

    @Override
    public void dispose() {
        dispose(true);
        flagAlreadyDisposed = true;
    }
    
    /** 이 객체 사용 중단 - 관련 리소스 모두 해제, 대화상자 닫기 여부도 지정 */
    @Override
    public void dispose(boolean closeDialog) {
        setEditable(false);
        disposeContents();
        
        cpNow = null;
        for(DefaultColonyPanel p : pnColonies) {
            p.dispose();
        }
        pnColonies.clear();
        colonies.clear();
        
        cardLocalLoading1 = null;
        
        if(pnLocalRoot != null) pnLocalRoot.removeAll();
        pnLocalRoot = null;
        
        if(fileChooser != null) fileChooser.setVisible(false);
        fileChooser = null;
        
        if(frame != null && closeDialog) frame.setVisible(false);
        
        if(backupManager != null) backupManager.dispose();
        backupManager = null;
        
        if(daemonManager != null) daemonManager.dispose();
        daemonManager = null;
        
        if(configManager != null) configManager.dispose();
        configManager = null;
        
        if(modManager != null) modManager.dispose();
        modManager = null;
        
        if(helpDialog != null) helpDialog.dispose();
        helpDialog = null;
        
        if(servletClient != null) servletClient.dispose();
        servletClient = null;

        if(dialogGlobalLog != null) {
            dialogGlobalLog.dispose();
            dialogGlobalLog = null;
        }
        
        for(ModDialog diag : modDialogs) {
        	diag.dispose();
        }
        modDialogs.clear();
        
        for(Mod mod : modsList) {
        	mod.dispose();
        }
        modsList.clear();
        modsEnabled.clear();
    }
    
    /** 메인 대화상자가 닫힐 때 호출 */
    public void onWindowClosing() {
        exit();
    }
    
    /** 프로그램 종료 */
    @Override
    public void exit() {
        setEditable(false);
        
        if(! flagSaveBeforeClose) return;
        flagSaveBeforeClose = false;
        
        final Vector<String> flagShutdowns = new Vector<String>();
        new Thread(new Runnable() {
            @Override
            public void run() { 
                dispose(false);
                flagShutdowns.add("1");
            }
        }).start();
        int numInfLoopPrev = 0;
        while(true) {
            if(flagShutdowns.size() >= 1) break;
            try { Thread.sleep(100L); } catch(InterruptedException ex) { break; }
            numInfLoopPrev++;
            if(numInfLoopPrev >= 100) break;
        }
        super.exit();
    }
    
    /** 메인 대화상자 내 모든 입력/버튼 등의 컴포넌트 활성화 여부 일괄 지정 */
    public void setEditable(boolean editable) {
        if(editable) {
            if(cardLocalLoading1 != null) cardLocalLoading1.show(pnLocalRoot, "C1");
        } else {
            if(cardLocalLoading1 != null) cardLocalLoading1.show(pnLocalRoot, "C2");
        }
        
        menuMods.setEnabled(editable);
        
        for(DefaultColonyPanel c : pnColonies) {
            Colony col = c.getColony();
            
            if(editable) {
                if(col == null) return;
                if(col.getHp() <= 0) return;
            }
            
            c.setEditable(editable); 
        }
    }

    @Override
    public void alert(String msg) {
        JOptionPane.showMessageDialog(getDialog(), msg);
    }

    public Icon getIcon() {
        Icon icon = UIManager.getIcon("OptionPane.informationIcon");
        Image img = GUIUtil.iconToImage(icon);
        img = img.getScaledInstance(12, 12, Image.SCALE_SMOOTH);
        ImageIcon newIcon = new ImageIcon(img);
        
        return newIcon;
    }

    /** 대화상자 객체 반환 */
    public JFrame getDialog() {
        return frame;
    }
    
    /** 대화상자 가로 길이 반환 */
    public int getDialogWidth() {
        return getDialog().getWidth();
    }
    
    /** 대화상자 세로 길이 반환 */
    public int getDialogHeight() {
        return getDialog().getHeight();
    }
    
    public int getDialogX() {
    	return getDialog().getX();
    }
    
    public int getDialogY() {
    	return getDialog().getY();
    }
    
    /** 현재 선택된 정착지 반환 */
    public Colony getSelectedColony() {
        if(selectedColony < 0) return null;
        if(selectedColony >= colonies.size()) { selectedColony = 0; return null; }
        return colonies.get(selectedColony);
    }
    
    /** 현재 선택된 정착지 반환 */
    public Colony getColony() {
        return getSelectedColony();
    }
    
    /** 해당 키를 갖는 정착지 찾아 반환 (목록에 없으면 null 반환) */
    @Override
    public Colony getColony(long colonyKey) {
        for(Colony c : colonies) {
            if(c.getKey() == colonyKey) return c;
        }
        return null;
    }
    
    /** 시뮬레이션 시작/정지 토글 */
    public void toggleSimulationRunning() {
        boolean resv = (! threadPaused);
        
        if(resv) {
            pauseSimulation();
        } else {
        	int selOptIndex = cbxSimuCount.getSelectedIndex();
            int resumeCycle = -1;
            
            if(selOptIndex == 0)      resumeCycle =  -1;
            else if(selOptIndex == 1) resumeCycle =  10;
            else                      resumeCycle = 100;
            
            resumeSimulation(resumeCycle);
        }
    }
    
    @Override
    public void pauseSimulation() {
        threadPaused = true;
        
        btnThrPlay.setEnabled(false);
        menuActionThrPlay.setEnabled(false);
        
        btnThrPlay.setText(t("시뮬레이션 시작"));
        menuActionThrPlay.setText(t("시뮬레이션 시작") + " (" + t("로컬") + ")");
        btnSaveAs.setEnabled(true);
        btnLoadAs.setEnabled(true);
        cbxColony.setEnabled(true);
        menuFileLoad.setEnabled(true);
        menuFileSave.setEnabled(true);
        menuFileBackup.setEnabled(true);
        menuFileRestore.setEnabled(true);
        menuFileReset.setEnabled(true);
        menuFileDel.setEnabled(true);
        menuFileNew.setEnabled(true);
        menuFileConfig.setEnabled(true);
        
        refreshArenaPanelIn(0);
        
        for(DefaultColonyPanel c : pnColonies) {
            Colony col = c.getColony();
            if(col == null) return;
            if(col.getHp() <= 0) return;
            c.setEditable(true); 
        }
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try { Thread.sleep(500L); } catch(InterruptedException ex) { GlobalLogs.processExceptionOccured(ex, false); }
                btnThrPlay.setEnabled(true);
                menuActionThrPlay.setEnabled(true);
                cbxSimuCount.setEnabled(true);
            }
        }).start();
    }
    
    @Override
    public void resumeSimulation(int cycleCount) {
        threadPaused = false;
        reserveSaving = true;
        cycleRunCount = cycleCount;
        
        cardLocalLoading2.show(pnLocalSecond, "C1S");
        
        btnThrPlay.setEnabled(false);
        menuActionThrPlay.setEnabled(false);
        cbxSimuCount.setEnabled(false);
        if(backupManager != null) backupManager.close();
        
        // 쓰레드가 완전히 종료될 때까지 대기
        try {
            int prefInfLoop = 10;
            while(! bCheckerPauseCompleted) {
                Thread.sleep(1000L);
                prefInfLoop--;
                if(prefInfLoop <= 0) break;
            }
        } catch(InterruptedException ex) { GlobalLogs.processExceptionOccured(ex, false); }
        
        btnThrPlay.setText(t("시뮬레이션 정지"));
        menuActionThrPlay.setText(t("시뮬레이션 정지") + " (" + t("로컬") + ")");
        btnSaveAs.setEnabled(false);
        btnLoadAs.setEnabled(false);
        cbxColony.setEnabled(false);
        menuFileLoad.setEnabled(false);
        menuFileSave.setEnabled(false);
        menuFileBackup.setEnabled(false);
        menuFileRestore.setEnabled(false);
        menuFileReset.setEnabled(false);
        menuFileDel.setEnabled(false);
        menuFileNew.setEnabled(false);
        menuFileConfig.setEnabled(false);
        if(configManager != null) configManager.close();
        if(modManager    != null) modManager.close();
        
        for(DefaultColonyPanel c : pnColonies) { c.setEditable(false); }
        
        btnThrPlay.setEnabled(true);
        menuActionThrPlay.setEnabled(true);
        
        tabMain.setSelectedComponent(pnLocalRoot);
    }
    
    /** 정착지 목록과 화면 내용 갱신 */
    @Override
    public void refreshColonyList() {
        cbxColony.setModel(new DefaultComboBoxModel<Colony>(colonies));
        int s = cbxColony.getSelectedIndex();
        if(s != selectedColony) time = null;
        selectedColony = s;
        refreshColonyContent();
    }
    
    /** 정착지 화면 내용 갱신 */
    @Override
    public void refreshColonyContent() {
    	int s = cbxColony.getSelectedIndex();
    	if(s != selectedColony) time = null;
        selectedColony = s;
        assureMainThreadRunning();
        refreshArenaPanel(0);
    }
    
    /** 사이클 진행에 따른 정착지 화면 내용 갱신 (성능을 위해 항상 전체를 새로고침하지는 않음. 확실히 새로고침하려면 refreshColonyContent 메소드 사용) */
    public void refreshArenaPanel(final int cycle) {
        // 전체 새로고침 여부 판단
        boolean refreshFull = false;
        
        Colony col = null;
        
        if(cycle == 0) { refreshFull = true; }
        else {
            col = getSelectedColony();
            if(col == null) { refreshFull = true; }
            else {
                DefaultColonyPanel colPn = (DefaultColonyPanel) getColonyPanel(col);
                if(cpNow == null || cpNow != colPn) {
                    refreshFull = true;
                }
            }
        }
        
        if(refreshFull) {
            cardLocalLoading2.show(pnLocalSecond, "C1S");
            if(col != null) col.markAsRefreshChildren(true);
            
            queueRefreshes.clear();
        }
        
        queueRefreshes.add(new RefreshRequest("refreshArenaPanelIn", cycle));
        handleRefreshRequests();
    }
    
    /** 화면 새로고침 요청 처리 */
    protected synchronized void handleRefreshRequests() {
    	SwingUtilities.invokeLater(new Runnable() {	
			@Override
			public void run() {
				while(! queueRefreshes.isEmpty()) {
					RefreshRequest r = queueRefreshes.poll();
					int cycle = r.getCycle();
					if(cycle == 0) cardLocalLoading2.show(pnLocalSecond, "C1S");
					refreshArenaPanelIn(cycle);
					if(cycle == 0) queueRefreshes.clear();
				}
			}
		});
    }
    
    /** 사이클 진행에 따른 정착지 화면 내용 갱신 */
    protected synchronized void refreshArenaPanelIn(int cycle) {
        Colony col = getSelectedColony();
        if(col == null) {
            cardLocalLoading2.show(pnLocalSecond, "C1S");
            pnCols.removeAll();
            pnCols.add(pnNoColonies, BorderLayout.CENTER);
            cardLocalLoading2.show(pnLocalSecond, "C1F");
            return;
        }
        
        DefaultColonyPanel colPn = (DefaultColonyPanel) getColonyPanel(col);
        if(colPn == null) {
            colPn = new DefaultColonyPanel(col, this);
            pnColonies.add(colPn);
        }
        
        if(cpNow == null || cpNow != colPn) {
            cardLocalLoading2.show(pnLocalSecond, "C1S");
            pnCols.removeAll();
            cpNow = colPn;
            if(cpNow != null) pnCols.add(colPn, BorderLayout.CENTER);
        }
        
        if(cycle == 0 || cycleSkipRefr <= 1 || cycle % cycleSkipRefr == 0) colPn.refresh(cycle, null, col, this);
        else colPn.refreshColonyBasicMeta(col, this);
        
        for(Mod mod : modsEnabled) {
        	if(mod.isReadOnly()) {
        		if(cycle % 4 == 0) mod.refresh(cycle, col.toJson(), broker); // 읽기 전용인 경우 JSON으로 변환해 반환
        	} else {
        		col.disableChecked(); // 쓰기 허용 MOD 사용 시 인증 비활성화
        	    mod.refresh(cycle, col, broker);
        	}
        }
        
        cardLocalLoading2.show(pnLocalSecond, "C1F");
    }
    
    /** 서블릿(웹) 패널 내 정착지 영역 새로고침 요청 */
    public void requestLoadServletColony() {
        if(servletClient != null) servletClient.requestLoadColony();
    }
    
    /** 해당 정착지를 출력하는 영역 반환 */
    @Override
    public ColonyPanel getColonyPanel(Colony col) {
        for(DefaultColonyPanel cp : pnColonies) {
            if(cp.getColony().getKey() == col.getKey()) {
                return cp;
            }
        }
        return null;
    }
    
    /** 해당 도시를 출력하는 도시 영역 반환 */
    public CityPanel getCityPanel(City city) {
        Colony col = getSelectedColony();
        DefaultColonyPanel colPn = (DefaultColonyPanel) getColonyPanel(col);
        if(colPn == null) return null;
        return colPn.getCityPanel(city);
    }
    
    /** 도시가 속한 정착지 찾기 */
    @Override
    public Colony getColonyFrom(City city) {
        for(Colony c : colonies) {
            for(City ct : c.getCities()) {
                if(ct.getKey() == city.getKey()) return c;
            }
        }
        return null;
    }
    
    @Override
    protected void applyModOnUI() {
    	menuMods.removeAll();
    	
    	for(final Mod mod : modsList) {
    		JMenuItem menuItem = new JMenuItem(t(mod.getName()));
    		menuMods.add(menuItem);
    		
    		final ModDialog dialog = new ModDialog(getDialog(), mod);
    		modDialogs.add(dialog);
    		
    		menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(! dialog.isOpenedOnce()) {
						mod.init(broker);
						if(! modsEnabled.contains(mod)) modsEnabled.add(mod);
					}
					dialog.open();
				}
			});
    	}
    }
}