package chav1961.calc.references.interfaces;

import javax.swing.Icon;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.references.interfaces.TubePanelType/chav1961/calculator/i18n/i18n.xml")
public enum TubePanelType {
	@LocaleResource(value="chav1961.calc.references.tubes.tubePanelType.pin9",tooltip="chav1961.calc.references.tubes.tubePanelType.pin9.tt")
	PIN9(TubePanelGroup.PIN9);
	
	private final TubePanelGroup	group;
	
	private TubePanelType(final TubePanelGroup group) {
		this.group = group;
	}
	
	public TubePanelGroup getGroup() {
		return group;
	}
	
	public Icon getIcon() {
		return null;
	}
}
