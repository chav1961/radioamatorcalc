package chav1961.calc.interfaces;

import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.JComponent;

import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;

public interface PluginInterface {
	public interface PluginInstance extends AutoCloseable, LocaleChangeListener {
		PluginInterface getPluginDescriptor();
		JComponent getComponent();
		Dimension getRecommendedSize();
		Localizer getLocalizerAssociated() throws LocalizationException;
		void close();
	}
	
	PluginInstance newInstance(final Localizer localizer, final LoggerFacade logger) throws LocalizationException, SyntaxException, ContentException, IOException;
	String getPluginId();
	String getCaptionId();
	String getToolTipId();
	String getHelpId();
	Icon getIcon();
	URL getMiniIconURL();
	URL getLeftIconURL();
	String[] getRecommendedNavigationPath();
	Localizer getLocalizerAssociated(final Localizer parent) throws LocalizationException;
	String[] getUsesIds(final Localizer parent) throws LocalizationException;
	String[] getTagsIds(final Localizer parent) throws LocalizationException;
	String[] getSeeAlsoIds(final Localizer parent) throws LocalizationException;
}
