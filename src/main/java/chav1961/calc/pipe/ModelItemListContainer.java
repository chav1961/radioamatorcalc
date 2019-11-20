package chav1961.calc.pipe;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.io.IOException;
import java.util.Locale;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import chav1961.calc.pipe.ModelContentChangeListener.ChangeType;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.concurrent.LightWeightListenerList;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public class ModelItemListContainer extends JList<LocaleChangeListener> implements LocaleChangeListener {
	private static final long 	serialVersionUID = 1L;
	private static final Icon	REMOVE_ICON = null;

	private final LightWeightListenerList<ModelContentChangeListener>	listeners = new LightWeightListenerList<>(ModelContentChangeListener.class);
	
	
	public ModelItemListContainer() throws LocalizationException {
        setDropMode(DropMode.INSERT);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setTransferHandler(new DropModelHandler());
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
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
	
	public void placeContent(final ContentNodeMetadata metadata) {
		if (metadata == null) {
			throw new NullPointerException("Element to add can't be null");
		}
		else {
			((DefaultListModel<LocaleChangeListener>)getModel()).add(getComponentCount()-1,new KeepContent(metadata));
			listeners.fireEvent((e)->e.contentChangePerormed(ChangeType.INSERTED, null,metadata));
		}
	}

	public void removeContent(final ContentNodeMetadata metadata) {
		if (metadata == null) {
			throw new NullPointerException("Element to remove can't be null");
		}
		else {
			for (int index = 0, maxIndex = getComponentCount(); index < maxIndex; index++) {
				if (((KeepContent)getComponent(index)).metadata.equals(metadata)) {
					remove(index);
					listeners.fireEvent((e)->e.contentChangePerormed(ChangeType.REMOVED, null,metadata));
					return;
				}
			}
		}
	}
	
	public ContentNodeMetadata[] getContent() {
		final ContentNodeMetadata[]	result = new ContentNodeMetadata[getComponentCount()-1];
		
		for (int index = 0, maxIndex = result.length; index < maxIndex; index++) {
			result[index] = ((KeepContent)getComponent(index)).metadata;			
		}
		return result;
	}
	
	private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		for (Component item : getComponents()) {
			if (item instanceof LocaleChangeListener) {
				((LocaleChangeListener)item).localeChanged(oldLocale, newLocale);
			}
		}
	}

	private class KeepContent extends JPanel implements LocaleChangeListener {
		private static final long 			serialVersionUID = 1L;
		
		private final ContentNodeMetadata 	metadata;
		private final JLabel				label = new JLabel();
		private final JTextField			field = new JTextField();
		private final JButton				button = new JButton(REMOVE_ICON);
		
		KeepContent(final ContentNodeMetadata metadata) {
			super(new BorderLayout());
			this.metadata = metadata;
			this.field.setEnabled(false);			
			this.button.setBorderPainted(false);
			this.button.setFocusPainted(false);
			this.button.setContentAreaFilled(false);
			this.button.addActionListener((event)->{
				removeContent(metadata);
				listeners.fireEvent((e)->{
					e.contentChangePerormed(ModelContentChangeListener.ChangeType.REMOVED,null,metadata);
				});
			});
			final JPanel	centerPanel = new JPanel(new GridLayout(1,2));
			
			centerPanel.add(this.label);
			centerPanel.add(this.field);
			add(centerPanel,BorderLayout.CENTER);
			add(button,BorderLayout.EAST);
		}

		@Override
		public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
			fillLocalizedStrings(oldLocale,newLocale);
		}

		private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
			try{label.setText(LocalizerFactory.getLocalizer(metadata.getLocalizerAssociated()).getValue(metadata.getLabelId()));
			} catch (IOException e) {
				label.setText(metadata.getLabelId());
			}
			field.setText(metadata.getName());
			if (metadata.getTooltipId() != null) {
				try{field.setToolTipText(LocalizerFactory.getLocalizer(metadata.getLocalizerAssociated()).getValue(metadata.getTooltipId()));
				} catch (IOException e) {
					label.setText(metadata.getLabelId());
				}
			}
		}
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
	            	
	            	((DefaultListModel)getModel()).add(index, new KeepContent(metaData));
					listeners.fireEvent((e)->e.contentChangePerormed(ChangeType.INSERTED,support.getComponent(),metaData));
	            	return true;
	            } catch (UnsupportedFlavorException | IOException e) {
	            	return false;
				}
            }
        }
    }
}
