package chav1961.calc.plugins.calc.contour;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.purelib.basic.CSSUtils.Frequency;
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

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.calc.contour.ContourPlugin/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.curcuits.contour",tooltip="menu.curcuits.contour.tt",help="help.aboutApplication")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.calc.contour.button.freq",tooltip="chav1961.calc.plugins.calc.contour.button.freq.tt"),actionString="calcFreq")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.calc.contour.button.ind",tooltip="chav1961.calc.plugins.calc.contour.button.ind.tt"),actionString="calcInd")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.calc.contour.button.cap",tooltip="chav1961.calc.plugins.calc.contour.button.cap.tt"),actionString="calcCap")
@PluginProperties(width=500,height=150,leftWidth=250,svgURI="schema.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
public class ContourPlugin implements FormManager<Object,ContourPlugin> {
	private final LoggerFacade 	logger;
	
	@LocaleResource(value="chav1961.calc.plugins.calc.contour.inductanñe",tooltip="chav1961.calc.plugins.calc.contour.inductanñe.tt")
	@Format("9.2pzs")
	public float inductance = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.contour.capacity",tooltip="chav1961.calc.plugins.calc.contour.capacity.tt")
	@Format("9.2pzs")
	public float capacity = 0;
	@LocaleResource(value="chav1961.calc.plugins.calc.contour.frequency",tooltip="chav1961.calc.plugins.calc.contour.frequency.tt")
	@Format("9.2pzs")
	public float frequency = 0;

	public ContourPlugin(final LoggerFacade logger) {
		if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.logger = logger;
		}
	}
	
	@Override
	public RefreshMode onField(final ContourPlugin inst, final Object id, final String fieldName, final Object oldValue) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onAction(final ContourPlugin inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
		switch (actionName) {
			case "app:action:/ContourPlugin.calcFreq"	:
				if (inductance == 0 || capacity == 0) {
					getLogger().message(Severity.warning,"L == 0 || C == 0");
					return RefreshMode.NONE;
				}
				else {
					frequency = (float) frequencyByInductanceAndCapacity(inductance,capacity);
					return RefreshMode.RECORD_ONLY;
				}
			case "app:action:/ContourPlugin.calcInd"	:
				if (frequency == 0 || capacity == 0) {
					getLogger().message(Severity.warning,"F == 0 || C == 0");
					return RefreshMode.NONE;
				}
				else {
					inductance = (float) inductanceByFrequencyAndCapacity(frequency,capacity);
					return RefreshMode.RECORD_ONLY;
				}
			case "app:action:/ContourPlugin.calcCap"	:
				if (frequency == 0 || inductance == 0) {
					getLogger().message(Severity.warning,"F == 0 || L == 0");
					return RefreshMode.NONE;
				}
				else {
					capacity = (float) capacityByFrequencyAndInductance(frequency,inductance);
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
	
	/**
	 * <p>Calculate capacity by frequency and inductance</p>
	 * @param frequency frequency to calculate (kHz)
	 * @param inductance inductance to calculate (uH)
	 * @return capacity calculated (pF)
	 */
	public static double capacityByFrequencyAndInductance(final double frequency, final double inductance) {
		return 2533.029e7 / (frequency * frequency * inductance);
	}

	/**
	 * <p>Calculate inductance by frequency and capacity</p>
	 * @param frequency frequency to calculate (kHz)
	 * @param capacity capacity to calculate (pF)
	 * @return inductance calculated (uH)
	 */
	public static double inductanceByFrequencyAndCapacity(final double frequency, final double capacity) {
		return 2533.029e7 / (frequency * frequency * capacity);
	}

	/**
	 * <p>Calculate {@link Frequency} by inductance and capacity</p>
	 * @param inductance inductance to calculate (uH)
	 * @param capacity capacity to calculate (pF)
	 * @return frequency calculated (kHz)
	 */
	public static double frequencyByInductanceAndCapacity(final double inductance, final double capacity) {
		return 159154.943 / Math.sqrt(inductance*capacity);
	}
}