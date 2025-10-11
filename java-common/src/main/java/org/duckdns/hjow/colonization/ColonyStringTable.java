package org.duckdns.hjow.colonization;

import java.util.Properties;

import org.duckdns.hjow.commons.resource.StringTable;

/** Colonization 다국어 구현을 위한 스트링 테이블 */
public class ColonyStringTable implements StringTable {
	private static final long serialVersionUID = -1072075853155908566L;
	protected StringTable originalInstance;

	@Override
	public String t(String originals) {
		if(originalInstance == null) return originals;
		return originalInstance.t(originals);
	}

	@Override
	public String getName() {
		if(originalInstance == null) return "ColonizationStringTable";
		return originalInstance.getName();
	}

	@Override
	public Properties getData() {
		if(originalInstance == null) return new Properties();
		return originalInstance.getData();
	}

	public StringTable getOriginalInstance() {
		return originalInstance;
	}

	public void setOriginalInstance(StringTable originalInstance) {
		this.originalInstance = originalInstance;
	}
}
