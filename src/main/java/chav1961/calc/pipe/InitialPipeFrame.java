package chav1961.calc.pipe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.calc.pipe.ModelContentChangeListener.ChangeType;
import chav1961.calc.utils.PipePluginFrame;
import chav1961.calc.windows.PipeManager;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.growablearrays.GrowableCharArray;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.FieldFormat;
import chav1961.purelib.model.MutableContentNodeMetadata;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.swing.AutoBuiltForm;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.interfaces.OnAction;
import chav1961.purelib.ui.swing.useful.JContentMetadataEditor;
import chav1961.purelib.ui.swing.useful.JDialogContainer;
import chav1961.purelib.ui.swing.useful.JStateString;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.Application/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="chav1961.calc.pipe.initial.caption",tooltip="chav1961.calc.pipe.initial.caption.tt",help="help.aboutApplication")
@PluginProperties(resizable=true,width=500,height=180,pluginIconURI="initialFrameIcon.png",desktopIconURI="initialDesktopIcon.png")
public class InitialPipeFrame extends PipePluginFrame<InitialPipeFrame> {
	private static final 					long serialVersionUID = 1L;
	private static final String				FIELDS_TITLE = "chav1961.calc.pipe.initial.fields"; 
	private static final String				FIELDS_TITLE_TT = "chav1961.calc.pipe.initial.fields.tt"; 
	private static final String				FIELDS_ADD_TITLE = "chav1961.calc.pipe.initial.fields.add.caption"; 
	private static final String				FIELDS_ADD_TITLE_TT = "chav1961.calc.pipe.initial.fields.add.caption.tt";
	
	private static final URI				PIPE_MENU_ROOT = URI.create("ui:/model/navigation.top.initial.toolbar");	
	private static final String				PIPE_MENU_ADD_FIELD = "chav1961.calc.pipe.initial.toolbar.addfield";	
	private static final String				PIPE_MENU_REMOVE_FIELD = "chav1961.calc.pipe.initial.toolbar.removefield";	
	private static final String				PIPE_MENU_EDIT_FIELD = "chav1961.calc.pipe.initial.toolbar.editfield";	
	
	private final ContentMetadataInterface	mdi;
	private final Localizer					localizer;
	private final JStateString				state;
	private final JControlSource			sourceControl;
	private final List<ContentNodeMetadata>	controls = new ArrayList<>();
	private final ModelItemListContainer	fields;
	private final TitledBorder				fieldsTitle = new TitledBorder(new LineBorder(Color.BLACK),"");
	private final JToolBar					toolbar;
	
	@LocaleResource(value="chav1961.calc.plugins.calc.contour.inductanñe",tooltip="chav1961.calc.plugins.calc.contour.inductanñe.tt")
	@Format("9.2pz")
	public float temp = 0;
	
	public InitialPipeFrame(final PipeManager parent, final Localizer localizer, final ContentNodeMetadata initial, final ContentMetadataInterface general) throws ContentException {
		super(parent,localizer,InitialPipeFrame.class,PipeItemType.INITIAL_ITEM);
		if (initial == null) {
			throw new NullPointerException("Initial metadata can't be null");
		}
		else if (general == null) {
			throw new NullPointerException("Genetal metadata can't be null");
		}
		else {
			try{this.mdi = ContentModelFactory.forAnnotatedClass(this.getClass());
				this.localizer = LocalizerFactory.getLocalizer(mdi.getRoot().getLocalizerAssociated());
				this.state = new JStateString(localizer);
				this.sourceControl = new JControlSource(initial);
				this.fields = new ModelItemListContainer(false);
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
				
				final JScrollPane	pane = new JScrollPane(fields); 
				
				pane.setBorder(fieldsTitle);
				pane.setPreferredSize(new Dimension(props.width(),props.height()));
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
							controls.add((ContentNodeMetadata)current);
							break;
						case REMOVED	:
							controls.remove((ContentNodeMetadata)current);
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

	public void addSourceField(final ContentNodeMetadata metadata) {
		fields.addContent(metadata);
	}
	
	public ContentNodeMetadata[] getSourceFields() {
		return fields.getContent();
	}

	public void addSourceControl(final ContentNodeMetadata control) {
		if (control == null) {
			throw new NullPointerException("Control to add can't be null");
		}
		else {
			controls.add(control);
			refreshList();
		}
	}
	
	public void removeSourceControl(final ContentNodeMetadata control) {
		if (control == null) {
			throw new NullPointerException("Control to remove can't be null");
		}
		else {
			controls.remove(control);
			refreshList();
		}
	}
	
	public ContentNodeMetadata[] getSourceControls() {
		return controls.toArray(new ContentNodeMetadata[controls.size()]);
	}	
	
	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings(oldLocale,newLocale);
	}

	@OnAction("action:/addField")
	private void addField() throws LocalizationException, ContentException {
		final ContentNodeMetadata		meta = new MutableContentNodeMetadata("name",String.class,"name",null,"testSet1","testSet2","testSet3",new FieldFormat(String.class),URI.create(ContentMetadataInterface.APPLICATION_SCHEME+":/name"), null); 
		final JContentMetadataEditor	ed = new JContentMetadataEditor(localizer);

		ed.setPreferredSize(new Dimension(350,350));
		ed.setValue(meta);
		
		final JDialogContainer<Object,Enum<?>,JContentMetadataEditor>	dlg = new JDialogContainer<Object,Enum<?>,JContentMetadataEditor>(localizer,(JFrame)null,FIELDS_ADD_TITLE,ed);
		
		if (dlg.showDialog()) {
			ContentNodeMetadata temp = ed.getContentNodeMetadataValue(); 
			fields.addContent(temp);
		}
	}

	@OnAction("action:/removeField")
	private void removeField() throws LocalizationException {
		// TODO:
	}

	@OnAction("action:/editField")
	private void editField() throws LocalizationException, ContentException {
		final ContentNodeMetadata		meta = fields.getSelectedValue(); 
		final JContentMetadataEditor	ed = new JContentMetadataEditor(localizer);

		ed.setPreferredSize(new Dimension(350,350));
		ed.setValue(meta);
		
		final JDialogContainer<Object,Enum<?>,JContentMetadataEditor>	dlg = new JDialogContainer<Object,Enum<?>,JContentMetadataEditor>(localizer,(JFrame)null,FIELDS_ADD_TITLE,ed);
		
		if (dlg.showDialog()) {
			fields.changeContent(ed.getContentNodeMetadataValue());
		}
	}
	
	protected void showHelp(final String helpId) {
		try{SwingUtils.showCreoleHelpWindow(this,
				URIUtils.convert2selfURI(new GrowableCharArray<>(false).append(localizer.getContent(helpId)).extract(),"UTF-8")
			);
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
	}

	private void enableButtons(final boolean buttonsState) {
		((JButton)SwingUtils.findComponentByName(toolbar,PIPE_MENU_REMOVE_FIELD)).setEnabled(buttonsState);
		((JButton)SwingUtils.findComponentByName(toolbar,PIPE_MENU_EDIT_FIELD)).setEnabled(buttonsState);
	}

	private void refreshList() {
		// TODO Auto-generated method stub
		
	}

}
