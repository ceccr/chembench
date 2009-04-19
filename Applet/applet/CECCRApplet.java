package applet;

//package tanimoto_applet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.BorderLayout;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JApplet;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
//import prefuse.activity.ActivityManager;

/**
 *
 * @author msypa
 */
public class CECCRApplet extends JApplet {

    static String codebase;
    static String xfile;
    static String viz;
    static HashMap<String, ImageIcon> images;
    JPanel p;
    static Vector<Compound> tmatr;
    static Vector<Vector<String>> dmmatr_tan;
    static Vector<Vector<String>> dmmatr_mah;
    static String ncom;
    static String type;
    static String date;
    static String desc;
    static String dataset;
    static String xmlfile_tan;
    static String xmlfile_mah;
    static HeatMap hmap;
    static Trees trees;
    static CompoundTable ctable;
    static PCAPlot pca;
    public static final int MAHALANOBIS = 0;
    public static final int TANIMOTO = 1;
    Frame root = null;
    static Loader preloader = new Loader("");

    /**
     * Initialization method that will be called after the applet is loaded
     * into the browser.
     */
    @Override
    public void init() {

        validate();
        setLAF();
        readParameters();
        initGUI();
    }

    @Override
    public void start() {

    }

    private void initGUI() {
        preloader.updateTitle("Initializing GUI");

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        preloader.update(65);
        JPanel tab0panel = new JPanel();
        tab0panel.setSize(750, 600);
        JPanel tab1panel = new JPanel();
        JPanel tab2panel = new JPanel(new FlowLayout());
        JPanel tab3panel = new JPanel(new FlowLayout());
             preloader.update(70);
        try {
            createTab0(tab0panel);

            createTab1(tab1panel);

            createTab2(tab2panel);
            
            createTab3(tab3panel);


        } catch (Exception ex) {
            System.out.println("initGUI::" + ex.getMessage());

        }
        preloader.update(99);
        tabs.addTab("Compound table", tab0panel);
        if (tab1panel.getComponentCount() > 0) {
            tabs.addTab("Trees", tab1panel);
        }
        if (tab2panel.getComponentCount() > 0) {
            tabs.addTab("Heat map", tab2panel);
        }
        if (tab3panel.getComponentCount() > 0) {
            tabs.addTab("PCA plots", tab3panel);
        }
        
        preloader.update(100);
        getContentPane().add(tabs, BorderLayout.CENTER);
    }

    public static void renew(boolean tan) {
        try {
            if (tan) {
                dmmatr_tan = Utility.readFile(codebase + viz + "_tan.mat");
                hmap.setData(dmmatr_tan);
            } else {
                dmmatr_mah = Utility.readFile(codebase + viz + "_mah.mat");
                hmap.setData(dmmatr_mah);
            }


        } catch (IOException ex) {
            System.out.println("renew::" + ex.getMessage());
        }
    }

    public static void setData(int type) {
        switch (type) {
            case TANIMOTO: {
                hmap.setData(dmmatr_tan);
                hmap.renew();
                trees.setInputStream(new ByteArrayInputStream(xmlfile_tan.getBytes()));
                trees.setInfoText("Clustering method: Agglomerative hierarchical clustering\n Distance between two elements: max(Complete linkage clustering)");
                trees.renew();
                break;
            }
            case MAHALANOBIS: {
                hmap.setData(dmmatr_mah);
                hmap.renew();
                trees.setInputStream(new ByteArrayInputStream(xmlfile_mah.getBytes()));
                trees.setInfoText("Clustering method: Mahalanobis distance measure\n Similarity between two elements = 1/mahalanobis distance between them");
                trees.renew();
                break;
            }
            }


    }

    /// General Info
    private static void createTab0(JPanel panel) {
        try {
            preloader.updateTitle("Creating info tab");
            preloader.update(71);

            System.out.println("1");
            preloader.update(72);
            ctable = new CompoundTable(tmatr);
            System.out.println("2");
            preloader.update(73);
            JPanel tp = ctable.createTablePanel();
            System.out.println("3");
            preloader.update(74);
            JPanel ip = ctable.createInfoPanel(dataset, ncom, type, date, tp);
            System.out.println("4");
            preloader.update(75);
            JPanel dp = ctable.createDescriptionPanel(desc, tp);
            System.out.println("5");
            preloader.update(76);
            JPanel bp = ctable.createButtonsPanel(tmatr.size() / 2, tp);
            System.out.println("HERE");
            images = ctable.getImagesMap();
            preloader.update(77);
            panel.add(ip, BorderLayout.NORTH);
            if (desc.length() > 0) {
                panel.add(dp, BorderLayout.NORTH);
            } else {
                tp.setPreferredSize(new Dimension(720, 540));
                tp.getComponent(0).setPreferredSize(new Dimension(700, 500));

                tp.setMinimumSize(new Dimension(720, 540));
                tp.getComponent(0).setMinimumSize(new Dimension(700, 500));

                tp.setMaximumSize(new Dimension(720, 540));
                tp.getComponent(0).setMaximumSize(new Dimension(700, 500));

                tp.revalidate();
                tp.repaint();
            }
            preloader.update(78);
            panel.add(bp, BorderLayout.NORTH);
            preloader.update(79);
            panel.add(tp, BorderLayout.SOUTH);
            preloader.update(80);
        } catch (OutOfMemoryError ooem) {
            System.out.println(ooem.getMessage());
            System.gc();

        //JOptionPane.showMessageDialog(this, "JVM running out of memmory. Try to restart your browser.");
        } catch (MalformedURLException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println("createTab0::" + ex.getMessage());
        }


    }

    private static void createTab1(JPanel panel) {
        // JOptionPane.showMessageDialog(null, xmlfile_tan);

        preloader.updateTitle("Creating tree tab");

        try {
            if (xmlfile_tan.isEmpty()) {
                return;
            }
            preloader.update(81);
            trees = new Trees(new ByteArrayInputStream(xmlfile_tan.getBytes()), images);
            preloader.update(82);
            JPanel tp = trees.createTreePanel();
            preloader.update(83);
            JPanel inf = trees.createInfoPanel(tp);
            preloader.update(84);
            JPanel dm = trees.createTreeTypePanel(tp);
            preloader.update(85);
            panel.add(inf, BorderLayout.NORTH);
            panel.add(dm, BorderLayout.NORTH);
            panel.add(tp, BorderLayout.SOUTH);
            preloader.update(86);
        } catch (Exception e) {
            System.out.println("111::" + e.getMessage());
        }
    //JOptionPane.showMessageDialog(null, "end");
    }

    /// HeatMaphmap.
    private static void createTab2(JPanel panel) {

//           Vector<Vector<String>>  heatmapdata = new Vector<Vector<String>>();
//           Vector<String> header = new Vector<String>();
//           Collections.addAll(header, new String[]{"", "a", "b", "c", "d", "e","f"});
//        Vector<String> header2 = new Vector<String>();
//        Collections.addAll(header2, new String[]{"a", "1","0.25","0.89", "0.23", "0.12", "0.75"});
//        Vector<String> header3 = new Vector<String>();
//        Collections.addAll(header3, new String[]{"b","0.25","1","0.31","0.1", "0.88", "0.75"});
//        Vector<String> header4 = new Vector<String>();
//        Collections.addAll(header4, new String[]{"c","0.89","0.31","1","0.82", "0.4", "0.75"});
//        Vector<String> header5 = new Vector<String>();
//        Collections.addAll(header5, new String[]{"d","0.23","0.1","0.82", "1", "0.57", "0.75"});
//        Vector<String> header6 = new Vector<String>();
//        Collections.addAll(header6, new String[]{"e","0.12","0.88","0.4", "0.57","1", "0.89"});
//         Vector<String> header7 = new Vector<String>();
//        Collections.addAll(header7, new String[]{"f", "0.75", "0.75", "0.75", "0.75", "0.89","1"});
//            
//        heatmapdata.add(header);
//        heatmapdata.add(header2);
//        heatmapdata.add(header3);
//        heatmapdata.add(header4);
//        heatmapdata.add(header5);
//        heatmapdata.add(header6);
//        heatmapdata.add(header7);
        try {
            preloader.updateTitle("Creating heatmap tab");
            if (dmmatr_tan == null && dmmatr_mah == null) {

                return;
            }
            preloader.update(86);
            if (dmmatr_tan != null) {

                hmap = new HeatMap(dmmatr_tan/* = new Vector<Vector<String>>()*/);

            } else {

                hmap = new HeatMap(dmmatr_mah);

                hmap.maha.setSelected(true);
            }
            preloader.update(87);
            // HeatMap hmap = new HeatMap(heatmapdata/* = new Vector<Vector<String>>()*/);
            //new TreeStructure(heatmapdata).build();
            JPanel hm = hmap.createHeatMapPanel();
            preloader.update(88);
            JPanel cm = hmap.createColorPanel(hm);
            preloader.update(89);
            JPanel dm = hmap.createDistancePanel(hm);
            preloader.update(90);
            JPanel op = hmap.createOrderingPanel(hm);
            preloader.update(91);
            panel.add(dm, BorderLayout.NORTH);
            panel.add(op, BorderLayout.NORTH);
            panel.add(cm, BorderLayout.NORTH);
            panel.add(hm, BorderLayout.SOUTH);
            preloader.update(92);
        } catch (Exception e) {
            System.out.println("2::" + e.getMessage());
        }
    }
    
    private static void createTab3(JPanel panel){
        try{
            
            preloader.updateTitle("Creating PCA tab");
            preloader.update(94);
            String filePath = codebase + xfile;
            System.out.println("Path::"+filePath);
            String user = filePath.substring(filePath.indexOf("user=")+5, filePath.indexOf("&"));
            System.out.println("User::"+user);
            String type_ = filePath.substring(filePath.indexOf("project=")+8, filePath.indexOf("&name="));
            System.out.println("Type::"+type);
            String filename = filePath.substring(filePath.indexOf("name=")+5, filePath.length()-4);         
            System.out.println("Filename::"+filename);
            String img ="http://chembench-dev.metalab.unc.edu"+"/imageServlet?user="+user+"&projectType=PCA&compoundId="+filename+"&project="+type_+"&datasetID=-1";
            System.out.println("Img::"+img);
            pca = new PCAPlot(img);
            preloader.update(96);
            JPanel p = pca.createPCAPanel();
            panel.add(p, BorderLayout.NORTH);
            preloader.update(98);
        }
        catch(Exception e){
            System.out.println("3::"+e.getMessage());
            return;
        }
    }

    private void setLAF() {
        //   JOptionPane.showMessageDialog(rootPane, "setLAF");
        String laf = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(laf);
        } catch (Exception exc) {
            System.out.println(exc.getMessage());
        }
    }

    private void readParameters() {
        preloader.updateTitle("Reading parameters");
        preloader.show();

        //   JOptionPane.showMessageDialog(rootPane, "Start");
        xfile = getParameter("actFile");
        preloader.update(1);
        //    JOptionPane.showMessageDialog(rootPane, xfile);
        viz = getParameter("viz_path");
        preloader.update(2);
        System.out.println(viz);
        dataset = getParameter("dataset");
        preloader.update(3);
        //    JOptionPane.showMessageDialog(rootPane, dataset);
        ncom = getParameter("ncom");
        preloader.update(4);
        //    JOptionPane.showMessageDialog(rootPane, ncom);
        type = getParameter("type_");
        preloader.update(5);
        //   JOptionPane.showMessageDialog(rootPane, type);
        date = getParameter("creation_date");
        preloader.update(6);
        //    JOptionPane.showMessageDialog(rootPane, date);
        desc = getParameter("desc");
        preloader.update(7);

        //   JOptionPane.showMessageDialog(rootPane, desc);
        codebase = getCodeBase().toString().substring(0, getCodeBase().toString().indexOf("u/") + 2);
        preloader.update(8);
        //    JOptionPane.showMessageDialog(rootPane, codebase);

        try {

            System.out.println(codebase + xfile);
            //          JOptionPane.showMessageDialog(rootPane, codebase+xfile);
            System.out.println(codebase + viz);
            //             JOptionPane.showMessageDialog(rootPane, codebase+viz);

            preloader.updateTitle("Reading data");
            preloader.update(10);
            tmatr = Utility.readCompoundInfo(codebase + xfile);
            System.out.println("start1");
            preloader.update(20);
            //        JOptionPane.showMessageDialog(rootPane, "start1");
            dmmatr_tan = Utility.readFile(codebase + viz + "_tan.mat");
            System.out.println("start2");
            preloader.update(30);
            //        JOptionPane.showMessageDialog(rootPane, "start2");
            dmmatr_mah = Utility.readFile(codebase + viz + "_mah.mat");
            System.out.println("start3");
            preloader.update(40);
            //        JOptionPane.showMessageDialog(rootPane, "start3 "+codebase+viz+"_tan.xml");
            xmlfile_tan = Utility.readXMLFile(codebase + viz + "_tan.xml");
            System.out.println("start4");
            preloader.update(50);
            //       JOptionPane.showMessageDialog(rootPane, "start4");
            xmlfile_mah = Utility.readXMLFile(codebase + viz + "_mah.xml");
            System.out.println("start5");
            preloader.update(60);
        //        JOptionPane.showMessageDialog(rootPane, "start5");
        } catch (Exception e) {
            System.out.println("readParameters::" + e.getMessage());
        }



    }

    @Override
    public void destroy() {
    //    ActivityManager.stopThread();
    }

    /**
     * Automatically shuts down the ActivityManager when the applet is
     * stopped.
     * @see java.applet.Applet#stop()
     */
    @Override
    public void stop() {
      //  ActivityManager.stopThread();
    }
}
