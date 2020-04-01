package chav1961.calc.interfaces;

import java.net.URI;

import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public interface PluginInterface<T> {
	String PLUGIN_SCHEME = "calc";
	
	boolean canServe(URI plugin);
	String getPluginName();
	T newIstance(LoggerFacade facade);
	ContentNodeMetadata getMetadata();
}
