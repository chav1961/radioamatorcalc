package chav1961.calc.plugins.details.flybacktrans;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.calc.interfaces.RingMyu;
import chav1961.calc.interfaces.RingType;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.basic.interfaces.ModuleAccessor;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.ui.interfaces.Action;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.details.flybacktrans.FlybackTransPlugin/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.details.flybacktrans",tooltip="menu.details.flybacktrans.tt",help="help.aboutApplication")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.button.calc",tooltip="chav1961.calc.plugins.details.flybacktrans.button.calc.tt"),actionString="calculate")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.button.calcdetailed",tooltip="chav1961.calc.plugins.details.flybacktrans.button.calcdetailed.tt"),actionString="calculateDetailed")
@PluginProperties(width=680,height=640,leftWidth=320,svgURI="schema.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
public class FlybackTransPlugin implements FormManager<Object,FlybackTransPlugin>, ModuleAccessor {
	private static final float	MYU_0 =  1.257e-3f;
	private static final float	U_DIODE = 0.7f;
	private static final float	ETHA = 0.9f;
	
	private final LoggerFacade 	logger;
	
	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.inputVoltage",tooltip="chav1961.calc.plugins.details.flybacktrans.inputVoltage.tt")
	@Format("9.2mpzs")
	public float 		inputVoltage = 310;

	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.outputVoltage1",tooltip="chav1961.calc.plugins.details.flybacktrans.outputVoltage1.tt")
	@Format("9.2mpzs")
	public float 		outputVoltage1;
	
	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.outputVoltage2",tooltip="chav1961.calc.plugins.details.flybacktrans.outputVoltage2.tt")
	@Format("9.2pzs")
	public float 		outputVoltage2;
	
	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.outputVoltage3",tooltip="chav1961.calc.plugins.details.flybacktrans.outputVoltage3.tt")
	@Format("9.2pzs")
	public float 		outputVoltage3;

	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.inputCurrent",tooltip="chav1961.calc.plugins.details.flybacktrans.inputCurrent.tt")
	@Format("9.2or")
	public float		inputCurrent;
	
	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.outputCurrent1",tooltip="chav1961.calc.plugins.details.flybacktrans.outputCurrent1.tt")
	@Format("9.2mpzs")
	public float		outputCurrent1;
	
	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.outputCurrent2",tooltip="chav1961.calc.plugins.details.flybacktrans.outputCurrent2.tt")
	@Format("9.2pzs")
	public float		outputCurrent2;
	
	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.outputCurrent3",tooltip="chav1961.calc.plugins.details.flybacktrans.outputCurrent3.tt")
	@Format("9.2pzs")
	public float		outputCurrent3;
	
	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.induction",tooltip="chav1961.calc.plugins.details.flybacktrans.induction.tt")
	@Format("9.2mpzs")
	public float 		induction = 0.7f;

	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.usemaxinduction",tooltip="chav1961.calc.plugins.details.flybacktrans.usemaxinduction.tt")
	@Format("1m")
	public boolean		useMaxInduction = false;
	
	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.frequency",tooltip="chav1961.calc.plugins.details.flybacktrans.frequency.tt")
	@Format("9.2mpzs")
	public float 		frequency = 80;
	
	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.permability",tooltip="chav1961.calc.plugins.details.flybacktrans.permability.tt")
	@Format("40m")
	public RingMyu 		ringMui = RingMyu.MUI_140;
	
	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.ringtype",tooltip="chav1961.calc.plugins.details.flybacktrans.ringtype.tt")
	@Format("40m")
	public RingType		ringType = RingType.K20x12x6;

	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.fieldinduction",tooltip="chav1961.calc.plugins.details.flybacktrans.fieldinduction.tt")
	@Format("9or")
	public float		fieldInduction;
	
	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.inputCoils",tooltip="chav1961.calc.plugins.details.flybacktrans.inputCoils.tt")
	@Format("9or")
	public int			inputCoils;
	
	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.outputCoils1",tooltip="chav1961.calc.plugins.details.flybacktrans.outputCoils1.tt")
	@Format("9or")
	public int			outputCoils1;
	
	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.outputCoils2",tooltip="chav1961.calc.plugins.details.flybacktrans.outputCoils2.tt")
	@Format("9or")
	public int			outputCoils2;
	
	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.outputCoils3",tooltip="chav1961.calc.plugins.details.flybacktrans.outputCoils3.tt")
	@Format("9or")
	public int			outputCoils3;

	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.inputDiameter",tooltip="chav1961.calc.plugins.details.flybacktrans.inputDiameter.tt")
	@Format("9.2or")
	public float		inputDiameter;
	
	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.outputDiameter1",tooltip="chav1961.calc.plugins.details.flybacktrans.outputDiameter1.tt")
	@Format("9.2or")
	public float		outputDiameter1;
	
	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.outputDiameter2",tooltip="chav1961.calc.plugins.details.flybacktrans.outputDiameter2.tt")
	@Format("9.2or")
	public float		outputDiameter2;
	
	@LocaleResource(value="chav1961.calc.plugins.details.flybacktrans.outputDiameter3",tooltip="chav1961.calc.plugins.details.flybacktrans.outputDiameter3.tt")
	@Format("9.2or")
	public float		outputDiameter3;
	
 	public FlybackTransPlugin(final LoggerFacade logger) {
		if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.logger = logger;
		}
	}
	
	@Override
	public RefreshMode onField(final FlybackTransPlugin inst, final Object id, final String fieldName, final Object oldValue, final boolean beforeCommit) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onAction(final FlybackTransPlugin inst, final Object id, final String actionName, final Object... parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "app:action:/FlybackTransPlugin.calculate"	:
				if (inputVoltage == 0 || outputVoltage1 == 0 || outputCurrent1 == 0 || induction == 0 || frequency == 0) {
					getLogger().message(Severity.warning,"Ui == 0 || Uo1 == 0 || Io1 == 0 || Bn == 0 || F == 0");
					return RefreshMode.NONE;
				}
				else {
					return calculate(false);
				}
			case "app:action:/FlybackTransPlugin.calculateDetailed"	:
				if (inputVoltage == 0 || outputVoltage1 == 0 || outputCurrent1 == 0 || induction == 0 || frequency == 0) {
					getLogger().message(Severity.warning,"Ui == 0 || Uo1 == 0 || Io1 == 0 || Bn == 0 || F == 0");
					return RefreshMode.NONE;
				}
				else {
					return calculate(true);
				}
			default :
				throw new UnsupportedOperationException("Unknown action string ["+actionName+"]");
		}
	}
	
	@Override
	public LoggerFacade getLogger() {
		return logger;
	}

	@Override
	public void allowUnnamedModuleAccess(final Module... unnamedModules) {
		for (Module item : unnamedModules) {
			this.getClass().getModule().addExports(this.getClass().getPackageName(),item);
		}
	}
	
	private RefreshMode calculate(final boolean detailed) {
		final float	totalTargetPower = (outputVoltage1 + U_DIODE) * outputCurrent1 + (outputVoltage2 + U_DIODE) * outputCurrent2 + (outputVoltage3 + U_DIODE) * outputCurrent3;
		final float	totalSourcePower = totalTargetPower / ETHA, cyclePower = totalSourcePower / frequency;  
		final float	deltaT = 1 / (2 * frequency);
		final float	wireN = 2;
		final float j = 1.87f;
		
		inputCurrent = (float)(cyclePower / (inputVoltage * deltaT));
		
		float	inductance = 1000 * inputVoltage * deltaT / inputCurrent;
		int		coils = calculateCoils(inductance);
		
		fieldInduction = (float) (MYU_0*ringMui.getMyu()*inputCurrent*coils/ringType.getMiddleLen());

		if (fieldInduction < induction) {
			if (useMaxInduction) {
				final float	koeff = induction / fieldInduction;
				
				inputCurrent *= koeff * koeff;
				inductance /= koeff * koeff;
				coils = calculateCoils(inductance);
				fieldInduction = (float) (MYU_0*ringMui.getMyu()*inputCurrent*coils/ringType.getMiddleLen());
			}
			inputCoils = coils;
			inputDiameter = (float) (1.13 * Math.sqrt(inputCurrent / (j * wireN)));
			outputCoils1 = (int) (inputCoils * (outputVoltage1 + U_DIODE) / inputVoltage);
			outputDiameter1 = (float) (1.13 * Math.sqrt(outputCurrent1 / (j * wireN)));
			if (outputVoltage2 != 0 && outputCurrent2 != 0) {
				outputCoils2 = (int) (inputCoils * (outputVoltage2 + U_DIODE) / inputVoltage);
				outputDiameter2 = (float) (1.13 * Math.sqrt(outputCurrent2 / (j * wireN)));
			}
			if (outputVoltage3 != 0 && outputCurrent3 != 0) {
				outputCoils3 = (int) (inputCoils * (outputVoltage3  + U_DIODE) / inputVoltage);
				outputDiameter3 = (float) (1.13 * Math.sqrt(outputCurrent3 / (j * wireN)));
			}
			return RefreshMode.RECORD_ONLY;
		}
		else {
			getLogger().message(Severity.warning,"induction ["+fieldInduction+"] greater than max ["+induction+"]");
			inputCoils = outputCoils1 = outputCoils2 = outputCoils3 = 0;
			inputDiameter = outputDiameter1 = outputDiameter2 = outputDiameter3 = 0f;
			return RefreshMode.NONE;
		}
	}
	
	private int calculateCoils(final float inductance) {
		if (ringType.getOuterDiameter()/ringType.getInnerDiameter() > 1.75f) {
			return (int) (100 * Math.sqrt(inductance / (2 * ringMui.getMyu() * ringType.getHeight() * Math.log(ringType.getOuterDiameter()/ringType.getInnerDiameter()))));
		}
		else {
			return (int) (100 * Math.sqrt(inductance * (ringType.getOuterDiameter() + ringType.getInnerDiameter())/ (4 * ringMui.getMyu() * ringType.getHeight() * (ringType.getOuterDiameter() - ringType.getInnerDiameter()))));
		}
	}
}