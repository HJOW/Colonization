package org.duckdns.hjow.colonization.elements.facilities.script;

import javax.script.ScriptEngine;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.facilities.DefaultFacility;
import org.duckdns.hjow.colonization.elements.facilities.ScriptFacilityInformation;
import org.duckdns.hjow.commons.json.JsonObject;

/** 스크립트 기반 잃반 시설 */
public class ScriptFacility extends DefaultFacility {
    private static final long serialVersionUID = 6565598463462210082L;
    protected transient ScriptFacilityInformation info;
    protected transient ScriptEngine engine;
    protected transient JsonObject storage = new JsonObject();
    
    public ScriptFacility() { super(); }
    public ScriptFacility(ScriptFacilityInformation info, ScriptEngine engine) {
        super();
        this.info = info;
        this.engine = engine;
        this.engine.put("storage", storage);
    }

    @Override
    public String getType() {
        return getName();
    }
    
    @Override
    public String getName() {
        return info.getName();
    }
    
    @Override
    public String getTooltip() {
        return info.getDescription();
    }
    
    @Override
    public int getMaxHp() {
        try { return Integer.parseInt(String.valueOf(ColonyManager.evaluate(engine, "getMaxHp()"))); } catch(Throwable tx) { throw new RuntimeException(tx.getMessage(), tx); }
    }
    
    @Override
    public short getDefenceType() {
        try { return (short) Integer.parseInt(String.valueOf(ColonyManager.evaluate(engine, "getDefenceType()"))); } catch(Throwable tx) { throw new RuntimeException(tx.getMessage(), tx); }
    }
    
    @Override
    public long usingFee() {
        try { return Long.parseLong(String.valueOf(ColonyManager.evaluate(engine, "usingFee()"))); } catch(Throwable tx) { throw new RuntimeException(tx.getMessage(), tx); } 
    }
    
    @Override
    public long getMaintainFee(City city, Colony colony) {
        try {
            engine.put("__city"  , city.toJson());
            engine.put("__colony", colony.toJson());
            return Long.parseLong(String.valueOf(ColonyManager.evaluate(engine, "getMaintainFee(__city, __colony)"))); 
        } catch(Throwable tx) { throw new RuntimeException(tx.getMessage(), tx); }
    }
    
    @Override
    public long getDestructionFee(City city, Colony colony) {
        try {
            engine.put("__city"  , city.toJson());
            engine.put("__colony", colony.toJson());
            return Long.parseLong(String.valueOf(ColonyManager.evaluate(engine, "getDestructionFee(__city, __colony)"))); 
        } catch(Throwable tx) { throw new RuntimeException(tx.getMessage(), tx); }
    }

    @Override
    public int getPowerConsume() {
        try { return Integer.parseInt(String.valueOf(ColonyManager.evaluate(engine, "getPowerConsume()"))); } catch(Throwable tx) { throw new RuntimeException(tx.getMessage(), tx); }
    }

    @Override
    public int getWorkerSuitability(Citizen citizen) {
        try {
            engine.put("__citizen", citizen.toJson());
            return Integer.parseInt(String.valueOf(ColonyManager.evaluate(engine, "getWorkerSuitability(__citizen)"))); 
        } catch(Throwable tx) { throw new RuntimeException(tx.getMessage(), tx); }
    }

    @Override
    protected String getDefaultNamePrefix() {
        try { return String.valueOf(ColonyManager.evaluate(engine, "getDefaultNamePrefix()")); } catch(Throwable tx) { throw new RuntimeException(tx.getMessage(), tx); }
    }
    
    @Override
    public String getStatusDescription(City city, Colony colony) {
        return info.getDescription();
    }
    
    @Override
    public void fromJson(JsonObject json) {
        super.fromJson(json);
        
        storage = (JsonObject) json.get("storage");
        if(storage == null) storage = new JsonObject();
        
        engine.put("storage", storage);
    }
    
    @Override
    public JsonObject toJson(boolean details, Colony col, City city, boolean excludeSecrets) {
        JsonObject json = super.toJson(details, col, city, excludeSecrets);
        json.put("storage", storage);
        return json;
    }
    
    @Override
    public void dispose() {
        super.dispose();
        storage.clear();
        engine = null;
        info   = null;
    }
}
