package org.duckdns.hjow.colonization.addpack
import org.duckdns.hjow.colonization.pack.DefaultPack

class DebugPack: DefaultPack() {
    override fun init() {
        setKey(-7978542917940370919L);
        setName("Debug Pack")
        setAuthor("HJOW")
        setEmail("hujinone22@naver.com")
        addFeatureKeywords("DebugEnable")
    }
}