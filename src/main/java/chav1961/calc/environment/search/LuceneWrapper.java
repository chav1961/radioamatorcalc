package chav1961.calc.environment.search;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;

import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.Localizer;

public class LuceneWrapper {
	public static final int		COUNTERS_PARSED = 0;
	public static final int		COUNTERS_FAILED = 1;
	public static final int		DEFAULT_MAX_RESULT = 1000;

	public static final String	FIELD_ID = "pluginId";
	public static final String	FIELD_CONTENT = "content";
	public static final String	FIELD_USES = "uses";
	public static final String	FIELD_TAGS = "tags";
	public static final String	FIELD_SEEALSO = "seeAlso";
	
	private static final LangAndAnalyzer[]	ANALYZERS = {new LangAndAnalyzer("ru",new RussianAnalyzer())
														, new LangAndAnalyzer("en",new EnglishAnalyzer())
														};
	
	private final Directory		luceneDir;

	public interface URIAndScore {
		URI getURI();
		float getScore();
		String getFragment();
	}

	public LuceneWrapper(final Directory luceneDir) {
		if (luceneDir == null) {
			throw new NullPointerException("Lucene directory can't be null");
		}
		else {
			this.luceneDir = luceneDir;
		}
	}

	public int[] buildDirectoryIndex(final Localizer parent, final LoggerFacade printer) throws IOException, LocalizationException {
		final int[]		counters = new int[]{0,0};
		
		for (LangAndAnalyzer item : ANALYZERS) {
			buildDirectoryIndex(parent,item,printer,counters);
		}
		return counters;
	}

	public Iterable<URIAndScore> search(final String searchField, final Analyzer analyzer, final String searchString) throws IOException {
		return search(searchField,analyzer,searchString,DEFAULT_MAX_RESULT);
	}
	
	public Iterable<URIAndScore> search(final String searchField, final Analyzer analyzer, final String searchString, final int maxResults) throws IOException {
		try{final Query 			query = new QueryParser(searchField, analyzer).parse(searchString);
		    final List<URIAndScore>	result = new ArrayList<>(); 
	
		    try(final IndexReader 	reader = DirectoryReader.open(luceneDir)) {
			    final IndexSearcher	searcher = new IndexSearcher(reader);
			    final TopScoreDocCollector 	collector = TopScoreDocCollector.create(maxResults);
		    	final Formatter 	formatter = new SimpleHTMLFormatter("<b><font color=red>", "</font></b>");
		    	final QueryScorer 	queryScorer = new QueryScorer(query);
		   	    final Highlighter 	highlighter = new Highlighter(formatter, queryScorer);
		   	    final Set<URI>		preventDuplicates = new HashSet<>();

		   	    highlighter.setTextFragmenter(new SimpleSpanFragmenter(queryScorer, Integer.MAX_VALUE));
		   	    highlighter.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);
			    searcher.search(query, collector);
			    
			    for (ScoreDoc item : collector.topDocs().scoreDocs) {
			    	final URI	docId = URI.create(searcher.doc(item.doc).get(FIELD_ID));
			    	
			    	if (!preventDuplicates.contains(docId)) {
				    	preventDuplicates.add(docId);
				    	result.add(new URIAndScoreImpl(docId,item.score,highlighter.getBestFragment(analyzer,FIELD_CONTENT,searcher.doc(item.doc).get(FIELD_CONTENT))));
			    	}
			    }
		    } catch (IOException | InvalidTokenOffsetsException e) {
				e.printStackTrace();
			}
			return result;
		} catch (ParseException e) {
			return new ArrayList<>();
		}
	}
	
	
	private void buildDirectoryIndex(final Localizer parent, final LangAndAnalyzer item, final LoggerFacade printer, final int[] counters) throws LocalizationException, IOException {
		final IndexWriterConfig	config = new IndexWriterConfig(item.analyzer);

	    try(final IndexWriter writer = new IndexWriter(luceneDir,config)) {
	    	indexContent(parent,item,writer,printer,counters);
		}
	}

	
	private void indexContent(final Localizer parent, final LangAndAnalyzer item, final IndexWriter writer, final LoggerFacade printer, final int[] counters) throws LocalizationException, IOException {
		for (PluginInterface plugin : ServiceLoader.load(PluginInterface.class)) {
			final Localizer		localizer = plugin.getLocalizerAssociated(parent);
			final Document 		doc = new Document();
			final StringBuilder	sb = new StringBuilder();
			
			localizer.setCurrentLocale(Locale.forLanguageTag(item.language));
			
			doc.add(new StringField(FIELD_ID, plugin.getPluginId(), Field.Store.YES));
			doc.add(new TextField(FIELD_CONTENT, localizer.getValue(plugin.getCaptionId()) +localizer.getValue(plugin.getHelpId()), Field.Store.YES));
			
			sb.setLength(0);
			for (String value : plugin.getUsesIds(parent)) {
				sb.append(localizer.getValue(value)).append("\n");
			}
			doc.add(new TextField(FIELD_USES, sb.toString(), Field.Store.YES));
			
			sb.setLength(0);
			for (String value : plugin.getTagsIds(parent)) {
				sb.append(localizer.getValue(value)).append("\n");
			}
			doc.add(new TextField(FIELD_TAGS, sb.toString(), Field.Store.YES));

			sb.setLength(0);
			for (String value : plugin.getSeeAlsoIds(parent)) {
				sb.append(value).append("\n");
			}
			doc.add(new TextField(FIELD_SEEALSO, sb.toString(), Field.Store.YES));
			
			writer.addDocument(doc);
			
			localizer.setCurrentLocale(parent.currentLocale().getLocale());
		}
	}
	

	private static class URIAndScoreImpl implements URIAndScore {
		private final URI		uri;
		private final float		score;
		private final String	fragment;
		
		public URIAndScoreImpl(URI uri, float score, final String fragment) {
			this.uri = uri;
			this.score = score;
			this.fragment = fragment;
		}

		@Override public URI getURI() {return uri;}
		@Override public float getScore() {return score;}
		@Override public String getFragment() {return fragment;}
	}
}
