package chav1961.calc.windows;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import chav1961.calc.interfaces.DragMode;
import chav1961.calc.interfaces.MetadataTarget;
import chav1961.calc.interfaces.PluginInterface;
import chav1961.calc.interfaces.TabContent;
import chav1961.calc.pipe.CalcPipeFrame;
import chav1961.calc.pipe.ConditionalPipeFrame;
import chav1961.calc.pipe.ContainerPipeFrame;
import chav1961.calc.pipe.DialogPipeFrame;
import chav1961.calc.pipe.InitialPipeFrame;
import chav1961.calc.pipe.JControlLabel;
import chav1961.calc.pipe.JControlTargetLabel;
import chav1961.calc.pipe.ModelItemListContainer;
import chav1961.calc.pipe.ModelItemListContainer.DropAction;
import chav1961.calc.pipe.TerminalPipeFrame;
import chav1961.calc.utils.PipeExecutor;
import chav1961.calc.utils.PipeLink;
import chav1961.calc.utils.PipeLink.PipeLinkType;
import chav1961.calc.utils.PipePluginFrame;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.PrintingException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.basic.subscribable.SubscribableInt;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.MutableContentNodeMetadata;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.model.interfaces.NodeMetadataOwner;
import chav1961.purelib.streams.JsonStaxParser;
import chav1961.purelib.streams.JsonStaxPrinter;
import chav1961.purelib.streams.interfaces.JsonStaxParserLexType;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.interfaces.OnAction;
import chav1961.purelib.ui.swing.useful.DnDManager;
import chav1961.purelib.ui.swing.useful.DnDManager.DnDInterface;
import chav1961.purelib.ui.swing.useful.DnDManager.DnDMode;
import chav1961.purelib.ui.swing.useful.JCloseableTab;
import chav1961.purelib.ui.swing.useful.JExtendedScrollPane;

//
//	Json serial file format:
//	<file> ::= <file_format_version><global_desc><number_of_items>[<item_desc>...]
//  <global_desc> ::= <pipe_name><pipe_comment><last_free_id><pipe_geometry>
//	<pipe_geometry> ::= <width><height>
//	<item_desc> ::= <item_id><item_type><item_geometry><content><links>
//  <item_geometry> ::= <window_modifiers><location><width><height>
//	<content> ::= {<initial_content>|<conditional_content>|<calculation_content>|<dialog_content>|<terminal_content>|<container_content>}
//	<initial_content> ::= <pipe_links><initial_code>
//	<conditional_content> ::= <income_pipe_links><outgoing_pipe_links><conditional_code>
//	<calculation_content> ::= <income_pipe_links><outgoing_pipe_links><calculation_code>
//	<dialog_content> ::= <caption_and_message><income_pipe_links><outgoing_pipe_links><visual_settings>
//	<terminal_content> ::= <message_and_error><pipe_links>
//	<container_content> ::= <plugin_reference><income_pipe_links><selected_action>
//	<links> ::= <pipe_link>
//

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.Application/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value = "chav1961.calc.pipe", tooltip = "chav1961.calc.pipe.tt", icon = "root:/WorkbenchTab!")
public class PipeTab extends JPanel implements AutoCloseable, LocaleChangeListener, TabContent, DnDInterface {
	private static final long 				serialVersionUID = 1L;
	
	public static final String				JSON_VERSION = "1.0";

	private static final String[]			AVAILABLE_VERISONS = {JSON_VERSION};
	
	private static final String				MODIFICATION_MARK = "*";
	private static final String				PIPE_SOURCE_TT = "chav1961.calc.pipe.screen.source.tt";	
	private static final String				PIPE_TARGET_TT = "chav1961.calc.pipe.screen.target.tt";	
	private static final String				PIPE_TRUE_TT = "chav1961.calc.pipe.screen.true.tt";	
	private static final String				PIPE_FALSE_TT = "chav1961.calc.pipe.screen.false.tt";	
	private static final String				PIPE_OK_TT = "chav1961.calc.pipe.screen.ok.tt";	
	private static final String				PIPE_CANCEL_TT = "chav1961.calc.pipe.screen.cancel.tt";	

	private static final URI				PIPE_MENU_ROOT = URI.create("ui:/model/navigation.top.pipeMenu");	
	private static final String				PIPE_MENU_CLEAN_NAME = "pipeMenu.clean";	
	private static final String				PIPE_MENU_VALIDATE_NAME = "pipeMenu.validate";	
	private static final String				PIPE_MENU_START_NAME = "pipeMenu.start";	
	private static final String				PIPE_MENU_DEBUG_NAME = "pipeMenu.debug";	
	private static final String				PIPE_MENU_STOP_NAME = "pipeMenu.stop";	

	private static final String				PIPE_EXECUTION_COMPLETED = "chav1961.calc.pipe.screen.execution.completed";	

	private static final String				JSON_VERSION_NAME = "version";
	private static final String				JSON_PIPE_NAME = "pipeName";
	private static final String				JSON_PIPE_DESCRIPTION = "pipeDescription";
	private static final String				JSON_LAST_UNIQUE_ID = "lastUniqueId";
	private static final String				JSON_GEOMETRY = "geometry";
	private static final String				JSON_GEOMETRY_WIDTH = "width";
	private static final String				JSON_GEOMETRY_HEIGHT = "height";
	private static final String				JSON_PIPE_ITEMS = "items";
	
	public final SubscribableInt			pluginCount = new SubscribableInt(); 
	
	private final JScrollPane				scroll;
	private final DnDManager				dndManager;
	private final PipeManager				pipeManager;
	private final URI						localizerURI;
	private final Localizer					localizer;
	private final LoggerFacade				logger;
	private final ContentMetadataInterface	ownModel, xmlModel;
	private final JCloseableTab				tab;
	private final JPopupMenu				popup;
	private final JToolBar					toolbar;

	private File							fileAssociated = null;
	private boolean							isModified = false;			
	private int								uniquePipeItemId = 1;
	private volatile Thread					executorThread = null;

	@LocaleResource(value="chav1961.calc.pipe",tooltip="chav1961.calc.pipe.tt")
	@Format("50m")
	private String							pipeName = "<new>";
	
	@LocaleResource(value="chav1961.calc.pipe",tooltip="chav1961.calc.pipe.tt")
	@Format("50m")
	private String 							pipeDescription = "";
	
	public PipeTab(final JTabbedPane tabs, final Localizer localizer, final LoggerFacade logger) throws SyntaxException, LocalizationException, ContentException {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.localizer = localizer;
			this.localizerURI = URI.create(this.getClass().getAnnotation(LocaleResourceLocation.class).value());
			this.logger = logger;
			this.ownModel = ContentModelFactory.forAnnotatedClass(this.getClass());
			try{
				this.pipeManager = new PipeManager(this,localizer,logger,ownModel);
			} catch (EnvironmentException | IOException e) {
				throw new ContentException(e);
			}
			this.dndManager = new DnDManager(pipeManager,this);
			
			localizer.associateValue(this.getClass().getAnnotation(LocaleResource.class).value(),()->new Object[] {isModified ? MODIFICATION_MARK : "",pipeName});
			
			try(final InputStream	is = this.getClass().getResourceAsStream("pipe.xml")) {
				this.xmlModel = ContentModelFactory.forXmlDescription(is);
			} catch (EnvironmentException | IOException e) {
				throw new ContentException(e);
			}
			
			this.tab = new JCloseableTab(localizer,this.ownModel.getRoot());
			this.popup = SwingUtils.toJComponent(xmlModel.byUIPath(PIPE_MENU_ROOT),JPopupMenu.class);
			this.toolbar = SwingUtils.toJComponent(xmlModel.byUIPath(PIPE_MENU_ROOT),JToolBar.class);
			SwingUtils.assignActionListeners(this.toolbar,this);
			
			setLayout(new BorderLayout());
			add(toolbar,BorderLayout.NORTH);
			
			scroll = new JExtendedScrollPane(pipeManager,false);
			pipeManager.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
			add(scroll,BorderLayout.CENTER);
			
			fillLocalizedStrings(localizer.currentLocale().getLocale(),localizer.currentLocale().getLocale());
			
			pluginCount.addListener((oldValue,newValue)->{
				((JMenuItem)SwingUtils.findComponentByName(popup,PIPE_MENU_CLEAN_NAME)).setEnabled(newValue != 0);
				((JButton)SwingUtils.findComponentByName(toolbar,PIPE_MENU_CLEAN_NAME)).setEnabled(newValue != 0);
				((JMenuItem)SwingUtils.findComponentByName(popup,PIPE_MENU_VALIDATE_NAME)).setEnabled(newValue != 0);
				((JButton)SwingUtils.findComponentByName(toolbar,PIPE_MENU_VALIDATE_NAME)).setEnabled(newValue != 0);
				refreshPlayingButtons(newValue != 0);
			});
			pluginCount.refresh();
		}
	}
	
	@Override
	public JPopupMenu getPopupMenu() {
		return popup;
	}

	@Override
	public JCloseableTab getTab() {
		return tab;
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings(oldLocale,newLocale);
	}

	@Override
	public void close() throws Exception {
		pipeManager.close();
		dndManager.close();
	}

	@Override
	public Class<?> getSourceClass(final DnDMode currentMode, final Component component, final int x, final int y) {
		if (component instanceof NodeMetadataOwner) {
			return ContentNodeMetadata.class;
		}
		else {
			return null;
		}
	}

	@Override
	public Object getSource(final DnDMode currentMode, final Component from, final int xFrom, final int yFrom, final Component to, final int xTo, final int yTo) {
		if (from instanceof NodeMetadataOwner) {
			return ((NodeMetadataOwner)from).getNodeMetadata(xFrom,yFrom);
		}
		else {
			return null;
		}
	}

	@Override
	public boolean canReceive(final DnDMode currentMode, final Component from, final int xFrom, final int yFrom, final Component to, final int xTo, final int yTo, final Class<?> contentClass) {
		if (from != to && (((from instanceof ModelItemListContainer) && (to instanceof ModelItemListContainer) && ((ModelItemListContainer)to).getDropAction() != DropAction.NONE) || ((from instanceof JControlLabel) && (to instanceof JControlLabel)))) {
			return ContentNodeMetadata.class.isAssignableFrom(contentClass) && (from instanceof NodeMetadataOwner) && (to instanceof MetadataTarget) && to.getBounds().contains(xTo,yTo);
		}
		else {
			return false;
		}
	}

	@Override
	public void track(final DnDMode currentMode, final Component from, final int xFromAbsolute, final int yFromAbsolute, final Component to, final int xToAbsolute, final int yToAbsolute) {
		// TODO Auto-generated method stub
	}

	@Override
	public void complete(final DnDMode currentMode, final Component from, final int xFrom, final int yFrom, final Component to, final int xTo, final int yTo, final Object content) {
		final PipeLink	pl;
	
		if ((to instanceof JControlLabel) && (to instanceof JControlTargetLabel)) {
			pl = new PipeLink(PipeLinkType.CONTROL_LINK,((JControlLabel)from).getOwner(),from,((JControlLabel)to).getOwner(),to,(MutableContentNodeMetadata)content,null);
			((MetadataTarget)to).drop(pl,xFrom,yFrom,xTo,yTo);
		}
		else if ((from instanceof ModelItemListContainer) && (to instanceof ModelItemListContainer)) {
			pl = new PipeLink(PipeLinkType.DATA_LINK,((ModelItemListContainer)from).getOwner(),from,((ModelItemListContainer)to).getOwner(),to,(MutableContentNodeMetadata)content,null);
			((MetadataTarget)to).drop(pl,xFrom,yFrom,xTo,yTo);
		}
		pipeManager.repaint();
	}

	public void serialize(final JsonStaxPrinter printer) throws IOException {
		if (printer == null) {
			throw new NullPointerException("Json printer can't be null");
		}
		else {
			printer.startObject();
			printer.name(JSON_VERSION_NAME).value(JSON_VERSION);
			serializeGlobal(printer);
			printer.splitter().name(JSON_PIPE_ITEMS);
			pipeManager.serializeFrames(printer);
			printer.endObject();
			printer.flush();
		}
	}

	public void deserialize(final JsonStaxParser parser) throws IOException {
		if (parser == null) {
			throw new NullPointerException("Json parser can't be null");
		}
		else {
			String	lastName = null;
			int		width = pipeManager.getPreferredSize().width, height = pipeManager.getPreferredSize().height; 
			int		currentState = 0;
			
loop:		while (parser.hasNext()) {
				final JsonStaxParserLexType lex = parser.next();
				
				switch (currentState) {
					case 0 :	// Before '{'
						if (lex == JsonStaxParserLexType.START_OBJECT) {
							currentState = 1;
						}
						else {
							throw new IOException(new SyntaxException(parser.row(),parser.col(),"'{' is missing"));
						}
						break;
					case 1 : 	// Name awaited
						if (lex == JsonStaxParserLexType.NAME) {
							switch (lastName = parser.name()) {
								case JSON_VERSION_NAME		: case JSON_PIPE_NAME		:
								case JSON_PIPE_DESCRIPTION	: case JSON_LAST_UNIQUE_ID	:
									currentState = 2;
									break;
								case JSON_GEOMETRY			:
									currentState = 10;
									break;
								case JSON_PIPE_ITEMS		:
									currentState = 20;
									break;
								default :
									throw new IOException(new SyntaxException(parser.row(),parser.col(),"Unsupported name ["+parser.name()+"] in the JSON"));
							}
						}
						else {
							throw new IOException(new SyntaxException(parser.row(),parser.col(),"Name is missing"));
						}
						break;
					case 2 :	// ':' after simple name awaiting
						if (lex == JsonStaxParserLexType.NAME_SPLITTER) {
							currentState = 3;
						}
						else {
							throw new IOException(new SyntaxException(parser.row(),parser.col(),"':' is missing"));
						}
						break;
					case 3 :	// value for simple name
						switch (lex) {
							case INTEGER_VALUE	:
								if (JSON_LAST_UNIQUE_ID.equals(lastName)) {
									uniquePipeItemId = (int)parser.intValue();
								}
								else {
									throw new IOException(new SyntaxException(parser.row(),parser.col(),"Illegal value type ["+lex+"] for name ["+lastName+"]"));
								}
								break;
							case STRING_VALUE	:
								switch (lastName) {
									case JSON_VERSION_NAME		:
										if (!JSON_VERSION.equals(parser.stringValue())) {
											throw new IOException(new SyntaxException(parser.row(),parser.col(),"Unsupported JSON serialization format version ["+parser.stringValue()+"]. Available versions are "+Arrays.toString(AVAILABLE_VERISONS)));
										}
										break;
									case JSON_PIPE_NAME			:
										pipeName = parser.stringValue();
										break;
									case JSON_PIPE_DESCRIPTION	:
										pipeDescription = parser.stringValue();
										break;
									default :
										throw new IOException(new SyntaxException(parser.row(),parser.col(),"Illegal value type ["+lex+"] for name ["+lastName+"]"));
								}
								break;
							default				:
								throw new IOException(new SyntaxException(parser.row(),parser.col(),"String or integer value awaited"));
						}
						currentState = 4;	
						break;
					case 4 :	// list splitter awaited
						if (lex == JsonStaxParserLexType.LIST_SPLITTER) {
							currentState = 1;
						}
						else if (lex == JsonStaxParserLexType.END_OBJECT) {
							break loop;
						}
						else {
							throw new IOException(new SyntaxException(parser.row(),parser.col(),"',' or '}' is missing"));
						}
						break;
					case 10 :	// ':' after 'geometry'
						if (lex == JsonStaxParserLexType.NAME_SPLITTER) {
							currentState = 11;
						}
						else {
							throw new IOException(new SyntaxException(parser.row(),parser.col(),"':' is missing"));
						}
						break;
					case 11 :	// '{' awaiting
						if (lex == JsonStaxParserLexType.START_OBJECT) {
							currentState = 12;
						}
						else {
							throw new IOException(new SyntaxException(parser.row(),parser.col(),"':' is missing"));
						}
						break;
					case 12 : 	// Name awaited
						if (lex == JsonStaxParserLexType.NAME) {
							switch (lastName = parser.name()) {
								case JSON_GEOMETRY_WIDTH	:
								case JSON_GEOMETRY_HEIGHT	:
									currentState = 13;
									break;
								default :
									throw new IOException(new SyntaxException(parser.row(),parser.col(),"Unsupported name ["+parser.name()+"] in the JSON"));
							}
						}
						else {
							throw new IOException(new SyntaxException(parser.row(),parser.col(),"Name is missing"));
						}
						break;
					case 13 :	// ':' after geometry item name awaiting
						if (lex == JsonStaxParserLexType.NAME_SPLITTER) {
							currentState = 14;
						}
						else {
							throw new IOException(new SyntaxException(parser.row(),parser.col(),"':' is missing"));
						}
						break;
					case 14	:	// value for geometry item awaiting
						if (lex == JsonStaxParserLexType.INTEGER_VALUE) {
							switch (lastName) {
								case JSON_GEOMETRY_WIDTH	:
									width = (int)parser.intValue();
									break;
								case JSON_GEOMETRY_HEIGHT	:
									height = (int)parser.intValue();
									break;
								default :
									throw new IOException(new SyntaxException(parser.row(),parser.col(),"Unsupported name ["+parser.name()+"] in the JSON"));
							}
							currentState = 15;
						}
						else {
							throw new IOException(new SyntaxException(parser.row(),parser.col(),"Integer value is missing"));
						}
						break;
					case 15	:	// list splitter after geometry item
						if (lex == JsonStaxParserLexType.LIST_SPLITTER) {
							currentState = 12;
						}
						else if (lex == JsonStaxParserLexType.END_OBJECT) {
							pipeManager.setPreferredSize(new Dimension(width,height));
							currentState = 4;
						}
						else {
							throw new IOException(new SyntaxException(parser.row(),parser.col(),"',' or '}' is missing"));
						}
						break;
					case 20 :	// ':' after pipe items
						if (parser.hasNext()) {
							parser.next();
							pipeManager.deserializeFrames(parser);
							currentState = 1;
						}
						else {
							throw new IOException(new SyntaxException(parser.row(),parser.col(),"Premature end of pipe items data"));
						}
						break;
					default : 
						throw new UnsupportedOperationException("Internal error");
				}
			}
		}
	}
	
	public void clear() {
		cleanPipe();
	}
	
	public File getFileAssciated() {
		return fileAssociated;
	}

	public void setFileAssciated(final File fileAssociated) {
		this.fileAssociated = fileAssociated;
	}
	
	DragMode setDragMode(final DragMode newMode) {
		switch (newMode) {
			case CONTROLS	:
				return toDragMode(dndManager.selectDnDMode(DnDMode.COPY));
			case LINKS		:
				return toDragMode(dndManager.selectDnDMode(DnDMode.LINK));
			case NONE		:
				return toDragMode(dndManager.selectDnDMode(DnDMode.NONE));
			default	:
				throw new UnsupportedOperationException("Drag mode ["+newMode+"] is not supported yet");
		}
	}
	
	private DragMode toDragMode(final DnDMode mode) {
		switch (mode) {
			case COPY	: return DragMode.CONTROLS;
			case LINK	: return DragMode.LINKS;
			default 	: return DragMode.NONE;
		}
	}
	
	@OnAction("action:/pipeMenu.new")
	private void showPopup() {
		final Container	btn = SwingUtils.findComponentByName(popup,xmlModel.byUIPath(URI.create("ui:/model/navigation.top.pipeMenu/navigation.node.pipeMenu.new")).getName());
		
		try{getPopupMenu().show(btn,btn.getWidth()/2,btn.getHeight()/2);
		} catch (java.awt.IllegalComponentStateException e) {
		}
	}
	
	@OnAction("action:/newInitial")
	public InitialPipeFrame newInitial() {
		return newInitial(uniquePipeItemId++);
	}
	
	public InitialPipeFrame newInitial(final int unique) {
		final ContentNodeMetadata	initial = new MutableContentNodeMetadata("initial",InitialPipeFrame.class,"./initial",localizerURI,PIPE_SOURCE_TT, PIPE_SOURCE_TT, null, null, URI.create("app:action:/start"),null); 
		
		try{final InitialPipeFrame	frame = new InitialPipeFrame(unique, pipeManager, localizer, initial, xmlModel); 
			
			putPlugin(frame);
			return frame;
		} catch (ContentException e) {
			logger.message(Severity.error,e,e.getLocalizedMessage());
			return null;
		}
	}

	@OnAction("action:/newConditional")
	public ConditionalPipeFrame newConditional() {
		return newConditional(uniquePipeItemId++);
	}
	
	public ConditionalPipeFrame newConditional(final int unique) {
		final ContentNodeMetadata	inner = new MutableContentNodeMetadata("inner",ConditionalPipeFrame.class,"./inner",localizerURI,PIPE_TARGET_TT, PIPE_TARGET_TT, null, null, URI.create("app:action:/inner"),null); 
		final ContentNodeMetadata	onTrue = new MutableContentNodeMetadata("onTrue",ConditionalPipeFrame.class,"./ontrue",localizerURI,PIPE_TRUE_TT, PIPE_TRUE_TT, null, null, URI.create("app:action:/ontrue"),null); 
		final ContentNodeMetadata	onFalse = new MutableContentNodeMetadata("onFalse",ConditionalPipeFrame.class,"./onfalse",localizerURI,PIPE_FALSE_TT, PIPE_FALSE_TT, null, null, URI.create("app:action:/onfalse"),null); 
		
		try{final ConditionalPipeFrame	frame = new ConditionalPipeFrame(unique, pipeManager, localizer, inner, onTrue, onFalse, xmlModel); 
			
			putPlugin(frame);
			return frame;
		} catch (ContentException e) {
			logger.message(Severity.error,e,e.getLocalizedMessage());
			return null;
		}
	}
	
	@OnAction("action:/newCalc")
	public CalcPipeFrame newCalc() {
		return newCalc(uniquePipeItemId++);
	}
	
	public CalcPipeFrame newCalc(final int unique) {
		final ContentNodeMetadata	inner = new MutableContentNodeMetadata("inner",CalcPipeFrame.class,"./inner",localizerURI,PIPE_TARGET_TT, PIPE_TARGET_TT, null, null, URI.create("app:action:/inner"),null); 
		final ContentNodeMetadata	outer = new MutableContentNodeMetadata("outer",CalcPipeFrame.class,"./outer",localizerURI,PIPE_SOURCE_TT, PIPE_SOURCE_TT, null, null, URI.create("app:action:/outer"),null); 
		
		try{final CalcPipeFrame		frame = new CalcPipeFrame(unique, pipeManager, localizer, inner, outer, xmlModel);
		
			putPlugin(frame);
			return frame;
		} catch (ContentException e) {
			logger.message(Severity.error,e,e.getLocalizedMessage());
			return null;
		}
	}

	@OnAction("action:/newDialog")
	public DialogPipeFrame newDialog() {
		return newDialog(uniquePipeItemId++);
	}
	
	public DialogPipeFrame newDialog(final int unique) {
		final ContentNodeMetadata	inner = new MutableContentNodeMetadata("inner",DialogPipeFrame.class,"./inner",localizerURI,PIPE_TARGET_TT, PIPE_TARGET_TT, null, null, URI.create("app:action:/inner"),null); 
		final ContentNodeMetadata	onTrue = new MutableContentNodeMetadata("onTrue",ConditionalPipeFrame.class,"./ontrue",localizerURI,PIPE_OK_TT, PIPE_OK_TT, null, null, URI.create("app:action:/onok"),null); 
		final ContentNodeMetadata	onFalse = new MutableContentNodeMetadata("onFalse",ConditionalPipeFrame.class,"./onfalse",localizerURI,PIPE_CANCEL_TT, PIPE_CANCEL_TT, null, null, URI.create("app:action:/oncancel"),null); 
		
		try{final DialogPipeFrame	frame = new DialogPipeFrame(unique, pipeManager, localizer, inner, onTrue, onFalse, xmlModel); 
			
			putPlugin(frame);
			return frame;
		} catch (ContentException e) {
			logger.message(Severity.error,e,e.getLocalizedMessage());
			return null;
		}
	}

	@OnAction("action:/newTerminal")
	public TerminalPipeFrame newTerminal() {
		return newTerminal(uniquePipeItemId++);
	}
		
	public TerminalPipeFrame newTerminal(final int unique) {
		final ContentNodeMetadata	terminal = new MutableContentNodeMetadata("terminal",TerminalPipeFrame.class,"./terminal",localizerURI,PIPE_TARGET_TT, PIPE_TARGET_TT, null, null, URI.create("app:action:/stop"),null); 
		
		try{final TerminalPipeFrame	frame = new TerminalPipeFrame(unique, pipeManager, localizer, terminal, xmlModel);
		
			putPlugin(frame);
			return frame;
		} catch (ContentException e) {
			logger.message(Severity.error,e,e.getLocalizedMessage());
			return null;
		}
	}

	@OnAction("action:/cleanPipe")
	private void cleanPipe() {
		try{pipeManager.clean(logger);
			pluginCount.set(0);
		} catch (LocalizationException e) {
			logger.message(Severity.error,e,e.getLocalizedMessage());
		}
	}
	
	@OnAction("action:/validatePipe")
	private void validatePipe() {
		pipeManager.validatePipe(logger);
		refreshPlayingButtons(pluginCount.get() != 0);
	}

	@OnAction("action:/startPipe")
	private void startPipe() {
		if (pipeManager.validatePipe(logger)) {
			executorThread = new Thread(()->{
								try{refreshPlayingButtons(pluginCount.get() != 0);
									new PipeExecutor(logger,pipeManager.getPipeComponents(),false).run();
									logger.message(Severity.info,localizer.getValue(PIPE_EXECUTION_COMPLETED));
								} catch (Exception exc) {
									exc.printStackTrace();
									logger.message(Severity.error,exc,exc.getLocalizedMessage() == null ? "???" : exc.getLocalizedMessage());
								} finally {
									SwingUtilities.invokeLater(()->{refreshPlayingButtons(pluginCount.get() != 0);});
								}
							});
			executorThread.setDaemon(true);
			executorThread.start();
		}
		else {
			refreshPlayingButtons(pluginCount.get() != 0);
		}
	}
	
	@OnAction("action:/debugPipe")
	private void debugPipe() {
		if (pipeManager.validatePipe(logger)) {
			executorThread = new Thread(()->{
								try{refreshPlayingButtons(pluginCount.get() != 0);
									new PipeExecutor(logger,pipeManager.getPipeComponents(),true).run();
									logger.message(Severity.info,localizer.getValue(PIPE_EXECUTION_COMPLETED));
								} catch (Exception exc) {
									logger.message(Severity.error,exc,exc.getLocalizedMessage() == null ? "???" : exc.getLocalizedMessage());
								} finally {
									SwingUtilities.invokeLater(()->{refreshPlayingButtons(pluginCount.get() != 0);});
								}
							});
			executorThread.setDaemon(true);
			executorThread.start();
		}
		else {
			refreshPlayingButtons(pluginCount.get() != 0);
		}
	}

	@OnAction("action:/stopPipe")
	private void stopPipe() {
		if (executorThread != null && executorThread.isAlive()) {
			executorThread.interrupt();
		}
	}

	private void refreshPlayingButtons(final boolean totalEnable) {
		final boolean	isPipeValid = pipeManager.validatePipe(PureLibSettings.NULL_LOGGER);
		final boolean	threadIsAlive = executorThread != null && executorThread.isAlive();
		boolean			startButton, stopButton;
		
		startButton = stopButton = totalEnable && isPipeValid;
		startButton &= !threadIsAlive;
		stopButton &= threadIsAlive;
		
		((JMenuItem)SwingUtils.findComponentByName(popup,PIPE_MENU_START_NAME)).setEnabled(startButton);
		((JButton)SwingUtils.findComponentByName(toolbar,PIPE_MENU_START_NAME)).setEnabled(startButton);
		((JMenuItem)SwingUtils.findComponentByName(popup,PIPE_MENU_DEBUG_NAME)).setEnabled(startButton);
		((JButton)SwingUtils.findComponentByName(toolbar,PIPE_MENU_DEBUG_NAME)).setEnabled(startButton);
		((JMenuItem)SwingUtils.findComponentByName(popup,PIPE_MENU_STOP_NAME)).setEnabled(stopButton);
		((JButton)SwingUtils.findComponentByName(toolbar,PIPE_MENU_STOP_NAME)).setEnabled(stopButton);
	}

	
	public <T> void placePlugin(final PluginInterface<?> item, final T inst) {
		try{
			putPlugin(new ContainerPipeFrame<T>(uniquePipeItemId++, pipeManager, localizer, (FormManager<?,T>)inst, xmlModel));
		} catch (ContentException e) {
			logger.message(Severity.error,e,e.getLocalizedMessage());
		}
	}
	
	private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) {
		// TODO Auto-generated method stub
		
	}

	private void putPlugin(final PipePluginFrame<?> frame) throws ContentException {
		frame.addInternalFrameListener(new InternalFrameListener() {
			@Override public void internalFrameOpened(InternalFrameEvent e) {}
			@Override public void internalFrameDeactivated(InternalFrameEvent e) {}
			@Override public void internalFrameClosing(InternalFrameEvent e) {}
			@Override public void internalFrameActivated(InternalFrameEvent e) {}
			@Override public void internalFrameIconified(InternalFrameEvent e) {}
			@Override public void internalFrameDeiconified(InternalFrameEvent e) {}
			
			@Override 
			public void internalFrameClosed(InternalFrameEvent e) {
				pipeManager.removePipeComponent(frame);
				pluginCount.set(pluginCount.get()-1);
			}
		});
		frame.addComponentListener(new ComponentListener() {
			@Override 
			public void componentShown(ComponentEvent e) {
				pipeManager.refreshLinks();
			}
			
			@Override 
			public void componentHidden(ComponentEvent e) {
				pipeManager.refreshLinks();
			}
			
			@Override 
			public void componentResized(ComponentEvent e) {
				resizeDesktopPane();
				pipeManager.refreshLinks();
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				resizeDesktopPane();
				pipeManager.refreshLinks();
			}
		});
		frame.setVisible(true);
		pipeManager.addPipeComponent(frame);
		pluginCount.set(pluginCount.get()+1);
		try{frame.setSelected(true);
		} catch (PropertyVetoException e) {
			throw new ContentException(e);
		}
	}
	
	private void resizeDesktopPane() {
		int		x = pipeManager.getPreferredSize().width, y = pipeManager.getPreferredSize().height;
		int		xOld = x, yOld = y;
		
		for (JInternalFrame item : pipeManager.getAllFrames()) {
			final Point	pt = new Point(item.getWidth(),item.getHeight());
			
			SwingUtilities.convertPointToScreen(pt,item);
			SwingUtilities.convertPointFromScreen(pt,pipeManager);
			
			x = Math.max(x,pt.x);
			y = Math.max(y,pt.y);
		}
		
		if (x > xOld || y > yOld) {
			final Dimension	newSize = new Dimension(x > xOld ? x+30 : x, y > yOld ? y+30 : y);

			pipeManager.setPreferredSize(newSize);
			scroll.revalidate();
		}
	}

	private void serializeGlobal(final JsonStaxPrinter printer) throws IOException {
		final Dimension	prefSize = pipeManager.getPreferredSize();
		
		printer.splitter().name(JSON_PIPE_NAME).value(pipeName);
		printer.splitter().name(JSON_PIPE_DESCRIPTION).value(pipeDescription);
		printer.splitter().name(JSON_LAST_UNIQUE_ID).value(uniquePipeItemId);
		printer.splitter().name(JSON_GEOMETRY).startObject();
			printer.name(JSON_GEOMETRY_WIDTH).value(prefSize.width);
			printer.splitter().name(JSON_GEOMETRY_HEIGHT).value(prefSize.height);
		printer.endObject();
	}
}
