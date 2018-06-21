package chav1961.calc.elements.coils.multilayercoilsplugin;

import java.io.Closeable;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import chav1961.calc.interfaces.PluginInterface;
import chav1961.calc.interfaces.PluginInterface.PluginInstance;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.swing.AutoBuiltForm;

public class MultiLayerCoilsService implements PluginInterface {
	private static final Icon	ICON = new ImageIcon(MultiLayerCoils.class.getResource("SingleCoils.png"));
	
	public MultiLayerCoilsService() {
	}
	
	@Override
	public PluginInstance newInstance(final Localizer localizer, final LoggerFacade logger) throws LocalizationException {
		return null;
	}
	
	@Override
	public String getPluginId() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getCaptionId() {
		return getPluginId()+".caption";
	}

	@Override
	public String getToolTipId() {
		return getPluginId()+".tooltip";
	}

	@Override
	public String getHelpId() {
		return getPluginId()+".help";
	}

	@Override
	public String getTagsId() {
		return getPluginId()+".tags";
	}
	
	@Override
	public Icon getIcon() {
		return ICON;
	}

	private static class MultiLayerCoils extends AutoBuiltForm<MultiLayerCoilsCalculator> implements Closeable {
		private static final long 	serialVersionUID = 2615737307529282959L;
		
		public MultiLayerCoils(final Localizer localizer, final LoggerFacade logger) throws NullPointerException, IllegalArgumentException, LocalizationException, SyntaxException, ContentException, IOException {
			this(localizer,new MultiLayerCoilsCalculator(localizer,logger));
		}

		protected MultiLayerCoils(final Localizer localizer, final MultiLayerCoilsCalculator instance) throws NullPointerException, IllegalArgumentException, LocalizationException, SyntaxException, ContentException, IOException {
			super(localizer,instance,instance);
		}
		
	}
}
