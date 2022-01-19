package chav1961.calc.plugins.devices.stepupautogenerator;

import java.awt.Dimension;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.calc.interfaces.RingMyu;
import chav1961.calc.interfaces.RingType;
import chav1961.calc.plugins.math.MathUtils;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.basic.interfaces.ModuleAccessor;
import chav1961.purelib.enumerations.MarkupOutputFormat;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.streams.char2char.CreoleWriter;
import chav1961.purelib.streams.char2char.SubstitutableWriter;
import chav1961.purelib.ui.interfaces.Action;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;
import chav1961.purelib.ui.swing.useful.JMultiLineEditor;

/**
 * <p>Pulse stabilizer calculation plugin. Calculations are based on formulas in MC34063 datasheet.</p> 
 * @author Alexander Chernomyrdin aka chav1961
 * @since 0.0.1
 */

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.devices.stepupautogenerator.StepUpAutoGeneratorPlugin/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.devices.stepupautogenerator",tooltip="menu.devices.stepupautogenerator.tt",help="help.aboutApplication")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.calculate",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.calculate.tt"),actionString="calculate")
@Action(resource=@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.calculatedetailed",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.calculatedetailed.tt"),actionString="calculateDetailed")
@PluginProperties(width=900,height=610,leftWidth=600,svgURI="schema1.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
public class StepUpAutoGeneratorPlugin implements FormManager<Object,StepUpAutoGeneratorPlugin>, ModuleAccessor {
	private static final float	MYU_0 =  1.257e-3f;
	private static final float	ETHA = 0.9f; 
	private static final float	BETHA = 20; 

	private final LoggerFacade 	logger;
	
	@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.uIn",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.uIn.tt")
	@Format("9.2mpzs")
	public float 			uIn = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.uOut",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.uOut.tt")
	@Format("9.2mnpzs")
	public float 			uOut = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.iOut",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.iOut.tt")
	@Format("9.2mpzs")
	public float 			iOut = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.frequency",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.frequency.tt")
	@Format("9.2mpzs")
	public float 			freq = 50;
	@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.ringType",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.ringType.tt")
	@Format("40m")
	public RingType ringType = RingType.K20x12x6;
	@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.ringMyu",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.ringMyu.tt")
	@Format("40m")
	public RingMyu 	ringMyu = RingMyu.MUI_140;
	@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.uSat",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.uSat.tt")
	@Format("9.2mpzs")
	public float 			uSat = 0.8f;
	@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.uF",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.uF.tt")
	@Format("9.2mpzs")
	public float 			uF = 0.8f;
	@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.uRipple",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.uRipple.tt")
	@Format("9.2mpzs")
	public float 			uRipple = 0.1f;
	@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.uMax",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.uMax.tt")
	@Format("9.2npzs")
	public float 			uMax = 40;
	
	@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.iInMid",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.iInMid.tt")
	@Format("9.2ro")
	public float 			iInMid = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.iInPeak",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.iInPeak.tt")
	@Format("9.2ro")
	public float 			iInPeak = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.iDiodePeak",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.iDiodePeak.tt")
	@Format("9.2ro")
	public float 			iDiodePeak = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.capacitance",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.capacitance.tt")
	@Format("9.2ro")
	public float 			capacitance = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.fieldInduction",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.fieldInduction.tt")
	@Format("9.2ro")
	public float 			fieldInduction = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.coilsIn",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.coilsIn.tt")
	@Format("9ro")
	public int 				coilsIn = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.wireDiameterIn",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.wireDiameterIn.tt")
	@Format("9.2ro")
	public float 			wireDiameterIn = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.coilsOut",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.coilsOut.tt")
	@Format("9ro")
	public int 				coilsOut = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.wireDiameterOut",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.wireDiameterOut.tt")
	@Format("9.2ro")
	public float 			wireDiameterOut = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.coilsFeedback",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.coilsFeedback.tt")
	@Format("9ro")
	public int 				coilsFeedback = 0;
	@LocaleResource(value="chav1961.calc.plugins.devices.stepupautogenerator.wireDiameterFeedback",tooltip="chav1961.calc.plugins.devices.stepupautogenerator.wireDiameterFeedback.tt")
	@Format("9.2ro")
	public float 			wireDiameterFeedback = 0;
	
	public StepUpAutoGeneratorPlugin(final LoggerFacade logger) {
		if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.logger = logger;
		}
	}
	
	@Override
	public RefreshMode onField(final StepUpAutoGeneratorPlugin inst, final Object id, final String fieldName, final Object oldValue, final boolean beforeCommit) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onAction(final StepUpAutoGeneratorPlugin inst, final Object id, final String actionName, final Object... parameter) throws FlowException, LocalizationException {
		boolean	needDetailed = false;
		
		try(final Writer	wr = new StringWriter();
			final Writer	cwr = new CreoleWriter(wr, MarkupOutputFormat.XML2HTML);
			final Writer	swr = new SubstitutableWriter(cwr, this)) {
			
			switch (actionName) {
				case "app:action:/StepUpAutoGeneratorPlugin.calculateDetailed"	:
					needDetailed = true;
				case "app:action:/StepUpAutoGeneratorPlugin.calculate"	:
					if (uIn == 0 || uOut == 0 || iOut == 0 || uRipple == 0) {
						getLogger().message(Severity.warning,"Uin == 0 || Uout == 0 || Iout == 0 || Uripple == 0");
						return RefreshMode.DEFAULT;
					}
					else {
						final float	pTarget = uOut * iOut;
						final float	kTr = uMax < uOut ? uMax/uOut : 1;	
						final float	tOntOff = (uOut/kTr+uF-uIn)/(uIn-uSat);
						final float tTotal = 0.001f/freq;
						final float tOn = tTotal/(1+tOntOff);
						final float	wireN = 1;
	
						final float	_iInPeak = 2*iOut*kTr*(1+tOntOff);
						final float	_iDiodePeak = 2*iOut*(1+tOntOff);
						final float	_iInMid = uOut*iOut/(uIn*ETHA);
						final float	_capacitance = 1e6f * iOut*tOn/uRipple;
						
						final float ind = 1e6f * tOn*(uIn-uSat)/iInPeak, _coilsIn;
						
						if (ringType.getOuterDiameter()/ringType.getInnerDiameter() > 1.75f) {
							_coilsIn = (int) (100 * Math.sqrt(ind / (2 * ringMyu.getMyu() * ringType.getHeight() * Math.log(ringType.getOuterDiameter()/ringType.getInnerDiameter()))));
						}
						else {
							_coilsIn = (int) (100 * Math.sqrt(ind * (ringType.getOuterDiameter() + ringType.getInnerDiameter())/ (4 * ringMyu.getMyu() * ringType.getHeight() * (ringType.getOuterDiameter() - ringType.getInnerDiameter()))));
						}
						
						final float		_fieldInduction = (float) (MYU_0*ringMyu.getMyu()*_iInPeak*_coilsIn/ringType.getMiddleLen());
						final float		overallP = (float) MathUtils.calculateOverallPower(ringType, ringMyu, _fieldInduction, freq, pTarget);
						
						if (overallP > 1.2 * pTarget) {
							final float	coilsPerV = _coilsIn / uIn, j = 1.87f;
							
							iInPeak = _iInPeak;
							iDiodePeak = _iDiodePeak;
							iInMid = _iInMid;
							capacitance = _capacitance;
							fieldInduction = _fieldInduction;
							
							coilsIn = (int) _coilsIn;
							wireDiameterIn = (float) (1.13 * Math.sqrt(iInPeak / (j * wireN)));
							
							coilsFeedback = (int) (2.5f * coilsPerV);
							wireDiameterFeedback = (float) (1.13 * Math.sqrt(iInPeak / (BETHA * j * wireN)));
	
							if (kTr == 1) {
								coilsOut = 0;
								wireDiameterOut = 0;
							}
							else {
								coilsOut = (int) (_coilsIn * kTr + coilsPerV * uF);
								wireDiameterOut = (float) (1.13 * Math.sqrt(iDiodePeak / (j * wireN)));
							}
							if (needDetailed) {
								swr.write(Utils.fromResource(this.getClass().getResource("protocol.cre"), PureLibSettings.DEFAULT_CONTENT_ENCODING));
								swr.flush();
								final JMultiLineEditor	ed = new JMultiLineEditor(PureLibSettings.PURELIB_LOCALIZER, PureLibSettings.MIME_HTML_TEXT, wr.toString());
								
								ed.show(null);
							}
							return RefreshMode.RECORD_ONLY;
						}
						else {
							getLogger().message(Severity.warning,"pGab < 1.2 pTarget");
							return RefreshMode.DEFAULT;
						}
					}
				default :
					throw new UnsupportedOperationException("Unknown action string ["+actionName+"]");
			}
		} catch (IOException e) {
			throw new FlowException(e);
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