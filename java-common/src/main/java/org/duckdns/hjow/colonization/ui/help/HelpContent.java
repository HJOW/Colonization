package org.duckdns.hjow.colonization.ui.help;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.ClassUtil;

/** 도움말 컨텐츠 */
public class HelpContent implements Serializable {
    private static final long serialVersionUID = 4441074931971499888L;
    protected String name, contentType, content;
    public HelpContent() { contentType = "text/plain"; content = ""; }
    public HelpContent(String name, String content) {
        this();
        this.name = name;
        this.content = content;
    }
    public HelpContent(String name, String contentType, String content) {
        this();
        this.name = name;
        this.contentType = contentType;
        this.content = content;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    /** content.json 에서 해당 리소스를 찾아 도움말 컨텐츠 꺼내 반환 */
    public static List<HelpContent> getHelpContentsFrom(String resources) {
        return getHelpContentsFrom(HelpContent.class, resources);
    }
    
    /** content.json 에서 해당 리소스를 찾아 도움말 컨텐츠 꺼내 반환 */
    public static List<HelpContent> getHelpContentsFrom(Class<?> basisClass, String resources) {
        List<HelpContent> list = new ArrayList<HelpContent>();
        
        InputStream       inp1 = null;
        InputStreamReader inp2 = null;
        BufferedReader    inp3 = null;
        
        StringBuilder strContent = new StringBuilder("");
        try {
            // 컨텐츠 정보 요약 json 읽기
            inp1 = basisClass.getResourceAsStream("content.json");
            inp2 = new InputStreamReader(inp1, "UTF-8");
            inp3 = new BufferedReader(inp2);
            String line;
            while(true) {
                line = inp3.readLine();
                if(line == null) break;
                strContent = strContent.append("\n").append(line);
            }
            
            ClassUtil.closeAll(inp3, inp2, inp1);
            inp3 = null;
            inp2 = null;
            inp1 = null;
            
            JsonArray arr = (JsonArray) JsonObject.parseJson(strContent.toString().trim());
            strContent = null;
            
            // 컨텐츠별 루프
            for(Object obj : arr) {
                JsonObject json = (JsonObject) obj;
                
                HelpContent instances = new HelpContent();
                instances.setName(json.get("name").toString());
                instances.setContentType(json.get("contentType") == null ? "text/plain" : json.get("contentType").toString());
                
                // 실제 도움말 내용 읽기
                String fileName = json.get("file") == null ? null : json.get("file").toString();
                strContent = new StringBuilder("");
                if(fileName != null) {
                    inp1 = basisClass.getResourceAsStream(fileName);
                    if(inp1 == null) continue;
                    
                    inp2 = new InputStreamReader(inp1, "UTF-8");
                    inp3 = new BufferedReader(inp2);
                    
                    while(true) {
                        line = inp3.readLine();
                        if(line == null) break;
                        strContent = strContent.append("\n").append(line);
                    }
                    
                    ClassUtil.closeAll(inp3, inp2, inp1);
                    inp3 = null;
                    inp2 = null;
                    inp1 = null;
                    
                    instances.setContent(strContent.toString().trim());
                    strContent = null;
                }
                
                list.add(instances);
            }
            
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            ClassUtil.closeAll(inp3, inp2, inp1);
        }
        
        return list;
    }
}
