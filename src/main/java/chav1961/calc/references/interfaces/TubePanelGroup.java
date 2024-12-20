package chav1961.calc.references.interfaces;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.references.interfaces.TubePanelGroup/chav1961/calculator/i18n/i18n.xml")
public enum TubePanelGroup {
	@LocaleResource(value="chav1961.calc.references.tubes.tubePanelGroup.pin9",tooltip="chav1961.calc.references.tubes.tubePanelGroup.pin9.tt",icon="root://chav1961.calc.references.interfaces.TubePanelGroup/images/pipeOnFalseControl.png")
	PIN9("pin9.svg");
	
	private final String	svg;
	
	private TubePanelGroup(final String svg) {
		this.svg = svg;
	}
	
	public String getSVGName() {
		return svg;
	}
}
