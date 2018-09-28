package chav1961.calc.components.lccontourplugin;


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

public class LCContourService implements PluginInterface {
	private static final URL		LEFT_ICON_RESOURCE = LCContour.class.getResource("LCContour.png");
	private static final URL		MINI_ICON_RESOURCE = LCContour.class.getResource("LCContourIcon.png");
	private static final Icon		ICON = new ImageIcon(MINI_ICON_RESOURCE);
	private static final String[]	RECOMMENDED_PATH = {"menu.elements","components","lccontour"};

	private LCContour				inner = null;
	
	public LCContourService() {
	}
	
	@Override
	public synchronized PluginInstance newInstance(final Localizer localizer, final LoggerFacade logger) throws LocalizationException, SyntaxException, ContentException, IOException {
		return new LCContour(this,localizer,new LCContourCalculator(localizer,logger)); 
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
		Localizer	result = parent.getLocalizerById(LCContourCalculator.class.getAnnotation(LocaleResourceLocation.class).value());
		
		if (result == null) {
			return Utils.attachLocalizer(parent,URI.create(LCContourCalculator.class.getAnnotation(LocaleResourceLocation.class).value()));
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
			try{inner = new LCContour(this,parent,new SystemErrLoggerFacade());
			} catch (SyntaxException | ContentException | IOException e) {
				throw new LocalizationException(e.getLocalizedMessage(),e);
			}
		}
		return inner.getLabelIds();
	}

	@Override
	public String[] getTagsIds(final Localizer parent) throws LocalizationException {
		return new String[]{"LCContourService.tag1","LCContourService.tag2","LCContourService.tag3"};
	}

	@Override
	public String[] getSeeAlsoIds(final Localizer parent) throws LocalizationException {
		return Utils.extractFormulas(LCContourCalculator.class);
	}	
	
	private static class LCContour extends AutoBuiltForm<LCContourCalculator> implements PluginInstance {
		private static final long 		serialVersionUID = 2615737307529282959L;
		private static final Dimension	RECOMMENDED_SIZE = new Dimension(400,150);
		
		private final PluginInterface	owner;			
		
		public LCContour(final PluginInterface owner, final Localizer localizer, final LoggerFacade logger) throws NullPointerException, IllegalArgumentException, LocalizationException, SyntaxException, ContentException, IOException {
			this(owner,localizer,new LCContourCalculator(localizer,logger));
		}

		protected LCContour(final PluginInterface owner, final Localizer localizer, final LCContourCalculator instance) throws NullPointerException, IllegalArgumentException, LocalizationException, SyntaxException, ContentException, IOException {
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
