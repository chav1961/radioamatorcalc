package chav1961.calc.windows;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Locale;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import chav1961.calc.LocalizationKeys;
import chav1961.calc.interfaces.TabContent;
import chav1961.calc.utils.SVGPluginFrame;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.subscribable.SubscribableInt;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.interfaces.OnAction;
import chav1961.purelib.ui.swing.useful.JCloseableTab;
import chav1961.purelib.ui.swing.useful.JExtendedScrollPane;
import chav1961.purelib.ui.swing.useful.JLocalizedOptionPane;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.Application/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value = "chav1961.calc.workbench", tooltip = "chav1961.calc.workbench.tt", icon = "root:/WorkbenchTab!")
public class WorkbenchTab extends JPanel implements AutoCloseable, LocaleChangeListener, TabContent {
	private static final long serialVersionUID = 1L;

	public final SubscribableInt			pluginCount = new SubscribableInt(); 
	public final SubscribableInt			iconifiedCount = new SubscribableInt(); 
	
	private final JScrollPane				scroll;
	private final JDesktopPane				pane = new JDesktopPane();
	private final Localizer					localizer;
	private final LoggerFacade				logger;
	private final ContentMetadataInterface	ownModel;
	private final ContentMetadataInterface	xmlModel;
	private final JCloseableTab				tab;
	private final JPopupMenu				popup;
	
	@LocaleResource(value="chav1961.calc.workbench",tooltip="chav1961.calc.workbench.tt")
	private final boolean field = false;
	
	public WorkbenchTab(final JTabbedPane tabs, final Localizer localizer, final LoggerFacade logger) throws SyntaxException, LocalizationException, ContentException {
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
			try(final InputStream	is = this.getClass().getResourceAsStream("pipe.xml")) {
				this.xmlModel = ContentModelFactory.forXmlDescription(is);
			} catch (IOException | EnvironmentException e) {
				throw new ContentException(e);
			}
			this.tab = new JCloseableTab(localizer,this.ownModel.getRoot());
			this.popup = SwingUtils.toJComponent(xmlModel.byUIPath(URI.create("ui:/model/navigation.top.workbenchMenu")),JPopupMenu.class);
			this.tab.associate(tabs,this,popup);

			pluginCount.addListener((oldValue,newValue)->{
				((JMenuItem)SwingUtils.findComponentByName(popup,"workbenchMenu.closeAll")).setEnabled(newValue != 0);
				iconifiedCount.refresh();
			});
			iconifiedCount.addListener((oldValue,newValue)->((JMenuItem)SwingUtils.findComponentByName(popup,"workbenchMenu.iconifyAll")).setEnabled(newValue < pluginCount.get()));
			
			setLayout(new BorderLayout());
			pane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
			scroll = new JExtendedScrollPane(pane,true); 
			add(scroll,BorderLayout.CENTER);
		}
	}

	@Override
	public void close() throws RuntimeException {
		removeAll();
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		for (Component item : SwingUtils.children(this)) {
			SwingUtils.refreshLocale(item,oldLocale,newLocale);
		}
		tab.localeChanged(oldLocale, newLocale);
	}

	@Override
	public JPopupMenu getPopupMenu() {
		return popup;
	}

	@Override
	public JCloseableTab getTab() {
		return tab;
	}
	
	public <T> void placePlugin(final SVGPluginFrame<T> frame) throws ContentException {
		frame.addInternalFrameListener(new InternalFrameListener() {
			@Override public void internalFrameOpened(InternalFrameEvent e) {}
			@Override public void internalFrameDeactivated(InternalFrameEvent e) {}
			@Override public void internalFrameClosing(InternalFrameEvent e) {
				
			}
			@Override public void internalFrameActivated(InternalFrameEvent e) {}
			
			@Override 
			public void internalFrameIconified(InternalFrameEvent e) {
				iconifiedCount.set(iconifiedCount.get()+1);
			}
			
			@Override
			public void internalFrameDeiconified(InternalFrameEvent e) {
				iconifiedCount.set(iconifiedCount.get()-1);
			}
			
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
				pluginCount.set(pluginCount.get()-1);
				if (frame.isIcon()) {
					iconifiedCount.set(iconifiedCount.get()-1);
				}
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
		pane.add(frame);
		pluginCount.set(pluginCount.get()+1);
		try{frame.setSelected(true);
		} catch (PropertyVetoException e) {
			throw new ContentException(e);
		}
	}
	
	@OnAction("action:/iconifyAll")
	public void iconifyAll() {
		for (JInternalFrame item : pane.getAllFrames()) {
			if (item.isIconifiable()) {
				pane.getDesktopManager().iconifyFrame(item);
			}
		}
		iconifiedCount.set(pluginCount.get());
	}

	@OnAction("action:/closeAll")
	public void closeAll() throws LocalizationException {
		if (new JLocalizedOptionPane(localizer).confirm(this, LocalizationKeys.CONFIRM_CLEAR_DESKTOP_MESSAGE, LocalizationKeys.CONFIRM_CLEAR_DESKTOP_CAPTION, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			for (JInternalFrame item : pane.getAllFrames()) {
				pane.getDesktopManager().closeFrame(item);
			}
			iconifiedCount.set(0);
			pluginCount.set(0);
		}
	}
	
	private void resizeDesktopPane() {
		int		x = pane.getPreferredSize().width, y = pane.getPreferredSize().height;
		int		xOld = x, yOld = y;
		
		for (JInternalFrame item : pane.getAllFrames()) {
			final Point	pt = new Point(item.getWidth(),item.getHeight());
			
			SwingUtilities.convertPointToScreen(pt,item);
			SwingUtilities.convertPointFromScreen(pt,pane);
			
			x = Math.max(x,pt.x);
			y = Math.max(y,pt.y);
		}
		
		if (x > xOld || y > yOld) {
			final Dimension	newSize = new Dimension(x > xOld ? x+30 : x, y > yOld ? y+30 : y);

			pane.setPreferredSize(newSize);
			scroll.revalidate();
		}
	}
}
