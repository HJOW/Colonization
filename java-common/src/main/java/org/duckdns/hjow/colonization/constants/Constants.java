package org.duckdns.hjow.colonization.constants;

import java.math.BigInteger;

/** 자주 쓰는 상수들을 정리한 클래스 (단, Colonization 게임 밸런스 자체와 연관이 깊은 상수는 ColonyManager 에 배치) */
public class Constants {
	public static final BigInteger BIGINTEGER_2           = new BigInteger(String.valueOf(2));
	public static final BigInteger BIGINTEGER_3           = new BigInteger(String.valueOf(3));
	public static final BigInteger BIGINTEGER_7           = new BigInteger(String.valueOf(7));
    public static final BigInteger BIGINTEGER_10          = new BigInteger(String.valueOf(10));
    public static final BigInteger BIGINTEGER_12          = new BigInteger(String.valueOf(12));
    public static final BigInteger BIGINTEGER_17          = new BigInteger(String.valueOf(17));
    public static final BigInteger BIGINTEGER_23          = new BigInteger(String.valueOf(23));
    public static final BigInteger BIGINTEGER_24          = new BigInteger(String.valueOf(24));
    public static final BigInteger BIGINTEGER_30          = new BigInteger(String.valueOf(30));
    public static final BigInteger BIGINTEGER_60          = new BigInteger(String.valueOf(60));
    public static final BigInteger BIGINTEGER_600         = new BigInteger(String.valueOf(600));
    public static final BigInteger BIGINTEGER_1000        = new BigInteger(String.valueOf(1000));
    public static final BigInteger BIGINTEGER_3000        = new BigInteger(String.valueOf(3000));
    public static final BigInteger BIGINTEGER_1000000     = new BigInteger(String.valueOf(1000000));
    public static final BigInteger BIGINTEGER_10000000000 = new BigInteger("10000000000");
    public static final BigInteger BIGINTEGER_INTMAX      = new BigInteger(String.valueOf(Integer.MAX_VALUE));
    public static final BigInteger BIGINTEGER_LONGMAX     = new BigInteger(String.valueOf(Long.MAX_VALUE));
    
    /** 아무것도 하지 않음. 단지 이 메소드를 호출함으로써, 이 클래스와 위의 상수들도 같이 준비되는 것을 목적으로 사용 */
    public static void prepare() {};
}
