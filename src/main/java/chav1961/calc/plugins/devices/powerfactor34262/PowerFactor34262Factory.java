package chav1961.calc.plugins.devices.powerfactor34262;

import java.net.URI;
import java.net.URISyntaxException;

import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.model.Constants;
import chav1961.purelib.model.MutableContentNodeMetadata;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public class PowerFactor34262Factory implements PluginInterface<PowerFactor34262Plugin>{
	private static final String	PLUGIN_NAME = "menu.devices.powerfactor34262"; 
	private static final URI	PLUGIN_URI = URI.create(ContentMetadataInterface.APPLICATION_SCHEME+":"+Constants.MODEL_APPLICATION_SCHEME_ACTION+":/"+PLUGIN_NAME);

	@Override
	public boolean canServe(final URI plugin) {
		return PLUGIN_URI.equals(plugin);
	}

	@Override
	public PowerFactor34262Plugin newIstance(final LoggerFacade facade) {
		return new PowerFactor34262Plugin(facade);
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
		return new MutableContentNodeMetadata(getPluginName()
				, PowerFactor34262Plugin.class
				, Constants.MODEL_NAVIGATION_LEAF_PREFIX+'.'+getPluginName()
				, null
				, PowerFactor34262Plugin.class.getAnnotation(LocaleResource.class).value()
				, PowerFactor34262Plugin.class.getAnnotation(LocaleResource.class).tooltip()
				, PowerFactor34262Plugin.class.getAnnotation(LocaleResource.class).help()
				, null
				, PLUGIN_URI
				, null);
	}
}
