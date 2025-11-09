package org.duckdns.hjow.colonization.ui.tools;

import java.awt.Window;
import java.io.File;

import org.duckdns.hjow.colonization.ColonyManager;

/** 스크립트 기반 Facility 개발 툴 */
public class ScriptFacilityEditor extends ScriptCreatorTool {
    public ScriptFacilityEditor(Window win) {
        super(win);
    }

    @Override
    public String getName() {
        return "SCRIPTFAC";
    }

    @Override
    public String getTitle() {
        return "시설 에디터";
    }

    @Override
    protected String target() {
        return "시설";
    }

    @Override
    protected String getDefaultContent() {
        StringBuilder res = new StringBuilder("");
        
        res = res.append("/*                                                                                                                      \n");
        res = res.append("아래 함수들을 구현하여 사용자 정의 시설을 개발할 수 있습니다.                                                           \n");
        res = res.append("     프리 정착지 시나리오에서만 사용이 가능하며, 사용 시 인증이 되지 않습니다.                                          \n");
        res = res.append("도움말에 있는 스크립트 API를 사용할 수 있습니다.                                                                        \n");
        res = res.append("저장한 MOD는 프로그램 재시작 후 사용할 수 있습니다.                                                                     \n");
        res = res.append("                                                                                                                        \n");
        res = res.append("function getName() {                                                                                                    \n");
        res = res.append("    return \"\";                                                                                                        \n");
        res = res.append("}                                                                                                                       \n");
        res = res.append("                                                                                                                        \n");
        res = res.append("function getTitle() {                                                                                                   \n");
        res = res.append("    return \"\";                                                                                                        \n");
        res = res.append("}                                                                                                                       \n");
        res = res.append("                                                                                                                        \n");
        res = res.append("function getDescription() {                                                                                             \n");
        res = res.append("    return \"\";                                                                                                        \n");
        res = res.append("}                                                                                                                       \n");
        res = res.append("                                                                                                                        \n");
        res = res.append("function getPrice() {                                                                                                   \n");
        res = res.append("    return 10000;                                                                                                       \n");
        res = res.append("}                                                                                                                       \n");
        res = res.append("                                                                                                                        \n");
        res = res.append("function getBuildingCycle() {                                                                                           \n");
        res = res.append("    return 300;                                                                                                         \n");
        res = res.append("}                                                                                                                       \n");
        res = res.append("                                                                                                                        \n");
        res = res.append("function getSpaceSize() {                                                                                               \n");
        res = res.append("    return 1;                                                                                                           \n");
        res = res.append("}                                                                                                                       \n");
        res = res.append("                                                                                                                        \n");
        res = res.append("function getUniqueGrade() {                                                                                             \n");
        res = res.append("    return 0;                                                                                                           \n");
        res = res.append("}                                                                                                                       \n");
        res = res.append("                                                                                                                        \n");
        res = res.append("function getTech() {                                                                                                    \n");
        res = res.append("    return 0;                                                                                                           \n");
        res = res.append("}                                                                                                                       \n");
        res = res.append("                                                                                                                        \n");
        res = res.append("function getResearchCoditions(colony) {                                                                                 \n");
        res = res.append("    return null;                                                                                                        \n");
        res = res.append("}                                                                                                                       \n");
        res = res.append("                                                                                                                        \n");
        res = res.append("function getDefaultNamePrefix() {                                                                                       \n");
        res = res.append("    return \"customFacility01\";                                                                                        \n");
        res = res.append("}                                                                                                                       \n");
        res = res.append("                                                                                                                        \n");
        res = res.append("function getMaxHp() {                                                                                                   \n");
        res = res.append("    return 1000;                                                                                                        \n");
        res = res.append("}                                                                                                                       \n");
        res = res.append("                                                                                                                        \n");
        res = res.append("function getDefenceType() {                                                                                             \n");
        res = res.append("    return 9;                                                                                                           \n");
        res = res.append("}                                                                                                                       \n");
        res = res.append("                                                                                                                        \n");
        res = res.append("function usingFee() {                                                                                                   \n");
        res = res.append("    return 0;                                                                                                           \n");
        res = res.append("}                                                                                                                       \n");
        res = res.append("                                                                                                                        \n");
        res = res.append("function getPowerConsume() {                                                                                            \n");
        res = res.append("    return 1;                                                                                                           \n");
        res = res.append("}                                                                                                                       \n");
        res = res.append("                                                                                                                        \n");
        res = res.append("function getMaintainFee(city, colony) {                                                                                 \n");
        res = res.append("    return 1;                                                                                                           \n");
        res = res.append("}                                                                                                                       \n");
        res = res.append("                                                                                                                        \n");
        res = res.append("function getDestructionFee(city, colony) {                                                                              \n");
        res = res.append("    return 100;                                                                                                         \n");
        res = res.append("}                                                                                                                       \n");
        res = res.append("                                                                                                                        \n");
        res = res.append("function getWorkerSuitability(citizen) {                                                                                \n");
        res = res.append("    return 1;                                                                                                           \n");
        res = res.append("}                                                                                                                       \n");
        
        return res.toString().trim();
    }

    @Override
    protected File getDefaultDirectory() throws Throwable {
        File roots = ColonyManager.getHomeDir("colonization", "scripts");
        if(! roots.exists()) roots.mkdirs();
        
        File mods = new File(roots.getAbsolutePath() + File.separator + "facilities");
        if(! mods.exists()) mods.mkdirs();
        
        return mods;
    }
}
