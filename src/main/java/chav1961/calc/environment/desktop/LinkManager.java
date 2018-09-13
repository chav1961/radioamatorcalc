package chav1961.calc.environment.desktop;

import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;

import chav1961.purelib.ui.AbstractLowLevelFormFactory.FieldDescriptor;

public class LinkManager extends JComponent {
	private static final long serialVersionUID = -4993591710394857349L;

	public LinkManager(final JDesktopPane parent) {
		parent.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(final ComponentEvent e) {
				resizeComponent(e.getComponent().getBounds());
			}
			
			@Override
			public void componentResized(final ComponentEvent e) {
				resizeComponent(e.getComponent().getBounds());
			}
			
			@Override
			public void componentMoved(final ComponentEvent e) {
				resizeComponent(e.getComponent().getBounds());
			}
			
			@Override
			public void componentHidden(final ComponentEvent e) {
				resizeComponent(e.getComponent().getBounds());
			}
		});
		parent.addContainerListener(new ContainerListener() {
			@Override
			public void componentRemoved(final ContainerEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentAdded(final ContainerEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		setOpaque(false);
		resizeComponent(parent.getBounds());
	}

	public void createDataLink(final JComponent from, final FieldDescriptor fromDesc, final JComponent to, final FieldDescriptor toDesc) {
		if (from == null) {
			throw new NullPointerException("From component can't be null");
		}
		else if (fromDesc == null) {
			throw new NullPointerException("From field descriptor can't be null");
		}
		else if (to == null) {
			throw new NullPointerException("From component can't be null");
		}
		else if (fromDesc == null) {
			throw new NullPointerException("From field descriptor can't be null");
		}
	}

	public void createControlLink(final JComponent from, final FieldDescriptor fromDesc, final JComponent to, final FieldDescriptor toDesc) {
		
	}
	
	public void removeDataLink(final JComponent from, final JComponent to) {
		
	}

	public void removeControlLink(final JComponent from, final JComponent to) {
		
	}
	
	private void resizeComponent(final Rectangle bounds) {
		setBounds(bounds);
		paintImmediately(bounds);
	}
}
