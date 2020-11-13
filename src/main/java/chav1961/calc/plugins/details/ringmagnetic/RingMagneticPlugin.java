package chav1961.calc.plugins.details.ringmagnetic;

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
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;
import chav1961.purelib.ui.interfaces.Action;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.details.ringmagnetic.RingMagneticPlugin/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.details.ringmagnetic",tooltip="menu.details.ringmagnetic.tt",help="help.aboutApplication")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.details.ringmagnetic.button.inductance",tooltip="chav1961.calc.plugins.details.ringmagnetic.button.inductance.tt"),actionString="calcInductance")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.details.ringmagnetic.button.current",tooltip="chav1961.calc.plugins.details.ringmagnetic.button.current.tt"),actionString="calcCurrent")
@PluginProperties(width=600,height=220,leftWidth=300,svgURI="schema.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
public class RingMagneticPlugin implements FormManager<Object,RingMagneticPlugin>, ModuleAccessor {
	private static final double		MYU_0 =  1.257e-3;
	private final LoggerFacade 		logger;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringmagnetic.numberOfCoils",tooltip="chav1961.calc.plugins.details.ringmagnetic.numberOfCoils.tt")
	@Format("9.2mpzs")
	public float numberOfCoils = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.ringmagnetic.current",tooltip="chav1961.calc.plugins.details.ringmagnetic.current.tt")
	@Format("9.2pzs")
	public float current = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.ringmagnetic.fieldInduction",tooltip="chav1961.calc.plugins.details.ringmagnetic.fieldInduction.tt")
	@Format("9.2pzs")
	public float fieldInduction = 0;
	@LocaleResource(value="chav1961.calc.plugins.details.ringmagnetic.ringType",tooltip="chav1961.calc.plugins.details.ringmagnetic.ringType.tt")
	@Format("40m")
	public RingType ringType = RingType.K20x12x6;
	@LocaleResource(value="chav1961.calc.plugins.details.ringmagnetic.ringMyu",tooltip="chav1961.calc.plugins.details.ringmagnetic.ringMyu.tt")
	@Format("40m")
	public RingMyu 	ringMyu = RingMyu.MUI_140;

	
	public RingMagneticPlugin(final LoggerFacade logger) {
		if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.logger = logger;
		}
	}
	
	@Override
	public RefreshMode onField(final RingMagneticPlugin inst, final Object id, final String fieldName, final Object oldValue, final boolean beforeCommit) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onAction(final RingMagneticPlugin inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "app:action:/RingMagneticPlugin.calcInductance"	:
				if (numberOfCoils == 0 || current == 0) {
					getLogger().message(Severity.warning,"w == 0 || I == 0");
					return RefreshMode.NONE;
				}
				else {
					fieldInduction = (float) (MYU_0*ringMyu.getMyu()*current*numberOfCoils/ringType.getMiddleLen());
					return RefreshMode.RECORD_ONLY;
				}
			case "app:action:/RingMagneticPlugin.calcCurrent"	:
				if (numberOfCoils == 0 || fieldInduction == 0) {
					getLogger().message(Severity.warning,"w == 0 || B == 0");
					return RefreshMode.NONE;
				}
				else {
					current = (float) (fieldInduction*ringType.getMiddleLen()/(MYU_0*ringMyu.getMyu()*numberOfCoils));
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