package chav1961.calc.plugins.devices.pulsestab;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.calc.plugins.calc.contour.ContourPlugin;
import chav1961.purelib.basic.CSSUtils.Frequency;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.basic.interfaces.ModuleAccessor;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;
import chav1961.purelib.ui.interfaces.Action;

/**
 * <p>Pulse stabilizer calculation plugin. Calculations are based on formulas in MC34063 datasheet.</p> 
 * @author Alexander Chernomyrdin aka chav1961
 * @since 0.0.1
 */

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.devices.pulsestab.PulseStabPlugin/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.devices.pulsestab",tooltip="menu.devices.pulsestab.tt",help="help.aboutApplication")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.devices.pulsestab.button",tooltip="chav1961.calc.plugins.devices.pulsestab.button.tt"),actionString="calculate")
@PluginProperties(width=650,height=400,leftWidth=350,svgURI="schema1.SVG,schema2.SVG,schema3.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
public class PulseStabPlugin implements FormManager<Object,PulseStabPlugin>, ModuleAccessor {
	private static final float	ETHA = 0.92f; 

	private final LoggerFacade 	logger;
	
	@LocaleResource(value="chav1961.calc.plugins.devices.pulsestab.schematics",tooltip="chav1961.calc.plugins.devices.pulsestab.schematics.tt")
	@Format("30m")
	public SchemaType		schematics = SchemaType.STEP_DOWN;
	@LocaleResource(value="chav1961.calc.plugins.devices.pulsestab.uIn",tooltip="chav1961.calc.plugins.devices.pulsestab.uIn.tt")
	@Format("9.2mpzs")
	public float 			uIn = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.pulsestab.uOut",tooltip="chav1961.calc.plugins.devices.pulsestab.uOut.tt")
	@Format("9.2mnpzs")
	public float 			uOut = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.pulsestab.iOut",tooltip="chav1961.calc.plugins.devices.pulsestab.iOut.tt")
	@Format("9.2mpzs")
	public float 			iOut = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.pulsestab.frequency",tooltip="chav1961.calc.plugins.devices.pulsestab.frequency.tt")
	@Format("9.2mpzs")
	public float 			freq = 100;
	@LocaleResource(value="chav1961.calc.plugins.devices.pulsestab.uSat",tooltip="chav1961.calc.plugins.devices.pulsestab.uSat.tt")
	@Format("9.2mpzs")
	public float 			uSat = 0.8f;
	@LocaleResource(value="chav1961.calc.plugins.devices.pulsestab.uF",tooltip="chav1961.calc.plugins.devices.pulsestab.uF.tt")
	@Format("9.2mpzs")
	public float 			uF = 0.8f;
	@LocaleResource(value="chav1961.calc.plugins.devices.pulsestab.uRipple",tooltip="chav1961.calc.plugins.devices.pulsestab.uRipple.tt")
	@Format("9.2mpzs")
	public float 			uRipple = 0.1f;
	
	@LocaleResource(value="chav1961.calc.plugins.devices.pulsestab.iInMid",tooltip="chav1961.calc.plugins.devices.pulsestab.iInMid.tt")
	@Format("9.2ro")
	public float iInMid = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.pulsestab.iInPeak",tooltip="chav1961.calc.plugins.devices.pulsestab.iInPeak.tt")
	@Format("9.2ro")
	public float iInPeak = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.pulsestab.iDiodePeak",tooltip="chav1961.calc.plugins.devices.pulsestab.iDiodePeak.tt")
	@Format("9.2ro")
	public float iDiodePeak = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.pulsestab.indictance",tooltip="chav1961.calc.plugins.devices.pulsestab.indictance.tt")
	@Format("9.2ro")
	public float inductance = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.pulsestab.capacitance",tooltip="chav1961.calc.plugins.devices.pulsestab.capacitance.tt")
	@Format("9.2ro")
	public float capacitance = 0;
	
	public PulseStabPlugin(final LoggerFacade logger) {
		if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.logger = logger;
		}
	}
	
	@Override
	public RefreshMode onField(final PulseStabPlugin inst, final Object id, final String fieldName, final Object oldValue, final boolean beforeCommit) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onAction(final PulseStabPlugin inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "app:action:/PulseStabPlugin.calculate"	:
				if (uIn == 0 || uOut == 0 || iOut == 0 || uRipple == 0) {
					getLogger().message(Severity.warning,"Uin == 0 || Uout == 0 || Iout == 0 || Uripple == 0");
					return RefreshMode.NONE;
				}
				else {
					switch (schematics) {
						case INVERTED	:
							if (uOut > 0) {
								getLogger().message(Severity.warning,"Uout > 0");
							}
							else {
								invertedCalc();
							}
							break;
						case STEP_DOWN	:
							if (uIn <= uOut || uOut < 0) {
								getLogger().message(Severity.warning,"Uin <= Uout || uOut < 0");
							}
							else {
								stepDownCalc();
							}
							break;
						case STEP_UP	:
							if (uIn >= uOut) {
								getLogger().message(Severity.warning,"Uin >= Uout");
							}
							else {
								stepUpCalc();
							}
							break;
						default:
							throw new UnsupportedOperationException("Schematics type ["+schematics+"] is not supported yet");
					}
				}
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
	
	private void invertedCalc() {
		final float	tOntOff = (-uOut+uF)/(uIn-uSat);
		final float tTotal = 0.001f/freq;
		final float tOn = tTotal/(1+tOntOff);

		iInPeak = iDiodePeak = 2*iOut*(1+tOntOff);
		capacitance = 1e6f * iOut*tOn/uRipple;
		inductance = 1e6f * tOn*(uIn-uSat)/iInPeak;
		iInMid = -uOut*iOut/(uIn*ETHA);
	}

	private void stepUpCalc() {
		final float	tOntOff = (uOut+uF-uIn)/(uIn-uSat);
		final float tTotal = 0.001f/freq;
		final float tOn = tTotal/(1+tOntOff);

		iInPeak = iDiodePeak = 2*iOut*(1+tOntOff);
		capacitance = 1e6f * iOut*tOn/uRipple;
		inductance = 1e6f * tOn*(uIn-uSat)/iInPeak;
		iInMid = uOut*iOut/(uIn*ETHA);
	}

	private void stepDownCalc() {
		final float	tOntOff = (uOut+uF)/(uIn-uSat-uOut);
		final float tTotal = 0.001f/freq;
		final float tOn = tTotal/(1+tOntOff);
		
		iInPeak = iDiodePeak = 2*iOut;
		capacitance = 1e6f * iInPeak*tTotal/(8.0f*uRipple);
		inductance = 1e6f * tOn*(uIn-uSat-uOut)/iInPeak;
		iInMid = uOut*iOut/(uIn*ETHA);
	}
}