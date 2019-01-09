package chav1961.calc.schemes.stepdown.tps40200plugin;


import java.awt.Dimension;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import chav1961.calc.environment.Utils;
import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.SystemErrLoggerFacade;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.swing.AutoBuiltForm;

public class TPS40200Service implements PluginInterface {
	private static final URL		LEFT_ICON_RESOURCE = TPS40200.class.getResource("TPS40200.png");
	private static final URL		MINI_ICON_RESOURCE = TPS40200.class.getResource("TPS40200Icon.png");
	private static final Icon		ICON = new ImageIcon(MINI_ICON_RESOURCE);
	private static final String[]	RECOMMENDED_PATH = {"menu.schemes","stepdown", "TPS40200"};

	private TPS40200				inner = null;
	
	public TPS40200Service() {
	}
	
	@Override
	public synchronized PluginInstance newInstance(final Localizer localizer, final LoggerFacade logger) throws LocalizationException, SyntaxException, ContentException, IOException {
		return new TPS40200(this,localizer,new TPS40200Calculator(localizer,logger)); 
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
		Localizer	result = parent.getLocalizerById(TPS40200Calculator.class.getAnnotation(LocaleResourceLocation.class).value());
		
		if (result == null) {
			return Utils.attachLocalizer(parent,URI.create(TPS40200Calculator.class.getAnnotation(LocaleResourceLocation.class).value()));
		}
		else {
			return result; 
		}
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
	public String[] getRecommendedNavigationPath() {
		return RECOMMENDED_PATH;
	}
	
	@Override
	public synchronized String[] getUsesIds(final Localizer parent) throws LocalizationException {
		if (inner == null) {
			try{inner = new TPS40200(this,parent,new SystemErrLoggerFacade());
			} catch (ContentException | IOException e) {
				throw new LocalizationException(e.getLocalizedMessage(),e);
			}
		}
		return inner.getModifiableLabelIds();
	}

	@Override
	public String[] getTagsIds(final Localizer parent) throws LocalizationException {
		return new String[]{"TPS40200Service.tag1","TPS40200Service.tag2"};
	}

	@Override
	public String[] getSeeAlsoIds(final Localizer parent) throws LocalizationException {
		return Utils.extractFormulas(TPS40200Calculator.class);
	}	
	
	private static class TPS40200 extends AutoBuiltForm<TPS40200Calculator> implements PluginInstance {
		private static final long 		serialVersionUID = 2615737307529282959L;
		private static final Dimension	RECOMMENDED_SIZE = new Dimension(450,360);
		
		private final PluginInterface 	owner; 
		
		public TPS40200(final PluginInterface owner, final Localizer localizer, final LoggerFacade logger) throws NullPointerException, IllegalArgumentException, LocalizationException, SyntaxException, ContentException, IOException {
			this(owner,localizer,new TPS40200Calculator(localizer,logger));
		}

		protected TPS40200(final PluginInterface owner, final Localizer localizer, final TPS40200Calculator instance) throws NullPointerException, IllegalArgumentException, LocalizationException, SyntaxException, ContentException, IOException {
			super(localizer,LEFT_ICON_RESOURCE,instance,instance);
			this.owner = owner;
		}

		@Override
		public JComponent getComponent() {
			return this;
		}

		@Override
		public Dimension getRecommendedSize() {
			return RECOMMENDED_SIZE;
		}

		@Override
		public PluginInterface getPluginDescriptor() {
			return owner;
		}
	}
}
