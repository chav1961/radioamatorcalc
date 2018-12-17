package chav1961.calc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import chav1961.purelib.basic.SubstitutableProperties;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.interfacers.Action;
import chav1961.purelib.ui.interfacers.FormManager;
import chav1961.purelib.ui.interfacers.Format;
import chav1961.purelib.ui.interfacers.RefreshMode;

@Action(resource=@LocaleResource(value="settings.save",tooltip="settings.save.tooltip"),actionString="OK") 
@Action(resource=@LocaleResource(value="settings.reset",tooltip="settings.reset.tooltip"),actionString="reset") 
@Action(resource=@LocaleResource(value="settings.cancel",tooltip="settings.cancel.tooltip"),actionString="cancel") 
public class CurrentSettings implements FormManager<Object,CurrentSettings> {
	public static final String	SETTINGS_LOCATION = "./.radioamatorcalc";
	
	private static final String	KEY_AUTOSAVEFIELD = "autoSaveField";
	private static final String	KEY_AUTOSAVELOCATION = "autoSaveLocation";
	private static final String	KEY_AUTOUPDATE = "autoUpdate";
	
@LocaleResource(value="settings.autosave",tooltip="settings.autosave.tooltip")
@Format("1")
	public boolean	autoSaveField = false;

@LocaleResource(value="settings.autosave.location",tooltip="settings.autosave.location.tooltip")
@Format("30m")
	public File		autoSaveLocation = new File("./autosave");

@LocaleResource(value="settings.autoupdate",tooltip="settings.autoupdate.tooltip")
@Format("1")
	public boolean	autoUpdate = false;

	private final Localizer		localizer;
	private final LoggerFacade	logger;
	
	public CurrentSettings(final Localizer localizer, final LoggerFacade logger) throws LocalizationException, FlowException {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null"); 
		}
		else if (logger == null) {
			throw new NullPointerException("Logger facade can't be null"); 
		}
		else {
			this.localizer = localizer;
			this.logger = logger;
			onAction(this,null,"reset",null);
		}
	}

	
	@Override
	public RefreshMode onRecord(final Action action, final CurrentSettings oldRecord, final Object oldId, final CurrentSettings newRecord, final Object newId) throws FlowException, LocalizationException {
		return RefreshMode.NONE;
	}
	
	@Override
	public RefreshMode onField(final CurrentSettings inst, final Object id, final String fieldName, final Object oldValue) throws FlowException, LocalizationException {
		return RefreshMode.FIELD_ONLY;
	}
	
	@Override
	public RefreshMode onAction(final CurrentSettings inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "OK"		:
				saveCurrent();
				return RefreshMode.NONE;
			case "reset"	:
				loadLastSaved();
				return RefreshMode.RECORD_ONLY;
			case "cancel"	:
				loadLastSaved();
				return RefreshMode.NONE;
			default : throw new UnsupportedOperationException("Operation ["+actionName+"] is not supported yet");
		}
	}
	
	@Override
	public LoggerFacade getLogger() {
		return logger;
	}

	private void loadLastSaved() throws FlowException {
		final File	source = new File(SETTINGS_LOCATION);
		
		if (source.exists()) {
			if (source.isFile()) {
				final SubstitutableProperties	props = new SubstitutableProperties();
				
				try(final InputStream	is = new FileInputStream(source);
					final Reader		rdr = new InputStreamReader(is,"UTF-8")) {
					
					props.load(rdr);
				} catch (IOException exc) {
					throw new FlowException("Settings location ["+source.getAbsolutePath()+"]: I/O error reading content ("+exc.getLocalizedMessage()+")",exc);
				}
				this.autoSaveField = props.getProperty(KEY_AUTOSAVEFIELD,boolean.class,"false");
				this.autoSaveLocation = props.getProperty(KEY_AUTOSAVELOCATION,File.class,SETTINGS_LOCATION);
				this.autoUpdate = props.getProperty(KEY_AUTOUPDATE,boolean.class,"false");
			}
			else {
				throw new FlowException("Settings location ["+source.getAbsolutePath()+"] is directory, not file!");
			}
		}
		else {
			saveCurrent();
		}
	}


	private void saveCurrent() throws FlowException {
		final File			source = new File(SETTINGS_LOCATION);
		final Properties	props = new Properties();

		props.setProperty(KEY_AUTOSAVEFIELD,autoSaveField ? "true" : "false");
		props.setProperty(KEY_AUTOSAVELOCATION,autoSaveLocation.getAbsolutePath());
		props.setProperty(KEY_AUTOUPDATE,autoUpdate ? "true" : "false");
		
		try(final OutputStream	is = new FileOutputStream(source);
			final Writer		wr = new OutputStreamWriter(is,"UTF-8")) {
			
			props.store(wr,null);
		} catch (IOException exc) {
			throw new FlowException("Settings location ["+source.getAbsolutePath()+"]: I/O error writing content ("+exc.getLocalizedMessage()+")",exc);
		}
	}


}
