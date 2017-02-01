package cop5556sp17;

import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;
import cop5556sp17.Scanner.Token;

public class Parser {

	/**
	 * Exception to be thrown if a syntax error is detected in the input.
	 * You will want to provide a useful error message.
	 *
	 */
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}
	
	/**
	 * Useful during development to ensure unimplemented routines are
	 * not accidentally called during development.  Delete it when 
	 * the Parser is finished.
	 *
	 */
	@SuppressWarnings("serial")	
	public static class UnimplementedFeatureException extends RuntimeException {
		public UnimplementedFeatureException() {
			super();
		}
	}

	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * parse the input using tokens from the scanner.
	 * Check for EOF (i.e. no trailing junk) when finished
	 * 
	 * @throws SyntaxException
	 */
	void parse() throws SyntaxException {
		program();
		matchEOF();
		return;
	}

	// expression ∷= term ( relOp term)*
	void expression() throws SyntaxException {
		//TODO
		throw new UnimplementedFeatureException();
	}

	// term ∷= elem ( weakOp  elem)*
	void term() throws SyntaxException {
		//TODO
		throw new UnimplementedFeatureException();
	}

	// elem ∷= factor ( strongOp factor)*
	void elem() throws SyntaxException {
		//TODO
		throw new UnimplementedFeatureException();
	}

	void factor() throws SyntaxException {
		Kind kind = t.kind;
		switch (kind) {
		case IDENT: {
			consume();
		}
			break;
		case INT_LIT: {
			consume();
		}
			break;
		case KW_TRUE:
		case KW_FALSE: {
			consume();
		}
			break;
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: {
			consume();
		}
			break;
		case LPAREN: {
			consume();
			expression();
			match(RPAREN);
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("illegal factor");
		}
	}

	// block ::= { ( dec | statement) * }
	void block() throws SyntaxException {
		//TODO
		Kind kind = t.kind;
		if (!kind.equals(LBRACE)) {
			throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " + t.getLinePos().posInLine + "; Expected left brace but found " + kind);
		}
		consume();
		while (true) {
			if (kind.equals(KW_INTEGER) || kind.equals(KW_BOOLEAN) || kind.equals(KW_IMAGE) || kind.equals(KW_FRAME)) {
				consume();
				dec();
			}
			else if (kind.equals(OP_SLEEP)) {
				
			}
			else if (kind.equals(RBRACE)) {
				return;
			}
			else {
				throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " + t.getLinePos().posInLine + "; Expected right brace but found " + kind);
			}
		}
		//throw new UnimplementedFeatureException();
	}

	// program ::=  IDENT param_dec ( , param_dec )*   block
	void program() throws SyntaxException {
		//TODO
		throw new UnimplementedFeatureException();
	}

	// param_dec ::= ( KW_URL | KW_FILE | KW_INTEGER | KW_BOOEAN)   IDENT
	void paramDec() throws SyntaxException {
		//TODO
		throw new UnimplementedFeatureException();
	}

	// dec ::= (  KW_INTEGER | KW_BOOLEAN | KW_IMAGE | KW_FRAME)    IDENT
	void dec() throws SyntaxException {
		//TODO
		throw new UnimplementedFeatureException();
	}

	// statement ::=   OP_SLEEP expression ; | whileStatement | ifStatement | chain ; | assign ;
	void statement() throws SyntaxException {
		//TODO
		throw new UnimplementedFeatureException();
	}

	// chain ::=  chainElem arrowOp chainElem ( arrowOp  chainElem)*
	void chain() throws SyntaxException {
		//TODO
		throw new UnimplementedFeatureException();
	}

	// chainElem ::= IDENT | filterOp arg | frameOp arg | imageOp arg
	void chainElem() throws SyntaxException {
		//TODO
		throw new UnimplementedFeatureException();
	}

	// arg ::= ε | ( expression (   ,expression)* )
	void arg() throws SyntaxException {
		//TODO
		throw new UnimplementedFeatureException();
	}
	
	// relOp ∷=  LT | LE | GT | GE | EQUAL | NOTEQUAL
	public boolean relOp() {	
		switch (t.kind) {
			case LT: case LE: case GT: case GE: case EQUAL: case NOTEQUAL: 
				return true;
		}
		return false;
	}
	
	// weakOp  ∷= PLUS | MINUS | OR   
	public boolean weakOp() {
		switch (t.kind) {
		case PLUS: case MINUS: case OR: 
			return true;
		}
		return false;
	}
	
	// strongOp ∷= TIMES | DIV | AND | MOD 
	public boolean strongOp() {
		switch (t.kind) {
		case TIMES: case DIV: case AND: case MOD: 
			return true;
		}
		return false;
	}
	
	// arrowOp ∷= ARROW   |   BARARROW
	public boolean arrowOp() {
		switch (t.kind) {
		case ARROW: case BARARROW: 
			return true;
		}
		return false;
	}
	
	// filterOp ::= KW_BLUR |KW_GRAY | KW_CONVOLVE
	public boolean filterOp() {
		switch (t.kind) {
		case OP_BLUR: case OP_GRAY: case OP_CONVOLVE:  
			return true;
		}
		return false;
	}
	
	// frameOp ::= KW_SHOW | KW_HIDE | KW_MOVE | KW_XLOC |KW_YLOC
	public boolean frameOp() {
		switch (t.kind) {
		case KW_SHOW: case KW_HIDE: case KW_MOVE: case KW_XLOC: case KW_YLOC: 
			return true;
		}
		return false;
	}
	
	// imageOp ::= KW_WIDTH |KW_HEIGHT | KW_SCALE
	public boolean imageOp() {
		switch (t.kind) {
		case OP_WIDTH: case OP_HEIGHT: case KW_SCALE: 
			return true;
		}
		return false;
	}
	
	// statement ::=   OP_SLEEP expression ; | whileStatement | ifStatement | chain ; | assign ;
	public boolean isStatement(Kind kind) {
		if (kind.equals(OP_SLEEP)) {
			return true;
		}
		else if (isWhileStatement(kind)) {
			return true;
		}
		else if (isIfStatement(kind)) {
			return true;
		}
		return false;
	}
	
	public boolean isWhileStatement(Kind kind) {
		if (kind.equals(KW_WHILE)) {
			return true;
		}
		return false;
	}
	
	public boolean isIfStatement(Kind kind) {
		if (kind.equals(KW_IF)) {
			return true;
		}
		return false;
	}
	
	public boolean isChain(Kind kind) {
		if (kind.equals(KW_IF)) {
			return true;
		}
		return false;
	}
	
	public boolean isAssign(Kind kind) {
		if (kind.equals(IDENT)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.kind.equals(EOF)) {
		//if (t.isKind(EOF)) {
			return t;
		}
		throw new SyntaxException("expected EOF");
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 * 
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		if (t.kind.equals(kind)) {
		//if (t.isKind(kind)) {
			return consume();
		}
		throw new SyntaxException("saw " + t.kind + "expected " + kind);
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 * 
	 * * Precondition: for all given kinds, kind != EOF
	 * 
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind... kinds) throws SyntaxException {
		// TODO. Optional but handy
		for (Kind k : kinds) {
			if (t.kind.equals(k)) {
				consume();
				return t;
			}
		}
		throw new SyntaxException("Error with token " + t + " and kind " + t.kind);
		//return null; //replace this statement
	}

	/**
	 * Gets the next token and returns the consumed token.
	 * 
	 * Precondition: t.kind != EOF
	 * 
	 * @return
	 * 
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}

}
