package chav1961.calc.environment.search;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import chav1961.calc.LocalizationKeys;
import chav1961.calc.environment.Constants;
import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;

class SearchResult extends JPanel implements SearchComponent{
	private static final long 		serialVersionUID = 1L;	
	private static final String		LEFT_FORMAT = "<html><body><img src=\"%1$s\"><br><hr><b>%2$f</b></body></html>";
	private static final String		CENTER_FORMAT = "<html><body><h2><img src=\"%1$s\">%2$s</h2><p>%3$s</p><hr><p><b>%4$s :</b>%5$s</p><p><b>%6$s :</b>%7$s</p><p><b>%8$s :</b>%9$s</p></body></html>";
	private static final String		USES_FORMAT = " <a href=\""+Constants.USES_PATH+"#%1$s\">%2$s</a>";
	private static final String		TAGS_FORMAT = " <a href=\""+Constants.TAGS_PATH+"#%1$s\">%2$s</a>";
	private static final String		SEE_ALSO_FORMAT = " <a href=\""+Constants.SEE_ALSO_PATH+"#%1$s\">%2$s</a>";

	private final Localizer			localizer;
	private final SearchListener	listener;
	
	SearchResult(final Localizer localizer, final PluginInterface pluginInterface, final SearchListener listener, final String fragment, final double rating) throws LocalizationException {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (pluginInterface == null) {
			throw new NullPointerException("Plugin interface can't be null");
		}
		else if (listener == null) {
			throw new NullPointerException("Search listener can't be null");
		}
		else if (fragment == null || fragment.isEmpty()) {
			throw new IllegalArgumentException("String fragment can't be null");
		}
		else {
			final JEditorPane	leftPane = new JEditorPane("text/html",null), centerPane = new JEditorPane("text/html",null); 
	
			this.localizer = localizer;
			this.listener = listener;
			
			setLayout(new BorderLayout());		
			leftPane.setEditable(false);
			leftPane.setBackground(Constants.LEFT_PANE_COLOR);
			add(leftPane,BorderLayout.WEST);
			leftPane.setText(String.format(LEFT_FORMAT,pluginInterface.getLeftIconURL(),rating));
			
			centerPane.setEditable(false);
			centerPane.setBackground(Constants.CENTER_PANE_COLOR);
			add(centerPane,BorderLayout.CENTER);

			final String 	centerText = String.format(CENTER_FORMAT
											,pluginInterface.getMiniIconURL()
											,InternalUtils.localizeAndEscape(localizer,pluginInterface.getCaptionId())
											,fragment
											,InternalUtils.localizeAndEscape(localizer,LocalizationKeys.SEARCH_USES)
											,buildUses(pluginInterface)
											,InternalUtils.localizeAndEscape(localizer,LocalizationKeys.SEARCH_TAGS)
											,buildTags(pluginInterface)
											,InternalUtils.localizeAndEscape(localizer,LocalizationKeys.SEARCH_SEE_ALSO)
											,buildSeeAlso(pluginInterface)
										);
			centerPane.setText(centerText);
			
			centerPane.addHyperlinkListener(new HyperlinkListener(){
		         public void hyperlinkUpdate(final HyperlinkEvent e) {
		             if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		                 if (e instanceof HTMLFrameHyperlinkEvent) {
		                	 try{InternalUtils.parseAndCall(localizer,SearchResult.this,listener,e.getURL().toURI());
							} catch (URISyntaxException | LocalizationException exc) {
							}
		                 }
		             }
		         }
			});
		}
	}	

	private String buildUses(final PluginInterface pluginInterface) throws LocalizationException {
		final StringBuilder	sb = new StringBuilder();
		char				splitter = ' ';
		
		for (String item : pluginInterface.getUsesIds(localizer)) {
			sb.append(splitter).append(String.format(USES_FORMAT,item,InternalUtils.localizeAndEscape(localizer,item)));
			splitter = ',';
		}
		
		return sb.toString();
	}

	private String buildTags(final PluginInterface pluginInterface) throws LocalizationException {
		final StringBuilder	sb = new StringBuilder();
		char				splitter = ' ';
		
		for (String item : pluginInterface.getTagsIds(localizer)) {
			sb.append(splitter).append(String.format(TAGS_FORMAT,item,InternalUtils.localizeAndEscape(localizer,item)));
			splitter = ',';
		}
		
		return sb.toString();
	}

	private String buildSeeAlso(final PluginInterface pluginInterface) throws LocalizationException {
		final StringBuilder	sb = new StringBuilder();
		char				splitter = ' ';
		
		for (String item : pluginInterface.getSeeAlsoIds(localizer)) {
			sb.append(splitter).append(String.format(SEE_ALSO_FORMAT,item,InternalUtils.localizeAndEscape(localizer,item)));
			splitter = ',';
		}
		
		return sb.toString();
	}

}
