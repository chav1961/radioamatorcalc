package chav1961.calc.environment;

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
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
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

import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;

public class LuceneWrapper {
	public static final int		COUNTERS_DIRS = 0;
	public static final int		COUNTERS_FILES = 1;
	public static final int		COUNTERS_PARSED = 2;
	public static final int		COUNTERS_FAILED = 3;
	
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
	
	public int[] buildDirectoryIndex(final File root, final FileFilter filter, final Map<String,URI> classes, final LoggerFacade printer) throws IOException {
		final int[]		counters = new int[]{0,0,0,0};
		
		buildDirectoryIndex(root,filter,new StandardAnalyzer(),classes,printer,counters);
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
	
	
	private void buildDirectoryIndex(final File root, final FileFilter filter, final Analyzer analyzer, final Map<String,URI> classes, final LoggerFacade printer, final int[] counters) throws IOException {
		final IndexWriterConfig	config = new IndexWriterConfig(analyzer);

		Arrays.fill(counters,0);
	    try(final IndexWriter writer = new IndexWriter(luceneDir,config)) {
	    	indexContent(root,filter,writer,classes,printer,counters);
		}
	}
	
	private void indexContent(final File dir, final FileFilter filter, final IndexWriter writer, final Map<String,URI> classes, final LoggerFacade printer, final int[] counters) {
		if (dir != null) {
			if (dir.isFile()) {
				counters[COUNTERS_FILES]++;
				if (filter.accept(dir)) {
					try(final InputStream	is = new FileInputStream(dir)) {
						
						parseContent(is, dir.getName(), dir.getAbsoluteFile().toURI(), writer, classes);
						printer.message(Severity.debug,"File ["+dir.getAbsolutePath()+"] appended to search index");
						counters[COUNTERS_PARSED]++;
					} catch (Exception e) {
						printer.message(Severity.warning,"File ["+dir.getAbsolutePath()+"] - I/O error on parsing ("+e.getMessage()+")");
						counters[COUNTERS_FAILED]++;
					}
				}
			}
			else {
				final File[]	content = dir.listFiles(); 
				
				if (content != null) {
					counters[COUNTERS_DIRS]++;
					for (File item : content) {
						printer.message(Severity.debug,"Scanning ["+dir.getAbsolutePath()+"]...");
						indexContent(item, filter, writer, classes, printer, counters);
					}
				}
			}
		}
	}
	
	private void parseContent(final InputStream is, final String name, final URI location, final IndexWriter writer, final Map<String,URI> classes) throws IOException {
		final StringBuilder	sb = new StringBuilder(); 
		
		try(final Reader	rdr = new InputStreamReader(is);
			final BufferedReader	brdr = new BufferedReader(rdr)) {
			int				index;
			String			line;
			
			while ((line = brdr.readLine()) != null) {
				if ((index = line.indexOf("package ")) >= 0) {
					final String	className = line.substring(index+"package ".length()).split("\\;")[0].trim()+"."+(name.replace(".java",""));
					
					classes.put(className,location);
				}
				
				sb.append(line).append('\n');
			}
		}
		if (sb.length() > 0) {
			final Document 	doc = new Document();
			
			doc.add(new StringField("name", name, Field.Store.YES));
			doc.add(new TextField("content", sb.toString(), Field.Store.YES));
			doc.add(new StringField("location", location.toString(), Field.Store.YES));
			writer.addDocument(doc);
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
