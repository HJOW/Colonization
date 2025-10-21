package org.duckdns.hjow.colonization.addpack;
import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.pack.Pack;

/** 
 * 추가 팩 인스턴스를 제공하기 위한 클래스
 *     java-common 쪽에서 이 클래스를 Reflection 으로 액세스
 *     새로 개발한 Pack 을 테스트하려면 이 클래스 내 getPacks() 메소드에서 같이 리턴해주면 됨. 
 */
public class AddPackInfo {
    public List<Pack> getPacks() {
    	List<Pack> list = new ArrayList<Pack>();
        
    	// TODO : 이 곳에 Pack 추가
    	// 예: try { list.add((Pack) Class.forName("org.duckdns.hjow.colonization.addpack.NewPack").newInstance()); } catch(Exception ex) {}
    	return list;
    }
}