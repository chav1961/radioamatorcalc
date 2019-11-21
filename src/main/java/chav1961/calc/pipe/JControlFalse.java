package chav1961.calc.pipe;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.model.interfaces.NodeMetadataOwner;

public class JControlFalse extends JControlLabel {
	private static final long 	serialVersionUID = 1L;
	private static final Icon	FALSE_CONTROL_ICON = new ImageIcon(JControlFalse.class.getResource("onFalseControl.png"));

	public JControlFalse(final ContentNodeMetadata metadata) {
		super(FALSE_CONTROL_ICON,metadata);
	}
}
