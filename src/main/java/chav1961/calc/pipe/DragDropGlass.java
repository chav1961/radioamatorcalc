package chav1961.calc.pipe;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import chav1961.calc.interfaces.ContentClassificator;
import chav1961.calc.interfaces.ContentEnumerator;
import chav1961.calc.interfaces.DragMode;
import chav1961.calc.interfaces.PipeContainerItemInterface;
import chav1961.calc.interfaces.PipeContainerItemInterface.ContainerItemType;

public class DragDropGlass extends JComponent implements MouseListener, MouseMotionListener, MouseWheelListener {
	private static final long 		serialVersionUID = 1L;
	
	private static final int		CIRCLE_RADIUS = 10;
	private static final int		CIRCLE_RADIUS_2 = CIRCLE_RADIUS * CIRCLE_RADIUS;
	private static final float		ARROW_ANGLE = (float)Math.toRadians(15);
	private static final float		ARROW_LENGTH = 20;
	private static final Color[]	COLORS_NORMAL = {};
	private static final Color[]	COLORS_SELECTED = {};
	private static final Cursor[]	CURSORS = {};

	@FunctionalInterface
	public interface DragNotification {
		public enum DragOperation {
			MOVE, CREATE_LINK, DROP_LINK
		}
		
		void process(final DragOperation oper, final PipeContainerItemInterface source, final PipeContainerItemInterface target);
	}
	
	
	private enum MouseEventType {
		WHEEL_MOVED, DRAGGED, MOVED, CLICKED, PRESSED, RELEASED, ENTERED, EXITED
	}
	
	static {
		
	}
	
	private final Container				container;
	private final ContentClassificator	classificator;
	private final ContentEnumerator		enumerator;
	private final List<Cursor>			cursorStack = new ArrayList<>();
	
	private DragMode					currentDragMode = DragMode.NONE;
	private Point						startDragPoint = null;
	private Cursor						prevCursor;
	
	public DragDropGlass(final Container container, final ContentClassificator classificator, final ContentEnumerator enumerator) {
		setOpaque(false);
		this.container = container;
		this.classificator = classificator;
		this.enumerator = enumerator;
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		setTransferHandler(null);
		setCursor(prevCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	public void setDragMode(final DragMode mode, final DragNotification notificator) {
		switch (currentDragMode = mode) {
			case CONTROLS	:
				break;
			case LINKS		:
				break;
			case NONE		:
				break;
			default:
				throw new UnsupportedOperationException("Drag mode ["+currentDragMode+"] is not supported yet");
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (needCheckOwnContent() && locatedOnOwnContent(e)) {
			processMouseEvents(MouseEventType.WHEEL_MOVED,e);
		}
		else {
			redirectMouseEvents(e,false);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (needCheckOwnContent() && locatedOnOwnContent(e)) {
			processMouseEvents(MouseEventType.DRAGGED,e);
		}
		else {
			redirectMouseEvents(e,false);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (needCheckOwnContent() && locatedOnOwnContent(e)) {
			processMouseEvents(MouseEventType.MOVED,e);
		}
		else {
			redirectMouseEvents(e,false);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (needCheckOwnContent() && locatedOnOwnContent(e)) {
			processMouseEvents(MouseEventType.CLICKED,e);
		}
		else {
			redirectMouseEvents(e,false);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (needCheckOwnContent() && locatedOnOwnContent(e)) {
			processMouseEvents(MouseEventType.PRESSED,e);
		}
		else {
			redirectMouseEvents(e,false);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (needCheckOwnContent() && locatedOnOwnContent(e)) {
			processMouseEvents(MouseEventType.RELEASED,e);
		}
		else {
			redirectMouseEvents(e,false);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (needCheckOwnContent() && locatedOnOwnContent(e)) {
			processMouseEvents(MouseEventType.ENTERED,e);
		}
		else {
			redirectMouseEvents(e,false);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (needCheckOwnContent() && locatedOnOwnContent(e)) {
			processMouseEvents(MouseEventType.EXITED,e);
		}
		else {
			redirectMouseEvents(e,false);
		}
	}
	
	@Override
	protected void paintComponent(final Graphics g) {
		final Graphics2D		g2d = (Graphics2D)g; 
		final AffineTransform	oldAt = g2d.getTransform();
		
		pickCoordinates((Graphics2D)g);
		for (PipeContainerItemInterface item : enumerator) {
			final Point	center = rectangleCenter(item.getLocation());

			drawCircle(g2d,center.x,center.y,item.getType(),item.canUseAsTarget(currentDragMode));
		}
		for (PipeContainerItemInterface item : enumerator) {
			final Point	source = rectangleCenter(item.getLocation());

			for (PipeContainerItemInterface link : item.getOutgoingLinks()) {
				final Point	target = rectangleCenter(item.getLocation());

				drawArrow(g2d,buildArrow(source.x,source.y,target.x,target.y),item.getType());
			}

		}
		g2d.setTransform(oldAt);
	}
	
	private void pickCoordinates(final Graphics2D g2d) {
	}

	private static void drawCircle(final Graphics2D g2d, final int x, final int y, final ContainerItemType type, final boolean asTarget) {
		final Color		oldColor = g2d.getColor();
		final Ellipse2D	ellipse = new Ellipse2D.Float(x,y,CIRCLE_RADIUS,CIRCLE_RADIUS);
		
		switch (type) {
			case CONTROL	:
				g2d.setColor(Color.BLUE);
				break;
			case FIELD		:
				g2d.setColor(Color.GREEN);
				break;
			default			:
				break;
		}
		g2d.fill(ellipse);
		g2d.setColor(oldColor);
	}
	
	private static Path2D buildArrow(final int x1, final int y1, final int x2, final int y2) {
		final Path2D	path = new Path2D.Float();
		final float		angle = (float) Math.atan2(y2-y1,x2-x1);
		final float		length = (float)Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1)), arrowLength = Math.max(length,ARROW_LENGTH);
        final float 	xL1 = (float) (x2 + arrowLength * Math.cos(angle + ARROW_ANGLE)), yL1 = (float) (y2 + arrowLength * Math.sin(angle + ARROW_ANGLE));
        final double 	xL2 = x2 + arrowLength * Math.cos(angle - ARROW_ANGLE), yL2 = y2 + arrowLength * Math.sin(angle - ARROW_ANGLE);
		
		path.moveTo(x1,y1);
		path.lineTo(x2,y2);
	    path.lineTo(xL1, yL1);
	    path.moveTo(x2,y2);
	    path.lineTo(xL2, yL2);
		
	    return path;
	}

	private static void drawArrow(final Graphics2D g2d, final Path2D arrow, final ContainerItemType type) {
		final Color		oldColor = g2d.getColor();
		final Stroke	oldStroke = g2d.getStroke();
		
		switch (type) {
			case CONTROL	:
				g2d.setColor(Color.BLUE);
				break;
			case FIELD		:
				g2d.setColor(Color.GREEN);
				break;
			default			:
				break;
		}
		g2d.setStroke(new BasicStroke(1));
		g2d.draw(arrow);
		g2d.setStroke(oldStroke);
		g2d.setColor(oldColor);
	}
	
	public static Point rectangleCenter(final Rectangle rect) {
		return new Point(rect.x+rect.width/2,rect.y+rect.height/2);
	}

	private void pushCursor(final Cursor cursor) {
		cursorStack.add(0,getCursor());
		changeCursor(cursor);
	}

	private Cursor popCursor() {
		if (cursorStack.isEmpty()) {
			throw new IllegalStateException("Cursor stack exhausted");
		}
		else {
			return cursorStack.remove(0);
		}
	}
	
	private Cursor changeCursor(final Cursor cursor) {
		final Cursor	result = getCursor();
		
		setCursor(cursor);
		return result;
	}

	private boolean needCheckOwnContent() {
		return false;
	}	
	
	private boolean locatedOnOwnContent(final MouseEvent e) {
		return false;
	}
	
	private void redirectMouseEvents(final MouseEvent e, final boolean repaint) {
		final JComponent	underlying = getUnderlayedComponent(e.getX(),e.getY());

        if (underlying != null) {
            final Point componentPoint = SwingUtilities.convertPoint(this,e.getPoint(),underlying);
            
            underlying.dispatchEvent(new MouseEvent(underlying,e.getID(),e.getWhen(),e.getModifiers(),componentPoint.x,componentPoint.y,e.getClickCount(),e.isPopupTrigger()));
        }

	    if (repaint) {
	        repaint();
	    }	
	}
	
	private JComponent getUnderlayedComponent(int x, int y) {
		final Point 	glassPanePoint = new Point(x,y);
		final Point 	containerPoint = SwingUtilities.convertPoint(this,glassPanePoint,container);

	    if (containerPoint.y >= 0) {
	        final Component component = SwingUtilities.getDeepestComponentAt(container,containerPoint.x,containerPoint.y);
	    
	        return component instanceof JComponent ? (JComponent)component : null;
	    }
	    else {
			return null;
	    }
	}

	private PipeContainerItemInterface getUnderlayingItem(int x, int y) {
		switch (currentDragMode) {
			case CONTROLS:
				for (PipeContainerItemInterface item : enumerator) {
					if (item.getLocation().contains(x, y)) {
						return item;
					}
				}
				break;
			case LINKS:
				for (PipeContainerItemInterface item : enumerator) {
					if (item.getLocation().contains(x, y)) {
						final Point	center = rectangleCenter(item.getLocation());
						
						if ((center.x - x) * (center.x - x) + (center.y - y) * (center.y - y) <= CIRCLE_RADIUS_2) {
							return item;
						}
					}
				}
				break;
			case NONE:
				break;
			default:
				throw new UnsupportedOperationException("Drag mode ["+currentDragMode+"] is not supported yet");
		}
		return null;
	}

	private void notifyDnd(final DragMode operation, final Point source, final Point target) {
		
	}
	
	private void processMouseEvents(final MouseEventType type, final MouseEvent e) {
		PipeContainerItemInterface	item;
		
		switch (type) {
			case CLICKED	:
				break;
			case DRAGGED	:
				switch (currentDragMode) {
					case CONTROLS	:
						if ((item = getUnderlayingItem(e.getPoint().x, e.getPoint().y)) != null && item.canUseAsTarget(currentDragMode)) {
							prevCursor = changeCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
						}
						else {
							setCursor(prevCursor);
						}
						break;
					case LINKS		:
						if ((item = getUnderlayingItem(e.getPoint().x, e.getPoint().y)) != null && item.canUseAsTarget(currentDragMode)) {
							prevCursor = changeCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
						}
						else {
							setCursor(prevCursor);
						}
						break;
					case NONE		:
						break;
					default:
						throw new UnsupportedOperationException("Mouse event type ["+type+"] for drag mode ["+currentDragMode+"] is not supported yet");
				}
				break;
			case ENTERED	:
				break;
			case EXITED		:
				break;
			case MOVED		:
				switch (currentDragMode) {
					case CONTROLS	:
						if ((item = getUnderlayingItem(e.getPoint().x, e.getPoint().y)) != null && item.canUseAsSource(currentDragMode)) {
							prevCursor = changeCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
						}
						else {
							setCursor(prevCursor);
						}
						getTransferHandler().exportAsDrag(this,e,TransferHandler.MOVE);
						break;
					case LINKS		:
						if ((item = getUnderlayingItem(e.getPoint().x, e.getPoint().y)) != null && item.canUseAsSource(currentDragMode)) {
							prevCursor = changeCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
						}
						else {
							setCursor(prevCursor);
						}
						getTransferHandler().exportAsDrag(this,e,TransferHandler.LINK);
						break;
					case NONE		:
						break;
					default:
						throw new UnsupportedOperationException("Mouse event type ["+type+"] for drag mode ["+currentDragMode+"] is not supported yet");
				}
				break;
			case PRESSED	:
				switch (currentDragMode) {
					case CONTROLS	:
						if ((item = getUnderlayingItem(e.getPoint().x, e.getPoint().y)) != null && item.canUseAsSource(currentDragMode)) {
							setTransferHandler(new TransferHandler("info"));
							startDragPoint = e.getPoint();
	 						prevCursor = changeCursor(DragSource.DefaultMoveDrop);
						}
						break;
					case LINKS		:
						if ((item = getUnderlayingItem(e.getPoint().x, e.getPoint().y)) != null && item.canUseAsSource(currentDragMode)) {
							setTransferHandler(new TransferHandler("info"));
							startDragPoint = e.getPoint();
	 						prevCursor = changeCursor(DragSource.DefaultLinkDrop);
						}
						break;
					case NONE		:
						break;
					default:
						throw new UnsupportedOperationException("Mouse event type ["+type+"] for drag mode ["+currentDragMode+"] is not supported yet");
				}
				break;
			case RELEASED	:
				switch (currentDragMode) {
					case CONTROLS	:
						popCursor();
						if ((item = getUnderlayingItem(e.getPoint().x, e.getPoint().y)) != null && item.canUseAsTarget(currentDragMode)) {
							notifyDnd(currentDragMode,startDragPoint,e.getPoint());
						}
 						setCursor(prevCursor);
						setTransferHandler(null);
						startDragPoint = null;
						break;
					case LINKS		:
						if ((item = getUnderlayingItem(e.getPoint().x, e.getPoint().y)) != null && item.canUseAsTarget(currentDragMode)) {
							notifyDnd(currentDragMode,startDragPoint,e.getPoint());
						}
 						setCursor(prevCursor);
						setTransferHandler(null);
						startDragPoint = null;
						break;
					case NONE		:
						break;
					default:
						throw new UnsupportedOperationException("Mouse event type ["+type+"] for drag mode ["+currentDragMode+"] is not supported yet");
				}
				break;
			case WHEEL_MOVED:
				break;
			default:
				throw new UnsupportedOperationException("Mouse event type ["+type+"] is not supported yet");
		}
	}
}
