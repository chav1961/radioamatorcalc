package chav1961.calc.components.rcgeneratorplugin;

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

@LocaleResourceLocation(Localizer.LOCALIZER_SCHEME+":prop:chav1961/calc/components/rcgeneratorplugin/rcgenerator")
@Action(resource=@LocaleResource(value="calculate",tooltip="calculateTooltip"),actionString="calculate",simulateCheck=true) 
//@UseFormulas({LocalizationKeys.FORMULA_COILS_RING_COIL,LocalizationKeys.FORMULA_INDUCTANCE_RING_COIL})
class RCGeneratorCalculator implements FormManager<Object,RCGeneratorCalculator> {
	private static final float		SQRT_6 = (float)Math.sqrt(6);
	private static final float		LN_2 = (float)Math.log(2);
	
	private static final String[]	FIELDS_ANNOTATED = chav1961.calc.environment.Utils.buildFieldsAnnotated(RCGeneratorCalculator.class);
	private static final String 	MESSAGE_FREQUENCY_POSITIVE = "frequencyPositive";
	private static final String 	MESSAGE_RESISTANCE_POSITIVE = "resistancePositive";
	private static final String 	MESSAGE_CAPACITY_POSITIVE = "capacityPositive";
	
	private final Localizer			localizer;
	private final LoggerFacade		logger;

@LocaleResource(value="genType",tooltip="genTypeTooltip")	
@Format("10.3ms")
	private GeneratorType			genType = GeneratorType.MULTIVIBRATOR;
@LocaleResource(value="calcType",tooltip="calcTypeTooltip")	
@Format("10.3ms")
	private GeneratorCalculationType	calcType = GeneratorCalculationType.FREQ_BY_R_C;
@LocaleResource(value="frequency",tooltip="frequencyTooltip")	
@Format("10.3s")
	private float					frequency = 0.0f;
@LocaleResource(value="resistance",tooltip="resistanceTooltip")	
@Format("10.3s")
	private float					resistance = 0.0f;
@LocaleResource(value="capacity",tooltip="capacityTooltip")	
@Format("10.3s")
	private float					capacity = 0.0f;

	RCGeneratorCalculator(final Localizer localizer,final LoggerFacade logger) {
		this.localizer = localizer;
		this.logger = logger;		
	}
	
	@Override
	public RefreshMode onRecord(final Action action, final RCGeneratorCalculator oldRecord, final Object oldId, final RCGeneratorCalculator newRecord, final Object newId) throws FlowException, LocalizationException {
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
	public RefreshMode onField(final RCGeneratorCalculator inst, final Object id, final String fieldName, final Object oldValue) throws FlowException, LocalizationException, IllegalArgumentException {
		switch (fieldName) {
			case "frequency"	:
				if (calcType == GeneratorCalculationType.C_BY_FREQ_R || calcType == GeneratorCalculationType.R_BY_FREQ_C) {
					return checkAndNotify(frequency > 0,localizer.getValue(MESSAGE_FREQUENCY_POSITIVE),frequency) ? RefreshMode.NONE : RefreshMode.REJECT;
				}
				else {
					return RefreshMode.NONE;
				}
			case "resistance"	:
				if (calcType == GeneratorCalculationType.C_BY_FREQ_R || calcType == GeneratorCalculationType.FREQ_BY_R_C) {
					return checkAndNotify(resistance > 0,localizer.getValue(MESSAGE_RESISTANCE_POSITIVE),resistance) ? RefreshMode.NONE : RefreshMode.REJECT;
				}
				else {
					return RefreshMode.NONE;
				}
			case "capacity"	:
				if (calcType == GeneratorCalculationType.R_BY_FREQ_C || calcType == GeneratorCalculationType.FREQ_BY_R_C) {
					return checkAndNotify(capacity > 0,localizer.getValue(MESSAGE_CAPACITY_POSITIVE),capacity) ? RefreshMode.NONE : RefreshMode.REJECT;
				}
				else {
					return RefreshMode.NONE;
				}
			default :
				return RefreshMode.NONE;
		}
	}

	@Override
	public RefreshMode onAction(final RCGeneratorCalculator inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "calculate"	:
				switch (calcType) {
					case FREQ_BY_R_C	:
						switch (genType) {
							case WIN_ROBINSON	:
								frequency = (float)(1  / (2 * Math.PI * (resistance * 1E3) * (capacity * 1E-9)));
								break;
							case PHASE_SHIFTER	:
								frequency = (float)(1  / (2 * Math.PI * SQRT_6 * (resistance * 1E3) * (capacity * 1E-9)));
								break;
							case MULTIVIBRATOR	:
								frequency = (float)(1  / (2 * Math.PI * LN_2 * (resistance * 1E3) * (capacity * 1E-9)));
								break;
							default : throw new UnsupportedOperationException("Generator type ["+genType+"] is not supported yet");
						}
						break;
					case C_BY_FREQ_R	:
						switch (genType) {
							case WIN_ROBINSON	:
								capacity = (float)(1  / (2 * Math.PI * (resistance * 1E3) * (frequency * 1E3)));
								break;
							case PHASE_SHIFTER	:
								capacity = (float)(1  / (2 * Math.PI * SQRT_6 * (resistance * 1E3) * (frequency * 1E3)));
								break;
							case MULTIVIBRATOR	:
								capacity = (float)(1  / (2 * Math.PI * LN_2 * (resistance * 1E3) * (frequency * 1E3)));
								break;
							default : throw new UnsupportedOperationException("Generator type ["+genType+"] is not supported yet");
						}
						break;
					case R_BY_FREQ_C	:
						switch (genType) {
							case WIN_ROBINSON	:
								resistance = (float)(1  / (2 * Math.PI * (frequency * 1E3) * (capacity * 1E-9)));
								break;
							case PHASE_SHIFTER	:
								resistance = (float)(1  / (2 * Math.PI * SQRT_6 * (frequency * 1E3) * (capacity * 1E-9)));
								break;
							case MULTIVIBRATOR	:
								resistance = (float)(1  / (2 * Math.PI * LN_2 * (frequency * 1E3) * (capacity * 1E-9)));
								break;
							default : throw new UnsupportedOperationException("Generator type ["+genType+"] is not supported yet");
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