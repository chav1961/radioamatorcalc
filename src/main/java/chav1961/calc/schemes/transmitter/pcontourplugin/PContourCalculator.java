package chav1961.calc.schemes.transmitter.pcontourplugin;


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

@LocaleResourceLocation(Localizer.LOCALIZER_SCHEME+":prop:chav1961/calc/schemes/transmitter/pcontourplugin/pcontour")
@Action(resource=@LocaleResource(value="calculate",tooltip="calculateTooltip"),actionString="calculate",simulateCheck=true) 
@UseFormulas({LocalizationKeys.FORMULA_NUMBER_OF_COILS_ONE_LAYER_COIL,LocalizationKeys.FORMULA_INDUCTANCE_ONE_LAYER_COIL})
class PContourCalculator implements FormManager<Object,PContourCalculator> {
	private static final String[]	FIELDS_ANNOTATED = chav1961.calc.environment.Utils.buildFieldsAnnotated(PContourCalculator.class);
	private static final float		MAGIC = 530.6f;
	private static final float		ROOT_2 = (float)Math.sqrt(2);

	private static final String 	MESSAGE_INNER_VOLTAGE_POSITIVE = "innerVoltagePositive";
	private static final String 	MESSAGE_INNER_CURRENT_POSITIVE = "innerCurrentPositive";
	private static final String 	MESSAGE_INNER_RESISTANCE_POSITIVE = "innerResistancePositive";
	private static final String 	MESSAGE_OUTER_RESISTANCE_POSITIVE = "outerResistancePositive";
	private static final String 	MESSAGE_OUTER_POWER_POSITIVE = "outerPowerPositive";
	private static final String 	MESSAGE_QUALITY_OUT_OF_RANGE = "qualityOutOfRange";
	private static final String 	MESSAGE_MIN_FREQUENCY_POSITIVE = "minFrequencyPositive";
	private static final String 	MESSAGE_MAX_FREQUENCY_POSITIVE = "maxFrequencyPositive"; 

@LocaleResource(value="contourType",tooltip="contourTypeTooltip")	
@Format("1m")
	private PContourType			contourType = PContourType.SINGLE;
@LocaleResource(value="innerVoltage",tooltip="innerVoltageTooltip")	
@Format("10.3ms")
	private float					innerVoltage = 1.0f;
@LocaleResource(value="innerCurrent",tooltip="innerCurrentTooltip")	
@Format("10.3ms")
	private float					innerCurrent = 1.0f;
@LocaleResource(value="innerResistance",tooltip="innerResistanceTooltip")	
@Format("10.3ms")
	private float					innerResistance = 75.0f;
@LocaleResource(value="outerResistance",tooltip="outerResistanceTooltip")	
@Format("10.3ms")
	private float					outerResistance = 75.0f;
@LocaleResource(value="outerPower",tooltip="outerPowerTooltip")	
@Format("10.3ms")
	private float					outerPower = 1.0f;
@LocaleResource(value="quality",tooltip="qualityTooltip")	
@Format("10.3ms")
	private float					quality = 2.0f;
@LocaleResource(value="minFrequency",tooltip="minFrequencyTooltip")	
@Format("10.3ms")
	private float					minFrequency = 500.0f;
@LocaleResource(value="maxFrequency",tooltip="maxFrequencyTooltip")	
@Format("10.3ms")
	private float					maxFrequency = 500.0f;
@LocaleResource(value="c1Min",tooltip="c1MinTooltip")	
@Format("10.3r")
	private float					c1Min = 0.0f;
@LocaleResource(value="c1Max",tooltip="c1MaxTooltip")	
@Format("10.3r")
	private float					c1Max = 0.0f;
@LocaleResource(value="uC1",tooltip="uC1Tooltip")	
@Format("10.3r")
	private float					uC1 = 0.0f;
@LocaleResource(value="q1",tooltip="q1Tooltip")	
@Format("10.3r")
	private float					q1 = 0.0f;
@LocaleResource(value="c2",tooltip="c2Tooltip")	
@Format("10.3r")
	private float					c2 = 0.0f;
@LocaleResource(value="uC2",tooltip="uC2Tooltip")	
@Format("10.3r")
	private float					uC2 = 0.0f;
@LocaleResource(value="q2",tooltip="q2Tooltip")	
@Format("10.3r")
	private float					q2 = 0.0f;
@LocaleResource(value="c3Min",tooltip="c3MinTooltip")	
@Format("10.3r")
	private float					c3Min = 0.0f;
@LocaleResource(value="c3Max",tooltip="c3MaxTooltip")	
@Format("10.3r")
	private float					c3Max = 0.0f;
@LocaleResource(value="uC3",tooltip="uC3Tooltip")	
@Format("10.3r")
	private float					uC3 = 0.0f;
@LocaleResource(value="q3",tooltip="q3Tooltip")	
@Format("10.3r")
	private float					q3 = 0.0f;
@LocaleResource(value="l1",tooltip="l1Tooltip")	
@Format("10.3r")
	private float					l1 = 0.0f;
@LocaleResource(value="i1",tooltip="i1Tooltip")	
@Format("10.3r")
	private float					i1 = 0.0f;
@LocaleResource(value="l2",tooltip="l2Tooltip")	
@Format("10.3r")
	private float					l2 = 0.0f;
@LocaleResource(value="i2",tooltip="i2Tooltip")	
@Format("10.3r")
	private float					i2 = 0.0f;
	
	private final Localizer			localizer;
	private final LoggerFacade		logger;


	PContourCalculator(final Localizer localizer,final LoggerFacade logger) {
		this.localizer = localizer;
		this.logger = logger;		
	}
	
	@Override
	public RefreshMode onRecord(final Action action, final PContourCalculator oldRecord, final Object oldId, final PContourCalculator newRecord, final Object newId) throws FlowException, LocalizationException {
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
	public RefreshMode onField(final PContourCalculator inst, final Object id, final String fieldName, final Object oldValue) throws FlowException, LocalizationException, IllegalArgumentException {
		switch (fieldName) {
			case "innerVoltage"		:
				return checkAndNotify(innerVoltage > 0,localizer.getValue(MESSAGE_INNER_VOLTAGE_POSITIVE),innerVoltage) ? RefreshMode.NONE : RefreshMode.REJECT;
			case "innerCurrent"		:
				return checkAndNotify(innerCurrent > 0,localizer.getValue(MESSAGE_INNER_CURRENT_POSITIVE),innerCurrent) ? RefreshMode.NONE : RefreshMode.REJECT;
			case "innerResistance"	:
				return checkAndNotify(innerResistance > 0,localizer.getValue(MESSAGE_INNER_RESISTANCE_POSITIVE),innerResistance) ? RefreshMode.NONE : RefreshMode.REJECT;
			case "outerResistance"	:
				return checkAndNotify(outerResistance > 0,localizer.getValue(MESSAGE_OUTER_RESISTANCE_POSITIVE),innerResistance) ? RefreshMode.NONE : RefreshMode.REJECT;
			case "outerPower"		:
				return checkAndNotify(outerPower > 0,localizer.getValue(MESSAGE_OUTER_POWER_POSITIVE),outerPower) ? RefreshMode.NONE : RefreshMode.REJECT;
			case "quality"			:
				return checkAndNotify(quality > 1 && quality < 50,localizer.getValue(MESSAGE_QUALITY_OUT_OF_RANGE),quality) ? RefreshMode.NONE : RefreshMode.REJECT;
			case "minFrequency"		:
				if (checkAndNotify(minFrequency > 0,localizer.getValue(MESSAGE_MIN_FREQUENCY_POSITIVE),minFrequency)) {
					if (maxFrequency < minFrequency) {
						maxFrequency = minFrequency;
						return RefreshMode.RECORD_ONLY;
					}
					else {
						return RefreshMode.NONE;
					}
				}
				else {
					return RefreshMode.REJECT;
				}
			case "maxFrequency"		:
				if (checkAndNotify(maxFrequency > 0,localizer.getValue(MESSAGE_MAX_FREQUENCY_POSITIVE),maxFrequency)) {
					if (maxFrequency < minFrequency) {
						minFrequency = maxFrequency;
						return RefreshMode.RECORD_ONLY;
					}
					else {
						return RefreshMode.NONE;
					}
				}
				else {
					return RefreshMode.REJECT;
				}
			default :
				return RefreshMode.NONE;
		}
	}

	@Override
	public RefreshMode onAction(final PContourCalculator inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "calculate"	:
				c1Min = 0.0f;		c1Max = 0.0f;
				uC1 = 0.0f;			q1 = 0.0f;
				c2 = 0.0f;			uC2 = 0.0f;
				q2 = 0.0f;			c3Min = 0.0f;
				c3Max = 0.0f;		uC3 = 0.0f;
				q3 = 0.0f;			l1 = 0.0f;
				i1 = 0.0f;			l2 = 0.0f;
				i2 = 0.0f;
				
				switch (contourType) {
					case SINGLE :
						final float 	rGeomS = (float)Math.sqrt(innerResistance*outerResistance);
						final float 	x1S = (innerResistance * quality + rGeomS) / (quality * quality - 1);
						final float 	x3S = (outerResistance * quality + rGeomS) / (quality * quality - 1);
						final float 	xL1S = x1S + x1S;
						final float		fGeomS = (float)Math.sqrt(minFrequency*maxFrequency);
						final float		lambdaGeomS = 300000 / fGeomS;
						final float		c1S = MAGIC * lambdaGeomS / x1S;
						final float		c3S = MAGIC * lambdaGeomS / x3S;
						final float		deltaC1S = (float)Math.pow(maxFrequency/fGeomS,2);
						final float		iFirstS = 2 * outerPower / innerVoltage;
						final float		outerVoltageS = (float)Math.sqrt(outerPower * outerResistance);
						final float		iC1S = ROOT_2 * innerVoltage / x1S, iC3S = outerVoltageS / x3S;
						
						c1Min = c1S * (1 - deltaC1S);
						c1Max = c1S * (1 + deltaC1S);
						c3Min = c3S * (1 - deltaC1S);
						c3Max = c3S * (1 + deltaC1S);
						l1 = (float) (xL1S / (2 * Math.PI * fGeomS));
						i1 = (float)Math.sqrt(iFirstS * iFirstS + iC1S * iC1S);
						q1 = ROOT_2 * innerVoltage * iC1S;
						q3 = outerVoltageS * iC3S;
						break;
					case DOUBLE	:
						final float 	rGeom = (float)Math.sqrt(innerResistance*outerResistance);
						final float 	x1 = (innerResistance * quality + rGeom) / (quality * quality - 1);
						final float 	x3 = (outerResistance * quality + rGeom) / (quality * quality - 1);
						final float		x2 = x1 * x3 / rGeom;
						final float 	xL1 = x1 + x2;
						final float		xL2 = x2 + x3;
						final float		fGeom = (float)Math.sqrt(minFrequency*maxFrequency);
						final float		lambdaGeom = 300000 / fGeom;
						final float		c1 = MAGIC * lambdaGeom / x1;
						final float		c3 = MAGIC * lambdaGeom / x3;
						final float		deltaC1 = (float)Math.pow(maxFrequency/fGeom,2) - 1;
						final float		iFirst = 2 * outerPower / innerVoltage;
						final float		outerVoltage = (float)Math.sqrt(outerPower * outerResistance);
						
						c1Min = c1 * (1 - deltaC1);
						c1Max = c1 * (1 + deltaC1);
						c2 = MAGIC * lambdaGeom / x2;
						c3Min = c3 * (1 - deltaC1);
						c3Max = c3 * (1 + deltaC1);
						l1 = (float) (1000 * xL1 / (2 * Math.PI * fGeom));
						l2 = (float) (1000 * xL2 / (2 * Math.PI * fGeom));
						
						final float		iC1 = innerVoltage / (ROOT_2 * x1), iC3 = outerVoltage / x3;
						final float		outerCurrent = outerVoltage / outerResistance;
						
						q1 = innerVoltage * iC1 / ROOT_2;
						q3 = outerVoltage * iC3;
						i1 = (float)Math.sqrt(iFirst * iFirst + iC1 * iC1);
						i2 = (float)Math.sqrt(iC3 * iC3 + outerCurrent * outerCurrent);  

						final float		iC2 = (float)Math.sqrt(i2 * i2 - i1 * i1);
						
						uC1 = iC1 * x1;
						uC2 = iC2 * x2;						
						uC3 = iC3 * x3;
						q2 = uC2 * iC2;
						break;
					default : throw new UnsupportedOperationException("Contour type ["+contourType+"] is not supported");
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