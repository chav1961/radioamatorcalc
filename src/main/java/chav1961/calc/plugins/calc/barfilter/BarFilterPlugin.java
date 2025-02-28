package chav1961.calc.plugins.calc.barfilter;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.ModuleAccessor;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.ui.interfaces.Action;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.calc.barfilter.BarFilterPlugin/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.curcuits.barfilter",tooltip="menu.curcuits.barfilter.tt",help="help.aboutApplication")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.calc.barfilter.calc",tooltip="chav1961.calc.plugins.calc.barfilter.calc.tt"),actionString="calculate")
@PluginProperties(width=600,height=435,leftWidth=350,svgURI="schema1.SVG,schema2.SVG,schema3.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
public class BarFilterPlugin implements FormManager<Object,BarFilterPlugin>, ModuleAccessor {
	private final LoggerFacade 	logger;
	
	@LocaleResource(value="chav1961.calc.plugins.calc.barfilter.type",tooltip="chav1961.calc.plugins.calc.barfilter.type.tt")
	@Format("9.2m")
	public BarFilterType type = BarFilterType.PARALLEL_TYPE;
	@LocaleResource(value="chav1961.calc.plugins.calc.barfilter.nodecount",tooltip="chav1961.calc.plugins.calc.barfilter.nodecount.tt")
	@Format("1mps")
	public int nodeCount = 1;
	@LocaleResource(value="chav1961.calc.plugins.calc.barfilter.centralfreq",tooltip="chav1961.calc.plugins.calc.barfilter.centralfreq.tt")
	@Format("9.2mpzs")
	public float centralFreq = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.barfilter.passbarwidth",tooltip="chav1961.calc.plugins.calc.barfilter.passbarwidth.tt")
	@Format("9.2mpzs")
	public float passBarWidth = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.barfilter.inputresistance",tooltip="chav1961.calc.plugins.calc.barfilter.inputresistance.tt")
	@Format("9mpzs")
	public float inputResistance = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.barfilter.outputresistance",tooltip="chav1961.calc.plugins.calc.barfilter.outputresistance.tt")
	@Format("9mpzs")
	public float outputResistance = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.barfilter.charresistance",tooltip="chav1961.calc.plugins.calc.barfilter.charresistance.tt")
	@Format("9mpzs")
	public float charResistance = 0;

	@LocaleResource(value="chav1961.calc.plugins.calc.barfilter.inductance1",tooltip="chav1961.calc.plugins.calc.barfilter.inductance1.tt")
	@Format("9.3ro")
	public float inductance1 = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.barfilter.capacitance1",tooltip="chav1961.calc.plugins.calc.barfilter.capacitance1.tt")
	@Format("9.2ro")
	public float capacitance1 = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.barfilter.inductance2",tooltip="chav1961.calc.plugins.calc.barfilter.inductance2.tt")
	@Format("9.3ro")
	public float inductance2 = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.barfilter.capacitance2",tooltip="chav1961.calc.plugins.calc.barfilter.capacitance2.tt")
	@Format("9.2ro")
	public float capacitance2 = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.barfilter.capacitance3",tooltip="chav1961.calc.plugins.calc.barfilter.capacitance3.tt")
	@Format("9.2ro")
	public float capacitance3 = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.barfilter.trans1",tooltip="chav1961.calc.plugins.calc.barfilter.trans1.tt")
	@Format("9.2ro")
	public float trans1 = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.barfilter.trans2",tooltip="chav1961.calc.plugins.calc.barfilter.trans2.tt")
	@Format("9.2ro")
	public float trans2 = 0;
	
	public BarFilterPlugin(final LoggerFacade logger) {
		if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.logger = logger;
		}
	}
	
	@Override
	public RefreshMode onField(final BarFilterPlugin inst, final Object id, final String fieldName, final Object oldValue, final boolean beforeCommit) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onAction(final BarFilterPlugin inst, final Object id, final String actionName, final Object... parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "app:action:/BarFilterPlugin.calculate"	:
				switch (type) {
					case G_TYPE			:
						final float	rG = charResistance;
						final float	qG = centralFreq / (2 * passBarWidth);
						
						inductance1 = (float) (1e6 * rG * qG / (2000 * Math.PI * centralFreq));
						inductance2 = (float) (1e6 * rG / (2000 * Math.PI * centralFreq * qG));
						capacitance1 = (float) (1e12 / (2000 * Math.PI * centralFreq * rG * qG));
						capacitance2 = (float) (1e12 * qG / (2000 * Math.PI * centralFreq * rG));
						capacitance3 = 0;
						trans1 = 1;
						trans2 = (float) Math.sqrt(outputResistance / rG);
						break;
					case PARALLEL_TYPE	:
						final float	rParallel = charResistance;
						final float	qParallel = centralFreq / (2 * passBarWidth);
						
						inductance1 = inductance2 = (float) (1e6 * rParallel / (2000 * Math.PI * centralFreq * qParallel));
						capacitance1 = capacitance2 = (float) (1e12 * qParallel / (2000 * Math.PI * centralFreq * rParallel));
						capacitance3 = capacitance2 / qParallel;
						trans1 = (float) Math.sqrt(inputResistance / rParallel);
						trans2 = (float) Math.sqrt(outputResistance / rParallel);
						break;
					case P_TYPE			:
						final float	rP = charResistance;
						final float	qP = centralFreq / (2 * passBarWidth);
						
						inductance1 = (float) (2e6 * rP * qP / (2000 * Math.PI * centralFreq));
						inductance2 = (float) (1e6 * rP / (2000 * Math.PI * centralFreq * qP));
						capacitance1 = (float) (1e12 / (4000 * Math.PI * centralFreq * rP * qP));
						capacitance2 = (float) (1e12 * qP / (2000 * Math.PI * centralFreq * rP));
						capacitance3 = 0;
						trans1 = (float) Math.sqrt(inputResistance / rP);
						trans2 = (float) Math.sqrt(outputResistance / rP);
						break;
					default :
						throw new UnsupportedOperationException("Bar filet type ["+type+"] is not supported yet");
				}
				return RefreshMode.RECORD_ONLY;
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
}