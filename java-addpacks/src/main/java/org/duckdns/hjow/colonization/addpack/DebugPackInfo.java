package org.duckdns.hjow.colonization.addpack;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.pack.Pack;

/** 디버깅 기능 해제용 팩 */
public class DebugPackInfo {
	public List<Pack> getPacks() {
    	List<Pack> list = new ArrayList<Pack>();
        try { list.add((Pack) Class.forName("org.duckdns.hjow.colonization.addpack.DebugPack").newInstance()); } catch(Exception ex) {}
    	return list;
    }
}
