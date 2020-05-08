package chav1961.calc.plugins.details.ringpulsetrans;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.calc.interfaces.RingMyu;
import chav1961.calc.interfaces.RingType;
import chav1961.calc.plugins.details.CoilsUtil;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.ui.interfaces.Action;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.details.ringpulsetrans.RingPulseTransPlugin/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.details.ringpulsetrans",tooltip="menu.details.ringpulsetrans.tt",help="help.aboutApplication")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.button.calc",tooltip="chav1961.calc.plugins.details.ringpulsetrans.button.calc.tt"),actionString="calculate")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.button.calcdetailed",tooltip="chav1961.calc.plugins.details.ringpulsetrans.button.calcdetailed.tt"),actionString="calculateDetailed")
@PluginProperties(width=680,height=620,leftWidth=320,svgURI="schema.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
public class RingPulseTransPlugin implements FormManager<Object,RingPulseTransPlugin> {
	private final LoggerFacade 	logger;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage.tt")
	@Format("9.2mpzs")
	public float 		inputVoltage;

	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.outputVoltage1",tooltip="chav1961.calc.plugins.details.ringpulsetrans.outputVoltage1.tt")
	@Format("9.2mpzs")
	public float 		outputVoltage1;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.outputVoltage2",tooltip="chav1961.calc.plugins.details.ringpulsetrans.outputVoltage2.tt")
	@Format("9.2pzs")
	public float 		outputVoltage2;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.outputVoltage3",tooltip="chav1961.calc.plugins.details.ringpulsetrans.outputVoltage3.tt")
	@Format("9.2pzs")
	public float 		outputVoltage3;

	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputCurrent",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputCurrent.tt")
	@Format("9.2or")
	public float		inputCurrent;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.outputCurrent1",tooltip="chav1961.calc.plugins.details.ringpulsetrans.outputCurrent1.tt")
	@Format("9.2mpzs")
	public float		outputCurrent1;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.outputCurrent2",tooltip="chav1961.calc.plugins.details.ringpulsetrans.outputCurrent2.tt")
	@Format("9.2pzs")
	public float		outputCurrent2;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.outputCurrent3",tooltip="chav1961.calc.plugins.details.ringpulsetrans.outputCurrent3.tt")
	@Format("9.2pzs")
	public float		outputCurrent3;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.induction",tooltip="chav1961.calc.plugins.details.ringpulsetrans.induction.tt")
	@Format("9.2mpzs")
	public float 		induction = 0.25f;

	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.frequency",tooltip="chav1961.calc.plugins.details.ringpulsetrans.frequency.tt")
	@Format("9.2mpzs")
	public float 		frequency = 80;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.permability",tooltip="chav1961.calc.plugins.details.ringpulsetrans.permability.tt")
	@Format("40m")
	public RingMyu 		ringMui = RingMyu.MUI_2000;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.ringtype",tooltip="chav1961.calc.plugins.details.ringpulsetrans.ringtype.tt")
	@Format("40m")
	public RingType		ringType = RingType.K28x16x9;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.schematype",tooltip="chav1961.calc.plugins.details.ringpulsetrans.schematype.tt")
	@Format("40m")
	public SchemaType	schemaType = SchemaType.HALF_BRIDGE;

	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputCoils",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputCoils.tt")
	@Format("9or")
	public int			inputCoils;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.outputCoils1",tooltip="chav1961.calc.plugins.details.ringpulsetrans.outputCoils1.tt")
	@Format("9or")
	public int			outputCoils1;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.outputCoils2",tooltip="chav1961.calc.plugins.details.ringpulsetrans.outputCoils2.tt")
	@Format("9or")
	public int			outputCoils2;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.outputCoils3",tooltip="chav1961.calc.plugins.details.ringpulsetrans.outputCoils3.tt")
	@Format("9or")
	public int			outputCoils3;

	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputDiameter",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputDiameter.tt")
	@Format("9.2or")
	public float		inputDiameter;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.outputDiameter1",tooltip="chav1961.calc.plugins.details.ringpulsetrans.outputDiameter1.tt")
	@Format("9.2or")
	public float		outputDiameter1;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.outputDiameter2",tooltip="chav1961.calc.plugins.details.ringpulsetrans.outputDiameter2.tt")
	@Format("9.2or")
	public float		outputDiameter2;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.outputDiameter3",tooltip="chav1961.calc.plugins.details.ringpulsetrans.outputDiameter3.tt")
	@Format("9.2or")
	public float		outputDiameter3;
	
 	public RingPulseTransPlugin(final LoggerFacade logger) {
		if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.logger = logger;
		}
	}
	
	@Override
	public RefreshMode onField(final RingPulseTransPlugin inst, final Object id, final String fieldName, final Object oldValue) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onAction(final RingPulseTransPlugin inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "app:action:/RingPulseTransPlugin.calculate"	:
				if (inputVoltage == 0 || outputVoltage1 == 0 || outputCurrent1 == 0 || induction == 0 || frequency == 0) {
					getLogger().message(Severity.warning,"Ui == 0 || Uo1 == 0 || Io1 == 0 || Bn == 0 || F == 0");
					return RefreshMode.NONE;
				}
				else {
					calculate(false);
					return RefreshMode.RECORD_ONLY;
				}
			case "app:action:/RingPulseTransPlugin.calculateDetailed"	:
				if (inputVoltage == 0 || outputVoltage1 == 0 || outputCurrent1 == 0 || induction == 0 || frequency == 0) {
					getLogger().message(Severity.warning,"Ui == 0 || Uo1 == 0 || Io1 == 0 || Bn == 0 || F == 0");
					return RefreshMode.NONE;
				}
				else {
					calculate(true);
					return RefreshMode.RECORD_ONLY;
				}
			default :
				throw new UnsupportedOperationException("Unknown action string ["+actionName+"]");
		}
	}
	
	@Override
	public LoggerFacade getLogger() {
		return logger;
	}
	
	// @see http://vicgain.sdot.ru/Programs/Calculation_pulsed_transformer.pdf
	private void calculate(final boolean detailed) {
		final double	pTarget = outputVoltage1 * outputCurrent1 + outputVoltage2 * outputCurrent2 + outputVoltage3 * outputCurrent3;
		final double	etha = 0.99 - 0.175/frequency - (1 + 9.95 / Math.pow(frequency,1.3))/pTarget;
		final double	pUsed = pTarget / etha;
		final double	squareC = ringType.getSquare() * 1e-6;
		final double	squareO = Math.PI * ringType.getInnerDiameter() * ringType.getInnerDiameter() / 4e6;
		final double	lMid = ringType.getMiddleLen() * 1e-3;
		final double	bm = 0.625 * induction;
		final double	s = 1;
		final double	kc = 1;
		final double	km = pTarget < 15 ? 0.1 : 0.15;
		final double	kf = 1;
		final double	wireN = 1;

		double	j = 1.87, jOld = 0;	// 1.5 + 24/Math.sqrt(pGab); 
		double	pGab = 2e9 * squareC * squareO * frequency * bm * etha * j * s * kc * km * kf;

		while (Math.abs(jOld - j)/j > 0.1) {	// Iterate value...
			jOld = j;
			j = 1.5 + 24/Math.sqrt(pGab);
			pGab = 2e9 * squareC * squareO * frequency * bm * etha * j * s * kc * km * kf;
		}
		
		if (pGab > 1.2 * pTarget) {
			final double	uI;
			final double	inputCurrentM; 
			
			switch (schemaType) {
				case BRIDGE			:
					uI = inputVoltage;
					inputCurrentM = pUsed / uI;
					break;
				case HALF_BRIDGE	:
					uI = inputVoltage / 2;
					inputCurrentM = pUsed / uI;
					break;
				case MIDDLE_POINT	:
					uI = 2 * inputVoltage;
					inputCurrentM = 2 * pUsed / uI;
					break;
				default:
					throw new UnsupportedOperationException("Schema type ["+schemaType+"] is not supported yet");
			}
			inputCoils = (int) (uI / (4e3 * frequency * bm * squareC * kc * kf));
			
			final double 	inputInductance = inputCoils * inputCoils * ringMui.getMyu() * CoilsUtil.MYU0 * squareC / lMid;
			final double	inputCurrentT;
			
			switch (schemaType) {
				case BRIDGE			:
					inputCurrentT = uI / (4e3 * frequency * inputInductance);
					break;
				case HALF_BRIDGE	:
					inputCurrentT = uI / (4e3 * frequency * inputInductance);
					break;
				case MIDDLE_POINT	:
					inputCurrentT = uI / (2e3 * frequency * inputInductance);
					break;
				default:
					throw new UnsupportedOperationException("Schema type ["+schemaType+"] is not supported yet");
			}
			
			if (inputCurrentT > 0.1 * inputCurrentM) {
				inputCurrent = (float) (inputCurrentM + inputCurrentT);
			
				inputDiameter = (float) (1.13 * Math.sqrt(inputCurrent / (j * wireN)));
				
				outputCoils1 = (int) (inputCoils * outputVoltage1 / uI);
				outputDiameter1 = (float) (1.13 * Math.sqrt(outputCurrent1 / (j * wireN)));
				
				if (outputVoltage2 * outputCurrent2 != 0) {
					outputCoils2 = (int) (inputCoils * outputVoltage2 / uI); 
					outputDiameter2 = (float) (1.13 * Math.sqrt(outputCurrent2 / (j * wireN)));
				}

				if (outputVoltage3 * outputCurrent3 != 0) {
					outputCoils3 = (int) (inputCoils * outputVoltage3 / uI); 
					outputDiameter3 = (float) (1.13 * Math.sqrt(outputCurrent3 / (j * wireN)));
				}
			}
			else {
				getLogger().message(Severity.warning,"It > 0.1 Im");
			}
		}
		else {
			getLogger().message(Severity.warning,"pGab < 1.2 pTarget");
		}
	}
}