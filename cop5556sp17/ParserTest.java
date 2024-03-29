package cop5556sp17;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.assertEquals;
import static cop5556sp17.Scanner.Kind.*;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;


public class ParserTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
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
		//System.out.println(scanner);
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
	public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.factor();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testFactor2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "2";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.factor();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testFactor3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "true";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.factor();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testFactor4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "false";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.factor();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testFactor5() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "screenwidth";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.factor();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testFactor6() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "screenheight";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.factor();
		assertEquals(EOF, parser.t.kind);
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
		parser.factor();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testFactor9() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "  ( false)  ";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.factor();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testFactor10() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "(xloc)";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.factor();
		assertEquals(EOF, parser.t.kind);
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
	
	//param_dec ::= ( KW_URL | KW_FILE | KW_INTEGER | KW_BOOLEAN)   IDENT
	@Test
	public void testParamDec1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "url chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.paramDec();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testParamDec2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "file chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.paramDec();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testParamDec3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "integer chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.paramDec();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testParamDec4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "boolean chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.paramDec();
		assertEquals(EOF, parser.t.kind);
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
		parser.paramDec();
		assertEquals(IDENT, parser.t.kind);
	}

	//dec ::= (  KW_INTEGER | KW_BOOLEAN | KW_IMAGE | KW_FRAME)    IDENT
	@Test
	public void testDec1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "integer chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.dec();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testDec2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "boolean chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.dec();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testDec3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "image chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.dec();
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testDec4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "frame chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.dec();
		assertEquals(EOF, parser.t.kind);
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
		parser.dec();
		assertEquals(IDENT, parser.t.kind);
	}
	
	//assign ::= IDENT ASSIGN expression
	@Test
	public void testAssign1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "chelsea <- true";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.assign();
		assertEquals(EOF, parser.t.kind);
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
	public void testArg1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,5,3,4,7,8) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
        assertEquals(EOF, parser.t.kind);
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
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testArg6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(2 )";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
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
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.arg();
	}
	
	@Test
	public void testArg9() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(2(+))";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.arg();
	}
	
	
	@Test
	public void testArg10() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(2+(i>5))";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testArg11() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(2+(i>=5))";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testElem1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "3 * 4";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.elem();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testElem2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "2";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.elem();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testElem3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "3 / 4";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.elem();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testElem4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "3&4";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.elem();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testElem5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "3%4*2";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.elem();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testElem6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "3*";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.elem();
	}
	
	@Test
	public void testElem7() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea / 2";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.elem();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testElem8() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true&false";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.elem();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testElem9() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "screenwidth%screenheight*2";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.elem();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testElem10() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(5 != 4)*2";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.elem();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testElem11() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(5+4)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.elem();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testElem12() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(c";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.elem();
	}
	
	@Test
	public void testElem13() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(5->3)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.elem();
	}
	
	@Test
	public void testElem14() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "2+3";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.elem();
        assertEquals(PLUS, parser.t.kind);
	}
	
	@Test
	public void testTerm1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea + screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.term();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testTerm2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea +";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.term();
	}
	
	@Test
	public void testTerm3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea < screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.term();
        assertEquals(LT, parser.t.kind);
	}
	
	@Test
	public void testTerm4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea + screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.term();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testTerm5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "-";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.term();
	}
	
	@Test
	public void testTerm6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true | screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.term();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testTerm7() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true - screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.term();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testTerm8() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true * screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.term();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testTerm9() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true / screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.term();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testTerm10() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true & screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.term();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testTerm11() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true % screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.term();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testTerm12() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true < screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.term();
        assertEquals(LT, parser.t.kind);
	}
	
	@Test
	public void testTerm13() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(true < screenheight)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.term();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testTerm14() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "image + chelsea";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.term();
	}
	
	@Test
	public void testExpression1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true < screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.expression();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testExpression2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(true > screenheight)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.expression();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testExpression3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true <= screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.expression();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testExpression4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true >= screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.expression();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testExpression5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true > screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.expression();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testExpression6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "false == screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.expression();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testExpression7() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "screenwidth != screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.expression();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testExpression8() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true < screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.expression();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testExpression9() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true - screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.expression();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testExpression10() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true % screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.expression();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testExpression11() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true & screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.expression();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testExpression12() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "url";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.expression();
	}
	
	@Test
	public void testExpression13() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "int_lit * int_lit + int_lit ! int_lit ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.expression();
	}
	
	// chainElem ::= IDENT | filterOp arg | frameOp arg | imageOp arg
	@Test
	public void testChainElem1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.chainElem();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testChainElem2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "blur (2)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.chainElem();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testChainElem3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray ()";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.chainElem();
	}
	
	@Test
	public void testChainElem4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "convolve (2)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.chainElem();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testChainElem5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.chainElem();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testChainElem6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "hide (2)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.chainElem();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testChainElem7() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "scale";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.chainElem();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testChainElem8() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray (2,2,3)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.chainElem();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testChainElem9() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray (2,2,chelsea)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.chainElem();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testChainElem10() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "show (2,2,)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.chainElem();
	}
	
	// chain ::=  chainElem arrowOp chainElem ( arrowOp  chainElem)*
	@Test
	public void testChain1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray (2,2,chelsea) -> show (2,2)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.chain();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testChain2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray (2,2,chelsea) -> show (2,2) -> hide";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.chain();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testChain3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray (2,2,chelsea) -> show (2,2) ->";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.chain();
	}
	
	@Test
	public void testChain4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray (2,2,chelsea) ->";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.chain();
	}
	
	@Test
	public void testChain5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray (2,2,chelsea) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.chain();
	}
	
	@Test
	public void testChain6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "boolean";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.chain();
	}
	
	@Test
	public void testChain7() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray (2,2,chelsea) -> show (2,2) -> hide -> xloc(1)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.chain();
        assertEquals(EOF, parser.t.kind);
	}
	
	// statement ::=   OP_SLEEP expression ; | whileStatement | ifStatement | chain ; | assign ;
	@Test
	public void testStatement1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "sleep(5);";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.statement();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testStatement2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "sleep 2;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.statement();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testStatement3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "sleep;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.statement();
	}
	
	@Test
	public void testStatement4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray (2,2,chelsea) -> show (2,2) -> hide -> xloc(1);";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.statement();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testStatement5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea <- 8;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.statement();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testStatement6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea <- integer;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.statement();
	}
	
	@Test
	public void testStatement7() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while(true) {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.statement();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testStatement8() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if(true) {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.statement();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testBlock1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.block();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testBlock2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{integer c}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.block();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testBlock3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.block();
	}
	
	@Test
	public void testBlock4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{integer c sleep(5);}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.block();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testBlock5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{sleep(5); integer c}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.block();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testBlock6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{sleep(5); integer c sleep(5); frame c}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.block();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testWhileStatement1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while(true) { integer c }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.whileStatement();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testWhileStatement2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while(true) { integer c ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.whileStatement();
	}
	
	@Test
	public void testWhileStatement3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while (true { integer c }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.whileStatement();
	}
	
	@Test
	public void testWhileStatement4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while true) { integer c }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.whileStatement();
	}
	
	@Test
	public void testWhileStatement5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while (true) integer c }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.whileStatement();
	}
	
	@Test
	public void testWhileStatement6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.whileStatement();
	}
	
	@Test
	public void testIfStatement1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if(true) { integer c }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.ifStatement();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testIfStatement2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if(true) { integer c ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.ifStatement();
	}
	
	@Test
	public void testIfStatement3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if (true { integer c }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.ifStatement();
	}
	
	@Test
	public void testIfStatement4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if true) { integer c }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.ifStatement();
	}
	
	@Test
	public void testIfStatement6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if (true) integer c }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.ifStatement();
	}
	
	@Test
	public void testIfStatement7() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.ifStatement();
	}
	
	@Test
	public void testParse1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.parse();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testParse2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main { while(true) {integer c} }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.parse();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testParse3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main url x, boolean t {image c}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.parse();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testParse4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main url x {frame c}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.parse();
        assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testParse5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main url x {frame c";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.parse();
	}
	
	@Test
	public void testParse6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main url x {frame 2}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.parse();
	}
	
	@Test
	public void testParse7() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.parse();
	}
	
	@Test
	public void testParse8() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "22";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.parse();
	}
	
	@Test
	public void testParse9() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "file";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.parse();
	}
	
	@Test
	public void testParse10() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main url x, {frame c}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.parse();
	}
	
	@Test
	public void testParse11() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main { while(true,2) {integer c} }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.parse();
	}
	
	@Test
	public void testParse12() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "method file x { }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.parse();
	}
	
	@Test
	public void testParse13() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea {integer x \n sleep(x<2); \n if(x>y) {boolean t} \n blur(3) -> move(2,4); }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.parse();
	}
	
	@Test
	public void testParse14() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "awesomeProgram file awesome {\n    integer i\n    i  <- 1;\n    integer j\n    j <- 5;\n    while(i < j){\n    i <- (i + 1);\n    }\n    frame f\n    if(f){\n    f <- (i + j);\n}    \n}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.parse();
	}
	
	@Test
	public void testParse15() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "me {sleep(5+(4*(false/(4-(true%(5&(me)))))));}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.parse();
	}
	
	@Test
	public void testParse16() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea{if(a<b <= c){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.parse();
	}
	
	@Test
	public void testParse17() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea{if(a<b <= c){};}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.parse();
	}
	
	@Test
	public void testParse18() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea{\n while(i<3){\n if(a+b==3){\n boolean chelsea}\n}\n}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.parse();
	}
	
	@Test
	public void testParse19() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea{\n while(i<3){\n if(a+b==3){\n boolean chelsea\n while(true){blur(2) |-> convolve(2,3); \n} \n}\n}\n}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.parse();
	}
	
	@Test
	public void testParse20() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "me {sleep(5+4*false/4-true%5&me);}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.parse();
	}

	@Test
	public void testParse21() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea{\n while(i<3){\n if(a+b==3){\n boolean chelsea\n while(true){blur3,2) |-> convolve(2,3); \n} \n}\n}\n}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
        parser.parse();
	}
}
