
/*
   A decorative applet that draws symmetric pictures of moving shapes,
   looking something like a view in a kaleidoscope.  Many aspects of the
   image are chosen randomly.  A given random animation plays for about
   10 seconds, then a new selection is made.  There are four types
   of symmetry that are possible:  no symmetry, two-fold symmetry
   obtained by reflecting each shape horizontally, four-fold symmetry
   obtained by vertical and horizontal reflection, and eight-fold
   symmetry which adds diagonal reflections.
   
   This applet works well at a size of 200 by 200 pixels.  If the applet
   is larger, you might want to increase the size or the number of
   shapes created in the makeFigures() method.
   
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Vector;

public class KaleidoAnimate extends JApplet {

   int symmetryStyle; // A number between 0 and 18 specifying the type of symmetry that
                      // the animation displays.  The value is set in makeFigures()
                      // and is used in putMultiFigure().  The meaning of the values:
                      // 0 for none, 1-2 for two-way, 2-8 for four-way, 8-19 for 8-way.

   Vector figureList = new Vector();  // Contains the figures in the current animation.
                                      // The figures belong to the class Figure, defined below.
   
   int frameNumber = 109;

   public void init() {
      setContentPane( new JPanel() { 
            public void paintComponent(Graphics g) {
                  drawFrame(g);
            }
      });
      Timer t = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                  frameNumber++;
                  if (frameNumber >= 110) {
                        frameNumber = 0;
                        makeFigures();
                  }
                  repaint();
            }
      });
      t.setInitialDelay(500);
      t.start();
   }
   
   public void drawFrame(Graphics g) {
         // Draw one frame in the animation.  A new random animation is
         // begun each time the frame number is zero (including the first
         // frame that is shown).  For frames numbered 100 to 109, only a
         // black rectangle is shown.
                                          
      ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
      ((Graphics2D)g).setStroke( new BasicStroke(2) );
      
      if (frameNumber >= 100) {
         g.setColor(Color.black);  // For frames 100-109, just draw a black rectangle.
         g.fillRect(0, 0, getWidth(), getHeight());
      }
      
      else {  // Draw the figures over the applet's background color.
              
         g.setColor(getBackground());  // Fill the frame with the background color.
         g.fillRect(0,0,getWidth(),getHeight());
         
         for (int i = 0; i < figureList.size(); i++) {
            Figure fig = (Figure)figureList.elementAt(i);  // Get the figure from the vector.
            fig.drift();   // The figure moves itself a bit.
            putMultiFigure(g, fig);  // Draw the figure, plus required reflections.
         }
         
         g.setColor(Color.black);  // Draw a 1-pixel black border around the frame.
         g.drawRect(0,0,getWidth()-1,getHeight()-1);
      }
      
      
   } // end drawFrame()
   
   
   private void makeFigures() {
          // Create the random data for a new random animation.
          
      figureList.removeAllElements();
      
      int figureCt = 3 + (int)(Math.random()*50);  // Number of figures.
      
      int figStyle = (int)(Math.random()*5);  // What type if figures will be used?
                                              // 0 = all lines, 1 = all rectangles,
                                              // 2 = all ovals, 3 or 4 = a mixture
      
      boolean monochrome = Math.random() < 0.1;  // A 10% chance that the picture will be in grayscale.
      
      boolean useOneColor = Math.random() < 0.15;  // A 15% change that all the figures will be the same color.
      
      Color useColor = null;  // If useOneColor is true, this will be the color used.
      if (useOneColor) {
         if (monochrome)
            useColor = Color.getHSBColor(0.0F, 0.0F, (float)Math.random());  // A random gray.
         else 
            useColor = Color.getHSBColor((float)Math.random(), 1.0F, 1.0F);  // A random bright color.
      }

      symmetryStyle = (int)(Math.random()*20);  // Determines the type of symmetry used in
                                                // the animation.  See comment on the declaration
                                                // of this instance variable, above.

      /* Set a random background color. */

      if (Math.random() < 0.1)        // A 10% chance that the background is white.
         setBackground(Color.white);
      else if (Math.random() < 0.11)  // A 10% chance (1/9 of 9/10) that the color is black
         setBackground(Color.black);
      else if (monochrome)
         setBackground(Color.gray);   // A gray background for most monochrome images.
      else
         setBackground(Color.getHSBColor((float)Math.random(), 0.5F, 0.5F)); // A random not-so-bright color.
         
      for (int i = 0; i < figureCt; i++) {
             // Create a random figure.
         Figure fig = new Figure();
         fig.x1 = (int)(Math.random()*getWidth());  // Somewhere on the screen, horizontally.
         fig.x2 = fig.x1 + (int)(Math.random()*20) + 10;  // 10 to 29 pixels wide.
         fig.y1 = (int)(Math.random()*getHeight()); // Somewhere on the screen, vertically.
         fig.y2 = fig.y1 + (int)(Math.random()*20) + 10;  // 10 to 29 pixels tall.
         fig.driftx = (int)(Math.random()*13) - 6; // Moves horizontally between -6 and 6 pixels per frame.
         fig.drifty = (int)(Math.random()*13) - 6; // Moves vertically between -6 and 6 pixels per frame.
         if (figStyle > 2)
            fig.style = (int)(Math.random()*3); // Randomly make it a line, rect, or oval.
         else
            fig.style = figStyle;
         if (useOneColor)
            fig.color = useColor;
         else if (monochrome)
            fig.color = Color.getHSBColor(0.0F, 0.0F, (float)Math.random()); // A random gray.
         else
            fig.color = Color.getHSBColor((float)Math.random(), 1.0F, 1.0F); // A random bright color.
         figureList.addElement(fig);  // Add the figure to the vector.
      }
      
   }  // end makeFigures();
   

   private void put(Graphics g, int figureType, int x1, int y1, int x2, int y2) {
         // Draws a figure of the specified type, between (x1,y1) and (x2,y2);
      if (figureType == 0) {
              // If the figure is a line, just draw it.
         g.drawLine(x1,y1,x2,y2);
      }
      else {
              // For a rect or oval, we need the top-left corner, the height, and the width.
              // The height and width must be positive.
         int x, y, w, h;
         if (x2 > x1) {
            x = x1;
            w = x2 - x1;
         }
         else {
            x = x2;
            w = x1 - x2;
         }
         if (y2 > y1) {
            y = y1;
            h = y2 - y1;
         }
         else {
            y = y2;
            h = y1 - y2;
         }
         if (figureType == 1)
            g.fillRect(x,y,w,h);
         else
            g.fillOval(x,y,w,h);
      }
   } // end put()
   
   
   private void putMultiFigure(Graphics g, Figure figure) {
           // Draw the figure and its reflections in the graphics context g.
           // The reflections that we need are specified by the instance
           // variable, symmetryStyle.
           
      g.setColor(figure.color);  // Use the color specified by figure.
      
      put(g, figure.style, figure.x1, figure.y1,      // Draw the figure.
                           figure.x2, figure.y2);
                           
      if (symmetryStyle > 0) {  
             // Draw the horizontal reflection.
         put(g, figure.style, getWidth() - figure.x1, figure.y1,
                              getWidth() - figure.x2, figure.y2);
      }
      
      if (symmetryStyle > 2) {
            // Draw the vertical reflection and the figure obtained
            // by reflecting both horizontally and vertically.
         put(g, figure.style, figure.x1, getHeight() - figure.y1,
                              figure.x2, getHeight() - figure.y2);
         put(g, figure.style, getWidth() - figure.x1, getHeight() - figure.y1,
                              getWidth() - figure.x2, getHeight() - figure.y2);
      }
      
      if (symmetryStyle > 8) {
             // Calculate the diagonal reflection of the figure.
             // Draw that reflection, then draw its three reflections.
         int a1 = (int)(((double)figure.y1 / getHeight()) * getWidth());
         int b1 = (int)(((double)figure.x1 / getWidth()) * getHeight());
         int a2 = (int)(((double)figure.y2 / getHeight()) * getWidth());
         int b2 = (int)(((double)figure.x2 / getWidth()) * getHeight());
         put(g, figure.style, a1, b1, a2, b2);
         put(g, figure.style, getWidth() - a1, b1, getWidth() - a2, b2);
         put(g, figure.style, a1, getHeight() - b1, a2, getHeight() - b2);
         put(g, figure.style, getWidth() - a1, getHeight() - b1, 
                                getWidth() - a2, getHeight() - b2);
      }
      
   } // end putMultiFigure()
   

   private class Figure {
           
           // This class holds all the data relevant to one of the
           // figures in an animation.  The values of the instance
           // variables in this class are set when the figures
           // are created in the makeFigures() method.
           
      int x1, x2, y1, y2;  // The two corners of the figure.
                           // (These satisfy x2 > x1 and y2 > y1)
      
      int driftx, drifty;  // How much this figure moves from frame to frame.
      
      int style;  // Type of figure.  0 for line, 1 for rect, 2 for oval.

      Color color;  // Color of this figure.

      void drift() {
              // Displace the figure by the amout given in the instance
              // variables driftx and drifty.  If the figure has moved
              // off the applet, move it to the other side of the
              // applet so it will reappear.
         x1 += driftx;
         x2 += driftx;
         y1 += drifty;
         y2 += drifty;
         if (x2 < 0) {
            x2 = getWidth() + (x2 - x1);
            x1 = getWidth();
         }
         else if (x1 > getWidth()) {
            x1 = -(x2 - x1);
            x2 = 0;
         }
         if (y2 < 0) {
            y2 = getHeight() + (y2 - y1);
            y1 = getHeight();
         }
         else if (y1 > getHeight()) {
            y1 = -(y2 - y1);
            y2 = 0;
         }
      }
      
   }   // end nested class Figure


}  // end class KaleidascopeAnimation

