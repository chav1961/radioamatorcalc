package chav1961.calc.script;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chav1961.purelib.basic.AndOrTree;
import chav1961.purelib.basic.CharUtils;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.SyntaxTreeInterface;
import chav1961.purelib.cdb.SyntaxNode;
import chav1961.purelib.enumerations.ContinueMode;
import chav1961.purelib.enumerations.NodeEnterMode;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public class ScriptProcessor {
	private static final char 								LEX_TERMINAL = '\0';
	private static final SyntaxTreeInterface<LexemaType>	KEYWORDS = new AndOrTree<>();
	
	static {
		KEYWORDS.placeName("if",LexemaType.LexIf);
		KEYWORDS.placeName("then",LexemaType.LexThen);
		KEYWORDS.placeName("else",LexemaType.LexElse);
		KEYWORDS.placeName("endif",LexemaType.LexEndIf);
		KEYWORDS.placeName("return",LexemaType.LexReturn);
		KEYWORDS.placeName("print",LexemaType.LexPrint);
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
		KEYWORDS.placeName("sqrt",LexemaType.LexFSqrt);
	}
	
	public enum GroupType {
		GroupMul, GroupAdd, GroupCmp, GroupOther
	}
	
	public enum LexemaType {
		LexEOF, LexOpen, LexClose, 
		LexPlus(GroupType.GroupAdd), LexMinus(GroupType.GroupAdd), LexMul(GroupType.GroupMul), LexDiv(GroupType.GroupMul), 
		LexGE(GroupType.GroupCmp), LexGT(GroupType.GroupCmp), LexLE(GroupType.GroupCmp), LexLT(GroupType.GroupCmp), LexEQ(GroupType.GroupCmp), LexNE(GroupType.GroupCmp), 
		LexFSin, LexFCos, LexFTan, LexFASin,LexFACos, LexFATan, LexFExp, LexFExp10, LexFLn, LexFLog10, LexFSqrt,  
		LexAnd, LexOr, LexNot, LexAssign, 
		LexInt, LexReal, LexName, LexString, LexList, LexEndOp,
		LexIf, LexThen, LexElse, LexEndIf, LexReturn, LexPrint, LexRoot;
		
		private final GroupType	groupType;

		private LexemaType() {
			this.groupType = GroupType.GroupOther;
		}
		
		private LexemaType(final GroupType groupType) {
			this.groupType = groupType;
		}
		
		public GroupType groupType() {
			return groupType;
		}
	}

	public enum ExpressionDepth {
		ExprOr, ExprAnd, ExprNot, ExprCmp, ExprAdd, ExprMul, ExprUnary, ExprTerm,
	}

	public enum SyntaxNodeType {
		NodeRoot, NodeSequence, NodePrint, NodeReturn, NodeAssign, NodeShortIf, NodeLongIf,
		NodeGetVar, NodeGetInt, NodeGetReal, NodeNegation, NodeMul, NodeAdd, NodeCmp, NodeNot, NodeAnd, NodeOr,
		NodeFunc
	}
	
	public enum ComparisonType {
		EQ, NE, GT, GE, LT, LE
	}

	public enum FunctionType {
		Sin, Cos, Tan, ArcSin, ArcCos, ArcTan, Exp, Exp10, Ln, Ln10, Sqrt
	}
	
	public static List<Lexema> buildLexemaList(final String content) throws SyntaxException, IllegalArgumentException, NullPointerException {
		return buildLexemaList(CharUtils.terminateAndConvert2CharArray(content,LEX_TERMINAL));
	}
	
	private static List<Lexema> buildLexemaList(final char[] source) throws SyntaxException, IllegalArgumentException, NullPointerException {
		final List<Lexema>	lexemas = new ArrayList<>();
		final StringBuilder	sb = new StringBuilder();
		final long[]		forLongs = new long[2];
		final int[]			forInts = new int[2]; 
		int					from = 0, start, row, col;
		
loop:	for (;;) {
			from = CharUtils.skipBlank(source,from,false);
			row = SyntaxException.toRow(source,from);
			col = SyntaxException.toCol(source,from);
			switch (source[from]) {
				case LEX_TERMINAL :
					lexemas.add(new Lexema(LexemaType.LexEOF,row,col));
					break loop;
				case ';'	:
					lexemas.add(new Lexema(LexemaType.LexEndOp,row,col));
					from++;
					break;
				case '('	:
					lexemas.add(new Lexema(LexemaType.LexOpen,row,col));
					from++;
					break;
				case ')'	:
					lexemas.add(new Lexema(LexemaType.LexClose,row,col));
					from++;
					break;
				case '+'	:
					lexemas.add(new Lexema(LexemaType.LexPlus,row,col));
					from++;
					break;
				case '-'	:
					lexemas.add(new Lexema(LexemaType.LexMinus,row,col));
					from++;
					break;
				case '*'	:
					lexemas.add(new Lexema(LexemaType.LexMul,row,col));
					from++;
					break;
				case '/'	:
					if (source[from+1] == '/') { // Comment
						while (source[from] != '\n' && source[from] != LEX_TERMINAL) {
							from++;
						}
					}
					else {
						lexemas.add(new Lexema(LexemaType.LexDiv,row,col));
						from++;
					}
					break;
				case '>'	:
					if (source[from+1] == '=') {
						lexemas.add(new Lexema(LexemaType.LexGE,row,col));
						from += 2;
					}
					else {
						lexemas.add(new Lexema(LexemaType.LexGT,row,col));
						from++;
					}
					break;
				case '<'	:
					if (source[from+1] == '=') {
						lexemas.add(new Lexema(LexemaType.LexLE,row,col));
						from += 2;
					}
					else if (source[from+1] == '>') {
						lexemas.add(new Lexema(LexemaType.LexNE,row,col));
						from += 2;
					}
					else {
						lexemas.add(new Lexema(LexemaType.LexLT,row,col));
						from++;
					}
					break;
				case '='	:
					lexemas.add(new Lexema(LexemaType.LexEQ,row,col));
					from++;
					break;
				case ':'	:
					if (source[from+1] == '=') {
						lexemas.add(new Lexema(LexemaType.LexAssign,row,col));
						from += 2;
					}
					else {
						throw new SyntaxException(SyntaxException.toRow(source,from),SyntaxException.toCol(source,from),"Unknown lexema");
					}
					break;
				case ','	:
					lexemas.add(new Lexema(LexemaType.LexList,row,col));
					from++;
					break;
				case '0' : case '1' : case '2' : case '3' : case '4' : case '5' : case '6' : case '7' : case '8' : case '9' :
					start = from;
					from = CharUtils.parseNumber(source,from,forLongs,CharUtils.PREF_LONG|CharUtils.PREF_DOUBLE,true);
					if (forLongs[1] == CharUtils.PREF_LONG) {
						lexemas.add(new Lexema(LexemaType.LexInt,row,col,forLongs[0]));
					}
					else {
						lexemas.add(new Lexema(LexemaType.LexReal,row,col,forLongs[0]));
					}
					break;
				case '\"'	:
					start = from;
					sb.setLength(0);
					from = CharUtils.parseString(source,from+1,'\"',sb);
					if (source[from] == '\"') {
						from++;
						lexemas.add(new Lexema(LexemaType.LexString,row,col,sb.toString()));
					}
					else {
						throw new SyntaxException(SyntaxException.toRow(source,from),SyntaxException.toCol(source,from),"Unquoted string");
					}
					break;
				default :
					if (Character.isJavaIdentifierStart(source[from])) {
						start = from;
						from = CharUtils.parseName(source,from,forInts);
						final long 	id = KEYWORDS.seekName(source,forInts[0],forInts[1]-forInts[0]);
						
						if (id >= 0) {
							lexemas.add(new Lexema(KEYWORDS.getCargo(id),row,col));
						}
						else {
							lexemas.add(new Lexema(LexemaType.LexName,row,col,new String(source,start,from-start)));
						}
					}
					else {
						throw new SyntaxException(SyntaxException.toRow(source,from),SyntaxException.toCol(source,from),"Unknown lexema");
					}
					break;
			}
		}
		return lexemas;
	}
	
	public static <T> SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>> buildSyntaxTree(final Lexema[] lexemas, final ContentMetadataInterface model) throws SyntaxException {
		final Map<String,ContentNodeMetadata>	vars = new HashMap<>();
		final boolean[]		found = new boolean[1];

		for (Lexema item : lexemas) {
			if (item.type == LexemaType.LexName) {
				final String	name = item.stringContent;
				
				found[0] = false;
				model.walkDown((mode,appPath,uiPath,node) -> {
						if (mode == NodeEnterMode.ENTER && node.getName().equals(name)) {
							vars.put(name,node);
							found[0] = true;
						}
						return ContinueMode.CONTINUE;
					}
					,model.getRoot().getUIPath()
				);
				if (!found[0]) {
					throw new SyntaxException(item.row,item.col,"Unknown name ["+name+"]");
				}
			}
		}

		final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	root = new SyntaxNode<>(0,0,SyntaxNodeType.NodeSequence,0,null); 
		final int	from = buildSequence(lexemas,0,root, vars);
		
		if (lexemas[from].type != LexemaType.LexEOF) {
			throw new SyntaxException(lexemas[from].row,lexemas[from].col,"Dust in the tail");
		}
		else {
			return root;
		}
	}

	static int buildSequence(final Lexema[] lexemas, int from, final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>> root, final Map<String,ContentNodeMetadata> vars) throws SyntaxException {
		final List<SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>>	operators = new ArrayList<>();
		
		from--;
loop:	do {from++;
			switch (lexemas[from].type) {
				case LexName	:
					final int		rowAssign = lexemas[from].row, colAssign = lexemas[from].col;
					final ContentNodeMetadata	metaData = vars.get(lexemas[from].stringContent);
					
					if (lexemas[++from].type != LexemaType.LexAssign) {
						throw new SyntaxException(lexemas[from].row,lexemas[from].col,"Assignment (:=) is missing");
					}
					else {
						final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	node = new SyntaxNode<>(0,0,SyntaxNodeType.NodeRoot,0,null);
						
						from = buildExpression(lexemas,from+1,ExpressionDepth.ExprOr,node,vars);
						operators.add(new SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>(rowAssign,colAssign,SyntaxNodeType.NodeAssign,0,metaData,node));
					}
					break;
				case LexIf		:
					final int		rowIf = lexemas[from].row, colIf = lexemas[from].col;
					final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	exprNode = new SyntaxNode<>(lexemas[from].row,lexemas[from].col,SyntaxNodeType.NodeRoot,0,null);
					
					from = buildExpression(lexemas,from++,ExpressionDepth.ExprOr,exprNode,vars);
					if (lexemas[from].type != LexemaType.LexThen) {
						throw new SyntaxException(lexemas[from].row,lexemas[from].col,"keyword 'then' is missing");
					}
					else {
						final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	thenNode = new SyntaxNode<>(lexemas[from].row,lexemas[from].col,SyntaxNodeType.NodeSequence,0,null);

						from = buildSequence(lexemas,from+1,thenNode,vars);
						if (lexemas[from].type != LexemaType.LexEndIf) {
							if (lexemas[from].type == LexemaType.LexElse) {
								final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	elseNode = new SyntaxNode<>(lexemas[from].row,lexemas[from].col,SyntaxNodeType.NodeSequence,0,null);

								from = buildSequence(lexemas,from+1,thenNode,vars);
								if (lexemas[from].type != LexemaType.LexEndIf) {
									throw new SyntaxException(lexemas[from].row,lexemas[from].col,"keyword 'endif' is missing");
								}
								else {
									operators.add(new SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>(rowIf,colIf,SyntaxNodeType.NodeLongIf,0,exprNode,thenNode,elseNode));
								}
							}
							else {
								throw new SyntaxException(lexemas[from].row,lexemas[from].col,"keyword 'endif' is missing");
							}
						}
						else {
							operators.add(new SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>(rowIf,colIf,SyntaxNodeType.NodeShortIf,0,exprNode,thenNode));
						}
					}
					break;
				case LexPrint	:
					if (lexemas[++from].type != LexemaType.LexString) {
						throw new SyntaxException(lexemas[from].row,lexemas[from].col,"String constant waited");
					}
					else {
						final int		rowPrint = lexemas[from].row, colPrint = lexemas[from].col;
						final String	format = lexemas[from].stringContent;
						final List<SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>>	printList = new ArrayList<>();
						
						while (lexemas[from].type == LexemaType.LexList) {
							final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	node = new SyntaxNode<>(0,0,SyntaxNodeType.NodeRoot,0,null);
							
							from = buildExpression(lexemas,from+1,ExpressionDepth.ExprOr,node,vars);
							printList.add(node);
						}
						operators.add(new SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>(rowPrint,colPrint,SyntaxNodeType.NodePrint,0,format,printList.toArray(new SyntaxNode[printList.size()])));
					}
					break;
				case LexReturn	:
					operators.add(new SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>(lexemas[from].row,lexemas[from].col,SyntaxNodeType.NodeReturn,0,null));
					from++;
					break;
				default :
					break loop;
			}
		} while (lexemas[from].type == LexemaType.LexEndOp);
		
		return from;
	}
	
	static int buildExpression(final Lexema[] lexemas, int from, final ExpressionDepth depth, final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>> root, final Map<String,ContentNodeMetadata> vars) throws SyntaxException {
		switch (depth) {
			case ExprOr		:
				from = buildExpression(lexemas,from,ExpressionDepth.ExprAnd,root,vars); 
				if (lexemas[from].type == LexemaType.LexOr) {
					final List<SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>>	operands = new ArrayList<>();
					
					operands.add(new SyntaxNode<>(root));
					do {final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	operand = new SyntaxNode<>(root);
						
						from = buildExpression(lexemas,from+1,ExpressionDepth.ExprAnd,root,vars);
						operands.add(operand);
					} while (lexemas[from].type == LexemaType.LexOr);
					root.type = SyntaxNodeType.NodeOr;
					root.children = operands.toArray(new SyntaxNode[operands.size()]);
					operands.clear();
				}
				break;
			case ExprAnd	:
				from = buildExpression(lexemas,from,ExpressionDepth.ExprNot,root,vars); 
				if (lexemas[from].type == LexemaType.LexAnd) {
					final List<SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>>	operands = new ArrayList<>();
					
					operands.add(new SyntaxNode<>(root));
					do {final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	operand = new SyntaxNode<>(root);
						
						from = buildExpression(lexemas,from+1,ExpressionDepth.ExprNot,root,vars);
						operands.add(operand);
					} while (lexemas[from].type == LexemaType.LexAnd);
					root.type = SyntaxNodeType.NodeAnd;
					root.children = operands.toArray(new SyntaxNode[operands.size()]);
					operands.clear();
				}
				break;
			case ExprNot	:
				if (lexemas[from].type == LexemaType.LexNot) {
					final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	node = new SyntaxNode<>(root);
					
					from = buildExpression(lexemas,from+1,ExpressionDepth.ExprCmp,node,vars);
					root.type = SyntaxNodeType.NodeNot;
					root.cargo = node;
				}
				else {
					from = buildExpression(lexemas,from,ExpressionDepth.ExprCmp,root,vars); 
				}
				break;
			case ExprCmp	:
				from = buildExpression(lexemas,from,ExpressionDepth.ExprAdd,root,vars);
				if (lexemas[from].type.groupType() == GroupType.GroupCmp) {
					final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	left = new SyntaxNode<>(root), right = new SyntaxNode<>(root);
					final ComparisonType	oper; 
					
					switch (lexemas[from].type) {
						case LexGT : oper = ComparisonType.GT; break;
						case LexGE : oper = ComparisonType.GE; break;
						case LexLT : oper = ComparisonType.LT; break;
						case LexLE : oper = ComparisonType.LE; break;
						case LexEQ : oper = ComparisonType.EQ; break;
						case LexNE :  oper = ComparisonType.NE; break;
						default    : throw new UnsupportedOperationException(); 
					}
					from = buildExpression(lexemas,from+1,ExpressionDepth.ExprAdd,right,vars);
					root.type = SyntaxNodeType.NodeCmp;
					root.cargo = oper;
					root.children = new SyntaxNode[] {left,right};
				}
				break;
			case ExprAdd	:
				from = buildExpression(lexemas,from,ExpressionDepth.ExprMul,root,vars); 
				if (lexemas[from].type.groupType() == GroupType.GroupAdd) {
					final List<SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>>	operands = new ArrayList<>();
					final StringBuilder sb = new StringBuilder();
					
					operands.add(new SyntaxNode<>(root));
					do {sb.append(lexemas[from].type == LexemaType.LexPlus ? '+' : '-');
						final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	operand = new SyntaxNode<>(root);
						
						from = buildExpression(lexemas,from+1,ExpressionDepth.ExprMul,root,vars);
						operands.add(operand);
					} while (lexemas[from].type.groupType() == GroupType.GroupAdd);
					root.type = SyntaxNodeType.NodeAdd;
					root.cargo = sb.toString().toCharArray();
					root.children = operands.toArray(new SyntaxNode[operands.size()]);
					operands.clear();
				}
				break;
			case ExprMul	:
				from = buildExpression(lexemas,from,ExpressionDepth.ExprUnary,root,vars);
				if (lexemas[from].type.groupType() == GroupType.GroupMul) {
					final List<SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>>	operands = new ArrayList<>();
					final StringBuilder sb = new StringBuilder();
					
					operands.add(new SyntaxNode<>(root));
					do {sb.append(lexemas[from].type == LexemaType.LexMul ? '*' : '/');
						final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	operand = new SyntaxNode<>(root);
						
						from = buildExpression(lexemas,from+1,ExpressionDepth.ExprUnary,root,vars);
						operands.add(operand);
					} while (lexemas[from].type.groupType() == GroupType.GroupMul);
					root.type = SyntaxNodeType.NodeMul;
					root.cargo = sb.toString().toCharArray();
					root.children = operands.toArray(new SyntaxNode[operands.size()]);
					operands.clear();
				}
				break;
			case ExprUnary	:
				if (lexemas[from].type == LexemaType.LexPlus) {
					from = buildExpression(lexemas,from+1,ExpressionDepth.ExprTerm,root,vars); 
				}
				else if (lexemas[from].type == LexemaType.LexMinus) {
					final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	node = new SyntaxNode<>(root);
					
					from = buildExpression(lexemas,from+1,ExpressionDepth.ExprTerm,node,vars);
					root.type = SyntaxNodeType.NodeNegation;
					root.cargo = node;
				}
				else {
					from = buildExpression(lexemas,from,ExpressionDepth.ExprTerm,root,vars); 
				}
				break;
			case ExprTerm	:
				switch (lexemas[from].type) {
					case LexInt 	:
						root.type = SyntaxNodeType.NodeGetInt;
						root.value = lexemas[from].numberContent;
						break;
					case LexReal 	:
						root.type = SyntaxNodeType.NodeGetReal;
						root.value = lexemas[from].numberContent;
						break;
					case LexName 	:
						root.type = SyntaxNodeType.NodeGetVar;
						root.cargo = vars.get(lexemas[from].stringContent);
						break;
					case LexOpen	:
						from = buildExpression(lexemas,from+1,ExpressionDepth.ExprOr,root,vars);
						if (lexemas[from].type == LexemaType.LexClose) {
							from++;
						}
						else {
							throw new SyntaxException(lexemas[from].row,lexemas[from].col,"Close bracket ')' is missing"); 
						}
					case LexFSin 	:
						from = buildFunctionCall(lexemas,from+1,FunctionType.Sin,1,root,vars);
						break;
					case LexFCos 	:
						from = buildFunctionCall(lexemas,from+1,FunctionType.Cos,1,root,vars);
						break;
					case LexFTan 	:
						from = buildFunctionCall(lexemas,from+1,FunctionType.Tan,1,root,vars);
						break;
					case LexFASin 	:
						from = buildFunctionCall(lexemas,from+1,FunctionType.ArcSin,1,root,vars);
						break;
					case LexFACos 	:
						from = buildFunctionCall(lexemas,from+1,FunctionType.ArcCos,1,root,vars);
						break;
					case LexFATan 	:
						from = buildFunctionCall(lexemas,from+1,FunctionType.ArcTan,1,root,vars);
						break;
					case LexFExp 	:
						from = buildFunctionCall(lexemas,from+1,FunctionType.Exp,1,root,vars);
						break;
					case LexFExp10 	: 
						from = buildFunctionCall(lexemas,from+1,FunctionType.Exp10,1,root,vars);
						break;
					case LexFLn 	:
						from = buildFunctionCall(lexemas,from+1,FunctionType.Ln,1,root,vars);
						break;
					case LexFLog10 	: 
						from = buildFunctionCall(lexemas,from+1,FunctionType.Ln10,1,root,vars);
						break;
					case LexFSqrt 	:
						from = buildFunctionCall(lexemas,from+1,FunctionType.Sqrt,1,root,vars);
						break;
					default :
						break;
				}
				break;
			default:
				break;
		}
		return from;
	}

	static int buildFunctionCall(final Lexema[] lexemas, int from, final FunctionType funcType, final int argCount, final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>> root, final Map<String,ContentNodeMetadata> vars) throws SyntaxException {
		@SuppressWarnings("unchecked")
		final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>[]	args = new SyntaxNode[argCount];
		int	index = 0;

		if (lexemas[from].type == LexemaType.LexOpen) {
			do {final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	operand = new SyntaxNode<>(root);
				
				from = buildExpression(lexemas,from+1,ExpressionDepth.ExprAdd,operand,vars);
				args[index++] = operand;
			} while (index < argCount && lexemas[from].type == LexemaType.LexList);
			if (index < argCount) {
				throw new SyntaxException(lexemas[from].row,lexemas[from].col,"Too few arguments for the given function"); 
			}
			else if (lexemas[from].type != LexemaType.LexClose) {
				throw new SyntaxException(lexemas[from].row,lexemas[from].col,"Close bracket ')' is missing"); 
			}
			else {
				from++;
				root.type = SyntaxNodeType.NodeFunc;
				root.cargo = funcType;
				root.children = args;
			}
		}
		else {
			throw new SyntaxException(lexemas[from].row,lexemas[from].col,"Open bracket '(' is missing"); 
		}
		return from;
	}	
	
	public static <T> void processSyntaxTree(final SyntaxNode<LexemaType,?> root, final T instance) {
		
	}

	
	public static class Lexema {
		public final LexemaType	type;
		public final int		row, col;
		public final long		numberContent;
		public final String		stringContent;

		public Lexema(final LexemaType type, final int from, final int to) {
			this.type = type;
			this.row = from;
			this.col = to;
			this.numberContent = 0;
			this.stringContent = null;
		}
		
		public Lexema(final LexemaType type, final int from, final int to, final String stringContent) {
			this.type = type;
			this.row = from;
			this.col = to;
			this.numberContent = 0;
			this.stringContent = stringContent;
		}

		public Lexema(final LexemaType type, final int from, final int to, final long numberContent) {
			this.type = type;
			this.row = from;
			this.col = to;
			this.numberContent = numberContent;
			this.stringContent = null;
		}

		@Override
		public String toString() {
			return "Lexema [type=" + type + ", from=" + row + ", to=" + col + ", numberContent=" + numberContent + ", stringContent=" + stringContent + "]";
		}
	}
}
