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
import chav1961.purelib.basic.exceptions.CalculationException;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.SyntaxTreeInterface;
import chav1961.purelib.cdb.SyntaxNode;
import chav1961.purelib.enumerations.ContinueMode;
import chav1961.purelib.enumerations.NodeEnterMode;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public class ScriptProcessor {
	private static final char 								LEX_TERMINAL = '\uFFFF';
	private static final SyntaxTreeInterface<LexemaType>	KEYWORDS = new AndOrTree<>();
	private static final double								LOG10 = Math.log(10);
	private static final double								INV_LOG10 = 1/LOG10;
	
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
	
	public enum LexemaType {
		LexEOF, LexOpen, LexClose, 
		LexPlus(GroupType.GroupAdd), LexMinus(GroupType.GroupAdd), LexMul(GroupType.GroupMul), LexDiv(GroupType.GroupMul), 
		LexGE(GroupType.GroupCmp), LexGT(GroupType.GroupCmp), LexLE(GroupType.GroupCmp), LexLT(GroupType.GroupCmp), LexEQ(GroupType.GroupCmp), LexNE(GroupType.GroupCmp), 
		LexFSin(GroupType.GroupFunc), LexFCos(GroupType.GroupFunc), LexFTan(GroupType.GroupFunc),
		LexFASin(GroupType.GroupFunc), LexFACos(GroupType.GroupFunc), LexFATan(GroupType.GroupFunc), 
		LexFExp(GroupType.GroupFunc), LexFExp10(GroupType.GroupFunc), LexFLn(GroupType.GroupFunc), LexFLog10(GroupType.GroupFunc),
		LexFSqrt(GroupType.GroupFunc),  
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

	public enum SyntaxNodeType {
		NodeRoot, NodeSequence, NodePrint, NodeReturn, NodeAssign, NodeShortIf, NodeLongIf,
		NodeGetVar, NodeGetInt, NodeGetReal, NodeNegation, NodeMul, NodeAdd, NodeCmp, NodeNot, NodeAnd, NodeOr,
		NodeFunc
	}
	
	enum ComparisonType {
		EQ, NE, GT, GE, LT, LE
	}

	enum FunctionType {
		Sin, Cos, Tan, ArcSin, ArcCos, ArcTan, Exp, Exp10, Ln, Ln10, Sqrt
	}

	enum GroupType {
		GroupMul, GroupAdd, GroupCmp, GroupFunc, GroupOther
	}
	
	enum ExpressionDepth {
		ExprOr, ExprAnd, ExprNot, ExprCmp, ExprAdd, ExprMul, ExprUnary, ExprTerm,
	}
	
	public static List<Lexema> buildLexemaList(final String content) throws SyntaxException, IllegalArgumentException, NullPointerException {
		if (content == null || content.isEmpty()) {
			throw new IllegalArgumentException("Content string can't be null or empty"); 
		}
		else {
			return buildLexemaList(CharUtils.terminateAndConvert2CharArray(content,LEX_TERMINAL));
		}
	}
	
	private static List<Lexema> buildLexemaList(final char[] source) throws SyntaxException, IllegalArgumentException, NullPointerException {
		final List<Lexema>	lexemas = new ArrayList<>();
		final StringBuilder	sb = new StringBuilder();
		final long[]		forLongs = new long[2];
		final int[]			forInts = new int[2]; 
		int					from = 0, start, row = 0, lastRowLocation = 0, col = 0;
		
loop:	for (;;) {
			from = CharUtils.skipBlank(source,from,true); 
			col = from - lastRowLocation;
			switch (source[from]) {
				case LEX_TERMINAL :
					lexemas.add(new Lexema(LexemaType.LexEOF,row,col));
					break loop;
				case '\n'	:	// Only to calculate rows and columns
					lastRowLocation = ++from;
					row++;
					break;
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
						throw new SyntaxException(row,col,"Unknown lexema");
					}
					break;
				case ','	:
					lexemas.add(new Lexema(LexemaType.LexList,row,col));
					from++;
					break;
				case '0' : case '1' : case '2' : case '3' : case '4' : case '5' : case '6' : case '7' : case '8' : case '9' :
					start = from;
					from = CharUtils.parseNumber(source,from,forLongs,CharUtils.PREF_LONG|CharUtils.PREF_DOUBLE,true);
					if (forLongs[1] == CharUtils.PREF_INT || forLongs[1] == CharUtils.PREF_LONG) {
						lexemas.add(new Lexema(LexemaType.LexInt,row,col,forLongs[0]));
					}
					else {
						lexemas.add(new Lexema(LexemaType.LexReal,row,col,forLongs[0]));
					}
					break;
				case '\"'	:
					
					try{start = from;
						sb.setLength(0);
						from = CharUtils.parseString(source,from+1,'\"',sb);
						
						lexemas.add(new Lexema(LexemaType.LexString,row,col,sb.toString()));
					} catch (IllegalArgumentException exc) {
						throw new SyntaxException(row,col,"Unquoted string");
					}
					break;
				default :
					if (Character.isJavaIdentifierStart(source[from])) {
						start = from;
						from = CharUtils.parseName(source,from,forInts);
						final long 	id = KEYWORDS.seekName(source,forInts[0],forInts[1]+1);
						
						if (id >= 0) {
							lexemas.add(new Lexema(KEYWORDS.getCargo(id),row,col));
						}
						else {
							lexemas.add(new Lexema(LexemaType.LexName,row,col,new String(source,start,from-start)));
						}
					}
					else {
						throw new SyntaxException(row,col,"Unknown lexema");
					}
					break;
			}
		}
		return lexemas;
	}
	
	public static <T> SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>> buildSyntaxTree(final Lexema[] lexemas, final ContentMetadataInterface model) throws NullPointerException, IllegalArgumentException, SyntaxException {
		if (lexemas == null || lexemas.length == 0) {
			throw new IllegalArgumentException("Lexemas list can't be null or empty array"); 
		}
		else if (model == null) {
			throw new IllegalArgumentException("Model can't be null"); 
		}
		else {
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
						throw new SyntaxException(item.row,item.col,"Unknown var name ["+name+"]");
					}
				}
			}
	
			final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	root = new SyntaxNode<>(0,0,SyntaxNodeType.NodeSequence,0,null); 
			final int	from = buildSequence(lexemas,0,root, vars);
			
			if (lexemas[from].type != LexemaType.LexEOF) {
				throw new SyntaxException(lexemas[from].row,lexemas[from].col,"Dust in the tail");
			}
			else {
				vars.clear();
				return root;
			}
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
						final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	node = new SyntaxNode<>(0,0,SyntaxNodeType.NodeAssign,0,null);
						
						from = buildExpression(lexemas,from+1,ExpressionDepth.ExprOr,node,vars);
						operators.add(new SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>(rowAssign,colAssign,SyntaxNodeType.NodeAssign,0,metaData,node));
					}
					break;
				case LexIf		:
					final int		rowIf = lexemas[from].row, colIf = lexemas[from].col;
					final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	exprNode = new SyntaxNode<>(lexemas[from].row,lexemas[from].col,SyntaxNodeType.NodeRoot,0,null);
					
					from = buildExpression(lexemas,from+1,ExpressionDepth.ExprOr,exprNode,vars);
					if (lexemas[from].type != LexemaType.LexThen) {
						throw new SyntaxException(lexemas[from].row,lexemas[from].col,"keyword 'then' is missing");
					}
					else {
						final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	thenNode = new SyntaxNode<>(lexemas[from].row,lexemas[from].col,SyntaxNodeType.NodeSequence,0,null);

						from = buildSequence(lexemas,from+1,thenNode,vars);
						if (lexemas[from].type != LexemaType.LexEndIf) {
							if (lexemas[from].type == LexemaType.LexElse) {
								final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>	elseNode = new SyntaxNode<>(lexemas[from].row,lexemas[from].col,SyntaxNodeType.NodeSequence,0,null);

								from = buildSequence(lexemas,from+1,elseNode,vars);
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
						throw new SyntaxException(lexemas[from].row,lexemas[from].col,"Format sting is missing");
					}
					else {
						final int		rowPrint = lexemas[from].row, colPrint = lexemas[from].col;
						final String	format = lexemas[from].stringContent;
						final List<SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>>	printList = new ArrayList<>();
						
						from++;
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

		if (operators.size() == 1) {
			root.assign(operators.get(0));
		}
		else {
			root.type = SyntaxNodeType.NodeSequence;
			root.children = operators.toArray(new SyntaxNode[operators.size()]); 
		}
		operators.clear();
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
					final StringBuilder sb = new StringBuilder("+");
					
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
					final StringBuilder sb = new StringBuilder("*");
					
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
						from++;
						break;
					case LexReal 	:
						root.type = SyntaxNodeType.NodeGetReal;
						root.value = lexemas[from].numberContent;
						from++;
						break;
					case LexName 	:
						root.type = SyntaxNodeType.NodeGetVar;
						if ((root.cargo = vars.get(lexemas[from].stringContent)) == null) {
							throw new SyntaxException(lexemas[from].row,lexemas[from].col,"Variable name ["+lexemas[from].stringContent+"] is unknown"); 
						}
						from++;
						break;
					case LexOpen	:
						from = buildExpression(lexemas,from+1,ExpressionDepth.ExprOr,root,vars);
						if (lexemas[from].type == LexemaType.LexClose) {
							from++;
						}
						else {
							throw new SyntaxException(lexemas[from].row,lexemas[from].col,"Close bracket ')' is missing"); 
						}
						break;
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
	
	public interface PrintAndVariableAccessor {
		Object getVar(ContentNodeMetadata metadata) throws ContentException;
		void setVar(ContentNodeMetadata metadata, Object value) throws ContentException;
		void print(String format, Object... parameters) throws ContentException;
	}

	public static <T> void processSyntaxTree(final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>> root, final PrintAndVariableAccessor accessor) throws NullPointerException, CalculationException {
		if (root == null) {
			throw new NullPointerException("Syntax node root can't be null");
		}
		else if (accessor == null) {
			throw new NullPointerException("Var accessor can't be null");
		}
		else {
			try {processSyntaxTreeInternal(root,accessor);
			} catch (ReturnException exc) {
			}
		}
	}	

	private static class ReturnException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	private static <T> Object processSyntaxTreeInternal(final SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>> root, final PrintAndVariableAccessor accessor) throws NullPointerException, CalculationException, ReturnException {
		if (root == null) {
			throw new NullPointerException("Syntax node root can't be null");
		}
		else if (accessor == null) {
			throw new NullPointerException("Var accessor can't be null");
		}
		else {
			switch (root.getType()) {
				case NodeAdd		:
					final Object[]	addValues = new Object[root.children.length];
					boolean			needDoubleAdd = false;
					
					for (int index = 0; index < addValues.length; index++) {
						addValues[index] = processSyntaxTreeInternal((SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>)root.children[index],accessor);
					}
					for (int index = 0; index < addValues.length; index++) {
						if ((addValues[index] instanceof Float) || (addValues[index] instanceof Double)) {
							needDoubleAdd = true;
						}
						else if (!(addValues[index] instanceof Number)) {
							throw new CalculationException("Error processing additional operators: operand at row "+root.children[index].row+", col "+root.children[index].col+" is not a number");
						}
					}
					if (needDoubleAdd) {
						double	sum = 0;
						
						for (int index = 0; index < addValues.length; index++) {
							if (((char[])root.cargo)[index] == '+') {
								sum += ((Number)addValues[index]).doubleValue();
							}
							else {
								sum -= ((Number)addValues[index]).doubleValue();
							}
						}
						return sum;
					}
					else {
						long sum = 0;
						
						for (int index = 0; index < addValues.length; index++) {
							if (((char[])root.cargo)[index] == '+') {
								sum += ((Number)addValues[index]).longValue();
							}
							else {
								sum -= ((Number)addValues[index]).longValue();
							}
						}
						return sum;
					}
				case NodeAnd		:
					final Object[]	andValues = new Object[root.children.length];
					
					for (int index = 0; index < andValues.length; index++) {
						andValues[index] = processSyntaxTreeInternal((SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>)root.children[index],accessor);
					}
					for (int index = 0; index < andValues.length; index++) {
						if (!(andValues[index] instanceof Boolean)) {
							throw new CalculationException("Error processing multiplication operators: operand at row "+root.children[index].row+", col "+root.children[index].col+" is not a boolean");
						}
					}
					for (Object item : andValues) {
						if (!(Boolean)item) {
							return false;
						}
					}
					return true;
				case NodeAssign		:
					try{accessor.setVar((ContentNodeMetadata)root.cargo, processSyntaxTreeInternal((SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>)root.children[0],accessor));
					} catch (ContentException e) {
						throw new CalculationException(e.getLocalizedMessage(),e);
					}
				case NodeCmp		:
					final Object	leftCmp = processSyntaxTreeInternal((SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>)root.children[0],accessor);
					final Object	rightCmp = processSyntaxTreeInternal((SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>)root.children[1],accessor);
					
					if (!(leftCmp instanceof Number)) {
						throw new CalculationException("Error processing comparison operators: left operand at row "+root.children[0].row+", col "+root.children[0].col+" is not a number");
					}
					else if (!(rightCmp instanceof Number)) {
						throw new CalculationException("Error processing comparison operators: right operand at row "+root.children[1].row+", col "+root.children[1].col+" is not a number");
					}
					else if ((leftCmp instanceof Float) || (leftCmp instanceof Double) || (rightCmp instanceof Float) || (rightCmp instanceof Double)) {
						return testSign(((Number)leftCmp).doubleValue() - ((Number)rightCmp).doubleValue(), (ComparisonType)root.cargo); 
					}
					else {
						return testSign(((Number)leftCmp).longValue() - ((Number)rightCmp).longValue(), (ComparisonType)root.cargo);
					}
				case NodeFunc		:
					final Object[]	paramValues = new Object[root.children.length];
					
					for (int index = 0; index < paramValues.length; index++) {
						paramValues[index] = processSyntaxTreeInternal((SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>)root.children[index],accessor);
					}
					for (int index = 0; index < paramValues.length; index++) {
						if (!(paramValues[index] instanceof Number)) {
							throw new CalculationException("Error calling function : parameter at row "+root.children[index].row+", col "+root.children[index].col+" is not a number");
						}
					}
					return callFunction((FunctionType)root.cargo,paramValues);
				case NodeGetInt		:
					return root.value;
				case NodeGetReal	:
					return Double.longBitsToDouble(root.value);
				case NodeGetVar		:
					try{return accessor.getVar((ContentNodeMetadata)root.cargo);
					} catch (ContentException e) {
						throw new CalculationException(e.getLocalizedMessage(),e);
					}
				case NodeLongIf		:
					final Object	longIfCond = processSyntaxTreeInternal((SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>)root.cargo,accessor);

					if (!(longIfCond instanceof Boolean)) {
						throw new CalculationException("Error processing of: operand at row "+((SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>)root.cargo).row+", col "+((SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>)root.cargo).col+" is not a boolean");
					}
					else if ((Boolean)longIfCond) {
						processSyntaxTreeInternal((SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>)root.children[0],accessor);
					}
					else {
						processSyntaxTreeInternal((SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>)root.children[1],accessor);
					}
					return null;
				case NodeMul		:
					final Object[]	mulValues = new Object[root.children.length];
					boolean			needDoubleMul = false;
					
					for (int index = 0; index < mulValues.length; index++) {
						mulValues[index] = processSyntaxTreeInternal((SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>)root.children[index],accessor);
					}
					for (int index = 0; index < mulValues.length; index++) {
						if ((mulValues[index] instanceof Float) || (mulValues[index] instanceof Double)) {
							needDoubleMul = true;
						}
						else if (!(mulValues[index] instanceof Number)) {
							throw new CalculationException("Error processing multiplication operators: operand at row "+root.children[index].row+", col "+root.children[index].col+" is not a number");
						}
					}
					if (needDoubleMul) {
						double	sum = 1;
						
						for (int index = 0; index < mulValues.length; index++) {
							if (((char[])root.cargo)[index] == '*') {
								sum *= ((Number)mulValues[index]).doubleValue();
							}
							else {
								sum /= ((Number)mulValues[index]).doubleValue();
							}
						}
						return sum;
					}
					else {
						long sum = 1;
						
						for (int index = 0; index < mulValues.length; index++) {
							if (((char[])root.cargo)[index] == '*') {
								sum *= ((Number)mulValues[index]).longValue();
							}
							else {
								sum /= ((Number)mulValues[index]).longValue();
							}
						}
						return sum;
					}
				case NodeNegation	:
					final Object	negationResult = processSyntaxTreeInternal((SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>)root.cargo,accessor);
					
					if (negationResult instanceof Number) {
						if ((negationResult instanceof Float) || (negationResult instanceof Double)) {
							return -((Number)negationResult).doubleValue();
						}
						else {
							return -((Number)negationResult).longValue();
						}
					}
					else {
						throw new CalculationException("Error processing negation: operand at row "+((SyntaxNode<?,?>)root.cargo).row+", col "+((SyntaxNode<?,?>)root.cargo).col+" is not a number");
					}
				case NodeNot		:
					final Object	notResult = processSyntaxTreeInternal((SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>)root.cargo,accessor);
					
					if (notResult instanceof Boolean) {
						return !(Boolean)notResult;
					}
					else {
						throw new CalculationException("Error processing negation: operand at row "+((SyntaxNode<?,?>)root.cargo).row+", col "+((SyntaxNode<?,?>)root.cargo).col+" is not a boolean");
					}
				case NodeOr			:
					final Object[]	orValues = new Object[root.children.length];
					
					for (int index = 0; index < orValues.length; index++) {
						orValues[index] = processSyntaxTreeInternal((SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>)root.children[index],accessor);
					}
					for (int index = 0; index < orValues.length; index++) {
						if (!(orValues[index] instanceof Boolean)) {
							throw new CalculationException("Error processing multiplication operators: operand at row "+root.children[index].row+", col "+root.children[index].col+" is not a boolean");
						}
					}
					for (Object item : orValues) {
						if ((Boolean)item) {
							return true;
						}
					}
					return false;
				case NodePrint		:
					final Object[]	printValues = new Object[root.children.length];
					
					for (int index = 0; index < printValues.length; index++) {
						printValues[index] = processSyntaxTreeInternal((SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>)root.children[index],accessor);
					}
					
					try{accessor.print((String)root.cargo,printValues);
					} catch (ContentException e) {
						throw new CalculationException(e.getLocalizedMessage(),e);
					}
					break;
				case NodeReturn		:
					throw new ReturnException();
				case NodeSequence	:
					for (SyntaxNode<?, ?> item : root.children) {
						processSyntaxTreeInternal((SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>)item,accessor);
					}
					break;
				case NodeShortIf	:
					final Object	shortIfCond = processSyntaxTreeInternal((SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>)root.cargo,accessor);

					if (!(shortIfCond instanceof Boolean)) {
						throw new CalculationException("Error processing of: operand at row "+((SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>)root.cargo).row+", col "+((SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>)root.cargo).col+" is not a boolean");
					}
					else if ((Boolean)shortIfCond) {
						processSyntaxTreeInternal((SyntaxNode<SyntaxNodeType,SyntaxNode<?,?>>)root.children[0],accessor);
					}
					return null;
				default	:
					throw new IllegalArgumentException("Unsupported node type ["+root.getType()+"] in the tree");
			}
			return null;
		}
	}

	private static Object callFunction(final FunctionType func, final Object[] parameters) {
		switch (func) {
			case ArcCos	: return Math.acos(((Number)parameters[0]).doubleValue());
			case ArcSin	: return Math.asin(((Number)parameters[0]).doubleValue());
			case ArcTan	: return Math.atan(((Number)parameters[0]).doubleValue());
			case Cos	: return Math.cos(((Number)parameters[0]).doubleValue());
			case Exp	: return Math.exp(((Number)parameters[0]).doubleValue());
			case Exp10	: return Math.exp(LOG10*((Number)parameters[0]).doubleValue());
			case Ln		: return Math.log(((Number)parameters[0]).doubleValue());
			case Ln10	: return INV_LOG10 * Math.log(((Number)parameters[0]).doubleValue());
			case Sin	: return Math.sin(((Number)parameters[0]).doubleValue());
			case Sqrt	: return Math.sqrt(((Number)parameters[0]).doubleValue());
			case Tan	: return Math.tan(((Number)parameters[0]).doubleValue());
			default	: throw new IllegalArgumentException("Unsupported function call ["+func+"] in the tree");
		}
	}

	private static boolean testSign(final double sign, final ComparisonType comp) {
		switch (comp) {
			case EQ	: return sign == 0;
			case GE	: return sign >= 0;
			case GT	: return sign > 0;
			case LE	: return sign <= 0;
			case LT	: return sign < 0;
			case NE	: return sign != 0;
			default	: throw new IllegalArgumentException("Unsupported comparison operator ["+comp+"] in the tree");
		}
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
