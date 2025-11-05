package org.duckdns.hjow.colonization.elements.enemies;

import java.math.BigInteger;

import org.duckdns.hjow.colonization.ColonyManager;

public class Goord extends Enemy {
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
        return ColonyManager.DEFENCETYPE_NORMAL;
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

}
