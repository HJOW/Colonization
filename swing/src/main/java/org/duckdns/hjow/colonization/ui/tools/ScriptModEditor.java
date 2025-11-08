package org.duckdns.hjow.colonization.ui.tools;

import java.awt.Window;
import java.io.File;

import org.duckdns.hjow.colonization.ColonyManager;

/** 스크립트 기반 MOD 개발 툴 */
public class ScriptModEditor extends ScriptCreatorTool {
	public ScriptModEditor(Window win) {
		super(win);
	}

	@Override
	public String getName() {
		return "SCRIPTMOD";
	}

	@Override
	public String getTitle() {
		return "MOD 에디터";
	}

	@Override
	protected String target() {
		return "MOD";
	}

	@Override
	protected String getDefaultContent() {
		StringBuilder res = new StringBuilder("");
		
		res = res.append("/*                                                                                                                      \n");
		res = res.append("아래 함수들을 구현하여 MOD를 개발할 수 있습니다.                                                                        \n");
		res = res.append("도움말에 있는 스크립트 API를 사용할 수 있습니다.                                                                        \n");
		res = res.append("저장한 MOD는 프로그램 재시작 후 사용할 수 있습니다.                                                                     \n");
		res = res.append("*/                                                                                                                      \n");
		res = res.append("/** " + ColonyManager.t("이 함수는 UI 초기화 시 호출됩니다. 이 함수 안에서 UI를 구성해 주세요. 컴포넌트를 만들어 panel 안에 붙여 주세요.") + " */ \n");
		res = res.append("function init(panel) {                                                                                                  \n");
		res = res.append("	                                                                                                                      \n");
		res = res.append("}                                                                                                                       \n");
		res = res.append("                                                                                                                        \n");
		res = res.append("/** " + ColonyManager.t("이 MOD의 이름을 지어 이 함수에서 반환해 주세요.") + " */                                       \n");
		res = res.append("function getName() {                                                                                                    \n");
		res = res.append("    return \"\";                                                                                                        \n");
		res = res.append("}                                                                                                                       \n");
		res = res.append("                                                                                                                        \n");
		res = res.append("/** " + ColonyManager.t("이 MOD의 설명문을 반환해 주세요.") + " */                                                      \n");
		res = res.append("function getDescription() {                                                                                             \n");
		res = res.append("    return \"\";                                                                                                        \n");
		res = res.append("}                                                                                                                       \n");
		res = res.append("                                                                                                                        \n");
		res = res.append("/** " + ColonyManager.t("이 함수에서는 0을 리턴해 주세요. MOD가 배치될 위치 코드를 반환하는 함수이며, 지금은 대화상자형 MOD만 지원됩니다.") + " */ \n");
		res = res.append("function getLocation() {                                                                                                \n");
		res = res.append("    return 0;                                                                                                           \n");
		res = res.append("}                                                                                                                       \n");
		res = res.append("                                                                                                                        \n");
		res = res.append("/** " + ColonyManager.t("시뮬레이션이 진행될 때 자동으로 호출됩니다. panel 은 이 MOD의 영역 (init 함수의 것과 동일), cycle 은 정수로 시간값을 반환합니다. colony 에는 현재의 정착지가 JSON 형태로 변환되어 반환되고, manager 객체로는 게임 프로그램을 어느정도 통제하는 데 사용할 수 있습니다.") + " */  \n");
		res = res.append("function refresh(panel, cycle, colony, manager) {                                                                       \n");
		res = res.append("	                                                                                                                      \n");
		res = res.append("}                                                                                                                       \n");
		res = res.append("/** " + ColonyManager.t("이 함수에서는 true 를 리턴해 주세요. false 지정 시에는, 위 refresh 함수의 colony 매개변수에 JSON 대신 정착지 객체 자체가 들어와 값을 변경할 수 있게 됩니다. 이는 인증 해제 원인요소가 되며, 설정에서 이를 활성화하기 전에는 이 MOD를 사용할 수 없게 됩니다.") + " */\n");
		res = res.append("function isReadOnly() {                                                                                                 \n");
		res = res.append("    return true;                                                                                                        \n");
		res = res.append("}                                                                                                                       \n");
		res = res.append("/** " + ColonyManager.t("이 함수는 프로그램 종료 시 호출됩니다.") + " */                                                \n");
		res = res.append("function dispose() {                                                                                                    \n");
		res = res.append("                                                                                                                        \n");
		res = res.append("}                                                                                                                       \n");
		
		return res.toString().trim();
	}

	@Override
	protected File getDefaultDirectory() {
		File roots = ColonyManager.getHomeDir("colonization", "scripts");
		if(! roots.exists()) roots.mkdirs();
		
		File mods = new File(roots.getAbsolutePath() + File.separator + "mods");
		if(! mods.exists()) mods.mkdirs();
		
		return mods;
	}
}
