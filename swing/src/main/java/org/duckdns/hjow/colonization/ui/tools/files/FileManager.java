package org.duckdns.hjow.colonization.ui.tools.files;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.duckdns.hjow.colonization.ColonyClassManager;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.console.PreWorks;
import org.duckdns.hjow.colonization.ui.tools.AbstractTool;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.NetUtil;

/** lib 파일들을 관리하는 대화상자 */
public class FileManager extends AbstractTool {
	protected transient boolean eventTabChanges = false;
	
	protected transient JTabbedPane tab;
	protected transient JProgressBar prog;
	
	protected transient JSplitPane splitLibs;
	protected transient JList<JarFile> listLibs;
	protected transient JList<JarWeb>  listLibOnWeb;
	protected transient DefaultListModel<JarFile> modelLibs;
	protected transient DefaultListModel<JarWeb>  modelLibsOnWeb;
	
	protected transient JButton btnRemove, btnRefresh, btnDownload, btnDownloadAll;
	
	protected transient Vector<JarFile> collectionLibs      = new Vector<JarFile>();
	protected transient Vector<JarWeb>  collectionLibsOnWeb = new Vector<JarWeb>();
	
	public FileManager() {
		super();
	}
    public FileManager(Window win) {
    	super(win);
    }

	@Override
	public String getName() {
		return "FILEMANAGER";
	}

	@Override
	public String getTitle() {
		return "파일 관리자";
	}

	@Override
	protected void init() {
	    tab = new JTabbedPane();
	    pnCenter.add(tab, BorderLayout.CENTER);
	    
	    tab.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(eventTabChanges) refresh();
			}
		});
	    
	    prog = new JProgressBar();
	    pnCenter.add(prog, BorderLayout.NORTH);
	    
	    JPanel pnLb = new JPanel();
	    pnLb.setLayout(new FlowLayout());
	    JLabel lb = new JLabel(ColonyManager.t("다운로드된 lib 파일은 다음번 실행에 적용됩니다."));
	    pnLb.add(lb);
	    pnCenter.add(pnLb, BorderLayout.SOUTH);
	    
	    JPanel pnLibRoot = new JPanel();
	    pnLibRoot.setLayout(new BorderLayout());
	    tab.add(ColonyManager.t("라이브러리 파일 관리"), pnLibRoot);
	    
	    JPanel pnLibCenter = new JPanel();
	    pnLibCenter.setLayout(new BorderLayout());
	    pnLibRoot.add(pnLibCenter, BorderLayout.CENTER);
	    
	    splitLibs = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	    pnLibCenter.add(splitLibs, BorderLayout.CENTER);
	    
	    JPanel pnLibLeft, pnLibRight;
	    pnLibLeft  = new JPanel();
	    pnLibRight = new JPanel();
	    pnLibLeft.setLayout(new BorderLayout());
	    pnLibRight.setLayout(new BorderLayout());
	    splitLibs.setLeftComponent(pnLibLeft);
	    splitLibs.setRightComponent(pnLibRight);
	    
	    modelLibs      = new DefaultListModel<JarFile>();
	    modelLibsOnWeb = new DefaultListModel<JarWeb>();
	    
	    listLibs     = new JList<JarFile>(modelLibs);
	    listLibOnWeb = new JList<JarWeb>(modelLibsOnWeb);
	    
	    listLibs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    listLibOnWeb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    
	    pnLibLeft.add(listLibs, BorderLayout.CENTER);
	    pnLibRight.add(listLibOnWeb, BorderLayout.CENTER);
	    
	    JPanel pnLibCtrlLeft, pnLibCtrlRight, pnLibCtrlLeftIn, pnLibCtrlRightIn;
	    
	    pnLibCtrlLeft  = new JPanel();
	    pnLibCtrlRight = new JPanel();
	    pnLibCtrlLeft.setLayout(new BorderLayout());
	    pnLibCtrlRight.setLayout(new BorderLayout());
	    pnLibLeft.add(pnLibCtrlLeft, BorderLayout.NORTH);
	    pnLibRight.add(pnLibCtrlRight, BorderLayout.NORTH);
	    
	    pnLibCtrlLeftIn  = new JPanel();
	    pnLibCtrlRightIn = new JPanel();
	    pnLibCtrlLeftIn.setLayout(new FlowLayout(FlowLayout.LEFT));
	    pnLibCtrlRightIn.setLayout(new FlowLayout(FlowLayout.RIGHT));
	    pnLibCtrlLeft.add(pnLibCtrlLeftIn, BorderLayout.CENTER);
	    pnLibCtrlRight.add(pnLibCtrlRightIn, BorderLayout.CENTER);
	    
	    pnLb = new JPanel();
	    pnLb.setLayout(new FlowLayout(FlowLayout.CENTER));
	    lb = new JLabel(ColonyManager.t("설치된 파일"));
	    pnLb.add(lb);
	    pnLibCtrlLeft.add(pnLb, BorderLayout.NORTH);
	    
	    pnLb = new JPanel();
	    pnLb.setLayout(new FlowLayout(FlowLayout.CENTER));
	    lb = new JLabel(ColonyManager.t("다운로드 가능한 파일"));
	    pnLb.add(lb);
	    pnLibCtrlRight.add(pnLb, BorderLayout.NORTH);
	    
	    btnRefresh = new JButton(ColonyManager.t("새로고침"));
	    pnLibCtrlLeftIn.add(btnRefresh);
	    btnRefresh.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
	    
	    btnRemove = new JButton(ColonyManager.t("삭제"));
	    pnLibCtrlLeftIn.add(btnRemove);
	    btnRemove.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				JarFile j = listLibs.getSelectedValue();
				if(j == null) { JOptionPane.showMessageDialog(dialog, ColonyManager.t("삭제할 항목을 먼저 선택해 주세요.")); return; }
				
				if(j.getFile() == null) { refresh(); return; }
				if(! j.getFile().exists()) { refresh(); return; }
				
				prog.setIndeterminate(true);
				try { j.getFile().delete(); } catch(Exception ex) { JOptionPane.showMessageDialog(dialog, ColonyManager.t("파일 삭제에 실패하였습니다.") + "\n" + ColonyManager.t("오류") + " : " + ex.getMessage()); }
				refresh();
			}
		});
	    
	    btnDownload = new JButton(ColonyManager.t("다운로드"));
	    pnLibCtrlRightIn.add(btnDownload);
	    btnDownload.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				JarWeb w = listLibOnWeb.getSelectedValue();
				if(w == null) { JOptionPane.showMessageDialog(dialog, ColonyManager.t("다운로드할 항목을 먼저 선택해 주세요.")); return; }
				
				prog.setIndeterminate(true);
				btnRefresh.setEnabled(false);
		    	btnDownload.setEnabled(false);
		    	btnDownloadAll.setEnabled(false);
		    	btnRemove.setEnabled(false);
				onDownloadRequested();				
			}
		});
	    
	    btnDownloadAll = new JButton(ColonyManager.t("모두 다운로드"));
	    pnLibCtrlRightIn.add(btnDownloadAll);
	    btnDownloadAll.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				prog.setIndeterminate(true);
				btnRefresh.setEnabled(false);
		    	btnDownload.setEnabled(false);
		    	btnDownloadAll.setEnabled(false);
		    	btnRemove.setEnabled(false);
				onDownloadAllRequested();				
			}
		});
	}
	
	@Override
    public void open() {
		refresh();
		eventTabChanges = true;
        dialog.setVisible(true);
        splitLibs.setDividerLocation(0.5);
    }
	
	protected int getDialogProperWidth( ) { return 750; }
    protected int getDialogProperHeight() { return 600; }
    
    /** 새로 고침 */
    public void refresh() {
    	btnRefresh.setEnabled(false);
    	btnDownload.setEnabled(false);
    	btnDownloadAll.setEnabled(false);
    	btnRemove.setEnabled(false);
    	prog.setIndeterminate(true);
    	
    	new Thread(new Runnable() {	
			@Override
			public void run() {
				refreshIn();
			}
		}).start();
    }
	
	/** 새로 고침 */
	protected synchronized void refreshIn() {
		try {
			// lib 폴더 액세스
			File libRoot = ColonyClassManager.getHomeLibDir();
	        if(! libRoot.exists()) libRoot.mkdirs();
			
			File[] lists = libRoot.listFiles(new FileFilter() {	
				@Override
				public boolean accept(File pathname) {
					if(pathname.isDirectory()) return false;
					String name = pathname.getName().toLowerCase();
					return (name.endsWith(".jar") || name.endsWith(".class") || name.endsWith(".dll") || name.endsWith(".so"));
				}
			});
			
			collectionLibs.clear();
			for(File f : lists) {
				if(! f.exists()) continue;
				collectionLibs.add(new JarFile(f));
			}
			
			// 웹 액세스
			JsonObject jsonConfig = ColonyClassManager.getWebConfigRoot();
			JsonObject jsonConfigSwing = (JsonObject) jsonConfig.get("swing");
			JsonArray libs = (JsonArray) jsonConfigSwing.get("libs");
			
			collectionLibsOnWeb.clear();
			for(Object obj : libs) {
	            try {
	                JsonObject libOne = (JsonObject) obj;
	                String libUrl   = libOne.get("url").toString();
	                String libName  = libOne.get("name").toString();
	                
	                // 조건 확인
	                if(! PreWorks.isAcceptLib(libOne)) continue;
	                
	                // 상대경로 여부 확인
	                if(! libUrl.startsWith("http")) libUrl = ColonyClassManager.htmlRootUrl() + libUrl;
	                
	                // 해당 lib 미존재 시 리스트에 표시
	                File file = new File(libRoot.getAbsolutePath() + File.separator + libName);
	                if(! file.exists()) {
	                	JarWeb w = new JarWeb(new URL(libUrl), file);
	                	collectionLibsOnWeb.add(w);
	                }
	            } catch(Exception ex) {
	            	SwingUtilities.invokeLater(new Runnable() {	
	    				@Override
	    				public void run() {
	    					GlobalLogs.processExceptionOccured(ex, true);
	    				}
	    			});
	            }
	        }
		} catch(final Throwable tx) {
			SwingUtilities.invokeLater(new Runnable() {	
				@Override
				public void run() {
					GlobalLogs.processExceptionOccured(tx, true);
				    JOptionPane.showMessageDialog(dialog, ColonyManager.t("오류") + " : " + tx.getMessage());
				    dialog.setVisible(false);
				}
			});
		} finally {
			SwingUtilities.invokeLater(new Runnable() {	
				@Override
				public synchronized void run() {
					modelLibs.removeAllElements();
					for(JarFile f : collectionLibs) {
						modelLibs.addElement(f);
					}
					
					modelLibsOnWeb.removeAllElements();
					for(JarWeb w : collectionLibsOnWeb) {
						modelLibsOnWeb.addElement(w);
					}
					
					collectionLibs.clear();
					collectionLibsOnWeb.clear();
					
					btnRefresh.setEnabled(true);
			    	btnDownload.setEnabled(true);
			    	btnDownloadAll.setEnabled(true);
			    	btnRemove.setEnabled(true);
			    	prog.setIndeterminate(false);
				}
			});
		}
	}
	
	/** 다운로드 요청 */
    protected void onDownloadRequested() {
    	final JarWeb w = listLibOnWeb.getSelectedValue();
    	
    	btnRefresh.setEnabled(false);
    	btnDownload.setEnabled(false);
    	btnDownloadAll.setEnabled(false);
    	btnRemove.setEnabled(false);
    	prog.setIndeterminate(true);
    	
    	new Thread(new Runnable() {	
			@Override
			public void run() {
				processDownload(w, true);
			}
		}).start();
    }
    
    /** 다운로드 처리 */
    protected void processDownload(JarWeb web, boolean refreshAfter) {
    	try {
    		NetUtil.download(web.getUrl(), web.getFile());
    		if(refreshAfter) refresh();
    	} catch(final Throwable tx) {
			SwingUtilities.invokeLater(new Runnable() {	
				@Override
				public void run() {
					GlobalLogs.processExceptionOccured(tx, true);
				    JOptionPane.showMessageDialog(dialog, ColonyManager.t("오류") + " : " + tx.getMessage());
				    btnRefresh.setEnabled(true);
				    prog.setIndeterminate(false);
				}
			});
		}
    }
    
    /** 전체 다운로드 요청 */
    protected void onDownloadAllRequested() {
    	btnRefresh.setEnabled(false);
    	btnDownload.setEnabled(false);
    	btnDownloadAll.setEnabled(false);
    	btnRemove.setEnabled(false);
    	prog.setIndeterminate(true);
    	
    	collectionLibsOnWeb.clear();
    	for(int idx=0; idx<modelLibsOnWeb.getSize(); idx++) {
    		collectionLibsOnWeb.add(modelLibsOnWeb.getElementAt(idx));
    	}
    	
    	for(JarWeb w : collectionLibsOnWeb) {
    		modelLibsOnWeb.removeElement(w);
    	}
    	
    	new Thread(new Runnable() {	
			@Override
			public void run() {
				for(JarWeb w : collectionLibsOnWeb) {
				    processDownload(w, false);
				}
				refresh();
			}
		}).start();
    }
}
