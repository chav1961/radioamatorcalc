package chav1961.calc.pipe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Locale;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.border.LineBorder;

import chav1961.calc.interfaces.MetadataTarget;
import chav1961.calc.pipe.ModelContentChangeListener.ChangeType;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.concurrent.LightWeightListenerList;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.interfaces.NodeMetadataOwner;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public class ModelItemListContainer extends JList<ContentNodeMetadata> implements LocaleChangeListener, NodeMetadataOwner, MetadataTarget {
	private static final long 	serialVersionUID = 1L;
	private static final Icon	REMOVE_ICON = null;

	private final LightWeightListenerList<ModelContentChangeListener>	listeners = new LightWeightListenerList<>(ModelContentChangeListener.class);
	private final Localizer		localizer;
	
	public ModelItemListContainer(final Localizer localizer, final boolean useAsDropTarget) throws LocalizationException {
		this.localizer = localizer;
        setDropMode(DropMode.INSERT);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setModel(new DefaultListModel<ContentNodeMetadata>());
        if (useAsDropTarget) {
        	setTransferHandler(new DropModelHandler());
        }
        setCellRenderer((list,value,index,isSelected,cellHasFocus)->{
        		final ContentNodeMetadata	meta = (ContentNodeMetadata)value; 
        		final JLabel	label = new JLabel();
        		
        		label.setOpaque(true);
        		label.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
        		label.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
        		if (cellHasFocus) {
        			label.setBorder(new LineBorder(Color.BLUE));
        		}
        		try{label.setText(meta.getName()+": "+localizer.getValue(meta.getLabelId())+" ("+meta.getType().getCanonicalName()+")");
        			if (meta.getTooltipId() != null && !meta.getTooltipId().isEmpty()) {
        				label.setToolTipText(localizer.getValue(meta.getTooltipId()));
        			}
        			if (meta.getIcon() != null) {
        				label.setIcon(new ImageIcon(meta.getIcon().toURL()));
        			}
				} catch (LocalizationException | MalformedURLException e) {
					label.setText(e.getLocalizedMessage());
				}
        		return label;
			}
		);
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		updateUI();
	}

	@Override
	public ContentNodeMetadata getNodeMetadata() {
		return isSelectionEmpty() ? (getModel().getSize() == 0 ? null : getModel().getElementAt(0)) : getSelectedValue();
	}

	@Override
	public void drop(final ContentNodeMetadata meta, final Component from, final int xFrom, final int yFrom, final int xTo, final int yTo) {
		addContent(meta);
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
	
	public void addContent(final ContentNodeMetadata metadata) {
		if (metadata == null) {
			throw new NullPointerException("Element to add can't be null");
		}
		else {
			((DefaultListModel<ContentNodeMetadata>)getModel()).add(getComponentCount()-1,metadata);
			listeners.fireEvent((e)->e.contentChangePerformed(ChangeType.INSERTED, null,metadata));
		}
	}

	public void changeContent(final ContentNodeMetadata metadata) {
		if (metadata == null) {
			throw new NullPointerException("Element to add can't be null");
		}
		else {
			((DefaultListModel<ContentNodeMetadata>)getModel()).set(getSelectedIndex(),metadata);
			listeners.fireEvent((e)->e.contentChangePerformed(ChangeType.CHANGED, null,metadata));
		}
	}
	
	public void removeContent(final ContentNodeMetadata metadata) {
		if (metadata == null) {
			throw new NullPointerException("Element to remove can't be null");
		}
		else {
			for (int index = 0, maxIndex = getModel().getSize(); index < maxIndex; index++) {
				if (getModel().getElementAt(index).equals(metadata)) {
					((DefaultListModel<ContentNodeMetadata>)getModel()).remove(index);
					listeners.fireEvent((e)->e.contentChangePerformed(ChangeType.REMOVED, null,metadata));
					return;
				}
			}
		}
	}
	
	public ContentNodeMetadata[] getContent() {
		final ContentNodeMetadata[]	result = new ContentNodeMetadata[getModel().getSize()];
		
		for (int index = 0, maxIndex = result.length; index < maxIndex; index++) {
			result[index] = getModel().getElementAt(index);			
		}
		return result;
	}
	
    private class DropModelHandler extends TransferHandler {
		private static final long 	serialVersionUID = 1L;

		@Override
        public boolean canImport(final TransferSupport support) {
            if (!support.isDrop()) {
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
	            	final JList.DropLocation 	dl = (JList.DropLocation) support.getDropLocation();
	            	final int 					index = dl.getIndex();
	            	
	            	((DefaultListModel)getModel()).add(index, metaData);
					listeners.fireEvent((e)->e.contentChangePerformed(ChangeType.INSERTED,support.getComponent(),metaData));
	            	return true;
	            } catch (UnsupportedFlavorException | IOException e) {
	            	return false;
				}
            }
        }
    }
}
