package chav1961.calc.components.lccontourplugin;

import chav1961.calc.LocalizationKeys;
import chav1961.calc.interfaces.UseFormulas;
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

@LocaleResourceLocation(Localizer.LOCALIZER_SCHEME+":prop:chav1961/calc/components/lccontourplugin/lccontour")
@Action(resource=@LocaleResource(value="calculate",tooltip="calculateTooltip"),actionString="calculate",simulateCheck=true) 
//@UseFormulas({LocalizationKeys.FORMULA_COILS_RING_COIL,LocalizationKeys.FORMULA_INDUCTANCE_RING_COIL})
class LCContourCalculator implements FormManager<Object,LCContourCalculator> {
	private static final float		MAGIC = 159000f;
	
	private static final String[]	FIELDS_ANNOTATED = chav1961.calc.environment.Utils.buildFieldsAnnotated(LCContourCalculator.class);
	private static final String 	MESSAGE_FREQUENCY_POSITIVE = "frequencyPositive";
	private static final String 	MESSAGE_INDUCTANCE_POSITIVE = "inductancePositive";
	private static final String 	MESSAGE_CAPACITY_POSITIVE = "capacityPositive";
	
	private final Localizer			localizer;
	private final LoggerFacade		logger;
	
@LocaleResource(value="calcType",tooltip="calcTypeTooltip")	
@Format("10.3ms")
	private ContourCalculationType	calcType = ContourCalculationType.FREQ_BY_L_C;
@LocaleResource(value="frequency",tooltip="frequencyTooltip")	
@Format("10.3s")
	private float					frequency = 0.0f;
@LocaleResource(value="inductance",tooltip="inductanceTooltip")	
@Format("10.3s")
	private float					inductance = 0.0f;
@LocaleResource(value="capacity",tooltip="capacityTooltip")	
@Format("10.3s")
	private float					capacity = 0.0f;

	LCContourCalculator(final Localizer localizer,final LoggerFacade logger) {
		this.localizer = localizer;
		this.logger = logger;		
	}
	
	@Override
	public RefreshMode onRecord(final Action action, final LCContourCalculator oldRecord, final Object oldId, final LCContourCalculator newRecord, final Object newId) throws FlowException, LocalizationException {
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
	public RefreshMode onField(final LCContourCalculator inst, final Object id, final String fieldName, final Object oldValue) throws FlowException, LocalizationException, IllegalArgumentException {
		switch (fieldName) {
			case "frequency"	:
				if (calcType == ContourCalculationType.C_BY_FREQ_L || calcType == ContourCalculationType.L_BY_FREQ_C) {
					return checkAndNotify(frequency > 0,localizer.getValue(MESSAGE_FREQUENCY_POSITIVE),frequency) ? RefreshMode.NONE : RefreshMode.REJECT;
				}
				else {
					return RefreshMode.NONE;
				}
			case "inductance"	:
				if (calcType == ContourCalculationType.C_BY_FREQ_L || calcType == ContourCalculationType.FREQ_BY_L_C) {
					return checkAndNotify(inductance > 0,localizer.getValue(MESSAGE_INDUCTANCE_POSITIVE),inductance) ? RefreshMode.NONE : RefreshMode.REJECT;
				}
				else {
					return RefreshMode.NONE;
				}
			case "capacity"	:
				if (calcType == ContourCalculationType.L_BY_FREQ_C || calcType == ContourCalculationType.FREQ_BY_L_C) {
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
	public RefreshMode onAction(final LCContourCalculator inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "calculate"	:
				switch (calcType) {
					case FREQ_BY_L_C	:
						frequency = (float) (MAGIC / Math.sqrt(inductance * capacity));
						break;
					case C_BY_FREQ_L	:
						capacity = (float) (Math.pow(MAGIC / frequency, 2) / inductance);
						break;
					case L_BY_FREQ_C	:
						inductance = (float) (Math.pow(MAGIC / frequency, 2) / capacity);
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