package chav1961.calc.references.tubes;

import java.io.IOException;

import javax.swing.JSplitPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.i18n.interfaces.Localizer;

public class TubesScreen extends JSplitPane {
	private static final long serialVersionUID = 5215087600297617153L;

	private final Localizer	localizer;
	
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
				
				final TubesPreview	tp = new TubesPreview(localizer);
				setLeftComponent(tp);
				final TubesTabs		tt = new TubesTabs(localizer, (t)->{}, item);
				setRightComponent(tt);
			} catch (ParserConfigurationException | SAXException e) {
				throw new IOException(e.getLocalizedMessage(), e);
			}
			
		}
		
		
	}
}
