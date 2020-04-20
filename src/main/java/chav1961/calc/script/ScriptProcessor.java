package chav1961.calc.script;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import chav1961.calc.script.LocalVarStack.ValueType;
import chav1961.calc.script.ScriptProcessor.DataManager;
import chav1961.calc.script.ScriptProcessor.Lexema;
import chav1961.purelib.basic.AndOrTree;
import chav1961.purelib.basic.CharUtils;
import chav1961.purelib.basic.SequenceIterator;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.SyntaxTreeInterface;

//	Language BNF:
// 
//	<program> ::= <operator> [';'...]
//  <operator> ::= {<assignment>|<if>|<while>|<until>|<for>|<case>|<break>|<continue>|<return>|<print>|<call>|<sequence>|<declarations>}
//  <assignment> ::= <leftPart> ':=' <expression>
//  <if> ::= 'if' <expression> 'then' <operator> ['else' <operator>]
//  <while> ::= 'while' <expression> 'do' <operator>
//  <until> ::= 'repeat' <operator> 'until' <expression>
//  <for> ::= 'for' <name> 'in' <rangeList> 'do' <operator>
//  <case> ::= 'case' <expression> ('of' <rangeList> ':' <operator>)... ['else' ':' <operator>] 'end'  
//  <break> ::= 'break' [<intValue>] 		// break thru N nested loops, default = 1
//	<continue> ::= 'continue' [<intValue>] 	// break thru N-1 nested loops and continue, default = 1
//  <return> ::= 'return'
//  <print> ::= 'print' <expression> [(',' <expression)...]
//  <call> ::= -- to be defined later
//  <sequence> ::= 'begin' <operator> [(';' <operator>)...] 'end'
//  <declarations> ::= {<intDecl>|<reatDecl>|<strDecl>|<boolDecl>}
//  <intDecl> ::= 'int' <name> [:= <expression>] [(',' <name> [:= <expression>] )...]
//	<realDecl> ::= 'real' <name> [:= <expression>] [(',' <name> [:= <expression>] )...]
//	<strDecl> ::= 'str' <name> [:= <expression>] [(',' <name> [:= <expression>] )...]
//	<boolDecl> ::= 'bool' <name> [:= <expression>] [(',' <name> [:= <expression>])...]
//  <rangeList> ::= <rangeItem> [(',' <rangeItem>)...]
//  <rangeItem ::= <expression>['..' <expression>]
//
//	<leftPart> ::= [<pluginId> '.']<name>
//  <expression> ::= <andExpr> [('or' <andExpr>)...]
//	<andExpr> ::= <notExpr> [('and' <notExpr>)...]
//	<notExpr> ::= ['not'] <comparison>
//  <comparison> ::= {<addExpr> {'>='|'>'|'<='|'<'|'='|'<>'} <addExpr> | <addExpr> 'in' <rangeList>}
//  <addExpr> ::= <mulExpr> [({'+'|'-'} <mulExpr>)...]
//	<mulExpr> ::= <unaryExpr> [({'*'|'/'|'div'|'mod'} <unaryExpr>)...]
//  <unaryExpr> ::= [{'+'|'-'}]<term>
//  <term> ::= {<constant>|<name>|<pluginId>.<name>|<function>|'('<expression>')'} 
//  <constant> ::= {<intValue> | <realValue> | <strValue> | 'true' | 'false' }
//	<name> ::= <letter>[({<letter>|<digit>})...]
//  <pluginId> ::= '#'<intValue>
//	<function> ::= <functionName>'('[<expression> [(',' <expression>)...]]')'
//  <functionName> ::= {'sin' | 'cos' | 'tan' | 'arcsin' | 'arccos' | 'arctan' | 'exp' | 'exp10' | 'ln' | 'log10' | 'sqr' | 'sqrt'} 
//

public class ScriptProcessor {
	private static final char 								LEX_TERMINAL = '\uFFFF';
	private static final SyntaxTreeInterface<LexemaType>	KEYWORDS = new AndOrTree<>();
	private static final double								LOG10 = Math.log(10);
	private static final double								INV_LOG10 = 1/LOG10;
	private static final int								CURRENT_MASK = 0xFF000000;
	private static final int								CURRENT_VALUE = 0x00FFFFFF;
	private static final int								CURRENT_JUMPS = 0x80000000;
	private static final int								CURRENT_RETURN = 0x40000000;
	private static final int								CURRENT_BREAK = 0x20000000;
	private static final int								CURRENT_CONTINUE = 0x10000000;
	private static final int								CURRENT_JUMP_DEPTH = 0x0F000000;
	private static final int								CURRENT_JUMP_DEPTH_SHIFT = 24;
	
	static {
		KEYWORDS.placeName("if",LexemaType.LexIf);
		KEYWORDS.placeName("then",LexemaType.LexThen);
		KEYWORDS.placeName("else",LexemaType.LexElse);
		KEYWORDS.placeName("while",LexemaType.LexWhile);
		KEYWORDS.placeName("do",LexemaType.LexDo);
		KEYWORDS.placeName("repeat",LexemaType.LexRepeat);
		KEYWORDS.placeName("until",LexemaType.LexUntil);
		KEYWORDS.placeName("for",LexemaType.LexFor);
		KEYWORDS.placeName("case",LexemaType.LexCase);
		KEYWORDS.placeName("of",LexemaType.LexOf);
		KEYWORDS.placeName("break",LexemaType.LexBreak);
		KEYWORDS.placeName("continue",LexemaType.LexContinue);
		KEYWORDS.placeName("return",LexemaType.LexReturn);
		KEYWORDS.placeName("begin",LexemaType.LexBegin);
		KEYWORDS.placeName("end",LexemaType.LexEnd);
		KEYWORDS.placeName("print",LexemaType.LexPrint);
		KEYWORDS.placeName("call",LexemaType.LexCall);
		KEYWORDS.placeName("div",LexemaType.LexIDiv);
		KEYWORDS.placeName("mod",LexemaType.LexMod);
		KEYWORDS.placeName("in",LexemaType.LexIn);
		KEYWORDS.placeName("and",LexemaType.LexAnd);
		KEYWORDS.placeName("not",LexemaType.LexNot);
		KEYWORDS.placeName("or",LexemaType.LexOr);
		KEYWORDS.placeName("sin",LexemaType.LexFSin);
		KEYWORDS.placeName("cos",LexemaType.LexFCos);
		KEYWORDS.placeName("tan",LexemaType.LexFTan);
		KEYWORDS.placeName("arcsin",LexemaType.LexFASin);
		KEYWORDS.placeName("arccos",LexemaType.LexFACos);
		KEYWORDS.placeName("arctan",LexemaType.LexFATan);
		KEYWORDS.placeName("exp",LexemaType.LexFExp);
		KEYWORDS.placeName("exp10",LexemaType.LexFExp10); 
		KEYWORDS.placeName("ln",LexemaType.LexFLn);
		KEYWORDS.placeName("ln10",LexemaType.LexFLog10); 
		KEYWORDS.placeName("sqr",LexemaType.LexFSqr);
		KEYWORDS.placeName("sqrt",LexemaType.LexFSqrt);
		KEYWORDS.placeName("int",LexemaType.LexInt);
		KEYWORDS.placeName("real",LexemaType.LexReal);
		KEYWORDS.placeName("str",LexemaType.LexString);
		KEYWORDS.placeName("bool",LexemaType.LexBoolean);
		KEYWORDS.placeName("true",LexemaType.LexTrue);
		KEYWORDS.placeName("false",LexemaType.LexFalse);
	}
	
	public enum LexemaType {
		LexEOF, LexOpen, LexClose, 
		LexDot, LexList, LexRange, 
		LexColon, LexSemicolon,
		LexPlus(GroupType.GroupAdd), LexMinus(GroupType.GroupAdd), LexMul(GroupType.GroupMul), LexDiv(GroupType.GroupMul), LexIDiv(GroupType.GroupMul), LexMod(GroupType.GroupMul), 
		LexGE(GroupType.GroupCmp,ComparisonType.GE), LexGT(GroupType.GroupCmp,ComparisonType.GT), LexLE(GroupType.GroupCmp,ComparisonType.LE), LexLT(GroupType.GroupCmp,ComparisonType.LT),
		LexEQ(GroupType.GroupCmp,ComparisonType.EQ), LexNE(GroupType.GroupCmp,ComparisonType.NE), LexIn(GroupType.GroupCmp,ComparisonType.IN), 
		LexFSin(GroupType.GroupFunc,(x)->Math.sin(x)), LexFCos(GroupType.GroupFunc,(x)->Math.cos(x)), LexFTan(GroupType.GroupFunc,(x)->Math.tan(x)),
		LexFASin(GroupType.GroupFunc,(x)->Math.asin(x)), LexFACos(GroupType.GroupFunc,(x)->Math.acos(x)), LexFATan(GroupType.GroupFunc,(x)->Math.atan(x)), 
		LexFExp(GroupType.GroupFunc,(x)->Math.exp(x)), LexFExp10(GroupType.GroupFunc,(x)->Math.exp(x)),
		LexFLn(GroupType.GroupFunc,(x)->Math.log(x)), LexFLog10(GroupType.GroupFunc,(x)->Math.log(x)), 
		LexFSqr(GroupType.GroupFunc,(x)->x*x), LexFSqrt(GroupType.GroupFunc,(x)->Math.sqrt(x)),
		LexAnd(GroupType.GroupAnd), LexOr(GroupType.GroupOr), LexNot(GroupType.GroupNot), LexAssign(GroupType.GroupAssign), 
		LexInt(GroupType.GroupDeclaration), LexReal(GroupType.GroupDeclaration), LexString(GroupType.GroupDeclaration), LexBoolean(GroupType.GroupDeclaration), 
		LexPlugin(GroupType.GroupReference), LexName(GroupType.GroupReference), 
		LexIntValue(GroupType.GroupConst), LexRealValue(GroupType.GroupConst), LexStringValue(GroupType.GroupConst), LexTrue(GroupType.GroupConst), LexFalse(GroupType.GroupConst), 
		LexIf(GroupType.GroupOperator), LexThen(GroupType.GroupOperator), LexElse(GroupType.GroupOperator), 
		LexWhile(GroupType.GroupOperator), LexDo(GroupType.GroupOperator), 
		LexRepeat(GroupType.GroupOperator), LexUntil(GroupType.GroupOperator),
		LexFor(GroupType.GroupOperator),
		LexCase(GroupType.GroupOperator), LexOf(GroupType.GroupOperator), LexOtherwise(GroupType.GroupOperator),
		LexBreak(GroupType.GroupOperator), LexContinue(GroupType.GroupOperator), LexReturn(GroupType.GroupOperator),
		LexBegin(GroupType.GroupOperator), LexEnd(GroupType.GroupOperator),
		LexPrint(GroupType.GroupOperator), 
		LexCall(GroupType.GroupOperator),
		LexError(GroupType.GroupError),
		LexComment(GroupType.GroupComment),
		LexRoot(GroupType.GroupOther);
		
		@FunctionalInterface
		private interface DoubleFunction {
			double process(double value);
		}
		
		private final GroupType			groupType;
		private final ComparisonType	comparisonType;
		private final DoubleFunction	doubleFunc;

		private LexemaType() {
			this.groupType = GroupType.GroupOther;
			this.comparisonType = null;
			this.doubleFunc = null;
		}
		
		private LexemaType(final GroupType groupType) {
			this.groupType = groupType;
			this.comparisonType = null;
			this.doubleFunc = null;
		}

		private LexemaType(final GroupType groupType, final ComparisonType comparisonType) {
			this.groupType = groupType;
			this.comparisonType = comparisonType;
			this.doubleFunc = null;
		}

		private LexemaType(final GroupType groupType, final DoubleFunction doubleFunc) {
			this.groupType = groupType;
			this.comparisonType = null;
			this.doubleFunc = doubleFunc;
		}
		
		public GroupType groupType() {
			return groupType;
		}
		
		public ComparisonType getComparisonType() {
			return comparisonType;
		}
		
		public double callDoubleFunc(double value) {
			return doubleFunc != null ? doubleFunc.process(value) : Double.NaN;
		}
	}

	enum ComparisonType {
		EQ, NE, GT, GE, LT, LE, IN
	}

	enum GroupType {
		GroupUnary, GroupMul, GroupAdd, GroupCmp, GroupNot, GroupAnd, GroupOr, GroupAssign, GroupFunc, GroupOperator, GroupDeclaration, GroupReference, GroupConst, GroupError, GroupComment, GroupOther
	}
	
	public static List<Lexema> buildLexemaList(final String content, final boolean suppressErrors, final boolean keepComments) throws SyntaxException, IllegalArgumentException, NullPointerException {
		if (content == null || content.isEmpty()) {
			throw new IllegalArgumentException("Content string can't be null or empty"); 
		}
		else {
			return buildLexemaList(CharUtils.terminateAndConvert2CharArray(content,LEX_TERMINAL),suppressErrors,keepComments);
		}
	}
	
	private static List<Lexema> buildLexemaList(final char[] source, final boolean suppressErrors, final boolean keepComments) throws SyntaxException, IllegalArgumentException, NullPointerException {
		final List<Lexema>	lexemas = new ArrayList<>();
		final StringBuilder	sb = new StringBuilder();
		final long[]		forLongs = new long[2];
		final int[]			forInts = new int[2]; 
		int					from = 0, start, row = 0, lastRowLocation = 0, col = 0;
		
loop:	for (;;) {
			from = CharUtils.skipBlank(source,from,true); 
			col = from - lastRowLocation;
			if (source[from] == '/' && source[from+1] == '/') {
				while (source[from] != '\n' && source[from] != LEX_TERMINAL) {
					from++;
				}
				if (keepComments) {
					lexemas.add(new Lexema(from,LexemaType.LexComment,row,col));
				}
			}
			switch (source[from]) {
				case LEX_TERMINAL :
					lexemas.add(new Lexema(from,LexemaType.LexEOF,row,col));
					break loop;
				case '\n'	:	// Only to calculate rows and columns
					lastRowLocation = ++from;
					row++;
					break;
				case '('	:
					lexemas.add(new Lexema(from,LexemaType.LexOpen,row,col));
					from++;
					break;
				case ')'	:
					lexemas.add(new Lexema(from,LexemaType.LexClose,row,col));
					from++;
					break;
				case '.'	:
					if (source[from+1] == '.') {
						lexemas.add(new Lexema(from,LexemaType.LexRange,row,col));
						from+=2;
					}
					else {
						lexemas.add(new Lexema(from,LexemaType.LexDot,row,col));
						from++;
					}
					break;
				case ','	:
					lexemas.add(new Lexema(from,LexemaType.LexList,row,col));
					from++;
					break;
				case ';'	:
					lexemas.add(new Lexema(from,LexemaType.LexSemicolon,row,col));
					from++;
					break;
				case '+'	:
					lexemas.add(new Lexema(from,LexemaType.LexPlus,row,col));
					from++;
					break;
				case '-'	:
					lexemas.add(new Lexema(from,LexemaType.LexMinus,row,col));
					from++;
					break;
				case '*'	:
					lexemas.add(new Lexema(from,LexemaType.LexMul,row,col));
					from++;
					break;
				case '/'	:
					lexemas.add(new Lexema(from,LexemaType.LexDiv,row,col));
					from++;
					break;
				case '>'	:
					if (source[from+1] == '=') {
						lexemas.add(new Lexema(from,LexemaType.LexGE,row,col));
						from += 2;
					}
					else {
						lexemas.add(new Lexema(from,LexemaType.LexGT,row,col));
						from++;
					}
					break;
				case '<'	:
					if (source[from+1] == '=') {
						lexemas.add(new Lexema(from,LexemaType.LexLE,row,col));
						from += 2;
					}
					else if (source[from+1] == '>') {
						lexemas.add(new Lexema(from,LexemaType.LexNE,row,col));
						from += 2;
					}
					else {
						lexemas.add(new Lexema(from,LexemaType.LexLT,row,col));
						from++;
					}
					break;
				case '='	:
					lexemas.add(new Lexema(from,LexemaType.LexEQ,row,col));
					from++;
					break;
				case ':'	:
					if (source[from+1] == '=') {
						lexemas.add(new Lexema(from,LexemaType.LexAssign,row,col));
						from += 2;
					}
					else if (!lexemas.isEmpty() && lexemas.get(lexemas.size()-1).type == LexemaType.LexElse) {
						final Lexema	old = lexemas.get(lexemas.size()-1);
						
						lexemas.set(lexemas.size()-1, new Lexema(old.displ,LexemaType.LexOtherwise,old.row,old.col));
						from++;
					}
					else {
						lexemas.add(new Lexema(from,LexemaType.LexColon,row,col));
						from++;
					}
					break;
				case '0' : case '1' : case '2' : case '3' : case '4' : case '5' : case '6' : case '7' : case '8' : case '9' :
					start = from;
					from = CharUtils.parseNumber(source,start,forLongs,CharUtils.PREF_LONG,true);
					if (source[from] == '.' && source[from+1] >= '0' && source[from+1] <= '9') {
						from = CharUtils.parseNumber(source,start,forLongs,CharUtils.PREF_LONG|CharUtils.PREF_DOUBLE,true);
					}
					if (forLongs[1] == CharUtils.PREF_INT || forLongs[1] == CharUtils.PREF_LONG) {
						lexemas.add(new Lexema(start,LexemaType.LexIntValue,row,col,forLongs[0]));
					}
					else {
						lexemas.add(new Lexema(start,LexemaType.LexRealValue,row,col,forLongs[0]));
					}
					break;
				case '#' :
					if (source[from+1] >= '0' && source[from+1] <= '9') {
						start = from;
						try{from = CharUtils.parseNumber(source,from+1,forLongs,CharUtils.PREF_INT,true);
							lexemas.add(new Lexema(start-1,LexemaType.LexPlugin,row,col,forLongs[0]));
						} catch (IllegalArgumentException exc) {
							processError(from,row,col,"Illegal plugin reference",lexemas,suppressErrors);
							from++;
						}
					}
					else {
						processError(from,row,col,"Illegal plugin reference",lexemas,suppressErrors);
						from++;
					}
					break;
				case '\"'	:
					try{start = from+1;
						sb.setLength(0);
						from = CharUtils.parseString(source,from+1,'\"',sb);
						
						lexemas.add(new Lexema(start-1,LexemaType.LexStringValue,row,col,sb.toString()));
					} catch (IllegalArgumentException exc) {
						processError(from,row,col,"Unquoted string",lexemas,suppressErrors);
						from++;
					}
					break;
				default :
					if (Character.isJavaIdentifierStart(source[from])) {
						start = from;
						from = CharUtils.parseName(source,from,forInts);
						final long 	id = KEYWORDS.seekName(source,forInts[0],forInts[1]+1);
						
						if (id >= 0) {
							lexemas.add(new Lexema(start,KEYWORDS.getCargo(id),row,col));
						}
						else {
							lexemas.add(new Lexema(start,LexemaType.LexName,row,col,new String(source,start,from-start)));
						}
					}
					else {
						processError(from,row,col,"Unknown lexema",lexemas,suppressErrors);
						from++;
					}
					break;
			}
		}
		return lexemas;
	}

	private static void processError(final int displ,final int row, final int col, final String message, final List<Lexema> lexemas, final boolean suppressErrors) throws SyntaxException {
		if (suppressErrors) {
			lexemas.add(new Lexema(displ,LexemaType.LexError,row,col,message));
		}
		else {
			throw new SyntaxException(row,col,message); 
		}
	}

	public interface DataManager {
		boolean exists(int pluginId, String name);
		Object getVar(int pluginId, String name);
		void setVar(int pluginId, String name, Object value);
		void print(Object value);
	}

	public static void execute(final String code, final DataManager mgr) throws SyntaxException {
		final List<Lexema>	list = ScriptProcessor.buildLexemaList(code,false,false);						
		ScriptProcessor.execute(list.toArray(new Lexema[list.size()]), mgr);
	}	

	public static void execute(final Lexema[] lex, final DataManager mgr) throws SyntaxException {
		try(final LocalVarStack 			locals = new LocalVarStack()) {
			final List<Object> 				stack = new ArrayList<>();
			final TerminalSet<LexemaType>	terminals = new TerminalSet<>(LexemaType.class,LexemaType.LexEOF);
			
			executeSequence(lex,0,false,mgr,terminals,locals,stack);
		}
	}

	public static int executeExpression(final String code, final DataManager mgr, final List<Object> stack) throws SyntaxException {
		try(final LocalVarStack	locals = new LocalVarStack()) {
			final List<Lexema>	list = ScriptProcessor.buildLexemaList(code,false,false);						
		
			return executeExpression(list.toArray(new Lexema[list.size()]),0,GroupType.GroupOr,mgr,locals,stack);
		}
	}
	
	static int executeSequence(final Lexema[] lex, final int from, final boolean loopControlAllows, final DataManager mgr, final TerminalSet<LexemaType> terminals, final LocalVarStack locals, final List<Object> stack) throws SyntaxException {
		int	current = from - 1;	// -1 - to pass semicolons on do/while
		
		try(final LocalVarStack	innerVars = locals.push()) {
			
			do {current = execute(lex,(current + 1) & CURRENT_VALUE,loopControlAllows,mgr,terminals,innerVars,stack);
			} while (current >= 0 && lex[current].type == LexemaType.LexSemicolon);
		}
		
		if (current < 0 || terminals.contains(lex[current & CURRENT_VALUE].type)) {
			return current;
		}
		else {
			throw new SyntaxException(lex[current].row, lex[current].row, "Unparsed tail in the program...");
		}
	}
	
	static int execute(final Lexema[] lex, final int from, final boolean loopControlAllows, final DataManager mgr, final TerminalSet<LexemaType> terminals, final LocalVarStack locals, final List<Object> stack) throws SyntaxException {
		int	current = from - 1;
		
		switch (lex[++current].type) {
			case LexInt			:
				current = declareLocals(lex,current,LocalVarStack.ValueType.INTEGER,mgr,locals,stack);
				break;
			case LexReal		:
				current = declareLocals(lex,current,LocalVarStack.ValueType.REAL,mgr,locals,stack);
				break;
			case LexString		:
				current = declareLocals(lex,current,LocalVarStack.ValueType.STRING,mgr,locals,stack);
				break;
			case LexBoolean		:
				current = declareLocals(lex,current,LocalVarStack.ValueType.BOOLEAN,mgr,locals,stack);
				break;
			case LexName		:
				current = executeAssign(lex,current,mgr,locals,stack);
				break;
			case LexPlugin		:
				current = executeAssign(lex,current,mgr,locals,stack);
				break;
			case LexIf			: 
				current = executeExpression(lex,current+1,GroupType.GroupOr,mgr,locals,stack);
				if (lex[current].type == LexemaType.LexThen) {
					if (getBooleanValue(stack,lex[current].row,lex[current].row)) {
						current = execute(lex,current+1,loopControlAllows,mgr,terminals,locals,stack);
						if (current < 0) {
							return current;
						}
						else if (lex[current].type == LexemaType.LexElse) {
							current = skipTail(lex,current+1,terminals);
						}
					}
					else {
						current = skipTail(lex,current+1,terminals.add(LexemaType.LexElse));
						if (lex[current].type == LexemaType.LexElse) {
							current = execute(lex,current+1,loopControlAllows,mgr,terminals,locals,stack);
							if (current < 0) {
								return current;
							}
						}
					}
				}
				else {
					throw new SyntaxException(lex[current].row, lex[current].row, "'then' clause is missing");
				}
				break;
			case LexWhile		:
				final int	startWhileLoop = current+1;

				for(;;) {
					current = executeExpression(lex,startWhileLoop,GroupType.GroupOr,mgr,locals,stack);
					if (lex[current].type == LexemaType.LexDo) {
						if (getBooleanValue(stack,lex[current].row,lex[current].row)) {
							current = execute(lex,current+1,true,mgr,terminals,locals,stack);
							if ((current & CURRENT_JUMPS) != 0) {
								if ((current & CURRENT_RETURN) != 0) {
									return current;
								}
								else if ((current & CURRENT_JUMP_DEPTH) != 0) {
									return current - (1 << CURRENT_JUMP_DEPTH_SHIFT);
								}
								else if ((current & CURRENT_BREAK) != 0) {
									current = skipTail(lex,startWhileLoop,terminals);
									break;
								}
								else if ((current & CURRENT_CONTINUE) != 0) {
									continue;
								}
							}
						}
						else {
							current = skipTail(lex,startWhileLoop+1,terminals);
							break;
						}
					}
					else {
						throw new SyntaxException(lex[current].row, lex[current].row, "'do' clause is missing");
					}
				} 
				break;
			case LexRepeat		:
				final int	startUntilLoop = current+1;
				
				for (;;) {
					current = executeSequence(lex,startUntilLoop,true,mgr,terminals.add(LexemaType.LexUntil),locals,stack);
					if ((current & CURRENT_JUMPS) != 0) {
						if ((current & CURRENT_RETURN) != 0) {
							return current;
						}
						else if ((current & CURRENT_JUMP_DEPTH) != 0) {
							return current - (1 << CURRENT_JUMP_DEPTH_SHIFT);
						}
						else if ((current & CURRENT_BREAK) != 0) {
							current = skipTail(lex,startUntilLoop-1,terminals);
							break;
						}
						else if ((current & CURRENT_CONTINUE) != 0) {
							continue;
						}
						current &= CURRENT_VALUE; 
					}
					if (lex[current].type == LexemaType.LexUntil) {
						current = executeExpression(lex,current+1,GroupType.GroupOr,mgr,locals,stack);
						if (getBooleanValue(stack,lex[current].row,lex[current].row)) {
							break;
						}
					}
					else {
						throw new SyntaxException(lex[current].row, lex[current].row, "'until' clause is missing");
					}
				}
				break;
			case LexFor			:
				final int	startForLoopHead = current;
				
				if (lex[current+1].type == LexemaType.LexName && lex[current+2].type == LexemaType.LexIn) {
					if (locals.exists(lex[current+1].stringContent)) {
						final String	localName = lex[current+1].stringContent;
						
						current = collectRangeList(lex,current+3,mgr,locals,stack);
						if (lex[current].type == LexemaType.LexDo) {
							final int	startForLoop = current+1;
							
							for (int value : iterateRange(stack,lex[current].row,lex[current].col)) {
								locals.setValue(localName,Long.valueOf(value));
								current = execute(lex,startForLoop,true,mgr,terminals,locals,stack);
								if ((current & CURRENT_JUMPS) != 0) {
									if ((current & CURRENT_RETURN) != 0) {
										return current;
									}
									else if ((current & CURRENT_JUMP_DEPTH) != 0) {
										return current - (1 << CURRENT_JUMP_DEPTH_SHIFT);
									}
									else if ((current & CURRENT_BREAK) != 0) {
										current = skipTail(lex,startForLoopHead,terminals);
										break;
									}
									current &= CURRENT_VALUE; 
								}
							}
						}
						else {
							throw new SyntaxException(lex[current].row, lex[current].row, "'do' clause is missing");
						}
					}
					current = skipTail(lex,startForLoopHead,terminals);
				}
				else {
					throw new SyntaxException(lex[current].row, lex[current].row, "name or 'in' clause is missing");
				}
				break;
			case LexCase		:
				final int	startCase = current;
				boolean		caseFound = false;				
				
				current = executeExpression(lex,current + 1,GroupType.GroupOr,mgr,locals,stack);
				if (lex[current].type == LexemaType.LexOf) {
					final Object	value = getAnyValue(stack,lex[current].row,lex[current].col);
					
					while (lex[current].type == LexemaType.LexOf) {
						current = collectRangeList(lex,current+1,mgr,locals,stack);
		 				if (lex[current].type == LexemaType.LexColon) {
							if (isInRange(value,stack,lex[current].row,lex[current].col)) {
								current = execute(lex,current+1,loopControlAllows,mgr,terminals.add(LexemaType.LexOf,LexemaType.LexOtherwise,LexemaType.LexEnd),locals,stack);
								current = skipTail(lex,startCase,terminals.add(LexemaType.LexEnd));
								caseFound = true;
							}
							else {
								current = skipTail(lex,current+1,terminals.add(LexemaType.LexOf,LexemaType.LexOtherwise,LexemaType.LexEnd));
							}
						}
						else {
							throw new SyntaxException(lex[current].row, lex[current].row, "colon (:) is missing");
						}
					}
					if (lex[current].type == LexemaType.LexOtherwise) {
						if (!caseFound) {
							current = execute(lex,current+1,loopControlAllows,mgr,terminals.add(LexemaType.LexEnd),locals,stack);
						}
						else {
							current = skipTail(lex,startCase,terminals.add(LexemaType.LexEnd));
						}
					}
					if (lex[current].type == LexemaType.LexEnd) {
						current++;
					}
					else {
						throw new SyntaxException(lex[current].row, lex[current].row, "'end' clause is missing");
					}
				}
				else {
					throw new SyntaxException(lex[current].row, lex[current].row, "'of' clause is missing");
				}
				break;
			case LexBreak		: 
				if (loopControlAllows) {
					if (lex[current+1].type == LexemaType.LexIntValue) {
						current = (current+2) | CURRENT_JUMPS | CURRENT_BREAK | (int)(((lex[current+1].numberContent-1) << CURRENT_JUMP_DEPTH_SHIFT) & CURRENT_JUMP_DEPTH);				
					}
					else {
						current = (current+1) | CURRENT_JUMPS | CURRENT_BREAK | (int)((lex[current+1].numberContent << CURRENT_JUMP_DEPTH_SHIFT) & CURRENT_JUMP_DEPTH);				
					}
				}
				else {
					throw new SyntaxException(lex[current].row, lex[current].row, "'break' clause outside loop");
				}
				break;
			case LexContinue	:
				if (loopControlAllows) {
					if (lex[current+1].type == LexemaType.LexIntValue) {
						current = (current+2) | CURRENT_JUMPS | CURRENT_CONTINUE | (int)(((lex[current+1].numberContent-1) << CURRENT_JUMP_DEPTH_SHIFT) & CURRENT_JUMP_DEPTH);				
					}
					else {
						current = (current+1) | CURRENT_JUMPS | CURRENT_CONTINUE | (int)((lex[current+1].numberContent << CURRENT_JUMP_DEPTH_SHIFT) & CURRENT_JUMP_DEPTH);				
					}
				}
				else {
					throw new SyntaxException(lex[current].row, lex[current].row, "'continue' clause outside loop");
				}
				break;
			case LexReturn		:
				current = (current+1) | CURRENT_JUMPS | CURRENT_RETURN;				
				break;
			case LexBegin		:
				current = executeSequence(lex,current+1,loopControlAllows,mgr,terminals.add(LexemaType.LexEnd),locals,stack);
				
				if (current < 0) {
					return current;
				}
				else {
					if (lex[current].type == LexemaType.LexEnd) {
						current++;
					}
					else {
						throw new SyntaxException(lex[current].row, lex[current].row, "'end' clause is missing");
					}
				}
				break;
			case LexPrint		: 
				do {current = executeExpression(lex,current+1,GroupType.GroupOr,mgr,locals,stack);
					mgr.print(getAnyValue(stack,lex[current].row,lex[current].row));
				} while (lex[current].type == LexemaType.LexList);
				break;
			case LexCall		:
				throw new SyntaxException(lex[from].row, lex[from].row, "Not supported yet...");
			default :
				if (terminals.contains(lex[current].type)) {
					break;
				}
				else {
					throw new SyntaxException(lex[from].row, lex[from].row, "Unwaited lexema");
				}
		}
		return current;
	}
	
	private static int declareLocals(final Lexema[] lex, final int from, final LocalVarStack.ValueType valueType, final DataManager mgr, final LocalVarStack locals, final List<Object> stack) throws SyntaxException {
		int					current = from, nameIndex;
		
		do {if (lex[++current].type == LexemaType.LexName) {
				nameIndex = current++;
				
				if (!locals.isDefined(lex[nameIndex].stringContent)) {
					if (lex[current].type == LexemaType.LexAssign) {
						switch (valueType) {
							case BOOLEAN	:
								current = executeExpression(lex,current+1,GroupType.GroupOr,mgr,locals,stack);
								locals.add(lex[nameIndex].stringContent, valueType, getBooleanValue(stack,lex[current].row,lex[current].col));
								break;
							case INTEGER	:
								current = executeExpression(lex,current+1,GroupType.GroupAdd,mgr,locals,stack);
								locals.add(lex[nameIndex].stringContent, valueType, getIntValue(stack,lex[current].row,lex[current].col));
								break;
							case REAL		:
								current = executeExpression(lex,current+1,GroupType.GroupAdd,mgr,locals,stack);
								locals.add(lex[nameIndex].stringContent, valueType, getRealValue(stack,lex[current].row,lex[current].col));
								break;
							case STRING		:
								current = executeExpression(lex,current+1,GroupType.GroupAdd,mgr,locals,stack);
								locals.add(lex[nameIndex].stringContent, valueType, getStringValue(stack,lex[current].row,lex[current].col));
								break;
							default : throw new UnsupportedOperationException("Value type ["+valueType+"] is not supported yet"); 
						}
					}
					else {
						locals.add(lex[nameIndex].stringContent, valueType);
					}
				}
				else {
					throw new SyntaxException(lex[nameIndex].row, lex[nameIndex].col, "duplicate name definition ["+lex[nameIndex].stringContent+"] in this block"); 
				}
			}
			else {
				throw new SyntaxException(lex[current].row, lex[current].col, "name is missing"); 
			}
		} while (lex[current].type == LexemaType.LexList);
		
		return current;
	}

	private static int executeAssign(final Lexema[] lex, final int from, final DataManager mgr, final LocalVarStack locals, final List<Object> stack) throws SyntaxException {
		int	current = from;
		
		if (lex[current].type == LexemaType.LexPlugin && lex[current+1].type == LexemaType.LexDot && lex[current+2].type == LexemaType.LexName) {
			if (mgr.exists((int)lex[current].numberContent,lex[current+2].stringContent)) {
				if (lex[current+3].type == LexemaType.LexAssign) {
					final int	left = current;
					
					current = executeExpression(lex,current+4,GroupType.GroupOr,mgr,locals,stack);
					mgr.setVar((int)lex[left].numberContent,lex[left+2].stringContent,getAnyValue(stack,lex[current].row,lex[current].col));
				}
				else {
					throw new SyntaxException(lex[current+3].row, lex[current+3].col, "Assignment operator ':=' is missing"); 
				}
			}
			else {
				throw new SyntaxException(lex[current].row, lex[current].col, "Plugin variable [#"+lex[current].numberContent+"."+lex[current+2].stringContent+"] is not exists"); 
			}
		}
		else if (lex[current].type == LexemaType.LexName) {
			if (locals.exists(lex[current].stringContent)) {
				if (lex[current+1].type == LexemaType.LexAssign) {
					final int	left = current;
					
					current = executeExpression(lex,current+2,GroupType.GroupOr,mgr,locals,stack);
					switch (locals.getType(lex[left].stringContent)) {
						case BOOLEAN	:
							locals.setValue(lex[left].stringContent,getBooleanValue(stack,lex[current].row,lex[current].col));
							break;
						case INTEGER	:
							locals.setValue(lex[left].stringContent,getIntValue(stack,lex[current].row,lex[current].col));
							break;
						case REAL		:
							locals.setValue(lex[left].stringContent,getRealValue(stack,lex[current].row,lex[current].col));
							break;
						case STRING		:
							locals.setValue(lex[left].stringContent,getStringValue(stack,lex[current].row,lex[current].col));
							break;
						default	: throw new UnsupportedOperationException("Value type ["+locals.getType(lex[left].stringContent)+"] is not supported yet"); 
					}
				}
				else {
					throw new SyntaxException(lex[current+3].row, lex[current+3].col, "Assignment operator ':=' is missing"); 
				}
			}
			else if (mgr.exists(-1,lex[current].stringContent)) {
				if (lex[current+1].type == LexemaType.LexAssign) {
					final int	left = current;
					
					current = executeExpression(lex,current+2,GroupType.GroupOr,mgr,locals,stack);
					mgr.setVar(-1,lex[left].stringContent,getAnyValue(stack,lex[current].row,lex[current].col));
				}
				else {
					throw new SyntaxException(lex[current+3].row, lex[current+3].col, "Assignment operator ':=' is missing"); 
				}
			}
			else {
				throw new SyntaxException(lex[current].row, lex[current].col, "Plugin or local variable ["+lex[current].stringContent+"] is not exists"); 
			}
		}
		else {
			throw new SyntaxException(lex[current].row, lex[current].col, "Plugin or local variable name awaited"); 
		}
		return current;
	}

	private static boolean getBooleanValue(final List<Object> stack, final int row, final int col) throws SyntaxException {
		final Object	obj = getAnyValue(stack,row,col);
		
		if (obj instanceof Boolean) {
			return ((Boolean)obj).booleanValue();
		}
		else {
			throw new SyntaxException(row,col,"Bool value awaiting"); 
		}
	}

	private static long getIntValue(final List<Object> stack, final int row, final int col) throws SyntaxException {
		final Object	obj = getAnyValue(stack,row,col);
		
		if (obj instanceof Number) {
			return ((Number)obj).longValue();
		}
		else {
			throw new SyntaxException(row,col,"Int value awaiting"); 
		}
	}

	private static double getRealValue(final List<Object> stack, final int row, final int col) throws SyntaxException {
		final Object	obj = getAnyValue(stack,row,col);
		
		if (obj instanceof Number) {
			return ((Number)obj).doubleValue();
		}
		else {
			throw new SyntaxException(row,col,"Real value awaiting"); 
		}
	}

	private static String getStringValue(final List<Object> stack, final int row, final int col) throws SyntaxException {
		final Object	obj = getAnyValue(stack,row,col);
		
		if (obj instanceof String) {
			return (String)obj;
		}
		else {
			throw new SyntaxException(row,col,"String value awaiting"); 
		}
	}
	
	private static Object getAnyValue(final List<Object> stack, final int row, final int col) throws SyntaxException {
		if (!stack.isEmpty()) {
			return stack.remove(0);
		}
		else {
			throw new SyntaxException(row,col,"empty expression result"); 
		}
	}
	
	private static int executeExpression(final Lexema[] lex, final int from, final GroupType maxGroup, final DataManager mgr, final LocalVarStack locals, final List<Object> stack) throws SyntaxException {
		final int	topStackDepth = stack.size();
		int			current = from;

loop:	for (;;) {
			switch (lex[current].type) {
				case LexIntValue:
					stack.add(lex[current].numberContent);
					current++;
					break;
				case LexStringValue:
					stack.add(lex[current].stringContent);
					current++;
					break;
				case LexRealValue:
					stack.add(Double.longBitsToDouble(lex[current].numberContent));
					current++;
					break;
				case LexTrue	:
					stack.add(true);
					current++;
					break;
				case LexFalse	:
					stack.add(false);
					current++;
					break;
				case LexPlugin	:
					if (lex[current+1].type == LexemaType.LexDot && lex[current+2].type == LexemaType.LexName) {
						if (mgr.exists((int)lex[current].numberContent,lex[current+2].stringContent)) {
							stack.add(mgr.getVar((int)lex[current].numberContent,lex[current+2].stringContent));
							current += 3;
						}
						else {
							throw new SyntaxException(lex[current].row,lex[current].col,"Plugin valiable [#"+lex[current].numberContent+"."+lex[current+2].stringContent+"] is missing");
						}
					}
					else {
						throw new SyntaxException(lex[current].row,lex[current].col,"Illegal plugin valiable syntax");
					}
					break;
				case LexName	:
					if (locals.exists(lex[current].stringContent)) {
						stack.add(locals.getValue(lex[current].stringContent));
						current++;
					}
					else if (mgr.exists(-1,lex[current].stringContent)) {
						stack.add(mgr.getVar(-1,lex[current].stringContent));
						current++;
					}
					else {
						throw new SyntaxException(lex[current].row,lex[current].col,"Plugin or local valiable ["+lex[current].stringContent+"] is missing");
					}
					break;
				case LexOpen	:
					current = executeExpression(lex,current+1,GroupType.GroupOr,mgr,locals,stack);
					if (lex[current].type == LexemaType.LexClose) {
						current++;
					}
					else {
						throw new SyntaxException(lex[current].row,lex[current].col,"Close bracket ')' is missing");
					}
					break;
				case LexFCos	: case LexFSin	: case LexFTan		: case LexFACos	: case LexFASin		:
				case LexFATan	: case LexFExp	: case LexFExp10	: case LexFLn	: case LexFLog10	:
				case LexFSqr	: case LexFSqrt	:
					final int	funcIndex = current++;
					
					if (lex[current].type == LexemaType.LexOpen) {
						current = executeExpression(lex,current+1,GroupType.GroupAdd,mgr,locals,stack);
						if (lex[current].type == LexemaType.LexClose) {
							current++;
							stack.add(lex[funcIndex].type.callDoubleFunc(getRealValue(stack,lex[funcIndex].row,lex[funcIndex].col)));
						}
						else {
							throw new SyntaxException(lex[current].row,lex[current].col,"Close bracket ')' is missing");
						}
					}
					else {
						throw new SyntaxException(lex[current].row,lex[current].col,"Open bracket '(' is missing");
					}
					break;
				case LexIn		:
					if (lex[current].type.groupType().compareTo(maxGroup) <= 0) {
						final List<Object>	inRange = new ArrayList<>();
						
						stack.add(lex[current]);
						current = collectRangeList(lex,current+1,mgr,locals,inRange);
						stack.add(inRange);
					}
					else {
						break loop;
					}
					break;					
				case LexPlus	: case LexMinus		:
					if (lex[current].type.groupType().compareTo(maxGroup) <= 0) {
						if (stack.size() == topStackDepth || (stack.get(stack.size()-1) instanceof Lexema)) {
							final Lexema	oper = lex[current]; 
							
							current = executeExpression(lex,current+1,GroupType.GroupUnary,mgr,locals,stack);
							if (oper.type == LexemaType.LexMinus) {
								stack.add(negate(stack.remove(stack.size()-1),oper.row,oper.col));
							}
						}
						else {
							stack.add(lex[current++]);
						}
					}
					else {
						break loop;
					}
					break;			
				case LexNot		:					
					if (lex[current].type.groupType().compareTo(maxGroup) <= 0) {
						if (stack.size() == topStackDepth || (stack.get(stack.size()-1) instanceof Lexema)) {
							final Lexema	oper = lex[current]; 
							
							current = executeExpression(lex,current+1,GroupType.GroupCmp,mgr,locals,stack);
							stack.add(not(stack.remove(stack.size()-1),oper.row,oper.col));
						}
						else {
							stack.add(lex[current++]);
						}
					}
					else {
						break loop;
					}
					break;			
				case LexEQ	: case LexGE	: case LexGT	:
				case LexLE	: case LexLT	: case LexNE	:
					if (stack.size() > topStackDepth + 2 && (stack.get(stack.size()-3) instanceof Lexema) && ((Lexema)stack.get(stack.size()-3)).type.groupType == GroupType.GroupCmp) {
						throw new SyntaxException(lex[current].row,lex[current].col,"Comparison oparators can't be chained, use brackets to define it's order");
					}
				case LexAnd	: case LexDiv	: case LexIDiv	: 
				case LexMod	: case LexMul	: case LexOr	:  
					if (lex[current].type.groupType().compareTo(maxGroup) <= 0) {
						if (stack.size() == topStackDepth || (stack.get(stack.size()-1) instanceof Lexema)) {
							throw new SyntaxException(lex[current].row,lex[current].col,"Operand is missing");
						}
						else {
							stack.add(lex[current++]);
						}
					}
					else {
						break loop;
					}
					break;
				default 		:
					break loop;
			}
			if (stack.size() >= topStackDepth + 2 && !(stack.get(stack.size()-1) instanceof Lexema) && !(stack.get(stack.size()-2) instanceof Lexema)) {
				throw new SyntaxException(lex[current].row,lex[current].col,"Two operands without operator between!");
			}
			else if (stack.get(stack.size()-1) instanceof Lexema) {
				reduceStack(stack,topStackDepth);
			}
		}
		
		if (stack.size() > topStackDepth && (stack.get(stack.size()-1) instanceof Lexema)) {
			throw new SyntaxException(lex[current].row,lex[current].col,"Missing operand in the expression");
		}
		else {
			final Lexema	terminal = new Lexema(0,LexemaType.LexRoot,0,0); 
			
			stack.add(terminal);
			reduceStack(stack,topStackDepth);
			if (stack.get(stack.size()-1) == terminal) {
				stack.remove(stack.size()-1);
			}
			return current;
		}
	}

	private static void reduceStack(final List<Object> stack, final int topStackDepth) throws SyntaxException {
		final boolean 	b1 = stack.size() >= topStackDepth + 4;
		
		if (stack.size() >= topStackDepth + 4 && ((Lexema)stack.get(stack.size()-3)).type.groupType().compareTo(((Lexema)stack.get(stack.size()-1)).type.groupType()) < 0) {
			final GroupType	group = ((Lexema)stack.get(stack.size()-3)).type.groupType();
			int 			index, count = 0, from;
			
			for (index = stack.size()-3; index > topStackDepth && ((Lexema)stack.get(index)).type.groupType() == group; index -= 2) {
				count++;
			}
			from = index + 1;
			
			for (int loop = 0; loop < count; loop++) {
				stack.set(from,calculate(stack.get(from),(Lexema)stack.remove(from+1),stack.remove(from+1)));
			}
			reduceStack(stack,topStackDepth);
		}
	}

	private static Object negate(final Object value, final int row, final int col) throws SyntaxException {
		final ValueType	type = ValueType.classify(value);
		
		if (type == ValueType.INTEGER) {
			return -((Number)value).longValue();
		}
		else if (type == ValueType.REAL) {
			return -((Number)value).doubleValue();
		}
		else {
			throw new SyntaxException(row,col,"Illegal types for operation: operand must be numeric");
		}
	}

	private static Object not(final Object value, final int row, final int col) throws SyntaxException {
		final ValueType	type = ValueType.classify(value);
		
		if (type == ValueType.BOOLEAN) {
			return !((Boolean)value).booleanValue();
		}
		else {
			throw new SyntaxException(row,col,"Illegal types for operation: operand must be boolean");
		}
	}
	
	private static Object calculate(final Object left, Lexema oper, final Object right) throws SyntaxException {
		final ValueType	leftType = ValueType.classify(left), rightType = ValueType.classify(right);   
		
		switch (oper.type) {
			case LexOr		:  
				if (leftType == ValueType.BOOLEAN && rightType == ValueType.BOOLEAN) {
					return ((Boolean)left) || ((Boolean)right); 
				}
				else {
					throw new SyntaxException(oper.row,oper.col,"Illegal types for boolean 'or': must be bool");
				}
			case LexAnd		:
				if (leftType == ValueType.BOOLEAN && rightType == ValueType.BOOLEAN) {
					return ((Boolean)left) && ((Boolean)right); 
				}
				else {
					throw new SyntaxException(oper.row,oper.col,"Illegal types for boolean 'and': must be bool");
				}
			case LexEQ		:
				if (isComparisonAvailable(leftType,rightType)){
					return compare(left,right) == 0;
				}
				else {
					throw new SyntaxException(oper.row,oper.col,"Illegal types for comparison: both operand must have the same type");
				}
			case LexNE		:
				if (isComparisonAvailable(leftType,rightType)){
					return compare(left,right) != 0;
				}
				else {
					throw new SyntaxException(oper.row,oper.col,"Illegal types for comparison: both operand must have the same type");
				}
			case LexGE		: 
				if (isComparisonAvailable(leftType,rightType)){
					return compare(left,right) >= 0;
				}
				else {
					throw new SyntaxException(oper.row,oper.col,"Illegal types for comparison: both operand must have the same type");
				}
			case LexGT		:
				if (isComparisonAvailable(leftType,rightType)){
					return compare(left,right) > 0;
				}
				else {
					throw new SyntaxException(oper.row,oper.col,"Illegal types for comparison: both operand must have the same type");
				}
			case LexLE		:
				if (isComparisonAvailable(leftType,rightType)){
					return compare(left,right) <= 0;
				}
				else {
					throw new SyntaxException(oper.row,oper.col,"Illegal types for comparison: both operand must have the same type");
				}
			case LexLT		:
				if (isComparisonAvailable(leftType,rightType)){
					return compare(left,right) < 0;
				}
				else {
					throw new SyntaxException(oper.row,oper.col,"Illegal types for comparison: both operand must have the same type");
				}
			case LexIn		:
				return isInRange(left,(List<Object>)right,oper.row,oper.col);
			case LexMul		:
				if (isNumeric(leftType,rightType)) {
					if (leftType == ValueType.INTEGER && rightType == ValueType.INTEGER) {
						return ((Number)left).longValue() * ((Number)right).longValue(); 
					}
					else {
						return ((Number)left).doubleValue() * ((Number)right).doubleValue(); 
					}
				}
				else {
					throw new SyntaxException(oper.row,oper.col,"Illegal types for operation: both operand must be numeric");
				}
			case LexDiv		: 
				if (isNumeric(leftType,rightType)) {
					return ((Number)left).doubleValue() / ((Number)right).doubleValue(); 
				}
				else {
					throw new SyntaxException(oper.row,oper.col,"Illegal types for operastion: both operand must be numeric");
				}
			case LexIDiv	:
				if (isNumeric(leftType,rightType)) {
					return ((Number)left).longValue() / ((Number)right).longValue(); 
				}
				else {
					throw new SyntaxException(oper.row,oper.col,"Illegal types for operastion: both operand must be numeric");
				}
			case LexMod		:
				if (isNumeric(leftType,rightType)) {
					return ((Number)left).longValue() % ((Number)right).longValue(); 
				}
				else {
					throw new SyntaxException(oper.row,oper.col,"Illegal types for operastion: both operand must be numeric");
				}
			case LexPlus	:
				if (isNumeric(leftType,rightType)) {
					if (leftType == ValueType.INTEGER && rightType == ValueType.INTEGER) {
						return ((Number)left).longValue() + ((Number)right).longValue(); 
					}
					else {
						return ((Number)left).doubleValue() + ((Number)right).doubleValue(); 
					}
				}
				else if (leftType == ValueType.STRING || rightType == ValueType.STRING) {
					return left.toString()+right.toString();
				}
				else {
					throw new SyntaxException(oper.row,oper.col,"Illegal types for operastion: both operand must be numeric");
				}
			case LexMinus	:
				if (isNumeric(leftType,rightType)) {
					if (leftType == ValueType.INTEGER && rightType == ValueType.INTEGER) {
						return ((Number)left).longValue() - ((Number)right).longValue(); 
					}
					else {
						return ((Number)left).doubleValue() - ((Number)right).doubleValue(); 
					}
				}
				else {
					throw new SyntaxException(oper.row,oper.col,"Illegal types for operastion: both operand must be numeric");
				}
			default : throw new UnsupportedOperationException("Operation type ["+oper.type+"] is not supported yet");
		}
	}

	private static int collectRangeList(final Lexema[] lex, final int from, final DataManager mgr, final LocalVarStack locals, final List<Object> stack) throws SyntaxException {
		final int	inIndex = from;
		int			current = from - 1, depth = stack.size();
		
		do {current = executeExpression(lex,current+1,GroupType.GroupAdd,mgr,locals,stack);
			if (stack.size() == depth) {
				throw new SyntaxException(lex[inIndex].row,lex[inIndex].col,"Missing excepression in 'in' list");
			}
			else if (lex[current].type == LexemaType.LexRange) {
				depth = stack.size();
				current = executeExpression(lex,current+1,GroupType.GroupAdd,mgr,locals,stack);
				if (stack.size() == depth) {
					throw new SyntaxException(lex[inIndex].row,lex[inIndex].col,"Missing excepression in 'in' list");
				}
			}
			else {
				stack.add(stack.get(stack.size()-1));
			}
			depth = stack.size();
		} while (lex[current].type == LexemaType.LexList);
		
		return current;
	}

	private static boolean isComparisonAvailable(final ValueType leftType, final ValueType rightType) {
		if (leftType == rightType) {
			return true;
		}
		else if ((leftType == ValueType.INTEGER || leftType == ValueType.REAL) && (rightType == ValueType.INTEGER || rightType == ValueType.REAL)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	private static int compare(final Object left, final Object right) {
		final ValueType	leftType = ValueType.classify(left), rightType = ValueType.classify(right);   

		if (leftType == ValueType.INTEGER && rightType == ValueType.INTEGER) {
			final long	result = ((Number)left).longValue() - ((Number)right).longValue();
			
			return result < 0 ? -1 : (result > 0 ? 1 : 0);
		}
		else if (leftType == ValueType.REAL || rightType == ValueType.REAL) {
			return (int)Math.signum(((Number)left).doubleValue() - ((Number)right).doubleValue());
		}
		else if (leftType == ValueType.STRING && rightType == ValueType.STRING) {
			return ((String)left).compareTo((String)right);
		}
		else if (leftType == ValueType.BOOLEAN && rightType == ValueType.BOOLEAN) {
			final boolean	leftVal = ((Boolean)left).booleanValue(), rightVal = ((Boolean)right).booleanValue();
			
			return leftVal == rightVal ? 0 : (leftVal == false && rightVal == true ? -1 : 1);
		}
		else {
			return -1;
		}
	}
	
	private static boolean isInRange(final Object value, final List<Object> stack, final int row, final int col) throws SyntaxException {
		final ValueType	type = ValueType.classify(value);

		switch (type) {
			case BOOLEAN	:
				throw new SyntaxException(row,col,"Illegal types for range check: operator is not applicable for booleans");
			case INTEGER	: case REAL		:
				while (!stack.isEmpty()) {
					final Object	low = stack.remove(0), high = stack.remove(0);
					final ValueType	lowType = ValueType.classify(low), highType = ValueType.classify(high);
					
					if ((lowType == ValueType.INTEGER || lowType == ValueType.REAL) && (highType == ValueType.INTEGER || highType == ValueType.REAL)) {
						if (((Number)value).doubleValue() >= ((Number)low).doubleValue() && ((Number)value).doubleValue() <= ((Number)high).doubleValue()) {
							return true;
						}
					}
					else {
						throw new SyntaxException(row,col,"Illegal types for range check: all values in range list mist be numeric");
					}
				}
				return false;
			case STRING:
				while (!stack.isEmpty()) {
					final Object	low = stack.remove(0), high = stack.remove(0);
					final ValueType	lowType = ValueType.classify(low), highType = ValueType.classify(high);
					
					if (lowType == ValueType.STRING && highType == ValueType.STRING) {
						if (((String)value).compareTo((String)low) >= 0 && ((String)value).compareTo((String)high) <= 0) {
							return true;
						}
					}
					else {
						throw new SyntaxException(row,col,"Illegal types for range check: all values in range list mist be strings");
					}
				}
				return false;
			default:
				throw new UnsupportedOperationException("Range check for ["+type+"] is not supported yet");
		}
	}

	private static Iterable<Integer> iterateRange(final List<Object> stack, final int row, final int col) throws SyntaxException {
		final List<Iterator<Integer>>	seq = new ArrayList<>();
		
		while (!stack.isEmpty()) {
			final Object	from = stack.remove(0), to = stack.remove(0);
			final ValueType	fromType = ValueType.classify(from), toType = ValueType.classify(to);
			
			if (fromType == ValueType.INTEGER && toType == ValueType.INTEGER) {
				final int	fromVal = ((Number)from).intValue(), toVal = ((Number)to).intValue(), step = fromVal <= toVal ? 1 : -1;
				
				seq.add(new Iterator<Integer>() {
					int		index = fromVal;
					
					@Override
					public boolean hasNext() {
						return step > 0 ? index <= toVal : index >= toVal;
					}

					@Override
					public Integer next() {
						final int	result = index;
						
						index += step;
						return result;
					}
				});
			}
			else {
				throw new SyntaxException(row, col,"Range list for operator 'for' must contain integers only"); 
			}
		}
		
		return new Iterable<Integer>() {
			@Override
			public Iterator<Integer> iterator() {
				return new SequenceIterator<Integer>(seq);
			}
		};
	}

	private static boolean isNumeric(final ValueType leftType, final ValueType rightType) {
		if (leftType == ValueType.STRING || leftType == ValueType.BOOLEAN) {
			return false;
		}
		else if (rightType == ValueType.STRING || rightType == ValueType.BOOLEAN) {
			return false;
		}
		else {
			return true;
		}
	}
	
	private static int skipTail(final Lexema[] lex, final int from, final TerminalSet<LexemaType> terminals) {
		int	nestedDepth = 0;
		
		for (int index = from, maxIndex = lex.length-1; index < maxIndex; index++) {
			switch (lex[index].type) {
				case LexBegin		: nestedDepth++; break;
				case LexCase		: nestedDepth++; break;
				case LexRepeat		: nestedDepth++; break;
				case LexEnd			: nestedDepth--; break;
				case LexUntil		: nestedDepth--; break;
				case LexSemicolon	:
					if (nestedDepth == 0) {
						return index;
					}
				default :
					break;
			}
			if (nestedDepth <= 0 && terminals.contains(lex[index].type)) {
				return index;
			}
		}
		return lex.length-1;
	}
	
	public static class Lexema {
		public final LexemaType	type;
		public final int		displ;
		public final int		row, col;
		public final long		numberContent;
		public final String		stringContent;

		public Lexema(final int displ, final LexemaType type, final int from, final int to) {
			this.displ = displ;
			this.type = type;
			this.row = from;
			this.col = to;
			this.numberContent = 0;
			this.stringContent = null;
		}
		
		public Lexema(final int displ, final LexemaType type, final int from, final int to, final String stringContent) {
			this.displ = displ;
			this.type = type;
			this.row = from;
			this.col = to;
			this.numberContent = 0;
			this.stringContent = stringContent;
		}

		public Lexema(final int displ, final LexemaType type, final int from, final int to, final long numberContent) {
			this.displ = displ;
			this.type = type;
			this.row = from;
			this.col = to;
			this.numberContent = numberContent;
			this.stringContent = null;
		}

		@Override
		public String toString() {
			return "Lexema [type=" + type + ", displ=" + displ + ", row=" + row + ", col=" + col + ", numberContent=" + numberContent + ", stringContent=" + stringContent + "]";
		}
	}
	
	
	private static final class TerminalSet<LexType extends Enum<?>> {
		private final Class<LexType>	clazz;
		private final boolean[]			content;
		
		public TerminalSet(final Class<LexType> clazz, final LexType... initials) {
			this.clazz = clazz;
			this.content = new boolean[clazz.getEnumConstants().length];
			
			for (LexType item : initials) {
				content[item.ordinal()] = true;
			}
		}

		private TerminalSet(final Class<LexType> clazz, final boolean[] content) {
			this.clazz = clazz;
			this.content = content;
		}
		
		public TerminalSet<LexType> add(final LexType... additionals) {
			final boolean[]	clone = content.clone();
			
			for (LexType item : additionals) {
				clone[item.ordinal()] = true;
			}
			return new TerminalSet<>(clazz,clone);
		}

		public boolean contains(final LexemaType type) {
			return content[type.ordinal()];
		}
		
		@Override
		public String toString() {
			final StringBuilder	sb = new StringBuilder();
			final Enum<?>[]		constants = clazz.getEnumConstants();
			String				prefix = "TerminalSet [";
			
			for (int index = 0, maxIndex = content.length; index < maxIndex; index++) {
				if (content[index]) {
					sb.append(prefix).append(constants[index]);
					prefix = " , ";
				}
			}
			return sb.append("]").toString();
		}
	}
}
