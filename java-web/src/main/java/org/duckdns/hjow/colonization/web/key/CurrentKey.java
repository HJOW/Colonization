package org.duckdns.hjow.colonization.web.key;

public class CurrentKey implements Key {
    @Override
    public String getJWTKey() {
        return "Hello";
    }

    @Override
    public String getJWTVerifyingClaim() {
        return "World";
    }
}
