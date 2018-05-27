package chav1961.calc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

import chav1961.calc.elements.coils.SingleCoils;
import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.AbstractLoggerFacade;
import chav1961.purelib.basic.SystemErrLoggerFacade;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.PureLibLocalizer;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.XMLDescribedApplication;
import chav1961.purelib.ui.swing.interfaces.OnAction;

public class Application extends JDialog implements LocaleChangeListener {
	private static final long 				serialVersionUID = -2663340436788182341L;
	
	private final XMLDescribedApplication	xda;
	private final Localizer					localizer;
	private final LoggerFacade				logger;
	private final JTabbedPane				tab = new JTabbedPane();
	
	public Application(final XMLDescribedApplication xda, final Localizer parentLocalizer) throws NullPointerException, IllegalArgumentException, EnvironmentException {
		if (xda == null) {
			throw new NullPointerException("Application descriptor can't be null");
		}
		else if (parentLocalizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else {
			this.xda = xda;
			this.localizer = xda.getLocalizer();
			final JLabel		stateString = new JLabel();
			final JPanel		statePanel = new JPanel();
			
			stateString.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			statePanel.add(stateString);
			this.logger = new AbstractLoggerFacade() {
								@Override
								protected void toLogger(final Severity level, final String text, final Throwable throwable) {
									stateString.setText(text);
								}
								
								@Override
								protected AbstractLoggerFacade getAbstractLoggerFacade(final String mark, final Class<?> root) {
									return this;
								}
							};
			
			localizer.setParent(parentLocalizer);
			
			final JMenuBar	bar = xda.getEntity("mainmenu",JMenuBar.class,null); 
			final JPanel	centerPanel = new JPanel(new BorderLayout()); 
			
			SwingUtils.assignActionListeners(bar,this);
			getContentPane().add(bar,BorderLayout.NORTH);
			centerPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			getContentPane().add(centerPanel,BorderLayout.CENTER);
			getContentPane().add(statePanel,BorderLayout.SOUTH);

			final JToolBar	tool = xda.getEntity("toolbar",JToolBar.class,null); 
			
			SwingUtils.assignActionListeners(tool,this);
			tool.setFloatable(false);
			centerPanel.add(tool,BorderLayout.NORTH);
			
			centerPanel.add(tab,BorderLayout.CENTER);
			
			localizer.addLocaleChangeListener(this);
			
			SwingUtils.assignHelpKey((JPanel)getContentPane(),localizer,LocalizationKeys.HELP_ABOUT_APPLICATION);
			SwingUtils.centerMainWindow(this,0.75f);
			addWindowListener(new WindowListener() {
				@Override public void windowOpened(WindowEvent e) {}
				
				@Override 
				public void windowClosing(WindowEvent e) {
					exitApplication();
				}

				@Override public void windowClosed(WindowEvent e) {}
				@Override public void windowIconified(WindowEvent e) {}
				@Override public void windowDeiconified(WindowEvent e) {}
				@Override public void windowActivated(WindowEvent e) {}
				@Override public void windowDeactivated(WindowEvent e) {}
			});
			
		}
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		setTitle(localizer.getValue(LocalizationKeys.TITLE_APPLICATION));
		SwingUtils.refreshLocale(this,oldLocale, newLocale);
	}
	
	@OnAction("exit")
	private void exitApplication () {
		setVisible(false);
		dispose();
	}

	@OnAction("elementsCoilsOneLayer")
	private void elementsCoilsOneLayer() throws NullPointerException, IllegalArgumentException, LocalizationException, SyntaxException, ContentException, IOException {
		final SingleCoils	item = new SingleCoils(localizer,logger); 
		
		placePlugin(item);
	}
	
	@OnAction("helpAbout")
	private void showAboutScreen() {
		try{final JEditorPane 	pane = new JEditorPane("text/html",null);
			final Icon			icon = new ImageIcon(this.getClass().getResource("avatar.jpg"));
			
			try(final Reader	rdr = localizer.getContent(LocalizationKeys.HELP_ABOUT_APPLICATION,new MimeType("text","x-wiki.creole"),new MimeType("text","html"))) {
				pane.read(rdr,null);
			}
			pane.setEditable(false);
			pane.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			pane.setPreferredSize(new Dimension(300,300));
			
			JOptionPane.showMessageDialog(this,pane,localizer.getValue(LocalizationKeys.TITLE_HELP_ABOUT_APPLICATION),JOptionPane.PLAIN_MESSAGE,icon);
		} catch (LocalizationException | MimeTypeParseException | IOException e) {
			e.printStackTrace();
		}
	}

	private void placePlugin(final PluginInterface plugin) {
		try{tab.addTab(localizer.getValue(plugin.getCaptionId()),plugin.getIcon(),(JComponent)plugin,localizer.getValue(plugin.getToolTipId()));
		} catch (LocalizationException exc) {
			message(exc.getLocalizedMessage());
		}
	}
	
	private void message(final String format, final Object... parameters) {
		JOptionPane.showMessageDialog(this,parameters == null || parameters.length == 0 ? format : String.format(format,parameters));
	}
	
	
	public static void main(final String[] args) throws IOException, EnvironmentException {
		try(final InputStream				is = Application.class.getResourceAsStream("application.xml");
			final Localizer					localizer = new PureLibLocalizer()) {
			final XMLDescribedApplication	xda = new XMLDescribedApplication(is,new SystemErrLoggerFacade());
			
			new Application(xda,localizer).setVisible(true);
		}
	}
}
