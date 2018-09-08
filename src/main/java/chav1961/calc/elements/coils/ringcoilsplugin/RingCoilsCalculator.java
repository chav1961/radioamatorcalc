package chav1961.calc.elements.coils.ringcoilsplugin;

import chav1961.calc.LocalizationKeys;
import chav1961.calc.elements.coils.CoilsCalculationType;
import chav1961.calc.formulas.Utils;
import chav1961.calc.interfaces.UseFormulas;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.interfacers.FormManager;
import chav1961.purelib.ui.interfacers.Format;
import chav1961.purelib.ui.interfacers.Action;

/**
 * 
 * @author Alexander Chernomyrdin aka chav1961
 * @since 0.0.1
 */

@LocaleResourceLocation(Localizer.LOCALIZER_SCHEME+":prop:chav1961/calc/elements/coils/ringcoilsplugin/ringcoils")
@Action(resource=@LocaleResource(value="calculate",tooltip="calculateTooltip"),actionString="calculate",simulateCheck=true) 
@UseFormulas({LocalizationKeys.FORMULA_COILS_RING_COIL,LocalizationKeys.FORMULA_INDUCTANCE_RING_COIL})
class RingCoilsCalculator implements FormManager<Object,RingCoilsCalculator> {
	private static final String		MESSAGE_HEIGHT_POSITIVE = "heightPositive";
	private static final String		MESSAGE_DIAMETER_POSITIVE = "diameterPositive";
	private static final String		MESSAGE_PERMABILITY_GREAT_ONE = "permabilityGreatOne";
	private static final String		MESSAGE_WIREDIAMETER_POSITIVE = "wirediameterPositive";
	private static final String		MESSAGE_INDUCTANCE_NONNEGATIVE = "indictanceNonnegative";
	private static final String		MESSAGE_COILS_NONNEGATIVE = "coilsNonnegative";
	private static final String		MESSAGE_NOT_IMPLEMENTED = "notImplemented";
	
	private static final String[]	FIELDS_ANNOTATED = chav1961.calc.environment.Utils.buildFieldsAnnotated(RingCoilsCalculator.class); 
	
	private final Localizer			localizer;
	private final LoggerFacade		logger;
	
@LocaleResource(value="calcType",tooltip="calcTypeTooltip")	
@Format("10.3ms")
	private CoilsCalculationType	calcType = CoilsCalculationType.INDUCTANCE;
@LocaleResource(value="outerDiameter",tooltip="outerDiameterTooltip")	
@Format("10.3ms")
	private float					outerDiameter = 0.0f;
@LocaleResource(value="innerDiameter",tooltip="innerDiameterTooltip")	
@Format("10.3ms")
	private float					innerDiameter = 0.0f;
@LocaleResource(value="height",tooltip="heightTooltip")	
@Format("10.3ms")
	private float					height = 0.0f;
@LocaleResource(value="permability",tooltip="permabilityTooltip")	
@Format("4ms")
	private int						permability = 1;
@LocaleResource(value="wireDiameter",tooltip="wireDiameterTooltip")	
@Format("10.3ms")
	private float					wireDiameter = 0.0f;
@LocaleResource(value="inductance",tooltip="inductanceTooltip")	
@Format("10.3s")
	private float					inductance = 0.0f;
@LocaleResource(value="coils",tooltip="coilsTooltip")	
@Format("10s")
	private int						coils = 0;
@LocaleResource(value="wireLength",tooltip="wireLengthTooltip")	
@Format("10.3r")
	private float					wireLength = 0.0f;

	RingCoilsCalculator(final Localizer localizer,final LoggerFacade logger) {
		this.localizer = localizer;
		this.logger = logger;		
	}
	
	@Override
	public RefreshMode onRecord(final Action action, final RingCoilsCalculator oldRecord, final Object oldId, final RingCoilsCalculator newRecord, final Object newId) throws FlowException, LocalizationException {
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
	public RefreshMode onField(final RingCoilsCalculator inst, final Object id, final String fieldName, final Object oldValue) throws FlowException, LocalizationException, IllegalArgumentException {
		switch (fieldName) {
			case "height"		:
				return checkAndNotify(height > 0,localizer.getValue(MESSAGE_HEIGHT_POSITIVE),height) ? RefreshMode.NONE : RefreshMode.REJECT;
			case "outerDiameter"		:
				if (checkAndNotify(outerDiameter > 0,localizer.getValue(MESSAGE_DIAMETER_POSITIVE),outerDiameter)) {
					if (outerDiameter < innerDiameter) {
						innerDiameter = outerDiameter;
						return RefreshMode.RECORD_ONLY;
					}
					else {
						return RefreshMode.NONE;
					}
				}
				else {
					return RefreshMode.REJECT;
				}
			case "innerDiameter"		:
				if (checkAndNotify(innerDiameter > 0,localizer.getValue(MESSAGE_DIAMETER_POSITIVE),innerDiameter)) {
					if (outerDiameter < innerDiameter) {
						outerDiameter = innerDiameter;
						return RefreshMode.RECORD_ONLY;
					}
					else {
						return RefreshMode.NONE;
					}
				}
				else {
					return RefreshMode.REJECT;
				}
			case "permability"		:
				return checkAndNotify(permability > 1,localizer.getValue(MESSAGE_PERMABILITY_GREAT_ONE),permability) ? RefreshMode.NONE : RefreshMode.REJECT;
			case "wireDiameter"	:
				return checkAndNotify(wireDiameter > 0,localizer.getValue(MESSAGE_WIREDIAMETER_POSITIVE),wireDiameter) ? RefreshMode.NONE : RefreshMode.REJECT;
			case "inductance"	:
				if (calcType == CoilsCalculationType.NUMBER_OF_COILS) {
					return checkAndNotify(inductance > 0,localizer.getValue(MESSAGE_INDUCTANCE_NONNEGATIVE),inductance) ? RefreshMode.NONE : RefreshMode.REJECT;
				}
				else {
					return RefreshMode.REJECT;
				}
			case "coils"		:
				if (calcType == CoilsCalculationType.INDUCTANCE) {
					return checkAndNotify(coils > 0,localizer.getValue(MESSAGE_COILS_NONNEGATIVE),coils) ? RefreshMode.NONE : RefreshMode.REJECT;
				}
				else {
					return RefreshMode.REJECT;
				}
			default :
				return RefreshMode.NONE;
		}
	}

	@Override
	public RefreshMode onAction(final RingCoilsCalculator inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "calculate"	:
				switch (calcType) {
					case INDUCTANCE		:
						inductance = (float) Utils.inductanceRingCoil(coils, outerDiameter, innerDiameter, height, permability);
						break;
					case NUMBER_OF_COILS:
						if (outerDiameter/innerDiameter > 1.75) {
							coils = (int) Math.sqrt(inductance / (0.0002f * permability * height * Math.log(outerDiameter/innerDiameter)));  
						}
						else {
							coils = (int) Math.sqrt(inductance / (0.0004f * permability * height * (outerDiameter - innerDiameter) / (outerDiameter + innerDiameter)));
						}
						
						final float[]	forLength = Utils.wireLength4Ring(coils,wireDiameter,outerDiameter,innerDiameter,height);
						
						if (forLength == null) {
							getLogger().message(Severity.warning,localizer.getValue(MESSAGE_NOT_IMPLEMENTED),coils);
							return RefreshMode.REJECT;
						}
						else {
							wireLength = forLength[0];
						}
						break;
					default: throw new UnsupportedOperationException("Calculation type ["+calcType+"] is not supported yet");
				}
				break;
			default :
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