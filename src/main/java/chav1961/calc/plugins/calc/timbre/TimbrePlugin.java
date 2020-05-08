package chav1961.calc.plugins.calc.timbre;

import chav1961.calc.interfaces.PluginProperties;


import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;
import chav1961.purelib.ui.interfaces.Action;

//1. R2=R7=100k
//2. Ku=10
//3. F1=20, F4=10000
//3. R4=R1=R2/Ku, R3 = R1/Ku
//4. C2=1/(2*pi*F1*R2)
//5. C1=C2/Ku
//6. C3=1000, C4= C3*Ku
//7. R5=1/(2*pi*F2*C3)
//8. R6=R5*Ku
//
//1. R2=R6=100k
//2. Ku=10
//3. F1=20, F4=10000
//4. R1=R3=R4=R2/Ku
//5. C1=C2=1/(2*pi*F1*R2)
//6. R5=R7=(R1+2*R4)/(Ku-1)
//7. C3=1/(2*pi*F4*R5)

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.calc.timbre.TimbrePlugin/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.curcuits.timbre",tooltip="menu.curcuits.timbre.tt",help="help.aboutApplication")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.calc.timbre.button.calc",tooltip="chav1961.calc.plugins.calc.timbre.button.calc.tt"),actionString="calculate")
@PluginProperties(width=500,height=460,leftWidth=250,svgURI="schema1.SVG,schema2.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
public class TimbrePlugin implements FormManager<Object,TimbrePlugin> {
	private final LoggerFacade 	logger;
	
	@LocaleResource(value="chav1961.calc.plugins.calc.timbre.type",tooltip="chav1961.calc.plugins.calc.timbre.type.tt")
	@Format("9.2m")
	public TimbreType 	timbreType = TimbreType.ACTIVE;
	
	@LocaleResource(value="chav1961.calc.plugins.calc.timbre.resistance",tooltip="chav1961.calc.plugins.calc.timbre.resistance.tt")
	@Format("9.2mpzs")
	public float 		resistance = 0;
	
	@LocaleResource(value="chav1961.calc.plugins.calc.timbre.ku",tooltip="chav1961.calc.plugins.calc.timbre.ku.tt")
	@Format("9.2mpzs")
	public float 		kU = 20;
	
	@LocaleResource(value="chav1961.calc.plugins.calc.timbre.lowFrequency",tooltip="chav1961.calc.plugins.calc.timbre.lowFrequency.tt")
	@Format("9.2mpzs")
	public float 		lowFrequency = 0;
	
	@LocaleResource(value="chav1961.calc.plugins.calc.timbre.highFrequency",tooltip="chav1961.calc.plugins.calc.timbre.highFrequency.tt")
	@Format("9.2mpzs")
	public float 		highFrequency = 0;

	@LocaleResource(value="chav1961.calc.plugins.calc.timbre.r1",tooltip="chav1961.calc.plugins.calc.timbre.r1.tt")
	@Format("9.2or")
	public float 		r1 = 0;

	@LocaleResource(value="chav1961.calc.plugins.calc.timbre.r2",tooltip="chav1961.calc.plugins.calc.timbre.r2.tt")
	@Format("9.2or")
	public float 		r2 = 0;

	@LocaleResource(value="chav1961.calc.plugins.calc.timbre.r3",tooltip="chav1961.calc.plugins.calc.timbre.r3.tt")
	@Format("9.2or")
	public float 		r3 = 0;

	@LocaleResource(value="chav1961.calc.plugins.calc.timbre.r4",tooltip="chav1961.calc.plugins.calc.timbre.r4.tt")
	@Format("9.2or")
	public float 		r4 = 0;

	@LocaleResource(value="chav1961.calc.plugins.calc.timbre.r5",tooltip="chav1961.calc.plugins.calc.timbre.r5.tt")
	@Format("9.2or")
	public float 		r5 = 0;

	@LocaleResource(value="chav1961.calc.plugins.calc.timbre.r6",tooltip="chav1961.calc.plugins.calc.timbre.r6.tt")
	@Format("9.2or")
	public float 		r6 = 0;

	@LocaleResource(value="chav1961.calc.plugins.calc.timbre.c1",tooltip="chav1961.calc.plugins.calc.timbre.c1.tt")
	@Format("9.2or")
	public float 		c1 = 0;

	@LocaleResource(value="chav1961.calc.plugins.calc.timbre.c2",tooltip="chav1961.calc.plugins.calc.timbre.c2.tt")
	@Format("9.2or")
	public float 		c2 = 0;

	@LocaleResource(value="chav1961.calc.plugins.calc.timbre.c3",tooltip="chav1961.calc.plugins.calc.timbre.c3.tt")
	@Format("9.2or")
	public float 		c3 = 0;

	@LocaleResource(value="chav1961.calc.plugins.calc.timbre.c4",tooltip="chav1961.calc.plugins.calc.timbre.c4.tt")
	@Format("9.2or")
	public float 		c4 = 0;
	
	public TimbrePlugin(final LoggerFacade logger) {
		if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.logger = logger;
		}
	}
	
	@Override
	public RefreshMode onField(final TimbrePlugin inst, final Object id, final String fieldName, final Object oldValue) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onAction(final TimbrePlugin inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "app:action:/TimbrePlugin.calculate"	:
				if (resistance == 0 || kU == 0 || lowFrequency == 0 || highFrequency == 0) {
					getLogger().message(Severity.warning,"R == 0 || Ku == 0 || Fb == 0 || Ft == 0");
					return RefreshMode.NONE;
				}
				else {
					final float	koeff = (float) Math.pow(10,kU/20);  

					// see VRL 104
					switch (timbreType) {
						case ACTIVE		:
							r1 = r2 = resistance / koeff;
							c1 = (float) (1 / (2e-6 * Math.PI * lowFrequency * resistance));
							r3 = (r1 + 2 * r2)/(koeff - 1);
							c2 = (float) (1/(2e-6 * Math.PI * highFrequency * resistance));
							break;
						case PASSIVE	:
							r4 = r1 = resistance/koeff;
							r3 = r1/koeff;
							c2 = (float) (1/(2e-6 * Math.PI * lowFrequency * resistance));
							c1 = c2/koeff;
							c3 = 1;
							c4 = c3 * koeff;
							r5 = (float) (1/(2e-6*Math.PI*highFrequency*c3));
							r6 = r5/koeff;
							break;
						default:
							throw new UnsupportedOperationException("Timpbe type ["+timbreType+"] is not supported yet");
					}
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
}