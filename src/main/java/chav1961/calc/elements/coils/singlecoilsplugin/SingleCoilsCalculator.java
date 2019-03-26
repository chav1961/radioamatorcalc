package chav1961.calc.elements.coils.singlecoilsplugin;


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
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;
import chav1961.purelib.ui.interfaces.Action;

/**
 * 
 * @author Alexander Chernomyrdin aka chav1961
 * @since 0.0.1
 */

@LocaleResourceLocation(Localizer.LOCALIZER_SCHEME+":prop:chav1961/calc/elements/coils/singlecoilsplugin/singlecoils")
@LocaleResource(value="SingleCoilsService.caption",tooltip="SingleCoilsService.tooltip")
@Action(resource=@LocaleResource(value="calculate",tooltip="calculateTooltip"),actionString="calculate",simulateCheck=true) 
@UseFormulas({LocalizationKeys.FORMULA_NUMBER_OF_COILS_ONE_LAYER_COIL,LocalizationKeys.FORMULA_INDUCTANCE_ONE_LAYER_COIL})
class SingleCoilsCalculator implements FormManager<Object,SingleCoilsCalculator> {
	private static final String		MESSAGE_LENGTH_POSITIVE = "lengthPositive";
	private static final String		MESSAGE_DIAMETER_POSITIVE = "diameterPositive";
	private static final String		MESSAGE_WIREDIAMETER_POSITIVE = "wirediameterPositive";
	private static final String		MESSAGE_INDUCTANCE_NONNEGATIVE = "indictanceNonnegative";
	private static final String		MESSAGE_COILS_NONNEGATIVE = "coilsNonnegative";

	private static final String[]	FIELDS_ANNOTATED = chav1961.calc.environment.Utils.buildFieldsAnnotated(SingleCoilsCalculator.class); 
	
	private final Localizer			localizer;
	private final LoggerFacade		logger;
	
@LocaleResource(value="length",tooltip="lengthTooltip")
@Format("10.3mpzn")
	private float 					length = 0.0f;
@LocaleResource(value="diameter",tooltip="diameterTooltip")	
@Format("10.3ms")
	private float					diameter = 0.0f;
@LocaleResource(value="wireDiameter",tooltip="wireDiameterTooltip")	
@Format("10.3ms")
	private float					wireDiameter = 0.0f;
@LocaleResource(value="calcType",tooltip="calcTypeTooltip")	
@Format("10.3m")
	private CoilsCalculationType	calcType = CoilsCalculationType.INDUCTANCE;
@LocaleResource(value="inductance",tooltip="inductanceTooltip")	
@Format("10.3s")
	private float					inductance = 0.0f;
@LocaleResource(value="coils",tooltip="coilsTooltip")	
@Format("10s")
	private int						coils = 0;

	SingleCoilsCalculator(final Localizer localizer,final LoggerFacade logger) {
		this.localizer = localizer;
		this.logger = logger;		
	}
	
	@Override
	public RefreshMode onRecord(final Action action, final SingleCoilsCalculator oldRecord, final Object oldId, final SingleCoilsCalculator newRecord, final Object newId) throws FlowException, LocalizationException {
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
	public RefreshMode onField(final SingleCoilsCalculator inst, final Object id, final String fieldName, final Object oldValue) throws FlowException, LocalizationException, IllegalArgumentException {
		switch (fieldName) {
			case "length"		:
				return checkAndNotify(length > 0,localizer.getValue(MESSAGE_LENGTH_POSITIVE),length) ? RefreshMode.NONE : RefreshMode.REJECT;
			case "diameter"		:
				return checkAndNotify(diameter > 0,localizer.getValue(MESSAGE_DIAMETER_POSITIVE),diameter) ? RefreshMode.NONE : RefreshMode.REJECT;
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
		}
		return RefreshMode.NONE;
	}

	@Override
	public RefreshMode onAction(final SingleCoilsCalculator inst, final Object id, final String actionName, final Object parameter) throws FlowException {
		switch (actionName) {
			case "calculate"	:
				switch (calcType) {
					case INDUCTANCE		:
						inductance = Utils.inductanceOneLayerCoil(coils,diameter,length,wireDiameter);
						break;
					case NUMBER_OF_COILS:
						coils = Utils.numberOfCoilsOneLayerCoil(inductance,diameter,length,wireDiameter);
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