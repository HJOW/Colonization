package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.ColonyManagerInterface;
import org.duckdns.hjow.colonization.GUIColonyManagerInterface;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.facilities.Port;
import org.duckdns.hjow.colonization.elements.facilities.PortItem;
import org.duckdns.hjow.colonization.elements.ship.Ship;
import org.duckdns.hjow.colonization.ui.ask.ShipOrderDialog;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.ui.graphics.Coordinate3D;
import org.duckdns.hjow.commons.util.GUIUtil;

/** 함선 하나의 현황 출력 및 컨트롤 화면 */
public class ShipPanel extends JPanel implements Disposeable {
	private static final long serialVersionUID = -3246934564467455324L;
	protected Colony colony;
	protected Port   port;
	protected Ship   ship;
	
	protected transient JTextField   tfName, tfX, tfY, tfZ, tfDestX, tfDestY, tfDestZ;
	protected transient JTextArea    ta;
	protected transient JButton      btnOrder, btnMovePort;
	protected transient JProgressBar progHp;
	protected transient ShipMovementIndicator indicator;
	protected transient GUIColonyManagerInterface superInstance;
	
	protected transient boolean flagDestChangeEvent = false;
	
	public ShipPanel() { init(); }
	public ShipPanel(Ship ship, Port port, Colony colony, GUIColonyManagerInterface superInstance) { this(); this.ship = ship; this.port = port; this.colony = colony; this.superInstance = superInstance; }
	
	/** UI 초기화 */
	protected void init() {
		setLayout(new BorderLayout());
		
		JPanel pnUp, pnCenter, pnBottom;
		pnUp     = new JPanel();
		pnCenter = new JPanel();
		pnBottom = new JPanel();
		pnUp.setLayout(new BorderLayout());
		pnBottom.setLayout(new BorderLayout());
		add(pnUp    , BorderLayout.NORTH);
		add(pnCenter, BorderLayout.CENTER);
		add(pnBottom, BorderLayout.SOUTH);
		
		tfName = new JTextField();
		pnUp.add(tfName, BorderLayout.WEST);
		
		JPanel pnStatus = new JPanel();
		pnStatus.setLayout(new BorderLayout());
		pnUp.add(pnStatus, BorderLayout.EAST);
		
		JPanel pnHp = new JPanel();
		pnHp.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnStatus.add(pnHp, BorderLayout.CENTER);
		
		btnOrder = new JButton(ColonyManager.t("명령"));
		btnMovePort = new JButton(ColonyManager.t("항구 변경"));
		pnHp.add(btnOrder);
		pnHp.add(btnMovePort);
		
		progHp = new JProgressBar(JProgressBar.HORIZONTAL);
		pnHp.add(progHp);
		
		ta = new JTextArea();
		ta.setEditable(false);
		pnBottom.add(ta, BorderLayout.CENTER);
		
		pnStatus = new JPanel();
		pnStatus.setLayout(new BorderLayout());
		pnUp.add(pnStatus, BorderLayout.CENTER);
		
		JPanel pnLeft, pnRight, pnLb;
		pnLeft  = new JPanel();
		pnRight = new JPanel();
		pnLeft.setLayout(new BorderLayout());
		pnRight.setLayout(new GridLayout(1, 7));
		pnStatus.add(pnLeft , BorderLayout.CENTER);
		pnStatus.add(pnRight, BorderLayout.EAST);
		
		tfX = new JTextField(); tfX.setEditable(false); pnRight.add(tfX);
		tfY = new JTextField(); tfY.setEditable(false); pnRight.add(tfY);
		tfZ = new JTextField(); tfZ.setEditable(false); pnRight.add(tfZ);
		
		pnLb = new JPanel();
		pnLb.setLayout(new FlowLayout(FlowLayout.CENTER));
		indicator = new ShipMovementIndicator();
		pnLb.add(indicator);
		pnRight.add(pnLb);
		
		tfDestX = new JTextField(); tfDestX.setEditable(false); pnRight.add(tfDestX);
		tfDestY = new JTextField(); tfDestY.setEditable(false); pnRight.add(tfDestY);
		tfDestZ = new JTextField(); tfDestZ.setEditable(false); pnRight.add(tfDestZ);
		
		tfName.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				if(ship == null) { tfName.setText(""); return; }
				ship.setName(tfName.getText());
			}
		});
		
		btnOrder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onChangeDestination();
			}
		});
		
		btnMovePort.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				onMovePortRequested();
			}
		});
		btnMovePort.setVisible(false);
	}
	
	/** 항구 변경 요청 시 호출 */
	protected void onMovePortRequested() {
		PortSelectionDialog d = new PortSelectionDialog(superInstance, colony, ship);
		d.open();
	}
	
	/** 목적지 변경 요청 시 호출 */
	protected void onChangeDestination() {
		Coordinate3D coordinate = ShipOrderDialog.ask(superInstance.getDialog(), ColonyManager.t("좌표 입력"), ColonyManager.t("목적지 좌표를 입력해 주세요.\n시뮬레이션 시작 시, 현재 위치와 목적지가 다르면 이동을 시작합니다."), ship.getCoordinate(), port.getCity(colony).getCoordinate());
		if(coordinate == null) return;
		
		ship.setDestination(coordinate);
		tfDestX.setText(String.valueOf(coordinate.getX()));
		tfDestY.setText(String.valueOf(coordinate.getY()));
		tfDestZ.setText(String.valueOf(coordinate.getZ()));
	}
	
	/** 컴포넌트 활성화 여부 일괄 지정 */
	public void setEditable(boolean editable) {
		if(btnMovePort != null) btnMovePort.setEnabled(editable);
		if(btnOrder    != null) btnOrder.setEnabled(editable);
	}
	
	@Override
	public void dispose() {
		ship = null;
		port = null;
		colony = null;
		superInstance = null;
	}
	
	/** 화면 새로고침 시 호출 */
	public void refresh(int cycle, Colony colony, ColonyManagerInterface superInstance) {
		ship = getShip(colony);
		if(ship == null) {
			tfName.setText("");
			tfX.setText(String.valueOf(0));
			tfY.setText(String.valueOf(0));
			tfZ.setText(String.valueOf(0));
			tfDestX.setText(String.valueOf(0L));
			tfDestY.setText(String.valueOf(0L));
			tfDestZ.setText(String.valueOf(0L));
			ta.setText("");
			progHp.setValue(0);
			return;
		}
		
		tfName.setText(ship.getName());
		tfX.setText(String.valueOf(ship.getX()));
		tfY.setText(String.valueOf(ship.getY()));
		tfZ.setText(String.valueOf(ship.getZ()));
		tfDestX.setText(String.valueOf(ship.getDestinationX()));
		tfDestY.setText(String.valueOf(ship.getDestinationY()));
		tfDestZ.setText(String.valueOf(ship.getDestinationZ()));
		
		progHp.setMaximum(ship.getMaxHp());
		progHp.setValue(ship.getHp());
		
		ta.setText(ship.getStatusString(colony, superInstance));
		indicator.refresh(cycle, ship, colony, superInstance);
		
		// btnMovePort.setVisible(false);
		City city = colony.findCity(ship);
		btnMovePort.setVisible((ship.getX() == city.getX() && ship.getY() == city.getY() && ship.getZ() == city.getZ()));
	}
	
	/** Ship 객체 반환 */
	public Ship getShip(Colony col) {
		if(col.getShip(ship.getKey()) == null) { ship = null; return null; }
		return ship;
	}
}


/** 우주공항 선택 대화상자 */
class PortSelectionDialog implements Disposeable {
	protected GUIColonyManagerInterface superInstance;
	protected Colony colony;
	protected Ship   ship;
	
	protected transient List<Port> excepts = new ArrayList<Port>();
	protected transient JDialog dialog;
	protected transient JComboBox<PortItem> cbxPort;
	protected transient JButton btnOk, btnCancel;
	protected transient ActionListener actionOk, actionCancel;
	
	public PortSelectionDialog(GUIColonyManagerInterface superInstance, Colony colony, Ship ship) {
		this.superInstance = superInstance;
		this.colony = colony;
		this.ship = ship;
		
		dialog = new JDialog(superInstance.getDialog(), true);
		dialog.setSize(400, 250);
		GUIUtil.centerWindow(dialog);
		dialog.setTitle(ColonyManager.t("우주공항 목록"));
		dialog.setIconImage(GUIColonyManager.getIcon());
		dialog.setLayout(new BorderLayout());
	    dialog.addWindowListener(new WindowAdapter() {
	    	@Override
	    	public void windowClosing(WindowEvent e) {
	    		dispose();
	    	}
		});
	    
	    JPanel pnMain = new JPanel();
	    pnMain.setLayout(new BorderLayout());
	    dialog.add(pnMain, BorderLayout.CENTER);
	    
	    JPanel pnCenter, pnDown;
	    pnCenter = new JPanel();
	    pnDown   = new JPanel();
	    pnCenter.setLayout(new BorderLayout());
	    pnDown.setLayout(new FlowLayout(FlowLayout.RIGHT));
	    pnMain.add(pnCenter, BorderLayout.CENTER);
	    pnMain.add(pnDown  , BorderLayout.SOUTH);
	    
	    cbxPort = new JComboBox<PortItem>();
	    pnCenter.add(cbxPort, BorderLayout.NORTH);
	    
	    actionOk = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PortItem pi = (PortItem) cbxPort.getSelectedItem();
				if(pi == null) { superInstance.alert(ColonyManager.t("해당 우주공항에 정박할 수 없습니다.")); return; }
				
				Port p = pi.getPort(colony);
				if(p == null) { superInstance.alert(ColonyManager.t("해당 우주공항에 정박할 수 없습니다.")); return; }
				
				if(p.leftShipSpaces() < ship.getSize()) { superInstance.alert(ColonyManager.t("해당 우주공항에는 이 함선이 들어갈 공간이 부족합니다.")); return; }
				
				colony.removeShip(ship);
				p.addShip(ship);
				
				superInstance.refreshColonyContent();
				dispose();
			}
		};
	    
		actionCancel = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};
		
		btnOk     = new JButton("확인");
		btnCancel = new JButton("취소");
		pnDown.add(btnOk);
		pnDown.add(btnCancel);
		
		btnOk.addActionListener(actionOk);
		btnCancel.addActionListener(actionCancel);
	}
	
	public void open() {
		cbxPort.removeAllItems();
		
		List<Port> ports = colony.getPorts();
		for(Port p : ports) {
			if(excepts.contains(p)) continue;
			
			// 가능 여부 판단
			if(p.leftShipSpaces() < ship.getSize()) continue;
			
			// 추가
			cbxPort.addItem(new PortItem(p));
		}
		
		dialog.setVisible(true);
	}
	
	public void registerExcepts(Port p) {
		excepts.add(p);
	}
	
	@Override
	public void dispose() {
		if(dialog != null) {
			if(dialog.isVisible()) dialog.setVisible(false);
			dialog.removeAll();
			dialog = null;
		}
		if(btnOk != null && actionOk != null) {
			btnOk.removeActionListener(actionOk);
			btnOk = null; actionOk = null;
		}
		if(btnCancel != null && actionCancel != null) {
			btnCancel.removeActionListener(actionCancel);
			btnCancel = null; actionCancel = null;
		}
		superInstance = null;
		colony = null;
		ship = null;
	}
}