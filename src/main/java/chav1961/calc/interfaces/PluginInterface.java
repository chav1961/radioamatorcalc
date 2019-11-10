package chav1961.calc.interfaces;

import java.net.URI;

import chav1961.purelib.basic.interfaces.LoggerFacade;

public interface PluginInterface<T> {
	String PLUGIN_SCHEME = "calc";
	
	boolean canServe(URI plugin);
	T newIstance(LoggerFacade facade);
}
