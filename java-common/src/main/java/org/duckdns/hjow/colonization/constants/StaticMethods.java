package org.duckdns.hjow.colonization.constants;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.duckdns.hjow.classwrapper.ClassWrapper;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.commons.util.Base64Util;
import org.duckdns.hjow.commons.util.HexUtil;

/** 주요 static 메소드들 모음 */
public class StaticMethods {
	/** 바이너리 인코딩 */
    public static String encode(byte[] binary) {
    	return Base64Util.convertBase64AsURLSafe(Base64Util.encode(binary));
    	// return HexUtil.encode(binary);
    }
    
    /** BASE64 또는 HEX 인코딩된 문자열을 받아, 패턴을 인식하여 BASE64가 맞으면 디코딩해 반환. 그외의 경우 HEX 인코딩으로 판단해 디코딩해 반환. */
    public static byte[] decode(String str) {
    	try {
    		String mayBeBase64 = str;
    		if(str.contains("-") || str.contains("_") || str.contains(".")) mayBeBase64 = Base64Util.recoverBase64Default(str);
    		
    	    Pattern pattern = Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$");
    	    Matcher matcher = pattern.matcher(mayBeBase64);
    	    
    	    if(matcher.find()) return Base64Util.decode(mayBeBase64);
    	} catch(Exception ex) {
    		GlobalLogs.processExceptionOccured(ex, false); // 일단 오류 출력 후 HEX 디코딩 시도
    	}
    	return HexUtil.decode(str);
    }
    
    /** 지정된 바이너리 인코딩 방식으로 문자열 인코딩 */
    public static String encodeString(String originalStr) {
    	try {
            byte[] originalBinaries = originalStr.getBytes("UTF-8");
            return encode(originalBinaries);
    	} catch(UnsupportedEncodingException e) { throw new RuntimeException(e.getMessage(), e); }
    }
    
    /** 인코딩된 문자열 디코딩 */
    public static String decodeString(String str) {
    	try { return new String(decode(str), "UTF-8"); } catch(UnsupportedEncodingException e) { throw new RuntimeException(e.getMessage(), e); }
    }
    
    /** ClassWrapper 들의 List 로부터 Class 리스트 생성 */ // TODO 공통 lib 로 이관
    public static List<Class<?>> getClassListsFrom(List<ClassWrapper> wrappers) {
    	List<Class<?>> list = new ArrayList<Class<?>>();
    	for(ClassWrapper cls : wrappers) {
    		list.add(cls.getWrappedClass());
    	}
    	return list;
    }
}
