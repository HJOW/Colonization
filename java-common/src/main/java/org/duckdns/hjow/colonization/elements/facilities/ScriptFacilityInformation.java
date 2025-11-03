package org.duckdns.hjow.colonization.elements.facilities;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.research.ResearchCondition;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;

/** 시설 정보 (스크립트 기반) */
public class ScriptFacilityInformation extends FacilityInformation implements Disposeable {
	private static final long serialVersionUID = -8200689983132535331L;
	protected String scripts = "";
	protected transient ScriptEngine engine = null;
    public ScriptFacilityInformation() {}
    public ScriptFacilityInformation(ScriptEngine engine, String scripts) { this(); init(engine, scripts); }
    protected void init(ScriptEngine engine, String scripts) {
    	this.engine  = engine;
    	this.scripts = scripts;
    	try {
    		// 스크립트 실행 - 함수 정의 (리플렉션 검사는 이 객체 생성 전에 수행)
    		engine.eval(scripts); 
    		
    		// 함수 존재여부 확인 (함수가 없으면 예외가 발생할 것이므로)
    		getName();
    		getTitle();
    		getDescription();
    		getPrice();
    		getBuildingCycle();
    		getSpaceSize();
    		getUniqueGrade();
    		getTech();
    	} catch(RuntimeException tx) { throw tx;
        } catch(Throwable        tx) { throw new RuntimeException(tx.getMessage(), tx); }
    }
    
    @Override
    public String getName() {
    	try { return String.valueOf(engine.eval("getName()")); } catch(Throwable tx) { throw new RuntimeException(tx.getMessage(), tx); }
    }
    
    @Override
    public String getTitle() {
    	try { return String.valueOf(engine.eval("getTitle()")); } catch(Throwable tx) { throw new RuntimeException(tx.getMessage(), tx); }
    }
    
    @Override
    public String getDescription() {
    	try { return String.valueOf(engine.eval("getDescription()")); } catch(Throwable tx) { throw new RuntimeException(tx.getMessage(), tx); }
    }
    
    @Override
    public Long getPrice() {
    	try { return new Long(String.valueOf(engine.eval("getPrice()"))); } catch(Throwable tx) { throw new RuntimeException(tx.getMessage(), tx); }
    }
    
    @Override
    public int getBuildingCycle() {
    	try { return Integer.parseInt(String.valueOf(engine.eval("getBuildingCycle()"))); } catch(Throwable tx) { throw new RuntimeException(tx.getMessage(), tx); }
    }
    
    @Override
    public int getSpaceSize() {
    	try { return Integer.parseInt(String.valueOf(engine.eval("getSpaceSize()"))); } catch(Throwable tx) { throw new RuntimeException(tx.getMessage(), tx); }
    }
    
    @Override
    public int getUniqueGrade() {
    	try { return Integer.parseInt(String.valueOf(engine.eval("getUniqueGrade()"))); } catch(Throwable tx) { throw new RuntimeException(tx.getMessage(), tx); }
    }
    
    @Override
    public Long getTech() {
    	try { return new Long(String.valueOf(engine.eval("getTech()"))); } catch(Throwable tx) { throw new RuntimeException(tx.getMessage(), tx); }
    }
    
    @Override
    public List<ResearchCondition> getResearchCoditions(Colony col) {
    	List<ResearchCondition> list = new ArrayList<ResearchCondition>();
    	
    	engine.put("__colony", col.toJson());
    	try {
    		Object val = engine.eval("getResearchCoditions(__colony)");
    		if(val == null) return list;
    		
    	    JsonArray arr = (JsonArray) JsonObject.parseJson(String.valueOf(val).trim());
    	    if(arr != null) {
    	    	for(Object element : arr) {
    	    		JsonObject json = (JsonObject) element;
    	    		ResearchCondition cond = new ResearchCondition(json.get("name").toString(), Integer.parseInt(json.get("level").toString()));
    	    		list.add(cond);
    	    	}
    	    }
    	} catch(Throwable tx) { throw new RuntimeException(tx.getMessage(), tx); }
    	
    	return list;
    }
    
    @Override
    public final boolean isScriptBasedFacility() {
		return true;
	}
    
	@Override
	public void dispose() {
		try { engine.getBindings(ScriptContext.ENGINE_SCOPE).clear(); } catch(Throwable ignores) {}
		engine  = null;
		scripts = null;
	}
	
	@Override
	public boolean equals(Object others) {
		if(others == null) return false;
		if(! (others instanceof ScriptFacilityInformation)) return false;
		
		ScriptFacilityInformation o = (ScriptFacilityInformation) others;
		return (o.getName().equals(getName()));
	}
}
