package chav1961.calc.references.interfaces;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public enum TubeCorpusGroup {
	TG("type.png");
	
	private final String	icon;
	
	private TubeCorpusGroup(final String icon) {
		this.icon = icon;
	}
	
	public Icon getGroupIcon() {
		return new ImageIcon(this.getClass().getResource(icon));
	}
}
