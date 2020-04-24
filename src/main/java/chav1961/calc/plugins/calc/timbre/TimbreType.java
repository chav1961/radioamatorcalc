package chav1961.calc.plugins.calc.timbre;

import chav1961.calc.interfaces.SVGIconKeeper;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.calc.timbre.TimbreType/chav1961/calculator/i18n/i18n.xml")
public enum TimbreType implements SVGIconKeeper {
	@LocaleResource(value="chav1961.calc.plugins.calc.timbre.timbreType.active",tooltip="chav1961.calc.plugins.calc.timbre.timbreType.active.tt")
	ACTIVE("schema.SVG"), 
	@LocaleResource(value="chav1961.calc.plugins.calc.timbre.timbreType.passive",tooltip="chav1961.calc.plugins.calc.timbre.timbreType.passive.tt")
	PASSIVE("schema1.SVG");

	private final String	icon;
	
	TimbreType(final String icon) {
		this.icon = icon;
	}
	
	@Override
	public String getSVGIcon() {
		return icon;
	}
}
