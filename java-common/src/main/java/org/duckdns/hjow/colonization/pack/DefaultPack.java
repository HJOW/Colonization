package org.duckdns.hjow.colonization.pack;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;

/** 기본 형태의 Pack */
public class DefaultPack implements Pack {
    private static final long serialVersionUID = -8964086871404887882L;
    protected long key = ColonyManager.generateKey();
    protected String name   = getDefaultName();
    protected String desc   = getDefaultDesc();
    protected String author = getDefaultAuthor();
    protected String email  = getDefaultEmail();
    protected boolean enabled = true;
    
    protected List<Class<?>> colonyClasses   = new ArrayList<Class<?>>();
    protected List<Class<?>> facilityClasses = new ArrayList<Class<?>>();
    protected List<Class<?>> researchClasses = new ArrayList<Class<?>>();
    protected List<Class<?>> enemyClasses    = new ArrayList<Class<?>>();
    protected List<Class<?>> shipClasses     = new ArrayList<Class<?>>();
    protected List<Class<?>> stateClasses    = new ArrayList<Class<?>>();
    protected List<Class<?>> productClasses  = new ArrayList<Class<?>>();
    protected List<Class<?>> policyClasses   = new ArrayList<Class<?>>();
    protected List<String>   featureKeywords = new ArrayList<String>();
    
    public DefaultPack() { init(); }
    protected void init() {}
    
    protected String getDefaultName()   { return "Pack_" + ColonyManager.getNaturalNumberFrom(key); }
    protected String getDefaultDesc()   { return ""; }
    protected String getDefaultAuthor() { return ""; }
    protected String getDefaultEmail()  { return ""; }
    
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
    @Override
    public List<Class<?>> getShipClasses() {
		return shipClasses;
	}
	public void setShipClasses(List<Class<?>> shipClasses) {
		this.shipClasses = shipClasses;
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
    public long getKey() {
        return key;
    }
    public void setKey(long key) {
        this.key = key;
    }
    
    @Override
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    
    @Override
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public List<String> getFeatureKeywords() {
        return featureKeywords;
    }
    public void setFeatureKeywords(List<String> featureKeywords) {
        this.featureKeywords = featureKeywords;
    }
    protected void addFeatureKeywords(String ... keywords) {
        for(String str : keywords) {
            if(! this.featureKeywords.contains(str)) this.featureKeywords.add(str);
        }
    }
    @Override
    public List<Class<?>> getProductClasses() {
        return productClasses;
    }
    
    public void setProductClasses(List<Class<?>> productClasses) {
        this.productClasses = productClasses;
    }
    
    public List<Class<?>> getPolicyClasses() {
        return policyClasses;
    }
    public void setPolicyClasses(List<Class<?>> policyClasses) {
        this.policyClasses = policyClasses;
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
    
    @Override
    public List<String> newFeatures() {
        return getFeatureKeywords();
    }
}
