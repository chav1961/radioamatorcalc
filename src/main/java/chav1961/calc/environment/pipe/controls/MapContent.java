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

public class MapContent extends JPanel implements LocaleChangeListener, PluginInstance, PipeInstanceControlInterface {
	private static final long serialVersionUID = -1860587528233881285L;
	
	
	private final MapNode				owner;
	private final Localizer				localizer;
	
	public MapContent(final MapNode owner, final Localizer localizer) throws LocalizationException, IllegalArgumentException, NullPointerException, SyntaxException {
		if (owner == null) {
			throw new NullPointerException("Plugin instance owner can't be null");
		}
		else if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else {
			this.owner = owner; 
			this.localizer = localizer; 
			fillLocalizedContent();
		}
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedContent();
	}
	
	private void fillLocalizedContent() throws LocalizationException, IllegalArgumentException {
	}

	@Override
	public JComponent getComponent() {
		return this;
	}

	@Override
	public Dimension getRecommendedSize() {
		return new Dimension(400,30);
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
