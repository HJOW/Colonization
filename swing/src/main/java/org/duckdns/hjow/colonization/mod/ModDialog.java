package org.duckdns.hjow.colonization.mod;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.duckdns.hjow.colonization.ui.GUIColonyManager;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.util.GUIUtil;

/** MOD 대화상자 */
public class ModDialog implements Disposeable {
    protected JDialog dialog;
    protected Mod mod;
    
    protected transient boolean openedOnce = false;
    
    public ModDialog(Window win, Mod mod) {
        dialog = new JDialog(win);
        this.mod = mod;
        
        dialog.setTitle(mod.getName());
        dialog.setSize(500, 400);
        dialog.setIconImage(GUIColonyManager.getIcon());
        GUIUtil.centerWindow(dialog);
        
        dialog.setLayout(new BorderLayout());
        
        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BorderLayout());
        dialog.add(pnMain, BorderLayout.CENTER);
        
        pnMain.add((Component) mod.getComponent(), BorderLayout.CENTER);
    }
    
    public void open() {
        openedOnce = true;
        dialog.setVisible(true);
    }

    @Override
    public void dispose() {
        if(dialog != null) dialog.setVisible(false);
        mod = null;
    }
    
    public boolean isOpenedOnce() {
        return openedOnce;
    }
}
