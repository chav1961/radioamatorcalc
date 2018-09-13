package chav1961.calc.components.rcgeneratorplugin;

import chav1961.purelib.i18n.interfaces.LocaleResource;

public enum GeneratorType {
	@LocaleResource(value="winRobinson",tooltip="winRobinsonTooltip")
	WIN_ROBINSON,
	@LocaleResource(value="phaseShifter",tooltip="phaseShifterTooltip")
	PHASE_SHIFTER,
	@LocaleResource(value="multivibrator",tooltip="multivibratorTooltip")
	MULTIVIBRATOR
}
