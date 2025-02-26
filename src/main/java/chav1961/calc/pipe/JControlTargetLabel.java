package chav1961.calc.pipe;

import java.awt.Cursor;

import javax.swing.Icon;

import chav1961.calc.interfaces.MetadataTarget;
import chav1961.calc.interfaces.PipeContainerInterface;
import chav1961.calc.pipe.ModelContentChangeListener.ChangeType;
import chav1961.calc.utils.PipeLink;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.concurrent.LightWeightListenerList;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public class JControlTargetLabel extends JControlLabel implements MetadataTarget {
	private static final long serialVersionUID = -1833654248724452219L;
	
	private final LightWeightListenerList<ModelContentChangeListener>	listeners = new LightWeightListenerList<>(ModelContentChangeListener.class);

	public JControlTargetLabel(final Icon icon, final ContentNodeMetadata metadata, final PipeContainerInterface owner) throws ContentException {
		super(icon, metadata, owner);
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public void drop(final PipeLink link, final int xFrom, final int yFrom, final int xTo, final int yTo) {
		// TODO Auto-generated method stub
		addContent(link);
	}
	
	public void addContentChangeListener(final ModelContentChangeListener listener) {
		if (listener == null) {
			throw new NullPointerException("Listener to add can't be null");
		}
		else {
			listeners.addListener(listener);
		}
	}

	public void removeContentChangeListener(final ModelContentChangeListener listener) {
		if (listener == null) {
			throw new NullPointerException("Listener to remove can't be null");
		}
		else {
			listeners.removeListener(listener);
		}
	}
	
	public void addContent(final PipeLink metadata) {
		if (metadata == null) {
			throw new NullPointerException("Element to add can't be null");
		}
		else {
			listeners.fireEvent((e)->e.contentChangePerformed(ChangeType.INSERTED, null,metadata));
		}
	}

	public void changeContent(final PipeLink metadata) {
		if (metadata == null) {
			throw new NullPointerException("Element to add can't be null");
		}
		else {
			listeners.fireEvent((e)->e.contentChangePerformed(ChangeType.CHANGED, null,metadata));
		}
	}
	
	public void removeContent(final PipeLink metadata) {
		if (metadata == null) {
			throw new NullPointerException("Element to remove can't be null");
		}
		else {
			listeners.fireEvent((e)->e.contentChangePerformed(ChangeType.REMOVED, null,metadata));
		}
	}
}
