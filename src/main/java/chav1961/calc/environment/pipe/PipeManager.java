package chav1961.calc.environment.pipe;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.Locale;

import chav1961.calc.Application;
import chav1961.calc.interfaces.PipeInterface;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;

class PipeManager implements PipeInterface, LocaleChangeListener {
	
	PipeManager(final PipeFactory factory) {
		
	}
	
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getPipeNameId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPipeCaptionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getPipeLocation() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getComponentsRequired() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPipeTooltipId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPipeHelpId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDefinedField[] getSourceFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDefinedField[] getDestinationFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContentDescription getContentDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void serialize(Writer writer) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deserialize(Reader reader) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isReadyToPlay() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void play() throws IOException {
		// TODO Auto-generated method stub
		
	}

}
