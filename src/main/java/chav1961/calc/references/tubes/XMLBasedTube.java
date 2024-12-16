package chav1961.calc.references.tubes;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import chav1961.calc.references.interfaces.Graphic;
import chav1961.calc.references.interfaces.PinType;
import chav1961.calc.references.interfaces.TubeConnector;
import chav1961.calc.references.interfaces.TubeConnectorType;
import chav1961.calc.references.interfaces.TubeCorpusType;
import chav1961.calc.references.interfaces.TubeDescriptor;
import chav1961.calc.references.interfaces.TubePanelType;
import chav1961.calc.references.interfaces.TubeParameter;
import chav1961.calc.references.interfaces.TubesType;
import chav1961.purelib.basic.URIUtils;

/*
 * <tube type="TubesType" abbr="1a1a" corpus="TubeCorpusType" panel="TubePanelType" description="localizedKey">
 * 	 <parms>
 *   	<parm name="TubeParameter" number="0|1|2">value</parm>
 * 	 </parms>
 *   <scheme>base64Image</scheme>
 *   <connectors>
 *   	<connector number="0|1|2" type="TubeConnector.TypeConnectorType" pin="pinNumber" pinType="TubeConnector.PinType"/>
 *   </connectors>
 *   <graphics>
 *   	<graphic name="localizedKey" tooltip="localizedKey" number="0|1|2">base64Image</graphic>
 *   </graphics>
 * </tube>
 */

public class XMLBasedTube implements TubeDescriptor {
	private static final String			TAG_PARM = "parm";
	private static final String			TAG_SCHEME = "scheme";
	private static final String			TAG_CONNECTOR = "connector";
	private static final String			TAG_GRAPHIC = "graphic";
	private static final String			ATTR_TYPE = "type";
	private static final String			ATTR_ABBR = "abbr";
	private static final String			ATTR_CORPUS = "corpus";
	private static final String			ATTR_PANEL = "panel";
	private static final String			ATTR_DESCRIPTION = "description";
	private static final String			ATTR_NAME = "name";
	private static final String			ATTR_NUMBER = "number";
	private static final String			ATTR_PIN = "pin";
	private static final String			ATTR_PINTYPE = "pinType";
	private static final String			ATTR_TOOLTIP = "tooltip";

	private final TubesType				type;
	private final String				abbr;
	private final String				description;
	private final TubeCorpusType		corpus;
	private final TubePanelType			panel;
	private final Icon					scheme;
	private final TubeParmDescriptor[]	parms;
	private final TubeConnectorImpl[]	connectors;
	private final GraphicImpl[]			graphics;
	
	public XMLBasedTube(final Element root) throws IOException {
		if (root == null) {
			throw new NullPointerException("Root element can't be null");
		}
		else {
			try {
				final NodeList parms = root.getElementsByTagName(TAG_PARM);
				final NodeList scheme = root.getElementsByTagName(TAG_SCHEME);
				final NodeList connectors = root.getElementsByTagName(TAG_CONNECTOR);
				final NodeList graphics = root.getElementsByTagName(TAG_GRAPHIC);
				
				this.type = TubesType.valueOf(root.getAttribute(ATTR_TYPE));
				this.abbr = root.getAttribute(ATTR_ABBR);
				this.description = root.getAttribute(ATTR_DESCRIPTION);
				this.corpus = TubeCorpusType.valueOf(root.getAttribute(ATTR_CORPUS));
				this.panel = TubePanelType.valueOf(root.getAttribute(ATTR_PANEL));
				this.scheme = loadIcon(scheme.item(0).getTextContent().trim());
				this.parms = new TubeParmDescriptor[parms.getLength()];
				
				for(int index = 0; index < this.parms.length; index++) {
					final int			number = ((Element)parms.item(index)).hasAttribute(ATTR_NUMBER) ? Integer.valueOf(((Element)parms.item(index)).getAttribute(ATTR_NUMBER)) : 0;
					final TubeParameter	key = TubeParameter.valueOf(((Element)parms.item(index)).getAttribute(ATTR_NAME));
					final float			value = Float.valueOf(parms.item(index).getTextContent().trim());
					
					this.parms[index] = new TubeParmDescriptor(number, key, value);
				}
				
				this.graphics = new GraphicImpl[graphics.getLength()];
				
				for(int index = 0; index < this.graphics.length; index++) {
					final int			number = ((Element)graphics.item(index)).hasAttribute(ATTR_NUMBER) ? Integer.valueOf(((Element)graphics.item(index)).getAttribute(ATTR_NUMBER)) : 0;
					final String		name = ((Element)graphics.item(index)).getAttribute(ATTR_NAME);
					final String		tooltip = ((Element)graphics.item(index)).getAttribute(ATTR_TOOLTIP);
					final Icon			picture = new ImageIcon(URIUtils.convert2selfURI(graphics.item(index).getTextContent().trim().getBytes()).toURL());
					
					this.graphics[index] = new GraphicImpl(number, name, tooltip, picture);
				}

				this.connectors = new TubeConnectorImpl[connectors.getLength()];
				
				for(int index = 0; index < this.connectors.length; index++) {
					final int				number = ((Element)connectors.item(index)).hasAttribute(ATTR_NUMBER) ? Integer.valueOf(((Element)connectors.item(index)).getAttribute(ATTR_NUMBER)) : 0;
					final int				pin = Integer.valueOf(((Element)connectors.item(index)).getAttribute(ATTR_PIN));
					final TubeConnectorType	type = TubeConnectorType.valueOf(((Element)connectors.item(index)).getAttribute(ATTR_TYPE));
					final PinType			pinType = ((Element)connectors.item(index)).hasAttribute(ATTR_PINTYPE) ? PinType.valueOf(((Element)connectors.item(index)).getAttribute(ATTR_PINTYPE)) : PinType.ORDINAL;
					
					this.connectors[index] = new TubeConnectorImpl(number, type, pin, pinType);
				}
			} catch (DOMException exc) {
				throw new IOException(exc.getLocalizedMessage(), exc);
			}
		}
	}
	
	@Override
	public TubesType getType() {
		return type;
	}

	@Override
	public String getAbbr() {
		return abbr;
	}
	
	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Icon getScheme() {
		return scheme;
	}

	@Override
	public TubeConnector[] getConnectors() {
		return connectors;
	}
	
	@Override
	public TubeCorpusType getCorpus() {
		return corpus;
	}

	@Override
	public TubePanelType getPanelType() {
		return panel;
	}
	
	@Override
	public TubeParameter[] getParameters() {
		return getParameters(0);
	}

	@Override
	public float[] getValues() {
		return getValues(0);
	}

	@Override
	public TubeParameter[] getParameters(final int lampNo) {
		int	count = 0;
		
		for(TubeParmDescriptor item : parms) {
			if (item.lampNo == lampNo) {
				count++;
			}
		}
		final TubeParameter[]	result = new TubeParameter[count];
		
		count = 0;
		for(TubeParmDescriptor item : parms) {
			if (item.lampNo == lampNo) {
				result[count++] = item.parm;
			}
		}
		return result;
	}

	@Override
	public float[] getValues(final int lampNo) {
		int	count = 0;
		
		for(TubeParmDescriptor item : parms) {
			if (item.lampNo == lampNo) {
				count++;
			}
		}
		final float[]	result = new float[count];
		
		count = 0;
		for(TubeParmDescriptor item : parms) {
			if (item.lampNo == lampNo) {
				result[count++] = item.value;
			}
		}
		return result;
	}

	@Override
	public Graphic[] getGraphics() {
		return getGraphics(0);
	}

	@Override
	public Graphic[] getGraphics(final int lampNo) {
		int	count = 0;
		
		for(GraphicImpl item : graphics) {
			if (item.lampNo == lampNo) {
				count++;
			}
		}
		final Graphic[]	result = new Graphic[count];
		
		count = 0;
		for(GraphicImpl item : graphics) {
			if (item.lampNo == lampNo) {
				result[count++] = item;
			}
		}
	
		return result;
	}

	
	private Icon loadIcon(final String base64Content) {
		try {
			final byte[]		content = Base64.getDecoder().decode(base64Content);
			final BufferedImage	image = ImageIO.read(new ByteArrayInputStream(content));
			
			return new ImageIcon(image);
		} catch (IOException e) {
			return null;
		}
	}

	private static class TubeParmDescriptor {
		final int			lampNo;
		final TubeParameter	parm;
		final float			value;

		private TubeParmDescriptor(final int lampNo, final TubeParameter parm, final float value) {
			this.lampNo = lampNo;
			this.parm = parm;
			this.value = value;
		}

		@Override
		public String toString() {
			return "TubeParmDescriptor [lampNo=" + lampNo + ", parm=" + parm + ", value=" + value + "]";
		}
	}

	private static class TubeConnectorImpl implements TubeConnector {
		private final int				lampNo;
		private final TubeConnectorType type;
		private final int				pin;
		private final PinType			pinType;
		
		private TubeConnectorImpl(int lampNo, TubeConnectorType type, int pin, PinType pinType) {
			this.lampNo = lampNo;
			this.type = type;
			this.pin = pin;
			this.pinType = pinType;
		}

		@Override
		public int getLampNo() {
			return lampNo;
		}

		@Override
		public int getPin() {
			return pin;
		}

		@Override
		public PinType getPinType() {
			return pinType;
		}

		@Override
		public TubeConnectorType getType() {
			return type;
		}

		@Override
		public String toString() {
			return "TubeConnectorImpl [lampNo=" + lampNo + ", type=" + type + ", pin=" + pin + ", pinType=" + pinType + "]";
		}
	}
	
	private static class GraphicImpl implements Graphic {
		private final int		lampNo;
		private final String	title;
		private final String	tooltip;
		private final Icon		picture;

		private GraphicImpl(final int lampNo, final String title, final String tooltip, final Icon picture) {
			this.lampNo = lampNo;
			this.title = title;
			this.tooltip = tooltip;
			this.picture = picture;
		}

		@Override
		public String getTitle() {
			return title;
		}

		@Override
		public String getTooptip() {
			return tooltip;
		}

		@Override
		public Icon getPicture() {
			return picture;
		}

		@Override
		public String toString() {
			return "GraphicImpl [title=" + title + ", tooltip=" + tooltip + "]";
		}
	}
}
