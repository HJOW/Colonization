package org.duckdns.hjow.addutils;

import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.duckdns.hjow.commons.exception.KnownRuntimeException;
import org.duckdns.hjow.commons.util.ClassUtil;

/** 공통 lib 의 ClassUtil 에 추가할 메소드들 임시 모음집 */
public class ClassUtilx { // TODO 공통 lib 으로 이관
	
	/** 현재 이 런타임의 클래스패스들을 반환 */
    public static Set<String> getClasspaths() {
    	String classpaths = System.getProperty("java.class.path");
    	
    	Set<String> list = new HashSet<String>();
    	StringTokenizer classpathTokenizer = new StringTokenizer(classpaths, File.pathSeparator); // File.separator 와는 다름에 주의 !
    	while(classpathTokenizer.hasMoreTokens()) {
    		String res = classpathTokenizer.nextToken().trim();
    		list.add(res);
    	}
    	return list;
    }
    
    /** 현재 이 런타임 내 모든 클래스 이름들을 반환. 단 자바 런타임의 클래스는 반환되지 않으며, * 기호가 들어간 클래스패스는 제외됨. 또한 서블릿 프로젝트의 WEB-INF / lib 디렉토리 내 jar 파일들 또한 보증하지 못함. */
    public static Set<String> getAllClassNames() {
    	Set<String> list = new HashSet<String>();
    	
    	for(String classPaths : getClasspaths()) {
    		if(classPaths.contains("*")) continue;
    		
    		File file = new File(classPaths);
    		if(! file.exists()) continue;
    		
    		if(file.isDirectory()) {
    			list.addAll(getClassNamesFromDirectory(file));
    		} else {
    			String lower = file.getName().toLowerCase();
    			if(! lower.endsWith(".jar")) continue;
    			
    			list.addAll(getClassNamesFromJar(file));
    		}
    	}
    	
    	return list;
    }
    
    /** 해당 디렉토리로부터 클래스 이름들을 찾아 반환. 이 디렉토리를 해당 클래스패스 경로로 생각하고 인식하므로, 안에 있는 다른 리소스 파일이나 jar 파일은 읽지 않음. */
    public static Set<String> getClassNamesFromDirectory(File dir) {
    	if(! dir.exists()) throw new KnownRuntimeException("There is no directory ! " + dir.getAbsolutePath());
    	if(! dir.isDirectory()) throw new KnownRuntimeException("This is not a directory ! " + dir.getAbsolutePath());
    	
    	String nowDir = dir.getAbsolutePath();
    	nowDir = nowDir.replace(File.separator, "/"); // 디렉토리 구분자 /로 통일
    	if(! nowDir.endsWith("/")) nowDir = nowDir + "/";
    	
    	Set<String> list = new HashSet<String>();
    	list.addAll(getClassNamesFromDirectory(dir, ""));
    	return list;
    }
    
    /** 외부에서 직접 호출 금지 ! */
    private static Set<String> getClassNamesFromDirectory(File dir, String packageName) {
    	Set<String> list = new HashSet<String>();
    	File[] files = dir.listFiles();
    	
    	for(File f : files) {
    		if(f.isDirectory()) {
    			list.addAll(getClassNamesFromDirectory(f, packageName + f.getName() + "."));
    		} else {
    			String name = f.getName();
    			if(! name.toLowerCase().endsWith(".class")) continue;
    			name = packageName + name.trim(); // 클래스 풀네임이 필요하므로, 앞에 패키지명 추가
    			
    			// 확장자 제거
    	    	name = name.substring(0, name.length() - 6);
    	    	
    	    	// $ 기호 있는 경우 뒷부분 자르기
    	    	if(name.contains("$")) {
    	    		int dollorIndex = name.indexOf("$");
    	    		name = name.substring(0, dollorIndex);
    	    	}
    			
    			list.add(name);
    		}
    	}
    	
    	return list;
    }
    
    /** 해당 jar 파일로부터 클래스 이름들을 찾아 반환 */
    public static Set<String> getClassNamesFromJar(File jarFile) {
    	if(! jarFile.exists()) throw new KnownRuntimeException("There is no jar ! " + jarFile.getAbsolutePath());
    	if(jarFile.isDirectory()) throw new KnownRuntimeException("This is not a file ! " + jarFile.getAbsolutePath());
    	Set<String> list = new HashSet<String>();
    	
    	JarFile jarInst = null;
    	try {
    	    jarInst = new JarFile(jarFile, false, JarFile.OPEN_READ);
    	    Enumeration<JarEntry> entries = jarInst.entries();
    	    while(entries.hasMoreElements()) {
    	    	JarEntry entry = entries.nextElement();
    	    	String name = entry.getName();
    	    	name = name.replace("/", ".");
    	    	
    	    	// 디렉토리와 리소스들 제외
    	    	if(! name.endsWith(".class")) continue;
    	    	if(entry.isDirectory()) continue;
    	    	// 확장자 제거
    	    	name = name.substring(0, name.length() - 6);
    	    	
    	    	// $ 기호 있는 경우 뒷부분 자르기
    	    	if(name.contains("$")) {
    	    		int dollorIndex = name.indexOf("$");
    	    		name = name.substring(0, dollorIndex);
    	    	}
    	    	
    	    	// 목록에 추가
    	    	list.add(name);
    	    }
    	} catch(Exception ex) {
    		throw new RuntimeException(ex.getMessage(), ex);
    	} finally {
    		ClassUtil.closeAll(jarInst);
    	}
    	return list;
    }
    
    /** 클래스 이름으로 클래스 객체 불러오기, 초기화 (static 블록 호출) 여부 지정 가능. (주의 ! 반환된 Class 객체가 나타내는 대상이 클래스가 아닌, 인터페이스나 어노테이션일 수도 있음.) */
    public static Class<?> forName(String className, boolean initialize) throws ClassNotFoundException {
    	ClassLoader loader = Thread.currentThread().getContextClassLoader();
    	return Class.forName(className, initialize, loader);
    }
    
    /** 해당 클래스/인터페이스의 자식 클래스들 모두 찾기 (반환되는 목록에서는 인터페이스와 어노테이션은 제외됨) */
    public static Set<Class<?>> getChildClasses(Class<?> parent, boolean initialize) {
    	Set<Class<?>> classSet = new HashSet<Class<?>>();
        for(String className : getAllClassNames()) {
        	try {
        	    Class<?> classOne = forName(className, false); // 일단 초기화하지 않고 불러오기
        	    if(classOne.isInterface() ) continue; // 인터페이스 건너뛰기
        	    if(classOne.isAnnotation()) continue; // 어노테이션 건너뛰기
        	    
        	    // 부모관계 아니면 건너뛰기
        	    if(parent.isInterface()) {
        	    	if(! parent.isAssignableFrom(classOne)) continue;
        	    } else {
        	    	if(! parent.isInstance(classOne)) continue;
        	    }
        	    
        	    if(initialize) classOne = forName(className, true); // 초기화 필요 시, 다시 불러오기
                classSet.add(classOne);
        	} catch(ClassNotFoundException ex) {
        		System.out.println(className + " not found !");
        	} catch(NoClassDefFoundError ex) {
        		System.out.println(className + " cannot be load ! " + ex.getMessage());
        	} catch(UnsupportedClassVersionError ex) {
        		System.out.println(className + " class version is unsupported !" + ex.getMessage());
        	} catch(Throwable ex) {
        	    throw new RuntimeException(ex.getMessage(), ex);
        	}
        }
        return classSet;
    }
}
