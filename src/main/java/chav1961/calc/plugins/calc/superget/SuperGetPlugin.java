package chav1961.calc.plugins.calc.superget;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.calc.plugins.calc.contour.ContourPlugin;
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

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.calc.superget.SuperGetPlugin/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.curcuits.superget",tooltip="menu.curcuits.superget.tt",help="help.curcuits.superget.help")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.calc.superget.button.calc",tooltip="chav1961.calc.plugins.calc.superget.button.calc.tt"),actionString="calc")
@PluginProperties(width=550,height=400,leftWidth=300,svgURI="schema.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
public class SuperGetPlugin implements FormManager<Object,SuperGetPlugin>, ModuleAccessor {
	private final LoggerFacade 	logger;

	@LocaleResource(value="chav1961.calc.plugins.calc.superget.freqmin",tooltip="chav1961.calc.plugins.calc.superget.freqmin.tt")
	@Format("9.2mps")
	public float freqMin = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.superget.freqmax",tooltip="chav1961.calc.plugins.calc.superget.freqmax.tt")
	@Format("9.2mps")
	public float freqMax = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.superget.freqout",tooltip="chav1961.calc.plugins.calc.superget.freqout.tt")
	@Format("9.2mps")
	public float freqOut = 455;
	@LocaleResource(value="chav1961.calc.plugins.calc.superget.upperfreq",tooltip="chav1961.calc.plugins.calc.superget.upperfreq.tt")
	@Format("1m")
	public boolean upperFreq = true;
	@LocaleResource(value="chav1961.calc.plugins.calc.superget.halffreq",tooltip="chav1961.calc.plugins.calc.superget.halffreq.tt")
	@Format("1m")
	public boolean halfFreq = false;
	@LocaleResource(value="chav1961.calc.plugins.calc.superget.capmin",tooltip="chav1961.calc.plugins.calc.superget.capmin.tt")
	@Format("9.2mps")
	public float capMin = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.superget.capmax",tooltip="chav1961.calc.plugins.calc.superget.capmax.tt")
	@Format("9.2mps")
	public float capMax = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.superget.ind1",tooltip="chav1961.calc.plugins.calc.superget.ind1.tt")
	@Format("9.2mps")
	public float ind1 = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.superget.ind2",tooltip="chav1961.calc.plugins.calc.superget.ind2.tt")
	@Format("9.2mps")
	public float ind2 = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.superget.cap11",tooltip="chav1961.calc.plugins.calc.superget.cap11.tt")
	@Format("9.2ro")
	public float cap11 = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.superget.cap12",tooltip="chav1961.calc.plugins.calc.superget.cap12.tt")
	@Format("9.2ro")
	public float cap12 = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.superget.cap21",tooltip="chav1961.calc.plugins.calc.superget.cap21.tt")
	@Format("9.2ro")
	public float cap21 = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.superget.cap22",tooltip="chav1961.calc.plugins.calc.superget.cap22.tt")
	@Format("9.2ro")
	public float cap22 = 0;
	
	public SuperGetPlugin(final LoggerFacade logger) {
		if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.logger = logger;
		}
	}
	
	@Override
	public RefreshMode onField(final SuperGetPlugin inst, final Object id, final String fieldName, final Object oldValue, final boolean beforeCommit) throws FlowException, LocalizationException {
		switch (fieldName) {
			case "freqMin" :
				if (freqMax == 0) {
					freqMax = freqMin;
					return RefreshMode.RECORD_ONLY;
				}
				else {
					return RefreshMode.DEFAULT;
				}
			case "capMin" :
				if (capMax == 0) {
					capMax = capMin;
					return RefreshMode.RECORD_ONLY;
				}
				else {
					return RefreshMode.DEFAULT;
				}
			case "ind1" :
				if (ind2 == 0) {
					ind2 = ind1;
					return RefreshMode.RECORD_ONLY;
				}
				else {
					return RefreshMode.DEFAULT;
				}
			default :
				return RefreshMode.DEFAULT;
		}
	}

	@Override
	public RefreshMode onAction(final SuperGetPlugin inst, final Object id, final String actionName, final Object... parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "app:action:/SuperGetPlugin.calc"	:
				if (freqMin == 0 || freqMax == 0 || freqOut == 0 || capMin == 0 || capMax == 0 || ind1 == 0 || ind2 == 0) {
					getLogger().message(Severity.warning, "freqMin == 0 || freqMax == 0 || freqOut == 0 || capMin == 0 || capMax == 0 || ind1 == 0 || ind2 == 0");
					return RefreshMode.REJECT;
				}
				else if (freqMin >= freqMax) {
					getLogger().message(Severity.warning, "freqMin >= freqMax");
					return RefreshMode.REJECT;
				}
				else if (capMin >= capMax) {
					getLogger().message(Severity.warning, "capMin >= capMax");
					return RefreshMode.REJECT;
				}
				else {
					final double	deltaFreq = upperFreq ? freqOut : -freqOut; 
					final double	ck1Min = ContourPlugin.capacityByFrequencyAndInductance(freqMax, ind1);	
					final double	ck1Max = ContourPlugin.capacityByFrequencyAndInductance(freqMin, ind1);	
					final double	ck1Delta = ck1Max - ck1Min;	
					final double	ck2Min = ContourPlugin.capacityByFrequencyAndInductance((freqMax + deltaFreq) / (halfFreq ? 2 : 1), ind2);	
					final double	ck2Max = ContourPlugin.capacityByFrequencyAndInductance((freqMin + deltaFreq) / (halfFreq ? 2 : 1), ind2);
					final double	ck2Delta = ck2Max - ck2Min;	
					final double	capMid = (capMin + capMax) / 2, deltaCap = capMax - capMin;
					
					cap12 = (float) (Math.sqrt(capMid*capMid + ck1Min*ck1Max*deltaCap/ck1Delta - capMin*capMax) - capMid);
					cap11 = (float) (ck1Min*(cap12 + capMin)/(cap12 + (capMin - ck1Min)));
					cap22 = (float) (Math.sqrt(capMid*capMid + ck2Min*ck2Max*deltaCap/ck2Delta - capMin*capMax) - capMid);
					cap21 = (float) (ck2Min*(cap22 + capMin)/(cap22 + (capMin - ck2Min)));
					
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

	@Override
	public void allowUnnamedModuleAccess(final Module... unnamedModules) {
		for (Module item : unnamedModules) {
			this.getClass().getModule().addExports(this.getClass().getPackageName(),item);
		}
	}
}