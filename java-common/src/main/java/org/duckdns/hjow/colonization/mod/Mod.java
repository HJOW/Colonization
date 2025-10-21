package org.duckdns.hjow.colonization.mod;

import java.io.Serializable;

import org.duckdns.hjow.colonization.ColonyManagerInterface;
import org.duckdns.hjow.commons.core.Disposeable;

/** MOD, 사용자 정의 컴포넌트가 구현해야 할 인터페이스 */
public interface Mod extends Disposeable, Serializable {
	public void init(ColonyManagerInterface manager);
    public String getName();
    public String getDescription();
    public int getLocation();
    public Object getComponent();
    public void refresh(int cycle, Object colony, ColonyManagerInterface manager);
    public boolean isReadOnly();
}
