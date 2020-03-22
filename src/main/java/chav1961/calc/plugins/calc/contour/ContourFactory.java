package chav1961.calc.plugins.calc.contour;

import java.net.URI;

import javax.swing.text.html.parser.ContentModel;

import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;

public class ContourFactory implements PluginInterface<ContourPlugin>{
	private static final URI	PLUGIN_URI = URI.create(ContentMetadataInterface.APPLICATION_SCHEME+":"+PLUGIN_SCHEME+":/ContourCalculator"); 

	@Override
	public boolean canServe(final URI plugin) {
		return URIUtils.canServeURI(plugin, PLUGIN_URI);
	}

	@Override
	public ContourPlugin newIstance(final LoggerFacade facade) {
		return new ContourPlugin(facade);
	}
}
