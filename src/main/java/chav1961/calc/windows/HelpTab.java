package chav1961.calc.windows;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import chav1961.calc.interfaces.TabContent;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.ui.swing.useful.JCloseableTab;
import chav1961.purelib.ui.swing.useful.JCreoleHelpWindow;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.Application/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value = "chav1961.calc.help", tooltip = "chav1961.calc.help.tt", icon = "root:/WorkbenchTab!")
public class HelpTab extends JPanel implements AutoCloseable, LocaleChangeListener, TabContent {
	private static final long serialVersionUID = 6906294131317135056L;

	private final Localizer			localizer;
	private final LoggerFacade		logger;
	private final ContentMetadataInterface	ownModel;
	private final JCloseableTab		tab;
	private final JCreoleHelpWindow	help;

	@LocaleResource(value="chav1961.calc.workbench",tooltip="chav1961.calc.workbench.tt")
	private final boolean field = false;
	
	public HelpTab(final JTabbedPane tabs, final Localizer localizer, final LoggerFacade logger, final String root) throws SyntaxException, LocalizationException, ContentException {
		super(new BorderLayout(5, 5));
		if (tabs == null) {
			throw new NullPointerException("Tabs can't be null");
		}
		else if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else if (Utils.checkEmptyOrNullString(root)) {
			throw new IllegalArgumentException("Root string can't be null or empty");
		}
		else {
			this.localizer = localizer;
			this.logger = logger;
			this.ownModel = ContentModelFactory.forAnnotatedClass(this.getClass());
			this.tab = new JCloseableTab(localizer,this.ownModel.getRoot());
			this.help = new JCreoleHelpWindow(localizer, root);
			add(new JScrollPane(this.help));
		}
	}
	
	@Override
	public JPopupMenu getPopupMenu() {
		return null;
	}

	@Override
	public JCloseableTab getTab() {
		return tab;
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings();
	}

	@Override
	public void close() throws Exception {
	}

	public void showHelp(final String helpUri) {
		if (Utils.checkEmptyOrNullString(helpUri)) {
			throw new IllegalArgumentException("Help URI string can't be null or empty");
		}
		else {
			try {
				help.loadContent(helpUri);
			} catch (LocalizationException | IOException e) {
				logger.message(Severity.error, e.getLocalizedMessage());
			}
		}		
	}
	
	private void fillLocalizedStrings() {
		// TODO Auto-generated method stub
		
	}
}
