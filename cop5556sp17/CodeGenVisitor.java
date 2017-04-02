package cop5556sp17;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.ASTVisitor;
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
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import static cop5556sp17.AST.Type.TypeName.FRAME;
import static cop5556sp17.AST.Type.TypeName.IMAGE;
import static cop5556sp17.AST.Type.TypeName.URL;
import static cop5556sp17.Scanner.Kind.*;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;
	
	Integer slotCount = 2;
	Integer globalsVisited = 0;
	ArrayList<String> globalsList = new ArrayList<String>();

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.getName();
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
				new String[] { "java/lang/Runnable" });
		cw.visitSource(sourceFileName, null);

		// generate constructor code
		// get a MethodVisitor
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		// Create label at start of code
		Label constructorStart = new Label();
		mv.visitLabel(constructorStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");
		// generate code to call superclass constructor
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		// visit parameter decs to add each as field to the class
		// pass in mv so decs can add their initialization code to the
		// constructor.
		ArrayList<ParamDec> params = program.getParams();
		for (ParamDec dec : params)
			dec.visit(this, mv);
		mv.visitInsn(RETURN);
		// create label at end of code
		Label constructorEnd = new Label();
		mv.visitLabel(constructorEnd);
		// finish up by visiting local vars of constructor
		// the fourth and fifth arguments are the region of code where the local
		// variable is defined as represented by the labels we inserted.
		mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);
		// indicates the max stack size for the method.
		// because we used the COMPUTE_FRAMES parameter in the classwriter
		// constructor, asm
		// will do this for us. The parameters to visitMaxs don't matter, but
		// the method must
		// be called.
		mv.visitMaxs(1, 1);
		// finish up code generation for this method.
		mv.visitEnd();
		// end of constructor

		// create main method which does the following
		// 1. instantiate an instance of the class being generated, passing the
		// String[] with command line arguments
		// 2. invoke the run method.
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");
		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
		mv.visitInsn(RETURN);
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// create run method
		mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		Label startRun = new Label();
		mv.visitLabel(startRun);
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");
		program.getB().visit(this, null);
		mv.visitInsn(RETURN);
		Label endRun = new Label();
		mv.visitLabel(endRun);
		mv.visitLocalVariable("this", classDesc, null, startRun, endRun, 0);
//TODO  visit the local variables
		
		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method
		
		
		cw.visitEnd();//end of class
		
		//generate classfile and return it
		return cw.toByteArray();
	}

	// Store value of Expression into location indicated by IdentLValue
	// IMPORTANT:  
	// insert the following statement into your code for an Assignment Statement
	// after value of expression is put on top of stack and before it is written into the IdentLValue
	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		if(assignStatement.getVar().get_Dec() instanceof ParamDec){
			mv.visitVarInsn(ALOAD, 0);//load "this" to later be used for PUTFIELD as objRef
		}
		
		assignStatement.getE().visit(this, arg);
		//System.out.println("ASSIGN STATEMENT: " + assignStatement.e.firstToken.getText());
		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + "=");
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().get_TypeName());
		assignStatement.getVar().visit(this, arg);
		return null;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}
	
	// Visit children to generate code to leave values of arguments on stack
    // perform operation, leaving result on top of the stack.  Expressions should
    // be evaluated from left to right consistent with the structure of the AST.
	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		Kind opKind = binaryExpression.getOp().kind;
		binaryExpression.getE0().visit(this, arg);
		binaryExpression.getE1().visit(this, arg);
		String e0Type = binaryExpression.getE0().get_TypeName().getJVMTypeDesc();
		String e1Type = binaryExpression.getE1().get_TypeName().getJVMTypeDesc();
		if (opKind.equals(PLUS)) {
			if (e0Type.equals("I") && e1Type.equals("I")) {
				mv.visitInsn(IADD);
			}
			// IMAGE + IMAGE goes here
		}
		else if (opKind.equals(MINUS)) {
			if (e0Type.equals("I") && e1Type.equals("I")) {
				mv.visitInsn(ISUB);
			}
			// IMAGE - IMAGE goes here
		}
		else if (opKind.equals(TIMES)) {
			if (e0Type.equals("I") && e1Type.equals("I")) {
				mv.visitInsn(IMUL);
			}
			// IMAGE * INTEGER goes here
			// INTEGER * IMAGE goes here
		}
		else if (opKind.equals(DIV)) {
			if (e0Type.equals("I") && e1Type.equals("I")) {
				mv.visitInsn(IDIV);
			}
		}
		else if (opKind.equals(LT)) {
			if ( (e0Type.equals("I") && e1Type.equals("I")) || (e0Type.equals("Z") && e1Type.equals("Z")) ) {
				Label ltLabel = new Label();
				Label finishLabel = new Label();
				mv.visitJumpInsn(IF_ICMPLT, ltLabel);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, finishLabel);
				mv.visitLabel(ltLabel);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(finishLabel);
			}
		}
		else if (opKind.equals(GT)) {
			if ( (e0Type.equals("I") && e1Type.equals("I")) || (e0Type.equals("Z") && e1Type.equals("Z")) ) {
				Label gtLabel = new Label();
				Label finishLabel = new Label();
				mv.visitJumpInsn(IF_ICMPGT, gtLabel);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, finishLabel);
				mv.visitLabel(gtLabel);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(finishLabel);
			}
		}
		else if (opKind.equals(LE)) {
			if ( (e0Type.equals("I") && e1Type.equals("I")) || (e0Type.equals("Z") && e1Type.equals("Z")) ) {
				Label leLabel = new Label();
				Label finishLabel = new Label();
				mv.visitJumpInsn(IF_ICMPLE, leLabel);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, finishLabel);
				mv.visitLabel(leLabel);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(finishLabel);
			}
		}
		else if (opKind.equals(GE)) {
			if ( (e0Type.equals("I") && e1Type.equals("I")) || (e0Type.equals("Z") && e1Type.equals("Z")) ) {
				Label geLabel = new Label();
				Label finishLabel = new Label();
				mv.visitJumpInsn(IF_ICMPGE, geLabel);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, finishLabel);
				mv.visitLabel(geLabel);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(finishLabel);
			}
		}
		else if (opKind.equals(EQUAL)) {
			if (e0Type.equals(e1Type)) {
				Label eqLabel = new Label();
				Label finishLabel = new Label();
				mv.visitJumpInsn(IF_ICMPEQ, eqLabel);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, finishLabel);
				mv.visitLabel(eqLabel);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(finishLabel);
			}
		}
		else if (opKind.equals(NOTEQUAL)) {
			if (e0Type.equals(e1Type)) {
				Label neqLabel = new Label();
				Label finishLabel = new Label();
				mv.visitJumpInsn(IF_ICMPNE, neqLabel);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, finishLabel);
				mv.visitLabel(neqLabel);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(finishLabel);
			}
		}
		return "Z";
	}

	// Block ∷= List<Dec>  List<Statement>
	//			Decs are local variables in current scope of run method
	//			Statements are executed in run method
	//			Must label beginning and end of scope, and keep track of local variables, 
	//					their slot in the local variable array, and their range of visibility.
	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		Label startLabel = new Label();
		Label finishLabel = new Label();
		mv.visitLabel(startLabel);
		mv.visitLabel(finishLabel);
		for (Dec d : block.getDecs()) {
			d.visit(this, arg);
			String localDesc = d.getFirstToken().get_TypeName().getJVMTypeDesc();
			mv.visitLocalVariable(d.getIdent().getText(), localDesc, null, startLabel, finishLabel, d.getSlotNumber());
		}
		for (Statement s : block.getStatements()) {
			s.visit(this, arg);
		}
		return null;
	}

	// Load constant
	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		if (booleanLitExpression.getValue().booleanValue()) {
			mv.visitInsn(ICONST_1);
		}
		else {
			mv.visitInsn(ICONST_0);
		}
		return "Z";
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		assert false : "not yet implemented";
		return null;
	}

	// Assign a slot in the local variable array to this variable and save it in the new slot attribute in the Dec class
	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		declaration.setSlotNumber(slotCount);
		slotCount++;
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	// Load value of variable (this could be a field or a local var)
	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		Dec d = identExpression.get_Dec();
		if (d instanceof ParamDec) {
			String variableName = d.getIdent().getText();
			String localDesc = d.getFirstToken().get_TypeName().getJVMTypeDesc();
			mv.visitVarInsn(ALOAD, 0);//load "this" to later be used for GETREF as objRef
			mv.visitFieldInsn(GETFIELD, className, variableName, localDesc);
		}
		else {
			int slot = d.getSlotNumber();
			mv.visitVarInsn(ILOAD, slot);
		}
		return null;
	}

	// Store value on top of stack to this variable (which could be a field or local var)
	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		Dec d = identX.get_Dec();
		if (d instanceof ParamDec) {
			String variableName = d.getIdent().getText();
			String localDesc = d.getFirstToken().get_TypeName().getJVMTypeDesc();
			mv.visitFieldInsn(PUTFIELD, className, variableName, localDesc);
		}
		else {
			int slot = d.getSlotNumber();
			mv.visitVarInsn(ISTORE, slot);
		}
		return null;
	}

	// IfStatement ::= Expression Block
    //				Expression
    //				IFEQ AFTER
	//			Block
	//     AFTER ...
	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		ifStatement.getE().visit(this, arg);
		Label ifeqLabel = new Label();
		mv.visitJumpInsn(IFEQ, ifeqLabel);
		ifStatement.getB().visit(this, arg);
		mv.visitLabel(ifeqLabel);
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	// Load constant
	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		mv.visitLdcInsn(intLitExpression.getFirstToken().intVal());
		return "I";
	}

	// Instance variable in class, initialized with values from arg array
	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		// For assignment 5, only needs to handle integers and booleans
		String variableName = paramDec.getIdent().getText();
		String localDesc = paramDec.getFirstToken().get_TypeName().getJVMTypeDesc();
		globalsList.add(variableName);
		cw.visitField(ACC_PUBLIC, variableName, localDesc, null, null);
		
		mv.visitVarInsn(ALOAD, 0);//load "this" to later be used for PUTFIELD as objRef
		//System.out.println("Visiting the globals[" + globalsVisited + "]");
		mv.visitVarInsn(ALOAD, 1);//load args[]
		mv.visitLdcInsn(globalsVisited);//load globalsVisited index
		mv.visitInsn(AALOAD);//put the args[globalsVistied] onto the stack
		
		//check type of string (int/bool)
		if(localDesc == "I"){
			//System.out.println("Putting the integer value into " + variableName);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
			mv.visitFieldInsn(PUTFIELD, className, variableName, "I");//put the value into the variable
		}else if(localDesc == "Z"){
			//System.out.println("Putting the boolean value into " + variableName);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
			mv.visitFieldInsn(PUTFIELD, className, variableName, "Z");//put the value into the variable
		}
		
		globalsVisited++;
		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	// WhileStatement ::= Expression Block
    // 			goto GUARD
	//    BODY     Block
	//    GUARD  Expression
	// 		     IFNE  BODY
	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		Label expressionLabel = new Label();
		Label finishLabel = new Label();
		mv.visitLabel(expressionLabel);
		whileStatement.getE().visit(this, arg);
		// check expression, if not equal go to finish label
		mv.visitJumpInsn(IFNE, finishLabel);
		// if equal, continue and visit block
		whileStatement.getB().visit(this, arg);
		mv.visitJumpInsn(GOTO, expressionLabel);
		mv.visitLabel(finishLabel);
		return null;
	}
	
	/*static class Name implements Runnable {
		// Variables declared in List<ParamDec> are instance variables of the class

		public Name(String[] args) {
			// Initialize instance variables with values from args.
			for (int i = 0; i < args.length; i++) {
				boolean isInteger = false;
				try { 
			        Integer.parseInt(args[i]); 
			        isInteger = true;
			    } catch(NumberFormatException e) {
			    } catch(NullPointerException e) {
			    }
				
				if (isInteger == false) {
					try { 
				        Boolean.parseBoolean(args[i]); 
				    } catch(Exception e) { 
				    }
				}
			}
		}
		
		public static void main(String[] args) {
			Name instance = new Name(args);
			instance.run();
		}

		@Override
		public void run() {
			// Declarations and statements from block
			
		}
	}*/

}
