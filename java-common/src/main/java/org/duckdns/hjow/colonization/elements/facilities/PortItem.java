package org.duckdns.hjow.colonization.elements.facilities;

import java.io.Serializable;
import java.util.List;

import org.duckdns.hjow.colonization.elements.Colony;
/** 항구 선택 콤보박스 사용 용도 */
public class PortItem implements Serializable {
	private static final long serialVersionUID = 1227068684406291394L;
	protected long key;
	protected String name;
	
	public PortItem() {}
	public PortItem(Port p) { this.key = p.getKey(); this.name = p.getName(); }
	
	@Override
	public String toString() {
		return name;
	}
	public long getKey() {
		return key;
	}
	public void setKey(long key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Port getPort(Colony col) {
		List<Port> ports = col.getPorts();
		for(Port p : ports) {
			if(getKey() == p.getKey()) return p;
		}
		return null;
	}
}