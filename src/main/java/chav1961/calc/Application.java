package chav1961.calc;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URISyntaxException;
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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.border.EtchedBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;

import org.apache.lucene.store.FSDirectory;

import chav1961.calc.environment.desktop.DesktopManager;
import chav1961.calc.environment.pipe.PipeFactory;
import chav1961.calc.environment.pipe.controls.StartNode;
import chav1961.calc.environment.pipe.controls.TerminalNode;
import chav1961.calc.environment.search.LuceneWrapper;
import chav1961.calc.environment.search.SearchManager;
import chav1961.calc.interfaces.PipeInterface;
import chav1961.calc.interfaces.PluginInterface;
import chav1961.calc.interfaces.PluginInterface.PluginInstance;
import chav1961.purelib.basic.AbstractLoggerFacade;
import chav1961.purelib.basic.SystemErrLoggerFacade;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.PureLibLocalizer;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.ui.swing.AnnotatedActionListener;
import chav1961.purelib.ui.swing.AutoBuiltForm;
import chav1961.purelib.ui.swing.LocalizedDialog;
import chav1961.purelib.ui.swing.LocalizedFileChooser;
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

	private static final String				DESKTOP_WINDOW = "DesktopWindow";
	private static final String				SEARCH_WINDOW = "SearchWindow";

	private final CurrentSettings			settings;
	private final Localizer					localizer;
	private final JLabel					stateString = new JLabel();
	private final LoggerFacade				logger = new AbstractLoggerFacade() {
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
	private final JMenuBar					menu;
	private final SimpleNavigatorTree		leftMenu;
	private final DesktopManager			desktopMgr;
	private final SearchManager				searchMgr;
	private final PipeFactory				pipeFactory;
	private final File						luceneDir = new File("./lucene");
	private final LuceneWrapper				lucene;
	private final CardLayout				cardLayout = new CardLayout(); 
	private final JPanel					rightScreen = new JPanel(cardLayout);
	private final Timer						timer = new Timer(true);
	
	private PipeInterface					currentPipe = null;
	private File							currentPipeFile = null;
	private File							currentWorkingDir = new File("./");

	public Application(final XMLDescribedApplication xda, final Localizer parentLocalizer) throws NullPointerException, IllegalArgumentException, EnvironmentException, IOException, FlowException {
		if (xda == null) {
			throw new NullPointerException("Application descriptor can't be null");
		}
		else if (parentLocalizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else {
			
			this.localizer = xda.getLocalizer();
			final JPanel				statePanel = new JPanel();
			
			stateString.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			statePanel.add(stateString);
			this.settings = new CurrentSettings(this.localizer,this.logger); 
			
			parentLocalizer.push(localizer);
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
			leftMenu.addActionListener(SwingUtils.buildAnnotatedActionListener(this,(action)->{startPlugin(action);}));

			desktopMgr = new DesktopManager(this,xda,localizer);
			searchMgr = new SearchManager(this,xda,localizer,logger);
			pipeFactory = new PipeFactory(this,localizer);
			currentPipe = pipeFactory.newPipe(); 
			
			rightScreen.add(desktopMgr,DESKTOP_WINDOW);
			rightScreen.add(searchMgr,SEARCH_WINDOW);
			cardLayout.show(rightScreen,DESKTOP_WINDOW);			
			
			split.setLeftComponent(new JScrollPane(leftMenu));
			split.setRightComponent(rightScreen);
			split.setDividerLocation(200);
			
			centerPanel.add(split,BorderLayout.CENTER);
			
			SwingUtils.assignHelpKey((JPanel)getContentPane(),localizer,LocalizationKeys.HELP_ABOUT_APPLICATION);
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
			this.lucene = buildIndex();
			searchMgr.assignLiceneWrapper(this.lucene);
		}
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings(oldLocale,newLocale);
		SwingUtils.refreshLocale(menu,oldLocale, newLocale);
		SwingUtils.refreshLocale(leftMenu,oldLocale, newLocale);
		SwingUtils.refreshLocale(desktopMgr,oldLocale, newLocale);
		SwingUtils.refreshLocale(searchMgr,oldLocale, newLocale);
	}
	
	public void expandPluginByItsId(final String pluginId) {
		leftMenu.findAndSelect(pluginId);
	}
	
	private void fillLocalizedStrings(Locale oldLocale, Locale newLocale) throws LocalizationException {
		setTitle(localizer.getValue(LocalizationKeys.TITLE_APPLICATION));
	}

	private LuceneWrapper buildIndex() throws IOException, LocalizationException {
		final LuceneWrapper		result;
		
		if (!luceneDir.exists()) {
			luceneDir.mkdirs();
			result = new LuceneWrapper(FSDirectory.open(luceneDir.toPath()));
			
			try(final LoggerFacade 	transLogger	= logger.transaction("lucene index")) {
				
				result.buildDirectoryIndex(localizer,transLogger);
				transLogger.rollback();
			}
		}
		else {
			result = new LuceneWrapper(FSDirectory.open(luceneDir.toPath()));
		}
		return result;
	}

	@OnAction("cleanDektop")
	private void cleanDesktop() {
		try{if (accuratelyClosePipe()) {
				switch (JOptionPane.showOptionDialog(this,localizer.getValue(LocalizationKeys.CONFIRM_CLEAR_DESKTOP_MESSAGE)
						,localizer.getValue(LocalizationKeys.CONFIRM_CLEAR_DESKTOP_CAPTION)
						,JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,
						new String[] {localizer.getValue(LocalizationKeys.CONFIRM_BUTTON_YES),
									  localizer.getValue(LocalizationKeys.CONFIRM_BUTTON_NO),
									  localizer.getValue(LocalizationKeys.CONFIRM_BUTTON_CANCEL)
									  }
						,0)) {
					case JOptionPane.YES_OPTION		:
						desktopMgr.closeContent();
						break;
					case JOptionPane.NO_OPTION		:
						break;
					case JOptionPane.CANCEL_OPTION	:
						return;
					default : throw new UnsupportedOperationException("Unknown confirmation option!");
				}
			}
		} catch (IOException | HeadlessException | LocalizationException  e) {
			message(e,e.getLocalizedMessage());
		}
	}

	@OnAction("newPipe")
	private void newPipe() {
		try{if (accuratelyClosePipe()) {
				desktopMgr.closeContent();
				currentPipe = pipeFactory.newPipe();
				currentPipeFile = null;
			}
		} catch (IOException | HeadlessException | LocalizationException  e) {
			message(e,e.getLocalizedMessage());
		}
	}
	
	@OnAction("loadPipe")
	private void loadPipe() {
		try{if (accuratelyClosePipe()) {
				switch (askFile2Load()) {
					case JFileChooser.APPROVE_OPTION	:
						currentPipe = loadPipe(currentPipeFile);
						break;
					case JFileChooser.CANCEL_OPTION		:
						return;
					default : throw new UnsupportedOperationException("Unknown confirmation option!");
				}
			}
		} catch (IOException | HeadlessException | LocalizationException  e) {
			message(e,e.getLocalizedMessage());
		}
	}
	
	@OnAction("savePipe")
	private void savePipe() {
		try{if (currentPipeFile == null) {
				savePipeAs();
			}
			else {
				saveCurrentPipe(currentPipe,currentPipeFile);
			}
		} catch (IOException | HeadlessException e) {
			message(e,e.getLocalizedMessage());
		}
	}
	
	@OnAction("savePipeAs")
	private void savePipeAs() {
		try{switch (askFile2Save()) {
				case JFileChooser.APPROVE_OPTION	:
					saveCurrentPipe(currentPipe,currentPipeFile);
					break;
				case JFileChooser.CANCEL_OPTION		:
					return;
				default : throw new UnsupportedOperationException("Unknown confirmation option!");
			}
		} catch (IOException | HeadlessException | LocalizationException  e) {
			message(e,e.getLocalizedMessage());
		}
	}

	@OnAction("StartNode")
	private void newStartNode() {
		try{final PluginInterface	plugin = new StartNode(localizer);
			final PluginInstance	inst = plugin.newInstance(localizer, logger);
			
			inst.getComponent().setPreferredSize(inst.getRecommendedSize());
			placePlugin(plugin,inst);
		} catch (LocalizationException | SyntaxException | ContentException | IOException e) {
			message(e,e.getLocalizedMessage());
		}
	}
	
	@OnAction("TerminalNode")
	private void newTerminalNode() {
		try{final PluginInterface	plugin = new TerminalNode(localizer, false);
			final PluginInstance	inst = plugin.newInstance(localizer, logger);
			
			inst.getComponent().setPreferredSize(inst.getRecommendedSize());
			placePlugin(plugin,inst);
		} catch (LocalizationException | SyntaxException | ContentException | IOException e) {
			message(e,e.getLocalizedMessage());
		}
	}
	
	private boolean accuratelyClosePipe() throws HeadlessException, LocalizationException, IllegalArgumentException, IOException {
		if (currentPipe != null) {
			if (currentPipe.isModified()) {
				switch (JOptionPane.showOptionDialog(this,localizer.getValue(LocalizationKeys.CONFIRM_SAVE_PIPE_MESSAGE)
						,localizer.getValue(LocalizationKeys.CONFIRM_SAVE_PIPE_CAPTION)
						,JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,
						new String[] {localizer.getValue(LocalizationKeys.CONFIRM_BUTTON_YES),
									  localizer.getValue(LocalizationKeys.CONFIRM_BUTTON_NO),
									  localizer.getValue(LocalizationKeys.CONFIRM_BUTTON_CANCEL)
									  }
						,0)) {
					case JOptionPane.YES_OPTION		:
						savePipe();	
						// break NOT needed!!!
					case JOptionPane.NO_OPTION		:
						currentPipe.close();
						currentPipe = null;
						break;
					case JOptionPane.CANCEL_OPTION	:
						return false;
					default : throw new UnsupportedOperationException("Unknown confirmation option!");
				}
			}
			else {
				currentPipe.close();
				currentPipe = null;
			}
		}
		return true;
	}

	private int askFile2Load() throws LocalizationException {
		final LocalizedFileChooser	chooser = new LocalizedFileChooser(localizer);
		
		chooser.setCurrentDirectory(currentWorkingDir);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				try{return localizer.getValue(LocalizationKeys.CONFIRM_FILEFILTER_PIPE);
				} catch (LocalizationException e) {
					return LocalizationKeys.CONFIRM_FILEFILTER_PIPE;
				}
			}
			
			@Override
			public boolean accept(final File f) {
				return f.getName().endsWith(".pipe");
			}
		});
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		chooser.setApproveButtonText(localizer.getValue(LocalizationKeys.CONFIRM_BUTTON_OPEN));
		chooser.setApproveButtonToolTipText(localizer.getValue(LocalizationKeys.CONFIRM_BUTTON_OPEN_TOOLTIP));
		chooser.setLocale(localizer.currentLocale().getLocale());
		if (currentPipeFile != null) {
			chooser.setSelectedFile(currentPipeFile);
		}
		
		final int	rc = chooser.showOpenDialog(this);
		
		if (rc == JFileChooser.APPROVE_OPTION) {
			currentWorkingDir = chooser.getCurrentDirectory();
			currentPipeFile = chooser.getSelectedFile();
		}
		return rc;
	}
	
	private int askFile2Save() throws LocalizationException, IllegalArgumentException {
		final LocalizedFileChooser	chooser = new LocalizedFileChooser(localizer);
		
		chooser.setCurrentDirectory(currentWorkingDir);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				try{return localizer.getValue(LocalizationKeys.CONFIRM_FILEFILTER_PIPE);
				} catch (LocalizationException e) {
					return LocalizationKeys.CONFIRM_FILEFILTER_PIPE;
				}
			}
			
			@Override
			public boolean accept(final File f) {
				return f.getName().endsWith(".pipe");
			}
		});
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		chooser.setApproveButtonText(localizer.getValue(LocalizationKeys.CONFIRM_BUTTON_SAVE));
		chooser.setApproveButtonToolTipText(localizer.getValue(LocalizationKeys.CONFIRM_BUTTON_SAVE_TOOLTIP));
		chooser.setLocale(localizer.currentLocale().getLocale());
		if (currentPipeFile != null) {
			chooser.setSelectedFile(currentPipeFile);
		}
		
		final int	rc = chooser.showSaveDialog(this);
		
		if (rc == JFileChooser.APPROVE_OPTION) {
			currentWorkingDir = chooser.getCurrentDirectory();
			currentPipeFile = chooser.getSelectedFile();
		}
		return rc;
	}

	private PipeInterface loadPipe(final File file) throws IOException {
		try(final InputStream	is = new FileInputStream(file);
			final Reader		rdr = new InputStreamReader(is)) {
			return pipeFactory.loadPipe(rdr);
		}
	}

	
	private void saveCurrentPipe(final PipeInterface pip, final File file) throws IOException {
		try(final OutputStream	os = new FileOutputStream(file);
			final Writer		wr = new OutputStreamWriter(os)) {
			currentPipe.serialize(wr);
		}
	}

	
	@OnAction("exit")
	private void exitApplication () {
		if (desktopMgr.getPipeManager().getComponentCount() > 0) {
			setVisible(false);
			dispose();
		}
		else {
			setVisible(false);
			dispose();
		}
	}

	@OnAction("find")
	public void search() {
		cardLayout.show(rightScreen,SEARCH_WINDOW);
		searchMgr.focus();
	}

	@OnAction("index")
	public void buildSearchIndex() {
		try{buildIndex();
			message(localizer.getValue(LocalizationKeys.MESSAGE_REINDEXED));
		} catch (LocalizationException | IOException e) {
			message(e,e.getLocalizedMessage());
		}
	}
	
	public void unsearch() {
		cardLayout.show(rightScreen,DESKTOP_WINDOW);			
	}

	@OnAction("builtin.languages:en")
	private void selectEnglish() throws LocalizationException, NullPointerException {
		localizer.setCurrentLocale(Locale.forLanguageTag("en"));
	}
	
	@OnAction("builtin.languages:ru")
	private void selectRussian() throws LocalizationException, NullPointerException {
		localizer.setCurrentLocale(Locale.forLanguageTag("ru"));
	}

	@OnAction("settings")
	private void settings() throws LocalizationException, SyntaxException, ContentException {
		try(final AutoBuiltForm<CurrentSettings>	form = new AutoBuiltForm<CurrentSettings>(localizer,settings,settings)) {
			
			form.setPreferredSize(new Dimension(300,150));
			LocalizedDialog.askParameters((JComponent)this.getContentPane(),localizer,LocalizationKeys.SETTINGS_CAPTION,LocalizationKeys.SETTINGS_HELP,form);
		}		
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
			message(e,e.getLocalizedMessage());
		}
	}

	private void startPlugin(final String pluginName) {
		try{final PluginInterface	plugin = seekSPIPlugin(pluginName);
		
			if (plugin != null) {
				final PluginInstance 	inst = plugin.newInstance(plugin.getLocalizerAssociated(localizer),logger);
				
				inst.getComponent().setPreferredSize(inst.getRecommendedSize());
				placePlugin(plugin,inst);
			}
		} catch (LocalizationException | SyntaxException | ContentException | IOException | RuntimeException e) {
			message(e,e.getLocalizedMessage());
		}
	}
	
	private void placePlugin(final PluginInterface plugin, final PluginInstance component) throws LocalizationException {
		unsearch();
		desktopMgr.getPipeManager().newWindow(component.getLocalizerAssociated(),plugin.getPluginId(),plugin.getCaptionId(),plugin.getHelpId(),plugin.getIcon(),(JComponent)component);
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

	private void message(final Throwable t, final String format, final Object... parameters) {
		final TimerTask		tt = new TimerTask() {
								@Override
								public void run() {
									stateString.setText("");			
								}
							};
		t.printStackTrace();
		timer.purge();				
		timer.schedule(tt,3000);
		stateString.setText(parameters == null || parameters.length == 0 ? format : String.format(format,parameters));
		t.printStackTrace();
	}
	
	static PluginInterface seekSPIPlugin(final String pluginName) {
		for (PluginInterface item : ServiceLoader.load(PluginInterface.class)) {
			if (pluginName.equals(item.getPluginId())) {
				return item;
			}
		}
		return null;
	}
	
	
	public static void main(final String[] args) throws IOException, EnvironmentException, FlowException {
		try(final InputStream				is = Application.class.getResourceAsStream("application.xml");
			final Localizer					localizer = new PureLibLocalizer()) {
			final XMLDescribedApplication	xda = new XMLDescribedApplication(is,new SystemErrLoggerFacade());
			
			new Application(xda,localizer).setVisible(true);
		}
	}
}
