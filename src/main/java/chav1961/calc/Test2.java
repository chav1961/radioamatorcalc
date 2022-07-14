package chav1961.calc;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class Test2 extends JFrame {
   JPanel mainPanel = new JPanel() {
      ImageIcon originalIcon = new ImageIcon("~/Pictures/apple.png");

      ImageFilter filter = new RGBImageFilter() {
         int transparentColor = Color.white.getRGB() | 0xFF000000;

         public final int filterRGB(int x, int y, int rgb) {
            if ((rgb | 0xFF000000) == transparentColor) {
               return 0x00FFFFFF & rgb;
            } else {
               return rgb;
            }
         }
      };

      ImageProducer filteredImgProd = new FilteredImageSource(originalIcon.getImage().getSource(), filter);
      Image transparentImg = Toolkit.getDefaultToolkit().createImage(filteredImgProd);

      public void paintComponent(Graphics g) {
         g.setColor(getBackground());
         g.fillRect(0, 0, getSize().width, getSize().height);

         // draw the original icon
         g.drawImage(originalIcon.getImage(), 100, 10, this);
         // draw the transparent icon
         g.drawImage(transparentImg, 140, 10, this);
      }
   };

   public Test2() {
      super("Transparency Example");

      JPanel content = (JPanel)getContentPane();
      mainPanel.setBackground(Color.black);
      content.add("Center", mainPanel);
   }

   public static void main(String[] argv) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            Test2 c = new Test2();
            c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            c.setSize(280,100);
            c.setVisible(true);
         }
      });
   }
   /*
   
   @Override protected void paintComponent(Graphics g) {
	   Graphics2D g2 = (Graphics2D) g.create();
	   g2.setPaint(getForeground());
	   Insets i = getInsets();
	   float x = i.left;
	   float y = i.top;
	   int w = getWidth() - i.left - i.right;
	   AttributedString as = new AttributedString(getText());
	   as.addAttribute(TextAttribute.FONT, getFont());
	   AttributedCharacterIterator aci = as.getIterator();
	   FontRenderContext frc = g2.getFontRenderContext();
	   LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);
	   while (lbm.getPosition() < aci.getEndIndex()) {
	     TextLayout tl = lbm.nextLayout(w);
	     tl.draw(g2, x, y + tl.getAscent());
	     y += tl.getDescent() + tl.getLeading() + tl.getAscent();
	   }
	   g2.dispose();
	 }
	 */ 
}