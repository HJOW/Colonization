package org.duckdns.hjow.colonization.constants;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.commons.util.Base64Util;
import org.duckdns.hjow.commons.util.HexUtil;

/** 주요 static 메소드들 모음 */
public class StaticMethods {
	/** 바이너리 인코딩 */
    public static String encode(byte[] binary) {
    	return HexUtil.encode(binary);
    }
    
    /** BASE64 또는 HEX 인코딩된 문자열을 받아, 패턴을 인식하여 BASE64가 맞으면 디코딩해 반환. 그외의 경우 HEX 인코딩으로 판단해 디코딩해 반환. */
    public static byte[] decode(String str) {
    	try {
    	    Pattern pattern = Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$");
    	    Matcher matcher = pattern.matcher(str);
    	    if(matcher.find()) return Base64Util.decode(str);
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
    public static String decodeString(String hexString) {
    	try { return new String(decode(hexString), "UTF-8"); } catch(UnsupportedEncodingException e) { throw new RuntimeException(e.getMessage(), e); }
    }
}
