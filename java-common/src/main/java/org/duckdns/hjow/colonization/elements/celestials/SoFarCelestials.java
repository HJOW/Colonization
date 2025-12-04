package org.duckdns.hjow.colonization.elements.celestials;

import java.util.Map;

import org.duckdns.hjow.commons.util.DataUtil;

/** 정착지 개척 시 아주 먼 곳에 배치되는 천체들 */
public class SoFarCelestials extends DefaultCelestials implements FarCelestials {
	private static final long serialVersionUID = 6094684050802543378L;

	public SoFarCelestials() {}

	@Override
	public String getClassName() {
		return "farcelestials";
	}
	
	/** 랜덤 천체 생성 */
	public static FarCelestials createRandom(long stdx, long stdy, long stdz) {
		FarCelestials c = new SoFarCelestials();
		Map<String, Number> coordinate = DataUtil.createCoordinateLongScale(stdx, stdy, stdz, (Long.MAX_VALUE / 100L), (Long.MAX_VALUE / 10L));
		c.setX(coordinate.get("x").longValue());
		c.setY(coordinate.get("y").longValue());
		c.setZ(coordinate.get("z").longValue());
		
		int grade = 9;
		int debriesCount = grade <= 0 ? 5 : grade * 5;
		int idx;
		for(idx=0; idx<debriesCount; idx++) {
			if(Math.random() >= 0.5) {
				// TODO : 보상 추가 - 별도 클래스에서 추가해야 할 듯 (common 프로젝트에서 default 클래스 액세스 안됨)
				
			}
		}
		
		int enemyCount = grade <= 0 ? 5 : grade * 5;
		for(idx=0; idx<enemyCount; idx++) {
			if(Math.random() >= 0.3) {
				// TODO : 적 추가 - 별도 클래스에서 추가해야 할 듯 (common 프로젝트에서 default 클래스 액세스 안됨)
				
			}
		}
		
		c.setMoney( (long) ( (10.0 * Math.random()) * Math.pow(2, 1 + (0.01 * (grade > 10 ? 10 : grade) * Math.random()) ) ) + 1L );
		
		return c;
	}
}
