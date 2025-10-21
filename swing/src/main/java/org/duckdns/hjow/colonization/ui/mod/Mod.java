package org.duckdns.hjow.colonization.ui.mod;

import java.awt.Component;
import java.io.Serializable;

import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.ui.GUIColonyManager;
import org.duckdns.hjow.commons.core.Disposeable;

/** MOD, 사용자 정의 컴포넌트가 구현해야 할 인터페이스 */
public interface Mod extends Disposeable, Serializable {
	public void init(GUIColonyManager manager);
    public String getName();
    public String getDescription();
    public int getLocation();
    public Component getComponent();
    public void refresh(int cycle, Colony colony, GUIColonyManager manager);
}
