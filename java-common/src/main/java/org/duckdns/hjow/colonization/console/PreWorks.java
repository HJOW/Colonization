package org.duckdns.hjow.colonization.console;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.duckdns.hjow.colonization.ColonyClassLoader;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.DataUtil;
import org.duckdns.hjow.commons.util.FileUtil;
import org.duckdns.hjow.commons.util.NetUtil;

/** 프로그램 실행 전 사전 작업 */
public class PreWorks {
    protected Map<String, String> params = new HashMap<String, String>();
    public PreWorks() {}
    public PreWorks(Map<String, String> params) { this();  this.params.putAll(params); }
    
    protected boolean runOffline = false;
    protected JsonObject jsonConfigSwing;
    protected String versionNew;
    
    /** 사전 작업 수행 */
    public final void work() {
        String strUsingUpdator = params.get("--updator");
        boolean runOffline = true;
        if(DataUtil.isNotEmpty(strUsingUpdator)) runOffline = (! DataUtil.parseBoolean(strUsingUpdator.trim()));
        
        try { prepareConnectHttp();  } catch(Throwable ex) { ex.printStackTrace(); runOffline = true; jsonConfigSwing = null; return; }// 서버 접속을 아예 못한 경우 PreWork 작업 자체를 중단
        try { prepareDownloadLibs(); } catch(Throwable ex) { ex.printStackTrace(); runOffline = true;  }
        
        if(! runOffline) {
            try { downloadNewVersion(); } catch(Throwable ex) { ex.printStackTrace(); runOffline = true;  } // 새 버전 다운로드 및 실행되는 경우 새 버전 실행 후 이 프로세스는 종료됨 !
            // 위 downloadNewVersion 에서, 새 버전 실행이 되면, 이 프로세스가 종료되므로
            //    이 시점에서 메소드 동작이 중단됨 !
        }
        
        // 기타 사항 준비 (현재의 버전으로 구동 시에만 호출됨)
        try { prepareOthers(); } catch(Throwable ex) { ex.printStackTrace(); }
    }
    
    /** 버전 체크 */
    protected void prepareConnectHttp() throws Throwable {
        JsonObject jsonConfig = ColonyClassLoader.getWebConfigRoot();
        jsonConfigSwing = (JsonObject) jsonConfig.get("swing");
        versionNew = jsonConfigSwing.get("version").toString().trim();
        
        int[] nowVersion = ColonyManager.getVersionArray();
        int[] newVersion = ColonyManager.parseVersionString(versionNew);
        
        if(nowVersion[0] >  newVersion[0]) { runOffline = true; return; }
        if(nowVersion[0] == newVersion[0] && nowVersion[1] >  newVersion[1]) { runOffline = true; return; }
        if(nowVersion[0] == newVersion[0] && nowVersion[1] == newVersion[1] && nowVersion[2] >= newVersion[2]) { runOffline = true; return; }
    }
    
    /** lib 누락사항 다운로드 받기 (단, 이 항목들은 다음 실행 때 적용됨. 이번 런타임에는 적용되지 않음.) */
    protected void prepareDownloadLibs() throws Throwable {
        if(jsonConfigSwing == null) return;
        
        // lib 폴더 (사용자홈 / .colonization / )
        File libRoot = ColonyClassLoader.getHomeLibDir();
        if(! libRoot.exists()) libRoot.mkdirs();
        
        // 기존 Pack 클래스들 목록 읽기
        List<String> packClasses = new ArrayList<String>();
        String packClassComments = "";
        File packClassFile = ColonyClassLoader.getLibPackClassFile();
        
        if(packClassFile.exists()) {
            String packClassText = FileUtil.readString(packClassFile, "UTF-8");
            StringTokenizer lineTokenizer = new StringTokenizer(packClassText, "\n");
            
            while(lineTokenizer.hasMoreTokens()) {
                String line = lineTokenizer.nextToken().trim();
                if(line.startsWith("#")) {
                    packClassComments += "\n" + line;
                    continue;
                }
                
                packClasses.add(line);
            }
            packClassComments = packClassComments.trim();
        }
        
        // lib 폴더 안에 들어갈 파일 다운로드
        JsonArray libs = (JsonArray) jsonConfigSwing.get("libs");
        for(Object obj : libs) {
            try {
                JsonObject libOne = (JsonObject) obj;
                String libUrl  = libOne.get("url").toString();
                String libName = libOne.get("name").toString();
                
                if(! libUrl.startsWith("http")) libUrl = ColonyClassLoader.htmlRootUrl() + libUrl;
                
                // 해당 lib 미존재 시 다운로드
                File file = new File(libRoot.getAbsolutePath() + File.separator + libName);
                if(! file.exists()) {
                    NetUtil.download(new URL(libUrl), file);
                }
                
                String libPack = (libOne.get("pack") == null ? null : libOne.get("pack").toString());
                if(DataUtil.isNotEmpty(libPack)) {
                    if(! packClasses.contains(libPack)) packClasses.add(libPack);
                }
                
            } catch(Exception ex) {
                ex.printStackTrace(); // UI 초기화 전이므로 표준 출력 사용
            }
        }
        
        // 주석 내용 처리
        if(DataUtil.isEmpty(packClassComments)) {
            packClassComments = "# 불러올 Pack 의 class name 을 이 파일에 기재해 주세요. 한줄에 하나씩 입력해 주세요. # 기호로 시작하는 줄은 무시됩니다.";
        }
        
        // packs.txt 작성
        StringBuilder packRebuild = new StringBuilder("");
        packRebuild = packRebuild.append("\n").append(packClassComments);
        for(String str : packClasses) {
            packRebuild = packRebuild.append("\n").append(str);
        }
        FileUtil.writeString(packClassFile, "UTF-8", packRebuild.toString().trim());        
    }
    
    /** 필요 시 새 버전 다운로드, 다운로드한 jar 실행 후 이 프로세스는 종료가 됨 ! 반대로, 다운로드 혹은 실행에 실패할 경우 이 메소드가 예외처리 혹은 return으로 빠져 나가므로 종료되지 않음. */
    protected void downloadNewVersion() throws Throwable {
        if(jsonConfigSwing == null) return;
        if(runOffline) return;
        
        File buildPath = ColonyManager.getHomeDir("colonization", "build");
        if(! buildPath.exists()) buildPath.mkdirs();
        
        File targetToRun = null;
        
        // 최신 버전 존재여부 확인
        targetToRun = new File(buildPath.getAbsolutePath() + File.separator + "colonization-swing-" + versionNew + ".jar");
        if(! targetToRun.exists()) {
            // 존재하지 않으면 다운로드 시도
            try {
                JsonObject buildConfig = (JsonObject) jsonConfigSwing.get("builds");
                JsonObject versionInfo = (JsonObject) buildConfig.get(versionNew);
                
                String downloadUrl = versionInfo.get("url").toString();
                if(! downloadUrl.startsWith("http")) downloadUrl = ColonyClassLoader.htmlRootUrl() + downloadUrl;
                
                NetUtil.download(new URL(downloadUrl), targetToRun);
            } catch(Throwable tx) {
                tx.printStackTrace();
                targetToRun = null; // 다운로드 실패 시 null 처리
            }
        }
        
        // 다운로드 실패 - 이미 다운로드 되어있는 파일들 중 그나마 최신버전 찾기
        if(targetToRun == null) {
            File[] arr = buildPath.listFiles(new FileFilter() {    
                @Override
                public boolean accept(File pathname) {
                    String lower = pathname.getName().trim();
                    return (lower.startsWith("colonization-swing-") && lower.endsWith(".jar"));
                }
            });
            
            if(arr.length <= 0) {
                runOffline = true;
                targetToRun = null;
                return;
            }
            
            // 컬렉션으로 변경
            List<File> lists = new ArrayList<File>();
            for(File f : arr) {
                lists.add(f);
            }
            arr = null;
            
            // 정렬
            Collections.sort(lists);
            
            // 마지막 파일 (버전이 가장 높은 파일)
            targetToRun = lists.get(lists.size() - 1);
        }
        
        if(targetToRun == null) return;
        
        // jar 파일 실행
        String javaHome = System.getProperty("java.home");
        File jreBinPath = new File(javaHome + File.separator + "bin");
        File libRoot = ColonyClassLoader.getHomeLibDir();
        
        List<String> commands = new ArrayList<String>();
        commands.add(jreBinPath.getAbsolutePath() + File.separator + "java");
        commands.add("-jar");
        commands.add(targetToRun.getAbsolutePath());
        commands.add("-cp");
        commands.add(libRoot.getAbsolutePath() + File.separator + "*");
        commands.add("--updator");
        commands.add("N");
        
        ProcessBuilder procBuilder = new ProcessBuilder(commands);
        procBuilder.directory(jreBinPath);
        
        System.out.println("Run downloaded newer version of colonization - " + targetToRun.getAbsolutePath());
        procBuilder.start();
        
        System.exit(0);
    }
    
    /** lib 클래스로더 생성 */
    public static URLClassLoader LibClassLoader() throws MalformedURLException {
        // lib 폴더 (사용자홈 / .colonization / )
        File libRoot = ColonyClassLoader.getHomeLibDir();
        if(! libRoot.exists()) libRoot.mkdirs();
        
        File[] lists = libRoot.listFiles(new FileFilter() {    
            @Override
            public boolean accept(File pathname) {
                if(pathname.isDirectory()) return false;
                return pathname.getName().toLowerCase().endsWith(".jar");
            }
        }); // TODO 여러 jar 파일이 들어가면 인식을 못함.
        
        URL[] urls = new URL[lists.length];
        for(int idx=0; idx<urls.length; idx++) {
            urls[idx] = lists[idx].toURI().toURL();
        }
        
        return new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
    }
    
    /** 기타 사항 준비 */
    protected void prepareOthers() throws Throwable {
        System.out.println("Run current version of colonization.");
    }
    
    public Map<String, String> getParams() {
        return params;
    }
    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
