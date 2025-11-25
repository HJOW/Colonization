package org.duckdns.hjow.classwrapper;

import java.io.File;
import java.io.Serializable;
import java.net.URLClassLoader;

import org.duckdns.hjow.commons.util.ClassUtil;

/** jar 파일 위치정보와 클래스명을 가지고 있는 Class Wrapper */ // TODO : 공통 lib로 이관
public class FileClassWrapper implements ClassWrapper, Serializable {
	private static final long serialVersionUID = 6493338606303709306L;
	protected File   file;
    protected String className;
    public FileClassWrapper() {}
	public FileClassWrapper(File file, String className) {
		this();
		this.file = file;
		this.className = className;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	@Override
	public Class<?> getWrappedClass() {
		URLClassLoader loader = null;
		Class<?> classObject = null;
		try {
			loader = ClassLoaderManager.newClassLoader(getFile());
			classObject = loader.loadClass(getClassName());
		} catch(Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			if(loader != null) { ClassUtil.closeAll(loader); }
		}
		
		return classObject;
	}
}
