package org.duckdns.hjow.colonization;

import javax.swing.JFrame;

/** GUIColonyManager 추상화를 위한 인터페이스 */
public interface GUIColonyManagerInterface extends ColonyManagerInterface {
	/** 대화상자 객체 반환 */
    public JFrame getDialog();
    
    /** 대화상자 가로 길이 반환 */
    public int getDialogWidth();
    
    /** 대화상자 세로 길이 반환 */
    public int getDialogHeight();
    
    public int getDialogX();
    public int getDialogY();
    
    /** 룩앤필 변경 시 호출되어야 함 */
    public void refreshLookAndFeel();
    
    /** 새 정착지 대화상자 응답 시 호출, swing 프로젝트의 NewColonyManager 에서만 호출 가능 */
    public void onNewColonyTypeDecided(String type, String name, int difficulty, Object decider);
    /** 서블릿(웹) 패널 내 정착지 영역 새로고침 요청 */
    public void requestLoadServletColony();
    
    /** 자식 대화상자 닫일 때 프레임 동작에 영향을 받아야 하는 경우 외부에서 호출됨 */
    public void onChildDialogClosed();
}
