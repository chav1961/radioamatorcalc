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
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.interfaces.Action;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;

@Action(resource=@LocaleResource(value="settings.save",tooltip="settings.save.tooltip"),actionString="OK") 
@Action(resource=@LocaleResource(value="settings.reset",tooltip="settings.reset.tooltip"),actionString="reset") 
@Action(resource=@LocaleResource(value="settings.cancel",tooltip="settings.cancel.tooltip"),actionString="cancel") 
@LocaleResourceLocation("i18n:xml:root://chav1961.calc.Application/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.settings",tooltip="menu.ttSettings")
public class CurrentSettings implements FormManager<Object,CurrentSettings> {
	public static final String	SETTINGS_LOCATION = "./.radioamatorcalc";

	public static final String	SETTINGS_SAVED = "settings.saved";
	
	private static final String	KEY_AUTOSAVEFIELD = "autoSaveField";
	private static final String	KEY_AUTOSAVELOCATION = "autoSaveLocation";
	private static final String	KEY_AUTOUPDATE = "autoUpdate";

	private static final File	SETTINGS_FILE = new File(SETTINGS_LOCATION);
	
	
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
			onAction(this,null,"app:action:/CurrentSettings.reset",null);
		}
	}
	
	@Override
	public RefreshMode onField(final CurrentSettings inst, final Object id, final String fieldName, final Object oldValue, boolean beforeCommit) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}
	
	@Override
	public RefreshMode onAction(final CurrentSettings inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "app:action:/CurrentSettings.OK"		:
				saveCurrent();
				return RefreshMode.NONE;
			case "app:action:/CurrentSettings.reset"	:
				loadLastSaved();
				return RefreshMode.RECORD_ONLY;
			case "app:action:/CurrentSettings.cancel"	:
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
		
		if (SETTINGS_FILE.exists()) {
			if (SETTINGS_FILE.isFile()) {
				final SubstitutableProperties	props = new SubstitutableProperties();
				
				try(final InputStream	is = new FileInputStream(SETTINGS_FILE);
					final Reader		rdr = new InputStreamReader(is,"UTF-8")) {
					
					props.load(rdr);
				} catch (IOException exc) {
					throw new FlowException("Settings location ["+SETTINGS_FILE.getAbsolutePath()+"]: I/O error reading content ("+exc.getLocalizedMessage()+")",exc);
				}
				this.autoSaveField = props.getProperty(KEY_AUTOSAVEFIELD,boolean.class,"false");
				this.autoSaveLocation = props.getProperty(KEY_AUTOSAVELOCATION,File.class,SETTINGS_LOCATION);
				this.autoUpdate = props.getProperty(KEY_AUTOUPDATE,boolean.class,"false");
			}
			else {
				throw new FlowException("Settings location ["+SETTINGS_FILE.getAbsolutePath()+"] is directory, not file!");
			}
		}
		else {
			saveCurrent();
		}
	}


	private void saveCurrent() throws FlowException {
		final Properties	props = Utils.mkProps(KEY_AUTOSAVEFIELD, autoSaveField ? "true" : "false",
												  KEY_AUTOSAVELOCATION, autoSaveLocation.getAbsolutePath(),
												  KEY_AUTOUPDATE, autoUpdate ? "true" : "false");

		try(final OutputStream	is = new FileOutputStream(SETTINGS_FILE);
			final Writer		wr = new OutputStreamWriter(is,"UTF-8")) {
			
			props.store(wr,null);
		} catch (IOException exc) {
			throw new FlowException("Settings location ["+SETTINGS_FILE.getAbsolutePath()+"]: I/O error writing content ("+exc.getLocalizedMessage()+")",exc);
		}
	}


}
