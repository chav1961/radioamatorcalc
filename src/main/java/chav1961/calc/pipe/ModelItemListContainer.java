package chav1961.calc.pipe;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.net.MalformedURLException;
import java.util.Locale;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;

import chav1961.calc.interfaces.MetadataTarget;
import chav1961.calc.interfaces.PipeContainerInterface;
import chav1961.calc.pipe.ModelContentChangeListener.ChangeType;
import chav1961.calc.utils.PipeLink;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.concurrent.LightWeightListenerList;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.ui.swing.useful.JLocalizedOptionPane;
import chav1961.purelib.model.interfaces.NodeMetadataOwner;

public class ModelItemListContainer extends JList<PipeLink> implements LocaleChangeListener, NodeMetadataOwner, MetadataTarget {
	private static final long 	serialVersionUID = 1L;
	
	private static final String	TOOLTIP_INSERTED_FROM = "chav1961.calc.pipe.modelItemListContainer.tooltip.insertedFrom";
	private static final String	TOOLTIP_LINKED_WITH = "chav1961.calc.pipe.modelItemListContainer.tooltip.linkedWith";

	private static final String	MESSAGE_NO_COMPONENT_TITLE = "chav1961.calc.pipe.modelItemListContainer.noComponent.caption";
	private static final String	MESSAGE_NO_COMPONENT_MESSAGE = "chav1961.calc.pipe.modelItemListContainer.noComponent.message";
	private static final String	MESSAGE_REPLACE_LINK_TITLE = "chav1961.calc.pipe.modelItemListContainer.replaceLink.caption";
	private static final String	MESSAGE_REPLACE_LINK_QUESTION = "chav1961.calc.pipe.modelItemListContainer.replaceLink.message";
	
	private static final Icon	ICON_DRAGGED = new ImageIcon(ModelItemListContainer.class.getResource("dragged.png"));
	private static final Icon	ICON_LOCK = new ImageIcon(ModelItemListContainer.class.getResource("lockIcon.png"));
	private static final Icon	ICON_LINK = new ImageIcon(ModelItemListContainer.class.getResource("linkIcon.png"));
	private static final Icon	ICON_MAYBE_LINKED = new ImageIcon(ModelItemListContainer.class.getResource("mayBeLinked.png"));

	
	public enum DropAction {
		INSERT, LINK, NONE
	}
	
	private final LightWeightListenerList<ModelContentChangeListener>	listeners = new LightWeightListenerList<>(ModelContentChangeListener.class);
	private final Localizer					localizer;
	private final PipeContainerInterface	owner;
	private final DropAction				action; 
	
	public ModelItemListContainer(final Localizer localizer, final PipeContainerInterface owner, final DropAction action) throws LocalizationException {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (owner == null) {
			throw new NullPointerException("Control owner can't be null");
		}
		else if (action == null) {
			throw new NullPointerException("Drop action can't be null");
		}
		else {
			this.localizer = localizer;
			this.owner = owner;
			this.action = action;
			
	        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        setModel(new DefaultListModel<PipeLink>());
	        
	        switch (action) {
				case INSERT	:
					setCursor(new Cursor(Cursor.HAND_CURSOR));
					break;
				case LINK	:
					setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
					break;
				case NONE	:
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					break;
				default		: throw new UnsupportedOperationException("Drop action ["+action+"] is not supported yet"); 
	        }
	        
	        setCellRenderer((list,value,index,isSelected,cellHasFocus)->{
	        		final ContentNodeMetadata	meta = ((PipeLink)value).getMetadata(); 
	        		final JLabel				label = new JLabel();
	        		
	        		label.setOpaque(true);
	        		label.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
	        		label.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
	        		if (cellHasFocus) {
	        			label.setBorder(new LineBorder(Color.BLUE));
	        		}
	        		try{label.setText(meta.getName()+": '"+localizer.getValue(meta.getLabelId())+"' ("+meta.getType().getCanonicalName()+")");
	        			switch (action) {
		    				case INSERT	:
		        				label.setIcon(ICON_DRAGGED);
		        				if (((PipeLink)value).getSource() != null) {
		        					label.setToolTipText(localizer.getValue(TOOLTIP_INSERTED_FROM,((PipeLink)value).getSource().getPipeItemName()));
		        				}
		    					break;
		    				case LINK	:
		        				label.setIcon(((PipeLink)value).getSource() != null ? ICON_LINK : ICON_MAYBE_LINKED);
		        				if (((PipeLink)value).getSource() != null) {
				        			label.setToolTipText(localizer.getValue(TOOLTIP_LINKED_WITH,((PipeLink)value).getSource().getPipeItemName(),((PipeLink)value).getAssociatedMeta().getName()));
		        				}
		    					break;
		    				case NONE	:
		        				label.setIcon(ICON_LOCK);
		    					break;
		    				default		: throw new UnsupportedOperationException("Drop action ["+action+"] is not supported yet"); 
	        			}
					} catch (LocalizationException e) {
						label.setText(e.getLocalizedMessage());
					}
	        		
	        		return label;
				}
			);
		}
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		updateUI();
	}

	@Override
	public ContentNodeMetadata getNodeMetadata() {
		return isSelectionEmpty() ? (getModel().getSize() == 0 ? null : getModel().getElementAt(0).getMetadata()) : getSelectedValue().getMetadata();
	}

	@Override
	public ContentNodeMetadata getNodeMetadata(final int x, final int y) {
		final int	index = locationToIndex(new Point(x,y));

		if (index < 0) {
			return null;
		}
		else {
			return getModel().getElementAt(index).getMetadata();
		}
	}
	
	@Override
	public void drop(final PipeLink link, final int xFrom, final int yFrom, final int xTo, final int yTo) {
		try{switch (action) {
				case INSERT	:
					addContent(link);
					break;
				case LINK	:
					final int index = locationToIndex(new Point(xTo,yTo));
					
					if (index < 0) {
						new JLocalizedOptionPane(localizer).message(this,MESSAGE_NO_COMPONENT_MESSAGE,MESSAGE_NO_COMPONENT_TITLE,JOptionPane.WARNING_MESSAGE);
					}
					else {
						final PipeLink	existentLink = ((DefaultListModel<PipeLink>)getModel()).getElementAt(index);
						
						if (existentLink.getSource() != null && new JLocalizedOptionPane(localizer).confirm(this,MESSAGE_REPLACE_LINK_QUESTION,MESSAGE_REPLACE_LINK_TITLE,JOptionPane.QUESTION_MESSAGE,JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
							break;
						}
						changeContent(index,new PipeLink(existentLink.getType(),link.getSource(),link.getSourcePoint(),existentLink.getTarget(),existentLink.getTargetPoint(),existentLink.getMetadata(),link.getMetadata()));
					}
					break;
				default		: throw new UnsupportedOperationException("Drop action ["+action+"] is not supported yet"); 
	        }
		} catch (LocalizationException e) {
			e.printStackTrace();
		}
	}

	public void deserialize(final PipeLink link, final int xFrom, final int yFrom, final int xTo, final int yTo) {
		final int 		index = locationToIndex(new Point(xTo,yTo));
		final PipeLink	existentLink = ((DefaultListModel<PipeLink>)getModel()).getElementAt(index);

		changeContent(index,new PipeLink(existentLink.getType(),link.getSource(),link.getSourcePoint(),existentLink.getTarget(),existentLink.getTargetPoint(),existentLink.getMetadata(),link.getMetadata()));
	}	
	
	public DropAction getDropAction() {
		return action;
	}

	public PipeContainerInterface getOwner() {
		return owner;
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
			((DefaultListModel<PipeLink>)getModel()).addElement(metadata);
			listeners.fireEvent((e)->e.contentChangePerformed(ChangeType.INSERTED, null,metadata));
		}
	}

	public void changeContent(final int index, final PipeLink metadata) {
		if (metadata == null) {
			throw new NullPointerException("Element to add can't be null");
		}
		else {
			((DefaultListModel<PipeLink>)getModel()).setElementAt(metadata,index);
			listeners.fireEvent((e)->e.contentChangePerformed(ChangeType.CHANGED, null,metadata));
		}
	}
	
	public void removeContent(final PipeLink metadata) {
		if (metadata == null) {
			throw new NullPointerException("Element to remove can't be null");
		}
		else {
			for (int index = 0, maxIndex = getModel().getSize(); index < maxIndex; index++) {
				if (getModel().getElementAt(index).getMetadata().equals(metadata.getMetadata())) {
					((DefaultListModel<PipeLink>)getModel()).remove(index);
					listeners.fireEvent((e)->e.contentChangePerformed(ChangeType.REMOVED, null,metadata));
					return;
				}
			}
		}
	}
	
	public PipeLink[] getContent() {
		final PipeLink[]	result = new PipeLink[getModel().getSize()];
		
		for (int index = 0, maxIndex = result.length; index < maxIndex; index++) {
			result[index] = getModel().getElementAt(index);			
		}
		return result;
	}
	
//    private class DropModelHandler extends TransferHandler {
//		private static final long 	serialVersionUID = 1L;
//
//		@Override
//        public boolean canImport(final TransferSupport support) {
//            if (!support.isDrop()) {
//                return false;
//            }
//            else {
//                return support.isDataFlavorSupported(PureLibSettings.MODEL_DATA_FLAVOR);
//            }
//        }
//
//    	@Override
//        public boolean importData(final TransferSupport support) {
//            if (!canImport(support)) {
//                return false;
//            }
//            else {
//	            try{
//	                final Transferable 			transferable = support.getTransferable();
//	            	final ContentNodeMetadata	metaData = (ContentNodeMetadata) transferable.getTransferData(PureLibSettings.MODEL_DATA_FLAVOR);
//	            	final JList.DropLocation 	dl = (JList.DropLocation) support.getDropLocation();
//	            	final int 					index = dl.getIndex();
//	            	
//	            	((DefaultListModel)getModel()).add(index, metaData);
//					listeners.fireEvent((e)->e.contentChangePerformed(ChangeType.INSERTED,support.getComponent(),metaData));
//	            	return true;
//	            } catch (UnsupportedFlavorException | IOException e) {
//	            	return false;
//				}
//            }
//        }
//    }
}
