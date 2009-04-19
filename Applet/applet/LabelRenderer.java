package applet;


import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
     * LabelRenderer - renderer for drawing row headers      
 * 
     */
class LabelRenderer extends JLabel
            implements TableCellRenderer {

        JTable table;
        int _row;
        JTableHeader header;
        
       public LabelRenderer(JTable table) {
            header = table.getTableHeader();
            setOpaque(true);
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            setHorizontalAlignment(CENTER);
            setForeground(header.getForeground());
            setBackground(header.getBackground());
            setFont(header.getFont());


        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            if(column ==0) setText((String) value);
            return this;
        }
        
}
