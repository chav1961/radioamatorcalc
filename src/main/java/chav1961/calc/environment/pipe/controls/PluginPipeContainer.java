package chav1961.calc.environment.pipe.controls;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JPanel;

import chav1961.calc.environment.Constants;
import chav1961.calc.interfaces.PipeInstanceControlInterface;
import chav1961.calc.interfaces.PluginInterface;
import chav1961.calc.interfaces.PluginInterface.PluginInstance;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;

public class PluginPipeContainer extends JPanel implements LocaleChangeListener, PluginInstance, PipeInstanceControlInterface {
	private static final long 		serialVersionUID = -3089113084663928823L;
	private final PluginInstance 	instance;
	
	public PluginPipeContainer(final Localizer localizer, final PluginInstance instance) throws LocalizationException, IllegalArgumentException {
		if (instance == null) {
			throw new NullPointerException("Plugin instance can't be null");
		}
		else {
			this.instance = instance;
			setLayout(new BorderLayout());
			add(instance.getComponent(),BorderLayout.CENTER);
			add(new BottomState(instance.getLocalizerAssociated(),Constants.LEFT_ORDINAL,Constants.RIGHT_ORDINAL));
		}
	}
	
	@Override
	public boolean execute(final String action) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PluginInterface getPluginDescriptor() {
		return instance.getPluginDescriptor();
	}

	@Override
	public JComponent getComponent() {
		return this;
	}

	@Override
	public Dimension getRecommendedSize() {
		final Dimension	inner = instance.getRecommendedSize();
		
		return new Dimension(inner.width,inner.height+25);
	}

	@Override
	public Localizer getLocalizerAssociated() throws LocalizationException {
		return instance.getLocalizerAssociated();
	}

	@Override
	public void close() {
		instance.close();
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		instance.localeChanged(oldLocale, newLocale);
	}
}
