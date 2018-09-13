package chav1961.calc.components.rcgeneratorplugin;

import chav1961.purelib.i18n.interfaces.LocaleResource;

public enum GeneratorCalculationType {
	@LocaleResource(value="frequencyType",tooltip="frequencyTypeTooltip")
	FREQ_BY_R_C, 
	@LocaleResource(value="resistanceType",tooltip="resistanceTypeTooltip")
	R_BY_FREQ_C, 
	@LocaleResource(value="capacityType",tooltip="capacityTypeTooltip")
	C_BY_FREQ_R
}
