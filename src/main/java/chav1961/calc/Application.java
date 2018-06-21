package chav1961.calc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.Timer;
import java.util.TimerTask;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EtchedBorder;

import chav1961.calc.environment.DesktopManager;
import chav1961.calc.interfaces.PluginInterface;
import chav1961.calc.interfaces.PluginInterface.PluginInstance;
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
import chav1961.purelib.ui.swing.SimpleNavigatorTree;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.XMLDescribedApplication;
import chav1961.purelib.ui.swing.interfaces.OnAction;

public class Application extends JFrame implements LocaleChangeListener {
	private static final long 				serialVersionUID = -2663340436788182341L;
	
	private static final String				TRACE_FORMAT = "<html><body><font color=grey>%1$s</font></body></html>"; 
	private static final String				DEBUG_FORMAT = "<html><body><font color=grey>%1$s</font></body></html>"; 
	private static final String				INFO_FORMAT = "<html><body><font color=black>%1$s</font></body></html>"; 
	private static final String				WARNING_FORMAT = "<html><body><font color=blue>%1$s</font></body></html>"; 
	private static final String				ERROR_FORMAT = "<html><body><font color=red>%1$s</font></body></html>"; 
	private static final String				SEVERE_FORMAT = "<html><body><font color=red><b>%1$s</b></font></body></html>"; 
	
	private final Localizer					localizer;
	private final JLabel					stateString = new JLabel();
	private final LoggerFacade				logger;
	private final JMenuBar					menu;
	private final SimpleNavigatorTree		leftMenu;
	private final DesktopManager			mgr;
	private final Timer						timer = new Timer(true); 
	
	public Application(final XMLDescribedApplication xda, final Localizer parentLocalizer) throws NullPointerException, IllegalArgumentException, EnvironmentException {
		if (xda == null) {
			throw new NullPointerException("Application descriptor can't be null");
		}
		else if (parentLocalizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else {
			this.localizer = xda.getLocalizer();
			final JPanel		statePanel = new JPanel();
			
			stateString.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			statePanel.add(stateString);
			this.logger = new AbstractLoggerFacade() {
								@Override
								protected void toLogger(final Severity level, final String text, final Throwable throwable) {
									if (throwable != null) {
										throwable.printStackTrace();
									}
									switch (level) {
										case debug		: Application.this.message(DEBUG_FORMAT,text); break;
										case error		: Application.this.message(ERROR_FORMAT,text); break;
										case info		: Application.this.message(INFO_FORMAT,text); break;
										case severe		: Application.this.message(SEVERE_FORMAT,text); break;
										case trace		: Application.this.message(TRACE_FORMAT,text); break;
										case warning	: Application.this.message(WARNING_FORMAT,text); break;
										default	: throw new UnsupportedOperationException("Severity level ["+level+"] is not suported yet");
									}
								}
								
								@Override
								protected AbstractLoggerFacade getAbstractLoggerFacade(final String mark, final Class<?> root) {
									return this;
								}
							};
			
			localizer.setParent(parentLocalizer);
			localizer.addLocaleChangeListener(this);
			
			this.menu = xda.getEntity("mainmenu",JMenuBar.class,null); 
			final JPanel	centerPanel = new JPanel(new BorderLayout()); 
			
			SwingUtils.assignActionListeners(this.menu,this);
			getContentPane().add(this.menu,BorderLayout.NORTH);
			centerPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			getContentPane().add(centerPanel,BorderLayout.CENTER);
			getContentPane().add(statePanel,BorderLayout.SOUTH);

			final JSplitPane	split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			
			leftMenu = new SimpleNavigatorTree(localizer,xda.getEntity("navigator",JMenuBar.class,null));
			leftMenu.addActionListener(SwingUtils.buildAnnotatedActionListener(this,(action)->{}));

			mgr = new DesktopManager(xda,localizer);
			
			split.setLeftComponent(new JScrollPane(leftMenu));
			split.setRightComponent(mgr);
			split.setDividerLocation(200);
			
			centerPanel.add(split,BorderLayout.CENTER);
			
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
			fillLocalizedStrings(localizer.currentLocale().getLocale(),localizer.currentLocale().getLocale());
		}
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings(oldLocale,newLocale);
		SwingUtils.refreshLocale(menu,oldLocale, newLocale);
		SwingUtils.refreshLocale(leftMenu,oldLocale, newLocale);
		SwingUtils.refreshLocale(mgr,oldLocale, newLocale);
	}
	
	private void fillLocalizedStrings(Locale oldLocale, Locale newLocale) throws LocalizationException {
		setTitle(localizer.getValue(LocalizationKeys.TITLE_APPLICATION));
	}

	@OnAction("cleanPipe")
	private void cleanPipe() {
		mgr.closeContent();
	}
	
	@OnAction("loadPipe")
	private void loadPipe() {
		// TODO:
	}
	
	@OnAction("savePipe")
	private void savePipe() {
		// TODO:
	}
	
	@OnAction("savePipeAs")
	private void savePipeAs() {
		// TODO:
	}
	
	@OnAction("exit")
	private void exitApplication () {
		if (mgr.getPipeManager().getComponentCount() > 0) {
			setVisible(false);
			dispose();
		}
		else {
			setVisible(false);
			dispose();
		}
	}

	@OnAction("elementsCoilsOneLayer")
	private void elementsCoilsOneLayer() throws NullPointerException, IllegalArgumentException, LocalizationException, SyntaxException, ContentException, IOException {
		final PluginInterface	plugin = seekSPIPlugin("SingleCoilsService"); 
		final PluginInstance	inst = plugin.newInstance(localizer,logger);
		
		inst.getComponent().setPreferredSize(new Dimension(450,200));
		placePlugin(plugin,inst);
	}
	
	@OnAction("elementsCoilsMultiLayer")
	private void elementsCoilsMultiLayer() throws NullPointerException, IllegalArgumentException, LocalizationException, SyntaxException, ContentException, IOException {
		final PluginInterface	plugin = seekSPIPlugin("MultiLayerCoilsService"); 
		final PluginInstance	inst = plugin.newInstance(localizer,logger);
		
		inst.getComponent().setPreferredSize(new Dimension(450,200));
		placePlugin(plugin,inst);
	}

	
	@OnAction("builtin.languages:en")
	private void selectEnglish() throws LocalizationException, NullPointerException {
		localizer.setCurrentLocale(Locale.forLanguageTag("en"));
	}
	
	@OnAction("builtin.languages:ru")
	private void selectRussian() throws LocalizationException, NullPointerException {
		localizer.setCurrentLocale(Locale.forLanguageTag("ru"));
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

	private void placePlugin(final PluginInterface plugin, final PluginInstance component) throws LocalizationException {
		mgr.getPipeManager().newWindow(component.getLocalizerAssociated(),plugin.getPluginId(),plugin.getCaptionId(),plugin.getHelpId(),plugin.getIcon(),(JComponent)component);
	}
	
	private void message(final String format, final Object... parameters) {
		final TimerTask		tt = new TimerTask() {
								@Override
								public void run() {
									stateString.setText("");			
								}
							};
							
		timer.purge();				
		timer.schedule(tt,3000);
		stateString.setText(parameters == null || parameters.length == 0 ? format : String.format(format,parameters));
	}

	static PluginInterface seekSPIPlugin(final String pluginName) {
		for (PluginInterface item : ServiceLoader.load(PluginInterface.class)) {
			if (pluginName.equals(item.getPluginId())) {
				return item;
			}
		}
		return null;
	}
	
	
	public static void main(final String[] args) throws IOException, EnvironmentException {
		try(final InputStream				is = Application.class.getResourceAsStream("application.xml");
			final Localizer					localizer = new PureLibLocalizer()) {
			final XMLDescribedApplication	xda = new XMLDescribedApplication(is,new SystemErrLoggerFacade());
			
			new Application(xda,localizer).setVisible(true);
		}
	}
}
