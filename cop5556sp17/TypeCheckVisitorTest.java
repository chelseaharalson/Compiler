/**  Important to test the error cases in case the
 * AST is not being completely traversed.
 * 
 * Only need to test syntactically correct programs, or
 * program fragments.
 */

package cop5556sp17;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.Statement;
import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.TypeCheckVisitor.TypeCheckException;

public class TypeCheckVisitorTest {
	

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testAssignmentBoolLit0() throws Exception{
		String input = "p {\nboolean y \ny <- false;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void testAssignmentBoolLitError0() throws Exception {
		String input = "p {\nboolean y \ny <- 3;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}		

	@Test
	public void testVisit1() throws Exception {
		String input = "method file x { }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
		
	@Test
	public void testVisit2() throws Exception {
		String input = "chelsea{if(a<b <= c){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//a, b, and c not declared
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}
	
	@Test
	public void testVisit3() throws Exception {
		String input = "chelsea{integer a\n a <- 5;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testVisit4() throws Exception {
		String input = "chelsea{integer a\n a <- false;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}
	
	@Test
	public void testVisit5() throws Exception {
		String input = "chelsea{if(true){integer a}\n a <- 5}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//a <- 5 is out of scope
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}
	
	//All the working combinations in the binary expression table
	@Test
	public void testExpTable1() throws Exception {
		String input = "chelsea{integer a\n integer b\n integer c\n c <- (a + b)}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable2() throws Exception {
		String input = "chelsea{integer a\n integer b\n integer c\n c <- (a - b)}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable3() throws Exception {
		String input = "chelsea{image a\n image b\n image c\n c <- (a + b)}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable5() throws Exception {
		String input = "chelsea{image a\n image b\n image c\n c <- (a - b)}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable6() throws Exception {
		String input = "chelsea{integer a\n integer b\n integer c\n c <- (a * b)}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable7() throws Exception {
		String input = "chelsea{integer a\n integer b\n if(a / b){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable8() throws Exception {
		String input = "chelsea{integer a\n image b\n image c\n c <- (a * b)}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable9() throws Exception {
		String input = "chelsea{image a\n integer b\n image c\n c <- (a * b)}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable10() throws Exception {
		String input = "chelsea{integer a\n integer b\n if(a < b){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable11() throws Exception {
		String input = "chelsea{integer a\n integer b\n while(a > b){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable12() throws Exception {
		String input = "chelsea{integer a\n integer b\n if(a <= b){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable13() throws Exception {
		String input = "chelsea{integer a\n integer b\n while(a >= b){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable14() throws Exception {
		String input = "chelsea{boolean a\n boolean b\n if(a < b){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable15() throws Exception {
		String input = "chelsea{boolean a\n boolean b\n while(a > b){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable16() throws Exception {
		String input = "chelsea{boolean a\n boolean b\n if(a <= b){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable17() throws Exception {
		String input = "chelsea{boolean a\n boolean b\n while(a >= b){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable18() throws Exception {
		String input = "chelsea{boolean a\n if(a == true){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable19() throws Exception {
		String input = "chelsea{boolean a\n while(a != true){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable20() throws Exception {
		String input = "chelsea{integer a\n integer b\n if(a != b){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable21() throws Exception {
		String input = "chelsea{integer a\n if(a != 5){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable22() throws Exception {
		String input = "chelsea{integer a\n if(5 == a){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable23() throws Exception {
		String input = "chelsea{if(true != true){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTable24() throws Exception {
		String input = "chelsea{if(4 < 6){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	//Some random nested combinations in the binary expression table
	
	@Test
	public void testExpTableNest1() throws Exception {
		String input = "chelsea{integer a\n integer b\n integer c\n while(a != (b + c)){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTableNest2() throws Exception {
		String input = "chelsea{boolean a\n boolean b\n boolean c\n if((a < b) != c){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testExpTableNest3() throws Exception {
		String input = "chelsea{integer a\n integer b\n integer c\n integer d\n while((a < b) >= (c <= c)){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	//Some random bad combinations in the binary expression table
	@Test
	public void testExpTableBad1() throws Exception {
		String input = "chelsea{boolean a\n integer b\n if(a != b){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}
	
	@Test
	public void testExpTableBad2() throws Exception {
		String input = "chelsea{boolean a\n integer b\n while(a > b){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}
	
	@Test
	public void testExpTableBad3() throws Exception {
		String input = "chelsea{image a\n image b\n if(a + b){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}
	
	@Test
	public void testExpTableBad4() throws Exception {
		String input = "chelsea{boolean a\n integer b\n while(a != b){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}
	
	@Test
	public void testExpTableBad5() throws Exception {
		String input = "chelsea{image a\n integer b\n integer c\n c <- (a * b)}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}
	
	@Test
	public void testExpTableBad6() throws Exception {
		String input = "chelsea{image a\n integer b\n integer c\n a <- (b * c)}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}
	
	//url - in program dec
	//file - in program dec
	//frame - show, hide, move, xloc, yloc
	//image - width, height, scale
	//filter - blur, gray, convolve
	//All the working combinations in the chainop table
	@Test
	public void testChainTable1() throws Exception {
		String input = "chelsea url tim{image i\n tim -> i;}";//type image
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testChainTable2() throws Exception {
		String input = "chelsea file tim{image i\n tim -> i;}";//type image
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testChainTable3() throws Exception {
		String input = "chelsea{yloc -> xloc(5);}";//type integer
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testChainTable4() throws Exception {
		String input = "chelsea{xloc -> yloc(3);}";//type integer
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testChainTable5() throws Exception {
		String input = "chelsea{move -> move;}";//type frame
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testChainTable6() throws Exception {
		String input = "chelsea{show(3) -> yloc;}";//type frame
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testChainTable7() throws Exception {
		String input = "chelsea{hide -> move(1);}";//type frame
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testChainTable8() throws Exception {
		String input = "chelsea{image i\n i -> width(5);}";//type image
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testChainTable9() throws Exception {
		String input = "chelsea{image i\n i -> height(9);}";//type image
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testChainTable10() throws Exception {
		String input = "chelsea{image i\n i -> show;}";//type frame
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testChainTable11() throws Exception {
		String input = "chelsea file tim{image i\n i -> tim;}";//type none
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testChainTable12() throws Exception {
		String input = "chelsea{image i\n i -> blur(3);}";//type image
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testChainTable13() throws Exception {
		String input = "chelsea{image i\n i -> gray;}";//type image
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testChainTable14() throws Exception {
		String input = "chelsea{image i\n i -> gray;}";//type image
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testChainTable15() throws Exception {
		String input = "chelsea{image i\n i -> convolve(4);}";//type image
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testChainTable16() throws Exception {
		String input = "chelsea{image i\n i |-> blur(3);}";//type image
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testChainTable17() throws Exception {
		String input = "chelsea{image i\n i |-> gray;}";//type image
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testChainTable18() throws Exception {
		String input = "chelsea{image i\n i |-> convolve(4);}";//type image
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testChainTable19() throws Exception {
		String input = "chelsea{image i\n i ->scale(4);}";//type image
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testChainTable20() throws Exception {
		String input = "chelsea{image i\n boolean b\n i -> b})";//type image
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	//TODO more for the chains
}
