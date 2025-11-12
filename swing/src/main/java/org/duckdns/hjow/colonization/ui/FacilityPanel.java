package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.elements.HoldingJob;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.facilities.Factory;
import org.duckdns.hjow.colonization.elements.facilities.Home;
import org.duckdns.hjow.colonization.elements.facilities.Port;
import org.duckdns.hjow.colonization.elements.facilities.ResearchCenter;
import org.duckdns.hjow.colonization.elements.products.Money;
import org.duckdns.hjow.colonization.elements.products.Product;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.colonization.elements.states.State;
import org.duckdns.hjow.commons.util.DataUtil;
import org.duckdns.hjow.commons.util.HexUtil;

public class FacilityPanel extends JPanel implements ColonyElementPanel {
    private static final long serialVersionUID = -6078767714905474678L;
    
    protected transient JProgressBar progHp;
    protected transient JPanel pnUp, pnCenter, pnDown, pnCbxResearch, pnCbxProducts, pnBtnNewShip;
    protected transient ImagePanel pnImage;
    protected transient CardLayout cardResProd;
    protected transient JButton btnToggle, btnDestroy, btnUpgrade;
    protected transient JTextField tfName;
    protected transient JTextArea ta;
    protected transient JComboBox<Research> cbxResearch;
    protected transient JComboBox<Product>  cbxProducts;
    protected transient JButton btnNewShip;
    protected transient NewShipManager shipManager;
    
    protected transient boolean flagEditable = true;
    
    protected long facilityKey = 0L;
    protected String targetName;
    
    public FacilityPanel() {
        super();
    }
    
    public FacilityPanel(Facility f, City city, Colony colony, GUIColonyManager superInstance) {
        this();
        init(f, city, colony, superInstance);   
    }
    
    public void init(final Facility f, final City city, final Colony colony, final GUIColonyManager superInstance) {
        dispose();
        setFacilityKey(f.getKey());
        setTargetName(f.getName());
        
        removeAll();
        setLayout(new BorderLayout());
        
        pnUp     = new JPanel();
        pnCenter = new JPanel();
        pnDown   = new JPanel();
        
        pnUp.setLayout(new BorderLayout());
        pnCenter.setLayout(new BorderLayout());
        pnDown.setLayout(new BorderLayout());
        
        add(pnUp    , BorderLayout.NORTH);
        add(pnCenter, BorderLayout.CENTER);
        add(pnDown  , BorderLayout.SOUTH);
        
        JPanel pnName = new JPanel();
        pnName.setLayout(new BorderLayout());
        pnUp.add(pnName, BorderLayout.CENTER);
        
        tfName = new JTextField();
        pnName.add(tfName, BorderLayout.CENTER);
        tfName.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                Facility f = getFacility(city);
                if(f != null) {
                    f.setName(tfName.getText());
                    superInstance.refreshColonyContent();
                }
            }
        });
        
        JPanel pnCtrls = new JPanel();
        pnCtrls.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnUp.add(pnCtrls, BorderLayout.EAST);
        
        progHp = new JProgressBar(JProgressBar.HORIZONTAL);
        pnCtrls.add(progHp);
        
        btnDestroy = new JButton(ColonyManager.t("철거"));
        pnCtrls.add(btnDestroy);
        
        btnUpgrade = new JButton(ColonyManager.t("증축"));
        pnCtrls.add(btnUpgrade);
        btnUpgrade.setVisible(false);
        
        btnToggle = new JButton("▼");
        pnCtrls.add(btnToggle);

        pnImage = new ImagePanel();
        pnImage.setPreferredSize(new Dimension(40, 40));
        pnCenter.add(pnImage, BorderLayout.WEST);

        ta = new JTextArea();
        ta.setEditable(false);
        ta.setLineWrap(true);
        pnCenter.add(new JScrollPane(ta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
        
        JPanel pnCenterDown = new JPanel();
        cardResProd = new CardLayout();
        pnCenterDown.setLayout(cardResProd);
        pnCenter.add(pnCenterDown, BorderLayout.SOUTH);
        
        pnCbxResearch = new JPanel();
        pnCbxProducts = new JPanel();
        pnBtnNewShip  = new JPanel();
        pnCbxResearch.setLayout(new BorderLayout());
        pnCbxProducts.setLayout(new BorderLayout());
        pnBtnNewShip.setLayout(new BorderLayout());
        pnCenterDown.add(pnCbxResearch, "Research");
        pnCenterDown.add(pnCbxProducts, "Product");
        pnCenterDown.add(pnBtnNewShip , "Ship");
        
        cbxResearch = new JComboBox<Research>();
        pnCbxResearch.add(cbxResearch);
        
        cbxProducts = new JComboBox<Product>();
        pnCbxProducts.add(cbxProducts);
        
        btnNewShip = new JButton(ColonyManager.t("새 함선"));
        pnBtnNewShip.add(btnNewShip);
        
        if(! ((f instanceof ResearchCenter) || (f instanceof Factory) || (f instanceof Port))) {
            pnCenterDown.setVisible(false);
        } else {
            pnCenterDown.setVisible(true);
            if(f instanceof ResearchCenter) {
                ResearchCenter rcenter = (ResearchCenter) f;
                
                List<Research> tResearches = colony.getResearches();
                Vector<Research> researches = new Vector<Research>();
                
                for(Research r : tResearches) {
                    if(r.isResearchAvail(colony)) researches.add(r);
                }
                
                tResearches = null;
                
                cbxResearch.setModel(new DefaultComboBoxModel<Research>(researches));
                
                Research research = rcenter.getResearch(colony);
                if(research != null) {
                    cbxResearch.setSelectedItem(research);
                } else {
                    if(! researches.isEmpty()) cbxResearch.setSelectedIndex(0);
                    
                    research = (Research) cbxResearch.getSelectedItem();
                    if(research == null) rcenter.setResearchKey(0L);
                    else rcenter.setResearchKey(research.getKey());
                }
                
                cardResProd.show(pnCenterDown, "Research");
            } else if(f instanceof Factory) {
                Factory factory = (Factory) f;
                List<Product> products = Product.getProductTypeList();
                Vector<Product> avails = new Vector<Product>();
                avails.add(new Money());
                for(Product p : products) {
                    if(factory.isStoreAvail(p) && factory.isProduced(p)) avails.add(p);
                }
                products = null;
                
                cbxProducts.setModel(new DefaultComboBoxModel<Product>(avails));
                
                String producingType = factory.getProductType();
                if(producingType == null || producingType == "Money") {
                    cbxProducts.setSelectedIndex(0);
                    factory.setProductType(null);
                } else {
                    for(Product p : avails) {
                        if(producingType.equals(p.getType())) {
                            cbxProducts.setSelectedItem(p);
                            break;
                        }
                    }
                }
                
                cardResProd.show(pnCenterDown, "Product");
            } else if(f instanceof Port) {
            	final Port p = (Port) f;
            	btnNewShip.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						onNewShipRequested(superInstance, city, p);
					}
				});
            	cardResProd.show(pnCenterDown, "Ship");
            }
        }
        
        pnCenter.setVisible(false);
        btnToggle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(pnCenter.isVisible()) {
                    pnCenter.setVisible(false);
                    btnToggle.setText("▼");
                } else {
                    pnCenter.setVisible(true);
                    btnToggle.setText("▲");
                }
            }
        });
        
        btnDestroy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long m = f.getDestructionFee(city, colony);
                long currentMoney = colony.getMoney();
                
                if(currentMoney < m) { JOptionPane.showMessageDialog(superInstance.getDialog(), ColonyManager.t("예산이 [MONEY] 부족합니다.").replace("[MONEY]", ColonyManager.formatInt( m - currentMoney ))); return; }
                
                if(f.getHp() <= 0) { JOptionPane.showMessageDialog(superInstance.getDialog(), ColonyManager.t("이미 곧 철거될 예정입니다.")); return; }
                
                String msg = ColonyManager.t("이 시설을 철거하시겠습니까?");
                msg += "\n" + ColonyManager.t("[MONEY] 예산 필요").replace("[MONEY]", ColonyManager.formatInt(m));
                
                int sel = JOptionPane.showConfirmDialog(superInstance.getDialog(), msg, ColonyManager.t("확인"), JOptionPane.YES_NO_OPTION);
                if(sel != JOptionPane.YES_OPTION) return;
                
                colony.modifyingMoney(m * (-1L), city, f, "Destruction", String.valueOf(f.getName()));
                
                f.setHp(0);
                superInstance.refreshColonyContent();
                JOptionPane.showMessageDialog(superInstance.getDialog(), ColonyManager.t("철거 지시가 내려졌습니다. 곧 철거될 것입니다."));
            }
        });
        
        btnUpgrade.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long m = f.getUpgradePrice(colony, city);
                int  c = f.getUpgradeCycle(colony, city);
                long currentMoney = colony.getMoney();
                
                if(currentMoney < m) { JOptionPane.showMessageDialog(superInstance.getDialog(), ColonyManager.t("예산이 [MONEY] 부족합니다.").replace("[MONEY]", ColonyManager.formatInt( m - currentMoney ))); return; }
                if(f.getLevel() >= f.getMaxLevel()) { JOptionPane.showMessageDialog(superInstance.getDialog(), ColonyManager.t("더 이상 증축이 불가능합니다.")); return; }
                
                String noMsg = checkUpgradeAvail(f, colony, city);
                if(DataUtil.isNotEmpty(noMsg)) { JOptionPane.showMessageDialog(superInstance.getDialog(), ColonyManager.t(noMsg)); return; }
                
                String msg = ColonyManager.t("이 시설을 증축하시겠습니까?");
                msg += "\n" + ColonyManager.t("[MONEY] 예산 필요").replace("[MONEY]", ColonyManager.formatInt(m));
                
                int sel = JOptionPane.showConfirmDialog(superInstance.getDialog(), msg, ColonyManager.t("확인"), JOptionPane.YES_NO_OPTION);
                if(sel != JOptionPane.YES_OPTION) return;
                
                colony.modifyingMoney(m * (-1L), city, f, "Upgrade", f.getName());
                
                HoldingJob newJob = new HoldingJob(c, c, "UpgradeFacility", String.valueOf(f.getKey()));
                city.addHoldingJob(newJob);
            }
        });
        
        cbxResearch.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(f instanceof ResearchCenter) {
                    ResearchCenter c = (ResearchCenter) f;
                    Research r = (Research) cbxResearch.getSelectedItem();
                    if(r != null) {
                        c.setResearchKey(r.getKey());
                    }
                }
            }
        });
        
        cbxProducts.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(f instanceof Factory) {
                    Factory factory = (Factory) f;
                    Product p = (Product) cbxProducts.getSelectedItem();
                    if(p != null) {
                        if(p instanceof Money) {
                            factory.setProductType(null);
                        } else {
                            factory.setProductType(p.getType());
                        }
                    }
                }
            }
        });
        
        refresh(f, city, colony, superInstance, true);
    }
    
    public long getFacilityKey() {
        return facilityKey;
    }

    public void setFacilityKey(long facilityKey) {
        this.facilityKey = facilityKey;
    }
    
    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public Facility getFacility(City city) {
        if(getFacilityKey() == 0L) return null;
        for(Facility f : city.getFacility()) {
            if(f.getKey() == getFacilityKey()) return f;
        }
        return null;
    }
    
    /** 화면 새로고침 */
    public void refresh(Facility fac, City city, Colony colony, ColonyManager superInstance) {
        refresh(fac, city, colony, superInstance, false);
    }

    /** 화면 새로고침 */
    public void refresh(Facility fac, City city, Colony colony, ColonyManager superInstance, boolean force) {
        if(fac == null) {
            tfName.setText("");
            ta.setText("");
            return;
        }
        
        progHp.setMaximum(fac.getMaxHp());
        progHp.setValue(fac.getHp());
        
        tfName.setText(fac.getName());
        setTargetName(fac.getName());
        
        if(! force) {
            if(! fac.isMarkedAsRefresh()) {
                if(fac.getHp() <= 0) setEditable(false);
                else setEditable(flagEditable);
                return;
            }
        }
        
        StringBuilder res = new StringBuilder("");
        res = res.append("\n").append("Type : ").append(fac.getType());
        if(fac instanceof Home) res = res.append(" (").append(ColonyManager.t("Home")).append(")");
        
        res = res.append("\n").append("HP : ").append(fac.getHp()).append(" / ").append(fac.getMaxHp());
        
        String desc = fac.getStatusDescription(city, colony);
        if(desc != null) res = res.append("\n").append(desc);
        
        if(fac instanceof Home) {
            Home home = (Home) fac;
            res = res.append("\n").append(ColonyManager.t("거주인원") + " : ").append(home.getCitizens(city, colony).size()).append(" / ").append(home.getCapacity());
            res = res.append("\n").append(ColonyManager.t("편안함") + " : ").append(fac.getComportGrade());
            res = res.append("\n").append(ColonyManager.t("거주자") + "...");
            List<Citizen> citizens = home.getCitizens(city, colony);
            if(citizens.isEmpty()) {
                res = res.append("\n    ").append(ColonyManager.t("거주 인원이 없습니다."));
            } else {
                for(Citizen c : citizens) {
                    res = res.append("\n    ").append(c.getName());
                }
            }
            
        }
        
        if(fac instanceof ResearchCenter) {
            ResearchCenter rcenter = (ResearchCenter) fac;
            
            List<Research> tResearches = colony.getResearches();
            Vector<Research> researches = new Vector<Research>();
            for(Research r : tResearches) {
                if(r.isResearchAvail(colony)) researches.add(r);
            }
            tResearches = null;
            
            cbxResearch.setModel(new DefaultComboBoxModel<Research>(researches));
            
            Research research = rcenter.getResearch(colony);
            if(research != null) {
                cbxResearch.setSelectedItem(research);
            } else {
                if(! researches.isEmpty()) cbxResearch.setSelectedIndex(0);
                
                research = (Research) cbxResearch.getSelectedItem();
                if(research == null) rcenter.setResearchKey(0L);
                else rcenter.setResearchKey(research.getKey());
            }
            
            res = res.append("\n").append(ColonyManager.t("연구") + " : ");
            if(research == null) {
                res = res.append(" -");
            } else {
                res = res.append(" ").append(research.getTitle() + " ( " + research.getProgressPercents() + " % )");
            }
        } else if(fac instanceof Factory) {
            Factory factory = (Factory) fac;
            
            List<Product> products = Product.getProductTypeList();
            Vector<Product> avails = new Vector<Product>();
            avails.add(new Money());
            for(Product p : products) {
                if(factory.isStoreAvail(p) && factory.isProduced(p)) avails.add(p);
            }
            products = null;
            
            cbxProducts.setModel(new DefaultComboBoxModel<Product>(avails));
            
            String producingType = factory.getProductType();
            if(producingType == null || producingType == "Money") {
                cbxProducts.setSelectedIndex(0);
                factory.setProductType(null);
            } else {
                for(Product p : avails) {
                    if(producingType.equals(p.getType())) {
                        cbxProducts.setSelectedItem(p);
                        break;
                    }
                }
            }
        }
        
        List<Citizen> workers = fac.getWorkingCitizens(city, colony);
        res = res.append("\n").append(ColonyManager.t("재직자") + "...");
        if(! workers.isEmpty()) {
            for(Citizen c : workers) {
                res = res.append("\n    ").append(c.getName());
            }
        } else {
            res = res.append("\n    ").append(ColonyManager.t("재직 중인 인원이 없습니다."));
        }
        
        List<State> states = fac.getStates();
        if(! states.isEmpty()) {
            res = res.append("\n").append(ColonyManager.t("상태") + "...");
            res = res.append("\n    ");
            for(State st : states) {
                res = res.append(st.getTitle()).append("\t");
            }
        }
        
        ta.setText(res.toString().trim());
        
        if(fac.getHp() <= 0) setEditable(false);
        else setEditable(flagEditable);
        
        btnUpgrade.setVisible(DataUtil.isEmpty(checkUpgradeAvail(fac, colony, city)));
        fac.markAsRefreshChildren(false);
        
        // 이미지 불러오기
        Image img = loadImage(fac);
        pnImage.setImage(img);
    }
    
    /** 새 함선 버튼 클릭 시 호출 */
    protected void onNewShipRequested(GUIColonyManager colonyManager, City city, Port p) {
    	if(shipManager != null) shipManager.dispose();
    	shipManager = new NewShipManager(colonyManager, city, p);
    	shipManager.setVisible(true);
    }
    
    /** 
     * <pre>
     * 시설의 이미지 불러오기
     * 
     * 해당 Facility 객체의 getImage 메소드 호출 시, Image 혹은 ImageIcon 타입 객체가 반환되면 그대로 반환,
     * 그외의 경우 문자열로 강제 형변환하여, 다음 Prefix 에 따라 동작
     *    resource: - 리소스 경로 내 파일을 찾아 이미지를 읽음
     *    file:     - 파일 실제경로에서 파일을 읽음
     *    hex:      - HEX String 을 해석하여 나온 바이너리를 이미지로 변환
     * </pre>
     */
    protected Image loadImage(Facility fac) {
        Object mayBeImg = fac.getImageContent(); // getImage 와 동일
        Image img = null;
        
        if(mayBeImg != null) {
            try {
                if(mayBeImg instanceof Image) {
                    img = (Image) mayBeImg;
                } else if(mayBeImg instanceof ImageIcon) {
                    img = ((ImageIcon) mayBeImg).getImage();
                } else {
                    String str = mayBeImg.toString();
                    if(str.startsWith("resource:")) {
                        String resName = str.substring(9);
                        URL url = getClass().getResource(resName);
                        
                        ImageIcon icon = new ImageIcon(url);
                        img = icon.getImage();
                    } else if(str.startsWith("file:")) {
                        String fName = str.substring(5);
                        File file = new File(fName);
                        
                        if(! file.exists()) return null;
                        URL url = file.toURI().toURL();
                        ImageIcon icon = new ImageIcon(url);
                        img = icon.getImage();
                    } else if(str.startsWith("hex:")) {
                        String hexStr = str.substring(4);
                        ByteArrayInputStream binStream = new ByteArrayInputStream(HexUtil.decode(hexStr));
                        img = ImageIO.read(binStream);
                    }
                }
            } catch(Exception ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
        
        return img;
    }
    
    @Override
    public void refresh(int cycle, City city, Colony colony, ColonyManager superInstance) {
        Facility fac = getFacility(city);
        if(fac == null) { setEditable(false); return; }
        refresh(fac, city, colony, superInstance);
    }
    
    /** 업그레이드 가능 여부 확인 (가능 시 null 반환, 불가능 시 메시지 반환) */
    protected String checkUpgradeAvail(Facility fac, Colony col, City city) {
    	if(fac.getLevel() >= fac.getMaxLevel()) return "더 이상 증축할 수 없는 시설입니다.";
        if(col.getMoney() < fac.getUpgradePrice(col, city)) return "예산이 부족하여 증축할 수 없습니다.";
        
    	String noMsg = fac.checkUpgradeAvail(col, city);
    	if(DataUtil.isNotEmpty(noMsg)) return noMsg;
        
        // 이미 업그레이드 중인지 확인
        for(HoldingJob j : city.getHoldings()) {
            if("UpgradeFacility".equalsIgnoreCase(j.getCommand())) {
                long key = Long.parseLong(j.getParameter().trim());
                if(fac.getKey() == key) {
                    return "이미 증축 중인 시설입니다.";
                }
            }
        }
        
        return null;
    }

    @Override
    public void setEditable(boolean editable) {
        flagEditable = editable;
        tfName.setEditable(editable);
        btnDestroy.setEnabled(editable);
        btnUpgrade.setEnabled(editable);
        cbxResearch.setEnabled(editable);
    }

    @Override
    public void dispose() {
        facilityKey = 0L;
        if(shipManager != null) { shipManager.dispose(); shipManager = null; }
        removeAll();
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
