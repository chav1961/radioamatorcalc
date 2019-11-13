package chav1961.calc.pipe;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Locale;

import javax.swing.JDesktopPane;
import javax.swing.JToolBar;

import chav1961.calc.interfaces.DragMode;
import chav1961.calc.interfaces.PipeContainerInterface;
import chav1961.calc.interfaces.PipeContainerInterface.PipeItemType;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.ui.swing.SwingModelUtils;

public class PipeManager extends JDesktopPane implements Closeable, LocaleChangeListener {
	private static final long serialVersionUID = 1L;

	private final LoggerFacade				logger;
	private final Localizer					localizer;
	private final ContentMetadataInterface	cmi;
	private final JToolBar					toolbar;
	
	public PipeManager(final Localizer localizer, final LoggerFacade logger) throws IOException, EnvironmentException {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.logger = logger;
			try(final InputStream	is = this.getClass().getResourceAsStream("pipe.xml")) {
				this.cmi = ContentModelFactory.forXmlDescription(is);
			}
			this.localizer = localizer.push(LocalizerFactory.getLocalizer(cmi.getRoot().getLocalizerAssociated()));
			this.toolbar = SwingModelUtils.toToolbar(cmi.byUIPath(URI.create("")),JToolBar.class);
		}
	}
	
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		try{this.localizer.pop();
		} catch (LocalizationException e) {
		}
	}	

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		// TODO Auto-generated method stub
		fillLocalizedStrings(oldLocale, newLocale);
	}
	
	public void loadPipe(final InputStream is) throws IOException, ContentException {
		
	}

	public void storePipe(final OutputStream is) throws IOException, ContentException {
		
	}
	
	public PipeItemFrame[] getPipeComponents() {
		return null;
	}

	public PipeItemFrame[] getPipeComponents(final PipeItemType type) {
		return null;
	}

	public boolean hasComponentAt(int x, int y) {
		return false;		
	}
	
	public PipeItemFrame at(int x, int y) {
		return null;		
	}
	
	public void addPipeComponent(final PipeItemFrame item) {
		
	}

	public void removePipeComponent(final PipeItemFrame item) {
		
	}
	
	public boolean validatePipe(final LoggerFacade facade) {
		return false;		
	}
	
	public boolean start(final LoggerFacade facade) throws ContentException {
		return false;
	}

	public boolean stop(final LoggerFacade facade) throws ContentException {
		return false;
	}
	
	DragMode setDragMode(final DragMode newMode) {
		return newMode;
	}
	
	private void fillLocalizedStrings(Locale oldLocale, Locale newLocale) {
		// TODO Auto-generated method stub
		
	}

}
