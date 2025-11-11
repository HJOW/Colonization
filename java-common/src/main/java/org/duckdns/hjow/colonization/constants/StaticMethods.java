package org.duckdns.hjow.colonization.constants;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    
    /** 제곱근 구하기 (출처 : https://stackoverflow.com/questions/13649703/square-root-of-bigdecimal-in-java) */ // TODO 공통 lib 로 이관
    public static BigDecimal sqrt(BigDecimal originals) {
    	return sqrtNewtonRaphson(originals, BigDecimal.ONE, BigDecimal.ONE.divide(Constants.BIGDECIMAL_SQRT_PRE));
    }
    
    private static BigDecimal sqrtNewtonRaphson(BigDecimal c, BigDecimal xn, BigDecimal precision) {
    	BigDecimal fx  = xn.pow(2).add(c.negate());
        BigDecimal fpx = xn.multiply(new BigDecimal(2));
        BigDecimal xn1 = fx.divide(fpx, 2 * Constants.BIGDECIMAL_SQRT_DIG.intValue(), RoundingMode.HALF_DOWN);
        xn1 = xn.add(xn1.negate());
        BigDecimal currentSquare = xn1.pow(2);
        BigDecimal currentPrecision = currentSquare.subtract(c);
        currentPrecision = currentPrecision.abs();
        if (currentPrecision.compareTo(precision) <= -1){
            return xn1;
        }
        return sqrtNewtonRaphson(c, xn1, precision);
    }
    
    /** 두 좌표 간 거리 구하기 */ // TODO 공통 lib 로 이관
    public static long getDistance(long x1, long y1, long z1, long x2, long y2, long z2) {
    	BigDecimal xd = new BigDecimal(String.valueOf(x2)).subtract(new BigDecimal(String.valueOf(x1)));
    	BigDecimal yd = new BigDecimal(String.valueOf(y2)).subtract(new BigDecimal(String.valueOf(y1)));
    	BigDecimal zd = new BigDecimal(String.valueOf(z2)).subtract(new BigDecimal(String.valueOf(z1)));
    	
    	xd = xd.pow(2);
    	yd = yd.pow(2);
    	zd = zd.pow(2);
    	
    	BigDecimal sum = xd.add(yd).add(zd);
    	return sqrt(sum).longValue();
    }
}
