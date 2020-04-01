package chav1961.calc.plugins.calc.phaseshift;

import java.net.URI;

import chav1961.calc.interfaces.PluginInterface;
import chav1961.calc.plugins.calc.contour.ContourPlugin;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.model.Constants;
import chav1961.purelib.model.MutableContentNodeMetadata;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public class PhaseShiftFactory implements PluginInterface<PhaseShiftPlugin>{
	private static final String	PLUGIN_NAME = "menu.curcuits.phaseshift"; 
	private static final URI	PLUGIN_URI = URI.create(ContentMetadataInterface.APPLICATION_SCHEME+":"+Constants.MODEL_APPLICATION_SCHEME_ACTION+":/"+PLUGIN_NAME);

	@Override
	public boolean canServe(final URI plugin) {
		return PLUGIN_URI.equals(plugin);
	}

	@Override
	public PhaseShiftPlugin newIstance(final LoggerFacade facade) {
		return new PhaseShiftPlugin(facade);
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}

	@Override
	public ContentNodeMetadata getMetadata() {
		return new MutableContentNodeMetadata(getPluginName()
				, PhaseShiftPlugin.class
				, Constants.MODEL_NAVIGATION_LEAF_PREFIX+'.'+getPluginName()
				, null
				, PhaseShiftPlugin.class.getAnnotation(LocaleResource.class).value()
				, PhaseShiftPlugin.class.getAnnotation(LocaleResource.class).tooltip()
				, PhaseShiftPlugin.class.getAnnotation(LocaleResource.class).help()
				, null
				, PLUGIN_URI
				, null);
	}
}
