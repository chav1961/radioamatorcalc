package chav1961.calc.plugins.details.quartz;

import chav1961.calc.interfaces.PluginProperties;
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

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.details.quartz.QuartzParametersPlugin/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.details.quartzparameters",tooltip="menu.details.quartzparameters.tt",help="help.aboutApplication")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.details.quartz.button.calc",tooltip="chav1961.calc.plugins.details.quartz.button.calc.tt"),actionString="calcParameters")
@PluginProperties(width=600,height=300,leftWidth=300,svgURI="schema.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
public class QuartzParametersPlugin implements FormManager<Object,QuartzParametersPlugin>, ModuleAccessor {
	private final LoggerFacade 	logger;
	
	@LocaleResource(value="chav1961.calc.plugins.details.quartz.capC1",tooltip="chav1961.calc.plugins.details.quartz.capC1.tt")
	@Format("9.2mpzs")
	public float capC1 = 22;
	@LocaleResource(value="chav1961.calc.plugins.details.quartz.capCs",tooltip="chav1961.calc.plugins.details.quartz.capCs.tt")
	@Format("9.2mpzs")
	public float capCs = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.quartz.capC2",tooltip="chav1961.calc.plugins.details.quartz.capC2.tt")
	@Format("9.2mpzs")
	public float capC2 = 820;
	@LocaleResource(value="chav1961.calc.plugins.details.quartz.capC3",tooltip="chav1961.calc.plugins.details.quartz.capC3.tt")
	@Format("9.2mpzs")
	public float capC3 = 330;
	@LocaleResource(value="chav1961.calc.plugins.details.quartz.freq0",tooltip="chav1961.calc.plugins.details.quartz.freq0.tt")
	@Format("9.3mpzs")
	public float freq0 = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.quartz.freq1",tooltip="chav1961.calc.plugins.details.quartz.freq1.tt")
	@Format("9.3mpzs")
	public float freq1 = 0;

	@LocaleResource(value="chav1961.calc.plugins.details.quartz.Ld",tooltip="chav1961.calc.plugins.details.quartz.Ld.tt")
	@Format("9.4ro")
	public float Ld = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.quartz.Cd",tooltip="chav1961.calc.plugins.details.quartz.Cd.tt")
	@Format("9.4ro")
	public float Cd = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.quartz.Rd",tooltip="chav1961.calc.plugins.details.quartz.Rd.tt")
	@Format("9.4ro")
	public float Rd = 0;
	
	public QuartzParametersPlugin(final LoggerFacade logger) {
		if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.logger = logger;
		}
	}
	
	@Override
	public RefreshMode onField(final QuartzParametersPlugin inst, final Object id, final String fieldName, final Object oldValue, final boolean beforeCommit) throws FlowException, LocalizationException {
		if (freq0 <= freq1) {
			getLogger().message(Severity.warning, "freq0 < freq1");
		}
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onAction(final QuartzParametersPlugin inst, final Object id, final String actionName, final Object... parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "app:action:/QuartzParametersPlugin.calcParameters"	:
				final float	cA = 1 / (1/capC2 + 1/capC3) + capCs;
				final float	cB = 1 / (1/capC1 + 1/capC2 + 1/capC3) + capCs;
				
				Cd = cA * cB * (freq0 * freq0 - freq1 * freq1) / (cA * freq0 * freq0 - cB * freq1 * freq1);
				final float	cK1 = cA * Cd / (cA + Cd);
				
				Ld = (float) (1e3 / (4 * Math.PI * Math.PI * freq0 * freq0 * cK1));
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
