
/*
   This applet solves the Towers of Hanoi problem for a tower of 10 disks.
   (Ten differently-sized disks are stacked in a pile, in order of
   decreasing size.  There are two other places for piles.  The object
   is to move the pile to the second available place, subject to the
   rules that only one disk at a time can be moved, and no disk can
   be piled on top of a smaller disk.)  The solution is shown as
   an animation.  The Towers of Hanoi problem is a standard example
   of recursion.
   
   The solution is repeated over and over indefinitely.

   The applet MUST have a width of 430 and a height of 143.
*/

import java.awt.*;
import java.applet.Applet;

public class TowersOfHanoiGUI extends Applet implements Runnable {

   private static Color BACKGROUND_COLOR = new Color(255,255,180);
   private static Color BORDER_COLOR = new Color(100,0,0);
   private static Color DISK_COLOR = new Color(0,0,180);
   private static Color MOVE_DISK_COLOR = new Color(180,180,255);

   private Image OSC;   // The off-screen canvas.

   private static final int GO = 1, SUSPEND = 2,  TERMINATE = 3;  // Values for status.
                                                   
   private int status = GO;   // Controls the execution of the thread.
   
   private Thread runner;     // A thread to run the animation.
   
   /* The following variables are the data needed for the animation.  The
      three "piles" of disks are represented by the variables tower and
      towerHeight.  towerHeight[i] is the number of disks on pile number i.
      For i=0,1,2 and for j=0,1,...,towerHeight[i]-1, tower[i][j] is an integer
      representing one of the ten disks.  (The disks are numbered from 1 to 10.)
      (tower is null between repetitions of the solution.)
      
      During the solution, as one disk is moved from one pile to another,
      the variable moveDisk is the number of the disk that is being moved,
      and moveTower is the number of the pile that it is currently on.
      This disk is not stored in the tower variable.  it is drawn in a
      different color from the other disks.
      
      All the data in these variables is for use in the drawCurrentFrame() method,
      which redraws the whose picture for each frame of the animation.
   */
   
   private int[][] tower;
   private int[] towerHeight;
   private int moveDisk;
   private int moveTower;
   

   public void init() {
         // Initialize the applet by setting the background color.
      setBackground(BACKGROUND_COLOR);
   }
   

   public void run() {
          // Run the animated solution over and over until the status
          // variable is set to TERMINATED.  When this happens, the
          // delay() method will throw an IllegalArgumentException
          // and the run() method will be terminated.
       try {
          while (true) {
             tower = null;
             if (OSC != null) {
                Graphics g = OSC.getGraphics();
                drawCurrentFrame(g);
                g.dispose();
             }
             repaint();
             delay(2000);
             synchronized(this) {
                tower = new int[3][10];
                for (int i = 0; i < 10; i++)
                   tower[0][i] = 10 - i;
                towerHeight = new int[3];
                towerHeight[0] = 10;
                if (OSC != null) {
                   Graphics g = OSC.getGraphics();
                   drawCurrentFrame(g);
                   g.dispose();
                }
                repaint();
                delay(2000);
             }
             solve(10,0,1,2);
             delay(4000);
          }
       }
       catch (IllegalArgumentException e) {
       }
   }
   

   private void solve(int disks, int from, int to, int spare) {
          // Solve the problem of moving the number of disks specified
          // by the first parameter from the pile specified by the 
          // second parameter to the pile specified by the third parameter.
          // The pile specified by the fourth parameter is available
          // for use as a spare.  On the "top level", this recursive
          // subroutine is called by the run() method.
      if (disks == 1)
         moveOne(from,to);
      else {
         solve(disks-1, from, spare, to);
         moveOne(from,to);
         solve(disks-1, spare, to, from);
      }
   }
   

   synchronized private void moveOne(int fromStack, int toStack) {
          // Move the disk at the top of pile number fromStack to
          // the top of pile number toStack.  (The disk changes to
          // a new color, then moves, then changes back to the standard
          // color.
      moveDisk = tower[fromStack][towerHeight[fromStack]-1];
      moveTower = fromStack;
      delay(120);
      towerHeight[fromStack]--;
      putDisk(MOVE_DISK_COLOR,moveDisk,moveTower);
      delay(80);
      putDisk(BACKGROUND_COLOR,moveDisk,moveTower);
      delay(80);
      moveTower = toStack;
      putDisk(MOVE_DISK_COLOR,moveDisk,moveTower);
      delay(80);
      putDisk(DISK_COLOR,moveDisk,moveTower);
      tower[toStack][towerHeight[toStack]] = moveDisk;
      towerHeight[toStack]++;
      moveDisk = 0;
   }
   

   private void putDisk(Color color, int disk, int t) {
          // Draw the specified disk on top of the pile number t.
          // This disk is NOT part of the data for the tower that
          // is stored in the tower[][] array.  This routine is
          // only called by moveOne.  (But if OSC is null, then
          // the call to repaint() will redraw the entire frame on
          // the screen.)
      if (OSC != null) {
         Graphics g = OSC.getGraphics();
         g.setColor(color);
         g.fillRoundRect(75+140*t - 5*disk - 5, 116-12*towerHeight[t], 10*disk+10, 10, 10, 10);
         g.dispose();
      }
      repaint();
   }
                                                   

   synchronized void drawCurrentFrame(Graphics g) {
          // Called to draw the current frame.  But it is not drawn during
          // the animation of the solution.  During the animation, the
          // moveDisk() method just modifies the existing picture.
      g.setColor(BACKGROUND_COLOR);
      g.fillRect(0,0,430,143);
      g.setColor(BORDER_COLOR);
      g.drawRect(0,0,429,142);
      g.drawRect(1,1,427,140);
      if (tower == null)
         return;
      g.fillRect(10,128,130,5);
      g.fillRect(150,128,130,5);
      g.fillRect(290,128,130,5);
      g.setColor(DISK_COLOR);
      for (int t = 0; t < 3; t++) {
         for (int i = 0; i < towerHeight[t]; i++) {
            int disk = tower[t][i];
            g.fillRoundRect(75+140*t - 5*disk - 5, 116-12*i, 10*disk+10, 10, 10, 10);
         }
      }
      if (moveDisk > 0) {
         g.setColor(MOVE_DISK_COLOR);
         g.fillRoundRect(75+140*moveTower - 5*moveDisk - 5, 116-12*towerHeight[moveTower], 
                                             10*moveDisk+10, 10, 10, 10);
      }
   }
   

   synchronized void delay(int milliseconds) {
            // This routine is called repeatedly during the animation,
            // between frames.  It delays for the specified number
            // of milliseconds.  It handles suspension of the
            // applet by not returning as long as the status variable
            // has the value SUSPEND.  And it handles termination of
            // the applet by throwing an IllegalArgumentException if
            // the status variable is TERMINATE.  This exception will
            // be caught in the run() method, which will terminate
            // in response.
       if (status == TERMINATE)
          throw new IllegalArgumentException("Terminated");
       try {
           wait(milliseconds);
       }
       catch (InterruptedException e) {
       }
       while (status == SUSPEND) {
          try {
             wait();
          }
          catch (InterruptedException e) {
          }
       }
       if (status == TERMINATE)
          throw new IllegalArgumentException("Terminated");
   }
                                                   

   synchronized public void start() {
         // When applet is started or restarted, start or
         // restart the thread.
      status = GO;  // Indicates that the applet is running.
      if (runner == null || !runner.isAlive()) {
          tower = null;
          runner = new Thread(this);
          runner.start();
      }
      else {
         notify();
      }
   }
   

   synchronized public void stop() {
          // When the applet is stopped, change the status
          // to SUSPEND.
      status = SUSPEND;
      notify();
   }
   

   synchronized public void destroy() {
         // When the applet is killed, change the status
         // to TERMINATE.
      status = TERMINATE;
      notify();
   }
   

   synchronized public void paint(Graphics g) {
          // Paint the applet by copying the OSC to the
          // screen.  (If the OSC could not be created, 
          // draw the current frame directly.)
       if (OSC == null)
          setupOSC();
       if (OSC == null)
          drawCurrentFrame(g);
       else
          g.drawImage(OSC,0,0,this);
   }


   public void update(Graphics g) {
          // Modified so the applet doesn't get cleared before it is painted.
       paint(g);
   }
   

   synchronized private void setupOSC() {
           // Create an off-screen canvas matching the applet in size,
           // if possible.  (Applet must be 430 by 143)
       OSC = null;  // Free memory currently used by OSC.
       try {
          OSC = createImage(430,143);
       }
       catch (OutOfMemoryError e) {
          OSC = null;
          return;
       }
       Graphics OSG = OSC.getGraphics();
       drawCurrentFrame(OSG);
       OSG.dispose();
   }


}  // end class TowersOfHanoi
