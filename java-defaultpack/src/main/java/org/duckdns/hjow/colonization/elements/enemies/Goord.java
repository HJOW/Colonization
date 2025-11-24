package org.duckdns.hjow.colonization.elements.enemies;

import java.math.BigInteger;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.constants.Constants;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;

public class Goord extends AbstractEnemy {
    private static final long serialVersionUID = 2685545395823241723L;

    @Override
    public String getName() {
        return ColonyManager.t("고드");
    }
    
    @Override
    public final String getClassName() {
        return getClass().getSimpleName();
    }

    @Override
    public short getDefenceType() {
        return Constants.DEFENCETYPE_NORMAL;
    }

    @Override
    public int getDefencePoint() {
        return 1;
    }

    @Override
    public BigInteger getCheckerValue() {
        return BigInteger.TEN;
    }
    
    @Override
    public int getMaxHp() {
        return 80;
    }
    
    @Override
    public int getDamage() {
        return 10;
    }

	@Override
	public int getRealDamage(ColonyElements target, Colony colony) {
		return getDamage() + (int) Math.floor(getDamageIncreases(target, colony));
	}
	
	/** 레벨 당 증가치 등 계산 */
    protected double getDamageIncreases(ColonyElements target, Colony colony) {
    	return level * (getDamage() * 0.1);
    }
}
