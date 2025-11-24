package org.duckdns.hjow.colonization.mod;

import java.awt.Component;

import javax.swing.JToolBar;

import org.duckdns.hjow.commons.core.Disposeable;

/** MOD 툴바 */
public class ModToolbar implements Disposeable {
	protected JToolBar toolbar;
	protected Mod mod;
	
	public ModToolbar(Mod mod) {
		toolbar = new JToolBar();
		init();
	}
	
	/** UI 초기화 */
	protected void init() {}
	
	/** 컴포넌트 반환 */
	public Component getComponent() {
		return toolbar;
	}

	@Override
	public void dispose() {
		mod = null;
	}
}
