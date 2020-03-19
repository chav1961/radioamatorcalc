package chav1961.calc.windows;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Locale;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import chav1961.calc.interfaces.MetadataTarget;
import chav1961.calc.interfaces.TabContent;
import chav1961.calc.pipe.CalcPipeFrame;
import chav1961.calc.pipe.ConditionalPipeFrame;
import chav1961.calc.pipe.DialogPipeFrame;
import chav1961.calc.pipe.InitialPipeFrame;
import chav1961.calc.pipe.TerminalPipeFrame;
import chav1961.calc.utils.PipePluginFrame;
import chav1961.calc.utils.SVGPluginFrame;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.MutableContentNodeMetadata;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.model.interfaces.NodeMetadataOwner;
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
	
	private final JScrollPane				scroll;
//	private final JDesktopPane				pane = new JDesktopPane();
	private final DnDManager				dndManager;
	private final PipeManager				pipeManager;
	private final Localizer					localizer;
	private final LoggerFacade				logger;
	private final ContentMetadataInterface	ownModel, xmlModel;
	private final JCloseableTab				tab;
	private final JPopupMenu				popup;
	private final JToolBar					toolbar;

	private boolean							pressed = false;
	private Point							pressedPoint;
	private boolean							isModified = false;			
	private String							pipeName = "<new>";

	@LocaleResource(value="chav1961.calc.pipe",tooltip="chav1961.calc.pipe.tt")
	private final boolean field = false;
	
	public PipeTab(final Localizer localizer, final LoggerFacade logger) throws SyntaxException, LocalizationException, ContentException {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.localizer = localizer;
			this.logger = logger;
			this.ownModel = ContentModelFactory.forAnnotatedClass(this.getClass());
			try{
				this.pipeManager = new PipeManager(localizer,logger,ownModel);
			} catch (EnvironmentException | IOException e) {
				throw new ContentException(e);
			}
//			this.dndManager = new DnDManager(pane,this);
			this.dndManager = new DnDManager(pipeManager,this);
			
			localizer.associateValue(this.getClass().getAnnotation(LocaleResource.class).value(),()->new Object[] {isModified ? MODIFICATION_MARK : "",pipeName});
			
			try(final InputStream	is = this.getClass().getResourceAsStream("pipe.xml")) {
				this.xmlModel = ContentModelFactory.forXmlDescription(is);
			} catch (EnvironmentException | IOException e) {
				throw new ContentException(e);
			}
			
			this.tab = new JCloseableTab(localizer,this.ownModel.getRoot());
			this.popup = SwingUtils.toJComponent(xmlModel.byUIPath(URI.create("ui:/model/navigation.top.pipeMenu")),JPopupMenu.class);
			SwingUtils.assignActionListeners(this.popup,this);
			this.toolbar = SwingUtils.toJComponent(xmlModel.byUIPath(URI.create("ui:/model/navigation.top.pipeMenu")),JToolBar.class);
			SwingUtils.assignActionListeners(this.toolbar,this);
			
			setLayout(new BorderLayout());
			add(toolbar,BorderLayout.NORTH);
			
			scroll = new JExtendedScrollPane(pipeManager,true);
//			scroll = new JExtendedScrollPane(pane,true);
			pipeManager.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
//			pane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
			add(scroll,BorderLayout.CENTER);
			
			fillLocalizedStrings(localizer.currentLocale().getLocale(),localizer.currentLocale().getLocale());
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
			return ((NodeMetadataOwner)from).getNodeMetadata();
		}
		else {
			return null;
		}
	}

	@Override
	public boolean canReceive(final DnDMode currentMode, final Component from, final int xFrom, final int yFrom, final Component to, final int xTo, final int yTo, final Class<?> contentClass) {
		return ContentNodeMetadata.class.isAssignableFrom(contentClass) && (from instanceof NodeMetadataOwner) && (to instanceof MetadataTarget); 
	}

	@Override
	public void track(final DnDMode currentMode, final Component from, final int xFromAbsolute, final int yFromAbsolute, final Component to, final int xToAbsolute, final int yToAbsolute) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void complete(final DnDMode currentMode, final Component from, final int xFrom, final int yFrom, final Component to, final int xTo, final int yTo, final Object content) {
		// TODO Auto-generated method stub
		
	}

	public void putPlugin(final Object plugin) throws ContentException {
		final SVGPluginFrame	frame = new SVGPluginFrame(localizer, plugin);
	        
		frame.setVisible(true);
		frame.addInternalFrameListener(new InternalFrameListener() {
				@Override public void internalFrameOpened(InternalFrameEvent e) {}
				@Override public void internalFrameDeactivated(InternalFrameEvent e) {}
				@Override public void internalFrameClosing(InternalFrameEvent e) {
					
				}
				@Override public void internalFrameActivated(InternalFrameEvent e) {}
				
				@Override 
				public void internalFrameIconified(InternalFrameEvent e) {
//					iconifiedCount.set(iconifiedCount.get()+1);
				}
				
				@Override
				public void internalFrameDeiconified(InternalFrameEvent e) {
	//				iconifiedCount.set(iconifiedCount.get()-1);
				}
				
				@Override
				public void internalFrameClosed(InternalFrameEvent e) {
//					pluginCount.set(pluginCount.get()-1);
//					if (frame.isIcon()) {
//						iconifiedCount.set(iconifiedCount.get()-1);
//					}
				}
		});
		frame.addComponentListener(new ComponentListener() {
			@Override public void componentShown(ComponentEvent e) {}
			@Override public void componentHidden(ComponentEvent e) {}
			
			@Override 
			public void componentResized(ComponentEvent e) {
				resizeDesktopPane();
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				resizeDesktopPane();
			}
		});
		frame.setVisible(true);
		pipeManager.add(frame);
//		pane.add(frame);
//			pluginCount.set(pluginCount.get()+1);
		try{frame.setSelected(true);
		} catch (PropertyVetoException e) {
			throw new ContentException(e);
		}
	}
	
	
	@OnAction("action:/pipeMenu.new")
	private void showPopup() {
		final Container	btn = SwingUtils.findComponentByName(popup,xmlModel.byUIPath(URI.create("ui:/model/navigation.top.pipeMenu/navigation.node.pipeMenu.new")).getName());
		
		try{
			getPopupMenu().show(btn,btn.getWidth()/2,btn.getHeight()/2);
		} catch (java.awt.IllegalComponentStateException e) {
		}
	}

	@OnAction("action:/cleanPipe")
	private void cleanPipe() {
		// TODO Auto-generated method stub
		
	}
	
	@OnAction("action:/validatePipe")
	private void validatePipe() {
		// TODO Auto-generated method stub
		
	}
	
	@OnAction("action:/newInitial")
	private void newInitial() {
		final ContentNodeMetadata	initial = new MutableContentNodeMetadata("initial",Object.class,"./initial",URI.create(localizer.getLocalizerId()),PIPE_SOURCE_TT, PIPE_SOURCE_TT, null, null, URI.create("app:action:/start"),null); 
		
		try{
			putPlugin(new InitialPipeFrame(localizer, initial));
		} catch (ContentException e) {
			logger.message(Severity.error,e,e.getLocalizedMessage());
		}
	}

	@OnAction("action:/newConditional")
	private void newConditional() {
		final ContentNodeMetadata	inner = new MutableContentNodeMetadata("inner",Object.class,"./inner",URI.create(localizer.getLocalizerId()),PIPE_TARGET_TT, PIPE_TARGET_TT, null, null, URI.create("app:action:/inner"),null); 
		final ContentNodeMetadata	onTrue = new MutableContentNodeMetadata("onTrue",Object.class,"./ontrue",URI.create(localizer.getLocalizerId()),PIPE_TRUE_TT, PIPE_TRUE_TT, null, null, URI.create("app:action:/ontrue"),null); 
		final ContentNodeMetadata	onFalse = new MutableContentNodeMetadata("onFalse",Object.class,"./onfalse",URI.create(localizer.getLocalizerId()),PIPE_FALSE_TT, PIPE_FALSE_TT, null, null, URI.create("app:action:/onfalse"),null); 
		
		try{
			putPlugin(new ConditionalPipeFrame(localizer, inner, onTrue, onFalse));
		} catch (ContentException e) {
			logger.message(Severity.error,e,e.getLocalizedMessage());
		}
	}
	
	@OnAction("action:/newCalc")
	private void newCalc() {
		final ContentNodeMetadata	inner = new MutableContentNodeMetadata("inner",Object.class,"./inner",URI.create(localizer.getLocalizerId()),PIPE_TARGET_TT, PIPE_TARGET_TT, null, null, URI.create("app:action:/inner"),null); 
		final ContentNodeMetadata	outer = new MutableContentNodeMetadata("outer",Object.class,"./outer",URI.create(localizer.getLocalizerId()),PIPE_SOURCE_TT, PIPE_SOURCE_TT, null, null, URI.create("app:action:/outer"),null); 
		
		try{
			putPlugin(new CalcPipeFrame(localizer, inner, outer));
		} catch (ContentException e) {
			logger.message(Severity.error,e,e.getLocalizedMessage());
		}
	}

	@OnAction("action:/newDialog")
	private void newDialog() {
		final ContentNodeMetadata	inner = new MutableContentNodeMetadata("inner",Object.class,"./inner",URI.create(localizer.getLocalizerId()),PIPE_TARGET_TT, PIPE_TARGET_TT, null, null, URI.create("app:action:/inner"),null); 
		final ContentNodeMetadata	outer = new MutableContentNodeMetadata("outer",Object.class,"./outer",URI.create(localizer.getLocalizerId()),PIPE_SOURCE_TT, PIPE_SOURCE_TT, null, null, URI.create("app:action:/outer"),null); 
		
		try{
			putPlugin(new DialogPipeFrame(localizer, inner, outer));
		} catch (ContentException e) {
			logger.message(Severity.error,e,e.getLocalizedMessage());
		}
	}

	@OnAction("action:/newTerminal")
	private void newTerminal() {
		final ContentNodeMetadata	terminal = new MutableContentNodeMetadata("terminal",Object.class,"./terminal",URI.create(localizer.getLocalizerId()),PIPE_TARGET_TT, PIPE_TARGET_TT, null, null, URI.create("app:action:/stop"),null); 
		
		try{
			putPlugin(new TerminalPipeFrame(localizer, terminal));
		} catch (ContentException e) {
			logger.message(Severity.error,e,e.getLocalizedMessage());
		}
	}

	private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) {
		// TODO Auto-generated method stub
		
	}

	private void putPlugin(final PipePluginFrame<?> frame) throws ContentException {
		frame.setVisible(true);
		frame.addInternalFrameListener(new InternalFrameListener() {
				@Override public void internalFrameOpened(InternalFrameEvent e) {}
				@Override public void internalFrameDeactivated(InternalFrameEvent e) {}
				@Override public void internalFrameClosing(InternalFrameEvent e) {
					
				}
				@Override public void internalFrameActivated(InternalFrameEvent e) {}
				
				@Override 
				public void internalFrameIconified(InternalFrameEvent e) {
				}
				
				@Override
				public void internalFrameDeiconified(InternalFrameEvent e) {
				}
				
				@Override
				public void internalFrameClosed(InternalFrameEvent e) {
//					pluginCount.set(pluginCount.get()-1);
				}
		});
		frame.addComponentListener(new ComponentListener() {
			@Override public void componentShown(ComponentEvent e) {}
			@Override public void componentHidden(ComponentEvent e) {}
			
			@Override 
			public void componentResized(ComponentEvent e) {
				resizeDesktopPane();
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				resizeDesktopPane();
			}
		});
		frame.setVisible(true);
		pipeManager.add(frame);
//		pane.add(frame);
//			pluginCount.set(pluginCount.get()+1);
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
