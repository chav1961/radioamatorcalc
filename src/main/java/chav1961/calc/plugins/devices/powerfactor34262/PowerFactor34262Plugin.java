package chav1961.calc.plugins.devices.powerfactor34262;

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
 * <p>Power factor calculation plugin. Calculations are based on formulas in MC34262 datasheet.</p> 
 * @author Alexander Chernomyrdin aka chav1961
 * @since 0.0.1
 */

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.devices.powerfactor34262.PowerFactor34262Plugin/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.devices.powerfactor34262",tooltip="menu.devices.powerfactor34262.tt",help="help.aboutApplication")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.devices.powerfactor34262.button",tooltip="chav1961.calc.plugins.devices.powerfactor34262.button.tt"),actionString="calculate")
@PluginProperties(width=650,height=400,leftWidth=350,svgURI="schema.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
public class PowerFactor34262Plugin implements FormManager<Object,PowerFactor34262Plugin>, ModuleAccessor {
	private static final float	ETHA = 0.92f; 
	private static final float	SQRT_2 = (float) Math.sqrt(2); 

	private final LoggerFacade 	logger;
	
	@LocaleResource(value="chav1961.calc.plugins.devices.powerfactor34262.uIn",tooltip="chav1961.calc.plugins.devices.powerfactor34262.uIn.tt")
	@Format("9.2mpzs")
	public float 			uIn = 230;
	@LocaleResource(value="chav1961.calc.plugins.devices.powerfactor34262.uOut",tooltip="chav1961.calc.plugins.devices.powerfactor34262.uOut.tt")
	@Format("9.2mpzs")
	public float 			uOut = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.powerfactor34262.iOut",tooltip="chav1961.calc.plugins.devices.powerfactor34262.iOut.tt")
	@Format("9.2mpzs")
	public float 			iOut = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.powerfactor34262.tPulse",tooltip="chav1961.calc.plugins.devices.powerfactor34262.tPulse.tt")
	@Format("9.2mpzs")
	public float 			tPulse = 20;
	@LocaleResource(value="chav1961.calc.plugins.devices.powerfactor34262.uCS",tooltip="chav1961.calc.plugins.devices.powerfactor34262.uCS.tt")
	@Format("9.2mpzs")
	public float 			uCS = 0.5f;
	
	@LocaleResource(value="chav1961.calc.plugins.devices.powerfactor34262.iPeak",tooltip="chav1961.calc.plugins.devices.powerfactor34262.iPeak.tt")
	@Format("9.2ro")
	public float 			iPeak = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.powerfactor34262.inductance",tooltip="chav1961.calc.plugins.devices.powerfactor34262.inductance.tt")
	@Format("9.2ro")
	public float 			inductance = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.powerfactor34262.tOn",tooltip="chav1961.calc.plugins.devices.powerfactor34262.tOn.tt")
	@Format("9.2ro")
	public float 			tOn = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.powerfactor34262.tOff",tooltip="chav1961.calc.plugins.devices.powerfactor34262.tOff.tt")
	@Format("9.2ro")
	public float 			tOff = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.powerfactor34262.frequency",tooltip="chav1961.calc.plugins.devices.powerfactor34262.frequency.tt")
	@Format("9.2ro")
	public float 			frequency = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.powerfactor34262.rCS",tooltip="chav1961.calc.plugins.devices.powerfactor34262.rCS.tt")
	@Format("9.2ro")
	public float 			rCS = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.powerfactor34262.r1",tooltip="chav1961.calc.plugins.devices.powerfactor34262.r1.tt")
	@Format("9.2ro")
	public float 			r1 = 0;
	
	
	public PowerFactor34262Plugin(final LoggerFacade logger) {
		if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.logger = logger;
		}
	}
	
	@Override
	public RefreshMode onField(final PowerFactor34262Plugin inst, final Object id, final String fieldName, final Object oldValue, final boolean beforeCommit) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onAction(final PowerFactor34262Plugin inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "app:action:/PowerFactor34262Plugin.calculate"	:
				if (uIn == 0 || uOut == 0 || iOut == 0 || tPulse == 0 || uCS == 0) {
					getLogger().message(Severity.warning,"uIn == 0 || uOut == 0 || iOut == 0 || tPulse == 0 || uCS == 0");
					return RefreshMode.NONE;
				}
				else {
					final float	outputPower = uOut*iOut;
					
					iPeak = 2 * SQRT_2 * outputPower / (ETHA * uIn);
					inductance = tPulse * (uOut/SQRT_2 - uIn) * ETHA * uIn * uIn / (SQRT_2 * uOut * outputPower);
					tOn = 2 * outputPower * inductance / (ETHA * uIn * uIn);
					tOff = tOn / (uOut/(SQRT_2*uIn) - 1);
					frequency = 1e3f / (tOn + tOff);
					rCS = uCS / iPeak;
					r1 = 10 * (uOut - 2.5f)/2.5f;
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
}