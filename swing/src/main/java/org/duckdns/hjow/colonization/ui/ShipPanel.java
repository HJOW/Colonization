package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.ColonyManagerInterface;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.facilities.Port;
import org.duckdns.hjow.colonization.elements.ship.Ship;
import org.duckdns.hjow.colonization.ui.ask.ShipOrderDialog;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.ui.graphics.Coordinate3D;

/** 함선 하나의 현황 출력 및 컨트롤 화면 */
public class ShipPanel extends JPanel implements Disposeable {
	private static final long serialVersionUID = -3246934564467455324L;
	protected Colony colony;
	protected Port   port;
	protected Ship   ship;
	
	protected transient JTextField   tfName, tfX, tfY, tfZ;
	protected transient JSpinner     tfDestX, tfDestY, tfDestZ;
	protected transient JTextArea    ta;
	protected transient JProgressBar progHp;
	protected transient ShipMovementIndicator indicator;
	protected transient ColonyManagerInterface superInstance;
	
	protected transient boolean flagDestChangeEvent = false;
	
	public ShipPanel() { init(); }
	public ShipPanel(Ship ship, Port port, Colony colony, ColonyManagerInterface superInstance) { this(); this.ship = ship; this.port = port; this.colony = colony; this.superInstance = superInstance; }
	
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
		
		JButton btnOrder, btnMovePort;
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
		
		SpinnerNumberModel spNum;
		
		spNum = new SpinnerNumberModel(new Long(0L), new Long(Long.MIN_VALUE), new Long(Long.MAX_VALUE), new Long(1L));
		tfDestX = new JSpinner(spNum);
		tfDestX.setPreferredSize(new Dimension(130, 25));
		pnRight.add(tfDestX);
		
		spNum = new SpinnerNumberModel(new Long(0L), new Long(Long.MIN_VALUE), new Long(Long.MAX_VALUE), new Long(1L));
		tfDestY = new JSpinner(spNum);
		tfDestY.setPreferredSize(new Dimension(130, 25));
		pnRight.add(tfDestY);
		
		spNum = new SpinnerNumberModel(new Long(0L), new Long(Long.MIN_VALUE), new Long(Long.MAX_VALUE), new Long(1L));
		tfDestZ = new JSpinner(spNum);
		tfDestZ.setPreferredSize(new Dimension(130, 25));
		pnRight.add(tfDestZ);
		
		tfName.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				if(ship == null) { tfName.setText(""); return; }
				ship.setName(tfName.getText());
			}
		});
		
		tfDestX.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(! flagDestChangeEvent) return;
				if(! tfDestX.isEnabled()) return;
				onChangeDestination();
			}
		});
		
		tfDestY.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(! flagDestChangeEvent) return;
				if(! tfDestY.isEnabled()) return;
				onChangeDestination();
			}
		});
		
		tfDestZ.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(! flagDestChangeEvent) return;
				if(! tfDestZ.isEnabled()) return;
				onChangeDestination();
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
		btnMovePort.setVisible(false); // TODO
	}
	
	/** 항구 변경 요청 시 호출 */
	protected void onMovePortRequested() {
		// TODO
	}
	
	/** 목적지 변경 요청 시 호출 */
	protected void onChangeDestination() {
		Object diag = superInstance.getDialogObject();
		if(diag == null) return;
		if(! (diag instanceof JFrame)) return;
		
		Coordinate3D coordinate = ShipOrderDialog.ask((JFrame) diag, ColonyManager.t("좌표 입력"), ColonyManager.t("목적지 좌표를 입력해 주세요.\n시뮬레이션 시작 시, 현재 위치와 목적지가 다르면 이동을 시작합니다."), ship.getCoordinate(), port.getCity(colony).getCoordinate());
		if(coordinate == null) return;
		
		ship.setDestination(coordinate);
		tfDestX.setValue(new Long(coordinate.getX()));
		tfDestY.setValue(new Long(coordinate.getY()));
		tfDestZ.setValue(new Long(coordinate.getZ()));
	}
	
	/** 컴포넌트 활성화 여부 일괄 지정 */
	public void setEditable(boolean editable) { }
	
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
			tfDestX.setValue(new Long(0L));
			tfDestY.setValue(new Long(0L));
			tfDestZ.setValue(new Long(0L));
			ta.setText("");
			progHp.setValue(0);
			return;
		}
		
		tfName.setText(ship.getName());
		tfX.setText(String.valueOf(ship.getX()));
		tfY.setText(String.valueOf(ship.getY()));
		tfZ.setText(String.valueOf(ship.getZ()));
		tfDestX.setValue(new Long(ship.getDestinationX()));
		tfDestY.setValue(new Long(ship.getDestinationY()));
		tfDestZ.setValue(new Long(ship.getDestinationZ()));
		
		progHp.setMaximum(ship.getMaxHp());
		progHp.setValue(ship.getHp());
		
		ta.setText(ship.getStatusString(colony, superInstance));
		indicator.refresh(cycle, ship, colony, superInstance);
	}
	
	/** Ship 객체 반환 */
	public Ship getShip(Colony col) {
		if(col.getShip(ship.getKey()) == null) { ship = null; return null; }
		return ship;
	}
}
