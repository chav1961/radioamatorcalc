package chav1961.calc;


import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.Timer;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.border.EtchedBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import chav1961.calc.interfaces.PluginInterface;
import chav1961.calc.interfaces.TabContent;
import chav1961.calc.utils.SVGPluginFrame;
import chav1961.calc.windows.PipeTab;
import chav1961.calc.windows.WorkbenchTab;
import chav1961.purelib.basic.ArgParser;
import chav1961.purelib.basic.NullLoggerFacade;
import chav1961.purelib.basic.SystemErrLoggerFacade;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.PreparationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.fsys.FileSystemOnFile;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.PureLibLocalizer;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.ui.swing.AnnotatedActionListener;
import chav1961.purelib.ui.swing.SimpleNavigatorTree;
import chav1961.purelib.ui.swing.SwingModelUtils;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.interfaces.OnAction;
import chav1961.purelib.ui.swing.useful.JCloseableTab;
import chav1961.purelib.ui.swing.useful.JFileContentManipulator;
import chav1961.purelib.ui.swing.useful.JStateString;

public class Application extends JFrame implements LocaleChangeListener {
	private static final long 				serialVersionUID = -2663340436788182341L;

	private static final String				DESKTOP_WINDOW = "DesktopWindow";
	private static final String				SEARCH_WINDOW = "SearchWindow";

	private final CurrentSettings			settings;
	private final Localizer					localizer;
	private final LoggerFacade				logger;
	private final JMenuBar					menu;
	private final SimpleNavigatorTree		leftMenu;
	private final File						luceneDir = new File("./lucene");
	private final JTabbedPane				tabs = new JTabbedPane();
	private final Timer						timer = new Timer(true);
	private final JStateString				stateString;
	private final JFileContentManipulator	contentManipulator;
	private final WorkbenchTab				wbt;
	private final List<PipeTab>				pipes = new ArrayList<>();
			
	private File							currentPipeFile = null;
	private File							currentWorkingDir = new File("./");

	public Application(final ContentMetadataInterface xda, final Localizer parentLocalizer, final LoggerFacade logger) throws NullPointerException, IllegalArgumentException, EnvironmentException, IOException, FlowException, SyntaxException, PreparationException, ContentException {
		if (xda == null) {
			throw new NullPointerException("Application descriptor can't be null");
		}
		else if (parentLocalizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.localizer = LocalizerFactory.getLocalizer(xda.getRoot().getLocalizerAssociated());
			this.logger = logger;
			this.stateString = new JStateString(this.localizer,10);
			this.settings = new CurrentSettings(this.localizer,this.logger);
			
			parentLocalizer.push(localizer);
			localizer.addLocaleChangeListener(this);
			
			this.menu = SwingModelUtils.toMenuEntity(xda.byUIPath(URI.create("ui:/model/navigation.top.mainmenu")),JMenuBar.class); 
			SwingUtils.assignActionListeners(this.menu,this);
			
			final JPanel	centerPanel = new JPanel(new BorderLayout()); 
			
			getContentPane().add(this.menu,BorderLayout.NORTH);
			centerPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			getContentPane().add(centerPanel,BorderLayout.CENTER);
			getContentPane().add(stateString,BorderLayout.SOUTH);

			final JSplitPane	split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			
			leftMenu = new SimpleNavigatorTree(localizer,SwingModelUtils.toMenuEntity(xda.byUIPath(URI.create("ui:/model/navigation.top.navigator")),JMenuBar.class));

			leftMenu.addActionListener((e)->{callPlugin(e.getActionCommand());});
			
			this.contentManipulator = new JFileContentManipulator(new FileSystemOnFile(URI.create("file://./")),this.localizer
												,()->{return new InputStream() {@Override public int read() throws IOException {return -1;}};}
												,()->{return new OutputStream() {@Override public void write(int b) throws IOException {}};}
												);

			split.setLeftComponent(new JScrollPane(leftMenu));
			split.setRightComponent(tabs);
			split.setDividerLocation(200);
			
			centerPanel.add(split,BorderLayout.CENTER);

			wbt = new WorkbenchTab(localizer,stateString);			
			wbt.pluginCount.addListener((oldValue,newValue)->((JMenuItem)SwingUtils.findComponentByName(menu,"menu.file.cleandesktop")).setEnabled(newValue != 0));
			wbt.pluginCount.refresh();
			placeTab(tabs,wbt,false);
			
			SwingUtils.assignActionKey((JPanel)getContentPane()
						,KeyStroke.getKeyStroke(KeyEvent.VK_F,KeyEvent.CTRL_DOWN_MASK)
						,new AnnotatedActionListener<Application>(this)
						,"find");
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
			pack();
		}
	}

	private void callPlugin(final String actionCommand) {
		final URI	actionURI = URI.create(PluginInterface.PLUGIN_SCHEME+":"+URI.create(actionCommand).getSchemeSpecificPart().substring(1)+":/");
		
		for (PluginInterface item : ServiceLoader.load(PluginInterface.class)) {
			if (item.canServe(actionURI)) {
				try{final Object			inst = item.newIstance(stateString);
					final SVGPluginFrame	frame = new SVGPluginFrame(localizer, inst);
				        
					 frame.setVisible(true);
					 ((WorkbenchTab)tabs.getSelectedComponent()).placePlugin(frame);
//				     desktopPane.add(frame);
					 frame.setSelected(true);
				} catch (java.beans.PropertyVetoException | ContentException e) {
					stateString.message(Severity.error,e,"Error creating plugin window: "+e.getLocalizedMessage());
				}
				return;
			}			
		}
		stateString.message(Severity.error,"No any plugin found for ["+actionCommand+"]");
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings(oldLocale,newLocale);
		SwingUtils.refreshLocale(menu,oldLocale, newLocale);
		SwingUtils.refreshLocale(leftMenu,oldLocale, newLocale);
	}
	
	public void expandPluginByItsId(final String pluginId) {
		leftMenu.findAndSelect(pluginId);
	}
	
	private void fillLocalizedStrings(Locale oldLocale, Locale newLocale) throws LocalizationException {
		setTitle(localizer.getValue(LocalizationKeys.TITLE_APPLICATION));
	}

	@OnAction("action:/cleanDektop")
	private void cleanDesktop() {
		try{
			wbt.closeAll();
		} catch (LocalizationException e) {
			stateString.message(Severity.error,e.getLocalizedMessage());
		}
	}	

	@OnAction("action:/newPipe")
	private void newPipe() {
		try{
			final PipeTab	pipe = new PipeTab(localizer,logger);
			
			placeTab(tabs,pipe,true);
		} catch (LocalizationException | ContentException | MalformedURLException e) {
			stateString.message(Severity.error,e.getLocalizedMessage());
		}
	}
	
	@OnAction("action:/exit")
	private void exitApplication () {
		try{//if (contentManipulator.saveFile()) {
//				if (desktopMgr.getPipeManager().getComponentCount() > 0) {
//					setVisible(false);
//					dispose();
//				}
//				else {
//					setVisible(false);
//					dispose();
//				}
			//}
			contentManipulator.close();
			setVisible(false);
			dispose();
		} catch (IOException e) {
			stateString.message(Severity.error,e.getLocalizedMessage());
		}
	}

	@OnAction("action:/helpAbout")
	private void showAboutScreen() {
		try{final JEditorPane 	pane = new JEditorPane("text/html",null);
			final Icon			icon = new ImageIcon(this.getClass().getResource("avatar.jpg"));
			
			try(final Reader	rdr = localizer.getContent(LocalizationKeys.HELP_ABOUT_APPLICATION,new MimeType("text","x-wiki.creole"),new MimeType("text","html"))) {
				pane.read(rdr,null);
			}
			pane.setEditable(false);
			pane.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			pane.setPreferredSize(new Dimension(300,300));
			pane.addHyperlinkListener(new HyperlinkListener() {
								@Override
								public void hyperlinkUpdate(final HyperlinkEvent e) {
									if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
										try{Desktop.getDesktop().browse(e.getURL().toURI());
										} catch (URISyntaxException | IOException exc) {
											exc.printStackTrace();
										}
									}
								}
			});
			
			JOptionPane.showMessageDialog(this,pane,localizer.getValue(LocalizationKeys.TITLE_HELP_ABOUT_APPLICATION),JOptionPane.PLAIN_MESSAGE,icon);
		} catch (LocalizationException | MimeTypeParseException | IOException e) {
			stateString.message(Severity.error,e.getLocalizedMessage());
		}
	}

	private void placeTab(final JTabbedPane pane, final JPanel tab, final boolean canClose) throws MalformedURLException, LocalizationException, SyntaxException, ContentException {
		final JCloseableTab	label = ((TabContent)tab).getTab();
		final JPopupMenu	menu = ((TabContent)tab).getPopupMenu();
		
		if (menu != null) {
			label.associate(pane, tab, menu);
		}
		else {
			label.associate(pane, tab);
		}
		label.setCloseEnable(canClose);
		pane.addTab("",tab);
		pane.setTabComponentAt(pane.getTabCount()-1,label);
	}

	public static void main(final String[] args) throws IOException, EnvironmentException, FlowException, ContentException, HeadlessException, URISyntaxException {
		final ArgParser		parser = new ApplicationArgParser().parse(args);
		
		try(final InputStream				is = Application.class.getResourceAsStream("application.xml");
			final Localizer					localizer = new PureLibLocalizer();
			final LoggerFacade				logger = parser.getValue("debug",boolean.class) ? new SystemErrLoggerFacade() : new NullLoggerFacade()) {
			final ContentMetadataInterface	xda = ContentModelFactory.forXmlDescription(is);
			
			new Application(xda,localizer,logger).setVisible(true);
		}
	}
	
	private static class ApplicationArgParser extends ArgParser {
		private static final ArgParser.AbstractArg[]	KEYS = {
			new BooleanArg("debug", false, "turn on debugging trace", false)
		};
		
		ApplicationArgParser() {
			super(KEYS);
		}
	}
}