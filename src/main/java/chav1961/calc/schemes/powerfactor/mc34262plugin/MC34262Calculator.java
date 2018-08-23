package chav1961.calc.schemes.powerfactor.mc34262plugin;


import chav1961.calc.formulas.Utils;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.interfacers.Action;
import chav1961.purelib.ui.interfacers.FormManager;
import chav1961.purelib.ui.interfacers.Format;

/**
 * 
 * @author Alexander Chernomyrdin aka chav1961
 * @since 0.0.1
 */

@LocaleResourceLocation(Localizer.LOCALIZER_SCHEME+":prop:chav1961/calc/schemes/powerfactor/mc34262plugin/MC34262")
@Action(resource=@LocaleResource(value="calculate",tooltip="calculateTooltip"),actionString="calculate") 
class MC34262Calculator implements FormManager<Object,MC34262Calculator> {
	private static final float 		ETHA = 0.92f;
	private static final float 		ROOT2 = (float)Math.sqrt(2);
//	private static final String		MESSAGE_HEIGHT_POSITIVE = "heightPositive";
//	private static final String		MESSAGE_DIAMETER_POSITIVE = "diameterPositive";
//	private static final String		MESSAGE_DIAMETER_INNER_GREATER = "diameterInnerGreater";
//	private static final String		MESSAGE_PERMABILITY_GREAT_ONE = "permabilityGreatOne";
//	private static final String		MESSAGE_WIREDIAMETER_POSITIVE = "wirediameterPositive";
//	private static final String		MESSAGE_INDUCTANCE_NONNEGATIVE = "indictanceNonnegative";
//	private static final String		MESSAGE_COILS_NONNEGATIVE = "coilsNonnegative";
	private static final String		MESSAGE_TOO_STRONG = "coilsNonnegative";
	
	private final Localizer			localizer;
	private final LoggerFacade		logger;

@LocaleResource(value="innerVoltage",tooltip="innerVoltageTooltip")	
@Format("10.3m")
	private float					innerVoltage = 220.0f;
@LocaleResource(value="outerVoltage",tooltip="outerVoltageTooltip")	
@Format("10.3m")
	private float					outerVoltage = 385.0f;
@LocaleResource(value="outerCurrent",tooltip="outerCurrentTooltip")	
@Format("10.3m")
	private float					outerCurrent = 0.0f;
@LocaleResource(value="switchingCycle",tooltip="switchingCycleTooltip")	
@Format("10.3m")
	private float					switchingCycle = 40.0f;
@LocaleResource(value="permability",tooltip="permabilityTooltip")	
@Format("4m")
	private int						permability = 140;
@LocaleResource(value="induction",tooltip="inductionTooltip")	
@Format("10.3m")
	private float					induction = 0.7f;
@LocaleResource(value="outerDiameter",tooltip="outerDiameterTooltip")	
@Format("10.3m")
	private float 					outerDiameter = 0.0f;
@LocaleResource(value="innerDiameter",tooltip="innerDiameterTooltip")	
@Format("10.3m")
	private float 					innerDiameter = 0.0f;
@LocaleResource(value="height",tooltip="heightTooltip")	
@Format("10.3m")
	private float 					height = 0.0f;
@LocaleResource(value="wireDiameter",tooltip="wireDiameterTooltip")	
@Format("10.3r")
	private float 					wireDiameter = 0.0f;
@LocaleResource(value="pulseCurrent",tooltip="pulseCurrentTooltip")	
@Format("10.3r")
	private float 					pulseCurrent = 0.0f;
@LocaleResource(value="coils1",tooltip="coils1Tooltip")	
@Format("10.3r")
	private float 					coils1 = 0.0f;
@LocaleResource(value="wireLength1",tooltip="wireLength1Tooltip")	
@Format("10.3r")
	private float 					wireLength1 = 0.0f;
@LocaleResource(value="coils2",tooltip="coils2Tooltip")	
@Format("10.3r")
	private float 					coils2 = 0.0f;
@LocaleResource(value="wireLength2",tooltip="wireLength2Tooltip")	
@Format("10.3r")
	private float 					wireLength2 = 0.0f;
@LocaleResource(value="currentSenceResistance",tooltip="currentSenceResistanceTooltip")	
@Format("10.3r")
	private float 					currentSenceResistance = 0.0f;

	MC34262Calculator(final Localizer localizer,final LoggerFacade logger) {
		this.localizer = localizer;
		this.logger = logger;		
	}
	
	@Override
	public RefreshMode onRecord(final Action action, final MC34262Calculator oldRecord, final Object oldId, final MC34262Calculator newRecord, final Object newId) throws FlowException {
		return RefreshMode.NONE;
	}

	@Override
	public RefreshMode onField(final MC34262Calculator inst, final Object id, final String fieldName, final Object oldValue) throws FlowException, LocalizationException, IllegalArgumentException {
//		switch (fieldName) {
//			case "height"		:
//				if (height <= 0) {
//					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_HEIGHT_POSITIVE),height);
//					return RefreshMode.REJECT;
//				}
//				break;
//			case "outerDiameter"		:
//				if (outerDiameter <= 0) {
//					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_DIAMETER_POSITIVE),outerDiameter);
//					return RefreshMode.REJECT;
//				}
//				else if (outerDiameter < innerDiameter) {
//					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_DIAMETER_INNER_GREATER),outerDiameter,innerDiameter);
//					return RefreshMode.REJECT;
//				}
//				break;
//			case "innerDiameter"		:
//				if (innerDiameter <= 0) {
//					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_DIAMETER_POSITIVE),innerDiameter);
//					return RefreshMode.REJECT;
//				}
//				else if (outerDiameter < innerDiameter) {
//					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_DIAMETER_INNER_GREATER),outerDiameter,innerDiameter);
//					return RefreshMode.REJECT;
//				}
//				break;
//			case "permability"		:
//				if (permability < 1) {
//					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_PERMABILITY_GREAT_ONE),permability);
//					return RefreshMode.REJECT;
//				}
//				break;
//			case "wireDiameter"	:
//				if (wireDiameter <= 0) {
//					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_WIREDIAMETER_POSITIVE),wireDiameter);
//					return RefreshMode.REJECT;
//				}
//				break;
//			case "inductance"	:
//				if (inductance < 0) {
//					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_INDUCTANCE_NONNEGATIVE),inductance);
//					return RefreshMode.REJECT;
//				}
//				break;
//			case "coils"		:
//				if (coils < 0) {
//					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_COILS_NONNEGATIVE),coils);
//					return RefreshMode.REJECT;
//				}
//				break;
//			default :
//		}
		return RefreshMode.NONE;
	}

	@Override
	public RefreshMode onAction(final MC34262Calculator inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "calculate"	:
				final float power = outerVoltage * outerCurrent;
				final float peakCurrent = 2 * ROOT2 * power / (ETHA * innerVoltage);
				final float coilInductance = switchingCycle * (outerVoltage / ROOT2 - innerVoltage) * ETHA * innerVoltage * innerVoltage / (ETHA * outerVoltage * power);
				final int 	drosselCoils = Utils.ringCoilsCoils(coilInductance,outerDiameter,innerDiameter,height,permability);
				final float currentInduction = Utils.ringCoilsInduction(peakCurrent,drosselCoils,outerDiameter,innerDiameter,height,permability);
				
				if (currentInduction > induction) {
					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_TOO_STRONG),currentInduction,induction);
					return RefreshMode.NONE;
				}
				else {
					final float switchOn = 2 * power * coilInductance / (ETHA * innerVoltage * innerVoltage);  
				}
				break;
			default :
				break;
		}
		return RefreshMode.RECORD_ONLY;
	}

	@Override
	public LoggerFacade getLogger() {
		return logger;
	}
}