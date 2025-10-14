package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;

import org.duckdns.hjow.colonization.AccountingData;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.City;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.elements.loan.Loan;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.commons.util.DataUtil;

/** 정착지 정보 출력 및 컨트롤을 담당하는 UI 컴포넌트 */
public class DefaultColonyPanel extends JPanel implements ColonyElementPanel, ColonyPanel {
    private static final long serialVersionUID = 3851432705333464777L;
    protected GUIColonyManager superInstance;
    protected Colony colony;
    
    protected transient List<CityPanel> pnCities = new Vector<CityPanel>();
    protected transient JPanel pnColonyBasics, pnColonyCardCity, pnAccountingMain, pnHoldings, pnResearches, pnLoanHaves;
    protected transient CardLayout cardCity;
    protected transient DefaultTableModel tableAccounting;
    protected transient JTabbedPane tabMain, tabCities;
    protected transient JProgressBar progHp;
    protected transient JTextField tfColonyName, tfColonyTime, tfIncomes;
    protected transient JTextArea taStatus, taLoans;
    protected transient JToolBar toolbar;
    protected transient JButton btnNewCity, btnNewLoan;
    
    protected transient boolean flagEditable = true;
    
    public DefaultColonyPanel() {
        super();
    }
    
    public DefaultColonyPanel(Colony colony, GUIColonyManager superInstance) {
        this();
        init(colony, superInstance);
    }
    
    public void init(Colony colony, GUIColonyManager superInstance) {
        if(colony != null) dispose();
        this.colony = colony;
        this.superInstance = superInstance;
        
        setLayout(new BorderLayout());
        
        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BorderLayout());
        add(pnMain, BorderLayout.CENTER);
        
        JPanel pnCenter = new JPanel();
        pnCenter.setLayout(new BorderLayout());
        pnMain.add(pnCenter, BorderLayout.CENTER);
        
        tabMain = new JTabbedPane();
        pnCenter.add(tabMain, BorderLayout.CENTER);
        
        pnColonyCardCity = new JPanel();
        tabMain.add(ColonyManager.t("도시"), pnColonyCardCity);
        
        pnResearches = new JPanel();
        tabMain.add(ColonyManager.t("연구"), pnResearches);
        
        pnAccountingMain = new JPanel();
        tabMain.add(ColonyManager.t("예산"), pnAccountingMain);
        
        pnLoanHaves = new JPanel();
        tabMain.add(ColonyManager.t("남은 대출"), pnLoanHaves);
        
        cardCity = new CardLayout();
        pnColonyCardCity.setLayout(cardCity);
        
        tabCities = new JTabbedPane();
        pnColonyCardCity.add(tabCities, "CITY");
        
        JPanel pnLoading, pnLoadingIn;
        JProgressBar prog;
        
        pnLoading = new JPanel();
        pnLoading.setLayout(new BorderLayout());
        pnLoadingIn = new JPanel();
        pnLoadingIn.setLayout(new FlowLayout(FlowLayout.CENTER));
        pnLoading.add(pnLoadingIn, BorderLayout.CENTER);
        pnLoading.add(new JPanel(), BorderLayout.NORTH);
        pnLoading.add(new JPanel(), BorderLayout.SOUTH);
        prog = new JProgressBar();
        prog.setIndeterminate(true);
        pnLoadingIn.add(prog);
        pnColonyCardCity.add(pnLoading, "LOADING");
        cardCity.show(pnColonyCardCity, "LOADING");
        
        tableAccounting = new DefaultTableModel();
        tableAccounting.addColumn(ColonyManager.t("사유"));
        tableAccounting.addColumn(ColonyManager.t("대상"));
        tableAccounting.addColumn(ColonyManager.t("금액"));
        
        tfIncomes = new JTextField();
        tfIncomes.setEditable(false);
        
        pnAccountingMain.setLayout(new BorderLayout());
        pnAccountingMain.add(new JScrollPane(new JTable(tableAccounting)), BorderLayout.CENTER);
        pnAccountingMain.add(tfIncomes, BorderLayout.SOUTH);
        
        pnResearches.setLayout(new GridBagLayout());
        
        JPanel pnColTop, pnColBottom;
        pnColTop    = new JPanel();
        pnColBottom = new JPanel();
        pnColTop.setLayout(new BorderLayout());
        pnColBottom.setLayout(new BorderLayout());
        
        pnCenter.add(pnColTop   , BorderLayout.NORTH);
        pnCenter.add(pnColBottom, BorderLayout.SOUTH);
        
        pnColonyBasics = new JPanel();
        pnColonyBasics.setLayout(new BorderLayout());
        
        JPanel pnTopLeft, pnTopCenter, pnTopRight, pnTopSouth;
        pnTopLeft   = new JPanel();
        pnTopCenter = new JPanel();
        pnTopRight  = new JPanel();
        pnTopSouth  = new JPanel();
        pnTopLeft.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnTopCenter.setLayout(new BorderLayout());
        pnTopRight.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnTopSouth.setLayout(new BorderLayout());
        
        pnColonyBasics.add(pnTopLeft  , BorderLayout.WEST);
        pnColonyBasics.add(pnTopCenter, BorderLayout.CENTER);
        pnColonyBasics.add(pnTopRight , BorderLayout.EAST);
        pnColonyBasics.add(pnTopSouth , BorderLayout.SOUTH);
        pnColTop.add(pnColonyBasics, BorderLayout.CENTER);
        
        tfColonyName = new JTextField(15);
        pnTopLeft.add(tfColonyName);
        tfColonyName.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                Colony c = getColony();
                if(c != null) {
                    c.setName(tfColonyName.getText());
                    superInstance.refreshColonyList();
                }
            }
        });
        
        tfColonyTime = new JTextField(14);
        tfColonyTime.setEditable(false);
        pnTopRight.add(tfColonyTime);
        
        progHp = new JProgressBar(JProgressBar.HORIZONTAL);
        pnTopRight.add(progHp);
        
        JPanel pnTopDetail = new JPanel();
        pnTopDetail.setLayout(new BorderLayout());
        pnTopSouth.add(pnTopDetail, BorderLayout.CENTER);
        
        taStatus = new JTextArea();
        taStatus.setEditable(false);
        pnTopDetail.add(taStatus, BorderLayout.CENTER);
        
        toolbar = new JToolBar();
        pnTopDetail.add(toolbar, BorderLayout.SOUTH);
        
        btnNewCity = new JButton(ColonyManager.t("새 도시 건설"));
        toolbar.add(btnNewCity);
        
        btnNewCity.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                onNewCityRequested();
            }
        });
        
        btnNewLoan = new JButton(ColonyManager.t("새 대출 받기"));
        toolbar.add(btnNewLoan);
        
        btnNewLoan.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                onNewLoanRequested();
            }
        });
        
        pnLoanHaves.setLayout(new BorderLayout());
        taLoans = new JTextArea();
        taLoans.setEditable(false);
        pnLoanHaves.add(new JScrollPane(taLoans), BorderLayout.CENTER);
        
        JPanel pnLog = new JPanel();
        pnLog.setLayout(new BorderLayout());
        pnMain.add(pnLog, BorderLayout.SOUTH);
        
        pnHoldings = new JPanel();
        pnLog.add(new JScrollPane(pnHoldings), BorderLayout.CENTER);
    }
    
    @Override
    public void dispose() {
        colony = null;
        for(CityPanel c : pnCities) {
            c.dispose();
        }
        pnCities.clear();
        
        removeAll();
        superInstance = null;
    }

    @Override
    public void setEditable(boolean editable) {
        flagEditable = editable;
        tfColonyName.setEditable(editable);
        btnNewCity.setEnabled(editable);
        btnNewLoan.setEnabled(editable);
        for(CityPanel c : pnCities) {
            if(c.getCity().getHp() <= 0) c.setEditable(false);
            else c.setEditable(editable);
        }
    }
    
    /** 화면 새로고침 예약 */
    @Override
    public void reserveRefresh() {
        if(superInstance != null) superInstance.reserveRefresh();
    }
    
    @Override
    public void refresh(int cycle, City city, Colony colony, ColonyManager superInstance) { // city is null
        if(colony == null) {
            tfColonyName.setText("");
            tfColonyTime.setText("");
            taStatus.setText("");
            cardCity.show(pnColonyCardCity, "CITY");
            return;
        }
        
        progHp.setMaximum(colony.getMaxHp());
        progHp.setValue(colony.getHp());
        
        List<City> cities = colony.getCities();
        if(cycle == 0 || cycle % 36000 == 0 || tabCities.getTabCount() != cities.size()) {
        	cardCity.show(pnColonyCardCity, "LOADING");
        	
            tabCities.removeAll();
            for(CityPanel c : pnCities) { c.dispose(); }
            pnCities.clear();
            
            for(int idx=0; idx<cities.size(); idx++) {
                CityPanel c = new CityPanel(cities.get(idx), colony, (GUIColonyManager) superInstance);
                pnCities.add(c);
                tabCities.add(cities.get(idx).getName(), c);
            }
        } else {
            for(int idx=0; idx<pnCities.size(); idx++) {
            	CityPanel p = pnCities.get(idx);
                City cityCurrent = p.getCity();
                
                tabCities.setTitleAt(idx, cityCurrent == null ? "" : cityCurrent.getName());
                p.refresh(cycle, cityCurrent, colony, superInstance);
            }
        }
        
        tfColonyName.setText(colony.getName());
        tfColonyTime.setText(colony.getDateString());
        taStatus.setText(colony.getStatusString(superInstance));
        
        pnHoldings.removeAll();
        pnResearches.removeAll();
        GridBagConstraints gridBagConst;
        int rowNo = 0;
        for(Research r : colony.getResearches()) {
            if(r.getLevel() <= 0 && r.getProgress() <= 0) continue;
            
            ResearchPanel pnRes = new ResearchPanel(r);
            
            gridBagConst = new GridBagConstraints();
            gridBagConst.gridx = 0;
            gridBagConst.gridy = rowNo; rowNo++;
            gridBagConst.gridwidth = 1;
            gridBagConst.gridheight = 1;
            gridBagConst.weightx = 1.0;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
            gridBagConst.fill = GridBagConstraints.HORIZONTAL;
            gridBagConst.anchor = GridBagConstraints.NORTH;
            
            pnResearches.add(pnRes, gridBagConst);
            pnRes.refresh(cycle, city, colony);
        }
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo; rowNo++;
        gridBagConst.gridwidth = 1;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 1.0;
        gridBagConst.weighty = 1.0;
        gridBagConst.fill = GridBagConstraints.BOTH;
        pnResearches.add(new JPanel(), gridBagConst);
        
        refreshAccoutingTable();
        refreshLoanHaveList();
        setEditable(flagEditable);
        
        colony.markAsRefreshChildren(false);
        cardCity.show(pnColonyCardCity, "CITY");
    }
    
    public CityPanel getCityPanel(City city) {
        for(CityPanel c : pnCities) {
            if(c.getCity().getKey() == city.getKey()) {
                return c;
            }
        }
        return null;
    }

    public Colony getColony() {
        return colony;
    }

    public void setColony(Colony colony) {
        this.colony = colony;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public String getTargetName() {
        return colony.getName();
    }
    
    /** 모든 행 삭제 */
    public void clearAccountingTable() {
        while(tableAccounting.getRowCount() >= 1) { tableAccounting.removeRow(0); }
    }
    
    /** 새 도시 건설 요청 시 호출 */
    protected void onNewCityRequested() {
        Colony col = getColony();
        
        // 최대 도시 수 제한 체크
        int cityCnt = col.getCityCount();
        if(cityCnt >= col.getMaxCityCount()) { JOptionPane.showMessageDialog(superInstance.getDialog(), ColonyManager.t("더 이상 새 도시를 건설할 수 없습니다.")); return; }
        
        // 예산 체크
        long howMuch = City.getBuildingNewCityFee(col);
        long nowHave = col.getMoney();
        if(nowHave < howMuch) { JOptionPane.showMessageDialog(superInstance.getDialog(), ColonyManager.t("새 도시 건설에는 [MONEY] 의 예산이 더 필요합니다.").replace("[MONEY]", String.valueOf(howMuch - nowHave))); return; }
        
        // 인구 체크 (소모는 되지 않지만, 최소 조건으로 적용)
        long population = col.getCitizenCount();
        if(cityCnt >= 1) {
            if((population / cityCnt) < 1000) { JOptionPane.showMessageDialog(superInstance.getDialog(), ColonyManager.t("새 도시를 건설하려면, 현재의 도시들의 인구 평균이 [AVERAGE] 을 넘어야 합니다.").replace("[AVERAGE]", "1000")); return; }
        }
        
        int sel = JOptionPane.showConfirmDialog(superInstance.getDialog(), ColonyManager.t("새 도시를 건설하시겠습니까?\n[MONEY] 의 예산이 필요합니다.").replace("[MONEY]", String.valueOf(howMuch)), ColonyManager.t("확인"), JOptionPane.YES_NO_OPTION);
        if(sel != JOptionPane.YES_OPTION) return;
        
        newCity();
    }
    
    /** 새 도시 건설 */
    public void newCity() {
        Colony col = getColony();
        
        // 최대 도시 수 제한 체크
        int cityCnt = col.getCityCount();
        if(cityCnt >= col.getMaxCityCount()) throw new RuntimeException(ColonyManager.t("더 이상 새 도시를 건설할 수 없습니다."));
        
        // 예산 체크
        long howMuch = City.getBuildingNewCityFee(col);
        long nowHave = col.getMoney();
        if(nowHave < howMuch) throw new RuntimeException(ColonyManager.t("새 도시 건설에는 [MONEY] 의 예산이 더 필요합니다.").replace("[MONEY]", String.valueOf(howMuch - nowHave)));
        
        // 인구 체크 (소모는 되지 않지만, 최소 조건으로 적용)
        long population = col.getCitizenCount();
        if(cityCnt >= 1) {
            if((population / cityCnt) < 1000) throw new RuntimeException(ColonyManager.t("새 도시를 건설하려면, 현재의 도시들의 인구 평균이 [AVERAGE] 을 넘어야 합니다.").replace("[AVERAGE]", "1000"));
        }
        
        City c = col.newCity();
        col.modifyingMoney( City.getBuildingNewCityFee(col) * (-1) , c, col, "NewCity", "");
        
        reserveRefresh();
    }
    
    /** 새 대출 요청 시 호출 */
    protected void onNewLoanRequested() {
        NewLoanDialog dialog = new NewLoanDialog(superInstance);
        dialog.open(superInstance, colony);
    }
    
    /** 회계 정보 새로고침 */
    public void refreshAccoutingTable() {
        // 모든 행 삭제
        clearAccountingTable();
        
        Colony col = getColony();
        Vector<Object> rows;
        
        // 데이터 쌓기
        List<AccountingData> list = col.getAccountingData();
        BigInteger timeStd = new BigInteger(col.getTime().toByteArray()).subtract(new BigInteger(String.valueOf(col.getAccountingPeriod())));
        long incomes = 0L;
        for(AccountingData data : list) {
            if(data.isDisposed()) continue;
            
            BigInteger time = data.getTime();
            
            if(timeStd.compareTo(time) <= 0) {
                rows = new Vector<Object>();
                
                rows.add(data.getReason());
                
                String sourceName = null;
                
                City cityCurrent = null;
                if(data.getCityKey() != 0L) {
                    for(City ct : col.getCities()) {
                        if(ct.getKey() == data.getCityKey()) {
                            cityCurrent = ct;
                            break;
                        }
                    }
                }
                if(DataUtil.isNotEmpty(cityCurrent)) {
                    for(Facility f : cityCurrent.getFacility()) {
                        if(f.getKey() == data.getSourceKey()) {
                            sourceName = f.getName();
                            break;
                        }
                    }
                    if(sourceName == null) {
                        for(Citizen c : cityCurrent.getCitizens()) {
                            if(c.getKey() == data.getSourceKey()) {
                                sourceName = c.getName();
                                break;
                            }
                        }
                    }
                }
                
                if(sourceName == null) sourceName = "UNKNOWN";
                
                String mores = data.getMoreString();
                if(DataUtil.isNotEmpty(mores)) {
                	if(sourceName.equals("UNKNOWN") || sourceName.equals("")) sourceName = mores;
                	else if(sourceName.equals(mores)) sourceName = mores;
                	if(! sourceName.equals(mores)) sourceName += " " + mores;
                }
                
                rows.add(sourceName);
                
                long val = data.getAmount();
                rows.add(new Long(val));
                
                tableAccounting.addRow(rows);
                incomes += val;
            } else {
                data.dispose();
                continue;
            }
        }
        tfIncomes.setText(String.valueOf(incomes));
        
        // 오래된 회계자료 제거
        int idx = 0;
        while(idx < col.getAccountingData().size()) {
            if(col.getAccountingData().get(idx).isDisposed()) {
                col.getAccountingData().remove(idx);
                continue;
            }
            idx++;
        }
    }
    
    /** 대출 목록 새로고침 */
    protected void refreshLoanHaveList() {
        taLoans.setText("");
        Colony col = getColony();
        
        StringBuilder res = new StringBuilder("");
        List<Loan> list = col.getLoanHave();
        if(list.isEmpty()) {
            res = res.append(ColonyManager.t("남아 있는 대출이 없습니다."));
        } else {
            res = res.append("\n");
            for(Loan l : list) {
                res = res.append("\n").append(l.getName());
                res = res.append("\n    ").append(ColonyManager.t("원금")).append(" : ").append(ColonyManager.FORMATTER_INT.format(l.getOriginals()));
                res = res.append("\n    ").append(ColonyManager.t("기간(월)")).append(" : ").append(ColonyManager.FORMATTER_INT.format(l.getInterestLeft())).append(" / ").append(ColonyManager.FORMATTER_INT.format(l.getInterestCount()));
                res = res.append("\n    ").append(ColonyManager.t("이자율(연)")).append(" : ").append(ColonyManager.FORMATTER_RATE.format(l.getInterestRate100())).append(" %");
                res = res.append("\n    ").append(ColonyManager.t("이자(월당)")).append(" : ").append(ColonyManager.FORMATTER_INT.format(l.getInterestOnce(1)));
            }
        }
        
        taLoans.setText(res.toString().trim());
    }
}
