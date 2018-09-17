package chav1961.calc.environment.pipe;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import chav1961.calc.environment.Constants;
import chav1961.calc.interfaces.PipeInterface;
import chav1961.calc.interfaces.PluginInterface;
import chav1961.calc.interfaces.PluginInterface.PluginInstance;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;

class PipeManager implements PipeInterface, LocaleChangeListener {
	private final List<PluginInterface>		pluginsRequired = new ArrayList<>();
	
	private boolean	wasModified = false;
	
	PipeManager(final PipeFactory factory) {
	}

	@Override
	public boolean isModified() {
		return wasModified;
	}

	@Override
	public void setModified(final boolean modificationFlag) {
		this.wasModified = modificationFlag;
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
	public void serialize(final Writer writer) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deserialize(final Reader reader) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Iterable<PluginInterface> getPluginsRequired() {
		return pluginsRequired;
	}

	@Override
	public Iterable<PluginInstance> getPluginInstanceUsed() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public boolean isReadyToPlay() {
		boolean	hasStartNode = false, hasTerminalNode = false;
		
		for (PluginInterface item : getPluginsRequired()) {
			if (Constants.PIPE_START_NODE.equals(item.getPluginId())) {
				hasStartNode = true;
			}
			else if (Constants.PIPE_TERMINAL_NODE.equals(item.getPluginId())) {
				hasTerminalNode = true;
			}
		}
		return hasStartNode && hasTerminalNode;
	}

	@Override
	public void play() throws IOException, FlowException {
		if (!isReadyToPlay()) {
			throw new IllegalStateException("Pipe is not ready yet to play (isReadyToPlay() == false)");
		}
		else {
			// TODO Auto-generated method stub
			
		}
	}

}
