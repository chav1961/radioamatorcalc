package chav1961.calc.references.interfaces;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public enum TubeCorpusType {
	t("type.png", TubeCorpusGroup.TG);
	
	private final String			corpusName;
	private final TubeCorpusGroup	corpusGroup;
	
	private TubeCorpusType(final String corpusName, final TubeCorpusGroup group) {
		this.corpusName = corpusName;
		this.corpusGroup = group;
	}
	
	public String getCorpusName() {
		return corpusName;
	}
	
	public Icon getCorpus() {
		return new ImageIcon(this.getClass().getResource(getCorpusName()));
	}
	
	public TubeCorpusGroup getGroup() {
		return corpusGroup;
	}
}
