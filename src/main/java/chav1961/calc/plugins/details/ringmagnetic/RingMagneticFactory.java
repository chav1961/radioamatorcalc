package chav1961.calc.plugins.details.ringmagnetic;

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

public class RingMagneticFactory implements PluginInterface<RingMagneticPlugin>{
	private static final String	PLUGIN_NAME = "menu.details.ringmagnetic"; 
	private static final URI	PLUGIN_URI = URI.create(ContentMetadataInterface.APPLICATION_SCHEME+":"+Constants.MODEL_APPLICATION_SCHEME_ACTION+":/"+PLUGIN_NAME);

	@Override
	public boolean canServe(final URI plugin) {
		return PLUGIN_URI.equals(plugin);
	}

	@Override
	public RingMagneticPlugin newIstance(final LoggerFacade facade) {
		return new RingMagneticPlugin(facade);
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
					, RingMagneticPlugin.class
					, Constants.MODEL_NAVIGATION_LEAF_PREFIX+'.'+getPluginName()
					, null
					, RingMagneticPlugin.class.getAnnotation(LocaleResource.class).value()
					, RingMagneticPlugin.class.getAnnotation(LocaleResource.class).tooltip()
					, RingMagneticPlugin.class.getAnnotation(LocaleResource.class).help()
					, null
					, PLUGIN_URI
					, getClass().getResource("frameIcon.png").toURI());
		} catch (URISyntaxException e) {
			return new MutableContentNodeMetadata(getPluginName()
					, RingMagneticPlugin.class
					, Constants.MODEL_NAVIGATION_LEAF_PREFIX+'.'+getPluginName()
					, null
					, RingMagneticPlugin.class.getAnnotation(LocaleResource.class).value()
					, RingMagneticPlugin.class.getAnnotation(LocaleResource.class).tooltip()
					, RingMagneticPlugin.class.getAnnotation(LocaleResource.class).help()
					, null
					, PLUGIN_URI
					, null);
		}
	}
}
