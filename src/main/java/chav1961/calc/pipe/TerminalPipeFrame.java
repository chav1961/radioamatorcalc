package chav1961.calc.pipe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.calc.interfaces.PipeContainerInterface.PipeItemType;
import chav1961.calc.pipe.ModelItemListContainer.DropAction;
import chav1961.calc.utils.PipeLink;
import chav1961.calc.utils.PipePluginFrame;
import chav1961.calc.utils.PipeLink.PipeLinkType;
import chav1961.calc.windows.PipeManager;
import chav1961.calc.windows.PipeManagerSerialForm.PluginSpecific;
import chav1961.purelib.basic.CharUtils;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.FlowException;
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
import chav1961.purelib.model.MutableContentNodeMetadata;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.streams.JsonStaxPrinter;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.interfaces.OnAction;
import chav1961.purelib.ui.swing.useful.JLocalizedOptionPane;
import chav1961.purelib.ui.swing.useful.JStateString;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.Application/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="chav1961.calc.pipe.terminal.caption",tooltip="chav1961.calc.pipe.terminal.caption.tt",help="help.aboutApplication")
@PluginProperties(width=500,height=200,pluginIconURI="terminalFrameIcon.png",desktopIconURI="terminalDesktopIcon.png")
public class TerminalPipeFrame extends PipePluginFrame<TerminalPipeFrame> {
	private static final 					long serialVersionUID = 1L;
	private static final String				FIELDS_TITLE = "chav1961.calc.pipe.terminal.fields"; 
	private static final String				FIELDS_TITLE_TT = "chav1961.calc.pipe.terminal.fields.tt"; 
	private static final String				MESSAGE_TITLE = "chav1961.calc.pipe.terminal.message"; 
	private static final String				MESSAGE_TITLE_TT = "chav1961.calc.pipe.terminal.message.tt"; 
	private static final String				FAILURE_TITLE = "chav1961.calc.pipe.terminal.failure"; 
	private static final String				FAILURE_TITLE_TT = "chav1961.calc.pipe.terminal.failure.tt"; 

	private static final String				FIELDS_REMOVE_TITLE = "chav1961.calc.pipe.terminal.fields.remove.caption"; 
//	private static final String				FIELDS_REMOVE_TITLE_TT = "chav1961.calc.pipe.terminal.fields.remove.caption.tt";
	private static final String				FIELDS_REMOVE_QUESTION = "chav1961.calc.pipe.terminal.fields.remove.question"; 

	private static final String				VALIDATION_MISSING_FIELD = "chav1961.calc.pipe.terminal.validation.missingfield"; 
	
	private static final String				RUNTIME_TERMINATE_TITLE = "chav1961.calc.pipe.terminal.terminate.caption"; 
//	private static final String				RUNTIME_TERMINATE_TITLE_TT = "chav1961.calc.pipe.terminal.terminate.caption.tt";
	
	private static final URI				PIPE_MENU_ROOT = URI.create("ui:/model/navigation.top.terminal.toolbar");	
	private static final String				PIPE_MENU_REMOVE_FIELD = "chav1961.calc.pipe.terminal.toolbar.removefield";	

	
	private final ContentMetadataInterface	mdi;
	private final Localizer					localizer;
	private final JStateString				state;
	private final JControlTarget			targetControl;
	private final List<PipeLink>			links = new ArrayList<>();
	private final List<PipeLink>			controls = new ArrayList<>();
	private final ModelItemListContainer	fields;
	private final JToolBar					toolbar;
	private final TitledBorder				fieldsTitle = new TitledBorder(new LineBorder(Color.BLACK)); 
	private final JCheckBox					terminalFailure = new JCheckBox();
	private final JLabel					terminalLabel = new JLabel();
	private final JTextField				terminalMessage = new JTextField();
	
	@LocaleResource(value="chav1961.calc.pipe.terminal.caption",tooltip="chav1961.calc.pipe.terminal.caption.tt")
	@Format("9.2pz")
	public float temp = 0;
	
	public TerminalPipeFrame(final int uniqueId, final PipeManager parent,final Localizer localizer, final ContentNodeMetadata terminal, final ContentMetadataInterface general) throws ContentException {
		super(uniqueId,parent,localizer,TerminalPipeFrame.class,PipeItemType.TERMINAL_ITEM);
		if (terminal == null) {
			throw new NullPointerException("Initial metadata can't be null");
		}
		else {
			try{this.mdi = ContentModelFactory.forAnnotatedClass(this.getClass());
				this.localizer = LocalizerFactory.getLocalizer(mdi.getRoot().getLocalizerAssociated());
				this.state = new JStateString(localizer);
				this.targetControl = new JControlTarget(terminal,this);
				this.fields = new ModelItemListContainer(localizer,this,DropAction.INSERT);
				this.toolbar = SwingUtils.toJComponent(general.byUIPath(PIPE_MENU_ROOT),JToolBar.class);
				this.toolbar.setOrientation(JToolBar.VERTICAL);
				this.toolbar.setFloatable(false);
				SwingUtils.assignActionListeners(this.toolbar,this);
				
				final JPanel			bottom = new JPanel(new BorderLayout());
				final JPanel			top = new JPanel(new BorderLayout(5,5));
				final Color				ordinalTextColor = terminalMessage.getForeground();
				final PluginProperties	props = this.getClass().getAnnotation(PluginProperties.class);
				
				bottom.add(state,BorderLayout.CENTER);
				bottom.add(targetControl,BorderLayout.WEST);

				top.add(terminalLabel,BorderLayout.WEST);
				top.add(terminalMessage,BorderLayout.CENTER);
				top.add(terminalFailure,BorderLayout.EAST);
				
				terminalFailure.addActionListener((e)->{
					terminalMessage.setForeground(terminalFailure.isSelected() ? Color.RED : ordinalTextColor);
				});

				assignDndComponent(fields);
				
				final JScrollPane	pane = new JScrollPane(fields); 
				
				pane.setBorder(fieldsTitle);
				pane.setPreferredSize(new Dimension(props.width(),props.height()));
				add(top,BorderLayout.NORTH);
				add(pane,BorderLayout.CENTER);
				add(toolbar,BorderLayout.EAST);
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
				fields.setName("fields");
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
		return new JControlLabel[0];
	}

	@Override
	public JControlTargetLabel getControlTarget() {
		return targetControl;
	}

	@Override
	public boolean validate(final LoggerFacade logger) {
		if (terminalMessage.getText().trim().isEmpty()) {
			logger.message(Severity.warning,VALIDATION_MISSING_FIELD,getPipeItemName());
			return false;
		}
		else {
			return true;
		}
	}
	
	@Override
	public Object preparePipeItem() throws FlowException {
		final Map<String,Object>	variables = new HashMap<>();
		
		for (int index = 0, maxIndex = fields.getModel().getSize(); index < maxIndex; index++) {
			variables.put(buildVarName(fields.getModel().getElementAt(index).getMetadata()),null);
		}
		return variables;
	}

	@Override
	public void storeIncomingValue(final Object temp, final ContentNodeMetadata meta, final Object value) throws ContentException {
		if (temp == null || !(temp instanceof Map)) {
			throw new IllegalArgumentException("Temporary object is null or is not an implementation of Map interface"); 
		}
		else {
			final Map<String,Object>	variables = (Map<String,Object>)temp;
			
			variables.replace(buildVarName(meta),value);
		}
	}

	@Override
	public PipeStepReturnCode processPipeStep(final Object temp, final LoggerFacade logger, final boolean confirmAll) throws FlowException {
		if (temp == null || !(temp instanceof Map)) {
			throw new IllegalArgumentException("Temporary object is null or is not an implementation of Map interface"); 
		}
		else if (logger == null) {
			throw new NullPointerException("Logger can't be null"); 
		}
		else {
			final Map<String,Object>	variables = (Map<String,Object>)temp;
			
			try{final String	title = localizer.getValue(RUNTIME_TERMINATE_TITLE);
				final String	message = CharUtils.substitute("message",terminalMessage.getText(),(name)->variables.get(buildVarName(-1,name)) == null ? "<"+name+" is missing>" : variables.get(buildVarName(-1,name)).toString());
			
				if (terminalFailure.isSelected()) {
					if (!confirmAll) {
						JOptionPane.showMessageDialog(this,message,title,JOptionPane.ERROR_MESSAGE);
					}
					return PipeStepReturnCode.TERMINATE_FALSE;
				}
				else {
					if (!confirmAll) {
						JOptionPane.showMessageDialog(this,message,title,JOptionPane.INFORMATION_MESSAGE);
					}
					return PipeStepReturnCode.TERMINATE_TRUE;
				}
			} catch (LocalizationException e) {
				throw new FlowException(e.getLocalizedMessage(),e);
			}
		}
	}

	@Override
	public Object getOutgoingValue(final Object temp, final ContentNodeMetadata meta) throws ContentException {
		throw new IllegalStateException("Terminal node doesn't support this operation");
	}

	@Override
	public void unpreparePipeItem(final Object temp) throws FlowException {
		if (temp == null || !(temp instanceof Map)) {
			throw new IllegalArgumentException("Temporary object is null or is not an implementation of Map interface"); 
		}
		else {
			final Map<String,Object>	variables = (Map<String,Object>)temp;
			
			variables.clear();
		}
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
	public PipeLink[] getIncomingControls() {
		return controls.toArray(new PipeLink[controls.size()]);
	}
	
	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings(oldLocale,newLocale);
	}

	@Override
	public void serializeFrame(final PluginSpecific specific) throws IOException {
		if (specific == null) {
			throw new NullPointerException("Plugin specific can't be null");
		}
		else {
			specific.message = terminalMessage.getText();
			specific.isError = terminalFailure.isSelected();
			if (!controls.isEmpty()) {
				specific.fields = new MutableContentNodeMetadata[controls.size()];
				for (int index = 0, maxIndex = specific.fields.length; index < maxIndex; index++) {
					specific.fields[index] = (MutableContentNodeMetadata) controls.get(index).getMetadata();
				}
			}
		}
	}
	
	@Override
	public void deserializeFrame(final PluginSpecific specific) throws IOException {
		if (specific == null) {
			throw new NullPointerException("Plugin specific can't be null");
		}
		else {
			terminalMessage.setText(specific.message);
			terminalFailure.setSelected(specific.isError);
			if (specific.fields != null) {
				for (MutableContentNodeMetadata item : specific.fields) {
					fields.addContent(new PipeLink(PipeLinkType.DATA_LINK,null,null,this,fields,item,null));
				}
			}
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
		terminalLabel.setText(localizer.getValue(MESSAGE_TITLE));
		terminalMessage.setToolTipText(localizer.getValue(MESSAGE_TITLE_TT));
		terminalFailure.setText(localizer.getValue(FAILURE_TITLE));
		terminalFailure.setToolTipText(localizer.getValue(FAILURE_TITLE_TT));
	}

	private void enableButtons(final boolean buttonsState) {
		((JButton)SwingUtils.findComponentByName(toolbar,PIPE_MENU_REMOVE_FIELD)).setEnabled(buttonsState);
	}
}
