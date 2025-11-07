package org.duckdns.hjow.colonization;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import javax.script.ScriptEngine;

import org.duckdns.hjow.colonization.cheats.Cheat;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyInformation;
import org.duckdns.hjow.colonization.elements.facilities.ScriptFacilityInformation;
import org.duckdns.hjow.colonization.elements.policy.Policy;
import org.duckdns.hjow.colonization.mod.Mod;
import org.duckdns.hjow.colonization.pack.Library;
import org.duckdns.hjow.colonization.pack.Pack;
import org.duckdns.hjow.commons.exception.KnownRuntimeException;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.ClassUtil;
import org.duckdns.hjow.commons.util.DataUtil;
import org.duckdns.hjow.commons.util.FileUtil;
import org.duckdns.hjow.commons.util.NetUtil;

/** 정착지 시나리오, 시설, 연구, 시설과 시민의 상태 타입 등 클래스들과 타입 리스트를 관리하는 클래스 */
public class ColonyClassLoader {
    private static final List<Pack> packs = new Vector<Pack>();
    private static List<Class<?>> modClasses = new ArrayList<Class<?>>();
    
    private static final List<ColonyInformation> colonyInfoList     = new Vector<ColonyInformation>();
    private static       boolean                 colonyInfoListFlag = false; 
    
    /** 정착지 시나리오 정보들을 반환, 타입명과 제목, 설명, 클래스가 포함 */
    public static synchronized List<ColonyInformation> colonyInfos() {
        if(colonyInfoListFlag) return colonyInfoList;
        
        colonyInfoList.clear();
        for(Class<?> classOne : colonyClasses()) {
            ColonyInformation info = new ColonyInformation();
            
            try {
                Method method = classOne.getMethod("getColonyClassName");
                info.setName((String) method.invoke(null));
                
                method = classOne.getMethod("getColonyClassTitle");
                info.setTitle((String) method.invoke(null));
                
                method = classOne.getMethod("getColonyClassDescription");
                info.setDescription((String) method.invoke(null));
                
                method = classOne.getMethod("getAvailableDifficulties");
                info.setDifficulties((int[]) method.invoke(null));
                
                info.setColonyClass(classOne);
                if(! colonyInfoList.contains(info)) colonyInfoList.add(info);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        colonyInfoListFlag = true;
        return colonyInfoList;
    }
    
    private static final List<Class<?>> colonyClassList     = new Vector<Class<?>>();
    private static       boolean        colonyClassListFlag = false;
    
    /** 정착지 시나리오 클래스들을 반환 */
    public static synchronized List<Class<?>> colonyClasses() {
        if(colonyClassListFlag) return colonyClassList;
        
        colonyClassList.clear();
        for(Pack p : packs) { 
            if(p.isEnabled()) colonyClassList.addAll(p.getColonyClasses());
        }
        try { colonyClassList.add((Class<?>) Class.forName("org.duckdns.hjow.colonization.elements.custom.FreeColony")); } catch(Exception ex) { GlobalLogs.log(ColonyManager.t("java-default-pack not detected.")); }
        
        colonyClassListFlag = true;
        return colonyClassList;
    }
    
    /** 
     * 타입을 받아 그에 맞는 새 정착지 객체 생성, 해당 클래스 정보가 없으면 null 반환 
     * @param typeOrClass 정착지 타입명 혹은 클래스명
    */
    public static Colony newColonyInstance(String typeOrClass) {
        try {
            for(ColonyInformation info : colonyInfos()) {
                if(info.getName().equals(typeOrClass)) {
                    Colony col = (Colony) info.getColonyClass().newInstance();
                    return col;
                }
            }
            for(ColonyInformation info : colonyInfos()) {
                if(info.getColonyClass().getName().equals(typeOrClass) || info.getColonyClass().getSimpleName().equals(typeOrClass)) {
                    Colony col = (Colony) info.getColonyClass().newInstance();
                    return col;
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    /** 파일로부터 정착지 객체 읽어 반환
     * @param f 파일 객체 (확장자가 colgz 인 경우 GZIP 압축 해제 후 읽음)
    */
    public static Colony loadColony(File f) throws Exception {
        String fileName = f.getName().toLowerCase();
        String strJson;
        
        if(fileName.endsWith(".colgz")) {
            strJson = FileUtil.readString(f, "UTF-8", GZIPInputStream.class);
        } else {
            strJson = FileUtil.readString(f, "UTF-8");
        }
        strJson = DataUtil.remove65279(strJson);
        
        JsonObject json = (JsonObject) JsonObject.parseJson(strJson);
        return loadColony(json);
    }
    
    /**  JSON 으로부터 정착지 객체 읽어 반환 
     * @param json JSON 객체
    */
    public static Colony loadColony(JsonObject json) throws Exception {
        String type = json.get("type").toString();
        
        for(ColonyInformation info : colonyInfos()) {
            if(info.getName().equals(type)) {
                Colony col = (Colony) info.getColonyClass().newInstance();
                if(col == null) continue;
                
                col.fromJson(json);
                return col;
            }
        }
        return null;
    }
    
    private static final List<Class<?>> facilityClassList     = new Vector<Class<?>>();
    private static       boolean        facilityClassListFlag = false;
    
    /** 시설 클래스 목록 반환 */
    public static synchronized List<Class<?>> facilityClasses() {
        if(facilityClassListFlag) return facilityClassList;
        
        facilityClassList.clear();
        for(Pack p : packs) { if(p.isEnabled()) facilityClassList.addAll(p.getFacilityClasses()); }
        
        facilityClassListFlag = true;
        return facilityClassList;
    }
    
    private static final List<ScriptFacilityInformation> scriptFacilityInfo = new Vector<ScriptFacilityInformation>();
    /** 
     * 스크립트 Facility 불러오기 
     * @param cfg 프로그램 설정
     * @param man 게임 매니저 객체
    */
    protected static void loadScriptFacilities(ColonyManagerConfig cfg, ColonyManager man) {
        // 스크립트 Facility 사용 시 인증이 해제되므로, 설정 먼저 검사
        if(cfg.containsKey("UseCheckDisablingContent") && cfg.getBool("UseCheckDisablingContent")) {
            // 디렉토리 검사
            File scriptRoot = man.getColonyScriptRootDirectory();
            if(! scriptRoot.exists()) scriptRoot.mkdirs();
            
            File facRoot = new File(scriptRoot.getAbsolutePath() + File.separator + "facilities");
            if(! facRoot.exists()) facRoot.mkdirs();
            
            // 디렉토리 내 파일 스캔
            File[] lists = facRoot.listFiles(new FileFilter() {    
                @Override
                public boolean accept(File pathname) {
                    if(pathname.isDirectory()) return false;
                    return pathname.getName().toLowerCase().endsWith(".js"); // js 파일만 스캔
                }
            });
            
            for(File f : lists) {
                try {
                    // 스크립트 읽기
                    String scripts = FileUtil.readString(f, "UTF-8");
                    scripts = DataUtil.remove65279(scripts);
                    
                    // 엔진 준비
                    ScriptEngine engine = man.newScriptEngine();
                    if(engine == null) continue;
                    
                    // 리플렉션 존재여부 체크
                    ColonyManager.checkBannedKeywords(scripts);
                    
                    // 등록
                    ScriptFacilityInformation infoOne = new ScriptFacilityInformation(engine, scripts);
                    if(! scriptFacilityInfo.contains(infoOne)) scriptFacilityInfo.add(infoOne);
                } catch(Throwable tx) {
                    GlobalLogs.processExceptionOccured(tx, false);
                }
            }
        } else {
            scriptFacilityInfo.clear();
        }
    }
    
    /** 스크립트 Facility 리스트 반환 */
    public static List<ScriptFacilityInformation> getScriptFacilityList() {
        List<ScriptFacilityInformation> newList = new ArrayList<ScriptFacilityInformation>();
        newList.addAll(scriptFacilityInfo);
        return newList;
    }
    
    /** 
     * 해당 이름의 스크립트 Facility 반환 
     * 
     * @param name 스크립트 기반 시설정보 구분명
    */
    public static ScriptFacilityInformation getScriptFacilityOne(String name) {
        for(ScriptFacilityInformation s : getScriptFacilityList()) {
            if(s.getName().equals(name)) return s;
        }
        return null;
    }
    
    private static final List<Class<?>> researchClassList     = new Vector<Class<?>>();
    private static       boolean        researchClassListFlag = false;
    
    /** 연구 클래스 목록 반환 */
    public static synchronized List<Class<?>> researchClasses() {
        if(researchClassListFlag) return researchClassList;
        
        researchClassList.clear();
        for(Pack p : packs) { 
            if(p.isEnabled()) {
                researchClassList.addAll(p.getResearchClasses()); 
            }
        }
        
        researchClassListFlag = true;
        return researchClassList;
    }
    
    private static final List<Class<?>> enemyClassList     = new Vector<Class<?>>();
    private static       boolean        enemyClassListFlag = false;
    
    /** 적 클래스 목록 반환 */
    public static synchronized List<Class<?>> enemyClasses() {
        if(enemyClassListFlag) return enemyClassList;
        
        enemyClassList.clear();
        for(Pack p : packs) { if(p.isEnabled()) enemyClassList.addAll(p.getEnemyClasses()); }
        
        enemyClassListFlag = true;
        return enemyClassList;
    }
    
    private static final List<Class<?>> stateClassList     = new Vector<Class<?>>();
    private static       boolean        stateClassListFlag = false;
    
    /** 상태 클래스 목록 반환 */
    public static synchronized List<Class<?>> stateClasses() {
        if(stateClassListFlag) return stateClassList;
        
        stateClassList.clear();
        for(Pack p : packs) { if(p.isEnabled()) stateClassList.addAll(p.getStateClasses()); }
        
        stateClassListFlag = true;
        return stateClassList;
    }
    
    private static final List<Class<?>> productClassList     = new Vector<Class<?>>();
    private static       boolean        productClassListFlag = false;
    
    /** 생산품 클래스 목록 반환 */
    public static synchronized List<Class<?>> productClasses() {
        if(productClassListFlag) return productClassList;
        
        productClassList.clear();
        for(Pack p : packs) { if(p.isEnabled()) productClassList.addAll(p.getProductClasses()); }
        
        productClassListFlag = true;
        return productClassList;
    }
    
    private static final List<Class<?>> policyClassList     = new Vector<Class<?>>();
    private static       boolean        policyClassListFlag = false;
    
    /** 정책 클래스 목록 반환 */
    public static synchronized List<Class<?>> policyClasses() {
        if(policyClassListFlag) return policyClassList;
        
        policyClassList.clear();
        for(Pack p : packs) { if(p.isEnabled()) policyClassList.addAll(p.getPolicyClasses()); }
        
        policyClassListFlag = true;
        return policyClassList;
    }
    
    /** 
     * 정책 객체 생성
     * 
     * @param className 정책 클래스명 혹은 타입명
     */
    public static Policy createPolicyInstance(String className) {
        for(Class<?> classes : policyClassList) {
            if(! (classes.getSimpleName().equals(className) || classes.getName().equals(className))) continue;
            try {
                Policy p = (Policy) classes.newInstance();
                return p;
            } catch(Exception ex) {
                GlobalLogs.processExceptionOccured(ex, false);
            }
        }
        return null;
    }
    
    /** 기본 공지사항 컨텐츠 html 반환 (웹 접근 못했을 시 이 내용 출력) */
    public static String htmlNoticeEmpty() {
        StringBuilder res = new StringBuilder("");
        
        res = res.append("\n").append("<html>                                                                                                                                                                            ");
        res = res.append("\n").append("<head>                                                                                                                                                                            ");
        res = res.append("\n").append("<title>Notice</title>                                                                                                                                                             ");
        res = res.append("\n").append("</head>                                                                                                                                                                           ");
        res = res.append("\n").append("<body style='margin-left: 0; margin-right: 0; margin-top: 0; margin-bottom: 0; background-color: #EEEEEE;'>                                                                       ");
        res = res.append("\n").append("    <div style='padding-left: 30px; padding-top: 30px; font-size: 30px; font-family: NanumGothic, \"나눔고딕\", \"Nanum Gothic\", NanumGothicCoding, \"나눔고딕코딩\", \"Nanum Gothic Coding\", Arial, Consolas, \"돋움체\";'></div>");
        res = res.append("\n").append("</body>                                                                                                                                                                           ");
        res = res.append("\n").append("</html>                                                                                                                                                                           ");
        
        return res.toString().trim();
    }
    
    /** 공지사항 웹 URL 반환 */
    public static String htmlNoticeUrl() {
        return getWebConfigSwingNoticeKorean();
    }
    
    /** 공통 설정 URL 반환 (이 안에서 최신 버전 코드와 추가 컨텐츠 정보 등을 얻게 됨) */
    public static String htmlConfigJsonUrl() {
        return htmlRootUrl() + "content.json";
    }
    
    /** 공통 설정 최상단 URL 반환 */
    public static String htmlRootUrl() {
        return "http://hjow.duckdns.org/colonization/";
    }
    
    /** 웹 공통 설정 전체를 Json 으로 반환 */
    public static JsonObject getWebConfigRoot() {
        try {
            return (JsonObject) JsonObject.parseJson(NetUtil.sendGet(new URL(htmlConfigJsonUrl()), "UTF-8"));
        } catch(java.net.UnknownHostException ex) {
            throw new RuntimeException("Cannot connect to web config server. Please check the internet status.", ex);
        } catch(Throwable ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    /** 웹 공통 설정 중 Swing 파트를 Json 으로 반환 */
    public static JsonObject getWebConfigSwing() {
        JsonObject roots = getWebConfigRoot();
        return (JsonObject) roots.get("swing");
    }
    
    /** 웹 공통 설정 중 Swing 파트의 최신 버전을 문자열로 반환 */
    public static String getWebConfigSwingNewVersion() {
        return getWebConfigSwing().get("version").toString();
    }
    
    /** 웹 공통 설정 중 Swing 파트의 한글 공지사항 파일명 혹은 URL을 문자열로 반환 */
    public static String getWebConfigSwingNoticeKorean() {
        String res = getWebConfigSwing().get("noticeKo").toString();
        if(! res.startsWith("http")) res = htmlRootUrl() + res;
        return res;
    }
    
    /** 웹 공통 설정 중 Swing 파트의 빌드 목록을 Json 객체로 반환 */
    public static JsonObject getWebConfigSwingBuilds() {
        return (JsonObject) getWebConfigSwing().get("builds");
    }
    
    /** 웹 공통 설정 중 Swing 파트의 lib 내 들어갈 목록을 Json 배열로 반환 */
    public static JsonArray getWebConfigSwingLibs() {
        return (JsonArray) getWebConfigSwing().get("libs");
    }
    
    /** 저장 경로 내 lib 디렉토리 반환 */
    public static File getHomeLibDir() {
        return ColonyManager.getHomeDir("colonization", "lib");
    }
    
    /** lib 디렉토리 내 packs.txt 파일 반환 */
    public static File getLibPackClassFile() {
        return new File(getHomeLibDir().getAbsolutePath() + File.separator + "packs.txt");
    }
    
    /** 
     * 공통 설정 정보 조회
     * @param man 게임 매니저 객체
     */
    public static synchronized void loadWebConfigs(ColonyManager man) {
        try {
            applyWebConfigs(getWebConfigSwing(), man);
        } catch(Exception ex) {
            GlobalLogs.processExceptionOccured(ex, true);
        }
    }
    
    /** 공통 웹 설정 정보 적용 (현재는 아무것도 하지 않음) */
    protected static void applyWebConfigs(JsonObject json, ColonyManager man) throws Exception {
        // TODO
    }
    
    /** 예약된 Library 클래스명 */
    protected static final String[] RESERVED_LIB_NAMES = {
        "org.duckdns.hjow.colonization.addpack.AddPackInfo"
      , "org.duckdns.hjow.colonization.addpack.DebugPackInfo"
    };
    
    /** 기본 제공되는 Library 클래스명 배열 반환 */
    public static String[] getReservedLibraryClassNames() {
        String[] newArr = new String[RESERVED_LIB_NAMES.length];
        for(int idx=0; idx<newArr.length; idx++) { newArr[idx] = RESERVED_LIB_NAMES[idx]; }
        return newArr;
    }
    
    /** 공통 로컬 설정 정보 적용, Pack 불러오기 */
    public static void applyLocalConfigs(ColonyManagerConfig cfg, ColonyManager man) {
        // 예약어로 지정된 Pack 불러오기
        for(String resv : RESERVED_LIB_NAMES) {
            processAddClass(resv, man);
        }
        
        // Pack class file 탐색
        File libDir = man.getColonyLibRootDirectory();
        if(! libDir.exists()) libDir.mkdirs();
        File packClassFile = new File(libDir.getAbsolutePath() + File.separator + "packs.txt");
        if(packClassFile.exists()) {
            try {
                String packClassContent = FileUtil.readString(packClassFile, "UTF-8");
                packClassContent = DataUtil.remove65279(packClassContent);
                StringTokenizer lineTokenizer = new StringTokenizer(packClassContent, "\n");
                while(lineTokenizer.hasMoreTokens()) {
                    String line = lineTokenizer.nextToken().trim();
                    if(line.startsWith("#")) continue;
                    if(DataUtil.isEmpty(line)) continue;
                    processAddClass(line, man);
                }
                
            } catch(Exception ex) {
                GlobalLogs.processExceptionOccured(ex, false);
            }
        } else {
            String newContent = "# 불러올 Pack 의 class name 을 이 파일에 기재해 주세요. 한줄에 하나씩 입력해 주세요. # 기호로 시작하는 줄은 무시됩니다.\n";
            try { FileUtil.writeString(packClassFile, "UTF-8", newContent); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        }
        
        // Pack 모두 열어 내용물 적용
        loadAllListedPacks();
        
        // 스크립트 Facility 적용
        loadScriptFacilities(cfg, man);
    }

    /** 클래스명 체크 */
    public static void checkModClassName(String modClassName) {
        if(modClassName.contains(",")) throw new KnownRuntimeException(ColonyManager.t("클래스명에는 , 기호가 들어갈 수 없습니다."));
        if(modClassName.contains(";")) throw new KnownRuntimeException(ColonyManager.t("클래스명에는 ; 기호가 들어갈 수 없습니다."));
        if(modClassName.contains("!")) throw new KnownRuntimeException(ColonyManager.t("클래스명에는 ! 기호가 들어갈 수 없습니다."));
        if(modClassName.contains("\"")) throw new KnownRuntimeException(ColonyManager.t("클래스명에는 \" 기호가 들어갈 수 없습니다."));
        if(modClassName.contains("'")) throw new KnownRuntimeException(ColonyManager.t("클래스명에는 ' 기호가 들어갈 수 없습니다."));
        if(modClassName.contains(" ") || modClassName.contains("\t") || modClassName.contains("\n")) throw new KnownRuntimeException(ColonyManager.t("클래스명에는 공백 기호가 들어갈 수 없습니다."));
    }
    
    /** 
     * 클래스 불러오기, Library 나 Pack, Mod 인식. (선택사항으로, 클래스를 찾을 수 없어도 다음 단계로 넘어감) 
     * 
     * @param className 불러올 클래스명, 이 클래스를 불러온 후 Library, Pack, Mode 등을 인식헤 그에 맞게 동작함
     * @param man 게임 매니저 객체
    */
    @SuppressWarnings("unchecked")
    protected static void processAddClass(String className, ColonyManager man) {
        Object instances = null;
        try {
            Class<?> classSomeone = Class.forName(className);
            instances = classSomeone.newInstance();
            
            if(instances instanceof Library) {
                Library library = (Library) instances;
                List<Pack> addPacks = library.getPacks();
                for(Pack packOne : addPacks) {
                    if(! packs.contains(packOne)) packs.add(packOne);
                }
                List<Mod> mods = library.getMods();
                if(mods != null) {
                    for(Mod m : mods) {
                        Class<?> modClass = m.getClass();
                        if(! modClasses.contains(modClass)) modClasses.add(modClass);
                    }
                }
            } else if(instances instanceof Pack) {
                Pack pack = (Pack) instances;
                if(! packs.contains(pack)) packs.add(pack);
            } else if(instances instanceof Mod) {
                if(! modClasses.contains(classSomeone)) modClasses.add(classSomeone);
            } else if(instances instanceof Cheat) {
                Cheat.register((Cheat) instances);
            }
        } catch(ClassNotFoundException ex) {
            // DO Nothing
        } catch(Exception ex) {
            GlobalLogs.processExceptionOccured(ex, false);
        }
        
        if(instances != null) {
            try {
                // Cheat 목록도 제공하는지 확인 (선택사항이므로 리플렉션으로 접근)
                Class<?> libClass = instances.getClass();
                Method mthd = libClass.getMethod("getCheats");
                List<Cheat> cheats = (List<Cheat>) mthd.invoke(instances);
                for(Cheat c : cheats) { Cheat.register(c); }
            } catch(NoSuchMethodException ex) {
                // DO Nothing
            } catch(Exception ex) {
                GlobalLogs.processExceptionOccured(ex, false);
            }
        }
    }
    
    /** Pack 모두 열어 내용물 적용 */
    protected static void loadAllListedPacks() {
        for(Pack p : getInstalledPacks()) {
            try {
                loadPack(p);
            } catch(Exception ex) {
                GlobalLogs.processExceptionOccured(ex, false);
            }
        }
    }
    
    /** 
     * 설정 Map 으로부터 클래스 정보 추출 
     * 
     * @param info 설정 정보 객체
    */
    public static Class<?> loadClassFrom(ColonyManagerConfig info) throws Exception {
        String className = info.getString("name");
        String classUrl  = info.getString("url");
        URL[] urls = new URL[1];
        urls[0] = new URL(classUrl);
        
        URLClassLoader loader = null;
        try {
            loader = new URLClassLoader(urls, ColonyClassLoader.class.getClassLoader());
            Class<?> classObj = loader.loadClass(className);
            return classObj;
        } finally {
            ClassUtil.closeAll(loader);
        }
    }
    
    /** Pack 적용 */
    public static void loadPack(Pack pack) throws Exception {
        if(pack == null) return;
        if(! pack.isEnabled()) return;
        
        if(pack.getColonyClasses() != null) {
            colonyClassList.addAll(pack.getColonyClasses());
            colonyInfoListFlag = false;
        }
        
        if(pack.getFacilityClasses() != null) facilityClassList.addAll(pack.getFacilityClasses());
        if(pack.getResearchClasses() != null) researchClassList.addAll(pack.getResearchClasses());
        if(pack.getEnemyClasses()    != null) enemyClassList.addAll(pack.getEnemyClasses());
        if(pack.getStateClasses()    != null) stateClassList.addAll(pack.getStateClasses());
        if(pack.getProductClasses()  != null) productClassList.addAll(pack.getProductClasses());
        if(pack.getPolicyClasses()   != null) policyClassList.addAll(pack.getPolicyClasses());
    }
    
    /** 등록된 Pack 객체들 리턴 (새 List 객체로 리턴) */
    public static synchronized List<Pack> getInstalledPacks() {
        List<Pack> newList = new ArrayList<Pack>();
        newList.addAll(packs);
        return newList;
    }
    
    /** 기본 제공 Pack 불러오기 */
    private static void loadDefaultPacks() {
        try { packs.add((Pack) Class.forName("org.duckdns.hjow.colonization.pack.BundledPack").newInstance()); } catch(Exception ex) { throw new RuntimeException("java-default-pack not detected."); }
    }
    
    /** Pack class 를 받아, 그에 해당하는 이미 불러온 Pack 객체를 리턴 */
    public static Pack getInstalledPackInstance(Class<?> packClass) {
        for(Pack p : getInstalledPacks()) {
            if(p.getClass() == packClass) return p;
        }
        return null;
    }
    
    /** Pack 으로 인해 사용 허가되어야 하는 기능 키워드 반환 */
    public static List<String> getInstalledPackNewFeatures() {
        List<String> list = new ArrayList<String>();
        for(Pack p : getInstalledPacks()) {
            if(p.newFeatures() != null) list.addAll(p.newFeatures());
        }
        return list;
    }
    
    /** lib 안에 등록되어 있던 MOD 지원 클래스들 반환 */
    public static List<Class<?>> getModClasses() {
        List<Class<?>> newList = new ArrayList<Class<?>>();
        newList.addAll(modClasses);
        return newList;
    }
    
    /** 클래스 정보들과, 불러온 Pack 모두 다시 확인 */
    public static synchronized void refresh() {
        colonyInfoListFlag    = false;
        colonyClassListFlag   = false;
        facilityClassListFlag = false;
        researchClassListFlag = false;
        enemyClassListFlag    = false;
        stateClassListFlag    = false;
        productClassListFlag  = false;
        policyClassListFlag   = false;
    }
    
    /** 저장된 클래스 정보들 비우기 */
    public static synchronized void clearAll() {
        colonyInfoListFlag    = false; colonyInfoList.clear();
        colonyClassListFlag   = false; colonyClassList.clear();
        facilityClassListFlag = false; facilityClassList.clear();
        researchClassListFlag = false; researchClassList.clear();
        enemyClassListFlag    = false; enemyClassList.clear();   
        stateClassListFlag    = false; stateClassList.clear();
        productClassListFlag  = false; productClassList.clear();
        policyClassListFlag   = false; policyClassList.clear();
        for(ScriptFacilityInformation s : scriptFacilityInfo) { s.dispose(); }
        scriptFacilityInfo.clear();
        packs.clear();
        loadDefaultPacks();
    }
}
