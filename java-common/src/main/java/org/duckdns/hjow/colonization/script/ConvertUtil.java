package org.duckdns.hjow.colonization.script;

import org.duckdns.hjow.commons.exception.KnownRuntimeException;

/** 스크립트 언어 변환 Util */
public class ConvertUtil {
	/** 스크립트 주요 문법 변환, 오리지널 스크립트는 JavaScript 언어로 넣어야 함. */
    public static String convert(String originalScripts, String targetLanguage) {
    	if(targetLanguage == null) targetLanguage = "javascript";
    	targetLanguage = targetLanguage.toLowerCase();
    	if(targetLanguage.equals("javascript") || targetLanguage.equals("js") || targetLanguage.equals("ecmascript") || targetLanguage.equals("nashorn")) return originalScripts;
    	
    	throw new KnownRuntimeException("Unsupported language " + targetLanguage);
    }
}
