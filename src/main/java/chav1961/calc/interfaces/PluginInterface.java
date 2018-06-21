package chav1961.calc.interfaces;

import java.io.IOException;

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
		JComponent getComponent();
		Localizer getLocalizerAssociated() throws LocalizationException;
		void close();
	}
	
	PluginInstance newInstance(final Localizer localizer, final LoggerFacade logger) throws LocalizationException, SyntaxException, ContentException, IOException;
	String getPluginId();
	String getCaptionId();
	String getToolTipId();
	String getHelpId();
	String getTagsId();
	Icon getIcon();
}
