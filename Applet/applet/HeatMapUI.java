package applet;


import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.DefaultTableModel;
 /**
     * HeatMapUI - user interface for drag&drop functionality for rows 
     */
class HeatMapUI extends BasicTableUI {

        private boolean draggingRow = false;
        private int startDragPoint;
        private int dyOffset;
          
                
        @Override
        protected MouseInputListener createMouseInputListener() {
            return new DragDropRowMouseInputHandler();
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            super.paint(g, c);

          if (draggingRow) {
                g.setColor(table.getParent().getBackground());
                Rectangle cellRect = table.getCellRect(table.getSelectedRow(), 0, false);
                g.copyArea(cellRect.x, cellRect.y, table.getWidth(), table.getRowHeight(), cellRect.x, dyOffset);

                if (dyOffset < 0) {
                    g.fillRect(cellRect.x, cellRect.y + (table.getRowHeight() + dyOffset), table.getWidth(), (dyOffset * -1));
                } else {
                    g.fillRect(cellRect.x, cellRect.y, table.getWidth(), dyOffset);
                }
            }
        }
        
 class DragDropRowMouseInputHandler extends MouseInputHandler {
    private int startDragPoint;

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                startDragPoint = (int) e.getPoint().getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int fromRow = table.getSelectedRow();

                if (fromRow >= 0) {
                    draggingRow = true;

                    int rowHeight = table.getRowHeight();
                    int middleOfSelectedRow = (rowHeight * fromRow) + (rowHeight / 2);

                    int toRow = -1;
                    int yMousePoint = (int) e.getPoint().getY();

                    if (yMousePoint < (middleOfSelectedRow - rowHeight)) {
                        // Move row up
                        toRow = fromRow - 1;
                    } else if (yMousePoint > (middleOfSelectedRow + rowHeight)) {
                        // Move row down
                        toRow = fromRow + 1;
                    }

                    if (toRow >= 0 && toRow < table.getRowCount()) {
                        DefaultTableModel model = (DefaultTableModel) table.getModel();

                        for (int i = 0; i < model.getColumnCount(); i++) {
                            Object fromValue = model.getValueAt(fromRow, i);
                            Object toValue = model.getValueAt(toRow, i);

                            model.setValueAt(toValue, fromRow, i);
                            model.setValueAt(fromValue, toRow, i);
                        }
                        table.setRowSelectionInterval(toRow, toRow);
                        startDragPoint = yMousePoint;
                        table.scrollRectToVisible(table.getCellRect(toRow, table.getSelectedColumn(), true));
                    }

                    dyOffset = (startDragPoint - yMousePoint) * -1;
                    table.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                draggingRow = false;
                table.repaint();
            }
        }
}

    

