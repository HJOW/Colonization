package org.duckdns.hjow.colonization.cheats;

import java.util.HashMap;
import java.util.Map;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.commons.util.DataUtil;

/** 디버깅을 위한 치트 코드, 사용 시 정착지 인증 해제됨 */
public abstract class Cheat {
	protected String code = "";
    public Cheat() {}
    public Cheat(String code) { this(); setCode(code); }
    public abstract void onCodeInput(ColonyManager man, String params);
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public static Map<String, Cheat> map() {
		Map<String, Cheat> map = new HashMap<String, Cheat>();
		
		Cheat c;
		
		c = new Cheat("moneyplz") {
			@Override
			public void onCodeInput(ColonyManager man, String params) {
				if(man == null) return;
				Colony col = man.getSelectedColony();
				if(col == null) return;
				
				City city = null;
				if(col.getCities().isEmpty()) return;
				city = col.getCities().get(0);
				
				if(DataUtil.isEmpty(params)) params = "10000";
				
				col.disableChecked(); // 인증 제거 (여기서 안해도 이 메소드 호출되기 전 한번 더 함)
				col.modifyingMoney(Long.parseLong(params.replace(",", "").trim()), city, col, "Cheat", "Cheat");
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
				City city = col.getCities().get(0);
				
				col.disableChecked(); // 인증 제거 (여기서 안해도 이 메소드 호출되기 전 한번 더 함)
				
				for(Citizen c : city.getCitizens()) {
					if(c.getHappy() < 50) c.addHappy(50 - c.getHappy());
				}
			}
		};
		map.put(c.getCode(), c);
		
		
		return map;
	}
}
