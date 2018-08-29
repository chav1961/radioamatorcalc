package chav1961.calc.schemes.stepup.ucX84Xplugin;


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

public class UCX84XUpService implements PluginInterface {
	private static final URL		LEFT_ICON_RESOURCE = UCX84X.class.getResource("UCX84X.png");
	private static final URL		MINI_ICON_RESOURCE = UCX84X.class.getResource("UCX84XIcon.png");
	private static final Icon		ICON = new ImageIcon(MINI_ICON_RESOURCE);
	private static final String[]	RECOMMENDED_PATH = {"menu.schemes","stepup", "UCX84X"};

	private UCX84X					inner = null;
	
	public UCX84XUpService() {
	}
	
	@Override
	public synchronized PluginInstance newInstance(final Localizer localizer, final LoggerFacade logger) throws LocalizationException, SyntaxException, ContentException, IOException {
		return new UCX84X(localizer,new UCX84XCalculator(localizer,logger)); 
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
		Localizer	result = parent.getLocalizerById(UCX84XCalculator.class.getAnnotation(LocaleResourceLocation.class).value());
		
		if (result == null) {
			return Utils.attachLocalizer(parent,URI.create(UCX84XCalculator.class.getAnnotation(LocaleResourceLocation.class).value()));
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
			try{inner = new UCX84X(parent,new SystemErrLoggerFacade());
			} catch (SyntaxException | ContentException | IOException e) {
				throw new LocalizationException(e.getLocalizedMessage(),e);
			}
		}
		return inner.getLabelIds();
	}

	@Override
	public String[] getTagsIds(final Localizer parent) throws LocalizationException {
		return new String[]{"UCX84XService.tag1","UCX84XService.tag2","UCX84XService.tag3","UCX84XService.tag4"
						   ,"UCX84XService.tag5","UCX84XService.tag6","UCX84XService.tag7","UCX84XService.tag8"
						   ,"UCX84XService.tag9","UCX84XService.tag10","UCX84XService.tag11","UCX84XService.tag12"
						   ,"UCX84XService.tag13"};
	}

	@Override
	public String[] getSeeAlsoIds(final Localizer parent) throws LocalizationException {
		return Utils.extractFormulas(UCX84XCalculator.class);
	}	
	
	private static class UCX84X extends AutoBuiltForm<UCX84XCalculator> implements PluginInstance {
		private static final long 		serialVersionUID = 2615737307529282959L;
		private static final Dimension	RECOMMENDED_SIZE = new Dimension(450,360);
		
		public UCX84X(final Localizer localizer, final LoggerFacade logger) throws NullPointerException, IllegalArgumentException, LocalizationException, SyntaxException, ContentException, IOException {
			this(localizer,new UCX84XCalculator(localizer,logger));
		}

		protected UCX84X(final Localizer localizer, final UCX84XCalculator instance) throws NullPointerException, IllegalArgumentException, LocalizationException, SyntaxException, ContentException, IOException {
			super(localizer,LEFT_ICON_RESOURCE,instance,instance);
		}

		@Override
		public JComponent getComponent() {
			return this;
		}

		@Override
		public Dimension getRecommendedSize() {
			return RECOMMENDED_SIZE;
		}
	}
}
