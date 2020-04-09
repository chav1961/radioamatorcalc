package chav1961.calc.windows;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
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

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import chav1961.calc.LocalizationKeys;
import chav1961.calc.interfaces.ContentEnumerator;
import chav1961.calc.interfaces.DragMode;
import chav1961.calc.interfaces.PipeContainerInterface.PipeItemType;
import chav1961.calc.interfaces.PipeContainerItemInterface;
import chav1961.calc.pipe.JControlLabel;
import chav1961.calc.pipe.JControlTargetLabel;
import chav1961.calc.utils.PipeLink;
import chav1961.calc.utils.PipePluginFrame;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.SequenceIterator;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
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
public class PipeManager extends JDesktopPane implements Closeable, LocaleChangeListener, ContentEnumerator {
	private static final long serialVersionUID = 1L;

	private static final String				VALIDATION_NO_INITIAL_NODE = "chav1961.calc.windows.pipemanager.validation.noInitial";
	private static final String				VALIDATION_MULTIPLE_INITIAL_NODE = "chav1961.calc.windows.pipemanager.validation.multipleInitial";
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
	public Iterator<PipeContainerItemInterface> iterator() {
		final List<Iterator<PipeContainerItemInterface>>	collection = new ArrayList<>();
		
		for (PipePluginFrame<?> item : frames) {
			collection.add(item.getItems().iterator());
		}
		
		return new SequenceIterator<PipeContainerItemInterface>(collection);
	}

	@Override
	public void paint(final Graphics g) {
		super.paint(g);
		paintLinks((Graphics2D)g);
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
		boolean 					initialDetected = false, terminalDetected = false;
		PipePluginFrame<?>			initial = null;
		Set<PipePluginFrame<?>>		terminals = new HashSet<>();
		
		for (PipePluginFrame<?> item : getPipeComponents()) {
			if (item.getType() == PipeItemType.INITIAL_ITEM) {
				if (!initialDetected) {
					initialDetected = true;
					initial = item;
				}
				else {
					facade.message(Severity.warning,VALIDATION_MULTIPLE_INITIAL_NODE);
					return false;
				}
			}
			else if (item.getType() == PipeItemType.TERMINAL_ITEM) {
				terminalDetected = true;
				terminals.add(item);
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
		if (hasLoop(initial,new HashSet<>())) {
			facade.message(Severity.warning,VALIDATION_UNCONDITIONAL_LOOP);
			return false;
		}
		for (PipePluginFrame<?> item : terminals) {
			if (!hasWay2Terminal(initial,item)) {
				facade.message(Severity.warning,VALIDATION_NO_WAY_TO_TERMINAL);
				return false;
			}
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
	
	public void refreshLinks() {
		for (Point[] pair : walkLinkPoints()) {
			final int	minX = Math.min(pair[0].x,pair[1].x), minY = Math.min(pair[0].y,pair[1].y);
			
			repaint(minX,minY,Math.abs(pair[0].x-pair[1].x),Math.abs(pair[0].y-pair[1].y));
		}
	}
	
	
	private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) {
		// TODO Auto-generated method stub
	}

	private boolean hasLoop(final PipePluginFrame<?> node, final Set<PipePluginFrame<?>> passed) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private boolean hasWay2Terminal(final PipePluginFrame<?> node, final PipePluginFrame<?> terminal) {
		// TODO Auto-generated method stub
		return false;
	}

	private void paintLinks(final Graphics2D g) {
		final Color		oldColor = g.getColor();
		final Stroke	oldStroke= g.getStroke();
		
		g.setColor(Color.BLUE);
		g.setStroke(new BasicStroke(3));
		
		for (Point[] pair : walkLinkPoints()) {
			drawArrowLine(g,pair[1].x,pair[1].y,pair[0].x,pair[0].y,15,7);
		}
		
		g.setStroke(oldStroke);
		g.setColor(oldColor);
	}

	private Point[][] walkLinkPoints() {
		final List<Point[]>	result = new ArrayList<>();
		
		for (PipePluginFrame<?> item : frames) {
			final JControlTargetLabel	label = item.getControlTarget();

			if (label != null) {
				for (PipeLink target : item.getLinks()) {
					result.add(new Point[] {centralPoint(this,label), centralPoint(this,target.getSourcePoint())}); 
				}
			}
		}
		return result.toArray(new Point[result.size()][]);
	}
	
	private static Point centralPoint(final Component owner, final Component anchor) {
		final Point	result = new Point(anchor.getWidth()/2,anchor.getHeight()/2); 
		
		SwingUtilities.convertPointToScreen(result,anchor);
		SwingUtilities.convertPointFromScreen(result,owner);
		return result;
	}

	/**
	 * Draw an arrow line between two points.
	 * @param g the graphics component.
	 * @param x1 x-position of first point.
	 * @param y1 y-position of first point.
	 * @param x2 x-position of second point.
	 * @param y2 y-position of second point.
	 * @param d  the width of the arrow.
	 * @param h  the height of the arrow.
	 * @see https://stackoverflow.com/questions/2027613/how-to-draw-a-directed-arrow-line-in-java
	 */
	private static void drawArrowLine(final Graphics2D g, int x1, int y1, int x2, int y2, int d, int h) {
	    final int dx = x2 - x1, dy = y2 - y1;
	    final double D = Math.sqrt(dx*dx + dy*dy);
	    double xm = D - d, xn = xm, ym = h, yn = -h, x;
	    double sin = dy / D, cos = dx / D;

	    x = xm*cos - ym*sin + x1;
	    ym = xm*sin + ym*cos + y1;
	    xm = x;

	    x = xn*cos - yn*sin + x1;
	    yn = xn*sin + yn*cos + y1;
	    xn = x;

	    int[] xpoints = {x2, (int) xm, (int) xn};
	    int[] ypoints = {y2, (int) ym, (int) yn};

	    g.drawLine(x1, y1, x2, y2);
	    g.fillPolygon(xpoints, ypoints, 3);
	}
}
