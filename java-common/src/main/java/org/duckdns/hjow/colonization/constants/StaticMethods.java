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
    	return convertBase64AsURLSafe(Base64Util.encode(binary));
    	// return HexUtil.encode(binary);
    }
    
    /** BASE64 또는 HEX 인코딩된 문자열을 받아, 패턴을 인식하여 BASE64가 맞으면 디코딩해 반환. 그외의 경우 HEX 인코딩으로 판단해 디코딩해 반환. */
    public static byte[] decode(String str) {
    	try {
    		String mayBeBase64 = recoverBase64Default(str);
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
    
    /** BASE64 문자열을 URL Safe 하게 변환 */
    public static String convertBase64AsURLSafe(String base64Str) { // TODO 공통 lib 로 이관
    	return base64Str.replace("+", "-").replace("/", "_").replace("=", ".");
    }
    
    /** BASE64 URL Safe 처리된 문자열을 기존 BASE64 문자열로 변환 */
    public static String recoverBase64Default(String urlSafeBase64) { // TODO 공통 lib 로 이관
    	return urlSafeBase64.replace("-", "+").replace("_", "/").replace(".", "=");
    }
}
