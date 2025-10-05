package org.duckdns.hjow.colonization.pack;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;

/** 기본 형태의 Pack */
public class DefaultPack implements Pack {
    private static final long serialVersionUID = -8964086871404887882L;
    protected String name = "Pack_" + ColonyManager.generateKey();
    protected String desc = "";
    protected boolean enabled = true;
    
    protected List<Class<?>> colonyClasses   = new ArrayList<Class<?>>();
    protected List<Class<?>> facilityClasses = new ArrayList<Class<?>>();
    protected List<Class<?>> researchClasses = new ArrayList<Class<?>>();
    protected List<Class<?>> enemyClasses    = new ArrayList<Class<?>>();
    protected List<Class<?>> stateClasses    = new ArrayList<Class<?>>();
    protected List<Class<?>> productClasses  = new ArrayList<Class<?>>();
    
    public DefaultPack() { init(); }
    protected void init() {}
    
    @Override
    public List<Class<?>> getColonyClasses() {
        return colonyClasses;
    }
    @Override
    public List<Class<?>> getFacilityClasses() {
        return facilityClasses;
    }
    @Override
    public List<Class<?>> getResearchClasses() {
        return researchClasses;
    }
    @Override
    public List<Class<?>> getEnemyClasses() {
        return enemyClasses;
    }
    @Override
    public List<Class<?>> getStateClasses() {
        return stateClasses;
    }
    public void setColonyClasses(List<Class<?>> colonyClasses) {
        this.colonyClasses = colonyClasses;
    }
    public void setFacilityClasses(List<Class<?>> facilityClasses) {
        this.facilityClasses = facilityClasses;
    }
    public void setResearchClasses(List<Class<?>> researchClasses) {
        this.researchClasses = researchClasses;
    }
    public void setEnemyClasses(List<Class<?>> enemyClasses) {
        this.enemyClasses = enemyClasses;
    }
    public void setStateClasses(List<Class<?>> stateClasses) {
        this.stateClasses = stateClasses;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    @Override
    public List<Class<?>> getProductClasses() {
        return productClasses;
    }
    
    public void setProductClasses(List<Class<?>> productClasses) {
        this.productClasses = productClasses;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    @Override
    public boolean equals(Object oth) {
        if(oth == null) return false;
        if(! (oth instanceof Pack)) return false;
        Pack otherPack = (Pack) oth;
        return otherPack.getName().equals(getName());
    }
}
