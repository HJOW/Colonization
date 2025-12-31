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

import org.duckdns.hjow.colonization.ColonyClassManager;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.ColonyManagerInterface;
import org.duckdns.hjow.colonization.GUIColonyManagerInterface;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.facilities.Port;
import org.duckdns.hjow.colonization.elements.ship.Ship;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.util.DataUtil;
import org.duckdns.hjow.commons.util.GUIUtil;

public class NewShipManager extends JDialog implements Disposeable {
	private static final long serialVersionUID = -947684457643473489L;
	protected ColonyManagerInterface colonyManager;
    protected Colony colony;
    protected Port   port;
    
    protected JComboBox<ShipInformation> cbxFacInfos;
    protected JTextArea ta;
    protected JButton btnOk, btnClose;
    
    public NewShipManager() {
        super();
    }
    public NewShipManager(GUIColonyManagerInterface colonyManager, City city, Port port) {
        super(colonyManager.getDialog());
        init(colonyManager, city.getColony(colonyManager), port);
        refresh();
    }
    
    @Override
    public void dispose() {
        disposeFields();
        setVisible(false);
    }
    
    /** 이미 사용 불가 상태인지 확인 */
    public boolean isDisposed() {
    	return (colonyManager == null);
    }
    
    /** 순환 참조 우려가 있는 필드만 null 처리 */
    public void disposeFields() {
        this.colonyManager = null;
        this.colony        = null;
        this.port          = null;
    }
    
    /** UI 초기화 */
    public void init(ColonyManagerInterface colonyManager, Colony colony, Port port) {
        if(this.colony != null) disposeFields();
        
        this.colonyManager = colonyManager;
        this.colony = colony;
        this.port   = port;
        
        setSize(400, 300);
        setLayout(new BorderLayout());
        setTitle(ColonyManager.t("새 함선 건조"));
        setIconImage(GUIColonyManager.getIcon());
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
        
        cbxFacInfos = new JComboBox<ShipInformation>();
        pnUp.add(cbxFacInfos, BorderLayout.CENTER);
        
        ta = new JTextArea();
        ta.setEditable(false);
        ta.setLineWrap(true);
        pnCenter.add(new JScrollPane(ta), BorderLayout.CENTER);
        
        btnOk    = new JButton(ColonyManager.t("건조"));
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
                onOkRequested();
            }
        });
        
        refreshShipInfoList();
    }
    
    public JDialog getDialog() { return this; }
    
    /** 함선 목록 새로고침 */
    public void refreshShipInfoList() {
    	ShipInformation beforeSelected = (ShipInformation) cbxFacInfos.getSelectedItem();
        cbxFacInfos.removeAllItems();
        
        // boolean useCheckDisables = colonyManager.isUsingCheckDisablingContent();
        
        List<ShipInformation> lists = getAvailList();
        for(ShipInformation info : lists) {
            cbxFacInfos.addItem(info);
        }
        
        if(beforeSelected != null) {
            if(lists.contains(beforeSelected)) {
                cbxFacInfos.setSelectedItem(beforeSelected);
            }
        }
        
        refresh();
    }
    
    /** 생산 가능한 함선 정보들 반환 */
    public List<ShipInformation> getAvailList() {
    	List<ShipInformation> lists = new ArrayList<ShipInformation>();
    	if(colony != null) {
    		List<Class<?>> classes = ColonyClassManager.shipClasses();
        	for(Class<?> classOne : classes) {
        		ShipInformation info = new ShipInformation(classOne);
        		if(! detectBuildAvail(colony, info)) continue;
        		lists.add(info);
        	}
    	}
    	return lists;
    }
    
    /** 해당 함선이 현재 건조 가능한지 판별 */
    protected boolean detectBuildAvail(Colony col, ShipInformation info) {
    	if(! info.isBuildAvail(port, col)) return false;
    	if(col.getMoney() < info.getPrice(port, col)) return false;
    	if(port.leftShipSpaces() < info.getSize()) return false;
    	if(port.getBuildingShipCount() >= port.getBuildingLineCount()) return false;
    	
        return true;
    }
    
    /** 설치 요청 시 호출 */
    protected void onOkRequested() {
        ShipInformation info;
        
        try {
            info = (ShipInformation) cbxFacInfos.getSelectedItem();
            
            long price = info.getPrice(port, colony);
            
            if(colony.getMoney() < price) {
                JOptionPane.showMessageDialog(getDialog(), ColonyManager.t("예산이 부족합니다.\n[MONEY] 의 예산이 더 필요합니다.").replace("[MONEY]", String.valueOf(price - colony.getMoney())));
                return;
            };
            
            int leftSpaces = port.leftShipSpaces();
            int needSpaces = info.getSize();
            if(leftSpaces < needSpaces) {
                JOptionPane.showMessageDialog(getDialog(), ColonyManager.t("잔여 공간이 부족합니다.\n[SPACE] 의 공간이 더 필요합니다.").replace("[SPACE]", String.valueOf(needSpaces - leftSpaces)));
                return;
            }
            
            Method mthdChecker = info.getShipClass().getMethod("getMetaBuildAvail", Port.class, Colony.class);
            String chkRes = (String) mthdChecker.invoke(null, port, colony);
            if(chkRes != null) {
                JOptionPane.showMessageDialog(getDialog(), chkRes);
                return;
            }
            
            Ship ship = (Ship) info.getShipClass().newInstance();
            ship.init(port, colony);
            port.getShips().add(ship);
            
            colony.modifyingMoney(price * (-1), port.getCity(colony), port, "Building", info.toString());
            
            colonyManager.refreshColonyContent();
            dispose();
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(getDialog(), ColonyManager.t("오류가 발생하였습니다.") + "\n" + ex.getMessage());
        }
    }
    
    /** 이 대화상자 내 컨텐츠 새로고침 (함선 목록 제외) */
    public void refresh() {
    	ShipInformation info = (ShipInformation) cbxFacInfos.getSelectedItem();
        
        if(info == null) {
            btnOk.setEnabled(false);
            ta.setText("");
            return;
        }
        
        boolean avail = true;
        String prepends = "";
        String appends  = "";
        
        long price = info.getPrice(port, colony);
        
        if(colony.getMoney() < price) {
            prepends = "\n" + ColonyManager.t("건조에 [MONEY] 의 예산이 더 필요합니다.").replace("[MONEY]", String.valueOf(price - colony.getMoney()));
            avail = false;
        }
        
        appends = appends + "\n" + ColonyManager.t("비용") + " : " + price;
        appends = appends + "\n" + ColonyManager.t("소요") + " : " + info.getCycle(port, colony);
        
        ta.setText(new String(prepends + "\n\n" + info.getDesc() + "\n\n" + appends).trim());
        btnOk.setEnabled(avail);
    }
}

class ShipInformation {
	protected Class<?> shipClass;
	public ShipInformation() {}
	public ShipInformation(Class<?> classOne) { this.shipClass = classOne; }
	
	public String getDesc() {
		try {
    	    Method mthd = shipClass.getMethod("getMetaDescription");
    	    return (String) mthd.invoke(null);
    	} catch(Exception ex) {
    		throw new RuntimeException(ex.getMessage(), ex);
    	}
	}
	
	public long getPrice(Port port, Colony colony) {
		try {
    	    Method mthd = shipClass.getMethod("getMetaPrice", Port.class, Colony.class);
    	    return (long) (((Number) mthd.invoke(null, port, colony)).longValue() * colony.getSpace().getMoneyCostRate());
    	} catch(Exception ex) {
    		throw new RuntimeException(ex.getMessage(), ex);
    	}
	}
	
	public long getCycle(Port port, Colony colony) {
		try {
    	    Method mthd = shipClass.getMethod("getMetaBuildCycle", Port.class, Colony.class);
    	    return (long) (((Number) mthd.invoke(null, port, colony)).longValue() * colony.getSpace().getCycleCostRate());
    	} catch(Exception ex) {
    		throw new RuntimeException(ex.getMessage(), ex);
    	}
	}
	
	public int getSize() {
		try {
    	    Method mthd = shipClass.getMethod("getMetaSize");
    	    return ((Number) mthd.invoke(null)).intValue();
    	} catch(Exception ex) {
    		throw new RuntimeException(ex.getMessage(), ex);
    	}
	}
	
	public boolean isBuildAvail(Port port, Colony colony) {
		try {
    	    Method mthd = shipClass.getMethod("getMetaBuildAvail", Port.class, Colony.class);
    	    Object obj = mthd.invoke(null, port, colony);
    	    if(obj == null) return true;
    	    if(DataUtil.isEmpty(obj)) return true;
    	    return false;
    	} catch(Exception ex) {
    		throw new RuntimeException(ex.getMessage(), ex);
    	}
	}
	
	@Override
	public String toString() {
		try {
    	    Method mthd = shipClass.getMethod("getMetaName");
    	    return (String) mthd.invoke(null);
    	} catch(Exception ex) {
    		throw new RuntimeException(ex.getMessage(), ex);
    	}
	}
	public Class<?> getShipClass() {
		return shipClass;
	}
	public void setShipClass(Class<?> shipClass) {
		this.shipClass = shipClass;
	}
}
