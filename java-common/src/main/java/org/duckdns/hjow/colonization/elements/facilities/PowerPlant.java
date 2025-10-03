package org.duckdns.hjow.colonization.elements.facilities;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Citizen;
import org.duckdns.hjow.colonization.elements.City;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.ui.ColonyPanel;

public abstract class PowerPlant extends DefaultFacility {
    private static final long serialVersionUID = -7738915080952447743L;
    protected String name = getDefaultNamePrefix() + "_" + ColonyManager.generateNaturalNumber();

    protected String getDefaultNamePrefix() {
        return "발전소";
    }

    @Override
    protected int getDefaultCapacity() {
        return 100;
    }

    @Override
    public String getName() {
        return name;
    }
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getType() {
        return getClass().getSimpleName();
    }

    @Override
    public int increasingCityMaxHP() {
        return 1;
    }

    @Override
    public int getComportGrade() {
        return 0;
    }

    @Override
    public int getMaxHp() {
        return 1000;
    }
    @Override
    public int getWorkerNeeded() {
        return 1;
    }
    @Override
    public int getWorkerCapacity() {
        return 2;
    }

    @Override
    public int getWorkerSuitability(Citizen citizen) {
        int point = 3;
        if(citizen.getCarisma()     >= 3) point += 1;
        if(citizen.getAgility()     >= 6) point += 2;
        if(citizen.getStrength()    >= 6) point += 2;
        if(citizen.getIntelligent() >= 6) point += 2;
        
        return point;
    }

    @Override
    public void fromJson(JsonObject json) {
    	super.fromJson(json);
        setName(json.get("name").toString());
        key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
        setLevel(Integer.parseInt(json.get("level").toString()));
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.putAll(super.toJson());
        json.put("type", getType());
        json.put("name", getName());
        json.put("key", new Long(getKey()));
        json.put("hp", new Long(getHp()));
        json.put("level", new Integer(getLevel()));
        
        return json;
    }

    @Override
    public void oneCycle(int cycle, City city, Colony colony, int efficiency100, ColonyPanel colPanel) {
        super.oneCycle(cycle, city, colony, efficiency100, colPanel);
        
        // Do nothing on PowerStation (implemented on City class)
    }
}
