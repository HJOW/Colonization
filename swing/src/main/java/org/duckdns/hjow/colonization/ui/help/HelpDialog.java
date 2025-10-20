package org.duckdns.hjow.colonization.ui.help;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.ui.GUIColonyManager;
import org.duckdns.hjow.commons.core.Disposeable;

/** 도움말 대화상자 */
public class HelpDialog implements Disposeable {
    protected GUIColonyManager superInstance;
    protected JDialog dialog;
    protected JSplitPane splits;
    protected JList<HelpContent> listHelp;
    protected JEditorPane taContent;
    
    public HelpDialog(GUIColonyManager superInstance) {
    	this.superInstance = superInstance;
    	dialog = new JDialog(superInstance.getDialog());
    	dialog.setSize(600, 500);
    	dialog.setLayout(new BorderLayout());
    	
    	JPanel pnMain = new JPanel();
    	pnMain.setLayout(new BorderLayout());
    	dialog.add(pnMain, BorderLayout.CENTER);
    	
    	splits = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    	pnMain.add(splits, BorderLayout.CENTER);
    	
    	listHelp = new JList<HelpContent>();
    	splits.setLeftComponent(new JScrollPane(listHelp));
    	
    	listHelp.addListSelectionListener(new ListSelectionListener() {	
			@Override
			public void valueChanged(ListSelectionEvent e) {
				onListSelected();
			}
		});
    	
    	taContent = new JEditorPane();
    	taContent.setEditable(false);
    	splits.setRightComponent(new JScrollPane(taContent));
    }
    
    /** 리스트 선택 시 호출됨 */
    protected void onListSelected() {
    	HelpContent sels = listHelp.getSelectedValue();
    	if(sels == null) { 
    		taContent.setText("");
    	} else {
    		taContent.setContentType(sels.getContentType());
    		taContent.setText(sels.getContent());
    	}
    }
    
    /** 대화 상자 열기 */
    public void open() {
    	dialog.setTitle(ColonyManager.t("도움말"));
    	splits.setDividerLocation(0.3);
    	onListSelected();
    	
    	dialog.setVisible(true);
    }

	@Override
	public void dispose() {
		if(dialog != null) dialog.setVisible(false);
		superInstance = null;
		dialog = null;
	}
}
