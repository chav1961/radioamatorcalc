package chav1961.calc.environment;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Constants {
	public static final Color		LEFT_PANE_COLOR = new Color(240,240,240);	
	public static final Color		CENTER_PANE_COLOR = Color.WHITE;

	public static final String		PAGE_PATH = "page";
	public static final String		PLUGIN_PATH = "plugin";
	public static final String		USES_PATH = "uses";
	public static final String		TAGS_PATH = "tags";
	public static final String		SEE_ALSO_PATH = "seealso";
	
	public static final String		PIPE_START_NODE = "Pipe.StartNode"; 
	public static final String		PIPE_TERMINAL_NODE = "Pipe.TerminalNode"; 
	public static final String		PIPE_FORMULA_NODE = "Pipe.FormulaNode"; 
	public static final String		PIPE_SWITCH_NODE = "Pipe.SwitchNode"; 
	public static final String		PIPE_MAP_NODE = "Pipe.MapNode"; 
	public static final String		PIPE_REDUCE_NODE = "Pipe.ReduceNode"; 

	public static final Icon		LEFT_ORDINAL = new ImageIcon(Constants.class.getResource("leftOrdinal.png"));
	public static final Icon		LEFT_SOURCE = new ImageIcon(Constants.class.getResource("leftSource.png"));
	public static final Icon		LEFT_REDUCE = new ImageIcon(Constants.class.getResource("leftReduce.png"));
	public static final Icon		LEFT_ADVANCED = new ImageIcon(Constants.class.getResource("leftAdvanced.png"));
	public static final Icon		RIGHT_ORDINAL = new ImageIcon(Constants.class.getResource("rightOrdinal.png"));
	public static final Icon		RIGHT_TERMINAL = new ImageIcon(Constants.class.getResource("rightTerminal.png"));
	public static final Icon		RIGHT_TRUE = new ImageIcon(Constants.class.getResource("rightTrue.png"));
	public static final Icon		RIGHT_FALSE = new ImageIcon(Constants.class.getResource("rightFalse.png"));
	public static final Icon		RIGHT_MAP = new ImageIcon(Constants.class.getResource("rightMap.png"));
}
