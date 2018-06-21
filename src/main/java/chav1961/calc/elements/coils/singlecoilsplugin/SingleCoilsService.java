package chav1961.calc.elements.coils.singlecoilsplugin;

import java.io.Closeable;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.swing.AutoBuiltForm;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.Localizer;

public class SingleCoilsService implements PluginInterface {
	private static final Icon	ICON = new ImageIcon(SingleCoils.class.getResource("SingleCoils.png"));
	
	public SingleCoilsService() {
	}
	
	@Override
	public PluginInstance newInstance(final Localizer localizer, final LoggerFacade logger) throws LocalizationException, SyntaxException, ContentException, IOException {
		return new SingleCoils(localizer,new SingleCoilsCalculator(localizer,logger)); 
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

	private static class SingleCoils extends AutoBuiltForm<SingleCoilsCalculator> implements PluginInstance {
		private static final long 	serialVersionUID = 2615737307529282959L;
		
		private final Localizer	localizer;
		
		public SingleCoils(final Localizer localizer, final LoggerFacade logger) throws NullPointerException, IllegalArgumentException, LocalizationException, SyntaxException, ContentException, IOException {
			this(localizer,new SingleCoilsCalculator(localizer,logger));
		}

		protected SingleCoils(final Localizer localizer, final SingleCoilsCalculator instance) throws NullPointerException, IllegalArgumentException, LocalizationException, SyntaxException, ContentException, IOException {
			super(localizer,SingleCoils.class.getResource("OneLayerCoils.png"),instance,instance);
			this.localizer = localizer;
		}

		@Override
		public JComponent getComponent() {
			return this;
		}

		@Override
		public Localizer getLocalizerAssociated() throws LocalizationException {
			return localizer;
		}
	}	
}
