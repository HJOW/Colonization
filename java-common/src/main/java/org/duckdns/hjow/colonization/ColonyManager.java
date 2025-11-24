package org.duckdns.hjow.colonization;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.duckdns.hjow.colonization.cheats.Cheat;
import org.duckdns.hjow.colonization.constants.Constants;
import org.duckdns.hjow.colonization.elements.AbstractColony;
import org.duckdns.hjow.colonization.elements.AttackableObject;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.ColonyElements;
import org.duckdns.hjow.colonization.elements.HasLocation;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.mod.Mod;
import org.duckdns.hjow.colonization.mod.ScriptMod;
import org.duckdns.hjow.colonization.pack.Library;
import org.duckdns.hjow.colonization.script.NetObject;
import org.duckdns.hjow.colonization.script.PrimitiveObject;
import org.duckdns.hjow.colonization.script.ScriptClassLoader;
import org.duckdns.hjow.colonization.ui.ColonyPanel;
import org.duckdns.hjow.colonization.ui.GlobalLogUI;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.resource.BufferedFileStringTable;
import org.duckdns.hjow.commons.script.MathObject;
import org.duckdns.hjow.commons.script.ScriptPatternDetector;
import org.duckdns.hjow.commons.script.ScriptUtil;
import org.duckdns.hjow.commons.script.SecurityObject;
import org.duckdns.hjow.commons.util.DataUtil;
import org.duckdns.hjow.commons.util.FileUtil;

/** 
 * 게임 매니저 클래스로, 
 *     Colonization 프로그램 핵심 클래스 
 * 
 * 게임 동작에 필요한 설정, 메인 쓰레드, 플래그 변수들을 여기서 관리
*/
public abstract class ColonyManager implements ColonyManagerInterface, Serializable {
    private static final long serialVersionUID = -5740844908011980260L;
    
    protected transient ColonizationMainClass superInstance;
    protected transient Thread thread;
    protected transient volatile boolean threadSwitch, threadPaused, threadShutdown, reserveSaving, reserveRefresh;
    protected transient volatile boolean bCheckerPauseCompleted = false;
    protected transient ColonyManagerConfig configs = new DefaultColonyManagerConfig();
    
    protected transient volatile Vector<Colony> colonies = new Vector<Colony>();
    protected transient volatile int  selectedColony = -1;
    protected transient volatile int  cycle = 0;
    protected transient volatile long cycleGap = CYCLEGAP_DEFAULT;
    protected transient volatile long cycleGapEachCity     = 0L;
    protected transient volatile long cycleGapEachFacility = 0L;
    protected transient volatile long cycleRunningTime = 0L;
    protected transient volatile int  cycleRunCount = -1;
    protected transient volatile int  cycleSkipRefr = 3; // 컨텐츠 새로고침 주기 (값이 크면 성능 개선 but 화면 내 값 변화 속도가 느리다고 느껴지게 됨)
    
    protected transient volatile boolean flagSaveBeforeClose = true; // 종료 시 저장 플래그
    protected transient volatile boolean flagAlreadyDisposed = false;
    protected transient volatile boolean flagUseCheckDisablingContent = false; // 인증 해제 요인이 되는 컨텐츠 사용 여부
    
    protected transient BigInteger time;
    
    protected transient List<Mod> modsList    = new ArrayList<Mod>();
    protected transient List<Mod> modsEnabled = new ArrayList<Mod>();
    protected transient ColonyManagerBroker broker;
    
    protected transient ScriptEngineManager scriptEngineManager = null;
    protected transient ScriptEngine        rootEngine          = null;
    protected transient JsonObject          storage             = new JsonObject();
    
    protected transient String scriptLanguage = "JavaScript";
    protected transient String scriptVarPrefix = "a" + (100000 + (int) (random() * 899999));
    
    /** 기본 생성자 */
    public ColonyManager() {
        threadSwitch        = false;
        threadPaused        = true;
        threadShutdown      = true;
        reserveSaving       = false;
        reserveRefresh      = false;
        flagAlreadyDisposed = false;
        broker = new ColonyManagerBroker(this);
    }

    /** 자기자신 객체 반환 (익명클래스 내부에서 활용 용도) */
    public ColonyManager getSelf() { return this; }

    /** 메인 쓰레드 구동 중인지 확인하여, 미구동 중인 경우 구동 시작 */
    public void assureMainThreadRunning() {
        if(thread == null || (! threadSwitch)) turnOnMainThread();
    }
    
    /** 메인 쓰레드 실행 */
    protected void turnOnMainThread() {
        if(thread != null) {
            thread.interrupt();
            try { Thread.sleep(1000L); } catch(InterruptedException ex) { GlobalLogs.processExceptionOccured(ex, false); }
        }
        thread = new Thread(new Runnable() {    
            @Override
            public void run() {
                while(threadSwitch) {
                    if(! onMainThread()) break;
                }
                threadShutdown = true;
            }
        });
        threadSwitch   = true;
        threadShutdown = false;
        flagSaveBeforeClose = true;
        thread.start();
    }
    
    /** 메인 쓰레드 내에서 단독으로 실행되는 메소드 */
    protected boolean onMainThread() {
        threadShutdown = false;
        long elapsed = System.currentTimeMillis();
        long gap = cycleGap;
        
        // 쓰레드에서 수행할 실질 작업 수행
        try { if(! threadPaused) { bCheckerPauseCompleted = false; oneCycle(); } } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        
        // 실행 횟수 제한이 있는 경우 차감 (단, 음수인 경우는 무제한이라고 판단)
        if(cycleRunCount > 0 && (! threadPaused)) {
            cycleRunCount--;
            if(cycleRunCount <= 0) pauseSimulation();
        }
        
        // 저장 요청 수행
        if(reserveSaving) { try { saveColonies(); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); } reserveSaving = false; }
        
        // 일시정지 후 쓰레드가 실제 정지 중인지 판단하는 플래그
        if(threadPaused) bCheckerPauseCompleted = true;
        else bCheckerPauseCompleted = false;
        
        // 쓰레드 Sleep
        try { Thread.sleep(gap); } catch(InterruptedException e) { threadSwitch = false; return false; }

        cycleRunningTime = System.currentTimeMillis() - elapsed - gap;
        threadShutdown = false;
        
        return true;
    }
    
    /** 지원되는 시뮬 속도 목록 반환 */
    protected Vector<SimulationSpeed> getSpeedList() {
        Vector<SimulationSpeed> strSpeeds = new Vector<SimulationSpeed>();
        strSpeeds.add(new SimulationSpeed(1));
        strSpeeds.add(new SimulationSpeed(2));
        strSpeeds.add(new SimulationSpeed(3));
        
        for(Mod m : getMods()) {
            try {
                Class<? extends Mod> modClass = m.getClass();
                Method mthdSpeed = modClass.getMethod("getAdditionalSimulationSpeeds");
                SimulationSpeed speedOne = (SimulationSpeed) mthdSpeed.invoke(m);
                if(! strSpeeds.contains(speedOne)) strSpeeds.add(speedOne);
            } catch(Throwable tx) {
                GlobalLogs.processExceptionOccured(tx, false);
            }
        }
        
        return strSpeeds;
    }
    
    /** 정착지 세이브 파일 필터 생성 */
    public FileFilter getColonyFileFilter() {
        return new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if(pathname.isDirectory()) return false;
                String nameLower = pathname.getName().toLowerCase();
                return nameLower.endsWith(".colony") || nameLower.endsWith(".colgz");
            }
        };
    }
    
    /** 정착지 세이브 기본 경로 반환 */
    public File getColonySaveRootDirectory() {
        return getHomeDir("colonization", "saves");
    }
    
    /** 정착지 설정 기본 경로 반환 */
    @Override
    public File getColonyConfigRootDirectory() {
        return getHomeDir("colonization", "configs");
    }
    
    /** 정착지 Script 기본 경로 반환 */
    public File getColonyScriptRootDirectory() {
        return getHomeDir("colonization", "scripts");
    }
    
    /** 정착지 lib 기본 경로 반환 */
    public File getColonyLibRootDirectory() {
        return getHomeDir("colonization", "lib");
    }
    
    /** Colonization 기본 설정 불러오기 */
    public void loadLocalConfigs() {
    	GlobalLogs.log("Loading local configurations...");
    	
        File root = getColonyConfigRootDirectory();
        if(! root.exists()) root.mkdirs();
        
        try {
            // 기본 설정 파일 불러오기
            File conf = new File(root.getAbsolutePath() + File.separator + "config.json");
            if(conf.exists()) {
                String strJson = FileUtil.readString(conf, "UTF-8"); // 파일 읽고
                strJson = DataUtil.remove65279(strJson);
                
                JsonObject json = (JsonObject) JsonObject.parseJson(strJson); // JSON 파싱
                configs.fromJson(json); // 설정 넣기
            } else {
                JsonObject json = new JsonObject();
                FileUtil.writeString(conf, "UTF-8", json.toJSON());
            }
            if(configs instanceof DefaultColonyManagerConfig) ((DefaultColonyManagerConfig) configs).setConfigSaveOnNotExistingKeys(true);
            
            // 스트링 테이블 불러오기
            String stringTablePath = configs.getString("StringTableFile");
            File fileStringTable = null;
            if(DataUtil.isNotEmpty(stringTablePath)) {
                stringTablePath = stringTablePath.replace("[CONFIGPATH]", root.getAbsolutePath());
                fileStringTable = new File(stringTablePath.trim());
            }
            if(fileStringTable == null) {
                fileStringTable = new File(root.getAbsolutePath() + File.separator + "stringTable.xml");
                configs.set("StringTableFile", fileStringTable.getAbsolutePath().replace(root.getAbsolutePath(), "[CONFIGPATH]"));
            }
            
            if(! fileStringTable.exists()) {
                fileStringTable = new File(root.getAbsolutePath() + File.separator + "stringTable.xml");
                configs.set("StringTableFile", fileStringTable.getAbsolutePath().replace(root.getAbsolutePath(), "[CONFIGPATH]"));
                
                Properties newProp = new Properties();
                // TODO : 기본 데이터 불러오기
                FileUtil.saveProperties(fileStringTable, newProp);
            }
            BufferedFileStringTable stringTable = new BufferedFileStringTable(fileStringTable);
            STRINGTABLE.setOriginalInstance(stringTable);
            
            // 인증 해제 요인이 되는 요소들 사용 여부
            String strUseChkDis = configs.getString("UseCheckDisablingContent");
            if(DataUtil.isEmpty(strUseChkDis)) { strUseChkDis = "N"; configs.set("UseCheckDisablingContent", strUseChkDis); }
            flagUseCheckDisablingContent = DataUtil.parseBoolean(strUseChkDis);
            
            // 설정들 중 클래스 관련 설정 적용, Pack 불러오기
            ColonyClassLoader.clearAll();
            ColonyClassLoader.applyLocalConfigs(configs, this);
            
            // 스크립트 엔진 매니저 준비
            initScriptEngineManager();
            
            // MODS 불러오기
            loadMods(false);
        } catch(Exception ex) {
            GlobalLogs.processExceptionOccured(ex, false);
        }
    }
    
    /** 스크립트 엔진에서 액세스하는 스토리지 준비 */
    protected void loadLocalStorage() {
    	GlobalLogs.log("Loading local storages...");
        try {
            // 폴더 체크 (없으면 만들기)
            File root = getColonyScriptRootDirectory();
            if(! root.exists()) root.mkdirs();
            
            // 저장소 파일
            File fileStorage = new File(root.getAbsolutePath() + File.separator + "storage.json");
            if(! fileStorage.exists()) {
                FileUtil.writeString(fileStorage, "UTF-8", "{}");
            }
            
            // 읽고 파싱
            String reads = FileUtil.readString(fileStorage, "UTF-8");
            reads = DataUtil.remove65279(reads);
            
            JsonObject json = (JsonObject) JsonObject.parseJson(reads);
            
            storage.clear();
            storage.putAll(json);
        } catch(Exception ex) {
            GlobalLogs.processExceptionOccured(ex, false);
        }
    }
    
    /** 스크립트 엔진에서 액세스하는 스토리지 저장 */
    protected void saveLocalStorage() {
    	GlobalLogs.log(t("스토리지 저장 중..."));
        try {
            // 폴더 체크 (없으면 만들기)
            File root = getColonyScriptRootDirectory();
            if(! root.exists()) root.mkdirs();
            
            // 저장소 파일
            File fileStorage = new File(root.getAbsolutePath() + File.separator + "storage.json");
            
            // 저장
            FileUtil.writeString(fileStorage, "UTF-8", storage.toJSON());
        } catch(Exception ex) {
            GlobalLogs.processExceptionOccured(ex, false);
        }
    }
    
    /** 스크립트 엔진 매니저 준비 */
    protected void initScriptEngineManager() {
        scriptEngineManager = new ScriptEngineManager(new ScriptClassLoader());
        initDefaultScriptEngineManager();
    }
    
    /** 기본함수 선언 스크립트 실행 */
    protected void evalInitScripts(ScriptEngine engine) throws Exception {
    	if(engine == null) return;
        evalDefaultInitScripts(engine);
    }
    
    /** 스크립트 엔진 매니저에 기본적인 객체 삽입 */
    protected final void initDefaultScriptEngineManager() {
        scriptEngineManager.put(PrimitiveObject.getInstance().getPrefixName() + "_" + scriptVarPrefix, PrimitiveObject.getInstance());
        scriptEngineManager.put(MathObject.getInstance().getPrefixName() + "_" + scriptVarPrefix, MathObject.getInstance());
        scriptEngineManager.put(SecurityObject.getInstance().getPrefixName() + "_" + scriptVarPrefix, SecurityObject.getInstance());
        scriptEngineManager.put(NetObject.getInstance().getPrefixName() + "_" + scriptVarPrefix, NetObject.getInstance());
        
        scriptEngineManager.put("storage_" + scriptVarPrefix, storage);
        scriptEngineManager.put("storage", storage);
    }
    
    /** 기본함수 선언 스크립트 실행 - 공통 파트 */
    protected final void evalDefaultInitScripts(ScriptEngine engine) throws Exception {
        engine.put("BUILD_NO", String.valueOf(BUILD_NO));
        
        engine.eval(PrimitiveObject.getInstance().getInitScript(scriptVarPrefix));
        engine.eval(MathObject.getInstance().getInitScript(scriptVarPrefix));
        engine.eval(SecurityObject.getInstance().getInitScript(scriptVarPrefix));
        engine.eval(NetObject.getInstance().getInitScript(scriptVarPrefix));
    }
    
    /** 정착지들을 기본 경로에서 불러오기 */
    @Override
    public void loadColonies() {
    	GlobalLogs.log(t("정착지 모두 불러오는 중..."));
        File root = getColonySaveRootDirectory();
        File[] lists = root.listFiles(getColonyFileFilter());
        
        colonies.clear();
        for(File f : lists) {
            if(f.getName().equals("config.json")) continue;
            if(getColonyFileFilter().accept(f)) loadColony(f, false);
        }
        
        GlobalLogs.log(t("정착지 모두 불러오는 완료"));
        
        if(colonies.isEmpty()) {
            newColony();
        } else {
            refreshColonyList();
        }
    }
    
    /** 정착지 모두 포기, 초기화 */
    protected void resetAllColony() {
    	GlobalLogs.log(t("정착지 모두 포기하고 철수 중..."));
        colonies.clear();
        File root = getColonySaveRootDirectory();
        File[] lists = root.listFiles(getColonyFileFilter());
        for(File f : lists) {
            f.delete();
        }
        newColony();
    }
    
    /** 정착지를 별도 파일에서 불러오기 (화면에는 반영하지 않으므로, 사용 후 refreshColonyList 호출 필요) */
    public void loadColony(File f, boolean alert) {
    	GlobalLogs.log(t("파일로부터 정착지 불러오는 중..."));
        boolean exists = false;
        try { 
            Colony c = ColonyClassLoader.loadColony(f);
            exists = false;
            
            // 이미 불러왔는지 확인
            for(Colony cx : colonies) { if(c.getName().equals(cx.getName())) exists = true; break; }
            if(exists) return;
            
            // 기본 파일명 세팅
            if(c instanceof AbstractColony) ((AbstractColony) c).setOriginalFileName(f.getName());
            
            // 버전 체크
            long buildNo = c.getClientBuildNo();
            if(BUILD_NO != buildNo) {
                if(getConfig().get("LoadOldVersion") == null) getConfig().set("LoadOldVersion", "N");
                if(! getConfig().getBool("LoadOldVersion")) return;
            }
            
            // 일단 불러온 정착지는 버전 정보 리셋
            c.resetClientVersion(this);
            
            // 추가
            colonies.add(c); 
        } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); if(alert) alert("오류 : " + ex.getMessage()); }
    }
    
    /** 정착지들을 기본 경로에 저장 */
    @Override
    public void saveColonies() {
    	GlobalLogs.log(t("모든 정착지 기록 중..."));
    	
        File root = getColonySaveRootDirectory();
        
        // 백업 준비
        SimpleDateFormat format8 = new SimpleDateFormat("yyyyMMdd");
        int no = 1;
        String date8 = format8.format(new Date(System.currentTimeMillis()));
        String dirName = "backup" + date8 + "_" + no;
        
        // 백업 디렉토리 생성
        File dir = new File(root.getAbsolutePath() + File.separator + dirName);
        while(dir.exists()) {
            no++;
            dirName = "backup" + date8 + "_" + no;
            dir = new File(root.getAbsolutePath() + File.separator + dirName);
        }
        dir.mkdirs();
        
        // 백업
        File[] lists = root.listFiles(getColonyFileFilter());
        for(File f : lists) {
            File newPath = new File(dir.getAbsolutePath() + File.separator + f.getName());
            f.renameTo(newPath); // 파일 이동
        }
        
        // 저장
        for(Colony c : colonies) {
            String name = null;
            if(c instanceof AbstractColony) name = ((AbstractColony) c).getOriginalFileName();
            if(name == null) name = "col_" + c.getKey() + ".colony";
            
            File colFile = new File(root.getAbsolutePath() + File.separator + name);
            saveColony(c, colFile, false);
        }
        
        // 임시 백업 삭제
        if(dir.exists()) {
            lists = dir.listFiles();
            for(File f : lists) {
                if(f.isDirectory()) continue;
                f.delete();
            }
            if(dir.listFiles().length <= 0) dir.delete();
        }
        
        GlobalLogs.log(t("모든 정착지 기록 완료"));
    }
    
    /** 해당 정착지를 별도 파일로 저장 */
    public void saveColony(Colony c, File f, boolean alert) {
    	GlobalLogs.log(t("정착지 [COLONY] 기록 중...").replace("[COLONY]", c == null ? "NULL" : c.getName()));
    	
        try { 
            String nameLower = f.getName().toLowerCase().trim();
            if(! ( nameLower.endsWith(".colony") || nameLower.endsWith(".colgz") )) f = new File(f.getAbsolutePath() + ".colony");
            
            if(c instanceof AbstractColony) ((AbstractColony) c).save(f);
            
            GlobalLogs.log(t("정착지 기록 완료"));
        } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, true); if(alert) { alert("오류 : " + ex.getMessage()); } else { throw new RuntimeException(ex.getMessage()); } }
    }
    
    /** Colonization 기본 설정 저장 */
    @Override
    public void saveLocalConfigs() {
    	GlobalLogs.log(t("설정 저장 중..."));
    	
        File root = getColonyConfigRootDirectory();
        if(! root.exists()) root.mkdirs();
        
        try {
            File conf = new File(root.getAbsolutePath() + File.separator + "config.jsonn"); // n 일부러 더 붙인 것
            FileUtil.writeString(conf, "UTF-8", configs.toJson().toJSON()); // 저장
            
            File olds = new File(root.getAbsolutePath() + File.separator + "config.json"); // 기존 파일
            if(olds.exists()) olds.delete();
            
            conf.renameTo(olds); // 목표로 하는 이름으로 바꾸기
            GlobalLogs.log(t("설정 저장 완료"));
        } catch(Exception ex) {
            GlobalLogs.processExceptionOccured(ex, false);
        }
    }
    
    /** 새 정착지 생성 (기본형으로 생성) */
    @Override
    public Colony newColony() {
    	GlobalLogs.log(t("새 정착지 개척 중..."));
        try {
            Colony newCol = (Colony) Class.forName("org.duckdns.hjow.colonization.elements.NormalColony").newInstance();
            newColonyAfterJobs(newCol);
            
            return newCol;
        } catch(Exception ex) {
            throw new RuntimeException("java-default-pack not detected.");
        }
    }
    
    /** 새 정착지 생성 (타입 지정) */
    public Colony newColony(String type) {
        return newColony(type, null);
    }
    
    /** 새 정착지 생성 (타입 지정) */
    public Colony newColony(String type, String name) {
        Colony newCol = ColonyClassLoader.newColonyInstance(type);
        if(DataUtil.isNotEmpty(name)) newCol.setName(name);
        newColonyAfterJobs(newCol);
        
        return newCol;
    }
    
    /** 새 정착지 생성 후반부 동작 */
    protected void newColonyAfterJobs(Colony col) {
        col.newCity();
        col.resetAvailLoans();
        col.randomizeCelestials();
        colonies.add(col);
        refreshColonyList();
    }
    
    /** 정착지 추가 */
    public void addColony(Colony col) {
        for(Colony c : colonies) {
            if(c.getKey() == col.getKey()) return;
        }
        colonies.add(col);
        refreshColonyList();
    }
    
    /** Mods 불러오기 */
    protected void loadMods() {
        loadMods(true);
    }
    
    /** Mods 불러오기 */
    protected void loadMods(boolean refresh) {
        // 기존 Mod 들 제거
        for(Mod m : modsList) { try {  m.dispose(); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); } }
        modsList.clear();
        modsEnabled.clear();
        
        // lib 에 등록된 Mods 도 불러오기
        for(Class<?> classes : ColonyClassLoader.getModClasses()) {
            try {
                Mod m = (Mod) classes.newInstance();
                if((! m.isReadOnly()) && (! flagUseCheckDisablingContent)) continue;
                
                if(! modsList.contains(m)) modsList.add(m);
            } catch(Exception ex) {
                GlobalLogs.processExceptionOccured(ex, false);
            }
        }
        
        // 예약어 등록된 Library 도 불러오기
        for(String reservedLibClassNames : ColonyClassLoader.getReservedLibraryClassNames()) {
            try {
                Class<?> libClass = Class.forName(reservedLibClassNames);
                Library instances = (Library) libClass.newInstance();
                List<Mod> mods = instances.getMods();
                for(Mod m : mods) {
                    if((! m.isReadOnly()) && (! flagUseCheckDisablingContent)) continue;
                    if(! modsList.contains(m)) modsList.add(m);
                }
            } catch(ClassNotFoundException ignores) {
            } catch(Exception ex) {
                GlobalLogs.processExceptionOccured(ex, false);
            }
        }
        
        // 스크립트 MOD 불러오기
        loadScriptMods();
        
        if(refresh) applyModOnUI();
    }
    
    /** 스크립트 MOD 불러오기 */
    protected void loadScriptMods() {
        // 폴더 체크 (없으면 만들기)
        File root = getColonyScriptRootDirectory();
        if(! root.exists()) root.mkdirs();
        File dirScriptMods = new File(root.getAbsolutePath() + File.separator + "mods"); // [ROOT] / scripts / mods
        if(! dirScriptMods.exists()) dirScriptMods.mkdirs();
        
        // 폴더 내 js 파일 서치
        File[] files = dirScriptMods.listFiles(new FileFilter() {    
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().trim().endsWith(".js");
            }
        });
        
        // 파일 루프
        for(File f : files) {
            if(! f.exists()) continue;
            if(f.isDirectory()) continue;
            
            GlobalLogs.log(t("Trying to load script MOD from [FILE]").replace("[FILE]", f.getName()));
            
            try {
                // 엔진 생성
                ScriptEngine engine = newScriptEngine();
                if(engine == null) continue;
                
                // 스크립트 불러오기
                String scripts = FileUtil.readString(f, "UTF-8");
                scripts = DataUtil.remove65279(scripts);
                
                // 리플렉션 존재여부 체크
                checkBannedKeywords(scripts);
                
                // 스크립트 실행 (함수들이 선언될 것)
                engine.eval(scripts);
                
                // 스크립트 MOD 객체 생성 및 준비
                ScriptMod mod = new ScriptMod();
                mod.injectScriptEngine(engine);
                mod.check();
                
                if((! mod.isReadOnly()) && (! flagUseCheckDisablingContent)) continue;
                
                // MOD 등록
                if(! modsList.contains(mod)) modsList.add(mod);
                
                GlobalLogs.log(t("Load complete : [FILE]").replace("[FILE]", f.getName()));
            } catch(Exception ex) {
                GlobalLogs.processExceptionOccured(ex, false);
            }
        }
    }
    
    /** 스크립트 엔진 생성해 반환 */
    protected ScriptEngine newScriptEngine() throws Exception {
    	if(scriptEngineManager == null) {
    		logGlobals(t("[LANG] 스크립트 엔진 사용 불가").replace("[LANG]", scriptLanguage), 2); return null;
    	}
    	
        // 엔진 생성
        ScriptEngine engine = ScriptUtil.newEngine(scriptEngineManager, scriptLanguage);
        if(engine == null) { logGlobals(t("[LANG] 스크립트 엔진 사용 불가").replace("[LANG]", scriptLanguage), 2); return null; }
        
        // 스크립트 실행 (기본함수들 제공)
        evalInitScripts(engine);
        
        return engine;
    }
    
    /** 활성화된 모드들을 UI에 반영 */
    protected void applyModOnUI() {};
    
    /** Mods 들 목록 반환 */
    public List<Mod> getMods() {
        List<Mod> list = new ArrayList<Mod>();
        list.addAll(modsList);
        return list;
    }
    
    /** Mod 추가 */
    public void addMod(String modClassName) {
        addMod(modClassName, true, true);
    }
    
    /** Mod 추가 (이 메소드는 ColonyClassLoader 간의 통신을 위해서만 존재하므로 되도록 직접 호출 자제 !) */
    public void addMod(String modClassName, boolean refresh, boolean saveConfig) {
        try {
            ColonyClassLoader.checkModClassName(modClassName);
            modClassName = modClassName.trim();
            
            Class<?> modClass = Class.forName(modClassName);
            Mod mod = (Mod) modClass.newInstance();
            if((! mod.isReadOnly()) && (! flagUseCheckDisablingContent)) return;
            
            if(! modsList.contains(mod)) modsList.add(mod);
            
            if(saveConfig) {
                String strMods = configs.getString("Mods");
                if(! strMods.contains(modClassName)) {
                    if(DataUtil.isNotEmpty(strMods)) strMods += ",";
                    strMods += modClassName;
                }
                configs.set("Mods", strMods);
                saveLocalConfigs();
            }
        } catch(Throwable tx) {
            logGlobals(t("Error") + " : " + tx.getMessage(), 2);
        }
        
        if(refresh) applyModOnUI();
    }

    /** 로그 출력 */
    public void log(String msg) {
        System.out.println(msg);
    }
    
    /** 메인 쓰레드 종료까지 대기 */
    protected void waitThreadShutdown() {
        threadSwitch = false;
        int prevInfinites = 0;
        while(true) {
            if(threadShutdown) break;
            try { Thread.sleep(100L); } catch(Exception ex) {  }
            
            prevInfinites++;
            if(prevInfinites >= 100000) break;
        }
    }

    @Override
    public void dispose() {
        dispose(true);
        flagAlreadyDisposed = true;
    }
    
    /** 이 객체 사용 중단 - 관련 리소스 모두 해제 */
    public void dispose(boolean closeDialog) {
        disposeContents();
        colonies.clear();
    }
    
    /** 이 객체 사용 중단 - 내부 컨텐츠들만 리소스 해제 */
    public void disposeContents() {
        threadSwitch = false;
        waitThreadShutdown();
        if(! flagAlreadyDisposed) { saveLocalConfigs(); saveLocalStorage(); }
        if((! flagAlreadyDisposed) && (! colonies.isEmpty())) saveColonies();
        ColonyClassLoader.clearAll();
        broker = null;
        if(rootEngine != null) rootEngine = null;
    }
    
    /** 메시지 출력 */
    public void alert(String msg) {
        System.out.println(msg);
    }
    
    /** 불러온 모든 정착지 정보 반환 */
    public JsonArray getAllColonies() {
        JsonArray arr = new JsonArray();
        for(Colony c : colonies) {
            arr.add(c.toJson());
        }
        return arr;
    }
    
    /** 현재 선택된 정착지 정보 반환 */
    public JsonObject getSelectColonyInfo() {
        Colony c = getSelectedColony();
        if(c == null) return null;
        return c.toJson();
    }
    
    /** 현재 선택된 정착지 반환 */
    public Colony getSelectedColony() {
        if(selectedColony < 0) return null;
        if(selectedColony >= colonies.size()) { selectedColony = 0; return null; }
        return colonies.get(selectedColony);
    }
    
    /** 현재 선택된 정착지 반환 */
    @Override
    public Colony getColony() {
        return getSelectedColony();
    }
    
    /** 해당 키를 갖는 정착지 찾아 반환 (목록에 없으면 null 반환) */
    @Override
    public Colony getColony(long colonyKey) {
        for(Colony c : colonies) {
            if(c.getKey() == colonyKey) return c;
        }
        return null;
    }
    
    /** 시뮬레이션 시작/정지 토글 */
    public void toggleSimulationRunning() {
        threadPaused = (! threadPaused);
        if(threadPaused) {
            pauseSimulation();
        } else {
            resumeSimulation();
        }
    }
    
    /** 시뮬레이션 일시 정지 */
    public void pauseSimulation() {
        threadPaused = true;
    }
    
    /** 시뮬레이션 재개 */
    public void resumeSimulation() {
        resumeSimulation(-1);
    }
    
    /** 시뮬레이션 재개 (사이클 수 지정, 음수를 넣으면 일시 정지 따로 할 때까지 무제한) */
    public void resumeSimulation(int cycleCount) {
        threadPaused = false;
        reserveSaving = true;
        cycleRunCount = cycleCount;
        
        // 쓰레드가 완전히 종료될 때까지 대기
        try {
            int prefInfLoop = 10;
            while(! bCheckerPauseCompleted) {
                Thread.sleep(1000L);
                prefInfLoop--;
                if(prefInfLoop <= 0) break;
            }
        } catch(InterruptedException ex) { GlobalLogs.processExceptionOccured(ex, false); }
    }
    
    /** 해당 정착지를 출력하는 영역 반환 */
    public ColonyPanel getColonyPanel(Colony col) {
        return null;
    }
    
    /** 쓰레드에서 1 사이클 당 1회 호출됨 */
    public void oneCycle() {
        Colony col = getSelectedColony();
        if(col == null) return;
        
        if(time == null) {
            time = col.getTime();
            while(time.compareTo(CYCLE_NO_MAXIMUM_BIG) >= 0) {
                time = time.subtract(CYCLE_NO_MAXIMUM_BIG);
            }
            cycle = time.intValue();
        }
        
        // logGlobals("Running main cycle " + cycle, 1);
        
        if(cycle % col.cycleGap(col) == 0) {
            try { col.oneCycle(cycle, null, col, 100, getColonyPanel(col)); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        }
        
        // logGlobals("Running refreshing UI cycle " + cycle, 1);
        
        try {
            refreshArenaPanel(cycle);
        } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        
        cycle++;
        if(cycle >= CYCLE_NO_MAXIMUM) cycle = 0;
    }
    
    /** 화면 새로고침 예약 */
    @Override
    public void reserveRefresh() {
        if(threadPaused) { // 일시정지되어 있으면, 그냥 바로 새로고침 해버림
        	refreshColonyContent();
        } else {
        	reserveRefresh = true;
        }
    }
    
    /** 정착지 목록과 화면 내용 갱신 */
    @Override
    public void refreshColonyList() {
    	GlobalLogs.log(t("정착지 목록 갱신 중..."));
    	
        if(colonies.size() >= 1 && selectedColony < 0) selectedColony = 0;
        refreshColonyContent();
    }
    
    /** 정착지 화면 내용 갱신 */
    @Override
    public void refreshColonyContent() {
    	GlobalLogs.log(t("정착지 내부 화면 갱신 중..."));
    	
        assureMainThreadRunning();
        refreshArenaPanel(0);
    }
    
    /** 사이클 진행에 따른 정착지 화면 내용 갱신 (성능을 위해 항상 전체를 새로고침하지는 않음. 확실히 새로고침하려면 refreshColonyContent 메소드 사용) */
    public void refreshArenaPanel(int cycle) {
        Colony col = getSelectedColony();
        
        if(col != null) {
            BigInteger time = col.getTime();
            while(time.compareTo(ColonyManager.CYCLE_NO_MAXIMUM_BIG) >= 0) {
                time = time.subtract(ColonyManager.CYCLE_NO_MAXIMUM_BIG);
            }
            cycle = time.intValue();
        }
        
        // Do Nothing
    }
    
    /** 도시가 속한 정착지 찾기 */
    @Override
    public Colony getColonyFrom(City city) {
        for(Colony c : colonies) {
            for(City ct : c.getCities()) {
                if(ct.getKey() == city.getKey()) return c;
            }
        }
        return null;
    }
    
    /** 커맨드 적용 */
    public void runCommand(String commands) {
        if(commands == null) return;
        logGlobals(">> " + commands);
        
        StringTokenizer spaceTokenizer = new StringTokenizer(commands, " ");
        String code, param;
        
        code = spaceTokenizer.nextToken().trim();
        
        if(spaceTokenizer.hasMoreTokens()) param = spaceTokenizer.nextToken();
        else param = "";
        
        if(! applyCheat(code, param)) {
            try {
                Object res = null;
                if(rootEngine == null) rootEngine = newScriptEngine();
                if(rootEngine == null) { logGlobals(t("[LANG] 스크립트 엔진 사용 불가").replace("[LANG]", scriptLanguage)); return; }
                
                rootEngine.put("colony", getSelectColonyInfo());
                rootEngine.put("uix", broker);
                
                res = evaluate(rootEngine, commands);
                if(res != null) logGlobals(res.toString());
            } catch(Exception ex) {
                GlobalLogs.processExceptionOccured(ex, true);
            }
        }
    }
    
    /** 치트 적용 */
    private boolean applyCheat(String cheatCode, String params) {
        Cheat c = Cheat.map().get(cheatCode);
        if(c == null) return false;
        
        Colony col = getSelectedColony();
        if(col == null) return false;
        
        // 인증 제거
        if(col.isCheckEnabled()) logGlobals(t("Cheat 사용으로 정착지 [COLONY] 의 인증이 무효화됩니다.").replace("[COLONY]", col.getName()));
        col.disableChecked();
        
        // 실행
        c.onCodeInput(this, params);
        
        reserveRefresh();
        logGlobals(t("Cheat [CODE] 적용.").replace("[CODE]", c.getCode()));
        return true;
    }
    
    /** 설정 객체 자체를 반환 */
    public ColonyManagerConfig getConfig() {
        return configs;
    }
    
    /** 설정 객체 반환 */
    public ColonyManagerConfig c() {
        return getConfig();
    }
    
    @Override
    public long    getCycleGapEachCity()          { return cycleGapEachCity;     }
    @Override
    public long    getCycleGapEachFacility()      { return cycleGapEachFacility; }
    @Override
    public boolean isUsingCheckDisablingContent() { return flagUseCheckDisablingContent; }
    @Override
    public Object getDialogObject() { return null; }
    
    /** 프로그램 종료 */
    public void exit() {
        dispose(false);
        
        GlobalLogs.log("Exit");
        if(superInstance != null) superInstance.exit();
        else System.exit(0);
    }
    
    /** 홈 디렉토리 반환 */
    public static File getHomeDir(String programNameMain, String programNameSub) {
        if(programNameMain == null) throw new NullPointerException("programNameMain cannot be null !");
        if(programNameSub  == null) throw new NullPointerException("programNameSub cannot be null !");
        programNameMain = programNameMain.replace(".", "").trim();
        programNameSub  = programNameSub.replace(".", "").trim();
        if(programNameMain.equals("")) throw new NullPointerException("programNameMain cannot be a empty string !");
        if(programNameSub.equals(""))  throw new NullPointerException("programNameSub cannot be a empty string !");
        File f = new File(System.getProperty("user.home") + File.separator + "." + programNameMain + File.separator + programNameSub);
        if(! f.exists()) f.mkdirs();
        return f;
    }
    
    /** 각 요소들을 위한 고유키 생성 (절대 0이 나오지 않음) */
    public static long generateKey() {
        Random rd = new Random();
        long key = rd.nextLong();
        while(key == 0L) { key = rd.nextLong(); }
        return key;
    }
    
    /** 각 요소들의 고유 이름을 위한 자연수 반환 */
    public static int generateNaturalNumber() {
        return Math.abs(new Random().nextInt());
    }
    
    /** 키를 받아 자연수 추출 (이름에 사용) */
    public static int getNaturalNumberFrom(long key) {
        int res;
        
        key = Math.abs(key);
        String str = String.valueOf(key);
        if(str.length() > 9) {
            str = str.substring(0, 9);
        }
        res = Integer.parseInt(str);
        str = String.valueOf(res);
        
        if(str.length() < 9) res += 100000000;
        return res;
    }

    public static boolean isDebugModeEnabled() {
        return flagDebugMode;
    }
    
    /** 전역 로그 출력 */
    public static void logGlobals(String msg) {
        logGlobals(msg, 2);
    }
    
    /** 전역 로그 출력 */
    public static void logGlobals(String msg, int detailLevel) {
        if(dialogGlobalLog != null) dialogGlobalLog.log(msg, detailLevel);
        else {
        	if(detailLevel <= 1) System.err.println(msg);
        	else GlobalLogs.log(msg);
        }
    }
    
    /** 공격자의 대미지에 추가 연산 (랜덤성 부여, 속성 및 방어력, 상태 적용) */
    public static int naturalizeDamage(AttackableObject attacker, ColonyElements target, int damage) {
        if(damage < 0) return damage; // 대미지가 음수인 경우 그대로 반환
        double correctRate = 1.0; // 명중률
        int    defType = target.getDefenceType();
        int    atkType = attacker.getAttackType();
        
        // 속성 적용
        if(defType == DEFENCETYPE_SMALL) {
            if(atkType == ATTACKTYPE_THICK_BULLET ) { damage = (int) Math.round( damage / 2.0 ); correctRate = correctRate * 0.75; }
            if(atkType == ATTACKTYPE_THICK_MISSILE) { damage = (int) Math.round( damage / 2.0 ); }
            if(atkType == ATTACKTYPE_THICK_RAY    ) { correctRate = correctRate * 0.75; }
            if(atkType == ATTACKTYPE_THICK_ENERGY ) { correctRate = correctRate * 0.25; }
        }
        
        if(defType == DEFENCETYPE_BUILDING) {
            if(atkType >= 1 && atkType <= 9) damage = (int) Math.round( damage / 2.0 );
        }
        
        // 랜덤성 적용
        //    랜덤값 생성
        double naturalRandom = Math.round(damage * 0.2);
        naturalRandom = ((naturalRandom / 2.0) * random()) * 2.0;
        naturalRandom = naturalRandom - (naturalRandom / 2.0); // 반수는 음수로 가도록
        
        //    속성에 따라 랜덤값 추가 변동
        if(atkType == ATTACKTYPE_THICK_MISSILE) naturalRandom = naturalRandom * 2;
        if(atkType == ATTACKTYPE_THICK_ENERGY && defType == DEFENCETYPE_SMALL) naturalRandom = naturalRandom * 4;
        if(atkType == ATTACKTYPE_THIN_ENERGY  && defType == DEFENCETYPE_SMALL) naturalRandom = naturalRandom * 2;
        
        //     랜덤값 대미지에 적용
        damage = damage + (int) Math.round(naturalRandom);
        
        // 방어력 적용
        damage = damage - target.getDefencePoint();
        if(damage < 1) damage = 1;
        
        // 명중률 적용
        if(random() > correctRate) return 0; // 명중률이므로, 명중률에 벗어나야 0 리턴, 부등호 방향 주의 !
        return damage;
    }
    
    /** 다국어 번역, 번역 데이터에 없는 텍스트이면 매개변수 그대로 반환 */
    public static String t(String originals) {
        return STRINGTABLE.t(originals);
    }
    
    /** 현재 버전의 버전 배열 반환 */
    public static int[] getVersionArray() {
        int[] arr = new int[3];
        arr[0] = VERSION_MAIN;
        arr[1] = VERSION_SUB1;
        arr[2] = VERSION_SUB2;
        return arr;
    }
    
    /** 버전 문자열을 버전 배열로 반환 */
    public static int[] parseVersionString(String versionString) {
        String str = versionString.toLowerCase().replace("version", "").replace("ver", "").replace("v", "").trim();
        
        StringTokenizer dotTokenizer = new StringTokenizer(str, ".");
        int[] arr = new int[dotTokenizer.countTokens()];
        for(int idx=0; idx<arr.length; idx++) {
            arr[idx] = Integer.parseInt(dotTokenizer.nextToken().trim());
        }
        return arr;
    }
    
    /** 현재 버전 문자열로 반환 */
    public static String getVersionString() {
        return getVersionString(getVersionArray());
    }
    
    /** 버전 배열을 문자열로 변환 */
    public static String getVersionString(int[] num) {
        return num[0] + "." + num[1] + "." + num[2];
    }
    
    /** 스크립트 실행 */
    public static Object evaluate(ScriptEngine engine, String scripts) throws ScriptException {
    	if(scripts == null) return null;
    	return evaluate(engine, scripts, true);
    }
    
    /** 스크립트 실행 */
    public static Object evaluate(ScriptEngine engine, String scripts, boolean needConvert) throws ScriptException {
        if(scripts == null) return null;
        checkBannedKeywords(scripts);
        if(needConvert) scripts = ScriptUtil.convert(scripts, engine.getFactory().getLanguageName());
        return engine.eval(scripts);
    }
    
    public static final ScriptPatternDetector SCRIPT_PATTERN_DETECTOR = new ScriptPatternDetector();
    public static final List<String> BANNED_KEYWORDS = new Vector<String>();
    /** 스크립트 내 금지어 탐지 */
    public static void checkBannedKeywords(String scripts) {
        SCRIPT_PATTERN_DETECTOR.checkReflection(scripts);
        
        if(BANNED_KEYWORDS.isEmpty()) {
            BANNED_KEYWORDS.add("java.");
            BANNED_KEYWORDS.add("javax.");
            BANNED_KEYWORDS.add("org.");
            BANNED_KEYWORDS.add("com.");
        }
        
        SCRIPT_PATTERN_DETECTOR.check(scripts, BANNED_KEYWORDS);
    }
    
    /** 정수 포맷 설정 */
    public static String formatInt(long num) {
        return formatInt(num, true, 2);
    }
    
    /** 정수 포맷 설정 */
    public static String formatInt(long num, boolean simple, int disp) {
        long unit = 10000000000L;
        if(simple || num < unit) return FORMATTER_INT.format(num);
        
        List<String> list = new ArrayList<String>();
        
        long upper = 0L;
        long left  = num;
        
        unit = 1000000L;
        upper = (long) (left / unit);
        left  = (long) (left % unit);
        if(left > 0) list.add(FORMATTER_INT.format(left));
        
        unit = 1000L;
        left = upper;
        upper = (long) (left / unit);
        left  = (long) (left % unit);
        if(left > 0) list.add(FORMATTER_INT.format(left) + "M");
        
        if(upper >= 1) {
            left = upper;
            upper = (long) (left / unit);
            left  = (long) (left % unit);
            if(left > 0) list.add(FORMATTER_INT.format(left) + "G");
        }
        
        if(upper >= 1) {
            left = upper;
            upper = (long) (left / unit);
            left  = (long) (left % unit);
            if(left > 0) list.add(FORMATTER_INT.format(left) + "T");
        }
        
        if(upper >= 1) {
            left = upper;
            upper = (long) (left / unit);
            left  = (long) (left % unit);
            if(left > 0) list.add(FORMATTER_INT.format(left) + "F");
        }
        
        if(upper >= 1) {
            left = upper;
            upper = (long) (left / unit);
            left  = (long) (left % unit);
            if(left > 0) list.add(FORMATTER_INT.format(left) + "E");
        }
        
        if(upper >= 1) {
            left = upper;
            upper = 0L;
            if(left > 0) list.add(FORMATTER_INT.format(left) + "Z");
        }
        
        StringBuilder res = new StringBuilder("");
        for(int idx=list.size()-1; idx>=0; idx--) {
            res = res.append(" ").append(list.get(idx));
            disp--;
            if(disp == 0) break;
        }
        return res.toString().trim();
    }
    
    /** 정수 포맷 설정 */
    public static String formatInt(BigInteger num) {
        return formatInt(num, true, 2);
    }
    
    /** 정수 포맷 설정 */
    public static String formatInt(BigInteger num, boolean simple, int disp) {
        BigInteger unit = Constants.BIGINTEGER_10000000000;
        if(simple || num.compareTo(unit) < 0) return FORMATTER_INT.format(num);
        
        List<String> list = new ArrayList<String>();
        
        BigInteger upper = BigInteger.ZERO;
        BigInteger left  = num;
        
        unit = Constants.BIGINTEGER_1000000;
        upper = left.divide(unit);
        left  = left.mod(unit);
        if(left.compareTo(BigInteger.ZERO) > 0) list.add(FORMATTER_INT.format(left));
        
        unit = Constants.BIGINTEGER_1000;
        left = upper;
        upper = left.divide(unit);
        left  = left.mod(unit);
        if(left.compareTo(BigInteger.ZERO) > 0) list.add(FORMATTER_INT.format(left) + "M");
        
        if(upper.compareTo(BigInteger.ONE) > 0) {
            left = upper;
            upper = left.divide(unit);
            left  = left.mod(unit);
            if(left.compareTo(BigInteger.ZERO) > 0) list.add(FORMATTER_INT.format(left) + "G");
        }
        
        if(upper.compareTo(BigInteger.ONE) > 0) {
            left = upper;
            upper = left.divide(unit);
            left  = left.mod(unit);
            if(left.compareTo(BigInteger.ZERO) > 0) list.add(FORMATTER_INT.format(left) + "T");
        }
        
        if(upper.compareTo(BigInteger.ONE) > 0) {
            left = upper;
            upper = left.divide(unit);
            left  = left.mod(unit);
            if(left.compareTo(BigInteger.ZERO) > 0) list.add(FORMATTER_INT.format(left) + "F");
        }
        
        if(upper.compareTo(BigInteger.ONE) > 0) {
            left = upper;
            upper = left.divide(unit);
            left  = left.mod(unit);
            if(left.compareTo(BigInteger.ZERO) > 0) list.add(FORMATTER_INT.format(left) + "E");
        }
        
        if(upper.compareTo(BigInteger.ONE) > 0) {
            left = upper;
            upper = left.divide(unit);
            left  = left.mod(unit);
            if(left.compareTo(BigInteger.ZERO) > 0) list.add(FORMATTER_INT.format(left) + "Z");
        }
        
        if(upper.compareTo(BigInteger.ONE) > 0) {
            left = upper;
            upper = BigInteger.ZERO;
            if(left.compareTo(BigInteger.ZERO) > 0) list.add(FORMATTER_INT.format(left) + "Y");
        }
        
        StringBuilder res = new StringBuilder("");
        for(int idx=list.size()-1; idx>=0; idx--) {
            res = res.append(" ").append(list.get(idx));
            disp--;
            if(disp == 0) break;
        }
        return res.toString().trim();
    }
    
    /** 실수 포맷 설정 */
    public static String formatRate(double num) {
        return FORMATTER_RATE.format(num);
    }
    
    /** 실수 포맷 설정 */
    public static String formatRate(BigDecimal num) {
        return FORMATTER_RATE.format(num);
    }
    
    /** 좌표 포맷 설정 */
    public static String formatCoordinate(HasLocation loc) {
    	return "(" + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ")";
    }
    
    /** 랜덤 수 (0.0 ~ 1.0) 반환 */
    public static double random() {
        return Math.random();
    }
    
    /** 빌드 번호별 인증 상수 반환 */
    public static BigInteger getCheckerConst(long buildNo) {
    	if(buildNo == 6L) return Constants.BIGINTEGER_3;
    	return BigInteger.ONE;
    }
    
    /** 버전 코드 */
    public static final int  VERSION_MAIN = 0;
    public static final int  VERSION_SUB1 = 0;
    public static final int  VERSION_SUB2 = 1;
    public static final long BUILD_NO     = 9L;
    
    /** 각 객체들의 공격 타입과 방어 타입 코드 상수 */
    public static final short ATTACKTYPE_NORMAL = 0;
    public static final short ATTACKTYPE_THIN_BULLET = 1;
    public static final short ATTACKTYPE_THIN_RAY    = 2;
    public static final short ATTACKTYPE_THIN_ENERGY = 3;
    public static final short ATTACKTYPE_THICK_BULLET  = 11;
    public static final short ATTACKTYPE_THICK_RAY     = 12;
    public static final short ATTACKTYPE_THICK_ENERGY  = 13;
    public static final short ATTACKTYPE_THICK_MISSILE = 14;
    public static final short DEFENCETYPE_NORMAL   = 0;
    public static final short DEFENCETYPE_SMALL    = 1;
    public static final short DEFENCETYPE_BUILDING = 9;
    
    /** 숫자 형식 맞추기 위한 객체들 */
    public static final DecimalFormat FORMATTER_INT  = new DecimalFormat("#,###");
    public static final DecimalFormat FORMATTER_RATE = new DecimalFormat("#,##0.00");
    
    /** 기본 1사이클 쓰레드 간격, 밀리초 단위로, 매 사이클 연산이 끝날 때마다 이 밀리초만큼 쓰레드 Sleep */
    public static final long CYCLEGAP_DEFAULT = 249L;
    
    /** cycle 값 제한 (TIME 의 최대값 개념이 아님) - TIME 값이 이 값보다 커지면, 이 값으로 나눈 나머지로 cycle 시작값을 구함. */
    public static final int        CYCLE_NO_MAXIMUM     = 2000000000;
    public static final BigInteger CYCLE_NO_MAXIMUM_BIG = new BigInteger(String.valueOf(CYCLE_NO_MAXIMUM));
    
    /** 다국어 지원용 스트링 테이블 */
    protected static final ColonyStringTable STRINGTABLE = new ColonyStringTable();
    
    /** 전역 로그 수집 객체 */
    protected static transient GlobalLogUI dialogGlobalLog;
    
    /** 디버그 모드 사용여부 플래그 */
    protected static transient boolean flagDebugMode = false;
}