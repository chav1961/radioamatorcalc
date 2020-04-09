package chav1961.calc.pipe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

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
import chav1961.calc.utils.PipeLink;
import chav1961.calc.utils.PipePluginFrame;
import chav1961.calc.windows.PipeManager;
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
	@LocaleResource(value="chav1961.calc.plugins.calc.contour.inductanñe",tooltip="chav1961.calc.plugins.calc.contour.inductanñe.tt")
	@Format("9.2pz")
	public float temp = 0;
	
	public TerminalPipeFrame(final PipeManager parent,final Localizer localizer, final ContentNodeMetadata terminal, final ContentMetadataInterface general) throws ContentException {
		super(parent,localizer,TerminalPipeFrame.class,PipeItemType.TERMINAL_ITEM);
		if (terminal == null) {
			throw new NullPointerException("Initial metadata can't be null");
		}
		else {
			try{this.mdi = ContentModelFactory.forAnnotatedClass(this.getClass());
				this.localizer = LocalizerFactory.getLocalizer(mdi.getRoot().getLocalizerAssociated());
				this.state = new JStateString(localizer);
				this.targetControl = new JControlTarget(terminal,this);
				this.fields = new ModelItemListContainer(localizer,this);
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
	public PipeLink[] getLinks() {
		return links.toArray(new PipeLink[links.size()]);
	}
	
	public void addTargetField(final PipeLink metadata) {
		fields.addContent(metadata);
	}
	
	public PipeLink[] getTargetFields() {
		return fields.getContent();
	}

	public void addTargetControl(final PipeLink control) {
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
	
	public ContentNodeMetadata[] getTargetControls() {
		return controls.toArray(new ContentNodeMetadata[controls.size()]);
	}	
	
	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings(oldLocale,newLocale);
	}

	@OnAction("action:/removeField")
	private void removeField() throws LocalizationException {
		if (new JLocalizedOptionPane(localizer).confirm(this,FIELDS_REMOVE_QUESTION,FIELDS_REMOVE_TITLE,JOptionPane.QUESTION_MESSAGE,JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			fields.removeContent(fields.getSelectedValue());
		}
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
		terminalLabel.setText(localizer.getValue(MESSAGE_TITLE));
		terminalMessage.setToolTipText(localizer.getValue(MESSAGE_TITLE_TT));
		terminalFailure.setText(localizer.getValue(FAILURE_TITLE));
		terminalFailure.setToolTipText(localizer.getValue(FAILURE_TITLE_TT));
	}

	private void enableButtons(final boolean buttonsState) {
		((JButton)SwingUtils.findComponentByName(toolbar,PIPE_MENU_REMOVE_FIELD)).setEnabled(buttonsState);
	}
}
