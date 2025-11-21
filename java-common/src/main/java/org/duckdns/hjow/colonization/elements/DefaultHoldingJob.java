package org.duckdns.hjow.colonization.elements;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.commons.exception.KnownRuntimeException;
import org.duckdns.hjow.commons.json.JsonObject;

/** 진행중인 작업 하나를 나타내는 객체를 위한 클래스 */
public class DefaultHoldingJob implements HoldingJob {
    private static final long serialVersionUID = -8030473577462698183L;
    protected long   key       = ColonyManager.generateKey();
    protected int    cycleMax  = 0;
    protected int    cycleLeft = 0;
    protected int    usingSpace = 0;
    protected String command   = null;
    protected String parameter = null;
    
    protected transient boolean completed = false;
    
    public DefaultHoldingJob() {
        
    }
    
    public DefaultHoldingJob(int cycleLeft, int cycleMax, String command, String parameter) {
        this();
        this.cycleLeft = cycleLeft;
        this.cycleMax = cycleMax;
        this.command = command;
        this.parameter = parameter;
    }

    @Override
    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    @Override
    public int getUsingSpace() {
        if("NewFacility".equals(getCommand())) return usingSpace;
        return 0;
    }

    @Override
    public void setUsingSpace(int usingSpace) {
        this.usingSpace = usingSpace;
    }

    @Override
    public int getCycleMax() {
        return cycleMax;
    }

    public void setCycleMax(int cycleMax) {
        this.cycleMax = cycleMax;
        if(this.cycleMax < 0) this.cycleMax = 0;
    }

    @Override
    public int getCycleLeft() {
        return cycleLeft;
    }

    @Override
    public String getCommand() {
        return command;
    }
    
    @Override
    public String getCommandTitle() {
        if("NewFacility".equals(getCommand()))     return ColonyManager.t("건설");
        if("NewCitizen".equals(getCommand()))      return ColonyManager.t("이주");
        if("UpgradeFacility".equals(getCommand())) return ColonyManager.t("증축");
        
        return ColonyManager.t("작업");
    }

    @Override
    public void setCycleLeft(int cycleLeft) {
        this.cycleLeft = cycleLeft;
        if(this.cycleLeft < 0) this.cycleLeft = 0;
    }
    
    @Override
    public void decreaseCycle() {
        this.cycleLeft--;
        if(this.cycleLeft < 0) this.cycleLeft = 0;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
    
    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    @Override
    public List<Citizen> getWorkingCitizens(City city) {
        List<Citizen> citizens = new ArrayList<Citizen>();
        for(Citizen c : city.getCitizens()) {
            if(c.getBuildingFacility() == getKey()) citizens.add(c);
        }
        return citizens;
    }
    
    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type", "HoldingJob");
        json.put("key", String.valueOf(getKey()));
        json.put("cycleMax", new Integer(getCycleMax()));
        json.put("cycleLeft", new Integer(getCycleLeft()));
        json.put("space", new Integer(getUsingSpace()));
        json.put("command"  , getCommand());
        json.put("parameter", getParameter());
        return json;
    }
    
    @Override
    public void fromJson(JsonObject json) {
        if(! "HoldingJob".equals(json.get("type"))) throw new KnownRuntimeException("This object is not HoldingJob type.");
        key = Long.parseLong(json.get("key").toString());
        setCycleMax(Integer.parseInt(json.get("cycleMax").toString()));
        setCycleLeft(Integer.parseInt(json.get("cycleLeft").toString()));
        setUsingSpace(Integer.parseInt(json.get("space").toString()));
        setCommand(json.get("command").toString());
        setParameter(json.get("parameter").toString());
    }
    
    @Override
    public BigInteger getCheckerValue() {
        BigInteger res = new BigInteger(String.valueOf(getKey()));
        res = res.add(new BigInteger(String.valueOf(getCycleLeft())));
        for(int idx=0; idx<getCommand().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getCommand().charAt(idx)))); }
        for(int idx=0; idx<getCommandTitle().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getCommandTitle().charAt(idx)))); }
        for(int idx=0; idx<getParameter().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getParameter().charAt(idx)))); }
        return res;
    }

	@Override
	public Object cloneThis() {
		DefaultHoldingJob newInst = new DefaultHoldingJob();
		newInst.setKey(getKey());
		newInst.setCommand(getCommand());
		newInst.setParameter(getParameter());
		newInst.setCompleted(isCompleted());
		newInst.setCycleLeft(getCycleLeft());
		newInst.setCycleMax(getCycleMax());
		newInst.setUsingSpace(getUsingSpace());
		return newInst;
	}
}
