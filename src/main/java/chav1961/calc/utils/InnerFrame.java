package chav1961.calc.utils;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.plaf.basic.BasicDesktopIconUI;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.growablearrays.GrowableCharArray;
import chav1961.purelib.enumerations.ContinueMode;
import chav1961.purelib.enumerations.NodeEnterMode;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.ui.swing.SwingUtils;

public abstract class InnerFrame<T> extends JInternalFrame implements LocaleChangeListener {
	private static final long 			serialVersionUID = 1L;
    private static final int 			X_OFFSET = 30, Y_OFFSET = 30, OFFSET_REPEAT = 15;
	
    private static int 					openFrameCount = 0;
	
    private final int					windowId;
    private final SimpleDesktopIconUI 	iconUI;

	public InnerFrame(final T instance) throws ContentException {
        super("", true, true, true, true);
        
        if (instance == null) {
        	throw new NullPointerException("Instance to show can't be null"); 
        }
        else if (!instance.getClass().isAnnotationPresent(PluginProperties.class) && !this.getClass().isAnnotationPresent(PluginProperties.class)) {
        	throw new IllegalArgumentException("Instance passed must be annotated with @"+PluginProperties.class.getCanonicalName()); 
        }
        else {
        	final Object 			pluginPropKeeper = instance.getClass().isAnnotationPresent(PluginProperties.class) ? instance : this;
        	final PluginProperties	pp = pluginPropKeeper.getClass().getAnnotation(PluginProperties.class);
        	
        	this.windowId = ++openFrameCount;
			setSize(pp.width(),pp.height());
			
        	setLocation(X_OFFSET*(openFrameCount % OFFSET_REPEAT), Y_OFFSET*(openFrameCount % OFFSET_REPEAT));
        	setResizable(pp.resizable());

        	if (!pp.pluginIconURI().isEmpty()) {
        		setFrameIcon(new ImageIcon(pluginPropKeeper.getClass().getResource(pp.pluginIconURI())));
        	}
        	if (!pp.desktopIconURI().isEmpty()) {
        		getDesktopIcon().setUI(this.iconUI = new SimpleDesktopIconUI(getDesktopIcon(),new ImageIcon(pluginPropKeeper.getClass().getResource(pp.desktopIconURI()))));
        		setIconifiable(true);
        	}
        	else {
        		this.iconUI = null;
        		setIconifiable(false);
        	}

        	addInternalFrameListener(new InternalFrameListener() {
				@Override public void internalFrameOpened(InternalFrameEvent e) {}
				@Override public void internalFrameIconified(InternalFrameEvent e) {}
				@Override public void internalFrameDeiconified(InternalFrameEvent e) {}
				@Override public void internalFrameDeactivated(InternalFrameEvent e) {}
				@Override public void internalFrameActivated(InternalFrameEvent e) {}
				@Override public void internalFrameClosing(InternalFrameEvent e) {}
				
				@Override
				public void internalFrameClosed(InternalFrameEvent e) {
					SwingUtils.walkDown(InnerFrame.this, (mode,node)->{
						if (mode == NodeEnterMode.EXIT && (node instanceof AutoCloseable)) {
							try{((AutoCloseable)node).close();
							} catch (Exception exc) {
							}
						}
						return ContinueMode.CONTINUE;
					});

				}
			});
        }
	}
	
	@Override
	public abstract void localeChanged(Locale oldLocale, Locale newLocale) throws LocalizationException;

	@Override
	public void setTitle(final String title) {
		super.setTitle(title);
		if (iconUI != null && iconUI.label != null) {
			iconUI.label.setText(title == null ? "" : title);
		}
	}

	@Override
	public void setToolTipText(String text) {
		super.setToolTipText(text);
		getDesktopIcon().setToolTipText(text == null ? "" : text);
	}
	
	protected int getWindowId() {
		return windowId;
	}

	private static class SimpleDesktopIconUI extends BasicDesktopIconUI {
		private final JDesktopIcon	desktopIcon;
		private final Icon 			icon;
		private JInternalFrame		frame;
		private JLabel 				label;
		private Point				pointPressed;
		private boolean				pressedNow = false;

		private SimpleDesktopIconUI(final JDesktopIcon desktopIcon, final Icon icon) {
	        this.desktopIcon = desktopIcon;
	        this.desktopIcon.setFocusable(true);
			this.icon = icon;
	    }

	    @Override
	    protected void installComponents() {
	        frame = desktopIcon.getInternalFrame();
	        
	        final String title = frame.getTitle();
	        label = new JLabel(title, icon, SwingConstants.CENTER);
	        
	        label.setFocusable(true);
	        label.setVerticalTextPosition(JLabel.BOTTOM);
	        label.setHorizontalTextPosition(JLabel.CENTER);

	        desktopIcon.setBorder(null);
	        desktopIcon.setOpaque(false);
	        desktopIcon.setLayout(new GridLayout(1, 1));
	        desktopIcon.add(label);
	        
	        label.addMouseListener(new MouseListener() {
				@Override public void mouseExited(MouseEvent e) {}
				@Override public void mouseEntered(MouseEvent e) {}
				
				@Override 
				public void mouseReleased(MouseEvent e) {
					pressedNow = false;
				}
				
				@Override 
				public void mousePressed(MouseEvent e) {
					pointPressed = e.getPoint();
					pressedNow = true;
				}
				
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() > 1) {
						try{frame.setIcon(false);
						} catch (PropertyVetoException e1) {
						}
					}
				}
			});
	        label.addMouseMotionListener(new MouseMotionListener() {
				@Override public void mouseMoved(MouseEvent e) {}
				
				@Override
				public void mouseDragged(MouseEvent e) {
					final Point	currentLocation = desktopIcon.getLocation();
					final int	deltaX = e.getPoint().x - pointPressed.x;
					final int	deltaY = e.getPoint().y - pointPressed.y;
					
					currentLocation.translate(deltaX,deltaY);
					desktopIcon.setLocation(currentLocation);
				}
			});
	    }

	    @Override
	    protected void uninstallComponents() {
	        desktopIcon.setLayout(null);
	        desktopIcon.removeAll();
	        frame = null;
	    }

	    @Override
	    public Dimension getMinimumSize(JComponent c) {
	        final LayoutManager 	layout = desktopIcon.getLayout();
	        final Dimension 		size = layout.minimumLayoutSize(desktopIcon);
	        
	        return new Dimension(size.width + 15, size.height + 15);
	    }

	    @Override
	    public Dimension getPreferredSize(JComponent c) {
	        return getMinimumSize(c);
	    }

	    @Override
	    public Dimension getMaximumSize(JComponent c) {
	        return getMinimumSize(c);
	    }
	}
}
