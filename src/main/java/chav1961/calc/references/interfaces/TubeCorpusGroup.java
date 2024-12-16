package chav1961.calc.references.interfaces;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.references.interfaces.TubeCorpusGroup/chav1961/calculator/i18n/i18n.xml")
public enum TubeCorpusGroup {
	@LocaleResource(value="chav1961.calc.references.tubes.tubeCorpusGroup.tg",tooltip="chav1961.calc.references.tubes.tubeCorpusGroup.tg.tt")
	TG("type.png");
	
	private final String	icon;
	
	private TubeCorpusGroup(final String icon) {
		this.icon = icon;
	}
	
	public Icon getGroupIcon() {
		return new ImageIcon(this.getClass().getResource(icon));
	}
}
