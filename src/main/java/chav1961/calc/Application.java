package chav1961.calc;



import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.SystemTray;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.border.EtchedBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import chav1961.calc.interfaces.PluginInterface;
import chav1961.calc.interfaces.TabContent;
import chav1961.calc.utils.SVGPluginFrame;
import chav1961.calc.windows.PipeTab;
import chav1961.calc.windows.ReferenceTab;
import chav1961.calc.windows.WorkbenchTab;
import chav1961.purelib.basic.ArgParser;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.PreparationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.fsys.FileSystemFactory;
import chav1961.purelib.fsys.FileSystemOnFile;
import chav1961.purelib.fsys.interfaces.FileSystemInterface;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.i18n.interfaces.LocalizerOwner;
import chav1961.purelib.i18n.interfaces.SupportedLanguages;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.streams.JsonStaxParser;
import chav1961.purelib.streams.JsonStaxPrinter;
import chav1961.purelib.ui.swing.AutoBuiltForm;
import chav1961.purelib.ui.swing.SimpleNavigatorTree;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.interfaces.OnAction;
import chav1961.purelib.ui.swing.useful.JCloseableTab;
import chav1961.purelib.ui.swing.useful.JFileContentManipulator;
import chav1961.purelib.ui.swing.useful.JFileSelectionDialog;
import chav1961.purelib.ui.swing.useful.JFileSelectionDialog.FilterCallback;
import chav1961.purelib.ui.swing.useful.JStateString;
import chav1961.purelib.ui.swing.useful.JSystemTray;

public class Application extends JFrame implements LocaleChangeListener, LocalizerOwner {
	private static final long 				serialVersionUID = -2663340436788182341L;
	private static final String				ARG_HELP_PORT = "helpPort";
	private static final String				ARG_DEBUG = "debug";
	
	private final CurrentSettings			settings;
	private final Localizer			 		localizer;
	private final LoggerFacade				logger;
	private final JMenuBar					menu;
	private final JPopupMenu				trayMenu;
	private final int						localHelpPort;
	private final CountDownLatch			latch;
	private final SimpleNavigatorTree<ContentNodeMetadata>		leftMenu;
	private final File						luceneDir = new File("./lucene");
	private final JTabbedPane				tabs = new JTabbedPane();
	private final JStateString				stateString;
	private final JFileContentManipulator	contentManipulator;
	private final WorkbenchTab				wbt;
			
	private File							currentPipeFile = null;
	private File				 			currentWorkingDir = new File("./");
	
	public Application(final ContentMetadataInterface xda, final int helpPort, final Localizer parentLocalizer, final LoggerFacade logger, final CountDownLatch latch) throws NullPointerException, IllegalArgumentException, EnvironmentException, IOException, FlowException, SyntaxException, PreparationException, ContentException {
		if (xda == null) {
			throw new NullPointerException("Application descriptor can't be null");
		}
		else if (parentLocalizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else if (latch == null) {
			throw new NullPointerException("Latch can't be null");
		}
		else {
			this.localizer = LocalizerFactory.getLocalizer(xda.getRoot().getLocalizerAssociated());
			this.logger = logger;
			this.localHelpPort = helpPort;
			this.latch = latch;

			parentLocalizer.push(localizer);
			parentLocalizer.addLocaleChangeListener(this);
			
			this.stateString = new JStateString(this.localizer,10,true);
			this.settings = new CurrentSettings(this.localizer,this.logger);
			
			stateString.setAutomaticClearTime(Severity.error,1,TimeUnit.MINUTES);
			stateString.setAutomaticClearTime(Severity.warning,15,TimeUnit.SECONDS);
			stateString.setAutomaticClearTime(Severity.info,5,TimeUnit.SECONDS);
			
			this.menu = SwingUtils.toJComponent(xda.byUIPath(URI.create("ui:/model/navigation.top.mainmenu")),JMenuBar.class); 
			SwingUtils.assignActionListeners(this.menu,this);
			this.trayMenu = SwingUtils.toJComponent(xda.byUIPath(URI.create("ui:/model/navigation.top.traymenu")),JPopupMenu.class); 
			SwingUtils.assignActionListeners(this.trayMenu,this);
			
			final JPanel	centerPanel = new JPanel(new BorderLayout()); 
			
			getContentPane().add(this.menu,BorderLayout.NORTH);
			centerPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			getContentPane().add(centerPanel,BorderLayout.CENTER);
			getContentPane().add(stateString,BorderLayout.SOUTH);

			final JSplitPane	split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			
			leftMenu = new SimpleNavigatorTree<ContentNodeMetadata>(localizer,xda.byUIPath(URI.create("ui:/model/navigation.top.navigator"))) {
								private static final long serialVersionUID = 1L;
								@Override
								protected void appendNodes(final ContentNodeMetadata submenu, final DefaultMutableTreeNode node) {
									final String	namePrefix = submenu.getName()+'.';
									
									for (PluginInterface<?> item : ServiceLoader.load(PluginInterface.class)) {
										if (item.getPluginName().startsWith(namePrefix)) {
											node.add(new DefaultMutableTreeNode(item.getMetadata(),false));
										}
									}
								}
							};

			leftMenu.addActionListener((e)->{callPlugin(e.getActionCommand());});
			
			this.contentManipulator = new JFileContentManipulator(new FileSystemOnFile(URI.create("file://./")),this.localizer
												,()->{return new InputStream() {@Override public int read() throws IOException {return -1;}};}
												,()->{return new OutputStream() {@Override public void write(int b) throws IOException {}};}
												);

			split.setLeftComponent(new JScrollPane(leftMenu));
			split.setRightComponent(tabs);
			split.setDividerLocation(200);
			
			centerPanel.add(split,BorderLayout.CENTER);

			wbt = new WorkbenchTab(tabs,localizer,stateString);			
			wbt.pluginCount.addListener((oldValue,newValue)->((JMenuItem)SwingUtils.findComponentByName(menu,"menu.file.cleandesktop")).setEnabled(newValue != 0));
			wbt.pluginCount.refresh();
			placeTab(tabs,wbt,false);
			
			SwingUtils.assignActionKey((JPanel)getContentPane()
						,KeyStroke.getKeyStroke(KeyEvent.VK_F,KeyEvent.CTRL_DOWN_MASK)
						,SwingUtils.buildAnnotatedActionListener(this)
						,"find");
			SwingUtils.assignActionKey((JPanel)getContentPane(),SwingUtils.KS_HELP,(e)->showOverview(),"overview");
			SwingUtils.assignExitMethod4MainWindow(this,()->exitApplication());
			SwingUtils.centerMainWindow(this,0.75f);
			
			fillLocalizedStrings(localizer.currentLocale().getLocale(),localizer.currentLocale().getLocale());
			pack();
		}
	}

	@Override
	public Localizer getLocalizer() {
		return localizer;
	}
	
	private void callPlugin(final String actionCommand) {
		if (actionCommand != null && !actionCommand.isEmpty()) {
			final URI	actionURI = URI.create(ContentMetadataInterface.APPLICATION_SCHEME+":"+actionCommand);
			
			for (PluginInterface<?> item : ServiceLoader.load(PluginInterface.class)) {
				if (item.canServe(actionURI)) {
					if (tabs.getSelectedComponent() instanceof WorkbenchTab) {
						try{final Object			inst = item.newIstance(stateString);
							final SVGPluginFrame	frame = new SVGPluginFrame(localizer,inst.getClass(),inst);
						        
							 frame.setVisible(true);
							 ((WorkbenchTab)tabs.getSelectedComponent()).placePlugin(frame);
							 frame.setSelected(true);
						} catch (java.beans.PropertyVetoException | ContentException e) {
							stateString.message(Severity.error,e,"Error creating plugin window: "+e.getLocalizedMessage());
						}
					}
					else if (tabs.getSelectedComponent() instanceof PipeTab) {
						((PipeTab)tabs.getSelectedComponent()).placePlugin(item,item.newIstance(stateString));
					}
					return;
				}			
			}
			stateString.message(Severity.error,"No any plugin found for ["+actionCommand+"]");
		}
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings(oldLocale,newLocale);
		SwingUtils.refreshLocale(menu,oldLocale, newLocale);
		SwingUtils.refreshLocale(leftMenu,oldLocale, newLocale);
		SwingUtils.refreshLocale(wbt,oldLocale, newLocale);
	}
	
	public void expandPluginByItsId(final String pluginId) {
		leftMenu.findAndSelect(URI.create(pluginId));
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
			final PipeTab	pipe = new PipeTab(tabs,localizer,stateString);
			
			placeTab(tabs,pipe,true);
		} catch (LocalizationException | ContentException | MalformedURLException e) {
			stateString.message(Severity.error,e.getLocalizedMessage());
		}
	}

	@OnAction("action:/loadPipe")
	private void loadPipe() {
		final File						file = new File("./").getAbsoluteFile(); 

		try(final FileSystemInterface	fsi = FileSystemFactory.createFileSystem(URI.create(FileSystemInterface.FILESYSTEM_URI_SCHEME+":"+file.toURI().toString()))) {
			final FilterCallback		pipeFilter = new FilterCallback() {
											final String[]	fileMask = new String[]{"*.pip"};
											
											@Override public String getFilterName() {return "Pipe";}
											@Override public String[] getFileMask() {return fileMask;}
											@Override public boolean accept(FileSystemInterface item) throws IOException {return item.isDirectory() || item.getName().endsWith(".pip");}
											@Override public boolean accept(File item) throws IOException {return item.isDirectory() || item.getName().endsWith(".pip");}
										};
			
			for (String item : JFileSelectionDialog.select(this,localizer,fsi,JFileSelectionDialog.OPTIONS_CAN_SELECT_FILE|JFileSelectionDialog.OPTIONS_FOR_OPEN,pipeFilter)) {
				final String	relativeURI = item.endsWith(".pip") ? item : item+".pip";
				final PipeTab	pipe = new PipeTab(tabs,localizer,stateString);
				
				placeTab(tabs,pipe,true);
				
				try(final FileSystemInterface	frb = fsi.clone().open(relativeURI)) {
					try(final Reader			rdr = frb.charRead("UTF-8");
						final JsonStaxParser	jp = new JsonStaxParser(rdr)) {

						pipe.deserialize(jp);
					}
					stateString.message(Severity.info,"New fact/rule base %1$s was prepared",relativeURI);
				}
			}
		} catch (IOException | LocalizationException | ContentException e) {
			e.printStackTrace();
			stateString.message(Severity.error,e.getLocalizedMessage());
		}
	}
	
	@OnAction("action:/savePipe")
	private void savePipe() {
		final Component	comp = tabs.getSelectedComponent();
		
		if (comp instanceof PipeTab) {
			@SuppressWarnings("resource")
			final PipeTab	pipe = (PipeTab)comp; 
			
			if (pipe.getFileAssciated() == null) {
				savePipeAs();
			}
			else {
				try(final FileWriter		fw = new FileWriter(pipe.getFileAssciated(),Charset.forName("UTF-8"));
					final JsonStaxPrinter	jp = new JsonStaxPrinter(fw)) {

					pipe.serialize(jp);
					jp.flush();
				} catch (IOException e) {
					stateString.message(Severity.error,e.getLocalizedMessage());
				}
			}
		}
	}

	@OnAction("action:/savePipeAs")
	private void savePipeAs() {
		final Component	comp = tabs.getSelectedComponent();
		
		if (comp instanceof PipeTab) {
			@SuppressWarnings("resource")
			final PipeTab			pipe = (PipeTab)comp;
			final File				file = pipe.getFileAssciated() != null ? pipe.getFileAssciated() : new File("./").getAbsoluteFile(); 
			
			try(final FileSystemInterface	fsi = FileSystemFactory.createFileSystem(URI.create(FileSystemInterface.FILESYSTEM_URI_SCHEME+":"+file.toURI().toString()))) {
				final FilterCallback		pipeFilter = new FilterCallback() {
												final String[]	fileMask = new String[]{"*.pip"};
												
												@Override public String getFilterName() {return "Pipe";}
												@Override public String[] getFileMask() {return fileMask;}
												@Override public boolean accept(FileSystemInterface item) throws IOException {return item.isDirectory() || item.getName().endsWith(".pip");}
												@Override public boolean accept(File item) throws IOException {return item.isDirectory() || item.getName().endsWith(".pip");}
											};
				
				for (String item : JFileSelectionDialog.select(this,localizer,fsi,JFileSelectionDialog.OPTIONS_CAN_SELECT_FILE|JFileSelectionDialog.OPTIONS_FOR_SAVE,pipeFilter)) {
					final String	relativeURI = item.endsWith(".pip") ? item : item+".pip";
					
					try(final FileSystemInterface	frb = fsi.clone().open(relativeURI).push("../").mkDir().pop().create()) {
						try(final Writer			wr = frb.charWrite("UTF-8");
							final JsonStaxPrinter	jp = new JsonStaxPrinter(wr)) {

							pipe.serialize(jp);
							jp.flush();
						}
						stateString.message(Severity.info,"New fact/rule base %1$s was prepared",relativeURI);
					}
				}
			} catch (IOException | LocalizationException e) {
				stateString.message(Severity.error,e.getLocalizedMessage());
			}
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
		} finally {
			latch.countDown();
		}
	}
	
	@OnAction("action:/references.tubes")
	private void refTubes() {
		// TODO:
	}

	@OnAction("action:builtin:/builtin.languages")
	private void selectLang(final Hashtable<String,String[]> langs) throws LocalizationException {
		localizer.setCurrentLocale(SupportedLanguages.valueOf(langs.get("lang")[0]).getLocale());
	}

	@OnAction("action:/settings")
	private void settings() {
		try{final ContentMetadataInterface				mdi = ContentModelFactory.forAnnotatedClass(CurrentSettings.class);
		
			try(final AutoBuiltForm<CurrentSettings,?>	abf = new AutoBuiltForm<CurrentSettings,Object>(mdi,localizer,PureLibSettings.INTERNAL_LOADER,settings,settings)) {
				
				for (Module m : abf.getUnnamedModules()) {
					CurrentSettings.class.getModule().addExports(CurrentSettings.class.getPackageName(),m);
				}
				abf.setPreferredSize(new Dimension(300,140));
				if (AutoBuiltForm.ask(this,localizer,abf,new URI[]{URI.create("app:action:/CurrentSettings.OK"),URI.create("app:action:/CurrentSettings.cancel")})) {
					stateString.message(Severity.info,CurrentSettings.SETTINGS_SAVED);
				}
			}
		} catch (LocalizationException | ContentException e) {
			stateString.message(Severity.error,e.getLocalizedMessage());
		} 
	}	

	@OnAction("action:/helpOverview")
	private void showOverview() {
		if (Desktop.isDesktopSupported()) {
			try{Desktop.getDesktop().browse(URI.create("http://localhost:"+localHelpPort+"/static/index.cre"));
			} catch (IOException exc) {
				exc.printStackTrace();
			}
		}
	}	
	
	@OnAction("action:/helpAbout")
	private void showAboutScreen() throws URISyntaxException {
		SwingUtils.showAboutScreen(this, localizer, LocalizationKeys.TITLE_HELP_ABOUT_APPLICATION, LocalizationKeys.HELP_ABOUT_APPLICATION, this.getClass().getResource("avatar.jpg").toURI(), new Dimension(300,300));
	}

	private void placeTab(final JTabbedPane pane, final JPanel tab, final boolean canClose) throws MalformedURLException, LocalizationException, SyntaxException, ContentException {
		final JCloseableTab	label = ((TabContent)tab).getTab();
		final JPopupMenu	menu = ((TabContent)tab).getPopupMenu();
		
		if (menu != null) {
			label.associate(pane, tab, menu);
			SwingUtils.assignActionListeners(menu, tab);
		}
		else {
			label.associate(pane, tab);
		}
		label.setCloseEnable(canClose);
		pane.addTab("",tab);
		pane.setTabComponentAt(pane.getTabCount()-1,label);
		pane.setSelectedIndex(pane.getTabCount()-1);
	}

	public static void main(final String[] args) throws IOException, EnvironmentException, FlowException, ContentException, HeadlessException, URISyntaxException {
		final ArgParser		parser = new ApplicationArgParser().parse(args);
		
		try(final InputStream				is = Application.class.getResourceAsStream("application.xml");
			final LoggerFacade				logger = PureLibSettings.CURRENT_LOGGER) {
			final ContentMetadataInterface	xda = ContentModelFactory.forXmlDescription(is);
			final CountDownLatch			latch = new CountDownLatch(1);
			final Application				app = new Application(xda,parser.getValue(ARG_HELP_PORT,int.class),PureLibSettings.PURELIB_LOCALIZER,logger,latch);

			app.setVisible(true);
			
			if (SystemTray.isSupported()) {
				try(final JSystemTray		tray = new JSystemTray(LocalizerFactory.getLocalizer(xda.getRoot().getLocalizerAssociated()), "Radioamator calc", app.getClass().getResource("tray.png").toURI(), LocalizationKeys.TITLE_APPLICATION, app.trayMenu, false)) {
					final ActionListener	al = (e)->{
												app.setVisible(!app.isVisible());
											};

					tray.addActionListener(al);
					latch.await();
					tray.removeActionListener(al);
				}
			}
			else {
				latch.await();
			}
		} catch (InterruptedException e) {
		}
	}
	
	private static class ApplicationArgParser extends ArgParser {
		private static final ArgParser.AbstractArg[]	KEYS = {
			new IntegerArg(ARG_HELP_PORT, true, "Help port to use for help browser", PureLibSettings.instance().getProperty(PureLibSettings.BUILTIN_HELP_PORT,int.class)),
			new BooleanArg(ARG_DEBUG, false, "turn on debugging trace", false)
		};
		
		ApplicationArgParser() {
			super(KEYS);
		}
	}
}