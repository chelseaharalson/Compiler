package cop5556sp17;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.Chain;
import cop5556sp17.AST.ChainElem;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Expression;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import java.util.ArrayList;
import java.util.List;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.LinePos;
import cop5556sp17.Scanner.Token;
import static cop5556sp17.AST.Type.*;
import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.Scanner.Kind.ARROW;
import static cop5556sp17.Scanner.Kind.KW_HIDE;
import static cop5556sp17.Scanner.Kind.KW_MOVE;
import static cop5556sp17.Scanner.Kind.KW_SHOW;
import static cop5556sp17.Scanner.Kind.KW_XLOC;
import static cop5556sp17.Scanner.Kind.KW_YLOC;
import static cop5556sp17.Scanner.Kind.OP_BLUR;
import static cop5556sp17.Scanner.Kind.OP_CONVOLVE;
import static cop5556sp17.Scanner.Kind.OP_GRAY;
import static cop5556sp17.Scanner.Kind.OP_HEIGHT;
import static cop5556sp17.Scanner.Kind.OP_WIDTH;
import static cop5556sp17.Scanner.Kind.*;

public class TypeCheckVisitor implements ASTVisitor {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		TypeCheckException(String message) {
			super(message);
		}
	}

	SymbolTable symtab = new SymbolTable();

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		TypeName tn = null;
		binaryChain.getE0().visit(this, arg);
		binaryChain.getE1().visit(this, arg);
		if (binaryChain.getE0().get_TypeName() == TypeName.URL && binaryChain.getE1().get_TypeName() == TypeName.IMAGE
				&& binaryChain.getArrow().kind == ARROW) {
			//System.out.println("1");
			binaryChain.set_TypeName(IMAGE);
			tn = IMAGE;
		}
		else if (binaryChain.getE0().get_TypeName() == TypeName.FILE && binaryChain.getE1().get_TypeName() == TypeName.IMAGE
				&& binaryChain.getArrow().kind == ARROW) {
			//System.out.println("2");
			binaryChain.set_TypeName(IMAGE);
			tn = IMAGE;
		}
		else if (binaryChain.getE0().get_TypeName() == TypeName.IMAGE && binaryChain.getE1().get_TypeName() == TypeName.FRAME
				&& binaryChain.getArrow().kind == ARROW) {
			//System.out.println("3");
			binaryChain.set_TypeName(FRAME);
			tn = FRAME;
		}
		else if (binaryChain.getE0().get_TypeName() == TypeName.IMAGE && binaryChain.getE1().get_TypeName() == TypeName.FILE
				&& binaryChain.getArrow().kind == ARROW) {
			//System.out.println("4");
			binaryChain.set_TypeName(NONE);
			tn = NONE;
		}
		else if (binaryChain.getE0().get_TypeName() == TypeName.FRAME 
				&& binaryChain.getE1() instanceof FrameOpChain
				&& binaryChain.getArrow().kind == ARROW
				&& (binaryChain.getE1().firstToken.kind == KW_XLOC || binaryChain.getE1().firstToken.kind == KW_YLOC)) {
			//System.out.println("5");
			binaryChain.set_TypeName(INTEGER);
			tn = INTEGER;
		}
		else if (binaryChain.getE0().get_TypeName() == TypeName.FRAME 
				&& binaryChain.getE1() instanceof FrameOpChain
				&& binaryChain.getArrow().kind == ARROW
				&& (binaryChain.getE1().firstToken.kind == KW_SHOW || binaryChain.getE1().firstToken.kind == KW_HIDE
				|| binaryChain.getE1().firstToken.kind == KW_MOVE)) {
			//System.out.println("6");
			binaryChain.set_TypeName(FRAME);
			tn = FRAME;
		}
		else if (binaryChain.getE0().get_TypeName() == TypeName.IMAGE
				&& binaryChain.getArrow().kind == ARROW
				&& binaryChain.getE1() instanceof ImageOpChain
				&& (binaryChain.getE1().firstToken.kind == OP_WIDTH || binaryChain.getE1().firstToken.kind == OP_HEIGHT)) {
			//System.out.println("7");
			binaryChain.set_TypeName(INTEGER);
			tn = INTEGER;
		}
		else if (binaryChain.getE0().get_TypeName() == TypeName.IMAGE
				&& (binaryChain.getArrow().kind == ARROW || binaryChain.getArrow().kind == BARARROW)
				&& binaryChain.getE1() instanceof FilterOpChain
				&& (binaryChain.getE1().firstToken.kind == OP_GRAY || binaryChain.getE1().firstToken.kind == OP_BLUR
						|| binaryChain.getE1().firstToken.kind == OP_CONVOLVE)) {
			//System.out.println("8");
			binaryChain.getE1().setArrowKind(binaryChain.getArrow().kind);
			binaryChain.set_TypeName(IMAGE);
			tn = IMAGE;
		}
		else if (binaryChain.getE0().get_TypeName() == TypeName.IMAGE
				&& binaryChain.getArrow().kind == ARROW
				&& binaryChain.getE1() instanceof ImageOpChain
				&& binaryChain.getE1().firstToken.kind == KW_SCALE) {
			//System.out.println("9");
			binaryChain.set_TypeName(IMAGE);
			tn = IMAGE;
		}
		else if (binaryChain.getE0().get_TypeName() == TypeName.IMAGE
				&& binaryChain.getArrow().kind == ARROW
				&& binaryChain.getE1() instanceof IdentChain
				&& binaryChain.getE1().get_TypeName() == TypeName.IMAGE) {	// Added for assignment 6
			//System.out.println("10");
			binaryChain.set_TypeName(IMAGE);
			tn = IMAGE;
		}
		// Added for assignment 6
		else if (binaryChain.getE0().get_TypeName() == TypeName.INTEGER
				&& binaryChain.getArrow().kind == ARROW
				&& binaryChain.getE1() instanceof IdentChain
				&& binaryChain.getE1().get_TypeName() == TypeName.INTEGER) {
			//System.out.println("11");
			binaryChain.set_TypeName(INTEGER);
			tn = INTEGER;
		}
		else {
			throw new TypeCheckException("Unable to set type in visitBinaryChain");
		}
		return tn;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		TypeName tn = null;
		binaryExpression.getE0().visit(this, arg);
		binaryExpression.getE1().visit(this, arg);
		//System.out.println("E0: " + binaryExpression.getE0().get_TypeName());
		//System.out.println("E1: " + binaryExpression.getE1().get_TypeName());
		//System.out.println("OP: " + binaryExpression.getOp().getText());
		if (binaryExpression.getE0().get_TypeName() == TypeName.INTEGER && (binaryExpression.getOp().kind == PLUS
				|| binaryExpression.getOp().kind == MINUS) && binaryExpression.getE1().get_TypeName() == TypeName.INTEGER) {
			binaryExpression.set_TypeName(INTEGER);
			tn = INTEGER;
		}
		else if (binaryExpression.getE0().get_TypeName() == TypeName.IMAGE && (binaryExpression.getOp().kind == PLUS
				|| binaryExpression.getOp().kind == MINUS) && binaryExpression.getE1().get_TypeName() == TypeName.IMAGE) {
			binaryExpression.set_TypeName(IMAGE);
			tn = IMAGE;
		}
		else if (binaryExpression.getE0().get_TypeName() == TypeName.INTEGER && (binaryExpression.getOp().kind == TIMES
				|| binaryExpression.getOp().kind == DIV || binaryExpression.getOp().kind == MOD) 
				&& binaryExpression.getE1().get_TypeName() == TypeName.INTEGER) {
			binaryExpression.set_TypeName(INTEGER);
			tn = INTEGER;
		}
		else if (binaryExpression.getE0().get_TypeName() == TypeName.INTEGER && (binaryExpression.getOp().kind == TIMES) 
				&& binaryExpression.getE1().get_TypeName() == TypeName.IMAGE) {
			binaryExpression.set_TypeName(IMAGE);
			tn = IMAGE;
		}
		else if (binaryExpression.getE0().get_TypeName() == TypeName.IMAGE && 
				(binaryExpression.getOp().kind == TIMES
						|| binaryExpression.getOp().kind == DIV
						|| binaryExpression.getOp().kind == MOD) 
				&& binaryExpression.getE1().get_TypeName() == TypeName.INTEGER) {
			binaryExpression.set_TypeName(IMAGE);
			tn = IMAGE;
		}
		else if (binaryExpression.getE0().get_TypeName() == TypeName.INTEGER && 
				(binaryExpression.getOp().kind == LT
				|| binaryExpression.getOp().kind == GT
				|| binaryExpression.getOp().kind == LE
				|| binaryExpression.getOp().kind == GE) 
				&& binaryExpression.getE1().get_TypeName() == TypeName.INTEGER) {
			binaryExpression.set_TypeName(BOOLEAN);
			tn = BOOLEAN;
		}
		else if (binaryExpression.getE0().get_TypeName() == TypeName.BOOLEAN && 
				(binaryExpression.getOp().kind == LT
				|| binaryExpression.getOp().kind == GT
				|| binaryExpression.getOp().kind == LE
				|| binaryExpression.getOp().kind == GE) 
				&& binaryExpression.getE1().get_TypeName() == TypeName.BOOLEAN) {
			binaryExpression.set_TypeName(BOOLEAN);
			tn = BOOLEAN;
		}
		else if (binaryExpression.getE0().get_TypeName() == binaryExpression.getE1().get_TypeName() && 
				(binaryExpression.getOp().kind == EQUAL
				|| binaryExpression.getOp().kind == NOTEQUAL
				|| binaryExpression.getOp().kind == AND
				|| binaryExpression.getOp().kind == OR
				)) {
			binaryExpression.set_TypeName(BOOLEAN);
			tn = BOOLEAN;
		}
		else {
			throw new TypeCheckException("Unable to set type in visitBinaryExpression()");
		}
		return tn;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		symtab.enterScope();
		//System.out.println("Scope Number in visitBlock: " + symtab.currentScope);
		//System.out.println("Dec Count: " + block.getDecs().size());
		for (int i = 0; i < block.getDecs().size(); i++) {
			visitDec(block.getDecs().get(i), arg);
		}
		//System.out.println("Statement Count: " + block.getStatements().size());
		for (int i = 0; i < block.getStatements().size(); i++) {
			block.getStatements().get(i).visit(this, arg);
			//System.out.println("getBlock: " + block.getStatements().get(i).getClass());
			if (block.getStatements().get(i).getClass() == IfStatement.class) {
				visitIfStatement((IfStatement)block.getStatements().get(i), arg);
			}
			else if (block.getStatements().get(i).getClass() == WhileStatement.class) {
				visitWhileStatement((WhileStatement)block.getStatements().get(i), arg);
			}
			else if (block.getStatements().get(i).getClass() == AssignmentStatement.class) {
				visitAssignmentStatement((AssignmentStatement)block.getStatements().get(i), arg);
			}
			else if (block.getStatements().get(i).getClass() == SleepStatement.class) {
				visitSleepStatement((SleepStatement)block.getStatements().get(i), arg);
			}
			else if (block.getStatements().get(i).getClass() == Chain.class) {
				if (block.getStatements().get(i).getClass() == BinaryChain.class) {
					visitBinaryChain((BinaryChain)block.getStatements().get(i), arg);
				}
				else if (block.getStatements().get(i).getClass() == Chain.class) {
					if (block.getStatements().get(i).getClass() == IdentChain.class) {
						visitIdentChain((IdentChain)block.getStatements().get(i), arg);
					}
					else if (block.getStatements().get(i).getClass() == FilterOpChain.class) {
						visitFilterOpChain((FilterOpChain)block.getStatements().get(i), arg);
					}
					else if (block.getStatements().get(i).getClass() == FrameOpChain.class) {
						visitFrameOpChain((FrameOpChain)block.getStatements().get(i), arg);
					}
					else if (block.getStatements().get(i).getClass() == ImageOpChain.class) {
						visitImageOpChain((ImageOpChain)block.getStatements().get(i), arg);
					}
				}
			}
		}
		symtab.leaveScope();
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		// BooleanLitExpression.type <- BOOLEAN
		TypeName tn = null;
		booleanLitExpression.set_TypeName(BOOLEAN);
		tn = BOOLEAN;
		return tn;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		// condition: Tuple.length == 0
		// FilterOpChain.type <- IMAGE
		TypeName tn = null;
		if (filterOpChain.getArg().getExprList().size() == 0) {
			filterOpChain.set_TypeName(IMAGE);
			tn = IMAGE;
		}
		else {
			throw new TypeCheckException("Expected tuple size to be 0, but got: " 
					+ filterOpChain.getArg().getExprList().size());
		}
		return tn;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		TypeName tn = null;
		if (frameOpChain.getFirstToken().kind == KW_SHOW || frameOpChain.getFirstToken().kind == KW_HIDE) {
			if (frameOpChain.getArg().getExprList().size() == 0) {
				frameOpChain.set_TypeName(NONE);
				tn = NONE;
			}
			else {
				throw new TypeCheckException("Error in visitFrameOpChain(): " + frameOpChain.firstToken);
			}
		}
		else if (frameOpChain.getFirstToken().kind == KW_XLOC || frameOpChain.getFirstToken().kind == KW_YLOC) {
			if (frameOpChain.getArg().getExprList().size() == 0) {
				frameOpChain.set_TypeName(INTEGER);
				tn = INTEGER;
			}
			else {
				throw new TypeCheckException("Error in visitFrameOpChain(): " + frameOpChain.firstToken);
			}
		}
		else if (frameOpChain.getFirstToken().kind == KW_MOVE) {
			if (frameOpChain.getArg().getExprList().size() == 2) {
				for (int i = 0; i < frameOpChain.getArg().getExprList().size(); i++) {
					frameOpChain.getArg().getExprList().get(i).visit(this, arg);
				}
				frameOpChain.set_TypeName(NONE);
				tn = NONE;
			}
			else {
				throw new TypeCheckException("Error in visitFrameOpChain(): " + frameOpChain.firstToken);
			}
		}
		else {
			throw new TypeCheckException("BUG IN PARSER!! visitFrameOpChain()");
		}
		return tn;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		//condition:  ident has been declared and is visible in the current scope
		//IdentChain.type <- ident.type
		//ident.type <- symtab.lookup(ident.getText()).getType()
		TypeName tn = null;
		if (symtab.scopeTable != null) {
			Dec lookupDec = symtab.lookup(identChain.firstToken.getText());
			if (lookupDec == null) {
				throw new TypeCheckException("Scope list empty in visitIdentChain()");
			}
			else {
				identChain.set_TypeName(lookupDec.firstToken.get_TypeName());
				identChain.set_Dec(lookupDec);
				tn = lookupDec.firstToken.get_TypeName();
			}
		}
		return tn;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		//condition:  ident has been declared and is visible in the current scope
		//IdentExpression.type <- ident.type
		//IdentExpression.dec <- Dec of ident
		TypeName tn = null;
		if (symtab.scopeTable != null) {
			Dec lookupDec = symtab.lookup(identExpression.firstToken.getText());
			if (lookupDec == null) {
				throw new TypeCheckException("Scope list empty in visitIdentExpression()");
			}
			//System.out.println("Lookup Dec: " + lookupDec.get_TypeName());
			//System.out.println("Lookup Dec: " + lookupDec.getIdent().getText());
			identExpression.set_TypeName(lookupDec.firstToken.get_TypeName());
			identExpression.set_Dec(lookupDec);
			tn = lookupDec.firstToken.get_TypeName();
		}
		return tn;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		// condition:  Expression.type = Boolean
		ifStatement.getE().visit(this, arg);
		if (ifStatement.getE().get_TypeName() != TypeName.BOOLEAN) {
			throw new TypeCheckException("Expected ifStatement to be boolean, but got: " + 
					ifStatement.getE().get_TypeName());
		}
		ifStatement.getB().visit(this, arg);
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		// IntLitExpression.type <- INTEGER
		TypeName tn = null;
		intLitExpression.set_TypeName(INTEGER);
		tn = INTEGER;
		return tn;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		sleepStatement.getE().visit(this, arg);
		if (sleepStatement.getE().get_TypeName() != TypeName.INTEGER) {
			throw new TypeCheckException("Expected integer in visitSleepStatement, but got: " + 
					sleepStatement.getE().get_TypeName());
		}
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		// condition:  Expression.type = Boolean
		whileStatement.getE().visit(this, arg);
		if (whileStatement.getE().get_TypeName() != TypeName.BOOLEAN) {
			throw new TypeCheckException("Expected whileStatement to be boolean, but got: " + 
					whileStatement.getE().get_TypeName());
		}
		whileStatement.getB().visit(this, arg);
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		//System.out.println("Dec Kind: " + declaration.getType().kind);
		//System.out.println("Declaration: " + declaration.getIdent().getText());
		//System.out.println("Declaration Type: " + declaration.get_TypeName());
		if (symtab.scopeTable != null) {
			Dec lookupDec = symtab.lookup(declaration.getIdent().getText());
			if (lookupDec == null) {
				symtab.insert(declaration.getIdent().getText(), declaration);
			}
			else if (symtab.lookupScope(declaration.getIdent().getText()) == symtab.currentScope) {
					throw new TypeCheckException("Has already been declared in this scope: " + 
							declaration.getIdent().getText());
			}
			else {
				symtab.insert(declaration.getIdent().getText(), declaration);
			}
		}
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (int i = 0; i < program.getParams().size(); i++) {
			visitParamDec(program.getParams().get(i), arg);
		}
		visitBlock(program.getB(), arg);
		return null;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		// condition:  IdentLValue.type == Expression.type
		assignStatement.getVar().visit(this, arg);
		assignStatement.getE().visit(this, arg);
		//System.out.println("getVar: " + assignStatement.getVar().getText());
		//System.out.println("getE: " + assignStatement.getE().getFirstToken().getText());
		if (assignStatement.getVar().get_Dec().firstToken.get_TypeName() != assignStatement.getE().get_TypeName()) {
			throw new TypeCheckException("Expected IdentLValue[" + assignStatement.getVar().get_Dec().firstToken.get_TypeName() + "] "
					+ "to be " + assignStatement.getE().get_TypeName());
		}
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		//condition:  ident has been declared and is visible in the current scope
		//IdentLValue.dec <- Dec of ident
		if (symtab.scopeTable != null) {
			//System.out.println("identX: " + identX.firstToken.getText());
			Dec lookupDec = symtab.lookup(identX.firstToken.getText());
			if (lookupDec == null) {
				throw new TypeCheckException("Scope list empty in visitIdentLValue()");
			}
			//System.out.println("lookupDec: " + lookupDec.get_TypeName());
			//System.out.println("identX BEFORE: " + identX.get_Dec().get_TypeName());
			identX.set_Dec(lookupDec);
			//System.out.println("identX AFTER: " + identX.get_Dec().get_TypeName());
		}
		return null;
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		//System.out.println("Scope Number in visitPararmDec: " + symtab.currentScope);
		if (symtab.scopeTable != null) {
			Dec lookupDec = symtab.lookup(paramDec.getIdent().getText());
			if (lookupDec == null) {
				symtab.insert(paramDec.getIdent().getText(), paramDec);
			}
			else if (symtab.lookupScope(paramDec.getIdent().getText()) == symtab.currentScope) {
					throw new TypeCheckException("Has already been declared in this scope: " + 
							paramDec.getIdent().getText());
			}
			else {
				symtab.insert(paramDec.getIdent().getText(), paramDec);
				//System.out.println("Inserting in symbol table " + paramDec.firstToken.getText() + " with scope " + symtab.currentScope);
			}
		}
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		// ConstantExpression.type <- INTEGER
		TypeName tn = null;
		constantExpression.set_TypeName(INTEGER);
		tn = INTEGER;
		return tn;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		TypeName tn = null;
		if (imageOpChain.getFirstToken().kind == OP_WIDTH || imageOpChain.getFirstToken().kind == OP_HEIGHT) {
			if (imageOpChain.getArg().getExprList().size() == 0) {
				imageOpChain.set_TypeName(INTEGER);
				tn = INTEGER;
			}
		}
		else if (imageOpChain.getFirstToken().kind == KW_SCALE) {
			if (imageOpChain.getArg().getExprList().size() == 1) {
				imageOpChain.getArg().getExprList().get(0).visit(this, arg);
				imageOpChain.set_TypeName(IMAGE);
				tn = IMAGE;
			}
		}
		else {
			throw new TypeCheckException("BUG IN PARSER!! visitImageOpChain()");
		}
		return tn;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		// condition:  for all expression in List<Expression>: Expression.type == INTEGER
		List<Expression> exprList = tuple.getExprList();
		for (int i = 0; i < exprList.size(); i++) {
			exprList.get(i).visit(this, arg);
			if (exprList.get(i).get_TypeName() != INTEGER) {
				throw new TypeCheckException("Expected integer in visitTuple() but got: " + exprList.get(i).get_TypeName());
			}
		}
		return null;
	}
}
