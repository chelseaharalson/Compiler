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
	}

	@Test
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,5) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
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
	}
	
	@Test
	public void testFactor2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "2";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.factor();
	}
	
	@Test
	public void testFactor3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "true";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.factor();
	}
	
	@Test
	public void testFactor4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "false";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.factor();
	}
	
	@Test
	public void testFactor5() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "screenwidth";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.factor();
	}
	
	@Test
	public void testFactor6() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "screenheight";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.factor();
	}
	
	@Test
	public void testFactor7() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "integer";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.factor();
	}
	
	@Test
	public void testFactor8() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "(true)";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.factor();
	}
	
	@Test
	public void testFactor9() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "  ( false)  ";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.factor();
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
	
	//param_dec ::= ( KW_URL | KW_FILE | KW_INTEGER | KW_BOOLEAN)   IDENT
	@Test
	public void testParamDec1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "url chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.paramDec();
	}
	
	@Test
	public void testParamDec2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "file chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.paramDec();
	}
	
	@Test
	public void testParamDec3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "integer chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.paramDec();
	}
	
	@Test
	public void testParamDec4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "boolean chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.paramDec();
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

	//dec ::= (  KW_INTEGER | KW_BOOLEAN | KW_IMAGE | KW_FRAME)    IDENT
	@Test
	public void testDec1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "integer chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.dec();
	}
	
	@Test
	public void testDec2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "boolean chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.dec();
	}
	
	@Test
	public void testDec3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "image chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.dec();
	}
	
	@Test
	public void testDec4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "frame chelsea";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.dec();
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
	
	//assign ::= IDENT ASSIGN expression
	@Test
	public void testAssign1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "chelsea <- true";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.assign();
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
		thrown.expect(Parser.SyntaxException.class);
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
	}
	
	@Test
	public void testArg6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(2 )";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
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
	}
	
	@Test
	public void testArg11() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(2+(i>=5))";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
	}
	
	@Test
	public void testElem1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "3 * 4";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.elem();
	}
	
	@Test
	public void testElem2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "2";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.elem();
	}
	
	@Test
	public void testElem3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "3 / 4";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.elem();
	}
	
	@Test
	public void testElem4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "3&4";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.elem();
	}
	
	@Test
	public void testElem5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "3%4*2";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.elem();
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
	}
	
	@Test
	public void testElem8() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true&false";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.elem();
	}
	
	@Test
	public void testElem9() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "screenwidth%screenheight*2";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.elem();
	}
	
	@Test
	public void testElem10() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(5 != 4)*2";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		//thrown.expect(Parser.SyntaxException.class);
        parser.elem();
	}
	
	@Test
	public void testElem11() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(5+4)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
		//thrown.expect(Parser.SyntaxException.class);
        parser.elem();
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
}
