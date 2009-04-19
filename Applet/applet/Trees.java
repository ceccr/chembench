package applet;


import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.HashMap;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import prefuse.data.Graph;
import prefuse.data.io.GraphMLReader;
import prefuse.util.ui.UILib;


/**
 *
 * @author Myroslav Sypa
 */
public class Trees extends AbstractPanel{
    
    InputStream is;
    HashMap<String,ImageIcon> images;
    final static int RADIALTREE = 0;
    final static int HORIZONTALTREE = 1;
    final static int VERTICALTREE = 2;
    static Graph g;
    static JPanel infopanel;
    static JPanel treepanel;
    
     public Trees(final InputStream is, final HashMap<String,ImageIcon> images){
        this.is = is;
        this.images = images;
      //  JOptionPane.showMessageDialog(null, "Starting tabs creation11");
    }

    public JPanel createInfoPanel(JPanel datap){    
        Component[] cp = new Component[2];
        
        cp[0] = new JLabel("Clustering method: "+"Agglomerative hierarchical clustering");
        cp[1] = new JLabel("Distance between two elements: "+"max(Complete linkage clustering)");
        
        infopanel = createPanel(70, "Information", cp, new GridLayout(2,2), true, datap);
        return infopanel;
    }
    
    public void setInfoText(String text){
       if(infopanel!=null){
           System.out.println(infopanel.getComponent(0));
           System.out.println(infopanel.getComponent(1));
        ((JLabel)infopanel.getComponent(0)).setText(text.substring(0, text.indexOf('\n')));
        ((JLabel)infopanel.getComponent(1)).setText(text.substring(text.indexOf('\n'), text.length()));
       }
    }
    
    public void setInputStream(InputStream is){
        this.is = is;
    }
    
    public void renew(){
        readGraph(is);
        TreePanel.gview.setTreeGraph(g,"name");
    }
     
    public JPanel createTreeTypePanel(JPanel datap){
        Component[] cp = new Component[3];
        
        JRadioButton hor = new JRadioButton("Horizontal");
        JRadioButton ver = new JRadioButton("Vertical");
        JRadioButton rad = new JRadioButton("Radial");
        
        hor.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
               TreePanel.gview.setTreeLayout(HORIZONTALTREE);
            }
        });

            
       ver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 TreePanel.gview.setTreeLayout(VERTICALTREE);
            }
        });

       rad.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent e) {
                TreePanel.gview.setTreeLayout(RADIALTREE);
            }
        });


        
        
       ButtonGroup bg = new ButtonGroup();
        bg.add(hor);
        bg.add(ver);
        bg.add(rad);
        
        hor.setSelected(true);
        
        
        cp[0] = hor;
        cp[1] = ver;
        cp[2] = rad;
        
        return createPanel(70, "Tree type", cp, new FlowLayout(), true, datap);
    }
    
    
    private void readGraph(InputStream datafile){
        g = null;
        try {
        //    JOptionPane.showMessageDialog(null, datafile.toString());
            g = new GraphMLReader().readGraph(datafile);
     //        JOptionPane.showMessageDialog(null, g.getEdgeCount());
           TreePanel.setImages(images);
    //       JOptionPane.showMessageDialog(null, "images set");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            //System.out.println("<<<"+g.getNodeCount());
    
        } catch ( Exception e ) {
           
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public JPanel createTreePanel(){
        Component[] cp = new Component[1];
        
       //JTable heatmap_ = new JTable(); 
       UILib.setPlatformLookAndFeel();
       /*JScrollPane scrolltable = new JScrollPane(TreePanel.demo(is, "name"), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        Dimension d = new Dimension(700, 630);
        scrolltable.setPreferredSize(d);
        scrolltable.setMaximumSize(d);
        scrolltable.setMinimumSize(d);
        */
       readGraph(is);
       treepanel = TreePanel.demo(g, "name");
        cp[0] = treepanel;
    //    JOptionPane.showMessageDialog(null, "createTreePanel");
        return createPanel(600, "Tree", cp, new FlowLayout(), false, null);
    }


}
