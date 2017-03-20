package cop5556sp17;

import java.util.*;
import java.util.Map.Entry;

import cop5556sp17.AST.Dec;

public class SymbolTable {
	
	HashMap<String, ArrayList<Dec>> symbolTable;
	Stack<Integer> scopeStack;
	int currentScope;
	int nextScope;
	HashMap<String, ArrayList<Integer>> scopeTable;
	
	public SymbolTable() {
		symbolTable = new HashMap<String, ArrayList<Dec>>();
		scopeStack = new Stack<Integer>();
		currentScope = 0;
		nextScope = 0;
		scopeTable = new HashMap<String, ArrayList<Integer>>();
		scopeStack.push(currentScope);
	}

	/** 
	 * to be called when block entered
	 */
	public void enterScope() {
		currentScope = ++nextScope;
		scopeStack.push(currentScope);
	}
	
	
	/**
	 * leaves scope
	 */
	public void leaveScope() {
		scopeStack.pop();
		currentScope = scopeStack.peek();	
	}
	
	public boolean insert(String ident, Dec dec) {
		ArrayList<Dec> decList = new ArrayList<Dec>();
		ArrayList<Integer> scopeList = new ArrayList<Integer>();
		
		if (symbolTable.get(ident) != null) {
			decList = symbolTable.get(ident);
			scopeList = scopeTable.get(ident);
		}
		decList.add(dec);
		scopeList.add(currentScope);
		
		// Insert dec to the symbol table
		symbolTable.put(ident, decList);
		// Insert currentScope to scope table
		scopeTable.put(ident, scopeList);
		
		return true;
	}
	
	public Dec lookup(String ident) {
		// return null if no declaration exists 
		if (symbolTable.isEmpty()) {
			return null;
		}
		
		ArrayList<Dec> decList = symbolTable.get(ident);
		ArrayList<Integer> scopeList = scopeTable.get(ident);
		
		if (decList == null || scopeList == null) {
			return null;
		}
		
		// Scan for entry with scope number closest to the top of the scope stack
		for (int i = scopeStack.size()-1; i >= 0; i--) {
			for (int j = scopeList.size()-1; j >= 0; j--) {
				if (scopeStack.get(i) == scopeList.get(j)) {
					return decList.get(j);
				}
			}
		}
		return null;
	}
	
	public int lookupScope(String ident) {
		// return null if no declaration exists 
		if (symbolTable.isEmpty()) {
			return -1;
		}
		
		ArrayList<Dec> decList = symbolTable.get(ident);
		ArrayList<Integer> scopeList = scopeTable.get(ident);
		
		if (decList == null || scopeList == null) {
			return -1;
		}
		
		// Scan for entry with scope number closest to the top of the scope stack
		for (int i = scopeStack.size()-1; i >= 0; i--) {
			for (int j = scopeList.size()-1; j >= 0; j--) {
				if (scopeStack.get(i) == scopeList.get(j)) {
					return scopeList.get(j);
				}
			}
		}
		return -1;
	}

	@Override
	public String toString() {
		String str = "";
		str = "Current Scope: " + currentScope + "\n"; 
		str = str + "Next Scope: " + nextScope + "\n";
		String scopeStackStr = "Scope Stack: \n";
		for (int i = 0; i < scopeStack.size(); i++) {
			scopeStackStr = scopeStackStr + scopeStack.get(i) + " ";
		}
		str = str + scopeStackStr + "\n";
		
		String scopeTableStr = "Scope Table: \n";
		for (Entry<String, ArrayList<Integer>> entry : scopeTable.entrySet()) {
			scopeTableStr = scopeTableStr + entry.getKey() + " ";
			for (Integer i : entry.getValue()) {
				scopeTableStr = scopeTableStr + i + " ";
			}
			scopeTableStr = scopeTableStr + "\n";
		}
		str = str + scopeTableStr + "\n";
		
		String symbolTableStr = "Symbol Table: \n";
		for (Entry<String, ArrayList<Dec>> entry : symbolTable.entrySet()) {
			symbolTableStr = symbolTableStr + entry.getKey() + " ";
			for (Dec d : entry.getValue()) {
				symbolTableStr = symbolTableStr + d.firstToken.get_TypeName() + " " + d.firstToken.getText() + " ";
			}
			symbolTableStr = symbolTableStr + "\n";
		}
		str = str + symbolTableStr + "\n";
		return str;
	}
}
