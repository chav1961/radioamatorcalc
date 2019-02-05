package chav1961.calc.environment.pipe.controls;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import chav1961.calc.LocalizationKeys;
import chav1961.calc.environment.PipeParameterWrapper;
import chav1961.calc.environment.pipe.SelfDefinedPipeParametersModel;
import chav1961.calc.interfaces.PipeInstanceControlInterface;
import chav1961.calc.interfaces.PluginInterface;
import chav1961.calc.interfaces.PluginInterface.PluginInstance;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.ui.interfacers.FormManager;
import chav1961.purelib.ui.interfacers.RefreshMode;
import chav1961.purelib.ui.swing.SwingUtils;

public class StartContent extends JPanel implements LocaleChangeListener, PluginInstance, FormManager<Integer,PipeParameterWrapper>, PipeInstanceControlInterface {
	private static final long serialVersionUID = -3268088997213112095L;
	private static final List<PipeParameterWrapper>	EMPTY_CONTENT = new ArrayList<>();

	private final JLabel							nodeComment = new JLabel("");
	private final JTextField						commentControl = null;
	private final JPanel							forList = new JPanel(new BorderLayout());
	private final JComponent						mteComponent = null;
	private final List<PipeParameterWrapper>		content = new ArrayList<>();
	private final SelfDefinedPipeParametersModel	model = null;
	private final ButtonGroup						group = new ButtonGroup(); 
	
	private final StartNode 						owner;
	private final Localizer							localizer;
	private final LoggerFacade						logger;
	private String									comment = "";
	
	public StartContent(final StartNode owner, final Localizer localizer, final LoggerFacade logger) throws LocalizationException, NullPointerException, SyntaxException, ContentException {
		if (owner == null) {
			throw new NullPointerException("Plugin instance owner can't be null");
		}
		else if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.owner = owner; 
			this.localizer = localizer; 
			this.logger = logger; 
//			this.commentControl = (JTextField) SwingUtils.prepareCellEditorComponent(localizer,fdComment,comment);
//			this.mte = new MicroTableEditor(localizer,EditorRepresentation.EDITED_LIST);
//			this.model = new SelfDefinedPipeParametersModel(content,(key)->{return createParameter();});
//			this.mteComponent = mte.build(this,model,null,new String[]{"pluginFieldName","pluginFieldtype"});
			
			final JPanel 		commentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			
			setLayout(new BorderLayout());
			commentPanel.add(nodeComment);
//			commentPanel.add(commentControl);
	//		commentControl.setColumns(30);
			
			add(commentPanel,BorderLayout.NORTH);
		//	add(mteComponent,BorderLayout.CENTER);
			
			fillLocalizationStrings();
		}
	}
	
	@Override
	public JComponent getComponent() {
		return this;
	}

	@Override
	public Dimension getRecommendedSize() {
		return new Dimension(450,400);
	}

	@Override
	public PluginInterface getPluginDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Localizer getLocalizerAssociated() throws LocalizationException {
		return localizer;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizationStrings();
	}

	@Override
	public RefreshMode onRecord(final Action action, final PipeParameterWrapper oldRecord, final Integer oldId, final PipeParameterWrapper newRecord, final Integer newId) throws FlowException, LocalizationException {
		// TODO Auto-generated method stub
		switch (action) {
			case CHANGE		:
				return RefreshMode.RECORD_ONLY;
			case CHECK		:
				final Set<String>	names = new HashSet<>();
				
				for (PipeParameterWrapper item : content) {
					if (names.contains(item.getPluginFieldName())) {
						getLogger().message(Severity.error,LocalizationKeys.PIPE_START_MESSAGE_DUPLICATE_FIELD,item.getPluginFieldName());
						return RefreshMode.REJECT;
					}
					else {
						names.add(item.getPluginFieldName());
					}
				}
				return RefreshMode.NONE;
			case DELETE		:
				return RefreshMode.TOTAL;
			case DUPLICATE	:
				return RefreshMode.TOTAL;
			case INSERT		:
				return RefreshMode.TOTAL;
			default:
				throw new UnsupportedOperationException("Action ["+action+"] is not supported yet");
		}
	}

	@Override
	public RefreshMode onField(final PipeParameterWrapper inst, final Integer id, final String fieldName, final Object oldValue) throws FlowException, LocalizationException {
		// TODO Auto-generated method stub
		return RefreshMode.FIELD_ONLY;
	}

	@Override
	public RefreshMode onAction(final PipeParameterWrapper inst, final Integer id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		return RefreshMode.NONE;
	}

	@Override
	public LoggerFacade getLogger() {
		return logger;
	}

	private void fillLocalizationStrings() throws LocalizationException, IllegalArgumentException {
		nodeComment.setText(localizer.getValue(LocalizationKeys.PIPE_START_COMMENT));
		if (mteComponent instanceof LocaleChangeListener) {
			((LocaleChangeListener)mteComponent).localeChanged(localizer.currentLocale().getLocale(),localizer.currentLocale().getLocale());
		}
	}

	@Override
	public boolean execute(final String action) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private PipeParameterWrapper createParameter() {
		return new PipeParameterWrapper(owner.getPluginId(),"","parameter",String.class){
			@Override
			public String getPluginInstanceName() {
				return StartContent.this.getName();
			}
		};
	}
}
