package chav1961.calc.plugins.calc.activefilter;

import chav1961.calc.interfaces.SVGIconKeeper;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.calc.activefilter.ActiveFilterType/chav1961/calculator/i18n/i18n.xml")
public enum ActiveFilterType implements SVGIconKeeper {
	@LocaleResource(value="chav1961.calc.plugins.calc.activefilter.activeFilterType.bar",tooltip="chav1961.calc.plugins.calc.activefilter.activeFilterType.bar.tt")
	BAR_TYPE("schema1.SVG");

	private final String	icon;
	
	ActiveFilterType(final String icon) {
		this.icon = icon;
	}
	
	@Override
	public String getSVGIcon() {
		return icon;
	}
}
