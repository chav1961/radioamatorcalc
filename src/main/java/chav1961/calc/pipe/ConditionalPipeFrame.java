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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.calc.pipe.ModelItemListContainer.DropAction;
import chav1961.calc.utils.PipeLink;
import chav1961.calc.utils.PipePluginFrame;
import chav1961.calc.windows.PipeManager;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.PrintingException;
import chav1961.purelib.basic.growablearrays.GrowableCharArray;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.streams.JsonStaxPrinter;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.interfaces.OnAction;
import chav1961.purelib.ui.swing.useful.JLocalizedOptionPane;
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

	private static final String				FIELDS_REMOVE_TITLE = "chav1961.calc.pipe.conditional.fields.remove.caption"; 
//	private static final String				FIELDS_REMOVE_TITLE_TT = "chav1961.calc.pipe.conditional.fields.remove.caption.tt";
	private static final String				FIELDS_REMOVE_QUESTION = "chav1961.calc.pipe.conditional.fields.remove.question"; 

	private static final String				VALIDATION_MISSING_FIELD = "chav1961.calc.pipe.conditional.validation.missingfield"; 
	
	private static final URI				PIPE_MENU_ROOT = URI.create("ui:/model/navigation.top.conditional.toolbar");	
	private static final String				PIPE_MENU_REMOVE_FIELD = "chav1961.calc.pipe.conditional.toolbar.removefield";	

	private static final String				JSON_PIPE_ITEM_EXPRESSION = "expression";
	
	private final ContentMetadataInterface	mdi;
	private final Localizer					localizer;
	private final JStateString				state;
	private final JControlTarget			targetControl;
	private final JControlTrue				onTrueControl;
	private final JControlFalse				onFalseControl;
	private final List<PipeLink>			links = new ArrayList<>();
	private final List<PipeLink>			controls = new ArrayList<>();
	private final ModelItemListContainer	fields; 
	private final JToolBar					toolbar;
	private final TitledBorder				fieldsTitle = new TitledBorder(new LineBorder(Color.BLACK)); 
	private final JLabel					conditionLabel = new JLabel();
	private final JTextField				expression = new JTextField();
	@LocaleResource(value="chav1961.calc.pipe.conditional.caption",tooltip="chav1961.calc.pipe.conditional.caption.tt")
	@Format("9.2pz")
	public float temp = 0;
	
	public ConditionalPipeFrame(final int uniqueId, final PipeManager parent,final Localizer localizer, final ContentNodeMetadata inner, final ContentNodeMetadata onTrue, final ContentNodeMetadata onFalse, final ContentMetadataInterface general) throws ContentException {
		super(uniqueId,parent,localizer,ConditionalPipeFrame.class,PipeItemType.CONDITIONAL_ITEM);
		if (inner == null) {
			throw new NullPointerException("Initial metadata can't be null");
		}
		else {
			try{this.mdi = ContentModelFactory.forAnnotatedClass(this.getClass());
				this.localizer = LocalizerFactory.getLocalizer(mdi.getRoot().getLocalizerAssociated());
				this.state = new JStateString(localizer);
				this.targetControl = new JControlTarget(inner,this);
				this.onTrueControl = new JControlTrue(onTrue,this);
				this.onFalseControl = new JControlFalse(onFalse,this);
				this.fields = new ModelItemListContainer(localizer,this,DropAction.INSERT);
				this.toolbar = SwingUtils.toJComponent(general.byUIPath(PIPE_MENU_ROOT),JToolBar.class);
				this.toolbar.setOrientation(JToolBar.VERTICAL);
				this.toolbar.setFloatable(false);
				SwingUtils.assignActionListeners(this.toolbar,this);
				
				final JPanel		bottom = new JPanel(new BorderLayout());
				final JPanel		rightBottom = new JPanel(new GridLayout(1,2));
				final JPanel		top = new JPanel(new BorderLayout(5,5));
				
				rightBottom.add(onFalseControl);
				rightBottom.add(onTrueControl);
				
				bottom.add(state,BorderLayout.CENTER);
				bottom.add(targetControl,BorderLayout.WEST);
				bottom.add(rightBottom,BorderLayout.EAST);
				
				top.add(conditionLabel,BorderLayout.WEST);
				top.add(expression,BorderLayout.CENTER);
				
				assignDndLink(onFalseControl);
				assignDndLink(onTrueControl);
				assignDndComponent(fields);
				
				final JScrollPane	scroll = new JScrollPane(fields);  
				final JPanel		pane = new JPanel(new BorderLayout());
				
				scroll.setBorder(fieldsTitle);
				pane.add(scroll,BorderLayout.CENTER);
				pane.add(toolbar,BorderLayout.EAST);
				add(top,BorderLayout.NORTH);
				add(pane,BorderLayout.CENTER);
				add(bottom,BorderLayout.SOUTH);
				SwingUtils.assignActionKey(fields,SwingUtils.KS_HELP,(e)->{showHelp(e.getActionCommand());},mdi.getRoot().getHelpId());
				
				fields.addListSelectionListener((e)->{
					enableButtons(!fields.isSelectionEmpty());
				});
				fields.addContentChangeListener((changeType,source,current)->{
					switch (changeType) {
						case CHANGED	:
							break;
						case INSERTED	:
							controls.add((PipeLink)current);
							break;
						case REMOVED	:
							controls.remove((PipeLink)current);
							break;
						default 		: throw new UnsupportedOperationException("Change type ["+changeType+"] is not supported yet"); 
					}
				});
				targetControl.addContentChangeListener((changeType,source,current)->{
					switch (changeType) {
						case CHANGED	:
							break;
						case INSERTED	:
							links.add((PipeLink)current);
							break;
						case REMOVED	:
							links.remove((PipeLink)current);
							break;
						default 		: throw new UnsupportedOperationException("Change type ["+changeType+"] is not supported yet"); 
					}
				});
				enableButtons(!fields.isSelectionEmpty());
				
				fillLocalizedStrings(localizer.currentLocale().getLocale(),localizer.currentLocale().getLocale());
			} catch (LocalizationException e) {
				throw new ContentException(e);
			}
		}
	}

	@Override
	public ContentMetadataInterface getModel() {
		return mdi;
	}
	
	@Override
	public JControlLabel[] getControlSources() {
		return new JControlLabel[] {onTrueControl, onFalseControl};
	}

	@Override
	public JControlTargetLabel getControlTarget() {
		return targetControl;
	}

	@Override
	public PipeLink[] getLinks() {
		return links.toArray(new PipeLink[links.size()]);
	}

	@Override
	public void removeLink(final PipeLink link) {
		links.remove(link);
	}

	@Override
	public boolean validate(LoggerFacade logger) {
		if (expression.getText().trim().isEmpty()) {
			logger.message(Severity.warning,VALIDATION_MISSING_FIELD,getPipeItemName());
			return false;
		}
		else {
			return true;
		}
	}
	
	
	@Override
	public PipeLink[] getIncomingControls() {
		return controls.toArray(new PipeLink[controls.size()]);
	}
	
	
	@Override
	public <T> void storeIncomingValue(ContentNodeMetadata meta, T value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> T getOutgoingValue(ContentNodeMetadata meta) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PipeStepReturnCode processPipeStep() {
		// TODO Auto-generated method stub
		return PipeStepReturnCode.CONTINUE_TRUE;
	}
	
	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings(oldLocale,newLocale);
	}

	@Override
	public void serializeFrame(final JsonStaxPrinter printer) throws PrintingException, IOException {
		if (printer == null) {
			throw new NullPointerException("Json printer can't be null");
		}
		else {
			printer.splitter().name(JSON_PIPE_CONTENT).startObject();
				printer.name(JSON_PIPE_ITEM_EXPRESSION).value(expression.getText());
			printer.endObject();
		}
	}	
	
	@OnAction("action:/removeField")
	private void removeField() throws LocalizationException {
		if (new JLocalizedOptionPane(localizer).confirm(this,FIELDS_REMOVE_QUESTION,FIELDS_REMOVE_TITLE,JOptionPane.QUESTION_MESSAGE,JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			fields.removeContent(fields.getSelectedValue());
		}
	}
	
	private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		prepareTitle(mdi.getRoot().getLabelId(),mdi.getRoot().getTooltipId());
		fieldsTitle.setTitle(localizer.getValue(FIELDS_TITLE));
		fields.setToolTipText(localizer.getValue(FIELDS_TITLE_TT));
		conditionLabel.setText(localizer.getValue(EXPRESSION_TITLE));
		expression.setToolTipText(localizer.getValue(EXPRESSION_TITLE_TT));
	}

	private void enableButtons(final boolean buttonsState) {
		((JButton)SwingUtils.findComponentByName(toolbar,PIPE_MENU_REMOVE_FIELD)).setEnabled(buttonsState);
	}
}
