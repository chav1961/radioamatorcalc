package chav1961.calc.plugins.details.ringcoils;

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

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.details.ringcoils.RingCoilsPlugin/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.details.ringcoils",tooltip="menu.details.ringcoils.tt",help="help.aboutApplication")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.button.inductance",tooltip="chav1961.calc.plugins.details.ringcoils.button.inductance.tt"),actionString="calcInductance")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.button.coils",tooltip="chav1961.calc.plugins.details.ringcoils.button.coils.tt"),actionString="calcCoils")
@PluginProperties(width=600,height=300,leftWidth=250,svgURI="schema.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
public class RingCoilsPlugin implements FormManager<Object,RingCoilsPlugin> {
	private static final double		MYU0 = 4e-7 * Math.PI;
	private static final double		MYUr = 1;
	private static final double		R_CUPRUM =  0.0171;	// Ohm • mm²/m;
	private static final double		K_SKIN1000 = 1.98;	// mm 1000 Hz;
	private static final double[][]	B_COEFF = {{0,0},{1,0},{2,0.12},{3,0.16},{4,0.19},{5,0.22},{6,0.23},{7,0.24},{8,0.25},{10,0.26},{15,0.28},{20,0.295},{30,0.31},{40,0.315},{50,0.32},{80,0.325}};
	
	private final LoggerFacade 	logger;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.numberOfCoils",tooltip="chav1961.calc.plugins.details.ringcoils.numberOfCoils.tt")
	@Format("9.2pzs")
	public float coilsNumberOfCoils = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.inductance",tooltip="chav1961.calc.plugins.details.ringcoils.inductance.tt")
	@Format("9.2pzs")
	public float coilsInductance = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.wireDiameter",tooltip="chav1961.calc.plugins.details.ringcoils.wireDiameter.tt")
	@Format("9.2mpzs")
	public float wireDiameter = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringType",tooltip="chav1961.calc.plugins.details.ringcoils.ringType.tt")
	@Format("40m")
	public RingType ringType = RingType.K20x12x6;
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringMyu",tooltip="chav1961.calc.plugins.details.ringcoils.ringMyu.tt")
	@Format("40m")
	public RingMyu 	pingMyu = RingMyu.MUI_2000;
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.frequency",tooltip="chav1961.calc.plugins.details.ringcoils.frequency.tt")
	@Format("9.2pzs")
	public float frequency = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.length",tooltip="chav1961.calc.plugins.details.ringcoils.length.tt")
	@Format("9.2r")
	public float wireLength = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.quality",tooltip="chav1961.calc.plugins.details.ringcoils.quality.tt")
	@Format("9.2r")
	public float quality = 0;

	
	public RingCoilsPlugin(final LoggerFacade logger) {
		if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.logger = logger;
		}
	}
	
	@Override
	public RefreshMode onField(final RingCoilsPlugin inst, final Object id, final String fieldName, final Object oldValue) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onAction(final RingCoilsPlugin inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "app:action:/CoilsPlugin.calcInductance"	:
//				if (coilsDiameter == 0 || coilsLength == 0 || wireDiameter == 0 || coilsNumberOfCoils == 0) {
//					getLogger().message(Severity.warning,"D == 0 || len == 0 || w == 0 || Dw == 0");
//					return RefreshMode.NONE;
//				}
//				else {
//					switch (coilType) {
//						case MULTI_LAYER	:
//							coilsInductance = (float)multiLayerInductance(coilsDiameter,coilsLength,coilsNumberOfCoils,wireDiameter);
//							wireLength = (float) (Math.PI * (coilsDiameter + wireDiameter * wireDiameter * coilsNumberOfCoils / coilsLength) * coilsNumberOfCoils);
//							break;
//						case SINGLE_LAYER	:
//							coilsInductance = (float)singleLayerInductance(coilsDiameter,coilsLength,coilsNumberOfCoils,wireDiameter);
//							wireLength = (float) (Math.PI * (coilsDiameter+wireDiameter) * coilsNumberOfCoils);
//							break;
//						case STEPPED		:
//							coilsInductance = (float)steppedInductance(coilsDiameter,coilsLength,coilsNumberOfCoils,wireDiameter);
//							wireLength = (float) (Math.PI * (coilsDiameter+wireDiameter) * coilsNumberOfCoils + coilsLength);
//							break;
//						default:
//							throw new UnsupportedOperationException("Coil type ["+coilType+"] is not supported yet");
//					}
//					if (frequency > 0) {
//						quality = (float) calcQuality(frequency,coilsInductance,wireLength,wireDiameter);
//					}
//					return RefreshMode.RECORD_ONLY;
//				}
			case "app:action:/CoilsPlugin.calcCoils"	:
//				if (coilsDiameter == 0 || coilsLength == 0 || wireDiameter == 0 || coilsInductance == 0) {
//					getLogger().message(Severity.warning,"D == 0 || len == 0 || L == 0 || Dw == 0");
//					return RefreshMode.NONE;
//				}
//				else {
//					switch (coilType) {
//						case MULTI_LAYER	:
//							coilsNumberOfCoils = 2*coilsLength/wireDiameter;
//							if (multiLayerInductance(coilsDiameter,coilsLength,coilsNumberOfCoils,wireDiameter) > coilsInductance) {
//								while (coilsNumberOfCoils > 0 && multiLayerInductance(coilsDiameter,coilsLength,coilsNumberOfCoils,wireDiameter) > coilsInductance) {
//									coilsNumberOfCoils -= 1;
//								}
//								if (coilsNumberOfCoils <= 0) {
//									getLogger().message(Severity.warning,"iteration error - N <= 0!");
//									coilsNumberOfCoils = 0;
//									return RefreshMode.NONE;
//								}
//							}
//							else {
//								while (coilsNumberOfCoils < 10000 && multiLayerInductance(coilsDiameter,coilsLength,coilsNumberOfCoils,wireDiameter) < coilsInductance) {
//									coilsNumberOfCoils += 1;
//								}
//								if (coilsNumberOfCoils >= 0) {
//									getLogger().message(Severity.warning,"iteration error - N > 10000!");
//									coilsNumberOfCoils = 0;
//									return RefreshMode.NONE;
//								}
//							}
//							wireLength = (float) (Math.PI * (coilsDiameter + wireDiameter * wireDiameter * coilsNumberOfCoils / coilsLength) * coilsNumberOfCoils);
//							break;
//						case SINGLE_LAYER	:
//							final double	midDiameter = coilsDiameter;
//							final double	sq = Math.PI*midDiameter*midDiameter/4;
//							
//							coilsNumberOfCoils = (float)Math.sqrt((coilsInductance*1e-6*coilsLength*1e-3)/(MYU0*MYUr*sq*1e-6));
//							wireLength = (float) (Math.PI * (coilsDiameter+wireDiameter) * coilsNumberOfCoils);
//							break;
//						case STEPPED		:
//							final double	midDiameterS = coilsDiameter;
//							final double	sqS = Math.PI*midDiameterS*midDiameterS/4;
//							
//							coilsNumberOfCoils = (float)Math.sqrt((coilsInductance*1e-6*coilsLength*1e-3)/(MYU0*MYUr*sqS*1e-6));
//							while (coilsNumberOfCoils > 0 && steppedInductance(coilsDiameter,coilsLength,coilsNumberOfCoils,wireDiameter) > coilsInductance) {
//								coilsNumberOfCoils -= 0.25; 
//							}
//							if (coilsNumberOfCoils <= 0) {
//								getLogger().message(Severity.warning,"iteration error - N <= 0!");
//								coilsNumberOfCoils = 0;
//								return RefreshMode.NONE;
//							}
//							wireLength = (float) (Math.PI * (coilsDiameter+wireDiameter) * coilsNumberOfCoils + coilsLength);
//							break;
//						default:
//							throw new UnsupportedOperationException("Coil type ["+coilType+"] is not supported yet");
//					}
//					if (frequency > 0) {
//						quality = (float) calcQuality(frequency,coilsInductance,wireLength,wireDiameter);
//					}
//					return RefreshMode.RECORD_ONLY;
//				}
			default :
				throw new UnsupportedOperationException("Unknown action string ["+actionName+"]");
		}
	}
	
	@Override
	public LoggerFacade getLogger() {
		return logger;
	}

	private static double getA(final double wireDiameter, final double step) {
		final double	k = wireDiameter / step;
		
		if (k < 0.05) {
			return -2.2;
		}
		else if (k < 0.1) {
			return -1.8;
		}
		else if (k < 0.15) {
			return -1.4;
		}
		else if (k < 0.2) {
			return -1.1;
		}
		else if (k < 0.25) {
			return -0.8;
		}
		else if (k < 0.3) {
			return -0.65;
		}
		else if (k < 0.35) {
			return -0.5;
		}
		else if (k < 0.4) {
			return -0.35;
		}
		else if (k < 0.45) {
			return -0.2;
		}
		else if (k < 0.5) {
			return -0.15;
		}
		else if (k < 0.55) {
			return -0.05;
		}
		else if (k < 0.6) {
			return 0.05;
		}
		else if (k < 0.65) {
			return 0.1;
		}
		else if (k < 0.7) {
			return 0.18;
		}
		else if (k < 0.75) {
			return 0.25;
		}
		else if (k < 0.8) {
			return 0.3;
		}
		else if (k < 0.85) {
			return 0.37;
		}
		else if (k < 0.9) {
			return 0.45;
		}
		else if (k < 0.95) {
			return 0.50;
		}
		else if (k < 1.0) {
			return 0.55;
		}
		else {
			return 0.6;
		}
	}
	
	private static double getB(final double numberOfCoils) {
		final double[][]	tmp = B_COEFF;
		
		for (int index = 1, maxIndex = tmp.length; index < maxIndex; index++) {
			if (numberOfCoils >= tmp[index-1][0] && numberOfCoils <= tmp[index-1][0]) {	// Simple linear interpolation...
				return tmp[index-1][1]+(tmp[index][1]-tmp[index-1][1])*(numberOfCoils-tmp[index-1][0])/(tmp[index][0]-tmp[index-1][0]);
			}
		}
		return 0.327; 
	}
	
	private static double getAlpha(final float wireDiameter) {
		if (wireDiameter < 0.08) {
			return 1.35;
		}
		else if (wireDiameter < 0.11) {
			return 1.3;
		}
		else if (wireDiameter < 0.15) {
			return 1.275;
		}
		else if (wireDiameter < 0.25) {
			return 1.25;
		}
		else if (wireDiameter < 0.35) {
			return 1.225;
		}
		else if (wireDiameter < 0.41) {
			return 1.2;
		}
		else if (wireDiameter < 0.51) {
			return 1.15;
		}
		else if (wireDiameter < 0.91) {
			return 1.1;
		}
		else {
			return 1.05;
		}
	}
	
	private static double singleLayerInductance(final float diameter, final float length, final float coils, final float wireDiameter) {
		final double	n2 = coils*coils;
		final double	midDiameter = diameter;
		final double	sq = Math.PI*midDiameter*midDiameter/4;
		
		return MYU0*MYUr*n2*sq/(length*1e-3);
	}

	private static double steppedInductance(final float diameter, final float length, final float coils, final float wireDiameter) {
		final double	midDiameterS = diameter;
		
		return singleLayerInductance(diameter,length,coils,wireDiameter) - 2 * Math.PI * midDiameterS*1e-1 * coils * getA(wireDiameter,(length-wireDiameter*coils)/coils) * getB(coils);
	}	

	private static double multiLayerInductance(final float diameter, final float length, final float coils, final float wireDiameter) {
		final double	n2M = coils*coils;
		final double	thick = (wireDiameter*wireDiameter) * coils / (length * getAlpha(wireDiameter));
		final double	midDiameterM = diameter + thick;
		
		return 0.08*(midDiameterM*1e-1)*(midDiameterM*1e-1)*n2M/(1e-1*(3*midDiameterM + 9*length + 10*thick));
	}	

	private static double calcQuality(final float frequency, final float inductance, final float length, final float diameter) {
		final double	lSkin = K_SKIN1000 / Math.sqrt(frequency);
		final double	sq = Math.PI * diameter * diameter / 4;
		final double	sqWithoutSkin = lSkin > diameter/2 ? 0 : Math.PI * (diameter-2*lSkin) * (diameter-2*lSkin) / 4;
		final double	sqSkin = sq - sqWithoutSkin; 
		final double	rSkin = 1e-3 * R_CUPRUM * length / sqSkin;
		
		return 1e-3 * 2 * Math.PI * frequency * inductance / rSkin;
	}
}