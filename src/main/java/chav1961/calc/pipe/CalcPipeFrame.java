package chav1961.calc.pipe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.calc.utils.PipePluginFrame;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.growablearrays.GrowableCharArray;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.useful.JStateString;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.Application/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="chav1961.calc.pipe.calc.caption",tooltip="chav1961.calc.pipe.calc.caption.tt",help="help.aboutApplication")
@PluginProperties(width=600,height=250,pluginIconURI="calcFrameIcon.png",desktopIconURI="calcDesktopIcon.png")
public class CalcPipeFrame extends PipePluginFrame<Object> {
	private static final 					long serialVersionUID = 1L;
	
	private static final String				TABS_FIELDS_TITLE = "chav1961.calc.pipe.calc.tabs.fields";	
	private static final String				TABS_FIELDS_TITLE_TT = "chav1961.calc.pipe.calc.tabs.fields.tt";	
	private static final String				TABS_EXPRESSIONS_TITLE = "chav1961.calc.pipe.calc.tabs.expressions";	
	private static final String				TABS_EXPRESSIONS_TITLE_TT = "chav1961.calc.pipe.calc.tabs.expressions.tt";	
	private static final String				SOURCE_FIELDS_TITLE = "chav1961.calc.pipe.calc.fields.source"; 
	private static final String				SOURCE_FIELDS_TITLE_TT = "chav1961.calc.pipe.calc.fields.source.tt"; 
	private static final String				TARGET_FIELDS_TITLE = "chav1961.calc.pipe.calc.fields.target"; 
	private static final String				TARGET_FIELDS_TITLE_TT = "chav1961.calc.pipe.calc.fields.target.tt"; 
	
	private final ContentMetadataInterface	mdi;
	private final Localizer					localizer;
	private final JStateString				state;
	private final JControlTarget			targetControl;
	private final JControlSource			sourceControl;
	private final List<ContentNodeMetadata>	controls = new ArrayList<>();
	private final ModelItemListContainer	sourceFields; 
	private final TitledBorder				sourceFieldsTitle = new TitledBorder(new LineBorder(Color.BLACK)); 
	private final ModelItemListContainer	targetFields; 
	private final TitledBorder				targetFieldsTitle = new TitledBorder(new LineBorder(Color.BLACK));
	private final JTextArea					expression = new JTextArea();
	private final JTabbedPane				tabs = new JTabbedPane(); 
	@LocaleResource(value="chav1961.calc.plugins.calc.contour.inductanñe",tooltip="chav1961.calc.plugins.calc.contour.inductanñe.tt")
	@Format("9.2pz")
	public float temp = 0;
	
	public CalcPipeFrame(final Localizer parent, final ContentNodeMetadata inner, final ContentNodeMetadata outer) throws ContentException {
		super(inner);
		if (inner == null) {
			throw new NullPointerException("Initial metadata can't be null");
		}
		else {
			try{this.mdi = ContentModelFactory.forAnnotatedClass(this.getClass());
				this.localizer = LocalizerFactory.getLocalizer(mdi.getRoot().getLocalizerAssociated());
				this.state = new JStateString(localizer);
				this.targetControl = new JControlTarget(inner);
				this.sourceControl = new JControlSource(outer);
				this.sourceFields = new ModelItemListContainer();
				this.targetFields = new ModelItemListContainer();
				
				final JPanel	bottom = new JPanel(new BorderLayout());
				
				bottom.add(state,BorderLayout.CENTER);
				bottom.add(targetControl,BorderLayout.WEST);
				bottom.add(sourceControl,BorderLayout.EAST);
				
				final JScrollPane	pane1 = new JScrollPane(sourceFields); 
				final JScrollPane	pane2 = new JScrollPane(targetFields);
				final JPanel		centerPanel = new JPanel(new GridLayout(1,2));
				
				pane1.setBorder(sourceFieldsTitle);
				pane2.setBorder(targetFieldsTitle);
				centerPanel.add(pane1);
				centerPanel.add(pane2);
				
				tabs.addTab("",centerPanel);
				tabs.addTab("",new JScrollPane(expression));
				tabs.setSelectedIndex(0);
				
				add(tabs,BorderLayout.CENTER);
				
				add(bottom,BorderLayout.SOUTH);
				SwingUtils.assignActionKey(sourceFields,SwingUtils.KS_HELP,(e)->{showHelp(e.getActionCommand());},mdi.getRoot().getHelpId());
				SwingUtils.assignActionKey(targetFields,SwingUtils.KS_HELP,(e)->{showHelp(e.getActionCommand());},mdi.getRoot().getHelpId());
				fillLocalizedStrings(localizer.currentLocale().getLocale(),localizer.currentLocale().getLocale());
			} catch (LocalizationException e) {
				throw new ContentException(e);
			}
		}
	}

	public void addSourceField(final ContentNodeMetadata metadata) {
		sourceFields.placeContent(metadata);
	}

	public void addPargetField(final ContentNodeMetadata metadata) {
		targetFields.placeContent(metadata);
	}
	
	public ContentNodeMetadata[] getSourceFields() {
		return sourceFields.getContent();
	}

	public ContentNodeMetadata[] getTargetFields() {
		return targetFields.getContent();
	}
	
	public void addTargetControl(final ContentNodeMetadata control) {
		if (control == null) {
			throw new NullPointerException("Control to add can't be null");
		}
		else {
			controls.add(control);
		}
	}
	
	public void removeTargetControl(final ContentNodeMetadata control) {
		if (control == null) {
			throw new NullPointerException("Control to remove can't be null");
		}
		else {
			controls.remove(control);
		}
	}
	
	public ContentNodeMetadata[] getTaregtControls() {
		return controls.toArray(new ContentNodeMetadata[controls.size()]);
	}	
	
	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings(oldLocale,newLocale);
	}

	@Override
	public LoggerFacade getLogger() {
		return state;
	}

	protected void showHelp(final String helpId) {
		final GrowableCharArray	gca = new GrowableCharArray(false);
		
		try{gca.append(localizer.getContent(helpId));
			final byte[]	content = Base64.getEncoder().encode(new String(gca.extract()).getBytes());
			
			SwingUtils.showCreoleHelpWindow(this,URI.create("self:/#"+new String(content,0,content.length)));
		} catch (LocalizationException | NullPointerException | IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		setTitle(localizer.getValue(mdi.getRoot().getLabelId()));
		if (mdi.getRoot().getTooltipId() != null) {
			setToolTipText(localizer.getValue(mdi.getRoot().getTooltipId()));
		}
		sourceFieldsTitle.setTitle(localizer.getValue(SOURCE_FIELDS_TITLE));
		sourceFields.setToolTipText(localizer.getValue(SOURCE_FIELDS_TITLE_TT));
		targetFieldsTitle.setTitle(localizer.getValue(TARGET_FIELDS_TITLE));
		targetFields.setToolTipText(localizer.getValue(TARGET_FIELDS_TITLE_TT));

		tabs.setTitleAt(0,localizer.getValue(TABS_FIELDS_TITLE));
		tabs.setToolTipTextAt(0,localizer.getValue(TABS_FIELDS_TITLE_TT));
		tabs.setTitleAt(1,localizer.getValue(TABS_EXPRESSIONS_TITLE));
		tabs.setToolTipTextAt(1,localizer.getValue(TABS_EXPRESSIONS_TITLE_TT));
	}
}
