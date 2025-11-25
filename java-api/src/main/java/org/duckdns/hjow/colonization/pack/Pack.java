package org.duckdns.hjow.colonization.pack;

import java.io.Serializable;
import java.util.List;

import org.duckdns.hjow.classwrapper.ClassWrapper;

/** 여러 클래스 정보들을 담은 객체를 Pack 이라 하고, 이 Pack 정의 클래스임을 명시하기 위한 인터페이스 */
public interface Pack extends Serializable {
    public List<ClassWrapper> getColonyClasses();
    public List<ClassWrapper> getFacilityClasses();
    public List<ClassWrapper> getResearchClasses();
    public List<ClassWrapper> getEnemyClasses();
    public List<ClassWrapper> getShipClasses();
    public List<ClassWrapper> getStateClasses();
    public List<ClassWrapper> getProductClasses();
    public List<ClassWrapper> getPolicyClasses();
    public long getKey();
    public String getName();
    public String getDesc();
    public String getAuthor();
    public String getEmail();
    public boolean isEnabled();
    public void setEnabled(boolean enabled);
    public List<String> newFeatures();
}
