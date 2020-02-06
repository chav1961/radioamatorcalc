package chav1961.calc.script;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import chav1961.calc.script.ScriptProcessor.ComparisonType;
import chav1961.calc.script.ScriptProcessor.ExpressionDepth;
import chav1961.calc.script.ScriptProcessor.FunctionType;
import chav1961.calc.script.ScriptProcessor.Lexema;
import chav1961.calc.script.ScriptProcessor.LexemaType;
import chav1961.calc.script.ScriptProcessor.SyntaxNodeType;
import chav1961.purelib.basic.CharUtils;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.cdb.SyntaxNode;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.FieldFormat;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public class ScriptProcessorTest {
	@Test
	public void buildLexemaListTest() throws SyntaxException {
		List<Lexema>	lex;
		
		lex = ScriptProcessor.buildLexemaList(" ");
		Assert.assertEquals(1,lex.size());
		Assert.assertEquals(LexemaType.LexEOF,lex.get(0).type);

		lex = ScriptProcessor.buildLexemaList(";()+-*/,");
		Assert.assertEquals(9,lex.size());
		Assert.assertEquals(LexemaType.LexEndOp,lex.get(0).type);
		Assert.assertEquals(LexemaType.LexOpen,lex.get(1).type);
		Assert.assertEquals(LexemaType.LexClose,lex.get(2).type);
		Assert.assertEquals(LexemaType.LexPlus,lex.get(3).type);
		Assert.assertEquals(LexemaType.LexMinus,lex.get(4).type);
		Assert.assertEquals(LexemaType.LexMul,lex.get(5).type);
		Assert.assertEquals(LexemaType.LexDiv,lex.get(6).type);
		Assert.assertEquals(LexemaType.LexList,lex.get(7).type);
		Assert.assertEquals(LexemaType.LexEOF,lex.get(8).type);

		lex = ScriptProcessor.buildLexemaList("> >= < <= = <> :=");
		Assert.assertEquals(8,lex.size());
		Assert.assertEquals(LexemaType.LexGT,lex.get(0).type);
		Assert.assertEquals(LexemaType.LexGE,lex.get(1).type);
		Assert.assertEquals(LexemaType.LexLT,lex.get(2).type);
		Assert.assertEquals(LexemaType.LexLE,lex.get(3).type);
		Assert.assertEquals(LexemaType.LexEQ,lex.get(4).type);
		Assert.assertEquals(LexemaType.LexNE,lex.get(5).type);
		Assert.assertEquals(LexemaType.LexAssign,lex.get(6).type);
		Assert.assertEquals(LexemaType.LexEOF,lex.get(7).type);

		lex = ScriptProcessor.buildLexemaList("5 3.5 \"123\"");
		Assert.assertEquals(4,lex.size());
		Assert.assertEquals(LexemaType.LexInt,lex.get(0).type);
		Assert.assertEquals(LexemaType.LexReal,lex.get(1).type);
		Assert.assertEquals(LexemaType.LexString,lex.get(2).type);
		Assert.assertEquals(LexemaType.LexEOF,lex.get(3).type);
		
		lex = ScriptProcessor.buildLexemaList("name sin");
		Assert.assertEquals(3,lex.size());
		Assert.assertEquals(LexemaType.LexName,lex.get(0).type);
		Assert.assertEquals(LexemaType.LexFSin,lex.get(1).type);
		Assert.assertEquals(LexemaType.LexEOF,lex.get(2).type);
		
		lex = ScriptProcessor.buildLexemaList("name1 // nameNone\nname2 name3");
		Assert.assertEquals(4,lex.size());
		Assert.assertEquals(LexemaType.LexName,lex.get(0).type);
		Assert.assertEquals(0,lex.get(0).row);
		Assert.assertEquals(0,lex.get(0).col);
		Assert.assertEquals(LexemaType.LexName,lex.get(1).type);
		Assert.assertEquals(1,lex.get(1).row);
		Assert.assertEquals(0,lex.get(1).col);
		Assert.assertEquals(LexemaType.LexName,lex.get(2).type);
		Assert.assertEquals(1,lex.get(2).row);
		Assert.assertEquals(6,lex.get(2).col);
		Assert.assertEquals(LexemaType.LexEOF,lex.get(3).type);
		

		try {ScriptProcessor.buildLexemaList(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		
		try {ScriptProcessor.buildLexemaList("? ");
			Assert.fail("Mandatory exception was not detected (unknown lexema)");
		} catch (SyntaxException exc) {
		}
		
		try {ScriptProcessor.buildLexemaList(":: ");
			Assert.fail("Mandatory exception was not detected (unknown lexema)");
		} catch (SyntaxException exc) {
		} 

		try {ScriptProcessor.buildLexemaList("\"test");
			Assert.fail("Mandatory exception was not detected (unquoted constant)");
		} catch (SyntaxException exc) {
		}
	}
	
	@Test
	public void buildExpressionTest() throws SyntaxException {
		final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	node = new SyntaxNode<SyntaxNodeType, SyntaxNode<?,?>>(0,0,SyntaxNodeType.NodeRoot,0,null);
		final Map<String,ContentNodeMetadata>				vars = new HashMap<>();
		final ContentNodeMetadata							varData = new ContentNodeMetadata() {
																@Override public Iterator<ContentNodeMetadata> iterator() {return null;}
																@Override public String getName() {return "testVar";}
																@Override public boolean mounted() {return false;}
																@Override public Class<?> getType() {return double.class;}
																@Override public String getLabelId() {return null;}
																@Override public String getTooltipId() {return null;}
																@Override public String getHelpId() {return null;}
																@Override public FieldFormat getFormatAssociated() {return null;}
																@Override public URI getApplicationPath() {return null;}
																@Override public URI getUIPath() {return null;}
																@Override public URI getRelativeUIPath() {return null;}
																@Override public URI getLocalizerAssociated() {return null;}
																@Override public URI getIcon() {return null;}
																@Override public ContentNodeMetadata getParent() {return null;}
																@Override public int getChildrenCount() {return 0;}
																@Override public ContentMetadataInterface getOwner() {return null;}
															};
		vars.put(varData.getName(),varData);
		
		// Term clauses
		ScriptProcessor.buildExpression(toLexArray("123"),0,ExpressionDepth.ExprTerm,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeGetInt,node.getType());
		Assert.assertEquals(123,node.value);
		ScriptProcessor.buildExpression(toLexArray("123.456"),0,ExpressionDepth.ExprTerm,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeGetReal,node.getType());
		Assert.assertEquals(Double.doubleToLongBits(123.456),node.value);

		ScriptProcessor.buildExpression(toLexArray(varData.getName()),0,ExpressionDepth.ExprTerm,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeGetVar,node.getType());
		Assert.assertEquals(varData,node.cargo);
		
		try {ScriptProcessor.buildExpression(toLexArray("unknownVar"),0,ExpressionDepth.ExprTerm,node,vars);
			Assert.fail("Mandatory exception was not detected (unknown var name)");
		} catch (SyntaxException exc) {
		}

		// Negation clauses
		ScriptProcessor.buildExpression(toLexArray("123"),0,ExpressionDepth.ExprUnary,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeGetInt,node.getType());
		Assert.assertEquals(123,node.value);

		ScriptProcessor.buildExpression(toLexArray("+123"),0,ExpressionDepth.ExprUnary,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeGetInt,node.getType());
		Assert.assertEquals(123,node.value);
		
		ScriptProcessor.buildExpression(toLexArray("-123"),0,ExpressionDepth.ExprUnary,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeNegation,node.getType());
		Assert.assertEquals(SyntaxNodeType.NodeGetInt,((SyntaxNode)node.cargo).getType());
		Assert.assertEquals(123,((SyntaxNode)node.cargo).value);

		// Multiplication clauses
		ScriptProcessor.buildExpression(toLexArray("123"),0,ExpressionDepth.ExprMul,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeGetInt,node.getType());
		Assert.assertEquals(123,node.value);
		
		ScriptProcessor.buildExpression(toLexArray("2*3/4"),0,ExpressionDepth.ExprMul,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeMul,node.getType());
		Assert.assertArrayEquals("**/".toCharArray(),(char[])node.cargo);
		Assert.assertEquals(3,node.children.length);
	
		// Addition clauses
		ScriptProcessor.buildExpression(toLexArray("123"),0,ExpressionDepth.ExprAdd,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeGetInt,node.getType());
		Assert.assertEquals(123,node.value);
		
		ScriptProcessor.buildExpression(toLexArray("2+3-4"),0,ExpressionDepth.ExprAdd,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeAdd,node.getType());
		Assert.assertArrayEquals("++-".toCharArray(),(char[])node.cargo);
		Assert.assertEquals(3,node.children.length);

		// Comparison clauses
		ScriptProcessor.buildExpression(toLexArray("123"),0,ExpressionDepth.ExprCmp,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeGetInt,node.getType());
		Assert.assertEquals(123,node.value);
		
		ScriptProcessor.buildExpression(toLexArray("3>4"),0,ExpressionDepth.ExprCmp,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeCmp,node.getType());
		Assert.assertEquals(ComparisonType.GT,node.cargo);
		Assert.assertEquals(2,node.children.length);
		
		ScriptProcessor.buildExpression(toLexArray("3>=4"),0,ExpressionDepth.ExprCmp,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeCmp,node.getType());
		Assert.assertEquals(ComparisonType.GE,node.cargo);
		Assert.assertEquals(2,node.children.length);

		ScriptProcessor.buildExpression(toLexArray("3<4"),0,ExpressionDepth.ExprCmp,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeCmp,node.getType());
		Assert.assertEquals(ComparisonType.LT,node.cargo);
		Assert.assertEquals(2,node.children.length);

		ScriptProcessor.buildExpression(toLexArray("3<=4"),0,ExpressionDepth.ExprCmp,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeCmp,node.getType());
		Assert.assertEquals(ComparisonType.LE,node.cargo);
		Assert.assertEquals(2,node.children.length);

		ScriptProcessor.buildExpression(toLexArray("3=4"),0,ExpressionDepth.ExprCmp,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeCmp,node.getType());
		Assert.assertEquals(ComparisonType.EQ,node.cargo);
		Assert.assertEquals(2,node.children.length);

		ScriptProcessor.buildExpression(toLexArray("3<>4"),0,ExpressionDepth.ExprCmp,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeCmp,node.getType());
		Assert.assertEquals(ComparisonType.NE,node.cargo);
		Assert.assertEquals(2,node.children.length);
		
		// Not clauses
		ScriptProcessor.buildExpression(toLexArray("123"),0,ExpressionDepth.ExprNot,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeGetInt,node.getType());
		Assert.assertEquals(123,node.value);
		
		ScriptProcessor.buildExpression(toLexArray("not 3"),0,ExpressionDepth.ExprNot,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeNot,node.getType());
		Assert.assertEquals(SyntaxNodeType.NodeGetInt,((SyntaxNode)node.cargo).getType());

		// And clauses
		ScriptProcessor.buildExpression(toLexArray("123"),0,ExpressionDepth.ExprAnd,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeGetInt,node.getType());
		Assert.assertEquals(123,node.value);
		
		ScriptProcessor.buildExpression(toLexArray("1 and 2"),0,ExpressionDepth.ExprAnd,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeAnd,node.getType());
		Assert.assertEquals(2,node.children.length);

		// Or clauses
		ScriptProcessor.buildExpression(toLexArray("123"),0,ExpressionDepth.ExprOr,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeGetInt,node.getType());
		Assert.assertEquals(123,node.value);
		
		ScriptProcessor.buildExpression(toLexArray("1 or 2"),0,ExpressionDepth.ExprOr,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeOr,node.getType());
		Assert.assertEquals(2,node.children.length);

		// Nested expressions
		ScriptProcessor.buildExpression(toLexArray("(1 or 2)"),0,ExpressionDepth.ExprOr,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeOr,node.getType());
		Assert.assertEquals(2,node.children.length);
	}

	@Test
	public void buildFunctionTest() throws SyntaxException {
		final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	node = new SyntaxNode<SyntaxNodeType, SyntaxNode<?,?>>(0,0,SyntaxNodeType.NodeRoot,0,null);
		final Map<String,ContentNodeMetadata>				vars = new HashMap<>();
		final ContentNodeMetadata							varData = new ContentNodeMetadata() {
																@Override public Iterator<ContentNodeMetadata> iterator() {return null;}
																@Override public String getName() {return "testVar";}
																@Override public boolean mounted() {return false;}
																@Override public Class<?> getType() {return double.class;}
																@Override public String getLabelId() {return null;}
																@Override public String getTooltipId() {return null;}
																@Override public String getHelpId() {return null;}
																@Override public FieldFormat getFormatAssociated() {return null;}
																@Override public URI getApplicationPath() {return null;}
																@Override public URI getUIPath() {return null;}
																@Override public URI getRelativeUIPath() {return null;}
																@Override public URI getLocalizerAssociated() {return null;}
																@Override public URI getIcon() {return null;}
																@Override public ContentNodeMetadata getParent() {return null;}
																@Override public int getChildrenCount() {return 0;}
																@Override public ContentMetadataInterface getOwner() {return null;}
															};
		vars.put(varData.getName(),varData);

		// Parameter list
		ScriptProcessor.buildFunctionCall(toLexArray("(123)"),0,FunctionType.ArcCos,1,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeFunc,node.getType());
		Assert.assertEquals(FunctionType.ArcCos,node.cargo);
		Assert.assertEquals(1,node.children.length);

		ScriptProcessor.buildFunctionCall(toLexArray("(123,456)"),0,FunctionType.ArcSin,2,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeFunc,node.getType());
		Assert.assertEquals(FunctionType.ArcSin,node.cargo);
		Assert.assertEquals(2,node.children.length);

		// Total process
		ScriptProcessor.buildExpression(toLexArray("sin(1)"),0,ExpressionDepth.ExprTerm,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeFunc,node.getType());
		Assert.assertEquals(FunctionType.Sin,node.cargo);
		Assert.assertEquals(1,node.children[0].value);

		ScriptProcessor.buildExpression(toLexArray("cos(1)"),0,ExpressionDepth.ExprTerm,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeFunc,node.getType());
		Assert.assertEquals(FunctionType.Cos,node.cargo);
		Assert.assertEquals(1,node.children[0].value);

		ScriptProcessor.buildExpression(toLexArray("tan(1)"),0,ExpressionDepth.ExprTerm,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeFunc,node.getType());
		Assert.assertEquals(FunctionType.Tan,node.cargo);
		Assert.assertEquals(1,node.children[0].value);

		ScriptProcessor.buildExpression(toLexArray("arcsin(1)"),0,ExpressionDepth.ExprTerm,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeFunc,node.getType());
		Assert.assertEquals(FunctionType.ArcSin,node.cargo);
		Assert.assertEquals(1,node.children[0].value);

		ScriptProcessor.buildExpression(toLexArray("arccos(1)"),0,ExpressionDepth.ExprTerm,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeFunc,node.getType());
		Assert.assertEquals(FunctionType.ArcCos,node.cargo);
		Assert.assertEquals(1,node.children[0].value);

		ScriptProcessor.buildExpression(toLexArray("arctan(1)"),0,ExpressionDepth.ExprTerm,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeFunc,node.getType());
		Assert.assertEquals(FunctionType.ArcTan,node.cargo);
		Assert.assertEquals(1,node.children[0].value);

		ScriptProcessor.buildExpression(toLexArray("exp(1)"),0,ExpressionDepth.ExprTerm,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeFunc,node.getType());
		Assert.assertEquals(FunctionType.Exp,node.cargo);
		Assert.assertEquals(1,node.children[0].value);

		ScriptProcessor.buildExpression(toLexArray("exp10(1)"),0,ExpressionDepth.ExprTerm,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeFunc,node.getType());
		Assert.assertEquals(FunctionType.Exp10,node.cargo);
		Assert.assertEquals(1,node.children[0].value);

		ScriptProcessor.buildExpression(toLexArray("ln(1)"),0,ExpressionDepth.ExprTerm,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeFunc,node.getType());
		Assert.assertEquals(FunctionType.Ln,node.cargo);
		Assert.assertEquals(1,node.children[0].value);

		ScriptProcessor.buildExpression(toLexArray("ln10(1)"),0,ExpressionDepth.ExprTerm,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeFunc,node.getType());
		Assert.assertEquals(FunctionType.Ln10,node.cargo);
		Assert.assertEquals(1,node.children[0].value);

		ScriptProcessor.buildExpression(toLexArray("sqrt(1)"),0,ExpressionDepth.ExprTerm,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeFunc,node.getType());
		Assert.assertEquals(FunctionType.Sqrt,node.cargo);
		Assert.assertEquals(1,node.children[0].value);
		
		try{ScriptProcessor.buildFunctionCall(toLexArray("(123)"),0,FunctionType.ArcSin,2,node,vars);
			Assert.fail("Mandatory exception was not detected (not enough arguments)");
		} catch (SyntaxException exc) {
		}
		
		try{ScriptProcessor.buildFunctionCall(toLexArray("(123,456)"),0,FunctionType.ArcSin,1,node,vars);
			Assert.fail("Mandatory exception was not detected (too many arguments)");
		} catch (SyntaxException exc) {
		} 

		try{ScriptProcessor.buildFunctionCall(toLexArray("123"),0,FunctionType.ArcSin,1,node,vars);
			Assert.fail("Mandatory exception was not detected (open bracket is missing)");
		} catch (SyntaxException exc) {
		}

		try{ScriptProcessor.buildFunctionCall(toLexArray("(123"),0,FunctionType.ArcSin,1,node,vars);
			Assert.fail("Mandatory exception was not detected (close bracket is missing)");
		} catch (SyntaxException exc) {
		}
	}	

	@Test
	public void buildOperatorsTest() throws SyntaxException {
		final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	node = new SyntaxNode<SyntaxNodeType, SyntaxNode<?,?>>(0,0,SyntaxNodeType.NodeRoot,0,null);
		final Map<String,ContentNodeMetadata>				vars = new HashMap<>();
		final ContentNodeMetadata							varData = new ContentNodeMetadata() {
																@Override public Iterator<ContentNodeMetadata> iterator() {return null;}
																@Override public String getName() {return "testVar";}
																@Override public boolean mounted() {return false;}
																@Override public Class<?> getType() {return double.class;}
																@Override public String getLabelId() {return null;}
																@Override public String getTooltipId() {return null;}
																@Override public String getHelpId() {return null;}
																@Override public FieldFormat getFormatAssociated() {return null;}
																@Override public URI getApplicationPath() {return null;}
																@Override public URI getUIPath() {return null;}
																@Override public URI getRelativeUIPath() {return null;}
																@Override public URI getLocalizerAssociated() {return null;}
																@Override public URI getIcon() {return null;}
																@Override public ContentNodeMetadata getParent() {return null;}
																@Override public int getChildrenCount() {return 0;}
																@Override public ContentMetadataInterface getOwner() {return null;}
															};
		vars.put(varData.getName(),varData);
		
		// Assignment
		ScriptProcessor.buildSequence(toLexArray(varData.getName()+":=123"),0,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeAssign,node.getType());
		Assert.assertEquals(varData,node.cargo);
		Assert.assertEquals(1,node.children.length);
		Assert.assertEquals(SyntaxNodeType.NodeGetInt,node.children[0].getType());

		// Print statement
		ScriptProcessor.buildSequence(toLexArray("print \"message\""),0,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodePrint,node.getType());
		Assert.assertEquals("message",node.cargo);
		Assert.assertEquals(0,node.children.length);
		
		ScriptProcessor.buildSequence(toLexArray("print \"message\", 123, 456"),0,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodePrint,node.getType());
		Assert.assertEquals("message",node.cargo);
		Assert.assertEquals(2,node.children.length);
		
		// Return statement
		ScriptProcessor.buildSequence(toLexArray("return"),0,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeReturn,node.getType());
		
		// Short if statement
		ScriptProcessor.buildSequence(toLexArray("if 1 then print \"greater\" endif"),0,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeShortIf,node.getType());
		Assert.assertEquals(SyntaxNodeType.NodeGetInt,((SyntaxNode)node.cargo).getType());
		Assert.assertEquals(1,node.children.length);
		Assert.assertEquals(SyntaxNodeType.NodePrint,node.children[0].getType());
		
		// Long if statement
		ScriptProcessor.buildSequence(toLexArray("if 1 then print \"greater\" else print \"less\" endif"),0,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeLongIf,node.getType());
		Assert.assertEquals(SyntaxNodeType.NodeGetInt,((SyntaxNode)node.cargo).getType());
		Assert.assertEquals(2,node.children.length);
		Assert.assertEquals(SyntaxNodeType.NodePrint,node.children[0].getType());
		Assert.assertEquals(SyntaxNodeType.NodePrint,node.children[1].getType());

		// Sequence statement
		ScriptProcessor.buildSequence(toLexArray("print \"a\"; print \"b\""),0,node,vars);
		Assert.assertEquals(SyntaxNodeType.NodeSequence,node.getType());
		Assert.assertEquals(2,node.children.length);
		Assert.assertEquals(SyntaxNodeType.NodePrint,node.children[0].getType());
		Assert.assertEquals(SyntaxNodeType.NodePrint,node.children[1].getType());

		// Errors
		try{ScriptProcessor.buildSequence(toLexArray(varData.getName()+" = 1"),0,node,vars);
			Assert.fail("Mandatory exception was not detected (no assignment operator)");
		} catch (SyntaxException exc) {
		}

		try{ScriptProcessor.buildSequence(toLexArray("if 1 then print endif"),0,node,vars);
			Assert.fail("Mandatory exception was not detected (no format in the print operator)");
		} catch (SyntaxException exc) {
		}
		
		try{ScriptProcessor.buildSequence(toLexArray("if 1 print \"z\" endif"),0,node,vars);
			Assert.fail("Mandatory exception was not detected (then clause is missing)");
		} catch (SyntaxException exc) { 
		}
 		try{ScriptProcessor.buildSequence(toLexArray("if 1 then print \"z\""),0,node,vars);
			Assert.fail("Mandatory exception was not detected (endif clause is missing)");
		} catch (SyntaxException exc) {
		}
 		try{ScriptProcessor.buildSequence(toLexArray("if 1 then print \"z\" else print \"t\""),0,node,vars);
			Assert.fail("Mandatory exception was not detected (endif clause is missing)");
		} catch (SyntaxException exc) {
		}

		try{ScriptProcessor.buildSequence(toLexArray("if 1 then print \"z\" print\"t\" endif"),0,node,vars);
			Assert.fail("Mandatory exception was not detected (else clause is missing)");
		} catch (SyntaxException exc) {
		}
	}	

	@Test
	public void buildSyntaxTreeTest() throws SyntaxException, EnvironmentException {
		final ContentMetadataInterface				model = ContentModelFactory.forXmlDescription(this.getClass().getResourceAsStream("testmodel.xml")); 
		SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	root;
		
		root = ScriptProcessor.buildSyntaxTree(toLexArray("print\"z\""),model);
		Assert.assertEquals(SyntaxNodeType.NodePrint,root.getType());
		
		root = ScriptProcessor.buildSyntaxTree(toLexArray("if x > 1 then print \"z\" else print\"t\" endif"),model);
		Assert.assertEquals(SyntaxNodeType.NodeSequence,root.getType());
		
		try {ScriptProcessor.buildSyntaxTree(null,model);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try {ScriptProcessor.buildSyntaxTree(toLexArray("if x > 1 then print \"z\" else print\"t\" endif"),null);
			Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}
		try {ScriptProcessor.buildSyntaxTree(toLexArray("unknown := 0"),null);
			Assert.fail("Mandatory exception was not detected (unknown variable name)");
		} catch (NullPointerException exc) {
		}
		try {ScriptProcessor.buildSyntaxTree(toLexArray("x := 0 y := 2"),null);
			Assert.fail("Mandatory exception was not detected (unparsed tail in the string)");
		} catch (NullPointerException exc) {
		}
	}	
	
	
	private static Lexema[] toLexArray(final String expr) throws SyntaxException {
		final List<Lexema>	lex = ScriptProcessor.buildLexemaList(expr);

		return lex.toArray(new Lexema[lex.size()]);
	}
}
