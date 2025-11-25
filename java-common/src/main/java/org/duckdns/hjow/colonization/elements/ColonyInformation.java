package org.duckdns.hjow.colonization.elements;

import java.io.Serializable;

import org.duckdns.hjow.classwrapper.ClassWrapper;
import org.duckdns.hjow.classwrapper.SimpleClassWrapper;

/** 정착지 시나리오 정보 */
public class ColonyInformation implements Serializable {
    private static final long serialVersionUID = -764866136990865870L;
    protected String name, description, title;
    protected ClassWrapper colonyClassWrapper;
    protected int[] difficulties;
    
    public ColonyInformation() {
        difficulties = new int[9];
        for(int idx=0; idx<difficulties.length; idx++) {  difficulties[idx] = (idx + 1); }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    @SuppressWarnings("unchecked")
	public Class<? extends Colony> getColonyClass() {
        return (Class<? extends Colony>) colonyClassWrapper.getWrappedClass();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setColonyClass(Class<? extends Colony> colonyClass) {
        this.colonyClassWrapper = new SimpleClassWrapper(colonyClass);
    }
    
    public ClassWrapper getColonyClassWrapper() {
		return colonyClassWrapper;
	}

	public void setColonyClassWrapper(ClassWrapper colonyClassWrapper) {
		this.colonyClassWrapper = colonyClassWrapper;
	}

	public int[] getDifficulties() {
        return difficulties;
    }

    public void setDifficulties(int[] difficulties) {
        this.difficulties = difficulties;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
