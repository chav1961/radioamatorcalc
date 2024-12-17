package chav1961.calc.references.tubes;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import chav1961.calc.references.interfaces.TubeDescriptor;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.useful.JLocalizedOptionPane;

public class TubesScreen extends JSplitPane {
	private static final long serialVersionUID = 5215087600297617153L;
	private static final String	TITLE_FILTER_FORM = "chav1961.calc.reference.tubesReference.filterForm.title"; 

	private final Localizer		localizer;
	private final TubesPreview	tp;
	
	public TubesScreen(final Localizer localizer) throws IOException {
		super(JSplitPane.VERTICAL_SPLIT);
		
		if (localizer == null) {
			throw new NullPointerException("Localizer can' be null");
		}
		else {
			this.localizer = localizer;
			
			PureLibSettings.PURELIB_LOCALIZER.push(localizer);
			
			try {
				final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				
				Document doc = builder.parse(this.getClass().getResourceAsStream("test.xml"));
				doc.getDocumentElement().normalize();
				
				final XMLBasedTube	item = new XMLBasedTube((Element)doc.getElementsByTagName("tube").item(0));
				
				this.tp = new TubesPreview(localizer);
				setLeftComponent(this.tp);
				final TubesTabs		tt = new TubesTabs(localizer, (t)->{selection(t);}, item);
				setRightComponent(tt);
				
				SwingUtils.assignActionKey(this, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, SwingUtils.KS_FIND, (e)->{find();}, SwingUtils.ACTION_FIND);
			} catch (ParserConfigurationException | SAXException e) {
				throw new IOException(e.getLocalizedMessage(), e);
			}
			
		}
	}
	
	private void selection(final TubeDescriptor desc) {
		tp.refreshDesc(desc);
	}
	
	
	private void find() {
		final FilterForm	ff = new FilterForm(localizer);
		
		ff.setPreferredSize(new Dimension(640, 480));
		new JLocalizedOptionPane(localizer).confirm(this, ff, TITLE_FILTER_FORM, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
	}
}
