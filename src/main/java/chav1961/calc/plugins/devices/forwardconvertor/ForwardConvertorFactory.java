package chav1961.calc.plugins.devices.forwardconvertor;

import java.net.URI;
import java.net.URISyntaxException;

import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.model.Constants;
import chav1961.purelib.model.MutableContentNodeMetadata;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public class ForwardConvertorFactory implements PluginInterface<ForwardConvertorPlugin>{
	private static final String	PLUGIN_NAME = "menu.devices.forwardconvertor"; 
	private static final URI	PLUGIN_URI = URI.create(ContentMetadataInterface.APPLICATION_SCHEME+":"+Constants.MODEL_APPLICATION_SCHEME_ACTION+":/"+PLUGIN_NAME);

	@Override
	public boolean canServe(final URI plugin) {
		return PLUGIN_URI.equals(plugin);
	}

	@Override
	public ForwardConvertorPlugin newIstance(final LoggerFacade facade) {
		return new ForwardConvertorPlugin(facade);
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
					, ForwardConvertorPlugin.class
					, Constants.MODEL_NAVIGATION_LEAF_PREFIX+'.'+getPluginName()
					, null
					, ForwardConvertorPlugin.class.getAnnotation(LocaleResource.class).value()
					, ForwardConvertorPlugin.class.getAnnotation(LocaleResource.class).tooltip()
					, ForwardConvertorPlugin.class.getAnnotation(LocaleResource.class).help()
					, null
					, PLUGIN_URI
					, getClass().getResource("frameIcon.png").toURI());
		} catch (URISyntaxException e) {
			return new MutableContentNodeMetadata(getPluginName()
					, ForwardConvertorPlugin.class
					, Constants.MODEL_NAVIGATION_LEAF_PREFIX+'.'+getPluginName()
					, null
					, ForwardConvertorPlugin.class.getAnnotation(LocaleResource.class).value()
					, ForwardConvertorPlugin.class.getAnnotation(LocaleResource.class).tooltip()
					, ForwardConvertorPlugin.class.getAnnotation(LocaleResource.class).help()
					, null
					, PLUGIN_URI
					, null);
		}
	}
}
