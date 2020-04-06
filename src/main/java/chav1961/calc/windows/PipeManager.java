package chav1961.calc.windows;

import java.awt.Component;
import java.awt.Point;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import chav1961.calc.LocalizationKeys;
import chav1961.calc.interfaces.ContentClassificator;
import chav1961.calc.interfaces.ContentEnumerator;
import chav1961.calc.interfaces.DragMode;
import chav1961.calc.interfaces.PipeContainerInterface.PipeItemType;
import chav1961.calc.interfaces.PipeContainerItemInterface;
import chav1961.calc.interfaces.PipeItemDropTarget;
import chav1961.calc.utils.PipePluginFrame;
import chav1961.calc.windows.DragDropGlass.DragNotification;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.SequenceIterator;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.basic.subscribable.SubscribableBoolean;
import chav1961.purelib.basic.subscribable.SubscribableInt;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.model.interfaces.NodeMetadataOwner;
import chav1961.purelib.ui.swing.useful.JLocalizedOptionPane;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.Application/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value = "chav1961.calc.workbench", tooltip = "chav1961.calc.workbench.tt", icon = "root:/WorkbenchTab!")
public class PipeManager extends JDesktopPane implements Closeable, LocaleChangeListener, ContentClassificator, ContentEnumerator, DragNotification {
	private static final long serialVersionUID = 1L;

	private static final String				VALIDATION_NO_INITIAL_NODE = "chav1961.calc.windows.pipemanager.validation.noInitial";
	private static final String				VALIDATION_NO_TERMINAL_NODES = "chav1961.calc.windows.pipemanager.validation.noTerminal";
	private static final String				VALIDATION_NO_WAY_TO_TERMINAL = "chav1961.calc.windows.pipemanager.validation.noWayToTerminal";
	private static final String				VALIDATION_UNCONDITIONAL_LOOP = "chav1961.calc.windows.pipemanager.validation.unconditionalLoop";
	private static final String				VALIDATION_OK = "chav1961.calc.windows.pipemanager.validation.OK";
	
	private final PipeTab					parent;
	private final Localizer					localizer;
	private final LoggerFacade				logger;
	private final List<PipePluginFrame<?>>	frames = new ArrayList<>();
	private final List<PipeLink>			links = new ArrayList<>();
	
	public PipeManager(final PipeTab parent, final Localizer localizer, final LoggerFacade logger, final ContentMetadataInterface xmlModel) throws IOException, EnvironmentException, ContentException {
		if (parent == null) {
			throw new NullPointerException("Parent tab can't be null");
		}
		else if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.parent = parent;
			this.localizer = localizer;
			this.logger = logger;
		}
	}
	
	@Override
	public void close() throws IOException {
		try{this.localizer.pop();
		} catch (LocalizationException e) {
		}
	}	

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings(oldLocale, newLocale);
	}

	@Override
	public ContentType classify(final int x, final int y) {
		final Point		location = new Point(x,y);	
		
		for (PipeLink item : links) {
			if (item.atCorner(x, y)) {
				return ContentType.SOURCE_OR_TARGET;
			}
			else if (item.intersects(x, y)) {
				return ContentType.LINK;
			}
		}
		
		for (JInternalFrame item : frames) {
			if (item.isVisible()) {
				if (!item.isIconifiable() && item.getVisibleRect().contains(location)) {
					final Point 	innerPoint = SwingUtilities.convertPoint(this,location,item);
					final Component component = SwingUtilities.getDeepestComponentAt(item,innerPoint.x,innerPoint.y);
					
					if (component instanceof NodeMetadataOwner) {
						final ContentNodeMetadata meta = ((NodeMetadataOwner)component).getNodeMetadata();
						
						if ("action".equalsIgnoreCase(URI.create(meta.getApplicationPath().getSchemeSpecificPart()).getScheme())) {
							return ContentType.CONTROL;
						}
						else {
							return ContentType.VALUE;
						}
					}
					else {
						return ContentType.CONTAINER;
					}
				}
				else if (item.isIconifiable() && item.getDesktopIcon().getVisibleRect().contains(location)) {
					return ContentType.CONTAINER_ICON;
				}
			}
		}
		return ContentType.FREE;
	}
	
	@Override
	public Iterator<PipeContainerItemInterface> iterator() {
		final List<Iterator<PipeContainerItemInterface>>	collection = new ArrayList<>();
		
		for (PipePluginFrame<?> item : frames) {
			collection.add(item.getItems().iterator());
		}
		
		return new SequenceIterator<PipeContainerItemInterface>(collection);
	}

	@Override
	public void process(final DragOperation oper, final PipeContainerItemInterface source, final PipeContainerItemInterface target) {
		// TODO Auto-generated method stub
		switch (oper) {
			case CREATE_LINK	:
				links.add(new PipeLink(source,target));
				break;
			case DROP_LINK		:
				links.remove(new PipeLink(source,target));
				break;
			case MOVE			:
				if (target instanceof PipeItemDropTarget) {
					((PipeItemDropTarget)target).drop(source);
				}
				break;
			default:
				throw new UnsupportedOperationException("Drag operation ["+oper+"] is not supported yet");
		}
	}
	
	public PipePluginFrame<?>[] getPipeComponents() {
		final PipePluginFrame<?>[]	result = new PipePluginFrame<?>[frames.size()];
		
		for (int index = 0, maxIndex = result.length; index < maxIndex; index++) {
			result[index] = frames.get(index);
		}
		return result;
	}

	public boolean hasComponentAt(int x, int y) {
		for (PipePluginFrame<?> item : frames) {
			if (((JInternalFrame)item).getBounds().contains(x, y)) {
				return true;
			}
		}
		return false;		
	}
	
	public PipePluginFrame<?> at(int x, int y) {
		for (PipePluginFrame<?> item : frames) {
			if (((JInternalFrame)item).getBounds().contains(x, y)) {
				return item;
			}
		}
		return null;		
	}
	
	public void addPipeComponent(final PipePluginFrame<?> item) {
		if (item == null) {
			throw new NullPointerException("Iem to add can't be null");
		}
		else {
			add((JInternalFrame)item);
			frames.add(item);
		}
	}

	public void removePipeComponent(final PipePluginFrame<?> item) {
		if (item == null) {
			throw new NullPointerException("Iem to remove can't be null");
		}
		else {
			remove((JInternalFrame)item);
			frames.remove(item);
		}
	}
	
	public void loadPipe(final InputStream is, final LoggerFacade logger) throws IOException, ContentException {
		// TODO Auto-generated method stub
		
	}

	public void storePipe(final OutputStream is, final LoggerFacade logger) throws IOException, ContentException {
		// TODO Auto-generated method stub
		
	}

	public void clean(final LoggerFacade facade) throws LocalizationException {
		if (new JLocalizedOptionPane(localizer).confirm(this, LocalizationKeys.CONFIRM_CLEAR_DESKTOP_MESSAGE, LocalizationKeys.CONFIRM_CLEAR_DESKTOP_CAPTION, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			for (JInternalFrame item : this.getAllFrames()) {
				this.getDesktopManager().closeFrame(item);
				if (item instanceof PipePluginFrame) {
					frames.remove(item);
				}
			}
		}
	}

	public boolean validatePipe() {
		return validatePipe(logger);
	}	
	
	public boolean validatePipe(final LoggerFacade facade) {
		// TODO Auto-generated method stub
		boolean 			initialDetected = false, terminalDetected = false;
		PipePluginFrame<?>	initial = null;
		
		for (PipePluginFrame<?> item : getPipeComponents()) {
			if (item.getType() == PipeItemType.INITIAL_ITEM) {
				initialDetected = true;
				initial = item;
			}
			else if (item.getType() == PipeItemType.TERMINAL_ITEM) {
				terminalDetected = true;
			}
		}
		if (!initialDetected) {
			facade.message(Severity.warning,VALIDATION_NO_INITIAL_NODE);
			return false;
		}
		if (!terminalDetected) {
			facade.message(Severity.warning,VALIDATION_NO_TERMINAL_NODES);
			return false;
		}
		if (!hasWay2Terminal(initial,new HashSet<>())) {
			facade.message(Severity.warning,VALIDATION_NO_WAY_TO_TERMINAL);
			return false;
		}
		if (hasLoop(initial,new HashSet<>())) {
			facade.message(Severity.warning,VALIDATION_UNCONDITIONAL_LOOP);
			return false;
		}
		for (PipePluginFrame<?> item : getPipeComponents()) {
			
		}
		facade.message(Severity.info,VALIDATION_OK);
		return true;		
	}
	
	public boolean start(final LoggerFacade facade) throws ContentException {
		if (!validatePipe(PureLibSettings.CURRENT_LOGGER)) {
			facade.message(Severity.error,"Start rejected because there are problems in the pipe. Validate it!");
			return false;
		}
		else {
			// TODO Auto-generated method stub
			
			return true;
		}
	}

	public boolean stop(final LoggerFacade facade) throws ContentException {
		// TODO Auto-generated method stub
		return false;
	}
	
	public DragMode setDragMode(final DragMode newMode) {
		return parent.setDragMode(newMode);
	}
	
	private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) {
		// TODO Auto-generated method stub
	}

	private boolean hasWay2Terminal(final PipePluginFrame<?> node, final Set<PipePluginFrame<?>> passed) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean hasLoop(final PipePluginFrame<?> node, final Set<PipePluginFrame<?>> passed) {
		// TODO Auto-generated method stub
		return false;
	}

	
	
	private static class PipeLink {
		final PipeContainerItemInterface source, target;

		public PipeLink(final PipeContainerItemInterface source, final PipeContainerItemInterface target) {
			this.source = source;
			this.target = target;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((source == null) ? 0 : source.hashCode());
			result = prime * result + ((target == null) ? 0 : target.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			PipeLink other = (PipeLink) obj;
			if (source == null) {
				if (other.source != null) return false;
			} else if (!source.equals(other.source)) return false;
			if (target == null) {
				if (other.target != null) return false;
			} else if (!target.equals(other.target)) return false;
			return true;
		}

		@Override
		public String toString() {
			return "PipeLink [source=" + source + ", target=" + target + "]";
		}
		
		public boolean intersects(int x, int y) {
			return false;
		}

		public boolean atCorner(int x, int y) {
			return false;
		}
	}
}
