package chav1961.calc.references.interfaces;

import javax.swing.Icon;

public enum TubePanelType {
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
