package applet;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Label;
import javax.swing.JPanel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.swing.plaf.DimensionUIResource;

/**
 *
 * @author Myroslav Sypa
 */
public class ColorMap {
    
    public JPanel createPanel(){
        FlowLayout fl = new FlowLayout(FlowLayout.CENTER);
        fl.setHgap(0);
        JPanel p =new JPanel(fl);
        p.add(new Label("0", Label.RIGHT));
        for(double i=0;i<=1;i+=0.01){
            JPanel pp = new JPanel();
            pp.setBackground(getColor(i*255));
            Dimension d = new DimensionUIResource(5, 20);
            pp.setPreferredSize(d);
            p.add(pp);
        }
        p.add(new Label("1", Label.LEFT));
        return p;
    }
    
    public Color getColor(double c){
        Float ff = new Float(c+60);
        //Color.getHSBColor(ff.floatValue(), 0, ff.floatValue());
        return Color.getHSBColor( -ff/(255f+50), 1f, 1f);

    }

}
