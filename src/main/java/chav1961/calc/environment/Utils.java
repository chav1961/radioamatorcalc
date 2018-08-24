package chav1961.calc.environment;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import chav1961.calc.interfaces.UseFormulas;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.Localizer;

public class Utils {
	public static Localizer attachLocalizer(final Localizer parent, final URI localizerURI) throws LocalizationException {
		try{final Localizer 	result = LocalizerFactory.getLocalizer(localizerURI);
		
			parent.push(result);
			return result;
		} catch (IOException e) {
			throw new LocalizationException(e.getLocalizedMessage(),e);
		} 
	}
	
	public static String[] join(final String[]... content) {
		int totalLength = 0;
		
		for (String[] item : content) {
			totalLength += item.length;
		}
		final String[]	result = new String[totalLength];
		
		for (int index = 0, from = 0; index < content.length; index++) {
			System.arraycopy(content[index],0,result,from,content[index].length);
			from += content[index].length;
		}
		return result;
	}
	
	public static String[] extractFormulas(final Class<?> clazz) {
		if (clazz.isAnnotationPresent(UseFormulas.class)) {
			return clazz.getAnnotation(UseFormulas.class).value(); 
		}
		else {
			return new String[0];
		}
	}
	
	public static String[] buildFieldsAnnotated(final Class<?> clazz) {
		final List<String>	result = new ArrayList<>();
		
		for (Field item : clazz.getDeclaredFields()) {
			if (item.isAnnotationPresent(LocaleResource.class)) {
				result.add(item.getName());
			}
		}
		return result.toArray(new String[result.size()]);
	}
}
