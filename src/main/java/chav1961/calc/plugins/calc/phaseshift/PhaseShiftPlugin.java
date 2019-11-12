package chav1961.calc.plugins.calc.phaseshift;

import chav1961.calc.interfaces.PluginProperties;


import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;
import chav1961.purelib.ui.interfaces.Action;

@LocaleResourceLocation("i18n:prop:chav1961/calculator/i18n/i18n")
@LocaleResource(value="menu.curcuits.phaseshift",tooltip="menu.curcuits.phaseshift.tt",help="help.aboutApplication")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.calc.phaseshift.button.freq",tooltip="chav1961.calc.plugins.calc.phaseshift.button.freq.tt"),actionString="calcFreq")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.calc.phaseshift.button.res",tooltip="chav1961.calc.plugins.calc.phaseshift.button.res.tt"),actionString="calcRes")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.calc.phaseshift.button.cap",tooltip="chav1961.calc.plugins.calc.phaseshift.button.cap.tt"),actionString="calcCap")
@PluginProperties(width=500,height=150,leftWidth=250,svgURI="schema.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
public class PhaseShiftPlugin implements FormManager<Object,PhaseShiftPlugin> {
	private final LoggerFacade 	logger;
	
	@LocaleResource(value="chav1961.calc.plugins.calc.phaseshift.resistance",tooltip="chav1961.calc.plugins.calc.phaseshift.resistance.tt")
	@Format("9.2pz")
	public float inductance = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.phaseshift.capacity",tooltip="chav1961.calc.plugins.calc.phaseshift.capacity.tt")
	@Format("9.2pz")
	public float capacity = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.phaseshift.frequency",tooltip="chav1961.calc.plugins.calc.phaseshift.frequency.tt")
	@Format("9.2pz")
	public float frequency = 0;

	public PhaseShiftPlugin(final LoggerFacade logger) {
		if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.logger = logger;
		}
	}
	
	@Override
	public RefreshMode onField(final PhaseShiftPlugin inst, final Object id, final String fieldName, final Object oldValue) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onAction(final PhaseShiftPlugin inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "app:action:/PhaseShiftPlugin.calcFreq"	:
				if (inductance == 0 || capacity == 0) {
					getLogger().message(Severity.warning,"i == 0 || c == 0");
					return RefreshMode.NONE;
				}
				else {
					frequency = (float) (159000 / Math.sqrt(inductance*capacity));
					return RefreshMode.RECORD_ONLY;
				}
			case "app:action:/PhaseShiftPlugin.calcRes"	:
				if (frequency == 0 || capacity == 0) {
					getLogger().message(Severity.warning,"f == 0 || c == 0");
					return RefreshMode.NONE;
				}
				else {
					inductance = (float) (2528e7 / (frequency * frequency * capacity));
					return RefreshMode.RECORD_ONLY;
				}
			case "app:action:/PhaseShiftPlugin.calcCap"	:
				if (frequency == 0 || inductance == 0) {
					getLogger().message(Severity.warning,"f == 0 || i == 0");
					return RefreshMode.NONE;
				}
				else {
					capacity = (float) (2528e7 / (frequency * frequency * inductance));
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
}