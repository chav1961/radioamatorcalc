package chav1961.calc.script;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import chav1961.calc.script.ScriptProcessor.Lexema;
import chav1961.calc.script.ScriptProcessor.LexemaType;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.ui.HighlightItem;
import chav1961.purelib.ui.swing.useful.JTextPaneHighlighter;

public class ScriptEditor  extends JTextPaneHighlighter<LexemaType>{
	private static final long 				serialVersionUID = 1068656384609061286L;
	
	{	
		store(ScriptProcessor.LexemaType.LexEOF,false,false,Color.white);
		store(ScriptProcessor.LexemaType.LexOpen,false,false,Color.black);
		store(ScriptProcessor.LexemaType.LexClose,false,false,Color.black);
		store(ScriptProcessor.LexemaType.LexDot,false,false,Color.black);
		store(ScriptProcessor.LexemaType.LexList,false,false,Color.black);
		store(ScriptProcessor.LexemaType.LexRange,false,false,Color.black);
		store(ScriptProcessor.LexemaType.LexSemicolon,false,false,Color.black);
		
		store(ScriptProcessor.LexemaType.LexPlus,false,false,Color.black);
		store(ScriptProcessor.LexemaType.LexMinus,false,false,Color.black);
		store(ScriptProcessor.LexemaType.LexMul,false,false,Color.black);
		store(ScriptProcessor.LexemaType.LexDiv,false,false,Color.black);
		store(ScriptProcessor.LexemaType.LexIDiv,true,false,Color.black);
		store(ScriptProcessor.LexemaType.LexMod,true,false,Color.black);

		store(ScriptProcessor.LexemaType.LexGE,false,false,Color.black);
		store(ScriptProcessor.LexemaType.LexGT,false,false,Color.black);
		store(ScriptProcessor.LexemaType.LexLE,false,false,Color.black);
		store(ScriptProcessor.LexemaType.LexLT,false,false,Color.black);
		store(ScriptProcessor.LexemaType.LexEQ,false,false,Color.black);
		store(ScriptProcessor.LexemaType.LexNE,false,false,Color.black);
		store(ScriptProcessor.LexemaType.LexIn,true,false,Color.black);
		
		store(ScriptProcessor.LexemaType.LexFSin,true,true,Color.black);
		store(ScriptProcessor.LexemaType.LexFCos,true,true,Color.black);
		store(ScriptProcessor.LexemaType.LexFTan,true,true,Color.black);
		store(ScriptProcessor.LexemaType.LexFASin,true,true,Color.black);
		store(ScriptProcessor.LexemaType.LexFACos,true,true,Color.black);
		store(ScriptProcessor.LexemaType.LexFATan,true,true,Color.black);
		store(ScriptProcessor.LexemaType.LexFExp,true,true,Color.black);
		store(ScriptProcessor.LexemaType.LexFExp10,true,true,Color.black);
		store(ScriptProcessor.LexemaType.LexFLn,true,true,Color.black);
		store(ScriptProcessor.LexemaType.LexFLog10,true,true,Color.black);
		store(ScriptProcessor.LexemaType.LexFSqr,true,true,Color.black);
		store(ScriptProcessor.LexemaType.LexFSqrt,true,true,Color.black);

		store(ScriptProcessor.LexemaType.LexAnd,true,false,Color.magenta);
		store(ScriptProcessor.LexemaType.LexOr,true,false,Color.magenta);
		store(ScriptProcessor.LexemaType.LexNot,true,false,Color.magenta);
		store(ScriptProcessor.LexemaType.LexAssign,false,false,Color.black);
		
		store(ScriptProcessor.LexemaType.LexInt,true,true,Color.blue);
		store(ScriptProcessor.LexemaType.LexReal,true,true,Color.blue);
		store(ScriptProcessor.LexemaType.LexString,true,true,Color.blue);
		store(ScriptProcessor.LexemaType.LexBoolean,true,true,Color.blue);

		store(ScriptProcessor.LexemaType.LexPlugin,false,true,Color.darkGray);
		store(ScriptProcessor.LexemaType.LexName,false,true,Color.darkGray);

		store(ScriptProcessor.LexemaType.LexIntValue,true,false,Color.green);
		store(ScriptProcessor.LexemaType.LexRealValue,true,false,Color.green);
		store(ScriptProcessor.LexemaType.LexStringValue,true,false,Color.green);
		store(ScriptProcessor.LexemaType.LexTrue,true,false,Color.green);
		store(ScriptProcessor.LexemaType.LexFalse,true,false,Color.green);

		store(ScriptProcessor.LexemaType.LexIf,true,false,Color.blue);
		store(ScriptProcessor.LexemaType.LexThen,true,false,Color.blue);
		store(ScriptProcessor.LexemaType.LexElse,true,false,Color.blue);
		store(ScriptProcessor.LexemaType.LexWhile,true,false,Color.blue);
		store(ScriptProcessor.LexemaType.LexRepeat,true,false,Color.blue);
		store(ScriptProcessor.LexemaType.LexUntil,true,false,Color.blue);
		store(ScriptProcessor.LexemaType.LexFor,true,false,Color.blue);
		store(ScriptProcessor.LexemaType.LexCase,true,false,Color.blue);
		store(ScriptProcessor.LexemaType.LexOf,true,false,Color.blue);
		store(ScriptProcessor.LexemaType.LexBreak,true,false,Color.blue);
		store(ScriptProcessor.LexemaType.LexContinue,true,false,Color.blue);
		store(ScriptProcessor.LexemaType.LexReturn,true,false,Color.blue);
		store(ScriptProcessor.LexemaType.LexBegin,true,false,Color.cyan);
		store(ScriptProcessor.LexemaType.LexEnd,true,false,Color.cyan);
		store(ScriptProcessor.LexemaType.LexPrint,true,false,Color.magenta);
		store(ScriptProcessor.LexemaType.LexCall,true,false,Color.magenta);
		store(ScriptProcessor.LexemaType.LexError,true,true,Color.RED);
		store(ScriptProcessor.LexemaType.LexComment,false,true,Color.lightGray);
		store(ScriptProcessor.LexemaType.LexRoot,false,false,Color.black);
	}
	
	public ScriptEditor() {
		super(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected HighlightItem<LexemaType>[] parseString(final String text) {
		try{final List<Lexema> 						list = ScriptProcessor.buildLexemaList(text,true,true);
			final List<HighlightItem<LexemaType>>	result = new ArrayList<>();
			
			LexemaType	prev = null;
			int			prevPos = -1;
			
			for (Lexema item : list) {
				if (prevPos != -1) {
					result.add(new HighlightItem<LexemaType>(prevPos,item.displ,prev));
				}
				prevPos = item.displ;
				prev = item.type;
			}
			return result.toArray(new HighlightItem[result.size()]);
		} catch (SyntaxException e) {
			e.printStackTrace();
			return new HighlightItem[0];
		}
	}
	
	private void store(final ScriptProcessor.LexemaType lexType, final boolean bold, final boolean italic, final Color color) {
		final SimpleAttributeSet	sas = new SimpleAttributeSet();
		
		StyleConstants.setBold(sas,bold);
		StyleConstants.setItalic(sas,italic);
		StyleConstants.setForeground(sas,color);
		characterStyles.put(lexType,sas);		
	}
}
