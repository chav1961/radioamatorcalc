package chav1961.calc.elements.coils.singlecoilsplugin;

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

@LocaleResourceLocation(Localizer.LOCALIZER_SCHEME+":prop:chav1961/calc/elements/coils/singlecoilsplugin/singlecoils")
@Action(resource=@LocaleResource(value="calculate",tooltip="calculateTooltip"),actionString="calculate") 
class SingleCoilsCalculator implements FormManager<Object,SingleCoilsCalculator> {
	private static final String		MESSAGE_LENGTH_POSITIVE = "lengthPositive";
	private static final String		MESSAGE_DIAMETER_POSITIVE = "diameterPositive";
	private static final String		MESSAGE_WIREDIAMETER_POSITIVE = "wirediameterPositive";
	private static final String		MESSAGE_INDUCTANCE_NONNEGATIVE = "indictanceNonnegative";
	private static final String		MESSAGE_COILS_NONNEGATIVE = "coilsNonnegative";
	
	private final Localizer			localizer;
	private final LoggerFacade		logger;
	
@LocaleResource(value="length",tooltip="lengthTooltip")
@Format("m")
	private float 					length = 0.0f;
@LocaleResource(value="diameter",tooltip="diameterTooltip")	
@Format("m")
	private float					diameter = 0.0f;
@LocaleResource(value="wireDiameter",tooltip="wireDiameterTooltip")	
@Format("m")
	private float					wireDiameter = 0.0f;
@LocaleResource(value="calcType",tooltip="calcTypeTooltip")	
@Format("m")
	private CoilsCalculationType	calcType = CoilsCalculationType.INDUCTANCE;
@LocaleResource(value="inductance",tooltip="inductanceTooltip")	
	private float					inductance = 0.0f;
@LocaleResource(value="coils",tooltip="coilsTooltip")	
	private float					coils = 0.0f;

	SingleCoilsCalculator(final Localizer localizer,final LoggerFacade logger) {
		this.localizer = localizer;
		this.logger = logger;		
	}
	
	@Override
	public RefreshMode onRecord(final Action action, final SingleCoilsCalculator oldRecord, final Object oldId, final SingleCoilsCalculator newRecord, final Object newId) throws FlowException {
		return RefreshMode.NONE;
	}

	@Override
	public RefreshMode onField(final SingleCoilsCalculator inst, final Object id, final String fieldName, final Object oldValue) throws FlowException, LocalizationException, IllegalArgumentException {
		switch (fieldName) {
			case "length"		:
				if (length <= 0) {
					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_LENGTH_POSITIVE),length);
					return RefreshMode.REJECT;
				}
				break;
			case "diameter"		:
				if (diameter <= 0) {
					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_DIAMETER_POSITIVE),diameter);
					return RefreshMode.REJECT;
				}
				break;
			case "wireDiameter"	:
				if (wireDiameter <= 0) {
					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_WIREDIAMETER_POSITIVE),wireDiameter);
					return RefreshMode.REJECT;
				}
				break;
			case "inductance"	:
				if (inductance < 0) {
					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_INDUCTANCE_NONNEGATIVE),inductance);
					return RefreshMode.REJECT;
				}
				break;
			case "coils"		:
				if (coils < 0) {
					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_COILS_NONNEGATIVE),coils);
					return RefreshMode.REJECT;
				}
				break;
			default :
		}
		return RefreshMode.NONE;
	}

	@Override
	public RefreshMode onAction(final SingleCoilsCalculator inst, final Object id, final String actionName, final Object parameter) throws FlowException {
		switch (actionName) {
			case "calculate"	:
				if (calcType == CoilsCalculationType.INDUCTANCE) {
					inductance = 0.1f * diameter * coils * coils / (length/diameter + 0.44f);
				}
				else {
					inductance = 0.1f * diameter * coils * coils / (length/diameter + 0.44f);
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
}