package org.duckdns.hjow.colonization.ui.help;

import java.io.Serializable;

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
}
