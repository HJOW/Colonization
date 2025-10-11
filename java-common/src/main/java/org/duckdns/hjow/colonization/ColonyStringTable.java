package org.duckdns.hjow.colonization;

import java.util.Properties;

import org.duckdns.hjow.commons.resource.BrokerStringTable;
import org.duckdns.hjow.commons.resource.FileStringTable;
import org.duckdns.hjow.commons.resource.StringTable;

/** Colonization 다국어 구현을 위한 스트링 테이블 */
public class ColonyStringTable extends BrokerStringTable {
	private static final long serialVersionUID = -1072075853155908566L;
	
	public ColonyStringTable() { super(); }
	public ColonyStringTable(StringTable stringTable) { super(stringTable); }
	public ColonyStringTable(String name, StringTable stringTable) { super(name, stringTable); }

	@Override
	public String t(String originals) {
		Properties prop = originalInstance.getData();
		String res = null;
		if(prop.containsKey(originals)) res = prop.getProperty(originals);
		
		if(res == null) {
			if(originalInstance != null) {
				if(originalInstance instanceof FileStringTable) {
					FileStringTable fileTable = (FileStringTable) originalInstance;
					fileTable.set(originals, originals, true);
				} else {
					originalInstance.getData().setProperty(originals, originals);
				}
				return originals;
			}
		}
		
		return res;
	}

	@Override
	public String getName() {
		if(originalInstance == null) return "ColonizationStringTable";
		return originalInstance.getName();
	}
}
