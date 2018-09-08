package chav1961.calc.environment.search;

interface SearchListener {
	enum SortOfFacet {
		USES, TAGS, SEE_ALSO
	}
	
	void pluginClicked(final SearchComponent current, final String pluginId);
	void facetClicked(final SearchComponent current, final String facetId, final String facetText);
	void tagClicked(final SearchComponent current, final String tagId, final String tagText);
	void linkClicked(final SearchComponent current, final String pluginId);
}