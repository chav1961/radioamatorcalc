package chav1961.calc.elements.coils.ringcoilsplugin;

import chav1961.calc.elements.coils.CoilsCalculationType;
import chav1961.calc.formulas.Utils;
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
@Action(resource=@LocaleResource(value="calculate",tooltip="calculateTooltip"),actionString="calculate") 
class RingCoilsCalculator implements FormManager<Object,RingCoilsCalculator> {
	private static final String		MESSAGE_HEIGHT_POSITIVE = "heightPositive";
	private static final String		MESSAGE_DIAMETER_POSITIVE = "diameterPositive";
	private static final String		MESSAGE_DIAMETER_INNER_GREATER = "diameterInnerGreater";
	private static final String		MESSAGE_PERMABILITY_GREAT_ONE = "permabilityGreatOne";
	private static final String		MESSAGE_WIREDIAMETER_POSITIVE = "wirediameterPositive";
	private static final String		MESSAGE_INDUCTANCE_NONNEGATIVE = "indictanceNonnegative";
	private static final String		MESSAGE_COILS_NONNEGATIVE = "coilsNonnegative";
	
	private final Localizer			localizer;
	private final LoggerFacade		logger;
	
@LocaleResource(value="calcType",tooltip="calcTypeTooltip")	
@Format("10.3m")
	private CoilsCalculationType	calcType = CoilsCalculationType.INDUCTANCE;
@LocaleResource(value="outerDiameter",tooltip="outerDiameterTooltip")	
@Format("10.3m")
	private float					outerDiameter = 0.0f;
@LocaleResource(value="innerDiameter",tooltip="innerDiameterTooltip")	
@Format("10.3m")
	private float					innerDiameter = 0.0f;
@LocaleResource(value="height",tooltip="heightTooltip")	
@Format("10.3m")
	private float					height = 0.0f;
@LocaleResource(value="permability",tooltip="permabilityTooltip")	
@Format("4m")
	private int						permability = 1;
@LocaleResource(value="inductance",tooltip="inductanceTooltip")	
@Format("10.3")
	private float					inductance = 0.0f;
@LocaleResource(value="wireDiameter",tooltip="wireDiameterTooltip")	
@Format("10.3")
	private float					wireDiameter = 0.0f;
@LocaleResource(value="coils",tooltip="coilsTooltip")	
@Format("10")
	private int						coils = 0;
@LocaleResource(value="wireLength",tooltip="wireLengthTooltip")	
@Format("10.3")
	private float					wireLength = 0.0f;

	RingCoilsCalculator(final Localizer localizer,final LoggerFacade logger) {
		this.localizer = localizer;
		this.logger = logger;		
	}
	
	@Override
	public RefreshMode onRecord(final Action action, final RingCoilsCalculator oldRecord, final Object oldId, final RingCoilsCalculator newRecord, final Object newId) throws FlowException {
		return RefreshMode.NONE;
	}

	@Override
	public RefreshMode onField(final RingCoilsCalculator inst, final Object id, final String fieldName, final Object oldValue) throws FlowException, LocalizationException, IllegalArgumentException {
		switch (fieldName) {
			case "height"		:
				if (height <= 0) {
					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_HEIGHT_POSITIVE),height);
					return RefreshMode.REJECT;
				}
				break;
			case "outerDiameter"		:
				if (outerDiameter <= 0) {
					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_DIAMETER_POSITIVE),outerDiameter);
					return RefreshMode.REJECT;
				}
				else if (outerDiameter < innerDiameter) {
					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_DIAMETER_INNER_GREATER),outerDiameter,innerDiameter);
					return RefreshMode.REJECT;
				}
				break;
			case "innerDiameter"		:
				if (innerDiameter <= 0) {
					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_DIAMETER_POSITIVE),innerDiameter);
					return RefreshMode.REJECT;
				}
				else if (outerDiameter < innerDiameter) {
					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_DIAMETER_INNER_GREATER),outerDiameter,innerDiameter);
					return RefreshMode.REJECT;
				}
				break;
			case "permability"		:
				if (permability < 1) {
					getLogger().message(Severity.warning,localizer.getValue(MESSAGE_PERMABILITY_GREAT_ONE),permability);
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
	public RefreshMode onAction(final RingCoilsCalculator inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "calculate"	:
				switch (calcType) {
					case INDUCTANCE		:
						inductance = (float) Utils.ringCoilsInductance(coils, outerDiameter, innerDiameter, height, permability);
						break;
					case NUMBER_OF_COILS:
						if (outerDiameter/innerDiameter > 1.75) {
							coils = (int) Math.sqrt(inductance / (0.0002f * permability * height * Math.log(outerDiameter/innerDiameter)));  
						}
						else {
							coils = (int) Math.sqrt(inductance / (0.0004f * permability * height * (outerDiameter - innerDiameter) / (outerDiameter + innerDiameter)));
						}
						
						int 	restOfCoils = coils;
						float 	currentInnerDiameter = innerDiameter - wireDiameter;
						
						while (restOfCoils > 0 && currentInnerDiameter > 0) {
							restOfCoils -= Math.PI * currentInnerDiameter / wireDiameter;
							currentInnerDiameter -= wireDiameter;
						}
						
						if (currentInnerDiameter < 0) {
							getLogger().message(Severity.warning,localizer.getValue(MESSAGE_COILS_NONNEGATIVE),coils);
							return RefreshMode.REJECT;
						}
						else {
							float currentWireLength = 0.0f, currentCoilLength = 2 * ((outerDiameter - innerDiameter) + height) + 4 * wireDiameter;
							
							restOfCoils = coils;
							currentInnerDiameter = innerDiameter - wireDiameter;

							while (restOfCoils > 0 && currentInnerDiameter > 0) {
								int coilsInLayer = (int) (Math.PI * currentInnerDiameter / wireDiameter);
								
								currentWireLength += currentCoilLength * coilsInLayer;
								currentInnerDiameter -= wireDiameter;
								restOfCoils -= coilsInLayer;
								currentCoilLength += 4 * wireDiameter;
							}
							wireLength = currentWireLength;
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
}