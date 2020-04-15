package chav1961.calc.windows;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
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
import chav1961.calc.interfaces.PipeContainerInterface;
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
import chav1961.purelib.basic.exceptions.PrintingException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.model.interfaces.NodeMetadataOwner;
import chav1961.purelib.streams.JsonStaxPrinter;
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

	private static final String				JSON_PIPE_ITEMS = "items";
	private static final String				JSON_PIPE_ITEM_COUNT = "itemCount";
	private static final String				JSON_PIPE_ITEM_LIST = "itemList";
	private static final String				JSON_PIPE_ITEM_ID = "id";
	private static final String				JSON_PIPE_ITEM_TYPE = "type";
	private static final String				JSON_PIPE_ITEM_GEOMETRY = "geometry";
	private static final String				JSON_PIPE_ITEM_GEOMETRY_X = "x";
	private static final String				JSON_PIPE_ITEM_GEOMETRY_Y = "y";
	private static final String				JSON_PIPE_ITEM_GEOMETRY_WIDTH = "width";
	private static final String				JSON_PIPE_ITEM_GEOMETRY_HEIGHT = "height";
	private static final String				JSON_PIPE_ITEM_GEOMETRY_ICONIZED = "iconized";
	private static final String				JSON_PIPE_ITEM_GEOMETRY_MAXIMIZED = "maximized";
	private static final String				JSON_PIPE_ITEM_LINKS = "links";

	private static final String				JSON_PIPE_ITEM_LINK_TYPE = "type";
	private static final String				JSON_PIPE_ITEM_LINK_TYPE_CONTROL = "control";
	private static final String				JSON_PIPE_ITEM_LINK_TYPE_DATA = "data";
	private static final String				JSON_PIPE_ITEM_LINK_SOURCE = "source";
	private static final String				JSON_PIPE_ITEM_LINK_SOURCE_CONTROL = "sourceControl";
	private static final String				JSON_PIPE_ITEM_LINK_TARGET = "target";
	private static final String				JSON_PIPE_ITEM_LINK_TARGET_CONTROL = "targetControl";
	
	private final PipeTab					parent;
	private final Localizer					localizer;
	private final LoggerFacade				logger;
	private final List<PipePluginFrame<?>>	frames = new ArrayList<>();

	private interface TotalMouseListener extends MouseListener, MouseMotionListener, MouseWheelListener {}

	private final TotalMouseListener		listener = new TotalMouseListener() {
												@Override public void mouseWheelMoved(MouseWheelEvent e) {}
												@Override public void mouseDragged(MouseEvent e) {}
												@Override public void mouseReleased(MouseEvent e) {}
												@Override public void mousePressed(MouseEvent e) {}
												@Override public void mouseExited(MouseEvent e) {}
												@Override public void mouseEntered(MouseEvent e) {}
												
												@Override 
												public void mouseMoved(MouseEvent e) {
													final LinksAndPoints	lap = detectLineIntersection(e.getPoint());
													
													if (lap != null) {
														highlightLine(lap);
													}
													else {
														clearHighlighing();
													}
												}
												
												@Override 
												public void mouseClicked(MouseEvent e) {
													final LinksAndPoints	lap = detectLineIntersection(e.getPoint());
													
													if (lap != null && e.getClickCount() >= 2) {
														removeLine(lap);
													}
												}
											};
	private volatile LinksAndPoints[]		links = null;
	private volatile PipeLink				linkHighlighted = null;
	
	@FunctionalInterface
	public interface PipeExecutionEndedCallback {
		void process(final Throwable exc);
	}
	
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
			addMouseListener(listener);
			addMouseMotionListener(listener);
		}
	}
	
	@Override
	public void close() throws IOException {
		try{removeMouseMotionListener(listener);			
			removeMouseListener(listener);
			this.localizer.pop();
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
			if (!item.validate(facade)) {
				return false;
			}
		}		
		facade.message(Severity.info,VALIDATION_OK);
		return true;		
	}
	
	public DragMode setDragMode(final DragMode newMode) {
		return parent.setDragMode(newMode);
	}
	
	public void refreshLinks() {
		for (LinksAndPoints pair : links = walkLinkPoints()) {
			final int	minX = Math.min(pair.from.x,pair.to.x), minY = Math.min(pair.from.y,pair.to.y);
			
			repaint(minX,minY,Math.abs(pair.from.x-pair.to.x),Math.abs(pair.from.y-pair.to.y));
		}
	}

	void serializeFrames(final JsonStaxPrinter printer) throws PrintingException, IOException {
		printer.splitter().name(JSON_PIPE_ITEMS).startObject();
			printer.name(JSON_PIPE_ITEM_COUNT).value(frames.size());
			printer.splitter().name(JSON_PIPE_ITEM_LIST).startArray();
			
			boolean		splitterRequired = false;
			
			for (PipePluginFrame<?> frame : frames) {
				final Rectangle	rect = frame.getBounds();
				
				if (splitterRequired) {
					printer.splitter();
				}
				printer.startObject();
					printer.name(JSON_PIPE_ITEM_ID).value(frame.getPipeItemName());
					printer.splitter().name(JSON_PIPE_ITEM_TYPE).value(frame.getType().toString());
					printer.splitter().name(JSON_PIPE_ITEM_GEOMETRY).startObject();
						printer.name(JSON_PIPE_ITEM_GEOMETRY_X).value(rect.x);
						printer.splitter().name(JSON_PIPE_ITEM_GEOMETRY_Y).value(rect.y);
						printer.splitter().name(JSON_PIPE_ITEM_GEOMETRY_WIDTH).value(rect.width);
						printer.splitter().name(JSON_PIPE_ITEM_GEOMETRY_HEIGHT).value(rect.height);
						printer.splitter().name(JSON_PIPE_ITEM_GEOMETRY_ICONIZED).value(frame.isIcon());
						printer.splitter().name(JSON_PIPE_ITEM_GEOMETRY_MAXIMIZED).value(frame.isMaximum());
					printer.endObject();
					frame.serializeFrame(printer);
				printer.endObject();
				splitterRequired = true;
			}
			printer.endArray();

			printer.splitter().name(JSON_PIPE_ITEM_LINKS).startArray();
			splitterRequired = false;
			for (PipePluginFrame<?> frame : frames) {
				for (PipeLink ctrl : frame.getLinks()) {
					if (splitterRequired) {
						printer.splitter();
					}
					printer.startObject();
						printer.name(JSON_PIPE_ITEM_LINK_TYPE).value(JSON_PIPE_ITEM_LINK_TYPE_CONTROL);
						printer.splitter().name(JSON_PIPE_ITEM_LINK_SOURCE).value(ctrl.getSource().getPipeItemName());
						printer.splitter().name(JSON_PIPE_ITEM_LINK_SOURCE_CONTROL).value(ctrl.getSourcePoint().getClass().getSimpleName());
						printer.splitter().name(JSON_PIPE_ITEM_LINK_TARGET).value(ctrl.getTarget().getPipeItemName());
						printer.splitter().name(JSON_PIPE_ITEM_LINK_TARGET_CONTROL).value(ctrl.getTargetPoint().getClass().getSimpleName());
					printer.endObject();
					splitterRequired = true;
				}
				for (PipeLink data : frame.getIncomingControls()) {
					if (splitterRequired) {
						printer.splitter();
					}
					printer.startObject();
						printer.name(JSON_PIPE_ITEM_LINK_TYPE).value(JSON_PIPE_ITEM_LINK_TYPE_DATA);
						if (data.getSource() == null) {
							printer.splitter().name(JSON_PIPE_ITEM_LINK_SOURCE).nullValue();
							printer.splitter().name(JSON_PIPE_ITEM_LINK_SOURCE_CONTROL).nullValue();
						}
						else {
							printer.splitter().name(JSON_PIPE_ITEM_LINK_SOURCE).value(data.getSource().getPipeItemName());
							printer.splitter().name(JSON_PIPE_ITEM_LINK_SOURCE_CONTROL).value(data.getAssociatedMeta() != null ? data.getAssociatedMeta().getName() : data.getMetadata().getName());
						}
						printer.splitter().name(JSON_PIPE_ITEM_LINK_TARGET).value(data.getTarget().getPipeItemName());
						printer.splitter().name(JSON_PIPE_ITEM_LINK_TARGET_CONTROL).value(data.getMetadata().getName());
					printer.endObject();
					splitterRequired = true;
				}
			}
			printer.endArray();
		printer.endObject();
	}
	
	private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) {
		// TODO Auto-generated method stub
	}

	private boolean hasLoop(final PipeContainerInterface node, final Set<PipeContainerInterface> passed) {
		if (passed.contains(node)) {
			return true;
		}
		else {
			passed.add(node);
			for (PipeContainerInterface item : collectReferences(node)) {
				if (hasLoop(item,passed)) {
					return true;
				}
			}
			return false;
		}
	}
	
	private PipeContainerInterface[] collectReferences(final PipeContainerInterface node) {
		final List<PipeContainerInterface>	result = new ArrayList<>();
		
		for (PipeContainerInterface item : getPipeComponents()) {
			for (PipeLink link : item.getLinks()) {
				if (link.getSource() == node) {
					result.add(item);
					break;
				}
			}
		}
		return result.toArray(new PipeContainerInterface[result.size()]);
	}

	private boolean hasWay2Terminal(final PipeContainerInterface initial, final PipeContainerInterface node) {
		if (node == initial) {
			return true;
		}
		else {
			for (PipeLink item : node.getLinks()) {
				if (item.getSource() != null && hasWay2Terminal(initial,item.getSource())) {
					return true;
				}
			}
			return false;
		}
	}

	private void paintLinks(final Graphics2D g) {
		final Color		oldColor = g.getColor();
		final Stroke	oldStroke= g.getStroke();
		
		g.setStroke(new BasicStroke(3));
		
		for (LinksAndPoints pair : walkLinkPoints()) {
			if (pair.link == linkHighlighted) {
				g.setColor(Color.RED);
			}
			else {
				g.setColor(Color.BLUE);
			}
			drawArrowLine(g,pair.to.x,pair.to.y,pair.from.x,pair.from.y,15,7);
		}
		
		g.setStroke(oldStroke);
		g.setColor(oldColor);
	}

	private LinksAndPoints[] walkLinkPoints() {
		final List<LinksAndPoints>	result = new ArrayList<>();
		
		for (PipePluginFrame<?> item : frames) {
			final JControlTargetLabel	label = item.getControlTarget();

			if (label != null) {
				for (PipeLink target : item.getLinks()) {
					result.add(new LinksAndPoints(target,centralPoint(this,label), centralPoint(this,target.getSourcePoint()))); 
				}
			}
		}
		return result.toArray(new LinksAndPoints[result.size()]);
	}
	
	private static Point centralPoint(final Component owner, final Component anchor) {
		final Point	result = new Point(anchor.getWidth()/2,anchor.getHeight()/2); 
		
		SwingUtilities.convertPointToScreen(result,anchor);
		SwingUtilities.convertPointFromScreen(result,owner);
		return result;
	}

	private void removeLine(final LinksAndPoints lap) {
		((PipePluginFrame<?>)lap.link.getTarget()).removeLink(lap.link);
		refreshLinks();
		repaint();
	}

	private void clearHighlighing() {
		linkHighlighted = null;
		refreshLinks();
	}

	private void highlightLine(final LinksAndPoints lap) {
		linkHighlighted = lap.link;
		refreshLinks();
	}

	private LinksAndPoints detectLineIntersection(final Point point) {
		final LinksAndPoints[]	list = links;
		
		if (list != null) {
			for (LinksAndPoints item : list) {
				if (item.intersects(point)) {
					return item;
				}
			}
		}
		return null;
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
	
	private static class LinksAndPoints {
		private final PipeLink	link;
		private final Point		from;
		private final Point		to;
		private final double	length;
		
		public LinksAndPoints(final PipeLink link, final Point from, final Point to) {
			this.link = link;
			this.from = from;
			this.to = to;
			this.length = Math.sqrt((from.x - to.x)*(from.x - to.x) + (from.y - to.y)*(from.y - to.y));
		}
		
		public boolean intersects(final Point p) {
			return Math.abs(Math.sqrt((from.x - p.x)*(from.x - p.x) + (from.y - p.y)*(from.y - p.y)) + Math.sqrt((p.x - to.x)*(p.x - to.x) + (p.y - to.y)*(p.y - to.y)) - length) < 2;
		}
	}
}
