package chav1961.calc.windows;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Locale;

import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import chav1961.calc.interfaces.MetadataTarget;
import chav1961.calc.interfaces.TabContent;
import chav1961.calc.utils.SVGPluginFrame;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.model.interfaces.NodeMetadataOwner;
import chav1961.purelib.ui.swing.SwingModelUtils;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.interfaces.OnAction;
import chav1961.purelib.ui.swing.useful.DnDManager;
import chav1961.purelib.ui.swing.useful.DnDManager.DnDInterface;
import chav1961.purelib.ui.swing.useful.DnDManager.DnDMode;
import chav1961.purelib.ui.swing.useful.JCloseableTab;

@LocaleResourceLocation("i18n:prop:chav1961/calculator/i18n/i18n")
@LocaleResource(value = "chav1961.calc.pipe", tooltip = "chav1961.calc.pipe.tt", icon = "root:/WorkbenchTab!")
public class PipeTab extends JPanel implements AutoCloseable, LocaleChangeListener, TabContent, DnDInterface {
	private static final long 				serialVersionUID = 1L;
	private static final String				MODIFICATION_MARK = "*";

	private final JDesktopPane				pane = new JDesktopPane();
	private final DnDManager				dndManager;
	private final Localizer					localizer;
	private final LoggerFacade				logger;
	private final ContentMetadataInterface	ownModel, xmlModel;
	private final JCloseableTab				tab;
	private final JPopupMenu				popup;
	private final JToolBar					toolbar;

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
			this.dndManager = new DnDManager(pane,this);
			
			localizer.associateValue(this.getClass().getAnnotation(LocaleResource.class).value(),()->new Object[] {isModified ? MODIFICATION_MARK : "",pipeName});
			
			try(final InputStream	is = this.getClass().getResourceAsStream("pipe.xml")) {
				this.xmlModel = ContentModelFactory.forXmlDescription(is);
			} catch (EnvironmentException | IOException e) {
				throw new ContentException(e);
			}
			
			this.tab = new JCloseableTab(localizer,this.ownModel.getRoot());
			this.popup = SwingModelUtils.toMenuEntity(xmlModel.byUIPath(URI.create("ui:/model/navigation.top.pipeMenu")),JPopupMenu.class);
			SwingUtils.assignActionListeners(this.popup,this);
			this.toolbar = SwingModelUtils.toToolbar(xmlModel.byUIPath(URI.create("ui:/model/navigation.top.pipeMenu")),JToolBar.class);
			SwingUtils.assignActionListeners(this.toolbar,this);
			
			setLayout(new BorderLayout());
			add(toolbar,BorderLayout.NORTH);
			add(pane,BorderLayout.CENTER);
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
		frame.setVisible(true);
		pane.add(frame);
//			pluginCount.set(pluginCount.get()+1);
		try{frame.setSelected(true);
		} catch (PropertyVetoException e) {
			throw new ContentException(e);
		}
	}
	
	
	@OnAction("action:/pipeMenu.new")
	private void showPopup() {
		final Container	btn = SwingUtils.findComponentByName(popup,xmlModel.byUIPath(URI.create("ui:/model/navigation.top.pipeMenu/navigation.node.pipeMenu.new")).getName());
		
		getPopupMenu().show(btn,btn.getWidth()/2,btn.getHeight()/2);
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
		// TODO Auto-generated method stub
		
	}

	@OnAction("action:/newConditional")
	private void newConditional() {
		// TODO Auto-generated method stub
		
	}
	
	@OnAction("action:/newCalc")
	private void newCalc() {
		// TODO Auto-generated method stub
		
	}

	@OnAction("action:/newDialog")
	private void newDialog() {
		// TODO Auto-generated method stub
		
	}

	@OnAction("action:/newTerminal")
	private void newTerminal() {
		// TODO Auto-generated method stub
		
	}

	private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) {
		// TODO Auto-generated method stub
		
	}

}
