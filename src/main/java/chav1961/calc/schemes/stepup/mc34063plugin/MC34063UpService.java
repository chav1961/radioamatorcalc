package chav1961.calc.schemes.stepup.mc34063plugin;


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

public class MC34063UpService implements PluginInterface {
	private static final URL		LEFT_ICON_RESOURCE = MC34063.class.getResource("MC34063.png");
	private static final URL		MINI_ICON_RESOURCE = MC34063.class.getResource("MC34063Icon.png");
	private static final Icon		ICON = new ImageIcon(MINI_ICON_RESOURCE);
	private static final String[]	RECOMMENDED_PATH = {"menu.schemes","stepup", "mc34063"};

	private MC34063					inner = null;
	
	public MC34063UpService() {
	}
	
	@Override
	public synchronized PluginInstance newInstance(final Localizer localizer, final LoggerFacade logger) throws LocalizationException, SyntaxException, ContentException, IOException {
		return new MC34063(this,localizer,new MC34063Calculator(localizer,logger)); 
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
		Localizer	result = parent.getLocalizerById(MC34063Calculator.class.getAnnotation(LocaleResourceLocation.class).value());
		
		if (result == null) {
			return Utils.attachLocalizer(parent,URI.create(MC34063Calculator.class.getAnnotation(LocaleResourceLocation.class).value()));
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
			try{inner = new MC34063(this,parent,new SystemErrLoggerFacade());
			} catch (SyntaxException | ContentException | IOException e) {
				throw new LocalizationException(e.getLocalizedMessage(),e);
			}
		}
		return inner.getModifiableLabelIds();
	}

	@Override
	public String[] getTagsIds(final Localizer parent) throws LocalizationException {
		return new String[]{"MC34063UpService.tag1","MC34063UpService.tag2"};
	}

	@Override
	public String[] getSeeAlsoIds(final Localizer parent) throws LocalizationException {
		return Utils.extractFormulas(MC34063Calculator.class);
	}	
	
	private static class MC34063 extends AutoBuiltForm<MC34063Calculator> implements PluginInstance {
		private static final long 		serialVersionUID = 2615737307529282959L;
		private static final Dimension	RECOMMENDED_SIZE = new Dimension(450,360);
		
		private final PluginInterface	owner; 
		
		public MC34063(final PluginInterface owner, final Localizer localizer, final LoggerFacade logger) throws NullPointerException, IllegalArgumentException, LocalizationException, SyntaxException, ContentException, IOException {
			this(owner,localizer,new MC34063Calculator(localizer,logger));
		}

		protected MC34063(final PluginInterface owner, final Localizer localizer, final MC34063Calculator instance) throws NullPointerException, IllegalArgumentException, LocalizationException, SyntaxException, ContentException, IOException {
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
			// TODO Auto-generated method stub
			return null;
		}
	}
}
