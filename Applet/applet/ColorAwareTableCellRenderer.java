package applet;


import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
/**
     * ColorAwareTableCellRenderer - alows us to draw each cell   
     * with the different collor depending on the value this cell has.
     */
 class ColorAwareTableCellRenderer implements TableCellRenderer {

        private TableCellRenderer cellRenderer;

        public ColorAwareTableCellRenderer(TableCellRenderer cellRenderer) {
            if (cellRenderer == this || cellRenderer == null) {
                throw new IllegalArgumentException();
            }
            this.cellRenderer = cellRenderer;
        }

        public Component getTableCellRendererComponent(JTable table, Object v, boolean sel, boolean focus, int y, int x) {
            Component c = cellRenderer.getTableCellRendererComponent(table, v, sel, focus, y, x);
            if (c != null) {
                if (x < table.getModel().getColumnCount() && y < table.getModel().getRowCount()) {
                    String s = (String) table.getValueAt(y, x);
                    if (s != null && s != "") {
                        Double d = new Double(s);
                        ColorMap cm = new ColorMap();
                        c.setBackground(cm.getColor(d*255));
                        c.setForeground(cm.getColor(d*255));
                        c.setPreferredSize(new Dimension(20, table.getRowHeight()));
                        ((JComponent)c).setToolTipText(s);
                    } else {
                        c.setBackground(table.getTableHeader().getBackground());
                        c.setForeground(table.getTableHeader().getForeground());
                        c.setPreferredSize(new Dimension(20, table.getRowHeight()));

                    }
                }
            }
            return c;
        }

    }
    
