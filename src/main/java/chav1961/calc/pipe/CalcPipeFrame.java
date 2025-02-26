package chav1961.calc.pipe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.calc.pipe.ModelItemListContainer.DropAction;
import chav1961.calc.script.ScriptEditor;
import chav1961.calc.script.ScriptProcessor;
import chav1961.calc.script.ScriptProcessor.DataManager;
import chav1961.calc.utils.PipeLink;
import chav1961.calc.utils.PipeLink.PipeLinkType;
import chav1961.calc.utils.PipePluginFrame;
import chav1961.calc.windows.PipeManager;
import chav1961.calc.windows.PipeManagerSerialForm.PluginSpecific;
import chav1961.purelib.basic.SimpleURLClassLoader;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
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
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.interfaces.OnAction;
import chav1961.purelib.ui.swing.useful.JLocalizedOptionPane;
import chav1961.purelib.ui.swing.useful.JStateString;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.Application/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="chav1961.calc.pipe.calc.caption",tooltip="chav1961.calc.pipe.calc.caption.tt",help="help.aboutApplication")
@PluginProperties(width=450,height=250,pluginIconURI="calcFrameIcon.png",desktopIconURI="calcDesktopIcon.png")
public class CalcPipeFrame extends PipePluginFrame<CalcPipeFrame> {
	private static final 					long serialVersionUID = 1L;
	
	private static final String				TABS_FIELDS_TITLE = "chav1961.calc.pipe.calc.tabs.fields";	
	private static final String				TABS_FIELDS_TITLE_TT = "chav1961.calc.pipe.calc.tabs.fields.tt";	
	private static final String				TABS_EXPRESSIONS_TITLE = "chav1961.calc.pipe.calc.tabs.expressions";	
	private static final String				TABS_EXPRESSIONS_TITLE_TT = "chav1961.calc.pipe.calc.tabs.expressions.tt";	
	private static final String				SOURCE_FIELDS_TITLE = "chav1961.calc.pipe.calc.fields.source"; 
	private static final String				SOURCE_FIELDS_TITLE_TT = "chav1961.calc.pipe.calc.fields.source.tt"; 

	private static final String				FIELDS_REMOVE_TITLE = "chav1961.calc.pipe.calc.fields.remove.caption"; 
//	private static final String				FIELDS_REMOVE_TITLE_TT = "chav1961.calc.pipe.calc.fields.remove.caption.tt";
	private static final String				FIELDS_REMOVE_QUESTION = "chav1961.calc.pipe.calc.fields.remove.question"; 
	
	private static final URI				PIPE_MENU_SOURCE_ROOT = URI.create("ui:/model/navigation.top.calc.sourceToolbar");	
	private static final String				PIPE_MENU_REMOVE_SOURCE_FIELD = "chav1961.calc.pipe.calc.sourceToolbar.removefield";	

	private final ContentMetadataInterface	mdi;
	private final Localizer					localizer;
	private final JStateString				state;
	private final JControlTarget			targetControl;
	private final JControlSource			sourceControl;
	private final List<PipeLink>			links = new ArrayList<>();
	private final List<PipeLink>			sourceControls = new ArrayList<>();
	private final ModelItemListContainer	sourceFields; 
	private final TitledBorder				sourceFieldsTitle = new TitledBorder(new LineBorder(Color.BLACK)); 
	private final JToolBar					sourceToolbar;
	private final ScriptEditor				program = new ScriptEditor();
	private final JTabbedPane				tabs = new JTabbedPane(); 
	@LocaleResource(value="chav1961.calc.pipe.calc.caption",tooltip="chav1961.calc.pipe.calc.caption.tt")
	@Format("9.2pz")
	public float temp = 0;
	
	public CalcPipeFrame(final int uniqueId, final PipeManager parent, final Localizer localizer, final ContentNodeMetadata inner, final ContentNodeMetadata outer, final ContentMetadataInterface general) throws ContentException {
		super(uniqueId,parent,localizer,CalcPipeFrame.class,PipeItemType.CALC_ITEM);
		if (inner == null) {
			throw new NullPointerException("Initial metadata can't be null");
		}
		else {
			try{this.mdi = ContentModelFactory.forAnnotatedClass(this.getClass());
				this.localizer = LocalizerFactory.getLocalizer(mdi.getRoot().getLocalizerAssociated());
				this.state = new JStateString(localizer);
				this.targetControl = new JControlTarget(inner,this);
				this.sourceControl = new JControlSource(outer,this);
				this.sourceFields = new ModelItemListContainer(localizer,this,DropAction.INSERT);
				this.sourceToolbar = SwingUtils.toJComponent(general.byUIPath(PIPE_MENU_SOURCE_ROOT),JToolBar.class);
				this.sourceToolbar.setOrientation(JToolBar.VERTICAL);
				this.sourceToolbar.setFloatable(false);
				SwingUtils.assignActionListeners(this.sourceToolbar,this);
				
				final JPanel			bottom = new JPanel(new BorderLayout());
				final PluginProperties	props = this.getClass().getAnnotation(PluginProperties.class);
				
				assignDndLink(sourceControl);
				assignDndComponent(sourceFields);
				
				bottom.add(state,BorderLayout.CENTER);
				bottom.add(targetControl,BorderLayout.WEST);
				bottom.add(sourceControl,BorderLayout.EAST);
				
				final JScrollPane	scroll1 = new JScrollPane(sourceFields); 
				final JPanel		pane1 = new JPanel(new BorderLayout()); 
				final JPanel		centerPanel = new JPanel(new GridLayout(1,1));
				
				scroll1.setBorder(sourceFieldsTitle);
				pane1.add(scroll1,BorderLayout.CENTER);
				pane1.add(sourceToolbar,BorderLayout.EAST);
				centerPanel.add(pane1);
				
				tabs.addTab("",centerPanel);
				tabs.addTab("",new JScrollPane(program));
				tabs.setSelectedIndex(0);
				
				tabs.setPreferredSize(new Dimension(props.width(),props.height()));
				add(tabs,BorderLayout.CENTER);
				
				add(bottom,BorderLayout.SOUTH);
				SwingUtils.assignActionKey(sourceFields,SwingUtils.KS_HELP,(e)->{showHelp(e.getActionCommand());},mdi.getRoot().getHelpId());

				sourceFields.addListSelectionListener((e)->{
					enableSourceButtons(!sourceFields.isSelectionEmpty());
				});
				sourceFields.addContentChangeListener((changeType,source,current)->{
					switch (changeType) {
						case CHANGED	:
							break;
						case INSERTED	:
							sourceControls.add((PipeLink)current);
							break;
						case REMOVED	:
							sourceControls.remove((PipeLink)current);
							break;
						default 		: throw new UnsupportedOperationException("Change type ["+changeType+"] is not supported yet"); 
					}
				});
				sourceFields.setName("sourceFields");
				enableSourceButtons(!sourceFields.isSelectionEmpty());
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
		return new JControlLabel[] {sourceControl};
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
		// TODO Auto-generated method stub
		return true;
	}
	
	
	@Override
	public PipeLink[] getIncomingControls() {
		return sourceControls.toArray(new PipeLink[sourceControls.size()]);
	}

	@Override
	public Object preparePipeItem(final SimpleURLClassLoader loader) throws FlowException {
		final Map<String,Object>	sourceVariables = new HashMap<>(); 
		
		for (int index = 0, maxIndex = sourceFields.getModel().getSize(); index < maxIndex; index++) {
			sourceVariables.put(buildVarName(sourceFields.getModel().getElementAt(index).getMetadata()),null);
		}
		return sourceVariables;
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
	public PipeStepReturnCode processPipeStep(final Object temp, final LoggerFacade logger, final PipeConfigmation confirm) throws FlowException {
		if (temp == null || !(temp instanceof Map)) {
			throw new IllegalArgumentException("Temporary object is null or is not an implementation of Map interface"); 
		}
		else if (logger == null) {
			throw new NullPointerException("Logger can't be null"); 
		}
		else if (confirm == null) {
			throw new NullPointerException("Confirmation type can't be null"); 
		}
		else {
			final Map<String,Object>	variables = (Map<String,Object>)temp;
			final String				code = program.getText().trim();
			
			if (!code.isEmpty()) {
				try{ScriptProcessor.execute(code,new DataManager() {
						@Override
						public boolean exists(final int pluginId, final String name) {
							return variables.containsKey(buildVarName(pluginId,name));
						}
	
						@Override
						public Object getVar(final int pluginId, final String name) {
							return variables.get(buildVarName(pluginId,name));
						}
	
						@Override
						public void setVar(final int pluginId, final String name, final Object value) {
							variables.replace(buildVarName(pluginId,name),value);
						}
	
						@Override
						public void print(final Object value) {
							logger.message(Severity.debug,value.toString());
						}
					});
				} catch (SyntaxException e) {
					throw new FlowException("Node ["+getPipeItemName()+"] script error: "+e);
				}
			}
			return PipeStepReturnCode.CONTINUE;
		}
	}

	@Override
	public Object getOutgoingValue(final Object temp, final ContentNodeMetadata meta) throws ContentException {
		if (temp == null || !(temp instanceof Map)) {
			throw new IllegalArgumentException("Temporary object is null or is not an implementation of Map interface"); 
		}
		else if (meta == null) {
			throw new NullPointerException("Metadata can't be null"); 
		}
		else {
			final Map<String,Object>	variables = (Map<String,Object>)temp;
			
			return variables.get(buildVarName(meta));
		}
	}

	@Override
	public void unpreparePipeItem(Object temp) throws FlowException {
		if (temp == null || !(temp instanceof Map)) {
			throw new IllegalArgumentException("Temporary object is null or is not an implementation of Map interface"); 
		}
		else {
			final Map<String,Object>	variables = (Map<String,Object>)temp;
	
			variables.clear();
		}
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
			specific.program = toSerial(program.getText());
			
			if (!sourceControls.isEmpty()) {
				specific.fields = new MutableContentNodeMetadata[sourceControls.size()];
				
				for (int index = 0, maxIndex = specific.fields.length; index < maxIndex; index++) {
					specific.fields[index] = (MutableContentNodeMetadata) sourceControls.get(index).getMetadata();
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
			program.setText(specific.program);
		
			if (specific.fields != null) {
				for (MutableContentNodeMetadata item : specific.fields) {
					sourceFields.addContent(new PipeLink(PipeLinkType.DATA_LINK,null,null,this,sourceFields,item,null));
				}
			}
		}
	}
	
	@OnAction("action:/removeSourceField")
	private void removeSourceField() throws LocalizationException {
		if (new JLocalizedOptionPane(localizer).confirm(this,FIELDS_REMOVE_QUESTION,FIELDS_REMOVE_TITLE,JOptionPane.QUESTION_MESSAGE,JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			sourceFields.removeContent(sourceFields.getSelectedValue());
		}
	}

	private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		prepareTitle(mdi.getRoot().getLabelId(),mdi.getRoot().getTooltipId());
		sourceFieldsTitle.setTitle(localizer.getValue(SOURCE_FIELDS_TITLE));
		sourceFields.setToolTipText(localizer.getValue(SOURCE_FIELDS_TITLE_TT));

		tabs.setTitleAt(0,localizer.getValue(TABS_FIELDS_TITLE));
		tabs.setToolTipTextAt(0,localizer.getValue(TABS_FIELDS_TITLE_TT));
		tabs.setTitleAt(1,localizer.getValue(TABS_EXPRESSIONS_TITLE));
		tabs.setToolTipTextAt(1,localizer.getValue(TABS_EXPRESSIONS_TITLE_TT));
	}

	private void enableSourceButtons(final boolean buttonsState) {
		((JButton)SwingUtils.findComponentByName(sourceToolbar,PIPE_MENU_REMOVE_SOURCE_FIELD)).setEnabled(buttonsState);
	}
}
