
/* 
   This is a rather silly applet that just draws a static picture of
   a set of nested rectangles.  It ASSUMES that the size of the
   applet, as specified in the applet tag, is 300 pixels wide by
   160 pixels high.  It is declared as a subclass of SimpleAnimationApplet2,
   even though nothing changes from one frame to the next, for
   pedagogical purposed.
*/

import java.awt.*;

public class StaticRects extends AnimationBase {
   
     public void drawFrame(Graphics g) {
        
         // Draw set of nested black rectangles on a red background.
         // Each nested rectangle is separated by 15 pixels on all sides
         // from the rectangle that encloses it.  The applet is
        // assumed to be 300 pixels wide and 160 pixels high.
         
      int inset;    // Gap between borders of applet and one of the rectangles.
                    
      int rectWidth, rectHeight;   // The size of one of the rectangles.
                    
      g.setColor(Color.red);
      g.fillRect(0,0,300,160);  // Fill the entire applet with red.
      
      g.setColor(Color.black);  // Draw the rectangles in black.
                                       
      inset = 0;
      
      rectWidth = 299;    // Set size of the first rect to size of applet
      rectHeight = 159;
      
      while (rectWidth >= 0 && rectHeight >= 0) {
         g.drawRect(inset, inset, rectWidth, rectHeight);
         inset += 15;       // rects are 15 pixels apart
         rectWidth -= 30;   // width decreases by 15 pixels on left and 15 on right
         rectHeight -= 30;  // height decreases by 15 pixels on top and 15 on bottom
      }
      
   }  // end paint()

}  // end class StaticRects
