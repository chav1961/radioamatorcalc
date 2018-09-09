package chav1961.calc.environment.search;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import chav1961.calc.LocalizationKeys;
import chav1961.calc.environment.Constants;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;

class SearchNavigator extends JPanel implements SearchComponent {
	private static final long 		serialVersionUID = 1L;
	private static final String		LEFT_FORMAT = "<html><head></head><body><font size=16>%1$d %2$s</font></body></html>";
	private static final String		CENTER_FORMAT = "<html><head></head><body><font size=16>%1$s %2$s %3$s</font></body></html>";
	private static final String		CENTER_THEONLY_FORMAT = "<html><head></head><body><font size=16>%1$s</font></body></html>";
	private static final String		CENTER_SHORT_FORMAT = "<html><head></head><body><font size=16>%1$s %2$s</font></body></html>";
	private static final String		CENTER_NOT_FOUND_FORMAT = "<html><head></head><body><font size=16 color=red>%1$s</font></body></html>";
	private static final String		REF_FORMAT = " <a href=\"%1$s\">%2$s</a>";
	private static final String		CURRENT_REF_FORMAT = " <b>%2$s</b>";

	private final Localizer			localizer;
	
	public SearchNavigator(final Localizer localizer, final SearchListener listener, final int totalResults, final int startIndex, final int endIndex, final int currentIndex, final URI... content) throws LocalizationException {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null"); 
		}
		else if (listener == null) {
			throw new NullPointerException("Search listener can't be null"); 
		}
		else {
			this.localizer = localizer;
			
			final JEditorPane	leftPane = new JEditorPane("text/html",null), centerPane = new JEditorPane("text/html",null); 
			
			setLayout(new BorderLayout());		
			leftPane.setEditable(false);
			leftPane.setBackground(Constants.LEFT_PANE_COLOR);
			add(leftPane,BorderLayout.WEST);
			leftPane.setText(String.format(LEFT_FORMAT,totalResults,localizer.getValue(LocalizationKeys.SEARCH_FOUND)));
			leftPane.setPreferredSize(new Dimension(200,30));
			
			centerPane.setEditable(false);
			centerPane.setBackground(Constants.CENTER_PANE_COLOR);
			add(centerPane,BorderLayout.CENTER);
			
			if (totalResults > 0) {
				if (totalResults == 1) {
					centerPane.setText(String.format(CENTER_THEONLY_FORMAT
						,toRef(content[0],1,false)
						));
				}
				else {
					centerPane.setText(String.format(CENTER_FORMAT
								,toRef(content[0],1,false)
								,buildRefList(content,startIndex,endIndex,currentIndex)
								,toRef(content[content.length-1],1,false)
								));
				}
				
				centerPane.addHyperlinkListener(new HyperlinkListener(){
			         public void hyperlinkUpdate(final HyperlinkEvent e) {
			             if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			                 if (e instanceof HTMLFrameHyperlinkEvent) {
			                	 try{InternalUtils.parseAndCall(localizer,SearchNavigator.this,listener,e.getURL().toURI());
								} catch (URISyntaxException | LocalizationException exc) {
								}
			                 }
			             }
			         }
				});
			}
			else {
				centerPane.setText(String.format(CENTER_NOT_FOUND_FORMAT,localizer.getValue(LocalizationKeys.SEARCH_NOT_FOUND)));
			}
		}
	}

	private String buildRefList(final URI[] content, final int startIndex, final int endIndex, final int currentIndex) {
		final StringBuilder	sb = new StringBuilder();
		
		for (int index = startIndex; index < endIndex; index++) {
			sb.append(' ').append(toRef(content[index],index+1,index == currentIndex));
		}
		return sb.toString();
	}
	
	private String toRef(final URI ref, final int pageNo, final boolean current) {
		if (current) {
			return String.format(CURRENT_REF_FORMAT,ref,pageNo);
		}
		else {
			return String.format(REF_FORMAT,ref,pageNo);
		}
	}
}

