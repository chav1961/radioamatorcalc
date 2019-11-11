package chav1961.calc.plugins.calc.wienbridge;

import java.net.URI;

import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.interfaces.LoggerFacade;

public class WienBridgeFactory implements PluginInterface<WienBridgePlugin>{
	private static final URI	PLUGIN_URI = URI.create(PLUGIN_SCHEME+":action:/WinBridgeCalculator"); 

	@Override
	public boolean canServe(final URI plugin) {
		return URIUtils.canServeURI(plugin, PLUGIN_URI);
	}

	@Override
	public WienBridgePlugin newIstance(final LoggerFacade facade) {
		return new WienBridgePlugin(facade);
	}
}
