package org.duckdns.hjow.colonization.ui.tools;

import org.duckdns.hjow.commons.core.Disposeable;

/** 도구 상위 인터페이스 */
public interface Tool extends Disposeable {
    public String getName();
    public String getTitle();
    public void open();
}
