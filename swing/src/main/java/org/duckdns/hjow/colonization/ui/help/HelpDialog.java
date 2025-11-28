package org.duckdns.hjow.colonization.ui.help;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GUIColonyManagerInterface;
import org.duckdns.hjow.colonization.ui.GUIColonyManager;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.util.GUIUtil;

/** 도움말 대화상자 */
public class HelpDialog implements Disposeable {
    protected transient GUIColonyManagerInterface superInstance;
    protected transient JDialog dialog;
    protected transient JFrame  frame;
    
    protected transient JSplitPane splits;
    protected transient JList<HelpContent> listHelp;
    protected transient JEditorPane taContent;
    
    public HelpDialog() {
    	init(null);
    }
    
    public HelpDialog(GUIColonyManagerInterface superInstance) {
        this.superInstance = superInstance;
        init(superInstance.getDialog());
    }
    
    protected void init(Window win) {
        if(win != null) dialog = new JDialog(win);
        else frame = new JFrame();
        
        int width  = 600;
        int height = 500;
        if(superInstance != null) {
            width  = (int) (superInstance.getDialogWidth() * 0.75);
            height = (int) (superInstance.getDialogHeight() * 0.75);
            
            if(width  < 600) width  = 600;
            if(height < 500) height = 500;
        }
        
        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BorderLayout());
        
        if(frame != null) {
        	frame.setSize(width, height);
            GUIUtil.centerWindow(frame);
            frame.setIconImage(GUIColonyManager.getIcon());
            frame.setLayout(new BorderLayout());
            frame.add(pnMain, BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } else {
        	dialog.setSize(width, height);
            GUIUtil.centerWindow(dialog);
            dialog.setIconImage(GUIColonyManager.getIcon());
            dialog.setLayout(new BorderLayout());
            dialog.add(pnMain, BorderLayout.CENTER);
        }
        
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
        list.addAll(HelpContent.getHelpContentsFrom(this.getClass(), "content.json"));
        
        return list;
    }
    
    /** 리스트 선택 시 호출됨 */
    protected void onListSelected() {
        HelpContent sels = listHelp.getSelectedValue();
        if(sels == null) { 
            taContent.setText("");
        } else {
            taContent.setContentType(sels.getContentType() == null ? "text/plain" : sels.getContentType());
            processStyle(sels);
            taContent.setText(sels.getContent() == null ? "" : sels.getContent().trim());
        }
        taContent.setCaretPosition(0); // 맨 위로
    }
    
    /** 스타일 적용 */
    protected void processStyle(HelpContent content) {
        if(content.getContentType() == null) return;
        if(! content.getContentType().startsWith("text/html")) return;
        
        HTMLEditorKit kit = (HTMLEditorKit) taContent.getEditorKitForContentType(content.getContentType());
        if(kit == null) {
            kit = new HTMLEditorKit();
            taContent.setEditorKit(kit);
        }
        
        StyleSheet styles = kit.getStyleSheet();
        styles.addRule("h2 { font-size: 20px; font-family: 'NanumGothic'; }");
        styles.addRule("h3 { font-size: 16px; font-family: 'NanumGothic'; }");
        styles.addRule("div { font-size: 11px; margin-bottom: 20px; font-family: 'NanumGothic'; }");
        styles.addRule("p { font-size: 11px; font-family: 'NanumGothic'; }");
    }
    
    /** 대화 상자 열기 */
    public void open() {
        if(frame != null) frame.setTitle(ColonyManager.t("도움말"));
        else dialog.setTitle(ColonyManager.t("도움말"));
        
        if(listHelp.getModel().getSize() >= 1) listHelp.setSelectedIndex(0);
        onListSelected();
        
        if(frame != null) frame.setVisible(true);
        else dialog.setVisible(true);
        splits.setDividerLocation(0.3);
    }

    @Override
    public void dispose() {
        if(dialog != null) dialog.setVisible(false);
        if(frame  != null) frame.setVisible(false);
        superInstance = null;
        dialog = null;
        
        if(frame != null) { System.exit(0); return; }
        frame = null;
    }
}
