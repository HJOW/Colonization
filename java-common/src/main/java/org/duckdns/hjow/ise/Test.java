package org.duckdns.hjow.ise;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Test {
	public static void main(String[] args) throws Exception {
		InternalScriptEngine engine = new InternalScriptEngine();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		while(true) {
			System.out.print(">> ");
			String line = reader.readLine();
			if(line == null) break;
			if(line.equals("exit")) break;
			
			Object results = engine.eval(line);
			System.out.println(results);
		}
		
	}
}
