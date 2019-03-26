package chav1961.calc.schemes.stepdown.mc34063plugin;


import chav1961.calc.LocalizationKeys;
import chav1961.calc.formulas.Utils;
import chav1961.calc.interfaces.UseFormulas;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.interfaces.Action;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;

/**
 * 
 * @author Alexander Chernomyrdin aka chav1961
 * @since 0.0.1
 */

@LocaleResourceLocation(Localizer.LOCALIZER_SCHEME+":prop:chav1961/calc/schemes/stepdown/mc34063plugin/MC34063")
@Action(resource=@LocaleResource(value="calculate",tooltip="calculateTooltip"),actionString="calculate",simulateCheck=true) 
@UseFormulas({LocalizationKeys.FORMULA_COILS_RING_COIL,LocalizationKeys.FORMULA_INDUCTANCE_RING_COIL,LocalizationKeys.FORMULA_INDUCTION_RING_COIL})
class MC34063Calculator implements FormManager<Object,MC34063Calculator> {
	private static final float		ETHA = 0.9f;
	private static final float		FREQUENCY = 25000f;
	private static final float		RIPPLE = 0.1f;
	private static final String		MESSAGE_INNER_VOLTAGE_POSITIVE = "innerVoltagePositive";
	private static final String		MESSAGE_OUTER_VOLTAGE_POSITIVE = "outerVoltagePositive";	
	private static final String		MESSAGE_OUTER_VOLTAGE_LESS_INNER = "outerVoltageLessInner";
	private static final String		MESSAGE_OUTER_CURRENT_POSITIVE = "outerCurrentPositive";
	private static final String		MESSAGE_SWITCHING_CYCLE_OUT_OF_RANGE = "switchingCycleOutOfRange";
	private static final String		MESSAGE_PERMABILITY_POSITIVE = "permabilityPositive";
	private static final String		MESSAGE_INDUCTION_POSITIVE = "inductionPositive";
	private static final String		MESSAGE_OUTER_DIAMETER_POSITIVE = "outerDiameterPositive";
	private static final String		MESSAGE_OUTER_DIAMETER_LESS_INNER = "outerDiameterLessInner";
	private static final String		MESSAGE_INNER_DIAMETER_POSITIVE = "innerDiameterPositive";
	private static final String		MESSAGE_HEIGHT_POSITIVE = "heightPositive";
	private static final String		MESSAGE_WIRE_DIAMETER_POSITIVE = "wireDiameterPositive";
	private static final String		MESSAGE_INDUCTION_TOO_STRONG = "inductionTooStrong";
	private static final String		MESSAGE_TOO_MANY_COILS = "tooManyCoils";

	private static final String[]	FIELDS_ANNOTATED = chav1961.calc.environment.Utils.buildFieldsAnnotated(MC34063Calculator.class); 

@LocaleResource(value="innerVoltage",tooltip="innerVoltageTooltip")	
@Format("10.3ms")
	private float					innerVoltage = 12.0f;
@LocaleResource(value="outerVoltage",tooltip="outerVoltageTooltip")	
@Format("10.3ms")
	private float					outerVoltage = 24.0f;
@LocaleResource(value="outerCurrent",tooltip="outerCurrentTooltip")	
@Format("10.3ms")
	private float					outerCurrent = 1.0f;
@LocaleResource(value="permability",tooltip="permabilityTooltip")	
@Format("4ms")
	private int						permability = 140;
@LocaleResource(value="induction",tooltip="inductionTooltip")	
@Format("10.3ms")
	private float					induction = 0.7f;
@LocaleResource(value="outerDiameter",tooltip="outerDiameterTooltip")	
@Format("10.3ms")
	private float 					outerDiameter = 0.0f;
@LocaleResource(value="innerDiameter",tooltip="innerDiameterTooltip")	
@Format("10.3ms")
	private float 					innerDiameter = 0.0f;
@LocaleResource(value="height",tooltip="heightTooltip")	
@Format("10.3ms")
	private float 					height = 0.0f;
@LocaleResource(value="wireDiameter",tooltip="wireDiameterTooltip")	
@Format("10.3ms")
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
	
	private final Localizer			localizer;
	private final LoggerFacade		logger;


	MC34063Calculator(final Localizer localizer,final LoggerFacade logger) {
		this.localizer = localizer;
		this.logger = logger;		
	}
	
	@Override
	public RefreshMode onRecord(final Action action, final MC34063Calculator oldRecord, final Object oldId, final MC34063Calculator newRecord, final Object newId) throws FlowException, LocalizationException {
		switch (action) {
			case CHECK	:
				for (String field : FIELDS_ANNOTATED) {
					if (onField(oldRecord,oldId,field,null) == RefreshMode.REJECT) {
						return RefreshMode.REJECT;
					}
				}
				return RefreshMode.NONE;
			default 	:
				return RefreshMode.NONE;
		}
	}

	@Override
	public RefreshMode onField(final MC34063Calculator inst, final Object id, final String fieldName, final Object oldValue) throws FlowException, LocalizationException, IllegalArgumentException {
		switch (fieldName) {
			case "innerVoltage"		:
				return checkAndNotify(innerVoltage > 0,localizer.getValue(MESSAGE_INNER_VOLTAGE_POSITIVE),innerVoltage) ? 
						(checkAndNotify(outerVoltage > innerVoltage,localizer.getValue(MESSAGE_OUTER_VOLTAGE_LESS_INNER),outerVoltage,innerVoltage) ? RefreshMode.NONE : RefreshMode.REJECT)
					: RefreshMode.REJECT;
			case "outerVoltage"		:
				return checkAndNotify(outerVoltage > 0,localizer.getValue(MESSAGE_OUTER_VOLTAGE_POSITIVE),outerVoltage) ? 
						(checkAndNotify(outerVoltage > innerVoltage,localizer.getValue(MESSAGE_OUTER_VOLTAGE_LESS_INNER),outerVoltage,innerVoltage) ? RefreshMode.NONE : RefreshMode.REJECT)
					: RefreshMode.REJECT;
			case "outerCurrent"		:
				return checkAndNotify(outerCurrent > 0,localizer.getValue(MESSAGE_OUTER_CURRENT_POSITIVE),outerCurrent) ? RefreshMode.NONE : RefreshMode.REJECT;
			case "permability"		:
				return checkAndNotify(permability > 0,localizer.getValue(MESSAGE_PERMABILITY_POSITIVE),permability) ? RefreshMode.NONE : RefreshMode.REJECT;
			case "induction"		:
				return checkAndNotify(induction > 0,localizer.getValue(MESSAGE_INDUCTION_POSITIVE),induction) ? RefreshMode.NONE : RefreshMode.REJECT;
			case "outerDiameter"		:
				return checkAndNotify(outerDiameter > 0,localizer.getValue(MESSAGE_OUTER_DIAMETER_POSITIVE),outerDiameter) ? 
						(checkAndNotify(outerDiameter > innerDiameter,localizer.getValue(MESSAGE_OUTER_DIAMETER_LESS_INNER),outerDiameter,innerDiameter) ? RefreshMode.NONE : RefreshMode.REJECT)
					: RefreshMode.REJECT;
			case "innerDiameter"		:
				return checkAndNotify(innerDiameter > 0,localizer.getValue(MESSAGE_INNER_DIAMETER_POSITIVE),innerDiameter) ? 
						(checkAndNotify(outerDiameter > innerDiameter,localizer.getValue(MESSAGE_OUTER_DIAMETER_LESS_INNER),outerDiameter,innerDiameter) ? RefreshMode.NONE : RefreshMode.REJECT)
					: RefreshMode.REJECT;
			case "height"		:
				return checkAndNotify(height > 0,localizer.getValue(MESSAGE_HEIGHT_POSITIVE),height) ? RefreshMode.NONE : RefreshMode.REJECT;
			case "wireDiameter"		:
				return checkAndNotify(wireDiameter > 0,localizer.getValue(MESSAGE_WIRE_DIAMETER_POSITIVE),wireDiameter) ? RefreshMode.NONE : RefreshMode.REJECT;
			default :
				return RefreshMode.NONE;
		}
	}

	@Override
	public RefreshMode onAction(final MC34063Calculator inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "calculate"	:
				final float	power = outerVoltage * outerCurrent;
				final float	dutyCycle = 1 - innerVoltage * ETHA / outerVoltage;
				final float	coilInductance = innerVoltage * (outerVoltage - innerVoltage) / (RIPPLE * FREQUENCY * outerVoltage); 
				final float peakCurrent = power / (innerVoltage * dutyCycle); 
				final int 	drosselCoils = Utils.coilsRingCoil(coilInductance,outerDiameter,innerDiameter,height,permability);
				final float currentInduction = Utils.inductionRingCoil(peakCurrent,drosselCoils,outerDiameter,innerDiameter,permability);
				
				if (currentInduction > induction) {
					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_INDUCTION_TOO_STRONG),currentInduction,induction);
					return RefreshMode.NONE;
				}
				else {
					final int		secondCoils = (int) (outerVoltage < 40 ? 0 : (outerVoltage - innerVoltage - 40 ) / (innerVoltage - 40));
					final float[]	forLength1 = Utils.wireLength4Ring(drosselCoils,wireDiameter,outerDiameter,innerDiameter,height);
					final float[]	forLength2 = Utils.wireLength4Ring(secondCoils,wireDiameter,outerDiameter,innerDiameter,height);
					
					if (forLength1 == null) {
						getLogger().message(Severity.warning,localizer.getValue(MESSAGE_TOO_MANY_COILS));
						return RefreshMode.NONE;
					}
					else {
						pulseCurrent = peakCurrent;
						coils1 = drosselCoils;
						wireLength1 = forLength1[0] + 0.1f;
						coils2 = secondCoils;
						wireLength2 = secondCoils == 0 ? 0 : forLength2[0] + 0.1f;
					}
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

	private boolean checkAndNotify(final boolean condition, final String messageId, final Object... parameters) throws LocalizationException {
		if (!condition) {
			getLogger().message(Severity.warning,messageId,parameters);
			return false;
		}
		else {
			return true;
		}
	}
}