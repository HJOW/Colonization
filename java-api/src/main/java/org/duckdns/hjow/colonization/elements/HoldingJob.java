package org.duckdns.hjow.colonization.elements;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.commons.core.JsonCompatible;
import org.duckdns.hjow.commons.json.JsonObject;

/** 진행중인 작업 하나를 나타내는 객체를 위한 인터페이스 */
public interface HoldingJob extends Serializable, JsonCompatible {
    public long getKey();
    public int getUsingSpace();
    public int getCycleMax();
    public int getCycleLeft();
    public String getCommand();
    public String getCommandTitle();
    public void decreaseCycle();
    public String getParameter();
    public boolean isCompleted();
    public List<Citizen> getWorkingCitizens(City city);
    public JsonObject toJson();
    public void fromJson(JsonObject json);
    public BigInteger getCheckerValue();
    public void setCompleted(boolean completed);
    public void setUsingSpace(int usingSpace);
    public void setCycleLeft(int cycleLeft);
}
