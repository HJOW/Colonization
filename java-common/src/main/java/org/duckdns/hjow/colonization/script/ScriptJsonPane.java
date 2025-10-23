
package org.duckdns.hjow.colonization.script;

import java.awt.Component;
import java.math.BigDecimal;
import java.util.List;

import org.duckdns.hjow.commons.core.Releasable;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.script.PublicMethodOpenedClass;
import org.duckdns.hjow.commons.ui.RestrictedJsonPane;
import org.duckdns.hjow.commons.util.DataUtil;

public class ScriptJsonPane extends PublicMethodOpenedClass implements Releasable {
    private static final long serialVersionUID = 2177488459987647325L;
    protected RestrictedJsonPane jsonPane;
    public ScriptJsonPane() {
        jsonPane = new RestrictedJsonPane();
    }
    public ScriptJsonPane(Object scripts) {
        this();
        init(scripts);
    }
    
    public void init(Object scripts) {
        if(scripts instanceof JsonObject) {
            jsonPane.init((JsonObject) scripts);
        } else {
            jsonPane.init((JsonObject) DataUtil.parseJson(String.valueOf(scripts)));
        }
    }
    
    @Override
    public void releaseResource() {
        try { jsonPane.releaseResource(); } catch(Throwable t) {}
        jsonPane = null;
    }
    
    public Component getRootComponent() {
        return jsonPane.getRootComponent();
    }
    
    
    public Object findById(Object compIdObj) {
        long compId = -1;
        if(compIdObj instanceof Number) {
            compId = ((Number) compIdObj).longValue();
        } else {
            compId = new BigDecimal(String.valueOf(compIdObj)).longValue();
        }
        return jsonPane.findById(compId);
    }
    
    public Object findByName(Object name) {
        return jsonPane.findByName(String.valueOf(name));
    }
    
    public List<Object> findsByType(Object types) {
        return jsonPane.findsByType(String.valueOf(types));
    }
    
    public List<Object> findsByTag(Object tag) {
        return jsonPane.findsByTag(String.valueOf(tag));
    }
}
