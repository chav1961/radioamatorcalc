package chav1961.calc.script;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import chav1961.calc.script.ScriptProcessor.DataManager;
import chav1961.calc.script.ScriptProcessor.Lexema;
import chav1961.calc.script.ScriptProcessor.LexemaType;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.interfaces.Action;
import chav1961.purelib.ui.interfaces.Format;

public class ScriptProcessorTest {
	@Test
	public void buildLexemaListTest() throws SyntaxException {
		List<Lexema>	lex;
		
		lex = ScriptProcessor.buildLexemaList(" ",false,false);
		Assert.assertEquals(1,lex.size());
		Assert.assertEquals(LexemaType.LexEOF,lex.get(0).type);

		lex = ScriptProcessor.buildLexemaList(";()+-*/,.. .",false,false);
		Assert.assertEquals(11,lex.size());
		Assert.assertEquals(LexemaType.LexSemicolon,lex.get(0).type);
		Assert.assertEquals(LexemaType.LexOpen,lex.get(1).type);
		Assert.assertEquals(LexemaType.LexClose,lex.get(2).type);
		Assert.assertEquals(LexemaType.LexPlus,lex.get(3).type);
		Assert.assertEquals(LexemaType.LexMinus,lex.get(4).type);
		Assert.assertEquals(LexemaType.LexMul,lex.get(5).type);
		Assert.assertEquals(LexemaType.LexDiv,lex.get(6).type);
		Assert.assertEquals(LexemaType.LexList,lex.get(7).type);
		Assert.assertEquals(LexemaType.LexRange,lex.get(8).type);
		Assert.assertEquals(LexemaType.LexDot,lex.get(9).type);
		Assert.assertEquals(LexemaType.LexEOF,lex.get(10).type);

		lex = ScriptProcessor.buildLexemaList("> >= < <= = <> := :",false,false);
		Assert.assertEquals(9,lex.size());
		Assert.assertEquals(LexemaType.LexGT,lex.get(0).type);
		Assert.assertEquals(LexemaType.LexGE,lex.get(1).type);
		Assert.assertEquals(LexemaType.LexLT,lex.get(2).type);
		Assert.assertEquals(LexemaType.LexLE,lex.get(3).type);
		Assert.assertEquals(LexemaType.LexEQ,lex.get(4).type);
		Assert.assertEquals(LexemaType.LexNE,lex.get(5).type);
		Assert.assertEquals(LexemaType.LexAssign,lex.get(6).type);
		Assert.assertEquals(LexemaType.LexColon,lex.get(7).type);
		Assert.assertEquals(LexemaType.LexEOF,lex.get(8).type);

		lex = ScriptProcessor.buildLexemaList("5 3.5 \"123\"",false,false);
		Assert.assertEquals(4,lex.size());
		Assert.assertEquals(LexemaType.LexIntValue,lex.get(0).type);
		Assert.assertEquals(LexemaType.LexRealValue,lex.get(1).type);
		Assert.assertEquals(LexemaType.LexStringValue,lex.get(2).type);
		Assert.assertEquals(LexemaType.LexEOF,lex.get(3).type);
		
		lex = ScriptProcessor.buildLexemaList("name sin",false,false);
		Assert.assertEquals(3,lex.size());
		Assert.assertEquals(LexemaType.LexName,lex.get(0).type);
		Assert.assertEquals(LexemaType.LexFSin,lex.get(1).type);
		Assert.assertEquals(LexemaType.LexEOF,lex.get(2).type);
		
		lex = ScriptProcessor.buildLexemaList("name1 // nameNone\nname2 name3 \n#10",false,false);
		Assert.assertEquals(5,lex.size());
		Assert.assertEquals(LexemaType.LexName,lex.get(0).type);
		Assert.assertEquals(0,lex.get(0).row);
		Assert.assertEquals(0,lex.get(0).col);
		Assert.assertEquals(LexemaType.LexName,lex.get(1).type);
		Assert.assertEquals(1,lex.get(1).row);
		Assert.assertEquals(0,lex.get(1).col);
		Assert.assertEquals(LexemaType.LexName,lex.get(2).type);
		Assert.assertEquals(1,lex.get(2).row);
		Assert.assertEquals(6,lex.get(2).col);
		Assert.assertEquals(LexemaType.LexPlugin,lex.get(3).type);
		Assert.assertEquals(2,lex.get(3).row);
		Assert.assertEquals(0,lex.get(3).col);
		Assert.assertEquals(LexemaType.LexEOF,lex.get(4).type);
		

		try {ScriptProcessor.buildLexemaList(null,false,false);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		
		try {ScriptProcessor.buildLexemaList("? ",false,false);
			Assert.fail("Mandatory exception was not detected (unknown lexema)");
		} catch (SyntaxException exc) {
		}
		
		try {ScriptProcessor.buildLexemaList("\"test",false,false);
			Assert.fail("Mandatory exception was not detected (unquoted constant)");
		} catch (SyntaxException exc) {
		}
	}

	@Test
	public void simpleExpressionTest() throws SyntaxException {
		final Object[]		val = new Object[1];
		final DataManager	mgr = new DataManager() {
										
										@Override public void setVar(int pluginId, String name, Object value) {}
										@Override public Object getVar(int pluginId, String name) {return null;}
										@Override public boolean exists(int pluginId, String name) {return false;}
			
										@Override
										public void print(final Object value) {
											val[0] = value;
										}
									};
									
		// Constants
		call("print true",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print false",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);
		call("print 10",mgr);
		Assert.assertEquals(Long.valueOf(10),val[0]);
		call("print 10.0",mgr);
		Assert.assertEquals(Double.valueOf(10.0),val[0]);
		call("print \"test\"",mgr);
		Assert.assertEquals("test",val[0]);
		
		// Prefix operators
		call("print -10",mgr);
		Assert.assertEquals(Long.valueOf(-10),val[0]);
		call("print -10.0",mgr);
		Assert.assertEquals(Double.valueOf(-10.0),val[0]);

		try{call("print - true",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}		

		call("print not true",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);
		call("print not false",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);

		try{call("print not 10",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}		
		
		// Function calls
		call("print sin(0)",mgr);
		Assert.assertEquals(Double.valueOf(0),val[0]);
		call("print cos(0)",mgr);
		Assert.assertEquals(Double.valueOf(1),val[0]);
		call("print tan(0)",mgr);
		Assert.assertEquals(Double.valueOf(0),val[0]);
		call("print exp(0)",mgr);
		Assert.assertEquals(Double.valueOf(1),val[0]);
		call("print ln(1)",mgr);
		Assert.assertEquals(Double.valueOf(0),val[0]);
		call("print sqrt(sqr(2))",mgr);
		Assert.assertEquals(Double.valueOf(2),val[0]);

		try{call("print sin 0",mgr);
			Assert.fail("Mandatory exception was not detected (open bracket is missing)");
		} catch (SyntaxException exc) {
		}		
		try{call("print sin(0",mgr);
			Assert.fail("Mandatory exception was not detected (close bracket is missing)");
		} catch (SyntaxException exc) {
		}		
		try{call("print sin(true)",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}		
	}

	@Test
	public void simpleInfixExpressionTest() throws SyntaxException {
		final Object[]		val = new Object[1];
		final DataManager	mgr = new DataManager() {
										
										@Override public void setVar(int pluginId, String name, Object value) {}
										@Override public Object getVar(int pluginId, String name) {return null;}
										@Override public boolean exists(int pluginId, String name) {return false;}
			
										@Override
										public void print(final Object value) {
											val[0] = value;
										}
									};

		// Multiplicative operators: * / div mod
		call("print 10*10",mgr);
		Assert.assertEquals(Long.valueOf(100),val[0]);
		call("print 10*10.0",mgr);
		Assert.assertEquals(Double.valueOf(100),val[0]);
		call("print 10/10",mgr);
		Assert.assertEquals(Double.valueOf(1.0),val[0]);
		call("print 10 div 10",mgr);
		Assert.assertEquals(Long.valueOf(1),val[0]);
		call("print 10.0 div 10",mgr);
		Assert.assertEquals(Long.valueOf(1),val[0]);
		call("print 5 mod 3",mgr);
		Assert.assertEquals(Long.valueOf(2),val[0]);
		call("print 5.0 mod 3",mgr);
		Assert.assertEquals(Long.valueOf(2),val[0]);

		try{call("print 5*true",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}
		try{call("print 5/true",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}
		try{call("print 5 div true",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}
		try{call("print 5 mod true",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}
		
		// Additive operators: + -
		call("print 10+10",mgr);
		Assert.assertEquals(Long.valueOf(20),val[0]);
		call("print 10-10",mgr);
		Assert.assertEquals(Long.valueOf(0),val[0]);
		call("print 10+10.0",mgr);
		Assert.assertEquals(Double.valueOf(20),val[0]);
		call("print 10-10.0",mgr);
		Assert.assertEquals(Double.valueOf(0),val[0]);

		call("print \"a\"+10",mgr);
		Assert.assertEquals("a10",val[0]);
		call("print 10+\"a\"",mgr);
		Assert.assertEquals("10a",val[0]);
		
		try{call("print 5+true",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}
		try{call("print 5-true",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}
		
		// Comparison operators without 'in' : > >= < <= = <>
		
		// - operator '>'		
		call("print 10 > 5",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print 10 > 5.0",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print 10 > 20",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);
		call("print 10 > 20.0",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);

		call("print \"b\" > \"a\"",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print \"a\" > \"b\"",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);

		try{call("print 5 > \"a\"",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}
		try{call("print \"a\" > 5",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}
		
		// - operator '>='		
		call("print 10 >= 5",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print 10 >= 5.0",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print 10 >= 20",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);
		call("print 10 >= 20.0",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);

		call("print \"b\" >= \"a\"",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print \"a\" >= \"b\"",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);

		try{call("print 5 >= \"a\"",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}
		try{call("print \"a\" >= 5",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}
		
		// - operator '<'		
		call("print 5 < 10",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print 5 < 10.0",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print 20 < 10",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);
		call("print 20 < 10.0",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);

		call("print \"a\" < \"b\"",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print \"b\" < \"a\"",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);
		
		try{call("print 5 < \"a\"",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}
		try{call("print \"a\" < 5",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}
		
		// - operator '<='		
		call("print 5 <= 10",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print 5 <= 10.0",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print 20 <= 10",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);
		call("print 20 <= 10.0",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);

		call("print \"a\" <= \"b\"",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print \"b\" <= \"a\"",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);

		try{call("print 5 <= \"a\"",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}
		try{call("print \"a\" <= 5",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}
		
		// - operator '='		
		call("print 5 = 5",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print 5 = 5.0",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print 5 = 10",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);
		call("print 5 = 10.0",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);

		call("print \"a\" = \"a\"",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print \"a\" = \"b\"",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);
		
		try{call("print 5 = \"a\"",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}
		try{call("print \"a\" = 5",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}
		
		// - operator '<>'		
		call("print 5 <> 10",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print 5 <> 10.0",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print 5 <> 5",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);
		call("print 5 <> 5.0",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);
		
		call("print \"a\" <> \"b\"",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print \"a\" <> \"a\"",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);
		
		try{call("print 5 <> \"a\"",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}
		try{call("print \"a\" <> 5",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}

		// 'and' operator:
		call("print true and true",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print true and false",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);
		
		try{call("print true and 10",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}

		// 'or' operator:
		call("print false or true",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print false or false",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);
		
		try{call("print false or 10",mgr);
			Assert.fail("Mandatory exception was not detected (incompatible types)");
		} catch (SyntaxException exc) {
		}
	}

	@Test
	public void chainedInfixExpressionTest() throws SyntaxException {
		final Object[]		val = new Object[1];
		final DataManager	mgr = new DataManager() {
										
										@Override public void setVar(int pluginId, String name, Object value) {}
										@Override public Object getVar(int pluginId, String name) {return null;}
										@Override public boolean exists(int pluginId, String name) {return false;}
			
										@Override
										public void print(final Object value) {
											val[0] = value;
										}
									};
									
		call("print 10 * 5 + 7 * 8",mgr);
		Assert.assertEquals(Long.valueOf(106),val[0]);
		call("print 10 * 5 > 7 + 8 and \"test1\" <> \"test2\"",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);

		try{call("print 5 < 10 = 3 < 10",mgr);
			Assert.fail("Mandatory exception was not detected (non-chained operators)");
		} catch (SyntaxException exc) {
		}
		try{call("print 10 * 10 /",mgr);
			Assert.fail("Mandatory exception was not detected (missing operand)");
		} catch (SyntaxException exc) {
		}
		try{call("print 10 + * 10",mgr);
			Assert.fail("Mandatory exception was not detected (missing operand)");
		} catch (SyntaxException exc) {
		}
	}	

	@Test
	public void rangesExpressionTest() throws SyntaxException {
		final Object[]		val = new Object[1];
		final DataManager	mgr = new DataManager() {
										
										@Override public void setVar(int pluginId, String name, Object value) {}
										@Override public Object getVar(int pluginId, String name) {return null;}
										@Override public boolean exists(int pluginId, String name) {return false;}
			
										@Override
										public void print(final Object value) {
											val[0] = value;
										}
									};
									
		call("print 10 in 10",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print 10 in 10.0",mgr);
		
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print 10 in 9..11",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print 10 in 9..11.0",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);

		call("print 10 in 9,10,11",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print 10 in 7..8,10..12",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print 10 in 20..25,40",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);

		call("print \"b\" in \"a\",\"b\"",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print \"b\" in \"a\"..\"c\"",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print \"e\" in \"a\"..\"c\",\"d\",\"e\"",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("print \"f\" in \"a\"..\"c\",\"d\",\"e\"",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);
		
		try{call("print 10 in",mgr);
			Assert.fail("Mandatory exception was not detected (empty list)");
		} catch (SyntaxException exc) {
		}
		try{call("print 10 in 10,",mgr);
			Assert.fail("Mandatory exception was not detected (empty list)");
		} catch (SyntaxException exc) {
		}
		try{call("print 10 in ,10",mgr);
			Assert.fail("Mandatory exception was not detected (empty list)");
		} catch (SyntaxException exc) {
		}
		try{call("print 10 in 10..,",mgr);
			Assert.fail("Mandatory exception was not detected (empty list)");
		} catch (SyntaxException exc) {
		}
		try{call("print 10 in 10,..20",mgr);
			Assert.fail("Mandatory exception was not detected (empty list)");
		} catch (SyntaxException exc) {
		}
	}	

	@Test
	public void localVarsTest() throws SyntaxException {
		final Object[]		val = new Object[1];
		final DataManager	mgr = new DataManager() {
										
										@Override public void setVar(int pluginId, String name, Object value) {}
										@Override public Object getVar(int pluginId, String name) {return null;}
										@Override public boolean exists(int pluginId, String name) {return false;}
			
										@Override
										public void print(final Object value) {
											val[0] = value;
										}
									};
		// Int declarations
		call("int x := 10; print x;",mgr);
		Assert.assertEquals(Long.valueOf(10),val[0]);
		call("int x := 10, y := 20; print x+y;",mgr);
		Assert.assertEquals(Long.valueOf(30),val[0]);

		// Real declarations
		call("real x := 10.0; print x;",mgr);
		Assert.assertEquals(Double.valueOf(10.0),val[0]);
		call("real x := 10, y := 20; print x+y;",mgr);
		Assert.assertEquals(Double.valueOf(30.0),val[0]);

		// String declarations
		call("str x := \"a\"; print x;",mgr);
		Assert.assertEquals("a",val[0]);
		call("str x := \"a\", y := \"b\"; print x+y;",mgr);
		Assert.assertEquals("ab",val[0]);

		// Boolean declarations
		call("bool x := true; print x;",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("bool x := true, y := true; print x and y;",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		
		// Nested variables
		call("int x := 10; begin int x := 20; print x; end;",mgr);
		Assert.assertEquals(Long.valueOf(20),val[0]);
		call("int x := 10; begin int x := 20; end; print x; ",mgr);
		Assert.assertEquals(Long.valueOf(10),val[0]);
		
		try{call("print unknown",mgr);
			Assert.fail("Mandatory exception was not detected (unknown local var)");
		} catch (SyntaxException exc) {
		}
	}	

	@Test
	public void pluginVarsTest() throws SyntaxException {
		final Object[]		val = new Object[2];
		final DataManager	mgr = new DataManager() {
										@Override public void setVar(int pluginId, String name, Object value) {val[1] = value;}
										@Override public Object getVar(int pluginId, String name) {return "test";}
										@Override public boolean exists(int pluginId, String name) {return "pluginVar".equals(name);}
			
										@Override
										public void print(final Object value) {
											val[0] = value;
										}
									};
									
		call("print #1.pluginVar",mgr);
		Assert.assertEquals("test",val[0]);
		call("#1.pluginVar := \"new test\"",mgr);
		Assert.assertEquals("new test",val[1]);

		try{call("print #1.unknown",mgr);
			Assert.fail("Mandatory exception was not detected (unknown plugin var)");
		} catch (SyntaxException exc) {
		}
	}	

	@Test
	public void assignmentTest() throws SyntaxException {
		final Object[]		val = new Object[2];
		final DataManager	mgr = new DataManager() {
										@Override public void setVar(int pluginId, String name, Object value) {val[1] = value;}
										@Override public Object getVar(int pluginId, String name) {return "test";}
										@Override public boolean exists(int pluginId, String name) {return "pluginVar".equals(name);}
							
										@Override
										public void print(final Object value) {
											val[0] = value;
										}
									};
									
		call("int x; x := 10; print x;",mgr);
		Assert.assertEquals(Long.valueOf(10),val[0]);
		call("real x; x := 10; print x;",mgr);
		Assert.assertEquals(Double.valueOf(10.0),val[0]);
		call("str x; x := \"test\"; print x;",mgr);
		Assert.assertEquals("test",val[0]);
		call("bool x; x := true; print x;",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		
		try{call("int x; x := true",mgr);
			Assert.fail("Mandatory exception was not detected (incompatibla data types)");
		} catch (SyntaxException exc) {
		}
		try{call("real x; x := true",mgr);
			Assert.fail("Mandatory exception was not detected (incompatibla data types)");
		} catch (SyntaxException exc) {
		}
		try{call("str x; x := true",mgr);
			Assert.fail("Mandatory exception was not detected (incompatibla data types)");
		} catch (SyntaxException exc) {
		}
		try{call("bool x; x := 10",mgr);
			Assert.fail("Mandatory exception was not detected (incompatibla data types)");
		} catch (SyntaxException exc) {
		}
	}	

	@Test
	public void ifAndCaseTest() throws SyntaxException {
		final Object[]		val = new Object[2];
		final DataManager	mgr = new DataManager() {
										@Override public void setVar(int pluginId, String name, Object value) {val[1] = value;}
										@Override public Object getVar(int pluginId, String name) {return "test";}
										@Override public boolean exists(int pluginId, String name) {return "pluginVar".equals(name);}
							
										@Override
										public void print(final Object value) {
											val[0] = value;
										}
									};

		// long 'if'
		call("if 5 = 5 then print true else print false",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("if 5 <> 5 then print true else print false",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);
		
		// short 'if'
		call("if 5 = 5 then print true",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		val[0] = null;
		call("if 5 <> 5 then print true",mgr);
		Assert.assertNull(val[0]);

		try{call("if 5 print true",mgr);
			Assert.fail("Mandatory exception was not detected (illegal if expression type)");
		} catch (SyntaxException exc) {
		}
		try{call("if 5 = 5 print true",mgr);
			Assert.fail("Mandatory exception was not detected (then missing)");
		} catch (SyntaxException exc) {
		}
		
		// case with 'else :'		
		call("case 5 of 3..6: print true else : print false end",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("case 5 of 1..2: print 10 of 3..6: print true else : print false end",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("case 2 of 3..6: print true else : print false end",mgr);
		Assert.assertEquals(Boolean.valueOf(false),val[0]);
		
		// case without 'else :'
		call("case 5 of 3..6: print true end",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		call("case 5 of 1..2: print 10 of 3..6: print true end",mgr);
		Assert.assertEquals(Boolean.valueOf(true),val[0]);
		val[0] = null;
		call("case 2 of 3..6: print true end",mgr);
		Assert.assertNull(val[0]);
		
		try{call("case 5 end",mgr);
			Assert.fail("Mandatory exception was not detected ('of' clause missing)");
		} catch (SyntaxException exc) {
		}
		try{call("case 5 of : end",mgr);
			Assert.fail("Mandatory exception was not detected ('of' expression missing)");
		} catch (SyntaxException exc) {
		}
	}	
	
	@Test
	public void loopsTest() throws SyntaxException {
		final Object[]		val = new Object[2];
		final DataManager	mgr = new DataManager() {
										@Override public void setVar(int pluginId, String name, Object value) {val[1] = value;}
										@Override public Object getVar(int pluginId, String name) {return "test";}
										@Override public boolean exists(int pluginId, String name) {return "pluginVar".equals(name);}
							
										@Override
										public void print(final Object value) {
											val[0] = value;
										}
									};
	
		// 'while' loop
		call("int x := 2; while x > 0 do begin print x; x := x - 1 end",mgr);
		Assert.assertEquals(Long.valueOf(1),val[0]);
		val[0] = null;
		call("int x := 0; while x > 0 do begin print x; x := x - 1 end",mgr);
		Assert.assertNull(val[0]);
		
		try{call("int x := 0; while x > 0 print x",mgr);
			Assert.fail("Mandatory exception was not detected ('do' clause missing)");
		} catch (SyntaxException exc) {
		}

		// 'repeat' loop
		call("int x := 2; repeat print x; x := x - 1 until x <= 0",mgr);
		Assert.assertEquals(Long.valueOf(1),val[0]);
		call("int x := 0; repeat print x; x := x - 1 until x <= 0",mgr);
		Assert.assertEquals(Long.valueOf(0),val[0]);
		
		try{call("int x := 2; repeat print x; x := x - 1 ",mgr);
			Assert.fail("Mandatory exception was not detected ('until' clause missing)");
		} catch (SyntaxException exc) {
		}
	
		// 'for' loop
		call("int x; for x in 1..10 do print x;",mgr);
		Assert.assertEquals(Long.valueOf(10),val[0]);
		call("int x; for x in 10..1 do print x;",mgr);
		Assert.assertEquals(Long.valueOf(1),val[0]);

		try{call("int x; for x = 1..10 do print x;",mgr);
			Assert.fail("Mandatory exception was not detected ('in' clause missing)");
		} catch (SyntaxException exc) {
		}
		try{call("int x; for x = 1..10 print x;",mgr);
			Assert.fail("Mandatory exception was not detected ('do' clause missing)");
		} catch (SyntaxException exc) {
		}
	}	

	@Test
	public void bcrTest() throws SyntaxException {
		final Object[]		val = new Object[2];
		final DataManager	mgr = new DataManager() {
										@Override public void setVar(int pluginId, String name, Object value) {val[1] = value;}
										@Override public Object getVar(int pluginId, String name) {return "test";}
										@Override public boolean exists(int pluginId, String name) {return "pluginVar".equals(name);}
							
										@Override
										public void print(final Object value) {
											val[0] = value;
										}
									};
		// break
		call("int x := 2; while x > 0 do begin print x; x := x - 1; break end",mgr);
		Assert.assertEquals(Long.valueOf(2),val[0]);
		call("int x := 2; repeat print x; x := x - 1; break; until x <= 0",mgr);
		Assert.assertEquals(Long.valueOf(2),val[0]);
		call("int x; for x in 1..10 do begin print x; break; end;",mgr);
		Assert.assertEquals(Long.valueOf(1),val[0]);
		call("int x, y; for x in 1..10 do for y in 1..10 do begin print x; break 2; end;",mgr);
		Assert.assertEquals(Long.valueOf(1),val[0]);
		
		try{call("break;",mgr);
			Assert.fail("Mandatory exception was not detected (break outside loop)");
		} catch (SyntaxException exc) {
		}

		// continue
		val[0] = null;
		call("int x := 2; while x > 0 do begin x := x - 1; if x = 0 then continue; print x; end; ",mgr);
		Assert.assertEquals(Long.valueOf(1),val[0]);
		call("int x := 2; repeat print x; x := x - 1; if x mod 2 = 0 then continue; until x <= 0",mgr);
		Assert.assertEquals(Long.valueOf(0),val[0]);
		call("int x; for x in 1..10 do begin if x mod 2 = 0 then continue; print x; end;",mgr);
		Assert.assertEquals(Long.valueOf(9),val[0]);
		call("int x, y ; for x in 1..10 do for y in 1..10 do begin if x mod 2 = 0 then continue 2; print x; end;",mgr);
		Assert.assertEquals(Long.valueOf(9),val[0]);
		
		try{call("continue;",mgr);
			Assert.fail("Mandatory exception was not detected (break outside loop)");
		} catch (SyntaxException exc) {
		}

		// return
		val[0] = null;
		call("int x := 2; while x > 0 do begin x := x - 1; if x = 0 then return; print x; end; ",mgr);
		Assert.assertEquals(Long.valueOf(1),val[0]);
		call("int x := 2; repeat print x; x := x - 1; return; until x <= 0",mgr);
		Assert.assertEquals(Long.valueOf(2),val[0]);
		call("int x; for x in 1..10 do begin if x mod 2 = 0 then return; print x; end;",mgr);
		Assert.assertEquals(Long.valueOf(1),val[0]);
		call("int x, y ; for x in 1..10 do for y in 1..10 do begin if x mod 2 = 0 then return; print x; end;",mgr);
		Assert.assertEquals(Long.valueOf(1),val[0]);
	}	

	private void call(final String code, final DataManager mgr) throws SyntaxException, IllegalArgumentException, NullPointerException {
		final List<Lexema>	list = ScriptProcessor.buildLexemaList(code,false,false);						
		ScriptProcessor.execute(list.toArray(new Lexema[list.size()]), mgr);
	}
}
