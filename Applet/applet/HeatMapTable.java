package applet;


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.plaf.basic.BasicDesktopIconUI.MouseInputHandler;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;


/**
 *
 * @author Myroslav Sypa
 * HeatMapTable - interactive table for heatmap
 * Build heatmap automaticaly from table model
 */
public class HeatMapTable extends JTable {
  JTable headerColumn;
  Vector<Vector<String>> tabledata_;
  Vector<String> colsNames;
 //JTable t = this;
    
 private JTable t = this;
    
    /**
 * Constructor
 * @param tm - table model 
 * @param colsNames - table column names
 */
     public HeatMapTable(Vector<Vector<String>> tdata, Vector<String> colsNames_) {
        tabledata_ = tdata;
        colsNames = colsNames_; 
        init();               
       
   }
     
     
    public void init(){
         this.setUI(new HeatMapUI());
         buildColumModels();
        
        getTableHeader().addMouseMotionListener(new MouseMotionListener() {

            public void mouseDragged(MouseEvent e) {
                
                int col = columnAtPoint(e.getPoint());
                if(col>=0 && col<getColumnCount()){ scrollRectToVisible(getCellRect(getSelectedRow(), col, true));
                for(int i=0;i<getTableHeader().getColumnModel().getColumnCount();i++)
                getColumnModel().getColumn(i).setHeaderValue(((String)getTableHeader().getColumnModel().getColumn(i).getHeaderValue()).replaceFirst("<<", ""));
                getColumnModel().getColumn(col).setHeaderValue("<<"+getTableHeader().getColumnModel().getColumn(col).getHeaderValue());
                }                
            }

            public void mouseMoved(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        
        // mouse listener for highlight headers for selected column
        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                //int row = t.rowAtPoint(e.getPoint());
                //int col = t.columnAtPoint(e.getPoint());
                
            }

            public void mousePressed(MouseEvent e) {
              for(int i=0;i<getTableHeader().getColumnModel().getColumnCount();i++)
                getTableHeader().getColumnModel().getColumn(i).setHeaderValue(((String)getTableHeader().getColumnModel().getColumn(i).getHeaderValue()).replaceFirst("<<", ""));
               for(int j=0;j<getRowCount();j++){
                    headerColumn.setValueAt(((String)headerColumn.getValueAt(j,0)).replace(">>", ""), j, 0);
               }
                int col = getSelectedColumn();
                int row = getSelectedRow();
                if(col>=0) {
                    getTableHeader().getColumnModel().getColumn(col).setHeaderValue("<<"+getTableHeader().getColumnModel().getColumn(col).getHeaderValue());
                    getTableHeader().repaint();
               }
                if(row>=0) headerColumn.setValueAt(headerColumn.getValueAt(row,0)+">>", row, 0);
                
               
            }

            public void mouseReleased(MouseEvent e) {
               // throw new UnsupportedOperationException("Not supported yet.");
            }

            public void mouseEntered(MouseEvent e) {
               // throw new UnsupportedOperationException("Not supported yet.");
            }

            public void mouseExited(MouseEvent e) {
              //  throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
    }
    
   public void setTableData(Vector<Vector<String>> tdata, Vector<String> colsNames_){
       tabledata_ = tdata;
       colsNames = colsNames_; 
       
   }
    
   public JTable getColumnHeader(){
       return headerColumn;
   }
   
   @Override
   public boolean isCellEditable(int rowIndex, int mColIndex) {
            return false;
        }
   
   public void buildColumModels(){ 
       DefaultTableColumnModel cm =new DefaultTableColumnModel() {
                 
            boolean first = true;

            @Override
      public void addColumn(TableColumn tc) {
        // Drop the first column . . . that'll be the row header
        if (first) {
          first = false;
          return;
        }
        tc.setMinWidth(0);
        tc.setHeaderRenderer(new ColumnHeaderRenderer());
        super.addColumn(tc);
      }
        };       
        
        
     //table column model for first column
     DefaultTableColumnModel rowHeaderModel = new DefaultTableColumnModel() {
      boolean first = true;
      
      
      @Override
     public void moveColumn(int columnIndex, int newIndex){
         //do nothing on drag
     } 
      
            @Override
      public void addColumn(TableColumn tc) {
        if (first) {
          tc.setMaxWidth(tc.getPreferredWidth());
          tc.setCellRenderer(new LabelRenderer(t));
          tc.setResizable(false);
          super.addColumn(tc);
          first = false;
        }
        // Drop the rest of the columns . . . this is the header column
        // only
      }
   };
      
   DefaultTableModel tmm = new DefaultTableModel(colsNames, 0);
        for(int i=0;i<tabledata_.size();i++){
           tmm.addRow(tabledata_.get(i));
        }      
       // model for all columns except first one
        setModel(tmm);
        setColumnModel(cm);
        
        TableCellRenderer original = this.getDefaultRenderer(Object.class);
        setDefaultRenderer(Object.class, new ColorAwareTableCellRenderer(original));
        /// setting vertical labels for Column header
        
        ///////////////////
        /// setting Row header 
        
        headerColumn = new JTable(tmm, rowHeaderModel);
        createDefaultColumnsFromModel();
        headerColumn.createDefaultColumnsFromModel();
    
       headerColumn.setColumnSelectionAllowed(false);
       headerColumn.setCellSelectionEnabled(false);

    // Make sure that selections between the main table and the header stay
    // in sync (by sharing the same model)
      setSelectionModel(headerColumn.getSelectionModel());
      getTableHeader().setResizingAllowed(false);
      revalidate();
      repaint();
   }

      
    public JTable getRowHeader(){
        return headerColumn;
    }
    }
   