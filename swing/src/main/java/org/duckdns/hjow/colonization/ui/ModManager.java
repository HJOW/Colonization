package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.duckdns.hjow.colonization.ColonyClassLoader;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.mod.Mod;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.exception.KnownRuntimeException;
import org.duckdns.hjow.commons.util.DataUtil;
import org.duckdns.hjow.commons.util.GUIUtil;

/** Mods 관리 대화상자 */
public class ModManager implements Disposeable {
    protected JDialog dialog;
    protected ColonyManager manager;
    
    protected DefaultListModel<ModInfo> listModel;
    protected JList<ModInfo> list;
    
    
    public ModManager(ColonyManager manager) {
    	JFrame win = null;
    	
    	this.manager = manager;
    	if(manager instanceof GUIColonyManager) win = ((GUIColonyManager) manager).getDialog();
    	
    	if(win != null) dialog = new JDialog(win, true);
    	else            dialog = new JDialog();
    	
    	dialog.setTitle(ColonyManager.t("MOD Manager"));
    	dialog.setSize(500, 400);
    	dialog.setIconImage(GUIColonyManager.getIcon());
    	dialog.setLayout(new BorderLayout());
    	
        GUIUtil.centerWindow(dialog);
    	
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
    	
    	JPanel pnCtrl = new JPanel();
    	pnCtrl.setLayout(new FlowLayout(FlowLayout.LEFT));
    	pnUp.add(pnCtrl, BorderLayout.CENTER);
    	
    	JButton btnAdd, btnDel;
    	btnAdd = new JButton(ColonyManager.t("추가"));
    	btnDel = new JButton(ColonyManager.t("제거"));
    	
    	pnCtrl.add(btnAdd);
    	pnCtrl.add(btnDel);
    	
    	listModel = new DefaultListModel<ModInfo>();
    	list = new JList<ModInfo>(listModel);
    	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	pnCenter.add(new JScrollPane(list), BorderLayout.CENTER);
    	
    	btnAdd.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				onAddRequested();
			}
		});
    	
    	btnDel.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				onDelRequested();
			}
		});
    }
    
    public void open() {
    	dialog.setVisible(true);
    }
    
    public void close() {
    	if(dialog != null) dialog.setVisible(false);
    }
    
	@Override
	public void dispose() {
		close();
		manager = null;
		listModel.clear();
	}
	
	protected void refreshList() {
		listModel.removeAllElements();
		List<Mod> mods = manager.getMods();
		for(Mod mod : mods) {
			ModInfo info = new ModInfo(mod);
			listModel.addElement(info);
		}
		list.setModel(listModel);
	}
	
	protected void onAddRequested() {
		String req = JOptionPane.showInputDialog(dialog, ColonyManager.t("MOD 클래스명을 입력해 주세요. (공란 입력 시 취소)"));
		if(DataUtil.isEmpty(req)) return;
		req = req.trim();
		
		try {
			ColonyClassLoader.checkModClassName(req);
			if(! ((Class.forName(req).newInstance()) instanceof Mod)) throw new KnownRuntimeException(ColonyManager.t("입력하신 클래스는 MOD 클래스가 아닙니다."));
			
			List<Mod> mods = manager.getMods();
			for(Mod m : mods) {
				if(m.getClass().getName().equals(req)) throw new KnownRuntimeException(ColonyManager.t("이미 설정에 존재하는 MOD 입니다. MOD가 나타나지 않는다면 프로그램을 재실행해 주세요."));
			}
			
			final String modClass = req;
			
			SwingUtilities.invokeLater(new Runnable() {	
				@Override
				public void run() {
					manager.addMod(modClass);
					refreshList();
				}
			});
			
		} catch(ClassNotFoundException ex) {
			JOptionPane.showMessageDialog(dialog, ColonyManager.t("입력하신 클래스를 찾을 수 없습니다."));
		} catch(Throwable tx) {
			ColonyManager.logGlobals(ColonyManager.t("Error") + " : " + tx.getMessage(), 2);
			JOptionPane.showMessageDialog(dialog, ColonyManager.t("Error") + " : " + tx.getMessage());
			tx.printStackTrace();
		}
	}
	
    protected void onDelRequested() {
		final ModInfo sel = list.getSelectedValue();
		if(sel == null) {
			JOptionPane.showMessageDialog(dialog, ColonyManager.t("제거할 MOD를 선택 후 이용해 주세요."));
			return;
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					manager.removeMod(sel.getMod().getClass().getName());
					refreshList();
				} catch(Throwable tx) {
					ColonyManager.logGlobals(ColonyManager.t("Error") + " : " + tx.getMessage(), 2);
					JOptionPane.showMessageDialog(dialog, ColonyManager.t("Error") + " : " + tx.getMessage());
					tx.printStackTrace();
				}
			}
		});
	}
}

class ModInfo implements Serializable {
	private static final long serialVersionUID = -7305693297940080419L;
	protected Mod mod;
	protected String title;
	public ModInfo() {}
	public ModInfo(Mod mod) { this.mod = mod; title = mod.getName(); }
	@Override
	public String toString() {
		return title;
	}
	public Mod getMod() {
		return mod;
	}
	public void setMod(Mod mod) {
		this.mod = mod;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}