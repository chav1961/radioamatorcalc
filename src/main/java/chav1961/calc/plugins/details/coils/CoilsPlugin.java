package chav1961.calc.plugins.details.coils;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.calc.plugins.details.CoilsUtil;
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

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.details.coils.CoilsPlugin/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.details.coils",tooltip="menu.details.coils.tt",help="help.aboutApplication")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.details.coils.button.inductance",tooltip="chav1961.calc.plugins.details.coils.button.inductance.tt"),actionString="calcInductance")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.details.coils.button.coils",tooltip="chav1961.calc.plugins.details.coils.button.coils.tt"),actionString="calcCoils")
@PluginProperties(width=600,height=300,leftWidth=250,svgURI="schema.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
public class CoilsPlugin implements FormManager<Object,CoilsPlugin>, ModuleAccessor {
	private static final double		MYUr = 1;
	private static final double		R_CUPRUM =  0.0171;	// Ohm • mm²/m;
	private static final double		K_SKIN1000 = 1.98;	// mm 1000 Hz;
	private static final double[][]	B_COEFF = {{0,0},{1,0},{2,0.12},{3,0.16},{4,0.19},{5,0.22},{6,0.23},{7,0.24},{8,0.25},{10,0.26},{15,0.28},{20,0.295},{30,0.31},{40,0.315},{50,0.32},{80,0.325}};
	
	private final LoggerFacade 	logger;
	
	@LocaleResource(value="chav1961.calc.plugins.details.coils.coilsDiameter",tooltip="chav1961.calc.plugins.details.coils.coilsDiameter.tt")
	@Format("9.2mpzs")
	public float coilsDiameter = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.coils.coilsLength",tooltip="chav1961.calc.plugins.details.coils.coilsLength.tt")
	@Format("9.2mpzs")
	public float coilsLength = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.coils.numberOfCoils",tooltip="chav1961.calc.plugins.details.coils.numberOfCoils.tt")
	@Format("9.2pzs")
	public float coilsNumberOfCoils = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.coils.inductance",tooltip="chav1961.calc.plugins.details.coils.inductance.tt")
	@Format("9.2pzs")
	public float coilsInductance = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.coils.wireDiameter",tooltip="chav1961.calc.plugins.details.coils.wireDiameter.tt")
	@Format("9.2mpzs")
	public float wireDiameter = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.coils.coilType",tooltip="chav1961.calc.plugins.details.coils.coilType.tt")
	@Format("40m")
	public CoilType coilType = CoilType.SINGLE_LAYER;
	@LocaleResource(value="chav1961.calc.plugins.details.coils.frequency",tooltip="chav1961.calc.plugins.details.coils.frequency.tt")
	@Format("9.2pzs")
	public float frequency = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.coils.length",tooltip="chav1961.calc.plugins.details.coils.length.tt")
	@Format("9.2r")
	public float wireLength = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.coils.quality",tooltip="chav1961.calc.plugins.details.coils.quality.tt")
	@Format("9.2r")
	public float quality = 0;

	
	public CoilsPlugin(final LoggerFacade logger) {
		if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.logger = logger;
		}
	}
	
	@Override
	public RefreshMode onField(final CoilsPlugin inst, final Object id, final String fieldName, final Object oldValue, final boolean beforeCommit) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onAction(final CoilsPlugin inst, final Object id, final String actionName, final Object... parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "app:action:/CoilsPlugin.calcInductance"	:
				if (coilsDiameter == 0 || coilsLength == 0 || wireDiameter == 0 || coilsNumberOfCoils == 0) {
					getLogger().message(Severity.warning,"D == 0 || len == 0 || w == 0 || Dw == 0");
					return RefreshMode.NONE;
				}
				else {
					switch (coilType) {
						case MULTI_LAYER	:
							coilsInductance = (float)multiLayerInductance(coilsDiameter,coilsLength,coilsNumberOfCoils,wireDiameter);
							wireLength = (float) (Math.PI * (coilsDiameter + wireDiameter * wireDiameter * coilsNumberOfCoils / coilsLength) * coilsNumberOfCoils);
							break;
						case SINGLE_LAYER	:
							coilsInductance = (float)singleLayerInductance(coilsDiameter,coilsLength,coilsNumberOfCoils,wireDiameter);
							wireLength = (float) (Math.PI * (coilsDiameter+wireDiameter) * coilsNumberOfCoils);
							break;
						case PITCHED		:
							coilsInductance = (float)steppedInductance(coilsDiameter,coilsLength,coilsNumberOfCoils,wireDiameter);
							wireLength = (float) (Math.PI * (coilsDiameter+wireDiameter) * coilsNumberOfCoils + coilsLength);
							break;
						default:
							throw new UnsupportedOperationException("Coil type ["+coilType+"] is not supported yet");
					}
					if (frequency > 0) {
						quality = (float) calcQuality(frequency,coilsInductance,coilsNumberOfCoils,coilsDiameter,wireDiameter);
					}
					return RefreshMode.RECORD_ONLY;
				}
			case "app:action:/CoilsPlugin.calcCoils"	:
				if (coilsDiameter == 0 || coilsLength == 0 || wireDiameter == 0 || coilsInductance == 0) {
					getLogger().message(Severity.warning,"D == 0 || len == 0 || L == 0 || Dw == 0");
					return RefreshMode.NONE;
				}
				else {
					switch (coilType) {
						case MULTI_LAYER	:
							coilsNumberOfCoils = 2*coilsLength/wireDiameter;
							if (multiLayerInductance(coilsDiameter,coilsLength,coilsNumberOfCoils,wireDiameter) > coilsInductance) {
								while (coilsNumberOfCoils > 0 && multiLayerInductance(coilsDiameter,coilsLength,coilsNumberOfCoils,wireDiameter) > coilsInductance) {
									coilsNumberOfCoils -= 1;
								}
								if (coilsNumberOfCoils <= 0) {
									getLogger().message(Severity.warning,"iteration error - N <= 0!");
									coilsNumberOfCoils = 0;
									return RefreshMode.NONE;
								}
							}
							else {
								while (coilsNumberOfCoils < 10000 && multiLayerInductance(coilsDiameter,coilsLength,coilsNumberOfCoils,wireDiameter) < coilsInductance) {
									coilsNumberOfCoils += 1;
								}
								if (coilsNumberOfCoils >= 0) {
									getLogger().message(Severity.warning,"iteration error - N > 10000!");
									coilsNumberOfCoils = 0;
									return RefreshMode.NONE;
								}
							}
							wireLength = (float) (Math.PI * (coilsDiameter + wireDiameter * wireDiameter * coilsNumberOfCoils / coilsLength) * coilsNumberOfCoils);
							break;
						case SINGLE_LAYER	:
							final double	midDiameter = coilsDiameter;
							final double	sq = Math.PI*midDiameter*midDiameter/4;
							
							coilsNumberOfCoils = (float)Math.sqrt((coilsInductance*1e-6*coilsLength*1e-3)/(CoilsUtil.MYU0*MYUr*sq*1e-6));
							wireLength = (float) (Math.PI * (coilsDiameter+wireDiameter) * coilsNumberOfCoils);
							break;
						case PITCHED		:
							final double	midDiameterS = coilsDiameter;
							final double	sqS = Math.PI*midDiameterS*midDiameterS/4;
							
							coilsNumberOfCoils = (float)Math.sqrt((coilsInductance*1e-6*coilsLength*1e-3)/(CoilsUtil.MYU0*MYUr*sqS*1e-6));
							while (coilsNumberOfCoils > 0 && steppedInductance(coilsDiameter,coilsLength,coilsNumberOfCoils,wireDiameter) > coilsInductance) {
								coilsNumberOfCoils -= 0.25; 
							}
							if (coilsNumberOfCoils <= 0) {
								getLogger().message(Severity.warning,"iteration error - N <= 0!");
								coilsNumberOfCoils = 0;
								return RefreshMode.NONE;
							}
							wireLength = (float) (Math.PI * (coilsDiameter+wireDiameter) * coilsNumberOfCoils + coilsLength);
							break;
						default:
							throw new UnsupportedOperationException("Coil type ["+coilType+"] is not supported yet");
					}
					if (frequency > 0) {
						quality = (float) calcQuality(frequency,coilsInductance,coilsNumberOfCoils,coilsDiameter,wireDiameter);
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
	
	private static double singleLayerInductance(final float diameter, final float length, final float coils, final float wireDiameter) {
		final double	n2 = coils*coils;
		final double	midDiameter = diameter;
		final double	sq = Math.PI*midDiameter*midDiameter/4;
		
		return CoilsUtil.MYU0*MYUr*n2*sq/(length*1e-3);
	}

	private static double steppedInductance(final float diameter, final float length, final float coils, final float wireDiameter) {
		final double	midDiameterS = diameter;
		
		return singleLayerInductance(diameter,length,coils,wireDiameter) - 2 * Math.PI * midDiameterS*1e-1 * coils * CoilsUtil.getAForInductance(wireDiameter,(length-wireDiameter*coils)/coils) * CoilsUtil.getBForInductance(coils);
	}	

	private static double multiLayerInductance(final float diameter, final float length, final float coils, final float wireDiameter) {
		final double	n2M = coils*coils;
		final double	thick = (wireDiameter*wireDiameter) * coils / (length * CoilsUtil.getFillFactorForWire(wireDiameter));
		final double	midDiameterM = diameter + thick;
		
		return 0.08*(midDiameterM*1e-1)*(midDiameterM*1e-1)*n2M/(1e-1*(3*midDiameterM + 9*length + 10*thick));
	}	

	private static double calcQuality(final float frequency, final float inductance, final float numberOfCoils, float innerDiameter, final float wireDiameter) {
		return CoilsUtil.solve_Qr(inductance,innerDiameter,wireDiameter,wireDiameter,frequency/1000,numberOfCoils,CoilsUtil.Material.COPPER);
	}
}