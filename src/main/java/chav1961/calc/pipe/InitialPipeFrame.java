package chav1961.calc.pipe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
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
import chav1961.purelib.model.Constants;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.FieldFormat;
import chav1961.purelib.model.MutableContentNodeMetadata;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.interfaces.OnAction;
import chav1961.purelib.ui.swing.useful.JContentMetadataEditor;
import chav1961.purelib.ui.swing.useful.JDialogContainer;
import chav1961.purelib.ui.swing.useful.JLocalizedOptionPane;
import chav1961.purelib.ui.swing.useful.JStateString;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.Application/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="chav1961.calc.pipe.initial.caption",tooltip="chav1961.calc.pipe.initial.caption.tt",help="help.aboutApplication")
@PluginProperties(resizable=true,width=500,height=220,pluginIconURI="initialFrameIcon.png",desktopIconURI="initialDesktopIcon.png")
public class InitialPipeFrame extends PipePluginFrame<InitialPipeFrame> {
	private static final 					long serialVersionUID = 1L;

	private static final String				TABS_FIELDS_TITLE = "chav1961.calc.pipe.initial.tabs.fields";	
	private static final String				TABS_FIELDS_TITLE_TT = "chav1961.calc.pipe.initial.tabs.fields.tt";	
	private static final String				TABS_CODE_TITLE = "chav1961.calc.pipe.initial.tabs.expressions";	
	private static final String				TABS_CODE_TITLE_TT = "chav1961.calc.pipe.initial.tabs.expressions.tt";	
	private static final String				FIELDS_TITLE = "chav1961.calc.pipe.initial.fields"; 
	private static final String				FIELDS_TITLE_TT = "chav1961.calc.pipe.initial.fields.tt"; 
	private static final String				FIELDS_ADD_TITLE = "chav1961.calc.pipe.initial.fields.add.caption"; 
//	private static final String				FIELDS_ADD_TITLE_TT = "chav1961.calc.pipe.initial.fields.add.caption.tt";
	private static final String				FIELDS_EDIT_TITLE = "chav1961.calc.pipe.initial.fields.edit.caption"; 
//	private static final String				FIELDS_EDIT_TITLE_TT = "chav1961.calc.pipe.initial.fields.edit.caption.tt";
	private static final String				FIELDS_REMOVE_TITLE = "chav1961.calc.pipe.initial.fields.remove.caption"; 
//	private static final String				FIELDS_REMOVE_TITLE_TT = "chav1961.calc.pipe.initial.fields.remove.caption.tt";
	private static final String				FIELDS_REMOVE_QUESTION = "chav1961.calc.pipe.initial.fields.remove.question"; 
	
	private static final URI				PIPE_MENU_ROOT = URI.create("ui:/model/navigation.top.initial.toolbar");	
//	private static final String				PIPE_MENU_ADD_FIELD = "chav1961.calc.pipe.initial.toolbar.addfield";	
	private static final String				PIPE_MENU_REMOVE_FIELD = "chav1961.calc.pipe.initial.toolbar.removefield";	
	private static final String				PIPE_MENU_EDIT_FIELD = "chav1961.calc.pipe.initial.toolbar.editfield";	

	
	private final ContentMetadataInterface	mdi;
	private final Localizer					localizer;
	private final JStateString				state;
	private final JControlSource			sourceControl;
	private final List<PipeLink>			controls = new ArrayList<>();
	private final ModelItemListContainer	fields;
	private final TitledBorder				fieldsTitle = new TitledBorder(new LineBorder(Color.BLACK),"");
	private final JToolBar					toolbar;
	private final ScriptEditor				initialCode = new ScriptEditor();
	private final JTabbedPane				tabs = new JTabbedPane(); 
	
	@LocaleResource(value="chav1961.calc.pipe.initial.caption",tooltip="chav1961.calc.pipe.initial.caption.tt")
	@Format("9.2pz")
	public float temp = 0;
	
	public InitialPipeFrame(final int uniqueId, final PipeManager parent, final Localizer localizer, final ContentNodeMetadata initial, final ContentMetadataInterface general) throws ContentException {
		super(uniqueId,parent,localizer,InitialPipeFrame.class,PipeItemType.INITIAL_ITEM);
		if (initial == null) {
			throw new NullPointerException("Initial metadata can't be null");
		}
		else if (general == null) {
			throw new NullPointerException("General metadata can't be null");
		}
		else {
			try{this.mdi = ContentModelFactory.forAnnotatedClass(this.getClass());
				this.localizer = LocalizerFactory.getLocalizer(mdi.getRoot().getLocalizerAssociated());
				this.state = new JStateString(localizer);
				this.sourceControl = new JControlSource(initial,this);
				this.fields = new ModelItemListContainer(localizer,this,DropAction.NONE);
				this.toolbar = SwingUtils.toJComponent(general.byUIPath(PIPE_MENU_ROOT),JToolBar.class);
				this.toolbar.setOrientation(JToolBar.VERTICAL);
				this.toolbar.setFloatable(false);
				SwingUtils.assignActionListeners(this.toolbar,this);
				
				final JPanel			bottom = new JPanel(new BorderLayout());
				final PluginProperties	props = this.getClass().getAnnotation(PluginProperties.class);
				
				assignDndLink(sourceControl);
				assignDndComponent(fields);
				bottom.add(state,BorderLayout.CENTER);
				bottom.add(sourceControl,BorderLayout.EAST);
				
				final JScrollPane	fieldsScroll = new JScrollPane(fields);
				final JPanel		pane = new JPanel(new BorderLayout());
				
				fieldsScroll.setBorder(fieldsTitle);
				pane.add(fieldsScroll,BorderLayout.CENTER);
				pane.add(toolbar,BorderLayout.EAST);

				tabs.addTab("",pane);
				tabs.addTab("",new JScrollPane(initialCode));
				tabs.setSelectedIndex(0);
				
				tabs.setPreferredSize(new Dimension(props.width(),props.height()));
				add(tabs,BorderLayout.CENTER);
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
		return new JControlLabel[] {sourceControl};
	}

	@Override
	public JControlTargetLabel getControlTarget() {
		return null;
	}

	@Override
	public PipeLink[] getLinks() {
		return new PipeLink[0];
	}
	
	@Override
	public void removeLink(final PipeLink link) {
	}

	@Override
	public boolean validate(LoggerFacade logger) {
		return true;
	}
	
	
	@Override
	public PipeLink[] getIncomingControls() {
		return new PipeLink[0];
	}
	
	@Override
	public Object preparePipeItem(final SimpleURLClassLoader loader) throws FlowException {
		final Map<String,Object>	variables = new HashMap<>();
		
		for (int index = 0, maxIndex = fields.getModel().getSize(); index < maxIndex; index++) {
			variables.put(buildVarName(fields.getModel().getElementAt(index).getMetadata()),null);
		}
		return variables;
	}

	@Override
	public void storeIncomingValue(final Object temp, final ContentNodeMetadata meta, final Object value) throws ContentException {
		throw new IllegalStateException("Initial node doesn't support this operation");
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
			final String				code = initialCode.getText().trim();
			
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
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings(oldLocale,newLocale);
	}

	@Override
	public void serializeFrame(final PluginSpecific specific) throws IOException {
		if (specific == null) {
			throw new NullPointerException("Plugin specific can't be null");
		}
		else {
			specific.initialCode = toSerial(initialCode.getText());
			
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
			initialCode.setText(specific.initialCode);
	
			if (specific.fields != null) {
				for (MutableContentNodeMetadata item : specific.fields) {
					fields.addContent(new PipeLink(PipeLinkType.DATA_LINK,null,null,this,fields,item,null));
				}
			}
		}
	}	
	
	@OnAction("action:/addField")
	private void addField() throws LocalizationException, ContentException {
		final ContentNodeMetadata		meta = new MutableContentNodeMetadata("name",String.class,"name",null,"testSet1","testSet2","testSet3",new FieldFormat(String.class),URI.create(ContentMetadataInterface.APPLICATION_SCHEME+":"+Constants.MODEL_APPLICATION_SCHEME_FIELD+":/clazz/name"), null); 
		final JContentMetadataEditor	ed = new JContentMetadataEditor(localizer);

		ed.setPreferredSize(new Dimension(350,350));
		ed.setValue(meta);
		
		final JDialogContainer<Object,Enum<?>,JContentMetadataEditor>	dlg = new JDialogContainer<Object,Enum<?>,JContentMetadataEditor>(localizer,(JFrame)null,FIELDS_ADD_TITLE,ed);
		
		if (dlg.showDialog()) {
			ContentNodeMetadata temp = ed.getContentNodeMetadataValue(); 
			fields.addContent(new PipeLink(PipeLinkType.DATA_LINK,null,null,this,fields,temp,null));
		}
	}

	@OnAction("action:/removeField")
	private void removeField() throws LocalizationException {
		if (new JLocalizedOptionPane(localizer).confirm(this,FIELDS_REMOVE_QUESTION,FIELDS_REMOVE_TITLE,JOptionPane.QUESTION_MESSAGE,JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			fields.removeContent(fields.getSelectedValue());
		}
	}

	@OnAction("action:/editField")
	private void editField() throws LocalizationException, ContentException {
		final PipeLink					source = fields.getSelectedValue(); 
		final ContentNodeMetadata		meta = source.getMetadata(); 
		final JContentMetadataEditor	ed = new JContentMetadataEditor(localizer);

		ed.setPreferredSize(new Dimension(350,350));
		ed.setValue(meta);
		
		final JDialogContainer<Object,Enum<?>,JContentMetadataEditor>	dlg = new JDialogContainer<Object,Enum<?>,JContentMetadataEditor>(localizer,(JFrame)null,FIELDS_EDIT_TITLE,ed);
		
		if (dlg.showDialog()) {
			fields.changeContent(fields.getSelectedIndex(),new PipeLink(source.getType(),source.getSource(),source.getSourcePoint(),source.getTarget(),source.getTargetPoint(),ed.getContentNodeMetadataValue(),null));
		}
	}
	
	private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		prepareTitle(mdi.getRoot().getLabelId(),mdi.getRoot().getTooltipId());
		fieldsTitle.setTitle(localizer.getValue(FIELDS_TITLE));
		fields.setToolTipText(localizer.getValue(FIELDS_TITLE_TT));

		tabs.setTitleAt(0,localizer.getValue(TABS_FIELDS_TITLE));
		tabs.setToolTipTextAt(0,localizer.getValue(TABS_FIELDS_TITLE_TT));
		tabs.setTitleAt(1,localizer.getValue(TABS_CODE_TITLE));
		tabs.setToolTipTextAt(1,localizer.getValue(TABS_CODE_TITLE_TT));
	}

	private void enableButtons(final boolean buttonsState) {
		((JButton)SwingUtils.findComponentByName(toolbar,PIPE_MENU_REMOVE_FIELD)).setEnabled(buttonsState);
		((JButton)SwingUtils.findComponentByName(toolbar,PIPE_MENU_EDIT_FIELD)).setEnabled(buttonsState);
	}
}
