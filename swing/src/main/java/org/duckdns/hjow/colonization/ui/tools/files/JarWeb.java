package org.duckdns.hjow.colonization.ui.tools.files;

import java.io.File;
import java.net.URL;

/** 웹상에 있는 jar 파일을 JList 에 담기 위한 VO 클래스 */
public class JarWeb extends JarFile {
	private static final long serialVersionUID = -8284970893089497525L;
    protected URL url;
    
    public JarWeb() {}
    public JarWeb(URL url, File file) {this.url = url; this.file = file;} // file 필드는 다운로드 목적지로 쓰이게 됨
    
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
}
