package chav1961.calc.pipe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.calc.interfaces.PipeItemRuntime.PipeStepReturnCode;
import chav1961.calc.pipe.ModelItemListContainer.DropAction;
import chav1961.calc.script.ScriptEditor;
import chav1961.calc.script.ScriptProcessor;
import chav1961.calc.script.ScriptProcessor.DataManager;
import chav1961.calc.utils.InnerSVGPluginWindow;
import chav1961.calc.utils.PipeLink;
import chav1961.calc.utils.PipeLink.PipeLinkType;
import chav1961.calc.utils.PipePluginFrame;
import chav1961.calc.windows.PipeManager;
import chav1961.calc.windows.PipeManagerSerialForm.PluginSpecific;
import chav1961.purelib.basic.GettersAndSettersFactory;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.SimpleURLClassLoader;
import chav1961.purelib.basic.GettersAndSettersFactory.GetterAndSetter;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.ModuleAccessor;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.basic.interfaces.ModuleExporter;
import chav1961.purelib.enumerations.ContinueMode;
import chav1961.purelib.enumerations.NodeEnterMode;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.model.Constants;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.ModelUtils;
import chav1961.purelib.model.MutableContentNodeMetadata;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.streams.JsonStaxPrinter;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;
import chav1961.purelib.ui.swing.AutoBuiltForm;
import chav1961.purelib.ui.swing.JRadioButtonWithMeta;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.interfaces.JComponentMonitor.MonitorEvent;
import chav1961.purelib.ui.swing.interfaces.OnAction;
import chav1961.purelib.ui.swing.useful.JLocalizedOptionPane;
import chav1961.purelib.ui.swing.useful.JStateString;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.Application/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="chav1961.calc.pipe.container.caption",tooltip="chav1961.calc.pipe.container.caption.tt",help="help.aboutApplication")
@PluginProperties(width=100,height=100,pluginIconURI="containerFrameIcon.png",desktopIconURI="conditionalDesktopIcon.png")
public class ContainerPipeFrame<T> extends PipePluginFrame<ContainerPipeFrame> {
	private static final long serialVersionUID = 1846704657961834483L;

	private static final String					TABS_PLUGIN_TITLE = "chav1961.calc.pipe.container.tabs.plugin";	
	private static final String					TABS_PLUGIN_TITLE_TT = "chav1961.calc.pipe.container.tabs.plugin.tt";	
	private static final String					TABS_META_TITLE = "chav1961.calc.pipe.container.tabs.meta";	
	private static final String					TABS_META_TITLE_TT = "chav1961.calc.pipe.container.tabs.meta.tt";	
	private static final String					TABS_CODE_TITLE = "chav1961.calc.pipe.container.tabs.expressions";	
	private static final String					TABS_CODE_TITLE_TT = "chav1961.calc.pipe.container.tabs.expressions.tt";	

	private static final String					FIELDS_TITLE = "chav1961.calc.pipe.container.fields"; 
	private static final String					FIELDS_TITLE_TT = "chav1961.calc.pipe.container.fields.tt"; 
	private static final String					ACTIONS_TITLE = "chav1961.calc.pipe.container.actions"; 
//	private static final String					ACTIONS_TITLE_TT = "chav1961.calc.pipe.container.actions.tt"; 

	private static final String					LINK_REMOVE_TITLE = "chav1961.calc.pipe.container.links.remove.caption"; 
//	private static final String					LINK_REMOVE_TITLE_TT = "chav1961.calc.pipe.container.links.remove.caption.tt";
	private static final String					LINK_REMOVE_QUESTION = "chav1961.calc.pipe.container.links.remove.question"; 

	private static final URI					PIPE_MENU_ROOT = URI.create("ui:/model/navigation.top.container.toolbar");	
	private static final String					PIPE_MENU_REMOVE_LINK = "chav1961.calc.pipe.container.toolbar.removelink";	

	private static final String					VALIDATION_NO_ACTION_SELECTED = "chav1961.calc.pipe.container.validation.noactionselected"; 
	
	private static final String					JSON_PIPE_ITEM_INITIAL_CODE = "initialCode";
	
	private static final String					ACCESSOR_NAME = "<accessor>";
	
	private static final AtomicInteger			uniqueName = new AtomicInteger();
	
	private final ContentMetadataInterface		mdi;
	private final Localizer						localizer;
	private final JStateString					state;
	private final JControlTarget				targetControl;
	private final JControlSource				sourceControl;
	private final List<PipeLink>				links = new ArrayList<>();
	private final List<PipeLink>				controls = new ArrayList<>();
	private final ModelItemListContainer		fields;
	private final JToolBar						toolbar;
	private final List<JRadioButtonWithMeta>	actions = new ArrayList<>();
	private final TitledBorder					fieldsTitle = new TitledBorder(new LineBorder(Color.BLACK)); 
	private final TitledBorder					actionsTitle = new TitledBorder(new LineBorder(Color.BLACK)); 
	private final ScriptEditor					initialCode = new ScriptEditor();
	private final JTabbedPane					tabs = new JTabbedPane();
	private final LoggerFacade					logger;
	private PluginAndForm<T>					paf = null;
	@LocaleResource(value="chav1961.calc.pipe.container.caption",tooltip="chav1961.calc.pipe.container.caption.tt")
	@Format("9.2pz")
	public float temp = 0;

	public ContainerPipeFrame(final int uniqueId, final PipeManager parent, final Localizer localizer, final FormManager<?,?> content, final ContentMetadataInterface general) throws ContentException {
		super(uniqueId,parent, localizer, ContainerPipeFrame.class, PipeItemType.PLUGIN_ITEM);
		
		if (!(content instanceof ModuleAccessor)) {
			throw new IllegalArgumentException("Content plugin must implements "+ModuleAccessor.class.getCanonicalName()+" interface!");
		}
		
		try{
			this.mdi = ContentModelFactory.forAnnotatedClass(this.getClass());
			this.localizer = LocalizerFactory.getLocalizer(mdi.getRoot().getLocalizerAssociated());
			this.state = new JStateString(localizer);
			this.targetControl = new JControlTarget(mdi.getRoot(),this);
			this.sourceControl = new JControlSource(mdi.getRoot(),this);
			this.fields = new ModelItemListContainer(localizer,this,DropAction.LINK);
			this.toolbar = SwingUtils.toJComponent(general.byUIPath(PIPE_MENU_ROOT),JToolBar.class);
			this.toolbar.setOrientation(JToolBar.VERTICAL);
			this.toolbar.setFloatable(false);
			this.logger = content.getLogger();
			this.paf = buildPluginAndForm(content);
			
			final List<ContentNodeMetadata>	pluginFields = new ArrayList<>();
			final List<ContentNodeMetadata>	pluginActions = new ArrayList<>();
        	
			paf.innerMdi.walkDown((mode,applicationPath,uiPath,node)->{
				if (mode == NodeEnterMode.ENTER) {
					if (URIUtils.canServeURI(applicationPath,URI.create(ContentMetadataInterface.APPLICATION_SCHEME+":"+Constants.MODEL_APPLICATION_SCHEME_FIELD+":/"))) {
						pluginFields.add(node);
					}
					else if (URIUtils.canServeURI(applicationPath,URI.create(ContentMetadataInterface.APPLICATION_SCHEME+":"+Constants.MODEL_APPLICATION_SCHEME_ACTION+":/"))) {
						pluginActions.add(node);
					}
				}
				return ContinueMode.CONTINUE;
			}, paf.innerMdi.getRoot().getUIPath());
			
			final JSplitPane	metaTab = new JSplitPane();
			final JPanel		metaFieldsPanel = new JPanel(new BorderLayout());
			final JScrollPane	metaFields = new JScrollPane(fields);
			final JPanel		metaActions = new JPanel(new GridLayout(pluginActions.size(),1));
			final ButtonGroup 	buttonGroup = new ButtonGroup();
			
			for (ContentNodeMetadata item : pluginFields) {
				fields.addContent(new PipeLink(PipeLinkType.DATA_LINK,null,null,fields.getOwner(),fields,item,null));
			}
			
			for (ContentNodeMetadata item : pluginActions) {
				final MutableContentNodeMetadata	mcmd = new MutableContentNodeMetadata(item.getName(),boolean.class,item.getRelativeUIPath().toString(),item.getLocalizerAssociated(),item.getLabelId(),item.getTooltipId(),item.getHelpId(),item.getFormatAssociated(),item.getApplicationPath(),item.getIcon()); 
				final JRadioButtonWithMeta			r = new JRadioButtonWithMeta(mcmd,paf.pluginLocalizer,(event,metadata,component,parameters)->true); 
				
				metaActions.add(r);
				buttonGroup.add(r);
				actions.add(r);
			}
			
			metaFields.setBorder(fieldsTitle);
			metaFieldsPanel.add(metaFields,BorderLayout.CENTER);
			metaFieldsPanel.add(toolbar,BorderLayout.EAST);
			metaTab.setLeftComponent(metaFieldsPanel);
			metaActions.setBorder(actionsTitle);
			metaTab.setRightComponent(metaActions);
			
			tabs.addTab("",paf.w);
			tabs.addTab("",metaTab);
			tabs.addTab("",new JScrollPane(initialCode));
			tabs.setSelectedIndex(0);
			
			final JPanel			bottom = new JPanel(new BorderLayout());
			
			assignDndLink(sourceControl);
			assignDndComponent(fields);
			
			bottom.add(state,BorderLayout.CENTER);
			bottom.add(targetControl,BorderLayout.WEST);
			bottom.add(sourceControl,BorderLayout.EAST);
			
			add(tabs,BorderLayout.CENTER);
			add(bottom,BorderLayout.SOUTH);

			fields.addListSelectionListener((e)->{
				enableButtons(!fields.isSelectionEmpty() && fields.getSelectedValue().getSource() != null);
			});
			fields.addContentChangeListener((changeType,source,current)->{
				switch (changeType) {
					case CHANGED	:
						boolean	found = false;
						
						for (int index = 0, maxIndex = controls.size(); index < maxIndex; index++) {
							if (controls.get(index).getMetadata() == ((PipeLink)current).getMetadata()) {
								controls.set(index,(PipeLink)current);
								enableButtons(!fields.isSelectionEmpty() && fields.getSelectedValue().getSource() != null);
								found= true;
								break;
							}
						}
						if (!found) {
							for (int index = 0, maxIndex = fields.getModel().getSize(); index < maxIndex; index++) {
								if (fields.getModel().getElementAt(index).getMetadata() == ((PipeLink)current).getMetadata()) {
									controls.add((PipeLink)current);
									enableButtons(!fields.isSelectionEmpty() && fields.getSelectedValue().getSource() != null);
									break;
								}
							}
						}
						break;
					default 		: throw new UnsupportedOperationException("Change type ["+changeType+"] is not supported yet"); 
				}
			});
			fields.setName("fields");
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
			enableButtons(false);
			
			fillLocalizedStrings(localizer.currentLocale().getLocale(),localizer.currentLocale().getLocale()); 
		} catch (LocalizationException e) {
			throw new ContentException(e);
		}
	}

	@Override
	public ContentMetadataInterface getModel() {
		return paf.innerMdi;
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
	public boolean validate(final LoggerFacade logger) {
		boolean	selected = false;
		
		for (JRadioButtonWithMeta item : actions) {
			selected |= item.isSelected();
		}
		if (!selected) {
			logger.message(Severity.warning,VALIDATION_NO_ACTION_SELECTED,getPipeItemName());
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
	public Object preparePipeItem(final SimpleURLClassLoader loader) throws FlowException {
		final Map<String,Object>		variables = new HashMap<>();
		
		try{final Map<String,Object>	acc = paf.contentAccessor.getConstructor(paf.instanceClass).newInstance(paf.abf.getInstance());
			
			variables.put(ACCESSOR_NAME,acc);
			((ModuleAccessor)paf.abf.getInstance()).allowUnnamedModuleAccess(((ModuleExporter)acc).getUnnamedModules());
			
			for (int index = 0, maxIndex = fields.getModel().getSize(); index < maxIndex; index++) {
				final ContentNodeMetadata	meta = fields.getModel().getElementAt(index).getMetadata();
				
				variables.put(buildVarName(meta),acc.get(meta.getName()));
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException exc) {
			throw new FlowException(exc.getLocalizedMessage(),exc);
		}
		return variables;
	}

	@Override
	public void storeIncomingValue(Object temp, ContentNodeMetadata meta, Object value) throws ContentException {
		if (temp == null || !(temp instanceof Map)) {
			throw new IllegalArgumentException("Temporary object is null or is not an implementation of Map interface"); 
		}
		else {
			final Map<String,Object>	variables = (Map<String,Object>)temp;
			
			variables.replace(buildVarName(meta),value);
		}
	}

	@Override
	public PipeStepReturnCode processPipeStep(final Object temp, final LoggerFacade logger, final PipeConfigmation confirmation) throws FlowException {
		if (temp == null || !(temp instanceof Map)) {
			throw new IllegalArgumentException("Temporary object is null or is not an implementation of Map interface"); 
		}
		else if (logger == null) {
			throw new NullPointerException("Logger can't be null"); 
		}
		else if (confirmation == null) {
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
			try {
				final Map<String,Object>	acc = (Map<String, Object>) variables.get(ACCESSOR_NAME);

				for (String item : acc.keySet()) {			// Copy variable's values to plugin fields
					final String	key = "#"+getPluginId()+"."+item;
					final Object	value = variables.get(key); 
					
					if (value != null) {
						acc.replace(item,value);
					}
				}
				paf.abf.process(MonitorEvent.Action,paf.innerMdi.byApplicationPath(getSelectedAction())[0],null);
				for (String item : acc.keySet()) {			// Extract variable's values from plugin fields
					final String	key = "#"+getPluginId()+"."+item;
					final Object	value = acc.get(item); 
					
					if (value != null) {
						variables.replace(key,value);
					}
				}
			} catch (ContentException e) {
				throw new FlowException("Node ["+getPipeItemName()+"] plugin error: "+e);
			}
			return PipeStepReturnCode.CONTINUE;
		}
	}

	@Override
	public Object getOutgoingValue(Object temp, ContentNodeMetadata meta) throws ContentException {
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
	public void serializeFrame(final PluginSpecific specific) throws IOException {
		if (specific == null) {
			throw new NullPointerException("Plugin specific can't be null");
		}
		else {
			specific.pluginClass = paf.instanceClass.getCanonicalName();
			specific.initialCode = initialCode.getText().trim().isEmpty() ? null : initialCode.getText().trim();
			
//			if (fields.getModel().getSize() > 0) {
//				specific.fields = new MutableContentNodeMetadata[fields.getModel().getSize()];
//				
//				for (int index = 0, maxIndex = fields.getModel().getSize(); index < maxIndex; index++) {
//					specific.fields[index] = (MutableContentNodeMetadata)((PipeLink)fields.getContent()[index]).getMetadata();
//				}
//			}
			
			final URI	actionUri = getSelectedAction();
			
			if (actionUri != null) {
				specific.action = actionUri.toString(); 
			}
		}
	}	
	
	@Override
	public void deserializeFrame(final PluginSpecific specific) throws IOException {
		if (specific == null) {
			throw new NullPointerException("Plugin specific can't be null");
		}
		else {
//			try{
//				final Class<T>			cl = (Class<T>) Class.forName(specific.pluginClass);
//				final Constructor<T>	c = cl.getConstructor(LoggerFacade.class);
//				final T					inst = c.newInstance(logger);
//				
//				this.paf = buildPluginAndForm((FormManager<?,T>)inst);
//				tabs.setTabComponentAt(0,paf.w);
//			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ContentException | LocalizationException e) {
//			} catch (LocalizationException e) {
//				throw new IOException(e.getLocalizedMessage(),e);
//			}
			
//			if (specific.fields != null) {
//				for (MutableContentNodeMetadata item : specific.fields) {
//					for (int index = 0, maxIndex = fields.getModel().getSize(); index < maxIndex; index++) {
//						final PipeLink l1 = 
//						
//						System.err.println("item="+fields.getModel().getElementAt(index));
//					}
//					fields.addContent(new PipeLink(PipeLinkType.DATA_LINK,null,null,this,fields,item,null));
//				}
//			}
			
			if (specific.initialCode != null) {
				initialCode.setText(specific.initialCode);
			}

			if (specific.action != null) {
				for (JRadioButtonWithMeta item : actions) {
					if (item.getNodeMetadata().getApplicationPath() != null && item.getNodeMetadata().getApplicationPath().toString().equals(specific.action)) {
						item.setSelected(true);
						break;
					}
				}
			}
		}
	}	
	
	private void refresh() {
		if (paf != null) {
			paf.w.refresh();
		}
	}

	private URI getSelectedAction() {
		for (JRadioButtonWithMeta item : actions) {
			if (item.isSelected()) {
				return item.getNodeMetadata().getApplicationPath();
			}
		}
		return null;
	}
	
	@OnAction("action:/removeLink")
	private void removeLink() throws LocalizationException {
		if (new JLocalizedOptionPane(localizer).confirm(this,LINK_REMOVE_QUESTION,LINK_REMOVE_TITLE,JOptionPane.QUESTION_MESSAGE,JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			final PipeLink	current = fields.getSelectedValue();
			
			fields.changeContent(fields.getSelectedIndex(),new PipeLink(current.getType(),null,null,current.getTarget(),current.getTargetPoint(),current.getMetadata(),null));
			enableButtons(!fields.isSelectionEmpty() && fields.getSelectedValue().getSource() != null);
		}
	}
	
	private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		prepareTitle(mdi.getRoot().getLabelId(),mdi.getRoot().getTooltipId());
		fieldsTitle.setTitle(localizer.getValue(FIELDS_TITLE));
		fields.setToolTipText(localizer.getValue(FIELDS_TITLE_TT));
		actionsTitle.setTitle(localizer.getValue(ACTIONS_TITLE));
		tabs.setTitleAt(0,localizer.getValue(TABS_PLUGIN_TITLE));
		tabs.setToolTipTextAt(0,localizer.getValue(TABS_PLUGIN_TITLE_TT));
		tabs.setTitleAt(1,localizer.getValue(TABS_META_TITLE));
		tabs.setToolTipTextAt(1,localizer.getValue(TABS_META_TITLE_TT));
		tabs.setTitleAt(2,localizer.getValue(TABS_CODE_TITLE));
		tabs.setToolTipTextAt(2,localizer.getValue(TABS_CODE_TITLE_TT));
		
		for (JRadioButtonWithMeta item : actions) {
			String s = item.getNodeMetadata().getLabelId();
			item.setText(localizer.getValue(item.getNodeMetadata().getLabelId()));
			if (item.getNodeMetadata().getTooltipId() != null) {
				item.setToolTipText(localizer.getValue(item.getNodeMetadata().getTooltipId()));
			}
		}
	}

	private void enableButtons(final boolean buttonsState) {
		((JButton)SwingUtils.findComponentByName(toolbar,PIPE_MENU_REMOVE_LINK)).setEnabled(buttonsState);
	}

	private PluginAndForm<T> buildPluginAndForm(final FormManager<?,?> content) throws SyntaxException, ContentException, LocalizationException, NullPointerException, IllegalArgumentException {
    	final PluginAndForm<T>	paf = new PluginAndForm<>();

    	paf.instanceClass = (Class<T>) content.getClass();
		paf.innerMdi = ContentModelFactory.forAnnotatedClass(paf.instanceClass);
		paf.pluginLocalizer = LocalizerFactory.getLocalizer(paf.innerMdi.getRoot().getLocalizerAssociated());
		paf.contentAccessor = ModelUtils.buildMappedWrapperClassByModel(paf.innerMdi.getRoot(),this.getClass().getPackageName()+"."+paf.innerMdi.getRoot().getName()+uniqueName.getAndIncrement());
    	
    	final PluginProperties	pp = paf.instanceClass.getAnnotation(PluginProperties.class);
    	final PluginProperties	ownPP = this.getClass().getAnnotation(PluginProperties.class);
		
    	if (pp == null) {
    		throw new IllegalArgumentException("Content is not annotated with @PluginProperties"); 
    	}
    	
    	SwingUtils.assignActionListeners(this.toolbar,this);
    	
		try{final FormManager<Object,T>	wrapper = new FormManagerWrapper<>((FormManager<Object,T>)content, ()-> {refresh();}); 
			
			paf.abf = new AutoBuiltForm<T>(paf.innerMdi,localizer,PureLibSettings.INTERNAL_LOADER, (T) content, wrapper);
			
			for (Module m : paf.abf.getUnnamedModules()) {
				paf.instanceClass.getModule().addExports(paf.instanceClass.getPackageName(),m);
			}
			paf.w = new InnerSVGPluginWindow<T>(paf.instanceClass,pp.svgURI(),paf.abf,(src)->{
				try{final Object result = paf.instanceClass.getField(src).get(content);
				
					return result == null ? "" : result.toString();
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e1) {
					return "<"+src+">";
				}
			});

			SwingUtils.assignActionKey(paf.w,SwingUtils.KS_HELP,(e)->{showHelp(e.getActionCommand());},paf.abf.getContentModel().getRoot().getHelpId());

			setSize(new Dimension(pp.width()+ownPP.width(),pp.height()+ownPP.height()));
		} catch (IllegalArgumentException | LocalizationException | NullPointerException |  IOException exc) {
			throw new ContentException(exc);
		}
		
		return paf;
	}
	
	private static class FormManagerWrapper<T> implements FormManager<Object, T> {
		@FunctionalInterface
		private interface Refresher {
			void refresh();
		}		
		
		private final FormManager<Object, T>	delegate;
		private final Refresher					refresher;
		
		private FormManagerWrapper(final FormManager<Object, T> delegate, final Refresher refresher) {
			this.delegate = delegate;
			this.refresher = refresher;
		}

		@Override
		public RefreshMode onField(T inst, Object id, String fieldName, Object oldValue) throws FlowException, LocalizationException {
			final RefreshMode	mode = delegate.onField(inst, id, fieldName, oldValue);
			
			if (mode != RefreshMode.NONE && mode != RefreshMode.REJECT) {
				refresher.refresh();
			}
			return mode;
		}

		@Override
		public RefreshMode onAction(final T inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
			final RefreshMode	mode = delegate.onAction(inst, id, actionName, parameter);
			
			if (mode != RefreshMode.NONE && mode != RefreshMode.REJECT) {
				refresher.refresh();
			}
			return mode;
		}
		
		@Override
		public LoggerFacade getLogger() {
			return delegate.getLogger();
		}
	}
	
	private static class PluginAndForm<T> {
		private InnerSVGPluginWindow<T>		w;
		private AutoBuiltForm<T>			abf;
		private Class<T>					instanceClass; 
		private ContentMetadataInterface	innerMdi;
		private Localizer					pluginLocalizer;
		private Class<Map<String,Object>>	contentAccessor;
	}
}
