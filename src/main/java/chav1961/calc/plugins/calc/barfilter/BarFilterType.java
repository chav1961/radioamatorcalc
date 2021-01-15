package chav1961.calc.plugins.calc.barfilter;

import chav1961.calc.interfaces.SVGIconKeeper;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.calc.barfilter.BarFilterType/chav1961/calculator/i18n/i18n.xml")
public enum BarFilterType implements SVGIconKeeper {
	@LocaleResource(value="chav1961.calc.plugins.calc.barfilter.barFilterType.parallel",tooltip="chav1961.calc.plugins.calc.barfilter.barFilterType.parallel.tt")
	PARALLEL_TYPE("schema1.SVG"),
	@LocaleResource(value="chav1961.calc.plugins.calc.barfilter.barFilterType.gtype",tooltip="chav1961.calc.plugins.calc.barfilter.barFilterType.gtype.tt")
	G_TYPE("schema2.SVG"),
	@LocaleResource(value="chav1961.calc.plugins.calc.barfilter.barFilterType.ptype",tooltip="chav1961.calc.plugins.calc.barfilter.barFilterType.ptype.tt")
	P_TYPE("schema3.SVG");

	private final String	icon;
	
	BarFilterType(final String icon) {
		this.icon = icon;
	}
	
	@Override
	public String getSVGIcon() {
		return icon;
	}
}
