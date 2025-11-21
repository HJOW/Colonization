package org.duckdns.hjow.colonization.ui.ask;

import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.ui.GUIColonyManager;
import org.duckdns.hjow.commons.ui.graphics.Coordinate3D;

/** 함선 명령 대화상자 */
public class ShipOrderDialog extends CoordinateDialog {
	private static final long serialVersionUID = 6230755286202489876L;
	protected transient JButton btnStop, btnComeback;
	protected transient Coordinate3D shipCurrentLocation, portLocation;

	protected ShipOrderDialog(JFrame win, String title, String msg, Coordinate3D shipCurrentLocation, Coordinate3D portLocation) {
		super(win, title, msg, shipCurrentLocation);
		this.shipCurrentLocation = shipCurrentLocation;
		this.portLocation = portLocation;
    }
	
	protected ShipOrderDialog(JDialog win, String title, String msg, Coordinate3D shipCurrentLocation, Coordinate3D portLocation) {
		super(win, title, msg, shipCurrentLocation);
		this.shipCurrentLocation = shipCurrentLocation;
		this.portLocation = portLocation;
    }
	
	@Override
	protected void init(Window win, String title, String msg, Coordinate3D shipCurrentLocation) {
		super.init(win, title, msg, shipCurrentLocation);
		this.shipCurrentLocation = shipCurrentLocation;
	}
	
	protected void controlPanelLayout() {
		btnStop     = new JButton(ColonyManager.t("정지"));
		btnComeback = new JButton(ColonyManager.t("복귀"));
		
		pnCtrl.add(btnOk);
		pnCtrl.add(btnStop);
		pnCtrl.add(btnComeback);
		pnCtrl.add(btnCancel);
		
		btnStop.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				results = shipCurrentLocation;
				onClose();
			}
		});
		
		btnComeback.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				results = portLocation;
				onClose();
			}
		});
	}
	
	@Override
	protected String getOkMsg() {
		return ColonyManager.t("확인");
	}
	
	@Override
	protected String getCancelMsg() {
		return ColonyManager.t("취소");
	}
	
	@Override
	protected Image getDialogIconImage() {
		return GUIColonyManager.getIcon();
	}
	
	/** 좌표 입력받기 */
	public static Coordinate3D ask(Window win, String title, String msg, Coordinate3D shipCurrentLocation, Coordinate3D portLocation) {
		CoordinateDialog diag = null;
		if(     win instanceof JFrame ) diag = new ShipOrderDialog((JFrame)  win, title, msg, shipCurrentLocation, portLocation);
		else if(win instanceof JDialog) diag = new ShipOrderDialog((JDialog) win, title, msg, shipCurrentLocation, portLocation);
		else return null;
		
		diag.setVisible(true);
		return diag.getResult();
	}
}
