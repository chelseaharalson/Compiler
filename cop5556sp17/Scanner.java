package cop5556sp17;

import static cop5556sp17.AST.Type.TypeName.BOOLEAN;
import static cop5556sp17.AST.Type.TypeName.FILE;
import static cop5556sp17.AST.Type.TypeName.FRAME;
import static cop5556sp17.AST.Type.TypeName.IMAGE;
import static cop5556sp17.AST.Type.TypeName.INTEGER;
import static cop5556sp17.AST.Type.TypeName.URL;
import static cop5556sp17.Scanner.Kind.KW_BOOLEAN;
import static cop5556sp17.Scanner.Kind.KW_FILE;
import static cop5556sp17.Scanner.Kind.KW_FRAME;
import static cop5556sp17.Scanner.Kind.KW_IMAGE;
import static cop5556sp17.Scanner.Kind.KW_INTEGER;
import static cop5556sp17.Scanner.Kind.KW_URL;

import java.util.ArrayList;

import cop5556sp17.AST.Type.TypeName;

// Chelsea Metcalf

public class Scanner {
	/**
	 * Kind enum
	 */
	
	public static enum Kind {
		IDENT(""), 
		INT_LIT(""), 
		KW_INTEGER("integer"), 
		KW_BOOLEAN("boolean"), 
		KW_IMAGE("image"), 
		KW_URL("url"), 
		KW_FILE("file"), 
		KW_FRAME("frame"), 
		KW_WHILE("while"), 
		KW_IF("if"), 
		KW_TRUE("true"), 
		KW_FALSE("false"), 
		SEMI(";"), 
		COMMA(","), 
		LPAREN("("), 
		RPAREN(")"), 
		LBRACE("{"), 
		RBRACE("}"), 
		ARROW("->"), 
		BARARROW("|->"), 
		OR("|"), 
		AND("&"), 
		EQUAL("=="), 
		NOTEQUAL("!="), 
		LT("<"), 
		GT(">"), 
		LE("<="), 
		GE(">="), 
		PLUS("+"), 
		MINUS("-"), 
		TIMES("*"), 
		DIV("/"), 
		MOD("%"), 
		NOT("!"), 
		ASSIGN("<-"), 
		OP_BLUR("blur"), 
		OP_GRAY("gray"), 
		OP_CONVOLVE("convolve"), 
		KW_SCREENHEIGHT("screenheight"), 
		KW_SCREENWIDTH("screenwidth"), 
		OP_WIDTH("width"), 
		OP_HEIGHT("height"), 
		KW_XLOC("xloc"), 
		KW_YLOC("yloc"), 
		KW_HIDE("hide"), 
		KW_SHOW("show"), 
		KW_MOVE("move"), 
		OP_SLEEP("sleep"), 
		KW_SCALE("scale"), 
		EOF("eof");

		Kind(String text) {
			this.text = text;
		}

		final String text;

		String getText() {
			return text;
		}
	}
/**
 * Thrown by Scanner when an illegal character is encountered
 */
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}
	
	/**
	 * Thrown by Scanner when an int literal is not a value that can be represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
	public IllegalNumberException(String message){
		super(message);
		}
	}
	

	/**
	 * Holds the line and position in the line of a token.
	 */
	static class LinePos {
		public final int line;
		public final int posInLine;
		
		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}
		

	

	public class Token {
		public final Kind kind;
		public final int pos;  //position in input array
		public final int length;  

		//returns the text of this Token
		public String getText() {
			if (kind.equals(kind.EOF)) {
				return "eof";
			}
			return chars.substring(pos,pos+length);
		}
		
		//returns a LinePos object representing the line and column of this Token
		LinePos getLinePos(){
			int lineNum = 0;
			int col = 0;
			for (int i = 0; i < pos; i++) {
				col++;
				if (chars.charAt(i) == '\n') {
					lineNum++;
					col = 0;
				}
			}
			LinePos lp = new LinePos(lineNum, col);
			return lp;
		}

		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}
		
		public TypeName get_TypeName() {
			if (kind == KW_BOOLEAN) {
				return BOOLEAN;
			}
			else if (kind == KW_INTEGER) {
				return INTEGER;
			}
			else if (kind == KW_URL) {
				return URL;
			}
			else if (kind == KW_FILE) {
				return FILE;
			}
			else if (kind == KW_FRAME) {
				return FRAME;
			}
			else if (kind == KW_IMAGE) {
				return IMAGE;
			}
			return null;
		}

		/** 
		 * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
		 * Note that the validity of the input should have been checked when the Token was created.
		 * So the exception should never be thrown.
		 * 
		 * @return  int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		public int intVal() throws NumberFormatException{
			assert kind == Kind.INT_LIT : "Cannot get value of a non-digit token.";
			return Integer.valueOf(getText());
		}
		
		/* Addition for HW3 */
		private Scanner getOuterType() {
			 return Scanner.this;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + length;
			result = prime * result + pos;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Token)) {
				return false;
			}
			Token other = (Token) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (kind != other.kind) {
				return false;
			}
			if (length != other.length) {
				return false;
			}
			if (pos != other.pos) {
				return false;
			}
			return true;
		}
		
	}

	 


	Scanner(String chars) {
		this.chars = chars;
		tokens = new ArrayList<Token>();
	}


	
	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 * 
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException {
		//int pos = 0; 
		//TODO IMPLEMENT THIS!!!!
		pos = 0;
		Kind kind = null;
		while (!eof()) {
			boolean isAComment = false;
			skipWhitespace();
			if (eof()) {
				break;
			}
			int oldPos = pos;
			kind = this.ident();
			if (kind == null) {
				kind = this.intLiteral();
			}
			if (kind == null) {
				kind = this.operator();
			}
			if (kind == null) {
				kind = this.separator();
			}
			if (kind == null) {
				// comment check
				isAComment = isComment();
				if (isAComment == true) {
					comment();
					//if (!eof()) System.out.println("+++CHAR: " + chars.charAt(pos));
				}
			}
			if (kind != null) {
				//System.out.println("@@@ KIND: " + kind.getText());
				tokens.add(new Token(kind, oldPos, (pos - oldPos)));
			}
			else if (kind == null && !eof() && isAComment == false) {
				throw new IllegalCharException("Illegal character: " + chars.charAt(pos));
			}
		}
		
		// Add EOF token
		tokens.add(new Token(Kind.EOF,pos,0));
		return this;  
	}
	
	public Kind separator() {
		Kind kind = null;
		switch (chars.charAt(pos)) {
			case ';': kind = Kind.SEMI;
				break;
			case ',': kind = Kind.COMMA;
				break;
			case '(': kind = Kind.LPAREN;
				break;
			case ')': kind = Kind.RPAREN;
				break;
			case '{': kind = Kind.LBRACE;
				break;
			case '}': kind = Kind.RBRACE;
				break;
		}
		
		// Increment pos when a separator is found
		if (kind != null) {
			pos++;
		}
		return kind;
	}
	
	public Kind operator() throws IllegalCharException {
		Kind kind = null;
		switch (chars.charAt(pos)) {
			// Need to check for | and |->
			case '|': kind = Kind.OR;
				pos++;
				if (!eof() && chars.charAt(pos) == '-') {
					pos++;
					if (!eof() && chars.charAt(pos) == '>') {
						kind = Kind.BARARROW;
					}
					else {
						pos = pos - 2;
					}
				}
				else {
					pos--;
				}
				break;
			case '&': kind = Kind.AND;
				break;
			// Need to check for: == 
			case '=': 
				pos++;
				if (!eof() && chars.charAt(pos) == '=') {
					kind = Kind.EQUAL;
				}
				else {
					pos--;
					throw new IllegalCharException("Expected ==, got =");
				}
				break;
			// Need to check for: ! and !=
			case '!': kind = Kind.NOT;
				pos++;
				if (!eof() && chars.charAt(pos) == '=') {
					kind = Kind.NOTEQUAL;
				}
				else {
					pos--;
				}
				break;
			// Need to check for: < and <= and <-
			case '<': kind = Kind.LT;
				pos++;
				if (!eof() && chars.charAt(pos) == '=') {
					kind = Kind.LE;
				}
				else if (!eof() && chars.charAt(pos) == '-') {
					kind = Kind.ASSIGN;
				}
				else {
					pos--;
				}
				break;
			// Need to check for: > and >=
			case '>': kind = Kind.GT;
				pos++;
				if (!eof() && chars.charAt(pos) == '=') {
					kind = Kind.GE;
				}
				else {
					pos--;
				}
				break;
			case '+': kind = Kind.PLUS;
				break;
			// Need to check for: - and ->
			case '-': kind = Kind.MINUS;
				pos++;
				if (!eof() && chars.charAt(pos) == '>') {
					kind = Kind.ARROW;
				}
				else {
					pos--;
				}
				break;
			case '*': kind = Kind.TIMES;
				break;
			// Need to check for / and make sure there is not a leading * 
			case '/': 
				pos++;
				if (eof() || !(!eof() && chars.charAt(pos) == '*')) {
					kind = Kind.DIV;
				}
				pos--;
				break;
			case '%': kind = Kind.MOD;
				break;
		}
		// Increment pos when an operator is found
		if (kind != null) {
			pos++;
		}
		return kind;
	}
	
	static public Kind keyword(String str) {
		if (str.equals("integer")) {
			return Kind.KW_INTEGER;
		}
		else if (str.equals("boolean")) {
			return Kind.KW_BOOLEAN;
		}
		else if (str.equals("image")) {
			return Kind.KW_IMAGE;
		}
		else if (str.equals("url")) {
			return Kind.KW_URL;
		}
		else if (str.equals("file")) {
			return Kind.KW_FILE;
		}
		else if (str.equals("frame")) {
			return Kind.KW_FRAME;
		}
		else if (str.equals("while")) {
			return Kind.KW_WHILE;
		}
		else if (str.equals("if")) {
			return Kind.KW_IF;
		}
		else if (str.equals("sleep")) {
			return Kind.OP_SLEEP;
		}
		else if (str.equals("screenheight")) {
			return Kind.KW_SCREENHEIGHT;
		}
		else if (str.equals("screenwidth")) {
			return Kind.KW_SCREENWIDTH;
		}
		else {
			return null;
		}
	}
	
	static public Kind filterOpKeyword(String str) {
		if (str.equals("gray")) {
			return Kind.OP_GRAY;
		}
		else if (str.equals("convolve")) {
			return Kind.OP_CONVOLVE;
		}
		else if (str.equals("blur")) {
			return Kind.OP_BLUR;
		}
		else if (str.equals("scale")) {
			return Kind.KW_SCALE;
		}
		else {
			return null;
		}
	}
	
	static public Kind imageOpKeyword(String str) {
		if (str.equals("width")) {
			return Kind.OP_WIDTH;
		}
		else if (str.equals("height")) {
			return Kind.OP_HEIGHT;
		}
		else {
			return null;
		}
	}
	
	static public Kind frameOpKeyword(String str) {
		if (str.equals("xloc")) {
			return Kind.KW_XLOC;
		}
		else if (str.equals("yloc")) {
			return Kind.KW_YLOC;
		}
		else if (str.equals("hide")) {
			return Kind.KW_HIDE;
		}
		else if (str.equals("show")) {
			return Kind.KW_SHOW;
		}
		else if (str.equals("move")) {
			return Kind.KW_MOVE;
		}
		else {
			return null;
		}
	}
	
	public boolean isComment() {
		boolean isAComment = false;
		if (chars.charAt(pos) == '/') {
			if (chars.charAt(pos+1) == '*') {
				isAComment = true;
			}
		}
		//System.out.println("isAComment: " + isAComment);
		return isAComment;
	}
	
	public void comment() throws IllegalCharException {
		boolean commentDone = false;
		if (chars.charAt(pos) == '/') {
			pos++;
			if (!eof() && chars.charAt(pos) == '*') {
				// Now inside a comment
				pos++;
				//System.out.println("Found beginning comment");
				while (!eof() && !commentDone) {
					//System.out.println("Looking at character " + chars.charAt(pos) + " at pos " + pos);
					// Check for end of comment
					if (chars.charAt(pos) == '*') {
						pos++;
						if (!eof() && chars.charAt(pos) == '/') {
							commentDone = true;
							pos++;
							return;
						}
					}
					else {
						pos++;
					}
				}
				if (eof()) {
					throw new IllegalCharException("Failed: IllegalCharException, no closing comment found.");
				}
			}
			else {
				pos--;
			}
		}
	}
	
	// Checks to see if pos is at an ident. If it is, return the kind of ident and increment pos.
	public Kind ident() {
		Kind kind = null;
		if (!eof() && Character.isJavaIdentifierStart(chars.charAt(pos))) {
			int oldPos = pos;
			kind = Kind.IDENT;
			pos++;
			// Check to see if the following chars are also an ident
			while (!eof() && Character.isJavaIdentifierPart(chars.charAt(pos))) {
				pos++;
			}
			// Check to see if the ident is a keyword or reserved
			String ident = String.copyValueOf(chars.toCharArray(), oldPos, (pos - oldPos));
			Kind temp = Scanner.reserved(ident);
			if (temp != null) {
				kind = temp;
			}
		}
		return kind;
	}
	
	// Returns if the scanner has reached the end of the file. The end of the file is reached when the current position 
	// goes past the last element in the charArr.
	public boolean eof() {
		return pos >= chars.length();
	}
	
	public void skipWhitespace() {
		while ((!eof()) && (Character.isWhitespace(chars.charAt(pos)) == true)) {
			pos++;
		}
	}
	
	// Returns the kind of string if str is a reserved keyword
	// If the string is not a reserved keyword, then the method returns null
	static public Kind reserved(String str) {
		Kind kind = keyword(str);
		if (kind == null) {
			kind = filterOpKeyword(str);
		}
		if (kind == null) {
			kind = imageOpKeyword(str);
		}
		if (kind == null) {
			kind = frameOpKeyword(str);
		}
		if (kind == null) {
			kind = booleanLiteral(str);
		}
		return kind;
	}
	
	static public Kind booleanLiteral(String str) {
		if (str.equals("true")) {
			return Kind.KW_TRUE;
		}
		if (str.equals("false")) {
			return Kind.KW_FALSE;
		}
		else {
			return null;
		}
	}
	
	public Kind intLiteral() throws IllegalNumberException {
		int oldPos = pos;
		Kind kind = null;
		if (chars.charAt(pos) == '0') {
			kind = Kind.INT_LIT;
			pos++;
		}
		else if (Character.isDigit(chars.charAt(pos))) {
			kind = Kind.INT_LIT;
			while (!eof() && Character.isDigit(chars.charAt(pos))) {
				pos++;
			}
		}
		else {
			return null;
		}
		String numStr = new String(chars.toCharArray(), oldPos, (pos - oldPos));
		try {
			Integer.valueOf(numStr);
		}
		catch (NumberFormatException ne) {
			throw new IllegalNumberException("Failed because IllegalNumberException");
		}
		return kind;
	}



	final ArrayList<Token> tokens;
	final String chars;
	int tokenNum;
	int pos;

	/*
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..  
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}
	
	/*
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */
	public Token peek(){
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum);		
	}

	

	/**
	 * Returns a LinePos object containing the line and position in line of the 
	 * given token.  
	 * 
	 * Line numbers start counting at 0
	 * 
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		//TODO IMPLEMENT THIS
		return t.getLinePos();
		//return null;
	}


}
