package org.duckdns.hjow.colonization;

import java.util.Properties;

import org.duckdns.hjow.commons.resource.BrokerStringTable;
import org.duckdns.hjow.commons.resource.FileStringTable;
import org.duckdns.hjow.commons.resource.StringTable;
import org.duckdns.hjow.commons.util.FileUtil;

/** Colonization 다국어 구현을 위한 스트링 테이블 */
public class ColonyStringTable extends BrokerStringTable {
	private static final long serialVersionUID = -1072075853155908566L;
	
	public ColonyStringTable() { super(); }
	public ColonyStringTable(StringTable stringTable) { super(stringTable); }
	public ColonyStringTable(String name, StringTable stringTable) { super(name, stringTable); }

	@Override
	public synchronized String t(String originals) {
		Properties prop = originalInstance.getData();
		String res = null;
		if(prop.containsKey(originals)) res = prop.getProperty(originals);
		
		if(res == null) {
			if(originalInstance != null) {
				System.out.println("Save : " + originals);
				if(originalInstance instanceof FileStringTable) {
					FileStringTable fileTable = (FileStringTable) originalInstance;
					// TODO : 공통 lib 버그 수정 후 이곳 간소화 해야 함
					prop = fileTable.getData();
					prop.setProperty(originals, originals);
					try { FileUtil.saveProperties(fileTable.getFile(), prop); } catch(Exception ex) { ColonyManager.logGlobals("Error : (" + ex.getClass().getSimpleName() + ") " + ex.getMessage()); }
				} else {
					prop = originalInstance.getData();
					prop.setProperty(originals, originals);
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
