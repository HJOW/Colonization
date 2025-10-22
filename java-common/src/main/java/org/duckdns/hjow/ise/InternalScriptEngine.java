package org.duckdns.hjow.ise;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.duckdns.hjow.ise.block.DefaultScope;
import org.duckdns.hjow.ise.block.NullObject;
import org.duckdns.hjow.ise.block.Scope;
import org.duckdns.hjow.ise.statement.Statement;

public class InternalScriptEngine {
	protected transient List<Scope> scopes = new ArrayList<Scope>();
	
    public InternalScriptEngine() {
    	
    }
    
    protected List<Statement> separates(String scripts) {
    	List<Statement> list = new ArrayList<Statement>();
    	StringTokenizer lineTokenizer = new StringTokenizer(scripts, "\n");
    	while(lineTokenizer.hasMoreTokens()) {
    		list.add(new Statement(lineTokenizer.nextToken().trim()));
    	}
    	return list;
    }
    
    public Object eval(String scripts) {
		DefaultScope block = new DefaultScope(); // 최상단 Block
		block.setStatements(separates(scripts));
		return runBlock(block);
	}
    
    /** 문장 실행 */
    protected Object runBlock(Scope block) {
    	Object returnValue = null;
    	for(Statement st : block.getStatements()) {
    		returnValue = null;
    		
    		String variableReceiver = st.getReceiverVariable();
    		Scope receiverVariableBlock = null; // 변수가 더 상위 블록에 존재하는 경우를 대비
    		if(variableReceiver != null) {
    			receiverVariableBlock = block.getVariableIncludedBlock(variableReceiver, scopes);
    			if(receiverVariableBlock == null) {
    			    // 이전에 이 변수를 사용한 적이 없다 - 새 변수 생성 
    				block.getVariables().put(variableReceiver, new NullObject());
    				receiverVariableBlock = block;
    			}
    		}
    		
    		String rightSide = st.getRightSide();  // 우측 스크립트 가져오기
    		returnValue = runRightSide(rightSide, block); // 실행
    		
    		if(receiverVariableBlock != null) {
    			if(returnValue == null) returnValue = new NullObject();
    			int op = st.getOperation();
    			
    			if(op == Statement.OPERATION_PUT) {
    			    receiverVariableBlock.getVariables().put(variableReceiver, returnValue); // 변수에 넣기
    			} else if(op == Statement.OPERATION_ADD) {
    			    Object originalValue = receiverVariableBlock.getVariables().get(variableReceiver);
    			    if((originalValue instanceof String) || (returnValue instanceof String)) { // 둘 중 하나라도 문자열이면 결과는 무조건 문자열, concat 처리
    			    	returnValue = String.valueOf(originalValue) + String.valueOf(returnValue);
    			    } else if((originalValue instanceof Long) && (returnValue instanceof Long)) {
    			    	returnValue = new Long( (((Long) originalValue).longValue()) + (((Long) returnValue).longValue()) );
    			    } else if((originalValue instanceof Long) && (returnValue instanceof Integer)) {
    			    	returnValue = new Long( (((Long) originalValue).longValue()) + (((Integer) returnValue).intValue()) );
    			    } else if((originalValue instanceof Integer) && (returnValue instanceof Long)) {
    			    	returnValue = new Long( (((Integer) originalValue).intValue()) + (((Long) returnValue).longValue()) );
    			    } else if((originalValue instanceof Integer) && (returnValue instanceof Integer)) {
    			    	returnValue = new Integer( (((Integer) originalValue).intValue()) + (((Integer) returnValue).intValue()) );
    			    } else if((originalValue instanceof Number) && (returnValue instanceof Number)) {
    			    	returnValue = new Double( (((Double) originalValue).doubleValue()) + (((Double) returnValue).doubleValue()) );
    			    } else {
    			    	throw new RuntimeException("Wrong + operation on statement !");
    			    }
    			} else if(op == Statement.OPERATION_SUBTRACT) {
    				Object originalValue = receiverVariableBlock.getVariables().get(variableReceiver);
    				if((originalValue instanceof Long) && (returnValue instanceof Long)) {
    					returnValue = new Long( (((Long) originalValue).longValue()) - (((Long) returnValue).longValue()) );
    			    } else if((originalValue instanceof Long) && (returnValue instanceof Integer)) {
    			    	returnValue = new Long( (((Long) originalValue).longValue()) - (((Integer) returnValue).intValue()) );
    			    } else if((originalValue instanceof Integer) && (returnValue instanceof Long)) {
    			    	returnValue = new Long( (((Integer) originalValue).intValue()) - (((Long) returnValue).longValue()) );
    			    } else if((originalValue instanceof Integer) && (returnValue instanceof Integer)) {
    			    	returnValue = new Integer( (((Integer) originalValue).intValue()) - (((Integer) returnValue).intValue()) );
    			    } else if((originalValue instanceof Number) && (returnValue instanceof Number)) {
    			    	returnValue = new Double( (((Double) originalValue).doubleValue()) - (((Double) returnValue).doubleValue()) );
    			    } else {
    			    	throw new RuntimeException("Wrong - operation on statement !");
    			    }
    			} else if(op == Statement.OPERATION_MULTIPLY) {
    				Object originalValue = receiverVariableBlock.getVariables().get(variableReceiver);
    				if((originalValue instanceof Long) && (returnValue instanceof Long)) {
    					returnValue = new Long( (((Long) originalValue).longValue()) * (((Long) returnValue).longValue()) );
    			    } else if((originalValue instanceof Long) && (returnValue instanceof Integer)) {
    			    	returnValue = new Long( (((Long) originalValue).longValue()) * (((Integer) returnValue).intValue()) );
    			    } else if((originalValue instanceof Integer) && (returnValue instanceof Long)) {
    			    	returnValue = new Long( (((Integer) originalValue).intValue()) * (((Long) returnValue).longValue()) );
    			    } else if((originalValue instanceof Integer) && (returnValue instanceof Integer)) {
    			    	returnValue = new Integer( (((Integer) originalValue).intValue()) * (((Integer) returnValue).intValue()) );
    			    } else if((originalValue instanceof Number) && (returnValue instanceof Number)) {
    			    	returnValue = new Double( (((Double) originalValue).doubleValue()) * (((Double) returnValue).doubleValue()) );
    			    } else {
    			    	throw new RuntimeException("Wrong * operation on statement !");
    			    }
    			} else throw new RuntimeException("Wrong operation on statement !");
    		}
    	}
    	return returnValue; // 마지막 리턴값 반환
    }
    
    /** 우측 스크립트 실행 */
    protected Object runRightSide(String rightSide, Scope scope) {
    	if(rightSide == null) return null;
    	rightSide = rightSide.trim();
    	if(rightSide.equals("")) return null;
    	
    	Object value = null;
    	
    	// 숫자 리터럴 여부 체크
    	try {
    		Double.parseDouble(rightSide);
    		try {
    			Long.parseLong(rightSide); // 정부 여부도 판단
        		value = new Long(rightSide);
    		} catch(NumberFormatException ex) {
    			value = new Double(rightSide);
    		}
    	} catch(NumberFormatException ex) { }
    	
    	// 문자열 리터럴 여부 체크
    	if(value == null) {
    		if(rightSide.startsWith("\"") && rightSide.endsWith("\"")) {
    			String inside = rightSide.substring(1, rightSide.length() - 1); // 양옆 따옴표 제거
    			inside = inside.replace("\\" + "\"", "\""); // 캐스팅 된 따옴표 복원
    			value = inside;
    		} else if(rightSide.startsWith("'") && rightSide.endsWith("'")) {
    			String inside = rightSide.substring(1, rightSide.length() - 1); // 양옆 따옴표 제거
    			inside = inside.replace("\\" + "'", "'"); // 캐스팅 된 따옴표 복원
    			value = inside;
    		}
    		
    		if(rightSide.startsWith("\"") || rightSide.startsWith("'")) throw new RuntimeException("Wrong quote using !");
    	}
    	
    	// 함수 여부 체크
    	if(value == null) {
    		int leftBracelet  = rightSide.indexOf("(");
    		int rightBracelet = rightSide.lastIndexOf(")");
    		if(leftBracelet >= 0 && rightBracelet > leftBracelet) {
    		    // 함수 맞다
    			//    함수 객체 찾기
    			//        해당 이름의 함수가 있는 Scope 찾기
    			Scope scopeHas = scope.getFunctionIncludedBlock(rightSide, scopes); // TODO
    			
    			// 내부 매개변수부 꺼내기
    			String insides = rightSide.substring(leftBracelet + 1, rightBracelet).trim();
    			if(insides.equals("")) {
    				
    			}
    			
    		}
    	}
    	
    	return null;
    }
}
