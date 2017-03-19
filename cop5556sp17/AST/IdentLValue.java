package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;

public class IdentLValue extends ASTNode {
	Dec dec;
	
	public IdentLValue(Token firstToken) {
		super(firstToken);
	}
	
	@Override
	public String toString() {
		return "IdentLValue [firstToken=" + firstToken + "]";
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIdentLValue(this,arg);
	}

	public String getText() {
		return firstToken.getText();
	}
	
	public Dec get_Dec() {
		return dec;
	}
	
	public void set_Dec(Dec pDec) {
		dec = pDec;
	}

}
