package org.duckdns.hjow.colonization.ui.tools.files;

import java.io.File;
import java.io.Serializable;

/** jar 파일을 JList 에 담기 위한 VO 클래스 */
public class JarFile implements Serializable {
	private static final long serialVersionUID = 5157224949811924979L;
    protected File file;
    
    public JarFile() {}
    public JarFile(File file) { this.file = file; }
    
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
    
    @Override
    public String toString() {
    	if(file == null) return "[NULL]";
    	return file.getName();
    }
}
