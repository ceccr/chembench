package applet;


import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

class ColumnHeaderRenderer extends JLabel implements TableCellRenderer
{
   ColumnHeaderRenderer()
   {
      // Paint every pixel in the header's rectangle so no underlying
      // pixels show through.
      
      setOpaque(true);
      
      // Set the foreground color to the current Color object assigned
      // to the TableHeader.foreground property. Text appearing on the
      // header appears in the foreground color (unless that color is
      // overridden).
      
      setForeground(UIManager.getColor("TableHeader.foreground"));
      
      // Set the background color to the current Color object assigned
      // to the TableHeader.background property. Pixels behind the text
      // appearing on the header appear in the background color.
      
      setBackground(UIManager.getColor("TableHeader.background"));
      
      // Indirectly set the border to be drawn around each header to
      // the Border object assigned to the TableHeader.cellBorder
      // property. Indirection is necessary because the default Border
      // does not leave enough empty space around its edges. As a
      // result, portions of those characters that butt up against the
      // left and right border sides are clipped, which doesn't look
      // nice. (That happens using the Java look and feel.)
      
      
      setBorder(UIManager.getBorder("TableHeader.cellBorder"));
      
      setUI(new VerticalLabelUI(false));
   } // ColumnHeaderRenderer()
   
   public Component getTableCellRendererComponent(JTable table,
           Object value,
           boolean isSelected,
           boolean hasFocus,
           int row,
           int col)
   {
      int halignment = LEFT;     // Horizontally center text.
      int valignment = CENTER;     // Vertically center text.
      
      // It is always a good idea to verify a type before performing a
      // cast.
      
      if (value instanceof String)
      {
         String s = (String) value;
         
         int style = Font.PLAIN;
         Font f = table.getFont().deriveFont(style);
         setFont(f);
         setHorizontalAlignment(halignment);
         setVerticalAlignment(valignment);
         setText(s);
      } // if (value instanceof String)
      
      return this;
   } // public Component getTableCellRendererComponent
   
}

