package chav1961.calc.pipe;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.model.interfaces.NodeMetadataOwner;

public class JControlSource extends JControlLabel {
	private static final long 	serialVersionUID = 1L;
	private static final Icon	SOURCE_CONTROL_ICON = new ImageIcon(JControlSource.class.getResource("sourceControl.png"));

	public JControlSource(final ContentNodeMetadata metadata) throws ContentException {
		super(SOURCE_CONTROL_ICON,metadata);
	}
}
