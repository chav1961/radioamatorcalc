package chav1961.calc.pipe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
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

@LocaleResourceLocation("i18n:prop:chav1961/calculator/i18n/i18n")
@LocaleResource(value="chav1961.calc.pipe.terminal.caption",tooltip="chav1961.calc.pipe.terminal.caption.tt",help="help.aboutApplication")
@PluginProperties(width=500,height=200,pluginIconURI="terminalFrameIcon.png",desktopIconURI="terminalDesktopIcon.png")
public class TerminalPipeFrame extends PipePluginFrame<Object> {
	private static final 					long serialVersionUID = 1L;
	private static final String				FIELDS_TITLE = "chav1961.calc.pipe.terminal.fields"; 
	private static final String				FIELDS_TITLE_TT = "chav1961.calc.pipe.terminal.fields.tt"; 
	private static final String				MESSAGE_TITLE = "chav1961.calc.pipe.terminal.message"; 
	private static final String				MESSAGE_TITLE_TT = "chav1961.calc.pipe.terminal.message.tt"; 
	private static final String				FAILURE_TITLE = "chav1961.calc.pipe.terminal.failure"; 
	private static final String				FAILURE_TITLE_TT = "chav1961.calc.pipe.terminal.failure.tt"; 
	
	private final ContentMetadataInterface	mdi;
	private final Localizer					localizer;
	private final JStateString				state;
	private final JControlTarget			targetControl;
	private final List<ContentNodeMetadata>	controls = new ArrayList<>();
	private final ModelItemListContainer	fields; 
	private final TitledBorder				fieldsTitle = new TitledBorder(new LineBorder(Color.BLACK)); 
	private final JCheckBox					terminalFailure = new JCheckBox();
	private final JLabel					terminalLabel = new JLabel();
	private final JTextField				terminalMessage = new JTextField();
	@LocaleResource(value="chav1961.calc.plugins.calc.contour.inductanñe",tooltip="chav1961.calc.plugins.calc.contour.inductanñe.tt")
	@Format("9.2pz")
	public float temp = 0;
	
	public TerminalPipeFrame(final Localizer parent, final ContentNodeMetadata terminal) throws ContentException {
		super(terminal);
		if (terminal == null) {
			throw new NullPointerException("Initial metadata can't be null");
		}
		else {
			try{this.mdi = ContentModelFactory.forAnnotatedClass(this.getClass());
				this.localizer = LocalizerFactory.getLocalizer(mdi.getRoot().getLocalizerAssociated());
				this.state = new JStateString(localizer);
				this.targetControl = new JControlTarget(terminal);
				this.fields = new ModelItemListContainer();
				
				final JPanel	bottom = new JPanel(new BorderLayout());
				final JPanel	top = new JPanel(new BorderLayout(5,5));
				
				bottom.add(state,BorderLayout.CENTER);
				bottom.add(targetControl,BorderLayout.WEST);

				top.add(terminalLabel,BorderLayout.WEST);
				top.add(terminalMessage,BorderLayout.CENTER);
				top.add(terminalFailure,BorderLayout.EAST);
				
				final JScrollPane	pane = new JScrollPane(fields); 
				
				pane.setBorder(fieldsTitle);
				add(top,BorderLayout.NORTH);
				add(pane,BorderLayout.CENTER);
				add(bottom,BorderLayout.SOUTH);
				SwingUtils.assignActionKey(fields,SwingUtils.KS_HELP,(e)->{showHelp(e.getActionCommand());},mdi.getRoot().getHelpId());
				fillLocalizedStrings(localizer.currentLocale().getLocale(),localizer.currentLocale().getLocale());
			} catch (LocalizationException | IOException e) {
				throw new ContentException(e);
			}
		}
	}

	public void addTargetField(final ContentNodeMetadata metadata) {
		fields.placeContent(metadata);
	}
	
	public ContentNodeMetadata[] getTargetFields() {
		return fields.getContent();
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
		fieldsTitle.setTitle(localizer.getValue(FIELDS_TITLE));
		fields.setToolTipText(localizer.getValue(FIELDS_TITLE_TT));
		terminalLabel.setText(localizer.getValue(MESSAGE_TITLE));
		terminalMessage.setToolTipText(localizer.getValue(MESSAGE_TITLE_TT));
		terminalFailure.setText(localizer.getValue(FAILURE_TITLE));
		terminalFailure.setToolTipText(localizer.getValue(FAILURE_TITLE_TT));
	}
}
