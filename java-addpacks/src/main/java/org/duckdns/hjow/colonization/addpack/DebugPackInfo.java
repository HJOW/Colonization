package org.duckdns.hjow.colonization.addpack;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.mod.Mod;
import org.duckdns.hjow.colonization.pack.Library;
import org.duckdns.hjow.colonization.pack.Pack;

/** 
 * 디버깅 기능 해제용 팩 
 *     java-common 쪽에서 Reflection 으로 이 클래스 존재여부 확인하고 액세스를 시도함 - 클래스패스 안에만 있으면 자동으로 호출됨 (이 AddPackInfo 클래스명과 패키지명은 변경 불가)
 *     Pack 이나 Mod 를 추가하려면 AddPackInfo 에 추가할 것.
 */
public final class DebugPackInfo implements Library {
	private static final long serialVersionUID = 861040831784217207L;

	@Override
	public final List<Pack> getPacks() {
    	List<Pack> list = new ArrayList<Pack>();
        try { list.add((Pack) Class.forName("org.duckdns.hjow.colonization.addpack.DebugPack").newInstance()); } catch(Exception ex) {}
    	return list;
    }
	
	@Override
	public final List<Mod> getMods() {
		return new ArrayList<Mod>();
	}
}
