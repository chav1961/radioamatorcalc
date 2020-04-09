package chav1961.calc.utils;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Locale;

import chav1961.calc.interfaces.DragMode;
import chav1961.calc.interfaces.PipeContainerInterface;
import chav1961.calc.interfaces.PipeContainerItemInterface;
import chav1961.calc.windows.PipeManager;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.ui.swing.useful.CursorsLibrary;

public abstract class PipePluginFrame<T> extends InnerFrame<T> implements PipeContainerInterface {
	private static final long serialVersionUID = 1L;

	private final PipeManager	parent;
	private final Localizer		localizer;
	private final PipeItemType	itemType;
	
	public PipePluginFrame(final PipeManager parent, final Localizer localizer, final Class<T> classInstance, final PipeItemType itemType) throws ContentException {
		super(classInstance);
		if (parent == null) {
			throw new NullPointerException("Parent manager can't be null");
		}
		else if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (itemType == null) {
			throw new NullPointerException("Item type can't be null");
		}
		else {
			this.parent = parent;
			this.localizer = localizer;
			this.itemType = itemType;
		}
	}

	@Override
	public PipeItemType getType() {
		return itemType;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Iterable<PipeContainerItemInterface> getItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasComponentAt(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PipeContainerItemInterface at(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		// TODO Auto-generated method stub
		
	}
	
	protected PipeManager getParentManager() {
		return parent;
	}
	
	protected void assignDndLink(final Component component) {
		component.addMouseListener(new MouseListener() {
			private Cursor		oldCursor;
			private DragMode	oldDragMode;
			
			@Override public void mouseReleased(MouseEvent e) {}
			@Override public void mousePressed(MouseEvent e) {}
			@Override public void mouseClicked(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {
				parent.setDragMode(oldDragMode);
//				component.setCursor(oldCursor);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				oldCursor = component.getCursor();
				oldDragMode = parent.setDragMode(DragMode.LINKS);
//				component.setCursor(CursorsLibrary.DRAG_HAND);
			}
			
		});
	}

	protected void assignDndComponent(final Component component) {
		component.addMouseListener(new MouseListener() {
			private Cursor		oldCursor;
			private DragMode	oldDragMode;
			
			@Override public void mouseReleased(MouseEvent e) {}
			@Override public void mousePressed(MouseEvent e) {}
			@Override public void mouseClicked(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {
				parent.setDragMode(oldDragMode);
//				component.setCursor(oldCursor);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				oldCursor = component.getCursor();
				oldDragMode = parent.setDragMode(DragMode.CONTROLS);
//				component.setCursor(CursorsLibrary.DRAG_HAND);
			}
		});
	}
}
