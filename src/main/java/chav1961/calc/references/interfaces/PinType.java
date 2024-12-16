package chav1961.calc.references.interfaces;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.references.interfaces.PinType/chav1961/calculator/i18n/i18n.xml")
public enum PinType {
	@LocaleResource(value="chav1961.calc.references.tubes.pinType.ordinal",tooltip="chav1961.calc.references.tubes.pinType.ordinal.tt")
	ORDINAL,
	@LocaleResource(value="chav1961.calc.references.tubes.pinType.top",tooltip="chav1961.calc.references.tubes.pinType.top.tt")
	TOP
}