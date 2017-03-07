package cop5556sp17;

//import static cop5556sp17.Scanner.Kind.EOF;
//import static cop5556sp17.Scanner.Kind.PLUS;
import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.*;
//import cop5556sp17.AST.ASTNode;
//import cop5556sp17.AST.BinaryExpression;
//import cop5556sp17.AST.IdentExpression;
//import cop5556sp17.AST.IntLitExpression;

public class ASTTest2 {

	static final boolean doPrint = true;
	static void show (Object s) {
		if (doPrint) {
			System.out.println(s);
		}
	}
	

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IdentExpression.class, ast.getClass());
	}

	@Test
	public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IntLitExpression.class, ast.getClass());
	}

	@Test
	public void testBinaryExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "1+abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);
	}
	
	@Test
	public void testBinaryExpr1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "i<3";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(IdentExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(LT, be.getOp().kind);
	}
	
	@Test
	public void testParseALL() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "programChelsea url varURL1 {sleep(3); \n"
				+ "while (i <= 4) {boolean varBOOL1} \n"
				+ "if (j + 2 == 4) {integer varINT1} \n"
				+ "assignment1 <- 10; \n"
				+ "gray (2,2,chelsea) -> show (2,2) |-> hide; \n"
				+ "image varIMAGE1 \n"
				+ "frame varFRAME1"
				+ "\n }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Program prog = (Program) parser.parse();
		assertEquals(EOF, parser.t.kind);
		// TESTING PRE-BLOCK: programChelsea url varURL1
		assertEquals(KW_URL, prog.getParams().get(0).getType().kind);
		assertEquals(IDENT, prog.getParams().get(0).getIdent().kind);
		assertEquals("programChelsea", prog.getName());
		assertEquals("varURL1", prog.getParams().get(0).getIdent().getText());
		// TESTING IN THE BLOCK
		ArrayList<Statement> statementList = prog.getB().getStatements();
		// TESTING SLEEP: sleep(3);
		assertEquals(OP_SLEEP, statementList.get(0).getFirstToken().kind);
		assertEquals("sleep", statementList.get(0).getFirstToken().getText());
		SleepStatement ss = (SleepStatement) statementList.get(0);
		assertEquals(SleepStatement.class, ss.getClass());
        assertEquals(INT_LIT, ss.getE().firstToken.kind);
        assertEquals("3", ss.getE().firstToken.getText());
        // TESTING WHILE: while (i <= 4) {boolean varBOOL1}
        WhileStatement ws = (WhileStatement) statementList.get(1);
        assertEquals(WhileStatement.class, ws.getClass());
        assertEquals(IDENT, ws.getE().getFirstToken().kind);
		BinaryExpression beWHILE = (BinaryExpression) ws.getE();
		assertEquals(BinaryExpression.class, beWHILE.getClass());
		assertEquals(IdentExpression.class, beWHILE.getE0().getClass());
		assertEquals(IntLitExpression.class, beWHILE.getE1().getClass());
		assertEquals(LE, beWHILE.getOp().kind);
        ArrayList<Dec> decListWHILE = ws.getB().getDecs();
        assertEquals(KW_BOOLEAN, decListWHILE.get(0).getType().kind);
        assertEquals(IDENT, decListWHILE.get(0).getIdent().kind);
        assertEquals("varBOOL1", decListWHILE.get(0).getIdent().getText());
        // TESTING IF: if (j + 2 == 4) {integer varINT1}
        IfStatement is = (IfStatement) statementList.get(2);
        assertEquals(IfStatement.class, is.getClass());
        assertEquals(IDENT, is.getE().getFirstToken().kind);
		BinaryExpression beIF = (BinaryExpression) is.getE();
		assertEquals(BinaryExpression.class, beIF.getClass());
		assertEquals(BinaryExpression.class, beIF.getE0().getClass());
		assertEquals(IntLitExpression.class, beIF.getE1().getClass());
		assertEquals(EQUAL, beIF.getOp().kind);
		BinaryExpression beIF2 = (BinaryExpression) beIF.getE0();
		assertEquals(BinaryExpression.class, beIF2.getClass());
		assertEquals(IdentExpression.class, beIF2.getE0().getClass());
		assertEquals(IntLitExpression.class, beIF2.getE1().getClass());
		assertEquals(PLUS, beIF2.getOp().kind);
        ArrayList<Dec> decListIF = is.getB().getDecs();
        assertEquals(KW_INTEGER, decListIF.get(0).getType().kind);
        assertEquals(IDENT, decListIF.get(0).getIdent().kind);
        assertEquals("varINT1", decListIF.get(0).getIdent().getText());
        // TESTING ASSIGNMENT: assignment1 <- 10;
        AssignmentStatement as = (AssignmentStatement) statementList.get(3);
		assertEquals(IDENT, as.getVar().firstToken.kind);
		assertEquals(IdentLValue.class, as.var.getClass());
		assertEquals(INT_LIT, as.getE().firstToken.kind);
		assertEquals(AssignmentStatement.class, as.getClass());
		assertEquals("assignment1", as.getVar().firstToken.getText());
		assertEquals("10", as.getE().firstToken.getText());
		// TESTING CHAIN: gray (2,2,chelsea) -> show (2,2) -> hide;
		BinaryChain allBC = (BinaryChain) statementList.get(4);
        assertEquals(BinaryChain.class, allBC.getClass());
		BinaryChain mainBC = (BinaryChain) allBC;
		Chain grayShow = mainBC.getE0();
		ChainElem hide = mainBC.getE1();
		Token arrowRight = mainBC.getArrow();
		assertEquals(BARARROW, arrowRight.kind);
		BinaryChain grayShowBC = (BinaryChain)grayShow;
		Chain gray = grayShowBC.getE0();
		ChainElem show = grayShowBC.getE1();
		Token arrowLeft = grayShowBC.getArrow();
		assertEquals(ARROW, arrowLeft.kind);
		assertEquals(OP_GRAY, gray.firstToken.kind);
		assertEquals(FilterOpChain.class, gray.getClass());
		FilterOpChain grayFilter = (FilterOpChain)gray;
		assertEquals(3, grayFilter.getArg().getExprList().size());
		assertEquals(INT_LIT, grayFilter.getArg().getExprList().get(0).firstToken.kind);
		assertEquals(INT_LIT, grayFilter.getArg().getExprList().get(1).firstToken.kind);
		assertEquals(IDENT, grayFilter.getArg().getExprList().get(2).firstToken.kind);
		assertEquals("2", grayFilter.getArg().getExprList().get(0).firstToken.getText());
		assertEquals("chelsea", grayFilter.getArg().getExprList().get(2).firstToken.getText());
		assertEquals(FrameOpChain.class, show.getClass());
		FrameOpChain showFrame = (FrameOpChain)show;
		assertEquals(2, showFrame.getArg().getExprList().size());
		assertEquals(INT_LIT, showFrame.getArg().getExprList().get(0).firstToken.kind);
		assertEquals(INT_LIT, showFrame.getArg().getExprList().get(1).firstToken.kind);
		assertEquals("2", showFrame.getArg().getExprList().get(0).firstToken.getText());
		assertEquals("2", showFrame.getArg().getExprList().get(1).firstToken.getText());
		assertEquals(FrameOpChain.class, hide.getClass());
		FrameOpChain hideFrame = (FrameOpChain)hide;
		assertEquals(KW_HIDE, hideFrame.firstToken.kind);
		// TESTING LIST<DEC>
		ArrayList<Dec> decList = prog.getB().getDecs();
		// TESTING: image varIMAGE1
		Dec dImage = decList.get(0);
		assertEquals(KW_IMAGE, dImage.getType().kind);
		assertEquals(IDENT, dImage.getIdent().kind);
		assertEquals("varIMAGE1", dImage.getIdent().getText());
		assertEquals("image", dImage.getType().getText());
		assertEquals(Dec.class, dImage.getClass());
		// TESTING: frame varFRAME1
		Dec dFrame = decList.get(1);
		assertEquals(KW_FRAME, dFrame.getType().kind);
		assertEquals(IDENT, dFrame.getIdent().kind);
		assertEquals("varFRAME1", dFrame.getIdent().getText());
		assertEquals("frame", dFrame.getType().getText());
		assertEquals(Dec.class, dFrame.getClass());
	}

	@Test
	public void testParse1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "programChelsea {sleep(5+4*false/4-true%5&me);}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		assertEquals(EOF, parser.t.kind);
		//System.out.println(ast);
		//show(ast);
		//assertEquals(IdentExpression.class, ast.getClass());
	}
	
	@Test
	public void testParse2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea{\n while(i<3){\n if(a+b==3){\n boolean chelsea\n while(true){blur(2) |-> convolve(2,3); \n} \n}\n}\n}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testParse3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea{\n while(i<3){\n if(a+b==3){\n boolean chelsea}\n}\n}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testParse4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "method file x { }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testParse5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea {integer x \n sleep(x<2); \n if(x>y) {boolean t} \n blur(3) -> move(2,4); }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testParse6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "awesomeProgram file awesome {\n    integer i\n    i  <- 1;\n    integer j\n    j <- 5;\n    while(i < j){\n    i <- (i + 1);\n    }\n    frame f\n    if(f){\n    f <- (i + j);\n}    \n}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testParse7() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "me {sleep(5+(4*(false/(4-(true%(5&(me)))))));}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testParse8() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea{if(a<b <= c){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testParse9() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testParse10() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main { while(true) {integer c} }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testParse11() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main url x, boolean t {image c}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testParse12() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main url x {frame c}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testParse13() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea{\n while(i<3){\n if(a+b==3){\n boolean chelsea\n while(true){blur3,2) |-> convolve(2,3); \n} \n}\n}\n}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.parse();
	}
	
	@Test
	public void testParse14() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea{if(a<b <= c){};}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.parse();
	}
	
	@Test
	public void testParse15() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main url x {frame c";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.parse();
	}
	
	@Test
	public void testParse16() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main url x {frame 2}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.parse();
	}
	
	@Test
	public void testParse17() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.parse();
	}
	
	@Test
	public void testParse18() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "22";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.parse();
	}
	
	@Test
	public void testParse19() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "file";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.parse();
	}
	
	@Test
	public void testParse20() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main url x, {frame c}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.parse();
	}
	
	@Test
	public void testParse21() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main { while(true,2) {integer c} }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.parse();
	}
	
	@Test
	public void testFactor_0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
		assertEquals(EOF, parser.t.kind);
	}

	@Test
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,5) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.arg();
        assertEquals(EOF, parser.t.kind);
	}

	@Test
	public void testArgerror() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}


	@Test
	public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog0 {}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
		assertEquals(EOF, parser.t.kind);
	}
	
	// strongOp ∷= TIMES | DIV | AND | MOD
	@Test
	public void testStrongOp1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "*";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.strongOp(parser.t.kind));
	}
	
	@Test
	public void testStrongOp2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "/";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.strongOp(parser.t.kind));
	}
	
	@Test
	public void testStrongOp3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "&";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.strongOp(parser.t.kind));
	}
	
	@Test
	public void testStrongOp4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "%";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.strongOp(parser.t.kind));
	}
	
	@Test
	public void testStrongOp5() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "`";
		thrown.expect(IllegalCharException.class);
		Parser parser = new Parser(new Scanner(input).scan());
		parser.strongOp(parser.t.kind);
	}
	
	@Test
	public void testStrongOp6() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "c";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(false, parser.strongOp(parser.t.kind));
	}
	
	//weakOp  ∷= PLUS | MINUS | OR 
	@Test
	public void testWeakOp1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "+";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.weakOp(parser.t.kind));
	}
	
	@Test
	public void testWeakOp2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "-";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.weakOp(parser.t.kind));
	}
	
	@Test
	public void testWeakOp3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "|";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.weakOp(parser.t.kind));
	}
	
	@Test
	public void testWeakOp4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "%";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(false, parser.weakOp(parser.t.kind));
	}
	
	//relOp ∷=  LT | LE | GT | GE | EQUAL | NOTEQUAL
	@Test
	public void testRelOp1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "<";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.relOp(parser.t.kind));
	}
	
	@Test
	public void testRelOp2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "<=";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.relOp(parser.t.kind));
	}
	
	@Test
	public void testRelOp3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = ">";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.relOp(parser.t.kind));
	}
	
	@Test
	public void testRelOp4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = ">=";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.relOp(parser.t.kind));
	}
	
	@Test
	public void testRelOp5() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "==";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.relOp(parser.t.kind));
	}
	
	@Test
	public void testRelOp6() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "!=";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.relOp(parser.t.kind));
	}
	
	@Test
	public void testRelOp7() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(false, parser.relOp(parser.t.kind));
	}
	
	//arrowOp ∷= ARROW   |   BARARROW
	@Test
	public void testArrowOp1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "->";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.arrowOp(parser.t.kind));
	}
	
	@Test
	public void testArrowOp2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "|->";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.arrowOp(parser.t.kind));
	}
	
	@Test
	public void testArrowOp3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(false, parser.arrowOp(parser.t.kind));
	}
	
	//filterOp ::= KW_BLUR |KW_GRAY | KW_CONVOLVE
	@Test
	public void testFilterOp1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "blur";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.filterOp(parser.t.kind));
	}
	
	@Test
	public void testFilterOp2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "gray";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.filterOp(parser.t.kind));
	}
	
	@Test
	public void testFilterOp3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "convolve";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.filterOp(parser.t.kind));
	}
	
	@Test
	public void testFilterOp4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(false, parser.filterOp(parser.t.kind));
	}
	
	//frameOp ::= KW_SHOW | KW_HIDE | KW_MOVE | KW_XLOC |KW_YLOC
	@Test
	public void testFrameOp1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "show";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.frameOp(parser.t.kind));
	}
	
	@Test
	public void testFrameOp2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "hide";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.frameOp(parser.t.kind));
	}
	
	@Test
	public void testFrameOp3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "move";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.frameOp(parser.t.kind));
	}
	
	@Test
	public void testFrameOp4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "xloc";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.frameOp(parser.t.kind));
	}
	
	@Test
	public void testFrameOp5() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "yloc";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.frameOp(parser.t.kind));
	}
	
	@Test
	public void testFrameOp6() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(false, parser.frameOp(parser.t.kind));
	}
	
	
	//imageOp ::= KW_WIDTH |KW_HEIGHT | KW_SCALE
	@Test
	public void testImageOp1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "width";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.imageOp(parser.t.kind));
	}
	
	@Test
	public void testImageOp2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "height";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.imageOp(parser.t.kind));
	}
	
	@Test
	public void testImageOp3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "scale";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(true, parser.imageOp(parser.t.kind));
	}
	
	@Test
	public void testImageOp4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		assertEquals(false, parser.imageOp(parser.t.kind));
	}
	
	//factor ∷= IDENT | INT_LIT | KW_TRUE | KW_FALSE | KW_SCREENWIDTH | KW_SCREENHEIGHT | ( expression )
	@Test
	public void testFactor_1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		Expression f = parser.factor();
		assertEquals(EOF, parser.t.kind);
		assertEquals(IDENT, f.getFirstToken().kind);
	}
	
	@Test
	public void testFactor2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "2";
		Parser parser = new Parser(new Scanner(input).scan());
		Expression f = parser.factor();
		assertEquals(EOF, parser.t.kind);
		assertEquals(INT_LIT, f.getFirstToken().kind);
	}
	
	@Test
	public void testFactor3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "true";
		Parser parser = new Parser(new Scanner(input).scan());
		Expression f = parser.factor();
		assertEquals(EOF, parser.t.kind);
		assertEquals(KW_TRUE, f.getFirstToken().kind);
	}
	
	@Test
	public void testFactor4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "false";
		Parser parser = new Parser(new Scanner(input).scan());
		Expression f = parser.factor();
		assertEquals(EOF, parser.t.kind);
		assertEquals(KW_FALSE, f.getFirstToken().kind);
	}
	
	@Test
	public void testFactor5() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "screenwidth";
		Parser parser = new Parser(new Scanner(input).scan());
		Expression f = parser.factor();
		assertEquals(EOF, parser.t.kind);
		assertEquals(KW_SCREENWIDTH, f.getFirstToken().kind);
	}
	
	@Test
	public void testFactor6() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "screenheight";
		Parser parser = new Parser(new Scanner(input).scan());
		Expression f = parser.factor();
		assertEquals(EOF, parser.t.kind);
		assertEquals(KW_SCREENHEIGHT, f.getFirstToken().kind);
	}
	
	@Test
	public void testFactor7() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "integer";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.factor();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testFactor8() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "(true)";
		Parser parser = new Parser(new Scanner(input).scan());
		Expression f = parser.factor();
		assertEquals(EOF, parser.t.kind);
		assertEquals(KW_TRUE, f.getFirstToken().kind);
	}
	
	@Test
	public void testFactor9() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "  ( false)  ";
		Parser parser = new Parser(new Scanner(input).scan());
		Expression f = parser.factor();
		assertEquals(EOF, parser.t.kind);
		assertEquals(KW_FALSE, f.getFirstToken().kind);
	}
	
	@Test
	public void testFactor10() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "(xloc)";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.factor();
	}
	
	@Test
	public void testFactor11() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "()";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.factor();
	}
	
	@Test
	public void testFactor12() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "(true";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.factor();
	}
	
	@Test
	public void testFactor13() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "yloc)";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.factor();
	}
	
	// param_dec ::= ( KW_URL | KW_FILE | KW_INTEGER | KW_BOOLEAN)   IDENT
	// ParamDec ∷= type ident
	@Test
	public void testParamDec1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "url chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		ParamDec pd = parser.paramDec();
		assertEquals(EOF, parser.t.kind);
		assertEquals(KW_URL, pd.getType().kind);
		assertEquals(IDENT, pd.getIdent().kind);
		assertEquals(ParamDec.class, pd.getClass());
	}
	
	@Test
	public void testParamDec2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "file chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		ParamDec pd = parser.paramDec();
		assertEquals(EOF, parser.t.kind);
		assertEquals(KW_FILE, pd.getType().kind);
		assertEquals(IDENT, pd.getIdent().kind);
		assertEquals(ParamDec.class, pd.getClass());
	}
	
	@Test
	public void testParamDec3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "integer chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		ParamDec pd = parser.paramDec();
		assertEquals(EOF, parser.t.kind);
		assertEquals(KW_INTEGER, pd.getType().kind);
		assertEquals(IDENT, pd.getIdent().kind);
		assertEquals(ParamDec.class, pd.getClass());
	}
	
	@Test
	public void testParamDec4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "boolean chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		ParamDec pd = parser.paramDec();
		assertEquals(EOF, parser.t.kind);
		assertEquals(KW_BOOLEAN, pd.getType().kind);
		assertEquals(IDENT, pd.getIdent().kind);
		assertEquals(ParamDec.class, pd.getClass());
	}
	
	@Test
	public void testParamDec5() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "url";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.paramDec();
	}
	
	@Test
	public void testParamDec6() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "file";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.paramDec();
	}
	
	@Test
	public void testParamDec7() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "integer";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.paramDec();
	}
	
	@Test
	public void testParamDec8() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "boolean";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.paramDec();
	}
	
	@Test
	public void testParamDec9() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "xloc";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.paramDec();
	}
	
	@Test
	public void testParamDec10() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "boolean yloc";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.paramDec();
	}
	
	@Test
	public void testParamDec11() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "boolean chelsea metcalf";
		Parser parser = new Parser(new Scanner(input).scan());
		ParamDec pd = parser.paramDec();
		assertEquals(IDENT, parser.t.kind);
		assertEquals(KW_BOOLEAN, pd.getType().kind);
		assertEquals(IDENT, pd.getIdent().kind);
		assertEquals(ParamDec.class, pd.getClass());
	}

	//dec ::= (  KW_INTEGER | KW_BOOLEAN | KW_IMAGE | KW_FRAME)    IDENT
	// Dec ∷= type ident
	@Test
	public void testDec1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "integer chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		Dec d = parser.dec();
		assertEquals(EOF, parser.t.kind);
		assertEquals(KW_INTEGER, d.getType().kind);
		assertEquals(IDENT, d.getIdent().kind);
		assertEquals(Dec.class, d.getClass());
	}
	
	@Test
	public void testDec2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "boolean chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		Dec d = parser.dec();
		assertEquals(EOF, parser.t.kind);
		assertEquals(KW_BOOLEAN, d.getType().kind);
		assertEquals(IDENT, d.getIdent().kind);
		assertEquals(Dec.class, d.getClass());
	}
	
	@Test
	public void testDec3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "image chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		Dec d = parser.dec();
		assertEquals(EOF, parser.t.kind);
		assertEquals(KW_IMAGE, d.getType().kind);
		assertEquals(IDENT, d.getIdent().kind);
		assertEquals(Dec.class, d.getClass());
	}
	
	@Test
	public void testDec4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "frame chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		Dec d = parser.dec();
		assertEquals(EOF, parser.t.kind);
		assertEquals(KW_FRAME, d.getType().kind);
		assertEquals(IDENT, d.getIdent().kind);
		assertEquals(Dec.class, d.getClass());
	}
	
	@Test
	public void testDec5() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "integer";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.dec();
	}
	
	@Test
	public void testDec6() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "boolean";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.dec();
	}
	
	@Test
	public void testDec7() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "image";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.dec();
	}
	
	@Test
	public void testDec8() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "frame";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.dec();
	}
	
	@Test
	public void testDec9() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "xloc";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.dec();
	}
	
	@Test
	public void testDec10() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "boolean yloc";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.dec();
	}
	
	@Test
	public void testDec11() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "boolean chelsea metcalf";
		Parser parser = new Parser(new Scanner(input).scan());
		Dec d = parser.dec();
		assertEquals(IDENT, parser.t.kind);
		assertEquals(KW_BOOLEAN, d.getType().kind);
		assertEquals(IDENT, d.getIdent().kind);
		assertEquals(Dec.class, d.getClass());
	}
	
	// assign ::= IDENT ASSIGN expression
	// AssignmentStatement ∷= IdentLValue Expression
	@Test
	public void testAssign1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "chelsea <- true";
		Parser parser = new Parser(new Scanner(input).scan());
		AssignmentStatement as = parser.assign();
		assertEquals(EOF, parser.t.kind);
		assertEquals(IDENT, as.getVar().firstToken.kind);
		assertEquals(KW_TRUE, as.getE().firstToken.kind);
		assertEquals(AssignmentStatement.class, as.getClass());
	}
	
	@Test
	public void testAssign2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.dec();
	}
	
	@Test
	public void testAssign3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "<-";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.dec();
	}
	
	@Test
	public void testAssign4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "chelsea <- ";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.dec();
	}
	
	@Test
	public void testAssign5() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "chelsea <- yloc";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.dec();
	}
	
	@Test
	public void testAssign7() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "chelsea *";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.dec();
	}
	
	@Test
	public void testAssign8() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "chelsea <- true <- false";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.dec();
	}
	
	@Test
	public void testArg1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,5,3,4,7,8) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Tuple tuple = parser.arg();
        assertEquals(EOF, parser.t.kind);
        assertEquals(6, tuple.getExprList().size());
        assertEquals("4", tuple.getExprList().get(3).getFirstToken().getText());
	}

	@Test
	public void testArg2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,2";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}
	
	@Test
	public void testArg3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "3,2)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.arg();
	}
	
	@Test
	public void testArg4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "()";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}
	
	@Test
	public void testArg5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(3)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.arg();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testArg6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(2 )";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.arg();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testArg7() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(yloc)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}
	
	@Test
	public void testArg8() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(2(*))";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.arg();
	}
	
	@Test
	public void testArg9() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(2(+))";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.arg();
	}
	
	
	@Test
	public void testArg10() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(2+(i>5))";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Tuple tuple = parser.arg();
        assertEquals(EOF, parser.t.kind);
        assertEquals(1, tuple.getExprList().size());
	}
	
	@Test
	public void testArg11() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(2+(i>=5))";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Tuple tuple = parser.arg();
        assertEquals(EOF, parser.t.kind);
        assertEquals(1, tuple.getExprList().size());
	}
	
	@Test
	public void testArg12() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Tuple tuple = parser.arg();
        assertEquals(EOF, parser.t.kind);
        assertEquals(0, tuple.getExprList().size());
        assertEquals(EOF, tuple.getFirstToken().kind);
        assertEquals("eof", tuple.getFirstToken().getText());
	}
	
	@Test
	public void testElem1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "3 * 4";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression el = parser.elem();
        assertEquals(EOF, parser.t.kind);
        assertEquals(INT_LIT, el.getFirstToken().kind);
        assertEquals(BinaryExpression.class, el.getClass());
		BinaryExpression be = (BinaryExpression) el;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(TIMES, be.getOp().kind);
	}
	
	@Test
	public void testElem2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "2";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Expression el = parser.elem();
        assertEquals(EOF, parser.t.kind);
        assertEquals(INT_LIT, el.getFirstToken().kind);
	}
	
	@Test
	public void testElem3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "3 / 4";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Expression el = parser.elem();
        assertEquals(EOF, parser.t.kind);
        assertEquals(INT_LIT, el.getFirstToken().kind);
        assertEquals(BinaryExpression.class, el.getClass());
		BinaryExpression be = (BinaryExpression) el;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(DIV, be.getOp().kind);
	}
	
	@Test
	public void testElem4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "3&4";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Expression el = parser.elem();
        assertEquals(EOF, parser.t.kind);
        assertEquals(INT_LIT, el.getFirstToken().kind);
        assertEquals(BinaryExpression.class, el.getClass());
		BinaryExpression be = (BinaryExpression) el;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(AND, be.getOp().kind);
	}
	
	@Test
	public void testElem5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "3%4*2";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Expression el = parser.elem();
        assertEquals(EOF, parser.t.kind);
        assertEquals(INT_LIT, el.getFirstToken().kind);
        assertEquals(BinaryExpression.class, el.getClass());
		BinaryExpression be = (BinaryExpression) el;
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(TIMES, be.getOp().kind);
		BinaryExpression be2 = (BinaryExpression) be.getE0();
		assertEquals(IntLitExpression.class, be2.getE0().getClass());
		assertEquals(IntLitExpression.class, be2.getE1().getClass());
		assertEquals(MOD, be2.getOp().kind);
	}
	
	@Test
	public void testElem6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "3*";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.elem();
	}
	
	@Test
	public void testElem7() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea / 2";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Expression el = parser.elem();
        assertEquals(EOF, parser.t.kind);
        assertEquals(IDENT, el.getFirstToken().kind);
        BinaryExpression be = (BinaryExpression) el;
        assertEquals("chelsea", be.getE0().getFirstToken().getText());
		assertEquals("2", be.getE1().getFirstToken().getText());
		assertEquals(DIV, be.getOp().kind);
	}
	
	@Test
	public void testElem8() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true&false";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Expression el = parser.elem();
        assertEquals(EOF, parser.t.kind);
        assertEquals(KW_TRUE, el.getFirstToken().kind);
        BinaryExpression be = (BinaryExpression) el;
        assertEquals("true", be.getE0().getFirstToken().getText());
		assertEquals("false", be.getE1().getFirstToken().getText());
		assertEquals(AND, be.getOp().kind);
	}
	
	@Test
	public void testElem9() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "screenwidth%screenheight*2";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Expression el = parser.elem();
        assertEquals(EOF, parser.t.kind);
        assertEquals(KW_SCREENWIDTH, el.getFirstToken().kind);
        assertEquals(BinaryExpression.class, el.getClass());
		BinaryExpression be = (BinaryExpression) el;
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(TIMES, be.getOp().kind);
		BinaryExpression be2 = (BinaryExpression) be.getE0();
		assertEquals(ConstantExpression.class, be2.getE0().getClass());
		assertEquals(ConstantExpression.class, be2.getE1().getClass());
		assertEquals(MOD, be2.getOp().kind);
	}
	
	@Test
	public void testElem10() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(5 != 4)*2";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Expression el = parser.elem();
		BinaryExpression b1 = (BinaryExpression)el;
		assertEquals("2", b1.getE1().getFirstToken().getText());
		BinaryExpression b0 = (BinaryExpression)b1.getE0();
		assertEquals("5", b0.getE0().getFirstToken().getText());
		assertEquals("4", b0.getE1().getFirstToken().getText());
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testElem11() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(5+4)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Expression el = parser.elem();
        assertEquals(EOF, parser.t.kind);
        assertEquals(INT_LIT, el.getFirstToken().kind);
        BinaryExpression be = (BinaryExpression) el;
        assertEquals("5", be.getE0().getFirstToken().getText());
		assertEquals("4", be.getE1().getFirstToken().getText());
		assertEquals(PLUS, be.getOp().kind);
	}
	
	@Test
	public void testElem12() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(c";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.elem();
	}
	
	@Test
	public void testElem13() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(5->3)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.elem();
	}
	
	@Test
	public void testElem14() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "2+3";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression el = parser.elem();
        assertEquals(PLUS, parser.t.kind);
        assertEquals(INT_LIT, el.getFirstToken().kind);
	}
	
	@Test
	public void testTerm1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea + screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression t = parser.term();
        assertEquals(EOF, parser.t.kind);
        assertEquals(IDENT, t.getFirstToken().kind);
	}
	
	@Test
	public void testTerm2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea +";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.term();
	}
	
	@Test
	public void testTerm3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea < screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression t = parser.term();
        assertEquals(LT, parser.t.kind);
        assertEquals(IDENT, t.getFirstToken().kind);
	}
	
	@Test
	public void testTerm4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea + screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression t = parser.term();
        assertEquals(EOF, parser.t.kind);
        assertEquals(IDENT, t.getFirstToken().kind);
	}
	
	@Test
	public void testTerm5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "-";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.term();
	}
	
	@Test
	public void testTerm6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true | screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression t = parser.term();
        assertEquals(EOF, parser.t.kind);
        assertEquals(KW_TRUE, t.getFirstToken().kind);
	}
	
	@Test
	public void testTerm7() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true - screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression t = parser.term();
        assertEquals(EOF, parser.t.kind);
        assertEquals(KW_TRUE, t.getFirstToken().kind);
	}
	
	@Test
	public void testTerm8() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true * screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression t = parser.term();
        assertEquals(EOF, parser.t.kind);
        assertEquals(KW_TRUE, t.getFirstToken().kind);
	}
	
	@Test
	public void testTerm9() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true / screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression t = parser.term();
        assertEquals(EOF, parser.t.kind);
        assertEquals(KW_TRUE, t.getFirstToken().kind);
	}
	
	@Test
	public void testTerm10() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true & screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression t = parser.term();
        assertEquals(EOF, parser.t.kind);
        assertEquals(KW_TRUE, t.getFirstToken().kind);
	}
	
	@Test
	public void testTerm11() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true % screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression t = parser.term();
        assertEquals(EOF, parser.t.kind);
        assertEquals(KW_TRUE, t.getFirstToken().kind);
	}
	
	@Test
	public void testTerm12() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true < screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression t = parser.term();
        assertEquals(LT, parser.t.kind);
        assertEquals(KW_TRUE, t.getFirstToken().kind);
	}
	
	@Test
	public void testTerm13() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(true < screenheight)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression t = parser.term();
        assertEquals(EOF, parser.t.kind);
        assertEquals(KW_TRUE, t.getFirstToken().kind);
	}
	
	@Test
	public void testTerm14() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "image + chelsea";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.term();
	}
	
	// Expression ∷= IdentExpression | IntLitExpression | BooleanLitExpression | ConstantExpression | BinaryExpression
	@Test
	public void testExpression1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true < screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode ast = parser.expression();
        assertEquals(EOF, parser.t.kind);
        assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(BooleanLitExpression.class, be.getE0().getClass());
		assertEquals(ConstantExpression.class, be.getE1().getClass());
		assertEquals(LT, be.getOp().kind);
	}
	
	@Test
	public void testExpression2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(true > screenheight)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        ASTNode ast = parser.expression();
        assertEquals(EOF, parser.t.kind);
        assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(BooleanLitExpression.class, be.getE0().getClass());
		assertEquals(ConstantExpression.class, be.getE1().getClass());
		assertEquals(GT, be.getOp().kind);
	}
	
	@Test
	public void testExpression3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true <= screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression e = parser.expression();
        assertEquals(EOF, parser.t.kind);
        assertEquals(BinaryExpression.class, e.getClass());
		BinaryExpression be = (BinaryExpression) e;
		assertEquals(BooleanLitExpression.class, be.getE0().getClass());
		assertEquals(ConstantExpression.class, be.getE1().getClass());
		assertEquals(LE, be.getOp().kind);
	}
	
	@Test
	public void testExpression4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true >= screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression e = parser.expression();
        assertEquals(EOF, parser.t.kind);
        assertEquals(BinaryExpression.class, e.getClass());
		BinaryExpression be = (BinaryExpression) e;
		assertEquals(BooleanLitExpression.class, be.getE0().getClass());
		assertEquals(ConstantExpression.class, be.getE1().getClass());
		assertEquals(GE, be.getOp().kind);
	}
	
	@Test
	public void testExpression5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true > screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression e = parser.expression();
        assertEquals(EOF, parser.t.kind);
        assertEquals(BinaryExpression.class, e.getClass());
		BinaryExpression be = (BinaryExpression) e;
		assertEquals(BooleanLitExpression.class, be.getE0().getClass());
		assertEquals(ConstantExpression.class, be.getE1().getClass());
		assertEquals(GT, be.getOp().kind);
	}
	
	@Test
	public void testExpression6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "false == screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression e = parser.expression();
        assertEquals(EOF, parser.t.kind);
        assertEquals(BinaryExpression.class, e.getClass());
		BinaryExpression be = (BinaryExpression) e;
		assertEquals(BooleanLitExpression.class, be.getE0().getClass());
		assertEquals(ConstantExpression.class, be.getE1().getClass());
		assertEquals(EQUAL, be.getOp().kind);
	}
	
	@Test
	public void testExpression7() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "screenwidth != screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression e = parser.expression();
        assertEquals(EOF, parser.t.kind);
        assertEquals(BinaryExpression.class, e.getClass());
		BinaryExpression be = (BinaryExpression) e;
		assertEquals(ConstantExpression.class, be.getE0().getClass());
		assertEquals(ConstantExpression.class, be.getE1().getClass());
		assertEquals(NOTEQUAL, be.getOp().kind);
	}
	
	@Test
	public void testExpression8() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true < screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression e = parser.expression();
        assertEquals(EOF, parser.t.kind);
        assertEquals(BinaryExpression.class, e.getClass());
		BinaryExpression be = (BinaryExpression) e;
		assertEquals(BooleanLitExpression.class, be.getE0().getClass());
		assertEquals(ConstantExpression.class, be.getE1().getClass());
		assertEquals(LT, be.getOp().kind);
	}
	
	@Test
	public void testExpression9() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true - screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression e = parser.expression();
        assertEquals(EOF, parser.t.kind);
        assertEquals(BinaryExpression.class, e.getClass());
		BinaryExpression be = (BinaryExpression) e;
		assertEquals(BooleanLitExpression.class, be.getE0().getClass());
		assertEquals(ConstantExpression.class, be.getE1().getClass());
		assertEquals(MINUS, be.getOp().kind);
	}
	
	@Test
	public void testExpression10() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true % screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression e = parser.expression();
        assertEquals(EOF, parser.t.kind);
        assertEquals(BinaryExpression.class, e.getClass());
		BinaryExpression be = (BinaryExpression) e;
		assertEquals(BooleanLitExpression.class, be.getE0().getClass());
		assertEquals(ConstantExpression.class, be.getE1().getClass());
		assertEquals(MOD, be.getOp().kind);
	}
	
	@Test
	public void testExpression11() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true & screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Expression e = parser.expression();
        assertEquals(EOF, parser.t.kind);
        assertEquals(BinaryExpression.class, e.getClass());
		BinaryExpression be = (BinaryExpression) e;
		assertEquals(BooleanLitExpression.class, be.getE0().getClass());
		assertEquals(ConstantExpression.class, be.getE1().getClass());
		assertEquals(AND, be.getOp().kind);
	}
	
	@Test
	public void testExpression12() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "url";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.expression();
	}
	
	@Test
	public void testExpression13() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "int_lit * int_lit + int_lit ! int_lit ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.expression();
	}
	
	// chainElem ::= IDENT | filterOp arg | frameOp arg | imageOp arg
	// ChainElem ::= IdentChain | FilterOpChain | FrameOpChain | ImageOpChain
	@Test
	public void testChainElem1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode ast = parser.chainElem();
        assertEquals(EOF, parser.t.kind);
        assertEquals(IdentChain.class, ast.getClass());
        ChainElem ce = (ChainElem) ast;
		assertEquals(IDENT, ce.getFirstToken().kind);
	}
	
	@Test
	public void testChainElem2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "blur (2)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode ast = parser.chainElem();
        assertEquals(EOF, parser.t.kind);
        assertEquals(FilterOpChain.class, ast.getClass());
        ChainElem ce = (ChainElem) ast;
		assertEquals(OP_BLUR, ce.getFirstToken().kind);
		FilterOpChain f = (FilterOpChain)ce;
		Tuple tuple = f.getArg();
		assertEquals(1, tuple.getExprList().size());
		assertEquals(INT_LIT, tuple.getExprList().get(0).firstToken.kind);
	}
	
	@Test
	public void testChainElem3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray ()";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.chainElem();
	}
	
	@Test
	public void testChainElem4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "convolve (2)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode ast = parser.chainElem();
        assertEquals(EOF, parser.t.kind);
        assertEquals(FilterOpChain.class, ast.getClass());
        ChainElem ce = (ChainElem) ast;
		assertEquals(OP_CONVOLVE, ce.getFirstToken().kind);
		FilterOpChain f = (FilterOpChain)ce;
		Tuple tuple = f.getArg();
		assertEquals(1, tuple.getExprList().size());
		assertEquals(INT_LIT, tuple.getExprList().get(0).firstToken.kind);
	}
	
	@Test
	public void testChainElem5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ChainElem ast = parser.chainElem();
        assertEquals(EOF, parser.t.kind);
        assertEquals(FilterOpChain.class, ast.getClass());
		assertEquals(OP_GRAY, ast.getFirstToken().kind);
	}
	
	@Test
	public void testChainElem6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "hide (2)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ChainElem ast = parser.chainElem();
        assertEquals(EOF, parser.t.kind);
        assertEquals(EOF, parser.t.kind);
        assertEquals(FrameOpChain.class, ast.getClass());
        ChainElem ce = (ChainElem) ast;
		assertEquals(KW_HIDE, ce.getFirstToken().kind);
		FrameOpChain f = (FrameOpChain)ce;
		Tuple tuple = f.getArg();
		assertEquals(1, tuple.getExprList().size());
		assertEquals(INT_LIT, tuple.getExprList().get(0).firstToken.kind);
	}
	
	@Test
	public void testChainElem7() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "scale";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ChainElem ast = parser.chainElem();
        assertEquals(EOF, parser.t.kind);
        assertEquals(ImageOpChain.class, ast.getClass());
		assertEquals(KW_SCALE, ast.getFirstToken().kind);
	}
	
	@Test
	public void testChainElem8() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray (2,2,3)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode ast = parser.chainElem();
        assertEquals(EOF, parser.t.kind);
        assertEquals(FilterOpChain.class, ast.getClass());
        ChainElem ce = (ChainElem) ast;
		assertEquals(OP_GRAY, ce.getFirstToken().kind);
		FilterOpChain f = (FilterOpChain)ce;
		Tuple tuple = f.getArg();
		assertEquals(3, tuple.getExprList().size());
		assertEquals(INT_LIT, tuple.getExprList().get(0).firstToken.kind);
		assertEquals(INT_LIT, tuple.getExprList().get(1).firstToken.kind);
		assertEquals(INT_LIT, tuple.getExprList().get(2).firstToken.kind);
	}
	
	@Test
	public void testChainElem9() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray (2,2,chelsea)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode ast = parser.chainElem();
        assertEquals(EOF, parser.t.kind);
        assertEquals(FilterOpChain.class, ast.getClass());
        ChainElem ce = (ChainElem) ast;
		assertEquals(OP_GRAY, ce.getFirstToken().kind);
		FilterOpChain f = (FilterOpChain)ce;
		Tuple tuple = f.getArg();
		assertEquals(3, tuple.getExprList().size());
		assertEquals(INT_LIT, tuple.getExprList().get(0).firstToken.kind);
		assertEquals(INT_LIT, tuple.getExprList().get(1).firstToken.kind);
		assertEquals(IDENT, tuple.getExprList().get(2).firstToken.kind);
		assertEquals("2", tuple.getExprList().get(0).firstToken.getText());
		assertEquals("chelsea", tuple.getExprList().get(2).firstToken.getText());
	}
	
	@Test
	public void testChainElem10() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "show (2,2,)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.chainElem();
	}
	
	// chain ::=  chainElem arrowOp chainElem ( arrowOp  chainElem)*
	// Chain ∷= ChainElem | BinaryChain
	@Test
	public void testChain1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray (2,2,chelsea) -> show (2,2)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode ast = parser.chain();
        assertEquals(EOF, parser.t.kind);
        assertEquals(BinaryChain.class, ast.getClass());
		BinaryChain bc = (BinaryChain)ast;
		Chain e0 = bc.getE0();
		ChainElem e1 = bc.getE1();
		Token arrowOp = bc.getArrow();
		assertEquals(OP_GRAY, e0.firstToken.kind);
		FilterOpChain e00 = (FilterOpChain)e0;
		assertEquals(3, e00.getArg().getExprList().size());
		assertEquals(INT_LIT, e00.getArg().getExprList().get(0).firstToken.kind);
		assertEquals(INT_LIT, e00.getArg().getExprList().get(1).firstToken.kind);
		assertEquals(IDENT, e00.getArg().getExprList().get(2).firstToken.kind);
		assertEquals("2", e00.getArg().getExprList().get(0).firstToken.getText());
		assertEquals("chelsea", e00.getArg().getExprList().get(2).firstToken.getText());
		assertEquals(ARROW, arrowOp.kind);
		FrameOpChain e11 = (FrameOpChain)e1;
		assertEquals(2, e11.getArg().getExprList().size());
		assertEquals(INT_LIT, e11.getArg().getExprList().get(0).firstToken.kind);
		assertEquals(INT_LIT, e11.getArg().getExprList().get(1).firstToken.kind);
		assertEquals("2", e11.getArg().getExprList().get(0).firstToken.getText());
		assertEquals("2", e11.getArg().getExprList().get(1).firstToken.getText());
	}
	
	@Test
	public void testChain2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray (2,2,chelsea) -> show (2,2) -> hide";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode ast = parser.chain();
        assertEquals(EOF, parser.t.kind);
        assertEquals(BinaryChain.class, ast.getClass());
		BinaryChain mainBC = (BinaryChain)ast;
		Chain grayShow = mainBC.getE0();
		ChainElem hide = mainBC.getE1();
		Token arrowRight = mainBC.getArrow();
		assertEquals(ARROW, arrowRight.kind);
		BinaryChain grayShowBC = (BinaryChain)grayShow;
		Chain gray = grayShowBC.getE0();
		ChainElem show = grayShowBC.getE1();
		Token arrowLeft = grayShowBC.getArrow();
		assertEquals(ARROW, arrowLeft.kind);
		assertEquals(OP_GRAY, gray.firstToken.kind);
		assertEquals(FilterOpChain.class, gray.getClass());
		FilterOpChain grayFilter = (FilterOpChain)gray;
		assertEquals(3, grayFilter.getArg().getExprList().size());
		assertEquals(INT_LIT, grayFilter.getArg().getExprList().get(0).firstToken.kind);
		assertEquals(INT_LIT, grayFilter.getArg().getExprList().get(1).firstToken.kind);
		assertEquals(IDENT, grayFilter.getArg().getExprList().get(2).firstToken.kind);
		assertEquals("2", grayFilter.getArg().getExprList().get(0).firstToken.getText());
		assertEquals("chelsea", grayFilter.getArg().getExprList().get(2).firstToken.getText());
		assertEquals(FrameOpChain.class, show.getClass());
		FrameOpChain showFrame = (FrameOpChain)show;
		assertEquals(2, showFrame.getArg().getExprList().size());
		assertEquals(INT_LIT, showFrame.getArg().getExprList().get(0).firstToken.kind);
		assertEquals(INT_LIT, showFrame.getArg().getExprList().get(1).firstToken.kind);
		assertEquals("2", showFrame.getArg().getExprList().get(0).firstToken.getText());
		assertEquals("2", showFrame.getArg().getExprList().get(1).firstToken.getText());
		assertEquals(FrameOpChain.class, hide.getClass());
		FrameOpChain hideFrame = (FrameOpChain)hide;
		assertEquals(KW_HIDE, hideFrame.firstToken.kind);
	}
	
	@Test
	public void testChain3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray (2,2,chelsea) -> show (2,2) ->";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.chain();
	}
	
	@Test
	public void testChain4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray (2,2,chelsea) ->";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.chain();
	}
	
	@Test
	public void testChain5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray (2,2,chelsea) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.chain();
	}
	
	@Test
	public void testChain6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "boolean";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.chain();
	}
	
	@Test
	public void testChain7() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray (2,2,chelsea) -> show (2,2) -> hide -> xloc(1)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode ast = parser.chain();
        assertEquals(EOF, parser.t.kind);
        assertEquals(BinaryChain.class, ast.getClass());
		BinaryChain mainBC = (BinaryChain)ast;
		Chain grayShowHide = mainBC.getE0();
		ChainElem xloc = mainBC.getE1();
		Token arrowEnd = mainBC.getArrow();
		assertEquals(ARROW, arrowEnd.kind);
		BinaryChain grayShowHideBC = (BinaryChain)grayShowHide;
		Chain grayShow = grayShowHideBC.getE0();
		ChainElem hide = mainBC.getE1();
		Token arrowMiddle = grayShowHideBC.getArrow();
		assertEquals(ARROW, arrowMiddle.kind);
		BinaryChain grayShowBC = (BinaryChain)grayShow;
		Chain gray = grayShowBC.getE0();
		ChainElem show = grayShowBC.getE1();
		Token arrowLeft = grayShowBC.getArrow();
		assertEquals(ARROW, arrowLeft.kind);
		assertEquals(FilterOpChain.class, gray.getClass());
		assertEquals(FrameOpChain.class, show.getClass());
		FrameOpChain fshow = (FrameOpChain) show;
		assertEquals(2, fshow.getArg().getExprList().size());
		assertEquals("2", fshow.getArg().getExprList().get(0).firstToken.getText());
		assertEquals("2", fshow.getArg().getExprList().get(1).firstToken.getText());
	}
	
	// statement ::=   OP_SLEEP expression ; | whileStatement | ifStatement | chain ; | assign ;
	@Test
	public void testStatement1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "sleep(5);";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        //parser.statement();
		SleepStatement ss = (SleepStatement)parser.statement();
        assertEquals(EOF, parser.t.kind);
        assertEquals(SleepStatement.class, ss.getClass());
        assertEquals(INT_LIT, ss.getE().firstToken.kind);
        assertEquals("5", ss.getE().firstToken.getText());
	}
	
	@Test
	public void testStatement2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "sleep 2;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		SleepStatement ss = (SleepStatement)parser.statement();
        assertEquals(EOF, parser.t.kind);
        assertEquals(SleepStatement.class, ss.getClass());
        assertEquals(INT_LIT, ss.getE().firstToken.kind);
        assertEquals("2", ss.getE().firstToken.getText());
	}
	
	@Test
	public void testStatement3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "sleep;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.statement();
	}
	
	@Test
	public void testStatement4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray (2,2,chelsea) -> show (2,2) -> hide -> xloc(1);";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Statement s = parser.statement();
        assertEquals(EOF, parser.t.kind);
        assertEquals(BinaryChain.class, s.getClass());
	}
	
	@Test
	public void testStatement5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea <- 8;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Statement s = parser.statement();
        assertEquals(EOF, parser.t.kind);
        assertEquals(AssignmentStatement.class, s.getClass());
	}
	
	@Test
	public void testStatement6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea <- integer;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.statement();
	}
	
	@Test
	public void testStatement7() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while(true) {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        WhileStatement s = (WhileStatement)parser.statement();
        assertEquals(EOF, parser.t.kind);
        assertEquals(WhileStatement.class, s.getClass());
        assertEquals(KW_TRUE, s.getE().firstToken.kind);
        assertEquals(0, s.getB().getDecs().size());
        assertEquals(0, s.getB().getStatements().size());
	}
	
	@Test
	public void testStatement8() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if(true) {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        IfStatement s = (IfStatement) parser.statement();
        assertEquals(EOF, parser.t.kind);
        assertEquals(IfStatement.class, s.getClass());
        assertEquals(KW_TRUE, s.getE().firstToken.kind);
        assertEquals(0, s.getB().getDecs().size());
        assertEquals(0, s.getB().getStatements().size());
	}
	
	@Test
	public void testBlock1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Block b = parser.block();
        assertEquals(EOF, parser.t.kind);
        ArrayList<Dec> decList = b.getDecs();
        ArrayList<Statement> statementList = b.getStatements();
        assertEquals(0, decList.size());
        assertEquals(0, statementList.size());
	}
	
	@Test
	public void testBlock2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{integer c}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Block b = parser.block();
        assertEquals(EOF, parser.t.kind);
        ArrayList<Dec> decList = b.getDecs();
        ArrayList<Statement> statementList = b.getStatements();
        assertEquals(KW_INTEGER, decList.get(0).getType().kind);
        assertEquals(IDENT, decList.get(0).getIdent().kind);
        assertEquals(0, statementList.size());
	}
	
	@Test
	public void testBlock3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.block();
	}
	
	@Test
	public void testBlock4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{integer c sleep(5);}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Block b = parser.block();
        assertEquals(EOF, parser.t.kind);
        ArrayList<Dec> decList = b.getDecs();
        ArrayList<Statement> statementList = b.getStatements();
        assertEquals(KW_INTEGER, decList.get(0).getType().kind);
        assertEquals(IDENT, decList.get(0).getIdent().kind);
        assertEquals(SleepStatement.class, statementList.get(0).getClass());
	}
	
	@Test
	public void testBlock5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{sleep(5); integer c}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Block b = parser.block();
        assertEquals(EOF, parser.t.kind);
        ArrayList<Dec> decList = b.getDecs();
        ArrayList<Statement> statementList = b.getStatements();
        assertEquals(KW_INTEGER, decList.get(0).getType().kind);
        assertEquals(IDENT, decList.get(0).getIdent().kind);
        assertEquals(SleepStatement.class, statementList.get(0).getClass());
        assertEquals(OP_SLEEP, statementList.get(0).getFirstToken().kind);
	}
	
	@Test
	public void testBlock6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{sleep(5); integer c sleep(5); frame c}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        Block b = parser.block();
        assertEquals(EOF, parser.t.kind);
        ArrayList<Dec> decList = b.getDecs();
        ArrayList<Statement> statementList = b.getStatements();
        assertEquals(KW_INTEGER, decList.get(0).getType().kind);
        assertEquals(IDENT, decList.get(0).getIdent().kind);
        assertEquals(SleepStatement.class, statementList.get(0).getClass());
        assertEquals(OP_SLEEP, statementList.get(0).getFirstToken().kind);
        assertEquals(OP_SLEEP, statementList.get(1).getFirstToken().kind);
        assertEquals(2, statementList.size());
        assertEquals(2, decList.size());
        assertEquals(KW_FRAME, decList.get(1).getType().kind);
        assertEquals(IDENT, decList.get(1).getIdent().kind);
	}
	
	@Test
	public void testWhileStatement1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while(true) { integer c }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        WhileStatement ws = parser.whileStatement();
        assertEquals(EOF, parser.t.kind);
        assertEquals(WhileStatement.class, ws.getClass());
        assertEquals(KW_TRUE, ws.getE().getFirstToken().kind);
        ArrayList<Dec> decList = ws.getB().getDecs();
        assertEquals(KW_INTEGER, decList.get(0).getType().kind);
        assertEquals(IDENT, decList.get(0).getIdent().kind);
	}
	
	@Test
	public void testWhileStatement2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while(true) { integer c ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.whileStatement();
	}
	
	@Test
	public void testWhileStatement3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while (true { integer c }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.whileStatement();
	}
	
	@Test
	public void testWhileStatement4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while true) { integer c }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.whileStatement();
	}
	
	@Test
	public void testWhileStatement5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while (true) integer c }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.whileStatement();
	}
	
	@Test
	public void testWhileStatement6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.whileStatement();
	}
	
	@Test
	public void testIfStatement1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if(true) { integer c }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        IfStatement is = parser.ifStatement();
        assertEquals(EOF, parser.t.kind);
        assertEquals(IfStatement.class, is.getClass());
        assertEquals(KW_TRUE, is.getE().getFirstToken().kind);
        ArrayList<Dec> decList = is.getB().getDecs();
        assertEquals(KW_INTEGER, decList.get(0).getType().kind);
        assertEquals(IDENT, decList.get(0).getIdent().kind);
	}
	
	@Test
	public void testIfStatement2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if(true) { integer c ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.ifStatement();
	}
	
	@Test
	public void testIfStatement3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if (true { integer c }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.ifStatement();
	}
	
	@Test
	public void testIfStatement4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if true) { integer c }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.ifStatement();
	}
	
	@Test
	public void testIfStatement6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if (true) integer c }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.ifStatement();
	}
	
	@Test
	public void testIfStatement7() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.ifStatement();
	}
	
	@Test
	public void testCase1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " while (true) \n {x -> show |-> move (x,y) ;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        WhileStatement ws = parser.whileStatement();
        assertEquals(EOF, parser.t.kind);
        assertEquals(WhileStatement.class, ws.getClass());
	}
	
	@Test
	public void testCase2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		//String input = "  x -> show -> hide ;";
		String input = "  x -> show -> hide";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode ast = parser.chain();
        assertEquals(EOF, parser.t.kind);
        assertEquals(BinaryChain.class, ast.getClass());
		BinaryChain mainBC = (BinaryChain)ast;
		Chain xShow = mainBC.getE0();
		ChainElem hide = mainBC.getE1();
		Token arrowRight = mainBC.getArrow();
		assertEquals(ARROW, arrowRight.kind);
		BinaryChain xShowBC = (BinaryChain)xShow;
		Chain x = xShowBC.getE0();
		ChainElem show = xShowBC.getE1();
		Token arrowLeft = xShowBC.getArrow();
		assertEquals(ARROW, arrowLeft.kind);
		assertEquals(IDENT, x.firstToken.kind);	
		assertEquals(FrameOpChain.class, show.getClass());
		FrameOpChain hideFrame = (FrameOpChain)hide;
		assertEquals(Tuple.class, hideFrame.getArg().getClass());
		assertEquals(0, hideFrame.getArg().getExprList().size());
		assertEquals(FrameOpChain.class, show.getClass());
		FrameOpChain showFrame = (FrameOpChain)show;
		assertEquals(0, showFrame.getArg().getExprList().size());
		assertEquals(FrameOpChain.class, hide.getClass());
		assertEquals(KW_HIDE, hideFrame.firstToken.kind);
	}
	
	@Test
	public void testCase3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " if (true) \n {x -> show |-> move (x,y) ;} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        IfStatement is = parser.ifStatement();
        assertEquals(EOF, parser.t.kind);
        assertEquals(IfStatement.class, is.getClass());
	}
	
	@Test
	public void testTest() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "programChelsea url varURL1 {sleep(3); \n"
				+ "while (true) \n {x -> show |-> move (x,y) ;} \n"
				+ "if (true) \n {x -> show |-> move (x,y) ;} \n"
				+ "assignment1 <- 10; \n"
				+ "gray (2,2,chelsea) -> show (2,2) |-> hide; \n"
				+ "image varIMAGE1 \n"
				+ "frame varFRAME1"
				+ "\n }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Program prog = (Program) parser.parse();
		assertEquals(EOF, parser.t.kind);
	}
}
