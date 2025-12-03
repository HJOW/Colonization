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
		
		return c;
	}
}
