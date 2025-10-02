package org.duckdns.hjow.colonization.web.key;

/** JWT 인증 키 등 암호 관련 컨텐츠 관리 클래스임을 표시 */
public interface Key {
    public String getJWTKey();
    public String getJWTVerifyingClaim();
}
