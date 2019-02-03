package chav1961.calc.environment.pipe.controls;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import chav1961.calc.LocalizationKeys;
import chav1961.calc.interfaces.PipeInstanceControlInterface;
import chav1961.calc.interfaces.PluginInterface;
import chav1961.calc.interfaces.PluginInterface.PluginInstance;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.ui.AbstractLowLevelFormFactory.FieldDescriptor;
import chav1961.purelib.ui.FormFieldFormat;
import chav1961.purelib.ui.swing.SwingUtils;

public class TerminalContent extends JPanel implements LocaleChangeListener, PluginInstance, PipeInstanceControlInterface {
	private static final long serialVersionUID = -1860587528233881285L;
	
	private final JLabel				nodeType = new JLabel("");
	private final JRadioButton			successControl = new JRadioButton();
	private final JRadioButton			failControl = new JRadioButton();
	private final JLabel				nodeFormat = new JLabel("");
	private final JTextField			formatStringControl = null;
	private final JPanel				forList = new JPanel(new BorderLayout());
	private final JList<Object>			formatParametersControl = null;
	private final ButtonGroup			group = new ButtonGroup(); 
	
	private final TerminalNode			owner;
	private final Localizer				localizer;
	private String						format;
	private final FieldDescriptor		fdFormat; 
	private final List<Object>			parameters = new ArrayList<>();
	private final FieldDescriptor		fdParameters; 
	private boolean						success;
	
	public TerminalContent(final TerminalNode owner, final Localizer localizer) throws LocalizationException, IllegalArgumentException, NullPointerException, SyntaxException {
		if (owner == null) {
			throw new NullPointerException("Plugin instance owner can't be null");
		}
		else if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else {
			this.owner = owner; 
			this.localizer = localizer; 
			this.fdFormat = FieldDescriptor.newInstance("format",new FormFieldFormat("10.3ms"),this.getClass()); 
			this.fdParameters = FieldDescriptor.newInstance("parameters",new FormFieldFormat("10.3m"),this.getClass()); 
			group.add(successControl);
			group.add(failControl);
			group.setSelected(failControl.getModel(),true);
//			this.formatStringControl = (JTextField) SwingUtils.prepareCellEditorComponent(localizer,fdFormat,null);
//			this.formatParametersControl = (JList<Object>) SwingUtils.prepareCellEditorComponent(localizer,fdParameters,null);
			
			final SpringLayout	springLayout = new SpringLayout(); 
			final JPanel		forButtons = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
			final JPanel		forLeft = new JPanel(new GridLayout(2,1)), forRight = new JPanel(new GridLayout(2,1));

			forButtons.add(successControl);
			forButtons.add(failControl);
			forLeft.add(nodeType);
			forLeft.add(nodeFormat);
			forRight.add(forButtons);
//			forRight.add(formatStringControl);
//			forList.add(formatParametersControl,BorderLayout.CENTER);
//			formatParametersControl.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			
			setLayout(springLayout);
			add(forLeft);
			add(forRight);
			add(forList);
			springLayout.putConstraint(SpringLayout.NORTH,forLeft,0,SpringLayout.NORTH,this);
			springLayout.putConstraint(SpringLayout.WEST,forLeft,5,SpringLayout.WEST,this);
			springLayout.putConstraint(SpringLayout.EAST,forRight,-5,SpringLayout.EAST,this);
			springLayout.putConstraint(SpringLayout.WEST,forRight,10,SpringLayout.EAST,forLeft);
			springLayout.putConstraint(SpringLayout.SOUTH,forLeft,0,SpringLayout.SOUTH,forRight);
			springLayout.putConstraint(SpringLayout.NORTH,forList,5,SpringLayout.SOUTH,forRight);
			springLayout.putConstraint(SpringLayout.WEST,forList,0,SpringLayout.WEST,this);
			springLayout.putConstraint(SpringLayout.EAST,forList,0,SpringLayout.EAST,this);
			springLayout.putConstraint(SpringLayout.SOUTH,forList,0,SpringLayout.SOUTH,this);
			
			fillLocalizedContent();
		}
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedContent();
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(final boolean success) {
		if (this.success = success) {
			group.setSelected(successControl.getModel(),true);
		}
		else {
			group.setSelected(failControl.getModel(),true);
		}
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(final String format) {
		this.format = format;
		formatStringControl.setText(format);
	}

	public List<Object> getParameters() {
		return parameters;
	}

	private void fillLocalizedContent() throws LocalizationException, IllegalArgumentException {
		nodeType.setText(localizer.getValue(LocalizationKeys.PIPE_TERMINAL_NODE_TYPE));
		successControl.setText(localizer.getValue(LocalizationKeys.PIPE_TERMINAL_NODE_TYPE_SUCCESS));
		failControl.setText(localizer.getValue(LocalizationKeys.PIPE_TERMINAL_NODE_TYPE_FAILED));
		nodeFormat.setText(localizer.getValue(LocalizationKeys.PIPE_TERMINAL_FORMAT));
		forList.setBorder(new TitledBorder(localizer.getValue(LocalizationKeys.PIPE_TERMINAL_PARAMETERS)));
	}

	@Override
	public JComponent getComponent() {
		return this;
	}

	@Override
	public Dimension getRecommendedSize() {
		return new Dimension(400,300);
	}

	@Override
	public PluginInterface getPluginDescriptor() {
		return owner;
	}
	
	@Override
	public Localizer getLocalizerAssociated() throws LocalizationException {
		return localizer;
	}

	@Override
	public void close() {
	}

	@Override
	public Object getValue(final FieldDescriptor desc) {
		return null;
	}

	@Override
	public void setValue(final FieldDescriptor desc, final Object value) {
	}

	@Override
	public boolean execute(final String action) {
		return false;
	}
}
