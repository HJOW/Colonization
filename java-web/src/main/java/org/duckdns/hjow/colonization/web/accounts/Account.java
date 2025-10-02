package org.duckdns.hjow.colonization.web.accounts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.colonization.ColonyClassLoader;
import org.duckdns.hjow.colonization.GlobalLogs;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.SecurityUtil;

public class Account implements Serializable {
    private static final long serialVersionUID = -3588933635720038238L;
    protected String id, name;
    protected String passwordHash;
    protected int    status = 1;
    protected int    grade  = 1;
    protected List<Colony> colonies = new ArrayList<Colony>();
    
    public Account() {
        
    }
    
    public Account(String id, String name, String passwordHash, int status, int grade, List<Colony> colonies) {
        this();
        this.id = id;
        this.name = name;
        this.passwordHash = passwordHash;
        this.status = status;
        this.grade = grade;
        this.colonies = colonies;
    }

    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public int getStatus() {
        return status;
    }

    public int getGrade() {
        return grade;
    }

    public List<Colony> getColonies() {
        return colonies;
    }

    public void setId(String id) {
        this.id = removeProhibitedChars(id);
        this.id = this.id.replace("/", "").replace("\\", "").replace("!", "").replace("@", "").replace("%", "").replace("\"", "").replace("'", "").replace(";", "");
        this.id = this.id.replace(",", "").replace(":", "").replace("~", "").replace("\r", "").replace("\t", "").replace("\n", "");
        this.id = this.id.trim();
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public void setPassword(String password) {
        setPasswordHash(SecurityUtil.hash(password, "SHA-256"));
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public void setColonies(List<Colony> colonies) {
        this.colonies = colonies;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("id", getId());
        json.put("pw", getPasswordHash());
        json.put("name", getName());
        json.put("status", new Integer(getStatus()));
        json.put("grade" , new Integer(getGrade()));
        
        JsonArray arr = new JsonArray();
        for(Colony c : getColonies()) {
            arr.add(c.toJson());
        }
        json.put("colonies", arr);
        return json;
    }
    
    public void fromJson(JsonObject json) {
        setId(json.get("id").toString());
        setPasswordHash(json.get("pw").toString());
        setName(json.get("name").toString());
        setStatus(Integer.parseInt(json.get("status").toString()));
        setGrade(Integer.parseInt(json.get("grade").toString()));
        
        JsonArray list = null;
        try { list = (JsonArray) json.get("colonies"); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        colonies.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        Colony col = ColonyClassLoader.loadColony((JsonObject) o);
                        colonies.add(col);
                    } catch(Exception ex) {
                        GlobalLogs.processExceptionOccured(ex, false);
                    }
                }
            }
        }
    }
    
    public static String removeProhibitedChars(String id) {
        id = id.replace("/", "").replace("\\", "").replace("!", "").replace("@", "").replace("%", "").replace("\"", "").replace("'", "").replace(";", "");
        id = id.replace(",", "").replace(":", "").replace("~", "").replace("\r", "").replace("\t", "").replace("\n", "");
        id = id.trim();
        return id;
    }
}
