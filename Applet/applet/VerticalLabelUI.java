package applet;


import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicLabelUI;

class VerticalLabelUI extends BasicLabelUI
{
   private Rectangle paintIconR = new Rectangle();
   private Rectangle paintTextR = new Rectangle();
   private Rectangle paintViewR = new Rectangle();
   private Insets paintViewInsets = new Insets(0, 0, 0, 0);
   protected boolean clockwise;
   
   VerticalLabelUI( boolean clockwise )
   {
      super();
      this.clockwise = clockwise;
   } // VerticalLabelUI( boolean clockwise )
   
   
   public Dimension getPreferredSize(JComponent c)
   {
      Dimension dim = super.getPreferredSize(c);
      return new Dimension( dim.height, dim.width );
   } // public Dimension getPreferredSize(JComponent c)
   
    @Override
   public void paint(Graphics g, JComponent c)
   {
      JLabel label = (JLabel)c;
      String text = label.getText();
      Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();
      
      if ((icon == null) && (text == null))
      {
         return;
      } // if ((icon == null) && (text == null))
      
      FontMetrics fm = g.getFontMetrics();
      paintViewInsets = c.getInsets(paintViewInsets);
      
      paintViewR.x = paintViewInsets.left;
      paintViewR.y = paintViewInsets.top;
      
      // Use inverted height & width
      paintViewR.height = c.getWidth() - (paintViewInsets.left + paintViewInsets.right);
      paintViewR.width = c.getHeight() - (paintViewInsets.top + paintViewInsets.bottom);
      
      paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
      paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;
      
      String clippedText =
              layoutCL(label, fm, text, icon, paintViewR, paintIconR, paintTextR);
      
      Graphics2D g2 = (Graphics2D) g;
      AffineTransform tr = g2.getTransform();
      if( clockwise )
      {
         g2.rotate( Math.PI / 2 );
         g2.translate( 0, - c.getWidth() );
      } // if( clockwise )
      else
      {
         g2.rotate( - Math.PI / 2 );
         g2.translate( - c.getHeight(), 0 );
      } // else -> if( clockwise )
      
      if (icon != null)
      {
         icon.paintIcon(c, g, paintIconR.x, paintIconR.y);
      } // if (icon != null)
      
      if (text != null)
      {
         int textX = paintTextR.x;
         int textY = paintTextR.y + fm.getAscent();
         
         if (label.isEnabled())
         {
            paintEnabledText(label, g, clippedText, textX, textY);
         } // if (label.isEnabled())
         else
         {
            paintDisabledText(label, g, clippedText, textX, textY);
         } // else -> if (label.isEnabled())
      } // if (text != null)
      
      g2.setTransform( tr );
   } // public void paint(Graphics g, JComponent c)
} 
