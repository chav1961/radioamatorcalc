package chav1961.calc.references.tubes;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;

import chav1961.calc.references.interfaces.TubeConnector;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.ui.swing.useful.svg.SVGPainter;
import chav1961.purelib.ui.swing.useful.svg.SVGParser;

public class ConnPicture extends JComponent implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 4745784785481072237L;
	private static final float		POINT_AREA = 0.01f;
	private static final float		LINE_AREA = 0.01f;
	private static final Cursor		ORDINAL_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	private static final Cursor		START_DRAG_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	private static final Cursor		DRAG_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	private static final Cursor		DROP_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	private static final Cursor		REMOVE_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	
	private final SVGPainter		rightPainter;
	private final Set<Point>		rightPoints = new HashSet<>();
	private final Set<Point>		leftPoints = new HashSet<>();
	private final List<Line2D>		lines = new ArrayList<>();
	private Point					currentPoint = new Point(0,0);
	private Point					fromPoint = null, toPoint = null;
	private SVGPainter				leftPainter = null;
	
	public ConnPicture() throws ContentException {
		this.rightPainter = SVGParser.parse(getClass().getResourceAsStream(""));
	}

	public void setPinout(final InputStream svg) throws ContentException {
		if (svg == null) {
			throw new NullPointerException("SVG stream can't be null");
		}
		else {
			this.leftPainter = SVGParser.parse(svg);

			for(int index = lines.size()-1; index >= 0; index--) {
				if (!rightPoints.contains(lines.get(index).getP1()) || !rightPoints.contains(lines.get(index).getP2())) {
					lines.remove(index);
				}
			}
			repaint();
		}
	}
	
	public TubeConnector[] getConnectors() {
		return null;
	}
	
	@Override
	protected void paintComponent(final Graphics g) {
		final Graphics2D		g2d = (Graphics2D)g;
		final AffineTransform	oldAt = g2d.getTransform();
		final AffineTransform	leftAt = new AffineTransform(oldAt);
		final AffineTransform	rightAt = new AffineTransform(oldAt);
	
		rightAt.translate(0, getHeight());
		rightAt.scale(1, -1);
		g2d.setTransform(rightAt);
		rightPainter.paint(g2d);
		if (leftPainter != null) {
			leftAt.translate(0, getHeight());
			leftAt.scale(1, -1);
			g2d.setTransform(leftAt);
			leftPainter.paint(g2d);
		}
		g2d.setTransform(oldAt);
		
		for(Point item : rightPoints) {
			drawPoint(g2d, item, atPoint(currentPoint));
		}
		for(Point item : leftPoints) {
			drawPoint(g2d, item, atPoint(currentPoint));
		}
		for(Line2D item : lines) {
			drawLine(g2d, item, atLine(currentPoint));
		}
		if (fromPoint != null) {
			
		}
	}

	@Override
	public void mouseDragged(final MouseEvent e) {
		drawDragLine(fromPoint, currentPoint);
		currentPoint = e.getPoint();
		drawDragLine(fromPoint, e.getPoint());
		if (atPoint(e.getPoint())) {
			setCursor(DROP_CURSOR);
		}
		else {
			setCursor(DRAG_CURSOR);
		}
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		currentPoint = e.getPoint();
		if (atPoint(e.getPoint())) {
			setCursor(START_DRAG_CURSOR);
		}
		else if (atLine(e.getPoint())) {
			setCursor(REMOVE_CURSOR);
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
			drawDragLine(fromPoint, e.getPoint());
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
			drawDragLine(fromPoint, currentPoint);
		}
	}

	private boolean atPoint(final Point current) {
		for(Point item : rightPoints) {
			if (item.distanceSq(current) < POINT_AREA) {
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
		for(Point item : rightPoints) {
			if (item.distanceSq(current) < POINT_AREA) {
				return item;
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

	private void drawPoint(final Graphics2D g2d, final Point item, final boolean highLight) {
		// TODO Auto-generated method stub
		
	}
	
	private void drawLine(final Graphics2D g2d, final Line2D item, final boolean highLight) {
		// TODO Auto-generated method stub
		
	}

	private void drawDragLine(final Point from, final Point to) {
		// TODO Auto-generated method stub
		
	}
}
