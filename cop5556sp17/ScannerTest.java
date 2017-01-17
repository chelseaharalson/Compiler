package cop5556sp17;

//import static cop5556sp17.Scanner.Kind.SEMI;
import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;

public class ScannerTest {

	@Rule
    public ExpectedException thrown = ExpectedException.none();


	
	@Test
	public void testEmpty() throws IllegalCharException, IllegalNumberException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
	}

	@Test
	public void testSemiConcat() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = ";;;";
		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(SEMI, token.kind);
		assertEquals(0, token.pos);
		String text = SEMI.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//get the next token and check its kind, position, and contents
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(SEMI, token1.kind);
		assertEquals(1, token1.pos);
		assertEquals(text.length(), token1.length);
		assertEquals(text, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(SEMI, token2.kind);
		assertEquals(2, token2.pos);
		assertEquals(text.length(), token2.length);
		assertEquals(text, token2.getText());
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token3.kind);
	}
	
	
	/**
	 * This test illustrates how to check that the Scanner detects errors properly. 
	 * In this test, the input contains an int literal with a value that exceeds the range of an int.
	 * The scanner should detect this and throw and IllegalNumberException.
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testIntOverflowError() throws IllegalCharException, IllegalNumberException{
		String input = "99999999999999999";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalNumberException.class);
		scanner.scan();		
	}

//TODO  more tests
	
	@Test
	public void multiTokenTest() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = "integer tim == 5234;";
		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(KW_INTEGER, token.kind);
		assertEquals(0, token.pos);
		//String text = KW_INTEGER.getText();
		assertEquals(7, token.length);
		assertEquals("integer", token.getText());
		//get the next token and check its kind, position, and contents
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(IDENT, token1.kind);
		assertEquals(8, token1.pos);
		assertEquals(3, token1.length);
		assertEquals("tim", token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(EQUAL, token2.kind);
		assertEquals(12, token2.pos);
		assertEquals(2, token2.length);
		assertEquals("==", token2.getText());
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(INT_LIT, token3.kind);
		assertEquals(15, token3.pos);
		assertEquals(4, token3.length);
		assertEquals("5234", token3.getText());
		Scanner.Token token4 = scanner.nextToken();
		assertEquals(SEMI, token4.kind);
		assertEquals(19, token4.pos);
		assertEquals(1, token4.length);
		assertEquals(";", token4.getText());
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token end = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,end.kind);
	}
	
	@Test
	public void testIntLiteral() throws IllegalCharException, IllegalNumberException {
		Scanner scanner = new Scanner("2");
		assertEquals("\"2\" should return INT_LIT", INT_LIT, scanner.intLiteral());
        assertEquals("\"2\" should increment pos by 1", 1, scanner.pos);
        
        scanner = new Scanner("92921");
        assertEquals("\"92921\" should return INT_LIT", INT_LIT, scanner.intLiteral());
        assertEquals("\"92921\" should increment pos by 5", 5, scanner.pos);
        
        scanner = new Scanner("0923");
        assertEquals("\"0923\" should return INT_LIT", INT_LIT, scanner.intLiteral());
        assertEquals("\"0923\" should increment pos by 1", 1, scanner.pos);
        
        scanner = new Scanner("00");
        assertEquals("\"00\" should return INT_LIT", INT_LIT, scanner.intLiteral());
        assertEquals("\"00\" should increment pos by 1", 1, scanner.pos);
        
        scanner = new Scanner("1.234");
        assertEquals("\"1.234\" should return INT_LIT", INT_LIT, scanner.intLiteral());
        assertEquals("\"1.234\" should increment pos by 1", 1, scanner.pos);
        
        scanner = new Scanner("200,000");
        assertEquals("\"200,000\" should return INT_LIT", INT_LIT, scanner.intLiteral());
        assertEquals("\"200,000\" should increment pos by 3", 3, scanner.pos);
        
        scanner = new Scanner("-789");
        assertEquals("\"-789\" should return null", null, scanner.intLiteral());
        assertEquals("\"-789\" should not increment pos", 0, scanner.pos);
        
        scanner = new Scanner(".789");
        assertEquals("\".789\" should return null", null, scanner.intLiteral());
        assertEquals("\".789\" should NOT increment pos", 0, scanner.pos);
        
        scanner = new Scanner("c24");
        assertEquals("\"c24\" should return null", null, scanner.intLiteral());
        assertEquals("\"c24\" should NOT increment pos", 0, scanner.pos);
	}
	
	//@Test
	//public void testComment() throws IllegalCharException {
		//Scanner scanner = new Scanner("/* c */");
		//Scanner.Token token = scanner.nextToken();
		//scanner.comment();
		//scanner.isComment();
        //assertEquals("\"/* c */\" should increment pos by 7", 7, scanner.pos);
        //assertEquals("\"/*  */\" should increment the line counter by 0", 0, scanner.getLinePos(token));
	//}
	
	@Test
	public void testOperator() {
		Scanner scanner = new Scanner("/");
        assertEquals("\"/\" should return Kind.DIV", DIV, scanner.operator());
        assertEquals("\"/\" should increment pos by 1", 1, scanner.pos);
        
        scanner = new Scanner("|");
        assertEquals("\"|\" should return Kind.OR", OR, scanner.operator());
        assertEquals("\"|\" should increment pos by 1", 1, scanner.pos);
        
        scanner = new Scanner("&");
        assertEquals("\"&\" should return Kind.AND", AND, scanner.operator());
        assertEquals("\"&\" should increment pos by 1", 1, scanner.pos);
        
        scanner = new Scanner("!");
        assertEquals("\"!\" should return Kind.NOT", NOT, scanner.operator());
        assertEquals("\"!\" should increment pos by 1", 1, scanner.pos);
        
        scanner = new Scanner("!=");
        assertEquals("\"!=\" should return Kind.NOTEQUAL", NOTEQUAL, scanner.operator());
        assertEquals("\"!=\" should increment pos by 2", 2, scanner.pos);
        
        scanner = new Scanner("==");
        assertEquals("\"==\" should return Kind.EQUAL", EQUAL, scanner.operator());
        assertEquals("\"==\" should increment pos by 2", 2, scanner.pos);
        
        scanner = new Scanner("<");
        assertEquals("\"<\" should return Kind.LT", LT, scanner.operator());
        assertEquals("\"<\" should increment pos by 1", 1, scanner.pos);
        
        scanner = new Scanner(">");
        assertEquals("\">\" should return Kind.GT", GT, scanner.operator());
        assertEquals("\">\" should increment pos by 1", 1, scanner.pos);
        
        scanner = new Scanner("<=");
        assertEquals("\"<\" should return Kind.LE", LE, scanner.operator());
        assertEquals("\"<\" should increment pos by 2", 2, scanner.pos);
        
        scanner = new Scanner(">=");
        assertEquals("\">\" should return Kind.GE", GE, scanner.operator());
        assertEquals("\">\" should increment pos by 2", 2, scanner.pos);
        
        scanner = new Scanner("+");
        assertEquals("\"+\" should return Kind.PLUS", PLUS, scanner.operator());
        assertEquals("\"+\" should increment pos by 1", 1, scanner.pos);
        
        scanner = new Scanner("-");
        assertEquals("\"-\" should return Kind.MINUS", MINUS, scanner.operator());
        assertEquals("\"-\" should increment pos by 1", 1, scanner.pos);
        
        scanner = new Scanner("->");
        assertEquals("\"->\" should return Kind.ARROW", ARROW, scanner.operator());
        assertEquals("\"->\" should increment pos by 2", 2, scanner.pos);
        
        scanner = new Scanner("<-");
        assertEquals("\"<-\" should return Kind.ASSIGN", ASSIGN, scanner.operator());
        assertEquals("\"<-\" should increment pos by 2", 2, scanner.pos);
        
        scanner = new Scanner("|->");
        assertEquals("\"|->\" should return Kind.BARARROW", BARARROW, scanner.operator());
        assertEquals("\"|->\" should increment pos by 3", 3, scanner.pos);
        
        scanner = new Scanner("%");
        assertEquals("\"%\" should return Kind.MOD", MOD, scanner.operator());
        assertEquals("\"%\" should increment pos by 1", 1, scanner.pos);
        
        scanner = new Scanner("/*");
        assertEquals("\"/*\" should return null", null, scanner.operator());
        assertEquals("\"/*\" should not increment pos", 0, scanner.pos);
        
        scanner = new Scanner("=");
        assertEquals("\"=\" should return null", null, scanner.operator());
        assertEquals("\"=\" should not increment pos", 0, scanner.pos);
	}
	
	@Test
	public void testSeparator() {
		Scanner scanner = new Scanner(";");
		assertEquals("\";\" should return Kind.SEMI", SEMI, scanner.separator());
        assertEquals("\";\" should increment pos by 1", 1, scanner.pos);
        
        scanner = new Scanner(",");
		assertEquals("\",\" should return Kind.COMMA", COMMA, scanner.separator());
        assertEquals("\",\" should increment pos by 1", 1, scanner.pos);
        
        scanner = new Scanner("(");
		assertEquals("\";\" should return Kind.LPAREN", LPAREN, scanner.separator());
        assertEquals("\";\" should increment pos by 1", 1, scanner.pos);
        
        scanner = new Scanner(")");
		assertEquals("\")\" should return Kind.RPAREN", RPAREN, scanner.separator());
        assertEquals("\")\" should increment pos by 1", 1, scanner.pos);
        
        scanner = new Scanner("{");
		assertEquals("\";\" should return Kind.LBRACE", LBRACE, scanner.separator());
        assertEquals("\";\" should increment pos by 1", 1, scanner.pos);
        
        scanner = new Scanner("}");
		assertEquals("\";\" should return Kind.RBRACE", RBRACE, scanner.separator());
        assertEquals("\";\" should increment pos by 1", 1, scanner.pos);
        
        scanner = new Scanner(".");
        assertEquals("\".\" should return null", null, scanner.separator());
        assertEquals("\".\" should not increment pos", 0, scanner.pos);
	}
	
	@Test
	public void testBooleanLiteral() {
		assertEquals("\"true\" should return Kind.KW_TRUE", KW_TRUE, Scanner.booleanLiteral("true"));
        assertEquals("\"false\" should return Kind.KW_FALSE", KW_FALSE, Scanner.booleanLiteral("false"));
        assertEquals("\"t\" should return null", null, Scanner.booleanLiteral("t"));
        assertEquals("\"f\" should return null", null, Scanner.booleanLiteral("f"));
        assertEquals("\"FALSE\" should return null", null, Scanner.booleanLiteral("FALSE"));
        assertEquals("\"TRUE\" should return null", null, Scanner.booleanLiteral("TRUE"));
	}
	
	@Test
	public void testKeywords() {
		assertEquals("\"integer\" should return Kind.KW_INTEGER", KW_INTEGER, Scanner.keyword("integer"));
		assertEquals("\"boolean\" should return Kind.KW_BOOLEAN", KW_BOOLEAN, Scanner.keyword("boolean"));
		assertEquals("\"image\" should return Kind.KW_IMAGE", KW_IMAGE, Scanner.keyword("image"));
		assertEquals("\"url\" should return Kind.KW_URL", KW_URL, Scanner.keyword("url"));
		assertEquals("\"file\" should return Kind.KW_FILE", KW_FILE, Scanner.keyword("file"));
		assertEquals("\"frame\" should return Kind.KW_FRAME", KW_FRAME, Scanner.keyword("frame"));
		assertEquals("\"while\" should return Kind.KW_WHILE", KW_WHILE, Scanner.keyword("while"));
		assertEquals("\"if\" should return Kind.KW_WHILE", KW_IF, Scanner.keyword("if"));
		assertEquals("\"sleep\" should return Kind.OP_SLEEP", OP_SLEEP, Scanner.keyword("sleep"));
		assertEquals("\"screenheight\" should return Kind.KW_SCREENHEIGHT", KW_SCREENHEIGHT, Scanner.keyword("screenheight"));
		assertEquals("\"screenwidth\" should return Kind.KW_SCREENWIDTH", KW_SCREENWIDTH, Scanner.keyword("screenwidth"));
		assertEquals("\"printf\" should return null", null, Scanner.keyword("printf"));
		assertEquals("\"integer\n\" should return null", null, Scanner.keyword("integer\n"));
		assertEquals("\"if get\" should return null", null, Scanner.keyword("if get"));
	}
	
	@Test
	public void testFilterOpKeywords() {
		assertEquals("\"gray\" should return Kind.OP_GRAY", OP_GRAY, Scanner.filterOpKeyword("gray"));
		assertEquals("\"convolve\" should return Kind.OP_CONVOLVE", OP_CONVOLVE, Scanner.filterOpKeyword("convolve"));
		assertEquals("\"blur\" should return Kind.OP_BLUR", OP_BLUR, Scanner.filterOpKeyword("blur"));
		assertEquals("\"scale\" should return Kind.KW_SCALE", KW_SCALE, Scanner.filterOpKeyword("scale"));
		assertEquals("\"twist\" should return null", null, Scanner.filterOpKeyword("twist"));
	}
	
	@Test
	public void testImageOpKeywords() {
		assertEquals("\"width\" should return Kind.OP_WIDTH", OP_WIDTH, Scanner.imageOpKeyword("width"));
		assertEquals("\"height\" should return Kind.OP_HEIGHT", OP_HEIGHT, Scanner.imageOpKeyword("height"));
		assertEquals("\"turn\" should return null", null, Scanner.imageOpKeyword("turn"));
	}
	
	@Test
	public void testFrameOpKeywords() {
		assertEquals("\"xloc\" should return Kind.KW_XLOC", KW_XLOC, Scanner.frameOpKeyword("xloc"));
		assertEquals("\"yloc\" should return Kind.KW_YLOC", KW_YLOC, Scanner.frameOpKeyword("yloc"));
		assertEquals("\"hide\" should return Kind.KW_HIDE", KW_HIDE, Scanner.frameOpKeyword("hide"));
		assertEquals("\"show\" should return Kind.KW_SHOW", KW_SHOW, Scanner.frameOpKeyword("show"));
		assertEquals("\"move\" should return Kind.KW_MOVE", KW_MOVE, Scanner.frameOpKeyword("move"));
		assertEquals("\"rotate\" should return null", null, Scanner.frameOpKeyword("rotate"));
	}
	
	@Test
	public void testIdent() {
		Scanner scanner = new Scanner("integer");
		assertEquals("\"int\" should return Kind.KW_INTEGER", KW_INTEGER, scanner.ident());
        assertEquals("\"int\" should increment pos by 7", 7, scanner.pos);
        
        scanner = new Scanner("boolean");
        assertEquals("\"boolean\" should return Kind.KW_BOOLEAN", KW_BOOLEAN, scanner.ident());
        assertEquals("\"boolean\" should increment pos by 7", 7, scanner.pos);
        
        scanner = new Scanner("image");
        assertEquals("\"image\" should return Kind.KW_IMAGE", KW_IMAGE, scanner.ident());
        assertEquals("\"image\" should increment pos by 5", 5, scanner.pos);
        
        scanner = new Scanner("url");
        assertEquals("\"url\" should return Kind.KW_URL", KW_URL, scanner.ident());
        assertEquals("\"url\" should increment pos by 3", 3, scanner.pos);
        
        scanner = new Scanner("file");
        assertEquals("\"file\" should return Kind.KW_FILE", KW_FILE, scanner.ident());
        assertEquals("\"file\" should increment pos by 4", 4, scanner.pos);
        
        scanner = new Scanner("frame");
        assertEquals("\"frame\" should return Kind.KW_FRAME", KW_FRAME, scanner.ident());
        assertEquals("\"frame\" should increment pos by 5", 5, scanner.pos);
        
        scanner = new Scanner("while");
        assertEquals("\"while\" should return Kind.KW_WHILE", KW_WHILE, scanner.ident());
        assertEquals("\"while\" should increment pos by 5", 5, scanner.pos);
        
        scanner = new Scanner("if");
        assertEquals("\"if\" should return Kind.KW_IF", KW_IF, scanner.ident());
        assertEquals("\"if\" should increment pos by 2", 2, scanner.pos);
        
        scanner = new Scanner("sleep");
        assertEquals("\"sleep\" should return Kind.OP_SLEEP", OP_SLEEP, scanner.ident());
        assertEquals("\"sleep\" should increment pos by 5", 5, scanner.pos);
        
        scanner = new Scanner("screenheight");
        assertEquals("\"screenheight\" should return Kind.KW_SCREENHEIGHT", KW_SCREENHEIGHT, scanner.ident());
        assertEquals("\"screenheight\" should increment pos by 12", 12, scanner.pos);
        
        scanner = new Scanner("screenwidth");
        assertEquals("\"screenwidth\" should return Kind.KW_SCREENWIDTH", KW_SCREENWIDTH, scanner.ident());
        assertEquals("\"screenwidth\" should increment pos by 11", 11, scanner.pos);
        
        scanner = new Scanner("gray");
        assertEquals("\"gray\" should return Kind.OP_GRAY", OP_GRAY, scanner.ident());
        assertEquals("\"gray\" should increment pos by 4", 4, scanner.pos);
        
        scanner = new Scanner("convolve");
        assertEquals("\"convolve\" should return Kind.OP_CONVOLVE", OP_CONVOLVE, scanner.ident());
        assertEquals("\"convolve\" should increment pos by 8", 8, scanner.pos);
        
        scanner = new Scanner("blur");
        assertEquals("\"blur\" should return Kind.OP_BLUR", OP_BLUR, scanner.ident());
        assertEquals("\"blur\" should increment pos by 4", 4, scanner.pos);
        
        scanner = new Scanner("scale");
        assertEquals("\"scale\" should return Kind.KW_SCALE", KW_SCALE, scanner.ident());
        assertEquals("\"scale\" should increment pos by 5", 5, scanner.pos);
        
        scanner = new Scanner("width");
        assertEquals("\"width\" should return Kind.OP_WIDTH", OP_WIDTH, scanner.ident());
        assertEquals("\"width\" should increment pos by 5", 5, scanner.pos);
        
        scanner = new Scanner("height");
        assertEquals("\"height\" should return Kind.OP_HEIGHT", OP_HEIGHT, scanner.ident());
        assertEquals("\"height\" should increment pos by 6", 6, scanner.pos);
        
        scanner = new Scanner("xloc");
        assertEquals("\"xloc\" should return Kind.KW_XLOC", KW_XLOC, scanner.ident());
        assertEquals("\"xloc\" should increment pos by 4", 4, scanner.pos);
        
        scanner = new Scanner("yloc");
        assertEquals("\"yloc\" should return Kind.KW_YLOC", KW_YLOC, scanner.ident());
        assertEquals("\"yloc\" should increment pos by 4", 4, scanner.pos);
        
        scanner = new Scanner("hide");
        assertEquals("\"hide\" should return Kind.KW_HIDE", KW_HIDE, scanner.ident());
        assertEquals("\"hide\" should increment pos by 4", 4, scanner.pos);
        
        scanner = new Scanner("show");
        assertEquals("\"show\" should return Kind.KW_SHOW", KW_SHOW, scanner.ident());
        assertEquals("\"show\" should increment pos by 4", 4, scanner.pos);
        
        scanner = new Scanner("move");
        assertEquals("\"move\" should return Kind.KW_MOVE", KW_MOVE, scanner.ident());
        assertEquals("\"move\" should increment pos by 4", 4, scanner.pos);
	}
	
}
