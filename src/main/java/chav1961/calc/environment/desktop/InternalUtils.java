package chav1961.calc.environment.desktop;

import java.net.URI;

import chav1961.calc.environment.Constants;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;

class InternalUtils {

	static String escape(final String content) throws LocalizationException{
		return content.replace(">","&gt;").replace("<","&lt;").replace("&","&amp;");
	}
	
	static String localizeAndEscape(final Localizer localizer, final String id) throws LocalizationException{
		return escape(localizer.getValue(id));
	}
}
