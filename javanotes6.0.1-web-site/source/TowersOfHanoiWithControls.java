
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 *  This program shows an animation of the famous Towers of Hanoi problem, for a pile
 *  of ten disks.  Three control buttons allow the user to control the animation.
 *  A "Next" button allows the user to see just one move in the solution.  Clicking
 *  the "Run" button will let the animation run on its own; while it is running,
 *  "Run" changes to "Pause", and clicking the button will pause the animation.
 *  A "Start Again" button allows the user to restart the problem from the beginning.
 *  
 *  The program is an example of using the wait() and notify() methods.  The
 *  wait() method is used to pause the animation between moves.  When the user
 *  clicks "Next" or "Run", the notify() method is called to notify the thread to
 *  wake up and continue.  A "status" variable is used to communicate commands to
 *  the thread.
 *  
 *  A main() routine allows this class to be run as an application.  If it is used
 *  in an applet, the width of the applet should be 430, and the height should be
 *  143 plus enough space for the buttons at the bottom of the panel.
 */
public class TowersOfHanoiWithControls extends JPanel implements Runnable, ActionListener {
   
   /**
    * A main() routine to allow this class to be run as a stand-alone
    * application.  Just opens a window containing a panel of type
    * TowersOfHanoiWithControls.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("Towers Of Hanoi");
      window.setContentPane(new TowersOfHanoiWithControls());
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      window.pack();
      window.setResizable(true);
      window.setLocation(300,200);
      window.setVisible(true);
   }
   
   private static Color BACKGROUND_COLOR = new Color(255,255,180); // 4 colors used in drawing.
   private static Color BORDER_COLOR = new Color(100,0,0);
   private static Color DISK_COLOR = new Color(0,0,180);
   private static Color MOVE_DISK_COLOR = new Color(180,180,255);

   private BufferedImage OSC;   // The off-screen canvas.  Frames are drawn here, then copied to the screen.

   private int status;   // Controls the execution of the thread; value is one of the following constants.
   
   private static final int GO = 0;       // a value for status, meaning thread is to run continuously   
   private static final int PAUSE = 1;    // a value for status, meaning thread should not run
   private static final int STEP = 2;     // a value for status, meaning thread should run one step then pause
   private static final int RESTART = 3;  // a value for status, meaning thread should start again from the beginning

   /* 
    The following variables are the data needed for the animation.  The
      three "piles" of disks are represented by the variables tower and
      towerHeight.  towerHeight[i] is the number of disks on pile number i.
      For i=0,1,2 and for j=0,1,...,towerHeight[i]-1, tower[i][j] is an integer
      representing one of the ten disks.  (The disks are numbered from 1 to 10.)

      During the solution, as one disk is moved from one pile to another,
      the variable moveDisk is the number of the disk that is being moved,
      and moveTower is the number of the pile that it is currently on.
      This disk is not stored in the tower variable.  It is drawn in a
      different color from the other disks.
    */

   private int[][] tower;
   private int[] towerHeight;
   private int moveDisk;
   private int moveTower;

   private Display display;  // A subpanel where the frames of the animation are shown.

   private JButton runPauseButton;  // 3 control buttons for controlling the animation
   private JButton nextStepButton;
   private JButton startOverButton;


   /**
    * This class defines the panel that is used as a display, to show
    * the frames of the animation.  The paintComponent() method in this
    * class simply copies the off-screen canvas, OSC, to the screen.
    * This display will be given a preferred size of 430-by-143, which
    * is the same size as the canvas.  But to allow for possible small
    * variations from this size, OSC is drawn centered on the panel.
    */
   private class Display extends JPanel {
      protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         int x = (getWidth() - OSC.getWidth())/2;
         int y = (getHeight() - OSC.getHeight())/2;
         g.drawImage(OSC, x, y, null);
      }
   }

   
   /**
    *  Create the panel, containing a display panel and, beneath it,
    *  a sub-panel containing the three control buttons.  This
    *  constructor also creates the off-screen canvas, and creates
    *  and starts the animation thread.
    */
   public TowersOfHanoiWithControls () {
      OSC = new BufferedImage(430,143,BufferedImage.TYPE_INT_RGB);
      display = new Display();
      display.setPreferredSize(new Dimension(430,143));
      display.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 2));
      display.setBackground(BACKGROUND_COLOR);
      setLayout(new BorderLayout());
      add(display, BorderLayout.CENTER);
      JPanel buttonBar = new JPanel();
      add(buttonBar, BorderLayout.SOUTH);
      buttonBar.setLayout(new GridLayout(1,0));
      runPauseButton = new JButton("Run");
      runPauseButton.addActionListener(this);
      buttonBar.add(runPauseButton);
      nextStepButton = new JButton("Next Step");
      nextStepButton.addActionListener(this);
      buttonBar.add(nextStepButton);
      startOverButton = new JButton("Start Over");
      startOverButton.addActionListener(this);
      startOverButton.setEnabled(false);
      buttonBar.add(startOverButton);
      new Thread(this).start();
   }

   
   /**
    *  Event-handling method for the control buttons.  Changes in the
    *  value of the status variable will be seen by the animation thread,
    *  which will respond appropriately.
    */
   synchronized public void actionPerformed(ActionEvent evt) {
      Object source = evt.getSource();
      if (source == runPauseButton) {  // Toggle between running and paused.
         if (status == GO) {  // Animation is running.  Pause it.
            status = PAUSE;
            nextStepButton.setEnabled(true);
            runPauseButton.setText("Run");
         }
         else {  // Animation is paused.  Start it running.
            status = GO;
            nextStepButton.setEnabled(false);  // Disabled when animation is running
            runPauseButton.setText("Pause");
         }
      }
      else if (source == nextStepButton) {  // Set status to make animation run one step.
         status = STEP;
      }
      else if (source == startOverButton) { // Set status to make animation restart.
         status = RESTART;
      }
      notify();  // Wake up the thread so it can see the new status value!
   }


   /**
    *  The run() method for the animation thread.  Runs in an infinite loop.
    *  In the loop, the thread first sets up the initial state of the "towers"
    *  and of the buttons.  This includes setting the status to PAUSED, and
    *  calling checkStatus(), which will not return until the user clicks the
    *  "Run" button or the "Next" Button.  Once this happens, it calls
    *  the solve() method to run the recursive algorithm that solve the
    *  Towers Of Hanoi problem.  During the solution, checkStatus() is
    *  called after each move.  If the user clicks the "Start Again" button,
    *  checkStatus() will throw an IllegalStateException, which will cause
    *  the solve() method to be aborted.  The exception is caught to prevent
    *  it from crashing the thread.  
    */
   public void run() {
      while (true) {
         runPauseButton.setText("Run");
         nextStepButton.setEnabled(true);
         startOverButton.setEnabled(false);
         setUpProblem();  // Sets up the initial state of the puzzle
         status = PAUSE;
         checkStatus(); // Returns only when user has clicked "Run" or "Next"
         startOverButton.setEnabled(true);
         try {
            solve(10,0,1,2);  // Move 10 disks from pile 0 to pile 1.
         }
         catch (IllegalStateException e) {
               // Exception was thrown because use clicked "Start Over".
         }         
      }
   }

   
   /**
    *  This method is called before starting the solution and after each
    *  move of the solution.  If the status is PAUSE, it waits until
    *  the status changes.  If the status is RESTART, it throws
    *  an IllegalStateExcpetion that will abort the solution.
    *  When this method returns, the value of status must be 
    *  RUN oR STEP.
    *     (Note that this method requires synchronization, since
    *  otherwise calling wait() would produce an IllegalMonitorStateException.
    *  However, in fact, it is only called from other synchronized methods,
    *  so it would not be necessary to declare this method synchronized.
    *  Any method that calls it already owns the synchronization lock.)
    */
   synchronized private void checkStatus() {
      while (status == PAUSE) {
         try {
            wait();
         }
         catch (InterruptedException e) {
         }
      }
      // At this point, status is RUN, STEP, or RESTART.
      if (status == RESTART)
         throw new IllegalStateException("Restart");
      // At this point, status is RUN or STEP.
   }
   
   
   /**
    * Sets up the initial state of the Towers Of Hanoi puzzle, with
    * all the disks on the first pile.  
    */
   synchronized private void setUpProblem() {
      moveDisk= 0;
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
      display.repaint();
   }
   

   /**
    * Solves the TowersOfHanoi problem to move the specified
    * number of disks from one pile to another.
    * @param disks the number of disks to be moved
    * @param from the number of the pile where the disks are now
    * @param to the number of the pile to which the disks are to be moved.
    * @param spare the number of the pile that can be used as a spare.
    */
   private void solve(int disks, int from, int to, int spare) {
      if (disks == 1)
         moveOne(from,to);
      else {
         solve(disks-1, from, spare, to);
         moveOne(from,to);
         solve(disks-1, spare, to, from);
      }
   }


   /**
    * Move the disk at the top of pile number fromStack to
    * the top of pile number toStack.  (The disk changes to
    * a new color, then moves, then changes back to the standard
    * color.)  The delay() method is called to insert some short
    * delays into the animation.  After the move, if the value of
    * status was STEP, indicating that only one step was to be
    * executed before pausing, then the value of STATUS is changed
    * to PAUSE.  In any case, at the end of the method, the
    * checkStatus() method is called.
    */
   synchronized private void moveOne(int fromStack, int toStack) {
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
      if (status == STEP)
         status = PAUSE;
      checkStatus();
   }

   
   /**
    * Simple utility method for inserting a delay of a specified
    * number of milliseconds.
    */
   synchronized private void delay(int milliseconds) {
      try {
         wait(milliseconds);
      }
      catch (InterruptedException e) {
      }
   }

   
   /**
    * Draw a specified disk to the off-screen canvas.  This is
    * used only during the moveOne() method, to draw the disk
    * that is being moved.  Calls display.repaint() to redraw
    * display using the newly modified image.
    * @param color the color of the disk (use background color to erase).
    * @param disk the number of the disk that is to be drawn, 1 to 10.
    * @param t the number of the pile on top of which the disk is drawn.
    */
   private void putDisk(Color color, int disk, int t) {
      Graphics g = OSC.getGraphics();
      g.setColor(color);
      g.fillRoundRect(75+140*t - 5*disk - 5, 116-12*towerHeight[t], 10*disk+10, 10, 10, 10);
      g.dispose();
      display.repaint();
   }


   /**
    * Called to draw the current frame, not including the moving disk,
    * if any, which is drawn as part of the moveOne() method.
    */
   synchronized private void drawCurrentFrame(Graphics g) {
      // Called to draw the current frame.  But it is not drawn during
      // the animation of the solution.  During the animation, the
      // moveDisk() method just modifies the existing picture.
      g.setColor(BACKGROUND_COLOR);
      g.fillRect(0,0,430,143);
      g.setColor(BORDER_COLOR);
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



} // end class
