package org.duckdns.hjow.ise.block;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.duckdns.hjow.ise.functions.Function;
import org.duckdns.hjow.ise.statement.Statement;

/** Scope. 로컬 변수의 영향권 내를 의미. */
public interface Scope extends Serializable {
    public Map<String, Object> getVariables();
    public Map<String, Function> getFunctions();
    public List<Statement> getStatements();
    public Scope getVariableIncludedBlock(String variableName, List<Scope> scopes);
    public Scope getFunctionIncludedBlock(String variableName, List<Scope> scopes);
}
