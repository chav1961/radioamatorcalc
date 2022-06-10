package chav1961.calc.plugins.calc.resonant.tube;

import chav1961.calc.interfaces.PluginProperties;


import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.basic.interfaces.ModuleAccessor;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;
import chav1961.purelib.ui.interfaces.Action;

// Ri - внутреннее сопротивление лампы
// Rc - сопротивление сетки лампы следующего каскада
// Rвх - входное сопротивление лампы следующего каскада
// R - сопротивление потерь анодного контура
// L - индуктивность анодного контура
// C - емкость анодного контура
// S - крутизна характеристики лампы
// Cас - емкость анод-сетка (проходная)
// w - круговая частота контура
//
// // -----
// w = 1 / 2 * pi * sqrt(L C)
// X = sqrt(L / C)
// dRc = X^2 / Rc
// dRвх = X^2 / Rвх
// dRi = X^2 / Ri
// Rэкв = L / C(R+dRc+dRвх+dRi)
// Qэкв = X / (R+dRc+dRвх+dRi)
// K  = S * Rэкв
// Куст = (0.4-0.6)*sqrt(S/wCас) - макс. коэффициент устойчивого усиления

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.calc.resonant.tube.ResonantTubePlugin/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.curcuits.resonant.tube",tooltip="menu.curcuits.resonant.tube.tt",help="help.aboutApplication")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.calc.resonant.tube.button.calc",tooltip="chav1961.calc.plugins.calc.resonant.tube.button.calc.tt"),actionString="calculate")
@PluginProperties(width=500,height=400,leftWidth=250,svgURI="schema1.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
public class ResonantTubePlugin implements FormManager<Object,ResonantTubePlugin>, ModuleAccessor {
	private final LoggerFacade 	logger;

	@LocaleResource(value="chav1961.calc.plugins.calc.resonant.tube.resinternal",tooltip="chav1961.calc.plugins.calc.resonant.tube.resinternal.tt")
	@Format("9.2mpzs")
	public float 		resInternal = 0;

	@LocaleResource(value="chav1961.calc.plugins.calc.resonant.tube.resgrid",tooltip="chav1961.calc.plugins.calc.resonant.tube.resgrid.tt")
	@Format("9.2mpzs")
	public float 		resGrid = 0;

	@LocaleResource(value="chav1961.calc.plugins.calc.resonant.tube.resinput",tooltip="chav1961.calc.plugins.calc.resonant.tube.resinput.tt")
	@Format("9.2mpzs")
	public float 		resInput = 0;

	@LocaleResource(value="chav1961.calc.plugins.calc.resonant.tube.rescontour",tooltip="chav1961.calc.plugins.calc.resonant.tube.rescontour.tt")
	@Format("9.2mpzs")
	public float 		resContour = 0;

	@LocaleResource(value="chav1961.calc.plugins.calc.resonant.tube.inductance",tooltip="chav1961.calc.plugins.calc.resonant.tube.inductance.tt")
	@Format("9.2mpzs")
	public float 		inductance = 0;

	@LocaleResource(value="chav1961.calc.plugins.calc.resonant.tube.capacitance",tooltip="chav1961.calc.plugins.calc.resonant.tube.capacitance.tt")
	@Format("9.2mpzs")
	public float 		capacitance = 0;

	@LocaleResource(value="chav1961.calc.plugins.calc.resonant.tube.conductance",tooltip="chav1961.calc.plugins.calc.resonant.tube.conductance.tt")
	@Format("9.2mpzs")
	public float 		conductance = 0;

	@LocaleResource(value="chav1961.calc.plugins.calc.resonant.tube.captransfer",tooltip="chav1961.calc.plugins.calc.resonant.tube.captransfer.tt")
	@Format("9.4mpzs")
	public float 		capTransfer = 0;
	
	@LocaleResource(value="chav1961.calc.plugins.calc.resonant.tube.k",tooltip="chav1961.calc.plugins.calc.resonant.tube.k.tt")
	@Format("9.2or")
	public float 		k = 0;

	@LocaleResource(value="chav1961.calc.plugins.calc.resonant.tube.kmax",tooltip="chav1961.calc.plugins.calc.resonant.tube.kmax.tt")
	@Format("9.2or")
	public float 		kMax = 0;

	public ResonantTubePlugin(final LoggerFacade logger) {
		if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.logger = logger;
		}
	}
	
	@Override
	public RefreshMode onField(final ResonantTubePlugin inst, final Object id, final String fieldName, final Object oldValue, final boolean beforeCommit) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onAction(final ResonantTubePlugin inst, final Object id, final String actionName, final Object... parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "app:action:/ResonantTubePlugin.calculate"	:
				if (resInternal == 0 || resGrid == 0 || inductance  == 0 || capacitance == 0 || conductance == 0 || capTransfer == 0) {
					getLogger().message(Severity.warning,"resInternal == 0 || resGrid == 0 || inductance == 0 || capacitance == 0 || chars == 0 || capTransfer == 0");
					return RefreshMode.NONE;
				}
				else {
					final float	omega = (float) (1 / (2 * Math.PI * Math.sqrt(inductance * capacitance)));
					final float	x = (float) 1e3 * inductance / (capacitance * resContour);
					final float	x2 = (float) (1e3 * Math.sqrt(inductance / capacitance));
					final float dRc = x2 * x2 / resGrid;  
					final float dRi = x2 * x2 / resInternal;  
					final float dRinput = resInput == 0 ? 0 : x * x / resInput;  
					final float	rEq = (float) (1e3 * inductance / (capacitance * (resContour + dRc + dRi + dRinput)));
					final float	qEq = x2 / (resContour + dRc + dRi + dRinput);
					
					k = conductance * rEq;
					kMax = (float) (0.4 * Math.sqrt(conductance  / (omega * capTransfer)));
					
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