package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.City;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.HoldingJob;
import org.duckdns.hjow.colonization.elements.facilities.FacilityInformation;
import org.duckdns.hjow.colonization.elements.facilities.FacilityManager;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.util.GUIUtil;

public class NewFacilityManager extends JDialog implements Disposeable {
    private static final long serialVersionUID = 8433244450809087631L;
    protected ColonyManager colonyManager;
    protected City city;
    
    protected transient ServletClientColonyPanel servletPanel = null;
    
    protected JComboBox<FacilityInformation> cbxFacInfos;
    protected JTextArea ta;
    protected JButton btnOk, btnClose;
    
    public NewFacilityManager() {
        super();
    }
    public NewFacilityManager(GUIColonyManager colonyManager, City city) {
        super(colonyManager.getDialog());
        init(colonyManager, city);
        refresh();
    }
    
    @Override
    public void dispose() {
        disposeFields();
        setVisible(false);
    }
    
    /** 순환 참조 우려가 있는 필드만 null 처리 */
    public void disposeFields() {
        this.colonyManager = null;
        this.city          = null;
        this.servletPanel  = null;
    }
    
    /** UI 초기화 */
    public void init(ColonyManager colonyManager, City city) {
        if(this.city != null) disposeFields();
        
        this.colonyManager = colonyManager;
        this.city = city;
        
        setSize(400, 300);
        setLayout(new BorderLayout());
        setTitle(ColonyManager.t("새 시설 건설"));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disposeFields();
            }
        });
        GUIUtil.centerWindow(this);
        
        JPanel pnMain, pnCenter, pnUp, pnDown;
        
        pnMain = new JPanel();
        pnMain.setLayout(new BorderLayout());
        add(pnMain, BorderLayout.CENTER);
        
        pnUp     = new JPanel();
        pnCenter = new JPanel();
        pnDown   = new JPanel();
        
        pnUp.setLayout(new BorderLayout());
        pnCenter.setLayout(new BorderLayout());
        pnDown.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        pnMain.add(pnUp    , BorderLayout.NORTH);
        pnMain.add(pnCenter, BorderLayout.CENTER);
        pnMain.add(pnDown  , BorderLayout.SOUTH);
        
        cbxFacInfos = new JComboBox<FacilityInformation>();
        pnUp.add(cbxFacInfos, BorderLayout.CENTER);
        
        ta = new JTextArea();
        ta.setEditable(false);
        ta.setLineWrap(true);
        pnCenter.add(new JScrollPane(ta), BorderLayout.CENTER);
        
        btnOk    = new JButton(ColonyManager.t("건설"));
        btnClose = new JButton(ColonyManager.t("취소"));
        
        pnDown.add(btnOk);
        pnDown.add(btnClose);
        
        cbxFacInfos.addItemListener(new ItemListener() {   
            @Override
            public void itemStateChanged(ItemEvent e) {
                refresh();
            }
        });
        
        btnClose.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FacilityInformation info;
                Colony col;
                
                try {
                    info = (FacilityInformation) cbxFacInfos.getSelectedItem();
                    col = city.getColony(colonyManager);
                    
                    if(col.getMoney() < info.getPrice().longValue()) {
                        JOptionPane.showMessageDialog(getDialog(), ColonyManager.t("예산이 부족합니다.\n[MONEY] 의 예산이 더 필요합니다.").replace("[MONEY]", String.valueOf(info.getPrice() - col.getMoney())));
                        return;
                    };
                    
                    if(col.getTech() < info.getTech().longValue()) {
                        JOptionPane.showMessageDialog(getDialog(), ColonyManager.t("기술이 부족합니다.\n[TECH] 의 기술이 더 필요합니다.").replace("[TECH]", String.valueOf(info.getTech() - col.getTech())));
                        return;
                    };
                    
                    int leftSpaces = city.getLeftSpaces();
                    int needSpaces = info.getSpaceSize();
                    if(leftSpaces < needSpaces) {
                    	JOptionPane.showMessageDialog(getDialog(), ColonyManager.t("잔여 공간이 부족합니다.\n[SPACE] 의 공간이 더 필요합니다.").replace("[SPACE]", String.valueOf(needSpaces - leftSpaces)));
                        return;
                    }
                    
                    Method mthdChecker = info.getFacilityClass().getMethod("isBuildAvail", Colony.class, City.class);
                    String chkRes = (String) mthdChecker.invoke(null, col, city);
                    if(chkRes != null) {
                        JOptionPane.showMessageDialog(getDialog(), chkRes);
                        return;
                    }
                    
                    HoldingJob job = new HoldingJob(info.getBuildingCycle(), info.getBuildingCycle(), "NewFacility", info.getName());
                    job.setUsingSpace(info.getSpaceSize());
                    city.getHoldings().add(job);
                    
                    col.modifyingMoney(info.getPrice() * (-1) , city, city, "Building", info.getTitle());
                    
                    if(servletPanel != null) {
                        try { servletPanel.onNewFacilityAdded(info, city, col); } catch(Throwable t) {
                            JOptionPane.showMessageDialog(getDialog(), ColonyManager.t("오류") + " : " + ColonyManager.t(t.getMessage()));
                            return;
                        }
                    }
                    
                    colonyManager.refreshColonyContent();
                    dispose();
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(getDialog(), ColonyManager.t("오류가 발생하였습니다.") + "\n" + ex.getMessage());
                }
            }
        });
        
        refreshFacilityList();
    }
    
    public JDialog getDialog() { return this; }
    
    /** 서블릿 클라이언트 모드 설정 */
    public void setServletClientMode(ServletClientColonyPanel servletPanel) {
        this.servletPanel = servletPanel;
    }

    /** 시설 목록 새로고침 */
    public void refreshFacilityList() {
        FacilityInformation beforeSelected = (FacilityInformation) cbxFacInfos.getSelectedItem();
        cbxFacInfos.removeAllItems();
        
        Colony col = null; 
        if(city != null && colonyManager != null) col = city.getColony(colonyManager);

        List<FacilityInformation> lists = new ArrayList<FacilityInformation>();
        if(col != null) {
            lists.addAll(FacilityManager.getFacilityInformations());
            for(FacilityInformation info : lists) {
                if(! detectBuildAvail(col, info)) continue;
                cbxFacInfos.addItem(info);
            }
        }

        if(beforeSelected != null) {
            if(lists.contains(beforeSelected)) {
                cbxFacInfos.setSelectedItem(beforeSelected);
            }
        }
        
        
        refresh();
    }
    
    /** 해당 시설이 현재 설치 가능한지 판별 */
    protected boolean detectBuildAvail(Colony col, FacilityInformation info) {
        if(! col.supportedFacility(info))  return false;
        if(info.isBuildAvail(col, city) != null) return false;
        return true;
    }
    
    /** 이 대화상자 내 컨텐츠 새로고침 (시설 목록 제외) */
    public void refresh() {
        FacilityInformation info = (FacilityInformation) cbxFacInfos.getSelectedItem();
        
        if(info == null) {
            btnOk.setEnabled(false);
            ta.setText("");
            return;
        }
        
        Colony col = city.getColony(colonyManager);
        boolean avail = true;
        String prepends = "";
        String appends  = "";
        
        if(col.getMoney() < info.getPrice().longValue()) {
            prepends = "\n" + ColonyManager.t("건설에 [MONEY] 의 예산이 더 필요합니다.").replace("[MONEY]", String.valueOf(info.getPrice() - col.getMoney()));
            avail = false;
        }
        if(col.getTech() < info.getTech().longValue()) {
            prepends = "\n" + ColonyManager.t("건설에 [TECH] 의 기술이 더 필요합니다.").replace("[TECH]", String.valueOf(info.getTech() - col.getTech()));
            avail = false;
        }
        
        appends = appends + "\n" + ColonyManager.t("비용") + " : " + info.getPrice();
        appends = appends + "\n" + ColonyManager.t("기술") + " : " + info.getTech();
        appends = appends + "\n" + ColonyManager.t("소요") + " : " + info.getBuildingCycle();
        
        ta.setText(new String(prepends + "\n\n" + info.getDescription() + "\n\n" + appends).trim());
        btnOk.setEnabled(avail);
    }
}
