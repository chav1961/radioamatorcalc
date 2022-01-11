package chav1961.calc.plugins.calc.pcontour;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.purelib.basic.CSSUtils.Frequency;
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

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.calc.contour.ContourPlugin/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.curcuits.pcontour",tooltip="menu.curcuits.pcontour.tt",help="help.curcuits.pcontour.help")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.calc.pcontour.button.calc",tooltip="chav1961.calc.plugins.calc.pcontour.button.calc.tt"),actionString="calculate")
@PluginProperties(width=500,height=175,leftWidth=250,svgURI="schema1.SVG,schema2.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
public class PContourPlugin implements FormManager<Object,PContourPlugin>, ModuleAccessor {
	private final LoggerFacade 	logger;
	
	@LocaleResource(value="chav1961.calc.plugins.calc.pcontour.type",tooltip="chav1961.calc.plugins.calc.pcontour.type.tt")
	@Format("9.2pzs")
	public PContourType	type = PContourType.SINGLE;
	@LocaleResource(value="chav1961.calc.plugins.calc.pcontour.inputresistance",tooltip="chav1961.calc.plugins.calc.pcontour.inputresistance.tt")
	@Format("9pzs")
	public float	inputResistance = 1000;
	@LocaleResource(value="chav1961.calc.plugins.calc.pcontour.outputresistance",tooltip="chav1961.calc.plugins.calc.pcontour.outputresistance.tt")
	@Format("9pzs")
	public float	outputResistance = 50;
	@LocaleResource(value="chav1961.calc.plugins.calc.pcontour.lowfrequency",tooltip="chav1961.calc.plugins.calc.pcontour.lowfrequency.tt")
	@Format("9.3pzs")
	public float	lowFrequency = 27;
	@LocaleResource(value="chav1961.calc.plugins.calc.pcontour.highfrequency",tooltip="chav1961.calc.plugins.calc.pcontour.highfrequency.tt")
	@Format("9.3pzs")
	public float	highFrequency = 27;
	@LocaleResource(value="chav1961.calc.plugins.calc.pcontour.freequality",tooltip="chav1961.calc.plugins.calc.pcontour.freequality.tt")
	@Format("9pzs")
	public float	freeQuality = 100;
	@LocaleResource(value="chav1961.calc.plugins.calc.pcontour.loadquality",tooltip="chav1961.calc.plugins.calc.pcontour.loadquality.tt")
	@Format("9pzs")
	public float	loadQuality = 15;
	@LocaleResource(value="chav1961.calc.plugins.calc.pcontour.indictance1",tooltip="chav1961.calc.plugins.calc.pcontour.indictance1.tt")
	@Format("9ro")
	public float	l1 = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.pcontour.indictance2",tooltip="chav1961.calc.plugins.calc.pcontour.indictance2.tt")
	@Format("9ro")
	public float	l2 = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.pcontour.capacitance1",tooltip="chav1961.calc.plugins.calc.pcontour.capacitance1.tt")
	@Format("9ro")
	public float	c1 = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.pcontour.capacitance2",tooltip="chav1961.calc.plugins.calc.pcontour.capacitance2.tt")
	@Format("9ro")
	public float	c2 = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.pcontour.capacitance3",tooltip="chav1961.calc.plugins.calc.pcontour.capacitance3.tt")
	@Format("9ro")
	public float	c3 = 0;

	
	public PContourPlugin(final LoggerFacade logger) {
		if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.logger = logger;
		}
	}
	
	@Override
	public RefreshMode onField(final PContourPlugin inst, final Object id, final String fieldName, final Object oldValue, final boolean beforeCommit) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onAction(final PContourPlugin inst, final Object id, final String actionName, final Object... parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "app:action:/PContourPlugin.calculate"	:
				final float	geomFrequency = (float) (2 * Math.PI * Math.sqrt(lowFrequency * highFrequency));
				final float	k = inputResistance / outputResistance;
				final float	etha = 1 - loadQuality / freeQuality;
				
				switch (type) {
					case SINGLE	:
						final float	xC2 = (float) ((inputResistance - etha * outputResistance) / (Math.sqrt(k * (loadQuality * loadQuality + 1 + etha * etha) / etha - k*k - 1) - loadQuality));
						final float	xC1 = (float) (inputResistance * xC2 / (loadQuality * xC2 - etha * outputResistance));
						final float xL = (float) (loadQuality * outputResistance * xC2 * xC2 / (etha * (outputResistance * outputResistance + xC2 * xC2)));
						
						c1 = 1 / (xC1 * geomFrequency);
						c2 = 1 / (xC2 * geomFrequency);
						l1 = xL / geomFrequency;
						l2 = 0;
						c3 = 0;
						break;
					case DOUBLE	:
						final float	rGeom = (float) Math.sqrt(inputResistance * outputResistance);
						final float	xxC1 = (loadQuality * inputResistance + rGeom) / (loadQuality * loadQuality - 1);
						final float	xxC3 = (loadQuality * outputResistance + rGeom) / (loadQuality * loadQuality - 1);
						final float	xxC2 = xxC1 * xxC3 / rGeom;
						final float xxL1 = xxC1 + xxC2;
						final float xxL2 = xxC2 + xxC3;
						
						c1 = 1 / (xxC1 * geomFrequency);
						c2 = 1 / (xxC2 * geomFrequency);
						c3 = 1 / (xxC3 * geomFrequency);
						l1 = xxL1 / geomFrequency;
						l2 = xxL2 / geomFrequency;
						break;
					default:
						throw new UnsupportedOperationException("Contour type ["+type+"] is not supported yet");
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