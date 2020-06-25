package chav1961.calc.plugins.details.ringcurrenttrans;

import chav1961.calc.interfaces.SVGIconKeeper;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.details.ringmagnetic.CurrentTransType/chav1961/calculator/i18n/i18n.xml")
public enum CurrentTransType implements SVGIconKeeper {
	@LocaleResource(value="chav1961.calc.plugins.detalis.ringcurrenttrans.currenttranstype.oneWire",tooltip="chav1961.calc.plugins.detalis.ringcurrenttrans.currenttranstype.oneWire.tt")
	ONE_WIRE("schema1.SVG"), 
	@LocaleResource(value="chav1961.calc.plugins.detalis.ringcurrenttrans.currenttranstype.coiled",tooltip="chav1961.calc.plugins.detalis.ringcurrenttrans.currenttranstype.coiled.tt")
	COILED("schema2.SVG");
	
	private final String	icon;
	
	CurrentTransType(final String icon) {
		this.icon = icon;
	}
	
	@Override
	public String getSVGIcon() {
		return icon;
	}
}
