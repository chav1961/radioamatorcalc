package chav1961.calc.plugins.devices.pulsestab;

import chav1961.calc.interfaces.SVGIconKeeper;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.devices.pulsestab.SchemaType/chav1961/calculator/i18n/i18n.xml")
public enum SchemaType implements SVGIconKeeper {
	@LocaleResource(value="chav1961.calc.plugins.devices.pulsestab.schematype.stepup",tooltip="chav1961.calc.plugins.devices.pulsestab.schematype.stepup.tt")
	STEP_UP("schema2.SVG"),
	@LocaleResource(value="chav1961.calc.plugins.devices.pulsestab.schematype.stepdown",tooltip="chav1961.calc.plugins.devices.pulsestab.schematype.stepdown.tt")
	STEP_DOWN("schema1.SVG"),
	@LocaleResource(value="chav1961.calc.plugins.devices.pulsestab.schematype.inverted",tooltip="chav1961.calc.plugins.devices.pulsestab.schematype.inverted.tt")
	INVERTED("schema3.SVG");
	
	private final String	icon;
	
	SchemaType(final String icon) {
		this.icon = icon;
	}
	
	@Override
	public String getSVGIcon() {
		return icon;
	}
	
}
