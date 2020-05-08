package chav1961.calc.windows;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

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
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.swing.AutoBuiltForm;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.interfaces.OnAction;
import chav1961.purelib.ui.swing.useful.JCloseableTab;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.Application/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value = "chav1961.calc.reference", tooltip = "chav1961.calc.reference.tt", icon = "root:/WorkbenchTab!")
public class ReferenceTab<T> extends JPanel implements AutoCloseable, LocaleChangeListener, TabContent {
	private static final long serialVersionUID = 441497501005942523L;

	private static final String				REFERENCE_FIND = "chav1961.calc.reference.seek";	
	private static final URI				REFERENCE_MENU_ROOT = URI.create("ui:/model/navigation.top.referenceMenu");	
	
	private final Localizer 				localizer;
	private final URI 						localizerURI;
	private final LoggerFacade 				logger;
	private final ContentMetadataInterface	ownModel;
	private final JCloseableTab 			tab;
	private final ContentMetadataInterface	xmlModel;
	private final JPopupMenu 				popup;
	private final JToolBar 					toolbar;
	private final AutoBuiltForm<T>			findForm;
	private final TitledBorder				border = new TitledBorder("????");
	
	@LocaleResource(value="chav1961.calc.reference.name",tooltip="chav1961.calc.reference.name.tt")
	@Format("50r")
	private String							referenceName = "<new>";
	
	@LocaleResource(value="chav1961.calc.reference.description",tooltip="chav1961.calc.reference.descriptiontt")
	@Format("50r")
	private String 							referenceDescription = "";
	
	public ReferenceTab(final JTabbedPane tabs, final Localizer localizer, final LoggerFacade logger, final String referenceId, final JTable innerComponent, final AutoBuiltForm<T> findForm) throws SyntaxException, LocalizationException, ContentException {
		if (tabs == null) {
			throw new NullPointerException("Tabbed pane can't be null");
		}
		else if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else if (referenceId == null || referenceId.isEmpty()) {
			throw new IllegalArgumentException("Reference Id can't be null or empty");
		}
		else if (!localizer.containsKey(referenceId)) {
			throw new IllegalArgumentException("Reference Id ["+referenceId+"] not found in the ["+localizer.getLocalizerId()+"] localizer");
		}
		else if (innerComponent == null) {
			throw new NullPointerException("Inner component can't be null");
		}
		else if (findForm == null) {
			throw new NullPointerException("Find form can't be null");
		}
		else {
			this.localizer = localizer;
			this.localizerURI = URI.create(this.getClass().getAnnotation(LocaleResourceLocation.class).value());
			this.logger = logger;
			this.ownModel = ContentModelFactory.forAnnotatedClass(this.getClass());
			this.findForm = findForm;
			
			localizer.associateValue(this.getClass().getAnnotation(LocaleResource.class).value(),()->new Object[] {
					localizer.getValue(referenceId)});
			
			try(final InputStream	is = this.getClass().getResourceAsStream("reference.xml")) {
				
				this.xmlModel = ContentModelFactory.forXmlDescription(is);
			} catch (EnvironmentException | IOException e) {
				throw new ContentException(e);
			}
			
			this.tab = new JCloseableTab(localizer,this.ownModel.getRoot());
			this.popup = SwingUtils.toJComponent(xmlModel.byUIPath(REFERENCE_MENU_ROOT),JPopupMenu.class);
			this.toolbar = SwingUtils.toJComponent(xmlModel.byUIPath(REFERENCE_MENU_ROOT),JToolBar.class);
			SwingUtils.assignActionListeners(this.toolbar,this);
			
			final JPanel		leftPanel = new JPanel(new BorderLayout());
			final JScrollPane	scroll = new JScrollPane();
			final JTable		leftBar = new JTable(innerComponent.getModel());

			innerComponent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			innerComponent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			scroll.setRowHeaderView(leftBar);
			scroll.setViewportView(innerComponent);
			
			findForm.setBorder(border);
			leftPanel.add(findForm,BorderLayout.NORTH);
			leftPanel.add(new JLabel("assa"),BorderLayout.CENTER);
			
			setLayout(new BorderLayout());
			add(toolbar,BorderLayout.NORTH);
			add(scroll,BorderLayout.CENTER);
			add(leftPanel,BorderLayout.WEST);
			
			
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
		for (Component item : SwingUtils.children(this)) {
			SwingUtils.refreshLocale(item,oldLocale,newLocale);
		}
		tab.localeChanged(oldLocale, newLocale);
		fillLocalizedStrings(oldLocale,newLocale);
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@OnAction("action:/test")
	private void test() {
		
	}

	private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) throws LocalizationException, IllegalArgumentException {
		border.setTitle(localizer.getValue(REFERENCE_FIND));
	}
}
