package chav1961.calc.environment.search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;

import chav1961.calc.Application;
import chav1961.calc.LocalizationKeys;
import chav1961.calc.elements.coils.singlecoilsplugin.SingleCoilsService;
import chav1961.calc.environment.Constants;
import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
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
	private final SearchString				toolBar;
	private final ActionListener			toClose = new ActionListener() {
												@Override
												public void actionPerformed(ActionEvent e) {
													closeContent();
												}
											};	
	private final SearchListener			listener = new SearchListener(){
												@Override
												public void facetClicked(SearchComponent current, String facetId, String facetText) {
													processFacet(current,facetId,facetText);
												}
										
												@Override
												public void linkClicked(final SearchComponent current, String pluginId) {
													processLink(current,pluginId);
												}
											};
	private final List<History>				history = new ArrayList<>();
	private SearchResultAndNavigator		currentResult = null; 
	
	public SearchManager(final Application application, final XMLDescribedApplication xda, final Localizer localizer) throws NullPointerException, IllegalArgumentException, EnvironmentException {
		setLayout(new BorderLayout());
		this.application = application;
		this.xda = xda;
		this.localizer = localizer;
		
		add(this.toolBar = new SearchString(localizer,toClose),BorderLayout.NORTH);
		SwingUtils.assignActionListeners(toolBar,this);
		SwingUtils.assignActionKey(this,KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),toClose, "closeSearch");		
		SwingUtils.assignActionKey(this.toolBar,KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),toClose, "closeSearch");		
		fillLocalizedString(localizer.currentLocale().getLocale(),localizer.currentLocale().getLocale());
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
	
	public void open(final String searchString, final SearchCallback callback) {
	}

	public void forward() {
		
	}
	
	public void backward() {
		
	}

	public void focus() {
		toolBar.requestFocusInWindow();
		toolBar.focus();
	}
	
	@OnAction("closeSearch")
	public void closeContent() {
		application.unsearch();
	}

	@OnAction("enter")
	private void startSearch() {
		switch (localizer.currentLocale().getLocale().getLanguage()) {
			case "ru" :
				break;
			case "en" :
				break;
			default :
		}
		if (currentResult != null) {
			remove(currentResult);
			currentResult.close();
		}
		try {
			currentResult = new SearchResultAndNavigator(localizer,new PluginInterface[] {new SingleCoilsService()},10,listener);
			SwingUtils.assignActionKey(currentResult,KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),toClose,"closeSearch");		
		} catch (LocalizationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		add(currentResult,BorderLayout.CENTER);
		revalidate();
	}
	
	
	private void processLink(final SearchComponent current, final String pluginId) {
	}

	private void processFacet(final SearchComponent current, final String facetId, final String facetText) {
		
	}

	private void fillLocalizedString(final Locale oldLocale, final Locale newLocale) {
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
	
	
	private static class SearchResultAndNavigator extends JPanel implements Closeable {
		private static final int		PAGE_LIST_LENGTH = 2;
		private static final String[]	PLACEMENT = {BorderLayout.NORTH, BorderLayout.SOUTH};
		
		private final SearchListener	listener = new SearchListener(){
											@Override
											public void facetClicked(final SearchComponent current, final String facetId, final String facetText) {
											}
								
											@Override
											public void linkClicked(final SearchComponent current, final String pluginId) {
												final String[]	parts = pluginId.split("\\#");
												setCurrentPagetNumber(Integer.valueOf(parts[0]));
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
		}
		
		int getNumberOfPages() {
			return totalPages;
		}
		
		int getCurrentPageNumber() {
			return 0;
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
			}
		}
		
		private void fillCentral() throws LocalizationException {
			final SpringLayout	layout = new SpringLayout();
			final JPanel		content = new JPanel(layout);
			JComponent			previous = content;
			
			for (int index = getCurrentPageNumber()*resultsPerPage, maxIndex = Math.min((getCurrentPageNumber()+1)*resultsPerPage,found.length); index < maxIndex; index++) {
				final JComponent	toAdd = new SearchResult(found[index].getLocalizerAssociated(localizer),found[index],parentListener,"dffddffd",0.0); 
				
				content.add(toAdd);
				layout.putConstraint(SpringLayout.NORTH,toAdd,0,SpringLayout.NORTH,previous);
				layout.putConstraint(SpringLayout.WEST,toAdd,0,SpringLayout.WEST,content);
				layout.putConstraint(SpringLayout.EAST,toAdd,0,SpringLayout.EAST,content);
				previous = toAdd;
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
	
	private static class History {
		
	}
}
