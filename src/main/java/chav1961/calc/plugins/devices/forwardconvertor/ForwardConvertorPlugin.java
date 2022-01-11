package chav1961.calc.plugins.devices.forwardconvertor;


import chav1961.calc.interfaces.PluginProperties;
import chav1961.calc.interfaces.RingMyu;
import chav1961.calc.interfaces.RingType;
import chav1961.calc.plugins.math.MathUtils;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.basic.interfaces.ModuleAccessor;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.ui.interfaces.Action;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;

/**
 * <p>Forward convertor calculation plugin. </p> 
 * @author Alexander Chernomyrdin aka chav1961
 * @since 0.0.1
 */

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.devices.forwardconvertor.ForwardConvertorPlugin/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.devices.forwardconvertor",tooltip="menu.devices.forwardconvertor.tt",help="help.aboutApplication")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.button",tooltip="chav1961.calc.plugins.devices.forwardconvertor.button.tt"),actionString="calculate")
@PluginProperties(width=900,height=740,leftWidth=600,svgURI="schema1.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
public class ForwardConvertorPlugin implements FormManager<Object,ForwardConvertorPlugin>, ModuleAccessor {
	private static final float	MYU_0 =  1.257e-3f;
	private static final float	ETHA = 0.9f; 
	private static final float	DELTA_B = 0.1f; 
	private static final float	GAMMA = 0.5f; 
	private static final float	INV_SQRT_12 = (float) (1/ Math.sqrt(12)); 

	private final LoggerFacade 	logger;
	
	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.uIn",tooltip="chav1961.calc.plugins.devices.forwardconvertor.uIn.tt")
	@Format("9.2mpzs")
	public float 			uIn = 0;

	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.uKey",tooltip="chav1961.calc.plugins.devices.forwardconvertor.uKey.tt")
	@Format("9.2mpzs")
	public float 			uKey = 0.5f;
	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.uDiode",tooltip="chav1961.calc.plugins.devices.forwardconvertor.uDiode.tt")
	@Format("9.2mpzs")
	public float 			uDiode = 0.8f;
	
	
	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.uOut1",tooltip="chav1961.calc.plugins.devices.forwardconvertor.uOut1.tt")
	@Format("9.2mpzs")
	public float 			uOut1 = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.uOut2",tooltip="chav1961.calc.plugins.devices.forwardconvertor.uOut2.tt")
	@Format("9.2pzs")
	public float 			uOut2 = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.uOut3",tooltip="chav1961.calc.plugins.devices.forwardconvertor.uOut3.tt")
	@Format("9.2pzs")
	public float 			uOut3 = 0;

	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.iOut1",tooltip="chav1961.calc.plugins.devices.forwardconvertor.iOut1.tt")
	@Format("9.2mpzs")
	public float 			iOut1 = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.iOut2",tooltip="chav1961.calc.plugins.devices.forwardconvertor.iOut2.tt")
	@Format("9.2pzs")
	public float 			iOut2 = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.iOut3",tooltip="chav1961.calc.plugins.devices.forwardconvertor.iOut3.tt")
	@Format("9.2pzs")
	public float 			iOut3 = 0;

	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.deltaPerc",tooltip="chav1961.calc.plugins.devices.forwardconvertor.deltaPerc.tt")
	@Format("9.2mpzs")
	public float 			deltaPerc = 0.5f;
	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.kPulse",tooltip="chav1961.calc.plugins.devices.forwardconvertor.kPulse.tt")
	@Format("9.2mpzs")
	public float 			kPulse = 0.02f;
	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.frequency",tooltip="chav1961.calc.plugins.devices.forwardconvertor.frequency.tt")
	@Format("9.0mpzs")
	public float 			freq = 80f;

	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.ringType",tooltip="chav1961.calc.plugins.devices.forwardconvertor.ringType.tt")
	@Format("40m")
	public RingType ringType = RingType.K28x16x9;
	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.ringMyu",tooltip="chav1961.calc.plugins.devices.forwardconvertor.ringMyu.tt")
	@Format("40m")
	public RingMyu 	ringMyu = RingMyu.MUI_2000;

	
	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.iInMid",tooltip="chav1961.calc.plugins.devices.forwardconvertor.iInMid.tt")
	@Format("9.2ro")
	public float 			iInMid = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.iInMax",tooltip="chav1961.calc.plugins.devices.forwardconvertor.iInMax.tt")
	@Format("9.2ro")
	public float 			iInMax = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.coilsIn",tooltip="chav1961.calc.plugins.devices.forwardconvertor.coilsIn.tt")
	@Format("9.2ro")
	public float 			coilsIn = 0;
	
	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.coilsOut1",tooltip="chav1961.calc.plugins.devices.forwardconvertor.coilsOut1.tt")
	@Format("9.2ro")
	public float 			coilsOut1 = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.coilsOut2",tooltip="chav1961.calc.plugins.devices.forwardconvertor.coilsOut2.tt")
	@Format("9.2ro")
	public float 			coilsOut2 = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.coilsOut3",tooltip="chav1961.calc.plugins.devices.forwardconvertor.coilsOut3.tt")
	@Format("9.2ro")
	public float 			coilsOut3 = 0;
	

	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.lOut1",tooltip="chav1961.calc.plugins.devices.forwardconvertor.lOut1.tt")
	@Format("9.2ro")
	public float 			lOut1 = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.lOut2",tooltip="chav1961.calc.plugins.devices.forwardconvertor.lOut2.tt")
	@Format("9.2ro")
	public float 			lOut2 = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.lOut3",tooltip="chav1961.calc.plugins.devices.forwardconvertor.lOut3.tt")
	@Format("9.2ro")
	public float 			lOut3 = 0;

	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.cOut1",tooltip="chav1961.calc.plugins.devices.forwardconvertor.cOut1.tt")
	@Format("9.2ro")
	public float 			cOut1 = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.cOut2",tooltip="chav1961.calc.plugins.devices.forwardconvertor.cOut2.tt")
	@Format("9.2ro")
	public float 			cOut2 = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.forwardconvertor.cOut3",tooltip="chav1961.calc.plugins.devices.forwardconvertor.cOut3.tt")
	@Format("9.2ro")
	public float 			cOut3 = 0;
	
	
	public ForwardConvertorPlugin(final LoggerFacade logger) {
		if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.logger = logger;
		}
	}
	
	@Override
	public RefreshMode onField(final ForwardConvertorPlugin inst, final Object id, final String fieldName, final Object oldValue, final boolean beforeCommit) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onAction(final ForwardConvertorPlugin inst, final Object id, final String actionName, final Object... parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "app:action:/ForwardConvertorPlugin.calculate"	:
				final float	totalConsumed = (uOut1 * iOut1 + uOut2 * iOut2 + uOut3 * iOut3) / ETHA;
				final float sWinSkern = (float) (11.9e-3 * totalConsumed / (0.141 * DELTA_B * freq));
				final float lc = 1e-6f * (1 - GAMMA) / (8 * kPulse * freq * freq);
				
				if (ringType.getSquare()*ringType.getSquareWindow() >= sWinSkern) {
					coilsIn = 1e3f * GAMMA * (uIn - uKey) / (freq * DELTA_B * ringType.getSquare());
					iInMax = 0;
					iInMid = 0;
					
					final float deltaUrl1 = kPulse * uOut1;  
					final float	kTr1 = 0.95f * ((uIn - uKey)) * GAMMA / (uOut1 + uDiode + deltaUrl1);
					coilsOut1 = coilsIn / kTr1;
					lOut1 = 1e3f * ((1/kTr1) * (uIn - uKey) + uDiode + deltaUrl1) * GAMMA / (2 * iOut1 * freq);
					final float	deltaIrl1 = 1e3f * ((1/kTr1) * (uIn - uKey) * (1 - GAMMA) + uDiode + deltaUrl1) * GAMMA / (2 * lOut1 * freq);
					final float	deltaIrl1Mid = deltaIrl1 * INV_SQRT_12;
					cOut1 = 1e12f * lc / lOut1;
					final float iOut1Max = iOut1 + deltaIrl1 / 2;
					final float iOut1Mid = (float) ((iOut1Max - deltaIrl1 / 2) * Math.sqrt(GAMMA));
					iInMax += iOut1Max / kTr1;
					iInMid += iOut1Mid / kTr1;
					
					if (uOut2 * iOut2 > 0) {
						final float deltaUrl2 = kPulse * uOut2;  
						final float	kTr2 = 0.95f * ((uIn - uKey)) * GAMMA / (uOut2 + uDiode + deltaUrl2);
						coilsOut2 = coilsIn / kTr2;
						lOut2 = 1e3f * ((1/kTr2) * (uIn - uKey) + uDiode + deltaUrl2) * GAMMA / (2 * iOut2 * freq);
						final float	deltaIrl2 = 1e3f * ((1/kTr2) * (uIn - uKey) * (1 - GAMMA) + uDiode + deltaUrl2) * GAMMA / (2 * lOut2 * freq);
						final float	deltaIrl2Mid = deltaIrl2 * INV_SQRT_12;  
						cOut2 = 1e12f * lc / lOut2;
						final float iOut2Max = iOut2 + deltaIrl2 / 2;
						final float iOut2Mid = (float) ((iOut2Max - deltaIrl2 / 2) * Math.sqrt(GAMMA));
						iInMax += iOut2Max / kTr2;
						iInMid += iOut2Mid / kTr2;
					}

					if (uOut3 * iOut3 > 0) {
						final float deltaUrl3 = kPulse * uOut3;  
						final float	kTr3 = 0.95f * ((uIn - uKey)) * GAMMA / (uOut3 + uDiode + deltaUrl3);
						coilsOut3 = coilsIn / kTr3;
						lOut3 = 1e3f * ((1/kTr3) * (uIn - uKey) + uDiode + deltaUrl3) * GAMMA / (2 * iOut2 * freq);
						final float	deltaIrl3 = 1e3f * ((1/kTr3) * (uIn - uKey) * (1 - GAMMA) + uDiode + deltaUrl3) * GAMMA / (2 * lOut3 * freq);
						cOut3 = 1e12f * lc / lOut3;
						final float iOut3Max = iOut3 + deltaIrl3 / 2;
						final float iOut3Mid = (float) ((iOut3Max - deltaIrl3 / 2) * Math.sqrt(GAMMA));
						iInMax += iOut3Max / kTr3;
						iInMid += iOut3Mid / kTr3;
					}
					return RefreshMode.RECORD_ONLY;
				}
				else {
					getLogger().message(Severity.warning,"Sk*Sw ring < Sk*Sw required");
					return RefreshMode.DEFAULT;
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