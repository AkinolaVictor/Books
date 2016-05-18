
/*
   This code is totally uncommented.  Sorry.
   
   David Eck
   Department of Mathematics and Computer Science
   Hobart and William Smith Colleges
   Geneva, NY   14456
   E-mail:  eck@hws.edu
   WWW:     http://math.hws.edu/eck/
   
   June 18, 1996
   
   Modified June 2002 to remove definitions of getWidth() and getHeight(),
   which conflict with standard AWT methods in Java 1.3.  Modified August
   2010 to remove references to deprecated method size().
          
*/

import java.awt.*;

class CACanvas extends Canvas {

   private boolean backedUp = true;
   private Image OSC = null;
   private int[] rule = null;
   private int ruleCt = 8;
   private int states = 2;
   private int neighbors = 3;
   private int halfNeighborCt = 1;
   private int[] startWorld = null;
   private int[] world = null;
   private int[] newWorld = null;
   private Color[] stateColor = null;
   private int width = 0;
   private int height = 0;
   private int lineCt = 0;
      
   synchronized void properties(int stateCt, int neighborCt, 
                                int[] theRules, Color[] theColors, 
                                boolean useOffScreenCanvas) {
      backedUp = useOffScreenCanvas;
      states = stateCt;
      if (states < 2)
         states = 2;
      neighbors = neighborCt;
      if (neighbors % 2 == 0)
        neighbors++;
      if (neighborCt < 3)
         neighborCt = 3;
      ruleCt = 1;
      for (int i=0; i<neighbors; i++) {
         if (ruleCt > 32768) {
            states = 2;
            neighbors = 3;
            ruleCt = 8;
            break;
         }
         ruleCt = ruleCt*states;
      }
      halfNeighborCt = neighbors / 2;
      if (theRules != null && theRules.length >= ruleCt)
         rule = theRules;
      else 
         rule = null;
      if (theColors != null && theColors.length >= states)
         stateColor = theColors;
      else
         theColors = null;
   }
   
/*   int getWidth() {  
     return (width == 0)? getWidth() : width;
   } 
Removed because public int getWidth() is already defined in Java 1.3.1   
*/
   
   void disposeOSC() {
      OSC = null;
   }
      
   synchronized void set(int[] theWorld) {
       if ((width != getWidth() || height != getHeight()))
          OSC = null;
       width = getWidth();
       height = getHeight();
       if (world == null || world.length < width)
          world = new int[width];
       if (newWorld == null || newWorld.length < width)
          newWorld = new int[width];
       if (rule == null || rule.length < ruleCt) {
          rule = new int[ruleCt];
          for (int i = 0; i < ruleCt; i++)
             rule[i] = 1;
          rule[0] = 0;
          rule[ruleCt-1] = 0;
       }
       if (stateColor == null || stateColor.length < states) {
          stateColor = new Color[states];
          stateColor[0] = Color.white;
          stateColor[1] = Color.black;
          if (states>2) {
             float hue = 0.0F;
             for (int i = 2; i < states; i++) {
                 stateColor[i] = Color.getHSBColor(hue,1.0F,1.0F); 
                 hue = hue + (1.0F / (states - 2));
             }
          }
       }
       if (theWorld == null) {
          for (int i=0; i<width; i++)
             world[i] = 0;
          world[width/2] = 1;
       }
       else {
          for (int i=0; i<width; i++)
             if (i < theWorld.length && theWorld[i] >= 0 && theWorld[i] < states)
                world[i] = theWorld[i];
             else
               world[i] = 0;
       }
       if (startWorld == null || startWorld.length < width)
          startWorld = new int[width];
       for (int i = 0; i<width; i++)
          startWorld[i] = world[i];
       if (backedUp && OSC == null)
           OSC = createImage(width,height);
       clear();
   }
   
   void reset() {
      if (startWorld != null) {
          if (world == null || world.length < width)
             world = new int[width];
          for (int i=0; i<width; i++)
             if (i < startWorld.length && startWorld[i] >= 0 && startWorld[i] < states)
                world[i] = startWorld[i];
             else
               world[i] = 0;
         clear();
      }
   }
   
   void setStart() {
      if (world != null) {
         if (startWorld == null || startWorld.length < world.length)
            startWorld = new int[world.length];
         for (int i=0; i<world.length; i++)
            startWorld[i] = world[i];
      }
   }
   
   synchronized void setCell(int cellNum, int state) {
       if (world != null && cellNum >= 0 && cellNum < world.length && state >= 0 && state < states)
            world[cellNum] = state;
   }
   
   synchronized void clear() {
      if (OSC != null) {
         Graphics g = OSC.getGraphics();
         if (stateColor != null)
            g.setColor(stateColor[0]);
         else
            g.setColor(Color.black);
         g.fillRect(0,0,width,height);
         g.dispose();
         repaint();
      }
      else {
         Graphics g = getGraphics();
         if (stateColor != null)
            g.setColor(stateColor[0]);
         else
            g.setColor(Color.black);
         g.fillRect(0,0,width,height);
         g.dispose();
      }
      lineCt = 0;
   }
   
   synchronized void  setRules(int[] theRules) {
      if (theRules.length >= ruleCt)
         rule = theRules;
   }
   
   synchronized void  setRule(int ruleNum, int value) {
      if ((rule != null) && (value >= 0) && (value < states) && (ruleNum >= 0) && (ruleNum < rule.length))
         rule[ruleNum] = value;
   }
   
   synchronized void  setColors(Color[] theColors) {
      if (theColors.length >= states)
         stateColor = theColors;
   }

   synchronized void  setColor(int colorNum, Color value) {
      if ((stateColor != null) && (value != null) && (colorNum >= 0) && (colorNum < stateColor.length))
         stateColor[colorNum] = value;
   }
   
   public void update(Graphics g) {
      paint(g);
   }
   
   public void paint(Graphics g) {
      if (OSC != null)
         g.drawImage(OSC,0,0,this);
   }
   
   synchronized void putWorld() {
      Graphics g=null;
      if (OSC == null)
         g = getGraphics();
      else
         g = OSC.getGraphics();
      if (lineCt >= height && g != null) {
         g.copyArea(0,0,width,height,0,-1);
         lineCt = height-1;
      }
      if (g != null) {
         for (int i=0; i< width; i++) {
            g.setColor(stateColor[world[i]]);
            g.fillRect(i,lineCt,1,1);
         }
         lineCt++;
         g.dispose();
      }
      if (OSC != null) {
         g = getGraphics();
         if (g != null && OSC != null) {
            g.drawImage(OSC,0,0,this);
            g.dispose();
         }
      }
   }
   
   synchronized void next() {
      if (width > 0) {
        for (int i=0; i < width; i++) {
           int left = i - halfNeighborCt;
           if (left<0)
              left = width+left;
           int right = i + halfNeighborCt;
           if (right>=width)
              right = right-width;
           int k = 0;
           if (left < right) {
              for (int j=left; j<=right; j++)
                 k = states*k + world[j];
           }
           else {
              for (int j=left; j<width; j++)
                k = states*k + world[j];
              for (int j=0; j<=right; j++)
                k = states*k + world[j];
           }
           newWorld[i] = rule[k];
         }
         putWorld();
         for (int i = 0; i < width; i++)
            world[i] = newWorld[i];
      }
   }   // next()
   
}  // class CACanvas
