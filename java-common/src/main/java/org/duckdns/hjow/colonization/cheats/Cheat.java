package org.duckdns.hjow.colonization.cheats;

import java.util.HashMap;
import java.util.Map;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.elements.HoldingJob;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.facilities.ResearchCenter;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.commons.exception.KnownRuntimeException;
import org.duckdns.hjow.commons.util.DataUtil;

/** 디버깅을 위한 치트 코드, 사용 시 정착지 인증 해제됨 */
public abstract class Cheat {
	protected String code = "";
    public Cheat() {}
    public Cheat(String code) { this(); setCode(code); }
    public abstract void onCodeInput(ColonyManager man, String params);
	public final String getCode() {
		return code;
	}
	public final void setCode(String code) {
		if(code.contains(" ") || code.contains(".") || code.contains("(") || code.contains(")") || code.contains("'") || code.contains("\"") || code.contains("\n") || code.contains("\t") || code.contains("#") || code.contains("%")) throw new KnownRuntimeException("Illegal cheat code.");
		this.code = code;
	}
	
	public static Map<String, Cheat> cheatMap = init();
	private static Map<String, Cheat> init() {
        Map<String, Cheat> map = new HashMap<String, Cheat>();
		
		Cheat c;
		
		c = new Cheat("moneyplz") {
			@Override
			public void onCodeInput(ColonyManager man, String params) {
				if(man == null) return;
				Colony col = man.getSelectedColony();
				if(col == null) return;
				if(col.getCities().isEmpty()) return;
				
				col.disableChecked(); // 인증 제거 (여기서 안해도 이 메소드 호출되기 전 한번 더 함)
				
				for(City city : col.getCities()) {
					if(DataUtil.isEmpty(params)) params = "10000";
					col.modifyingMoney(Long.parseLong(params.replace(",", "").trim()), city, col, "Cheat", "Cheat");
				}
			}
		};
		map.put(c.getCode(), c);
		
		c = new Cheat("happyhour") {
			@Override
			public void onCodeInput(ColonyManager man, String params) {
				if(man == null) return;
				Colony col = man.getSelectedColony();
				if(col == null) return;
				if(col.getCities().isEmpty()) return;
				
				col.disableChecked(); // 인증 제거 (여기서 안해도 이 메소드 호출되기 전 한번 더 함)
				
				for(City city : col.getCities()) {
					for(Citizen c : city.getCitizens()) {
						if(c.getHappy() < 50) c.addHappy(50 - c.getHappy());
					}
				}
			}
		};
		map.put(c.getCode(), c);
		
		c = new Cheat("sofast") {
			@Override
			public void onCodeInput(ColonyManager man, String params) {
				if(man == null) return;
				Colony col = man.getSelectedColony();
				if(col == null) return;
				if(col.getCities().isEmpty()) return;
				
				col.disableChecked(); // 인증 제거 (여기서 안해도 이 메소드 호출되기 전 한번 더 함)
				
				for(City city : col.getCities()) {
					for(HoldingJob j : city.getHoldings()) {
						j.setCycleLeft(1);
					}
					
					for(Facility f : city.getFacility()) {
						if(f instanceof ResearchCenter) {
							ResearchCenter rc = (ResearchCenter) f;
							Research r = rc.getResearch(col);
							if(r.getLevel() < r.getMaxLevel()) {
								r.setProgress(r.getMaxProgress() - 1);
							}
						}
					}
				}
			}
		};
		map.put(c.getCode(), c);
		
		c = new Cheat("healall") {
			@Override
			public void onCodeInput(ColonyManager man, String params) {
				if(man == null) return;
				Colony col = man.getSelectedColony();
				if(col == null) return;
				if(col.getCities().isEmpty()) return;
				
				col.disableChecked(); // 인증 제거 (여기서 안해도 이 메소드 호출되기 전 한번 더 함)
				col.setHp(col.getMaxHp());
				
				for(City city : col.getCities()) {
					city.setHp(city.getMaxHp());
					
					for(Facility f : city.getFacility()) {
						f.setHp(f.getMaxHp());
					}
					
					for(Citizen c : city.getCitizens()) {
						c.setHp(c.getMaxHp());
					}
				}
			}
		};
		map.put(c.getCode(), c);
		
		return map;
	}
	public static Map<String, Cheat> map() {
		if(cheatMap == null) cheatMap = init();
		return cheatMap;
	}
}
