package chav1961.calc.pipe;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
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
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.ui.swing.SwingUtils;

public class PipePluginFrame extends JInternalFrame implements LocaleChangeListener {
	private static final long 		serialVersionUID = 1L;
    private static final int 		X_OFFSET = 30, Y_OFFSET = 30, OFFSET_REPEAT = 15;
	
    private static int 				openFrameCount = 0;
	
    private final Localizer			localizer;
    private final int				windowId;
    
    public PipePluginFrame(final Localizer localizer, final JComponent inside) throws ContentException {
        super("", true, true, true, true);
        
        if (inside == null) {
        	throw new NullPointerException("Instance to show can't be null"); 
        }
        else if (!inside.getClass().isAnnotationPresent(PluginProperties.class)) {
        	throw new IllegalArgumentException("Instance passed must be annotated with @"+PluginProperties.class.getCanonicalName()); 
        }
        else {
        	final PluginProperties	pp = inside.getClass().getAnnotation(PluginProperties.class);
        	
        	this.localizer = localizer;
        	this.windowId = ++openFrameCount;
			try{fillLocalizedStrings(localizer.currentLocale().getLocale(),localizer.currentLocale().getLocale());

	        	getContentPane().add(inside);
				setSize(pp.width(),pp.height());
				
	        	setLocation(X_OFFSET*(openFrameCount % OFFSET_REPEAT), Y_OFFSET*(openFrameCount % OFFSET_REPEAT));
	        	setResizable(pp.resizable());
	
	        	if (!pp.pluginIconURI().isEmpty()) {
	        		setFrameIcon(new ImageIcon(inside.getClass().getResource(pp.pluginIconURI())));
	        	}
	        	if (!pp.desktopIconURI().isEmpty()) {
	        		getDesktopIcon().setUI(new SimpleDesktopIconUI(getDesktopIcon(),new ImageIcon(inside.getClass().getResource(pp.desktopIconURI()))));
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
						if (inside instanceof AutoCloseable) {
							try{
								((AutoCloseable)inside).close();
							} catch (Exception e1) {
							}
						}
					}
				});
	        	
			} catch (LocalizationException exc) {
				throw new ContentException(exc);
			}
        }
    }

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings(oldLocale, newLocale);
	}

	private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) throws LocalizationException, IllegalArgumentException {
//		setTitle("#"+windowId+": "+abf.getLocalizerAssociated().getValue(abf.getContentModel().getRoot().getLabelId()));
//		w.localeChanged(oldLocale, newLocale);
	}

	private void showHelp(final String helpId) {
		final GrowableCharArray	gca = new GrowableCharArray(false);
		
		try{gca.append(localizer.getContent(helpId));
			final byte[]	content = Base64.getEncoder().encode(new String(gca.extract()).getBytes());
			
			SwingUtils.showCreoleHelpWindow(this,URI.create("self:/#"+new String(content,0,content.length)));
		} catch (LocalizationException | NullPointerException | IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void refresh() {
//		w.refresh();
	}
	
	private static class SimpleDesktopIconUI extends BasicDesktopIconUI {
		private final JDesktopIcon	desktopIcon;
		private final Icon 			icon;
		private JInternalFrame		frame;

		private SimpleDesktopIconUI(final JDesktopIcon desktopIcon, final Icon icon) {
	        this.desktopIcon = desktopIcon;
			this.icon = icon;
	    }

	    @Override
	    protected void installComponents() {
	        frame = desktopIcon.getInternalFrame();
	        
	        final String title = frame.getTitle();
	        final JLabel label = new JLabel(title, icon, SwingConstants.CENTER);
	        
	        label.setVerticalTextPosition(JLabel.BOTTOM);
	        label.setHorizontalTextPosition(JLabel.CENTER);

	        desktopIcon.setBorder(null);
	        desktopIcon.setOpaque(false);
	        desktopIcon.setLayout(new GridLayout(1, 1));
	        desktopIcon.add(label);
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
