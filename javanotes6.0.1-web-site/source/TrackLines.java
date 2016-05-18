
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


/**
 * This applet shows short line segments arranged into regular rows and
 * columns.  One end of each line if fixed, and the line can rotate around
 * that endpoint.  The lines rotate at randomly selected speeds, unless
 * the mouse moves over the applet.  Then all the lines point at the
 * mouse.  But if the mouse doesn't move for a while, the lines start
 * drifting again.  Furthermore, if the mouse button is pressed, then
 * the color of the lines changes.  The color depends on the exact mouse
 * position.
 */

public class TrackLines extends JApplet {

   boolean used = false; // Set to true the first time the lines
                         //    point at the mouse; until then, a
                         //    message is also displayed.

   int ROWS = 5;     // how many rows of lines; can be set as an applet parameter
   int COLUMNS = 7;  // how many columns; can be set as an applet parameter

   int[][] angle;       // current angle of each line, in degrees
   int[][] driftSpeed;  // current rotation speed of each line, in degrees per frame

   long lastTime = 0;  // the time when the lines last pointed at the mouse (milliseconds)
   int delayToDrift  = 1500;   // how long will lines point at same spot before drifting
                               //        (in milliseconds)
   
   int last_x = -1;  // the lines were most recently pointed at (last_x, last_y)
   int last_y = -1;  //     -1 means that the lines are just drifting

   Timer timer;   // Timer for doing the drifting animation
   
   int sleepTime = 75;  // Delay time for timer
   
   DisplayPanel display;  // The content pane of the applet.
   
   /**
    * An object belonging to this class is the content pane of the
    * applet.  The paintComponent() method is overridden in this class
    * to do draw the lines and message.
    */
   class DisplayPanel extends JPanel {
      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         Graphics2D g2 = (Graphics2D)g;
         g2.setStroke(new BasicStroke(2));
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         int height = getHeight();
         int width = getWidth();
         int hSpace = (width + 2) / (COLUMNS);
         int vSpace = (height + 2) / (ROWS);
         int space = Math.min(hSpace/2, vSpace/2);
         for (int i=0; i<ROWS; i++)
            for (int j=0; j<COLUMNS; j++) {
               angle[i][j] += driftSpeed[i][j];  // rotate the line a bit
               if (Math.random() < 0.05)  // a 1 out of 20 chance that drift speed changes
                  driftSpeed[i][j] = (int)(31*Math.random()) - 15;
               int x = j*hSpace + space;  // fixed endpoint of line
               int y = i*vSpace + space;
               int x1 = x + (int)(space*Math.cos((Math.PI/180.0)*angle[i][j]));  // other endpoint
               int y1 = y + (int)(space*Math.sin((Math.PI/180.0)*angle[i][j]));
               g2.drawLine(x,y,x1,y1);
            }
         if (!used) {
            g2.setColor(Color.red);
            g2.drawString("Point your mouse at me!", 25, 40);
            g2.drawString("Click on me!", 25, 80);
         }
      }
   }
   
   /**
    * An object belonging to this class is registered to listen for
    * MouseEvents on the display, to make the lines track the mouse.
    */
   class MouseHandler implements MouseListener, MouseMotionListener {
      public void mouseMoved(MouseEvent e) {
            // point at the mouse each time it moves
         point(e.getX(),e.getY());
      }
      public void mouseDragged(MouseEvent e) {
            // point at the mouse and select a color based on its position
         selectColor(e.getX());
         point(e.getX(),e.getY());
      }
      public void mousePressed(MouseEvent e) {
         used = true;
         selectColor(e.getX());
         last_x = -1;  // this forces the lines to point at (x,y), even if its the
                       //    same point as last time
         point(e.getX(),e.getY());
      }
      public void mouseClicked(MouseEvent e) {}
      public void mouseEntered(MouseEvent e) {}
      public void mouseExited(MouseEvent e) {}
      public void mouseReleased(MouseEvent e) {}
   }

   public void init() {
      display = new DisplayPanel();
      display.setBackground(Color.BLACK);
      display.setForeground(Color.WHITE);
      display.setFont(new Font("Serif", Font.BOLD, 16));
      MouseHandler mh = new MouseHandler();
      display.addMouseListener(mh);
      display.addMouseMotionListener(mh);
      setContentPane(display);
      String param;
      param = getParameter("rows");                  // get the parameters
      if (param != null) {
         try {
            ROWS = Integer.parseInt(param);
         }
         catch (NumberFormatException e) { }
      }
      param = getParameter("columns");
      if (param != null) {
         try {
            COLUMNS = Integer.parseInt(param);
         }
         catch (NumberFormatException e) { }
      }
      angle = new int[ROWS][COLUMNS];              // set up random initial angles and drift speeds
      driftSpeed = new int[ROWS][COLUMNS];
      for (int i = 0; i<ROWS; i++) {
         for (int j=0; j<COLUMNS; j++) {
            angle[i][j] = (int)(360*Math.random());
            driftSpeed[i][j] = (int)(41*Math.random()) - 20;
         }
      }
      timer = new Timer(sleepTime,new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            drift();
         }
      });
   }

   public void start() {
      timer.start();
   }

   public void stop() {
      timer.stop();
   }

   void drift() {
         // all the lines rotate a bit, but this is only done if
         // its been at least delayToDrift milliseconds since the
         // last time the lines pointed at the mouse
      if (System.currentTimeMillis() - lastTime < delayToDrift)
         return;
      for (int i=0; i<ROWS; i++)
         for (int j=0; j<COLUMNS; j++) {
            angle[i][j] += driftSpeed[i][j];  // rotate the line a bit
            if (Math.random() < 0.02)  // a 1 out of 50 chance that drift speed changes
               driftSpeed[i][j] = (int)(31*Math.random()) - 15;
         }
      display.repaint();  // arrange for screen updating
   }

   void point(int x1, int y1) {
         // all the lines point at (x1,y1)
      if (last_x == x1 && last_y == y1)
         return;  // if they are already pointing there, there's nothing to do
      int height = getHeight();
      int width = getWidth();
      int hSpace = (width + 2) / (COLUMNS);
      int vSpace = (height + 2) / (ROWS);
      int space = Math.min(hSpace/2, vSpace/2);
      for (int i=0; i<ROWS; i++)
         for (int j=0; j<COLUMNS; j++) {
            int x = j*hSpace + space;  // fixed endpoint of line
            int y = i*vSpace + space;
            int dx = x1 - x;  // (dx,dy) is a vector pointing in the right direction
            int dy = y1 - y;
            if (dx != 0 || dy != 0) {              
               double angl = Math.atan2(dy,dx);   // computes angle of vector (dx,dy)
               angle[i][j] = (int)(angl*180.0/Math.PI);
            }
         }
      last_x = x1;   // save info about this event
      last_y = y1;
      lastTime = System.currentTimeMillis();
      display.repaint();  // arrange for screen updating
   }

   void selectColor(int x) {
      // change the drawing color, depending on the value of x.
      // x gives the hue of the color, ranging from violet to red.
      if (x < 0)
         x = 0;
      else if (x > getWidth())
         x = getWidth();
      float hue = ((float)(getWidth()-x)) / getWidth();
      display.setForeground(Color.getHSBColor(hue,1.0F,1.0F));
      display.repaint();
   }

}