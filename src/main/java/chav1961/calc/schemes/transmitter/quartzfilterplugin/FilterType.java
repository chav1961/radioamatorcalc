package chav1961.calc.schemes.transmitter.quartzfilterplugin;

import chav1961.purelib.i18n.interfaces.LocaleResource;

public enum FilterType {
	@LocaleResource(value="chebysheffType",tooltip="chebysheffTypeTooltip")
	CHEB,
	@LocaleResource(value="butterworthType",tooltip="butterworthTypeTooltip")
	BATT
}
