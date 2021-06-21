package chav1961.calc.plugins.calc.activefilter;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.ModuleAccessor;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.ui.interfaces.Action;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.calc.activefilter.ActiveFilterPlugin/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.curcuits.activefilter",tooltip="menu.curcuits.activefilter.tt",help="help.aboutApplication")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.calc.activefilter.calc",tooltip="chav1961.calc.plugins.calc.activefilter.calc.tt"),actionString="calculate")
@PluginProperties(width=500,height=320,leftWidth=300,svgURI="schema1.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
public class ActiveFilterPlugin implements FormManager<Object,ActiveFilterPlugin>, ModuleAccessor {
	private final LoggerFacade 	logger;
	
	@LocaleResource(value="chav1961.calc.plugins.calc.activefilter.type",tooltip="chav1961.calc.plugins.calc.activefilter.type.tt")
	@Format("9m")
	public ActiveFilterType type = ActiveFilterType.BAR_TYPE;
	@LocaleResource(value="chav1961.calc.plugins.calc.activefilter.frequency",tooltip="chav1961.calc.plugins.calc.activefilter.frequency.tt")
	@Format("9.2mpzs")
	public float centralFreq = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.activefilter.quality",tooltip="chav1961.calc.plugins.calc.activefilter.quality.tt")
	@Format("9mpzs")
	public float quality = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.activefilter.transmit",tooltip="chav1961.calc.plugins.calc.activefilter.transmit.tt")
	@Format("9mpzs")
	public float transmit = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.activefilter.capacitance",tooltip="chav1961.calc.plugins.calc.activefilter.capacitance.tt")
	@Format("9.2mpzs")
	public float capacitance = 0;

	@LocaleResource(value="chav1961.calc.plugins.calc.activefilter.resistance1",tooltip="chav1961.calc.plugins.calc.activefilter.resistance1.tt")
	@Format("9.2ro")
	public float resistance1 = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.activefilter.resistance2",tooltip="chav1961.calc.plugins.calc.activefilter.resistance2.tt")
	@Format("9.2ro")
	public float resistance2 = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.activefilter.resistance3",tooltip="chav1961.calc.plugins.calc.activefilter.resistance3.tt")
	@Format("9.2ro")
	public float resistance3 = 0;
	
	public ActiveFilterPlugin(final LoggerFacade logger) {
		if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.logger = logger;
		}
	}
	
	@Override
	public RefreshMode onField(final ActiveFilterPlugin inst, final Object id, final String fieldName, final Object oldValue, final boolean beforeCommit) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onAction(final ActiveFilterPlugin inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "app:action:/ActiveFilterPlugin.calculate"	:
				resistance1 = (float) (quality / (transmit * 2 * Math.PI * centralFreq * capacitance * 1e-9));
				resistance2 = (float) (quality / ((2 * quality * quality - transmit) * 2 * Math.PI * centralFreq * capacitance * 1e-9));
				resistance3 = (float) (2 * quality / (2 * Math.PI * centralFreq * capacitance * 1e-9));
				return RefreshMode.RECORD_ONLY;
			default :
				throw new UnsupportedOperationException("Unknown action string ["+actionName+"]");
		}
	}
	
	@Override
	public LoggerFacade getLogger() {
		return logger;
	}

	@Override
	public void allowUnnamedModuleAccess(final Module... unnamedModules) {
		for (Module item : unnamedModules) {
			this.getClass().getModule().addExports(this.getClass().getPackageName(),item);
		}
	}
}