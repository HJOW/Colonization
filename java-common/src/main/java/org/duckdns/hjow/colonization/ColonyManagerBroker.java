package org.duckdns.hjow.colonization;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.mod.Mod;
import org.duckdns.hjow.colonization.ui.ImageResourcePackage;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;

/** ColonyManager 대리자, 일부 필드와 메소드에만 액세스할 수 있도록 제한된 객체를 전달하기 위해 사용, Call by reference 형태로 주로 사용 */
public class ColonyManagerBroker implements ColonyManagerInterface {
    private transient ColonyManager originals;
    public ColonyManagerBroker(ColonyManager originals) {
        this.originals = originals;
    }
    
    /** 다국어 지원 번역 */
    public String t(String text) {
        return ColonyManager.t(text);
    }
    
    /** 프로그램 종료 시 호출됨. 직접 호출하지 말 것. */
    @Override
    public void dispose() {
        this.originals = null;
    }
    
    /** 프로그램 종료 */
    @Override
    public void exit() {
        originals.exit();
    }
    
    /** 현재 프로그램 설정 내용을 복제해 반환 */
    @Override
    public ColonyManagerConfig getConfig() {
        return originals.getConfig().cloneSelf();
    }
    
    @Override
    public void reserveRefresh() {
        originals.reserveRefresh();
    }
    
    /** 로그 출력 */
    @Override
    public void log(String msg) {
        originals.log(msg);
    }
    
    /** 알림 메시지 출력 */
    @Override
    public void alert(String msg) {
        originals.alert(msg);
    }
    
    /** 시뮬레이션 정지 */
    public void pause() {
        pauseSimulation();
    }
    
    /** 시뮬레이션 재개 */
    public void resume() {
        resumeSimulation(-1);
    }
    
    /** 시뮬레이션 정지 */
    @Override
    public void pauseSimulation() {
        originals.pauseSimulation();
    }
    
    /** 시뮬레이션 재개 */
    @Override
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
    
    @Override
	public Colony getColony() {
    	Colony col = originals.getColony();
    	if(col == null) return null;
    	return (Colony) col.cloneThis(); // 읽기 전용이어야 하므로 복제해 반환해야 함
	}

	@Override
	public Colony getColony(long colonyKey) {
		Colony col = originals.getColony(colonyKey);
		if(col == null) return null;
		return (Colony) col.cloneThis(); // 읽기 전용이어야 하므로 복제해 반환해야 함
	}

	@Override
	public Colony getColonyFrom(City city) {
		Colony col = originals.getColonyFrom(city);
		if(col == null) return null;
		return (Colony) col.cloneThis(); // 읽기 전용이어야 하므로 복제해 반환해야 함
	}

	@Override
	public void open(ColonizationMainClass superInstance) { } // 의미없음 (이미 창이 열린 상태일 테므로)

	@Override
	public Object getDialogObject() {
		return null; // 읽기 전용이어야 하므로 액세스 금지
	}

	@Override
	public long getCycleGapEachCity() {
		return originals.getCycleGapEachCity();
	}

	@Override
	public long getCycleGapEachFacility() {
		return originals.getCycleGapEachFacility();
	}

	@Override
	public boolean isUsingCheckDisablingContent() {
		return originals.isUsingCheckDisablingContent();
	}

	@Override
	public void refreshColonyContent() {
		originals.reserveRefresh();
	}

	@Override
	public void refreshColonyList() {
		originals.reserveRefresh();
	}

	@Override
	public void loadColonies() {
		originals.loadColonies();
	}

	@Override
	public void saveLocalConfigs() {
		originals.loadLocalConfigs();
	}

	@Override
	public void saveColonies() {
		originals.saveColonies();
	}

	@Override
	public File getColonyConfigRootDirectory() {
		return null; // 접근 차단
	}

	@Override
	public Colony newColony() {
		return originals.newColony();
	}

	@Override
	public List<Mod> getMods() {
		return new ArrayList<Mod>(); // 접근 차단
	}

	@Override
	public String translate(String str) {
		return originals.translate(str);
	}

	@Override
	public List<ImageResourcePackage> getImagePackages() {
		return originals.getImagePackages();
	}

	@Override
	public Set<String> getImageNames() {
		return originals.getImageNames();
	}

	@Override
	public Object getImage(String imageName) {
		return originals.getImage(imageName);
	}
}
