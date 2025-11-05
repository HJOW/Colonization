package org.duckdns.hjow.colonization.ui;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.util.GUIUtil;
import org.duckdns.hjow.colonization.ColonyClassLoader;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.ColonyInformation;

public class NewColonyManager implements Disposeable {
    protected GUIColonyManager man;
    protected JDialog dialog;
    protected JComboBox<ColonyInformation> cbxColTypes;
    protected JComboBox<String> cbxDifficulty;
    protected JTextField tfName;
    protected JTextArea ta;
    
    public NewColonyManager(GUIColonyManager man) {
        this.man = man;
        dialog = new JDialog(man.getDialog(), true);
        dialog.setSize(400, 300);
        GUIUtil.centerWindow(dialog);
        dialog.setTitle(ColonyManager.t("새 정착지 개척"));
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dialog = null;
                dispose();
            }
        });
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
        
        tfName = new JTextField();
        pnUp.add(tfName, BorderLayout.NORTH);
        
        Vector<ColonyInformation> listColTypes = new Vector<ColonyInformation>();
        listColTypes.addAll(ColonyClassLoader.colonyInfos());
        cbxColTypes = new JComboBox<ColonyInformation>(listColTypes);
        pnUp.add(cbxColTypes, BorderLayout.CENTER);
        
        cbxColTypes.addItemListener(new ItemListener() {   
            @Override
            public void itemStateChanged(ItemEvent e) {
                refreshColonyInfo();
            }
        });
        
        ta = new JTextArea();
        ta.setEditable(false);
        pnCenter.add(new JScrollPane(ta), BorderLayout.CENTER);
        
        JPanel pnDiff = new JPanel();
        pnDiff.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnDown.add(pnDiff, BorderLayout.WEST);
        
        JLabel lb = new JLabel(ColonyManager.t("난이도"));
        pnDiff.add(lb);
        
        Vector<String> listDiff = new Vector<String>();
        for(int idx=1; idx<=9; idx++) {
            listDiff.add(String.valueOf(idx));
        }
        cbxDifficulty = new JComboBox<String>(listDiff);
        
        pnDiff.add(cbxDifficulty);
        
        JPanel pnCtrl = new JPanel();
        pnCtrl.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnDown.add(pnCtrl, BorderLayout.CENTER);
        
        JButton btn;
        
        btn = new JButton(ColonyManager.t("개척"));
        pnCtrl.add(btn);
        btn.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                ColonyInformation info = (ColonyInformation) cbxColTypes.getSelectedItem();
                if(info == null) { JOptionPane.showMessageDialog(getDialog(), ColonyManager.t("해당 타입으로 정착지를 만들 수 없습니다.")); return; }
                
                String strDiff = cbxDifficulty.getSelectedItem().toString();
                int diff = Integer.parseInt(strDiff.trim());
                
                man.onNewColonyTypeDecided(info.getName(), tfName.getText(), diff, getSelf());
                dispose();
            }
        });
        
        btn = new JButton(ColonyManager.t("취소"));
        pnCtrl.add(btn);
        btn.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        refreshColonyInfo();
    }
    
    protected JDialog getDialog() {
        return dialog;
    }
    
    protected NewColonyManager getSelf() {
        return this;
    }
    
    protected void refreshColonyInfo() {
        String desc = "";
        ColonyInformation info = (ColonyInformation) cbxColTypes.getSelectedItem();
        if(info != null) desc = info.getDescription();
        ta.setText(desc);
        
        
        int[] diffs = info.getDifficulties();
        String beforeDiff = cbxDifficulty.getSelectedItem() == null ? "1" : cbxDifficulty.getSelectedItem().toString();
        boolean beforeValueExists = false;
        
        cbxDifficulty.removeAllItems();
        for(int idx=0; idx<diffs.length; idx++) {
            String now = String.valueOf(diffs[idx]);
            cbxDifficulty.addItem(now);
            if(now.equals(beforeDiff)) beforeValueExists = true;
        }
        if(beforeValueExists) cbxDifficulty.setSelectedItem(beforeDiff);
        else                  cbxDifficulty.setSelectedIndex(0);
    }
    
    public void open() {
        tfName.setText(ColonyManager.t("정착지") + "_" + ColonyManager.generateNaturalNumber());
        dialog.setVisible(true);
    }
    
    public void close() {
        dispose();
    }

    @Override
    public void dispose() {
        if(dialog != null) dialog.setVisible(false);
        man    = null;
        dialog = null;
    }
}
