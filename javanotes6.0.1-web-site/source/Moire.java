
/*******************************************************************************


Class Moire implements an animated Moire pattern.  A Moire patterns occurs when
two similar patters are almost superimposed.  There is a kind of visual interference.
In this case, the pattern consists of lines radiating out from a common center.
One such pattern is drawn with the lines radiating out from the center of the
applet.  A second pattern has a center that drifts about, producing a changing
interference pattern.

You can also click and drag on the applet to move the second pattern about by
hand.

Several parameters can be set by <param> tags:

    Name       Default    Legal values     Meaning 
    ---------  ---------  ---------------  -----------------------------------
    lineCount   36         1 to 100         Number of lines drawn in each set
                                               of lines.  (Note:  each "line"
                                               gives two "spokes" radiating from
                                               the center.)
    lineColor   red        any color        Color of the lines.
    bgColor     cyan       any color        Color seen behind the lines.
    sleepTime   25         1 to 5000        Time, in milliseconds, between
                                               movements of the pattern; smaller
                                               values give faster movements.
    border      0          0 to 50          Width of border drawn around the
                                               Moire pattern; note that the
                                               default is to have no border.
    borderColor blue       any color        Color of the border.
    
Note that a color can be specified either as a set of three integers between
0 and 255, giving the red, blue, and green components of the color, or it
can be specified as one of the built-in color names: white, black, red, green,
blue, yellow, cyan, magneta, pink, orange, gray, lightGray, or darkGray.
Color names are not case sensitive, although param names are.

Note:  You might find the method getColorParam() useful in other applications.

Modified in July 1998 to be fully compliant with Java 1.1.
Modified in July 2010 to eliminate use of an off-screen image and of the
      deprecated Thread method, stop().

BY:  David Eck
     Department of Mathematics and Computer Science
     Hobart and William Smith Colleges
     Geneva, NY   14456
     
     E-mail:  eck@hws.edu


NOTE:  YOU CAN DO ANYTHING YOU WANT WITH THIS CODE AND APPLET, EXCEPT
       TRY TO COPYRIGHT OR PATENT THEM YOURSELF.

*******************************************************************************/


import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Moire extends java.applet.Applet 
                     implements Runnable, MouseListener, MouseMotionListener {

   static final Random rand = new Random();

   Thread runner;   // thread to produce the animation

   Image buffer = null;  // an off-screen canvas
   int w,h;       // width and height of the buffer
   double center_x, center_y;   // current position of the center of
                                // the second set of lines.
   int cx,cy;  // for use in mouseDrag, mouseExit;
               // position of center during dragging

   
   int mouseStart_x, mouseStart_y;   // used during dragging to hold the
                                     // location of the original mouse click
   volatile boolean dragging = false;    // set to "true" while dragging is in
                                         // progress, as signal to "run" method
                                         // to pause the regular animation.
   volatile boolean stopped = false;   // toggled when user shift-clicks on the
                                       // applet.
                            
   final static int GO = 0, SUSPEND = 1, TERMINATE = 2;           
   volatile int status = GO;
   
   int sleepTime = 25;             // applet <param>'s, described above
   Color backColor = Color.cyan;
   Color lineColor = Color.red;
   int lines = 36;
   int border = 0;
   Color borderColor = Color.blue;
      
   double[] cos, sin;  // hold sines and cosines of angle; one for each line
                       // line to be drawn.  This just avoids recomputing this
                       // all the time.
   
   public void init() {
      Color c;
      Integer temp;
      if ( (c = getColorParam("bgColor")) != null )
         backColor = c;
      if ( (c = getColorParam("lineColor")) != null )
         lineColor = c;
      if ( (temp = getIntParam("lineCount")) != null )
         lines = temp.intValue();
      if (lines < 1 || lines > 100)
         lines = 36;
      if ( (temp = getIntParam("sleepTime")) != null )
         sleepTime = temp.intValue();
      if (sleepTime < 1 || sleepTime > 5000)
         sleepTime = 25;
      if ( (temp = getIntParam("border")) != null )
         border = temp.intValue();
      if (border < 0 || border > 50)
         border = 0;
      if ( (c = getColorParam("borderColor")) != null )
         borderColor = c;
      setBackground(borderColor);
      double delta = 180.0 / lines;
      cos = new double[lines];
      sin = new double[lines];
      for (int i = 0; i < lines; i++) {
         double angle = ((i * delta * Math.PI)/180.0);
         cos[i] = Math.cos(angle);
         sin[i] = Math.sin(angle);  
      }
      this.addMouseListener(this);
      this.addMouseMotionListener(this);
   }
   
    Integer getIntParam(String paramName) {
       // retrieve an integer <param>; return null if the specified
       // param is not present or if the value is illegal
       String param = getParameter(paramName);
       if (param == null)
          return null;
       int i;
       try {
          i = Integer.parseInt(param);
       }
       catch (NumberFormatException e) {
          return null;
       }
       return new Integer(i);
    }
    
    Color getColorParam(String paramName) {
       // retrieve a color <param>; return null if the specified
       // param is not present or if the value is illegal.  Legal
       // values include the 13 named Java colors ("red", "black",
       // etc.) and RGB values given as 3 integers between 0 and 255.
       // Color names are not case sensitive.  Integers in RGB
       // colors can be separated by any non-digit characters.
       String param = getParameter(paramName);
       if (param == null || param.length() == 0)
          return null;
       if (Character.isDigit(param.charAt(0))) {  // try to parse RGB color
          int r=0,g=0,b=0;
          int pos=0;
          int d=0;
          int len=param.length();
          while (pos < len && Character.isDigit(param.charAt(pos)) && r < 255) {
              d = Character.digit(param.charAt(pos),10);
              r = 10*r + d;
              pos++;
          }
          if (r > 255)
             return null;
          while (pos < len && !Character.isDigit(param.charAt(pos)))
             pos++;
          if (pos >= len)
             return null;
          while (pos < len && Character.isDigit(param.charAt(pos)) && g < 255) {
              d = Character.digit(param.charAt(pos),10);
              g = 10*g + d;
              pos++;
          }
          if (g > 255)
             return null;
          while (pos < len && !Character.isDigit(param.charAt(pos)))
             pos++;
          if (pos >= len)
             return null;
          while (pos < len && Character.isDigit(param.charAt(pos)) && b < 255) {
              d = Character.digit(param.charAt(pos),10);
              b = 10*b + d;
              pos++;
          }
          if (b > 255)
             return null;
          return new Color(r,g,b);          
       }
       param.toLowerCase();
       if (param.equals("black"))
          return Color.black;
       if (param.equals("white"))
          return Color.white;
       if (param.equals("red"))
          return Color.red;
       if (param.equals("green"))
          return Color.green;
       if (param.equals("blue"))
          return Color.blue;
       if (param.equals("yellow"))
          return Color.yellow;
       if (param.equals("cyan"))
          return Color.cyan;
       if (param.equals("magenta"))
          return Color.magenta;
       if (param.equals("pink"))
          return Color.pink;
       if (param.equals("orange"))
          return Color.orange;
       if (param.equals("gray"))
          return Color.gray;
       if (param.equals("darkgray"))
          return Color.darkGray;
       if (param.equals("lightgray"))
          return Color.lightGray;
       return null;  // param is not a legal color
    }

   synchronized public void start() {
      if (runner == null || !runner.isAlive()) {
         runner = new Thread(this);
         status = GO;
         runner.start();
      }
      else {
         status = GO;
         notify();
      }
   }
   
   synchronized public void stop() {
      if (runner != null && runner.isAlive()) {
         status = SUSPEND;
         notify();
      }
   }
   
   synchronized public void destroy() {
      if (runner != null && runner.isAlive()) {
         status = TERMINATE;
         notify();
      }
   }
   
   synchronized public void mousePressed(MouseEvent evt) {
      if (evt.isShiftDown()) {  // toggle the drifting of the pattern
         stopped = !stopped;
      }
      else {  // begin dragging lines
         mouseStart_x = evt.getX();
         mouseStart_y = evt.getY();
         dragging = true;
      }
      notify();
   }
   
   synchronized public void mouseDragged(MouseEvent evt) {
      if (dragging) {
         int offset_x = evt.getX() - mouseStart_x;
         int offset_y = evt.getY() - mouseStart_y;
         cx = (int)center_x + offset_x;
         cy = (int)center_y + offset_y;
         Graphics g = buffer.getGraphics();
         makeMoire(g,w,h,cx,cy);
         g.dispose();
         repaint();
      }
   }
      
   synchronized public void mouseReleased(MouseEvent evt) {
      if (dragging) {
         dragging = false;
         center_x = center_x + evt.getX() - mouseStart_x;
         center_y = center_y + evt.getY() - mouseStart_y;
      }
      notify();
   }
   
   public void mouseClicked(MouseEvent evt) { }  // to satisfy the MouseListener/MouseMotionListener interfaces
   public void mouseEntered(MouseEvent evt) { }
   public void mouseMoved(MouseEvent evt) { }
   public void mouseExited(MouseEvent evt) { }
   
   synchronized public void paint(Graphics g) {  // paint method just copies canvas to applet
      g.drawImage(buffer,border,border,this);
   }
   
   public void update(Graphics g) {  // replace standard update method so it doesn't
                                     // erase the screen
      paint(g);
   }
   
   synchronized void makeMoire(Graphics g, int w, int h, int cx, int cy) {
        // Create the Moire pattern in the buffer.  w and h give the
        // size of the buffer (and yes, that could have been gotten from g).
        // cx and cy give the position of the center of the second set of lines.
      g.setColor(backColor);
      g.fillRect(0,0,w,h);
      g.setColor(lineColor);
      drawMoire(g,2*w,2*h,w/2,h/2);
      drawMoire(g,2*w,2*h,cx,cy);
   }   

   void drawMoire(Graphics g, int width, int height, int center_x, int center_y) {
        // one set of lines, with center at center_x, center_y
     int s = ( (width > height)? width : height );
     s = (int)(s * 0.72);
     for (int i = 0; i < lines; i++) {
        int x = (int)(s*cos[i]);
        int y = (int)(s*sin[i]);
        g.drawLine(center_x + x, center_y + y, center_x - x, center_y - y);
     }
   }
   
   public void run() {        // run method for animation thread
   
      if (buffer == null) {
         w = getSize().width - 2*border;   // get the width and height of the canvas
         h = getSize().height - 2*border;  //   (Applet is not effectively resizable.)
         buffer = createImage(w,h);  // create an off screen canvas of the same size
      }
      center_x = w / 2;   // initial position of center of second set of lines
      center_y = h / 2;
      double x_min = center_x - 25;   // limits for wandering of second set of lines
      double x_max = center_x + 25;
      double y_min = center_y - 25;
      double y_max = center_y + 25;
      double dx, dy;   // amount by which center should move at each step
      do {
          dx = 5 * (rand.nextDouble() - 0.5);
          dy = 5 * (rand.nextDouble() - 0.5);
      } while (dx*dx + dy*dy < 1);
      
      while (true) {
        synchronized(this) { 
           while (status == SUSPEND || ( (stopped || dragging) && status != TERMINATE) ) {
              try {
                 wait();
              }
              catch (InterruptedException e) {
              }
           }
        }
        if (status == TERMINATE)
           return;
        if (!dragging && !stopped) {
          center_x += dx;   // drift by amount (dx,dy)
          center_y += dy;
          if (center_x < x_min) {  // make sure motion is towards the right
             do {
                dx = rand.nextDouble() * 2.5;
             } while (dx*dx + dy*dy < 1);
          }
          else if (center_x > x_max) {  // make sure motion is towards the left
             do {
                dx = - rand.nextDouble() * 2.5;
             } while (dx*dx + dy*dy < 1);
          }
          if (center_y < y_min) {  // make sure motion is downwards
             do {
                dy = rand.nextDouble() * 2.5;
             } while (dx*dx + dy*dy < 1);
          }
          else if (center_y > y_max) {  // make sure motion is upwards
             do {
                dy = - rand.nextDouble() * 2.5;
             } while (dx*dx + dy*dy < 1);
          }
          if (rand.nextDouble() < 0.01) {  // Occasionally, change motion vector at random
              do {
                dx = 5 * (rand.nextDouble() - 0.5);
                dy = 5 * (rand.nextDouble() - 0.5);
              } while (dx*dx + dy*dy < 1);
          }
          Graphics g = buffer.getGraphics();   // create and display the pattern.
          makeMoire(g,w,h,(int)center_x,(int)center_y);
          g.dispose();
          repaint();
        }
        synchronized(this) {
          try {
             wait(sleepTime);
          }
          catch (InterruptedException e) {
          }
        }
      } // end while
      
   } // end run()

}  // end of class Moire
