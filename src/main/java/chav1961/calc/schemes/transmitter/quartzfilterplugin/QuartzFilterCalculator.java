package chav1961.calc.schemes.transmitter.quartzfilterplugin;


import chav1961.calc.LocalizationKeys;
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

@LocaleResourceLocation(Localizer.LOCALIZER_SCHEME+":prop:chav1961/calc/schemes/transmitter/quartzfilterplugin/quartzfilter")
@Action(resource=@LocaleResource(value="calculate",tooltip="calculateTooltip"),actionString="calculate",simulateCheck=true) 
@UseFormulas({LocalizationKeys.FORMULA_NUMBER_OF_COILS_ONE_LAYER_COIL,LocalizationKeys.FORMULA_INDUCTANCE_ONE_LAYER_COIL})
class QuartzFilterCalculator implements FormManager<Object,QuartzFilterCalculator> {
	private static final String[]	FIELDS_ANNOTATED = chav1961.calc.environment.Utils.buildFieldsAnnotated(QuartzFilterCalculator.class);

	private static final String MESSAGE_FREQUENCY_POSITIVE = "frequencyPositive";
	private static final String MESSAGE_DELTA_FREQUENCY_POSITIVE = "deltaFrequencyPositive";
	private static final String MESSAGE_KOEFF_OUT_OF_RANGE = "koeffOutOfRange";

@LocaleResource(value="filterType",tooltip="filterTypeTooltip")	
@Format("1m")
	private FilterType				type = FilterType.CHEB;
@LocaleResource(value="filterOrder",tooltip="filterOrderTooltip")	
@Format("1m")
	private FilterOrder				order = FilterOrder.ORD_4;
@LocaleResource(value="centralFrequency",tooltip="centralFrequencyTooltip")	
@Format("10.3ms")
	private float					frequency = 500.0f;
@LocaleResource(value="deltaFrequency",tooltip="deltaFrequencyTooltip")	
@Format("10.3ms")
	private float					deltaFrequency = 3.0f;
@LocaleResource(value="rectCoeff",tooltip="rectCoeffTooltip")	
@Format("10.3ms")
	private float					koeff = 1.0f;
@LocaleResource(value="C1Capacitance",tooltip="C1CapacitanceTooltip")	
@Format("10.3r")
	private float					ñ1 = 0.0f;
@LocaleResource(value="C2Capacitance",tooltip="C2CapacitanceTooltip")	
@Format("10.3r")
	private float					ñ2 = 0.0f;
@LocaleResource(value="C12Capacitance",tooltip="C12CapacitanceTooltip")	
@Format("10.3r")
	private float					ñ12 = 0.0f;
@LocaleResource(value="C23Capacitance",tooltip="C23CapacitanceTooltip")	
@Format("10.3r")
	private float					ñ23 = 0.0f;
@LocaleResource(value="C34Capacitance",tooltip="C34CapacitanceTooltip")	
@Format("10.3r")
	private float					ñ34 = 0.0f;
@LocaleResource(value="C45Capacitance",tooltip="C45CapacitanceTooltip")	
@Format("10.3r")
	private float					ñ45 = 0.0f;
	
	private final Localizer			localizer;
	private final LoggerFacade		logger;

// http://ra3rbe.r3r.ru/xfilter.htm
	QuartzFilterCalculator(final Localizer localizer,final LoggerFacade logger) {
		this.localizer = localizer;
		this.logger = logger;		
	}
	
	@Override
	public RefreshMode onRecord(final Action action, final QuartzFilterCalculator oldRecord, final Object oldId, final QuartzFilterCalculator newRecord, final Object newId) throws FlowException, LocalizationException {
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
	public RefreshMode onField(final QuartzFilterCalculator inst, final Object id, final String fieldName, final Object oldValue) throws FlowException, LocalizationException, IllegalArgumentException {
		switch (fieldName) {
			case "frequency"		:
				return checkAndNotify(frequency > 0,localizer.getValue(MESSAGE_FREQUENCY_POSITIVE),frequency) ? RefreshMode.NONE : RefreshMode.REJECT;
			case "deltaFrequency"	:
				return checkAndNotify(deltaFrequency > 0,localizer.getValue(MESSAGE_DELTA_FREQUENCY_POSITIVE),deltaFrequency) ? RefreshMode.NONE : RefreshMode.REJECT;
			case "koeff"			:
				return checkAndNotify(koeff >= 0.5f && koeff <= 2.0f,localizer.getValue(MESSAGE_KOEFF_OUT_OF_RANGE),deltaFrequency) ? RefreshMode.NONE : RefreshMode.REJECT;
			default :
				return RefreshMode.NONE;
		}
	}

	@Override
	public RefreshMode onAction(final QuartzFilterCalculator inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "calculate"	:
				
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