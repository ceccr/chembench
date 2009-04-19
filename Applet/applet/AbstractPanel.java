package applet;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.plaf.DimensionUIResource;




/**
 *
 * @author msypa
 */
public class AbstractPanel {
    
    protected JPanel createPanel(final int h, String title, final Component[] obj, LayoutManager layout, boolean isresizable,final JPanel mainp){
        final JPanel p = new JPanel(layout);
        p.setBorder(BorderFactory.createTitledBorder(title));
        if(isresizable)
        p.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
              //  if (e.getClickCount() == 2) {
               changeComponentSize(h,p,obj, mainp);     
            }
            

            public void mousePressed(MouseEvent e) {
               // throw new UnsupportedOperationException("Not supported yet.");
            }

            public void mouseReleased(MouseEvent e) {
               // throw new UnsupportedOperationException("Not supported yet.");
            }

            public void mouseEntered(MouseEvent e) {
              //  throw new UnsupportedOperationException("Not supported yet.");
            }

            public void mouseExited(MouseEvent e) {
               // throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        
 
        for(int i=0;i<obj.length;i++)
        p.add(obj[i]);
        
        Dimension d = new Dimension(720,h);
        p.setPreferredSize(d);
        p.setMinimumSize(d);
        p.setMaximumSize(d);
        return p;
    }
    
    
    private void changeComponentSize(int h, JPanel p, Component[] obj, JPanel mainp){
        if(p.getHeight()>20){
                    for(int i=0;i<obj.length;i++)
                    p.getComponent(i).setVisible(false);
                    Dimension d = new Dimension(720,20);
                        p.setPreferredSize(d);
                        p.setMinimumSize(d);
                        p.setMaximumSize(d);
                        p.revalidate();
                        p.repaint();
                        mainp.setPreferredSize(new DimensionUIResource(mainp.getWidth(), mainp.getHeight()+p.getHeight()-20));
                        mainp.getComponent(0).setPreferredSize(new DimensionUIResource(mainp.getComponent(0).getWidth(), mainp.getComponent(0).getHeight()+p.getHeight()-20));
                        mainp.revalidate();
                        mainp.repaint();
                    }
                    else{
                    for(int i=0;i<obj.length;i++)
                    p.getComponent(i).setVisible(true);
                    Dimension d = new Dimension(720,h);
                        p.setPreferredSize(d);
                        p.setMinimumSize(d);
                        p.setMaximumSize(d);
                        p.revalidate();
                        p.repaint();
                        mainp.setPreferredSize(new DimensionUIResource(mainp.getWidth(), mainp.getHeight()-h+20));
                        mainp.getComponent(0).setPreferredSize(new DimensionUIResource(mainp.getComponent(0).getWidth(),mainp.getComponent(0).getHeight()-h+20));
                        mainp.revalidate();
                        mainp.repaint();
            //        }
                }
    }
    
    

}
