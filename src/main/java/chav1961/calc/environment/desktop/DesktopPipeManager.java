package chav1961.calc.environment.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyVetoException;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.ui.swing.SwingUtils;

public class DesktopPipeManager extends JDesktopPane implements LocaleChangeListener {
	private static final long 		serialVersionUID = -954797073311928063L;
	private static final int 		CHILD_X_OFFSET = 25;
	private static final int 		CHILD_Y_OFFSET = 25;

	private static volatile	int 	openFrameCount = 0;
	
	private final Localizer			localizer;
	
	public DesktopPipeManager(final Localizer localizer) {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null"); 
		}
		else {
			this.localizer = localizer;
		}
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
        for (Component item : getComponents()) {
    		SwingUtils.refreshLocale(item, oldLocale, newLocale);
        }
	}

	public boolean isWindowsPresent(final String name) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Name can't be null or empty"); 
		}
		else {
	        for (Component item : getComponents()) {
	        	if (name.equals(item.getName())) {
	        		return true;
	        	}
	        }
	        return false;
		}
	}
	
    public void revalidateContent() {
    	int	totalWidth = -Integer.MAX_VALUE, totalHeight = -Integer.MAX_VALUE;
        
        for (Component item : getComponents()) {
            totalWidth = Math.max(totalWidth,item.getX()+item.getWidth());
            totalHeight = Math.max(totalHeight,item.getY()+item.getHeight());
        }
        setPreferredSize(new Dimension(totalWidth,totalHeight));
        revalidate();
    }        

    public void newWindow(final Localizer localizer, final String name, final String captionId, final String helpId, final Icon icon, final JComponent content) throws LocalizationException {
    	if (name == null || name.isEmpty()) {
    		throw new IllegalArgumentException("Window name can't be null or empty");
    	}
    	else if (captionId == null || captionId.isEmpty()) {
    		throw new IllegalArgumentException("Caption id can't be null or empty");
    	}
    	else if (helpId == null || helpId.isEmpty()) {
    		throw new IllegalArgumentException("Help id can't be null or empty");
    	}
    	else if (!localizer.containsKey(captionId)) {
    		throw new IllegalArgumentException("Caption id ["+captionId+"] is unknown in the any localizers");
    	}
    	else if (!localizer.containsKey(helpId)) {
    		throw new IllegalArgumentException("Help id ["+helpId+"] is unknown in the any localizers");
    	}
    	else if (content == null) {
    		throw new IllegalArgumentException("Content can't be null");
    	}
    	else {
        	final SmartContainer	sc = new SmartContainer(localizer,captionId,helpId,content,true);
        	
        	sc.setName(name);
        	if (icon != null) {
        		sc.setFrameIcon(icon);
        	}
            sc.setVisible(true);
            add(sc);
            try{sc.setSelected(true);
    		} catch (PropertyVetoException e) {
    		}    	
    	}
    }

    private class SmartContainer extends JInternalFrame implements LocaleChangeListener {
		private static final long serialVersionUID = 950696564256041602L;

		private final Localizer		localizer;
		private final String		captionId;
		private final String		helpId;
		private final JComponent	component;

		public SmartContainer(final String captionId, final String helpId, final JComponent component, final boolean resizable) throws LocalizationException {
			this(DesktopPipeManager.this.localizer,captionId,helpId,component,resizable);
    	}
		
		public SmartContainer(final Localizer localizer, final String captionId, final String helpId, final JComponent component, final boolean resizable) throws LocalizationException {
    	    super(localizer.getValue(captionId), resizable, true, false, true);
    	    this.localizer = localizer;
    	    this.captionId = captionId;
    	    this.helpId = helpId;
    	    this.component = component;
    	    
    	    getContentPane().setLayout(new BorderLayout());
    	    getContentPane().add(component,BorderLayout.CENTER);
    	    pack();
    	    openFrameCount++;
       	    setLocation(CHILD_X_OFFSET*openFrameCount, CHILD_X_OFFSET*openFrameCount);
       	    	    
    	    addComponentListener(new ComponentListener() {
				@Override public void componentShown(ComponentEvent e) {revalidateContent();}
				@Override public void componentResized(ComponentEvent e) {revalidateContent();}
				@Override public void componentMoved(ComponentEvent e) {revalidateContent();}
				@Override public void componentHidden(ComponentEvent e) {revalidateContent();}
			});
    	    SwingUtils.assignHelpKey(component,localizer,helpId);
    	}

		@Override
		public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
			fillLocalizedStrings(oldLocale,newLocale);
			SwingUtils.refreshLocale(component,oldLocale,newLocale);
		}

		private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
			setTitle(localizer.getValue(captionId));
		}   
    }
}
