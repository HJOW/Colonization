package org.duckdns.hjow.colonization.console;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.duckdns.hjow.colonization.ColonyClassLoader;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.ClassUtil;
import org.duckdns.hjow.commons.util.DataUtil;
import org.duckdns.hjow.commons.util.FileUtil;

/** 프로그램 실행 전 사전 작업 */
public class PreWorks {
    protected Map<String, String> params = new HashMap<String, String>();
    public PreWorks() {}
    public PreWorks(Map<String, String> params) { this();  this.params.putAll(params); }
    
    /** 사전 작업 수행 */
    public final void work() {
        try { prepareLibs(); } catch(Exception ex) { ex.printStackTrace(); } // UI 초기화 전이므로 표준 출력 사용
    }
    
    /** lib 누락사항 다운로드 받기 (단, 이 항목들은 다음 실행 때 적용됨. 이번 런타임에는 적용되지 않음.) */
    protected void prepareLibs() throws Exception {
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
        JsonArray libs = ColonyClassLoader.getWebConfigSwingLibs();
        for(Object obj : libs) {
            InputStream  finp = null;
            OutputStream fout = null;
            byte[] buffer = new byte[2048];
            int r;
            try {
                JsonObject libOne = (JsonObject) obj;
                String libUrl  = libOne.get("url").toString();
                String libName = libOne.get("name").toString();
                
                if(! libUrl.startsWith("http")) libUrl = ColonyClassLoader.getWebConfigRoot() + libUrl;
                
                // 해당 lib 미존재 시 다운로드
                File file = new File(libRoot.getAbsolutePath() + File.separator + libName);
                if(! file.exists()) {
                    fout = new FileOutputStream(file);
                    finp = new URL(libUrl).openStream();
                    
                    while(true) {
                        r = finp.read(buffer);
                        if(r < 0) break;
                        fout.write(buffer, 0, r);
                    }
                    
                    ClassUtil.closeAll(fout, finp);
                    fout = null;
                    finp = null;
                }
                
                String libPack = (libOne.get("pack") == null ? null : libOne.get("pack").toString());
                if(DataUtil.isNotEmpty(libPack)) {
                	if(! packClasses.contains(libPack)) packClasses.add(libPack);
                }
                
            } catch(Exception ex) {
                ex.printStackTrace(); // UI 초기화 전이므로 표준 출력 사용
            } finally {
                ClassUtil.closeAll(fout, finp);
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
    
    public Map<String, String> getParams() {
        return params;
    }
    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
