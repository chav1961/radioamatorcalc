package chav1961.calc.references.tubes;

import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;

import chav1961.calc.references.interfaces.TubeConnector;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.useful.svg.SVGPainter;
import chav1961.purelib.ui.swing.useful.svg.SVGParser;

class ConnPicture extends JComponent implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 4745784785481072237L;
	private static final int		POINT_RECT = 10;
	private static final float		POINT_AREA = POINT_RECT * POINT_RECT;
	private static final float		LINE_AREA = 0.01f;
	private static final Cursor		ORDINAL_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	private static final Cursor		START_DRAG_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	private static final Cursor		DRAG_CURSOR = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	private static final Cursor		DROP_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	private static final Cursor		REMOVE_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	
	private final SVGPainter		rightPainter;
	private final Set<Point>		rightPoints = new HashSet<>();
	private final Set<Point>		leftPoints = new HashSet<>();
	private final List<Line2D>		lines = new ArrayList<>();
	private Point					currentPoint = new Point(0,0);
	private Point					fromPoint = null, toPoint = null;
	private SVGPainter				leftPainter = null;
	
	ConnPicture() throws ContentException {
		this.rightPainter = SVGParser.parse(getClass().getResourceAsStream("right.svg"));
		this.rightPoints.addAll(Arrays.asList(new Point(20, 20), new Point(40, 40)));
		this.leftPoints.addAll(Arrays.asList(new Point(20, 20), new Point(40, 40)));
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void setPinout(final InputStream svg) {
		if (svg == null) {
			throw new NullPointerException("SVG stream can't be null");
		}
		else {
			try {
				this.leftPainter = SVGParser.parse(svg);
				
				for(int index = lines.size()-1; index >= 0; index--) {
					if (!rightPoints.contains(lines.get(index).getP1()) || !rightPoints.contains(lines.get(index).getP2())) {
						lines.remove(index);
					}
				}
				repaint();
			} catch (ContentException e) {
				SwingUtils.getNearestLogger(this).message(Severity.warning, e, e.getLocalizedMessage());
			}
		}
	}
	
	TubeConnector[] getConnectors() {
		return null;
	}
	
	@Override
	protected void paintComponent(final Graphics g) {
		final Graphics2D		g2d = (Graphics2D)g;
		final AffineTransform	oldAt = g2d.getTransform();
		final Color				oldColor = g2d.getColor();
		final AffineTransform	leftAt = new AffineTransform(oldAt);
		final AffineTransform	rightAt = new AffineTransform(oldAt);

		g2d.setColor(getBackground());
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		rightAt.translate(getWidth()/2, getHeight());
		rightAt.scale(0.5f*getWidth()/rightPainter.getWidth(), -1.0f*getHeight()/rightPainter.getHeight());
		g2d.setTransform(rightAt);
		rightPainter.paint(g2d);
		if (leftPainter != null) {
			leftAt.translate(0, getHeight());
			rightAt.scale(0.5f*getWidth()/leftPainter.getWidth(), -1.0f*getHeight()/leftPainter.getHeight());
			g2d.setTransform(leftAt);
			leftPainter.paint(g2d);
		}
		g2d.setTransform(oldAt);
		
		for(Point item : rightPoints) {
			drawPoint(g2d, item, atPoint(currentPoint), getWidth()/2);
		}
		for(Point item : leftPoints) {
			drawPoint(g2d, item, atPoint(currentPoint), 0);
		}
		for(Line2D item : lines) {
			drawLine(g2d, item, atLine(currentPoint), 0, getWidth()/2);
		}
		if (fromPoint != null) {
			drawDragLine(g2d, fromPoint, currentPoint);
		}
		g2d.setColor(oldColor);
		g2d.setTransform(oldAt);
	}


	@Override
	public void mouseDragged(final MouseEvent e) {
		drawDragLine(fromPoint, currentPoint, 0, getWidth()/2);
		if (atPoint(currentPoint)) {
			repaint(point2Rect(getPointByPoint(currentPoint)));
		}
		repaint(points2Rect(fromPoint, currentPoint));
		currentPoint = e.getPoint();
		drawDragLine(fromPoint, e.getPoint(), 0, getWidth()/2);
		if (atPoint(e.getPoint())) {
			setCursor(DROP_CURSOR);
			repaint(point2Rect(getPointByPoint(currentPoint)));
		}
		else {
			setCursor(DRAG_CURSOR);
			repaint(points2Rect(fromPoint, currentPoint));
		}
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		if (atPoint(currentPoint)) {
			repaint(point2Rect(getPointByPoint(currentPoint)));
		}
		currentPoint = e.getPoint();
		if (atPoint(currentPoint)) {
			setCursor(START_DRAG_CURSOR);
			repaint(point2Rect(getPointByPoint(currentPoint)));
		}
		else if (atLine(currentPoint)) {
			setCursor(REMOVE_CURSOR);
			repaint(getVisibleRect());
		}
		else {
			setCursor(ORDINAL_CURSOR);
		}
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		if (atPoint(e.getPoint())) {
			lines.remove(getLineByPoint(e.getPoint()));
		}
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		if (atPoint(e.getPoint())) {
			fromPoint = getPointByPoint(e.getPoint()); 
			setCursor(DRAG_CURSOR);
			drawDragLine(fromPoint, e.getPoint(), 0, getWidth()/2);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (atPoint(e.getPoint())) {
			toPoint = getPointByPoint(e.getPoint());
			appendLine(fromPoint, toPoint);
			fromPoint = null;
			toPoint = null;
		}
		setCursor(ORDINAL_CURSOR);
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
	}

	@Override
	public void mouseExited(final MouseEvent e) {
		if (fromPoint != null) {
			drawDragLine(fromPoint, currentPoint, 0, getWidth()/2);
		}
	}

	private boolean isPointRight(final Point current) {
		return current.x >= getWidth()/2;
	}
	
	private Point toScreenPoint(final Point current) {
		return null;
	}

	private Point fromScreenPoint(final Point current) {
		return null;
	}
	
	
	private boolean atPoint(final Point current) {
		final Point	rightCurrent = new Point(current.x - getWidth()/2, current.y);
		
		for(Point item : rightPoints) {
			if (item.distanceSq(rightCurrent) < POINT_AREA) {
				return true;
			}
		}
		for(Point item : leftPoints) {
			if (item.distanceSq(current) < POINT_AREA) {
				return true;
			}
		}
		return false;
	}

	private boolean atLine(final Point current) {
		for(Line2D item : lines) {
			if (item.ptLineDist(current) < LINE_AREA) {
				return true;
			}
		}
		return false;
	}

	private Point getPointByPoint(final Point current) {
		final Point	rightCurrent = new Point(current.x - getWidth()/2, current.y);
		
		for(Point item : rightPoints) {
			if (item.distanceSq(rightCurrent) < POINT_AREA) {
				return new Point(item.x + getWidth()/2, item.y);
			}
		}
		for(Point item : leftPoints) {
			if (item.distanceSq(current) < POINT_AREA) {
				return item;
			}
		}
		return null;
	}

	private Line2D getLineByPoint(final Point current) {
		for(Line2D item : lines) {
			if (item.ptLineDist(currentPoint) < LINE_AREA) {
				return item;
			}
		}
		return null;
	}

	private void appendLine(final Point from, final Point to) {
		for(Line2D item : lines) {
			if (item.getP1().equals(from) && item.getP2().equals(to) || item.getP1().equals(to) && item.getP2().equals(from)) {
				return;
			}
		}
		lines.add(new Line2D.Float(from, to));
	}

	private Rectangle point2Rect(final Point point) {
		return new Rectangle(point.x - POINT_RECT/2, point.y - POINT_RECT/2, POINT_RECT, POINT_RECT);
	}

	private Rectangle points2Rect(final Point first, final Point second) {
		return new Rectangle(Math.min(first.x, second.x) - 1, Math.min(first.y, second.y) - 1, Math.abs(second.x - first.x) + 2, Math.abs(second.y - first.y) + 2);
	}
	
	private void drawPoint(final Graphics2D g2d, final Point item, final boolean highLight, final int deltaX) {
		final Color		oldColor = g2d.getColor();
		
		g2d.setColor(highLight ? Color.RED : Color.GRAY);
		g2d.fillOval(item.x+deltaX-POINT_RECT/2, item.y - POINT_RECT/2, POINT_RECT, POINT_RECT);
		g2d.setColor(Color.WHITE);
		g2d.drawOval(item.x+deltaX-POINT_RECT/2, item.y - POINT_RECT/2, POINT_RECT, POINT_RECT);
		g2d.setColor(oldColor);
	}
	
	private void drawLine(final Graphics2D g2d, final Line2D item, final boolean highLight, final int deltaX1, final int deltaX2) {
		// TODO Auto-generated method stub
		
	}

	private void drawDragLine(final Point from, final Point to, final int deltaX1, final int deltaX2) {
		// TODO Auto-generated method stub
		
	}
	
	private void drawDragLine(final Graphics2D g2d, final Point from, final Point to) {
		final Color		oldColor = g2d.getColor();
		final Stroke	oldStroke = g2d.getStroke();
		
		g2d.setColor(Color.BLUE);
		g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.0f, new float[] {5.0f, 5.0f}, 0));
		g2d.drawLine(from.x, from.y, to.x, to.y);
		g2d.setStroke(oldStroke);
		g2d.setColor(oldColor);
	}
	
}
