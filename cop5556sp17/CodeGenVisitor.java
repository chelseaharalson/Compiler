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
		cw.visitEnd(); //end of class
		
		//generate classfile and return it
		return cw.toByteArray();
	}

	// Store value of Expression into location indicated by IdentLValue
	// IMPORTANT:  
	// insert the following statement into your code for an Assignment Statement
	// after value of expression is put on top of stack and before it is written into the IdentLValue
	// if the type of elements is image, this should copy the image. use PLPRuntimeImageOps.copyImage
	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		if (assignStatement.getVar().get_Dec() instanceof ParamDec) {
			mv.visitVarInsn(ALOAD, 0); // load "this" to later be used for PUTFIELD as objRef
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
		//System.out.println("visitBinaryChain");
		binaryChain.getE0().setSave(false);
		binaryChain.getE0().visit(this, arg);
		if (binaryChain.getE0().get_TypeName() == TypeName.URL) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromURL", PLPRuntimeImageIO.readFromURLSig, false);
		}
		else if (binaryChain.getE0().get_TypeName() == TypeName.FILE) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromFile", PLPRuntimeImageIO.readFromFileDesc, false);
		}
		
		binaryChain.getE1().setSave(true);
		binaryChain.getE1().visit(this, arg);
		return null;
	}
	
	// Visit children to generate code to leave values of arguments on stack
    // perform operation, leaving result on top of the stack.  Expressions should
    // be evaluated from left to right consistent with the structure of the AST.
	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		//System.out.println("visitBinaryExpression");
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
			else if (e0Type == TypeName.IMAGE.getJVMTypeDesc() && e1Type == TypeName.IMAGE.getJVMTypeDesc()) {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "add", PLPRuntimeImageOps.addSig, false);
			}
		}
		else if (opKind.equals(MINUS)) {
			if (e0Type.equals("I") && e1Type.equals("I")) {
				mv.visitInsn(ISUB);
			}
			// IMAGE - IMAGE goes here
			else if (e0Type == TypeName.IMAGE.getJVMTypeDesc() && e1Type == TypeName.IMAGE.getJVMTypeDesc()) {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "sub", PLPRuntimeImageOps.subSig, false);
			}
		}
		else if (opKind.equals(TIMES)) {
			if (e0Type.equals("I") && e1Type.equals("I")) {
				mv.visitInsn(IMUL);
			}
			// IMAGE * INTEGER goes here
			// INTEGER * IMAGE goes here
			else if (e0Type == TypeName.IMAGE.getJVMTypeDesc() && e1Type == TypeName.INTEGER.getJVMTypeDesc()) {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);
			}
			else if (e0Type == TypeName.INTEGER.getJVMTypeDesc() && e1Type == TypeName.IMAGE.getJVMTypeDesc()) {
				mv.visitInsn(SWAP);
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);
			}
		}
		else if (opKind.equals(DIV)) {
			if (e0Type.equals("I") && e1Type.equals("I")) {
				mv.visitInsn(IDIV);
			}
			else if (e0Type == TypeName.IMAGE.getJVMTypeDesc() && e1Type == TypeName.INTEGER.getJVMTypeDesc()) {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "div", PLPRuntimeImageOps.divSig, false);
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
		// Added for assignment 6
		else if (opKind.equals(AND)) {
			if ( (e0Type.equals("Z") && e1Type.equals("Z")) ) {
				Label firstFalseLabel = new Label();
				Label secFalseLabel = new Label();
				mv.visitJumpInsn(IFEQ, firstFalseLabel);
				mv.visitJumpInsn(IFEQ, secFalseLabel);
				mv.visitInsn(ICONST_1);
				Label finishLabel = new Label();
				mv.visitJumpInsn(GOTO, finishLabel);
				mv.visitLabel(firstFalseLabel);
				mv.visitInsn(POP);
				mv.visitLabel(secFalseLabel);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(finishLabel);
			}
		}
		else if (opKind.equals(OR)) {
			if ( (e0Type.equals("Z") && e1Type.equals("Z")) ) {
				Label firstTrueLabel = new Label();
				Label secTrueLabel = new Label();
				mv.visitJumpInsn(IFNE, firstTrueLabel);
				mv.visitJumpInsn(IFNE, secTrueLabel);
				mv.visitInsn(ICONST_0);
				Label finishLabel = new Label();
				mv.visitJumpInsn(GOTO, finishLabel);
				mv.visitLabel(firstTrueLabel);
				mv.visitInsn(POP);
				mv.visitLabel(secTrueLabel);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(finishLabel);
			}
		}
		else if (opKind.equals(MOD)) {
			if ( (e0Type.equals("I") && e1Type.equals("I")) ) {
				mv.visitInsn(IREM);
			}
			else if (e0Type == TypeName.IMAGE.getJVMTypeDesc() && e1Type == TypeName.INTEGER.getJVMTypeDesc()) {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mod", PLPRuntimeImageOps.modSig, false);
			}
		}
		return "Z";
	}

	// Block ∷= List<Dec>  List<Statement>
	//			Decs are local variables in current scope of run method
	//			Statements are executed in run method
	//			Must label beginning and end of scope, and keep track of local variables, 
	//					their slot in the local variable array, and their range of visibility.
	//			If a statement was a BinaryChain, it will have left a value on top of the stack. 
	//			Check for this and pop it if necessary.
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
			if (s instanceof BinaryChain && (((BinaryChain) s).get_TypeName() == TypeName.NONE || 
					((BinaryChain) s).get_TypeName() == TypeName.FRAME)) {
			    mv.visitInsn(POP);
			 }
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

	// Generate code to invoke PLPRuntimeFrame.getScreenWidth or PLPRuntimeFrame.getScreenHeight
	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		if (constantExpression.firstToken.kind.equals(KW_SCREENHEIGHT) ) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenHeight", PLPRuntimeFrame.getScreenHeightSig, false);
		}
		else if (constantExpression.firstToken.kind.equals(KW_SCREENWIDTH) ) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenWidth", PLPRuntimeFrame.getScreenWidthSig, false);
		}
		return null;
	}

	// Assign a slot in the local variable array to this variable and save it in the new slot attribute in the Dec class
	// frame maps to cop5556sp17.PLPRuntimeFrame
	// image maps to java.awt.image.BufferedImage
	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		declaration.setSlotNumber(slotCount);
		slotCount++;
		
		String localDesc = declaration.firstToken.get_TypeName().getJVMTypeDesc();
		if (localDesc == TypeName.FRAME.getJVMTypeDesc()) {
			mv.visitInsn(ACONST_NULL);
			int slot = declaration.getSlotNumber();
			mv.visitVarInsn(ASTORE, slot);
		}
		return null;
	}
	
	// Assume that a reference to a BufferedImage is on top of the stack.
	// Generate code to invoke the appropriate method from PLPRuntimeFilterOps.
	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		// Visit the expressions
		for (int i = 0; i < filterOpChain.getArg().getExprList().size(); i++) {
			filterOpChain.getArg().getExprList().get(i).visit(this, arg);
		}

		// Generate code to invoke the appropriate method for PLPRuntimeFilterOps
		if (filterOpChain.getFirstToken().kind == OP_BLUR) {
			if (filterOpChain.getArrowKind() == ARROW) {
				mv.visitInsn(ACONST_NULL);
			}
			else if (filterOpChain.getArrowKind() == BARARROW) {
				mv.visitInsn(DUP);
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", PLPRuntimeImageOps.copyImageSig, false);
				mv.visitInsn(SWAP);
			}
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "blurOp", PLPRuntimeFilterOps.opSig, false);
		}
		else if (filterOpChain.getFirstToken().kind == OP_CONVOLVE) {
			if (filterOpChain.getArrowKind() == ARROW) {
				mv.visitInsn(ACONST_NULL);
			}
			else if (filterOpChain.getArrowKind() == BARARROW) {
				mv.visitInsn(DUP);
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", PLPRuntimeImageOps.copyImageSig, false);
				mv.visitInsn(SWAP);
			}
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "convolveOp", PLPRuntimeFilterOps.opSig, false);
		}
		else if (filterOpChain.getFirstToken().kind == OP_GRAY) {
			if (filterOpChain.getArrowKind() == ARROW) {
				mv.visitInsn(ACONST_NULL);
			}
			else if (filterOpChain.getArrowKind() == BARARROW) {
				mv.visitInsn(DUP);
			}
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "grayOp", PLPRuntimeFilterOps.opSig, false);
		}
		return null;
	}

	// Assume that a reference to a PLPRuntimeFrame is on top of the stack.
	// Visit the tuple elements to generate code to leave their values on top of the stack.
	// Generate code to invokethe appropriate method from PLPRuntimeFrame.
	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		//System.out.println("visitFrameOpChain");
		// Visit the expressions
		for (int i = 0; i < frameOpChain.getArg().getExprList().size(); i++) {
			frameOpChain.getArg().getExprList().get(i).visit(this, arg);
		}
		
		// Generate code to invoke the appropriate method for PLPRuntimeFrame
		if (frameOpChain.firstToken.kind == KW_FRAME) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame", PLPRuntimeFrame.createOrSetFrameSig, false);
		}
		else if (frameOpChain.firstToken.kind == KW_MOVE) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "moveFrame", PLPRuntimeFrame.moveFrameDesc, false);
		}
		else if (frameOpChain.firstToken.kind == KW_SHOW) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "showImage", PLPRuntimeFrame.showImageDesc, false);
		}
		else if (frameOpChain.firstToken.kind == KW_HIDE) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "hideImage", PLPRuntimeFrame.hideImageDesc, false);
		}
		else if (frameOpChain.firstToken.kind == KW_XLOC) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getXVal", PLPRuntimeFrame.getXValDesc, false);
		}
		else if (frameOpChain.firstToken.kind == KW_YLOC) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getYVal", PLPRuntimeFrame.getYValDesc, false);
		}
		else if (frameOpChain.firstToken.kind == KW_SCREENWIDTH) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenWidth", PLPRuntimeFrame.getScreenWidthSig, false);
		}
		else if (frameOpChain.firstToken.kind == KW_SCREENHEIGHT) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenHeight", PLPRuntimeFrame.getScreenHeightSig, false);
		}
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		//System.out.println("visitIdentChain");
		Dec d = identChain.get_Dec();
		if (identChain.getSave() == true) {
			if (d.getFirstToken().get_TypeName() == TypeName.INTEGER || d.getFirstToken().get_TypeName() == TypeName.IMAGE) {
				if (d instanceof ParamDec) {
					String variableName = d.getIdent().getText();
					String localDesc = d.getFirstToken().get_TypeName().getJVMTypeDesc();
					mv.visitVarInsn(ALOAD, 0); // load "this" to later be used for PUTFIELD as objRef
					mv.visitInsn(SWAP);
					mv.visitFieldInsn(PUTFIELD, className, variableName, localDesc);
				}
				else {
					int slot = d.getSlotNumber();
					if (d.getFirstToken().get_TypeName() == IMAGE) {
						mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", PLPRuntimeImageOps.copyImageSig, false);
						//System.out.println("Putting the top of the stack into an image after calling copyImage");
						mv.visitInsn(DUP);
						mv.visitVarInsn(ASTORE, slot);
					}
					else {
						mv.visitVarInsn(ISTORE, slot);
					}
				}
			}
			else if (d.getFirstToken().get_TypeName() == TypeName.FILE) {
				String variableName = d.getIdent().getText();
				String localDesc = d.getFirstToken().get_TypeName().getJVMTypeDesc();
				mv.visitVarInsn(ALOAD, 0); // load "this" to later be used for GETFIELD as objRef
				mv.visitFieldInsn(GETFIELD, className, variableName, localDesc);
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "write", PLPRuntimeImageIO.writeImageDesc, false);
			}
			else if (d.getFirstToken().get_TypeName() == TypeName.FRAME) {
				int slot = d.getSlotNumber();
				mv.visitVarInsn(ALOAD, slot);
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame", PLPRuntimeFrame.createOrSetFrameSig, false);
				mv.visitInsn(DUP);
				mv.visitVarInsn(ASTORE, slot);
			}
		}
		else {
			if (d instanceof ParamDec) {
				String variableName = d.getIdent().getText();
				String localDesc = d.getFirstToken().get_TypeName().getJVMTypeDesc();
				mv.visitVarInsn(ALOAD, 0); // load "this" to later be used for GETFIELD as objRef
				mv.visitFieldInsn(GETFIELD, className, variableName, localDesc);
				//System.out.println("Put a paramDec on the stack");
			}
			else {
				int slot = d.getSlotNumber();
				if (d.getFirstToken().get_TypeName() == IMAGE || d.getFirstToken().get_TypeName() == FRAME) {
					mv.visitVarInsn(ALOAD, slot);
				}
				else {
					mv.visitVarInsn(ILOAD, slot);
				}
			}
		}
		return null;
	}

	// Load value of variable (this could be a field or a local var)
	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		Dec d = identExpression.get_Dec();
		if (d instanceof ParamDec) {
			String variableName = d.getIdent().getText();
			String localDesc = d.getFirstToken().get_TypeName().getJVMTypeDesc();
			mv.visitVarInsn(ALOAD, 0); // load "this" to later be used for GETFIELD as objRef
			mv.visitFieldInsn(GETFIELD, className, variableName, localDesc);
		}
		else {
			int slot = d.getSlotNumber();
			if (d.getFirstToken().get_TypeName() == IMAGE) {
				mv.visitVarInsn(ALOAD, slot);
			}
			else {
				mv.visitVarInsn(ILOAD, slot);
			}
		}
		return null;
	}

	// Store value on top of stack to this variable (which could be a field or local var)
	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		Dec d = identX.get_Dec();
		if (d instanceof ParamDec) {
			String variableName = d.getIdent().getText();
			//System.out.println("VARIABLE NAME: " + variableName);
			String localDesc = d.getFirstToken().get_TypeName().getJVMTypeDesc();
			mv.visitFieldInsn(PUTFIELD, className, variableName, localDesc);
		}
		else {
			int slot = d.getSlotNumber();
			if (d.getFirstToken().get_TypeName() == IMAGE) {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", PLPRuntimeImageOps.copyImageSig, false);
				mv.visitVarInsn(ASTORE, slot);
			}
			else {
				mv.visitVarInsn(ISTORE, slot);
			}
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

	// Assume that a reference to a BufferedImage is on top of the stack.  
	// Visit the tuple elements to generate code to leave their values on top of the stack.
	// Generate code to invoke the appropriate method from PLPRuntimeImageOps or PLPRuntimeImageIO.
	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		//System.out.println("visitImageOpChain");
		// Visit the expressions
		for (int i = 0; i < imageOpChain.getArg().getExprList().size(); i++) {
			imageOpChain.getArg().getExprList().get(i).visit(this, arg);
		}
		
		// Generate code to invoke the appropriate method from PLPRuntimeImageOps or PLPRuntimeImageIO
		if (imageOpChain.firstToken.kind == KW_SCALE) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "scale", PLPRuntimeImageOps.scaleSig, false);
		}
		else if (imageOpChain.firstToken.kind == OP_HEIGHT) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeImageIO.BufferedImageClassName, "getHeight", PLPRuntimeImageOps.getHeightSig, false);
		}
		else if (imageOpChain.firstToken.kind == OP_WIDTH) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeImageIO.BufferedImageClassName, "getWidth", PLPRuntimeImageOps.getWidthSig, false);
		}
		/*else if (imageOpChain.firstToken.kind == PLUS) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "add", PLPRuntimeImageOps.addSig, false);
		}
		else if (imageOpChain.firstToken.kind == MINUS) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "sub", PLPRuntimeImageOps.subSig, false);
		}
		else if (imageOpChain.firstToken.kind == TIMES) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);
		}
		else if (imageOpChain.firstToken.kind == DIV) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "div", PLPRuntimeImageOps.divSig, false);
		}
		else if (imageOpChain.firstToken.kind == MOD) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mod", PLPRuntimeImageOps.modSig, false);
		}*/
		return null;
	}

	// Load constant
	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		mv.visitLdcInsn(intLitExpression.getFirstToken().intVal());
		return "I";
	}
	
	public void loadParam() {
		mv.visitVarInsn(ALOAD, 1); // load args[]
		mv.visitLdcInsn(globalsVisited); // load globalsVisited index
		mv.visitInsn(AALOAD); // put the args[globalsVistied] onto the stack
	}

	// Instance variable in class, initialized with values from arg array
	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		// For assignment 5, only needs to handle integers and booleans
		String variableName = paramDec.getIdent().getText();
		String localDesc = paramDec.getFirstToken().get_TypeName().getJVMTypeDesc();
		cw.visitField(ACC_PUBLIC, variableName, localDesc, null, null);
		
		mv.visitVarInsn(ALOAD, 0); // load "this" to later be used for PUTFIELD as objRef
		//System.out.println("Visiting the globals[" + globalsVisited + "]");
		mv.visitVarInsn(ALOAD, 1); // load args[]
		mv.visitLdcInsn(globalsVisited); // load globalsVisited index
		mv.visitInsn(AALOAD); // put the args[globalsVistied] onto the stack
		
		// check type of string (int/bool)
		if (localDesc == "I") {
			loadParam();
			//System.out.println("Putting the integer value into " + variableName);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(SWAP);
			mv.visitFieldInsn(PUTFIELD, className, variableName, "I"); // put the value into the variable
		}
		else if (localDesc == "Z") {
			loadParam();
			//System.out.println("Putting the boolean value into " + variableName);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(SWAP);
			mv.visitFieldInsn(PUTFIELD, className, variableName, "Z"); // put the value into the variable
		}
		// Added for assignment 6
		else if (localDesc == PLPRuntimeImageIO.FileDesc) {
			mv.visitTypeInsn(NEW, "java/io/File");
			mv.visitInsn(DUP);
			loadParam();
			mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(SWAP);
			mv.visitFieldInsn(PUTFIELD, className, variableName, PLPRuntimeImageIO.FileDesc); // put the value into the variable
		}
		else if (localDesc == PLPRuntimeImageIO.URLDesc) {
			mv.visitVarInsn(ALOAD, 1); // load args[]
			mv.visitLdcInsn(globalsVisited); // load globalsVisited index
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "getURL", PLPRuntimeImageIO.getURLSig, false);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(SWAP);
			mv.visitFieldInsn(PUTFIELD, className, variableName, PLPRuntimeImageIO.URLDesc); // put the value into the variable
		}
		
		globalsVisited++;
		return null;
	}

	// SleepStatement ∷= Expression
    //      invoke java/lang/Thread/sleep
	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		sleepStatement.getE().visit(this, arg);
		mv.visitInsn(I2L);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
		return null;
	}

	// Visit expressions to generate code to leave values on top of the stack
	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		//System.out.println("visitTuple");
		for (int i = 0; i < tuple.getExprList().size(); i++) {
			tuple.getExprList().get(i).visit(this, arg);
		}
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
		mv.visitJumpInsn(IFEQ, finishLabel);
		// if equal, continue and visit block
		whileStatement.getB().visit(this, arg);
		mv.visitJumpInsn(GOTO, expressionLabel);
		mv.visitLabel(finishLabel);
		return null;
	}
}
