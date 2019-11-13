package chav1961.calc.utils;

import java.io.File;
import java.net.URI;
import java.util.Locale;

import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.AbstractLocalizer;
import chav1961.purelib.i18n.interfaces.Localizer;

public class MutableLocalizer extends AbstractLocalizer {
	public MutableLocalizer(final File repository) throws LocalizationException {
	}

	@Override
	public void close() throws LocalizationException {
		super.close();
	}
	
	@Override
	public String getLocalizerId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canServe(URI resource) throws NullPointerException {
		return false;
	}

	@Override
	public Localizer newInstance(URI resource) throws EnvironmentException, NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<String> localKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocalValue(String key) throws LocalizationException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public void addLocalKey(final String key) throws LocalizationException, IllegalArgumentException {
		
	}

	public void removeLocalKey(final String key) throws LocalizationException, IllegalArgumentException {
		
	}

	public void setLocalKeyValues(final String key, final LocalKeyValue... values) throws LocalizationException, IllegalArgumentException {
		
	}
	
	public LocalKeyValue[] getLocalKeyValues(final String key) {
		return null;
	}
	
	@Override
	protected void loadResource(Locale newLocale) throws LocalizationException, NullPointerException {
		// TODO Auto-generated method stub
	}
	

	@Override
	protected String getHelp(String helpId, String encoding) throws LocalizationException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static class LocalKeyValue {
		final Locale	localeAssociated;
		String			value;
		
		public LocalKeyValue(Locale localeAssociated, String value) {
			if (localeAssociated == null) {
				throw new NullPointerException(); 
			}
			else {
				this.localeAssociated = localeAssociated;
				this.value = value;
			}
		}

		public Locale getLocaleAssociated() {
			return localeAssociated;
		}
		
		public String getValue() {
			return value;
		}

		public void setValue(final String value) {
			this.value = value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((localeAssociated == null) ? 0 : localeAssociated.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			LocalKeyValue other = (LocalKeyValue) obj;
			if (localeAssociated == null) {
				if (other.localeAssociated != null) return false;
			} else if (!localeAssociated.equals(other.localeAssociated)) return false;
			if (value == null) {
				if (other.value != null) return false;
			} else if (!value.equals(other.value)) return false;
			return true;
		}

		@Override
		public String toString() {
			return "<"+localeAssociated.getLanguage()+">: '" + value + "'";
		}
	}
}
