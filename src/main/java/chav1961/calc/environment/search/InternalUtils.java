package chav1961.calc.environment.search;

import java.net.URI;

import chav1961.calc.environment.Constants;
import chav1961.calc.environment.search.SearchComponent;
import chav1961.calc.environment.search.SearchListener;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;

class InternalUtils {
	static void parseAndCall(final Localizer localizer, final SearchComponent component,final SearchListener listener, final URI uri) throws LocalizationException {
		switch (uri.getPath()) {
			case Constants.PLUGIN_PATH	:
				listener.pluginClicked(component,uri.getFragment());
				break;
			case Constants.USES_PATH		:
				listener.facetClicked(component,uri.getFragment(),localizeAndEscape(localizer,uri.getFragment()));
				break;
			case Constants.TAGS_PATH		:
				listener.facetClicked(component,uri.getFragment(),localizeAndEscape(localizer,uri.getFragment()));
				break;
			case Constants.SEE_ALSO_PATH	:
				listener.facetClicked(component,uri.getFragment(),localizeAndEscape(localizer,uri.getFragment()));
				break;
			default :
				throw new UnsupportedOperationException("Unsupported link ["+uri+"] in the content");
		}
	}

	static String escape(final String content) throws LocalizationException{
		return content.replace(">","&gt;").replace("<","&lt;").replace("&","&amp;");
	}
	
	static String localizeAndEscape(final Localizer localizer, final String id) throws LocalizationException{
		return escape(localizer.getValue(id));
	}
}
