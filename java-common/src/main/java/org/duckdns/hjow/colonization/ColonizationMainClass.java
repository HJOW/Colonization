package org.duckdns.hjow.colonization;

public interface ColonizationMainClass extends Runnable {
    /** 프로그램 재시작 */
    public void restart();
    /** 프로그램 종료 */
    public void exit();
}
