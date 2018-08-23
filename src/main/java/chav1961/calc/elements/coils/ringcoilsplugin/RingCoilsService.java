package chav1961.calc.elements.coils.ringcoilsplugin;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.SystemErrLoggerFacade;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.swing.AutoBuiltForm;

public class RingCoilsService implements PluginInterface {
	private static final URL	LEFT_ICON_RESOURCE = RingCoils.class.getResource("RingCoils.png");
	private static final URL	MINI_ICON_RESOURCE = RingCoils.class.getResource("RingCoilsIcon.png");
	private static final Icon	ICON = new ImageIcon(MINI_ICON_RESOURCE);

	private RingCoils			inner = null;
	
	public RingCoilsService() {
	}
	
	@Override
	public synchronized PluginInstance newInstance(final Localizer localizer, final LoggerFacade logger) throws LocalizationException, SyntaxException, ContentException, IOException {
		return new RingCoils(localizer,new RingCoilsCalculator(localizer,logger)); 
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
	public Icon getIcon() {
		return ICON;
	}

	@Override
	public synchronized Localizer getLocalizerAssociated(final Localizer parent) throws LocalizationException {
		return parent.getLocalizerById(RingCoilsCalculator.class.getAnnotation(LocaleResourceLocation.class).value()); 
	}

	@Override
	public URL getMiniIconURL() {
		return MINI_ICON_RESOURCE;
	}

	@Override
	public URL getLeftIconURL() {
		return LEFT_ICON_RESOURCE;
	}

	@Override
	public synchronized String[] getUsesIds(final Localizer parent) throws LocalizationException {
		if (inner == null) {
			try{inner = new RingCoils(parent,new SystemErrLoggerFacade());
			} catch (SyntaxException | ContentException | IOException e) {
				throw new LocalizationException(e.getLocalizedMessage(),e);
			}
		}
		return inner.getLabelIds();
	}

	@Override
	public String[] getTagsIds(final Localizer parent) throws LocalizationException {
		return new String[]{"RingCoilsService.tag1","RingCoilsService.tag2","RingCoilsService.tag3","RingCoilsService.tag4"};
	}

	@Override
	public String[] getSeeAlsoIds(final Localizer parent) throws LocalizationException {
		return new String[]{};
	}	
	
	private static class RingCoils extends AutoBuiltForm<RingCoilsCalculator> implements PluginInstance {
		private static final long 	serialVersionUID = 2615737307529282959L;
		
		public RingCoils(final Localizer localizer, final LoggerFacade logger) throws NullPointerException, IllegalArgumentException, LocalizationException, SyntaxException, ContentException, IOException {
			this(localizer,new RingCoilsCalculator(localizer,logger));
		}

		protected RingCoils(final Localizer localizer, final RingCoilsCalculator instance) throws NullPointerException, IllegalArgumentException, LocalizationException, SyntaxException, ContentException, IOException {
			super(localizer,LEFT_ICON_RESOURCE,instance,instance);
		}

		@Override
		public JComponent getComponent() {
			return this;
		}
	}
}
