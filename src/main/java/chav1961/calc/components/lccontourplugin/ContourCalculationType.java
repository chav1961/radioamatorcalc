package chav1961.calc.components.lccontourplugin;

import chav1961.purelib.i18n.interfaces.LocaleResource;

public enum ContourCalculationType {
	@LocaleResource(value="frequencyType",tooltip="frequencyTypeTooltip")
	FREQ_BY_L_C, 
	@LocaleResource(value="inductanceType",tooltip="inductanceTypeTooltip")
	L_BY_FREQ_C, 
	@LocaleResource(value="capacityType",tooltip="capacityTypeTooltip")
	C_BY_FREQ_L
}
