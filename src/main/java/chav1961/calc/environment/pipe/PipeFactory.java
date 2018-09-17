package chav1961.calc.environment.pipe;

import java.io.InputStream;
import java.io.Reader;

import chav1961.calc.Application;
import chav1961.calc.interfaces.PipeInterface;
import chav1961.purelib.i18n.interfaces.Localizer;

public class PipeFactory {
	private final Application	application;
	private final Localizer		localizer;
	
	public PipeFactory(final Application application, final Localizer localizer) {
		if (application == null) {
			throw new NullPointerException("Application can't be null");
		}
		else if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else {
			this.application = application;
			this.localizer = localizer;
		}
	}
	
	public PipeInterface newPipe() {
		return null;
	}

	public PipeInterface loadPipe(final Reader pipeDescriptor) {
		return null;
	}
}
