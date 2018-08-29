package chav1961.calc.environment.search;

import org.apache.lucene.analysis.StopwordAnalyzerBase;

class LangAndAnalyzer {
	final String				language;
	final StopwordAnalyzerBase	analyzer;
	
	public LangAndAnalyzer(final String language, final StopwordAnalyzerBase analyzer) {
		this.language = language;
		this.analyzer = analyzer;
	} 
}