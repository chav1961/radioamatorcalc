package chav1961.calc.plugins.details.coils;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.details.coils.CoilType/chav1961/calculator/i18n/i18n.xml")
public enum CoilType {
	@LocaleResource(value="chav1961.calc.plugins.details.coils.coilType.single",tooltip="chav1961.calc.plugins.details.coils.coilType.single.tt")
	SINGLE_LAYER, 
	@LocaleResource(value="chav1961.calc.plugins.details.coils.coilType.multilayer",tooltip="chav1961.calc.plugins.details.coils.coilType.multilayer.tt")
	MULTI_LAYER, 
	@LocaleResource(value="chav1961.calc.plugins.details.coils.coiltype.stepped",tooltip="chav1961.calc.plugins.details.coils.coiltype.stepped.tt")
	STEPPED
}
