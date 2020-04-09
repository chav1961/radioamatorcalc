package chav1961.calc.pipe;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import chav1961.calc.interfaces.PipeContainerInterface;
import chav1961.calc.interfaces.PipeContainerItemInterface;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public class JControlTarget extends JControlTargetLabel {
	private static final long 	serialVersionUID = 1L;
	private static final Icon	TARGET_CONTROL_ICON = new ImageIcon(JControlTarget.class.getResource("targetControl.png"));

	public JControlTarget(final ContentNodeMetadata metadata, final PipeContainerInterface owner) throws ContentException {
		super(TARGET_CONTROL_ICON,metadata,owner);
	}
}
