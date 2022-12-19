package chav1961.calc.plugins.details.flybacktrans;

import java.net.URI;
import java.net.URISyntaxException;

import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.model.Constants;
import chav1961.purelib.model.MutableContentNodeMetadata;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public class FlybackTransFactory implements PluginInterface<FlybackTransPlugin>{
	private static final String	PLUGIN_NAME = "menu.details.flybacktrans"; 
	private static final URI	PLUGIN_URI = URI.create(ContentMetadataInterface.APPLICATION_SCHEME+":"+Constants.MODEL_APPLICATION_SCHEME_ACTION+":/"+PLUGIN_NAME);

	@Override
	public boolean canServe(final URI plugin) {
		return PLUGIN_URI.equals(plugin);
	}

	@Override
	public FlybackTransPlugin newIstance(final LoggerFacade facade) {
		return new FlybackTransPlugin(facade);
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
					, FlybackTransPlugin.class
					, Constants.MODEL_NAVIGATION_LEAF_PREFIX+'.'+getPluginName()
					, null
					, FlybackTransPlugin.class.getAnnotation(LocaleResource.class).value()
					, FlybackTransPlugin.class.getAnnotation(LocaleResource.class).tooltip()
					, FlybackTransPlugin.class.getAnnotation(LocaleResource.class).help()
					, null
					, PLUGIN_URI
					, getClass().getResource("frameicon.png").toURI());
		} catch (URISyntaxException e) {
			return new MutableContentNodeMetadata(getPluginName()
					, FlybackTransPlugin.class
					, Constants.MODEL_NAVIGATION_LEAF_PREFIX+'.'+getPluginName()
					, null
					, FlybackTransPlugin.class.getAnnotation(LocaleResource.class).value()
					, FlybackTransPlugin.class.getAnnotation(LocaleResource.class).tooltip()
					, FlybackTransPlugin.class.getAnnotation(LocaleResource.class).help()
					, null
					, PLUGIN_URI
					, null);
		}
	}
}
