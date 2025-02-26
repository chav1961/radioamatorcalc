package chav1961.calc;

/**
 * <p>This class contains all the localization keys in the application.</p>
 * 
 * @author Alexander Chernomyrdin aka chav1961
 * @since 0.0.1
 */

public abstract class LocalizationKeys {
	public static final String		MENU_FILE = "menu.file";
	public static final String		MENU_FILE_TOOLTIP = "menu.ttFile";
	public static final String		MENU_FILE_EXIT = "menu.exit";
	public static final String		MENU_FILE_EXIT_TOOLTIP = "menu.ttExit";
	
	public static final String		MENU_HELP = "menu.help";
	public static final String		MENU_HELP_TOOLTIP = "menu.ttHelp";
	public static final String		MENU_HELP_ABOUT = "menu.about";
	public static final String		MENU_HELP_ABOUT_TOOLTIP = "menu.ttAbout";

	public static final String		SETTINGS_CAPTION = "settings.caption";
	public static final String		SETTINGS_HELP = "settings.help";
	
	public static final String		HELP_ABOUT_APPLICATION = "help.aboutApplication";
	public static final String		HELP_OVERVIEW = "help.overview";
	
	public static final String		CONFIRM_BUTTON_OPEN = "confirm.button.open";
	public static final String		CONFIRM_BUTTON_OPEN_TOOLTIP = "confirm.button.open.tooltip";
	public static final String		CONFIRM_BUTTON_SAVE = "confirm.button.save";
	public static final String		CONFIRM_BUTTON_SAVE_TOOLTIP = "confirm.button.save.tooltip";
	public static final String		CONFIRM_BUTTON_YES = "confirm.button.yes";
	public static final String		CONFIRM_BUTTON_NO = "confirm.button.no";
	public static final String		CONFIRM_BUTTON_CANCEL = "confirm.button.cancel";
	
	public static final String		CONFIRM_CLEAR_DESKTOP_CAPTION = "confirm.clearDesktop.caption";
	public static final String		CONFIRM_CLEAR_DESKTOP_MESSAGE = "confirm.clearDesktop.message";
	public static final String		CONFIRM_SAVE_PIPE_CAPTION = "confirm.savePipe.caption";
	public static final String		CONFIRM_SAVE_PIPE_MESSAGE = "confirm.savePipe.message";
	
	public static final String		CONFIRM_FILEFILTER_PIPE = "confirm.fileFilter.pipe";
	
	public static final String		MESSAGE_REINDEXED = "message.reindexed";
	public static final String		MESSAGE_PDF_INDEXED = "message.pdf.indexed";
	
	public static final String		TITLE_HELP_ABOUT_APPLICATION = "title.help.aboutApplication";
	public static final String		TITLE_APPLICATION = "title.Application";

	public static final String		ROOT = "root";
	public static final String		ROOT_TOOLTIP = "root.tooltip";
	
	public static final String		DESKTOP_TOOLBAR_CLOSEALL = "desktop.toolbar.closeAll";
	public static final String		DESKTOP_TOOLBAR_CLOSEALL_TOOLTIP = "desktop.toolbar.closeAllTT";
	public static final String		DESKTOP_STATE_NAME_LABEL = "desktop.state.nameLabel";

	public static final String		SEARCH_TOOLBAR_CLOSE = "search.toolbar.close";
	public static final String		SEARCH_TOOLBAR_CLOSE_TOOLTIP = "search.toolbar.closeTT";

	public static final String		SEARCH_LABEL = "search.label";
	public static final String		SEARCH_TOOLTIP = "search.tooltip";
	public static final String		SEARCH_FOUND = "search.found";
	public static final String		SEARCH_NOT_FOUND = "search.notfound";
	
	public static final String		SEARCH_USES = "search.uses";
	public static final String		SEARCH_TAGS = "search.tags";
	public static final String		SEARCH_SEE_ALSO = "search.seealso";

	//
	//	Formulas tags to use as cross-references
	//
	
	public static final String		FORMULA_INDUCTANCE_ONE_LAYER_COIL = "formula.inductanceOneLayerCoil";
	public static final String		FORMULA_NUMBER_OF_COILS_ONE_LAYER_COIL = "formula.numberOfCoilsOneLayerCoil";
	public static final String 		FORMULA_INDUCTANCE_RING_COIL = "formula.inductanceRingCoil";
	public static final String 		FORMULA_COILS_RING_COIL = "formula.coilsRingCoil";
	public static final String 		FORMULA_INDUCTION_RING_COIL = "formula.inductionRingCoil";

	//
	//	Pipe tags to use in the pipes
	//
	
	public static final String		PIPE_CALCULATION_SUCCESS = "pipe.calculationSuccess";
	public static final String		PIPE_CALCULATION_FAILED = "pipe.calculationFailed";
	public static final String		PIPE_PARAMETER_NAME = "pipe.parameterName";
	public static final String		PIPE_PARAMETER_TYPE = "pipe.parameterType";

	public static final String		PIPE_PLUGIN_ID = "pipe.pluginId"; 
	public static final String		PIPE_PLUGIN_ID_TOOLTIP = "pipe.pluginIdTooltip";
	public static final String		PIPE_PLUGIN_INSTANCE = "pipe.pluginInstanceName";
	public static final String		PIPE_PLUGIN_INSTANCE_TOOLTIP = "pipe.pluginInstanceNameTooltip"; 
	public static final String		PIPE_PLUGIN_FIELD = "pipe.pluginFieldName";
	public static final String		PIPE_PLUGIN_FIELD_TOOLTIP = "pipe.pluginFieldNameTooltip";
	public static final String		PIPE_PLUGIN_FIELD_TYPE = "pipe.pluginFieldtype";
	public static final String		PIPE_PLUGIN_FIELD_TYPE_TOOLTIP = "pipe.pluginFieldtypeTooltip"; 	
	
	
	//
	//	Pipe items localization
	//

	public static final String		PIPE_START_COMMENT = "pipe.start.comment";
	public static final String		PIPE_START_MESSAGE_DUPLICATE_FIELD = "StartNode.message.duplicateField";
	
	public static final String		PIPE_FORMULA_COMMENT = "pipe.formula.comment";
	public static final String		PIPE_FORMULA_COMMENT_TOOLTIP = "pipe.formula.comment.tooltip";
	public static final String		PIPE_FORMULA_EXPRESION = "pipe.formula.expression";
	public static final String		PIPE_FORMULA_EXPRESION_TOOLTIP = "pipe.formula.expression.tooltip";

	public static final String		PIPE_SWITCH_COMMENT = "pipe.switch.comment";
	public static final String		PIPE_SWITCH_COMMENT_TOOLTIP = "pipe.switch.comment.tooltip";
	public static final String		PIPE_SWITCH_EXPRESION = "pipe.switch.expression";
	public static final String		PIPE_SWITCH_EXPRESION_TOOLTIP = "pipe.switch.expression.tooltip";
	public static final String		PIPE_SWITCH_ASKBEFORE = "pipe.switch.askbefore";
	public static final String		PIPE_SWITCH_ASKBEFORE_TOOLTIP = "pipe.switch.askbefore.tooltip";
	
	public static final String		PIPE_TERMINAL_NODE_TYPE = "pipe.terminal.nodetype";
	public static final String		PIPE_TERMINAL_NODE_TYPE_SUCCESS = "pipe.terminal.nodetype.success";
	public static final String		PIPE_TERMINAL_NODE_TYPE_FAILED = "pipe.terminal.nodetype.failed";
	public static final String		PIPE_TERMINAL_FORMAT = "pipe.terminal.format";
	public static final String		PIPE_TERMINAL_PARAMETERS = "pipe.terminal.parameters";
}
