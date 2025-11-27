package org.duckdns.hjow.colonization.pack;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.commons.util.classwrapper.ClassWrapper;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.ui.ImageResourcePackage;

/** 기본 형태의 Pack */
public class DefaultPack implements Pack {
    private static final long serialVersionUID = -8964086871404887882L;
    protected long key = ColonyManager.generateKey();
    protected String name   = getDefaultName();
    protected String desc   = getDefaultDesc();
    protected String author = getDefaultAuthor();
    protected String email  = getDefaultEmail();
    protected boolean enabled = true;
    
    protected List<ClassWrapper> colonyClasses   = new ArrayList<ClassWrapper>();
    protected List<ClassWrapper> facilityClasses = new ArrayList<ClassWrapper>();
    protected List<ClassWrapper> researchClasses = new ArrayList<ClassWrapper>();
    protected List<ClassWrapper> enemyClasses    = new ArrayList<ClassWrapper>();
    protected List<ClassWrapper> shipClasses     = new ArrayList<ClassWrapper>();
    protected List<ClassWrapper> stateClasses    = new ArrayList<ClassWrapper>();
    protected List<ClassWrapper> productClasses  = new ArrayList<ClassWrapper>();
    protected List<ClassWrapper> policyClasses   = new ArrayList<ClassWrapper>();
    protected List<String>   featureKeywords = new ArrayList<String>();
    
    public DefaultPack() { init(); }
    protected void init() {}
    
    protected String getDefaultName()   { return "Pack_" + ColonyManager.getNaturalNumberFrom(key); }
    protected String getDefaultDesc()   { return ""; }
    protected String getDefaultAuthor() { return ""; }
    protected String getDefaultEmail()  { return ""; }
    
    @Override
    public List<ClassWrapper> getColonyClasses() {
        return colonyClasses;
    }
    @Override
    public List<ClassWrapper> getFacilityClasses() {
        return facilityClasses;
    }
    @Override
    public List<ClassWrapper> getResearchClasses() {
        return researchClasses;
    }
    @Override
    public List<ClassWrapper> getEnemyClasses() {
        return enemyClasses;
    }
    @Override
    public List<ClassWrapper> getStateClasses() {
        return stateClasses;
    }
    @Override
    public List<ClassWrapper> getShipClasses() {
		return shipClasses;
	}
	public void setShipClasses(List<ClassWrapper> shipClasses) {
		this.shipClasses = shipClasses;
	}
	public void setColonyClasses(List<ClassWrapper> colonyClasses) {
        this.colonyClasses = colonyClasses;
    }
    public void setFacilityClasses(List<ClassWrapper> facilityClasses) {
        this.facilityClasses = facilityClasses;
    }
    public void setResearchClasses(List<ClassWrapper> researchClasses) {
        this.researchClasses = researchClasses;
    }
    public void setEnemyClasses(List<ClassWrapper> enemyClasses) {
        this.enemyClasses = enemyClasses;
    }
    public void setStateClasses(List<ClassWrapper> stateClasses) {
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
    public List<ClassWrapper> getProductClasses() {
        return productClasses;
    }
    
    public void setProductClasses(List<ClassWrapper> productClasses) {
        this.productClasses = productClasses;
    }
    
    public List<ClassWrapper> getPolicyClasses() {
        return policyClasses;
    }
    public void setPolicyClasses(List<ClassWrapper> policyClasses) {
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
	@Override
	public ImageResourcePackage getImageResources() {
		return null;
	}
}
