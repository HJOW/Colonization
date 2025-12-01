package org.duckdns.hjow.colonization.ui.tools.files;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.duckdns.hjow.colonization.ColonyClassManager;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.ui.tools.AbstractTool;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.ClassUtil;
import org.duckdns.hjow.commons.util.DataUtil;

/** lib 파일들을 관리하는 대화상자 */
public class FileManager extends AbstractTool {
	protected transient boolean eventTabChanges = false;
	
	protected transient JTabbedPane tab;
	
	protected transient JSplitPane splitLibs;
	protected transient JList<JarFile> listLibs;
	protected transient JList<JarWeb>  listLibOnWeb;
	protected transient DefaultListModel<JarFile> modelLibs;
	protected transient DefaultListModel<JarWeb>  modelLibsOnWeb;
	
	protected transient Vector<JarFile> collectionLibs      = new Vector<JarFile>();
	protected transient Vector<JarWeb>  collectionLibsOnWeb = new Vector<JarWeb>();
	
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
	    
	    JPanel pnLibRoot = new JPanel();
	    pnLibRoot.setLayout(new BorderLayout());
	    tab.add(ColonyManager.t("Library"), pnLibRoot);
	    
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
	    
	    JPanel pnLibCtrlLeft, pnLibCtrlRight;
	    pnLibCtrlLeft  = new JPanel();
	    pnLibCtrlRight = new JPanel();
	    pnLibCtrlLeft.setLayout(new FlowLayout(FlowLayout.LEFT));
	    pnLibCtrlRight.setLayout(new FlowLayout(FlowLayout.RIGHT));
	    pnLibLeft.add(pnLibCtrlLeft, BorderLayout.NORTH);
	    pnLibRight.add(pnLibCtrlRight, BorderLayout.NORTH);
	    
	    modelLibs      = new DefaultListModel<JarFile>();
	    modelLibsOnWeb = new DefaultListModel<JarWeb>();
	    
	    listLibs     = new JList<JarFile>(modelLibs);
	    listLibOnWeb = new JList<JarWeb>(modelLibsOnWeb);
	    
	    listLibs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    listLibOnWeb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    
	    pnLibLeft.add(listLibs, BorderLayout.CENTER);
	    pnLibRight.add(listLibOnWeb, BorderLayout.CENTER);
	    
	    // TODO
	    
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
	                String condJava = libOne.get("java") == null ? null : libOne.get("java").toString().trim();
	                
	                // 자바 버전 조건
	                if(DataUtil.isNotEmpty(condJava)) {
	                	StringTokenizer waveTokenizer = new StringTokenizer(condJava, "~");
	                	String sFront = waveTokenizer.nextToken().trim();
	                	String sBack = null;
	                	if(waveTokenizer.hasMoreTokens()) sBack = waveTokenizer.nextToken().trim();
	                	
	                	int front = -1;
	                	int back  = -1;
	                	if(DataUtil.isNotEmpty(sFront)) front = Integer.parseInt(sFront);
	                	if(DataUtil.isNotEmpty(sBack )) back  = Integer.parseInt(sBack);
	                	
	                	int javaVer = ClassUtil.getJavaMajorVersion();
	                	if(front >= 1 && javaVer < front) continue;
	                	if(back  >= 1 && javaVer > back ) continue;
	                }
	                
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
				}
			});
		}
	}
}
