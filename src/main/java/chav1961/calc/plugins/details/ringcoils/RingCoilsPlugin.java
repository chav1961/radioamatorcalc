package chav1961.calc.plugins.details.ringcoils;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.calc.interfaces.RingMyu;
import chav1961.calc.interfaces.RingType;
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

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.details.ringcoils.RingCoilsPlugin/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.details.ringcoils",tooltip="menu.details.ringcoils.tt",help="help.aboutApplication")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.button.inductance",tooltip="chav1961.calc.plugins.details.ringcoils.button.inductance.tt"),actionString="calcInductance")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.button.coils",tooltip="chav1961.calc.plugins.details.ringcoils.button.coils.tt"),actionString="calcCoils")
@PluginProperties(width=600,height=300,leftWidth=300,svgURI="schema.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
public class RingCoilsPlugin implements FormManager<Object,RingCoilsPlugin>, ModuleAccessor {
	private static final double		R_CUPRUM =  0.0171;	// Ohm • mm²/m;
	private static final double		K_SKIN1000 = 1.98;	// mm 1000 Hz;
	
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
	public RingMyu 	ringMyu = RingMyu.MUI_2000;
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
			case "app:action:/RingCoilsPlugin.calcInductance"	:
				if (wireDiameter == 0 || coilsNumberOfCoils == 0) {
					getLogger().message(Severity.warning,"N == 0 || Dw == 0");
					return RefreshMode.NONE;
				}
				else if (!canFill(ringType.getInnerDiameter(),wireDiameter,coilsNumberOfCoils)) {
					getLogger().message(Severity.warning,"Coils not filled in the ring window");
					return RefreshMode.NONE;
				}
				else {
					if (ringType.getOuterDiameter()/ringType.getInnerDiameter() > 1.75f) {
						coilsInductance = (float) (2e-4 * ringMyu.getMyu() * ringType.getHeight() * Math.log(ringType.getOuterDiameter()/ringType.getInnerDiameter()) * coilsNumberOfCoils * coilsNumberOfCoils); 
					}
					else {
						coilsInductance = (float) (4e-4 * ringMyu.getMyu() * ringType.getHeight() * (ringType.getOuterDiameter() - ringType.getInnerDiameter()) * coilsNumberOfCoils * coilsNumberOfCoils / (ringType.getOuterDiameter() + ringType.getInnerDiameter()));
					}
					wireLength = (float) calcLength(ringType,wireDiameter,coilsNumberOfCoils);
					if (frequency > 0) {
						quality = (float) calcQuality(frequency,coilsInductance,wireLength,wireDiameter);
					}
					return RefreshMode.RECORD_ONLY;
				}
			case "app:action:/RingCoilsPlugin.calcCoils"	:
				if (wireDiameter == 0 || coilsInductance == 0) {
					getLogger().message(Severity.warning,"L == 0 || Dw == 0");
					return RefreshMode.NONE;
				}
				else {
					if (ringType.getOuterDiameter()/ringType.getInnerDiameter() > 1.75f) {
						coilsNumberOfCoils = (float) (100 * Math.sqrt(coilsInductance / (2 * ringMyu.getMyu() * ringType.getHeight() * Math.log(ringType.getOuterDiameter()/ringType.getInnerDiameter()))));
					}
					else {
						coilsNumberOfCoils = (float) (100 * Math.sqrt(coilsInductance * (ringType.getOuterDiameter() + ringType.getInnerDiameter())/ (4 * ringMyu.getMyu() * ringType.getHeight() * (ringType.getOuterDiameter() - ringType.getInnerDiameter()))));
					}
					if (!canFill(ringType.getInnerDiameter(),wireDiameter,coilsNumberOfCoils)) {
						getLogger().message(Severity.warning,"Coils not filled in the ring window");
						return RefreshMode.NONE;
					}
					else {
						wireLength = (float) calcLength(ringType,wireDiameter,coilsNumberOfCoils);
						if (frequency > 0) {
							quality = (float) calcQuality(frequency,coilsInductance,wireLength,wireDiameter);
						}
						return RefreshMode.RECORD_ONLY;
					}
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

	private static boolean canFill(final float windowDiameter, final float wireDiameter, final float numberOfCoils) {
		return (wireDiameter * getAlpha(wireDiameter)) * (wireDiameter * getAlpha(wireDiameter)) < windowDiameter * windowDiameter;  
	}
	
	private static double calcLength(final RingType ringType, final float wireDiameter, final float numberOfCoils) {
		float	restCoils = numberOfCoils;
		float	currentInnerDiameter = ringType.getInnerDiameter() - wireDiameter;
		float	currentOuterDiameter = ringType.getOuterDiameter() + wireDiameter;
		float	currentHeight = ringType.getHeight() + wireDiameter;
		float	currentLength = 0;
		
		do {final int	currentCoils = (int) Math.min(Math.PI * currentInnerDiameter / wireDiameter,restCoils);
			
			currentLength += currentCoils * (currentOuterDiameter - currentInnerDiameter + 2 * currentHeight);
			currentInnerDiameter -= wireDiameter;
			currentOuterDiameter += wireDiameter;
			currentHeight += 2 * wireDiameter;
			restCoils -= currentCoils;
		} while (restCoils >= 1);
		return currentLength;
	}
	
	private static double calcQuality(final float frequency, final float inductance, final float length, final float diameter) {
		final double	lSkin = K_SKIN1000 / Math.sqrt(frequency);
		final double	sq = Math.PI * diameter * diameter / 4;
		final double	sqWithoutSkin = lSkin > diameter/2 ? 0 : Math.PI * (diameter-2*lSkin) * (diameter-2*lSkin) / 4;
		final double	sqSkin = sq - sqWithoutSkin; 
		final double	rSkin = 1e-3 * R_CUPRUM * length / sqSkin;
		
		return 2e-3 * Math.PI * frequency * inductance / rSkin;
	}
}