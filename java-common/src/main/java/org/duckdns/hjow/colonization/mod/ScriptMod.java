package org.duckdns.hjow.colonization.mod;

import javax.script.ScriptEngine;
import javax.swing.JPanel;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.ColonyManagerInterface;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.script.ScriptUsingObject;
import org.duckdns.hjow.commons.util.DataUtil;

public class ScriptMod implements Mod, ScriptUsingObject {
    private static final long serialVersionUID = -8887470668482563210L;
    protected transient ScriptEngine engine;
    protected transient Object parent;
    
    protected transient JPanel panel;
    
    protected transient boolean flagChecking = false;
    
    @Override
    public void injectScriptEngine(ScriptEngine engine) {
        this.engine = engine;
        panel = new JPanel();
        this.engine.put("__panel", panel);
    }
    
    /** 부모 컴포넌트 객체 주입 */
    public void injectParentComponent(Object parent) {
        this.parent = parent;
        this.engine.put("__parent", parent);
    }
    
    @Override
    public void init(ColonyManagerInterface manager) {
        try { ColonyManager.evaluate(engine, "init(__panel)"); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
    }

    @Override
    public String getName() {
        try { return String.valueOf(ColonyManager.evaluate(engine, "getName()")); } catch(Exception ex) { if(flagChecking) throw new RuntimeException(ex.getMessage(), ex); GlobalLogs.processExceptionOccured(ex, false); }
        return null;
    }

    @Override
    public String getDescription() {
        try { return String.valueOf(ColonyManager.evaluate(engine, "getDescription()")); } catch(Exception ex) { if(flagChecking) throw new RuntimeException(ex.getMessage(), ex); GlobalLogs.processExceptionOccured(ex, false); }
        return null;
    }

    @Override
    public int getLocation() {
        try { return Integer.parseInt(String.valueOf(ColonyManager.evaluate(engine, "getLocation()"))); } catch(Exception ex) { if(flagChecking) throw new RuntimeException(ex.getMessage(), ex); GlobalLogs.processExceptionOccured(ex, false); }
        return 0;
    }

    @Override
    public Object getComponent() {
        return panel;
    }

    @Override
    public void refresh(int cycle, Object colony, ColonyManagerInterface manager) {
        engine.put("__cycle" , new Integer(cycle));
        engine.put("__colony", colony);
        engine.put("__manager", manager);
        try { ColonyManager.evaluate(engine, "refresh(__panel, __cycle, __colony, __manager)"); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
    }

    @Override
    public boolean isReadOnly() {
        try { return DataUtil.parseBoolean(String.valueOf(ColonyManager.evaluate(engine, "isReadOnly()"))); } catch(Exception ex) { if(flagChecking) throw new RuntimeException(ex.getMessage(), ex); GlobalLogs.processExceptionOccured(ex, false); }
        return true;
    }
    
    @Override
    public void dispose() {
        try { ColonyManager.evaluate(engine, "dispose(__panel)"); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        engine = null;
        parent = null;
        
        if(panel != null) panel.removeAll();
        panel = null;
    }
    
    /** 스크립트 모드 체크 메소드, 체크 실패 시 예외 발생 */
    public void check() {
        try {
            flagChecking = true;
            
            // 이름 체크
            String val = String.valueOf(ColonyManager.evaluate(engine, "getName()"));
            if(DataUtil.isEmpty(val)) throw new NullPointerException("getName() return value cannot be null !");
            
            // 설명 체크
            val = String.valueOf(ColonyManager.evaluate(engine, "getDescription()"));
            if(val == null) throw new NullPointerException("getDescription() return value cannot be null !");
            
            // 읽기 전용 체크
            String.valueOf(ColonyManager.evaluate(engine, "isReadOnly()"));
            
            // location 코드 체크
            String.valueOf(ColonyManager.evaluate(engine, "getLocation()"));
            
            flagChecking = false;
        } catch(RuntimeException ex) {
            flagChecking = false;
            throw ex;
        } catch(Exception ex) {
            flagChecking = false;
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
