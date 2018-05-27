package chav1961.calc.elements.coils;

import chav1961.purelib.basic.exceptions.FlowException;

import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.interfacers.FormManager;
import chav1961.purelib.ui.interfacers.Format;
import chav1961.purelib.ui.interfacers.Action;

@LocaleResourceLocation(Localizer.LOCALIZER_SCHEME+":prop:chav1961/calc/elements/coils/singlecoils")
@Action(resource=@LocaleResource(value="calculateInductance",tooltip="calculateInductanceTooltip"),actionString="calculateInductance")
@Action(resource=@LocaleResource(value="calculateCoils",tooltip="calculateCoilsTooltip"),actionString="calculateCoils")
class SingleCoilsCalculator implements FormManager<Object,SingleCoilsCalculator> {
	private final LoggerFacade	logger;
	
@LocaleResource(value="length",tooltip="lengthTooltip")
@Format("m")
	private float 				length = 0.0f;
@LocaleResource(value="diameter",tooltip="diameterTooltip")	
@Format("m")
	private float				diameter = 0.0f;
@LocaleResource(value="wireDiameter",tooltip="wireDiameterTooltip")	
@Format("m")
	private float				wireDiameter = 0.0f;
@LocaleResource(value="inductance",tooltip="inductanceTooltip")	
	private float				inductance = 0.0f;
@LocaleResource(value="coils",tooltip="coilsTooltip")	
	private float				coils = 0.0f;

	SingleCoilsCalculator(final LoggerFacade logger) {
		this.logger = logger;
	}
	
	@Override
	public RefreshMode onRecord(final Action action, final SingleCoilsCalculator oldRecord, final Object oldId, final SingleCoilsCalculator newRecord, final Object newId) throws FlowException {
		return RefreshMode.NONE;
	}

	@Override
	public RefreshMode onField(final SingleCoilsCalculator inst, final Object id, final String fieldName, final Object oldValue) throws FlowException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RefreshMode onAction(final SingleCoilsCalculator inst, final Object id, final String actionName, final Object parameter) throws FlowException {
		// TODO Auto-generated method stub
		switch (actionName) {
			case "calculateInductance"	:
				break;
			case "calculateCoils"		:
				break;
			default :
		}
		return RefreshMode.RECORD_ONLY;
	}

	@Override
	public LoggerFacade getLogger() {
		return logger;
	}
}