package org.duckdns.hjow.colonization.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonizationMainClass;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.commons.console.Choice;
import org.duckdns.hjow.commons.exception.KnownRuntimeException;
import org.duckdns.hjow.commons.util.ClassUtil;
import org.duckdns.hjow.commons.util.DataUtil;
import org.duckdns.hjow.commons.util.FileUtil;

/** 콘솔 모드에서 사용되는 매니저 */
public class ConsoleColonyManager extends ColonyManager {
    private static final long serialVersionUID = 7584671897900957493L;
    protected transient BufferedReader reader = null;
    
    public ConsoleColonyManager() { super(); }
    
    @Override
    public void open(ColonizationMainClass superInstance) {
    	try {
    		reader = new BufferedReader(new InputStreamReader(System.in));
    		loadLocalConfigs();
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    	
    	// 메인 메뉴 준비
    	
    	final StringBuilder whileSwitch = new StringBuilder("Y");
    	List<Choice> choices = new ArrayList<Choice>();
    	int idx;
    	
    	//    로컬 정착지 사이클 수행
    	
    	choices.add(new Choice() {
			private static final long serialVersionUID = -6755695907655992414L;
			@Override
			public String getText() { return t("로컬 정착지 모두 사이클 진행"); }
			@Override
			public void action() throws Throwable { onRunCycleRequested(); }
		});
    	
    	//    로컬 정착지 모두 포기
    	
    	choices.add(new Choice() {
			private static final long serialVersionUID = -6755695907655992414L;
			@Override
			public String getText() { return t("로컬 정착지 모두 포기"); }
			@Override
			public void action() throws Throwable { onResetLocalSavesRequested(); }
		});
    	
        //      설정 초기화
    	
      	choices.add(new Choice() {
  			private static final long serialVersionUID = -6755695907655992414L;
  			@Override
  			public String getText() { return t("설정 초기화"); }
  			@Override
  			public void action() throws Throwable { onResetConfigsRequested(); }
  		});
    	
        //      종료
    	
    	choices.add(new Choice() {
			private static final long serialVersionUID = -6755695907655992414L;
			@Override
			public String getText() { return t("종료"); }
			@Override
			public void action() throws Throwable { whileSwitch.setLength(0); whileSwitch.append("N"); }
		});
    	
    	// 메인 메뉴 실행
    	
    	while(DataUtil.parseBoolean(whileSwitch.toString().trim())) {
    		try {
    			// 빈 공간 출력
    		    for(idx=0; idx<10; idx++) { System.out.println(); }
    		    
    		    // 메인 제목 출력
    		    System.out.println("Colonization");
    		    
    		    // 메뉴 목록 출력
    		    for(idx=0; idx<choices.size(); idx++) {
                    Choice c = choices.get(idx);
                    System.out.println((idx+1) + ". " + t(c.getText()));
                }
    		    
    		    // 선택지 입력 받기
    		    System.out.print(t("Choice") + " >> ");
    		    
    		    String line = reader.readLine();
                if(line == null) line = "";
                
                if(! DataUtil.parseBoolean(whileSwitch.toString().trim())) break;
                
                int sel = Integer.parseInt(line.trim()) - 1;
                if(sel < 0 || sel >= choices.size()) throw new KnownRuntimeException("목록에 없는 선택지입니다.");
                
                Choice choosed = choices.get(sel);
                choosed.action();
    		} catch(Throwable t) {
    			onExceptionOccured(t);
    		}
    	}
    	
    	System.out.println(t("Bye"));
    	ClassUtil.closeAll(reader);
    	System.exit(0);
    }
    
    /** 오류 처리 */
    protected void onExceptionOccured(Throwable t) {
    	if(t instanceof RuntimeException) { System.out.println(t("오류") + " : " + t(t.getMessage())); }
    	else if(t instanceof NumberFormatException) { System.out.println(t("오류") + " : " + t("숫자 형식으로 입력해 주세요.")); }
    	else {
    		System.out.println(t("오류") + " : " + t(t.getMessage()));
    		t.printStackTrace();
    	}
    }
    
    /** 메인 메뉴 - 모든 정착지 사이클 수행 */
    protected void onRunCycleRequested() {
    	final StringBuilder whileSwitch = new StringBuilder("Y");
    	while(DataUtil.parseBoolean(whileSwitch.toString().trim())) {
    		try {
    			String strSel;
    			System.out.println();
    	        System.out.println(t("몇 초를 시뮬레이션 하겠습니까? (1 ~ 100)"));
    	        System.out.print(t("Input") + " >> ");
    	        
    	        String line = reader.readLine();
                if(line == null) line = "";
                
                if(DataUtil.isEmpty(line)) return;
                
                strSel = line.trim();
                int counts = Integer.parseInt(strSel);
                System.out.println();
                
                if(counts <= 0 || counts >= 101) throw new KnownRuntimeException(t("1 ~ 100 사이의 숫자를 입력해 주세요."));
                
                System.out.println(t("정착지 모두 불러오는 중..."));
                loadColonies();
                
                for(int idx=0; idx<colonies.size(); idx++) {
                	selectedColony = idx;
                	for(int jdx=0; jdx<counts; jdx++) {
                		Colony col = getSelectedColony();
                		System.out.println(t("정착지 [COLONYNAME] 시간 [TIME] 시뮬레이션...").replace("[COLONYNAME]", col.getName()).replace("[TIME]", col.getDateString()));
                	    oneCycle();
                	}
                }
                
                System.out.println(t("정착지 모두 저장하는 중..."));
                saveColonies();
                
                System.out.println(t("작업이 완료되었습니다."));
    		} catch(Throwable t) {
    			onExceptionOccured(t);
    		}
    	}
    }
    
    /** 메인 메뉴 - 로컬 정착지 모두 포기 */
    protected void onResetLocalSavesRequested() {
    	final StringBuilder whileSwitch = new StringBuilder("Y");
    	while(DataUtil.parseBoolean(whileSwitch.toString().trim())) {
    		try {
    	        System.out.println();
    	        System.out.println(t("모든 정착지를 포기하시겠습니까? (Y/N)"));
    	        System.out.print(t("Choice") + " >> ");
    	        
    	        String line = reader.readLine();
                if(line == null) line = "";
                
                if(! DataUtil.parseBoolean(whileSwitch.toString().trim())) break;
                if(DataUtil.parseBoolean(line.trim())) {
                	
                	File dir = getColonySaveRootDirectory();
                	if(! dir.exists()) { whileSwitch.setLength(0); whileSwitch.append("N"); break; }
                	
                	File[] lists = dir.listFiles(getColonyFileFilter());
                	for(File f : lists) {
                		FileUtil.delete(f);
                	}
                	System.out.println(t("작업이 완료되었습니다."));
                	whileSwitch.setLength(0); whileSwitch.append("N"); break;
                } else {
                	whileSwitch.setLength(0); whileSwitch.append("N"); break;
                }
    		} catch(Throwable t) {
    			onExceptionOccured(t);
    		}
    	}
    }
    
    /** 메인 메뉴 - 설정 초기화 */
    protected void onResetConfigsRequested() {
    	final StringBuilder whileSwitch = new StringBuilder("Y");
    	while(DataUtil.parseBoolean(whileSwitch.toString().trim())) {
    		try {
    	        System.out.println();
    	        System.out.println(t("설정을 모두 포기하시겠습니까? (Y/N)"));
    	        System.out.print(t("Choice") + " >> ");
    	        
    	        String line = reader.readLine();
                if(line == null) line = "";
                
                if(! DataUtil.parseBoolean(whileSwitch.toString().trim())) break;
                if(DataUtil.parseBoolean(line.trim())) {
                	
                	File dir = getColonyConfigRootDirectory();
                	if(! dir.exists()) { whileSwitch.setLength(0); whileSwitch.append("N"); break; }
                	
                	File[] lists = dir.listFiles(new FileFilter() {
						@Override
						public boolean accept(File pathname) {
							String name = pathname.getAbsolutePath().toLowerCase();
							return (name.endsWith(".xml") || name.endsWith(".json") || name.endsWith(".properties"));
						}
					});
                	for(File f : lists) {
                		FileUtil.delete(f);
                	}
                	
                	loadLocalConfigs();
                	System.out.println(t("작업이 완료되었습니다."));
                	whileSwitch.setLength(0); whileSwitch.append("N"); break;
                } else {
                	whileSwitch.setLength(0); whileSwitch.append("N"); break;
                }
    		} catch(Throwable t) {
    			onExceptionOccured(t);
    		}
    	}
    }
}
