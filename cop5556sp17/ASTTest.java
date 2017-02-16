package cop5556sp17;

//import static cop5556sp17.Scanner.Kind.EOF;
//import static cop5556sp17.Scanner.Kind.PLUS;
import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IntLitExpression;

public class ASTTest {

	static final boolean doPrint = true;
	static void show(Object s){
		if(doPrint){System.out.println(s);}
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
	public void testParse1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "programChelsea {sleep(5+4*false/4-true%5&me);}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		System.out.println(ast);
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
		System.out.println(ast);
	}
	
	@Test
	public void testParse3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea{\n while(i<3){\n if(a+b==3){\n boolean chelsea}\n}\n}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		System.out.println(ast);
	}
	
	@Test
	public void testParse4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "method file x { }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		System.out.println(ast);
	}
	
	@Test
	public void testParse5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea {integer x \n sleep(x<2); \n if(x>y) {boolean t} \n blur(3) -> move(2,4); }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		System.out.println(ast);
	}
	
	@Test
	public void testParse6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "awesomeProgram file awesome {\n    integer i\n    i  <- 1;\n    integer j\n    j <- 5;\n    while(i < j){\n    i <- (i + 1);\n    }\n    frame f\n    if(f){\n    f <- (i + j);\n}    \n}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		System.out.println(ast);
	}
	
	@Test
	public void testParse7() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "me {sleep(5+(4*(false/(4-(true%(5&(me)))))));}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		System.out.println(ast);
	}
	
	@Test
	public void testParse8() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "chelsea{if(a<b <= c){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		System.out.println(ast);
	}
	
	@Test
	public void testParse9() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		System.out.println(ast);
	}
	
	@Test
	public void testParse10() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main { while(true) {integer c} }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		System.out.println(ast);
	}
	
	@Test
	public void testParse11() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main url x, boolean t {image c}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		System.out.println(ast);
		assertEquals(EOF, parser.t.kind);
	}
	
	@Test
	public void testParse12() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main url x {frame c}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		System.out.println(ast);
	}
}
