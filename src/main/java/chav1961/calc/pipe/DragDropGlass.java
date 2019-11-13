package chav1961.calc.pipe;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import chav1961.calc.interfaces.ContentClassificator;
import chav1961.calc.interfaces.DragMode;

public class DragDropGlass extends JComponent {
	private static final long 		serialVersionUID = 1L;
	private static final Color[]	COLORS_NORMAL = {};
	private static final Color[]	COLORS_SELECTED = {};
	private static final Cursor[]	CURSORS = {};

	static {
		
	}
	
	public DragDropGlass(final JComponent back, final ContentClassificator classificator) {
		setOpaque(false);
	}

	public void setDragMode(final DragMode mode) {
		
	}
	
	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
	}
	
	private void pickCoordinates(final Graphics2D g2d) {
		
	}
	
	private void drawCircle(final int x, final int y, final Color color) {
		
	}
	
	private void drawArrow(final int x1, final int y1, final int x2, final int y2, final Color color) {
		
	}

	private void pushCursor(final Cursor cursor) {
		
	}

	private Cursor popCursor() {
		return null;
	}
	
	private Cursor changeCursor(final Cursor cursor) {
		return null;
	}
	
	private void redirectMouseEvents(final MouseEvent e) {
		
	}
	
	private JComponent getUnderlayedComponent(int x, int y) {
		return null;
	}
}
