package org.duckdns.hjow.ise.block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.duckdns.hjow.ise.functions.Function;
import org.duckdns.hjow.ise.statement.Statement;

public class DefaultScope implements Scope {
	private static final long serialVersionUID = -6663558813760411262L;
	protected transient long        key        = new Random().nextLong();
	protected Map<String, Object>   variables  = new HashMap<String, Object>();
	protected Map<String, Function> functions  = new HashMap<String, Function>();
	protected List<Statement>       statements = new Vector<Statement>();
	
    public DefaultScope() {
    	
    }

	@Override
	public Map<String, Object> getVariables() {
		return variables;
	}
	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

	@Override
	public List<Statement> getStatements() {
		return statements;
	}

	public void setStatements(List<Statement> statements) {
		this.statements = statements;
	}

	@Override
	public Map<String, Function> getFunctions() {
		return functions;
	}

	public void setFunctions(Map<String, Function> functions) {
		this.functions = functions;
	}

	@Override
	public Scope getVariableIncludedBlock(String variableName, List<Scope> scopes) {
		// 자기자신 먼저 찾기
		Set<String> varNames = variables.keySet();
		if(varNames.contains(variableName)) return this; // 자기자신 리턴 - 로컬 변수인 케이스
		
		// 내 위로 찾기
		int index = scopes.indexOf(this);
		if(index < 0) return null;
		
		for(int idx=index; idx>=0; idx--) { // 거꾸로 거슬러 올라가기
			Scope blockOne = scopes.get(idx);
			varNames = blockOne.getVariables().keySet();
			if(varNames.contains(variableName)) return blockOne;
		}
		
		return null;
	}
	
	@Override
	public Scope getFunctionIncludedBlock(String functionName, List<Scope> scopes) {
		// 자기자신 먼저 찾기
		Set<String> funcNames = functions.keySet();
		if(funcNames.contains(functionName)) return this; // 자기자신 리턴 - 로컬 변수인 케이스
		
		// 내 위로 찾기
		int index = scopes.indexOf(this);
		if(index < 0) return null;
		
		for(int idx=index; idx>=0; idx--) { // 거꾸로 거슬러 올라가기
			Scope blockOne = scopes.get(idx);
			funcNames = blockOne.getFunctions().keySet();
			if(funcNames.contains(functionName)) return blockOne;
		}
		
		return null;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == null) return false;
		if(other instanceof DefaultScope) {
			DefaultScope d = (DefaultScope) other;
			if(this.key == d.key && this.variables == d.variables) return true;
		}
		return false;
	}
}
