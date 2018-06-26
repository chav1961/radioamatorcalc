package chav1961.calc.environment.search;

interface SearchListener {
	enum SortOfFacet {
		USES, TAGS, SEE_ALSO
	}
	
	void facetClicked(final SearchComponent current, final String facetId, final String facetText);
	void linkClicked(final SearchComponent current, final String pluginId);
}