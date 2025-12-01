package org.duckdns.hjow.colonization.ui.tools;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.benchmark.BenchmarkManager;
import org.duckdns.hjow.colonization.ui.tools.files.FileManager;
import org.duckdns.hjow.commons.core.Disposeable;

/** 기타 도구들 관리 */
public class ToolManager implements Disposeable {
    protected Vector<Tool> tools = new Vector<Tool>();
    
    public ToolManager(Window win) {
        init(win);
    }
    
    public void init(Window win) {
        tools.clear();
        tools.add(new BenchmarkManager(win));
        tools.add(new Log4JManager(win));
        tools.add(new FileManager(win));
        tools.add(new GUITCPSimpleDaemonManager(win));
        tools.add(new CDOCViewer(win));
        tools.add(new ScriptModEditor(win));
        tools.add(new ScriptFacilityEditor(win));
    }
    
    public Tool getTool(String name) {
        for(Tool t : tools) {
            if(name.equals(t.getName())) return t;
        }
        return null;
    }
    
    public void open(String name) {
        Tool t = getTool(name);
        if(t == null) return;
        if(! t.isAvail()) return;
        t.open();
    }
    
    public void registerJMenu(JMenu menu) {
        for(Tool t : tools) {
            if(! t.isAvail()) continue;
            
            JMenuItem menuItem = new JMenuItem(ColonyManager.t(t.getTitle()));
            menu.add(menuItem);
            final String name = t.getName();
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    open(name);
                }
            });
        }
    }
    
    @Override
    public void dispose() {
        for(Tool t : tools) {
            try { t.dispose(); } catch(Throwable tx) { GlobalLogs.processExceptionOccured(tx, false); }
        }
    }
}
