package chav1961.calc.environment.desktop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyVetoException;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import chav1961.calc.LocalizationKeys;
import chav1961.calc.environment.Constants;
import chav1961.calc.environment.pipe.controls.StartNode;
import chav1961.calc.environment.pipe.controls.TerminalNode;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.ui.swing.SwingUtils;

public class DesktopPipeManager extends JDesktopPane implements LocaleChangeListener {
	private static final Icon		LEFT_ORDINAL = new ImageIcon(DesktopPipeManager.class.getResource("leftOrdinal.png"));
	private static final Icon		LEFT_SOURCE = new ImageIcon(DesktopPipeManager.class.getResource("leftSource.png"));
	private static final Icon		RIGHT_ORDINAL = new ImageIcon(DesktopPipeManager.class.getResource("rightOrdinal.png"));
	private static final Icon		RIGHT_TERMINAL = new ImageIcon(DesktopPipeManager.class.getResource("rightTerminal.png"));
	
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

    public void newWindow(final Localizer localizer, final String pluginId, final String captionId, final String helpId, final Icon icon, final JComponent content) throws LocalizationException {
    	if (pluginId == null || pluginId.isEmpty()) {
    		throw new IllegalArgumentException("Plugin id can't be null or empty");
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
        	final SmartContainer	sc = new SmartContainer(localizer,pluginId,captionId,helpId,content,true);
        	
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
		private final JLabel		nameLabel = new JLabel();
		private final JTextField	nameField = new JTextField();
		private final JComponent	component;

		public SmartContainer(final String pluginId, final String captionId, final String helpId, final JComponent component, final boolean resizable) throws LocalizationException {
			this(DesktopPipeManager.this.localizer,pluginId,captionId,helpId,component,resizable);
    	}
		
		public SmartContainer(final Localizer localizer, final String pluginId, final String captionId, final String helpId, final JComponent component, final boolean resizable) throws LocalizationException {
    	    super(localizer.getValue(captionId), resizable, true, false, true);

    	    final JPanel			statePanel = new JPanel(new BorderLayout()); 
    	    final JPanel			innerStatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	    
    	    this.localizer = localizer;
    	    this.captionId = captionId;
    	    this.helpId = helpId;
    	    this.component = component;
    	    
    	    getContentPane().setLayout(new BorderLayout());
    	    nameField.setColumns(20);
    	    nameField.setText("Node"+openFrameCount);
    	    nameField.addFocusListener(new FocusListener(){
				@Override
				public void focusGained(FocusEvent e) {
				}

				@Override
				public void focusLost(FocusEvent e) {
					component.setName(nameField.getText());
				}
			});
    	    setName(nameField.getText());
    	    innerStatePanel.add(nameLabel);
    	    innerStatePanel.add(nameField);
    	    innerStatePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    	    innerStatePanel.setBackground(Color.GRAY);
    	    
    	    statePanel.add(new JLabel(pluginId.equals(Constants.PIPE_START_NODE) ? LEFT_SOURCE : LEFT_ORDINAL),BorderLayout.WEST);
    	    statePanel.add(new JLabel(pluginId.equals(Constants.PIPE_TERMINAL_NODE) ? RIGHT_TERMINAL : RIGHT_ORDINAL),BorderLayout.EAST);
    	    statePanel.add(innerStatePanel,BorderLayout.CENTER);
    	    
    	    getContentPane().add(component,BorderLayout.CENTER);
    	    getContentPane().add(statePanel,BorderLayout.SOUTH);
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
			fillLocalizedStrings(localizer.currentLocale().getLocale(),localizer.currentLocale().getLocale());
    	}

		@Override
		public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
			fillLocalizedStrings(oldLocale,newLocale);
			SwingUtils.refreshLocale(component,oldLocale,newLocale);
		}

		private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
			setTitle(localizer.getValue(captionId));
			nameLabel.setText(localizer.getValue(LocalizationKeys.DESKTOP_STATE_NAME_LABEL));
		}   
    }
}
