/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applet;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Myroslav Sypa
 */
public class PCAPlot extends AbstractPanel{
    
    private String location;
    
    public PCAPlot(String location){
        this.location = location;
    }
    
    public JPanel createPCAPanel() throws MalformedURLException, IOException{
        Component[] cp = new Component[1];
                 URL url = new URL(location);
                 BufferedImage img_ = ImageIO.read(url);
                 ImageIcon image = new ImageIcon(img_);
        ImagePanel img =new ImagePanel(image.getImage());
        
         JScrollPane scrolltable = new JScrollPane(img, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        Dimension d = new Dimension(700, 480);
        scrolltable.setPreferredSize(  d);
        scrolltable.setMaximumSize( d);
        scrolltable.setMinimumSize( d);
        
        cp[0] = scrolltable;
        return createPanel(520, "PCA plots", cp, new FlowLayout(), true, null);
    }

}
