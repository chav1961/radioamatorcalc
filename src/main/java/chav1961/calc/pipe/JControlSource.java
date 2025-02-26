package chav1961.calc.pipe;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import chav1961.calc.interfaces.PipeContainerInterface;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public class JControlSource extends JControlLabel {
	private static final long 	serialVersionUID = 1L;
	private static final Icon	SOURCE_CONTROL_ICON = new ImageIcon(JControlSource.class.getResource("sourceControl.png"));

	public JControlSource(final ContentNodeMetadata metadata, final PipeContainerInterface owner) throws ContentException {
		super(SOURCE_CONTROL_ICON,metadata,owner);
	}
}
