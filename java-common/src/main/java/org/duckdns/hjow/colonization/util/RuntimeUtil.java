package org.duckdns.hjow.colonization.util;

import java.io.File;

/** Java Runtime 관련 Utilities, HJOW 공통 lib 로 이관 예정 */
public class RuntimeUtil {
    /** 실행 중인 자바 런타임의 버전 정보 반환 */
    public static String getJavaVersionString() {
        return System.getProperty("java.version");
    }
    
    /** 실행 중인 자바 런타임의 버전 정보 반환 */
    public static int getJavaVersion() {
        String str = getJavaVersionString();
        
        char[] chars = str.toCharArray();
        str = "";
        
        int dotCount = 0;
        for(int idx=0; idx<chars.length; idx++) {
            char c = chars[idx];
            if(! isNumberDigit(c)) {
                if(c == '.') { // 소수 1자리는 허용
                    dotCount++;
                    if(dotCount >= 2) break;
                } else {
                    break;
                }
            }
            str += String.valueOf(c);
        }
        
        if(str.indexOf(".") >= 0) {
            // 버전 코드에 . 이 있는 경우, 그 뒷부분만 사용
            String[] splits = str.split("\\.");
            return Integer.parseInt(splits[1].trim());
        } else {
            return Integer.parseInt(str);
        }
    }
    
    /** 자바 런타임 설치 경로 반환 */
    public static File getJavaHome() {
        return new File(System.getProperty("java.home"));
    }
    
    /** 운영체제 아키텍처 타입 반환 */
    public static String getOSArchitecture() {
        return System.getProperty("os.arch");
    }
    
    /** 운영체제 이름 반환 */
    public static String getOSName() {
        return System.getProperty("os.name");
    }
    
    /** 운영체제 버전 반환 */
    public static String getOSVersionString() {
        return System.getProperty("os.version");
    }
    
    /** 임시 폴더 경로 반환 */
    public static File getTemporaryDirectory() {
        return new File(System.getProperty("java.io.tmpdir"));
    }
    
    /** 해당 글자가 숫자인지 확인 */
    public static boolean isNumberDigit(char c) {
        if(c == '0') return true;
        else if(c == '1') return true;
        else if(c == '2') return true;
        else if(c == '3') return true;
        else if(c == '4') return true;
        else if(c == '5') return true;
        else if(c == '6') return true;
        else if(c == '7') return true;
        else if(c == '8') return true;
        else if(c == '9') return true;
        
        return false;
    }
}
