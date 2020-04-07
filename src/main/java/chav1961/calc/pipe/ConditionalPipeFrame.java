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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.calc.utils.PipePluginFrame;
import chav1961.calc.windows.PipeManager;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.growablearrays.GrowableCharArray;
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
@LocaleResource(value="chav1961.calc.pipe.conditional.caption",tooltip="chav1961.calc.pipe.conditional.caption.tt",help="help.aboutApplication")
@PluginProperties(width=500,height=200,pluginIconURI="conditionalFrameIcon.png",desktopIconURI="conditionalDesktopIcon.png")
public class ConditionalPipeFrame extends PipePluginFrame<ConditionalPipeFrame> {
	private static final 					long serialVersionUID = 1L;
	private static final String				FIELDS_TITLE = "chav1961.calc.pipe.conditional.fields"; 
	private static final String				FIELDS_TITLE_TT = "chav1961.calc.pipe.conditional.fields.tt"; 
	private static final String				EXPRESSION_TITLE = "chav1961.calc.pipe.conditional.expression"; 
	private static final String				EXPRESSION_TITLE_TT = "chav1961.calc.pipe.conditional.expression.tt"; 
	
	private final ContentMetadataInterface	mdi;
	private final Localizer					localizer;
	private final JStateString				state;
	private final JControlTarget			targetControl;
	private final JControlTrue				onTrueControl;
	private final JControlFalse				onFalseControl;
	private final List<ContentNodeMetadata>	controls = new ArrayList<>();
	private final ModelItemListContainer	fields; 
	private final TitledBorder				fieldsTitle = new TitledBorder(new LineBorder(Color.BLACK)); 
	private final JLabel					conditionLabel = new JLabel();
	private final JTextField				condition = new JTextField();
	@LocaleResource(value="chav1961.calc.plugins.calc.contour.inductanñe",tooltip="chav1961.calc.plugins.calc.contour.inductanñe.tt")
	@Format("9.2pz")
	public float temp = 0;
	
	public ConditionalPipeFrame(final PipeManager parent,final Localizer localizer, final ContentNodeMetadata inner, final ContentNodeMetadata onTrue, final ContentNodeMetadata onFalse) throws ContentException {
		super(parent,localizer,ConditionalPipeFrame.class,PipeItemType.CONDITIONAL_ITEM);
		if (inner == null) {
			throw new NullPointerException("Initial metadata can't be null");
		}
		else {
			try{this.mdi = ContentModelFactory.forAnnotatedClass(this.getClass());
				this.localizer = LocalizerFactory.getLocalizer(mdi.getRoot().getLocalizerAssociated());
				this.state = new JStateString(localizer);
				this.targetControl = new JControlTarget(inner);
				this.onTrueControl = new JControlTrue(onTrue);
				this.onFalseControl = new JControlFalse(onFalse);
				this.fields = new ModelItemListContainer(localizer,true);
				
				final JPanel		bottom = new JPanel(new BorderLayout());
				final JPanel		rightBottom = new JPanel(new GridLayout(1,2));
				final JPanel		top = new JPanel(new BorderLayout(5,5));
				
				rightBottom.add(onFalseControl);
				rightBottom.add(onTrueControl);
				
				bottom.add(state,BorderLayout.CENTER);
				bottom.add(targetControl,BorderLayout.WEST);
				bottom.add(rightBottom,BorderLayout.EAST);
				
				top.add(conditionLabel,BorderLayout.WEST);
				top.add(condition,BorderLayout.CENTER);
				
				final JScrollPane	pane = new JScrollPane(fields);  
				
				pane.setBorder(fieldsTitle);
				add(top,BorderLayout.NORTH);
				add(pane,BorderLayout.CENTER);
				add(bottom,BorderLayout.SOUTH);
				SwingUtils.assignActionKey(fields,SwingUtils.KS_HELP,(e)->{showHelp(e.getActionCommand());},mdi.getRoot().getHelpId());
				fillLocalizedStrings(localizer.currentLocale().getLocale(),localizer.currentLocale().getLocale());
			} catch (LocalizationException e) {
				throw new ContentException(e);
			}
		}
	}

	public void addTargetField(final ContentNodeMetadata metadata) {
		fields.addContent(metadata);
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

	protected void showHelp(final String helpId) {
		final GrowableCharArray<?>	gca = new GrowableCharArray<>(false);
		
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
		conditionLabel.setText(localizer.getValue(EXPRESSION_TITLE));
		condition.setToolTipText(localizer.getValue(EXPRESSION_TITLE_TT));
	}
}
