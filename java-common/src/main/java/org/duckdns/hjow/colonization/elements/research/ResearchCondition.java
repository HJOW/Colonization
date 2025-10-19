package org.duckdns.hjow.colonization.elements.research;

import java.io.Serializable;

import org.duckdns.hjow.commons.json.JsonObject;

/** 연구 진행을 위한 선행 조건을 위한 클래스 */
public class ResearchCondition implements Serializable {
	private static final long serialVersionUID = 6760082776646521792L;
	/** 필요로 하는 사전 연구의 클래스명 */
	protected String researchClassName = null;
	/** 해당 사전 연구의 최소 레벨 (대상 연구 레벨과는 관계가 없는 값) */
    protected int level = 1;
    /** 해당 사전 연구의 최소 레벨 비율 (이 사전 연구의 레벨이, 대상 연구 레벨에 이 수치가 곱셈된 수보다 커야 연구 가능) */
    protected double levelRate = 1.0;
    /** 대상 연구의 레벨이 이 이상이 되어야 이 조건이 활성화됨. 0이면 항상 활성화. */
    protected int startLevel = 0;
    public ResearchCondition() {}
    
	public ResearchCondition(String researchClassName, int level) {
		this();
		this.researchClassName = researchClassName;
		this.level = level;
	}
	
	public ResearchCondition(String researchClassName, int level, double levelRate) {
		this();
		this.researchClassName = researchClassName;
		this.level = level;
		this.levelRate = levelRate;
	}
	
	public ResearchCondition(String researchClassName, int level, double levelRate, int startLevel) {
		this();
		this.researchClassName = researchClassName;
		this.level = level;
		this.levelRate = levelRate;
		this.startLevel = startLevel;
	}

	public String getResearchClassName() {
		return researchClassName;
	}
	public void setResearchClassName(String researchClassName) {
		this.researchClassName = researchClassName;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}

	public double getLevelRate() {
		return levelRate;
	}

	public void setLevelRate(double levelRate) {
		this.levelRate = levelRate;
	}

	public int getStartLevel() {
		return startLevel;
	}

	public void setStartLevel(int startLevel) {
		this.startLevel = startLevel;
	}
	
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.put("targetResearch", getResearchClassName());
		json.put("level", new Integer(getLevel()));
		json.put("levelRate", new Double(getLevelRate()));
		json.put("startLevel", new Integer(getStartLevel()));
		return json;
	}
	
	public void fromJson(JsonObject json) {
		setResearchClassName(json.get("targetResearch").toString());
		if(json.containsKey("level"))      setLevel(Integer.parseInt(json.get("level").toString()));
		if(json.containsKey("levelRate"))  setLevelRate(Double.parseDouble(json.get("levelRate").toString()));
		if(json.containsKey("startLevel")) setStartLevel(Integer.parseInt(json.get("startLevel").toString()));
	}
}
