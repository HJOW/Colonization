package org.duckdns.hjow.ise.statement;

import java.io.Serializable;

/** 문장 객체, 문장은 대입할 변수, 연산자, 우측 스크립트로 구성 (우측 스크립트는 분리만 해두고 이 클래스에선 실행하지 않음) */
public class Statement implements Serializable {
	private static final long serialVersionUID = 1970355064681319761L;
    protected int    operation = OPERATION_PUT;
    protected String receiverVariable;
    protected String rightSide;
    
    public Statement() {}
    public Statement(String line) {
    	char[] chars = line.toCharArray();
    	char opPrefix = ' ';
    	char quote = ' ';
    	boolean operationDecided = false;
    	StringBuilder recvVariableName  = new StringBuilder("");
    	StringBuilder rightVariableName = new StringBuilder("");
    	StringBuilder quotesContent = new StringBuilder("");
    	
    	if(! line.contains("=")) {
    		operation = OPERATION_PUT;
    		receiverVariable = null;
    		operationDecided = true;
    	}
    	
    	for(int idx=0; idx<chars.length; idx++) {
    		char charOne = chars[idx];
    		if(quote == '\'') {
    			if(charOne == '\'') {
    				quote = ' ';
    				// TODO
    			} else {
    			    quotesContent = quotesContent.append(String.valueOf(charOne));
    			}
    			continue;
    		} else if(quote == '"') {
    			if(charOne == '"') {
    				quote = ' ';
    				// TODO
    			} else {
    			    quotesContent = quotesContent.append(String.valueOf(charOne));
    			}
    			continue;
    		}
    		
    		if(! operationDecided) {
    			if(charOne == '+' || charOne == '-' || charOne == '*') {
    				opPrefix = charOne;
    				continue;
    			} else if(charOne == '=') {
    				if(     opPrefix == ' ') operation = OPERATION_PUT;
    				else if(opPrefix == '+') operation = OPERATION_ADD;
    				else if(opPrefix == '-') operation = OPERATION_SUBTRACT;
    				else if(opPrefix == '*') operation = OPERATION_MULTIPLY;
    				opPrefix = ' ';
    				operationDecided = true;
    				receiverVariable = recvVariableName.toString().trim();
    				continue;
    			}
    			
    			recvVariableName = recvVariableName.append(String.valueOf(charOne));
    			continue;
    		} else {
    			if(charOne == '\'' || charOne == '"') {
    				quote = charOne;
    				continue;
    			}
    			
    			if(charOne == '#') { // 주석 기호 처리
    				break;
    			}
    			
    			rightVariableName = rightVariableName.append(String.valueOf(charOne));
    		}
    	}
    	
    	rightSide = rightVariableName.toString().trim();
    	recvVariableName  = null;
    	rightVariableName = null;
    }
    
	public int getOperation() {
		return operation;
	}
	public void setOperation(int operation) {
		this.operation = operation;
	}
	public String getReceiverVariable() {
		return receiverVariable;
	}
	public void setReceiverVariable(String receiverVariable) {
		this.receiverVariable = receiverVariable;
	}
	
	public String getRightSide() {
		return rightSide;
	}
	public void setRightSide(String rightSide) {
		this.rightSide = rightSide;
	}

	public static final int OPERATION_PUT      = 0;
	public static final int OPERATION_ADD      = 1;
	public static final int OPERATION_SUBTRACT = 2;
	public static final int OPERATION_MULTIPLY = 3;
}
