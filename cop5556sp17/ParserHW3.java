package cop5556sp17;

import cop5556sp17.AST.*;
import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;
import java.util.ArrayList;
import java.util.List;

import cop5556sp17.Scanner.Token;

public class ParserHW3 {

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

	ParserHW3(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * parse the input using tokens from the scanner.
	 * Check for EOF (i.e. no trailing junk) when finished
	 * 
	 * @throws SyntaxException
	 */
	ASTNode parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
	}

	// expression ∷= term ( relOp term)*
	// Expression ∷= IdentExpression | IntLitExpression | BooleanLitExpression | ConstantExpression | BinaryExpression
	Expression expression() throws SyntaxException {
		Expression e0 = null;
		Expression e1 = null;
		Kind kind = t.kind;
		Token startToken = t;
		if (isTerm(kind)) {
			e0 = term();
			kind = t.kind;
			while (!kind.equals(EOF)) {
				if (relOp(kind) || weakOp(kind) || strongOp(kind)) {
					Token op = t;
					consume();
					kind = t.kind;
					if (isTerm(kind)) {
						e1 = term();
						kind = t.kind;
						e0 = new BinaryExpression(startToken, e0, op, e1);
					}
					else {
						throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
								+ t.getLinePos().posInLine + "; Expected term but found " + kind);
					}
				}
				else {
					break;
				}
			}
		}
		else {
			throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
				+ t.getLinePos().posInLine + "; Reached end of file, but shouldn't have");
		}
		return e0;
	}

	// term ∷= elem ( weakOp  elem)*
	Expression term() throws SyntaxException {
		Expression e0 = null;
		Expression e1 = null;
		Kind kind = t.kind;
		Token startToken = t;
		if (isElem(kind)) {
			e0 = elem();
			kind = t.kind;
			while (!kind.equals(EOF)) {
				if (weakOp(kind)) {
					Token op = t;
					consume();
					kind = t.kind;
					if (isElem(kind)) {
						e1 = elem();
						kind = t.kind;
						e0 = new BinaryExpression(startToken, e0, op, e1);
					}
					else {
						throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
								+ t.getLinePos().posInLine + "; Expected elem but found " + kind);
					}
				}
				else {
					break;
				}
			}
			return e0;
		}
		else {
			throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
				+ t.getLinePos().posInLine + "; Expected elem but found " + kind);
		}
	}

	// elem ∷= factor ( strongOp factor)*
	Expression elem() throws SyntaxException {
		Expression e0 = null;
		Expression e1 = null;
		Kind kind = t.kind;
		Token startToken = t;
		if (isFactor(kind)) {
			e0 = factor();
			kind = t.kind;
			while (!kind.equals(EOF)) {
				if (strongOp(kind)) {
					Token op = t;
					consume();
					kind = t.kind;
					if (isFactor(kind)) {
						e1 = factor();
						kind = t.kind;
						e0 = new BinaryExpression(startToken, e0, op, e1);
					}
					else {
						throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
								+ t.getLinePos().posInLine + "; Expected factor but found " + kind);
					}
				}
				else {
					return e0;
				}
			}
		}
		else {
			throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
				+ t.getLinePos().posInLine + "; Expected factor but found " + kind);
		}
		return e0;
	}

	// Expression ∷= IdentExpression | IntLitExpression | BooleanLitExpression | ConstantExpression | BinaryExpression
	Expression factor() throws SyntaxException {
		Expression exp = null;
		Kind kind = t.kind;
		switch (kind) {
		case IDENT: {
			exp = new IdentExpression(t);
			consume();
		}
			break;
		case INT_LIT: {
			exp = new IntLitExpression(t);
			consume();
		}
			break;
		case KW_TRUE:
		case KW_FALSE: {
			exp = new BooleanLitExpression(t);
			consume();
		}
			break;
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: {
			exp = new ConstantExpression(t);
			consume();
		}
			break;
		case LPAREN: {
			consume();
			exp = expression();
			match(RPAREN);
		}
			break;
		default:
			throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
					+ t.getLinePos().posInLine + "; Expected factor but found " + kind);
		}
		return exp;
	}

	// block ::= { ( dec | statement) * }
	// statement ::=   OP_SLEEP expression ; | whileStatement | ifStatement | chain ; | assign ;
	// Block ∷= List<Dec>  List<Statement>
	Block block() throws SyntaxException {
		Token startToken = t;
		ArrayList<Dec> decList = new ArrayList<Dec>();
		ArrayList<Statement> statementList = new ArrayList<Statement>();
		Kind kind = t.kind;
		if (!kind.equals(LBRACE)) {
			throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
					+ t.getLinePos().posInLine + "; Expected left brace but found " + kind);
		}
		consume();
		while (!kind.equals(EOF)) {
			kind = t.kind;
			if (isDec(kind)) {
				decList.add(dec());
			}
			else if (isStatement(kind)) {
				statementList.add(statement());
			}
			else if (kind.equals(RBRACE)) {
				consume();
				return new Block(startToken, decList, statementList);
			}
			else {
				throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
						+ t.getLinePos().posInLine + "; Expected right brace but found " + kind);
			}
		}
		
		throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
				+ t.getLinePos().posInLine + "; Expected right brace but found " + kind);
	}

	// program ::=  IDENT block
	// program ::=  IDENT param_dec ( , param_dec )*   block
	// Program ∷= List<ParamDec> Block
	Program program() throws SyntaxException {
		ArrayList<ParamDec> paramDecList = new ArrayList<ParamDec>();
		Token startToken = t;
		Block b = null;
		Kind kind = t.kind;
		if (kind.equals(IDENT)) {
			consume();
			kind = t.kind;
			if (isParamDec(kind)) {
				paramDecList.add(paramDec());
				kind = t.kind;
				if (isBlock(kind)) {
					b = block();
				}
				else if (kind.equals(COMMA)) {
					while (!kind.equals(EOF)) {
						consume();
						paramDecList.add(paramDec());
						kind = t.kind;
						if (isBlock(kind)) {
							return new Program(startToken, paramDecList, block());
						}
						else if (!kind.equals(COMMA)) {
							throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
									+ t.getLinePos().posInLine + "; Expected comma or block but found " + kind);
						}
					}
					throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
							+ t.getLinePos().posInLine + "; Reached end of file, but shouldn't have");
				}
				else {
					throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
							+ t.getLinePos().posInLine + "; Expected comma or block but found " + kind);
				}
			}
			else if (isBlock(kind)) {
				b = block();
			}
			else {
				throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
						+ t.getLinePos().posInLine + "; Expected block or param_dec but found " + kind);
			}
		}
		else {
			throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
					+ t.getLinePos().posInLine + "; Expected IDENT but found " + kind);
		}
		return new Program(startToken, paramDecList, b);
	}

	// ParamDec ∷= type ident
	ParamDec paramDec() throws SyntaxException {
		Token startToken = t;
		match(KW_INTEGER, KW_BOOLEAN, KW_FILE, KW_URL);
        Token identToken = match(IDENT);
		return new ParamDec(startToken, identToken);
	}

	// Dec ∷= type ident
	Dec dec() throws SyntaxException {
		Token startToken = t;
		match(KW_INTEGER, KW_BOOLEAN, KW_IMAGE, KW_FRAME);
        Token identToken = match(IDENT);
		return new Dec(startToken, identToken);
	}

	WhileStatement whileStatement() throws SyntaxException {
		Token startToken = t;
		match(KW_WHILE);
		Kind kind = t.kind;
		if (kind.equals(LPAREN)) {
			consume();
			Expression e = expression();
			kind = t.kind;
			if (kind.equals(RPAREN)) {
				consume();
				return new WhileStatement(startToken, e, block());
			}
			else {
				throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
						+ t.getLinePos().posInLine + "; Expected right parenthesis but found " + kind);
			}
		}
		else {
			throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
					+ t.getLinePos().posInLine + "; Expected left parenthesis but found " + kind);
		}
	}
	
	IfStatement ifStatement() throws SyntaxException {
		Token startToken = t;
		match(KW_IF);
		Kind kind = t.kind;
		if (kind.equals(LPAREN)) {
			consume();
			Expression e = expression();
			kind = t.kind;
			if (kind.equals(RPAREN)) {
				consume();
				return new IfStatement(startToken, e, block());
			}
			else {
				throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
						+ t.getLinePos().posInLine + "; Expected right parenthesis but found " + kind);
			}
		}
		else {
			throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
					+ t.getLinePos().posInLine + "; Expected left parenthesis but found " + kind);
		}
	}

	// statement ::=   OP_SLEEP expression ; | whileStatement | ifStatement | chain ; | assign ;
	// Statement ∷= SleepStatement | WhileStatement | IfStatement | Chain | AssignmentStatement
	Statement statement() throws SyntaxException {
		Kind kind = t.kind;
		Token startToken = t;
		if (kind.equals(OP_SLEEP)) {
			consume();
			Expression e = expression();
			kind = t.kind;
			if (kind.equals(SEMI)) {
				consume();
				return new SleepStatement(startToken, e);
			}
			else {
				throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
						+ t.getLinePos().posInLine + "; Expected semicolon but found " + kind);
			}
		}
		else if (isWhileStatement(kind)) {
			return whileStatement();
		}
		else if (isIfStatement(kind)) {
			return ifStatement();
		}
		else if (isAssign(kind)) {
			AssignmentStatement a = assign();
			kind = t.kind;
			if (kind.equals(SEMI)) {
				consume();
				return a;
			}
			else {
				throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
						+ t.getLinePos().posInLine + "; Expected semicolon but found " + kind);
			}
		}
		else if (isChain(kind)) {
			Chain c = chain();
			kind = t.kind;
			if (kind.equals(SEMI)) {
				consume();
				return c;
			}
			else {
				throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
						+ t.getLinePos().posInLine + "; Expected semicolon but found " + kind);
			}
		}
		else {
			throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
					+ t.getLinePos().posInLine + "; Expected OP_SLEEP expression, whileStatement, ifStatement, chain, or assign"
							+ " but found " + kind);
		}
	}
	
	// assign ::= IDENT ASSIGN expression
	// AssignmentStatement ∷= IdentLValue Expression
	// IdentLValue ∷= ident
	AssignmentStatement assign() throws SyntaxException {
		Token startToken = t;
		IdentLValue lval = new IdentLValue(match(IDENT));
		match(ASSIGN);
		Kind kind = t.kind;
		if (isExpression(kind)) {
			Expression e = expression();
			return new AssignmentStatement(startToken, lval, e);
		}
		else {
			throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
					+ t.getLinePos().posInLine + "; Expected expression but found " + kind);
		}
	}

	// chain ::=  chainElem arrowOp chainElem ( arrowOp  chainElem)*
	// --------
	// Chain ∷= ChainElem | BinaryChain
	// BinaryChain ∷= Chain (arrow | bararrow)  ChainElem
	// ChainElem ::= IdentChain | FilterOpChain | FrameOpChain | ImageOpChain
	Chain chain() throws SyntaxException {
		Token startToken = t;
		Kind kind = t.kind;
		Chain c1 = null;
		ChainElem c2 = null;
		BinaryChain bc = null;
		if (isChainElem(kind)) {
			c1 = chainElem();
			kind = t.kind;
			if (arrowOp(kind)) {
				Token arrowToken = t;
				consume();
				kind = t.kind;
				if (isChainElem(kind)) {
					c2 = chainElem();
					bc = new BinaryChain(startToken, c1, arrowToken, c2);
					startToken = t;
					kind = t.kind;
					while (!kind.equals(EOF)) {
						if (arrowOp(kind)) {
							arrowToken = t;
							// BinaryChain(Token firstToken, Chain e0, Token arrow, ChainElem e1)
							consume();
							kind = t.kind;
							if (isChainElem(kind)) {
								c2 = chainElem();
								bc = new BinaryChain(startToken, bc, arrowToken, c2);
								startToken = t;
								kind = t.kind;
							}
							else {
								throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
										+ t.getLinePos().posInLine + "; Expected chainElem but found " + kind);
							}
						}
						else {
							break;
						}
					}
				}
				else {
					throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
							+ t.getLinePos().posInLine + "; Expected chainElem but found " + kind);
				}
			}
			else {
				throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
						+ t.getLinePos().posInLine + "; Expected arrowOp but found " + kind);
			}
		}
		else {
			throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
					+ t.getLinePos().posInLine + "; Expected chainElem but found " + kind);
		}
		return bc;
	}
	
	// chainElem ::= IDENT | filterOp arg | frameOp arg | imageOp arg
	// ChainElem ::= IdentChain | FilterOpChain | FrameOpChain | ImageOpChain
	ChainElem chainElem() throws SyntaxException {
		Kind kind = t.kind;
		Token startToken = t;
		if (kind.equals(IDENT)) {
			// IdentChain ∷= ident
			return new IdentChain(match(IDENT));
		}
		else if (filterOp(kind)) {
			Tuple listExpressionTuple = null;
			consume();
			kind = t.kind;
			if (kind.equals(LPAREN)) {
				listExpressionTuple = arg();
			}
			return new FilterOpChain(startToken, listExpressionTuple);
		}
		else if (frameOp(kind)) {
			Tuple listExpressionTuple = null;
			consume();
			kind = t.kind;
			if (kind.equals(LPAREN)) {
				listExpressionTuple = arg();
			}
			return new FrameOpChain(startToken, listExpressionTuple);
		}
		else if (imageOp(kind)) {
			Tuple listExpressionTuple = null;
			consume();
			kind = t.kind;
			if (kind.equals(LPAREN)) {
				listExpressionTuple = arg();
			}
			return new ImageOpChain(startToken, listExpressionTuple);
		}
		else {
			throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
					+ t.getLinePos().posInLine + "; Expected IDENT or OP but found " + kind);
		}
	}

	// arg ::= ε | ( expression (   ,expression)* )
	Tuple arg() throws SyntaxException {
		List<Expression> expressionList = new ArrayList<Expression>();
		Kind kind = t.kind;
		Token startToken = t;
		if (kind.equals(LPAREN)) {
			consume();
			expressionList.add(expression());
			kind = t.kind;
			if (kind.equals(RPAREN)) {
				consume();
				return new Tuple(startToken, expressionList);
			}
			else if (kind.equals(COMMA)) {
				while (!kind.equals(EOF)) {
					consume();
					expressionList.add(expression());
					kind = t.kind;
					if (kind.equals(RPAREN)) {
						consume();
						return new Tuple(startToken, expressionList);
					}
					else if (!kind.equals(COMMA)) {
						throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
								+ t.getLinePos().posInLine + "; Expected comma OR right parenthesis but found " + kind);
					}
				}
			}
			else {
				throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
						+ t.getLinePos().posInLine + "; Expected comma OR right parenthesis but found " + kind);
			}
		}
		return new Tuple(startToken, expressionList);
		/*else {
			throw new SyntaxException("Line: " + t.getLinePos().line + " and column: " 
				+ t.getLinePos().posInLine + "; Reached end of file, but shouldn't have");
		}*/
	}
	
	// relOp ∷=  LT | LE | GT | GE | EQUAL | NOTEQUAL
	public boolean relOp(Kind kind) {	
		switch (kind) {
			case LT: case LE: case GT: case GE: case EQUAL: case NOTEQUAL: 
				return true;
		}
		return false;
	}
	
	// weakOp  ∷= PLUS | MINUS | OR   
	public boolean weakOp(Kind kind) {
		switch (kind) {
		case PLUS: case MINUS: case OR: 
			return true;
		}
		return false;
	}
	
	// strongOp ∷= TIMES | DIV | AND | MOD 
	public boolean strongOp(Kind kind) {
		switch (kind) {
		case TIMES: case DIV: case AND: case MOD: 
			return true;
		}
		return false;
	}
	
	// arrowOp ∷= ARROW   |   BARARROW
	public boolean arrowOp(Kind kind) {
		switch (kind) {
		case ARROW: case BARARROW: 
			return true;
		}
		return false;
	}
	
	// filterOp ::= KW_BLUR |KW_GRAY | KW_CONVOLVE
	public boolean filterOp(Kind kind) {
		switch (kind) {
		case OP_BLUR: case OP_GRAY: case OP_CONVOLVE:  
			return true;
		}
		return false;
	}
	
	// frameOp ::= KW_SHOW | KW_HIDE | KW_MOVE | KW_XLOC |KW_YLOC
	public boolean frameOp(Kind kind) {
		switch (kind) {
		case KW_SHOW: case KW_HIDE: case KW_MOVE: case KW_XLOC: case KW_YLOC: 
			return true;
		}
		return false;
	}
	
	// imageOp ::= KW_WIDTH |KW_HEIGHT | KW_SCALE
	public boolean imageOp(Kind kind) {
		switch (kind) {
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
		else if (isAssign(kind)) {
			return true;
		}
		else if (isChain(kind)) {
			return true;
		}
		return false;
	}
	
	// block ::= { ( dec | statement) * }
	public boolean isBlock(Kind kind) {
		if (kind.equals(LBRACE)) {
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
		if (isChainElem(kind)) {
			return true;
		}
		return false;
	}
	
	// param_dec ::= ( KW_URL | KW_FILE | KW_INTEGER | KW_BOOLEAN)   IDENT
	public boolean isParamDec(Kind kind) {
		if (kind.equals(KW_URL) || kind.equals(KW_FILE) || kind.equals(KW_INTEGER) || kind.equals(KW_BOOLEAN)) {
			return true;
		}
		return false;
	}
	
	public boolean isDec(Kind kind) {
		if (kind.equals(KW_INTEGER) || kind.equals(KW_BOOLEAN) 
					|| kind.equals(KW_IMAGE) || kind.equals(KW_FRAME)) {
			return true;
		}
		return false;
	}
	
	public boolean isAssign(Kind kind) {
		if (kind.equals(IDENT)) {
			Token nextTok = scanner.peek();
			if (nextTok != null) {
				if (nextTok.kind.equals(ASSIGN)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isFactor(Kind kind) {
		if (kind.equals(IDENT) || kind.equals(INT_LIT) || kind.equals(KW_TRUE) || 
				kind.equals(KW_FALSE) || kind.equals(KW_SCREENHEIGHT) || 
				kind.equals(KW_SCREENWIDTH) || kind.equals(LPAREN) ) {
					return true;
		}
		return false;
	}
	
	public boolean isExpression(Kind kind) {
		if (isTerm(kind)) {
			return true;
		}
		return false;
	}
	
	public boolean isTerm(Kind kind) {
		if (isElem(kind)) {
			return true;
		}
		return false;
	}
	
	public boolean isElem(Kind kind) {
		if (isFactor(kind)) {
			return true;
		}
		return false;
	}
	
	public boolean isChainElem(Kind kind) {
		if (kind.equals(IDENT) || filterOp(kind) || frameOp(kind) || imageOp(kind)) {
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
		throw new SyntaxException("saw " + t.kind + " expected " + kind);
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
		for (Kind k : kinds) {
			if (t.kind.equals(k)) {
				consume();
				return t;
			}
		}
		throw new SyntaxException("Error with token " + t + " and kind " + t.kind);
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
