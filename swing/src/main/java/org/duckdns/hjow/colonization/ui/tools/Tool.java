package org.duckdns.hjow.colonization.ui.tools;

import org.duckdns.hjow.commons.core.Disposeable;

/** 도구 (게임 자체와 관련은 적고, 보조를 위한 간이 프로그램) 상위 인터페이스. 이 인터페이스 구현체는 생성자로 java.awt.Window 매개변수를 하나 받아야 함. */
public interface Tool extends Disposeable {
	public boolean isAvail();
    public String getName();
    public String getTitle();
    public void open();
}
