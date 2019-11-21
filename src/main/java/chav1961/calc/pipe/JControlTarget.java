package chav1961.calc.pipe;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.model.interfaces.NodeMetadataOwner;

public class JControlTarget extends JControlLabel {
	private static final long 	serialVersionUID = 1L;
	private static final Icon	TARGET_CONTROL_ICON = new ImageIcon(JControlTarget.class.getResource("targetControl.png"));

	public JControlTarget(final ContentNodeMetadata metadata) {
		super(TARGET_CONTROL_ICON,metadata);
	}
}
