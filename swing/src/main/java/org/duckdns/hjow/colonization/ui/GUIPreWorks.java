package org.duckdns.hjow.colonization.ui;

import java.lang.reflect.Method;
import java.util.Map;

import javax.swing.JEditorPane;

import org.duckdns.hjow.colonization.console.PreWorks;

/** 프로그램 실행 전 사전 작업 */
public class GUIPreWorks extends PreWorks {
    public GUIPreWorks() { super(); }
    public GUIPreWorks(Map<String, String> params) { super(params); }

    private static boolean flatLafEnabled = false;
    public static boolean isFlatLafEnabled() { return flatLafEnabled; }
    @Override
    protected void prepareOthers() throws Throwable {
        super.prepareOthers();

        // FlatLaf 로딩 시도 (없으면 넘어감)
        try {
            Class<?> flatLaf = Class.forName("com.formdev.flatlaf.FlatDarkLaf"); // FlatLightLaf, FlatDarculaLaf, FlatIntelliJLaf
            flatLaf.getMethod("setup");
            // setup 을 지금 호출하지는 않음
            flatLafEnabled = true;
            System.out.println("flatlaf enabled.");
        } catch(ClassNotFoundException ex) {  }
        
        // sciss SyntaxPane 로딩 시도 (없으면 넘어감)
        try {
            Class<?> scissClass = Class.forName("de.sciss.syntaxpane.DefaultSyntaxKit");
            Method mthd = scissClass.getMethod("initKit");
            mthd.invoke(null);
            syntaxPaneEnabled = true;
            System.out.println("sciss SyntaxPane enabled.");
        } catch(ClassNotFoundException ex) {  }
    }
    
    /** 실행 시작 클래스 */
    @Override
    protected String getDefaultClass() {
    	return "org.duckdns.hjow.colonization.Colonization";
    }
    
    private static boolean syntaxPaneEnabled = false;
    public static boolean isSyntaxPaneEnabled() {
        return syntaxPaneEnabled;
    }
    
    /** JEditorPane 에 컨텐츠 타입 적용 (sciss SyntaxPane 사용 불가 시에는 text/plain 혹은 text/html 만 적용 가능하며 그 외의 값은 무시됨) */
    public static void applySyntaxPane(JEditorPane pane, String contentType) {
        if(! isSyntaxPaneEnabled()) {
            if(contentType.equals("text/plain") || contentType.equals("text/html")) pane.setContentType(contentType);
            return;
        }
        pane.setContentType(contentType);
    }
}
