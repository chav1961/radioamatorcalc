package chav1961.calc.plugins.details.ringpulsetrans;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.details.ringpulsetrans.SchemaType/chav1961/calculator/i18n/i18n.xml")
public enum SchemaType {
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.schematype.halfbridge",tooltip="chav1961.calc.plugins.details.ringpulsetrans.schematype.halfbridge.tt")
	HALF_BRIDGE, 
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.schematype.bridge",tooltip="chav1961.calc.plugins.details.ringpulsetrans.schematype.bridge.tt")
	BRIDGE, 
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.schematype.middlepoint",tooltip="chav1961.calc.plugins.details.ringpulsetrans.schematype.middlepoint.tt")
	MIDDLE_POINT
}
