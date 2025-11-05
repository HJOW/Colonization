package org.duckdns.hjow.colonization.ui;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.commons.core.Disposeable;

public interface GlobalLogUI extends Disposeable {
    public void log(String msg);
    public void log(String msg, int lev);
    public void clear();
    public void open(ColonyManager superInstance);
    public void close();
    public int getDetailLevel();
    public void setDetailLevel(int detailLevel);
}
