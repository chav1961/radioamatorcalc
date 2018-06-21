package chav1961.calc.environment;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Locale;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.XMLDescribedApplication;
import chav1961.purelib.ui.swing.interfaces.OnAction;

public class DesktopManager extends JPanel implements LocaleChangeListener {
	private static final long serialVersionUID = 813296081208498667L;

	private final XMLDescribedApplication	xda;
	private final Localizer					localizer;
	private final JToolBar					tool;
	private final DesktopPipeManager		mgr; 
	
	public DesktopManager(final XMLDescribedApplication xda, final Localizer localizer) throws NullPointerException, IllegalArgumentException, EnvironmentException {
		super(new BorderLayout());
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null"); 
		}
		else {
			this.xda = xda; 
			this.localizer = localizer;
			
			final JScrollPane	pane = new JScrollPane(this.mgr = new DesktopPipeManager(localizer)); 
			
			tool = xda.getEntity("desktopToolbar",JToolBar.class,null); 
			
			SwingUtils.assignActionListeners(tool,this);
			add(pane,BorderLayout.CENTER);
			add(tool,BorderLayout.NORTH);
			fillLocalizedString(localizer.currentLocale().getLocale(),localizer.currentLocale().getLocale());
		}
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedString(oldLocale,newLocale);
		SwingUtils.refreshLocale(tool,oldLocale, newLocale);
		SwingUtils.refreshLocale(mgr,oldLocale, newLocale);
	}
	
	public DesktopPipeManager getPipeManager() {
		return mgr;
	}
	
	@OnAction("closeAll")
	public void closeContent() {
		for (Component item : mgr.getComponents()) {
			((JInternalFrame)item).dispose();
		}
		mgr.removeAll();
	}

	private void fillLocalizedString(final Locale oldLocale, final Locale newLocale) {
		// TODO Auto-generated method stub
	}
}
