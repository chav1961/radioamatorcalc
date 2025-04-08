package chav1961.calc.plugins.calc.pcontour;

import chav1961.calc.interfaces.SVGIconKeeper;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.calc.pcontour.PContourType/chav1961/calculator/i18n/i18n.xml")
public enum PContourType implements SVGIconKeeper {
	@LocaleResource(value="chav1961.calc.plugins.calc.pcontour.pContourType.single",tooltip="chav1961.calc.plugins.calc.pcontour.pContourType.single.tt")
	SINGLE("schema1.svg"),
	@LocaleResource(value="chav1961.calc.plugins.calc.pcontour.pContourType.double",tooltip="chav1961.calc.plugins.calc.pcontour.pContourType.double.tt")
	DOUBLE("schema2.svg");

	private final String	icon;
	
	PContourType(final String icon) {
		this.icon = icon;
	}
	
	@Override
	public String getSVGIcon() {
		return icon;
	}
}
