package org.duckdns.hjow.colonization.script;

import javax.script.ScriptEngine;

/** 스크립트 사용 타입의 객체임을 표시하는 인터페이스, 스크립트 엔진 주입 메소드 구현 필요성 명시 */
public interface ScriptUsingObject {
    public void injectScriptEngine(ScriptEngine engine);
}
