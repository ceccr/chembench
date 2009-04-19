package applet;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


/**
 *
 * @author Myroslav Sypa
 */
public class HeatMap extends AbstractPanel{
    static HeatMapTable heatmap_;
    static Vector<String> header;
    static Vector<Vector<String>> tabledata;
    private static Vector<Vector<String>> heatmapdata;
    static JViewport jv;
    public JRadioButton tanimoto;
    public JRadioButton maha;
    private JButton zoom;
    
    public HeatMap(Vector<Vector<String>> heatmapdata_){
        heatmapdata = heatmapdata_;
        fillVectors();
    }
    
    private void fillVectors(){
     tabledata = new Vector<Vector<String>>();
  
       header = heatmapdata.get(0);
              
       for(int i=1;i<heatmapdata.size();i++)
            tabledata.add(heatmapdata.get(i));
   }
    
    
    public void setData(Vector<Vector<String>> heatmapdata_){
        heatmapdata = heatmapdata_;
        fillVectors();
    }

        
    public JPanel createDistancePanel(JPanel datap){
        Component[] cp = new Component[2];
        
       tanimoto = new JRadioButton("Tanimoto");
       maha = new JRadioButton("Mahalanobis");
        
        tanimoto.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                CECCRApplet.setData(CECCRApplet.TANIMOTO);
            }
        });
        
        maha.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                    CECCRApplet.setData(CECCRApplet.MAHALANOBIS);
            }
        });
       ButtonGroup bg = new ButtonGroup();
        bg.add(tanimoto);
        bg.add(maha);
        
        cp[0] = tanimoto;
        cp[1] = maha;
        
        tanimoto.setSelected(true);
        return createPanel(70, "Distance measure", cp, new FlowLayout(), true, datap);
    }
    
    public JPanel createColorPanel(JPanel datap){
        Component[] cp = new Component[1];
        ColorMap cm = new ColorMap();
        JPanel p = cm.createPanel();
        cp[0] = p;
        return createPanel(70, "Color range", cp, new FlowLayout(), true, datap);
    }
    
    public JPanel createOrderingPanel(JPanel datap){
        Component[] cp = new Component[2];
        
        JButton reset = new JButton();
        reset.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                CECCRApplet.renew(tanimoto.isSelected());
                renew();
            }
        });
        reset.setText("Reset table");
        
        zoom = new JButton();
        zoom.setText("Zoom out");
        zoom.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                if(zoom.getText().equals("Zoom out")){
                    zoom.setText("Zoom in");
                    setColumnWidth(5);
                }
                else{
                    zoom.setText("Zoom out");
                    setColumnWidth(20);
                }
            }
        });
        cp[0] = reset;
        cp[1] = zoom;
        
        return createPanel(70, "Additional operations", cp, new FlowLayout(), true, datap);
    }
    private void setViewport()
    {
        jv.remove(heatmap_);
        jv.setView(heatmap_.getRowHeader());
        jv.setPreferredSize(heatmap_.getRowHeader().getMaximumSize());
        
    }
    private void setColumnWidth(int width){
        TableColumnModel tableColumnModel = heatmap_.getColumnModel();
        TableColumn tableColumn;
         
         for ( int i=0; i<tableColumnModel.getColumnCount(); i++ )
         {
            tableColumn = tableColumnModel.getColumn( i );
            tableColumn.setPreferredWidth(width);
            heatmap_.setRowHeight(width);
            
            }
            heatmap_.getRowHeader().setRowHeight(width);
    }
    
    public void renew(){
        heatmap_.setTableData(tabledata, header);
        heatmap_.init();
        setViewport();
        if(zoom.getText().equals("Zoom out")) setColumnWidth(20);
        else setColumnWidth(5);
    }
    
    public JPanel createHeatMapPanel(){
        Component[] cp = new Component[1];
        
           
        heatmap_ = new HeatMapTable(tabledata, header);
        setColumnWidth(20);
        
        
        
        jv = new JViewport();
        setViewport();

    // With out shutting off autoResizeMode, our tables won't scroll
    // correctly (horizontally, anyway)
        
    
        JScrollPane scrolltable = new JScrollPane(heatmap_, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrolltable.setRowHeader(jv);
    scrolltable.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, heatmap_.getRowHeader().getTableHeader());

        Dimension d = new Dimension(700, 490);
        scrolltable.setPreferredSize(d);
        scrolltable.setMaximumSize(d);
        scrolltable.setMinimumSize(d);
        
        cp[0] = scrolltable;
        
        return createPanel(530, "Heat Map", cp, new FlowLayout(), false, null);
    }

}
