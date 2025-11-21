package org.duckdns.hjow.colonization.elements.ship;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.facilities.Port;

public class Willamette extends AbstractShip {
	private static final long serialVersionUID = -7640108254157310544L;
	public Willamette() {}

	@Override
	public int getAttackCycle() {
		return 120;
	}

	@Override
	public int getAttackCount() {
		return 1;
	}

	@Override
	public int getDamage() {
		return 5;
	}
	
	@Override
	public int getSpeed() {
		return 10;
	}

	@Override
	public short getAttackType() {
		return ColonyManager.ATTACKTYPE_NORMAL;
	}
	
	@Override
	public int getMaxHp() {
		return 100;
	}
	
	@Override
	public short getDefenceType() {
		return ColonyManager.DEFENCETYPE_SMALL;
	}

	@Override
	public int getDefaultDefencePoint() {
		return 1;
	}
	
	@Override
	protected double getDefencePointIncreases() {
		return (getLevel() * 0.1);
	}
	
	@Override
	public int getSize() {
		return 1;
	}
	
	/** 함선 명칭 */
	public static String getMetaName() {
		return ColonyManager.t("윌라멧");
	}
	
	/** 함선 설명 */
    public static String getMetaDescription() {
    	StringBuilder res = new StringBuilder("");
    	res = res.append("\n").append("소형급 기본형 함선입니다.");
    	res = res.append("\n").append("속도     : 10");
    	res = res.append("\n").append("기본 HP  : 100");
    	res = res.append("\n").append("공격유형 : 일반");
    	res = res.append("\n").append("방어유형 : 소형");
    	res = res.append("\n").append("가격     : 10000");
    	res = res.append("\n").append("");
    	return res.toString().trim();
    }
    
    /** 함선 건조 시간 (사이클) */
    public static long getMetaBuildCycle() {
    	return 200;
    }
    
    /** 함선 건조 가능여부, null 리턴 시 가능한 것. 그외의 경우 건조 불가능 사유 리턴 */
    public static String getMetaBuildAvail(Port port, Colony colony) {
    	return null;
    }
    
    /** 함선 건조 비용 */
    public static long getMetaPrice(Port port, Colony colony) {
    	return 10000L;
    }
    
    /** 함선의 크기 */
    public static int getMetaSize() {
		return 1;
	}
}
