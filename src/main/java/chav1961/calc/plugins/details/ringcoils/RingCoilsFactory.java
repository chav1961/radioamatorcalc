package chav1961.calc.plugins.details.ringcoils;

import java.net.URI;
import java.net.URISyntaxException;

import chav1961.calc.interfaces.PluginInterface;
import chav1961.calc.plugins.calc.phaseshift.PhaseShiftPlugin;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.model.Constants;
import chav1961.purelib.model.MutableContentNodeMetadata;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public class RingCoilsFactory implements PluginInterface<RingCoilsPlugin> {
	private static final String	PLUGIN_NAME = "menu.details.ringcoils"; 
	private static final URI	PLUGIN_URI = URI.create(ContentMetadataInterface.APPLICATION_SCHEME+":"+Constants.MODEL_APPLICATION_SCHEME_ACTION+":/"+PLUGIN_NAME);

	@Override
	public boolean canServe(final URI plugin) {
		return PLUGIN_URI.equals(plugin);
	}

	@Override
	public RingCoilsPlugin newIstance(final LoggerFacade facade) {
		return new RingCoilsPlugin(facade);
	}
	
	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}

	@Override
	public URI getPluginDescription() {
		try {
			return this.getClass().getResource("help.cre").toURI();
		} catch (URISyntaxException e) {
			return null;
		}
	}

	@Override
	public ContentNodeMetadata getMetadata() {
		try {
			return new MutableContentNodeMetadata(getPluginName()
					, RingCoilsPlugin.class
					, Constants.MODEL_NAVIGATION_LEAF_PREFIX+'.'+getPluginName()
					, null
					, RingCoilsPlugin.class.getAnnotation(LocaleResource.class).value()
					, RingCoilsPlugin.class.getAnnotation(LocaleResource.class).tooltip()
					, RingCoilsPlugin.class.getAnnotation(LocaleResource.class).help()
					, null
					, PLUGIN_URI
					, getClass().getResource("frameicon.png").toURI());
		} catch (URISyntaxException e) {
			return new MutableContentNodeMetadata(getPluginName()
					, RingCoilsPlugin.class
					, Constants.MODEL_NAVIGATION_LEAF_PREFIX+'.'+getPluginName()
					, null
					, RingCoilsPlugin.class.getAnnotation(LocaleResource.class).value()
					, RingCoilsPlugin.class.getAnnotation(LocaleResource.class).tooltip()
					, RingCoilsPlugin.class.getAnnotation(LocaleResource.class).help()
					, null
					, PLUGIN_URI
					, null);
		}
	}
}
