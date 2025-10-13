package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.duckdns.hjow.colonization.ColonyClassLoader;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.DataUtil;
import org.duckdns.hjow.commons.util.NetUtil;

public class ServletClientPanel extends JPanel implements Disposeable {
	private static final long serialVersionUID = -1977421402739294793L;
	
	protected transient GUIColonyManager superInstance;
	
	protected transient volatile boolean fThreadLogin = false;
	protected transient volatile Thread  threadLogin;
	
	protected transient volatile boolean fThreadJoin = false;
	protected transient volatile Thread  threadJoin;
	
	protected transient volatile boolean fThreadDetail = false;
	protected transient volatile Thread  threadDetail;
	
	protected transient volatile boolean fThreadMain = false;
	protected transient volatile boolean fThreadMainPaused = true;
	protected transient volatile Thread  threadMain;
	
	protected transient volatile boolean fCbxColony = false;
	
	protected transient volatile String  token, url, id;
	
	protected JPanel pnMain;
	protected CardLayout cardMain;
	
	protected JPanel pnCardLogin, pnCardJoin, pnCardGame, pnCardLoading, pnGameMain;
	
	protected Vector<ColonySimpleInfo> colonyList = new Vector<ColonySimpleInfo>();
	protected Colony colony;
	protected ColonyPanel pnColony;
	
	protected JTextField     tfLoginId, tfLoginUrl, tfJoinUrl, tfJoinId, tfJoinName;
	protected JPasswordField tfLoginPw, tfJoinPw;
	protected JButton btnLoginJoin, btnLoginRun, btnJoinCancel, btnJoinRun, btnThrPlay;
	protected JComboBox<ColonySimpleInfo> cbxColony;
	
    public ServletClientPanel() { super(); init(); }
    public ServletClientPanel(GUIColonyManager superInstance) { super(); this.superInstance = superInstance; init(); }
    
	@Override
	public void dispose() {
		fThreadLogin  = false;
		fThreadJoin   = false;
		fThreadDetail = false;
		fThreadMain   = false;
		fThreadMainPaused = true;
		superInstance = null;
	}
    
	/** UI 초기화 */
    protected void init() {
    	setLayout(new BorderLayout());
    	
    	pnMain = new JPanel();
    	cardMain = new CardLayout();
    	pnMain.setLayout(cardMain);
    	add(pnMain, BorderLayout.CENTER);
    	
    	pnCardLogin   = new JPanel();
    	pnCardJoin    = new JPanel();
    	pnCardGame    = new JPanel();
    	pnCardLoading = new JPanel();
    	
    	pnCardLogin.setLayout(new BorderLayout());  
    	pnCardJoin.setLayout(new BorderLayout());
    	pnCardGame.setLayout(new BorderLayout());
    	pnCardLoading.setLayout(new BorderLayout());
    	
    	pnMain.add(pnCardLogin  , "Login");
    	pnMain.add(pnCardJoin   , "Join");
    	pnMain.add(pnCardGame   , "Game");
    	pnMain.add(pnCardLoading, "Loading");
    	
    	cardMain.show(pnMain, "Login");
    	
    	JPanel pnLoginMain = new JPanel();
    	pnLoginMain.setLayout(new GridBagLayout());
    	pnCardLogin.add(pnLoginMain, BorderLayout.CENTER);
    	pnCardLogin.add(new JPanel(), BorderLayout.SOUTH);
    	pnCardLogin.add(new JPanel(), BorderLayout.NORTH);
    	pnCardLogin.add(new JPanel(), BorderLayout.EAST);
    	pnCardLogin.add(new JPanel(), BorderLayout.WEST);
    	
    	GridBagConstraints gridBagConst;
    	JLabel lb;
    	JPanel pn;
    	int rowNo = 0;
    	
    	gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 1;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 0.1;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
        gridBagConst.fill = GridBagConstraints.HORIZONTAL;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        pn = new JPanel();
        pn.setLayout(new FlowLayout(FlowLayout.RIGHT));
        lb = new JLabel(ColonyManager.t("URL"));
        pn.add(lb);
        pnLoginMain.add(pn, gridBagConst);
        
        
        
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
        tfLoginUrl = new JTextField();
        pn.add(tfLoginUrl, BorderLayout.CENTER);
        pnLoginMain.add(pn, gridBagConst);
        
        rowNo++;
    	
    	
    	gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 1;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 0.1;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
        gridBagConst.fill = GridBagConstraints.HORIZONTAL;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        pn = new JPanel();
        pn.setLayout(new FlowLayout(FlowLayout.RIGHT));
        lb = new JLabel(ColonyManager.t("ID"));
        pn.add(lb);
        pnLoginMain.add(pn, gridBagConst);
        
        
        
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
        tfLoginId = new JTextField();
        pn.add(tfLoginId, BorderLayout.CENTER);
        pnLoginMain.add(pn, gridBagConst);
        
        rowNo++;
        
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 1;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 0.1;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
        gridBagConst.fill = GridBagConstraints.HORIZONTAL;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        pn = new JPanel();
        pn.setLayout(new FlowLayout(FlowLayout.RIGHT));
        lb = new JLabel(ColonyManager.t("Password"));
        pn.add(lb);
        pnLoginMain.add(pn, gridBagConst);
        
        
        
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
        tfLoginPw = new JPasswordField();
        pn.add(tfLoginPw, BorderLayout.CENTER);
        pnLoginMain.add(pn, gridBagConst);
        
        rowNo++;
        
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 10;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 1.0;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
        gridBagConst.fill = GridBagConstraints.HORIZONTAL;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        pn = new JPanel();
        pn.setLayout(new FlowLayout(FlowLayout.CENTER));
        pnLoginMain.add(pn, gridBagConst);
        
        btnLoginJoin = new JButton(ColonyManager.t("가입"));
        btnLoginRun  = new JButton(ColonyManager.t("접속"));
        pn.add(btnLoginJoin);
        pn.add(btnLoginRun);
        
        btnLoginJoin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onLoginJoinRequested();
			}
		});
        
        btnLoginRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onLoginRunRequested();
			}
		});
        
        
        JPanel pnJoinMain = new JPanel();
        pnJoinMain.setLayout(new GridBagLayout());
    	pnCardJoin.add(pnJoinMain, BorderLayout.CENTER);
    	pnCardJoin.add(new JPanel(), BorderLayout.SOUTH);
    	pnCardJoin.add(new JPanel(), BorderLayout.NORTH);
    	pnCardJoin.add(new JPanel(), BorderLayout.EAST);
    	pnCardJoin.add(new JPanel(), BorderLayout.WEST);
        rowNo = 0;
        
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 1;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 0.1;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
        gridBagConst.fill = GridBagConstraints.HORIZONTAL;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        pn = new JPanel();
        pn.setLayout(new FlowLayout(FlowLayout.RIGHT));
        lb = new JLabel(ColonyManager.t("URL"));
        pn.add(lb);
        pnJoinMain.add(pn, gridBagConst);
        
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
        tfJoinUrl = new JTextField();
        pn.add(tfJoinUrl, BorderLayout.CENTER);
        pnJoinMain.add(pn, gridBagConst);
        
        rowNo++;
        
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 1;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 0.1;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
        gridBagConst.fill = GridBagConstraints.HORIZONTAL;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        pn = new JPanel();
        pn.setLayout(new FlowLayout(FlowLayout.RIGHT));
        lb = new JLabel(ColonyManager.t("ID"));
        pn.add(lb);
        pnJoinMain.add(pn, gridBagConst);
        
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
        tfJoinId = new JTextField();
        pn.add(tfJoinId, BorderLayout.CENTER);
        pnJoinMain.add(pn, gridBagConst);
        
        rowNo++;
        
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 1;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 0.1;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
        gridBagConst.fill = GridBagConstraints.HORIZONTAL;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        pn = new JPanel();
        pn.setLayout(new FlowLayout(FlowLayout.RIGHT));
        lb = new JLabel(ColonyManager.t("Password"));
        pn.add(lb);
        pnJoinMain.add(pn, gridBagConst);
        
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
        tfJoinPw = new JPasswordField();
        pn.add(tfJoinPw, BorderLayout.CENTER);
        pnJoinMain.add(pn, gridBagConst);
        
        rowNo++;
        
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 1;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 0.1;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
        gridBagConst.fill = GridBagConstraints.HORIZONTAL;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        pn = new JPanel();
        pn.setLayout(new FlowLayout(FlowLayout.RIGHT));
        lb = new JLabel(ColonyManager.t("이름"));
        pn.add(lb);
        pnJoinMain.add(pn, gridBagConst);
        
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
        tfJoinName = new JTextField();
        pn.add(tfJoinName, BorderLayout.CENTER);
        pnJoinMain.add(pn, gridBagConst);
        
        rowNo++;
        
    	
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo;
        gridBagConst.gridwidth = 10;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 1.0;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
        gridBagConst.fill = GridBagConstraints.HORIZONTAL;
        gridBagConst.anchor = GridBagConstraints.NORTH;
        
        pn = new JPanel();
        pn.setLayout(new FlowLayout(FlowLayout.CENTER));
        pnJoinMain.add(pn, gridBagConst);
        
        
        btnJoinCancel = new JButton(ColonyManager.t("취소"));
        btnJoinRun    = new JButton(ColonyManager.t("가입"));
        pn.add(btnJoinCancel);
        pn.add(btnJoinRun);
        
        btnJoinCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onJoinCancelRequested();
			}
		});
        
        btnJoinRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onJoinRunRequested();
			}
		});
        
        
        JPanel pnLoadingMain = new JPanel();
        pnLoadingMain.setLayout(new FlowLayout(FlowLayout.CENTER));
    	pnCardLoading.add(pnLoadingMain, BorderLayout.CENTER);
    	pnCardLoading.add(new JPanel(), BorderLayout.SOUTH);
    	pnCardLoading.add(new JPanel(), BorderLayout.NORTH);
    	pnCardLoading.add(new JPanel(), BorderLayout.EAST);
    	pnCardLoading.add(new JPanel(), BorderLayout.WEST);
        
        JProgressBar prog = new JProgressBar();
        prog.setIndeterminate(true);
        pnLoadingMain.add(prog);
        
        JToolBar toolbar = new JToolBar();
        pnCardGame.add(toolbar, BorderLayout.NORTH);
        
        cbxColony = new JComboBox<ColonySimpleInfo>();
        toolbar.add(cbxColony);
        
        cbxColony.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(! fCbxColony) return;
				onLoadColonyRequested(((ColonySimpleInfo) cbxColony.getSelectedItem()).getKey());
			}
		});
        
        btnThrPlay = new JButton(ColonyManager.t("시뮬레이션 실행"));
        toolbar.add(btnThrPlay);
        
        btnThrPlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleSimulationRunning();
			}
		});
        
        pnGameMain = new JPanel();
        pnGameMain.setLayout(new BorderLayout());
        pnCardGame.add(pnGameMain, BorderLayout.CENTER);
        
        fThreadMain = true;
        fThreadMainPaused = true;
        threadMain = new Thread(new Runnable() {
			@Override
			public void run() {
				onMainThread();
			}
		});
        threadMain.start();
    }
    
    /** 가입 요청 시 호출 */
    protected void onLoginJoinRequested() {
    	cardMain.show(pnMain, "Join");
    }
    
    /** 로그인 접속 요청 시 호출 */
    protected void onLoginRunRequested() {
    	cardMain.show(pnMain, "Loading");
    	
    	if(threadLogin != null) {
    		fThreadLogin = false;
    		try { threadLogin.interrupt(); } catch(SecurityException    ex) {}
    		try { Thread.sleep(1000L);     } catch(InterruptedException ex) {}
    	}
    	
    	threadLogin = new Thread(new Runnable() {
			@Override
			public void run() {
				fThreadLogin = true;
				onLoginThread();
			}
		});
    	threadLogin.start();
    }
    
    /** 로그인 처리 핵심 메소드 */
    protected void onLoginThread() {
    	try {
    		this.url = tfLoginUrl.getText();
    		URL urls = new URL(this.url);
    		Map<String, Object> parameters = new HashMap<String, Object>();
    		parameters.put("svName", "login");
    		parameters.put("svSub" , "login");
    		
    		this.id = tfLoginId.getText();
    		parameters.put("id" , this.id);
    		
    		char[] charPw = tfLoginPw.getPassword();
    		String strPw  = new String(charPw);
    		parameters.put("pw" , strPw);
    		    	
    		String responseString = NetUtil.sendPost(urls, parameters, "application/json", "UTF-8");
    		JsonObject responseJson = (JsonObject) JsonObject.parseJson(responseString.trim());
    		
    		if(! fThreadLogin) return;
    		
    		boolean success = DataUtil.parseBoolean(responseJson.get("success").toString().trim());
    		if(! success) throw new RuntimeException(responseJson.get("message").toString().trim());
    		
    		token = responseJson.get("token").toString().trim();
    		colonyList.clear();
    		
    		JsonArray arr = (JsonArray) responseJson.get("list");
    		for(Object obj : arr) {
    			JsonObject rowOne = (JsonObject) obj;
    		    ColonySimpleInfo infoOne = new ColonySimpleInfo(rowOne.get("name").toString(), Long.parseLong(rowOne.get("key").toString()));
    		    colonyList.add(infoOne);
    		}
    		
    		SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					fCbxColony = false;
					cbxColony.removeAllItems();
					for(ColonySimpleInfo infos : colonyList) {
						cbxColony.addItem(infos);
					}
					cbxColony.setSelectedIndex(0);
					
					onLoadColonyRequested(((ColonySimpleInfo) cbxColony.getSelectedItem()).getKey());
					fCbxColony = true;
				}
			});
    	} catch(RuntimeException ex) {
    		JOptionPane.showMessageDialog(superInstance.getDialog(), ColonyManager.t(ex.getMessage()));
    		token = null;
    		url = null;
    		id = null;
    		colonyList.clear();
    		fThreadLogin  = false;
    		fThreadJoin   = false;
    		fThreadDetail = false;
    		fThreadMainPaused = true;
    		cardMain.show(pnMain, "Login");
    	} catch(Throwable t) {
    		JOptionPane.showMessageDialog(superInstance.getDialog(), ColonyManager.t("오류") + " : " + ColonyManager.t(t.getMessage()));
    		GlobalLogs.processExceptionOccured(t, true);
    		token = null;
    		url = null;
    		id = null;
    		colonyList.clear();
    		fThreadLogin  = false;
    		fThreadJoin   = false;
    		fThreadDetail = false;
    		fThreadMainPaused = true;
    		cardMain.show(pnMain, "Login");
    	} finally {
    		fThreadLogin = false;
    	}
    }
    
    /** 가입 취소 요청 시 호출 */
    protected void onJoinCancelRequested() {
    	cardMain.show(pnMain, "Login");
    }
    
    /** 가입 실행 요청 시 호출 */
    protected void onJoinRunRequested() {
    	cardMain.show(pnMain, "Loading");
    	
    	if(threadJoin != null) {
    		fThreadJoin = false;
    		try { threadJoin.interrupt(); } catch(SecurityException    ex) {}
    		try { Thread.sleep(1000L);    } catch(InterruptedException ex) {}
    	}
    	
    	threadJoin = new Thread(new Runnable() {
			@Override
			public void run() {
				fThreadJoin = true;
				onJoinThread();
			}
		});
    	threadJoin.start();
    }
    
    /** 가입 처리 핵심 메소드 */
    protected void onJoinThread() {
    	try {
    		this.url = tfJoinUrl.getText();
    		URL urls = new URL(this.url);
    		Map<String, Object> parameters = new HashMap<String, Object>();
    		parameters.put("svName", "login");
    		parameters.put("svSub" , "join");
    		
    		this.id = tfJoinId.getText();
    		parameters.put("id" , this.id);
    		
    		char[] charPw = tfJoinPw.getPassword();
    		String strPw  = new String(charPw);
    		parameters.put("pw" , strPw);
    		
    		String nm = tfJoinName.getText();
    		parameters.put("name" , nm);
    		    	
    		String responseString = NetUtil.sendPost(urls, parameters, "application/json", "UTF-8");
    		JsonObject responseJson = (JsonObject) JsonObject.parseJson(responseString.trim());
    		
    		if(! fThreadJoin) return;
    		
    		boolean success = DataUtil.parseBoolean(responseJson.get("success").toString().trim());
    		if(! success) throw new RuntimeException(responseJson.get("message").toString().trim());
    		
    		SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					cardMain.show(pnMain, "Login");
					JOptionPane.showMessageDialog(superInstance.getDialog(), ColonyManager.t("가입이 완료되었습니다."));
				}
			});
    	} catch(RuntimeException ex) {
    		JOptionPane.showMessageDialog(superInstance.getDialog(), ColonyManager.t(ex.getMessage()));
    		token = null;
    		url = null;
    		id = null;
    		colonyList.clear();
    		fThreadLogin  = false;
    		fThreadJoin   = false;
    		fThreadDetail = false;
    		fThreadMainPaused = true;
    		cardMain.show(pnMain, "Login");
    	} catch(Throwable t) {
    		JOptionPane.showMessageDialog(superInstance.getDialog(), ColonyManager.t("오류") + " : " + ColonyManager.t(t.getMessage()));
    		GlobalLogs.processExceptionOccured(t, true);
    		token = null;
    		url = null;
    		id = null;
    		colonyList.clear();
    		fThreadLogin  = false;
    		fThreadJoin   = false;
    		fThreadDetail = false;
    		fThreadMainPaused = true;
    		cardMain.show(pnMain, "Login");
    	} finally {
    		fThreadJoin = false;
    	}
    }
    
    /** 정착지 로드 요청 */
    protected void onLoadColonyRequested(final long colonyKey) {
    	cardMain.show(pnMain, "Loading");
    	pnGameMain.removeAll();
    	
    	if(threadDetail != null) {
    		fThreadDetail = false;
    		try { threadDetail.interrupt(); } catch(SecurityException    ex) {}
    		try { Thread.sleep(1000L);      } catch(InterruptedException ex) {}
    	}
    	
    	threadDetail = new Thread(new Runnable() {
			@Override
			public void run() {
				fThreadDetail = true;
				onDetailThread(colonyKey);
			}
		});
    	threadDetail.start();
    }
    
    /** 정착지 로드 핵심 메소드 */
    protected void onDetailThread(final long colonyKey) {
    	try {
    		fCbxColony = false;
    		
    		URL urls = new URL(this.url);
    		Map<String, Object> parameters = new HashMap<String, Object>();
    		parameters.put("svName", "colony");
    		parameters.put("svSub" , "detail");
    		parameters.put("jwt"   , this.token);
    		
    		ColonySimpleInfo sels = (ColonySimpleInfo) cbxColony.getSelectedItem();
    		parameters.put("key", String.valueOf(sels.getKey()));
    		
    		String responseString = NetUtil.sendPost(urls, parameters, "application/json", "UTF-8");
    		JsonObject responseJson = (JsonObject) JsonObject.parseJson(responseString.trim());
    		
    		if(! fThreadDetail) return;
    		
    		boolean success = DataUtil.parseBoolean(responseJson.get("success").toString().trim());
    		if(! success) throw new RuntimeException(responseJson.get("message").toString().trim());
    		
    		colony = ColonyClassLoader.loadColony((JsonObject) responseJson.get("detail"));
    		
    		SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					ColonySimpleInfo infoNow = null;
					for(ColonySimpleInfo infoOne : colonyList) {
						if(colony.getKey() == infoOne.getKey()) {
							infoNow = infoOne;
							break;
						}
					}
					
					if(infoNow != null) cbxColony.setSelectedItem(infoNow);
				    fCbxColony = true;
				    
				    pnColony = new ServletClientColonyPanel(colony, superInstance);
				    pnGameMain.add((Component) pnColony.getComponent(), BorderLayout.CENTER);
				    
				    cardMain.show(pnMain, "Game");
				}
			});
    	} catch(RuntimeException ex) {
    		JOptionPane.showMessageDialog(superInstance.getDialog(), ColonyManager.t(ex.getMessage()));
    		token = null;
    		fThreadLogin  = false;
    		fThreadJoin   = false;
    		fThreadDetail = false;
    		fThreadMainPaused = true;
    		cardMain.show(pnMain, "Login");
    	} catch(Throwable t) {
    		JOptionPane.showMessageDialog(superInstance.getDialog(), ColonyManager.t("오류") + " : " + ColonyManager.t(t.getMessage()));
    		GlobalLogs.processExceptionOccured(t, true);
    		token = null;
    		fThreadLogin  = false;
    		fThreadJoin   = false;
    		fThreadDetail = false;
    		fThreadMainPaused = true;
    		cardMain.show(pnMain, "Login");
    	} finally {
    		fThreadDetail = false;
    	}
    }
    
    /** 시뮬레이션 시작/정지 토글 */
    public void toggleSimulationRunning() {
        boolean resv = (! fThreadMainPaused);
        fCbxColony = false;
        
        if(resv) {
            pauseSimulation();
        } else {
            resumeSimulation();
        }
        
        // 그 사이에 콤보박스 바꾼 경우를 대비
        ColonySimpleInfo infoNow = null;
		for(ColonySimpleInfo infoOne : colonyList) {
			if(colony.getKey() == infoOne.getKey()) {
				infoNow = infoOne;
				break;
			}
		}
		
		if(infoNow != null) cbxColony.setSelectedItem(infoNow);
		if(fThreadMainPaused) { cbxColony.setEnabled(true);  fCbxColony = true;  }
		else                  { cbxColony.setEnabled(false); fCbxColony = false; }
    }
    
    /** 시뮬레이션 정지 */
    protected void pauseSimulation() {
    	fThreadMainPaused = true;
    	fCbxColony = false;
    	btnThrPlay.setEnabled(false);
    	pnColony.setEditable(true);
    	btnThrPlay.setText(ColonyManager.t("시뮬레이션 시작"));
    	btnThrPlay.setEnabled(true);
    }
    
    /** 시뮬레이션 재개 */
    protected void resumeSimulation() {
    	fThreadMainPaused = false;
    	fCbxColony = false;
    	btnThrPlay.setEnabled(false);
    	pnColony.setEditable(false);
    	btnThrPlay.setText(ColonyManager.t("시뮬레이션 정지"));
    	btnThrPlay.setEnabled(true);
    }
    
    /** 메인 쓰레드 */
    protected void onMainThread() {
    	while(fThreadMain) {
    		if(! fThreadMainPaused) {
    			try { onMainThreadOneCycle(); } catch(Throwable t) {
    				GlobalLogs.processExceptionOccured(t, true); 
    				token = null;
    	    		fThreadLogin  = false;
    	    		fThreadJoin   = false;
    	    		fThreadDetail = false;
    	    		fThreadMainPaused = true;
    	    		cardMain.show(pnMain, "Login");
    	    		JOptionPane.showMessageDialog(superInstance.getDialog(), ColonyManager.t("오류") + " : " + ColonyManager.t(t.getMessage()));
    			}
    		}
    		
    		try { Thread.sleep(100L); } catch(InterruptedException ex) { fThreadMain = false; break; }
    	}
    	fThreadMain = false;
    }
    
    /** 메인 쓰레드 내 핵심 동작 */
    protected void onMainThreadOneCycle() throws Throwable {
    	URL urls = new URL(this.url);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("svName", "colony");
		parameters.put("svSub" , "cycle");
		parameters.put("jwt"   , this.token);
		
		fCbxColony = false;
		ColonySimpleInfo sels = (ColonySimpleInfo) cbxColony.getSelectedItem();
		parameters.put("key", String.valueOf(sels.getKey()));
		parameters.put("cycle", "1");
		    	
		String responseString = NetUtil.sendPost(urls, parameters, "application/json", "UTF-8");
		JsonObject responseJson = (JsonObject) JsonObject.parseJson(responseString.trim());
		
		boolean success = DataUtil.parseBoolean(responseJson.get("success").toString().trim());
		if(! success) throw new RuntimeException(responseJson.get("message").toString().trim());
		
		colony = ColonyClassLoader.loadColony((JsonObject) responseJson.get("detail"));
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ColonySimpleInfo infoNow = null;
				for(ColonySimpleInfo infoOne : colonyList) {
					if(colony.getKey() == infoOne.getKey()) {
						infoNow = infoOne;
						break;
					}
				}
				
				if(infoNow != null) cbxColony.setSelectedItem(infoNow);
			    fCbxColony = true;
			    
			    DefaultColonyPanel pn = new DefaultColonyPanel(colony, superInstance); // TODO 서블릿 클라이언트용 Colony Panel 구현해야 함 (도시, 시설 건설 시 서버로 요청을 보내야 하기 때문)
			    pnGameMain.add(pn, BorderLayout.CENTER);
			    
			    cardMain.show(pnMain, "Game");
			}
		});
    }
}

/** 정착지 정보 */
class ColonySimpleInfo implements Serializable {
	private static final long serialVersionUID = 5957048445693707208L;
	protected String name;
	protected long   key;
	public ColonySimpleInfo() {}
	public ColonySimpleInfo(String name, long key) {
		this();
		this.name = name;
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getKey() {
		return key;
	}
	public void setKey(long key) {
		this.key = key;
	}
}