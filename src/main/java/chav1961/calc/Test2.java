package chav1961.calc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import chav1961.purelib.concurrent.LightWeightListenerList;

public class Test2 {
	public Test2() {
		
	}
	
	static boolean	pressed = false;
	static Point	last;
	
	public static void main(final String[] args) {
		final JFrame	f = new JFrame();
		final XPanel	x = new XPanel();
		
		x.sfm = new SelectionFrameManager(x, false);
		f.add(x);
		f.setVisible(true);
	}

	@FunctionalInterface
	public interface SelectionFrameListener {
		void selectionCompleted(Rectangle rect);
		default void selectionChanging(Rectangle rect) {}
		default void selectionStarted(Point pt) {}
		default void selectionCancelled(Rectangle rect) {}
	}
	
	public static class SelectionFrameManager {
		private static final int	STATE_INIT = 0;
		private static final int	STATE_DRAW = 1;
		private static final int	STATE_LOST_FOCUS = 2;
		private static final int	REFRESH_BOUND = 2;
		private static final Stroke	STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 5, new float[] {5}, 0);
		
		private final JComponent	background;
		private final LightWeightListenerList<SelectionFrameListener>	listeners = new LightWeightListenerList<>(SelectionFrameListener.class);
		private final Rectangle		currentFrame = new Rectangle();
		private final Rectangle		prevFrame = new Rectangle();
		private int					startX, startY;
		private int					currentX, currentY;
		private int					drawState = STATE_INIT; 
		
		public SelectionFrameManager(final JComponent component, final boolean keepSelectionOnMouseExit) {
			if (component == null) {
				throw new NullPointerException("Component can't be null"); 
			}
			else {
				this.background = component;
				
				component.addMouseListener(new MouseListener() {
					@Override public void mouseClicked(MouseEvent e) {}
					
					@Override
					public void mouseReleased(final MouseEvent e) {
						switch (drawState) {
							case STATE_INIT			:
								break;
							case STATE_DRAW			:
								drawState = STATE_INIT;
								currentX = e.getPoint().x;
								currentY = e.getPoint().y;
								prepareRectangle();
								listeners.fireEvent((l)->l.selectionCompleted(currentFrame));
								break;
							case STATE_LOST_FOCUS	:
								break;
							default :
								throw new UnsupportedOperationException("Draw state ["+drawState+"] is not supported yet");
						}
					}
					
					@Override
					public void mousePressed(final MouseEvent e) {
						switch (drawState) {
							case STATE_INIT			:
								drawState = STATE_DRAW;
								startX = currentX = e.getPoint().x;
								startY = currentY = e.getPoint().y;
								prepareRectangle();
								listeners.fireEvent((l)->l.selectionStarted(e.getPoint()));
								break;
							case STATE_DRAW	: case STATE_LOST_FOCUS :
								break;
							default :
								throw new UnsupportedOperationException("Draw state ["+drawState+"] is not supported yet");
						}
					}
					
					@Override
					public void mouseExited(final MouseEvent e) {
						switch (drawState) {
							case STATE_INIT			:
								break;
							case STATE_DRAW			:
								currentX = e.getPoint().x;
								currentY = e.getPoint().y;
								prepareRectangle();
								if (keepSelectionOnMouseExit) {
									drawState = STATE_LOST_FOCUS; 
								}
								else {
									drawState = STATE_INIT; 
									listeners.fireEvent((l)->l.selectionCancelled(currentFrame));
									startX = startY = currentX = currentY = 0;
									prepareRectangle();
								}
								refresh();
								break;
							case STATE_LOST_FOCUS	:
								break;
							default :
								throw new UnsupportedOperationException("Draw state ["+drawState+"] is not supported yet");
						}
					}
					
					@Override
					public void mouseEntered(MouseEvent e) {
						switch (drawState) {
							case STATE_INIT			:
								break;
							case STATE_DRAW			:
								break;
							case STATE_LOST_FOCUS	:
								currentX = e.getPoint().x;
								currentY = e.getPoint().y;
								prepareRectangle();
								refresh();
								drawState = STATE_DRAW; 
								listeners.fireEvent((l)->l.selectionChanging(currentFrame));
								break;
							default :
								throw new UnsupportedOperationException("Draw state ["+drawState+"] is not supported yet");
						}
					}
					
				});
				component.addMouseMotionListener(new MouseMotionListener() {
					@Override public void mouseMoved(MouseEvent e) {}
					
					@Override
					public void mouseDragged(MouseEvent e) {
						switch (drawState) {
							case STATE_INIT			:
								break;
							case STATE_DRAW			:
								currentX = e.getPoint().x;
								currentY = e.getPoint().y;
								prepareRectangle();
								refresh();
								listeners.fireEvent((l)->l.selectionChanging(currentFrame));
								break;
							case STATE_LOST_FOCUS	:
								break;
							default :
								throw new UnsupportedOperationException("Draw state ["+drawState+"] is not supported yet");
						}
					}
				});
			}
		}

		public void addSelectionFrameListener(final SelectionFrameListener l) {
			if (l == null) {
				throw new NullPointerException("Listener to add can't be null"); 
			}
			else {
				listeners.addListener(l);
			}
		}

		public void removeSelectionFrameListener(final SelectionFrameListener l) {
			if (l == null) {
				throw new NullPointerException("Listener to remove can't be null"); 
			}
			else {
				listeners.removeListener(l);
			}
		}

		public void drawSelection(final Graphics2D g2d) {
			final Stroke	oldStroke = g2d.getStroke();
			
			g2d.setXORMode(Color.white);
			g2d.setStroke(STROKE);
			g2d.drawRect(currentFrame.x, currentFrame.y, currentFrame.width, currentFrame.height);
			g2d.setStroke(oldStroke);
		}
		
		private void prepareRectangle() {
			prevFrame.setFrame(currentFrame);
			currentFrame.setFrameFromDiagonal(Math.min(startX, currentX), Math.min(startY, currentY), Math.max(startX, currentX), Math.max(startY, currentY));
		}
		
		private void refresh() {
			final Rectangle	rect = new Rectangle();
			
			rect.setFrameFromDiagonal(Math.min(currentFrame.x, prevFrame.x)-REFRESH_BOUND, Math.min(currentFrame.y, prevFrame.y)-REFRESH_BOUND, 
									  Math.max(currentFrame.x+currentFrame.width, prevFrame.x+prevFrame.width)+REFRESH_BOUND, 
									  Math.max(currentFrame.y+currentFrame.height, prevFrame.y+prevFrame.height)+REFRESH_BOUND);
			background.repaint(rect);
		}
	}
	
	private static class XPanel extends JPanel {
		SelectionFrameManager	sfm;
		
		 @Override
        public Dimension getPreferredSize() {
            return new Dimension(190, 320);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
        	final Graphics2D g2d = (Graphics2D)graphics;
        	
            super.paintComponent(graphics);
            g2d.setColor(Color.black);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(Color.green);
            g2d.fillRect(30, 40, 100, 200);
            g2d.setXORMode(Color.white);
            g2d.fillRect(60, 80, 100, 200);
            sfm.drawSelection(g2d);
        }
	}
}
