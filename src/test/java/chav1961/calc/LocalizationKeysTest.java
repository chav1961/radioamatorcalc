package chav1961.calc;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;

import org.junit.Assert;
import org.junit.Test;

import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleDescriptor;

public class LocalizationKeysTest {
	public static final String	LOCALIZER_URI = "i18n:xml:root://chav1961.calc.LocalizationKeys/chav1961/calculator/i18n/i18n.xml";

	@Test
	public void test() throws LocalizationException, IOException, IllegalAccessException {
		try(final Localizer	localizer = LocalizerFactory.getLocalizer(URI.create(LOCALIZER_URI))) {
			for (Field f : LocalizationKeys.class.getFields()) {
				Assert.assertTrue(localizer.containsKey(f.get(null).toString()));
			}
			for (LocaleDescriptor item : localizer.supportedLocales()) {
				localizer.setCurrentLocale(item.getLocale());
				for (Field f : LocalizationKeys.class.getFields()) {
					Assert.assertNotNull(localizer.getValue(f.get(null).toString()));
				}
			}
		}
	}
}
