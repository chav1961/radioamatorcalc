package chav1961.calc.environment.pipe.controls;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import chav1961.calc.LocalizationKeys;
import chav1961.calc.environment.PipeParameterWrapper;
import chav1961.calc.interfaces.PipeInstanceControlInterface;
import chav1961.calc.interfaces.PluginInterface;
import chav1961.calc.interfaces.PluginInterface.PluginInstance;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.ui.FormFieldFormat;
import chav1961.purelib.ui.AbstractLowLevelFormFactory.FieldDescriptor;
import chav1961.purelib.ui.swing.SwingUtils;

public class FormulaContent extends JPanel implements LocaleChangeListener, PluginInstance, PipeInstanceControlInterface {
	private static final long serialVersionUID = -3268088997213112095L;

	private final JLabel						nodeComment = new JLabel("");
	private final JTextField					commentControl;
	private final JLabel						nodeFormula = new JLabel("");
	private final JTextField					formulaControl;
	
	private final FormulaNode					owner;
	private final Localizer						localizer;
	private final LoggerFacade					logger;
	private final FieldDescriptor				fdComment;
	private final String						comment = "";
	private final FieldDescriptor				fdFormula;
	private final String						formula = "";
	
	public FormulaContent(final FormulaNode owner, final Localizer localizer, final LoggerFacade logger) throws LocalizationException, NullPointerException, SyntaxException {
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
			this.fdComment = FieldDescriptor.newInstance("comment",new FormFieldFormat("30ms"),this.getClass());
			this.commentControl = (JTextField) SwingUtils.prepareCellEditorComponent(localizer,fdComment,comment);
			this.fdFormula = FieldDescriptor.newInstance("formula",new FormFieldFormat("60ms"),this.getClass());
			this.formulaControl = (JTextField) SwingUtils.prepareCellEditorComponent(localizer,fdFormula,formula);
			
			final JPanel		leftPanel = new JPanel(new GridLayout(2,1,2,2)), rightPanel = new JPanel(new GridLayout(2,1,2,2)); 
			
			leftPanel.add(nodeComment);
			leftPanel.add(nodeFormula);
			rightPanel.add(commentControl);
			commentControl.setColumns(30);
			rightPanel.add(formulaControl);
			formulaControl.setColumns(50);
			
			final SpringLayout	springLayout = new SpringLayout();
			
			setLayout(springLayout);
			add(leftPanel);
			add(rightPanel);
			springLayout.putConstraint(SpringLayout.NORTH,leftPanel,0,SpringLayout.NORTH,this);
			springLayout.putConstraint(SpringLayout.NORTH,rightPanel,0,SpringLayout.NORTH,this);
			springLayout.putConstraint(SpringLayout.WEST,leftPanel,0,SpringLayout.WEST,this);
			springLayout.putConstraint(SpringLayout.EAST,rightPanel,0,SpringLayout.EAST,this);
			springLayout.putConstraint(SpringLayout.WEST,rightPanel,5,SpringLayout.EAST,leftPanel);
			springLayout.putConstraint(SpringLayout.SOUTH,leftPanel,0,SpringLayout.SOUTH,rightPanel);
			
			fillLocalizationStrings();
		}
	}

	@Override
	public JComponent getComponent() {
		return this;
	}

	@Override
	public Dimension getRecommendedSize() {
		return new Dimension(400,50);
	}

	@Override
	public Localizer getLocalizerAssociated() throws LocalizationException {
		return localizer;
	}

	@Override
	public PluginInterface getPluginDescriptor() {
		return owner;
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
	public Object getValue(final FieldDescriptor desc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValue(final FieldDescriptor desc, final Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean execute(String action) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void fillLocalizationStrings() throws LocalizationException {
		nodeComment.setText(localizer.getValue(LocalizationKeys.PIPE_FORMULA_COMMENT));
		commentControl.setToolTipText(localizer.getValue(LocalizationKeys.PIPE_FORMULA_COMMENT_TOOLTIP));
		nodeFormula.setText(localizer.getValue(LocalizationKeys.PIPE_FORMULA_EXPRESION));
		formulaControl.setToolTipText(localizer.getValue(LocalizationKeys.PIPE_FORMULA_EXPRESION_TOOLTIP));
	}

}
