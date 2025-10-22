package org.duckdns.hjow.colonization.addpack;
import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.mod.Mod;
import org.duckdns.hjow.colonization.pack.Library;
import org.duckdns.hjow.colonization.pack.Pack;

/** 
 * 추가 팩 인스턴스를 제공하기 위한 클래스
 *     java-common 쪽에서 Reflection 으로 이 클래스 존재여부 확인하고 액세스를 시도함 - 클래스패스 안에만 있으면 자동으로 호출됨 (이 AddPackInfo 클래스명과 패키지명은 변경 불가) 
 *     새로 개발한 Pack 을 테스트하려면 이 클래스 내 getPacks() 메소드에서 같이 리턴해주면 된다.
 *     Mod 도 마찬가지.
 */
public class AddPackInfo implements Library {
    private static final long serialVersionUID = -1451136149956027859L;
    @Override
	public List<Pack> getPacks() {
    	List<Pack> list = new ArrayList<Pack>();
        
    	// TODO : 이 곳에 Pack 추가
    	// 예: try { list.add((Pack) Class.forName("org.duckdns.hjow.colonization.addpack.NewPack").newInstance()); } catch(Exception ex) {}
    	return list;
    }
    
    @Override
    public List<Mod> getMods() {
    	List<Mod> list = new ArrayList<Mod>();
    	// TODO : 이 곳에 Mod 추가
    	// 예: try { list.add((Mod) Class.forName("org.duckdns.hjow.colonization.addpack.NewMod").newInstance()); } catch(Exception ex) {}
    	return list;
    }
}