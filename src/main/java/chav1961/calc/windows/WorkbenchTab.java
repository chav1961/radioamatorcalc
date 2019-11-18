package chav1961.calc.windows;

import java.awt.BorderLayout;
import java.beans.PropertyVetoException;
import java.util.Locale;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.text.html.parser.ContentModel;

import chav1961.calc.interfaces.TabContent;
import chav1961.calc.utils.SVGPluginFrame;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.PreparationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.ui.interfaces.Action;
import chav1961.purelib.ui.swing.SwingModelUtils;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.interfaces.OnAction;
import chav1961.purelib.ui.swing.useful.JCloseableTab;

@LocaleResourceLocation("i18n:prop:chav1961/calculator/i18n/i18n")
@LocaleResource(value = "chav1961.calc.workbench", tooltip = "chav1961.calc.workbench.tt", icon = "root:/WorkbenchTab!")
@Action(resource=@LocaleResource(value="chav1961.calc.workbench.action.iconifyAll",tooltip="chav1961.calc.workbench.action.iconifyAll.tt"),actionString="iconifyAll")
@Action(resource=@LocaleResource(value="chav1961.calc.workbench.action.closeAll",tooltip="chav1961.calc.workbench.action.closeAll.tt"),actionString="removeAll")
public class WorkbenchTab extends JPanel implements AutoCloseable, LocaleChangeListener, TabContent {
	private static final long serialVersionUID = 1L;

	private final JDesktopPane				pane = new JDesktopPane();
	private final Localizer					localizer;
	private final LoggerFacade				logger;
	private final ContentMetadataInterface	model;
	private final ContentMetadataInterface	ownModel;
	private final JCloseableTab				tab;
	private final JPopupMenu				popup;
	
	@LocaleResource(value="chav1961.calc.workbench",tooltip="chav1961.calc.workbench.tt")
	private final boolean field = false;
	
	public WorkbenchTab(final Localizer localizer, final LoggerFacade logger, final ContentMetadataInterface model) throws SyntaxException, LocalizationException, ContentException {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else if (model == null) {
			throw new NullPointerException("Content model can't be null");
		}
		else {
			this.localizer = localizer;
			this.logger = logger;
			this.model = model;
			this.ownModel = ContentModelFactory.forAnnotatedClass(this.getClass());
			this.tab = new JCloseableTab(localizer,this.ownModel.getRoot());
			this.popup = SwingModelUtils.actionToMenuEntity(ownModel.getRoot(),JPopupMenu.class);
			
			setLayout(new BorderLayout());
			add(pane,BorderLayout.CENTER);
		}
	}

	@Override
	public void close() throws RuntimeException {
		removeAll();
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		SwingUtils.refreshLocale(this,oldLocale,newLocale);
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
		frame.setVisible(true);
		pane.add(frame);
		try{frame.setSelected(true);
		} catch (PropertyVetoException e) {
			throw new ContentException(e);
		}
	}
	
	@OnAction("action:/WorkbenchTab.iconifyAll")
	public void iconifyAll() {
		for (JInternalFrame item : pane.getAllFrames()) {
			if (item.isIconifiable()) {
				pane.getDesktopManager().iconifyFrame(item);
			}
		}
	}

	@OnAction("action:/WorkbenchTab.removeAll")
	public void removeAll() {
		for (JInternalFrame item : pane.getAllFrames()) {
			pane.getDesktopManager().closeFrame(item);
		}
	}
}
