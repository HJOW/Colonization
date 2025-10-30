package org.duckdns.hjow.colonization.script;

import java.net.MalformedURLException;
import java.net.URL;

import org.duckdns.hjow.commons.script.ScriptObject;
import org.duckdns.hjow.commons.util.NetUtil;

/** 공통 Lib 의 NetObject 기능제한판 */
public class NetObject extends ScriptObject {
	private static final long serialVersionUID = 2731949774238629654L;
	private static final NetObject uniqueObject = new NetObject();
	private NetObject() {  }
	
    protected org.duckdns.hjow.commons.script.NetObject originObject = org.duckdns.hjow.commons.script.NetObject.getInstance();
    
    @Override
    public String getPrefixName() {
        return originObject.getPrefixName();
    }
    @Override
    public void releaseResource() {
    	originObject.releaseResource();
    }
    
    @Override
    public String getInitScript(String accessKey) {
        StringBuilder initScript = new StringBuilder("");
        
        initScript = initScript.append("function net_sendGet(url, encoding) {                                                                   ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".sendGet(url, encoding);                            ").append("\n");
        initScript = initScript.append("};                                                                                                      ").append("\n");
        initScript = initScript.append("function net_sendPost(url, param, contentType, encoding) {                                              ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".sendPost(url, param, contentType, encoding);       ").append("\n");
        initScript = initScript.append("};                                                                                                      ").append("\n");
        
        return initScript.toString();
    }
    
    public String sendGet(Object url, Object charset) throws MalformedURLException, Throwable {
    	if(charset == null) charset = "UTF-8";
    	return NetUtil.sendGet(new URL(url.toString()), charset.toString());
    }
    
    public String sendPost(Object url, Object parameters, Object contentType, Object parameterEncoding) throws Throwable {
    	return originObject.sendPost(url, parameters, contentType, parameterEncoding);
    }
    
    public static NetObject getInstance() {
        return uniqueObject;
    }
}
