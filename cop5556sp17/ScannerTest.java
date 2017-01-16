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
	
}
