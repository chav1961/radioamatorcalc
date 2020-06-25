package chav1961.calc.plugins.details.ringcurrenttrans;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.calc.interfaces.RingMyu;
import chav1961.calc.interfaces.RingType;
import chav1961.calc.plugins.details.ringcoils.RingCoilsPlugin;
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

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.details.ringcurrenttrans.RingCurrentTransPlugin/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.details.ringcurrenttrans",tooltip="menu.details.ringcurrenttrans.tt",help="help.aboutApplication")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.details.ringcurrenttrans.button.calc",tooltip="chav1961.calc.plugins.details.ringcurrenttrans.button.calc.tt"),actionString="calculate")
@PluginProperties(width=650,height=450,leftWidth=350,svgURI="schema1.SVG,schema2.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
// see "radiolyubitel" 04/2007, 05/2007 
public class RingCurrentTransPlugin implements FormManager<Object,RingCurrentTransPlugin>, ModuleAccessor {
	private static final float		MYU_0 =  1.257e-3f;
	private static final float		WIRE = 0.9f;
	private final LoggerFacade 		logger;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringcurrenttrans.currentIn",tooltip="chav1961.calc.plugins.details.ringcurrenttrans.currentIn.tt")
	@Format("9.2mpzs")
	public float currentIn = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.ringcurrenttrans.currentOut",tooltip="chav1961.calc.plugins.details.ringcurrenttrans.currentOut.tt")
	@Format("9.2mpzs")
	public float currentOut = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.ringcurrenttrans.transType",tooltip="chav1961.calc.plugins.details.ringcurrenttrans.transType.tt")
	@Format("40m")
	public CurrentTransType transType = CurrentTransType.ONE_WIRE;
	@LocaleResource(value="chav1961.calc.plugins.details.ringcurrenttrans.bSaturation",tooltip="chav1961.calc.plugins.details.ringcurrenttrans.bSaturation.tt")
	@Format("9.2mpzs")
	public float 	bSaturation = 0.7f;
	@LocaleResource(value="chav1961.calc.plugins.details.ringcurrenttrans.currentFrequency",tooltip="chav1961.calc.plugins.details.ringcurrenttrans.currentFrequency.tt")
	@Format("9.2mpzs")
	public float 	currentFrequency = 100;
	@LocaleResource(value="chav1961.calc.plugins.details.ringcurrenttrans.ringType",tooltip="chav1961.calc.plugins.details.ringcurrenttrans.ringType.tt")
	@Format("40m")
	public RingType ringType = RingType.K20x12x6;
	@LocaleResource(value="chav1961.calc.plugins.details.ringcurrenttrans.ringMyu",tooltip="chav1961.calc.plugins.details.ringcurrenttrans.ringMyu.tt")
	@Format("40m")
	public RingMyu 	ringMyu = RingMyu.MUI_140;

	
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringcurrenttrans.coilIn",tooltip="chav1961.calc.plugins.details.ringcurrenttrans.coilIn.tt")
	@Format("9.2ro")
	public float coil1 = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.ringcurrenttrans.coilOut",tooltip="chav1961.calc.plugins.details.ringcurrenttrans.coilOut.tt")
	@Format("9.2ro")
	public float coil2 = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.ringcurrenttrans.powerIn",tooltip="chav1961.calc.plugins.details.ringcurrenttrans.powerIn.tt")
	@Format("9.2ro")
	public float power1 = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.ringcurrenttrans.powerOut",tooltip="chav1961.calc.plugins.details.ringcurrenttrans.powerOut.tt")
	@Format("9.2ro")
	public float power2 = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.ringcurrenttrans.resistance",tooltip="chav1961.calc.plugins.details.ringcurrenttrans.resistance.tt")
	@Format("9.2ro")
	public float resistance = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.ringcurrenttrans.uOut",tooltip="chav1961.calc.plugins.details.ringcurrenttrans.uOut.tt")
	@Format("9.2ro")
	public float u2 = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.ringcurrenttrans.maxFrequency",tooltip="chav1961.calc.plugins.details.ringcurrenttrans.maxFrequency.tt")
	@Format("9.2ro")
	public float maxFrequency = 0;
	
	public RingCurrentTransPlugin(final LoggerFacade logger) {
		if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.logger = logger;
		}
	}
	
	@Override
	public RefreshMode onField(final RingCurrentTransPlugin inst, final Object id, final String fieldName, final Object oldValue) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onAction(final RingCurrentTransPlugin inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "app:action:/RingCurrentTransPlugin.calculate"	:
				if (currentIn == 0 || currentOut == 0) {
					getLogger().message(Severity.warning,"currentIn == 0 || currentOut == 0");
					return RefreshMode.NONE;
				}
				else {
					switch (transType) {
						case COILED		:
							coil1 = bSaturation / (MYU_0 * ringMyu.getMyu() * ringType.getMiddleLen() * currentIn); 
							final float ind1 = RingCoilsPlugin.calcInductance(ringType,ringMyu,coil1);
							final float	xInd1 = (float) (2e-3 * Math.PI * ind1 * currentFrequency); 
							final float uIn1 = currentIn * xInd1;
							
							power1 = uIn1 * currentIn;
							coil2 = currentIn * WIRE / currentOut;

							final float xInd2 = RingCoilsPlugin.calcInductance(ringType,ringMyu,coil2);
							
							u2 = uIn1 * coil2 / coil1;
							resistance = u2 / currentOut;
							power2 = u2 * currentOut;
							maxFrequency = (float) (resistance / (2e-3 * Math.PI * xInd2)); 
							break;
						case ONE_WIRE	:
							final float bMax = MYU_0 * ringMyu.getMyu() * currentIn / ringType.getMiddleLen();
							
							if (bMax > bSaturation) {
								getLogger().message(Severity.warning,"bMax > bSaturation");
								return RefreshMode.NONE;
							}
							else {
								final float inductance1 = RingCoilsPlugin.calcInductance(ringType,ringMyu,WIRE);
								final float	xInductance1 = (float) (2e-3 * Math.PI * inductance1 * currentFrequency); 
								final float uIn = currentIn * xInductance1;
								
								power1 = uIn * currentIn;
								coil1 = WIRE;
								coil2 = currentIn * WIRE / currentOut;
	
								final float xInductance2 = RingCoilsPlugin.calcInductance(ringType,ringMyu,coil2);
								
								u2 = uIn * coil2 / coil1;
								resistance = u2 / currentOut;
								power2 = u2 * currentOut;
								maxFrequency = (float) (resistance / (2e-3 * Math.PI * xInductance2)); 
								break;
							}
						default	:
							throw new UnsupportedOperationException("Transtormer type ["+transType+"] is not supported yet");
					}
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