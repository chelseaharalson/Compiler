package cop5556sp17.AST;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;

public abstract class ChainElem extends Chain {
	
	Kind arrowKind = Kind.ARROW;

	public ChainElem(Token firstToken) {
		super(firstToken);
	}
	
	public Kind getArrowKind() {
		return arrowKind;
	}
	
	public void setArrowKind(Kind arrKind) {
		arrowKind = arrKind;
	}

}
