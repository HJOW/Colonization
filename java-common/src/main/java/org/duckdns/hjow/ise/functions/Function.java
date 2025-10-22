package org.duckdns.hjow.ise.functions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** 함수 다루는 클래스. */
public abstract class Function implements Serializable {
	private static final long serialVersionUID = -6641414135983343318L;
	protected String name;
	protected List<String> parameterNames = new ArrayList<String>();
	
    public Function() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getParameterNames() {
		return parameterNames;
	}

	public void setParameterNames(List<String> parameterNames) {
		this.parameterNames = parameterNames;
	}
	
	public abstract Object run(Map<String, Object> parameters);
}
