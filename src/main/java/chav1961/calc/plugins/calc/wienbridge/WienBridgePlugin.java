package chav1961.calc.plugins.calc.wienbridge;

import chav1961.calc.interfaces.PluginProperties;


import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.ModuleAccessor;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;
import chav1961.purelib.ui.interfaces.Action;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.calc.wienbridge.WienBridgePlugin/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.curcuits.wienbridge",tooltip="menu.curcuits.wienbridge.tt",help="help.aboutApplication")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.calc.wienbridge.button.freq",tooltip="chav1961.calc.plugins.calc.wienbridge.button.freq.tt"),actionString="calcFreq")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.calc.wienbridge.button.res",tooltip="chav1961.calc.plugins.calc.wienbridge.button.res.tt"),actionString="calcRes")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.calc.wienbridge.button.cap",tooltip="chav1961.calc.plugins.calc.wienbridge.button.cap.tt"),actionString="calcCap")
@PluginProperties(width=500,height=150,leftWidth=250,svgURI="schema.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
public class WienBridgePlugin implements FormManager<Object,WienBridgePlugin>, ModuleAccessor {
	private final LoggerFacade 	logger;
	
	@LocaleResource(value="chav1961.calc.plugins.calc.wienbridge.resistance",tooltip="chav1961.calc.plugins.calc.wienbridge.resistance.tt")
	@Format("9.2pzs")
	public float resistance = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.wienbridge.capacity",tooltip="chav1961.calc.plugins.calc.wienbridge.capacity.tt")
	@Format("9.2pzs")
	public float capacity = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.wienbridge.frequency",tooltip="chav1961.calc.plugins.calc.wienbridge.frequency.tt")
	@Format("9.2pzs")
	public float frequency = 0;

	public WienBridgePlugin(final LoggerFacade logger) {
		if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.logger = logger;
		}
	}
	
	@Override
	public RefreshMode onField(final WienBridgePlugin inst, final Object id, final String fieldName, final Object oldValue, final boolean beforeCommit) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onAction(final WienBridgePlugin inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "app:action:/WienBridgePlugin.calcFreq"	:
				if (resistance == 0 || capacity == 0) {
					getLogger().message(Severity.warning,"R == 0 || C == 0");
					return RefreshMode.NONE;
				}
				else {
					frequency = (float) (1e-3 / (2 * Math.PI * resistance*1e3*capacity*1e-9));
					return RefreshMode.RECORD_ONLY;
				}
			case "app:action:/WienBridgePlugin.calcRes"	:
				if (frequency == 0 || capacity == 0) {
					getLogger().message(Severity.warning,"F == 0 || C == 0");
					return RefreshMode.NONE;
				}
				else {
					resistance = (float) (1e-3 / (2 * Math.PI * frequency * 1e3 * capacity * 1e-9));
					return RefreshMode.RECORD_ONLY;
				}
			case "app:action:/WienBridgePlugin.calcCap"	:
				if (frequency == 0 || resistance == 0) {
					getLogger().message(Severity.warning,"F == 0 || R == 0");
					return RefreshMode.NONE;
				}
				else {
					capacity = (float) (1e9 / (2 * Math.PI * frequency * 1e3 * resistance * 1e3));
					return RefreshMode.RECORD_ONLY;
				}
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