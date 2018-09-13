package chav1961.calc.environment.pipe.controls;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import chav1961.calc.environment.pipe.PipeParameterWrapper;
import chav1961.calc.interfaces.PluginInterface.PluginInstance;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.ui.FormFieldFormat;
import chav1961.purelib.ui.AbstractLowLevelFormFactory.FieldDescriptor;
import chav1961.purelib.ui.swing.SwingUtils;

public class FormulaContent extends JPanel implements LocaleChangeListener, PluginInstance {
	private static final long serialVersionUID = -3268088997213112095L;

	private final JLabel						nodeComment = new JLabel("");
	private final JTextField					commentControl;
	private final JPanel						forList = new JPanel(new BorderLayout());
	private final JList<Object>					parametersControl;
	private final ButtonGroup					group = new ButtonGroup(); 
	
	private final Localizer						localizer;
	private FieldDescriptor						fdComment;
	private String								comment = "";
	private FieldDescriptor						fdParameters;
	private final List<PipeParameterWrapper>	parameters = new ArrayList<>();
	
	public FormulaContent(final Localizer localizer) throws LocalizationException, IllegalArgumentException, NullPointerException, SyntaxException {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else {
			this.localizer = localizer; 
			this.fdComment = FieldDescriptor.newInstance("comment",new FormFieldFormat("30ms"),this.getClass());
			this.commentControl = (JTextField) SwingUtils.prepareCellEditorComponent(localizer,fdComment,comment);
			this.fdParameters = FieldDescriptor.newInstance("comment",new FormFieldFormat("30ms"),this.getClass());
			this.parametersControl = (JList)SwingUtils.prepareCellEditorComponent(localizer,fdParameters,parameters);
			fillLocalizationStrings();
		}
	}

	@Override
	public JComponent getComponent() {
		return this;
	}

	@Override
	public Dimension getRecommendedSize() {
		return new Dimension(200,200);
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

	private void fillLocalizationStrings() {
		// TODO Auto-generated method stub
		
	}

}
