package chav1961.calc.environment.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.interfaces.Localizer;

public class LuceneWrapper {
	public static final int		COUNTERS_PARSED = 0;
	public static final int		COUNTERS_FAILED = 1;
	
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

	public Iterable<URIAndScore> search(final Analyzer analyzer, final String searchString) throws IOException {
		return search(analyzer,searchString,1000);
	}
	
	public Iterable<URIAndScore> search(final Analyzer analyzer, final String searchString, final int maxResults) throws IOException {
		try{final Query 			query = new QueryParser("content", analyzer).parse(searchString);
		    final List<URIAndScore>	result = new ArrayList<>(); 
	
		    try(final IndexReader 	reader = DirectoryReader.open(luceneDir)) {
			    final IndexSearcher	searcher = new IndexSearcher(reader);
			    final TopScoreDocCollector 	collector = TopScoreDocCollector.create(maxResults);
		    	final Formatter 	formatter = new SimpleHTMLFormatter("<b><font color=red>", "</font></b>");
		    	final QueryScorer 	queryScorer = new QueryScorer(query);
		   	    final Highlighter 	highlighter = new Highlighter(formatter, queryScorer);
		    	
		   	    highlighter.setTextFragmenter(new SimpleSpanFragmenter(queryScorer, Integer.MAX_VALUE));
		   	    highlighter.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);
			    searcher.search(query, collector);
			    
			    for (ScoreDoc item : collector.topDocs().scoreDocs) {
			    	result.add(new URIAndScoreImpl(URI.create(searcher.doc(item.doc).get("location")),item.score,highlighter.getBestFragment(analyzer, "content",searcher.doc(item.doc).get("content"))));
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
			
			doc.add(new StringField("pluginId", plugin.getPluginId(), Field.Store.YES));
			doc.add(new TextField("content", localizer.getValue(plugin.getCaptionId()) +localizer.getValue(plugin.getHelpId()), Field.Store.YES));
			
			sb.setLength(0);
			for (String value : plugin.getUsesIds(parent)) {
				sb.append(localizer.getValue(value)).append("\n");
			}
			doc.add(new TextField("uses", sb.toString(), Field.Store.YES));
			
			sb.setLength(0);
			for (String value : plugin.getTagsIds(parent)) {
				sb.append(localizer.getValue(value)).append("\n");
			}
			doc.add(new TextField("tags", sb.toString(), Field.Store.YES));
			
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
	
	private static class LangAndAnalyzer {
		final String				language;
		final StopwordAnalyzerBase	analyzer;
		
		public LangAndAnalyzer(final String language, final StopwordAnalyzerBase analyzer) {
			this.language = language;
			this.analyzer = analyzer;
		} 
	}
}
