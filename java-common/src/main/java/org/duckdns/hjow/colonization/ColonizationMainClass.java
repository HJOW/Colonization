package org.duckdns.hjow.colonization;

/** 프로그램 실행을 위한 main 메소드가 있는 (즉 시작점이 되는) 클래스임을 나타내기 위한 인터페이스 */
public interface ColonizationMainClass extends Runnable {
    /** 프로그램 재시작 */
    public void restart();
    /** 프로그램 종료 */
    public void exit();
}
