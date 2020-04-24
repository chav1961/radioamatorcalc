package chav1961.calc.utils;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import chav1961.purelib.basic.CharUtils.SubstitutionSource;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.ui.swing.AutoBuiltForm;
import chav1961.purelib.ui.swing.useful.svg.SVGPainter;
import chav1961.purelib.ui.swing.useful.svg.SVGParser;
import chav1961.purelib.ui.swing.useful.svg.SVGPainter.FillPolicy;
import chav1961.purelib.ui.swing.useful.svg.SVGParser.InstrumentGetter;

public class InnerSVGPluginWindow<T> extends JPanel implements LocaleChangeListener {
	private static final long 			serialVersionUID = 1L;
	private static final int 			MINIMUM_SVG_SCALE = 5;

	private final AutoBuiltForm<T> 		form;
	private final String[]				svgItems; 
	private final Map<String,SVGViewer>	viewers = new HashMap<>();
	private final JPanel				card = new JPanel(new CardLayout());
	private String						currentView;

	public InnerSVGPluginWindow(final Class<T> instanceClass, final String svgURI, final AutoBuiltForm<T> form) throws IOException, ContentException {
		this(instanceClass,svgURI,form,null);
	}

	public InnerSVGPluginWindow(final Class<T> instanceClass, final String svgURI, final AutoBuiltForm<T> form, final SubstitutionSource ss) throws IOException, ContentException {
		this.form = form;
		this.svgItems = svgURI.split("\\,");
		
		setLayout(new BorderLayout(2,2));
		
		for (int index = 0; index < svgItems.length; index++) {
			try(final InputStream	is = instanceClass.getResourceAsStream(svgItems[index])) {
				final SVGViewer		v = new SVGViewer(ss != null ? SVGParser.parse(is,ss) : SVGParser.parse(is));
				
				card.add(v,svgItems[index]);
				this.viewers.put(svgItems[index],v);
			}
		}
		
		add(card,BorderLayout.CENTER);
		add(form,BorderLayout.EAST);
		((CardLayout)card.getLayout()).show(card,currentView = svgItems[0]);
		
		addComponentListener(new ComponentListener() {
			@Override public void componentShown(ComponentEvent e) {}
			@Override public void componentMoved(ComponentEvent e) {}
			@Override public void componentHidden(ComponentEvent e) {}
			
			@Override
			public void componentResized(ComponentEvent e) {
				final Dimension	windowSize = InnerSVGPluginWindow.this.getSize();
				final Dimension	panelSize = form.getSize();
				final Dimension	viewerSize = card.getMinimumSize();
				
				card.setPreferredSize(new Dimension(Math.min(windowSize.width-panelSize.width,viewerSize.width),Math.max(windowSize.height,panelSize.height)));
			}
		});
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		form.localeChanged(oldLocale, newLocale);
	}
	
	public Dimension getInnerSVGSize() {
		return viewers.get(currentView).getInnerSVGSize();
	}
	
	public void refresh() {
		card.repaint();
	}

	public void selectIcon(final String icon) {
		((CardLayout)card.getLayout()).show(card,currentView = icon);
	}
	
	private static class SVGViewer extends JComponent {
		private static final long 	serialVersionUID = 1L;
		
		private final SVGPainter	painter;
		
		private SVGViewer(final SVGPainter painter) {
			this.painter = painter;
			setBorder(new LineBorder(Color.BLACK));
			setMinimumSize(new Dimension(painter.getWidth()/MINIMUM_SVG_SCALE,painter.getHeight()/MINIMUM_SVG_SCALE));
			setPreferredSize(new Dimension(painter.getWidth()/MINIMUM_SVG_SCALE,painter.getHeight()/MINIMUM_SVG_SCALE));
		}

		Dimension getInnerSVGSize() {
			return new Dimension(painter.getWidth(),painter.getHeight());
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			final Dimension	pref = getSize();
			
			painter.paint((Graphics2D)g,pref.width,pref.height);
		}
	}
}
