package chav1961.calc.pipe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

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
import chav1961.calc.pipe.ModelItemListContainer.DropAction;
import chav1961.calc.script.ScriptEditor;
import chav1961.calc.utils.InnerSVGPluginWindow;
import chav1961.calc.utils.PipeLink;
import chav1961.calc.utils.PipeLink.PipeLinkType;
import chav1961.calc.utils.PipePluginFrame;
import chav1961.calc.windows.PipeManager;
import chav1961.calc.windows.PipeManagerSerialForm.PluginSpecific;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.enumerations.ContinueMode;
import chav1961.purelib.enumerations.NodeEnterMode;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.model.Constants;
import chav1961.purelib.model.ContentModelFactory;
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

	private static final String					JSON_PIPE_ITEM_INITIAL_CODE = "initialCode";
	
	private final Class<T>						instanceClass; 
	private final ContentMetadataInterface		mdi, innerMdi;
	private final Localizer						localizer, pluginLocalizer;
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
	private final InnerSVGPluginWindow<T>		w;
	private final ScriptEditor					initialCode = new ScriptEditor();
	private final JTabbedPane					tabs = new JTabbedPane();
	private final AutoBuiltForm<T>				abf;
	@LocaleResource(value="chav1961.calc.pipe.container.caption",tooltip="chav1961.calc.pipe.container.caption.tt")
	@Format("9.2pz")
	public float temp = 0;

	public ContainerPipeFrame(final int uniqueId, final PipeManager parent, final Localizer localizer, final FormManager<?,?> content, final ContentMetadataInterface general) throws ContentException {
		super(uniqueId,parent, localizer, ContainerPipeFrame.class, PipeItemType.PLUGIN_ITEM);
		
		try{this.instanceClass = (Class<T>) content.getClass();
			this.mdi = ContentModelFactory.forAnnotatedClass(this.getClass());
			this.innerMdi = ContentModelFactory.forAnnotatedClass(instanceClass);
			this.localizer = LocalizerFactory.getLocalizer(mdi.getRoot().getLocalizerAssociated());
			this.pluginLocalizer = LocalizerFactory.getLocalizer(innerMdi.getRoot().getLocalizerAssociated());
			this.state = new JStateString(localizer);
			this.targetControl = new JControlTarget(mdi.getRoot(),this);
			this.sourceControl = new JControlSource(mdi.getRoot(),this);
			this.fields = new ModelItemListContainer(localizer,this,DropAction.LINK);
			this.toolbar = SwingUtils.toJComponent(general.byUIPath(PIPE_MENU_ROOT),JToolBar.class);
			this.toolbar.setOrientation(JToolBar.VERTICAL);
			this.toolbar.setFloatable(false);
			
	    	final PluginProperties			pp = instanceClass.getAnnotation(PluginProperties.class);
	    	final PluginProperties			ownPP = this.getClass().getAnnotation(PluginProperties.class);
			
	    	SwingUtils.assignActionListeners(this.toolbar,this);
        	
			try{final FormManager<Object,T>	wrapper = new FormManagerWrapper<>((FormManager<Object,T>)content, ()-> {refresh();}); 
				
				abf = new AutoBuiltForm<T>(innerMdi,localizer,(T) content, wrapper);
				
				for (Module m : abf.getUnnamedModules()) {
					instanceClass.getModule().addExports(instanceClass.getPackageName(),m);
				}
				w = new InnerSVGPluginWindow<T>(instanceClass,pp.svgURI(),abf,(src)->{
					try{final Object result = instanceClass.getField(src).get(content);
					
						return result == null ? "" : result.toString();
					} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e1) {
						return "<"+src+">";
					}
				});

				SwingUtils.assignActionKey(w,SwingUtils.KS_HELP,(e)->{showHelp(e.getActionCommand());},abf.getContentModel().getRoot().getHelpId());

				setSize(new Dimension(pp.width()+ownPP.width(),pp.height()+ownPP.height()));
			} catch (IllegalArgumentException | LocalizationException | NullPointerException |  IOException exc) {
				throw new ContentException(exc);
			}
			
			final List<ContentNodeMetadata>	pluginFields = new ArrayList<>();
			final List<ContentNodeMetadata>	pluginActions = new ArrayList<>();
        	
			innerMdi.walkDown((mode,applicationPath,uiPath,node)->{
				if (mode == NodeEnterMode.ENTER) {
					if (URIUtils.canServeURI(applicationPath,URI.create(ContentMetadataInterface.APPLICATION_SCHEME+":"+Constants.MODEL_APPLICATION_SCHEME_FIELD+":/"))) {
						pluginFields.add(node);
					}
					else if (URIUtils.canServeURI(applicationPath,URI.create(ContentMetadataInterface.APPLICATION_SCHEME+":"+Constants.MODEL_APPLICATION_SCHEME_ACTION+":/"))) {
						pluginActions.add(node);
					}
				}
				return ContinueMode.CONTINUE;
			}, innerMdi.getRoot().getUIPath());
			
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
				final JRadioButtonWithMeta			r = new JRadioButtonWithMeta(mcmd,pluginLocalizer,(event,metadata,component,parameters)->true); 
				
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
			metaTab.setDividerLocation((int)(0.8f*pp.width()));
			
			tabs.addTab("",w);
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
						for (int index = 0, maxIndex = controls.size(); index < maxIndex; index++) {
							if (controls.get(index).getMetadata() == ((PipeLink)current).getMetadata()) {
								controls.set(index,(PipeLink)current);
								enableButtons(!fields.isSelectionEmpty() && fields.getSelectedValue().getSource() != null);
								break;
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
		return innerMdi;
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
		return controls.toArray(new PipeLink[controls.size()]);
	}
	
	@Override
	public Object preparePipeItem() throws FlowException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeIncomingValue(Object temp, ContentNodeMetadata meta, Object value) throws ContentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PipeStepReturnCode processPipeStep(final Object temp, final LoggerFacade logger, final boolean ConfirmAll) throws FlowException {
		// TODO Auto-generated method stub
		return PipeStepReturnCode.CONTINUE;
	}

	@Override
	public Object getOutgoingValue(Object temp, ContentNodeMetadata meta) throws ContentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unpreparePipeItem(Object temp) throws FlowException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void serializeFrame(final PluginSpecific specific) throws IOException {
		if (specific == null) {
			throw new NullPointerException("Plugin specific can't be null");
		}
		else {
			specific.pluginClass = instanceClass.getCanonicalName();
		}
	}	
	
	@Override
	public void deserializeFrame(final PluginSpecific specific) throws IOException {
		// pluginClass
	}	
	
	private void refresh() {
		w.refresh();
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
}
