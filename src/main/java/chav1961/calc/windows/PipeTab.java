package chav1961.calc.windows;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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
import chav1961.calc.interfaces.PipeContainerItemInterface;
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
import chav1961.calc.utils.PipeLink;
import chav1961.calc.utils.PipeLink.PipeLinkType;
import chav1961.calc.utils.PipePluginFrame;
import chav1961.calc.utils.SVGPluginFrame;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
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
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.interfaces.OnAction;
import chav1961.purelib.ui.swing.useful.DnDManager;
import chav1961.purelib.ui.swing.useful.DnDManager.DnDInterface;
import chav1961.purelib.ui.swing.useful.DnDManager.DnDMode;
import chav1961.purelib.ui.swing.useful.JCloseableTab;
import chav1961.purelib.ui.swing.useful.JExtendedScrollPane;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.Application/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value = "chav1961.calc.pipe", tooltip = "chav1961.calc.pipe.tt", icon = "root:/WorkbenchTab!")
public class PipeTab extends JPanel implements AutoCloseable, LocaleChangeListener, TabContent, DnDInterface {
	private static final long 				serialVersionUID = 1L;
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

	private boolean							isModified = false;			
	private String							pipeName = "<new>";
	private int								uniquePipeItemId = 1;

	@LocaleResource(value="chav1961.calc.pipe",tooltip="chav1961.calc.pipe.tt")
	private final boolean field = false;
	
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
	}

	@OnAction("action:/newInitial")
	private void newInitial() {
		final ContentNodeMetadata	initial = new MutableContentNodeMetadata("initial",InitialPipeFrame.class,"./initial",localizerURI,PIPE_SOURCE_TT, PIPE_SOURCE_TT, null, null, URI.create("app:action:/start"),null); 
		
		try{
			putPlugin(new InitialPipeFrame(uniquePipeItemId++, pipeManager, localizer, initial, xmlModel));
		} catch (ContentException e) {
			logger.message(Severity.error,e,e.getLocalizedMessage());
		}
	}

	@OnAction("action:/newConditional")
	private void newConditional() {
		final ContentNodeMetadata	inner = new MutableContentNodeMetadata("inner",ConditionalPipeFrame.class,"./inner",localizerURI,PIPE_TARGET_TT, PIPE_TARGET_TT, null, null, URI.create("app:action:/inner"),null); 
		final ContentNodeMetadata	onTrue = new MutableContentNodeMetadata("onTrue",ConditionalPipeFrame.class,"./ontrue",localizerURI,PIPE_TRUE_TT, PIPE_TRUE_TT, null, null, URI.create("app:action:/ontrue"),null); 
		final ContentNodeMetadata	onFalse = new MutableContentNodeMetadata("onFalse",ConditionalPipeFrame.class,"./onfalse",localizerURI,PIPE_FALSE_TT, PIPE_FALSE_TT, null, null, URI.create("app:action:/onfalse"),null); 
		
		try{
			putPlugin(new ConditionalPipeFrame(uniquePipeItemId++, pipeManager, localizer, inner, onTrue, onFalse, xmlModel));
		} catch (ContentException e) {
			logger.message(Severity.error,e,e.getLocalizedMessage());
		}
	}
	
	@OnAction("action:/newCalc")
	private void newCalc() {
		final ContentNodeMetadata	inner = new MutableContentNodeMetadata("inner",CalcPipeFrame.class,"./inner",localizerURI,PIPE_TARGET_TT, PIPE_TARGET_TT, null, null, URI.create("app:action:/inner"),null); 
		final ContentNodeMetadata	outer = new MutableContentNodeMetadata("outer",CalcPipeFrame.class,"./outer",localizerURI,PIPE_SOURCE_TT, PIPE_SOURCE_TT, null, null, URI.create("app:action:/outer"),null); 
		
		try{
			putPlugin(new CalcPipeFrame(uniquePipeItemId++, pipeManager, localizer, inner, outer, xmlModel));
		} catch (ContentException e) {
			logger.message(Severity.error,e,e.getLocalizedMessage());
		}
	}

	@OnAction("action:/newDialog")
	private void newDialog() {
		final ContentNodeMetadata	inner = new MutableContentNodeMetadata("inner",DialogPipeFrame.class,"./inner",localizerURI,PIPE_TARGET_TT, PIPE_TARGET_TT, null, null, URI.create("app:action:/inner"),null); 
		final ContentNodeMetadata	onTrue = new MutableContentNodeMetadata("onTrue",ConditionalPipeFrame.class,"./ontrue",localizerURI,PIPE_OK_TT, PIPE_OK_TT, null, null, URI.create("app:action:/onok"),null); 
		final ContentNodeMetadata	onFalse = new MutableContentNodeMetadata("onFalse",ConditionalPipeFrame.class,"./onfalse",localizerURI,PIPE_CANCEL_TT, PIPE_CANCEL_TT, null, null, URI.create("app:action:/oncancel"),null); 
		
		try{
			putPlugin(new DialogPipeFrame(uniquePipeItemId++, pipeManager, localizer, inner, onTrue, onFalse, xmlModel));
		} catch (ContentException e) {
			logger.message(Severity.error,e,e.getLocalizedMessage());
		}
	}

	@OnAction("action:/newTerminal")
	private void newTerminal() {
		final ContentNodeMetadata	terminal = new MutableContentNodeMetadata("terminal",TerminalPipeFrame.class,"./terminal",localizerURI,PIPE_TARGET_TT, PIPE_TARGET_TT, null, null, URI.create("app:action:/stop"),null); 
		
		try{
			putPlugin(new TerminalPipeFrame(uniquePipeItemId++, pipeManager, localizer, terminal, xmlModel));
		} catch (ContentException e) {
			logger.message(Severity.error,e,e.getLocalizedMessage());
		}
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
//		int		x = pane.getPreferredSize().width, y = pane.getPreferredSize().height;
		int		xOld = x, yOld = y;
		
		for (JInternalFrame item : pipeManager.getAllFrames()) {
//		for (JInternalFrame item : pane.getAllFrames()) {
			final Point	pt = new Point(item.getWidth(),item.getHeight());
			
			SwingUtilities.convertPointToScreen(pt,item);
			SwingUtilities.convertPointFromScreen(pt,pipeManager);
//			SwingUtilities.convertPointFromScreen(pt,pane);
			
			x = Math.max(x,pt.x);
			y = Math.max(y,pt.y);
		}
		
		if (x > xOld || y > yOld) {
			final Dimension	newSize = new Dimension(x > xOld ? x+30 : x, y > yOld ? y+30 : y);

			pipeManager.setPreferredSize(newSize);
//			pane.setPreferredSize(newSize);
			scroll.revalidate();
		}
	}
}
