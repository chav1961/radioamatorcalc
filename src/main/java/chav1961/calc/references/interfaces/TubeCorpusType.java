package chav1961.calc.references.interfaces;

import javax.swing.Icon;

public enum TubeCorpusType {
	t("type.png");
	
	private final String	corpusName;
	
	private TubeCorpusType(final String corpusName) {
		this.corpusName = corpusName;
	}
	
	public String getCorpusName() {
		return corpusName;
	}
	
	public Icon getCorpus() {
		return null;
	}
}
