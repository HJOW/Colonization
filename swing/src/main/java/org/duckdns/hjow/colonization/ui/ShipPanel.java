package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.duckdns.hjow.colonization.ColonyManagerInterface;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ship.Ship;
import org.duckdns.hjow.commons.core.Disposeable;

/** 함선 하나의 현황 출력 및 컨트롤 화면 */
public class ShipPanel extends JPanel implements Disposeable {
	private static final long serialVersionUID = -3246934564467455324L;
	protected Ship ship;
	
	protected transient JTextField   tfName, tfNow;
	protected transient JSpinner     tfDestX, tfDestY, tfDestZ;
	protected transient JTextArea    ta;
	protected transient JProgressBar progHp;
	protected transient ColonyManagerInterface superInstance;
	
	public ShipPanel() { init(); }
	public ShipPanel(Ship ship, ColonyManagerInterface superInstance) { this(); this.ship = ship; this.superInstance = superInstance; }
	
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
		
		progHp = new JProgressBar(JProgressBar.HORIZONTAL);
		pnStatus.add(progHp, BorderLayout.CENTER);
		
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
		pnRight.setLayout(new GridLayout(1, 4));
		pnStatus.add(pnLeft , BorderLayout.CENTER);
		pnStatus.add(pnRight, BorderLayout.EAST);
		
		tfNow = new JTextField(20);
		tfNow.setEditable(false);
		pnLeft.add(tfNow);
		
		pnLb = new JPanel();
		pnLb.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnLb.add(new JLabel("→"));
		pnRight.add(pnLb);
		
		SpinnerNumberModel spNum;
		
		spNum = new SpinnerNumberModel(new Long(0L), new Long(Long.MIN_VALUE), new Long(Long.MAX_VALUE), new Long(1L));
		tfDestX = new JSpinner(spNum);
		tfDestX.setPreferredSize(new Dimension(150, 30));
		pnRight.add(tfDestX);
		
		spNum = new SpinnerNumberModel(new Long(0L), new Long(Long.MIN_VALUE), new Long(Long.MAX_VALUE), new Long(1L));
		tfDestY = new JSpinner(spNum);
		tfDestY.setPreferredSize(new Dimension(150, 30));
		pnRight.add(tfDestY);
		
		spNum = new SpinnerNumberModel(new Long(0L), new Long(Long.MIN_VALUE), new Long(Long.MAX_VALUE), new Long(1L));
		tfDestZ = new JSpinner(spNum);
		tfDestZ.setPreferredSize(new Dimension(150, 30));
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
				Number num = (Number) tfDestX.getValue();
				ship.setDestinationX(num.longValue());
			}
		});
		
		tfDestY.addChangeListener(new ChangeListener() {	
			@Override
			public void stateChanged(ChangeEvent e) {
				Number num = (Number) tfDestY.getValue();
				ship.setDestinationY(num.longValue());
			}
		});
		
		tfDestZ.addChangeListener(new ChangeListener() {	
			@Override
			public void stateChanged(ChangeEvent e) {
				Number num = (Number) tfDestZ.getValue();
				ship.setDestinationZ(num.longValue());
			}
		});
	}
	
	@Override
	public void dispose() {
		ship = null;
		superInstance = null;
	}
	
	/** 화면 새로고침 시 호출 */
	public void refresh(int cycle, Colony colony, ColonyManagerInterface superInstance) {
		ship = getShip(colony);
		if(ship == null) {
			tfName.setText("");
			tfNow.setText("0, 0, 0");
			tfDestX.setValue(new Long(0L));
			tfDestY.setValue(new Long(0L));
			tfDestZ.setValue(new Long(0L));
			ta.setText("");
			return;
		}
		
		tfName.setText(colony.getName());
		tfNow.setText(String.valueOf(ship.getX()) + ", " + String.valueOf(ship.getY()) + ", " + String.valueOf(ship.getZ()));
		tfDestX.setValue(new Long(ship.getDestinationX()));
		tfDestY.setValue(new Long(ship.getDestinationY()));
		tfDestZ.setValue(new Long(ship.getDestinationZ()));
		ta.setText(ship.getStatusString(colony, superInstance));
	}
	
	/** Ship 객체 반환 */
	public Ship getShip(Colony col) {
		if(col.getShip(ship.getKey()) == null) { ship = null; return null; }
		return ship;
	}
}
