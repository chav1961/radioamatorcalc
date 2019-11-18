package chav1961.calc.windows;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Locale;

import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import chav1961.calc.interfaces.TabContent;
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
import chav1961.purelib.ui.swing.SwingModelUtils;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.useful.JCloseableTab;

@LocaleResourceLocation("i18n:prop:chav1961/calculator/i18n/i18n")
@LocaleResource(value = "chav1961.calc.workbench", tooltip = "chav1961.calc.workbench.tt", icon = "root:/WorkbenchTab!")
public class PipeTab extends JPanel implements AutoCloseable, LocaleChangeListener, TabContent {
	private static final long serialVersionUID = 1L;

	private final JDesktopPane				pane = new JDesktopPane();
	private final Localizer					localizer;
	private final LoggerFacade				logger;
	private final ContentMetadataInterface	model;
	private final ContentMetadataInterface	ownModel, xmlModel;
	private final JCloseableTab				tab;
	private final JPopupMenu				popup;

	@LocaleResource(value="chav1961.calc.workbench",tooltip="chav1961.calc.workbench.tt")
	private final boolean field = false;
	
	public PipeTab(final Localizer localizer, final LoggerFacade logger, final ContentMetadataInterface model) throws SyntaxException, LocalizationException, ContentException {
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
			
			try(final InputStream	is = this.getClass().getResourceAsStream("pipe.xml")) {
				this.xmlModel = ContentModelFactory.forXmlDescription(is);
			} catch (EnvironmentException | IOException e) {
				throw new ContentException(e);
			}
			
			this.tab = new JCloseableTab(localizer,this.ownModel.getRoot());
			this.popup = SwingModelUtils.toMenuEntity(xmlModel.byUIPath(URI.create("ui:/model/navigation.top.mainmenu")),JPopupMenu.class);
			SwingUtils.assignActionListeners(this.popup,this);
			
			setLayout(new BorderLayout());
			add(pane,BorderLayout.CENTER);
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
	public void localeChanged(Locale oldLocale, Locale newLocale) throws LocalizationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
