package chav1961.calc.environment.pipe.controls;


import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import chav1961.calc.LocalizationKeys;
import chav1961.calc.interfaces.PipeInstanceControlInterface;
import chav1961.calc.interfaces.PluginInterface;
import chav1961.calc.interfaces.PluginInterface.PluginInstance;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.ui.swing.SwingUtils;

public class SwitchContent extends JPanel implements LocaleChangeListener, PluginInstance, PipeInstanceControlInterface {
	private static final long serialVersionUID = -3268088997213112095L;

	private final JLabel						nodeComment = new JLabel("");
	private final JTextField					commentControl = null;
	private final JLabel						nodeExpression = new JLabel("");
	private final JTextField					expressionControl = null;
	private final JCheckBox						askBeforeControl = null;
	
	private final SwitchNode 					owner; 
	private final Localizer						localizer;
	private String								comment = "";
	private String								expression = "";
	private boolean								askBefore = false;
	
	public SwitchContent(final SwitchNode owner, final Localizer localizer) throws LocalizationException, IllegalArgumentException, NullPointerException, SyntaxException {
		if (owner == null) {
			throw new NullPointerException("Plugin instance owner can't be null");
		}
		else if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else {
			this.owner = owner; 
			this.localizer = localizer; 
//			this.commentControl = (JTextField) SwingUtils.prepareCellEditorComponent(localizer,fdComment,comment);
	//		this.expressionControl = (JTextField) SwingUtils.prepareCellEditorComponent(localizer,fdExpression,expression);
		//	this.askBeforeControl = (JCheckBox) SwingUtils.prepareCellEditorComponent(localizer,fdAskBefore,askBefore);
			
			final JPanel		leftPanel = new JPanel(new GridLayout(2,1,2,2)), rightPanel = new JPanel(new GridLayout(2,1,2,2));
			
			leftPanel.add(nodeComment);
			leftPanel.add(nodeExpression);
//			rightPanel.add(commentControl);
	//		rightPanel.add(expressionControl);
			
			final SpringLayout	springLayout = new SpringLayout(); 
			
			setLayout(springLayout);
			add(leftPanel);
			add(rightPanel);
//			add(askBeforeControl);
			springLayout.putConstraint(SpringLayout.NORTH,leftPanel,0,SpringLayout.NORTH,this);
			springLayout.putConstraint(SpringLayout.NORTH,rightPanel,0,SpringLayout.NORTH,this);
			springLayout.putConstraint(SpringLayout.WEST,leftPanel,0,SpringLayout.WEST,this);
			springLayout.putConstraint(SpringLayout.EAST,rightPanel,0,SpringLayout.EAST,this);
			springLayout.putConstraint(SpringLayout.WEST,rightPanel,5,SpringLayout.EAST,leftPanel);
			springLayout.putConstraint(SpringLayout.SOUTH,leftPanel,0,SpringLayout.SOUTH,rightPanel);
//			springLayout.putConstraint(SpringLayout.NORTH,askBeforeControl,5,SpringLayout.SOUTH,rightPanel);
	//		springLayout.putConstraint(SpringLayout.WEST,askBeforeControl,0,SpringLayout.WEST,this);
		//	springLayout.putConstraint(SpringLayout.EAST,askBeforeControl,0,SpringLayout.EAST,this);
			
			fillLocalizationStrings();
		}
	}

	@Override
	public JComponent getComponent() {
		return this;
	}

	@Override
	public Dimension getRecommendedSize() {
		return new Dimension(450,80);
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
		// TODO Auto-generated method stub
	}


	@Override
	public void localeChanged(Locale oldLocale, Locale newLocale) throws LocalizationException {
		fillLocalizationStrings();
	}

	@Override
	public boolean execute(final String action) {
		// TODO Auto-generated method stub
		return false;
	}

	
	private void fillLocalizationStrings() throws LocalizationException {
		nodeComment.setText(localizer.getValue(LocalizationKeys.PIPE_SWITCH_COMMENT));
		commentControl.setToolTipText(localizer.getValue(LocalizationKeys.PIPE_SWITCH_COMMENT_TOOLTIP));
		nodeExpression.setText(localizer.getValue(LocalizationKeys.PIPE_SWITCH_EXPRESION));
		expressionControl.setToolTipText(localizer.getValue(LocalizationKeys.PIPE_SWITCH_EXPRESION_TOOLTIP));
		askBeforeControl.setText(localizer.getValue(LocalizationKeys.PIPE_SWITCH_ASKBEFORE));
		askBeforeControl.setToolTipText(localizer.getValue(LocalizationKeys.PIPE_SWITCH_ASKBEFORE_TOOLTIP));
	}

}
