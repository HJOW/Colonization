package org.duckdns.hjow.updater;

public class TargetImpl implements Target {

	@Override
	public String getTitle() {
		return "Colonization";
	}
	
	@Override
	public String getFileName() {
		return "colonization_swing.jar";
	}

	@Override
	public String getMainUrl() {
		return "http://hjow.duckdns.org/colonization/";
	}
	
	@Override
	public String getConfigUrl() {
		return "content.json";
	}

	@Override
	public String getInstallPath() {
		return "[USERHOME]/.colonization/installes";
	}

	@Override
	public String getSubType() {
		return "swing";
	}
}
