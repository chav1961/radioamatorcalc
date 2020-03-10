package chav1961.calc.pipe;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import chav1961.calc.pipe.ModelContentChangeListener.ChangeType;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.concurrent.LightWeightListenerList;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public class ModelItemButtonContainer extends JButton implements LocaleChangeListener, DragGestureListener, Transferable {
	private static final long 				serialVersionUID = 1L;

	private final LightWeightListenerList<ModelContentChangeListener>	listeners = new LightWeightListenerList<>(ModelContentChangeListener.class);
	private final List<ContentNodeMetadata>	linked = new ArrayList<>();	
	private ContentNodeMetadata 			metadata;
	
	public ModelItemButtonContainer(final Container dragArea) {
		setTransferHandler(new DropModelHandler());
		new DragSource().createDefaultDragGestureRecognizer(dragArea, DnDConstants.ACTION_MOVE, this);
	}
	
	@Override
	public void localeChanged(Locale oldLocale, Locale newLocale) throws LocalizationException {
		fillLocalizedStrings(oldLocale,newLocale);
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
	
	public ContentNodeMetadata getOwnedModel() {
		return metadata;
	}

	public void setOwnedModel(final ContentNodeMetadata metadata) {
		if (metadata == null) {
			throw new NullPointerException("Metadata to set can't be null");
		}
		else {
			if ((this.metadata = metadata) != null) {
				
				try{final Localizer	localizer = LocalizerFactory.getLocalizer(metadata.getLocalizerAssociated());
					
					fillLocalizedStrings(localizer.currentLocale().getLocale(),localizer.currentLocale().getLocale());
				} catch (LocalizationException e) {
					setText(metadata.getLabelId());
					setToolTipText(metadata.getTooltipId() != null ? metadata.getTooltipId() : "");
				}
			}
			else {
				setText("");
				setToolTipText("");
			}
		}
	}
	
	public void link(final ContentNodeMetadata metadata) {
		if (metadata == null) {
			throw new NullPointerException("Metadata to set can't be null");
		}
		else {
			this.linked.add(metadata);
			listeners.fireEvent((e)->e.contentChangePerormed(ChangeType.INSERTED, null,metadata));
		}
	}

	public void unlink(final ContentNodeMetadata metadata) {
		if (metadata == null) {
			throw new NullPointerException("Metadata to set can't be null");
		}
		else {
			this.linked.remove(metadata);
			listeners.fireEvent((e)->e.contentChangePerormed(ChangeType.REMOVED, null,metadata));
		}
	}
	
	
	public ContentNodeMetadata[] getLinks() {
		return this.linked.toArray(new ContentNodeMetadata[this.linked.size()]);
	}
	
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] {PureLibSettings.MODEL_DATA_FLAVOR};
	}

	@Override
	public boolean isDataFlavorSupported(final DataFlavor flavor) {
		return PureLibSettings.MODEL_DATA_FLAVOR.equals(flavor);
	}

	@Override
	public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		return getOwnedModel();
	}

	@Override
	public void dragGestureRecognized(final DragGestureEvent event) {
		Cursor cursor = Cursor.getDefaultCursor();

        if (event.getDragAction() == DnDConstants.ACTION_MOVE) {
            cursor = DragSource.DefaultMoveDrop;
        }
        event.startDrag(cursor, this);	
    }
	
	private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) throws LocalizationException, IllegalArgumentException, NullPointerException {
		final ContentNodeMetadata	metadata = getOwnedModel();
		
		if (metadata != null) {
			try{setText(LocalizerFactory.getLocalizer(metadata.getLocalizerAssociated()).getValue(metadata.getLabelId()));
			} catch (LocalizationException e) {
				setText(metadata.getLabelId());
			}
			if (metadata.getTooltipId() != null) {
				try{setToolTipText(LocalizerFactory.getLocalizer(metadata.getLocalizerAssociated()).getValue(metadata.getTooltipId()));
				} catch (LocalizationException e) {
					setText(metadata.getLabelId());
				}
			}
		}
	}

    private class DropModelHandler extends TransferHandler {
		private static final long 	serialVersionUID = 1L;

		@Override
        public boolean canImport(final TransferSupport support) {
            if (!support.isDrop() || support.getDropAction() != TransferHandler.LINK) {
                return false;
            }
            else {
                return support.isDataFlavorSupported(PureLibSettings.MODEL_DATA_FLAVOR);
            }
        }

    	@Override
        public boolean importData(final TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            else {
	            try{
	                final Transferable 			transferable = support.getTransferable();
	            	final ContentNodeMetadata	metaData = (ContentNodeMetadata) transferable.getTransferData(PureLibSettings.MODEL_DATA_FLAVOR);
	            	final DropLocation 			dl = (DropLocation) support.getDropLocation();

	            	link(metadata);
	            	return true;
	            } catch (UnsupportedFlavorException | IOException e) {
	            	return false;
				}
            }
        }
    }
}
