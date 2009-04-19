/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FontMetrics;
//import java.awt.Frame;
import java.awt.Frame;
import java.awt.Graphics;


/**
 *
 * @author Myroslav Sypa
 */
public class Loader extends Frame
{
    private              int Count;
    private static final int FrameBottom = 24;

    public Loader (String Title)
    {
        super(Title);

        Count = 0;
   

        // Allowing this to be resized causes more trouble than it is worth
        // and the goal is for this to load and launch quickly!
        setResizable(false);

        setLayout(null);
        addNotify();
        resize (insets().left + insets().right + 379,
                insets().top + insets().bottom + FrameBottom);
    }

    public synchronized void show()
    {
        move(150, 150);
        super.show();
    }

    // Update the count and then update the progress indicator.  If we have
    // updated the progress indicator once for each item, dispose of the
    // progress indicator.
    public void update (int val)
    {
        Count = val;

        if (Count == 100)
            dispose();
        else
            repaint();
    }

        
    public void updateTitle(String title){
        super.setTitle(title);
    }
    
    // Paint the progress indicator.
    public void paint (Graphics g)
    {
        Dimension FrameDimension  = size();
        double    PercentComplete = (double)Count;
        int       BarPixelWidth   = (FrameDimension.width * Count)/ 100;

        // Fill the bar the appropriate percent full.
        g.setColor (Color.orange);
        g.fillRect (0, 0, BarPixelWidth, FrameDimension.height);

        // Build a string showing the % completed as a numeric value.
        String s        = String.valueOf((int)PercentComplete) + " %";

        // Set the color of the text.  If we don't, it appears in the same color
        // as the rectangle making the text effectively invisible.
        g.setColor (Color.black);

        // Calculate the width of the string in pixels.  We use this to center
        // the string in the progress bar window.
        FontMetrics fm       = g.getFontMetrics(g.getFont());
        int StringPixelWidth = fm.stringWidth(s);

        g.drawString(s, (FrameDimension.width - StringPixelWidth)/2, FrameBottom);
    }

    public boolean handleEvent(Event event)
    {
        if (event.id == Event.WINDOW_DESTROY)
        {
            dispose();
            return true;
        }

        return super.handleEvent(event);
    }
}
