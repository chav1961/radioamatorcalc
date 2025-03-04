package chav1961.calc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.Icon;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.XMLUtils;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.InputStreamGetter;
import chav1961.purelib.basic.interfaces.OutputStreamGetter;
import chav1961.purelib.enumerations.ContinueMode;
import chav1961.purelib.enumerations.XSDCollection;
import chav1961.purelib.i18n.AbstractLocalizer;
import chav1961.purelib.i18n.interfaces.Localizer;

public class MutableLocalizer extends AbstractLocalizer {
	private static final LocalKeyValue[]	EMPTY_VALUESET = new LocalKeyValue[0]; 
	private static final String				SUBSCHEME = "mutable";
	private static final String				NAMESPACE = "http://www.i18n.purelib.chav1961.ru/";
	private static final String				PREFIX = "";
	private static final String				TAG_ROOT = "localization";
	private static final String				TAG_LANG = "lang";
	private static final String				TAG_KEY = "key";
	private static final String				ATTR_NAME = "name";
	
	private final InputStreamGetter					isGetter;
	private final OutputStreamGetter				osGetter;
	private final Map<String,Set<LocalKeyValue>>	repo = new HashMap<>();
	private Locale									currentLocale = null;
	
	private volatile boolean	wasChanged = false;
	
	public MutableLocalizer(final File repository) throws IOException, LocalizationException {
		this(buildGetter4File(repository),()->new FileOutputStream(repository));
	}

	public MutableLocalizer(final InputStreamGetter isGetter, final OutputStreamGetter osGetter) throws IOException, LocalizationException {
		if (isGetter == null) {
			throw new NullPointerException("Input stream getter can't be null");
		}
		else if (osGetter == null) {
			throw new NullPointerException("Output stream getter can't be null");
		}
		else {
			this.isGetter = isGetter;
			this.osGetter = osGetter;
			loadContent();
		}
	}
	
	@Override
	public void close() throws LocalizationException {
		if (wasChanged) {
			try{storeContent();
			} catch (IOException e) {
				throw new LocalizationException(e);
			}
		}
		super.close();
	}
	

	@Override
	public URI getLocalizerId() {
		return URI.create(SUBSCHEME+":/");
	}

	@Override
	public String getSubscheme() {
		return SUBSCHEME;
	}
	
	@Override
	public boolean canServe(final URI resource) throws NullPointerException {
		return false;
	}

	@Override
	public Localizer newInstance(final URI resource) throws EnvironmentException, NullPointerException, IllegalArgumentException {
		return this;
	}

	@Override
	public Iterable<String> localKeys() {
		return repo.keySet();
	}

	@Override
	public String getLocalValue(final String key) throws LocalizationException, IllegalArgumentException {
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException("Key to get value for can't be null or empty");
		}
		else if (!repo.containsKey(key)) {
			throw new LocalizationException("Key ["+key+"] is missing in the localizer");
		}
		else {
			for (LocalKeyValue item : repo.get(key)) {
				if (item.getLocaleAssociated().getLanguage().equals(currentLocale.getLanguage())) {
					return item.getValue();
				}
			}
			return "";
		}
	}

	public void addLocalKey(final String key) throws LocalizationException, IllegalArgumentException {
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException("Key to add can't be null or empty");
		}
		else if (!repo.containsKey(key)) {
			repo.put(key,new HashSet<>());
		}
	}

	public void removeLocalKey(final String key) throws LocalizationException, IllegalArgumentException {
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException("Key to remove can't be null or empty");
		}
		else {
			repo.remove(key);
		}
	}

	public void setLocalKeyValues(final String key, final LocalKeyValue... values) throws LocalizationException, IllegalArgumentException {
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException("Key to set value for can't be null or empty");
		}
		else {
			addLocalKey(key);
			repo.get(key).clear();
			repo.get(key).addAll(Arrays.asList(values));
			wasChanged = true;
		}
	}
	
	public LocalKeyValue[] getLocalKeyValues(final String key) {
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException("Key to get value for can't be null or empty");
		}
		else if (!repo.containsKey(key)) {
			return EMPTY_VALUESET;
		}
		else {
			return repo.get(key).toArray(new LocalKeyValue[repo.get(key).size()]);  
		}
	}
	
	public static void prepareRepo(final File file) throws IOException {
		try(final InputStream	is = MutableLocalizer.class.getResourceAsStream("defaultLocalizerContent.xml"); 
			final OutputStream	os = new FileOutputStream(file)) {
	
			Utils.copyStream(is,os);
		}
	}
	
	@Override
	protected void loadResource(final Locale newLocale) throws LocalizationException, NullPointerException {
		currentLocale = newLocale;
	}

	@Override
	protected String getHelp(final String helpId, final Locale locale, final String encoding) throws LocalizationException, IllegalArgumentException {
		return getValue(helpId);
	}

	private void loadContent() throws IOException, LocalizationException {
		final Locale[]	currentLocale = new Locale[1];
		
		try(final InputStream	is = isGetter.getInputContent()) {
			XMLUtils.walkDownXML(XMLUtils.validateAndLoadXML(is,XMLUtils.getPurelibXSD(XSDCollection.XMLLocalizerContent),PureLibSettings.CURRENT_LOGGER).getDocumentElement(), (mode,node)->{
				switch (mode) {
					case ENTER	:
						switch (node.getNodeName()) {
							case "lang"	:
								currentLocale[0] = Locale.forLanguageTag(node.getAttributes().getNamedItem("name").getTextContent());
								break;
							case "key"	:
								final String				key = node.getAttributes().getNamedItem("name").getTextContent(); 
								final Set<LocalKeyValue>	temp = new HashSet<>();
								
								try{addLocalKey(key);
									temp.addAll(Arrays.asList(getLocalKeyValues(key)));
									temp.add(new LocalKeyValue(currentLocale[0],node.getTextContent()));
									setLocalKeyValues(key,temp.toArray(new LocalKeyValue[temp.size()]));
								} catch (LocalizationException e) {
									throw new ContentException(e); 
								}
								break;
							case "ref"	:
								break;
						}
						break;
					default		:
						throw new UnsupportedOperationException("Mode ["+mode+"] is not supported yet");
				}
				return ContinueMode.CONTINUE;
			});
		} catch (ContentException e) {
			throw new LocalizationException(e.getMessage(),e); 
		}
	}

	private void storeContent() throws IOException, LocalizationException {
		try(final OutputStream			os = osGetter.getOutputContent()) {
			final XMLEventFactory 		eventFactory = XMLEventFactory.newInstance();
			final XMLEventWriter 		writer = XMLOutputFactory.newInstance().createXMLEventWriter(os);
			final XMLStreamException[]	err = new XMLStreamException[] {null}; 

			writer.setDefaultNamespace(NAMESPACE);
			writer.add(eventFactory.createStartDocument());
			writer.add(eventFactory.createStartElement(PREFIX,NAMESPACE,TAG_ROOT));
			writer.add(eventFactory.createNamespace(PREFIX,NAMESPACE)); 					

			AbstractLocalizer.enumerateLocales((lang,langName,icon) -> {
				try{writer.add(eventFactory.createStartElement(PREFIX,NAMESPACE,TAG_LANG));
					writer.add(eventFactory.createAttribute(ATTR_NAME,langName));
				
					for (Entry<String, Set<LocalKeyValue>> item : repo.entrySet()) {
						for (LocalKeyValue kv : item.getValue()) {
							if (kv.getLocaleAssociated().getLanguage().equals(langName)) {
								writer.add(eventFactory.createStartElement(PREFIX,NAMESPACE,TAG_KEY));
								writer.add(eventFactory.createAttribute(ATTR_NAME,item.getKey()));
						 		writer.add(eventFactory.createCharacters(kv.getValue()));
								writer.add(eventFactory.createEndElement(PREFIX,NAMESPACE,TAG_KEY));
								break;
							}
						}
					}
					
					writer.add(eventFactory.createEndElement(PREFIX,NAMESPACE,TAG_LANG));
				} catch (XMLStreamException e) {
					err[0] = e;
				}
			});
			
			writer.add(eventFactory.createEndElement(PREFIX,NAMESPACE,TAG_ROOT));
			writer.flush();
			if (err[0] != null) {
				throw err[0]; 
			}			
		} catch (XMLStreamException | FactoryConfigurationError e) {
			throw new LocalizationException(e);
		}
		wasChanged = false;
	}

	private static InputStreamGetter buildGetter4File(final File file) throws IOException {
		if (file == null) {
			throw new NullPointerException("Repository file can't be null");
		}
		else {
			if (!file.exists()) {
				prepareRepo(file);
			}
			return ()->new FileInputStream(file);
		}
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

	@Override
	public String getLocalValue(String key, Locale locale) throws LocalizationException, IllegalArgumentException {
		return getLocalValue(key);
	}

	@Override
	protected boolean isLocaleSupported(String key, Locale locale) throws LocalizationException, IllegalArgumentException {
		return true;
	}
}
