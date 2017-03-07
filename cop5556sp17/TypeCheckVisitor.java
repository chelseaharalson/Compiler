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
		if (binaryChain.getE0().get_TypeName() == TypeName.URL && binaryChain.getE1().get_TypeName() == TypeName.IMAGE
				&& binaryChain.getArrow().kind == ARROW) {
			binaryChain.set_TypeName(IMAGE);
		}
		else if (binaryChain.getE0().get_TypeName() == TypeName.FILE && binaryChain.getE1().get_TypeName() == TypeName.IMAGE
				&& binaryChain.getArrow().kind == ARROW) {
			binaryChain.set_TypeName(IMAGE);
		}
		else if (binaryChain.getE0().get_TypeName() == TypeName.IMAGE && binaryChain.getE1().get_TypeName() == TypeName.FRAME
				&& binaryChain.getArrow().kind == ARROW) {
			binaryChain.set_TypeName(FRAME);
		}
		else if (binaryChain.getE0().get_TypeName() == TypeName.IMAGE && binaryChain.getE1().get_TypeName() == TypeName.FILE
				&& binaryChain.getArrow().kind == ARROW) {
			binaryChain.set_TypeName(NONE);
		}
		else if (binaryChain.getE0().get_TypeName() == TypeName.FRAME 
				&& isFrameOp(binaryChain.getE1().firstToken.kind)
				&& binaryChain.getArrow().kind == ARROW
				&& (binaryChain.getE1().firstToken.kind == KW_XLOC || binaryChain.getE1().firstToken.kind == KW_YLOC)) {
			binaryChain.set_TypeName(INTEGER);
		}
		else if (binaryChain.getE0().get_TypeName() == TypeName.FRAME 
				//&& isFrameOp(binaryChain.getE1().firstToken.kind)
				&& binaryChain.getE1() instanceof FrameOpChain // ???
				&& binaryChain.getArrow().kind == ARROW
				&& (binaryChain.getE1().firstToken.kind == KW_SHOW || binaryChain.getE1().firstToken.kind == KW_HIDE
				|| binaryChain.getE1().firstToken.kind == KW_MOVE)) {
			binaryChain.set_TypeName(FRAME);
		}
		else if (binaryChain.getE0().get_TypeName() == TypeName.IMAGE
				&& binaryChain.getArrow().kind == ARROW
				&& binaryChain.getE1() instanceof ImageOpChain
				&& (binaryChain.getE1().firstToken.kind == OP_WIDTH || binaryChain.getE1().firstToken.kind == OP_HEIGHT)) {
			binaryChain.set_TypeName(IMAGE);
		}
		else if (binaryChain.getE0().get_TypeName() == TypeName.IMAGE
				&& (binaryChain.getArrow().kind == ARROW || binaryChain.getArrow().kind == BARARROW)
				&& binaryChain.getE1() instanceof ImageOpChain
				&& (binaryChain.getE1().firstToken.kind == OP_GRAY || binaryChain.getE1().firstToken.kind == OP_BLUR
						|| binaryChain.getE1().firstToken.kind == OP_CONVOLVE)) {
			binaryChain.set_TypeName(IMAGE);
		}
		else if (binaryChain.getE0().get_TypeName() == TypeName.IMAGE
				&& binaryChain.getArrow().kind == ARROW
				&& binaryChain.getE1() instanceof ImageOpChain
				&& binaryChain.getE1().firstToken.kind == KW_SCALE) {
			binaryChain.set_TypeName(IMAGE);
		}
		else if (binaryChain.getE0().get_TypeName() == TypeName.IMAGE
				&& binaryChain.getArrow().kind == ARROW
				&& binaryChain.getE1() instanceof IdentChain) {
			binaryChain.set_TypeName(IMAGE);
		}
		else {
			throw new Exception("Unable to set type in visitBinaryChain");
		}
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		if (binaryExpression.getE0().get_TypeName() == TypeName.INTEGER && (binaryExpression.getOp().kind == PLUS
				|| binaryExpression.getOp().kind == MINUS) && binaryExpression.getE1().get_TypeName() == TypeName.INTEGER) {
			binaryExpression.set_TypeName(INTEGER);
		}
		else if (binaryExpression.getE0().get_TypeName() == TypeName.IMAGE && (binaryExpression.getOp().kind == PLUS
				|| binaryExpression.getOp().kind == MINUS) && binaryExpression.getE1().get_TypeName() == TypeName.IMAGE) {
			binaryExpression.set_TypeName(IMAGE);
		}
		else if (binaryExpression.getE0().get_TypeName() == TypeName.INTEGER && (binaryExpression.getOp().kind == TIMES
				|| binaryExpression.getOp().kind == DIV) && binaryExpression.getE1().get_TypeName() == TypeName.INTEGER) {
			binaryExpression.set_TypeName(INTEGER);
		}
		else if (binaryExpression.getE0().get_TypeName() == TypeName.INTEGER && (binaryExpression.getOp().kind == TIMES) 
				&& binaryExpression.getE1().get_TypeName() == TypeName.IMAGE) {
			binaryExpression.set_TypeName(IMAGE);
		}
		else if (binaryExpression.getE0().get_TypeName() == TypeName.IMAGE && (binaryExpression.getOp().kind == TIMES) 
				&& binaryExpression.getE1().get_TypeName() == TypeName.INTEGER) {
			binaryExpression.set_TypeName(IMAGE);
		}
		else if (binaryExpression.getE0().get_TypeName() == TypeName.INTEGER && 
				(binaryExpression.getOp().kind == LT
				|| binaryExpression.getOp().kind == GT
				|| binaryExpression.getOp().kind == LE
				|| binaryExpression.getOp().kind == GE) 
				&& binaryExpression.getE1().get_TypeName() == TypeName.INTEGER) {
			binaryExpression.set_TypeName(INTEGER);
		}
		else if (binaryExpression.getE0().get_TypeName() == TypeName.BOOLEAN && 
				(binaryExpression.getOp().kind == LT
				|| binaryExpression.getOp().kind == GT
				|| binaryExpression.getOp().kind == LE
				|| binaryExpression.getOp().kind == GE) 
				&& binaryExpression.getE1().get_TypeName() == TypeName.BOOLEAN) {
			binaryExpression.set_TypeName(BOOLEAN);
		}
		else if (binaryExpression.getE0().get_TypeName() == binaryExpression.getE1().get_TypeName() && 
				(binaryExpression.getOp().kind == EQUAL
				|| binaryExpression.getOp().kind == NOTEQUAL)) {
			binaryExpression.set_TypeName(BOOLEAN);
		}
		else {
			throw new Exception("Unable to set type in visitBinaryExpression()");
		}
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		symtab.enterScope();
		for (int i = 0; i < block.getDecs().size(); i++) {
			visitDec(block.getDecs().get(i), arg);
		}
		for (int i = 0; i < block.getStatements().size(); i++) {
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
		booleanLitExpression.set_TypeName(BOOLEAN);
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		if (filterOpChain.getArg().getExprList().size() == 0) {
			filterOpChain.set_TypeName(IMAGE);
		}
		else {
			throw new TypeCheckException("Expected tuple size to be 0, but got: " 
					+ filterOpChain.getArg().getExprList().size());
		}
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		//frameOpChain.getFirstToken().kind = KW_FRAME;
		
		if (frameOpChain.getFirstToken().kind == KW_SHOW || frameOpChain.getFirstToken().kind == KW_HIDE) {
			if (frameOpChain.getArg().getExprList().size() == 0) {
				frameOpChain.set_TypeName(NONE);
			}
		}
		else if (frameOpChain.getFirstToken().kind == KW_XLOC || frameOpChain.getFirstToken().kind == KW_YLOC) {
			if (frameOpChain.getArg().getExprList().size() == 0) {
				frameOpChain.set_TypeName(INTEGER);
			}
		}
		else if (frameOpChain.getFirstToken().kind == KW_MOVE) {
			if (frameOpChain.getArg().getExprList().size() == 2) {
				frameOpChain.set_TypeName(NONE);
			}
		}
		else {
			throw new Exception("BUG IN PARSER!! visitFrameOpChain()");
		}
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		//IdentChain.type <- ident.type
		//ident.type <- symtab.lookup(ident.getText()).getType()

		//identChain.setTypeName(symtab.lookup(getTypeName(identChain.getFirstToken()).toString());
		//getTypeName(identChain.getFirstToken())
		
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		//condition:  ident has been declared and is visible in the current scope
		//IdentExpression.type <- ident.type
		//IdentExpression.dec <- Dec of ident
		if (symtab.scopeTable != null) {
			ArrayList<Integer> scopeList = symtab.scopeTable.get(identExpression);
			if (scopeList.isEmpty()) {
				throw new Exception("Scope list empty in visitIdentExpression()");
			}
			for (int i = 0; i < scopeList.size(); i++) {
				if (scopeList.get(i) == symtab.currentScope) {
					
				}
				else {
					throw new Exception("Out of scope in visitIdentExpression()");
				}
			}
		}
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		// condition:  Expression.type = Boolean
		if (ifStatement.getE().get_TypeName() != TypeName.BOOLEAN) {
			throw new TypeCheckException("Expected ifStatement to be boolean, but got: " + 
					ifStatement.getE().get_TypeName());
		}
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		// IntLitExpression.type <- INTEGER
		intLitExpression.set_TypeName(INTEGER);
		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		if (sleepStatement.getE().get_TypeName() != TypeName.INTEGER) {
			throw new TypeCheckException("Expected integer in visitSleepStatement, but got: " + 
					sleepStatement.getE().get_TypeName());
		}
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		// condition:  Expression.type = Boolean
		if (whileStatement.getE().get_TypeName() != TypeName.BOOLEAN) {
			throw new TypeCheckException("Expected whileStatement to be boolean, but got: " + 
					whileStatement.getE().get_TypeName());
		}
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		symtab.insert(declaration.getIdent().getText(), declaration);
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
		if (assignStatement.getVar().get_TypeName() != assignStatement.getE().get_TypeName()) {
			throw new TypeCheckException("Expected IdentLValue[" + assignStatement.getVar().get_TypeName() + "] "
					+ "to be " + assignStatement.getE().get_TypeName());
		}
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		//condition:  ident has been declared and is visible in the current scope
		//IdentLValue.dec <- Dec of ident
		if (symtab.scopeTable != null) {
			ArrayList<Integer> scopeList = symtab.scopeTable.get(identX);
			if (scopeList.isEmpty()) {
				throw new Exception("Scope list empty in visitIdentLValue()");
			}
			for (int i = 0; i < scopeList.size(); i++) {
				if (scopeList.get(i) == symtab.currentScope) {
					
				}
				else {
					throw new Exception("Out of scope in visitIdentLValue()");
				}
			}
		}
		return null;
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		symtab.insert(paramDec.getIdent().getText(), paramDec);
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		// ConstantExpression.type <- INTEGER
		constantExpression.set_TypeName(INTEGER);
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		//imageOpChain.getFirstToken().kind = KW_IMAGE;
		
		if (imageOpChain.getFirstToken().kind == OP_WIDTH || imageOpChain.getFirstToken().kind == OP_HEIGHT) {
			if (imageOpChain.getArg().getExprList().size() == 0) {
				imageOpChain.set_TypeName(INTEGER);
			}
		}
		else if (imageOpChain.getFirstToken().kind == KW_SCALE) {
			if (imageOpChain.getArg().getExprList().size() == 1) {
				imageOpChain.set_TypeName(IMAGE);
			}
		}
		else {
			throw new Exception("BUG IN PARSER!! visitImageOpChain()");
		}
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		// condition:  for all expression in List<Expression>: Expression.type == INTEGER
		List<Expression> exprList = tuple.getExprList();
		for (int i = 0; i < exprList.size(); i++) {
			if (exprList.get(i).get_TypeName() != INTEGER) {
				throw new Exception("Expected integer in visitTuple() but got: " + exprList.get(i).get_TypeName());
			}
		}
		return null;
	}

	public boolean isFrameOp(Kind kind) {
		switch (kind) {
		case KW_SHOW: case KW_HIDE: case KW_MOVE: case KW_XLOC: case KW_YLOC: 
			return true;
		}
		return false;
	}
}
