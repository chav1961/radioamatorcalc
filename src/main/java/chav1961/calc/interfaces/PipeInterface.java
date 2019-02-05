package chav1961.calc.interfaces;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.UUID;

import javax.swing.Icon;

import chav1961.calc.environment.PipeParameterWrapper;
import chav1961.calc.interfaces.PluginInterface.PluginInstance;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.ModifiableEntity;

public interface PipeInterface extends Closeable, ModifiableEntity {
	public interface DataLinkDescription {
		UUID getLinkId();
		PipeParameterWrapper getSource();
		PipeParameterWrapper getTarget();
	}
	
	public interface ControlLinkDescription {
		UUID getLinkId();
		PluginInstance getSource();
		PluginInstance getTarget();
		boolean isReady();
	}
	
	public interface ContentDescription {
		Icon getSourceIcon();
		Icon getTargetIcon();
		PluginInstance getPluginInstance();
		String getNameInPipe();
		Iterable<ControlLinkDescription> getIncomingControlLinks();
		Iterable<DataLinkDescription> getIncomingDataLinks();
	}
	
	void registerPluginInstance(PluginInstance instance);
	void unregisterPluginInstance(PluginInstance instance);
	ContentDescription getContentDescription(PluginInstance instance);
	Iterable<PluginInterface> getPluginsRequired();
	Iterable<PluginInstance> getPluginInstanceUsed();

	void serialize(Writer writer) throws IOException;
	void deserialize(Reader reader) throws IOException;
	
	boolean isReadyToPlay();
	boolean validatePipe(LoggerFacade validationLog);
	void play() throws IOException, FlowException;
}
