package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;

public abstract class Chain extends Statement {
	TypeName typeName;
	boolean saveExpr = false;
	
	public Chain(Token firstToken) {
		super(firstToken);
	}
	
	public TypeName get_TypeName() {
		return typeName;
	}
	
	public void set_TypeName(TypeName pTypeName) {
		typeName = pTypeName;
	}
	
	public boolean getSave() {
		return saveExpr;
	}
	
	public void setSave(boolean s) {
		saveExpr = s;
	}

}
