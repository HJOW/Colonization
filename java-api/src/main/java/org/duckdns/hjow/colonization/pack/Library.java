package org.duckdns.hjow.colonization.pack;

import java.io.Serializable;
import java.util.List;

import org.duckdns.hjow.colonization.mod.Mod;

/** Pack 과 Mod 여럿을 묶는 용도의 객체 생성용 인터페이스 */
public interface Library extends Serializable {
    public List<Pack> getPacks();
    public List<Mod> getMods();
}
