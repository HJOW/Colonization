package org.duckdns.hjow.colonization;

import java.lang.reflect.Method;

import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;

/** ColonyManager 대리자, 일부 필드와 메소드에만 액세스할 수 있도록 제한된 객체를 전달하기 위해 사용, Call by reference */
public class ColonyManagerBroker implements ColonyManagerInterface {
    protected transient ColonyManager originals;
    public ColonyManagerBroker(ColonyManager originals) {
    	this.originals = originals;
    }
    
    /** 다국어 지원 번역 */
    public String t(String text) {
    	return ColonyManager.t(text);
    }
    
	@Override
	public void dispose() {
		this.originals = null;
	}
    
	public void exit() {
		originals.exit();
	}
	
	public ColonyManagerConfig getConfig() {
        return originals.getConfig().cloneSelf();
    }
	
	public void reserveRefresh() {
		originals.reserveRefresh();
	}
	
	public void log(String msg) {
		originals.log(msg);
	}
	
	public void alert(String msg) {
		originals.alert(msg);
	}
	
	public void pauseSimulation() {
		originals.pauseSimulation();
	}
	
	public void resumeSimulation(int cycleCount) {
		originals.resumeSimulation(cycleCount);
	}
	
	@Override
	public JsonObject getSelectColonyInfo() {
		return originals.getSelectColonyInfo();
	}
	
	@Override
	public JsonArray getAllColonies() {
		return originals.getAllColonies();
	}
	
	public Integer getFrameWidth() {
		try {
		    Method mthd = originals.getClass().getMethod("getDialogWidth");
		    return (Integer) mthd.invoke(originals);
		} catch(NoSuchMethodException ex) {} catch(Throwable tx) {
			originals.log(t("Error") + " : " + tx.getMessage());
		}
		
		return null;
	}
	
	public Integer getFrameHeight() {
		try {
		    Method mthd = originals.getClass().getMethod("getDialogHeight");
		    return (Integer) mthd.invoke(originals);
		} catch(NoSuchMethodException ex) {} catch(Throwable tx) {
			originals.log(t("Error") + " : " + tx.getMessage());
		}
		
		return null;
	}
	
	public Integer getFrameX() {
		try {
		    Method mthd = originals.getClass().getMethod("getDialogX");
		    return (Integer) mthd.invoke(originals);
		} catch(NoSuchMethodException ex) {} catch(Throwable tx) {
			originals.log(t("Error") + " : " + tx.getMessage());
		}
		
		return null;
    }
    
    public Integer getFrameY() {
    	try {
		    Method mthd = originals.getClass().getMethod("getDialogY");
		    return (Integer) mthd.invoke(originals);
		} catch(NoSuchMethodException ex) {} catch(Throwable tx) {
			originals.log(t("Error") + " : " + tx.getMessage());
		}
		
		return null;
    }
}
