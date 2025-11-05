package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.script.ScriptEngine;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.commons.ui.JLogArea;
import org.duckdns.hjow.commons.util.GUIUtil;

/** 스크립트 엔진 테스트 대화상자 */
public class ScriptTester {
    protected JDialog      dialog;
    
    protected JTextField   tfSingleScript;
    protected JLogArea     taSingleLog;
    
    protected JTextArea    taMultiScript, taMultiResult;
    
    protected transient ScriptEngine engine;
    
    public ScriptTester(Window w) {
        dialog = new JDialog(w);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { engine = null; }
        });
        
        int width  = 600;
        int height = 500;
        if(w != null) {
            width  = (int) (w.getWidth() * 0.75);
            height = (int) (w.getHeight() * 0.75);
            
            if(width  < 600) width  = 600;
            if(height < 500) height = 500;
        }
        
        dialog.setSize(width, height);
        dialog.setTitle(ColonyManager.t("Script Tester"));
        GUIUtil.centerWindow(dialog);
        dialog.setIconImage(GUIColonyManager.getIcon());
        dialog.setLayout(new BorderLayout());
        
        JTabbedPane tab = new JTabbedPane();
        dialog.add(tab, BorderLayout.CENTER);
        
        JPanel pnMain, pnDown, pnCtrl;
        pnMain = new JPanel();
        pnMain.setLayout(new BorderLayout());
        tab.add(ColonyManager.t("한 줄 실행"), pnMain);
        
        taSingleLog = new JLogArea();
        pnMain.add(taSingleLog, BorderLayout.CENTER);
        
        pnDown = new JPanel();
        pnDown.setLayout(new BorderLayout());
        pnMain.add(pnDown, BorderLayout.SOUTH);
        
        tfSingleScript = new JTextField();
        pnDown.add(tfSingleScript, BorderLayout.CENTER);
        
        tfSingleScript.addActionListener(new ActionListener() {    
            @Override
            public void actionPerformed(ActionEvent e) {
                onSingleRunRequested();
            }
        });
        
        pnCtrl = new JPanel();
        pnCtrl.setLayout(new BorderLayout());
        pnDown.add(pnCtrl, BorderLayout.EAST);
        
        JButton btn;
        
        btn = new JButton(ColonyManager.t("실행"));
        pnCtrl.add(btn);
        
        btn.addActionListener(new ActionListener() {    
            @Override
            public void actionPerformed(ActionEvent e) {
                onSingleRunRequested();
            }
        });
        
        pnMain = new JPanel();
        pnMain.setLayout(new GridLayout(2, 1));
        tab.add(ColonyManager.t("여러 줄 실행"), pnMain);
        
        taMultiScript = new JTextArea();
        pnMain.add(new JScrollPane(taMultiScript));
        
        pnDown = new JPanel();
        pnDown.setLayout(new BorderLayout());
        pnMain.add(pnDown);
        
        pnCtrl = new JPanel();
        pnCtrl.setLayout(new FlowLayout(FlowLayout.CENTER));
        pnDown.add(pnCtrl, BorderLayout.NORTH);
        
        taMultiResult = new JTextArea();
        taMultiResult.setEditable(false);
        pnDown.add(new JScrollPane(taMultiResult), BorderLayout.CENTER);
        
        btn = new JButton(ColonyManager.t("실행"));
        pnCtrl.add(btn);
        
        btn.addActionListener(new ActionListener() {    
            @Override
            public void actionPerformed(ActionEvent e) {
                onMultiRunRequested();
            }
        });
    }
    
    protected void onSingleRunRequested() {
        try {
            String scripts = tfSingleScript.getText();
            taSingleLog.log(">> " + scripts);
            tfSingleScript.setText("");
            
            // 리플렉션 존재여부 체크
            ColonyManager.checkBannedKeywords(scripts);
            
            Object res = ColonyManager.evaluate(engine, scripts);
            if(res == null) res = "[NULL]";
            taSingleLog.log(String.valueOf(res));
        } catch(Exception ex) {
            ex.printStackTrace();
            taSingleLog.log(ColonyManager.t("Error") + " : " + ex.getMessage());
        } finally {
            tfSingleScript.requestFocus();
        }
    }
    
    protected void onMultiRunRequested() {
        try {
            String scripts = taMultiScript.getText();
            System.out.println(scripts);
            
            // 리플렉션 존재여부 체크
            ColonyManager.checkBannedKeywords(scripts);
            
            Object res = ColonyManager.evaluate(engine, scripts);
            if(res == null) res = "[NULL]";
            taMultiResult.setText(String.valueOf(res));
        } catch(Exception ex) {
            ex.printStackTrace();
            taMultiResult.setText(ColonyManager.t("Error") + " : " + ex.getMessage());
        }
    }
    
    public void open(ScriptEngine engine) {
        this.engine = engine;
        dialog.setVisible(true);
        taSingleLog.log(ColonyManager.t("Script Test Console")
                + "\n    " + ColonyManager.t("자바스크립트 기본 문법 (var, if, for, while, function) 을 사용할 수 있습니다.")
                + "\n    " + ColonyManager.t("자세한 사항은 도움말을 참고해 주십시오."));
        tfSingleScript.requestFocus();
    }
    
    public void close() {
        dialog.setVisible(false);
        engine = null;
    }
}
