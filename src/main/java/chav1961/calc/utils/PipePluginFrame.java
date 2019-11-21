package chav1961.calc.utils;

import java.awt.BorderLayout;
import java.util.Locale;

import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;

public abstract class PipePluginFrame<T> extends InnerFrame<T> {
	private static final long serialVersionUID = 1L;

	public PipePluginFrame(final T instance) throws ContentException {
		super(instance);
		setLayout(new BorderLayout(5,5));
	}

	@Override public abstract void localeChanged(Locale oldLocale, Locale newLocale) throws LocalizationException;
	public abstract LoggerFacade getLogger();
}
