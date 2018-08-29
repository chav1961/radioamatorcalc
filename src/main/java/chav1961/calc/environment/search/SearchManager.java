package chav1961.calc.environment.search;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;

import chav1961.calc.Application;
import chav1961.calc.LocalizationKeys;
import chav1961.calc.environment.Constants;
import chav1961.calc.environment.search.LuceneWrapper.URIAndScore;
import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.ui.swing.SmartToolTip;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.XMLDescribedApplication;
import chav1961.purelib.ui.swing.interfaces.OnAction;

public class SearchManager extends JPanel implements LocaleChangeListener {
	private static final long 				serialVersionUID = 1308755467124786226L;
	
	private final Application				application;
	private final XMLDescribedApplication	xda;
	private final Localizer					localizer;
	private final LoggerFacade				logger;
	private final SearchString				toolBar;
	private final ActionListener			processKeys = new ActionListener() {
												@Override
												public void actionPerformed(ActionEvent e) {
													switch (e.getActionCommand()) {
														case "backward"		:
															backward();
															break;
														case "forward"		:
															forward();
															break;
														case "closeSearch"	:
															closeContent();
															break;
														default :
															throw new UnsupportedOperationException("Unknown action ["+e.getActionCommand()+"]");
													}
												}
											};	
	private final SearchListener			listener = new SearchListener(){
												@Override
												public void facetClicked(SearchComponent current, String facetId, String facetText) {
													processFacet(current,facetId,facetText);
												}
										
												@Override
												public void tagClicked(SearchComponent current, String tagId, String tagText) {
													processTag(current,tagId,tagText);
												}
												
												@Override
												public void linkClicked(final SearchComponent current, String pluginId) {
													processLink(current,pluginId);
												}

											};
	private final History					history = new History();
	private LuceneWrapper					wrapper;
	private SearchResultAndNavigator		currentResult = null; 
	
	public SearchManager(final Application application, final XMLDescribedApplication xda, final Localizer localizer, final LoggerFacade logger) throws NullPointerException, IllegalArgumentException, EnvironmentException {
		setLayout(new BorderLayout());
		this.application = application;
		this.xda = xda;
		this.localizer = localizer;
		this.logger = logger;
		
		add(this.toolBar = new SearchString(localizer,processKeys),BorderLayout.NORTH);
		add(this.history,BorderLayout.CENTER);
		SwingUtils.assignActionListeners(toolBar,this);
		SwingUtils.assignActionKey(this,KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),processKeys, "closeSearch");		
		SwingUtils.assignActionKey(this.toolBar,KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),processKeys, "closeSearch");		
		SwingUtils.assignActionKey(this.history,KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),processKeys, "closeSearch");		
		SwingUtils.assignActionKey(this.history,KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,KeyEvent.ALT_MASK),processKeys, "backward");		
		SwingUtils.assignActionKey(this.history,KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,KeyEvent.ALT_MASK),processKeys, "forward");		
		fillLocalizedString(localizer.currentLocale().getLocale(),localizer.currentLocale().getLocale());
	}

	public void assignLiceneWrapper(final LuceneWrapper wrapper) {
		this.wrapper = wrapper;
	}
	
	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedString(localizer.currentLocale().getLocale(),localizer.currentLocale().getLocale());
		this.toolBar.localeChanged(oldLocale, newLocale);
	}
	
	@FunctionalInterface
	public interface SearchCallback {
		Iterable<PluginInterface> search(final String searchString);
	}
	
	@OnAction("forward")
	public void forward() {
		history.forward();
	}
	
	@OnAction("backward")
	public void backward() {
		history.backward();
	}

	public void focus() {
		toolBar.requestFocusInWindow();
		toolBar.focus();
	}
	
	@OnAction("closeSearch")
	public void closeContent() {
		history.clear();
		application.unsearch();
	}

	@OnAction("enter")
	private void startSearch() {
		if (currentResult != null) {
			history.clear();
			currentResult.close();
		}
		try{currentResult = search(LuceneWrapper.FIELD_CONTENT,toolBar.getQueryString());
			history.append(currentResult);
			revalidate();
		} catch (IOException | LocalizationException e) {
			logger.message(Severity.warning,e,"Error searching content...");
		}
	}
	
	private void processFacet(final SearchComponent current, final String facetId, final String facetText) {
		try{history.append(search(LuceneWrapper.FIELD_USES,facetText));
		} catch (IOException | LocalizationException e) {
			logger.message(Severity.warning,e,"Error searching content...");
		}
	}

	private void processTag(final SearchComponent current, final String tagId, final String tagText) {
		try{history.append(search(LuceneWrapper.FIELD_TAGS,tagText));
		} catch (IOException | LocalizationException e) {
			logger.message(Severity.warning,e,"Error searching content...");
		}
	}

	private void processLink(final SearchComponent current, final String pluginId) {
		try{history.append(search(LuceneWrapper.FIELD_SEEALSO,pluginId));
		} catch (IOException | LocalizationException e) {
			logger.message(Severity.warning,e,"Error searching content...");
		}
	}

	private void fillLocalizedString(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		toolBar.localeChanged(oldLocale, newLocale);
	}

	private SearchResultAndNavigator search(final String searchFieldName, final String query) throws LocalizationException, IOException {
		final List<PluginInterface>	found = new ArrayList<>();
		final LangAndAnalyzer		laa;
	
		switch (localizer.currentLocale().getLocale().getLanguage()) {
			case "ru" : 
				laa = new LangAndAnalyzer("ru",new RussianAnalyzer());
				break;
			case "en" :
				laa = new LangAndAnalyzer("en",new EnglishAnalyzer());
				break;
			default :
				throw new UnsupportedOperationException("Lang ["+localizer.currentLocale().getLocale().getLanguage()+"] is not supported yet");
		}
		
		for (URIAndScore item : wrapper.search(searchFieldName,laa.analyzer,query)) {
			for (PluginInterface plugin : ServiceLoader.load(PluginInterface.class)) {
				if (item.getURI().toString().equals(plugin.getPluginId())) {
					found.add(plugin);
					break;
				}
			}
		}
		return new SearchResultAndNavigator(localizer,found.toArray(new PluginInterface[found.size()]),10,listener);
	}
	
	private static class SearchString extends JToolBar implements LocaleChangeListener {
		private static final long 		serialVersionUID = -3837317681000458485L;

		private final Localizer			localizer;
		private final JLabel			searchLabel = new JLabel();
		private final JTextField		searchQuery = new JTextField() { private static final long serialVersionUID = 1L;
											@Override
											public JToolTip createToolTip() {
												return new SmartToolTip(localizer,this);
											}
										};
		
		SearchString(final Localizer localizer, final ActionListener toClose) throws LocalizationException {
			this.localizer = localizer;
			searchQuery.setColumns(50);
			add(searchLabel);
			addSeparator();
			add(searchQuery);
			searchQuery.setActionCommand("enter");
			setFloatable(false);
			fillLocalizedString(localizer.currentLocale().getLocale(),localizer.currentLocale().getLocale());
			this.setFocusable(true);
			searchQuery.setFocusable(true);
			SwingUtils.assignActionKey(searchQuery,KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),toClose,"closeSearch");		
		}

		public String getQueryString() {
			return searchQuery.getText();
		}
		
		@Override
		public void localeChanged(Locale oldLocale, Locale newLocale) throws LocalizationException {
			fillLocalizedString(localizer.currentLocale().getLocale(),localizer.currentLocale().getLocale());
		}
		
		public void focus() {
			searchQuery.requestFocusInWindow();
		}
		
		private void fillLocalizedString(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
			searchLabel.setText(localizer.getValue(LocalizationKeys.SEARCH_LABEL));
			searchQuery.setToolTipText(localizer.getValue(LocalizationKeys.SEARCH_TOOLTIP));
		}
	}
	
	
	private static final String[]	PLACEMENT = {BorderLayout.NORTH, BorderLayout.SOUTH};
	
	private class SearchResultAndNavigator extends JPanel implements Closeable {
		private static final long 		serialVersionUID = 3152240694102305176L;
		private static final int		PAGE_LIST_LENGTH = 2;
		
		private final SearchListener	listener = new SearchListener(){
											@Override
											public void facetClicked(final SearchComponent current, final String facetId, final String facetText) {
											}
								
											@Override
											public void linkClicked(final SearchComponent current, final String pluginId) {
												final String[]	parts = pluginId.split("\\#");
												setCurrentPagetNumber(Integer.valueOf(parts[0]));
											}

											@Override
											public void tagClicked(SearchComponent current, String tagId, String tagText) {
												// TODO Auto-generated method stub
												
											}
										};
		private final Localizer			localizer;
		private final PluginInterface[]	found;
		private final int				totalPages, resultsPerPage;
		private final URI[]				pageUris;	
		private final SearchListener	parentListener;
		private int						currentPage = 0;
		
		SearchResultAndNavigator(final Localizer localizer, final PluginInterface[] found, final int resultsPerPage, final SearchListener parentListener) throws LocalizationException {
			setLayout(new BorderLayout());
			this.localizer = localizer;
			this.found = found.clone();
			this.resultsPerPage = resultsPerPage;
			this.totalPages = (found.length + resultsPerPage - 1) / resultsPerPage;
			this.pageUris = new URI[this.totalPages];
			this.parentListener = parentListener;
			buildPageUris(this.pageUris,this.totalPages,this.resultsPerPage);
			fillNavigator();
			fillCentral();
			SwingUtils.assignActionKey(this,KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),processKeys,"closeSearch");
			SwingUtils.assignActionKey(this,KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,KeyEvent.ALT_MASK),processKeys, "backward");		
			SwingUtils.assignActionKey(this,KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,KeyEvent.ALT_MASK),processKeys, "forward");		
		}
		
		int getNumberOfPages() {
			return totalPages;
		}
		
		int getCurrentPageNumber() {
			return currentPage;
		}
		
		void setCurrentPagetNumber(final int newCurrentPageNumber) {
			this.currentPage = newCurrentPageNumber;
			try{fillNavigator();
				fillCentral();
				revalidate();
			} catch (LocalizationException e) {
			}
		}

		private void buildPageUris(final URI[] pageUris, final int totalPages, final int resultsPerPage) {
			for (int index = 0; index < totalPages; index += resultsPerPage) {
				final int	limit = Math.min(totalPages*resultsPerPage,pageUris.length)-resultsPerPage*index;
				final int	offset = resultsPerPage*index;
				final int	number = index;
				
				pageUris[index] = URI.create(Constants.PAGE_PATH+"#"+number+"?offset="+offset+"&limit="+limit);
			}
		}

		private void fillNavigator() throws LocalizationException {
			final int	startPageNumber = ((getCurrentPageNumber() + PAGE_LIST_LENGTH - 1) / PAGE_LIST_LENGTH) * PAGE_LIST_LENGTH;
			final int	endPageNumer = Math.min(totalPages,startPageNumber+PAGE_LIST_LENGTH);
			
			for (int index = 0; index < PLACEMENT.length; index++) {
				final SearchNavigator	navigator = new SearchNavigator(localizer,listener,found.length,startPageNumber,endPageNumer,getCurrentPageNumber(),pageUris);
				
				add(navigator,PLACEMENT[index]);
				SwingUtils.assignActionKey(navigator,KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),processKeys,"closeSearch");
				SwingUtils.assignActionKey(navigator,KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,KeyEvent.ALT_MASK),processKeys, "backward");		
				SwingUtils.assignActionKey(navigator,KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,KeyEvent.ALT_MASK),processKeys, "forward");		
			}
		}
		
		private void fillCentral() throws LocalizationException {
			final SpringLayout	layout = new SpringLayout();
			final JPanel		content = new JPanel(layout);
			JComponent			previous = content;
			
			for (int index = getCurrentPageNumber()*resultsPerPage, maxIndex = Math.min((getCurrentPageNumber()+1)*resultsPerPage,found.length); index < maxIndex; index++) {
				final JComponent	toAdd = new SearchResult(found[index].getLocalizerAssociated(localizer),found[index],parentListener,"dffddffd",0.0); 
				
				content.add(toAdd);
				layout.putConstraint(SpringLayout.WEST,toAdd,0,SpringLayout.WEST,content);
				layout.putConstraint(SpringLayout.EAST,toAdd,0,SpringLayout.EAST,content);
				if (previous == content) {
					layout.putConstraint(SpringLayout.NORTH,toAdd,0,SpringLayout.NORTH,previous);
				}
				else {
					layout.putConstraint(SpringLayout.NORTH,toAdd,0,SpringLayout.SOUTH,previous);
				}
				previous = toAdd;
				SwingUtils.assignActionKey(toAdd,KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),processKeys,"closeSearch");
				SwingUtils.assignActionKey(toAdd,KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,KeyEvent.ALT_MASK),processKeys, "backward");		
				SwingUtils.assignActionKey(toAdd,KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,KeyEvent.ALT_MASK),processKeys, "forward");		
			}
			add(content,BorderLayout.CENTER);
		}

		@Override
		public void close() {
			for (int index = 0; index < pageUris.length; index++) {
				pageUris[index] = null;
			}
			for (int index = 0; index < pageUris.length; index++) {
				found[index] = null;
			}
		}
	}
	
	private class History extends JPanel {
		private static final long serialVersionUID = 6878162862781229791L;

		private final CardLayout	cl = new CardLayout();
		private final List<String>	items = new ArrayList<>();
		private int 				itemIndex = 0, currentIndex = -1;
		
		History() {
			setLayout(cl);
		}
		
		void append(final JPanel panel) {
			final String	label = "label "+itemIndex++;
			
			add(panel,label);
			items.add(label);
			cl.show(this,label);
			currentIndex = items.size()-1;
			SwingUtils.assignActionKey(panel,KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),processKeys,"closeSearch");
			SwingUtils.assignActionKey(panel,KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,KeyEvent.ALT_MASK),processKeys, "backward");		
			SwingUtils.assignActionKey(panel,KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,KeyEvent.ALT_MASK),processKeys, "forward");
			this.requestFocusInWindow();
		}
		
		void forward() {
			if (currentIndex < items.size()-1) {
				cl.show(this,items.get(++currentIndex));
				this.requestFocusInWindow();
			}
		}
		
		void backward() {
			if (currentIndex > 0) {
				cl.show(this,items.get(--currentIndex));
				this.requestFocusInWindow();
			}
		}

		void clear() {
			removeAll();
			items.clear();
			itemIndex = 0;
			currentIndex = -1;
		}
	}
}
