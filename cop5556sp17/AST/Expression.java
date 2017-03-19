package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;

public abstract class Expression extends ASTNode {
	TypeName typeName;
	
	protected Expression(Token firstToken) {
		super(firstToken);
	}

	@Override
	abstract public Object visit(ASTVisitor v, Object arg) throws Exception;
	
	public TypeName get_TypeName() {
		return typeName;
	}
	
	public void set_TypeName(TypeName pTypeName) {
		typeName = pTypeName;
	}

}
