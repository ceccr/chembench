package applet;




import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.GridLayout;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author Myroslav Sypa
 */

public class CompoundTable extends AbstractPanel{
    
    JTable cTable;
    Vector<Compound> compounds;
    Vector<String> ids;
    Vector<ImageIcon> imgs;
    Vector<String> vals;
    HashMap<String, ImageIcon> images_map;
       
    public HashMap<String, ImageIcon> getImagesMap(){
        return this.images_map;
    }
    
    public JPanel createButtonsPanel(int pageNumber, JPanel datap){
        Component[] cp = new Component[2];
        
        JButton bb = new JButton("Show all");
        
        bb.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try{
                    setTableModel(0, compounds.size());
                    }catch(Exception ex){
                        System.out.println(ex.getMessage());
                    }
                }
        });
        
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        if(pageNumber*2<compounds.size()) pageNumber+=1;
        for(int i=0;i<pageNumber;i++){
            JButton b = new JButton();
            final int j = i;
            final int pn = pageNumber;                    
            
            b.setText(new Integer(i+1).toString());
            b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try{
                    if(j == pn-1) setTableModel(j*2, compounds.size());
                    else setTableModel(j*2, j*2+2);
                    }
                    catch(Exception ex){
                         System.out.println(ex.getMessage());
                    }
                }
            });
            p.add(b);
            p.revalidate();
            p.repaint();
        }
                     
        JScrollPane bpanel = new JScrollPane(p, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        Dimension d = new Dimension(620,50);
        bpanel.setPreferredSize(d);
        bpanel.setMinimumSize(d);
        bpanel.setMaximumSize(d);
       
        cp[0] = bb;    
        cp[1] = bpanel;
        return createPanel(90, "Pages in table", cp, new FlowLayout(),true, datap);
    }
    
    public JPanel createInfoPanel(String name, String ncom, String type, String date,JPanel datap){
        Component[] cp = new Component[4];
        
        cp[0] = new JLabel("Dataset: "+name);
        cp[1] = new JLabel("Number of compounds: "+ncom);
        cp[2] = new JLabel("KNN type: "+type);
        cp[3] = new JLabel("Date: "+date);
        
        return createPanel(100, "General dataset info", cp, new GridLayout(4,1), true, datap);
    }
    
    public JPanel createDescriptionPanel(String des, JPanel datap){
        
        JTextArea ta = new JTextArea(des, 4, 85);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(BorderFactory.createBevelBorder(1));
        
        return createPanel(120, "Description", new Component[]{sp}, new FlowLayout(),true, datap);
    }
    
    public JPanel createTablePanel() throws MalformedURLException, IOException{
         
        cTable = new JTable(){

           @Override
           public Class getColumnClass(int column) {
                return column == 1 ? ImageIcon.class : Object.class;
            }
        };
        
        setTableModel(0, compounds.size());        
        
        
        JScrollPane scrolltable = new JScrollPane(cTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        Dimension d = new Dimension(700, 380);
        scrolltable.setPreferredSize(  d);
        scrolltable.setMaximumSize( d);
        scrolltable.setMinimumSize( d);
        
        return createPanel(420, "Data table", new Component[]{scrolltable},new FlowLayout(), false, null);
    }
    
    
    
    public CompoundTable(Vector<Compound> data) throws OutOfMemoryError, MalformedURLException, IOException {
        compounds = data;
        readData();
    }
    
   private void setTableModel(int from, int to) throws MalformedURLException, IOException{ 
            DefaultTableModel tm = new DefaultTableModel(){
             public boolean isCellEditable(int rowIndex, int mColIndex) {
            return false;
        }    
            };
         
           
            tm.addColumn("Compound ID", new Vector(ids.subList(from, to)));
            tm.addColumn("Structure", new Vector(imgs.subList(from, to)));
            tm.addColumn("Observed value", new Vector(vals.subList(from, to)));
          
                cTable.setModel(tm);
          alignColumns();
   }
   
   private void alignColumns(){
       
       cTable.setRowHeight(220);
                TableColumn col0 = cTable.getColumnModel().getColumn(0);
                TableColumn col1 = cTable.getColumnModel().getColumn(1);
                TableColumn col2 = cTable.getColumnModel().getColumn(2);
                int width = 250;
                col0.setPreferredWidth(width-34);
                col1.setPreferredWidth(width);
                col2.setPreferredWidth(width-35);
                                
                cTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
       
   }
   
   private void readData() throws MalformedURLException, IOException{ 
        ids = new Vector<String>();
        imgs = new Vector<ImageIcon>();
        vals = new Vector<String>();
        images_map = new HashMap<String,ImageIcon>();
            BufferedImage img;
            ImageIcon image;
            System.out.println("For");
            for(int i = 0; i<compounds.size();i++){
               try{
                URL url = new URL(compounds.get(i).getImage_path());
                 img = ImageIO.read(url);
                 image = new ImageIcon(img);      
                //System.out.println("NOT NULL");
                 }catch(Exception ex){
                  image = new ImageIcon(); 
                  ///if(i==1) System.out.println("NULL::"+ ex.toString());
               }
               images_map.put(compounds.get(i).getCompound_id(), image);
               ids.add(compounds.get(i).getCompound_id());
               imgs.add(image);
               vals.add(compounds.get(i).getValue());
               
            }
              
   }
   
        
}
