package chav1961.calc.plugins.calc.phaseshift;

import java.net.URI;

import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.interfaces.LoggerFacade;

public class PhaseShiftFactory implements PluginInterface<PhaseShiftPlugin>{
	private static final URI	PLUGIN_URI = URI.create(PLUGIN_SCHEME+":PhaseShiftCalculator:/"); 

	@Override
	public boolean canServe(final URI plugin) {
		return URIUtils.canServeURI(plugin, PLUGIN_URI);
	}

	@Override
	public PhaseShiftPlugin newIstance(final LoggerFacade facade) {
		return new PhaseShiftPlugin(facade);
	}
}
