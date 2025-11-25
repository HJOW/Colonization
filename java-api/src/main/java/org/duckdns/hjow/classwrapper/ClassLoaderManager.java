package org.duckdns.hjow.classwrapper;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.commons.util.ClassUtil;

/** 최상위 클래스 로더를 보관하는 용도의 클래스 */ // TODO 공통 lib로 이관
public class ClassLoaderManager {
	public static final ClassLoader ROOT_CLASS_LOADER = Thread.currentThread().getContextClassLoader();
	private static final List<URLClassLoader> urlClassLoaders = new ArrayList<URLClassLoader>();
	
	/** 해당 jar 파일을 통해 클래스를 불러올 수 있는 클래스로더 생성 */
	public static URLClassLoader newClassLoader(File file) {
		return newClassLoader(file, ROOT_CLASS_LOADER);
	}
	
	/** 해당 jar 파일을 통해 클래스를 불러올 수 있는 클래스로더 생성 */
	public static URLClassLoader newClassLoader(File file, ClassLoader parent) {
		File[] files = new File[1];
		files[0] = file;
		return newClassLoader(files, parent);
	}
	
	/** 해당 jar 파일을 통해 클래스를 불러올 수 있는 클래스로더 생성 - 주의 ! jar 파일인 경우 여러 파일을 넣으면 인식하지 못할 수 있음. */
	public static URLClassLoader newClassLoader(File[] files) {
		return newClassLoader(files, ROOT_CLASS_LOADER);
	}
	
	/** 해당 jar 파일을 통해 클래스를 불러올 수 있는 클래스로더 생성 - 주의 ! jar 파일인 경우 여러 파일을 넣으면 인식하지 못할 수 있음. */
	public static URLClassLoader newClassLoader(File[] files, ClassLoader parent) {
		try {
			URL[] urls = new URL[files.length];
			for(int idx=0; idx<urls.length; idx++) {
				urls[idx] = files[idx].toURI().toURL();
			}
			
			return newClassLoader(urls, parent);
		} catch(RuntimeException ex) {
			throw ex;
		} catch(Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
	
	/** 해당 jar 파일을 통해 클래스를 불러올 수 있는 클래스로더 생성 */
	public static URLClassLoader newClassLoader(URL url) {
		return newClassLoader(url, ROOT_CLASS_LOADER);
	}
	
	/** 해당 jar 파일을 통해 클래스를 불러올 수 있는 클래스로더 생성 */
	public static URLClassLoader newClassLoader(URL url, ClassLoader parent) {
		URL[] urls = new URL[1];
		urls[0] = url;
		return newClassLoader(urls, parent);
	}
	
	/** 해당 jar 파일을 통해 클래스를 불러올 수 있는 클래스로더 생성 - 주의 ! jar 파일인 경우 여러 파일을 넣으면 인식하지 못할 수 있음. */
	public static URLClassLoader newClassLoader(URL[] urls) {
		return newClassLoader(urls, ROOT_CLASS_LOADER);
	}
	
	/** 해당 jar 파일을 통해 클래스를 불러올 수 있는 클래스로더 생성 - 주의 ! jar 파일인 경우 여러 파일을 넣으면 인식하지 못할 수 있음. */
	public static URLClassLoader newClassLoader(URL[] urls, ClassLoader parent) {
		try {
		    URLClassLoader newOne = new URLClassLoader(urls, parent);
		    urlClassLoaders.add(newOne);
		    
		    return newOne;
		} catch(Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
	
	/** 모든 URLClassLoader 닫기 */
	public static synchronized void closeAll() {
		ClassUtil.closeAll(urlClassLoaders);
		urlClassLoaders.clear();
	}
}
