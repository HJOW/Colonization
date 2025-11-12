package org.duckdns.hjow.colonization.elements.ship;

import org.duckdns.hjow.colonization.ColonyManager;

public class Willamette extends AbstractShip {
	private static final long serialVersionUID = -7640108254157310544L;

	@Override
	protected String getDefaultName() {
    	return ColonyManager.t("윌라멧");
    }
	
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
	public int getDefencePoint() {
		return 1;
	}
}
