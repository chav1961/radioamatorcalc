package chav1961.calc.utils;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.growablearrays.GrowableCharArray;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.RefreshMode;
import chav1961.purelib.ui.swing.AutoBuiltForm;
import chav1961.purelib.ui.swing.SwingUtils;

public class SVGPluginFrame<T> extends JInternalFrame implements LocaleChangeListener {
	private static final long 		serialVersionUID = 1L;
    private static final int 		X_OFFSET = 30, Y_OFFSET = 30, OFFSET_REPEAT = 15;
	
    private static int 				openFrameCount = 0;
	
    private final Localizer					localizer;
    private final AutoBuiltForm<T>			abf;
    private final InnerSVGPluginWindow<T>	w;
    private final int						windowId;
    
    public SVGPluginFrame(final Localizer localizer, final T instance) throws ContentException {
        super("", true, true, true, true);
        
        if (instance == null) {
        	throw new NullPointerException("Instance to show can't be null"); 
        }
        else if (!(instance instanceof FormManager<?,?>)) {
        	throw new IllegalArgumentException("Instance passed must implements "+FormManager.class.getCanonicalName()+" interface!"); 
        }
        else if (!instance.getClass().isAnnotationPresent(PluginProperties.class)) {
        	throw new IllegalArgumentException("Instance passed must be annotated with @"+PluginProperties.class.getCanonicalName()); 
        }
        else {
        	final PluginProperties	pp = instance.getClass().getAnnotation(PluginProperties.class);
        	
        	this.localizer = localizer;
        	this.windowId = ++openFrameCount;
			try{final FormManager<Object,T>	wrapper = new FormManagerWrapper<>((FormManager<Object,T>)instance, ()-> {refresh();}); 
				
				abf = new AutoBuiltForm<T>(localizer, instance, wrapper);
				w = new InnerSVGPluginWindow<T>(instance.getClass().getResource(pp.svgURI()).toURI(),abf,(src)->
				{
					
					try{final Object result = instance.getClass().getField(src).get(instance);
					
						return result == null ? "" : result.toString();
					} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e1) {
						return "<"+src+">";
					}
				});

				SwingUtils.assignActionKey(w,SwingUtils.KS_HELP,(e)->{showHelp(e.getActionCommand());},abf.getContentModel().getRoot().getHelpId());
				SwingUtils.assignActionKey(w,SwingUtils.KS_CLOSE,(e)->{try{setClosed(true);} catch (PropertyVetoException exc) {}},"close");
	        	fillLocalizedStrings(localizer.currentLocale().getLocale(),localizer.currentLocale().getLocale());

	        	getContentPane().add(w);
				setSize(pp.width(),pp.height());
				
				if (pp.leftWidth() == -1) {
					final Dimension		viewSize = w.getInnerSVGSize(), preferences;
					
					if (viewSize.width > viewSize.height) {
						preferences = new Dimension(pp.width() - pp.height() * viewSize.height / viewSize.width, pp.height());
					}
					else {
						preferences = new Dimension(pp.width() - pp.height() * viewSize.width / viewSize.height, pp.height());
					}
					abf.setPreferredSize(preferences.width > pp.width()/2 ? new Dimension(pp.width()/2,pp.height()) : preferences);
				}
				else {
					abf.setPreferredSize(new Dimension(pp.width() - pp.leftWidth(),pp.height()));
				}
				
	        	setLocation(X_OFFSET*(openFrameCount % OFFSET_REPEAT), Y_OFFSET*(openFrameCount % OFFSET_REPEAT));
	        	setResizable(pp.resizable());
	
	        	if (!pp.pluginIconURI().isEmpty()) {
	        		setFrameIcon(new ImageIcon(instance.getClass().getResource(pp.pluginIconURI())));
	        	}
	        	if (!pp.desktopIconURI().isEmpty()) {
	        		getDesktopIcon().setUI(new SimpleDesktopIconUI(getDesktopIcon(),new ImageIcon(instance.getClass().getResource(pp.desktopIconURI()))));
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
						abf.close();
					}
				});
	        	
			} catch (IllegalArgumentException | LocalizationException | NullPointerException |  IOException | URISyntaxException exc) {
				throw new ContentException(exc);
			}
        }
    }

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings(oldLocale, newLocale);
	}

	private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) throws LocalizationException, IllegalArgumentException {
		setTitle("#"+windowId+": "+abf.getLocalizerAssociated().getValue(abf.getContentModel().getRoot().getLabelId()));
		w.localeChanged(oldLocale, newLocale);
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
		w.refresh();
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
	
	private static class FormManagerWrapper<T> implements FormManager<Object, T> {
		@FunctionalInterface
		private interface Refresher {
			void refresh();
		}		
		
		private final FormManager<Object, T>	delegate;
		private final Refresher					refresher;
		
		private FormManagerWrapper(final FormManager<Object, T> delegate, final Refresher refresher) {
			this.delegate = delegate;
			this.refresher = refresher;
		}

		@Override
		public RefreshMode onField(T inst, Object id, String fieldName, Object oldValue) throws FlowException, LocalizationException {
			final RefreshMode	mode = delegate.onField(inst, id, fieldName, oldValue);
			
			if (mode != RefreshMode.NONE && mode != RefreshMode.REJECT) {
				refresher.refresh();
			}
			return mode;
		}

		@Override
		public RefreshMode onAction(final T inst, final Object id, final String actionName, final Object parameter) throws FlowException, LocalizationException {
			final RefreshMode	mode = delegate.onAction(inst, id, actionName, parameter);
			
			if (mode != RefreshMode.NONE && mode != RefreshMode.REJECT) {
				refresher.refresh();
			}
			return mode;
		}
		
		@Override
		public LoggerFacade getLogger() {
			return delegate.getLogger();
		}
	}
}