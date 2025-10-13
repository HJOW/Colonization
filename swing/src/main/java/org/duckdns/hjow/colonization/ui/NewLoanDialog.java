package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.loan.Loan;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.util.GUIUtil;

public class NewLoanDialog implements Disposeable {
    protected GUIColonyManager man;
    protected JDialog dialog;
    protected JComboBox<Loan> cbxLoans;
    protected JTextArea ta;
    
    protected transient Colony colony;
    protected transient ServletClientColonyPanel servletPanel = null;
    
    public NewLoanDialog(GUIColonyManager man) {
        init(man);
    }
    
    /** UI 초기화 */
    protected void init(GUIColonyManager man) {
        this.man = man;
        dialog = new JDialog(man.getDialog(), true);
        dialog.setSize(400, 300);
        GUIUtil.centerWindow(dialog);
        dialog.setTitle(ColonyManager.t("새 대출"));
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dialog = null;
                dispose();
            }
        });
        
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
        
        cbxLoans = new JComboBox<Loan>();
        pnUp.add(cbxLoans, BorderLayout.CENTER);
        
        cbxLoans.addItemListener(new ItemListener() {   
            @Override
            public void itemStateChanged(ItemEvent e) {
                refreshDesc();
            }
        });
        
        ta = new JTextArea();
        ta.setEditable(false);
        pnCenter.add(new JScrollPane(ta), BorderLayout.CENTER);
        
        JPanel pnCtrl = new JPanel();
        pnCtrl.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnDown.add(pnCtrl, BorderLayout.CENTER);
        
        JButton btn;
        
        btn = new JButton(ColonyManager.t("대출"));
        pnCtrl.add(btn);
        btn.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                Loan loan = (Loan) cbxLoans.getSelectedItem();
                if(loan == null) { JOptionPane.showMessageDialog(getDialog(), ColonyManager.t("해당 대출을 받을 수 없습니다.")); return; }
                
                colony.addLoan(loan);
                
                if(servletPanel != null) {
                    try { servletPanel.onNewLoanAdded(loan, colony); } catch(Throwable t) {
                        JOptionPane.showMessageDialog(getDialog(), ColonyManager.t("오류") + " : " + ColonyManager.t(t.getMessage()));
                        return;
                    }
                }
                
                JOptionPane.showMessageDialog(getDialog(), ColonyManager.t("대출을 받았습니다."));
                man.refreshColonyContent();
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
        
        refreshDesc();
    }
    
    protected JDialog getDialog() {
        return dialog;
    }
    
    protected NewLoanDialog getSelf() {
        return this;
    }
    
    /** 서블릿 클라이언트 모드 설정 */
    public void setServletClientMode(ServletClientColonyPanel servletPanel) {
        this.servletPanel = servletPanel;
    }
    
    /** 설명 텍스트 새로고침 */
    protected void refreshDesc() {
        String desc = "";
        Loan loan = (Loan) cbxLoans.getSelectedItem();
        if(loan != null) {
            desc = loan.getName();
            desc += "\n";
            desc += "\n" + ColonyManager.t("원금") + " : " + ColonyManager.FORMATTER_INT.format(loan.getAmount());
            desc += "\n" + ColonyManager.t("기간(월)") + " : " + ColonyManager.FORMATTER_INT.format(loan.getInterestCount());
            desc += "\n" + ColonyManager.t("이자율(연)") + " : " + ColonyManager.FORMATTER_RATE.format(loan.getInterestRate100()) + " %";
            desc += "\n" + ColonyManager.t("이자(월당)") + " : " + ColonyManager.FORMATTER_INT.format(loan.getInterestOnce(1));
            
            desc += "\n";
            desc += "\n" + ColonyManager.t("만기일에 원금을 전액 상환해야 합니다.");
            desc += "\n" + ColonyManager.t("만기 시 원금 상환에 실패하는 경우,\n이자가 2배로 발생하며, 신용도가 하락합니다.");
        }
        ta.setText(desc);
    }
    
    /** 대화 상자 오픈 */
    public void open(GUIColonyManager man, Colony colony) {
        if(colony.getLoanAvail().isEmpty()) {
            JOptionPane.showMessageDialog(man.getDialog(), ColonyManager.t("현재 받을 수 있는 대출 상품이 없습니다."));
            dispose();
            return;
        }
        
        this.colony = colony;
        init(man);
        
        cbxLoans.removeAllItems();
        for(Loan l : colony.getLoanAvail()) {
            cbxLoans.addItem(l);
        }
        cbxLoans.setSelectedIndex(0);
        refreshDesc();
        
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
        servletPanel = null;
    }
}
