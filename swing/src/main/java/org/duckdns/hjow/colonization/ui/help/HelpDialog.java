package org.duckdns.hjow.colonization.ui.help;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

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
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.ClassUtil;

/** 도움말 대화상자 */
public class HelpDialog implements Disposeable {
    protected transient GUIColonyManager superInstance;
    protected transient JDialog dialog;
    protected transient JSplitPane splits;
    protected transient JList<HelpContent> listHelp;
    protected transient JEditorPane taContent;
    
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
    	
    	listHelp = new JList<HelpContent>(getHelpContents());
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
    
    /** 도움말 컨텐츠 반환 */
    public Vector<HelpContent> getHelpContents() {
    	Vector<HelpContent> list = new Vector<HelpContent>();
    	
    	InputStream       inp1 = null;
    	InputStreamReader inp2 = null;
    	BufferedReader    inp3 = null;
    	
    	StringBuilder strContent = new StringBuilder("");
    	try {
    		inp1 = this.getClass().getResourceAsStream("content.json");
    		inp2 = new InputStreamReader(inp1, "UTF-8");
    		inp3 = new BufferedReader(inp2);
    		String line;
    		while(true) {
    			line = inp3.readLine();
    			if(line == null) break;
    			strContent = strContent.append("\n").append(line);
    		}
    		
    		ClassUtil.closeAll(inp3, inp2, inp1);
    		inp3 = null;
    		inp2 = null;
    		inp1 = null;
    		
    		JsonArray arr = (JsonArray) JsonObject.parseJson(strContent.toString().trim());
    		strContent = null;
    		
    		for(Object obj : arr) {
    			JsonObject json = (JsonObject) obj;
    			list.add(new HelpContent( json.get("name").toString(), json.get("contentType") == null ? "text/plain" : json.get("contentType").toString(), json.get("content").toString() ));
    		}
    		
    	} catch(Exception ex) {
    		throw new RuntimeException(ex.getMessage(), ex);
    	} finally {
    		ClassUtil.closeAll(inp3, inp2, inp1);
    	}
    	
    	return list;
    }
    
    /** 리스트 선택 시 호출됨 */
    protected void onListSelected() {
    	HelpContent sels = listHelp.getSelectedValue();
    	if(sels == null) { 
    		taContent.setText("");
    	} else {
    		taContent.setContentType(sels.getContentType() == null ? "text/plain" : sels.getContentType());
    		taContent.setText(sels.getContent() == null ? "" : sels.getContent().trim());
    	}
    }
    
    /** 대화 상자 열기 */
    public void open() {
    	dialog.setTitle(ColonyManager.t("도움말"));
    	
    	if(listHelp.getModel().getSize() >= 1) listHelp.setSelectedIndex(0);
    	onListSelected();
    	
    	splits.setDividerLocation(0.3);
    	dialog.setVisible(true);
    }

	@Override
	public void dispose() {
		if(dialog != null) dialog.setVisible(false);
		superInstance = null;
		dialog = null;
	}
}
